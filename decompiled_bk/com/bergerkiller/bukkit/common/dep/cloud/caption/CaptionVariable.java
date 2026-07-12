/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.caption;

import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariableImpl;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public interface CaptionVariable {
    public static @NonNull CaptionVariable of(@NonNull String key, @NonNull String value) {
        return CaptionVariableImpl.of(key, value);
    }

    public @NonNull String key();

    public @NonNull String value();
}

