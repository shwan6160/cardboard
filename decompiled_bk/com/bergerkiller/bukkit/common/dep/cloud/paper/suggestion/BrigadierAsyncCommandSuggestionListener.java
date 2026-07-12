/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.event.server.AsyncTabCompleteEvent
 *  org.bukkit.event.EventHandler
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.brigadier.suggestion.TooltipSuggestion;
import com.bergerkiller.bukkit.common.dep.cloud.paper.LegacyPaperCommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.paper.suggestion.AsyncCommandSuggestionListener;
import com.bergerkiller.bukkit.common.dep.cloud.paper.suggestion.tooltips.CompletionMapper;
import com.bergerkiller.bukkit.common.dep.cloud.paper.suggestion.tooltips.CompletionMapperFactory;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionFactory;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestions;
import com.bergerkiller.bukkit.common.dep.cloud.util.StringUtils;
import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bukkit.event.EventHandler;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class BrigadierAsyncCommandSuggestionListener<C>
extends AsyncCommandSuggestionListener<C> {
    private final CompletionMapperFactory completionMapperFactory = CompletionMapperFactory.detectingRelocation();
    private final SuggestionFactory<C, ? extends TooltipSuggestion> suggestionFactory;

    BrigadierAsyncCommandSuggestionListener(@NonNull LegacyPaperCommandManager<C> paperCommandManager) {
        super(paperCommandManager);
        this.suggestionFactory = paperCommandManager.suggestionFactory().mapped(TooltipSuggestion::tooltipSuggestion);
    }

    @Override
    @EventHandler
    void onTabCompletion(@NonNull AsyncTabCompleteEvent event) {
        super.onTabCompletion(event);
    }

    @Override
    protected Suggestions<C, ? extends TooltipSuggestion> querySuggestions(@NonNull C commandSender, @NonNull String input) {
        return this.suggestionFactory.suggestImmediately(commandSender, input);
    }

    @Override
    protected void setSuggestions(@NonNull AsyncTabCompleteEvent event, @NonNull C commandSender, @NonNull String input) {
        CompletionMapper completionMapper = this.completionMapperFactory.createMapper();
        Suggestions suggestions = this.querySuggestions(commandSender, input);
        event.completions(suggestions.list().stream().map(suggestion -> {
            @Nullable String trim = StringUtils.trimBeforeLastSpace(suggestion.suggestion(), suggestions.commandInput());
            if (trim == null) {
                return null;
            }
            return suggestion.withSuggestion(trim);
        }).filter(Objects::nonNull).map(completionMapper::map).collect(Collectors.toList()));
    }
}

