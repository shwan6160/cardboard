/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.BlockLocation
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.components.SignTracker;
import com.bergerkiller.bukkit.tc.properties.IProperties;
import com.bergerkiller.bukkit.tc.properties.IPropertiesHolder;
import com.bergerkiller.bukkit.tc.properties.standard.StandardProperties;
import com.bergerkiller.bukkit.tc.properties.standard.type.SignSkipOptions;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.block.Block;

public class SignSkipTracker {
    private final IPropertiesHolder owner;
    private boolean isLoaded = false;
    private final Map<RailLookup.TrackedSign, Boolean> history = new HashMap<RailLookup.TrackedSign, Boolean>();

    public SignSkipTracker(IPropertiesHolder owner) {
        this.owner = owner;
    }

    public boolean isSkipped(RailLookup.TrackedSign sign) {
        return Boolean.TRUE.equals(this.history.get(sign));
    }

    public List<RailLookup.TrackedSign> getSkippedSigns() {
        if (this.history.isEmpty()) {
            return Collections.emptyList();
        }
        return this.history.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public void setSkipped(SignTracker.ActiveSign sign) {
        this.setSkipped(sign, true);
    }

    public void setSkipped(SignTracker.ActiveSign sign, boolean skipped) {
        this.history.put(sign.getSign(), skipped);
    }

    public void loadSigns(List<SignTracker.ActiveSign> signs) {
        this.isLoaded = true;
        this.history.clear();
        if (signs.isEmpty()) {
            return;
        }
        SignSkipOptions options = this.owner.getProperties().get(StandardProperties.SIGN_SKIP);
        if (options.hasSkippedSigns()) {
            block0: for (BlockLocation signPos : options.skippedSigns()) {
                for (SignTracker.ActiveSign sign : signs) {
                    Block signBlock = sign.getSign().signBlock;
                    if (signPos.x != signBlock.getX() || signPos.y != signBlock.getY() || signPos.z != signBlock.getZ() || !signPos.world.equals(signBlock.getWorld().getName())) continue;
                    this.history.put(sign.getSign(), Boolean.TRUE);
                    continue block0;
                }
            }
        }
        for (SignTracker.ActiveSign sign : signs) {
            this.history.putIfAbsent(sign.getSign(), Boolean.FALSE);
        }
    }

    public void unloadSigns() {
        if (this.isLoaded) {
            this.isLoaded = false;
            this.history.clear();
        }
    }

    public void onSignVisitStart(List<SignTracker.ActiveSign> signs) {
        if (!this.isLoaded) {
            this.loadSigns(signs);
        }
        Iterator<Map.Entry<RailLookup.TrackedSign, Boolean>> iter = this.history.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<RailLookup.TrackedSign, Boolean> e = iter.next();
            boolean found = false;
            for (SignTracker.ActiveSign sign : signs) {
                RailLookup.TrackedSign trackedSign = e.getKey();
                if (!sign.getSign().equals(trackedSign)) continue;
                found = true;
                break;
            }
            if (found) continue;
            iter.remove();
        }
    }

    public boolean onSignVisit(SignTracker.ActiveSign sign) {
        Boolean isSignSkipped = this.history.computeIfAbsent(sign.getSign(), trackedSign -> {
            Boolean isNewSignSkipped;
            IProperties properties = this.owner.getProperties();
            SignSkipOptions options = properties.get(StandardProperties.SIGN_SKIP);
            if (!options.isActive()) {
                return Boolean.FALSE;
            }
            boolean passFilter = true;
            if (options.hasFilter()) {
                passFilter = trackedSign.sign == null ? false : Util.getCleanLine(trackedSign.sign, 1).toLowerCase(Locale.ENGLISH).startsWith(options.filter());
            }
            if (passFilter) {
                SkipOptionChanges changes = new SkipOptionChanges(options);
                isNewSignSkipped = changes.handleSkip();
                if (options.hasSkippedSigns() || changes.countersChanged) {
                    properties.set(StandardProperties.SIGN_SKIP, SignSkipOptions.create(changes.ignoreCounter, changes.skipCounter, options.filter(), Collections.emptySet()));
                }
            } else {
                isNewSignSkipped = Boolean.FALSE;
            }
            return isNewSignSkipped;
        });
        return isSignSkipped == false;
    }

    private static final class SkipOptionChanges {
        public int ignoreCounter;
        public int skipCounter;
        public boolean countersChanged;

        public SkipOptionChanges(SignSkipOptions options) {
            this.ignoreCounter = options.ignoreCounter();
            this.skipCounter = options.skipCounter();
            this.countersChanged = false;
        }

        public Boolean handleSkip() {
            if (this.ignoreCounter > 0) {
                --this.ignoreCounter;
                this.countersChanged = true;
                return Boolean.FALSE;
            }
            if (this.skipCounter > 0) {
                --this.skipCounter;
                this.countersChanged = true;
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
    }
}

