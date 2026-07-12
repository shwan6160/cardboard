/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.DataComponentValue;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.DataComponentValueConversionImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.Services;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.Examinable;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DataComponentValueConverterRegistry {
    private static final Set<Provider> PROVIDERS = Services.services(Provider.class);

    private DataComponentValueConverterRegistry() {
    }

    public static Set<Key> knownProviders() {
        return Collections.unmodifiableSet(PROVIDERS.stream().map(Provider::id).collect(Collectors.toSet()));
    }

    @NotNull
    public static <O extends DataComponentValue> O convert(@NotNull Class<O> target, @NotNull Key key, @NotNull DataComponentValue in) {
        if (target.isInstance(in)) {
            return (O)((DataComponentValue)target.cast(in));
        }
        @Nullable RegisteredConversion converter = ConversionCache.converter(in.getClass(), target);
        if (converter == null) {
            throw new IllegalArgumentException("There is no data holder converter registered to convert from a " + in.getClass() + " instance to a " + target + " (on field " + key + ")");
        }
        try {
            return (O)((DataComponentValue)converter.conversion.convert(key, in));
        }
        catch (Exception ex) {
            throw new IllegalStateException("Failed to convert data component value of type " + in.getClass() + " to type " + target + " due to an error in a converter provided by " + converter.provider.asString() + "!", ex);
        }
    }

    static final class ConversionCache {
        private static final ConcurrentMap<Class<?>, ConcurrentMap<Class<?>, RegisteredConversion>> CACHE = new ConcurrentHashMap();
        private static final Map<Class<?>, Set<RegisteredConversion>> CONVERSIONS = ConversionCache.collectConversions();

        ConversionCache() {
        }

        private static Map<Class<?>, Set<RegisteredConversion>> collectConversions() {
            ConcurrentHashMap<Class, Set> collected = new ConcurrentHashMap<Class, Set>();
            for (Provider provider : PROVIDERS) {
                @NotNull Key id = Objects.requireNonNull(provider.id(), () -> "ID of provider " + provider + " is null");
                for (Conversion<?, ?> conv : provider.conversions()) {
                    collected.computeIfAbsent(conv.source(), $ -> ConcurrentHashMap.newKeySet()).add(new RegisteredConversion(id, conv));
                }
            }
            for (Map.Entry entry : collected.entrySet()) {
                entry.setValue(Collections.unmodifiableSet((Set)entry.getValue()));
            }
            return new ConcurrentHashMap(collected);
        }

        static RegisteredConversion compute(Class<?> src, Class<?> dst) {
            Class sourcePtr;
            ArrayDeque sourceTypes = new ArrayDeque();
            sourceTypes.add(src);
            while ((sourcePtr = (Class)sourceTypes.poll()) != null) {
                Set<RegisteredConversion> conversions = CONVERSIONS.get(sourcePtr);
                if (conversions != null) {
                    RegisteredConversion nearest = null;
                    for (RegisteredConversion potential : conversions) {
                        Class<?> potentialDst = potential.conversion.destination();
                        if (dst.equals(potentialDst)) {
                            return potential;
                        }
                        if (!dst.isAssignableFrom(potentialDst) || nearest != null && !potentialDst.isAssignableFrom(nearest.conversion.destination())) continue;
                        nearest = potential;
                    }
                    if (nearest != null) {
                        return nearest;
                    }
                }
                ConversionCache.addSupertypes(sourcePtr, sourceTypes);
            }
            return RegisteredConversion.NONE;
        }

        private static void addSupertypes(Class<?> clazz, Deque<Class<?>> queue) {
            if (clazz.getSuperclass() != null) {
                queue.add(clazz.getSuperclass());
            }
            queue.addAll(Arrays.asList(clazz.getInterfaces()));
        }

        @Nullable
        static RegisteredConversion converter(Class<? extends DataComponentValue> src, Class<? extends DataComponentValue> dst) {
            RegisteredConversion result = CACHE.computeIfAbsent(src, $ -> new ConcurrentHashMap()).computeIfAbsent(dst, $$ -> ConversionCache.compute(src, dst));
            if (result == RegisteredConversion.NONE) {
                return null;
            }
            return result;
        }
    }

    static final class RegisteredConversion {
        static final RegisteredConversion NONE = new RegisteredConversion(null, null);
        final Key provider;
        final Conversion<?, ?> conversion;

        RegisteredConversion(Key provider, Conversion<?, ?> conversion) {
            this.provider = provider;
            this.conversion = conversion;
        }
    }

    @ApiStatus.NonExtendable
    public static interface Conversion<I, O>
    extends Examinable {
        @NotNull
        public static <I1, O1> Conversion<I1, O1> convert(@NotNull Class<I1> src, @NotNull Class<O1> dst, @NotNull BiFunction<Key, I1, O1> op) {
            return new DataComponentValueConversionImpl<I1, O1>(Objects.requireNonNull(src, "src"), Objects.requireNonNull(dst, "dst"), Objects.requireNonNull(op, "op"));
        }

        @Contract(pure=true)
        @NotNull
        public Class<I> source();

        @Contract(pure=true)
        @NotNull
        public Class<O> destination();

        @NotNull
        public O convert(@NotNull Key var1, @NotNull I var2);
    }

    public static interface Provider {
        @NotNull
        public Key id();

        @NotNull
        public Iterable<Conversion<?, ?>> conversions();
    }
}

