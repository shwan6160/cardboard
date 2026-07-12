/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.control.light;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.control.light.LightAPIControllerForkImpl;
import com.bergerkiller.bukkit.tc.attachments.control.light.LightAPIControllerUnavailable;
import com.bergerkiller.bukkit.tc.attachments.control.light.LightAPIControllerV5Impl;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class LightAPIController {
    private static final Map<World, LightAPIController> _blockLightControllers = new HashMap<World, LightAPIController>();
    private static final Map<World, LightAPIController> _skyLightControllers = new HashMap<World, LightAPIController>();
    private static SyncTask _task;
    private boolean syncPending = false;

    protected LightAPIController() {
    }

    protected void schedule() {
        if (!this.syncPending) {
            this.syncPending = true;
            if (_task == null && (_task = new SyncTask()).getPlugin().isEnabled()) {
                _task.start(1L, 1L);
            }
        }
    }

    public static LightAPIController get(World world, boolean skyLight) {
        Map<World, LightAPIController> map = skyLight ? _skyLightControllers : _blockLightControllers;
        LightAPIController controller = map.get(world);
        if (controller == null) {
            Plugin plugin;
            boolean isLightAPIV5Installed = false;
            try {
                Class<?> typeLightAPI = Class.forName("ru.beykerykt.minecraft.lightapi.common.LightAPI");
                Class<?> typeEditPolicy = Class.forName("ru.beykerykt.minecraft.lightapi.common.api.engine.EditPolicy");
                Class<?> typeSendPolicy = Class.forName("ru.beykerykt.minecraft.lightapi.common.api.engine.SendPolicy");
                Class<?> typeICallBack = Class.forName("ru.beykerykt.minecraft.lightapi.common.api.engine.sched.ICallback");
                typeLightAPI.getMethod("setLightLevel", String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, typeEditPolicy, typeSendPolicy, typeICallBack);
                isLightAPIV5Installed = true;
            }
            catch (ClassNotFoundException | NoSuchMethodException | SecurityException typeLightAPI) {
                // empty catch block
            }
            if (isLightAPIV5Installed) {
                try {
                    controller = skyLight ? LightAPIControllerV5Impl.forSkyLight(world) : LightAPIControllerV5Impl.forBlockLight(world);
                }
                catch (Throwable t) {
                    plugin = Bukkit.getPluginManager().getPlugin("LightAPI");
                    if (plugin == null) {
                        TrainCarts.plugin.getLogger().log(Level.SEVERE, "Failed to initialize LightAPI handler: LightAPI plugin is not enabled!");
                    } else {
                        TrainCarts.plugin.getLogger().log(Level.SEVERE, "Failed to initialize LightAPI handler", t);
                    }
                    controller = LightAPIControllerUnavailable.INSTANCE;
                }
            } else {
                try {
                    controller = skyLight ? LightAPIControllerForkImpl.forSkyLight(world) : LightAPIControllerForkImpl.forBlockLight(world);
                }
                catch (Throwable t) {
                    plugin = Bukkit.getPluginManager().getPlugin("LightAPI");
                    if (plugin == null) {
                        TrainCarts.plugin.getLogger().log(Level.SEVERE, "Failed to initialize LightAPI-Fork handler: LightAPI-Fork plugin is not enabled!");
                    } else if (plugin.getDescription().getMain().equals("ru.beykerykt.minecraft.lightapi.bukkit.impl.BukkitPlugin")) {
                        TrainCarts.plugin.getLogger().log(Level.SEVERE, "Failed to initialize LightAPI-Fork handler: LightAPI is installed, but you need LightAPI-Fork instead!");
                    } else {
                        TrainCarts.plugin.getLogger().log(Level.SEVERE, "Failed to initialize LightAPI-Fork handler", t);
                    }
                    controller = LightAPIControllerUnavailable.INSTANCE;
                }
            }
            map.put(world, controller);
        }
        return controller;
    }

    public static void disableWorld(World world) {
        _blockLightControllers.remove(world);
        _skyLightControllers.remove(world);
    }

    public static void disable() {
        _blockLightControllers.clear();
        _skyLightControllers.clear();
        Task.stop((Task)_task);
        _task = null;
    }

    public abstract void add(IntVector3 var1, int var2);

    public abstract void remove(IntVector3 var1, int var2);

    public abstract void move(IntVector3 var1, IntVector3 var2, int var3);

    public abstract void update(IntVector3 var1, int var2, int var3);

    protected abstract boolean onSync();

    public final boolean sync() {
        this.syncPending = false;
        return this.onSync();
    }

    private static class SyncTask
    extends Task {
        private int ticksIdle = 0;

        public SyncTask() {
            super((JavaPlugin)TrainCarts.plugin);
        }

        public void run() {
            boolean busy = false;
            for (LightAPIController controller : _blockLightControllers.values()) {
                busy |= controller.sync();
            }
            for (LightAPIController controller : _skyLightControllers.values()) {
                busy |= controller.sync();
            }
            if (busy) {
                this.ticksIdle = 0;
            } else if (++this.ticksIdle > 100) {
                this.stop();
                _task = null;
            }
        }
    }
}

