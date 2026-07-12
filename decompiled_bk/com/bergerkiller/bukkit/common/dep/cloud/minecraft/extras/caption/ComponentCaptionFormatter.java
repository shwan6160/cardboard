/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.minimessage.MiniMessage
 *  com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.caption;

import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionFormatter;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.caption.MiniMessageComponentCaptionFormatter;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.caption.PatternReplacingComponentCaptionFormatter;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.caption.RichVariable;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.minimessage.MiniMessage;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE, since="2.0.0")
public interface ComponentCaptionFormatter<C>
extends CaptionFormatter<C, Component> {
    public static <C> @NonNull ComponentCaptionFormatter<C> miniMessage() {
        return ComponentCaptionFormatter.miniMessage(MiniMessage.miniMessage());
    }

    public static <C> @NonNull ComponentCaptionFormatter<C> miniMessage(@NonNull MiniMessage miniMessage) {
        return ComponentCaptionFormatter.miniMessage(miniMessage, new TagResolver[0]);
    }

    public static <C> @NonNull ComponentCaptionFormatter<C> miniMessage(@NonNull MiniMessage miniMessage, TagResolver ... resolvers) {
        return new MiniMessageComponentCaptionFormatter(miniMessage, Arrays.asList(resolvers));
    }

    @API(status=API.Status.EXPERIMENTAL)
    public static <C> @NonNull ComponentCaptionFormatter<C> translatable() {
        return new ComponentCaptionFormatter<C>(){

            @Override
            public @NonNull Component formatCaption(@NonNull Caption captionKey, @NonNull C recipient, @NonNull String caption, @NonNull List<@NonNull CaptionVariable> variables) {
                return Component.translatable(captionKey.key(), variables.stream().map(variable -> {
                    if (variable instanceof RichVariable) {
                        return (RichVariable)variable;
                    }
                    return Component.text(variable.value());
                }).collect(Collectors.toList()));
            }
        };
    }

    public static <C> @NonNull ComponentCaptionFormatter<C> patternReplacing(@NonNull Pattern pattern) {
        return new PatternReplacingComponentCaptionFormatter(pattern);
    }

    public static <C> @NonNull ComponentCaptionFormatter<C> placeholderReplacing() {
        return new PatternReplacingComponentCaptionFormatter(CaptionFormatter.placeholderPattern());
    }
}

