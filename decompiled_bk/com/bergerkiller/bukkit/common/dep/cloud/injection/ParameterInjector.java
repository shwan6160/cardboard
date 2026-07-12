/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.injection;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.util.annotation.AnnotationAccessor;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@FunctionalInterface
@API(status=API.Status.STABLE)
public interface ParameterInjector<C, T> {
    @API(status=API.Status.STABLE)
    public static <C, T> @NonNull ParameterInjector<C, T> constantInjector(@NonNull T value) {
        return new ConstantInjector(value);
    }

    public @Nullable T create(@NonNull CommandContext<C> var1, @NonNull AnnotationAccessor var2);

    public static final class ConstantInjector<C, T>
    implements ParameterInjector<C, T> {
        private final T value;

        private ConstantInjector(@NonNull T value) {
            this.value = value;
        }

        @Override
        public @NonNull T create(@NonNull CommandContext<C> context, @NonNull AnnotationAccessor annotationAccessor) {
            return this.value;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ConstantInjector that = (ConstantInjector)o;
            return Objects.equals(this.value, that.value);
        }

        public int hashCode() {
            return Objects.hash(this.value);
        }

        public String toString() {
            return "ConstantInjector{value=" + this.value + '}';
        }
    }
}

