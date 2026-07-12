/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.craftbukkit;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

@Deprecated
public final class MinecraftComponentSerializer
implements ComponentSerializer<Component, Component, Object> {
    private static final MinecraftComponentSerializer INSTANCE = new MinecraftComponentSerializer();
    private final com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer realSerial = com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer.get();

    public static boolean isSupported() {
        return com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer.isSupported();
    }

    @NotNull
    public static MinecraftComponentSerializer get() {
        return INSTANCE;
    }

    @Override
    @NotNull
    public Component deserialize(@NotNull Object input) {
        return this.realSerial.deserialize(input);
    }

    @Override
    @NotNull
    public Object serialize(@NotNull Component component) {
        return this.realSerial.serialize(component);
    }
}

