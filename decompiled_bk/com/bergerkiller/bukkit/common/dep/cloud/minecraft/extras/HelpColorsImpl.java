/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Generated
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras;

import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.MinecraftHelp;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.TextColor;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Generated;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="MinecraftHelp.HelpColors", generator="Immutables")
final class HelpColorsImpl
implements MinecraftHelp.HelpColors {
    private final @NonNull TextColor primary;
    private final @NonNull TextColor highlight;
    private final @NonNull TextColor alternateHighlight;
    private final @NonNull TextColor text;
    private final @NonNull TextColor accent;

    private HelpColorsImpl(@NonNull TextColor primary, @NonNull TextColor highlight, @NonNull TextColor alternateHighlight, @NonNull TextColor text, @NonNull TextColor accent) {
        this.primary = Objects.requireNonNull(primary, "primary");
        this.highlight = Objects.requireNonNull(highlight, "highlight");
        this.alternateHighlight = Objects.requireNonNull(alternateHighlight, "alternateHighlight");
        this.text = Objects.requireNonNull(text, "text");
        this.accent = Objects.requireNonNull(accent, "accent");
    }

    private HelpColorsImpl(HelpColorsImpl original, @NonNull TextColor primary, @NonNull TextColor highlight, @NonNull TextColor alternateHighlight, @NonNull TextColor text, @NonNull TextColor accent) {
        this.primary = primary;
        this.highlight = highlight;
        this.alternateHighlight = alternateHighlight;
        this.text = text;
        this.accent = accent;
    }

    @Override
    public @NonNull TextColor primary() {
        return this.primary;
    }

    @Override
    public @NonNull TextColor highlight() {
        return this.highlight;
    }

    @Override
    public @NonNull TextColor alternateHighlight() {
        return this.alternateHighlight;
    }

    @Override
    public @NonNull TextColor text() {
        return this.text;
    }

    @Override
    public @NonNull TextColor accent() {
        return this.accent;
    }

    public final HelpColorsImpl withPrimary(@NonNull TextColor value) {
        if (this.primary == value) {
            return this;
        }
        @NonNull TextColor newValue = Objects.requireNonNull(value, "primary");
        return new HelpColorsImpl(this, newValue, this.highlight, this.alternateHighlight, this.text, this.accent);
    }

    public final HelpColorsImpl withHighlight(@NonNull TextColor value) {
        if (this.highlight == value) {
            return this;
        }
        @NonNull TextColor newValue = Objects.requireNonNull(value, "highlight");
        return new HelpColorsImpl(this, this.primary, newValue, this.alternateHighlight, this.text, this.accent);
    }

    public final HelpColorsImpl withAlternateHighlight(@NonNull TextColor value) {
        if (this.alternateHighlight == value) {
            return this;
        }
        @NonNull TextColor newValue = Objects.requireNonNull(value, "alternateHighlight");
        return new HelpColorsImpl(this, this.primary, this.highlight, newValue, this.text, this.accent);
    }

    public final HelpColorsImpl withText(@NonNull TextColor value) {
        if (this.text == value) {
            return this;
        }
        @NonNull TextColor newValue = Objects.requireNonNull(value, "text");
        return new HelpColorsImpl(this, this.primary, this.highlight, this.alternateHighlight, newValue, this.accent);
    }

    public final HelpColorsImpl withAccent(@NonNull TextColor value) {
        if (this.accent == value) {
            return this;
        }
        @NonNull TextColor newValue = Objects.requireNonNull(value, "accent");
        return new HelpColorsImpl(this, this.primary, this.highlight, this.alternateHighlight, this.text, newValue);
    }

    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof HelpColorsImpl && this.equalsByValue((HelpColorsImpl)another);
    }

    private boolean equalsByValue(HelpColorsImpl another) {
        return this.primary.equals(another.primary) && this.highlight.equals(another.highlight) && this.alternateHighlight.equals(another.alternateHighlight) && this.text.equals(another.text) && this.accent.equals(another.accent);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.primary.hashCode();
        h += (h << 5) + this.highlight.hashCode();
        h += (h << 5) + this.alternateHighlight.hashCode();
        h += (h << 5) + this.text.hashCode();
        h += (h << 5) + this.accent.hashCode();
        return h;
    }

    public String toString() {
        return "HelpColors{primary=" + this.primary + ", highlight=" + this.highlight + ", alternateHighlight=" + this.alternateHighlight + ", text=" + this.text + ", accent=" + this.accent + "}";
    }

    public static HelpColorsImpl of(@NonNull TextColor primary, @NonNull TextColor highlight, @NonNull TextColor alternateHighlight, @NonNull TextColor text, @NonNull TextColor accent) {
        return new HelpColorsImpl(primary, highlight, alternateHighlight, text, accent);
    }

    public static HelpColorsImpl copyOf(MinecraftHelp.HelpColors instance) {
        if (instance instanceof HelpColorsImpl) {
            return (HelpColorsImpl)instance;
        }
        return HelpColorsImpl.of(instance.primary(), instance.highlight(), instance.alternateHighlight(), instance.text(), instance.accent());
    }
}

