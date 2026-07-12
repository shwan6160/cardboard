/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TranslatableComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation.AbstractTranslationStore;
import java.text.MessageFormat;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ComponentTranslationStore
extends AbstractTranslationStore<Component> {
    ComponentTranslationStore(@NotNull Key name) {
        super(name);
    }

    @Override
    @Nullable
    public MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        return null;
    }

    @Override
    @Nullable
    public Component translate(@NotNull TranslatableComponent component, @NotNull Locale locale) {
        Component translatedComponent = (Component)this.translationValue(component.key(), locale);
        if (translatedComponent == null) {
            return null;
        }
        return translatedComponent.append(component.children());
    }
}

