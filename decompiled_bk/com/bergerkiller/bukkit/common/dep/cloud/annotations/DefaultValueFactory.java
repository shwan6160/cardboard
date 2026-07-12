/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations;

import com.bergerkiller.bukkit.common.dep.cloud.component.DefaultValue;
import java.lang.reflect.Parameter;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
@API(status=API.Status.STABLE)
public interface DefaultValueFactory<C, T> {
    public static <C, T> @NonNull DefaultValueFactory<C, T> constant(@NonNull DefaultValue<C, T> defaultValue) {
        return parameter -> defaultValue;
    }

    public @NonNull DefaultValue<C, T> create(@NonNull Parameter var1);
}

