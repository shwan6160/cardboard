/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Generated
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.caption;

import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.caption.RichVariable;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Generated;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="RichVariable", generator="Immutables")
final class RichVariableImpl
implements RichVariable {
    private final @NonNull String key;
    private final @NonNull Component component;

    private RichVariableImpl(@NonNull String key, @NonNull Component component) {
        this.key = Objects.requireNonNull(key, "key");
        this.component = Objects.requireNonNull(component, "component");
    }

    private RichVariableImpl(RichVariableImpl original, @NonNull String key, @NonNull Component component) {
        this.key = key;
        this.component = component;
    }

    @Override
    public @NonNull String key() {
        return this.key;
    }

    @Override
    public @NonNull Component component() {
        return this.component;
    }

    public final RichVariableImpl withKey(@NonNull String value) {
        @NonNull String newValue = Objects.requireNonNull(value, "key");
        if (this.key.equals(newValue)) {
            return this;
        }
        return new RichVariableImpl(this, newValue, this.component);
    }

    public final RichVariableImpl withComponent(@NonNull Component value) {
        if (this.component == value) {
            return this;
        }
        @NonNull Component newValue = Objects.requireNonNull(value, "component");
        return new RichVariableImpl(this, this.key, newValue);
    }

    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof RichVariableImpl && this.equalsByValue((RichVariableImpl)another);
    }

    private boolean equalsByValue(RichVariableImpl another) {
        return this.key.equals(another.key) && this.component.equals(another.component);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.key.hashCode();
        h += (h << 5) + this.component.hashCode();
        return h;
    }

    public String toString() {
        return "RichVariable{key=" + this.key + ", component=" + this.component + "}";
    }

    public static RichVariableImpl of(@NonNull String key, @NonNull Component component) {
        return new RichVariableImpl(key, component);
    }

    public static RichVariableImpl copyOf(RichVariable instance) {
        if (instance instanceof RichVariableImpl) {
            return (RichVariableImpl)instance;
        }
        return RichVariableImpl.of(instance.key(), instance.component());
    }
}

