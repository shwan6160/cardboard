/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.internal.Internals;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.DataComponentValueConverterRegistry;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.ExaminableProperty;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

final class DataComponentValueConversionImpl<I, O>
implements DataComponentValueConverterRegistry.Conversion<I, O> {
    private final Class<I> source;
    private final Class<O> destination;
    private final BiFunction<Key, I, O> conversion;

    DataComponentValueConversionImpl(@NotNull Class<I> source, @NotNull Class<O> destination, @NotNull BiFunction<Key, I, O> conversion) {
        this.source = source;
        this.destination = destination;
        this.conversion = conversion;
    }

    @Override
    @NotNull
    public Class<I> source() {
        return this.source;
    }

    @Override
    @NotNull
    public Class<O> destination() {
        return this.destination;
    }

    @Override
    @NotNull
    public O convert(@NotNull Key key, @NotNull I input) {
        return this.conversion.apply(Objects.requireNonNull(key, "key"), Objects.requireNonNull(input, "input"));
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("source", this.source), ExaminableProperty.of("destination", this.destination), ExaminableProperty.of("conversion", this.conversion));
    }

    public String toString() {
        return Internals.toString(this);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        DataComponentValueConversionImpl that = (DataComponentValueConversionImpl)other;
        return Objects.equals(this.source, that.source) && Objects.equals(this.destination, that.destination) && Objects.equals(this.conversion, that.conversion);
    }

    public int hashCode() {
        return Objects.hash(this.source, this.destination, this.conversion);
    }
}

