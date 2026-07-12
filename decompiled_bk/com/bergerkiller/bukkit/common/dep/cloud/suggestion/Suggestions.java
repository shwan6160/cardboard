/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionsImpl;
import java.util.List;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public interface Suggestions<C, S extends Suggestion> {
    public @NonNull CommandContext<C> commandContext();

    public @NonNull List<S> list();

    public @NonNull CommandInput commandInput();

    @API(status=API.Status.INTERNAL)
    public static <C, S extends Suggestion> Suggestions<C, S> create(@NonNull CommandContext<C> ctx, @NonNull List<S> list, @NonNull CommandInput commandInput) {
        return SuggestionsImpl.of(ctx, list, commandInput);
    }
}

