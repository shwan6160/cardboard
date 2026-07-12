/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.tc.utils.CircularFIFOQueue;

public class CircularFIFOQueueSynchronized<E>
implements CircularFIFOQueue<E> {
    private int writePos = 0;
    private int readPos = 0;
    private Object[] buffer;
    private boolean aborted = false;
    private boolean waiting = false;
    private Runnable wakeCallback = () -> {};

    public CircularFIFOQueueSynchronized() {
        this(64);
    }

    public CircularFIFOQueueSynchronized(int initialCapacity) {
        this.buffer = new Object[initialCapacity];
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

    @Override
    public synchronized boolean runIfEmpty(Runnable runnable) {
        if (this.writePos == this.readPos) {
            runnable.run();
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized E take(long timeoutMillis) throws CircularFIFOQueue.EmptyQueueException {
        int rpos;
        block18: {
            rpos = this.readPos;
            if (rpos == this.writePos) {
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
                            catch (InterruptedException interruptedException) {
                                // empty catch block
                            }
                        } while ((rpos = this.readPos) == this.writePos);
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
                        rpos = this.readPos;
                        if (rpos == this.writePos) continue;
                        break block18;
                    } while ((remaining = deadline - System.currentTimeMillis()) >= 0L);
                    throw CircularFIFOQueue.EmptyQueueException.INSTANCE;
                }
                finally {
                    this.waiting = false;
                }
            }
        }
        Object[] buffer = this.buffer;
        if (--rpos < 0) {
            rpos = buffer.length - 1;
        }
        this.readPos = rpos;
        Object value = buffer[rpos];
        buffer[rpos] = null;
        return (E)value;
    }

    @Override
    public synchronized void put(E value) {
        Object[] buffer = this.buffer;
        int read_pos = this.readPos;
        int curr_pos = this.writePos;
        int next_pos = curr_pos - 1;
        if (next_pos < 0) {
            next_pos = buffer.length - 1;
        }
        if (next_pos == read_pos) {
            Object[] new_buffer = new Object[buffer.length * 4 / 3];
            int index = new_buffer.length;
            while (read_pos != curr_pos) {
                if (--read_pos < 0) {
                    read_pos = buffer.length - 1;
                }
                new_buffer[--index] = buffer[read_pos];
            }
            buffer = new_buffer;
            this.buffer = new_buffer;
            read_pos = 0;
            this.readPos = 0;
            this.writePos = curr_pos = index;
            next_pos = curr_pos - 1;
            if (next_pos < 0) {
                next_pos = new_buffer.length - 1;
            }
        }
        buffer[next_pos] = value;
        this.writePos = next_pos;
        if (curr_pos == read_pos) {
            if (this.waiting) {
                this.notifyAll();
            } else {
                this.wakeCallback.run();
            }
        }
    }
}

