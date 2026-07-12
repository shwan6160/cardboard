package org.cardboardpowered.impl.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class CardboardScheduledTask implements ScheduledTask {
    private final Plugin plugin;
    private BukkitTask bukkitTask;
    private final boolean repeating;
    private volatile boolean cancelled = false;

    public CardboardScheduledTask(Plugin plugin, boolean repeating) {
        this.plugin = plugin;
        this.repeating = repeating;
    }

    public void setBukkitTask(BukkitTask bukkitTask) {
        this.bukkitTask = bukkitTask;
    }

    @Override
    public Plugin getOwningPlugin() {
        return this.plugin;
    }

    @Override
    public boolean isRepeatingTask() {
        return this.repeating;
    }

    @Override
    public io.papermc.paper.threadedregions.scheduler.ScheduledTask.CancelledState cancel() {
        if (this.cancelled) {
            return io.papermc.paper.threadedregions.scheduler.ScheduledTask.CancelledState.CANCELLED_ALREADY;
        }
        this.cancelled = true;
        if (this.bukkitTask != null) {
            this.bukkitTask.cancel();
        }
        return io.papermc.paper.threadedregions.scheduler.ScheduledTask.CancelledState.CANCELLED_BY_CALLER;
    }

    @Override
    public io.papermc.paper.threadedregions.scheduler.ScheduledTask.ExecutionState getExecutionState() {
        if (this.cancelled) {
            return io.papermc.paper.threadedregions.scheduler.ScheduledTask.ExecutionState.CANCELLED;
        }
        return io.papermc.paper.threadedregions.scheduler.ScheduledTask.ExecutionState.IDLE;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
