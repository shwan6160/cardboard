/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.execution.preprocessor.CommandPreprocessingContext;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.ChainedSuggestionProcessor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
@API(status=API.Status.STABLE)
public interface SuggestionProcessor<C> {
    public static <C> @NonNull SuggestionProcessor<C> passThrough() {
        return (ctx, suggestions) -> suggestions;
    }

    public @NonNull Stream<@NonNull Suggestion> process(@NonNull CommandPreprocessingContext<C> var1, @NonNull Stream<@NonNull Suggestion> var2);

    default public @NonNull SuggestionProcessor<C> then(@NonNull SuggestionProcessor<C> nextProcessor) {
        Objects.requireNonNull(nextProcessor, "nextProcessor");
        return new ChainedSuggestionProcessor(Arrays.asList(this, nextProcessor));
    }
}

