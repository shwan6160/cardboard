/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations;

import com.bergerkiller.bukkit.common.dep.cloud.description.Description;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface DescriptionMapper {
    public static @NonNull DescriptionMapper simple() {
        return Description::of;
    }

    public @NonNull Description map(@NonNull String var1);
}

