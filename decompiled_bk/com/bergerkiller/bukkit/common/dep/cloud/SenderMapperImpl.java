/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud;

import com.bergerkiller.bukkit.common.dep.cloud.SenderMapper;
import java.util.Objects;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class SenderMapperImpl<A, B>
implements SenderMapper<A, B> {
    static final SenderMapper<?, ?> IDENTITY = new SenderMapperImpl(Function.identity(), Function.identity());
    private final @NonNull Function<@NonNull A, @NonNull B> map;
    private final @NonNull Function<@NonNull B, @NonNull A> reverse;

    SenderMapperImpl(@NonNull Function<@NonNull A, @NonNull B> map, @NonNull Function<@NonNull B, @NonNull A> reverse) {
        this.map = Objects.requireNonNull(map, "map function");
        this.reverse = Objects.requireNonNull(reverse, "reverse function");
    }

    @Override
    public @NonNull B map(@NonNull A base) {
        return this.map.apply(base);
    }

    @Override
    public @NonNull A reverse(@NonNull B mapped) {
        return this.reverse.apply(mapped);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SenderMapperImpl that = (SenderMapperImpl)o;
        return Objects.equals(this.map, that.map) && Objects.equals(this.reverse, that.reverse);
    }

    public int hashCode() {
        return Objects.hash(this.map, this.reverse);
    }

    public String toString() {
        return "SenderMapperImpl{map=" + this.map + ", reverse=" + this.reverse + '}';
    }
}

