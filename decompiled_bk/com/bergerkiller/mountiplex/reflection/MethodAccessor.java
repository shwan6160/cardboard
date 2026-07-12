/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection;

import java.lang.reflect.Method;

public interface MethodAccessor<T> {
    public boolean isMethod(Method var1);

    public boolean isValid();

    public T invoke(Object var1, Object ... var2);
}

