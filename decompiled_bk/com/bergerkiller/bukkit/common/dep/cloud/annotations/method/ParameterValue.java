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
package com.bergerkiller.bukkit.common.dep.cloud.annotations.method;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.Descriptor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.method.ParameterValueImpl;
import java.lang.reflect.Parameter;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Value;

@API(status=API.Status.INTERNAL)
@Value.Immutable
public interface ParameterValue {
    public static @NonNull ParameterValue of(@NonNull Parameter parameter, @Nullable Object value) {
        return ParameterValue.of(parameter, value, null);
    }

    public static @NonNull ParameterValue of(@NonNull Parameter parameter, @Nullable Object value, @Nullable Descriptor descriptor) {
        return ParameterValueImpl.of(parameter, value, descriptor);
    }

    public @NonNull Parameter parameter();

    public @Nullable Object value();

    public @Nullable Descriptor descriptor();
}

