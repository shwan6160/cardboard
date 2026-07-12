/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ComponentLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TranslationArgumentImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TranslationArgumentLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.Examinable;
import java.util.Objects;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface TranslationArgument
extends TranslationArgumentLike,
Examinable {
    @NotNull
    public static TranslationArgument bool(boolean value) {
        return new TranslationArgumentImpl(value);
    }

    @NotNull
    public static TranslationArgument numeric(@NotNull Number value) {
        return new TranslationArgumentImpl(Objects.requireNonNull(value, "value"));
    }

    @NotNull
    public static TranslationArgument component(@NotNull ComponentLike value) {
        if (value instanceof TranslationArgumentLike) {
            return ((TranslationArgumentLike)value).asTranslationArgument();
        }
        return new TranslationArgumentImpl(Objects.requireNonNull(Objects.requireNonNull(value, "value").asComponent(), "value.asComponent()"));
    }

    @NotNull
    public Object value();

    @Override
    @NotNull
    default public TranslationArgument asTranslationArgument() {
        return this;
    }
}

