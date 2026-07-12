/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.Range
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTagType;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTagTypes;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ByteBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.CompoundBinaryTagImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.CompoundTagBuilder;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.CompoundTagSetter;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ListBinaryTag;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public interface CompoundBinaryTag
extends BinaryTag,
CompoundTagSetter<CompoundBinaryTag>,
Iterable<Map.Entry<String, ? extends BinaryTag>> {
    @NotNull
    public static CompoundBinaryTag empty() {
        return CompoundBinaryTagImpl.EMPTY;
    }

    @NotNull
    public static CompoundBinaryTag from(@NotNull Map<String, ? extends BinaryTag> tags) {
        if (tags.isEmpty()) {
            return CompoundBinaryTag.empty();
        }
        return new CompoundBinaryTagImpl(new HashMap<String, BinaryTag>(tags));
    }

    @NotNull
    public static Collector<Map.Entry<String, ? extends BinaryTag>, ?, CompoundBinaryTag> toCompoundTag() {
        return CompoundBinaryTag.toCompoundTag(Map.Entry::getKey, Map.Entry::getValue);
    }

    @NotNull
    public static <T> Collector<T, ?, CompoundBinaryTag> toCompoundTag(@NotNull Function<T, String> keyLens, @NotNull Function<T, ? extends BinaryTag> valueLens) {
        Objects.requireNonNull(keyLens, "keyLens");
        Objects.requireNonNull(valueLens, "valueLens");
        return Collector.of(CompoundBinaryTag::builder, (b, ent) -> b.put((String)keyLens.apply(ent), (BinaryTag)valueLens.apply(ent)), (l, r) -> (Builder)l.put(r.build()), Builder::build, Collector.Characteristics.UNORDERED);
    }

    @NotNull
    public static Collector<Map.Entry<String, ? extends BinaryTag>, ?, CompoundBinaryTag> toCompoundTag(@NotNull CompoundBinaryTag initial) {
        return CompoundBinaryTag.toCompoundTag(initial, Map.Entry::getKey, Map.Entry::getValue);
    }

    @NotNull
    public static <T> Collector<T, ?, CompoundBinaryTag> toCompoundTag(@NotNull CompoundBinaryTag initial, @NotNull Function<T, String> keyLens, @NotNull Function<T, ? extends BinaryTag> valueLens) {
        Objects.requireNonNull(initial, "initial");
        Objects.requireNonNull(keyLens, "keyLens");
        Objects.requireNonNull(valueLens, "valueLens");
        return Collector.of(() -> (Builder)CompoundBinaryTag.builder().put(initial), (b, ent) -> b.put((String)keyLens.apply(ent), (BinaryTag)valueLens.apply(ent)), (l, r) -> (Builder)l.put(r.build()), Builder::build, Collector.Characteristics.UNORDERED);
    }

    @NotNull
    public static Builder builder() {
        return new CompoundTagBuilder();
    }

    @NotNull
    public static Builder builder(@Range(from=0L, to=0x7FFFFFFFL) int initialCapacity) {
        return new CompoundTagBuilder(initialCapacity);
    }

    @NotNull
    default public BinaryTagType<CompoundBinaryTag> type() {
        return BinaryTagTypes.COMPOUND;
    }

    public boolean contains(@NotNull String var1);

    public boolean contains(@NotNull String var1, @NotNull BinaryTagType<?> var2);

    @NotNull
    public Set<String> keySet();

    @Nullable
    public BinaryTag get(String var1);

    public int size();

    public boolean isEmpty();

    default public boolean getBoolean(@NotNull String key) {
        return this.getBoolean(key, false);
    }

    default public boolean getBoolean(@NotNull String key, boolean defaultValue) {
        BinaryTag tag = this.get(key);
        if (tag instanceof ByteBinaryTag) {
            return ((ByteBinaryTag)tag).value() != 0;
        }
        return defaultValue;
    }

    default public byte getByte(@NotNull String key) {
        return this.getByte(key, (byte)0);
    }

    public byte getByte(@NotNull String var1, byte var2);

    default public short getShort(@NotNull String key) {
        return this.getShort(key, (short)0);
    }

    public short getShort(@NotNull String var1, short var2);

    default public int getInt(@NotNull String key) {
        return this.getInt(key, 0);
    }

    public int getInt(@NotNull String var1, int var2);

    default public long getLong(@NotNull String key) {
        return this.getLong(key, 0L);
    }

    public long getLong(@NotNull String var1, long var2);

    default public float getFloat(@NotNull String key) {
        return this.getFloat(key, 0.0f);
    }

    public float getFloat(@NotNull String var1, float var2);

    default public double getDouble(@NotNull String key) {
        return this.getDouble(key, 0.0);
    }

    public double getDouble(@NotNull String var1, double var2);

    public byte @NotNull [] getByteArray(@NotNull String var1);

    @Contract(value="_, !null -> !null")
    public byte @Nullable [] getByteArray(@NotNull String var1, byte @Nullable [] var2);

    @NotNull
    default public String getString(@NotNull String key) {
        return this.getString(key, "");
    }

    @Contract(value="_, !null -> !null")
    @Nullable
    public String getString(@NotNull String var1, @Nullable String var2);

    @NotNull
    default public ListBinaryTag getList(@NotNull String key) {
        return this.getList(key, ListBinaryTag.empty());
    }

    @Contract(value="_, !null -> !null")
    @Nullable
    public ListBinaryTag getList(@NotNull String var1, @Nullable ListBinaryTag var2);

    @NotNull
    default public ListBinaryTag getList(@NotNull String key, @NotNull BinaryTagType<? extends BinaryTag> expectedType) {
        return this.getList(key, expectedType, ListBinaryTag.empty());
    }

    @Contract(value="_, _, !null -> !null")
    @Nullable
    public ListBinaryTag getList(@NotNull String var1, @NotNull BinaryTagType<? extends BinaryTag> var2, @Nullable ListBinaryTag var3);

    @NotNull
    default public CompoundBinaryTag getCompound(@NotNull String key) {
        return this.getCompound(key, CompoundBinaryTag.empty());
    }

    @Contract(value="_, !null -> !null")
    @Nullable
    public CompoundBinaryTag getCompound(@NotNull String var1, @Nullable CompoundBinaryTag var2);

    public int @NotNull [] getIntArray(@NotNull String var1);

    @Contract(value="_, !null -> !null")
    public int @Nullable [] getIntArray(@NotNull String var1, int @Nullable [] var2);

    public long @NotNull [] getLongArray(@NotNull String var1);

    @Contract(value="_, !null -> !null")
    public long @Nullable [] getLongArray(@NotNull String var1, long @Nullable [] var2);

    public Stream<Map.Entry<String, ? extends BinaryTag>> stream();

    public static interface Builder
    extends CompoundTagSetter<Builder> {
        @NotNull
        public CompoundBinaryTag build();
    }
}

