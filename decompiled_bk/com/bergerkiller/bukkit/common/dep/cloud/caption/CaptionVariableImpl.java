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
package com.bergerkiller.bukkit.common.dep.cloud.caption;

import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
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
@Generated(from="CaptionVariable", generator="Immutables")
@Immutable
final class CaptionVariableImpl
implements CaptionVariable {
    private final @NonNull String key;
    private final @NonNull String value;

    private CaptionVariableImpl(@NonNull String key, @NonNull String value) {
        this.key = Objects.requireNonNull(key, "key");
        this.value = Objects.requireNonNull(value, "value");
    }

    private CaptionVariableImpl(CaptionVariableImpl original, @NonNull String key, @NonNull String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public @NonNull String key() {
        return this.key;
    }

    @Override
    public @NonNull String value() {
        return this.value;
    }

    public final CaptionVariableImpl withKey(@NonNull String value) {
        @NonNull String newValue = Objects.requireNonNull(value, "key");
        if (this.key.equals(newValue)) {
            return this;
        }
        return new CaptionVariableImpl(this, newValue, this.value);
    }

    public final CaptionVariableImpl withValue(@NonNull String value) {
        @NonNull String newValue = Objects.requireNonNull(value, "value");
        if (this.value.equals(newValue)) {
            return this;
        }
        return new CaptionVariableImpl(this, this.key, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof CaptionVariableImpl && this.equalTo(0, (CaptionVariableImpl)another);
    }

    private boolean equalTo(int synthetic, CaptionVariableImpl another) {
        return this.key.equals(another.key) && this.value.equals(another.value);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.key.hashCode();
        h += (h << 5) + this.value.hashCode();
        return h;
    }

    public String toString() {
        return "CaptionVariable{key=" + this.key + ", value=" + this.value + "}";
    }

    public static CaptionVariableImpl of(@NonNull String key, @NonNull String value) {
        return new CaptionVariableImpl(key, value);
    }

    public static CaptionVariableImpl copyOf(CaptionVariable instance) {
        if (instance instanceof CaptionVariableImpl) {
            return (CaptionVariableImpl)instance;
        }
        return CaptionVariableImpl.of(instance.key(), instance.value());
    }
}

