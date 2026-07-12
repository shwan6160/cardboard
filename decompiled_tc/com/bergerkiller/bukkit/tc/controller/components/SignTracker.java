/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.ToggledState
 *  com.bergerkiller.bukkit.common.collections.ImplicitlySharedList
 *  com.bergerkiller.bukkit.common.utils.StreamUtil
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.common.ToggledState;
import com.bergerkiller.bukkit.common.collections.ImplicitlySharedList;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.controller.components.SignSkipTracker;
import com.bergerkiller.bukkit.tc.detector.DetectorRegion;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.properties.IPropertiesHolder;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.modlist.ModificationTrackedList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.block.Block;

public abstract class SignTracker {
    private static final ArrayList<ActiveSign> tmpSignBuffer = new ArrayList();
    private Set<Object> offlineLoadedSkippedSignKeys = Collections.emptySet();
    private Set<Object> offlineLoadedActiveSignKeys = Collections.emptySet();
    private final Map<Object, ActiveSign> activeSignsByKey = new LinkedHashMap<Object, ActiveSign>();
    private final ImplicitlySharedList<ActiveSign> activeSigns = new ImplicitlySharedList();
    protected ImplicitlySharedList<DetectorRegion> detectorRegions = new ImplicitlySharedList();
    protected final ToggledState needsUpdate = new ToggledState();
    protected final SignSkipTracker signSkipTracker;

    protected SignTracker(IPropertiesHolder owner) {
        this.signSkipTracker = new SignSkipTracker(owner);
    }

    public abstract TrainCarts.Provider getOwner();

    public SignSkipTracker getSignSkipTracker() {
        return this.signSkipTracker;
    }

    public ImplicitlySharedList<ActiveSign> getActiveTrackedSigns() {
        return this.activeSigns;
    }

    public Collection<DetectorRegion> getActiveDetectorRegions() {
        return this.detectorRegions;
    }

    public void addOfflineSkippedSignKey(Object signUniqueKey) {
        if (this.offlineLoadedSkippedSignKeys.isEmpty()) {
            this.offlineLoadedSkippedSignKeys = new HashSet<Object>();
        }
        this.offlineLoadedSkippedSignKeys.add(signUniqueKey);
    }

    protected void addOfflineActiveSignKey(Object signUniqueKey) {
        if (this.offlineLoadedActiveSignKeys.isEmpty()) {
            this.offlineLoadedActiveSignKeys = new HashSet<Object>();
        }
        this.offlineLoadedActiveSignKeys.add(signUniqueKey);
    }

    protected void clearOfflineActiveSignKeys() {
        this.offlineLoadedActiveSignKeys = Collections.emptySet();
        this.offlineLoadedSkippedSignKeys = Collections.emptySet();
    }

    protected void onSignVisitStart(List<ActiveSign> signs) {
        if (!signs.isEmpty()) {
            if (!this.offlineLoadedActiveSignKeys.isEmpty()) {
                this.signSkipTracker.loadSigns(signs.stream().filter(s -> this.offlineLoadedActiveSignKeys.contains(s.getUniqueKey())).collect(Collectors.toList()));
            } else if (!this.offlineLoadedSkippedSignKeys.isEmpty()) {
                this.signSkipTracker.loadSigns(Collections.emptyList());
            }
            if (!this.offlineLoadedSkippedSignKeys.isEmpty()) {
                for (ActiveSign sign : signs) {
                    if (!this.offlineLoadedSkippedSignKeys.contains(sign.getUniqueKey())) continue;
                    this.signSkipTracker.setSkipped(sign);
                }
            }
        }
        this.signSkipTracker.onSignVisitStart(signs);
    }

    public boolean isSkipped(RailLookup.TrackedSign sign) {
        return this.signSkipTracker.isSkipped(sign);
    }

    public boolean containsSign(RailLookup.TrackedSign sign) {
        if (sign != null) {
            ActiveSign existing = this.activeSignsByKey.get(sign.getUniqueKey());
            if (existing == null) {
                return false;
            }
            if (sign == existing.sign) {
                return true;
            }
            if (sign.isRealSign() && existing.sign.isRealSign()) {
                return sign.signBlock.equals((Object)((ActiveSign)existing).sign.signBlock);
            }
        }
        return false;
    }

    public boolean removeSign(RailLookup.TrackedSign sign) {
        if (sign == null) {
            return false;
        }
        ActiveSign removed = this.activeSignsByKey.remove(sign.getUniqueKey());
        if (removed != null) {
            this.activeSigns.remove((Object)removed);
            this.onSignChange(removed, false);
            return true;
        }
        return false;
    }

    public boolean hasSigns() {
        return !this.activeSigns.isEmpty();
    }

    public final void clear() {
        this.clear(ClearMode.LEAVE);
    }

    public void clear(ClearMode clearMode) {
        if (!this.activeSignsByKey.isEmpty()) {
            int maxResetIterCtr = 100;
            int expectedCount = this.activeSignsByKey.size();
            Iterator<ActiveSign> iter = this.activeSignsByKey.values().iterator();
            while (iter.hasNext()) {
                ActiveSign sign = iter.next();
                iter.remove();
                this.activeSigns.remove((Object)sign);
                clearMode.eventHandler.accept(this, sign);
                if (--expectedCount == this.activeSignsByKey.size()) continue;
                expectedCount = this.activeSignsByKey.size();
                iter = this.activeSignsByKey.values().iterator();
                if (--maxResetIterCtr > 0) continue;
                this.getOwner().getTrainCarts().log(Level.WARNING, "[SignTracker] Number of iteration reset attempts exceeded limit");
                break;
            }
            this.activeSigns.clear();
            this.activeSignsByKey.clear();
        }
    }

    public void update() {
        this.needsUpdate.set();
    }

    public void clearUpdates() {
        this.needsUpdate.clear();
    }

    @Deprecated
    public abstract boolean isOnRails(Block var1);

    protected abstract void onSignChange(ActiveSign var1, boolean var2);

    protected abstract void onLoadedChange(ActiveSign var1, boolean var2);

    protected void updateActiveSigns(Supplier<ModificationTrackedList<ActiveSign>> activeSignListSupplier) {
        int limit = 1000;
        while (!this.tryUpdateActiveSigns(activeSignListSupplier.get())) {
            if (--limit != 0) continue;
            this.getOwner().getTrainCarts().getLogger().log(Level.SEVERE, "Reached limit of loops updating active signs");
            break;
        }
    }

    private boolean tryUpdateActiveSigns(ModificationTrackedList<ActiveSign> list) {
        boolean hadSigns;
        int mod_start = list.getModCount();
        boolean bl = hadSigns = !this.activeSigns.isEmpty();
        if (list.isEmpty()) {
            if (hadSigns) {
                Iterator<ActiveSign> iter = this.activeSignsByKey.values().iterator();
                while (iter.hasNext()) {
                    ActiveSign sign = iter.next();
                    this.activeSigns.remove((Object)sign);
                    iter.remove();
                    this.onSignChange(sign, false);
                    if (list.getModCount() == mod_start) continue;
                    return false;
                }
            }
            return true;
        }
        this.activeSigns.forEach(a -> ((ActiveSign)a).detected = false);
        for (ActiveSign newActiveSign : list) {
            ActiveSign currActiveSign2 = this.activeSignsByKey.computeIfAbsent(newActiveSign.getUniqueKey(), u -> new ActiveSign(newActiveSign.sign, null));
            currActiveSign2.detected = true;
            if (currActiveSign2.enterState == null) {
                currActiveSign2.enterState = newActiveSign.enterState;
                if (this.offlineLoadedActiveSignKeys.contains(currActiveSign2.getUniqueKey())) {
                    this.activeSigns.add((Object)currActiveSign2);
                    this.signSkipTracker.setSkipped(currActiveSign2, false);
                    this.onLoadedChange(currActiveSign2, true);
                } else if (this.signSkipTracker.onSignVisit(currActiveSign2)) {
                    this.activeSigns.add((Object)currActiveSign2);
                    this.onSignChange(currActiveSign2, true);
                }
            } else {
                if (currActiveSign2.getAction() == newActiveSign.sign.getAction()) {
                    if (currActiveSign2.sign == newActiveSign.sign) continue;
                    if (currActiveSign2.sign.hasIdenticalText(newActiveSign.sign)) {
                        currActiveSign2.setSign(newActiveSign.sign);
                        continue;
                    }
                }
                SignAction action = currActiveSign2.getAction();
                boolean fireEvents = true;
                if (action != null && newActiveSign.sign.getAction() == action) {
                    SignActionEvent event = newActiveSign.sign.createEvent(SignActionType.NONE);
                    fireEvents = action.signTextChanged(event);
                }
                if (fireEvents) {
                    this.onSignChange(currActiveSign2, false);
                }
                currActiveSign2.setSign(newActiveSign.sign);
                if (fireEvents) {
                    this.onSignChange(currActiveSign2, true);
                }
            }
            if (list.getModCount() == mod_start) continue;
            return false;
        }
        if (hadSigns) {
            this.forEachActiveSignSafe(currActiveSign -> {
                if (!((ActiveSign)currActiveSign).detected) {
                    ActiveSign removed = this.activeSignsByKey.remove(currActiveSign.getUniqueKey());
                    if (removed != null) {
                        this.activeSigns.remove((Object)removed);
                    }
                    if (removed == currActiveSign) {
                        this.onSignChange((ActiveSign)currActiveSign, false);
                    }
                }
            });
            if (list.getModCount() != mod_start) {
                return false;
            }
        }
        return true;
    }

    private void forEachActiveSignSafe(Consumer<ActiveSign> action) {
        ArrayList<ActiveSign> buffer = tmpSignBuffer;
        if (buffer.isEmpty()) {
            buffer.addAll((Collection<ActiveSign>)this.activeSigns);
            try {
                buffer.forEach(action);
            }
            finally {
                buffer.clear();
            }
        }
        try (ImplicitlySharedList copy = this.activeSigns.clone();){
            copy.forEach(action);
        }
    }

    @Deprecated
    public Collection<Block> getActiveSigns() {
        return (Collection)this.getActiveTrackedSigns().stream().map(s -> ((ActiveSign)s).sign).filter(RailLookup.TrackedSign::isRealSign).map(s -> s.signBlock).collect(StreamUtil.toUnmodifiableList());
    }

    @Deprecated
    public boolean containsSign(Block signblock) {
        ActiveSign sign = this.activeSignsByKey.get(signblock);
        return sign != null && sign.sign.isRealSign();
    }

    @Deprecated
    public boolean removeSign(Block signBlock) {
        ActiveSign removed = this.activeSignsByKey.remove(signBlock);
        if (removed != null && removed.sign.isRealSign()) {
            this.activeSigns.remove((Object)removed);
            this.onSignChange(removed, false);
            return true;
        }
        this.activeSignsByKey.put(signBlock, removed);
        return false;
    }

    public static final class ActiveSign {
        private RailLookup.TrackedSign sign;
        private SignAction action;
        private Object uniqueKey;
        private RailState enterState;
        private boolean detected;

        public ActiveSign(RailLookup.TrackedSign sign, RailState enterState) {
            this.sign = sign;
            this.action = sign.getAction();
            this.uniqueKey = sign.getUniqueKey();
            this.enterState = enterState;
            this.detected = true;
        }

        public RailLookup.TrackedSign getSign() {
            return this.sign;
        }

        public SignAction getAction() {
            return this.action;
        }

        private void setSign(RailLookup.TrackedSign sign) {
            this.sign = sign;
            this.action = sign.getAction();
            this.uniqueKey = sign.getUniqueKey();
        }

        public Object getUniqueKey() {
            return this.uniqueKey;
        }

        public RailState getEnterState() {
            return this.enterState;
        }

        public void executeEventForMember(SignActionType action, MinecartMember<?> member) {
            this.sign.executeEventForMember(action, member, this.enterState);
        }

        public void executeEventForGroup(SignActionType action, MinecartGroup group) {
            this.sign.executeEventForGroup(action, group, this.enterState);
        }
    }

    public static enum ClearMode {
        UNLOAD((tracker, sign) -> tracker.onLoadedChange((ActiveSign)sign, false)),
        LEAVE((tracker, sign) -> tracker.onSignChange((ActiveSign)sign, false)),
        SILENT((tracker, sign) -> {});

        private final BiConsumer<SignTracker, ActiveSign> eventHandler;

        private ClearMode(BiConsumer<SignTracker, ActiveSign> eventHandler) {
            this.eventHandler = eventHandler;
        }
    }
}

