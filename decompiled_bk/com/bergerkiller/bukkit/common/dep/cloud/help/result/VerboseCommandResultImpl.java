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
import com.bergerkiller.bukkit.common.dep.cloud.help.result.VerboseCommandResult;
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
@Generated(from="VerboseCommandResult", generator="Immutables")
@Immutable
final class VerboseCommandResultImpl<C>
implements VerboseCommandResult<C> {
    private final @NonNull HelpQuery<C> query;
    private final @NonNull CommandEntry<C> entry;

    private VerboseCommandResultImpl(@NonNull HelpQuery<C> query, @NonNull CommandEntry<C> entry) {
        this.query = Objects.requireNonNull(query, "query");
        this.entry = Objects.requireNonNull(entry, "entry");
    }

    private VerboseCommandResultImpl(VerboseCommandResultImpl<C> original, @NonNull HelpQuery<C> query, @NonNull CommandEntry<C> entry) {
        this.query = query;
        this.entry = entry;
    }

    @Override
    public @NonNull HelpQuery<C> query() {
        return this.query;
    }

    @Override
    public @NonNull CommandEntry<C> entry() {
        return this.entry;
    }

    public final VerboseCommandResultImpl<C> withQuery(@NonNull HelpQuery<C> value) {
        if (this.query == value) {
            return this;
        }
        @NonNull HelpQuery<C> newValue = Objects.requireNonNull(value, "query");
        return new VerboseCommandResultImpl<C>(this, newValue, this.entry);
    }

    public final VerboseCommandResultImpl<C> withEntry(@NonNull CommandEntry<C> value) {
        if (this.entry == value) {
            return this;
        }
        @NonNull CommandEntry<C> newValue = Objects.requireNonNull(value, "entry");
        return new VerboseCommandResultImpl<C>(this, this.query, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof VerboseCommandResultImpl && this.equalTo(0, (VerboseCommandResultImpl)another);
    }

    private boolean equalTo(int synthetic, VerboseCommandResultImpl<?> another) {
        return this.query.equals(another.query) && this.entry.equals(another.entry);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.query.hashCode();
        h += (h << 5) + this.entry.hashCode();
        return h;
    }

    public String toString() {
        return "VerboseCommandResult{query=" + this.query + ", entry=" + this.entry + "}";
    }

    public static <C> VerboseCommandResultImpl<C> of(@NonNull HelpQuery<C> query, @NonNull CommandEntry<C> entry) {
        return new VerboseCommandResultImpl<C>(query, entry);
    }

    public static <C> VerboseCommandResultImpl<C> copyOf(VerboseCommandResult<C> instance) {
        if (instance instanceof VerboseCommandResultImpl) {
            return (VerboseCommandResultImpl)instance;
        }
        return VerboseCommandResultImpl.of(instance.query(), instance.entry());
    }
}

