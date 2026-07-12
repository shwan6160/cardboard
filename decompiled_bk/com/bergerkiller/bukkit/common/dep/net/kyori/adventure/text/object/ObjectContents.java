/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.object;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.object.PlayerHeadObjectContents;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.object.PlayerHeadObjectContentsImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.object.SpriteObjectContents;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.object.SpriteObjectContentsImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.Examinable;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface ObjectContents
extends Examinable {
    @Contract(value="_, _ -> new", pure=true)
    @NotNull
    public static SpriteObjectContents sprite(@NotNull Key atlas, @NotNull Key sprite) {
        return new SpriteObjectContentsImpl(Objects.requireNonNull(atlas, "atlas"), Objects.requireNonNull(sprite, "sprite"));
    }

    @Contract(value="_ -> new", pure=true)
    @NotNull
    public static SpriteObjectContents sprite(@NotNull Key sprite) {
        return new SpriteObjectContentsImpl(SpriteObjectContents.DEFAULT_ATLAS, Objects.requireNonNull(sprite, "sprite"));
    }

    @Contract(value="-> new", pure=true)
    public static @NotNull PlayerHeadObjectContents.Builder playerHead() {
        return new PlayerHeadObjectContentsImpl.BuilderImpl();
    }

    @Contract(value="_ -> new", pure=true)
    @NotNull
    public static PlayerHeadObjectContents playerHead(@NotNull String name) {
        return new PlayerHeadObjectContentsImpl(name, null, Collections.emptyList(), true, null);
    }

    @Contract(value="_ -> new", pure=true)
    @NotNull
    public static PlayerHeadObjectContents playerHead(@NotNull UUID id) {
        return new PlayerHeadObjectContentsImpl(null, id, Collections.emptyList(), true, null);
    }

    @Contract(value="_ -> new", pure=true)
    @NotNull
    public static PlayerHeadObjectContents playerHead(@NotNull PlayerHeadObjectContents.SkinSource skinSource) {
        return ObjectContents.playerHead().skin(skinSource).build();
    }
}

