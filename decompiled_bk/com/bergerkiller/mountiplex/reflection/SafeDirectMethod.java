/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection;

import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import java.lang.reflect.Method;

public abstract class SafeDirectMethod<T>
implements MethodAccessor<T> {
    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isMethod(Method method) {
        return false;
    }
}

