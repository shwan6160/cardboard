/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.DefaultValueFactory;
import com.bergerkiller.bukkit.common.dep.cloud.component.DefaultValue;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL)
final class MethodDefaultValueFactory<C, T>
implements DefaultValueFactory<C, T> {
    private final MethodHandle methodHandle;

    MethodDefaultValueFactory(@NonNull Method method, @NonNull Object instance) {
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        try {
            this.methodHandle = MethodHandles.lookup().unreflect(method).bindTo(instance);
        }
        catch (Exception e) {
            throw new RuntimeException(String.format("Failed to create the default value factory using method %s in class %s", method.getName(), method.getDeclaringClass().getName()), e);
        }
    }

    @Override
    public @NonNull DefaultValue<C, T> create(@NonNull Parameter parameter) {
        try {
            return this.methodHandle.invoke(parameter);
        }
        catch (Throwable throwable) {
            throw new RuntimeException("Failed to create default value instance", throwable);
        }
    }
}

