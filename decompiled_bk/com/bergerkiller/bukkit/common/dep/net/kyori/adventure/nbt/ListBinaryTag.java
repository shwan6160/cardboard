/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.Range
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTagType;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTagTypes;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ByteArrayBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.CompoundBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.IntArrayBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ListBinaryTagImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ListTagBuilder;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ListTagSetter;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.LongArrayBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.NumberBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.StringBinaryTag;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Stream;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public interface ListBinaryTag
extends ListTagSetter<ListBinaryTag, BinaryTag>,
BinaryTag,
Iterable<BinaryTag> {
    @NotNull
    public static ListBinaryTag empty() {
        return ListBinaryTagImpl.EMPTY;
    }

    @NotNull
    public static ListBinaryTag from(@NotNull Iterable<? extends BinaryTag> tags) {
        return ((Builder)ListBinaryTag.builder().add(tags)).build();
    }

    @NotNull
    public static Builder<BinaryTag> builder() {
        return new ListTagBuilder<BinaryTag>(false);
    }

    @NotNull
    public static Builder<BinaryTag> builder(@Range(from=0L, to=0x7FFFFFFFL) int initialCapacity) {
        return new ListTagBuilder<BinaryTag>(false, initialCapacity);
    }

    @NotNull
    public static Builder<BinaryTag> heterogeneousListBinaryTag() {
        return new ListTagBuilder<BinaryTag>(true);
    }

    @NotNull
    public static Builder<BinaryTag> heterogeneousListBinaryTag(@Range(from=0L, to=0x7FFFFFFFL) int initialCapacity) {
        return new ListTagBuilder<BinaryTag>(true, initialCapacity);
    }

    @NotNull
    public static <T extends BinaryTag> Builder<T> builder(@NotNull BinaryTagType<T> type) {
        if (type == BinaryTagTypes.END) {
            throw new IllegalArgumentException("Cannot create a list of " + BinaryTagTypes.END);
        }
        return new ListTagBuilder(false, type);
    }

    @NotNull
    public static <T extends BinaryTag> Builder<T> builder(@NotNull BinaryTagType<T> type, @Range(from=0L, to=0x7FFFFFFFL) int initialCapacity) {
        if (type == BinaryTagTypes.END) {
            throw new IllegalArgumentException("Cannot create a list of " + BinaryTagTypes.END);
        }
        return new ListTagBuilder(false, type, initialCapacity);
    }

    @NotNull
    public static ListBinaryTag listBinaryTag(@NotNull BinaryTagType<? extends BinaryTag> type, @NotNull List<BinaryTag> tags) {
        if (tags.isEmpty()) {
            return ListBinaryTag.empty();
        }
        if (type == BinaryTagTypes.END) {
            throw new IllegalArgumentException("Cannot create a list of " + BinaryTagTypes.END);
        }
        ListBinaryTagImpl.validateTagType(tags, type == BinaryTagTypes.LIST_WILDCARD);
        return new ListBinaryTagImpl(type, type == BinaryTagTypes.LIST_WILDCARD, new ArrayList<BinaryTag>(tags));
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    @NotNull
    public static ListBinaryTag of(@NotNull BinaryTagType<? extends BinaryTag> type, @NotNull List<BinaryTag> tags) {
        return ListBinaryTag.listBinaryTag(type, tags);
    }

    @NotNull
    public static Collector<BinaryTag, ?, ListBinaryTag> toListTag() {
        return ListBinaryTag.toListTag(null);
    }

    @NotNull
    public static Collector<BinaryTag, ?, ListBinaryTag> toListTag(@Nullable ListBinaryTag initial) {
        return Collector.of(initial == null ? ListBinaryTag::builder : () -> (Builder)ListBinaryTag.builder().add(initial), ListTagSetter::add, (l, r) -> (Builder)l.add(r.build()), Builder::build, new Collector.Characteristics[0]);
    }

    @NotNull
    default public BinaryTagType<ListBinaryTag> type() {
        return BinaryTagTypes.LIST;
    }

    @Deprecated
    @NotNull
    default public BinaryTagType<? extends BinaryTag> listType() {
        return this.elementType();
    }

    @NotNull
    public BinaryTagType<? extends BinaryTag> elementType();

    public int size();

    public boolean isEmpty();

    @NotNull
    public BinaryTag get(@Range(from=0L, to=0x7FFFFFFFL) int var1);

    @NotNull
    public ListBinaryTag set(int var1, @NotNull BinaryTag var2, @Nullable Consumer<? super BinaryTag> var3);

    @NotNull
    public ListBinaryTag remove(int var1, @Nullable Consumer<? super BinaryTag> var2);

    default public byte getByte(@Range(from=0L, to=0x7FFFFFFFL) int index) {
        return this.getByte(index, (byte)0);
    }

    default public byte getByte(@Range(from=0L, to=0x7FFFFFFFL) int index, byte defaultValue) {
        BinaryTag tag = this.get(index);
        if (tag.type().numeric()) {
            return ((NumberBinaryTag)tag).byteValue();
        }
        return defaultValue;
    }

    default public short getShort(@Range(from=0L, to=0x7FFFFFFFL) int index) {
        return this.getShort(index, (short)0);
    }

    default public short getShort(@Range(from=0L, to=0x7FFFFFFFL) int index, short defaultValue) {
        BinaryTag tag = this.get(index);
        if (tag.type().numeric()) {
            return ((NumberBinaryTag)tag).shortValue();
        }
        return defaultValue;
    }

    default public int getInt(@Range(from=0L, to=0x7FFFFFFFL) int index) {
        return this.getInt(index, 0);
    }

    default public int getInt(@Range(from=0L, to=0x7FFFFFFFL) int index, int defaultValue) {
        BinaryTag tag = this.get(index);
        if (tag.type().numeric()) {
            return ((NumberBinaryTag)tag).intValue();
        }
        return defaultValue;
    }

    default public long getLong(@Range(from=0L, to=0x7FFFFFFFL) int index) {
        return this.getLong(index, 0L);
    }

    default public long getLong(@Range(from=0L, to=0x7FFFFFFFL) int index, long defaultValue) {
        BinaryTag tag = this.get(index);
        if (tag.type().numeric()) {
            return ((NumberBinaryTag)tag).longValue();
        }
        return defaultValue;
    }

    default public float getFloat(@Range(from=0L, to=0x7FFFFFFFL) int index) {
        return this.getFloat(index, 0.0f);
    }

    default public float getFloat(@Range(from=0L, to=0x7FFFFFFFL) int index, float defaultValue) {
        BinaryTag tag = this.get(index);
        if (tag.type().numeric()) {
            return ((NumberBinaryTag)tag).floatValue();
        }
        return defaultValue;
    }

    default public double getDouble(@Range(from=0L, to=0x7FFFFFFFL) int index) {
        return this.getDouble(index, 0.0);
    }

    default public double getDouble(@Range(from=0L, to=0x7FFFFFFFL) int index, double defaultValue) {
        BinaryTag tag = this.get(index);
        if (tag.type().numeric()) {
            return ((NumberBinaryTag)tag).doubleValue();
        }
        return defaultValue;
    }

    default public byte @NotNull [] getByteArray(@Range(from=0L, to=0x7FFFFFFFL) int index) {
        BinaryTag tag = this.get(index);
        if (tag.type() == BinaryTagTypes.BYTE_ARRAY) {
            return ((ByteArrayBinaryTag)tag).value();
        }
        return new byte[0];
    }

    @Contract(value="_, !null -> !null")
    default public byte @Nullable [] getByteArray(@Range(from=0L, to=0x7FFFFFFFL) int index, byte @Nullable [] defaultValue) {
        BinaryTag tag = this.get(index);
        if (tag.type() == BinaryTagTypes.BYTE_ARRAY) {
            return ((ByteArrayBinaryTag)tag).value();
        }
        return defaultValue;
    }

    @NotNull
    default public String getString(@Range(from=0L, to=0x7FFFFFFFL) int index) {
        return this.getString(index, "");
    }

    @Contract(value="_, !null -> !null")
    @Nullable
    default public String getString(@Range(from=0L, to=0x7FFFFFFFL) int index, @Nullable String defaultValue) {
        BinaryTag tag = this.get(index);
        if (tag.type() == BinaryTagTypes.STRING) {
            return ((StringBinaryTag)tag).value();
        }
        return defaultValue;
    }

    @NotNull
    default public ListBinaryTag getList(@Range(from=0L, to=0x7FFFFFFFL) int index) {
        return this.getList(index, null, ListBinaryTag.empty());
    }

    @NotNull
    default public ListBinaryTag getList(@Range(from=0L, to=0x7FFFFFFFL) int index, @Nullable BinaryTagType<?> elementType) {
        return this.getList(index, elementType, ListBinaryTag.empty());
    }

    @Contract(value="_, !null -> !null")
    @Nullable
    default public ListBinaryTag getList(@Range(from=0L, to=0x7FFFFFFFL) int index, @Nullable ListBinaryTag defaultValue) {
        return this.getList(index, null, defaultValue);
    }

    @Contract(value="_, _, !null -> !null")
    @Nullable
    default public ListBinaryTag getList(@Range(from=0L, to=0x7FFFFFFFL) int index, @Nullable BinaryTagType<?> elementType, @Nullable ListBinaryTag defaultValue) {
        BinaryTag tag = this.get(index);
        if (tag.type() == BinaryTagTypes.LIST) {
            ListBinaryTag list = (ListBinaryTag)tag;
            if (elementType == null || list.elementType() == elementType) {
                return list;
            }
        }
        return defaultValue;
    }

    @NotNull
    default public CompoundBinaryTag getCompound(@Range(from=0L, to=0x7FFFFFFFL) int index) {
        return this.getCompound(index, CompoundBinaryTag.empty());
    }

    @Contract(value="_, !null -> !null")
    @Nullable
    default public CompoundBinaryTag getCompound(@Range(from=0L, to=0x7FFFFFFFL) int index, @Nullable CompoundBinaryTag defaultValue) {
        BinaryTag tag = this.get(index);
        if (tag.type() == BinaryTagTypes.COMPOUND) {
            return (CompoundBinaryTag)tag;
        }
        return defaultValue;
    }

    default public int @NotNull [] getIntArray(@Range(from=0L, to=0x7FFFFFFFL) int index) {
        BinaryTag tag = this.get(index);
        if (tag.type() == BinaryTagTypes.INT_ARRAY) {
            return ((IntArrayBinaryTag)tag).value();
        }
        return new int[0];
    }

    @Contract(value="_, !null -> !null")
    default public int @Nullable [] getIntArray(@Range(from=0L, to=0x7FFFFFFFL) int index, int @Nullable [] defaultValue) {
        BinaryTag tag = this.get(index);
        if (tag.type() == BinaryTagTypes.INT_ARRAY) {
            return ((IntArrayBinaryTag)tag).value();
        }
        return defaultValue;
    }

    default public long @NotNull [] getLongArray(@Range(from=0L, to=0x7FFFFFFFL) int index) {
        BinaryTag tag = this.get(index);
        if (tag.type() == BinaryTagTypes.LONG_ARRAY) {
            return ((LongArrayBinaryTag)tag).value();
        }
        return new long[0];
    }

    @Contract(value="_, !null -> !null")
    default public long @Nullable [] getLongArray(@Range(from=0L, to=0x7FFFFFFFL) int index, long @Nullable [] defaultValue) {
        BinaryTag tag = this.get(index);
        if (tag.type() == BinaryTagTypes.LONG_ARRAY) {
            return ((LongArrayBinaryTag)tag).value();
        }
        return defaultValue;
    }

    @NotNull
    public Stream<BinaryTag> stream();

    @NotNull
    public ListBinaryTag unwrapHeterogeneity();

    @NotNull
    public ListBinaryTag wrapHeterogeneity();

    public static interface Builder<T extends BinaryTag>
    extends ListTagSetter<Builder<T>, T> {
        @NotNull
        public ListBinaryTag build();
    }
}

