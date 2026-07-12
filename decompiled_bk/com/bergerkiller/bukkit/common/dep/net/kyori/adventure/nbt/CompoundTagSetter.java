/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ByteArrayBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ByteBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.CompoundBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.DoubleBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.FloatBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.IntArrayBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.IntBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.LongArrayBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.LongBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ShortBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.StringBinaryTag;
import java.util.Map;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CompoundTagSetter<R> {
    @NotNull
    public R put(@NotNull String var1, @NotNull BinaryTag var2);

    @NotNull
    public R put(@NotNull CompoundBinaryTag var1);

    @NotNull
    public R put(@NotNull Map<String, ? extends BinaryTag> var1);

    @NotNull
    default public R remove(@NotNull String key) {
        return this.remove(key, null);
    }

    @NotNull
    public R remove(@NotNull String var1, @Nullable Consumer<? super BinaryTag> var2);

    @NotNull
    default public R putBoolean(@NotNull String key, boolean value) {
        return this.put(key, value ? ByteBinaryTag.ONE : ByteBinaryTag.ZERO);
    }

    @NotNull
    default public R putByte(@NotNull String key, byte value) {
        return this.put(key, ByteBinaryTag.byteBinaryTag(value));
    }

    @NotNull
    default public R putShort(@NotNull String key, short value) {
        return this.put(key, ShortBinaryTag.shortBinaryTag(value));
    }

    @NotNull
    default public R putInt(@NotNull String key, int value) {
        return this.put(key, IntBinaryTag.intBinaryTag(value));
    }

    @NotNull
    default public R putLong(@NotNull String key, long value) {
        return this.put(key, LongBinaryTag.longBinaryTag(value));
    }

    @NotNull
    default public R putFloat(@NotNull String key, float value) {
        return this.put(key, FloatBinaryTag.floatBinaryTag(value));
    }

    @NotNull
    default public R putDouble(@NotNull String key, double value) {
        return this.put(key, DoubleBinaryTag.doubleBinaryTag(value));
    }

    @NotNull
    default public R putByteArray(@NotNull String key, byte @NotNull [] value) {
        return this.put(key, ByteArrayBinaryTag.byteArrayBinaryTag(value));
    }

    @NotNull
    default public R putString(@NotNull String key, @NotNull String value) {
        return this.put(key, StringBinaryTag.stringBinaryTag(value));
    }

    @NotNull
    default public R putIntArray(@NotNull String key, int @NotNull [] value) {
        return this.put(key, IntArrayBinaryTag.intArrayBinaryTag(value));
    }

    @NotNull
    default public R putLongArray(@NotNull String key, long @NotNull [] value) {
        return this.put(key, LongArrayBinaryTag.longArrayBinaryTag(value));
    }
}

