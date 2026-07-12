/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckReturnValue
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.concurrent.Immutable
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Generated
 */
package com.bergerkiller.bukkit.common.dep.cloud.help.result;

import com.bergerkiller.bukkit.common.dep.cloud.help.HelpQuery;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.CommandEntry;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.IndexCommandResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Generated;

@ParametersAreNonnullByDefault
@CheckReturnValue
@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="IndexCommandResult", generator="Immutables")
@Immutable
final class IndexCommandResultImpl<C>
implements IndexCommandResult<C> {
    private final @NonNull HelpQuery<C> query;
    private final @NonNull List<CommandEntry<C>> entries;

    private IndexCommandResultImpl(@NonNull HelpQuery<C> query, Iterable<? extends CommandEntry<C>> entries) {
        this.query = Objects.requireNonNull(query, "query");
        this.entries = IndexCommandResultImpl.createUnmodifiableList(false, IndexCommandResultImpl.createSafeList(entries, true, false));
    }

    private IndexCommandResultImpl(IndexCommandResultImpl<C> original, @NonNull HelpQuery<C> query, @NonNull List<CommandEntry<C>> entries) {
        this.query = query;
        this.entries = entries;
    }

    @Override
    public @NonNull HelpQuery<C> query() {
        return this.query;
    }

    @Override
    public @NonNull List<CommandEntry<C>> entries() {
        return this.entries;
    }

    public final IndexCommandResultImpl<C> withQuery(@NonNull HelpQuery<C> value) {
        if (this.query == value) {
            return this;
        }
        @NonNull HelpQuery<C> newValue = Objects.requireNonNull(value, "query");
        return new IndexCommandResultImpl<C>(this, newValue, this.entries);
    }

    @SafeVarargs
    public final IndexCommandResultImpl<C> withEntries(CommandEntry<C> ... elements) {
        @NonNull List<CommandEntry<C>> newValue = IndexCommandResultImpl.createUnmodifiableList(false, IndexCommandResultImpl.createSafeList(Arrays.asList(elements), true, false));
        return new IndexCommandResultImpl<C>(this, this.query, newValue);
    }

    public final IndexCommandResultImpl<C> withEntries(Iterable<? extends CommandEntry<C>> elements) {
        if (this.entries == elements) {
            return this;
        }
        @NonNull List<CommandEntry<C>> newValue = IndexCommandResultImpl.createUnmodifiableList(false, IndexCommandResultImpl.createSafeList(elements, true, false));
        return new IndexCommandResultImpl<C>(this, this.query, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof IndexCommandResultImpl && this.equalTo(0, (IndexCommandResultImpl)another);
    }

    private boolean equalTo(int synthetic, IndexCommandResultImpl<?> another) {
        return this.query.equals(another.query) && this.entries.equals(another.entries);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.query.hashCode();
        h += (h << 5) + this.entries.hashCode();
        return h;
    }

    public String toString() {
        return "IndexCommandResult{query=" + this.query + ", entries=" + this.entries + "}";
    }

    public static <C> IndexCommandResultImpl<C> of(@NonNull HelpQuery<C> query, @NonNull List<CommandEntry<C>> entries) {
        return IndexCommandResultImpl.of(query, entries);
    }

    public static <C> IndexCommandResultImpl<C> of(@NonNull HelpQuery<C> query, Iterable<? extends CommandEntry<C>> entries) {
        return new IndexCommandResultImpl<C>(query, entries);
    }

    public static <C> IndexCommandResultImpl<C> copyOf(IndexCommandResult<C> instance) {
        if (instance instanceof IndexCommandResultImpl) {
            return (IndexCommandResultImpl)instance;
        }
        return IndexCommandResultImpl.of(instance.query(), instance.entries());
    }

    private static <T> List<T> createSafeList(Iterable<? extends T> iterable, boolean checkNulls, boolean skipNulls) {
        ArrayList<T> list;
        if (iterable instanceof Collection) {
            int size = ((Collection)iterable).size();
            if (size == 0) {
                return Collections.emptyList();
            }
            list = new ArrayList(size);
        } else {
            list = new ArrayList<T>();
        }
        for (T element : iterable) {
            if (skipNulls && element == null) continue;
            if (checkNulls) {
                Objects.requireNonNull(element, "element");
            }
            list.add(element);
        }
        return list;
    }

    private static <T> List<T> createUnmodifiableList(boolean clone, List<T> list) {
        switch (list.size()) {
            case 0: {
                return Collections.emptyList();
            }
            case 1: {
                return Collections.singletonList(list.get(0));
            }
        }
        if (clone) {
            return Collections.unmodifiableList(new ArrayList<T>(list));
        }
        if (list instanceof ArrayList) {
            ((ArrayList)list).trimToSize();
        }
        return Collections.unmodifiableList(list);
    }
}

