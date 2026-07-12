/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.signature;

import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class MethodSignature {
    private final String name;
    private final Class<?>[] parameterTypes;

    public MethodSignature(String name, Class<?>[] parameterTypes) {
        this.name = name;
        this.parameterTypes = parameterTypes;
    }

    public MethodSignature(Method method) {
        this.name = MPLType.getName(method);
        this.parameterTypes = method.getParameterTypes();
    }

    public String getName() {
        return this.name;
    }

    public Class<?>[] getParameterTypes() {
        return this.parameterTypes;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof MethodSignature) {
            MethodSignature other = (MethodSignature)o;
            return this.name.equals(other.name) && Arrays.equals(this.parameterTypes, other.parameterTypes);
        }
        return false;
    }
}

