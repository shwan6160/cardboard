/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.bases.CheckedRunnable;

@FunctionalInterface
public interface CheckedSupplier<T> {
    public T get() throws Throwable;

    default public CheckedRunnable asRunnable() {
        return () -> this.get();
    }
}

