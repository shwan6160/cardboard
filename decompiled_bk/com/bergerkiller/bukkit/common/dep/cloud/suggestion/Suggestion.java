/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SimpleSuggestion;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface Suggestion {
    public static @NonNull Suggestion suggestion(@NonNull String suggestion) {
        return new SimpleSuggestion(suggestion);
    }

    public @NonNull String suggestion();

    public @NonNull Suggestion withSuggestion(@NonNull String var1);
}

