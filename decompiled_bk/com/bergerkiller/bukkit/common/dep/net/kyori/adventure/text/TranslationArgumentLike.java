/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ComponentLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TranslationArgument;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface TranslationArgumentLike
extends ComponentLike {
    @NotNull
    public TranslationArgument asTranslationArgument();

    @Override
    @NotNull
    default public Component asComponent() {
        return this.asTranslationArgument().asComponent();
    }
}

