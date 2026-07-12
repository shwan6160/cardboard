/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.fast;

import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;

public final class ConstantReturningInvoker<T>
implements Invoker<T> {
    private final T value;

    private ConstantReturningInvoker(T value) {
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }

    @Override
    public T invoke(Object instance) {
        return this.value;
    }

    @Override
    public T invoke(Object instance, Object arg0) {
        return this.value;
    }

    @Override
    public T invoke(Object instance, Object arg0, Object arg1) {
        return this.value;
    }

    @Override
    public T invoke(Object instance, Object arg0, Object arg1, Object arg2) {
        return this.value;
    }

    @Override
    public T invoke(Object instance, Object arg0, Object arg1, Object arg2, Object arg3) {
        return this.value;
    }

    @Override
    public T invoke(Object instance, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        return this.value;
    }

    @Override
    public T invokeVA(Object instance, Object ... args) {
        return this.value;
    }

    public static <T> ConstantReturningInvoker<T> of(T value) {
        return new ConstantReturningInvoker<T>(value);
    }
}

