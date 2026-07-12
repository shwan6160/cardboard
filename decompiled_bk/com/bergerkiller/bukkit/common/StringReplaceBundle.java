/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.collections.EntryList;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StringReplaceBundle {
    private EntryList<String, String> entries = new EntryList();

    public StringReplaceBundle add(String from, String to) {
        if (from == null) {
            throw new IllegalArgumentException("Can not use a 'from' key of null");
        }
        if (to == null) {
            throw new IllegalArgumentException("Can not use a 'to' value of null");
        }
        this.entries.add(new AbstractMap.SimpleEntry<String, String>(from, to));
        return this;
    }

    public String get(String from) {
        for (Map.Entry entry : this.entries) {
            if (!((String)entry.getKey()).equals(from)) continue;
            return (String)entry.getValue();
        }
        return null;
    }

    public String remove(String from) {
        Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            if (!((String)entry.getKey()).equals(from)) continue;
            iter.remove();
            return (String)entry.getValue();
        }
        return null;
    }

    public String replace(String input) {
        StringBuilder output = new StringBuilder(input);
        for (Map.Entry entry : this.entries) {
            int index = 0;
            while ((index = output.indexOf((String)entry.getKey(), index)) != -1) {
                output.replace(index, index + ((String)entry.getKey()).length(), (String)entry.getValue());
                index += ((String)entry.getValue()).length();
            }
        }
        return output.toString();
    }

    public List<Map.Entry<String, String>> getEntries() {
        return this.entries;
    }

    public StringReplaceBundle clear() {
        this.entries.clear();
        return this;
    }

    public StringReplaceBundle load(ConfigurationNode node) {
        for (String key : node.getKeys()) {
            this.add(key, node.get(key, key));
        }
        return this;
    }

    public StringReplaceBundle save(ConfigurationNode node) {
        for (Map.Entry entry : this.entries) {
            node.set((String)entry.getKey(), entry.getValue());
        }
        return this;
    }
}

