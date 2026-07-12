/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 */
package com.bergerkiller.bukkit.tc.utils.modularconfiguration;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationEntry;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ReadOnlyModuleException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

class ModularConfigurationEntryMap<T>
implements ModularConfigurationEntry.Container<T> {
    private final HashMap<String, ModularConfigurationEntry<T>> entries = new HashMap();
    private List<ModularConfigurationEntry<T>> entriesList = Collections.emptyList();
    private List<String> entryNamesList = Collections.emptyList();
    private List<T> entryValuesList = Collections.emptyList();

    ModularConfigurationEntryMap() {
    }

    public void clear() {
        this.entries.clear();
        this.entriesList = Collections.emptyList();
        this.entryNamesList = Collections.emptyList();
        this.entryValuesList = Collections.emptyList();
    }

    public void set(String name, ModularConfigurationEntry<T> entry) {
        this.entries.put(name, entry);
        this.regenSortedLists();
    }

    @Override
    public ModularConfigurationEntry<T> remove(String name) {
        ModularConfigurationEntry<T> entry = this.entries.remove(name);
        if (entry != null) {
            this.regenSortedLists();
        }
        return entry;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModularConfigurationEntry<T> getIfExists(String name) {
        return this.entries.get(name);
    }

    @Override
    public ModularConfigurationEntry<T> add(String name, ConfigurationNode initialConfig) throws ReadOnlyModuleException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean rename(String name, String newName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    @Override
    public List<String> getNames() {
        List<String> result = this.entryNamesList;
        if (result == null) {
            List<ModularConfigurationEntry<T>> entries = this.getAll();
            result = new ArrayList<String>(entries.size());
            for (ModularConfigurationEntry<T> entry : entries) {
                result.add(entry.getName());
            }
            this.entryNamesList = result = Collections.unmodifiableList(result);
        }
        return result;
    }

    @Override
    public List<ModularConfigurationEntry<T>> getAll() {
        List<ModularConfigurationEntry<T>> result = this.entriesList;
        if (result == null) {
            result = new ArrayList<ModularConfigurationEntry<T>>(this.entries.values());
            Collections.sort(result);
            this.entriesList = result = Collections.unmodifiableList(result);
        }
        return result;
    }

    @Override
    public List<T> getAllValues() {
        List<T> result = this.entryValuesList;
        if (result == null) {
            List<ModularConfigurationEntry<T>> allEntries = this.getAll();
            result = new ArrayList<T>(allEntries.size());
            for (ModularConfigurationEntry<T> entry : allEntries) {
                result.add(entry.get());
            }
            result = Collections.unmodifiableList(result);
            this.entryValuesList = result;
        }
        return result;
    }

    private void regenSortedLists() {
        this.entriesList = null;
        this.entryNamesList = null;
        this.entryValuesList = null;
    }
}

