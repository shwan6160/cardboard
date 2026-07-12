/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Task
implements Runnable {
    private final JavaPlugin plugin;
    private int id = -1;

    public Task(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public final JavaPlugin getPlugin() {
        return this.plugin;
    }

    public boolean isRunning() {
        return this.id != -1 && Bukkit.getServer().getScheduler().isCurrentlyRunning(this.id);
    }

    public boolean isQueued() {
        return Bukkit.getServer().getScheduler().isQueued(this.id);
    }

    public static boolean stop(Task task) {
        if (task == null) {
            return false;
        }
        task.stop();
        return true;
    }

    public Task stop() {
        if (this.id != -1) {
            Bukkit.getServer().getScheduler().cancelTask(this.id);
            this.id = -1;
        }
        return this;
    }

    public Task start() {
        this.id = this.plugin.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, (Runnable)this);
        return this;
    }

    public Task start(long delay) {
        this.id = this.plugin.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, (Runnable)this, delay);
        return this;
    }

    public Task start(long delay, long interval) {
        this.id = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, (Runnable)this, delay, interval);
        return this;
    }
}

