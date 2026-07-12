/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.fast;

import com.bergerkiller.mountiplex.reflection.UnhandledInvokerCheckedException;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.InvalidArgumentCountException;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class ReflectionInvoker<T>
implements Invoker<T> {
    private static final Object[] NO_ARGS = new Object[0];

    private static RuntimeException checkInstance(Executable executable, Object instance) {
        if (Modifier.isStatic(executable.getModifiers())) {
            if (instance != null) {
                return new IllegalArgumentException("Instance should be null for static fields, but was " + MPLType.getName(instance.getClass()) + " instead");
            }
        } else {
            if (instance == null) {
                return new IllegalArgumentException("Instance can not be null for member fields declared in " + MPLType.getName(executable.getDeclaringClass()));
            }
            if (!executable.getDeclaringClass().isAssignableFrom(instance.getClass())) {
                return new IllegalArgumentException("Instance of type " + MPLType.getName(instance.getClass()) + " does not contain the field declared in " + MPLType.getName(executable.getDeclaringClass()));
            }
        }
        return null;
    }

    protected static RuntimeException f(Executable executable, Object instance, Object[] args, Throwable t) {
        RuntimeException iex = ReflectionInvoker.checkInstance(executable, instance);
        if (iex != null) {
            return iex;
        }
        Class<?>[] paramTypes = executable.getParameterTypes();
        if (paramTypes.length != args.length) {
            return new InvalidArgumentCountException("method", args.length, paramTypes.length);
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
    public T invoke(Object instance) {
        return this.invokeVA(instance, NO_ARGS);
    }

    @Override
    public T invoke(Object instance, Object arg0) {
        return this.invokeVA(instance, arg0);
    }

    @Override
    public T invoke(Object instance, Object arg0, Object arg1) {
        return this.invokeVA(instance, arg0, arg1);
    }

    @Override
    public T invoke(Object instance, Object arg0, Object arg1, Object arg2) {
        return this.invokeVA(instance, arg0, arg1, arg2);
    }

    @Override
    public T invoke(Object instance, Object arg0, Object arg1, Object arg2, Object arg3) {
        return this.invokeVA(instance, arg0, arg1, arg2, arg3);
    }

    @Override
    public T invoke(Object instance, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        return this.invokeVA(instance, arg0, arg1, arg2, arg3, arg4);
    }

    public static <T> Invoker<T> create(Executable executable) {
        executable.setAccessible(true);
        if (executable instanceof Method) {
            return new ReflectionMethodInvoker((Method)executable);
        }
        if (executable instanceof Constructor) {
            return new ReflectionConstructorInvoker((Constructor)executable);
        }
        throw new IllegalArgumentException("Not a method or constructor");
    }

    private static class ReflectionMethodInvoker<T>
    extends ReflectionInvoker<T> {
        protected final Method m;

        public ReflectionMethodInvoker(Method method) {
            this.m = method;
        }

        @Override
        public T invokeVA(Object instance, Object ... args) {
            try {
                return (T)this.m.invoke(instance, args);
            }
            catch (InvocationTargetException ex) {
                Throwable cause = ex.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                throw new UnhandledInvokerCheckedException(cause);
            }
            catch (Throwable t) {
                throw ReflectionMethodInvoker.f(this.m, instance, args, t);
            }
        }
    }

    private static class ReflectionConstructorInvoker<T>
    extends ReflectionInvoker<T> {
        protected final Constructor<T> c;

        public ReflectionConstructorInvoker(Constructor<?> constructor) {
            this.c = constructor;
        }

        @Override
        public T invokeVA(Object instance, Object ... args) {
            try {
                return this.c.newInstance(args);
            }
            catch (InvocationTargetException ex) {
                Throwable cause = ex.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                throw new UnhandledInvokerCheckedException(cause);
            }
            catch (Throwable t) {
                throw ReflectionConstructorInvoker.f(this.c, instance, args, t);
            }
        }
    }
}

