/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.exception;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.method.AnnotatedMethodHandler;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.method.ParameterValue;
import com.bergerkiller.bukkit.common.dep.cloud.exception.handling.ExceptionContext;
import com.bergerkiller.bukkit.common.dep.cloud.exception.handling.ExceptionHandler;
import com.bergerkiller.bukkit.common.dep.cloud.injection.ParameterInjectorRegistry;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class MethodExceptionHandler<C>
extends AnnotatedMethodHandler<C>
implements ExceptionHandler<C, Throwable> {
    public MethodExceptionHandler(@NonNull Object instance, @NonNull Method method, @NonNull ParameterInjectorRegistry<C> injectorRegistry) {
        super(method, instance, injectorRegistry);
    }

    @Override
    public void handle(@NonNull ExceptionContext<C, Throwable> context) throws Throwable {
        List arguments = this.createParameterValues(context.context(), this.parameters(), Arrays.asList(context, context.exception())).stream().map(ParameterValue::value).collect(Collectors.toList());
        this.methodHandle().invokeWithArguments(arguments);
    }
}

