/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation.AbstractTranslationStore;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation.TranslationRegistry;
import java.text.MessageFormat;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class MessageFormatTranslationStore
extends AbstractTranslationStore.StringBased<MessageFormat>
implements TranslationRegistry {
    MessageFormatTranslationStore(Key name) {
        super(name);
    }

    @Override
    @NotNull
    protected MessageFormat parse(@NotNull String string, @NotNull Locale locale) {
        return new MessageFormat(string, locale);
    }

    @Override
    @Nullable
    public MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        return (MessageFormat)this.translationValue(key, locale);
    }
}

