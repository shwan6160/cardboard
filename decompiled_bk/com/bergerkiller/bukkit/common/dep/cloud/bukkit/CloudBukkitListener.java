/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerPreLoginEvent
 *  org.bukkit.event.server.PluginDisableEvent
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit;

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCommandManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

final class CloudBukkitListener<C>
implements Listener {
    private final BukkitCommandManager<C> bukkitCommandManager;

    CloudBukkitListener(@NonNull BukkitCommandManager<C> bukkitCommandManager) {
        this.bukkitCommandManager = bukkitCommandManager;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    void onPlayerLogin(@NonNull AsyncPlayerPreLoginEvent event) {
        this.bukkitCommandManager.lockIfBrigadierCapable();
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    void onPluginDisable(@NonNull PluginDisableEvent event) {
        if (event.getPlugin().equals((Object)this.bukkitCommandManager.owningPlugin())) {
            this.bukkitCommandManager.rootCommands().forEach(this.bukkitCommandManager::deleteRootCommand);
        }
    }
}

