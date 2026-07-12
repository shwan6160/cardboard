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
public class Sextet<U, V, W, X, Y, Z>
implements Tuple {
    private final U first;
    private final V second;
    private final W third;
    private final X fourth;
    private final Y fifth;
    private final Z sixth;

    protected Sextet(@NonNull U first, @NonNull V second, @NonNull W third, @NonNull X fourth, @NonNull Y fifth, @NonNull Z sixth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.fifth = fifth;
        this.sixth = sixth;
    }

    public static <U, V, W, X, Y, Z> @NonNull Sextet<@NonNull U, @NonNull V, @NonNull W, @NonNull X, @NonNull Y, @NonNull Z> of(@NonNull U first, @NonNull V second, @NonNull W third, @NonNull X fourth, @NonNull Y fifth, @NonNull Z sixth) {
        return new Sextet<U, V, W, X, Y, Z>(first, second, third, fourth, fifth, sixth);
    }

    public final @NonNull U first() {
        return this.first;
    }

    public final @NonNull V second() {
        return this.second;
    }

    public final @NonNull W third() {
        return this.third;
    }

    public final @NonNull X fourth() {
        return this.fourth;
    }

    public final @NonNull Y fifth() {
        return this.fifth;
    }

    public final @NonNull Z sixth() {
        return this.sixth;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Sextet sextet = (Sextet)o;
        return Objects.equals(this.first(), sextet.first()) && Objects.equals(this.second(), sextet.second()) && Objects.equals(this.third(), sextet.third()) && Objects.equals(this.fourth(), sextet.fourth()) && Objects.equals(this.fifth(), sextet.fifth()) && Objects.equals(this.sixth(), sextet.sixth());
    }

    public final int hashCode() {
        return Objects.hash(this.first(), this.second(), this.third(), this.fourth(), this.fifth(), this.sixth());
    }

    public final String toString() {
        return String.format("(%s, %s, %s, %s, %s, %s)", this.first, this.second, this.third, this.fourth, this.fifth, this.sixth);
    }

    @Override
    public final int size() {
        return 6;
    }

    @Override
    public final @NonNull Object @NonNull [] toArray() {
        Object[] array = new Object[]{this.first, this.second, this.third, this.fourth, this.fifth, this.sixth};
        return array;
    }
}

