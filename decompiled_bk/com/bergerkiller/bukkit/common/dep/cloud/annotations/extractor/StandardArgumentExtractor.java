/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.immutables.value.Value$Default
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.AnnotationParser;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.ArgumentMode;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Default;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.DefaultValueFactory;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.DescriptionMapper;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.SyntaxFragment;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.ArgumentDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.ImmutableArgumentDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor.ArgumentExtractor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor.ImmutableStandardArgumentExtractor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor.ParameterNameExtractor;
import com.bergerkiller.bukkit.common.dep.cloud.component.DefaultValue;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Value;

@Value.Immutable
public abstract class StandardArgumentExtractor
implements ArgumentExtractor {
    private static @Nullable String nullIfEmpty(@NonNull String string) {
        if (string.isEmpty()) {
            return null;
        }
        return string;
    }

    public static @NonNull StandardArgumentExtractor create(@NonNull AnnotationParser<?> annotationParser) {
        return StandardArgumentExtractor.builder(annotationParser).build();
    }

    public static @NonNull ImmutableStandardArgumentExtractor.Builder builder(@NonNull AnnotationParser<?> annotationParser) {
        return ImmutableStandardArgumentExtractor.builder().annotationParser(annotationParser);
    }

    public static @NonNull ImmutableStandardArgumentExtractor.Builder builder(@NonNull StandardArgumentExtractor extractor) {
        return ImmutableStandardArgumentExtractor.builder().from(extractor);
    }

    public abstract @NonNull AnnotationParser<?> annotationParser();

    @Value.Default
    public @NonNull ParameterNameExtractor parameterNameExtractor() {
        return ParameterNameExtractor.simple();
    }

    @Value.Default
    public @NonNull DescriptionMapper descriptionMapper() {
        return this.annotationParser()::mapDescription;
    }

    @Override
    public final @NonNull Collection<@NonNull ArgumentDescriptor> extractArguments(@NonNull List<@NonNull SyntaxFragment> syntax, @NonNull Method method) {
        HashMap variableFragments = new HashMap();
        syntax.stream().filter(fragment -> fragment.argumentMode() != ArgumentMode.LITERAL).forEach(fragment -> variableFragments.put(fragment.major(), fragment));
        ArrayList<ArgumentDescriptor> arguments = new ArrayList<ArgumentDescriptor>();
        for (Parameter parameter : method.getParameters()) {
            String parameterName = this.parameterNameExtractor().extract(parameter);
            DefaultValue<Object, Object> defaultValue = null;
            if (parameter.isAnnotationPresent(Default.class)) {
                Default defaultAnnotation = parameter.getAnnotation(Default.class);
                if (defaultAnnotation.name().isEmpty()) {
                    defaultValue = DefaultValue.parsed(this.annotationParser().processString(parameter.getAnnotation(Default.class).value()));
                } else {
                    DefaultValueFactory<?, ?> factory = this.annotationParser().defaultValueRegistry().named(this.annotationParser().processString(defaultAnnotation.name())).orElseThrow(() -> new IllegalArgumentException(String.format("No default value factory named '%s' has been registered", defaultAnnotation.name())));
                    defaultValue = factory.create(parameter);
                }
            }
            if (!parameter.isAnnotationPresent(Argument.class)) {
                SyntaxFragment fragment2 = (SyntaxFragment)variableFragments.get(parameterName);
                if (fragment2 == null) continue;
                arguments.add(ArgumentDescriptor.builder().parameter(parameter).defaultValue(defaultValue).name(parameterName).build());
                continue;
            }
            Argument argument = parameter.getAnnotation(Argument.class);
            String name = argument.value().equals("__INFERRED_ARGUMENT_NAME__") ? parameterName : this.annotationParser().processString(argument.value());
            ImmutableArgumentDescriptor argumentDescriptor = ArgumentDescriptor.builder().parameter(parameter).name(name).parserName(StandardArgumentExtractor.nullIfEmpty(this.annotationParser().processString(argument.parserName()))).defaultValue(defaultValue).description(this.descriptionMapper().map(argument.description())).suggestions(StandardArgumentExtractor.nullIfEmpty(this.annotationParser().processString(argument.suggestions()))).build();
            arguments.add(argumentDescriptor);
        }
        return arguments;
    }
}

