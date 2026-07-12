/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Immutable
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.caption;

import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.caption.RichVariableImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ComponentLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation.GlobalTranslator;
import java.util.Locale;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;

@API(status=API.Status.STABLE)
@Value.Immutable
public interface RichVariable
extends CaptionVariable,
ComponentLike {
    public static @NonNull RichVariable of(@NonNull String key, @NonNull Component component) {
        return RichVariableImpl.of(key, component);
    }

    @Override
    public @NonNull String key();

    public @NonNull Component component();

    @Override
    @NotNull
    default public Component asComponent() {
        return this.component();
    }

    @Override
    default public @NonNull String value() {
        return PlainTextComponentSerializer.plainText().serialize(GlobalTranslator.render(this.component(), Locale.getDefault()));
    }
}

