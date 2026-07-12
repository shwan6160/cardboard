/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.CheckReturnValue
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.builder.AbstractBuilder;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.InheritanceAwareMapImpl;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface InheritanceAwareMap<C, V> {
    @NotNull
    public static <K, E> InheritanceAwareMap<K, E> empty() {
        return InheritanceAwareMapImpl.EMPTY;
    }

    public static <K, E> @NotNull Builder<K, E> builder() {
        return new InheritanceAwareMapImpl.BuilderImpl();
    }

    public static <K, E> @NotNull Builder<K, E> builder(InheritanceAwareMap<? extends K, ? extends E> existing) {
        return new InheritanceAwareMapImpl.BuilderImpl<K, E>().putAll(existing);
    }

    public boolean containsKey(@NotNull Class<? extends C> var1);

    @Nullable
    public V get(@NotNull Class<? extends C> var1);

    @CheckReturnValue
    @NotNull
    public InheritanceAwareMap<C, V> with(@NotNull Class<? extends C> var1, @NotNull V var2);

    @CheckReturnValue
    @NotNull
    public InheritanceAwareMap<C, V> without(@NotNull Class<? extends C> var1);

    public static interface Builder<C, V>
    extends AbstractBuilder<InheritanceAwareMap<C, V>> {
        @NotNull
        public Builder<C, V> strict(boolean var1);

        @NotNull
        public Builder<C, V> put(@NotNull Class<? extends C> var1, @NotNull V var2);

        @NotNull
        public Builder<C, V> remove(@NotNull Class<? extends C> var1);

        @NotNull
        public Builder<C, V> putAll(@NotNull InheritanceAwareMap<? extends C, ? extends V> var1);
    }
}

