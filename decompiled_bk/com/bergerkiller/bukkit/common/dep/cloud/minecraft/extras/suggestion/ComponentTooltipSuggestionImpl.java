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
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.suggestion.ComponentTooltipSuggestion;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Generated;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="ComponentTooltipSuggestion", generator="Immutables")
final class ComponentTooltipSuggestionImpl
implements ComponentTooltipSuggestion {
    private final @NonNull String suggestion;
    private final @Nullable Component tooltip;

    private ComponentTooltipSuggestionImpl(@NonNull String suggestion, @Nullable Component tooltip) {
        this.suggestion = Objects.requireNonNull(suggestion, "suggestion");
        this.tooltip = tooltip;
    }

    private ComponentTooltipSuggestionImpl(ComponentTooltipSuggestionImpl original, @NonNull String suggestion, @Nullable Component tooltip) {
        this.suggestion = suggestion;
        this.tooltip = tooltip;
    }

    @Override
    public @NonNull String suggestion() {
        return this.suggestion;
    }

    @Override
    public @Nullable Component tooltip() {
        return this.tooltip;
    }

    @Override
    public final ComponentTooltipSuggestionImpl withSuggestion(@NonNull String value) {
        @NonNull String newValue = Objects.requireNonNull(value, "suggestion");
        if (this.suggestion.equals(newValue)) {
            return this;
        }
        return new ComponentTooltipSuggestionImpl(this, newValue, this.tooltip);
    }

    public final ComponentTooltipSuggestionImpl withTooltip(@Nullable Component value) {
        if (this.tooltip == value) {
            return this;
        }
        return new ComponentTooltipSuggestionImpl(this, this.suggestion, value);
    }

    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof ComponentTooltipSuggestionImpl && this.equalsByValue((ComponentTooltipSuggestionImpl)another);
    }

    private boolean equalsByValue(ComponentTooltipSuggestionImpl another) {
        return this.suggestion.equals(another.suggestion) && Objects.equals(this.tooltip, another.tooltip);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.suggestion.hashCode();
        h += (h << 5) + Objects.hashCode(this.tooltip);
        return h;
    }

    public String toString() {
        return "ComponentTooltipSuggestion{suggestion=" + this.suggestion + ", tooltip=" + this.tooltip + "}";
    }

    public static ComponentTooltipSuggestionImpl of(@NonNull String suggestion, @Nullable Component tooltip) {
        return new ComponentTooltipSuggestionImpl(suggestion, tooltip);
    }

    public static ComponentTooltipSuggestionImpl copyOf(ComponentTooltipSuggestion instance) {
        if (instance instanceof ComponentTooltipSuggestionImpl) {
            return (ComponentTooltipSuggestionImpl)instance;
        }
        return ComponentTooltipSuggestionImpl.of(instance.suggestion(), instance.tooltip());
    }
}

