/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import java.util.logging.Level;

public abstract class AsyncTask
implements Runnable {
    private boolean running = false;
    private boolean stoprequested = false;
    private boolean looped = false;
    private final Thread thread;

    public AsyncTask() {
        this(null);
    }

    public AsyncTask(String name) {
        this(name, 0);
    }

    public AsyncTask(String name, int priority) {
        final AsyncTask task = this;
        this.thread = new Thread(this){
            final /* synthetic */ AsyncTask this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void run() {
                task.running = true;
                try {
                    if (task.looped) {
                        while (!task.stoprequested) {
                            task.run();
                        }
                    } else {
                        task.run();
                    }
                }
                catch (Throwable t) {
                    CommonUtil.printFilteredStackTrace(t);
                }
                task.running = false;
            }
        };
        this.thread.setDaemon(true);
        if (priority >= 1 && priority <= 10) {
            this.thread.setPriority(priority);
        }
        if (name != null) {
            this.thread.setName(name);
        }
    }

    public static void sleep(long msdelay) {
        try {
            Thread.sleep(msdelay);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public boolean isStopRequested() {
        return this.stoprequested;
    }

    public AsyncTask start() {
        return this.start(false);
    }

    public AsyncTask start(boolean looped) {
        this.stoprequested = false;
        this.looped = looped;
        try {
            this.thread.start();
        }
        catch (IllegalThreadStateException ex) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to start async task", ex);
        }
        return this;
    }

    public static boolean stop(AsyncTask task) {
        if (task == null) {
            return false;
        }
        task.stop();
        return true;
    }

    public AsyncTask stop() {
        this.stoprequested = true;
        return this;
    }

    public final AsyncTask waitFinished() {
        while (this.running) {
            AsyncTask.sleep(20L);
        }
        return this;
    }
}

