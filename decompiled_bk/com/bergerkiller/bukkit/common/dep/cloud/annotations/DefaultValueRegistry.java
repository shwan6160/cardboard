/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.common.returnsreceiver.qual.This
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.DefaultValueFactory;
import java.util.Optional;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;

@API(status=API.Status.STABLE)
public interface DefaultValueRegistry<C> {
    public <T> @This @NonNull DefaultValueRegistry<C> register(@NonNull String var1, @NonNull DefaultValueFactory<C, T> var2);

    public @NonNull Optional<@NonNull DefaultValueFactory<C, ?>> named(@NonNull String var1);
}

