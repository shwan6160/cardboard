/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.event.server.AsyncTabCompleteEvent$Completion
 *  io.papermc.paper.brigadier.PaperBrigadier
 *  io.papermc.paper.command.brigadier.MessageComponentSerializer
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper.suggestion.tooltips;

import com.bergerkiller.bukkit.common.dep.cloud.brigadier.suggestion.TooltipSuggestion;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.CraftBukkitReflection;
import com.bergerkiller.bukkit.common.dep.cloud.paper.suggestion.tooltips.CompletionMapper;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.mojang.brigadier.Message;
import io.papermc.paper.brigadier.PaperBrigadier;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

final class NativeCompletionMapper
implements CompletionMapper {
    NativeCompletionMapper() {
    }

    @Override
    public // Could not load outer class - annotation placement on inner may be incorrect
     @NonNull AsyncTabCompleteEvent.Completion map(@NonNull TooltipSuggestion suggestion) {
        if (!CraftBukkitReflection.classExists("io.papermc.paper.command.brigadier.MessageComponentSerializer")) {
            return NativeCompletionMapper.mapLegacy(suggestion);
        }
        return AsyncTabCompleteEvent.Completion.completion((String)suggestion.suggestion(), (Component)MessageComponentSerializer.message().deserializeOrNull((Object)suggestion.tooltip()));
    }

    private static // Could not load outer class - annotation placement on inner may be incorrect
     @NonNull AsyncTabCompleteEvent.Completion mapLegacy(@NotNull TooltipSuggestion suggestion) {
        Message tooltip = suggestion.tooltip();
        if (tooltip == null) {
            return AsyncTabCompleteEvent.Completion.completion((String)suggestion.suggestion());
        }
        return AsyncTabCompleteEvent.Completion.completion((String)suggestion.suggestion(), (Component)PaperBrigadier.componentFromMessage((Message)tooltip));
    }
}

