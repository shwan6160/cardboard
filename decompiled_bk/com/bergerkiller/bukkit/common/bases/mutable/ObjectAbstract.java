/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.bases.mutable;

public abstract class ObjectAbstract<T> {
    public abstract T get();

    public abstract ObjectAbstract<T> set(T var1);

    public void clear() {
        this.set(null);
    }
}

