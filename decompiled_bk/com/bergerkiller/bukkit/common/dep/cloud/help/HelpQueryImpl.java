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
package com.bergerkiller.bukkit.common.dep.cloud.help;

import com.bergerkiller.bukkit.common.dep.cloud.help.HelpQuery;
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
@Generated(from="HelpQuery", generator="Immutables")
@Immutable
final class HelpQueryImpl<C>
implements HelpQuery<C> {
    private final C sender;
    private final @NonNull String query;

    private HelpQueryImpl(C sender, @NonNull String query) {
        this.sender = Objects.requireNonNull(sender, "sender");
        this.query = Objects.requireNonNull(query, "query");
    }

    private HelpQueryImpl(HelpQueryImpl<C> original, C sender, @NonNull String query) {
        this.sender = sender;
        this.query = query;
    }

    @Override
    public C sender() {
        return this.sender;
    }

    @Override
    public @NonNull String query() {
        return this.query;
    }

    public final HelpQueryImpl<C> withSender(C value) {
        if (this.sender == value) {
            return this;
        }
        C newValue = Objects.requireNonNull(value, "sender");
        return new HelpQueryImpl<C>(this, newValue, this.query);
    }

    public final HelpQueryImpl<C> withQuery(@NonNull String value) {
        @NonNull String newValue = Objects.requireNonNull(value, "query");
        if (this.query.equals(newValue)) {
            return this;
        }
        return new HelpQueryImpl<C>(this, this.sender, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof HelpQueryImpl && this.equalTo(0, (HelpQueryImpl)another);
    }

    private boolean equalTo(int synthetic, HelpQueryImpl<?> another) {
        return this.sender.equals(another.sender) && this.query.equals(another.query);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.sender.hashCode();
        h += (h << 5) + this.query.hashCode();
        return h;
    }

    public String toString() {
        return "HelpQuery{sender=" + this.sender + ", query=" + this.query + "}";
    }

    public static <C> HelpQueryImpl<C> of(C sender, @NonNull String query) {
        return new HelpQueryImpl<C>(sender, query);
    }

    public static <C> HelpQueryImpl<C> copyOf(HelpQuery<C> instance) {
        if (instance instanceof HelpQueryImpl) {
            return (HelpQueryImpl)instance;
        }
        return HelpQueryImpl.of(instance.sender(), instance.query());
    }
}

