/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.parser;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.parser.MethodArgumentParserFactoryImpl;
import com.bergerkiller.bukkit.common.dep.cloud.injection.ParameterInjectorRegistry;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import java.lang.reflect.Method;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
@API(status=API.Status.EXPERIMENTAL)
public interface MethodArgumentParserFactory<C> {
    public static <C> @NonNull MethodArgumentParserFactory<C> defaultFactory() {
        return new MethodArgumentParserFactoryImpl();
    }

    public @NonNull ParserDescriptor<C, ?> createArgumentParser(@NonNull SuggestionProvider<C> var1, @NonNull Object var2, @NonNull Method var3, @NonNull ParameterInjectorRegistry<C> var4);
}

