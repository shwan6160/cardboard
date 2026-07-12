/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.suggestion.ComponentTooltipSuggestionImpl;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Value;

@Value.Immutable
public interface ComponentTooltipSuggestion
extends Suggestion {
    @Override
    public @NonNull String suggestion();

    public @Nullable Component tooltip();

    @Override
    public @NonNull ComponentTooltipSuggestion withSuggestion(@NonNull String var1);

    public static @NonNull ComponentTooltipSuggestion suggestion(@NonNull String suggestion) {
        return ComponentTooltipSuggestionImpl.of(suggestion, null);
    }

    public static @NonNull ComponentTooltipSuggestion suggestion(@NonNull String suggestion, @Nullable Component tooltip) {
        return ComponentTooltipSuggestionImpl.of(suggestion, tooltip);
    }
}

