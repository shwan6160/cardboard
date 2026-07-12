/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.exception;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.exception.MethodExceptionHandler;
import com.bergerkiller.bukkit.common.dep.cloud.exception.handling.ExceptionHandler;
import com.bergerkiller.bukkit.common.dep.cloud.injection.ParameterInjectorRegistry;
import java.lang.reflect.Method;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
@API(status=API.Status.STABLE)
public interface ExceptionHandlerFactory<C> {
    public static <C> @NonNull ExceptionHandlerFactory<C> defaultFactory() {
        return MethodExceptionHandler::new;
    }

    public @NonNull ExceptionHandler<C, Throwable> createExceptionHandler(@NonNull Object var1, @NonNull Method var2, @NonNull ParameterInjectorRegistry<C> var3);
}

