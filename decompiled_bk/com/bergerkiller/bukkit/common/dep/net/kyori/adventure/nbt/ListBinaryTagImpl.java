/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Debug$Renderer
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.Range
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.AbstractBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTagType;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTagTypes;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.CompoundBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ListBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ListBinaryTag0;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.ExaminableProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.jetbrains.annotations.Debug;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

@Debug.Renderer(text="\"ListBinaryTag[type=\" + this.type.toString() + \"]\"", childrenArray="this.tags.toArray()", hasChildren="!this.tags.isEmpty()")
final class ListBinaryTagImpl
extends AbstractBinaryTag
implements ListBinaryTag {
    static final ListBinaryTag EMPTY = new ListBinaryTagImpl(BinaryTagTypes.END, false, Collections.emptyList());
    private final List<BinaryTag> tags;
    private final boolean permitsHeterogeneity;
    private final BinaryTagType<? extends BinaryTag> elementType;
    private final int hashCode;

    ListBinaryTagImpl(BinaryTagType<? extends BinaryTag> elementType, boolean permitsHeterogeneity, List<BinaryTag> tags) {
        this.tags = Collections.unmodifiableList(tags);
        this.permitsHeterogeneity = permitsHeterogeneity;
        this.elementType = elementType;
        this.hashCode = tags.hashCode();
    }

    @Override
    @NotNull
    public BinaryTagType<? extends BinaryTag> elementType() {
        return this.elementType;
    }

    @Override
    public int size() {
        return this.tags.size();
    }

    @Override
    public boolean isEmpty() {
        return this.tags.isEmpty();
    }

    @Override
    @NotNull
    public BinaryTag get(@Range(from=0L, to=0x7FFFFFFFL) int index) {
        return this.tags.get(index);
    }

    @Override
    @NotNull
    public ListBinaryTag set(int index, @NotNull BinaryTag newTag, @Nullable Consumer<? super BinaryTag> removed) {
        BinaryTagType<?> targetType = ListBinaryTagImpl.validateTagType(newTag, this.elementType, this.permitsHeterogeneity);
        return this.edit(tags -> {
            BinaryTag oldTag = tags.set(index, newTag);
            if (removed != null) {
                removed.accept(oldTag);
            }
        }, targetType);
    }

    @Override
    @NotNull
    public ListBinaryTag remove(int index, @Nullable Consumer<? super BinaryTag> removed) {
        return this.edit(tags -> {
            BinaryTag oldTag = (BinaryTag)tags.remove(index);
            if (removed != null) {
                removed.accept(oldTag);
            }
        }, null);
    }

    @Override
    @NotNull
    public ListBinaryTag add(BinaryTag tag) {
        BinaryTagType<?> targetType = ListBinaryTagImpl.validateTagType(tag, this.elementType, this.permitsHeterogeneity);
        return this.edit(tags -> tags.add(tag), targetType);
    }

    @Override
    @NotNull
    public ListBinaryTag add(Iterable<? extends BinaryTag> tagsToAdd) {
        if (tagsToAdd instanceof Collection && ((Collection)tagsToAdd).isEmpty()) {
            return this;
        }
        BinaryTagType<?> type = ListBinaryTagImpl.validateTagType(tagsToAdd, this.permitsHeterogeneity);
        return this.edit(tags -> {
            for (BinaryTag tag : tagsToAdd) {
                tags.add(tag);
            }
        }, type);
    }

    static void noAddEnd(BinaryTag tag) {
        if (tag.type() == BinaryTagTypes.END) {
            throw new IllegalArgumentException(String.format("Cannot add a %s to a %s", BinaryTagTypes.END, BinaryTagTypes.LIST));
        }
    }

    static BinaryTagType<?> validateTagType(Iterable<? extends BinaryTag> tags, boolean permitHeterogeneity) {
        BinaryTagType<? extends BinaryTag> type = null;
        for (BinaryTag binaryTag : tags) {
            if (type == null) {
                ListBinaryTagImpl.noAddEnd(binaryTag);
                type = binaryTag.type();
                continue;
            }
            ListBinaryTagImpl.validateTagType(binaryTag, type, permitHeterogeneity);
            if (type == binaryTag.type()) continue;
            type = BinaryTagTypes.LIST_WILDCARD;
        }
        return type;
    }

    static BinaryTagType<?> validateTagType(BinaryTag tag, BinaryTagType<? extends BinaryTag> type, boolean permitHeterogenity) {
        ListBinaryTagImpl.noAddEnd(tag);
        if (type == BinaryTagTypes.END) {
            return tag.type();
        }
        if (tag.type() != type && !permitHeterogenity) {
            throw new IllegalArgumentException(String.format("Trying to add tag of type %s to list of %s", tag.type(), type));
        }
        return tag.type() != type ? BinaryTagTypes.LIST_WILDCARD : type;
    }

    private ListBinaryTag edit(Consumer<List<BinaryTag>> consumer, @Nullable BinaryTagType<? extends BinaryTag> maybeElementType) {
        ArrayList<BinaryTag> tags = new ArrayList<BinaryTag>(this.tags);
        consumer.accept(tags);
        BinaryTagType<? extends BinaryTag> elementType = this.elementType;
        if (maybeElementType != null) {
            elementType = maybeElementType;
        }
        return new ListBinaryTagImpl(elementType, this.permitsHeterogeneity, new ArrayList<BinaryTag>(tags));
    }

    @Override
    @NotNull
    public Stream<BinaryTag> stream() {
        return this.tags.stream();
    }

    @Override
    @NotNull
    public ListBinaryTag unwrapHeterogeneity() {
        if (!this.permitsHeterogeneity) {
            if (this.elementType != BinaryTagTypes.COMPOUND) {
                return new ListBinaryTagImpl(this.elementType, true, this.tags);
            }
            List<BinaryTag> newTags = null;
            ListIterator<BinaryTag> it = this.tags.listIterator();
            while (it.hasNext()) {
                BinaryTag current = it.next();
                BinaryTag unboxed = ListBinaryTag0.unbox((CompoundBinaryTag)current);
                if (unboxed != current && newTags == null) {
                    newTags = new ArrayList<BinaryTag>(this.tags.size());
                    int idx = it.nextIndex() - 1;
                    for (int ptr = 0; ptr < idx; ++ptr) {
                        newTags.add(this.tags.get(ptr));
                    }
                }
                if (newTags == null) continue;
                newTags.add(unboxed);
            }
            return new ListBinaryTagImpl(newTags == null ? BinaryTagTypes.COMPOUND : BinaryTagTypes.LIST_WILDCARD, true, newTags == null ? this.tags : newTags);
        }
        return this;
    }

    @Override
    @NotNull
    public ListBinaryTag wrapHeterogeneity() {
        if (this.elementType != BinaryTagTypes.LIST_WILDCARD) {
            return this;
        }
        ArrayList<BinaryTag> newTags = new ArrayList<BinaryTag>(this.tags.size());
        for (BinaryTag tag : this.tags) {
            newTags.add(ListBinaryTag0.box(tag));
        }
        return new ListBinaryTagImpl(BinaryTagTypes.COMPOUND, false, newTags);
    }

    @Override
    @NotNull
    public Iterator<BinaryTag> iterator() {
        final Iterator<BinaryTag> iterator = this.tags.iterator();
        return new Iterator<BinaryTag>(){

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public BinaryTag next() {
                return (BinaryTag)iterator.next();
            }

            @Override
            public void forEachRemaining(Consumer<? super BinaryTag> action) {
                iterator.forEachRemaining(action);
            }
        };
    }

    @Override
    public void forEach(Consumer<? super BinaryTag> action) {
        this.tags.forEach(action);
    }

    @Override
    public Spliterator<BinaryTag> spliterator() {
        return Spliterators.spliterator(this.tags, 1040);
    }

    public boolean equals(Object that) {
        return this == that || that instanceof ListBinaryTagImpl && this.tags.equals(((ListBinaryTagImpl)that).tags);
    }

    public int hashCode() {
        return this.hashCode;
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("tags", this.tags), ExaminableProperty.of("type", this.elementType));
    }
}

