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
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class DynamicTuple
implements Tuple {
    private final Object[] internalArray;

    private DynamicTuple(@NonNull Object @NonNull [] internalArray) {
        this.internalArray = internalArray;
    }

    public static @NonNull DynamicTuple of(Object ... elements) {
        return new DynamicTuple(elements);
    }

    @Override
    public int size() {
        return this.internalArray.length;
    }

    @Override
    public @NonNull Object @NonNull [] toArray() {
        @NonNull Object @NonNull [] newArray = new Object[this.internalArray.length];
        System.arraycopy(this.internalArray, 0, newArray, 0, this.internalArray.length);
        return newArray;
    }
}

