/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.wrappers.HolderImpl;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Optional;
import java.util.function.Function;

public interface Holder<T> {
    public T value();

    public Object rawValue();

    public Optional<ResourceKey<T>> key();

    public Object toRawHolder();

    public static <T extends Template.Handle> Holder<T> direct(T handleInstance) {
        return HolderImpl.direct(handleInstance);
    }

    public static <T extends Template.Handle> Holder<T> directWrap(Object rawValue, Function<Object, T> handleCtor) {
        return HolderImpl.directWrap(rawValue, handleCtor);
    }

    public static <T> Holder<T> fromHandle(Object rawHolder, Function<Object, T> handleCtor) {
        return new HolderImpl<Function<Object, T>>(rawHolder, handleCtor);
    }
}

