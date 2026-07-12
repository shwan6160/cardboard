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
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProcessor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL)
final class ChainedSuggestionProcessor<C>
implements SuggestionProcessor<C> {
    private final List<SuggestionProcessor<C>> links;

    ChainedSuggestionProcessor(List<SuggestionProcessor<C>> links) {
        ArrayList<SuggestionProcessor<C>> list = new ArrayList<SuggestionProcessor<C>>();
        ChainedSuggestionProcessor.flattenChain(list, links);
        this.links = Collections.unmodifiableList(list);
    }

    private static <C> void flattenChain(@NonNull List<SuggestionProcessor<C>> into, @NonNull Collection<SuggestionProcessor<C>> links) {
        for (SuggestionProcessor<C> link : links) {
            if (link instanceof ChainedSuggestionProcessor) {
                ChainedSuggestionProcessor.flattenChain(into, ((ChainedSuggestionProcessor)link).links);
                continue;
            }
            into.add(link);
        }
    }

    @Override
    public @NonNull Stream<@NonNull Suggestion> process(@NonNull CommandPreprocessingContext<C> context, @NonNull Stream<@NonNull Suggestion> suggestions) {
        Stream<Suggestion> currentLink = suggestions;
        for (SuggestionProcessor<C> link : this.links) {
            currentLink = link.process(context, currentLink);
        }
        return currentLink;
    }
}

