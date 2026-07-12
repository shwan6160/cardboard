/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.server.PluginDisableEvent
 *  org.bukkit.event.server.PluginEnableEvent
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.regionflagtracker;

import com.bergerkiller.bukkit.common.regionflagtracker.RegionFlagRegistry;
import com.bergerkiller.bukkit.common.regionflagtracker.RegionFlagTracker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public abstract class RegionFlagRegistryBaseImpl
extends RegionFlagRegistry {
    private boolean enabled = false;
    private boolean ready = false;

    public static RegionFlagRegistryBaseImpl instance() {
        return (RegionFlagRegistryBaseImpl)RegionFlagRegistry.instance();
    }

    public synchronized void enable(final Plugin libraryPlugin) {
        this.enabled = true;
        Bukkit.getPluginManager().registerEvents(new Listener(){

            @EventHandler(priority=EventPriority.MONITOR)
            public void onPluginAfterEnable(PluginEnableEvent event) {
                RegionFlagRegistryBaseImpl.this.tryMakeReady(libraryPlugin);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @EventHandler(priority=EventPriority.MONITOR)
            public void onPluginBeforeDisable(PluginDisableEvent event) {
                Plugin disabledPlugin = event.getPlugin();
                RegionFlagRegistryBaseImpl regionFlagRegistryBaseImpl = RegionFlagRegistryBaseImpl.this;
                synchronized (regionFlagRegistryBaseImpl) {
                    List<RegionFlagRegistry.RegisteredRegionFlag> handlersToUnregister = Collections.emptyList();
                    Set unregisteredFlags = Collections.emptySet();
                    Iterator iter = RegionFlagRegistryBaseImpl.this.registeredFlags.iterator();
                    while (iter.hasNext()) {
                        RegionFlagRegistry.RegisteredRegionFlag registeredFlag = (RegionFlagRegistry.RegisteredRegionFlag)iter.next();
                        if (registeredFlag.plugin != disabledPlugin) continue;
                        iter.remove();
                        if (handlersToUnregister.isEmpty()) {
                            handlersToUnregister = new ArrayList();
                        }
                        handlersToUnregister.add(registeredFlag);
                        if (unregisteredFlags.isEmpty()) {
                            unregisteredFlags = new HashSet();
                        }
                        unregisteredFlags.add(registeredFlag.flag);
                    }
                    if (!unregisteredFlags.isEmpty()) {
                        Iterator trackerIter = RegionFlagRegistryBaseImpl.this.trackers.values().iterator();
                        while (trackerIter.hasNext()) {
                            if (!unregisteredFlags.contains(((RegionFlagTracker)trackerIter.next()).getFlag())) continue;
                            trackerIter.remove();
                        }
                    }
                    handlersToUnregister.forEach(RegionFlagRegistry.RegisteredRegionFlag::unregisterHandler);
                }
            }

            @EventHandler(priority=EventPriority.MONITOR)
            public void onPlayerQuit(PlayerQuitEvent event) {
                RegionFlagRegistryBaseImpl.this.removeTrackersOfPlayer(event.getPlayer());
            }
        }, libraryPlugin);
        this.tryMakeReady(libraryPlugin);
    }

    private void tryMakeReady(Plugin libraryPlugin) {
        if (this.ready || !this.enabled) {
            return;
        }
        this.ready = this.isStateReady();
        if (this.ready) {
            for (RegionFlagRegistry.RegisteredRegionFlag registeredFlag : this.registeredFlags) {
                registeredFlag.registerHandler();
            }
            this.onStateIsReady(libraryPlugin);
        }
    }

    private synchronized void removeTrackersOfPlayer(Player player) {
        for (RegionFlagRegistry.RegisteredRegionFlag registeredFlag : this.registeredFlags) {
            this.trackers.remove(new RegionFlagRegistry.PlayerFlagKey(player, registeredFlag.flag));
        }
    }

    public synchronized void disable() {
        this.enabled = false;
        if (this.ready) {
            this.ready = false;
            for (RegionFlagRegistry.RegisteredRegionFlag registeredFlag : this.registeredFlags) {
                registeredFlag.unregisterHandler();
            }
        }
        this.registeredFlags.clear();
        this.trackers.clear();
    }

    @Override
    protected void onFlagRegistered(RegionFlagRegistry.RegisteredRegionFlag<?> registeredFlag) {
        if (this.ready) {
            registeredFlag.registerHandler();
        }
    }

    protected abstract boolean isStateReady();

    protected void onStateIsReady(Plugin libraryPlugin) {
    }

    static Plugin findPlugin(String pluginName, Predicate<Plugin> condition) {
        Plugin p = Bukkit.getPluginManager().getPlugin(pluginName);
        if (p != null && condition.test(p)) {
            return p;
        }
        try {
            for (Plugin p2 : Bukkit.getPluginManager().getPlugins()) {
                for (String provide : p2.getDescription().getProvides()) {
                    if (!pluginName.equalsIgnoreCase(provide) || !condition.test(p2)) continue;
                    return p2;
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }
}

