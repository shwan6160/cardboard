/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser.standard;

import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.Range;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public abstract class NumberParser<C, N extends Number, R extends Range<N>>
implements ArgumentParser<C, N> {
    private final R range;

    protected NumberParser(@NonNull R range) {
        this.range = (Range)Objects.requireNonNull(range, "range");
    }

    public final @NonNull R range() {
        return this.range;
    }

    public abstract boolean hasMax();

    public abstract boolean hasMin();
}

