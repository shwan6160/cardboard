/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.parser;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.method.AnnotatedMethodHandler;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.method.ParameterValue;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.injection.ParameterInjectorRegistry;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class MethodArgumentParser<C, T>
extends AnnotatedMethodHandler<C>
implements ArgumentParser<C, T> {
    private final SuggestionProvider<C> suggestionProvider;

    public MethodArgumentParser(@NonNull SuggestionProvider<C> suggestionProvider, @NonNull Object instance, @NonNull Method method, @NonNull ParameterInjectorRegistry<C> injectorRegistry) {
        super(method, instance, injectorRegistry);
        this.suggestionProvider = suggestionProvider;
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull T> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        List arguments = this.createParameterValues(commandContext, this.parameters(), Collections.singletonList(commandInput)).stream().map(ParameterValue::value).collect(Collectors.toList());
        try {
            return ArgumentParseResult.success(this.methodHandle().invokeWithArguments(arguments));
        }
        catch (Throwable t) {
            return ArgumentParseResult.failure(t);
        }
    }

    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return this.suggestionProvider;
    }
}

