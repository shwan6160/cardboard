/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.InheritanceAwareMap;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class InheritanceAwareMapImpl<C, V>
implements InheritanceAwareMap<C, V> {
    private static final Object NONE = new Object();
    static final InheritanceAwareMapImpl EMPTY = new InheritanceAwareMapImpl(false, Collections.emptyMap());
    private final Map<Class<? extends C>, V> declaredValues;
    private final boolean strict;
    private final transient ConcurrentMap<Class<? extends C>, Object> cache = new ConcurrentHashMap<Class<? extends C>, Object>();

    InheritanceAwareMapImpl(boolean strict, Map<Class<? extends C>, V> declaredValues) {
        this.strict = strict;
        this.declaredValues = declaredValues;
    }

    @Override
    public boolean containsKey(@NotNull Class<? extends C> clazz) {
        return this.get(clazz) != null;
    }

    @Override
    @Nullable
    public V get(@NotNull Class<? extends C> clazz) {
        Object ret = this.cache.computeIfAbsent(clazz, c -> {
            @Nullable V value = this.declaredValues.get(c);
            if (value != null) {
                return value;
            }
            for (Map.Entry<Class<C>, V> entry : this.declaredValues.entrySet()) {
                if (!entry.getKey().isAssignableFrom((Class<?>)c)) continue;
                return entry.getValue();
            }
            return NONE;
        });
        return (V)(ret == NONE ? null : ret);
    }

    @Override
    @NotNull
    public InheritanceAwareMap<C, V> with(@NotNull Class<? extends C> clazz, @NotNull V value) {
        if (Objects.equals(this.declaredValues.get(clazz), value)) {
            return this;
        }
        if (this.strict) {
            InheritanceAwareMapImpl.validateNoneInHierarchy(clazz, this.declaredValues);
        }
        LinkedHashMap<Class<C>, V> newValues = new LinkedHashMap<Class<C>, V>(this.declaredValues);
        newValues.put(clazz, value);
        return new InheritanceAwareMapImpl<C, V>(this.strict, Collections.unmodifiableMap(newValues));
    }

    @Override
    @NotNull
    public InheritanceAwareMap<C, V> without(@NotNull Class<? extends C> clazz) {
        if (!this.declaredValues.containsKey(clazz)) {
            return this;
        }
        LinkedHashMap<Class<C>, V> newValues = new LinkedHashMap<Class<C>, V>(this.declaredValues);
        newValues.remove(clazz);
        return new InheritanceAwareMapImpl<C, V>(this.strict, Collections.unmodifiableMap(newValues));
    }

    private static void validateNoneInHierarchy(Class<?> beingRegistered, Map<? extends Class<?>, ?> entries) {
        for (Class<?> clazz : entries.keySet()) {
            InheritanceAwareMapImpl.testHierarchy(clazz, beingRegistered);
        }
    }

    private static void testHierarchy(Class<?> existing, Class<?> beingRegistered) {
        if (!existing.equals(beingRegistered) && (existing.isAssignableFrom(beingRegistered) || beingRegistered.isAssignableFrom(existing))) {
            throw new IllegalArgumentException("Conflict detected between already registered type " + existing + " and newly registered type " + beingRegistered + "! Types in a strict inheritance-aware map must not share a common hierarchy!");
        }
    }

    static final class BuilderImpl<C, V>
    implements InheritanceAwareMap.Builder<C, V> {
        private boolean strict;
        private final Map<Class<? extends C>, V> values = new LinkedHashMap<Class<? extends C>, V>();

        BuilderImpl() {
        }

        @Override
        @NotNull
        public InheritanceAwareMap<C, V> build() {
            return new InheritanceAwareMapImpl<C, V>(this.strict, Collections.unmodifiableMap(new LinkedHashMap<Class<? extends C>, V>(this.values)));
        }

        @Override
        @NotNull
        public InheritanceAwareMap.Builder<C, V> strict(boolean strict) {
            if (strict && !this.strict) {
                for (Class<? extends C> clazz : this.values.keySet()) {
                    InheritanceAwareMapImpl.validateNoneInHierarchy(clazz, this.values);
                }
            }
            this.strict = strict;
            return this;
        }

        @Override
        @NotNull
        public InheritanceAwareMap.Builder<C, V> put(@NotNull Class<? extends C> clazz, @NotNull V value) {
            if (this.strict) {
                InheritanceAwareMapImpl.validateNoneInHierarchy(clazz, this.values);
            }
            this.values.put(Objects.requireNonNull(clazz, "clazz"), Objects.requireNonNull(value, "value"));
            return this;
        }

        @Override
        @NotNull
        public InheritanceAwareMap.Builder<C, V> remove(@NotNull Class<? extends C> clazz) {
            this.values.remove(Objects.requireNonNull(clazz, "clazz"));
            return this;
        }

        @Override
        @NotNull
        public InheritanceAwareMap.Builder<C, V> putAll(@NotNull InheritanceAwareMap<? extends C, ? extends V> map) {
            InheritanceAwareMapImpl impl = (InheritanceAwareMapImpl)map;
            if (!(!this.strict || this.values.isEmpty() && impl.strict)) {
                for (Map.Entry entry : impl.declaredValues.entrySet()) {
                    InheritanceAwareMapImpl.validateNoneInHierarchy((Class)entry.getKey(), this.values);
                    this.values.put((Class)entry.getKey(), entry.getValue());
                }
                return this;
            }
            this.values.putAll(impl.declaredValues);
            return this;
        }
    }
}

