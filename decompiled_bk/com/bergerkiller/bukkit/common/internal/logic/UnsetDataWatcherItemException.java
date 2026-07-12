/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.logic;

public final class UnsetDataWatcherItemException
extends RuntimeException {
    public static final UnsetDataWatcherItemException INSTANCE = new UnsetDataWatcherItemException();

    private UnsetDataWatcherItemException() {
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}

