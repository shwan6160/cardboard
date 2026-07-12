/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.SyntaxFragment;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.Descriptor;
import java.lang.reflect.Method;
import java.util.List;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public interface CommandDescriptor
extends Descriptor {
    public @NonNull Method method();

    @Override
    default public @NonNull String name() {
        return this.commandToken();
    }

    public @NonNull List<@NonNull SyntaxFragment> syntax();

    public @NonNull String commandToken();

    public @NonNull Class<?> requiredSender();
}

