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
package com.bergerkiller.bukkit.common.dep.cloud.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestions;
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
@Generated(from="Suggestions", generator="Immutables")
@Immutable
final class SuggestionsImpl<C, S extends Suggestion>
implements Suggestions<C, S> {
    private final @NonNull CommandContext<C> commandContext;
    private final @NonNull List<S> list;
    private final @NonNull CommandInput commandInput;

    private SuggestionsImpl(@NonNull CommandContext<C> commandContext, Iterable<? extends S> list, @NonNull CommandInput commandInput) {
        this.commandContext = Objects.requireNonNull(commandContext, "commandContext");
        this.list = SuggestionsImpl.createUnmodifiableList(false, SuggestionsImpl.createSafeList(list, true, false));
        this.commandInput = Objects.requireNonNull(commandInput, "commandInput");
    }

    private SuggestionsImpl(SuggestionsImpl<C, S> original, @NonNull CommandContext<C> commandContext, @NonNull List<S> list, @NonNull CommandInput commandInput) {
        this.commandContext = commandContext;
        this.list = list;
        this.commandInput = commandInput;
    }

    @Override
    public @NonNull CommandContext<C> commandContext() {
        return this.commandContext;
    }

    @Override
    public @NonNull List<S> list() {
        return this.list;
    }

    @Override
    public @NonNull CommandInput commandInput() {
        return this.commandInput;
    }

    public final SuggestionsImpl<C, S> withCommandContext(@NonNull CommandContext<C> value) {
        if (this.commandContext == value) {
            return this;
        }
        @NonNull CommandContext<C> newValue = Objects.requireNonNull(value, "commandContext");
        return new SuggestionsImpl<C, S>(this, newValue, this.list, this.commandInput);
    }

    @SafeVarargs
    public final SuggestionsImpl<C, S> withList(S ... elements) {
        @NonNull List<S> newValue = SuggestionsImpl.createUnmodifiableList(false, SuggestionsImpl.createSafeList(Arrays.asList(elements), true, false));
        return new SuggestionsImpl<C, S>(this, this.commandContext, newValue, this.commandInput);
    }

    public final SuggestionsImpl<C, S> withList(Iterable<? extends S> elements) {
        if (this.list == elements) {
            return this;
        }
        @NonNull List<? extends S> newValue = SuggestionsImpl.createUnmodifiableList(false, SuggestionsImpl.createSafeList(elements, true, false));
        return new SuggestionsImpl<C, S>(this, this.commandContext, newValue, this.commandInput);
    }

    public final SuggestionsImpl<C, S> withCommandInput(@NonNull CommandInput value) {
        if (this.commandInput == value) {
            return this;
        }
        @NonNull CommandInput newValue = Objects.requireNonNull(value, "commandInput");
        return new SuggestionsImpl<C, S>(this, this.commandContext, this.list, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof SuggestionsImpl && this.equalTo(0, (SuggestionsImpl)another);
    }

    private boolean equalTo(int synthetic, SuggestionsImpl<?, ?> another) {
        return this.commandContext.equals(another.commandContext) && this.list.equals(another.list) && this.commandInput.equals(another.commandInput);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.commandContext.hashCode();
        h += (h << 5) + this.list.hashCode();
        h += (h << 5) + this.commandInput.hashCode();
        return h;
    }

    public String toString() {
        return "Suggestions{commandContext=" + this.commandContext + ", list=" + this.list + ", commandInput=" + this.commandInput + "}";
    }

    public static <C, S extends Suggestion> SuggestionsImpl<C, S> of(@NonNull CommandContext<C> commandContext, @NonNull List<S> list, @NonNull CommandInput commandInput) {
        return SuggestionsImpl.of(commandContext, list, commandInput);
    }

    public static <C, S extends Suggestion> SuggestionsImpl<C, S> of(@NonNull CommandContext<C> commandContext, Iterable<? extends S> list, @NonNull CommandInput commandInput) {
        return new SuggestionsImpl<C, S>(commandContext, list, commandInput);
    }

    public static <C, S extends Suggestion> SuggestionsImpl<C, S> copyOf(Suggestions<C, S> instance) {
        if (instance instanceof SuggestionsImpl) {
            return (SuggestionsImpl)instance;
        }
        return SuggestionsImpl.of(instance.commandContext(), instance.list(), instance.commandInput());
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

