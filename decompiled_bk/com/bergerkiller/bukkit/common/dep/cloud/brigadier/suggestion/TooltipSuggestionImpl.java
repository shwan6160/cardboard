/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.immutables.value.Generated
 */
package com.bergerkiller.bukkit.common.dep.cloud.brigadier.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.brigadier.suggestion.TooltipSuggestion;
import com.mojang.brigadier.Message;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Generated;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="TooltipSuggestion", generator="Immutables")
final class TooltipSuggestionImpl
implements TooltipSuggestion {
    private final @NonNull String suggestion;
    private final @Nullable Message tooltip;

    private TooltipSuggestionImpl(@NonNull String suggestion, @Nullable Message tooltip) {
        this.suggestion = Objects.requireNonNull(suggestion, "suggestion");
        this.tooltip = tooltip;
    }

    private TooltipSuggestionImpl(TooltipSuggestionImpl original, @NonNull String suggestion, @Nullable Message tooltip) {
        this.suggestion = suggestion;
        this.tooltip = tooltip;
    }

    @Override
    public @NonNull String suggestion() {
        return this.suggestion;
    }

    @Override
    public @Nullable Message tooltip() {
        return this.tooltip;
    }

    @Override
    public final TooltipSuggestionImpl withSuggestion(@NonNull String value) {
        @NonNull String newValue = Objects.requireNonNull(value, "suggestion");
        if (this.suggestion.equals(newValue)) {
            return this;
        }
        return new TooltipSuggestionImpl(this, newValue, this.tooltip);
    }

    public final TooltipSuggestionImpl withTooltip(@Nullable Message value) {
        if (this.tooltip == value) {
            return this;
        }
        return new TooltipSuggestionImpl(this, this.suggestion, value);
    }

    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof TooltipSuggestionImpl && this.equalsByValue((TooltipSuggestionImpl)another);
    }

    private boolean equalsByValue(TooltipSuggestionImpl another) {
        return this.suggestion.equals(another.suggestion) && Objects.equals(this.tooltip, another.tooltip);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.suggestion.hashCode();
        h += (h << 5) + Objects.hashCode(this.tooltip);
        return h;
    }

    public String toString() {
        return "TooltipSuggestion{suggestion=" + this.suggestion + ", tooltip=" + this.tooltip + "}";
    }

    public static TooltipSuggestionImpl of(@NonNull String suggestion, @Nullable Message tooltip) {
        return new TooltipSuggestionImpl(suggestion, tooltip);
    }

    public static TooltipSuggestionImpl copyOf(TooltipSuggestion instance) {
        if (instance instanceof TooltipSuggestionImpl) {
            return (TooltipSuggestionImpl)instance;
        }
        return TooltipSuggestionImpl.of(instance.suggestion(), instance.tooltip());
    }
}

