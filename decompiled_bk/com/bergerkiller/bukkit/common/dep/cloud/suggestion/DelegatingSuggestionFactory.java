/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.CommandTree;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContextFactory;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.execution.ExecutionCoordinator;
import com.bergerkiller.bukkit.common.dep.cloud.services.State;
import com.bergerkiller.bukkit.common.dep.cloud.setting.ManagerSetting;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionFactory;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionMapper;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestions;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
public final class DelegatingSuggestionFactory<C, S extends Suggestion>
implements SuggestionFactory<C, S> {
    private final List<S> singleEmptySuggestion;
    private final CommandManager<C> commandManager;
    private final CommandTree<C> commandTree;
    private final CommandContextFactory<C> contextFactory;
    private final ExecutionCoordinator<C> executionCoordinator;
    private final SuggestionMapper<S> mapper;

    public DelegatingSuggestionFactory(@NonNull CommandManager<C> commandManager, @NonNull CommandTree<C> commandTree, @NonNull CommandContextFactory<C> contextFactory, @NonNull ExecutionCoordinator<C> executionCoordinator, @NonNull SuggestionMapper<S> mapper) {
        this.commandManager = commandManager;
        this.commandTree = commandTree;
        this.contextFactory = contextFactory;
        this.executionCoordinator = executionCoordinator;
        this.mapper = mapper;
        this.singleEmptySuggestion = Collections.singletonList(mapper.map(Suggestion.suggestion("")));
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Suggestions<C, S>> suggest(@NonNull CommandContext<C> context, @NonNull String input) {
        return this.suggestFromTree(context, input);
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Suggestions<C, S>> suggest(@NonNull C sender, @NonNull String input) {
        return this.suggest((C)this.contextFactory.create(true, sender), input);
    }

    @Override
    public <S2 extends Suggestion> @NonNull SuggestionFactory<C, S2> mapped(@NonNull SuggestionMapper<S2> mapper) {
        return new DelegatingSuggestionFactory<C, S2>(this.commandManager, this.commandTree, this.contextFactory, this.executionCoordinator, this.mapper.then(mapper));
    }

    private @NonNull CompletableFuture<@NonNull Suggestions<C, S>> suggestFromTree(@NonNull CommandContext<C> context, @NonNull String input) {
        @NonNull CommandInput commandInput = CommandInput.of(input);
        context.store("__raw_input__", commandInput.copy());
        if (this.commandManager.preprocessContext(context, commandInput) != State.ACCEPTED) {
            if (this.commandManager.settings().get(ManagerSetting.FORCE_SUGGESTION)) {
                return CompletableFuture.completedFuture(Suggestions.create(context, this.singleEmptySuggestion, commandInput));
            }
            return CompletableFuture.completedFuture(Suggestions.create(context, Collections.emptyList(), commandInput));
        }
        return this.executionCoordinator.coordinateSuggestions(this.commandTree, context, commandInput, this.mapper).thenApply(suggestions -> {
            if (this.commandManager.settings().get(ManagerSetting.FORCE_SUGGESTION) && suggestions.list().isEmpty()) {
                return Suggestions.create(suggestions.commandContext(), this.singleEmptySuggestion, commandInput);
            }
            return suggestions;
        });
    }
}

