/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.BindingAnnotation
 *  com.google.inject.ConfigurationException
 *  com.google.inject.Injector
 *  com.google.inject.Key
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.injection;

import com.bergerkiller.bukkit.common.dep.cloud.injection.InjectionRequest;
import com.bergerkiller.bukkit.common.dep.cloud.injection.InjectionService;
import com.bergerkiller.bukkit.common.dep.cloud.util.annotation.AnnotationAccessor;
import com.google.inject.BindingAnnotation;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import java.lang.annotation.Annotation;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.STABLE)
public final class GuiceInjectionService<C>
implements InjectionService<C> {
    private final Injector injector;

    private static <T> @NonNull Key<T> createKey(@NonNull Class<T> clazz, @NonNull AnnotationAccessor annotationAccessor) {
        Annotation bindingAnnotation = annotationAccessor.annotations().stream().filter(annotation -> annotation.annotationType().isAnnotationPresent(BindingAnnotation.class)).findFirst().orElse(null);
        if (bindingAnnotation == null) {
            return Key.get(clazz);
        }
        return Key.get(clazz, (Annotation)bindingAnnotation);
    }

    private GuiceInjectionService(@NonNull Injector injector) {
        this.injector = injector;
    }

    public static <C> @NonNull GuiceInjectionService<C> create(@NonNull Injector injector) {
        return new GuiceInjectionService<C>(injector);
    }

    @Override
    public @Nullable Object handle(@NonNull InjectionRequest<C> request) {
        try {
            return this.injector.getInstance(GuiceInjectionService.createKey(request.injectedClass(), request.annotationAccessor()));
        }
        catch (ConfigurationException ignored) {
            return null;
        }
    }
}

