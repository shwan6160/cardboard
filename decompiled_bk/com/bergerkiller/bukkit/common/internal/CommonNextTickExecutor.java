/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommonNextTickExecutor
implements Executor {
    private volatile ExecutorTask executorTask = null;
    public static final CommonNextTickExecutor INSTANCE = new CommonNextTickExecutor();
    public static final Executor MAIN_THREAD = command -> {
        if (CommonUtil.isMainThread()) {
            try {
                command.run();
            }
            catch (Throwable t) {
                CommonNextTickExecutor.handleTaskError(command, t);
            }
        } else {
            INSTANCE.execute(command);
        }
    };

    protected CommonNextTickExecutor() {
    }

    protected synchronized void setExecutorTask(ExecutorTask executorTask) {
        ExecutorTask previousExecutorTask = this.executorTask;
        this.executorTask = executorTask;
        if (executorTask != null) {
            executorTask.start(1L, 1L);
        }
        if (previousExecutorTask != null) {
            previousExecutorTask.stop();
            previousExecutorTask.run();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute(Runnable command) {
        if (command == null) {
            return;
        }
        ExecutorTask task = this.executorTask;
        if (task != null) {
            task.offer(command);
            if (task != this.executorTask) {
                while (true) {
                    Runnable danglingCommand;
                    CommonNextTickExecutor commonNextTickExecutor = this;
                    synchronized (commonNextTickExecutor) {
                        danglingCommand = task.poll();
                    }
                    if (danglingCommand != null) {
                        this.executeFallback(danglingCommand);
                        continue;
                    }
                    break;
                }
            }
        } else {
            this.executeFallback(command);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void executeFallback(Runnable command) {
        Plugin plugin2 = CommonUtil.getPluginByClass(command.getClass());
        if (plugin2 == null) {
            PluginManager pluginManager = Bukkit.getPluginManager();
            synchronized (pluginManager) {
                for (Plugin plugin2 : CommonUtil.getPluginsUnsafe()) {
                    if (!plugin2.isEnabled()) continue;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin2, command);
                    return;
                }
            }
            Logging.LOGGER.log(Level.SEVERE, "Unable to properly schedule next-tick task: " + command.getClass().getName());
            Logging.LOGGER.log(Level.SEVERE, "The task is executed right away instead...we might recover!");
            try {
                command.run();
            }
            catch (Throwable t) {
                CommonNextTickExecutor.handleTaskError(command, t);
            }
        } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin2, command);
        }
    }

    private static void handleTaskError(Runnable command, Throwable error) {
        Logging.LOGGER.log(Level.SEVERE, "An error occurred in next-tick task '" + command.getClass().getName() + "'", CommonUtil.filterStackTrace(error));
    }

    protected static class ExecutorTask
    extends Task {
        private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<Runnable>();
        private final AtomicInteger taskCount = new AtomicInteger(0);

        public void offer(Runnable task) {
            this.tasks.offer(task);
            this.taskCount.incrementAndGet();
        }

        public Runnable poll() {
            Runnable task = this.tasks.poll();
            if (task != null) {
                this.taskCount.decrementAndGet();
            }
            return task;
        }

        public ExecutorTask(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            Runnable command;
            int numTasks = this.taskCount.get();
            while (numTasks-- > 0 && (command = this.tasks.poll()) != null) {
                this.taskCount.decrementAndGet();
                try {
                    command.run();
                }
                catch (Throwable t) {
                    CommonNextTickExecutor.handleTaskError(command, t);
                }
            }
        }
    }
}

