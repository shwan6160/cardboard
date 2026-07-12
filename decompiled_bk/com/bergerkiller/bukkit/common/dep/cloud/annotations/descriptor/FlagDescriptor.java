/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.Descriptor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.ImmutableFlagDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.description.Description;
import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import java.lang.reflect.Parameter;
import java.util.Collection;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public interface FlagDescriptor
extends Descriptor {
    public static @NonNull ImmutableFlagDescriptor.Builder builder() {
        return ImmutableFlagDescriptor.builder();
    }

    public @NonNull Parameter parameter();

    @Override
    public @NonNull String name();

    public @NonNull Collection<@NonNull String> aliases();

    public @Nullable String parserName();

    public @Nullable String suggestions();

    public @Nullable Permission permission();

    public @Nullable Description description();

    public boolean repeatable();
}

