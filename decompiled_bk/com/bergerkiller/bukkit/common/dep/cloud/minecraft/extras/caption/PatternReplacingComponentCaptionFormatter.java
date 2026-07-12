/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TextReplacementConfig;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL)
final class PatternReplacingComponentCaptionFormatter<C>
implements ComponentCaptionFormatter<C> {
    private final Pattern pattern;

    PatternReplacingComponentCaptionFormatter(@NonNull Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public @NonNull Component formatCaption(@NonNull Caption captionKey, @NonNull C recipient, @NonNull String caption, @NonNull List<@NonNull CaptionVariable> variables) {
        HashMap<String, Component> replacements = new HashMap<String, Component>();
        for (CaptionVariable variable : variables) {
            if (variable instanceof RichVariable) {
                replacements.put(variable.key(), ((RichVariable)variable).component());
                continue;
            }
            replacements.put(variable.key(), Component.text(variable.value()));
        }
        TextReplacementConfig replacementConfig = (TextReplacementConfig)TextReplacementConfig.builder().match(this.pattern).replacement((matcher, builder) -> replacements.getOrDefault(matcher.group(1), Component.text(matcher.group()))).build();
        return Component.text(caption).replaceText(replacementConfig);
    }
}

