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
import com.bergerkiller.bukkit.tc.attachments.animation.Animation;
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

public final class AnimationSceneSuggestionProvider
implements BlockingSuggestionProvider.Strings<CommandSender> {
    public static final AnimationSceneSuggestionProvider TRAIN_ANIMATION_SCENE = new AnimationSceneSuggestionProvider(true);
    public static final AnimationSceneSuggestionProvider CART_ANIMATION_SCENE = new AnimationSceneSuggestionProvider(false);
    private final boolean forTrain;

    private AnimationSceneSuggestionProvider(boolean forTrain) {
        this.forTrain = forTrain;
    }

    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<CommandSender> context, @NonNull CommandInput commandInput) {
        IPropertiesHolder holder;
        String input = commandInput.lastRemainingToken();
        String animationName = (String)context.getOrDefault("animation_name", (Object)"");
        if (animationName.isEmpty()) {
            return Collections.emptyList();
        }
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
        List<String> filtered = holder.getAnimationScenes(animationName).stream().filter(name -> name.startsWith(input)).collect(Collectors.toList());
        if (!filtered.isEmpty()) {
            return filtered;
        }
        Animation defaultAnim = TCConfig.defaultAnimations.get(animationName);
        return defaultAnim == null ? Collections.emptyList() : new ArrayList<String>(defaultAnim.getSceneNames());
    }
}

