/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.chunk;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.chunk.ForcedChunk;
import com.bergerkiller.bukkit.common.chunk.ForcedChunkManager;
import java.lang.ref.Cleaner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

abstract class ForcedChunkCleaner {
    ForcedChunkCleaner() {
    }

    public static ForcedChunkCleaner create() {
        try {
            Class.forName("java.lang.ref.Cleaner");
            return ForcedChunkCleaner.createCleanerJDK9();
        }
        catch (Throwable t) {
            return ForcedChunkCleaner.createCleanerJDK8();
        }
    }

    public abstract void shutdown();

    public abstract ForcedChunk createDefault(ForcedChunkManager.ForcedChunkEntry var1);

    public abstract ForcedChunk createAndTrackStack(ForcedChunkManager.ForcedChunkEntry var1, Throwable var2);

    private static ForcedChunkCleaner createCleanerJDK8() {
        return new CleanerJDK8();
    }

    private static ForcedChunkCleaner createCleanerJDK9() {
        return new CleanerJDK9();
    }

    private static class CleanerJDK8
    extends ForcedChunkCleaner {
        private final AtomicReference<PendingItem> pending = new AtomicReference();
        private final Object shutdownWaitLock = new Object();
        private boolean shutdown = false;
        private final Thread processThread = new Thread(this::processLoop, "ForcedChunkCleanerThread");

        public CleanerJDK8() {
            this.processThread.setDaemon(true);
            this.processThread.start();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void shutdown() {
            Object object = this.shutdownWaitLock;
            synchronized (object) {
                this.shutdown = true;
                this.shutdownWaitLock.notifyAll();
            }
            try {
                this.processThread.join(10000L);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }

        @Override
        public ForcedChunk createDefault(ForcedChunkManager.ForcedChunkEntry entry) {
            return new ForcedChunkHandlingFinalize(this, entry, null);
        }

        @Override
        public ForcedChunk createAndTrackStack(ForcedChunkManager.ForcedChunkEntry entry, Throwable stack) {
            return new ForcedChunkHandlingFinalize(this, entry, stack);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void processLoop() {
            boolean shutdown;
            do {
                PendingItem item;
                Object object = this.shutdownWaitLock;
                synchronized (object) {
                    try {
                        this.shutdownWaitLock.wait(2000L);
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                    shutdown = this.shutdown;
                }
                while ((item = (PendingItem)this.pending.getAndSet(null)) != null) {
                    ArrayList<PendingItem> allItems = new ArrayList<PendingItem>();
                    PendingItem p = item;
                    while (p != null) {
                        if (!p.processed.getAndSet(true)) {
                            allItems.add(p);
                        }
                        p = p.prev;
                    }
                    Collections.reverse(allItems);
                    allItems.forEach(PendingItem::clean);
                }
            } while (!shutdown);
        }

        public synchronized void processLater(ForcedChunkManager.ForcedChunkEntry entry, Throwable stack) {
            this.pending.set(new PendingItem(this.pending.get(), entry, stack));
        }

        private static class PendingItem {
            public final PendingItem prev;
            public final AtomicBoolean processed;
            public final CleanerFunction cleaner;

            public PendingItem(PendingItem prev, ForcedChunkManager.ForcedChunkEntry entry, Throwable stack) {
                this.prev = prev;
                this.processed = new AtomicBoolean(false);
                this.cleaner = stack == null ? new CleanerFunction(new AtomicReference<ForcedChunkManager.ForcedChunkEntry>(entry)) : new CleanerFunctionStackTracked(new AtomicReference<ForcedChunkManager.ForcedChunkEntry>(entry), stack);
            }

            public void clean() {
                try {
                    try {
                        this.cleaner.run();
                    }
                    catch (Throwable t) {
                        Logging.LOGGER.log(Level.SEVERE, "Error occurred handling missed ForcedChunk close()", t);
                    }
                }
                catch (Throwable t2) {
                    t2.printStackTrace();
                }
            }
        }
    }

    private static class CleanerJDK9
    extends ForcedChunkCleaner {
        private final Cleaner cleaner = Cleaner.create();

        private CleanerJDK9() {
        }

        @Override
        public void shutdown() {
        }

        @Override
        public ForcedChunk createDefault(ForcedChunkManager.ForcedChunkEntry entry) {
            ForcedChunk chunk = new ForcedChunk(entry);
            this.cleaner.register(chunk, new CleanerFunction(chunk.entry));
            return chunk;
        }

        @Override
        public ForcedChunk createAndTrackStack(ForcedChunkManager.ForcedChunkEntry entry, Throwable stack) {
            ForcedChunk chunk = new ForcedChunk(entry);
            this.cleaner.register(chunk, new CleanerFunctionStackTracked(chunk.entry, stack));
            return chunk;
        }
    }

    private static class CleanerFunction
    implements Runnable {
        private final AtomicReference<ForcedChunkManager.ForcedChunkEntry> entry;

        public CleanerFunction(AtomicReference<ForcedChunkManager.ForcedChunkEntry> entry) {
            this.entry = entry;
        }

        public void log(ForcedChunkManager.ForcedChunkEntry entry) {
            Logging.LOGGER_DEBUG.log(Level.WARNING, "ForcedChunk.close() was not called for " + entry.toString());
            entry.getManager().setTrackingCreationStack(true);
        }

        public final void clean(ForcedChunkManager.ForcedChunkEntry entry) {
            try {
                this.log(entry);
            }
            finally {
                entry.remove();
            }
        }

        @Override
        public final void run() {
            ForcedChunkManager.ForcedChunkEntry entry = this.entry.getAndSet(null);
            if (entry != null) {
                this.clean(entry);
            }
        }
    }

    private static final class CleanerFunctionStackTracked
    extends CleanerFunction {
        private final Throwable stack;

        public CleanerFunctionStackTracked(AtomicReference<ForcedChunkManager.ForcedChunkEntry> entry, Throwable stack) {
            super(entry);
            this.stack = stack;
        }

        @Override
        public void log(ForcedChunkManager.ForcedChunkEntry entry) {
            Logging.LOGGER_DEBUG.log(Level.WARNING, "ForcedChunk.close() was not called for " + entry.toString() + ", it was created at:", this.stack);
        }
    }

    private static class ForcedChunkHandlingFinalize
    extends ForcedChunk {
        private final CleanerJDK8 cleaner;
        private final Throwable stack;

        protected ForcedChunkHandlingFinalize(CleanerJDK8 cleaner, ForcedChunkManager.ForcedChunkEntry entry, Throwable stack) {
            super(entry);
            this.cleaner = cleaner;
            this.stack = stack;
        }

        public final void finalize() throws Throwable {
            ForcedChunkManager.ForcedChunkEntry entry = this.entry.getAndSet(null);
            if (entry != null) {
                this.cleaner.processLater(entry, this.stack);
            }
            super.finalize();
        }
    }
}

