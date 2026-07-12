/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.tc.utils.CircularFIFOQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

public class CircularFIFOQueueStampedRW<E>
implements CircularFIFOQueue<E> {
    private final AtomicInteger writePos;
    private int readPos = 0;
    private Object[] buffer;
    private final StampedLock lock;
    private boolean aborted = false;
    private boolean waiting = false;
    private Runnable wakeCallback = () -> {};
    private static final CompareAndExchangeFunc compareAndExchange = CircularFIFOQueueStampedRW.detectCompareAndExchangeFunc();

    public CircularFIFOQueueStampedRW() {
        this(64);
    }

    public CircularFIFOQueueStampedRW(int initialCapacity) {
        this.writePos = new AtomicInteger(0);
        this.buffer = new Object[initialCapacity];
        this.lock = new StampedLock();
    }

    @Override
    public int capacity() {
        return this.buffer.length;
    }

    @Override
    public synchronized void abort() {
        this.aborted = true;
        this.notifyAll();
    }

    @Override
    public boolean isAborted() {
        return this.aborted;
    }

    @Override
    public synchronized void setWakeCallback(Runnable callback) {
        this.wakeCallback = callback;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean runIfEmpty(Runnable runnable) {
        long slowCheckLock = this.lock.tryWriteLock();
        if (slowCheckLock == 0L) {
            return false;
        }
        try {
            if (this.readPos == this.writePos.get()) {
                runnable.run();
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.lock.unlockWrite(slowCheckLock);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public E take(long timeoutMillis) throws CircularFIFOQueue.EmptyQueueException {
        while (true) {
            long slowTakeLock = this.lock.writeLock();
            int rpos = this.readPos;
            if (rpos != this.writePos.get()) {
                Object[] buffer = this.buffer;
                if (--rpos < 0) {
                    rpos = buffer.length - 1;
                }
                this.readPos = rpos;
                Object value = buffer[rpos];
                buffer[rpos] = null;
                this.lock.unlockWrite(slowTakeLock);
                return (E)value;
            }
            CircularFIFOQueueStampedRW circularFIFOQueueStampedRW = this;
            synchronized (circularFIFOQueueStampedRW) {
                block22: {
                    this.lock.unlockWrite(slowTakeLock);
                    if (timeoutMillis <= 0L || this.aborted) {
                        throw CircularFIFOQueue.EmptyQueueException.INSTANCE;
                    }
                    if (timeoutMillis == Long.MAX_VALUE) {
                        try {
                            this.waiting = true;
                            do {
                                if (this.aborted) {
                                    throw CircularFIFOQueue.EmptyQueueException.INSTANCE;
                                }
                                try {
                                    this.wait();
                                }
                                catch (InterruptedException value) {
                                    // empty catch block
                                }
                            } while (this.readPos == this.writePos.get());
                        }
                        finally {
                            this.waiting = false;
                        }
                    }
                    try {
                        this.waiting = true;
                        long deadline = System.currentTimeMillis() + timeoutMillis;
                        long remaining = timeoutMillis;
                        do {
                            if (this.aborted) {
                                throw CircularFIFOQueue.EmptyQueueException.INSTANCE;
                            }
                            try {
                                this.wait(remaining);
                            }
                            catch (InterruptedException interruptedException) {
                                // empty catch block
                            }
                            if (this.readPos == this.writePos.get()) continue;
                            break block22;
                        } while ((remaining = deadline - System.currentTimeMillis()) >= 0L);
                        throw CircularFIFOQueue.EmptyQueueException.INSTANCE;
                    }
                    finally {
                        this.waiting = false;
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void put(E value) {
        if (!this.fastPut(value)) {
            long slowPutLock = this.lock.writeLock();
            try {
                this.slowPut(value);
            }
            finally {
                this.lock.unlockWrite(slowPutLock);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean fastPut(E value) {
        int nextWrite;
        int currWrite;
        long fastPutLock = this.lock.readLock();
        Object[] buffer = this.buffer;
        int rpos = this.readPos;
        int exchangeResult = this.writePos.get();
        do {
            if ((nextWrite = (currWrite = exchangeResult) - 1) < 0) {
                nextWrite = buffer.length - 1;
            }
            if (nextWrite != rpos) continue;
            long slowOverflowPutLock = this.lock.tryConvertToWriteLock(fastPutLock);
            if (slowOverflowPutLock == 0L) {
                this.lock.unlockRead(fastPutLock);
                return false;
            }
            try {
                this.slowPut(value);
            }
            finally {
                this.lock.unlockWrite(slowOverflowPutLock);
            }
            return true;
        } while ((exchangeResult = compareAndExchange.call(this.writePos, currWrite, nextWrite)) != currWrite);
        buffer[nextWrite] = value;
        this.lock.unlockRead(fastPutLock);
        if (currWrite == rpos) {
            this.notifyEmpty();
        }
        return true;
    }

    private void slowPut(E value) {
        int rpos;
        int currWrite = this.writePos.get();
        int nextWrite = currWrite - 1;
        if (nextWrite < 0) {
            nextWrite = this.buffer.length - 1;
        }
        if (nextWrite == (rpos = this.readPos)) {
            Object[] old_buffer = this.buffer;
            Object[] new_buffer = new Object[old_buffer.length * 4 / 3];
            int index = new_buffer.length;
            while (rpos != currWrite) {
                if (--rpos < 0) {
                    rpos = old_buffer.length - 1;
                }
                new_buffer[--index] = old_buffer[rpos];
            }
            new_buffer[--index] = value;
            this.buffer = new_buffer;
            this.readPos = 0;
            this.writePos.set(index);
        } else {
            this.buffer[nextWrite] = value;
            this.writePos.set(nextWrite);
            if (currWrite == rpos) {
                this.notifyEmpty();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void notifyEmpty() {
        CircularFIFOQueueStampedRW circularFIFOQueueStampedRW = this;
        synchronized (circularFIFOQueueStampedRW) {
            if (this.waiting) {
                this.notifyAll();
                return;
            }
        }
        this.wakeCallback.run();
    }

    private static CompareAndExchangeFunc detectCompareAndExchangeFunc() {
        try {
            AtomicInteger.class.getDeclaredMethod("compareAndExchange", Integer.TYPE, Integer.TYPE);
            return AtomicInteger::compareAndExchangeAcquire;
        }
        catch (Throwable t) {
            return (ai, expectedValue, newValue) -> {
                int realValue;
                do {
                    if (!ai.compareAndSet(expectedValue, newValue)) continue;
                    return expectedValue;
                } while ((realValue = ai.get()) == expectedValue);
                return realValue;
            };
        }
    }

    @FunctionalInterface
    private static interface CompareAndExchangeFunc {
        public int call(AtomicInteger var1, int var2, int var3);
    }
}

