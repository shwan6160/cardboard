/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.utils;

import java.util.concurrent.atomic.AtomicBoolean;

public final class RecursionGuard<T> {
    private final AtomicBoolean opened = new AtomicBoolean(false);
    private static final Token INACTIVE_TOKEN = () -> {};
    private final Token ACTIVE_TOKEN = () -> this.opened.set(false);
    private final Handler<T> handler;

    private RecursionGuard(Handler<T> handler) {
        this.handler = handler;
    }

    public static <T> RecursionGuard<T> handleOnce(final Handler<T> handler) {
        return RecursionGuard.handle(new Handler<T>(){
            private final AtomicBoolean handled = new AtomicBoolean(false);

            @Override
            public void onRecursion(T value) {
                if (this.handled.compareAndSet(false, true)) {
                    handler.onRecursion(value);
                }
            }
        });
    }

    public static <T> RecursionGuard<T> handle(Handler<T> handler) {
        return new RecursionGuard<T>(handler);
    }

    public Token open(T value) {
        if (this.opened.compareAndSet(false, true)) {
            return this.ACTIVE_TOKEN;
        }
        this.handler.onRecursion(value);
        return INACTIVE_TOKEN;
    }

    public static interface Token
    extends AutoCloseable {
        @Override
        public void close();
    }

    @FunctionalInterface
    public static interface Handler<T> {
        public void onRecursion(T var1);
    }
}

