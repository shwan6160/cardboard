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
public class Pair<U, V>
implements Tuple {
    private final U first;
    private final V second;

    protected Pair(U first, V second) {
        this.first = first;
        this.second = second;
    }

    public static <U, V> @NonNull Pair<U, V> of(U first, V second) {
        return new Pair<U, V>(first, second);
    }

    public final U first() {
        return this.first;
    }

    public final V second() {
        return this.second;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Pair pair = (Pair)o;
        return Objects.equals(this.first(), pair.first()) && Objects.equals(this.second(), pair.second());
    }

    public final int hashCode() {
        return Objects.hash(this.first(), this.second());
    }

    public final String toString() {
        return String.format("(%s, %s)", this.first, this.second);
    }

    @Override
    public final int size() {
        return 2;
    }

    @Override
    public final @NonNull Object @NonNull [] toArray() {
        Object[] array = new Object[]{this.first, this.second};
        return array;
    }
}

