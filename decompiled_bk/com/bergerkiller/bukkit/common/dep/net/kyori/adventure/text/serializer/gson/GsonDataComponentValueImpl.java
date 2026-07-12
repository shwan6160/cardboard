/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson;

import com.bergerkiller.bukkit.common.dep.gson.JsonElement;
import com.bergerkiller.bukkit.common.dep.gson.JsonNull;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.internal.Internals;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.DataComponentValue;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.GsonDataComponentValue;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.ExaminableProperty;
import java.util.Objects;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GsonDataComponentValueImpl
implements GsonDataComponentValue {
    private final JsonElement element;

    GsonDataComponentValueImpl(@NotNull JsonElement element) {
        this.element = element;
    }

    @Override
    @NotNull
    public JsonElement element() {
        return this.element;
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("element", this.element));
    }

    public String toString() {
        return Internals.toString(this);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        GsonDataComponentValueImpl that = (GsonDataComponentValueImpl)other;
        return Objects.equals(this.element, that.element);
    }

    public int hashCode() {
        return Objects.hashCode(this.element);
    }

    static final class RemovedGsonComponentValueImpl
    extends GsonDataComponentValueImpl
    implements DataComponentValue.Removed {
        static final RemovedGsonComponentValueImpl INSTANCE = new RemovedGsonComponentValueImpl();

        private RemovedGsonComponentValueImpl() {
            super(JsonNull.INSTANCE);
        }
    }
}

