/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.method.AnnotatedMethodHandler;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.method.ParameterValue;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.injection.ParameterInjectorRegistry;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class MethodSuggestionProvider<C>
extends AnnotatedMethodHandler<C>
implements SuggestionProvider<C> {
    public MethodSuggestionProvider(@NonNull Object instance, @NonNull Method method, @NonNull ParameterInjectorRegistry<C> injectorRegistry) {
        super(method, instance, injectorRegistry);
    }

    @Override
    public @NonNull CompletableFuture<Iterable<@NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext<C> context, @NonNull CommandInput input) {
        try {
            List arguments = this.createParameterValues(context, this.parameters(), Arrays.asList(context, input, input.lastRemainingToken())).stream().map(ParameterValue::value).collect(Collectors.toList());
            return MethodSuggestionProvider.mapSuggestions(this.methodHandle().invokeWithArguments(arguments));
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static @NonNull CompletableFuture<Iterable<@NonNull Suggestion>> mapSuggestions(@NonNull Object input) {
        if (input instanceof CompletableFuture) {
            return MethodSuggestionProvider.mapSuggestions((CompletableFuture)input);
        }
        return CompletableFuture.completedFuture(MethodSuggestionProvider.mapCompleted(input));
    }

    public static @NonNull CompletableFuture<Iterable<@NonNull Suggestion>> mapFuture(@NonNull CompletableFuture future) {
        return future.thenApply(MethodSuggestionProvider::mapCompleted);
    }

    public static @NonNull Iterable<@NonNull Suggestion> mapCompleted(@NonNull Object input) {
        List<Suggestion> suggestions;
        if (input instanceof List) {
            suggestions = (ArrayList<Suggestion>)input;
        } else if (input instanceof Collection) {
            suggestions = new ArrayList<Suggestion>((Collection)input);
        } else if (input instanceof Iterable) {
            suggestions = new ArrayList();
            for (Object suggestion : (Iterable)input) {
                suggestions.add((Suggestion)suggestion);
            }
        } else if (input instanceof Stream) {
            suggestions = ((Stream)input).collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException(String.format("Cannot handle suggestion input of type %s", input.getClass().getName()));
        }
        if (suggestions.isEmpty()) {
            return Collections.emptyList();
        }
        Object suggestion = suggestions.get(0);
        if (suggestion instanceof Suggestion) {
            return suggestions;
        }
        if (suggestion instanceof String) {
            return suggestions.stream().map(Object::toString).map(Suggestion::suggestion).collect(Collectors.toList());
        }
        throw new IllegalArgumentException(String.format("Cannot handle suggestions of type: %s", suggestion.getClass().getName()));
    }
}

