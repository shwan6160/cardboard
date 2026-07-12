/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.internal;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.execution.preprocessor.CommandPreprocessingContext;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionMapper;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProcessor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
public final class SuggestionContext<C, S extends Suggestion> {
    private final List<S> suggestions = new ArrayList<S>();
    private final CommandPreprocessingContext<C> preprocessingContext;
    private final SuggestionMapper<S> mapper;
    private final SuggestionProcessor<C> processor;
    private final CommandContext<C> commandContext;

    public SuggestionContext(@NonNull SuggestionProcessor<C> processor, @NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput, @NonNull SuggestionMapper<S> mapper) {
        this.processor = processor;
        this.commandContext = commandContext;
        this.preprocessingContext = CommandPreprocessingContext.of(this.commandContext, commandInput);
        this.mapper = mapper;
    }

    public @NonNull Suggestions<C, S> makeSuggestions() {
        Stream<Suggestion> processedStream;
        Stream<Suggestion> stream = this.suggestions.stream();
        List<Object> list = stream == (processedStream = this.processor.process(this.preprocessingContext, stream)) ? Collections.unmodifiableList(this.suggestions) : Collections.unmodifiableList(processedStream.peek(obj -> Objects.requireNonNull(obj, "suggestion")).map(this.mapper::map).collect(Collectors.toList()));
        return Suggestions.create(this.commandContext, list, this.preprocessingContext.commandInput());
    }

    public @NonNull CommandContext<C> commandContext() {
        return this.commandContext;
    }

    public void addSuggestions(@NonNull Iterable<? extends @NonNull Suggestion> suggestions) {
        suggestions.forEach(this::addSuggestion);
    }

    public void addSuggestion(@NonNull Suggestion suggestion) {
        Objects.requireNonNull(suggestion, "suggestion");
        this.suggestions.add(this.mapper.map(suggestion));
    }
}

