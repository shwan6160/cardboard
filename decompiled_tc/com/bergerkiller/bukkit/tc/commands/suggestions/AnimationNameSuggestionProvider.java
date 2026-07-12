/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput
 *  com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider$Strings
 *  org.bukkit.command.CommandSender
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.tc.commands.suggestions;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.IProperties;
import com.bergerkiller.bukkit.tc.properties.IPropertiesHolder;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class AnimationNameSuggestionProvider
implements BlockingSuggestionProvider.Strings<CommandSender> {
    public static final AnimationNameSuggestionProvider TRAIN_ANIMATION_NAME = new AnimationNameSuggestionProvider(true);
    public static final AnimationNameSuggestionProvider CART_ANIMATION_NAME = new AnimationNameSuggestionProvider(false);
    private final boolean forTrain;

    private AnimationNameSuggestionProvider(boolean forTrain) {
        this.forTrain = forTrain;
    }

    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<CommandSender> context, @NonNull CommandInput commandInput) {
        IPropertiesHolder holder;
        String input = commandInput.lastRemainingToken();
        try {
            IProperties properties;
            if (this.forTrain) {
                properties = (TrainProperties)context.inject(TrainProperties.class).get();
                holder = ((TrainProperties)properties).getHolder();
            } else {
                properties = (CartProperties)context.inject(CartProperties.class).get();
                holder = ((CartProperties)properties).getHolder();
            }
        }
        catch (RuntimeException ex) {
            return Collections.emptyList();
        }
        if (holder == null) {
            return Collections.emptyList();
        }
        List<String> filtered = holder.getAnimationNames().stream().filter(name -> name.startsWith(input)).collect(Collectors.toList());
        if (!filtered.isEmpty()) {
            return filtered;
        }
        return new ArrayList<String>(TCConfig.defaultAnimations.keySet());
    }
}

