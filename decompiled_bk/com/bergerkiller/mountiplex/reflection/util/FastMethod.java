/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.util.IgnoresRemapping;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;
import com.bergerkiller.mountiplex.reflection.util.fast.InitInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class FastMethod<T>
implements Invoker<T>,
LazyInitializedObject,
IgnoresRemapping {
    private MethodDeclaration method;
    private Invoker<T> invoker;

    public FastMethod() {
        this.method = null;
        this.invoker = InitInvoker.unavailableMethod();
    }

    public FastMethod(Method method) {
        this.init(method);
    }

    public FastMethod(Constructor<?> constructor) {
        this.init(constructor);
    }

    public FastMethod(MethodDeclaration method) {
        this.init(method);
    }

    public FastMethod(DelayedInitializer<T> initializer) {
        this.init(initializer);
    }

    public final void init(Method method) {
        if (method == null) {
            this.method = null;
            this.invoker = InitInvoker.unavailableMethod();
        } else {
            this.init(new MethodDeclaration(ClassResolver.DEFAULT, method));
        }
    }

    public final void init(Constructor<?> constructor) {
        if (constructor == null) {
            this.method = null;
            this.invoker = InitInvoker.unavailableMethod();
        } else {
            this.init(new MethodDeclaration(ClassResolver.DEFAULT, constructor));
        }
    }

    public final void init(MethodDeclaration methodDeclaration) {
        if (methodDeclaration == null) {
            this.method = null;
            this.invoker = InitInvoker.unavailableMethod();
        } else if (!methodDeclaration.isRecordFieldChanger && methodDeclaration.body == null && methodDeclaration.method == null && methodDeclaration.constructor == null) {
            this.method = null;
            this.invoker = InitInvoker.unavailable("method", methodDeclaration.toString());
        } else {
            this.method = methodDeclaration;
            this.invoker = InitInvoker.forMethod((Object)this, "invoker", methodDeclaration);
        }
    }

    public final void init(DelayedInitializer<T> initializer) {
        this.init(null, new DelayedInitializationInvoker<T>(this, initializer));
    }

    public final void init(MethodDeclaration methodDeclaration, Invoker<T> invoker) {
        this.method = methodDeclaration;
        this.invoker = invoker;
    }

    public final void initUnavailable(String missingInfo) {
        this.method = null;
        this.invoker = InitInvoker.unavailable("method", missingInfo);
    }

    public final void checkInit() {
        if (this.invoker instanceof InitInvoker.UnavailableInvoker) {
            this.invoker.initializeInvoker();
        }
    }

    public final boolean isAvailable() {
        if (this.method == null) {
            if (this.invoker instanceof DelayedInitializationInvoker) {
                this.invoker.initializeInvoker();
                return this.method != null;
            }
            return false;
        }
        return true;
    }

    public final Method getMethod() {
        return this.isAvailable() ? this.method.method : null;
    }

    public final boolean isMethod(Method method) {
        return this.isAvailable() && this.method.method != null && this.method.method.equals(method);
    }

    public final String getName() {
        if (this.isAvailable()) {
            return this.method.name.value();
        }
        return "null";
    }

    public final Invoker<T> getInvoker() {
        return this.invoker;
    }

    @Override
    public void forceInitialization() {
        this.invoker.forceInitialization();
    }

    @Override
    public T invokeVA(Object instance, Object ... args) {
        return this.invoker.invokeVA(instance, args);
    }

    @Override
    public T invoke(Object instance) {
        return this.invoker.invoke(instance);
    }

    @Override
    public T invoke(Object instance, Object arg0) {
        return this.invoker.invoke(instance, arg0);
    }

    @Override
    public T invoke(Object instance, Object arg0, Object arg1) {
        return this.invoker.invoke(instance, arg0, arg1);
    }

    @Override
    public T invoke(Object instance, Object arg0, Object arg1, Object arg2) {
        return this.invoker.invoke(instance, arg0, arg1, arg2);
    }

    @Override
    public T invoke(Object instance, Object arg0, Object arg1, Object arg2, Object arg3) {
        return this.invoker.invoke(instance, arg0, arg1, arg2, arg3);
    }

    @Override
    public T invoke(Object instance, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        return this.invoker.invoke(instance, arg0, arg1, arg2, arg3, arg4);
    }

    @FunctionalInterface
    public static interface DelayedInitializer<T> {
        public void initialize(FastMethod<T> var1) throws Throwable;
    }

    private static class DelayedInitializationInvoker<T>
    implements Invoker<T> {
        private final FastMethod<T> method;
        private final DelayedInitializer<T> initializer;

        public DelayedInitializationInvoker(FastMethod<T> method, DelayedInitializer<T> initializer) {
            this.method = method;
            this.initializer = initializer;
        }

        @Override
        public Invoker<T> initializeInvoker() {
            try {
                this.initializer.initialize(this.method);
                if (((FastMethod)this.method).invoker == this) {
                    this.method.initUnavailable("(callback-initialized) which did not initialize");
                } else {
                    this.method.forceInitialization();
                }
            }
            catch (Throwable t) {
                MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to callback-initialize method", t);
                this.method.initUnavailable("(callback-initialized) with init error");
            }
            return ((FastMethod)this.method).invoker;
        }

        @Override
        public T invokeVA(Object instance, Object ... args) {
            return this.initializeInvoker().invokeVA(instance, args);
        }
    }
}

