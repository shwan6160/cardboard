/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.config.FileConfiguration
 */
package com.bergerkiller.bukkit.tc.utils.modularconfiguration;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfiguration;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationModule;
import java.io.File;
import java.util.Locale;

public class ModularConfigurationFile<T>
extends ModularConfigurationModule<T> {
    ModularConfigurationFile(ModularConfiguration<T> main, File file) {
        this(main, ModularConfigurationFile.decodeModuleNameFromFile(file), file, !file.canWrite());
    }

    ModularConfigurationFile(ModularConfiguration<T> main, String name, File file, boolean readOnly) {
        super(main, name, (ConfigurationNode)new FileConfiguration(file), readOnly);
    }

    @Override
    protected void loadConfig() {
        ((FileConfiguration)this.config).load();
        super.loadConfig();
    }

    @Override
    public void reload() {
        if (this.configChanged) {
            this.saveChanges();
            return;
        }
        this.main.onModuleRemoved(this);
        this.loadConfig();
        this.main.onModuleAdded(this);
    }

    @Override
    public void saveChanges() {
        if (this.configChanged) {
            if (!this.isReadOnly()) {
                ((FileConfiguration)this.config).save();
            }
            this.configChanged = false;
        }
    }

    @Override
    public void save() {
        if (!this.isReadOnly()) {
            ((FileConfiguration)this.config).save();
        }
        this.configChanged = false;
    }

    static String decodeModuleNameFromFile(File file) {
        String name = file.getName();
        if (name.indexOf(".") > 0) {
            name = name.substring(0, name.lastIndexOf("."));
        }
        name = name.toLowerCase(Locale.ENGLISH);
        return name;
    }
}

