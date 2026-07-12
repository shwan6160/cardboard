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
import com.bergerkiller.bukkit.common.dep.cloud.help.result.MultipleCommandResult;
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
@Generated(from="MultipleCommandResult", generator="Immutables")
@Immutable
final class MultipleCommandResultImpl<C>
implements MultipleCommandResult<C> {
    private final @NonNull HelpQuery<C> query;
    private final @NonNull String longestPath;
    private final @NonNull List<String> childSuggestions;

    private MultipleCommandResultImpl(@NonNull HelpQuery<C> query, @NonNull String longestPath, Iterable<String> childSuggestions) {
        this.query = Objects.requireNonNull(query, "query");
        this.longestPath = Objects.requireNonNull(longestPath, "longestPath");
        this.childSuggestions = MultipleCommandResultImpl.createUnmodifiableList(false, MultipleCommandResultImpl.createSafeList(childSuggestions, true, false));
    }

    private MultipleCommandResultImpl(MultipleCommandResultImpl<C> original, @NonNull HelpQuery<C> query, @NonNull String longestPath, @NonNull List<String> childSuggestions) {
        this.query = query;
        this.longestPath = longestPath;
        this.childSuggestions = childSuggestions;
    }

    @Override
    public @NonNull HelpQuery<C> query() {
        return this.query;
    }

    @Override
    public @NonNull String longestPath() {
        return this.longestPath;
    }

    @Override
    public @NonNull List<String> childSuggestions() {
        return this.childSuggestions;
    }

    public final MultipleCommandResultImpl<C> withQuery(@NonNull HelpQuery<C> value) {
        if (this.query == value) {
            return this;
        }
        @NonNull HelpQuery<C> newValue = Objects.requireNonNull(value, "query");
        return new MultipleCommandResultImpl<C>(this, newValue, this.longestPath, this.childSuggestions);
    }

    public final MultipleCommandResultImpl<C> withLongestPath(@NonNull String value) {
        @NonNull String newValue = Objects.requireNonNull(value, "longestPath");
        if (this.longestPath.equals(newValue)) {
            return this;
        }
        return new MultipleCommandResultImpl<C>(this, this.query, newValue, this.childSuggestions);
    }

    public final MultipleCommandResultImpl<C> withChildSuggestions(String ... elements) {
        @NonNull List<String> newValue = MultipleCommandResultImpl.createUnmodifiableList(false, MultipleCommandResultImpl.createSafeList(Arrays.asList(elements), true, false));
        return new MultipleCommandResultImpl<C>(this, this.query, this.longestPath, newValue);
    }

    public final MultipleCommandResultImpl<C> withChildSuggestions(Iterable<String> elements) {
        if (this.childSuggestions == elements) {
            return this;
        }
        @NonNull List<String> newValue = MultipleCommandResultImpl.createUnmodifiableList(false, MultipleCommandResultImpl.createSafeList(elements, true, false));
        return new MultipleCommandResultImpl<C>(this, this.query, this.longestPath, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof MultipleCommandResultImpl && this.equalTo(0, (MultipleCommandResultImpl)another);
    }

    private boolean equalTo(int synthetic, MultipleCommandResultImpl<?> another) {
        return this.query.equals(another.query) && this.longestPath.equals(another.longestPath) && this.childSuggestions.equals(another.childSuggestions);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.query.hashCode();
        h += (h << 5) + this.longestPath.hashCode();
        h += (h << 5) + this.childSuggestions.hashCode();
        return h;
    }

    public String toString() {
        return "MultipleCommandResult{query=" + this.query + ", longestPath=" + this.longestPath + ", childSuggestions=" + this.childSuggestions + "}";
    }

    public static <C> MultipleCommandResultImpl<C> of(@NonNull HelpQuery<C> query, @NonNull String longestPath, @NonNull List<String> childSuggestions) {
        return MultipleCommandResultImpl.of(query, longestPath, childSuggestions);
    }

    public static <C> MultipleCommandResultImpl<C> of(@NonNull HelpQuery<C> query, @NonNull String longestPath, Iterable<String> childSuggestions) {
        return new MultipleCommandResultImpl<C>(query, longestPath, childSuggestions);
    }

    public static <C> MultipleCommandResultImpl<C> copyOf(MultipleCommandResult<C> instance) {
        if (instance instanceof MultipleCommandResultImpl) {
            return (MultipleCommandResultImpl)instance;
        }
        return MultipleCommandResultImpl.of(instance.query(), instance.longestPath(), instance.childSuggestions());
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

