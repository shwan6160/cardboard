/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapResourcePack
 *  com.bergerkiller.bukkit.common.map.MapResourcePack$ResourceType
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.utils.modularconfiguration;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfiguration;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationDirectory;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationEntry;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationFile;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationModule;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ReadOnlyModuleException;
import java.io.File;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;

public abstract class BasicModularConfiguration<T>
extends ModularConfiguration<T> {
    public static final String KEY_SAVED_NAME = "savedName";
    public final ModularConfigurationFile<T> DEFAULT;
    public final ModularConfigurationDirectory<T> MODULES;

    public BasicModularConfiguration(Plugin plugin, String mainFilePath, String moduleDirectoryPath) {
        super(plugin.getLogger());
        File data = plugin.getDataFolder();
        this.DEFAULT = this.addFileModule("DEFAULT", new File(data, mainFilePath), false);
        this.MODULES = this.addDirectoryModule(new File(data, moduleDirectoryPath));
    }

    public void addResourcePack(MapResourcePack resourcePack, String namespace, String directory) {
        Set yamlFiles;
        try {
            yamlFiles = resourcePack.listResources(MapResourcePack.ResourceType.YAML, namespace, directory);
        }
        catch (Throwable t) {
            return;
        }
        for (String resource : yamlFiles) {
            ConfigurationNode config;
            String name = resource;
            if (name.startsWith(directory + "/")) {
                name = name.substring(directory.length() + 1);
            }
            try {
                config = resourcePack.getConfig(namespace + ":" + resource);
                int trainCount = config.getKeys().size();
                if (trainCount == 0) continue;
                this.logger.info("[Resource Pack] Loaded " + trainCount + " saved train properties from '" + name + "'");
            }
            catch (Throwable t) {
                this.logger.log(Level.WARNING, "Failed to load resource pack saved train properties '" + name + "'", t);
                continue;
            }
            this.addBlock(new ModularConfigurationModule(this, "RESOURCEPACK:" + name, config, true), false);
        }
    }

    @Override
    public ModularConfigurationModule<T> getDefaultModule() {
        return this.DEFAULT;
    }

    public ModularConfigurationModule<T> createModule(String name) {
        if (name == null || name.isEmpty()) {
            return this.DEFAULT;
        }
        return this.MODULES.createFile(name);
    }

    public ModularConfigurationEntry<T> add(String name, ConfigurationNode config, String moduleName) throws ReadOnlyModuleException {
        ModularConfigurationEntry<T> entry = this.get(name);
        entry.createWithConfigInModule(config, this.createModule(moduleName));
        return entry;
    }
}

