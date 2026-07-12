/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util;

import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.util.fast.ConstantReturningInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LambdaBuilder<T> {
    private static final Map<Class<?>, LambdaBuilder<?>> cachedBuilders = new ConcurrentHashMap();
    private final Class<T> type;
    private final Method abstractMethod;

    private LambdaBuilder(Class<T> type) {
        List abstractMethods;
        this.type = type;
        if (!type.isInterface()) {
            try {
                Constructor<T> ctor = type.getDeclaredConstructor(new Class[0]);
                if (Modifier.isPrivate(ctor.getModifiers())) {
                    throw new IllegalArgumentException("Type " + type + " has a private empty constructor");
                }
            }
            catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Type " + type + " does not have an empty constructor");
            }
        }
        if ((abstractMethods = Stream.of(type.getMethods()).filter(m -> Modifier.isAbstract(m.getModifiers())).collect(Collectors.toList())).isEmpty()) {
            throw new IllegalArgumentException("Type " + type + " does not have any abstract methods");
        }
        if (abstractMethods.size() > 1) {
            throw new IllegalArgumentException("Type " + type + " has too many abstract methods (" + abstractMethods.size() + ")");
        }
        this.abstractMethod = (Method)abstractMethods.get(0);
    }

    public T createConstant(Object constantValue) {
        return this.create(ConstantReturningInvoker.of(constantValue));
    }

    public T create(final Invoker<?> invoker) {
        ClassInterceptor interceptor = new ClassInterceptor(this){
            final /* synthetic */ LambdaBuilder this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            protected Invoker<?> getCallback(Method method) {
                if (method.equals(this.this$0.abstractMethod)) {
                    return invoker;
                }
                return null;
            }
        };
        interceptor.setUseGlobalCallbacks(false);
        return interceptor.createInstance(this.type);
    }

    public static <C, T extends C> LambdaBuilder<T> of(Class<C> functionalInterfaceType) {
        return cachedBuilders.computeIfAbsent(functionalInterfaceType, LambdaBuilder::new);
    }
}

