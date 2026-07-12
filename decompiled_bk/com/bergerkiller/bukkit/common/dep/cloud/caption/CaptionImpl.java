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

import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
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
@Generated(from="Caption", generator="Immutables")
@Immutable
final class CaptionImpl
implements Caption {
    private final @NonNull String key;

    private CaptionImpl(@NonNull String key) {
        this.key = Objects.requireNonNull(key, "key");
    }

    private CaptionImpl(CaptionImpl original, @NonNull String key) {
        this.key = key;
    }

    @Override
    public @NonNull String key() {
        return this.key;
    }

    public final CaptionImpl withKey(@NonNull String value) {
        @NonNull String newValue = Objects.requireNonNull(value, "key");
        if (this.key.equals(newValue)) {
            return this;
        }
        return new CaptionImpl(this, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof CaptionImpl && this.equalTo(0, (CaptionImpl)another);
    }

    private boolean equalTo(int synthetic, CaptionImpl another) {
        return this.key.equals(another.key);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.key.hashCode();
        return h;
    }

    public String toString() {
        return "Caption{key=" + this.key + "}";
    }

    public static CaptionImpl of(@NonNull String key) {
        return new CaptionImpl(key);
    }

    public static CaptionImpl copyOf(Caption instance) {
        if (instance instanceof CaptionImpl) {
            return (CaptionImpl)instance;
        }
        return CaptionImpl.of(instance.key());
    }
}

