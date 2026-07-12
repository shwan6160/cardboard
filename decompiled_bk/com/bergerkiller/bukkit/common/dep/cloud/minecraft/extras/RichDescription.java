/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras;

import com.bergerkiller.bukkit.common.dep.cloud.description.Description;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ComponentLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation.GlobalTranslator;
import java.util.Locale;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class RichDescription
implements Description {
    private static final RichDescription EMPTY = new RichDescription(Component.empty());
    private final Component contents;

    RichDescription(@NonNull Component contents) {
        this.contents = contents;
    }

    public static @NonNull RichDescription empty() {
        return EMPTY;
    }

    public static @NonNull RichDescription of(@NonNull ComponentLike contents) {
        Component componentContents = Objects.requireNonNull(contents, "contents").asComponent();
        if (Component.empty().equals(componentContents)) {
            return EMPTY;
        }
        return new RichDescription(componentContents);
    }

    public static @NonNull RichDescription richDescription(@NonNull ComponentLike contents) {
        return RichDescription.of(contents);
    }

    public static @NonNull RichDescription translatable(@NonNull String key) {
        Objects.requireNonNull(key, "key");
        return new RichDescription(Component.translatable(key));
    }

    public static @NonNull RichDescription translatable(@NonNull String key, ComponentLike ... args) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(args, "args");
        return new RichDescription(Component.translatable(key, args));
    }

    @Override
    @Deprecated
    public @NonNull String textDescription() {
        return PlainTextComponentSerializer.plainText().serialize(GlobalTranslator.render(this.contents, Locale.getDefault()));
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public @NonNull Component contents() {
        return this.contents;
    }

    @Override
    public boolean isEmpty() {
        return Component.empty().equals(this.contents);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        RichDescription that = (RichDescription)object;
        return Objects.equals(this.contents, that.contents);
    }

    public int hashCode() {
        return Objects.hash(this.contents);
    }
}

