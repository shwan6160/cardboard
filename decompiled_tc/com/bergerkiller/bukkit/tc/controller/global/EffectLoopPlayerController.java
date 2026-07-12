/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.component.LibraryComponent
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.controller.global;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.control.effect.EffectLoop;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class EffectLoopPlayerController
implements LibraryComponent,
TrainCarts.Provider {
    private final TrainCarts plugin;
    private final Queue<EffectLoop> startPendingSync = new ConcurrentLinkedQueue<EffectLoop>();
    private final List<EffectLoop> syncRunning = new ArrayList<EffectLoop>();
    private final AsyncWorker asyncWorker = new AsyncWorker(1);

    public EffectLoopPlayerController(TrainCarts plugin) {
        this.plugin = plugin;
    }

    @Override
    public TrainCarts getTrainCarts() {
        return this.plugin;
    }

    public EffectLoop.Player createPlayer() {
        return new EffectLoopPlayer();
    }

    public EffectLoop.Player createPlayer(int limit) {
        return new EffectLoopPlayer(limit);
    }

    public void enable() {
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, this.asyncWorker::start);
    }

    public void disable() {
        this.asyncWorker.stop();
        this.syncRunning.clear();
        this.startPendingSync.clear();
    }

    public void updateSync() {
        EffectLoop loop;
        while ((loop = this.startPendingSync.poll()) != null) {
            this.syncRunning.add(loop);
        }
        this.syncRunning.removeIf(e -> !e.advance(EffectLoop.Time.ONE_TICK, EffectLoop.Time.ZERO, false));
    }

    private void schedule(EffectLoop loop, EffectLoop.RunMode runMode) {
        if (runMode == EffectLoop.RunMode.SYNCHRONOUS) {
            this.startPendingSync.add(loop);
        } else {
            this.asyncWorker.schedule(loop);
        }
    }

    private static class AsyncWorker {
        private static final long INTERVAL = 25000000L;
        private final Queue<EffectLoop> startPendingAsync = new ConcurrentLinkedQueue<EffectLoop>();
        private final Thread effectLoopThread;
        private volatile boolean stopping = false;

        public AsyncWorker(int n) {
            this.effectLoopThread = new Thread(this::processAsync, "TrainCarts.EffectLoopPlayer" + n);
            this.effectLoopThread.setDaemon(true);
        }

        public void start() {
            this.stopping = false;
            this.effectLoopThread.start();
        }

        public void stop() {
            this.stopping = true;
            try {
                this.effectLoopThread.join(1000L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            this.startPendingAsync.clear();
        }

        public void schedule(EffectLoop loop) {
            this.startPendingAsync.add(loop);
        }

        public void processAsync() {
            EffectLoop.Time zero_duration = EffectLoop.Time.ZERO;
            ArrayList<EffectLoop> asyncRunning = new ArrayList<EffectLoop>();
            long lastTime = System.nanoTime();
            long parkUntil = lastTime + 25000000L;
            while (!this.stopping) {
                EffectLoop loop;
                LockSupport.parkNanos(parkUntil - System.nanoTime());
                long now = System.nanoTime();
                EffectLoop.Time elapsedTime = EffectLoop.Time.nanos(now - lastTime);
                lastTime = now;
                if (now >= (parkUntil += 25000000L) + 25000000L) {
                    parkUntil = now;
                }
                while ((loop = this.startPendingAsync.poll()) != null) {
                    asyncRunning.add(loop);
                }
                asyncRunning.removeIf(e -> !e.advance(elapsedTime, zero_duration, false));
            }
        }
    }

    private class EffectLoopPlayer
    implements EffectLoop.Player,
    TrainCarts.Provider {
        private final Semaphore semaphore;

        public EffectLoopPlayer() {
            this.semaphore = new Semaphore(TCConfig.maxConcurrentEffectLoops);
        }

        public EffectLoopPlayer(int limit) {
            this.semaphore = limit == 0 ? new Semaphore(1) : (limit < 0 ? new Semaphore(TCConfig.maxConcurrentEffectLoops) : new Semaphore(Math.min(limit, TCConfig.maxConcurrentEffectLoops)));
        }

        @Override
        public TrainCarts getTrainCarts() {
            return EffectLoopPlayerController.this.plugin;
        }

        @Override
        public void play(EffectLoop loop, EffectLoop.RunMode runMode) {
            if (this.semaphore.tryAcquire()) {
                EffectLoopPlayerController.this.schedule(new EffectLoopWrap(this, loop), runMode);
            }
        }

        public void onEffectLoopDone() {
            this.semaphore.release();
        }
    }

    private static class EffectLoopWrap
    implements EffectLoop {
        private final EffectLoopPlayer player;
        private final EffectLoop base;

        public EffectLoopWrap(EffectLoopPlayer player, EffectLoop loop) {
            this.player = player;
            this.base = loop;
        }

        @Override
        public boolean advance(EffectLoop.Time dt, EffectLoop.Time duration, boolean loop) {
            try {
                if (this.base.advance(dt, duration, loop)) {
                    return true;
                }
            }
            catch (Throwable t) {
                this.player.getTrainCarts().getLogger().log(Level.SEVERE, "An error occurred inside an effect loop", t);
            }
            this.player.onEffectLoopDone();
            return false;
        }

        @Override
        public void resetToBeginning() {
            this.base.resetToBeginning();
        }
    }
}

