/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.fast;

public interface Constructor<T> {
    public T newInstance();

    public T newInstance(Object var1);

    public T newInstance(Object var1, Object var2);

    public T newInstance(Object var1, Object var2, Object var3);

    public T newInstance(Object var1, Object var2, Object var3, Object var4);

    public T newInstance(Object var1, Object var2, Object var3, Object var4, Object var5);

    public T newInstanceVA(Object ... var1);
}

