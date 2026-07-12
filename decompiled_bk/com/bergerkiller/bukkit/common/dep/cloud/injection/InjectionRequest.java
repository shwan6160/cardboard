/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Derived
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.injection;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.injection.InjectionRequestImpl;
import com.bergerkiller.bukkit.common.dep.cloud.util.annotation.AnnotationAccessor;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public interface InjectionRequest<C> {
    public static <C> @NonNull InjectionRequest<C> of(@NonNull CommandContext<C> context, @NonNull TypeToken<?> injectedType, @NonNull AnnotationAccessor annotationAccessor) {
        return InjectionRequestImpl.of(context, injectedType, annotationAccessor);
    }

    public static <C> @NonNull InjectionRequest<C> of(@NonNull CommandContext<C> context, @NonNull TypeToken<?> injectedType) {
        return InjectionRequestImpl.of(context, injectedType, AnnotationAccessor.empty());
    }

    public @NonNull CommandContext<C> commandContext();

    public @NonNull TypeToken<?> injectedType();

    @Value.Derived
    default public @NonNull Class<?> injectedClass() {
        return GenericTypeReflector.erase(this.injectedType().getType());
    }

    public @NonNull AnnotationAccessor annotationAccessor();
}

