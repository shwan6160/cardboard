/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.minimessage.MiniMessage
 *  com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
 *  com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
 *  com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver$Builder
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.caption;

import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.caption.ComponentCaptionFormatter;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.caption.RichVariable;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ComponentLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.minimessage.MiniMessage;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import java.util.List;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL)
final class MiniMessageComponentCaptionFormatter<C>
implements ComponentCaptionFormatter<C> {
    private final MiniMessage miniMessage;
    private final List<TagResolver> extraResolvers;

    MiniMessageComponentCaptionFormatter(@NonNull MiniMessage miniMessage, @NonNull List<TagResolver> extraResolvers) {
        this.miniMessage = miniMessage;
        this.extraResolvers = extraResolvers;
    }

    @Override
    public @NonNull Component formatCaption(@NonNull Caption captionKey, @NonNull C recipient, @NonNull String caption, @NonNull List<@NonNull CaptionVariable> variables) {
        TagResolver.Builder builder = TagResolver.builder();
        builder.resolvers(this.extraResolvers);
        for (CaptionVariable variable : variables) {
            String key = variable.key();
            if (variable instanceof RichVariable) {
                builder.resolver((TagResolver)Placeholder.component((String)key, (ComponentLike)((RichVariable)variable).component()));
                continue;
            }
            builder.resolver((TagResolver)Placeholder.parsed((String)key, (String)variable.value()));
        }
        return this.miniMessage.deserialize(caption, builder.build());
    }
}

