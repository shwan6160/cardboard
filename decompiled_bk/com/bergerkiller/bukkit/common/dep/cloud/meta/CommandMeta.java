/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.meta;

import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKeyContainer;
import com.bergerkiller.bukkit.common.dep.cloud.meta.CommandMetaBuilder;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public abstract class CommandMeta
implements CloudKeyContainer {
    public static @NonNull CommandMetaBuilder builder() {
        return new CommandMetaBuilder();
    }

    @API(status=API.Status.STABLE)
    public static @NonNull CommandMeta empty() {
        return CommandMeta.builder().build();
    }

    public final @NonNull String toString() {
        return "";
    }
}

