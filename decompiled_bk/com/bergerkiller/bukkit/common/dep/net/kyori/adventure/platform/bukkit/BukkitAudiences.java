/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.Audience;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.AudienceProvider;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.BukkitAudiencesImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.BukkitEmitter;
import java.util.function.Predicate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface BukkitAudiences
extends AudienceProvider {
    @NotNull
    public static BukkitAudiences create(@NotNull Plugin plugin) {
        return BukkitAudiencesImpl.instanceFor(plugin);
    }

    @NotNull
    public static Builder builder(@NotNull Plugin plugin) {
        return BukkitAudiencesImpl.builder(plugin);
    }

    public static  @NotNull Sound.Emitter asEmitter(@NotNull Entity entity) {
        return new BukkitEmitter(entity);
    }

    @NotNull
    public Audience sender(@NotNull CommandSender var1);

    @NotNull
    public Audience player(@NotNull Player var1);

    @NotNull
    public Audience filter(@NotNull Predicate<CommandSender> var1);

    public static interface Builder
    extends AudienceProvider.Builder<BukkitAudiences, Builder> {
    }
}

