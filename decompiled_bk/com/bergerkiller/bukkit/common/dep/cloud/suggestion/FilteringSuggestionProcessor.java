/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.execution.preprocessor.CommandPreprocessingContext;
import com.bergerkiller.bukkit.common.dep.cloud.internal.CommandInputTokenizer;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProcessor;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.STABLE)
public final class FilteringSuggestionProcessor<C>
implements SuggestionProcessor<C> {
    private final @NonNull Filter<C> filter;

    @API(status=API.Status.STABLE)
    public FilteringSuggestionProcessor() {
        this(Filter.partialTokenMatches(true));
    }

    @API(status=API.Status.STABLE)
    public FilteringSuggestionProcessor(@NonNull Filter<C> filter) {
        this.filter = filter;
    }

    @Override
    public @NonNull Stream<@NonNull Suggestion> process(@NonNull CommandPreprocessingContext<C> context, @NonNull Stream<@NonNull Suggestion> suggestions) {
        String input = context.commandInput().isEmpty(true) ? "" : context.commandInput().skipWhitespace().remainingInput();
        return suggestions.map(suggestion -> {
            String filtered = this.filter.filter(context, suggestion.suggestion(), input);
            if (filtered == null) {
                return null;
            }
            return suggestion.withSuggestion(filtered);
        }).filter(Objects::nonNull);
    }

    @API(status=API.Status.STABLE)
    @FunctionalInterface
    public static interface Filter<C> {
        @API(status=API.Status.STABLE)
        public @Nullable String filter(@NonNull CommandPreprocessingContext<C> var1, @NonNull String var2, @NonNull String var3);

        @API(status=API.Status.STABLE)
        default public @NonNull Filter<C> and(@NonNull Filter<C> and) {
            return (ctx, suggestion, input) -> {
                @Nullable String filtered = this.filter(ctx, suggestion, input);
                if (filtered == null) {
                    return null;
                }
                return and.filter(ctx, filtered, input);
            };
        }

        @API(status=API.Status.STABLE)
        public static <C> @NonNull Simple<C> startsWith(boolean ignoreCase) {
            BiPredicate<String, String> test = ignoreCase ? (suggestion, input) -> suggestion.toLowerCase(Locale.ROOT).startsWith(input.toLowerCase(Locale.ROOT)) : String::startsWith;
            return Simple.contextFree(test);
        }

        @API(status=API.Status.STABLE)
        public static <C> @NonNull Simple<C> contains(boolean ignoreCase) {
            BiPredicate<String, String> test = ignoreCase ? (suggestion, input) -> suggestion.toLowerCase(Locale.ROOT).contains(input.toLowerCase(Locale.ROOT)) : String::contains;
            return Simple.contextFree(test);
        }

        @API(status=API.Status.STABLE)
        public static <C> @NonNull Simple<C> partialTokenMatches(boolean ignoreCase) {
            return Simple.contextFree((String suggestion, String input) -> {
                LinkedList<String> suggestionTokens = new CommandInputTokenizer((String)suggestion).tokenize();
                LinkedList<String> inputTokens = new CommandInputTokenizer((String)input).tokenize();
                boolean passed = true;
                for (String inputToken : inputTokens) {
                    if (ignoreCase) {
                        inputToken = inputToken.toLowerCase(Locale.ROOT);
                    }
                    boolean foundMatch = false;
                    Iterator iterator = suggestionTokens.iterator();
                    while (iterator.hasNext()) {
                        String suggestionToken = (String)iterator.next();
                        String suggestionTokenLower = ignoreCase ? suggestionToken.toLowerCase(Locale.ROOT) : suggestionToken;
                        if (!suggestionTokenLower.contains(inputToken)) continue;
                        iterator.remove();
                        foundMatch = true;
                        break;
                    }
                    if (foundMatch) continue;
                    passed = false;
                    break;
                }
                return passed;
            });
        }

        @API(status=API.Status.STABLE)
        public static <C> @NonNull Filter<C> contextFree(@NonNull BiFunction<String, String, @Nullable String> function) {
            return (ctx, suggestion, input) -> (String)function.apply(suggestion, input);
        }

        @API(status=API.Status.STABLE)
        public static <C> @NonNull Simple<C> simple(Simple<C> filter) {
            return filter;
        }

        @API(status=API.Status.STABLE)
        @FunctionalInterface
        public static interface Simple<C>
        extends Filter<C> {
            @API(status=API.Status.STABLE)
            public boolean test(@NonNull CommandPreprocessingContext<C> var1, @NonNull String var2, @NonNull String var3);

            @Override
            default public @Nullable String filter(@NonNull CommandPreprocessingContext<C> context, @NonNull String suggestion, @NonNull String input) {
                if (this.test(context, suggestion, input)) {
                    return suggestion;
                }
                return null;
            }

            @API(status=API.Status.STABLE)
            public static <C> @NonNull Simple<C> contextFree(@NonNull BiPredicate<String, String> test) {
                return (ctx, suggestion, input) -> test.test(suggestion, input);
            }
        }
    }
}

