/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.method;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.method.AnnotatedMethodHandlerInitiationException;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.method.ParameterValue;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.injection.ParameterInjectorRegistry;
import com.bergerkiller.bukkit.common.dep.cloud.util.annotation.AnnotationAccessor;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.INTERNAL)
public abstract class AnnotatedMethodHandler<C> {
    private final Parameter[] parameters;
    private final MethodHandle methodHandle;
    private final AnnotationAccessor annotationAccessor;
    private final ParameterInjectorRegistry<C> injectorRegistry;

    protected AnnotatedMethodHandler(@NonNull Method method, @NonNull Object instance, @NonNull ParameterInjectorRegistry<C> injectorRegistry) {
        try {
            this.parameters = method.getParameters();
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            this.methodHandle = MethodHandles.lookup().unreflect(method).bindTo(instance);
            this.annotationAccessor = AnnotationAccessor.of(method);
            this.injectorRegistry = injectorRegistry;
        }
        catch (Exception exception) {
            throw new AnnotatedMethodHandlerInitiationException(exception);
        }
    }

    public @NonNull Parameter @NonNull [] parameters() {
        return this.parameters;
    }

    public @NonNull MethodHandle methodHandle() {
        return this.methodHandle;
    }

    public @NonNull AnnotationAccessor annotationAccessor() {
        return this.annotationAccessor;
    }

    public @NonNull ParameterInjectorRegistry<C> injectorRegistry() {
        return this.injectorRegistry;
    }

    protected @Nullable ParameterValue getParameterValue(@NonNull Parameter parameter, @NonNull CommandContext<C> context) {
        return null;
    }

    protected @Nullable ParameterValue getInjectedValue(@NonNull Parameter parameter, @NonNull CommandContext<C> context) {
        Optional<?> value = this.injectorRegistry.getInjectable(TypeToken.get(parameter.getParameterizedType()), context, AnnotationAccessor.of(AnnotationAccessor.of(parameter), this.annotationAccessor()));
        if (value.isPresent()) {
            return ParameterValue.of(parameter, value.get());
        }
        if (parameter.getType() == String.class) {
            return ParameterValue.of(parameter, parameter.getName());
        }
        return null;
    }

    public @NonNull List<@NonNull ParameterValue> createParameterValues(@NonNull CommandContext<C> context) {
        return this.createParameterValues(context, this.parameters());
    }

    public @NonNull List<@NonNull ParameterValue> createParameterValues(@NonNull CommandContext<C> context, @NonNull Parameter @NonNull [] parameters) {
        return this.createParameterValues(context, parameters, Collections.emptyList());
    }

    public @NonNull List<@NonNull ParameterValue> createParameterValues(@NonNull CommandContext<C> context, @NonNull Parameter @NonNull [] parameters, @NonNull Collection<Object> preDeterminedValues) {
        ArrayList<ParameterValue> values = new ArrayList<ParameterValue>(parameters.length);
        block0: for (Parameter parameter : parameters) {
            for (Object preDeterminedValue : preDeterminedValues) {
                if (!parameter.getType().isInstance(preDeterminedValue)) continue;
                values.add(ParameterValue.of(parameter, preDeterminedValue));
                continue block0;
            }
            ParameterValue contextualValue = this.getParameterValue(parameter, context);
            if (contextualValue != null) {
                values.add(contextualValue);
                continue;
            }
            if (parameter.getType().isAssignableFrom(context.sender().getClass())) {
                values.add(ParameterValue.of(parameter, context.sender()));
                continue;
            }
            ParameterValue injectedValue = this.getInjectedValue(parameter, context);
            if (injectedValue != null) {
                values.add(injectedValue);
                continue;
            }
            throw new IllegalArgumentException(String.format("Could not create value for parameter '%s' of type '%s' in method '%s'", parameter.getName(), parameter.getType().getTypeName(), this.methodHandle().toString()));
        }
        return Collections.unmodifiableList(values);
    }
}

