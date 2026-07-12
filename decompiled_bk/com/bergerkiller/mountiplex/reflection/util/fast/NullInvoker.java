/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.fast;

import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;

public class NullInvoker<T>
implements Invoker<T> {
    private final T nullObject;

    public NullInvoker() {
        this.nullObject = null;
    }

    public NullInvoker(T nullObject) {
        this.nullObject = nullObject;
    }

    public NullInvoker(Class<T> type) {
        this.nullObject = BoxedType.getDefaultValue(type);
    }

    @Override
    public T invoke(Object instance) {
        return this.nullObject;
    }

    @Override
    public T invoke(Object instance, Object arg0) {
        return this.nullObject;
    }

    @Override
    public T invoke(Object instance, Object arg0, Object arg1) {
        return this.nullObject;
    }

    @Override
    public T invoke(Object instance, Object arg0, Object arg1, Object arg2) {
        return this.nullObject;
    }

    @Override
    public T invoke(Object instance, Object arg0, Object arg1, Object arg2, Object arg3) {
        return this.nullObject;
    }

    @Override
    public T invoke(Object instance, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        return this.nullObject;
    }

    @Override
    public T invokeVA(Object instance, Object ... args) {
        return this.nullObject;
    }
}

