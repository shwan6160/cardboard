/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 */
package com.bergerkiller.bukkit.tc.utils.modularconfiguration;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfiguration;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationBlock;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationEntry;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationEntryMap;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ReadOnlyModuleException;
import java.util.Collections;
import java.util.List;

public class ModularConfigurationModule<T>
implements ModularConfigurationBlock<T>,
ModularConfigurationEntry.Container<T>,
Comparable<ModularConfigurationModule<T>> {
    protected final ModularConfiguration<T> main;
    private final ModularConfigurationEntryMap<T> entries;
    protected final String name;
    final ConfigurationNode config;
    private final boolean readOnly;
    boolean configChanged;

    ModularConfigurationModule(ModularConfiguration<T> main, String name, ConfigurationNode config, boolean readOnly) {
        this.main = main;
        this.entries = new ModularConfigurationEntryMap();
        this.name = name;
        this.config = config;
        this.readOnly = readOnly;
        if (!readOnly) {
            this.config.addChangeListener(p -> {
                this.configChanged = true;
            });
        }
        this.loadConfig();
    }

    protected void loadConfig() {
        this.configChanged = false;
        this.main.preProcessModuleConfiguration(this.config);
        this.saveChanges();
        this.entries.clear();
        for (ConfigurationNode nodeConfig : this.config.getNodes()) {
            this.entries.set(nodeConfig.getName(), new ModularConfigurationEntry<T>(this.main, nodeConfig.getName(), nodeConfig, this));
        }
        this.configChanged = false;
    }

    void removeInModule(String name) {
        this.entries.remove(name);
        this.config.remove(name);
    }

    void store(ModularConfigurationEntry<T> entry) {
        entry.module = this;
        this.entries.set(entry.getName(), entry);
        this.config.set(entry.getName(), (Object)entry.getConfig());
        this.configChanged = true;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public ModularConfiguration<T> getMain() {
        return this.main;
    }

    @Override
    public List<? extends ModularConfigurationModule<T>> getFiles() {
        return Collections.singletonList(this);
    }

    @Override
    public void reload() {
    }

    @Override
    public void saveChanges() {
        this.configChanged = false;
    }

    @Override
    public void save() {
        this.configChanged = false;
    }

    @Override
    public ModularConfigurationEntry<T> add(String name, ConfigurationNode initialConfig) throws ReadOnlyModuleException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name is null or empty");
        }
        if (initialConfig == null) {
            throw new IllegalArgumentException("Initial configuration is null");
        }
        ModularConfigurationEntry<T> entry = this.main.get(name);
        entry.createWithConfigInModule(initialConfig, this);
        return entry;
    }

    @Override
    public boolean rename(String name, String newName) {
        if (name.equals(newName)) {
            return true;
        }
        if (this.isReadOnly()) {
            return false;
        }
        ModularConfigurationEntry<T> entry = this.main.getIfExists(name);
        if (entry.isRemoved() || entry.getModule() != this) {
            return false;
        }
        this.add(newName, entry.getConfig());
        return true;
    }

    @Override
    public ModularConfigurationEntry<T> getIfExists(String name) {
        return this.entries.getIfExists(name);
    }

    @Override
    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    @Override
    public List<String> getNames() {
        return this.entries.getNames();
    }

    @Override
    public List<ModularConfigurationEntry<T>> getAll() {
        return this.entries.getAll();
    }

    @Override
    public List<T> getAllValues() {
        return this.entries.getAllValues();
    }

    @Override
    public int compareTo(ModularConfigurationModule<T> tModularConfigurationFile) {
        if (this.readOnly != tModularConfigurationFile.readOnly) {
            return this.readOnly ? 1 : -1;
        }
        return this.name.compareTo(tModularConfigurationFile.name);
    }
}

