/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckReturnValue
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.concurrent.Immutable
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Generated
 */
package com.bergerkiller.bukkit.common.dep.cloud.injection;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.injection.InjectionRequest;
import com.bergerkiller.bukkit.common.dep.cloud.util.annotation.AnnotationAccessor;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.Objects;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Generated;

@ParametersAreNonnullByDefault
@CheckReturnValue
@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="InjectionRequest", generator="Immutables")
@Immutable
final class InjectionRequestImpl<C>
implements InjectionRequest<C> {
    private final @NonNull CommandContext<C> commandContext;
    private final @NonNull TypeToken<?> injectedType;
    private final transient @NonNull Class<?> injectedClass;
    private final @NonNull AnnotationAccessor annotationAccessor;

    private InjectionRequestImpl(@NonNull CommandContext<C> commandContext, @NonNull TypeToken<?> injectedType, @NonNull AnnotationAccessor annotationAccessor) {
        this.commandContext = Objects.requireNonNull(commandContext, "commandContext");
        this.injectedType = Objects.requireNonNull(injectedType, "injectedType");
        this.annotationAccessor = Objects.requireNonNull(annotationAccessor, "annotationAccessor");
        this.injectedClass = Objects.requireNonNull(InjectionRequest.super.injectedClass(), "injectedClass");
    }

    private InjectionRequestImpl(InjectionRequestImpl<C> original, @NonNull CommandContext<C> commandContext, @NonNull TypeToken<?> injectedType, @NonNull AnnotationAccessor annotationAccessor) {
        this.commandContext = commandContext;
        this.injectedType = injectedType;
        this.annotationAccessor = annotationAccessor;
        this.injectedClass = Objects.requireNonNull(InjectionRequest.super.injectedClass(), "injectedClass");
    }

    @Override
    public @NonNull CommandContext<C> commandContext() {
        return this.commandContext;
    }

    @Override
    public @NonNull TypeToken<?> injectedType() {
        return this.injectedType;
    }

    @Override
    public @NonNull Class<?> injectedClass() {
        return this.injectedClass;
    }

    @Override
    public @NonNull AnnotationAccessor annotationAccessor() {
        return this.annotationAccessor;
    }

    public final InjectionRequestImpl<C> withCommandContext(@NonNull CommandContext<C> value) {
        if (this.commandContext == value) {
            return this;
        }
        @NonNull CommandContext<C> newValue = Objects.requireNonNull(value, "commandContext");
        return new InjectionRequestImpl<C>(this, newValue, this.injectedType, this.annotationAccessor);
    }

    public final InjectionRequestImpl<C> withInjectedType(@NonNull TypeToken<?> value) {
        if (this.injectedType == value) {
            return this;
        }
        @NonNull TypeToken<?> newValue = Objects.requireNonNull(value, "injectedType");
        return new InjectionRequestImpl<C>(this, this.commandContext, newValue, this.annotationAccessor);
    }

    public final InjectionRequestImpl<C> withAnnotationAccessor(@NonNull AnnotationAccessor value) {
        if (this.annotationAccessor == value) {
            return this;
        }
        @NonNull AnnotationAccessor newValue = Objects.requireNonNull(value, "annotationAccessor");
        return new InjectionRequestImpl<C>(this, this.commandContext, this.injectedType, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof InjectionRequestImpl && this.equalTo(0, (InjectionRequestImpl)another);
    }

    private boolean equalTo(int synthetic, InjectionRequestImpl<?> another) {
        return this.commandContext.equals(another.commandContext) && this.injectedType.equals(another.injectedType) && this.injectedClass.equals(another.injectedClass) && this.annotationAccessor.equals(another.annotationAccessor);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.commandContext.hashCode();
        h += (h << 5) + this.injectedType.hashCode();
        h += (h << 5) + this.injectedClass.hashCode();
        h += (h << 5) + this.annotationAccessor.hashCode();
        return h;
    }

    public String toString() {
        return "InjectionRequest{commandContext=" + this.commandContext + ", injectedType=" + this.injectedType + ", injectedClass=" + this.injectedClass + ", annotationAccessor=" + this.annotationAccessor + "}";
    }

    public static <C> InjectionRequestImpl<C> of(@NonNull CommandContext<C> commandContext, @NonNull TypeToken<?> injectedType, @NonNull AnnotationAccessor annotationAccessor) {
        return new InjectionRequestImpl<C>(commandContext, injectedType, annotationAccessor);
    }

    public static <C> InjectionRequestImpl<C> copyOf(InjectionRequest<C> instance) {
        if (instance instanceof InjectionRequestImpl) {
            return (InjectionRequestImpl)instance;
        }
        return InjectionRequestImpl.of(instance.commandContext(), instance.injectedType(), instance.annotationAccessor());
    }
}

