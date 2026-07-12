/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
@API(status=API.Status.STABLE)
public interface SuggestionMapper<S extends Suggestion> {
    public static @NonNull SuggestionMapper<Suggestion> identity() {
        return suggestion -> suggestion;
    }

    public @NonNull S map(@NonNull Suggestion var1);

    default public <S1 extends Suggestion> @NonNull SuggestionMapper<S1> then(@NonNull SuggestionMapper<S1> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        return suggestion -> mapper.map((Suggestion)this.map(suggestion));
    }
}

