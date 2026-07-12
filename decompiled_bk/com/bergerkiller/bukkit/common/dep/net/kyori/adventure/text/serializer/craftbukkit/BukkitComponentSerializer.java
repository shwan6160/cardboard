/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.craftbukkit;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

@Deprecated
public final class BukkitComponentSerializer {
    private BukkitComponentSerializer() {
    }

    @NotNull
    public static LegacyComponentSerializer legacy() {
        return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer.legacy();
    }

    @NotNull
    public static GsonComponentSerializer gson() {
        return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer.gson();
    }
}

