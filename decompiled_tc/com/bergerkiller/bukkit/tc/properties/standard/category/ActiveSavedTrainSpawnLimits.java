/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 */
package com.bergerkiller.bukkit.tc.properties.standard.category;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedProperty;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedStandardTrainProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ActiveSavedTrainSpawnLimits
extends FieldBackedStandardTrainProperty<List<String>> {
    @Override
    public List<String> getDefault() {
        return Collections.emptyList();
    }

    public void addSavedTrainToConfig(ConfigurationNode config, String savedTrainName) {
        List names = config.getList("activeSavedTrainSpawnLimits", String.class);
        if (!names.contains(savedTrainName)) {
            names.add(savedTrainName);
        }
    }

    @Override
    public Optional<List<String>> readFromConfig(ConfigurationNode config) {
        if (config.contains("activeSavedTrainSpawnLimits")) {
            List names = config.getList("activeSavedTrainSpawnLimits", String.class);
            names = Collections.unmodifiableList(new ArrayList(names));
            return Optional.of(names);
        }
        return Optional.empty();
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<List<String>> value) {
        List<String> names;
        if (value.isPresent() && !(names = value.get()).isEmpty()) {
            config.set("activeSavedTrainSpawnLimits", names);
        } else {
            config.remove("activeSavedTrainSpawnLimits");
        }
    }

    @Override
    public List<String> getData(FieldBackedProperty.TrainInternalData data) {
        return data.activeSavedTrainSpawnLimits;
    }

    @Override
    public void setData(FieldBackedProperty.TrainInternalData data, List<String> value) {
        data.activeSavedTrainSpawnLimits = value;
    }
}

