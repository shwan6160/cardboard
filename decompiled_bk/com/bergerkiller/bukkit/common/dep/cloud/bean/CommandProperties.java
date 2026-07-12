/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.bean;

import com.bergerkiller.bukkit.common.dep.cloud.bean.CommandPropertiesImpl;
import java.util.Arrays;
import java.util.Collection;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public interface CommandProperties {
    public static @NonNull CommandProperties of(@NonNull String name, String ... aliases) {
        return CommandPropertiesImpl.of(name, Arrays.asList(aliases));
    }

    public static @NonNull CommandProperties commandProperties(@NonNull String name, String ... aliases) {
        return CommandProperties.of(name, aliases);
    }

    public @NonNull String name();

    public @NonNull Collection<@NonNull String> aliases();
}

