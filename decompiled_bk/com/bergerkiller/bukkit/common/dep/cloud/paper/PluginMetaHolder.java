/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.plugin.configuration.PluginMeta
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper;

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.PluginHolder;
import io.papermc.paper.plugin.configuration.PluginMeta;
import java.util.Objects;
import org.apiguardian.api.API;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public interface PluginMetaHolder
extends PluginHolder {
    public PluginMeta owningPluginMeta();

    @Override
    default public Plugin owningPlugin() {
        return Objects.requireNonNull(Bukkit.getPluginManager().getPlugin(this.owningPluginMeta().getName()), () -> this.owningPluginMeta().getName() + " Plugin instance");
    }

    @API(status=API.Status.INTERNAL)
    public static PluginMetaHolder fromPluginHolder(final PluginHolder pluginHolder) {
        return new PluginMetaHolder(){

            @Override
            public PluginMeta owningPluginMeta() {
                return pluginHolder.owningPlugin().getPluginMeta();
            }

            @Override
            public Plugin owningPlugin() {
                return pluginHolder.owningPlugin();
            }
        };
    }
}

