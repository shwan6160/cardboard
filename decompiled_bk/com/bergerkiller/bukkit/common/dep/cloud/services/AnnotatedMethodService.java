/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.services;

import com.bergerkiller.bukkit.common.dep.cloud.services.ExecutionOrder;
import com.bergerkiller.bukkit.common.dep.cloud.services.annotation.Order;
import com.bergerkiller.bukkit.common.dep.cloud.services.type.Service;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class AnnotatedMethodService<Context, Result>
implements Service<Context, Result> {
    private final ExecutionOrder executionOrder;
    private final MethodHandle methodHandle;
    private final Method method;
    private final Object instance;

    AnnotatedMethodService(@NonNull Object instance, @NonNull Method method) throws Exception {
        ExecutionOrder executionOrder = ExecutionOrder.SOON;
        try {
            Order order = method.getAnnotation(Order.class);
            if (order != null) {
                executionOrder = order.value();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.instance = instance;
        this.executionOrder = executionOrder;
        method.setAccessible(true);
        this.methodHandle = MethodHandles.lookup().unreflect(method);
        this.method = method;
    }

    @Override
    public @Nullable Result handle(@NonNull Context context) {
        try {
            return (Result)this.methodHandle.invoke(this.instance, context);
        }
        catch (Throwable throwable) {
            new IllegalStateException(String.format("Failed to call method service implementation '%s' in class '%s'", this.method.getName(), this.instance.getClass().getCanonicalName()), throwable).printStackTrace();
            return null;
        }
    }

    @Override
    public @NonNull ExecutionOrder order() {
        return this.executionOrder;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AnnotatedMethodService that = (AnnotatedMethodService)o;
        return Objects.equals(this.methodHandle, that.methodHandle);
    }

    public int hashCode() {
        return Objects.hash(this.methodHandle);
    }
}

