/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.brigadier.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.brigadier.suggestion.BrigadierSuggestionFactory;
import com.bergerkiller.bukkit.common.dep.cloud.internal.CommandNode;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL, since="2.0.0")
public final class CloudDelegatingSuggestionProvider<C, S>
implements SuggestionProvider<S> {
    private final BrigadierSuggestionFactory<C, S> brigadierSuggestionFactory;
    private final CommandNode<C> node;

    public CloudDelegatingSuggestionProvider(@NonNull BrigadierSuggestionFactory<C, S> suggestionFactory, @NonNull CommandNode<C> node) {
        this.brigadierSuggestionFactory = suggestionFactory;
        this.node = node;
    }

    @Override
    public @NonNull CompletableFuture<Suggestions> getSuggestions(@NonNull CommandContext<S> context, @NonNull SuggestionsBuilder builder) throws CommandSyntaxException {
        return this.brigadierSuggestionFactory.buildSuggestions(context, this.node.parent(), builder);
    }
}

