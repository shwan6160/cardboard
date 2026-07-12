/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.fast;

import com.bergerkiller.mountiplex.reflection.UnhandledInvokerCheckedException;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.Constructor;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedConstructor;
import com.bergerkiller.mountiplex.reflection.util.fast.InvalidArgumentCountException;
import java.lang.reflect.InvocationTargetException;

public class ReflectionConstructor<T>
implements Constructor<T> {
    private static final Object[] NO_ARGS = new Object[0];
    private final java.lang.reflect.Constructor<Object> c;

    protected ReflectionConstructor(java.lang.reflect.Constructor<?> constructor) {
        this.c = constructor;
    }

    private RuntimeException f(Object[] args, Throwable t) {
        Class<?>[] paramTypes = this.c.getParameterTypes();
        if (paramTypes.length != args.length) {
            return new InvalidArgumentCountException("constructor", args.length, paramTypes.length);
        }
        for (int i = 0; i < paramTypes.length; ++i) {
            if (paramTypes[i].isPrimitive()) {
                if (args[i] == null) {
                    return new IllegalArgumentException("Illegal null value used for primitive " + paramTypes[i].getSimpleName() + " method parameter #" + i);
                }
                Class<?> boxed = BoxedType.getBoxedType(paramTypes[i]);
                if (boxed == null || boxed.isAssignableFrom(args[i].getClass())) continue;
                return new IllegalArgumentException("Value of type " + MPLType.getName(args[i].getClass()) + " can not be assigned to primitive " + paramTypes[i].getSimpleName() + " method parameter #" + i);
            }
            if (args[i] == null || paramTypes[i].isAssignableFrom(args[i].getClass())) continue;
            return new IllegalArgumentException("Value of type " + MPLType.getName(args[i].getClass()) + " can not be assigned to " + MPLType.getName(paramTypes[i]) + " method parameter #" + i);
        }
        if (t instanceof RuntimeException) {
            return (RuntimeException)t;
        }
        return new RuntimeException("Failed to invoke method", t);
    }

    @Override
    public T newInstanceVA(Object ... args) {
        try {
            return (T)this.c.newInstance(args);
        }
        catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            throw new UnhandledInvokerCheckedException(cause);
        }
        catch (Throwable t) {
            throw this.f(args, t);
        }
    }

    @Override
    public T newInstance() {
        return this.newInstanceVA(NO_ARGS);
    }

    @Override
    public T newInstance(Object arg0) {
        return this.newInstanceVA(arg0);
    }

    @Override
    public T newInstance(Object arg0, Object arg1) {
        return this.newInstanceVA(arg0, arg1);
    }

    @Override
    public T newInstance(Object arg0, Object arg1, Object arg2) {
        return this.newInstanceVA(arg0, arg1, arg2);
    }

    @Override
    public T newInstance(Object arg0, Object arg1, Object arg2, Object arg3) {
        return this.newInstanceVA(arg0, arg1, arg2, arg3);
    }

    @Override
    public T newInstance(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        return this.newInstanceVA(arg0, arg1, arg2, arg3, arg4);
    }

    public static <T> Constructor<T> create(java.lang.reflect.Constructor<?> constructor) {
        Class<?>[] paramTypes = constructor.getParameterTypes();
        if (paramTypes.length <= 5 && Resolver.isPublic(constructor)) {
            return GeneratedConstructor.create(constructor);
        }
        return new ReflectionConstructor<T>(constructor);
    }
}

