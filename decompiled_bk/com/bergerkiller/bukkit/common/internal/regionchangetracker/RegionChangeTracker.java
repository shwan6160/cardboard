/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.server.PluginDisableEvent
 *  org.bukkit.event.server.PluginEnableEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common.internal.regionchangetracker;

import com.bergerkiller.bukkit.common.internal.regionchangetracker.FastAsyncWorldEditHandlerV1;
import com.bergerkiller.bukkit.common.internal.regionchangetracker.FastAsyncWorldEditHandlerV2;
import com.bergerkiller.bukkit.common.internal.regionchangetracker.RegionChangeTrackerHandler;
import com.bergerkiller.bukkit.common.internal.regionchangetracker.RegionChangeTrackerHandlerOps;
import com.bergerkiller.bukkit.common.internal.regionchangetracker.WorldEditHandlerV1;
import com.bergerkiller.bukkit.common.internal.regionchangetracker.WorldEditHandlerV2;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class RegionChangeTracker
implements RegionChangeTrackerHandlerOps {
    private final JavaPlugin plugin;
    private final Handler[] all_handlers;
    private Plugin pluginCurrentlyDisabling = null;

    public RegionChangeTracker(JavaPlugin plugin) {
        this.plugin = plugin;
        ArrayList<Handler> handlers = new ArrayList<Handler>();
        this.initHandler(handlers, WorldEditHandlerV1::new);
        this.initHandler(handlers, WorldEditHandlerV2::new);
        this.initHandler(handlers, FastAsyncWorldEditHandlerV1::new);
        this.initHandler(handlers, FastAsyncWorldEditHandlerV2::new);
        this.all_handlers = handlers.toArray(new Handler[handlers.size()]);
    }

    private void initHandler(List<Handler> handlers, Supplier<RegionChangeTrackerHandler> supplier) {
        try {
            handlers.add(new Handler(supplier.get()));
        }
        catch (Throwable t) {
            this.plugin.getLogger().log(Level.SEVERE, "[RegionChangeTracker] Failed to register a handler", t);
        }
    }

    public void enable() {
        this.refreshHandlers();
        Bukkit.getPluginManager().registerEvents(new Listener(){

            @EventHandler(priority=EventPriority.MONITOR)
            public void onPluginAfterEnabled(PluginEnableEvent event) {
                RegionChangeTracker.this.refreshHandlers();
            }

            @EventHandler(priority=EventPriority.MONITOR)
            public void onPluginBeforeDisable(PluginDisableEvent event) {
                RegionChangeTracker.this.pluginCurrentlyDisabling = event.getPlugin();
                RegionChangeTracker.this.refreshHandlers();
                RegionChangeTracker.this.pluginCurrentlyDisabling = null;
            }
        }, (Plugin)this.plugin);
    }

    public void disable() {
        for (Handler handler : this.all_handlers) {
            handler.disable(false);
        }
    }

    private void refreshHandlers() {
        for (Handler handler : this.all_handlers) {
            handler.refresh();
        }
    }

    @Override
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public Plugin findPluginEnabledOrProvided(String pluginName) {
        Plugin p = Bukkit.getPluginManager().getPlugin(pluginName);
        if (p != null && p != this.pluginCurrentlyDisabling && p.isEnabled()) {
            return p;
        }
        try {
            for (Plugin p2 : Bukkit.getPluginManager().getPlugins()) {
                if (p2 == this.pluginCurrentlyDisabling || !p2.isEnabled()) continue;
                for (String provide : p2.getDescription().getProvides()) {
                    if (!pluginName.equalsIgnoreCase(provide)) continue;
                    return p2;
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private class Handler {
        public final RegionChangeTrackerHandler handler;
        public RegionChangeTrackerHandler.HandlerInstance<?> instance;
        private boolean enableFailed;

        public Handler(RegionChangeTrackerHandler handler) {
            this.handler = handler;
            this.instance = null;
            this.enableFailed = false;
        }

        public void refresh() {
            if (this.handler.isSupported(RegionChangeTracker.this)) {
                if (this.instance == null && !this.enableFailed) {
                    try {
                        this.instance = this.handler.enable(RegionChangeTracker.this);
                        RegionChangeTracker.this.plugin.getLogger().info("[RegionChangeTracker] Region block changes will be notified from " + this.handler.name());
                    }
                    catch (Throwable t) {
                        this.enableFailed = true;
                        RegionChangeTracker.this.plugin.getLogger().log(Level.SEVERE, "[RegionChangeTracker] Failed to enable handler for " + this.handler.name(), t);
                    }
                }
            } else {
                this.disable(true);
            }
        }

        public void disable(boolean logDisabled) {
            this.enableFailed = false;
            if (this.instance != null) {
                try {
                    this.instance.disable();
                    if (logDisabled) {
                        RegionChangeTracker.this.plugin.getLogger().info("[RegionChangeTracker] Region block changes will no longer be notified from " + this.handler.name());
                    }
                }
                catch (Throwable t) {
                    RegionChangeTracker.this.plugin.getLogger().log(Level.SEVERE, "[RegionChangeTracker] Failed to disable handler for " + this.handler.name(), t);
                }
                this.instance = null;
            }
        }
    }
}

