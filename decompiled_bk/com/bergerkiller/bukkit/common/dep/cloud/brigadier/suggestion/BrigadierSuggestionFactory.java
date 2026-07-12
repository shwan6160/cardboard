/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.brigadier.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.CloudBrigadierCommand;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.CloudBrigadierManager;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.suggestion.TooltipSuggestion;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.internal.CommandNode;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionFactory;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.INTERNAL, since="2.0.0")
public final class BrigadierSuggestionFactory<C, S> {
    private final CloudBrigadierManager<C, S> cloudBrigadierManager;
    private final CommandManager<C> commandManager;
    private final SuggestionFactory<C, ? extends TooltipSuggestion> suggestionFactory;

    public BrigadierSuggestionFactory(@NonNull CloudBrigadierManager<C, S> cloudBrigadierManager, @NonNull CommandManager<C> commandManager, @NonNull SuggestionFactory<C, ? extends TooltipSuggestion> suggestionFactory) {
        this.cloudBrigadierManager = cloudBrigadierManager;
        this.commandManager = commandManager;
        this.suggestionFactory = suggestionFactory;
    }

    public @NonNull CompletableFuture<@NonNull Suggestions> buildSuggestions(@NonNull CommandContext<S> senderContext, @Nullable CommandNode<C> parentNode, @NonNull SuggestionsBuilder builder) {
        C cloudSender = this.cloudBrigadierManager.senderMapper().map(senderContext.getSource());
        com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext<C> commandContext = new com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext<C>(true, cloudSender, this.commandManager);
        commandContext.store("_cloud_brigadier_native_sender", senderContext.getSource());
        String command = builder.getInput().substring(CloudBrigadierCommand.parsedNodes(senderContext.getLastChild()).get(0).second().getStart());
        String leading = command.split(" ")[0];
        if (leading.contains(":")) {
            command = command.substring(leading.split(":")[0].length() + 1);
        }
        return this.suggestionFactory.suggest(commandContext.sender(), command).thenApply(suggestionsResult -> {
            ArrayList suggestions = new ArrayList(suggestionsResult.list());
            if (parentNode != null) {
                Set siblingLiterals = parentNode.children().stream().map(CommandNode::component).filter(Objects::nonNull).filter(c -> c.type() == CommandComponent.ComponentType.LITERAL).flatMap(commandComponent -> commandComponent.aliases().stream()).collect(Collectors.toSet());
                suggestions.removeIf(suggestion -> siblingLiterals.contains(suggestion.suggestion()));
            }
            int trimmed = builder.getInput().length() - suggestionsResult.commandInput().length();
            int rawOffset = suggestionsResult.commandInput().cursor();
            SuggestionsBuilder suggestionsBuilder = builder.createOffset(rawOffset + trimmed);
            for (TooltipSuggestion suggestion2 : suggestions) {
                try {
                    suggestionsBuilder.suggest(Integer.parseInt(suggestion2.suggestion()), suggestion2.tooltip());
                }
                catch (NumberFormatException e) {
                    suggestionsBuilder.suggest(suggestion2.suggestion(), suggestion2.tooltip());
                }
            }
            return suggestionsBuilder.build();
        });
    }
}

