/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTagType;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTagTypes;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ListBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ListBinaryTagImpl;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ListTagBuilder<T extends BinaryTag>
implements ListBinaryTag.Builder<T> {
    private static final int DEFAULT_CAPACITY = -1;
    @Nullable
    private List<BinaryTag> tags;
    private final boolean permitsHeterogeneity;
    private BinaryTagType<? extends BinaryTag> elementType;
    private final int initialCapacity;

    ListTagBuilder(boolean permitsHeterogeneity) {
        this(permitsHeterogeneity, BinaryTagTypes.END);
    }

    ListTagBuilder(boolean permitsHeterogeneity, int initialCapacity) {
        this(permitsHeterogeneity, BinaryTagTypes.END, initialCapacity);
    }

    ListTagBuilder(boolean permitsHeterogeneity, BinaryTagType<? extends BinaryTag> type) {
        this(permitsHeterogeneity, type, -1);
    }

    ListTagBuilder(boolean permitsHeterogeneity, BinaryTagType<? extends BinaryTag> type, int initialCapacity) {
        this.permitsHeterogeneity = permitsHeterogeneity;
        this.elementType = type;
        this.initialCapacity = initialCapacity;
    }

    @Override
    public @NotNull ListBinaryTag.Builder<T> add(BinaryTag tag) {
        this.elementType = ListBinaryTagImpl.validateTagType(tag, this.elementType, this.permitsHeterogeneity);
        if (this.tags == null) {
            if (this.initialCapacity != -1) {
                if (this.initialCapacity < 0) {
                    throw new IllegalArgumentException("initialCapacity cannot be less than 0, was " + this.initialCapacity);
                }
                this.tags = new ArrayList<BinaryTag>(this.initialCapacity);
            } else {
                this.tags = new ArrayList<BinaryTag>();
            }
        }
        this.tags.add(tag);
        return this;
    }

    @Override
    public @NotNull ListBinaryTag.Builder<T> add(Iterable<? extends T> tagsToAdd) {
        for (BinaryTag tag : tagsToAdd) {
            this.add(tag);
        }
        return this;
    }

    @Override
    @NotNull
    public ListBinaryTag build() {
        if (this.tags == null) {
            return ListBinaryTag.empty();
        }
        return new ListBinaryTagImpl(this.elementType, this.permitsHeterogeneity, new ArrayList<BinaryTag>(this.tags));
    }
}

