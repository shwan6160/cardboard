/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.parser;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.parser.MethodArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.parser.MethodArgumentParserFactory;
import com.bergerkiller.bukkit.common.dep.cloud.injection.ParameterInjectorRegistry;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.lang.reflect.Method;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class MethodArgumentParserFactoryImpl<C>
implements MethodArgumentParserFactory<C> {
    @Override
    public @NonNull ParserDescriptor<C, ?> createArgumentParser(@NonNull SuggestionProvider<C> suggestionProvider, @NonNull Object instance, @NonNull Method method, @NonNull ParameterInjectorRegistry<C> injectorRegistry) {
        return ParserDescriptor.of(new MethodArgumentParser(suggestionProvider, instance, method, injectorRegistry), TypeToken.get(method.getGenericReturnType()));
    }
}

