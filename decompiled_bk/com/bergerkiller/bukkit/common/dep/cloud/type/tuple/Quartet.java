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
public class Quartet<U, V, W, X>
implements Tuple {
    private final U first;
    private final V second;
    private final W third;
    private final X fourth;

    protected Quartet(@NonNull U first, @NonNull V second, @NonNull W third, @NonNull X fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public static <U, V, W, X> @NonNull Quartet<@NonNull U, @NonNull V, @NonNull W, @NonNull X> of(@NonNull U first, @NonNull V second, @NonNull W third, @NonNull X fourth) {
        return new Quartet<U, V, W, X>(first, second, third, fourth);
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

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Quartet quartet = (Quartet)o;
        return Objects.equals(this.first(), quartet.first()) && Objects.equals(this.second(), quartet.second()) && Objects.equals(this.third(), quartet.third()) && Objects.equals(this.fourth(), quartet.fourth());
    }

    public final int hashCode() {
        return Objects.hash(this.first(), this.second(), this.third(), this.fourth());
    }

    public final String toString() {
        return String.format("(%s, %s, %s, %s)", this.first, this.second, this.third, this.fourth);
    }

    @Override
    public final int size() {
        return 4;
    }

    @Override
    public final @NonNull Object @NonNull [] toArray() {
        Object[] array = new Object[]{this.first, this.second, this.third, this.fourth};
        return array;
    }
}

