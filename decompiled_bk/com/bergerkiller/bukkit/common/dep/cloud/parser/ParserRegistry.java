/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.common.returnsreceiver.qual.This
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser;

import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserParameters;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;

@API(status=API.Status.STABLE)
public interface ParserRegistry<C> {
    public <T> @This ParserRegistry<C> registerParserSupplier(@NonNull TypeToken<T> var1, @NonNull Function<@NonNull ParserParameters, @NonNull ArgumentParser<C, ?>> var2);

    @API(status=API.Status.STABLE)
    default public <T> @This ParserRegistry<C> registerParser(@NonNull ParserDescriptor<C, T> descriptor) {
        return this.registerParserSupplier(descriptor.valueType(), parameters -> descriptor.parser());
    }

    public @This ParserRegistry<C> registerNamedParserSupplier(@NonNull String var1, @NonNull Function<@NonNull ParserParameters, @NonNull ArgumentParser<C, ?>> var2);

    default public @This ParserRegistry<C> registerNamedParser(@NonNull String name, @NonNull ParserDescriptor<C, ?> descriptor) {
        return this.registerNamedParserSupplier(name, parameters -> descriptor.parser());
    }

    public <A extends Annotation> @This ParserRegistry<C> registerAnnotationMapper(@NonNull Class<A> var1, @NonNull AnnotationMapper<A> var2);

    public @NonNull ParserParameters parseAnnotations(@NonNull TypeToken<?> var1, @NonNull Collection<? extends @NonNull Annotation> var2);

    public <T> @NonNull Optional<ArgumentParser<C, T>> createParser(@NonNull TypeToken<T> var1, @NonNull ParserParameters var2);

    public <T> @NonNull Optional<ArgumentParser<C, T>> createParser(@NonNull String var1, @NonNull ParserParameters var2);

    @API(status=API.Status.STABLE)
    public void registerSuggestionProvider(@NonNull String var1, @NonNull SuggestionProvider<C> var2);

    @API(status=API.Status.STABLE)
    public @NonNull Optional<SuggestionProvider<C>> getSuggestionProvider(@NonNull String var1);

    @FunctionalInterface
    @API(status=API.Status.STABLE)
    public static interface AnnotationMapper<A extends Annotation> {
        public @NonNull ParserParameters mapAnnotation(@NonNull A var1, @NonNull TypeToken<?> var2);
    }
}

