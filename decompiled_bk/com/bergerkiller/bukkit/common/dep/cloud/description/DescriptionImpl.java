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
package com.bergerkiller.bukkit.common.dep.cloud.description;

import com.bergerkiller.bukkit.common.dep.cloud.description.Description;
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
@Generated(from="Description", generator="Immutables")
@Immutable
final class DescriptionImpl
implements Description {
    private final @NonNull String textDescription;

    private DescriptionImpl(@NonNull String textDescription) {
        this.textDescription = Objects.requireNonNull(textDescription, "textDescription");
    }

    private DescriptionImpl(DescriptionImpl original, @NonNull String textDescription) {
        this.textDescription = textDescription;
    }

    @Override
    public @NonNull String textDescription() {
        return this.textDescription;
    }

    public final DescriptionImpl withTextDescription(@NonNull String value) {
        @NonNull String newValue = Objects.requireNonNull(value, "textDescription");
        if (this.textDescription.equals(newValue)) {
            return this;
        }
        return new DescriptionImpl(this, newValue);
    }

    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof DescriptionImpl && this.equalTo(0, (DescriptionImpl)another);
    }

    private boolean equalTo(int synthetic, DescriptionImpl another) {
        return this.textDescription.equals(another.textDescription);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.textDescription.hashCode();
        return h;
    }

    public String toString() {
        return "Description{textDescription=" + this.textDescription + "}";
    }

    public static DescriptionImpl of(@NonNull String textDescription) {
        return new DescriptionImpl(textDescription);
    }

    public static DescriptionImpl copyOf(Description instance) {
        if (instance instanceof DescriptionImpl) {
            return (DescriptionImpl)instance;
        }
        return DescriptionImpl.of(instance.textDescription());
    }
}

