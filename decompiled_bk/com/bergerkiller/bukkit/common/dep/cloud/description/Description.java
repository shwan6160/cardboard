/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.description;

import com.bergerkiller.bukkit.common.dep.cloud.description.DescriptionImpl;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public interface Description {
    public static final Description EMPTY = DescriptionImpl.of("");

    public static @NonNull Description empty() {
        return EMPTY;
    }

    public static @NonNull Description of(@NonNull String string) {
        if (Objects.requireNonNull(string, "string").isEmpty()) {
            return Description.empty();
        }
        return DescriptionImpl.of(string);
    }

    public static @NonNull Description description(@NonNull String string) {
        return Description.of(string);
    }

    public @NonNull String textDescription();

    default public boolean isEmpty() {
        return this.textDescription().isEmpty();
    }
}

