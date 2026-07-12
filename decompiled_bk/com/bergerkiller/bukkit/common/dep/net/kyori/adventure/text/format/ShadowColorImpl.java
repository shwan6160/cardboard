/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.internal.Internals;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.ShadowColor;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.Examinable;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.ExaminableProperty;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ShadowColorImpl
implements ShadowColor,
Examinable {
    static final int NONE_VALUE = 0;
    static final ShadowColorImpl NONE = new ShadowColorImpl(0);
    private final int value;

    ShadowColorImpl(int value) {
        this.value = value;
    }

    @Override
    public int value() {
        return this.value;
    }

    public boolean equals(@Nullable Object other) {
        if (!(other instanceof ShadowColorImpl)) {
            return false;
        }
        ShadowColorImpl that = (ShadowColorImpl)other;
        return this.value == that.value;
    }

    public int hashCode() {
        return Integer.hashCode(this.value);
    }

    public String toString() {
        return Internals.toString(this);
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("value", this.value));
    }
}

