/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common;

import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public abstract class RunOnceTask
implements Runnable {
    private static final int TASK_NOT_SCHEDULED = -1;
    private static final int TASK_SCHEDULED_SOON = -2;
    private final Runnable _logicProxy;
    private final Plugin _plugin;
    private final AtomicInteger _scheduledId;

    public RunOnceTask(Plugin plugin) {
        this._plugin = plugin;
        this._scheduledId = new AtomicInteger(-1);
        this._logicProxy = () -> {
            int oldTaskId;
            while (!this._scheduledId.compareAndSet(oldTaskId = this._scheduledId.get(), -1)) {
            }
            if (oldTaskId >= 0) {
                this.run();
            }
        };
    }

    public static RunOnceTask create(Plugin plugin, final Runnable runnable) {
        return new RunOnceTask(plugin){

            @Override
            public void run() {
                runnable.run();
            }
        };
    }

    public Plugin getPlugin() {
        return this._plugin;
    }

    public boolean isScheduled() {
        return this._scheduledId.get() != -1;
    }

    public void restart(long delay) {
        int newTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask(this._plugin, this._logicProxy, delay);
        int previousTaskId = this._scheduledId.getAndSet(newTaskId);
        if (previousTaskId >= 0) {
            Bukkit.getScheduler().cancelTask(previousTaskId);
        }
    }

    public void start(long delay) {
        int taskId;
        if (this._scheduledId.compareAndSet(-1, -2) && !this._scheduledId.compareAndSet(-2, taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(this._plugin, this._logicProxy, delay))) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    public void start() {
        int taskId;
        if (this._scheduledId.compareAndSet(-1, -2) && !this._scheduledId.compareAndSet(-2, taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(this._plugin, this._logicProxy))) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    public void runNowIfScheduled() {
        int oldTaskId;
        while (!this._scheduledId.compareAndSet(oldTaskId = this._scheduledId.get(), -1)) {
        }
        if (oldTaskId >= 0) {
            Bukkit.getScheduler().cancelTask(oldTaskId);
            this.run();
        }
    }

    public void cancel() {
        int oldTaskId;
        while (!this._scheduledId.compareAndSet(oldTaskId = this._scheduledId.get(), -1)) {
        }
        if (oldTaskId >= 0) {
            Bukkit.getScheduler().cancelTask(oldTaskId);
        }
    }
}

