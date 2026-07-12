/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.type.tuple;

import com.bergerkiller.bukkit.common.dep.cloud.type.tuple.Tuple;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public class Triplet<U, V, W>
implements Tuple {
    private final U first;
    private final V second;
    private final W third;

    protected Triplet(@NonNull U first, @NonNull V second, @NonNull W third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <U, V, W> @NonNull Triplet<@NonNull U, @NonNull V, @NonNull W> of(@NonNull U first, @NonNull V second, @NonNull W third) {
        return new Triplet<U, V, W>(first, second, third);
    }

    public final U first() {
        return this.first;
    }

    public final V second() {
        return this.second;
    }

    public final W third() {
        return this.third;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Triplet triplet = (Triplet)o;
        return Objects.equals(this.first(), triplet.first()) && Objects.equals(this.second(), triplet.second()) && Objects.equals(this.third(), triplet.third());
    }

    public final int hashCode() {
        return Objects.hash(this.first(), this.second(), this.third());
    }

    public final String toString() {
        return String.format("(%s, %s, %s)", this.first, this.second, this.third);
    }

    @Override
    public final int size() {
        return 3;
    }

    @Override
    public final @NonNull Object @NonNull [] toArray() {
        Object[] array = new Object[]{this.first, this.second, this.third};
        return array;
    }
}

