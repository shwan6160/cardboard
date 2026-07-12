/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.suggestion;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.suggestion.MethodSuggestionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.injection.ParameterInjectorRegistry;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import java.lang.reflect.Method;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
@API(status=API.Status.STABLE)
public interface SuggestionProviderFactory<C> {
    public static <C> @NonNull SuggestionProviderFactory<C> defaultFactory() {
        return MethodSuggestionProvider::new;
    }

    public @NonNull SuggestionProvider<C> createSuggestionProvider(@NonNull Object var1, @NonNull Method var2, @NonNull ParameterInjectorRegistry<C> var3);
}

