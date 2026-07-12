/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud;

import com.bergerkiller.bukkit.common.dep.cloud.SenderMapperImpl;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface SenderMapper<B, M> {
    public @NonNull M map(@NonNull B var1);

    public @NonNull B reverse(@NonNull M var1);

    public static <B, M> @NonNull SenderMapper<B, M> create(@NonNull Function<@NonNull B, @NonNull M> map, @NonNull Function<@NonNull M, @NonNull B> reverse) {
        return new SenderMapperImpl<B, M>(map, reverse);
    }

    public static <S> @NonNull SenderMapper<S, S> identity() {
        return SenderMapperImpl.IDENTITY;
    }
}

