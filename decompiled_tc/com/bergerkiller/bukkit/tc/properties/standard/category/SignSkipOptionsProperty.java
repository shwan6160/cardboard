/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.BlockLocation
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 */
package com.bergerkiller.bukkit.tc.properties.standard.category;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedProperty;
import com.bergerkiller.bukkit.tc.properties.standard.type.SignSkipOptions;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class SignSkipOptionsProperty
extends FieldBackedProperty<SignSkipOptions> {
    @Override
    public SignSkipOptions getDefault() {
        return SignSkipOptions.NONE;
    }

    @Override
    public boolean isAppliedAsDefault() {
        return false;
    }

    @Override
    public Optional<SignSkipOptions> readFromConfig(ConfigurationNode config) {
        List signLocationNames;
        if (!config.isNode("skipOptions")) {
            return Optional.empty();
        }
        ConfigurationNode skipOptions = config.getNode("skipOptions");
        int ignoreCtr = (Integer)skipOptions.get("ignoreCtr", (Object)0);
        int skipCtr = (Integer)skipOptions.get("skipCtr", (Object)0);
        String filter = (String)skipOptions.get("filter", (Object)"");
        Set<BlockLocation> signs = Collections.emptySet();
        if (skipOptions.contains("signs") && !(signLocationNames = skipOptions.getList("signs", String.class)).isEmpty()) {
            signs = new LinkedHashSet<BlockLocation>(signLocationNames.size());
            for (String signLocationName : signLocationNames) {
                BlockLocation loc = BlockLocation.parseLocation((String)signLocationName);
                if (loc == null) continue;
                signs.add(loc);
            }
            signs = Collections.unmodifiableSet(signs);
        }
        return Optional.of(SignSkipOptions.create(ignoreCtr, skipCtr, filter, signs));
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<SignSkipOptions> value) {
        if (value.isPresent()) {
            SignSkipOptions data = value.get();
            ConfigurationNode skipOptions = config.getNode("skipOptions");
            skipOptions.set("ignoreCtr", (Object)data.ignoreCounter());
            skipOptions.set("skipCtr", (Object)data.skipCounter());
            skipOptions.set("filter", (Object)data.filter());
            if (data.hasSkippedSigns()) {
                List signs = skipOptions.getList("signs", String.class);
                Iterator<BlockLocation> signBlockIter = data.skippedSigns().iterator();
                int num_signs = 0;
                while (signBlockIter.hasNext()) {
                    if (num_signs >= signs.size()) {
                        signs.add(signBlockIter.next().toString());
                    } else {
                        signs.set(num_signs, signBlockIter.next().toString());
                    }
                    ++num_signs;
                }
                while (signs.size() > num_signs) {
                    signs.remove(signs.size() - 1);
                }
            } else {
                skipOptions.remove("signs");
            }
        } else {
            config.remove("skipOptions");
        }
    }

    @Override
    public SignSkipOptions get(CartProperties properties) {
        return FieldBackedProperty.CartInternalData.get((CartProperties)properties).signSkipOptionsData;
    }

    @Override
    public void set(CartProperties properties, SignSkipOptions value) {
        if (value.equals(SignSkipOptions.NONE)) {
            FieldBackedProperty.CartInternalData.get((CartProperties)properties).signSkipOptionsData = SignSkipOptions.NONE;
            this.writeToConfig(properties.getConfig(), Optional.empty());
        } else {
            FieldBackedProperty.CartInternalData.get((CartProperties)properties).signSkipOptionsData = value;
            this.writeToConfig(properties.getConfig(), Optional.of(value));
        }
    }

    @Override
    public SignSkipOptions get(TrainProperties properties) {
        return FieldBackedProperty.TrainInternalData.get((TrainProperties)properties).signSkipOptionsData;
    }

    @Override
    public void set(TrainProperties properties, SignSkipOptions value) {
        if (value.equals(SignSkipOptions.NONE)) {
            FieldBackedProperty.TrainInternalData.get((TrainProperties)properties).signSkipOptionsData = SignSkipOptions.NONE;
            this.writeToConfig(properties.getConfig(), Optional.empty());
        } else {
            FieldBackedProperty.TrainInternalData.get((TrainProperties)properties).signSkipOptionsData = value;
            this.writeToConfig(properties.getConfig(), Optional.of(value));
        }
    }

    @Override
    public void onConfigurationChanged(CartProperties properties) {
        Optional<SignSkipOptions> opt = this.readFromConfig(properties.getConfig());
        FieldBackedProperty.CartInternalData.get((CartProperties)properties).signSkipOptionsData = opt.isPresent() ? opt.get() : SignSkipOptions.NONE;
    }

    @Override
    public void onConfigurationChanged(TrainProperties properties) {
        Optional<SignSkipOptions> opt = this.readFromConfig(properties.getConfig());
        FieldBackedProperty.TrainInternalData.get((TrainProperties)properties).signSkipOptionsData = opt.isPresent() ? opt.get() : SignSkipOptions.NONE;
    }
}

