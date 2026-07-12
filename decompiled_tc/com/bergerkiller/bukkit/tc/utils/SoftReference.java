/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.utils;

public class SoftReference<T> {
    private java.lang.ref.SoftReference<T> ref = null;

    public T get() {
        return this.ref == null ? null : (T)this.ref.get();
    }

    public T set(T value) {
        this.ref = value == null ? null : new java.lang.ref.SoftReference<T>(value);
        return value;
    }
}

