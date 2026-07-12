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
public class Quintet<U, V, W, X, Y>
implements Tuple {
    private final U first;
    private final V second;
    private final W third;
    private final X fourth;
    private final Y fifth;

    protected Quintet(@NonNull U first, @NonNull V second, @NonNull W third, @NonNull X fourth, @NonNull Y fifth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.fifth = fifth;
    }

    public static <U, V, W, X, Y> @NonNull Quintet<@NonNull U, @NonNull V, @NonNull W, @NonNull X, @NonNull Y> of(@NonNull U first, @NonNull V second, @NonNull W third, @NonNull X fourth, @NonNull Y fifth) {
        return new Quintet<U, V, W, X, Y>(first, second, third, fourth, fifth);
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

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Quintet quintet = (Quintet)o;
        return Objects.equals(this.first(), quintet.first()) && Objects.equals(this.second(), quintet.second()) && Objects.equals(this.third(), quintet.third()) && Objects.equals(this.fourth(), quintet.fourth()) && Objects.equals(this.fifth(), quintet.fifth());
    }

    public final int hashCode() {
        return Objects.hash(this.first(), this.second(), this.third(), this.fourth(), this.fifth());
    }

    public final String toString() {
        return String.format("(%s, %s, %s, %s, %s)", this.first, this.second, this.third, this.fourth, this.fifth);
    }

    @Override
    public final int size() {
        return 5;
    }

    @Override
    public final @NonNull Object @NonNull [] toArray() {
        Object[] array = new Object[]{this.first, this.second, this.third, this.fourth, this.fifth};
        return array;
    }
}

