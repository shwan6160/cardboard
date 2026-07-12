/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.utils;

import java.util.function.Consumer;

public interface CircularFIFOQueue<E> {
    public int capacity();

    public void abort();

    public boolean isAborted();

    default public boolean isEmpty() {
        return this.runIfEmpty(() -> {});
    }

    public void setWakeCallback(Runnable var1);

    public boolean runIfEmpty(Runnable var1);

    default public E take() throws EmptyQueueException {
        return this.take(Long.MAX_VALUE);
    }

    public E take(long var1) throws EmptyQueueException;

    public void put(E var1);

    public static <E> CircularFIFOQueue<E> forward(final Consumer<E> consumer) {
        return new CircularFIFOQueue<E>(){

            @Override
            public int capacity() {
                return 0;
            }

            @Override
            public void abort() {
            }

            @Override
            public boolean isAborted() {
                return true;
            }

            @Override
            public void setWakeCallback(Runnable callback) {
            }

            @Override
            public boolean isEmpty() {
                return true;
            }

            @Override
            public boolean runIfEmpty(Runnable runnable) {
                runnable.run();
                return true;
            }

            @Override
            public E take(long timeoutMillis) throws EmptyQueueException {
                throw EmptyQueueException.INSTANCE;
            }

            @Override
            public void put(E value) {
                consumer.accept(value);
            }
        };
    }

    public static final class EmptyQueueException
    extends Exception {
        public static final EmptyQueueException INSTANCE = new EmptyQueueException();
        private static final long serialVersionUID = 1824696362338789498L;

        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }
}

