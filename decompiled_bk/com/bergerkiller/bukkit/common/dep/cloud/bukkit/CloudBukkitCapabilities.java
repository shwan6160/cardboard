/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit;

import com.bergerkiller.bukkit.common.dep.cloud.CloudCapability;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.CraftBukkitReflection;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

public enum CloudBukkitCapabilities implements CloudCapability
{
    BRIGADIER(CraftBukkitReflection.classExists("com.mojang.brigadier.tree.CommandNode") && (CraftBukkitReflection.findOBCClass("command.BukkitCommandWrapper") != null || CraftBukkitReflection.classExists("io.papermc.paper.command.brigadier.CommandSourceStack"))),
    NATIVE_BRIGADIER(CraftBukkitReflection.classExists("com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent") || CraftBukkitReflection.classExists("io.papermc.paper.command.brigadier.CommandSourceStack")),
    COMMODORE_BRIGADIER(BRIGADIER.capable() && !NATIVE_BRIGADIER.capable() && !CraftBukkitReflection.classExists("org.bukkit.entity.Warden")),
    ASYNCHRONOUS_COMPLETION(CraftBukkitReflection.classExists("com.destroystokyo.paper.event.server.AsyncTabCompleteEvent"));

    @API(status=API.Status.INTERNAL)
    public static final Set<CloudBukkitCapabilities> CAPABLE;
    private final boolean capable;

    private CloudBukkitCapabilities(boolean capable) {
        this.capable = capable;
    }

    boolean capable() {
        return this.capable;
    }

    @Override
    public @NonNull String toString() {
        return this.name();
    }

    static {
        CAPABLE = Arrays.stream(CloudBukkitCapabilities.values()).filter(CloudBukkitCapabilities::capable).collect(Collectors.toSet());
    }
}

