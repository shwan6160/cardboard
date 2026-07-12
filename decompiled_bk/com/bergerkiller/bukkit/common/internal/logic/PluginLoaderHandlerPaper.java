/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.logic.PluginLoaderHandler;
import java.util.List;
import org.bukkit.plugin.Plugin;

class PluginLoaderHandlerPaper
extends PluginLoaderHandler {
    public PluginLoaderHandlerPaper(Plugin plugin, String pluginConfigText) {
        super(plugin, pluginConfigText);
    }

    @Override
    public void bootstrap() {
    }

    @Override
    public void onPluginLoaded(Plugin plugin) {
    }

    @Override
    public void addAccessToClassloader(Plugin plugin) {
    }

    @Override
    public void addAccessToClassloaders(List<String> pluginNames) {
    }
}

