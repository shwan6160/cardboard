/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.caption;

import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionProvider;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class TranslatableCaption
implements Caption {
    private final String key;

    public static Caption translatableCaption(String key) {
        return new TranslatableCaption(key);
    }

    public static <C> CaptionProvider<C> translatableCaptionProvider() {
        return (caption, recipient) -> {
            if (caption instanceof TranslatableCaption) {
                return caption.key();
            }
            return null;
        };
    }

    private TranslatableCaption(String key) {
        this.key = key;
    }

    @Override
    public @NonNull String key() {
        return this.key;
    }
}

