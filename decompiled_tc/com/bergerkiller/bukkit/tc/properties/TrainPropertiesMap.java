/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 */
package com.bergerkiller.bukkit.tc.properties;

import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

class TrainPropertiesMap {
    private Map<String, TrainProperties> trainProperties = new TreeMap<String, TrainProperties>();
    private Map<String, List<TrainProperties>> trainPropertiesRelaxed = new TreeMap<String, List<TrainProperties>>();

    TrainPropertiesMap() {
    }

    public Collection<TrainProperties> values() {
        return Collections.unmodifiableCollection(this.trainProperties.values());
    }

    public TrainProperties get(String trainName) {
        return this.trainProperties.get(trainName);
    }

    public TrainProperties getRelaxed(String trainName) {
        List<TrainProperties> result = this.trainPropertiesRelaxed.get(TrainPropertiesMap.createRelaxedKey(trainName));
        return result == null || result.size() != 1 ? null : result.get(0);
    }

    public boolean containsKey(String trainName) {
        return this.trainProperties.containsKey(trainName);
    }

    public void add(String trainName, TrainProperties properties) {
        TrainProperties previous = this.trainProperties.put(trainName, properties);
        if (previous != null) {
            previous.removed = true;
            this.removeFromRelaxedMappings(trainName, previous);
        }
        properties.removed = false;
        String relaxed = TrainPropertiesMap.createRelaxedKey(trainName);
        List<TrainProperties> prevAtRelaxedKey = this.trainPropertiesRelaxed.put(relaxed, Collections.singletonList(properties));
        if (prevAtRelaxedKey != null) {
            ArrayList<TrainProperties> combined = new ArrayList<TrainProperties>(prevAtRelaxedKey);
            combined.add(properties);
            this.trainPropertiesRelaxed.put(relaxed, combined);
        }
    }

    public TrainProperties remove(String trainName) {
        TrainProperties properties = this.trainProperties.remove(trainName);
        if (properties != null) {
            properties.removed = true;
            this.removeFromRelaxedMappings(trainName, properties);
        }
        return properties;
    }

    public void clear() {
        this.trainProperties.values().forEach(p -> {
            p.removed = true;
        });
        this.trainProperties.clear();
        this.trainPropertiesRelaxed.clear();
    }

    private void removeFromRelaxedMappings(String trainName, TrainProperties properties) {
        String relaxed = TrainPropertiesMap.createRelaxedKey(trainName);
        List<TrainProperties> atRelaxedKey = this.trainPropertiesRelaxed.remove(relaxed);
        if (atRelaxedKey != null) {
            if (atRelaxedKey.size() > 1) {
                atRelaxedKey.remove(properties);
                this.trainPropertiesRelaxed.put(relaxed, atRelaxedKey);
            } else if (atRelaxedKey.size() == 1 && atRelaxedKey.get(0) != properties) {
                this.trainPropertiesRelaxed.put(relaxed, atRelaxedKey);
            }
        }
    }

    private static String createRelaxedKey(String trainName) {
        return StringUtil.stripChatStyle((String)trainName).toLowerCase(Locale.ENGLISH);
    }
}

