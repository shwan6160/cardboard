/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.bases.CheckedConsumer;

@FunctionalInterface
public interface CheckedFunction<T, R> {
    public R apply(T var1) throws Throwable;

    default public CheckedConsumer<T> asConsumer() {
        return value -> this.apply(value);
    }
}

