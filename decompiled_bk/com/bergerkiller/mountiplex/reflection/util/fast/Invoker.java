/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.fast;

import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;

public interface Invoker<T>
extends LazyInitializedObject {
    default public Invoker<T> initializeInvoker() {
        return this;
    }

    @Override
    default public void forceInitialization() {
        this.initializeInvoker();
    }

    default public T invoke(Object instance) {
        return this.invokeVA(instance, new Object[0]);
    }

    default public T invoke(Object instance, Object arg0) {
        return this.invokeVA(instance, arg0);
    }

    default public T invoke(Object instance, Object arg0, Object arg1) {
        return this.invokeVA(instance, arg0, arg1);
    }

    default public T invoke(Object instance, Object arg0, Object arg1, Object arg2) {
        return this.invokeVA(instance, arg0, arg1, arg2);
    }

    default public T invoke(Object instance, Object arg0, Object arg1, Object arg2, Object arg3) {
        return this.invokeVA(instance, arg0, arg1, arg2, arg3);
    }

    default public T invoke(Object instance, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        return this.invokeVA(instance, arg0, arg1, arg2, arg3, arg4);
    }

    public T invokeVA(Object var1, Object ... var2);
}

