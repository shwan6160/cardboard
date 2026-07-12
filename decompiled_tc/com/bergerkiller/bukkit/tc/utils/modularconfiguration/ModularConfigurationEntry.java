/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.tc.utils.modularconfiguration;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.EntryRemovedException;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfiguration;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationModule;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ReadOnlyModuleException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class ModularConfigurationEntry<T>
implements Comparable<ModularConfigurationEntry<T>> {
    private final ModularConfiguration<T> main;
    ModularConfigurationModule<T> module;
    final List<ModularConfigurationModule<T>> shadowModules;
    private final String name;
    private final ConfigurationNode config;
    private T cachedValue;

    ModularConfigurationEntry(ModularConfiguration<T> main, String name) {
        this(main, name, new ConfigurationNode(), null);
    }

    ModularConfigurationEntry(ModularConfiguration<T> main, String name, ConfigurationNode config, ModularConfigurationModule<T> module) {
        this.main = main;
        this.module = module;
        this.shadowModules = new ArrayList<ModularConfigurationModule<T>>(2);
        this.name = name;
        this.config = config;
        this.cachedValue = null;
    }

    public T get() {
        T value = this.cachedValue;
        if (value == null) {
            this.cachedValue = value = this.main.decodeConfig(this);
        }
        return value;
    }

    public ModularConfiguration<T> getMain() {
        return this.main;
    }

    public ModularConfigurationModule<T> getModule() {
        return this.module;
    }

    public boolean isRemoved() {
        return this.module == null;
    }

    public boolean isReadOnly() {
        return this.module == null || this.module.isReadOnly();
    }

    public String getName() {
        return this.name;
    }

    public ConfigurationNode getConfig() {
        return this.config;
    }

    public ConfigurationNode getWritableConfig() {
        if (this.isRemoved()) {
            throw new EntryRemovedException();
        }
        return this.config;
    }

    public void setConfig(ConfigurationNode config) throws ReadOnlyModuleException {
        if (this.isRemoved()) {
            throw new EntryRemovedException();
        }
        if (this.module.isReadOnly()) {
            throw new ReadOnlyModuleException();
        }
        this.config.setToExcept((YamlNodeAbstract)config, Collections.singleton("savedName"));
        this.main.postProcessEntryConfiguration(this);
    }

    public void createWithConfigInModule(ConfigurationNode config, ModularConfigurationModule<T> module) throws ReadOnlyModuleException {
        if (module.isReadOnly()) {
            throw new ReadOnlyModuleException();
        }
        if (this.isRemoved()) {
            module.store(this);
            this.main.removedEntries.remove(this.name);
            this.main.entries.set(this.name, this);
        } else {
            this.setModule(module);
        }
        this.config.setToExcept((YamlNodeAbstract)config, Collections.singleton("savedName"));
        this.main.postProcessEntryConfiguration(this);
    }

    public void remove() throws ReadOnlyModuleException {
        if (this.isRemoved()) {
            throw new EntryRemovedException();
        }
        if (this.module.isReadOnly()) {
            throw new ReadOnlyModuleException();
        }
        this.module.removeInModule(this.name);
        this.module = null;
        this.onFileModuleDetached();
    }

    void onModuleRemoved(ModularConfigurationModule<T> module) {
        if (this.module == module) {
            this.module = null;
            this.onFileModuleDetached();
        } else {
            this.shadowModules.remove(module);
        }
    }

    private void onFileModuleDetached() {
        if (!this.shadowModules.isEmpty()) {
            this.loadFromModule(this.shadowModules.remove(this.shadowModules.size() - 1));
        } else {
            this.config.clear();
            this.main.entries.remove(this.name);
            this.main.removedEntries.put(this.name, this);
        }
    }

    public void setModule(ModularConfigurationModule<T> module) throws ReadOnlyModuleException {
        if (this.module == module) {
            return;
        }
        if (this.isRemoved()) {
            throw new EntryRemovedException();
        }
        if (module == null) {
            throw new IllegalArgumentException("Module is null");
        }
        if (this.main != module.getMain()) {
            throw new IllegalArgumentException("Module is in a different modular configuration");
        }
        if (module.isReadOnly()) {
            throw new ReadOnlyModuleException();
        }
        if (this.module.isReadOnly()) {
            this.shadowModules.add(this.module);
            this.detachAsShadowCopy();
        } else {
            this.module.removeInModule(this.name);
        }
        this.shadowModules.remove(module);
        module.store(this);
    }

    public void copyTo(ModularConfigurationEntry<T> targetEntry) throws ReadOnlyModuleException {
        if (this == targetEntry) {
            return;
        }
        if (targetEntry == null) {
            throw new IllegalArgumentException("Target entry is null");
        }
        if (targetEntry.main != this.main) {
            throw new IllegalArgumentException("Target entry is in a different modular configuration");
        }
        if (this.isRemoved() || targetEntry.isRemoved()) {
            throw new EntryRemovedException();
        }
        if (targetEntry.module.isReadOnly()) {
            throw new ReadOnlyModuleException();
        }
        targetEntry.config.setToExcept((YamlNodeAbstract)this.config, Collections.singleton("savedName"));
        this.main.postProcessEntryConfiguration(targetEntry);
    }

    void removeSilent() {
        this.detachAsShadowCopy();
        this.shadowModules.clear();
        this.module = null;
        this.config.clear();
    }

    void detachAsShadowCopy() {
        ModularConfigurationModule<T> module = this.module;
        if (module != null) {
            boolean wasChanged = module.configChanged;
            module.store(new ModularConfigurationEntry<T>(this.main, this.name, this.config.clone(), this.module));
            module.configChanged = wasChanged;
            this.module = null;
        }
    }

    void loadFromModule(ModularConfigurationModule<T> module) {
        if (this.module == module) {
            return;
        }
        boolean wasRemoved = this.isRemoved();
        if (!wasRemoved) {
            this.shadowModules.add(this.module);
            this.detachAsShadowCopy();
        }
        boolean wasChanged = module.configChanged;
        this.config.setToExcept((YamlNodeAbstract)module.config.getNode(this.name), Collections.singleton("savedName"));
        module.store(this);
        module.configChanged = wasChanged;
        if (wasRemoved) {
            this.main.removedEntries.remove(this.name);
            this.main.entries.set(this.name, this);
        }
    }

    @Override
    public int compareTo(@NotNull ModularConfigurationEntry<T> tModularConfigurationEntry) {
        return this.name.compareTo(tModularConfigurationEntry.name);
    }

    public static interface Container<T> {
        public String getName();

        public ModularConfigurationEntry<T> getIfExists(String var1);

        public ModularConfigurationEntry<T> add(String var1, ConfigurationNode var2) throws ReadOnlyModuleException;

        default public ModularConfigurationEntry<T> remove(String name) throws ReadOnlyModuleException {
            ModularConfigurationEntry<T> entry = this.getIfExists(name);
            if (entry == null) {
                return null;
            }
            entry.remove();
            return entry;
        }

        public boolean rename(String var1, String var2);

        public List<String> getNames();

        public List<ModularConfigurationEntry<T>> getAll();

        public List<T> getAllValues();

        public boolean isEmpty();
    }
}

