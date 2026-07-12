/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.google.common.collect.MapMaker
 */
package com.bergerkiller.bukkit.tc.utils.modularconfiguration;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationBlock;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationBlockList;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationEntry;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationEntryMap;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationModule;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ReadOnlyModuleException;
import com.google.common.collect.MapMaker;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public abstract class ModularConfiguration<T>
extends ModularConfigurationBlockList<T>
implements ModularConfigurationEntry.Container<T> {
    protected final Logger logger;
    final ModularConfigurationEntryMap<T> entries = new ModularConfigurationEntryMap();
    final ConcurrentMap<String, ModularConfigurationEntry<T>> removedEntries = new MapMaker().weakValues().makeMap();

    public ModularConfiguration(Logger logger) {
        this.logger = logger;
    }

    @Override
    public ModularConfiguration<T> getMain() {
        return this;
    }

    protected void preProcessModuleConfiguration(ConfigurationNode moduleConfig) {
    }

    protected void postProcessEntryConfiguration(ModularConfigurationEntry<T> entry) {
    }

    protected abstract T decodeConfig(ModularConfigurationEntry<T> var1);

    public abstract ModularConfigurationModule<T> getDefaultModule();

    void onModuleAdded(ModularConfigurationModule<T> module) {
        for (ModularConfigurationEntry<T> newEntry : module.getAll()) {
            ModularConfigurationEntry existing = this.entries.getIfExists(newEntry.getName());
            if (existing == null) {
                existing = (ModularConfigurationEntry)this.removedEntries.remove(newEntry.getName());
            }
            if (existing == null) {
                this.entries.set(newEntry.getName(), newEntry);
                continue;
            }
            if (!existing.isRemoved() && !this.isModuleOverriding(module, existing.getModule())) {
                int index;
                for (index = 0; index < existing.shadowModules.size() && this.isModuleOverriding(module, existing.shadowModules.get(index)); ++index) {
                }
                existing.shadowModules.add(index, module);
                continue;
            }
            existing.loadFromModule(module);
        }
    }

    void onModuleRemoved(ModularConfigurationModule<T> module) {
        for (String name : module.getNames()) {
            ModularConfigurationEntry<T> e = this.getIfExists(name);
            if (e == null) continue;
            e.onModuleRemoved(module);
        }
    }

    private boolean isModuleOverriding(ModularConfigurationModule<T> module, ModularConfigurationModule<T> otherModule) {
        for (ModularConfigurationBlock block : this.blocks) {
            for (ModularConfigurationModule existingModule : block.getFiles()) {
                if (existingModule == module) {
                    return true;
                }
                if (existingModule != otherModule) continue;
                return false;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        List<ModularConfigurationEntry<ModularConfigurationEntry>> newRemovedEntries = this.entries.getAll();
        newRemovedEntries.forEach(ModularConfigurationEntry::removeSilent);
        this.entries.clear();
        this.blocks.clear();
        newRemovedEntries.forEach(e -> this.removedEntries.put(e.getName(), (ModularConfigurationEntry<T>)e));
    }

    public ModularConfigurationEntry<T> get(String name) {
        ModularConfigurationEntry e = this.entries.getIfExists(name);
        return e != null ? e : this.removedEntries.computeIfAbsent(name, n -> new ModularConfigurationEntry(this, (String)n));
    }

    @Override
    public ModularConfigurationEntry<T> getIfExists(String name) {
        return this.entries.getIfExists(name);
    }

    @Override
    public ModularConfigurationEntry<T> add(String name, ConfigurationNode config) {
        ModularConfigurationEntry<T> entry = this.get(name);
        try {
            if (entry.isReadOnly()) {
                return this.getDefaultModule().add(name, config);
            }
            entry.setConfig(config);
            return entry;
        }
        catch (ReadOnlyModuleException ex) {
            throw new IllegalStateException("Unexpected read-only module exception", ex);
        }
    }

    @Override
    public boolean rename(String name, String newName) {
        if (name.equals(newName)) {
            return true;
        }
        ModularConfigurationEntry<T> entry = this.get(name);
        if (entry.isRemoved()) {
            return false;
        }
        ModularConfigurationEntry<T> target = this.get(newName);
        if (!entry.isReadOnly()) {
            target.createWithConfigInModule(entry.getConfig(), entry.getModule());
            entry.remove();
            return true;
        }
        if (target.isReadOnly()) {
            target.createWithConfigInModule(entry.getConfig(), this.getDefaultModule());
        } else {
            target.setConfig(entry.getConfig());
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    @Override
    public String getName() {
        return this.getDefaultModule().getName();
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
}

