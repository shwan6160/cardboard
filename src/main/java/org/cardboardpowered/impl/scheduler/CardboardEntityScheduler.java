package org.cardboardpowered.impl.scheduler;

import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class CardboardEntityScheduler implements EntityScheduler {
    private final Entity entity;

    public CardboardEntityScheduler(Entity entity) {
        this.entity = entity;
    }

    @Override
    public boolean execute(Plugin plugin, Runnable retired, Runnable run, long delay) {
        if (delay <= 0) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (entity.isValid()) {
                    run.run();
                } else if (retired != null) {
                    retired.run();
                }
            });
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (entity.isValid()) {
                    run.run();
                } else if (retired != null) {
                    retired.run();
                }
            }, delay);
        }
        return true;
    }

    @Override
    public ScheduledTask run(Plugin plugin, Consumer<ScheduledTask> taskConsumer, Runnable retired) {
        CardboardScheduledTask task = new CardboardScheduledTask(plugin, false);
        BukkitTask bukkitTask = Bukkit.getScheduler().runTask(plugin, () -> {
            if (entity.isValid()) {
                taskConsumer.accept(task);
            } else {
                if (retired != null) {
                    retired.run();
                }
            }
        });
        task.setBukkitTask(bukkitTask);
        return task;
    }

    @Override
    public ScheduledTask runDelayed(Plugin plugin, Consumer<ScheduledTask> taskConsumer, Runnable retired, long delay) {
        CardboardScheduledTask task = new CardboardScheduledTask(plugin, false);
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (entity.isValid()) {
                taskConsumer.accept(task);
            } else {
                if (retired != null) {
                    retired.run();
                }
            }
        }, delay);
        task.setBukkitTask(bukkitTask);
        return task;
    }

    @Override
    public ScheduledTask runAtFixedRate(Plugin plugin, Consumer<ScheduledTask> taskConsumer, Runnable retired, long delay, long period) {
        CardboardScheduledTask task = new CardboardScheduledTask(plugin, true);
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (entity.isValid()) {
                taskConsumer.accept(task);
            } else {
                if (retired != null) {
                    retired.run();
                    task.cancel();
                }
            }
        }, delay, period);
        task.setBukkitTask(bukkitTask);
        return task;
    }
}
