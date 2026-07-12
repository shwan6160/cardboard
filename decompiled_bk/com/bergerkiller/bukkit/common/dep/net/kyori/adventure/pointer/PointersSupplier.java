/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.pointer;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.builder.AbstractBuilder;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.pointer.Pointer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.pointer.Pointers;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.pointer.PointersSupplierImpl;
import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PointersSupplier<T> {
    @NotNull
    public static <T> Builder<T> builder() {
        return new PointersSupplierImpl.BuilderImpl();
    }

    @NotNull
    public Pointers view(@NotNull T var1);

    public <P> boolean supports(@NotNull Pointer<P> var1);

    @Nullable
    public <P> Function<? super T, P> resolver(@NotNull Pointer<P> var1);

    public static interface Builder<T>
    extends AbstractBuilder<PointersSupplier<T>> {
        @Contract(value="_ -> this")
        @NotNull
        public Builder<T> parent(@Nullable PointersSupplier<? super T> var1);

        @Contract(value="_, _ -> this")
        @NotNull
        public <P> Builder<T> resolving(@NotNull Pointer<P> var1, @NotNull Function<T, P> var2);
    }
}

