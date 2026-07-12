/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.object;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.internal.Internals;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.object.SpriteObjectContents;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class SpriteObjectContentsImpl
implements SpriteObjectContents {
    private final Key atlas;
    private final Key sprite;

    SpriteObjectContentsImpl(@NotNull Key atlas, @NotNull Key sprite) {
        this.atlas = atlas;
        this.sprite = sprite;
    }

    @Override
    @NotNull
    public Key atlas() {
        return this.atlas;
    }

    @Override
    @NotNull
    public Key sprite() {
        return this.sprite;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SpriteObjectContents)) {
            return false;
        }
        SpriteObjectContentsImpl that = (SpriteObjectContentsImpl)other;
        return Objects.equals(this.atlas, that.atlas()) && Objects.equals(this.sprite, that.sprite());
    }

    public int hashCode() {
        int result = this.atlas.hashCode();
        result = 31 * result + this.sprite.hashCode();
        return result;
    }

    public String toString() {
        return Internals.toString(this);
    }
}

