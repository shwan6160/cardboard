/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

final class SimpleSuggestion
implements Suggestion {
    private final String suggestion;

    SimpleSuggestion(@NonNull String suggestion) {
        this.suggestion = suggestion;
    }

    @Override
    public @NonNull String suggestion() {
        return this.suggestion;
    }

    @Override
    public @NonNull Suggestion withSuggestion(@NonNull String suggestion) {
        return new SimpleSuggestion(suggestion);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SimpleSuggestion that = (SimpleSuggestion)o;
        return Objects.equals(this.suggestion, that.suggestion);
    }

    public int hashCode() {
        return Objects.hash(this.suggestion);
    }

    public String toString() {
        return this.suggestion;
    }
}

