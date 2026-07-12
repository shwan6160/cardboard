/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.assembler;

import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Completions;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.AnnotationParser;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.ArgumentMode;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.PreprocessorMapper;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.SyntaxFragment;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.assembler.ArgumentAssembler;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.ArgumentDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.component.preprocessor.ComponentPreprocessor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserParameters;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.EitherParser;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.type.Either;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.annotations.*"})
public final class ArgumentAssemblerImpl<C>
implements ArgumentAssembler<C> {
    private final AnnotationParser<C> annotationParser;

    public ArgumentAssemblerImpl(@NonNull AnnotationParser<C> annotationParser) {
        this.annotationParser = annotationParser;
    }

    @Override
    public @NonNull CommandComponent<C> assembleArgument(@NonNull SyntaxFragment syntaxFragment, @NonNull ArgumentDescriptor descriptor) {
        ArgumentParser<C, Object> parser;
        Parameter parameter = descriptor.parameter();
        List<Annotation> annotations = Arrays.asList(parameter.getAnnotations());
        TypeToken<?> token = TypeToken.get(parameter.getParameterizedType());
        ParserParameters parameters = this.annotationParser.manager().parserRegistry().parseAnnotations(token, annotations);
        if (GenericTypeReflector.isSuperType(Either.class, token.getType())) {
            TypeToken<?> primaryType = TypeToken.get(GenericTypeReflector.getTypeParameter(parameter.getParameterizedType(), Either.class.getTypeParameters()[0]));
            TypeToken<?> fallbackType = TypeToken.get(GenericTypeReflector.getTypeParameter(parameter.getParameterizedType(), Either.class.getTypeParameters()[1]));
            ParserDescriptor primary = this.annotationParser.manager().parserRegistry().createParser(primaryType, parameters).map(primaryParser -> ParserDescriptor.of(primaryParser, primaryType)).orElseThrow(() -> new IllegalArgumentException(String.format("Parameter '%s' has parser 'Either<%s, ?>' but no parser exists for that type", parameter.getName(), token.getType().getTypeName())));
            ParserDescriptor fallback = this.annotationParser.manager().parserRegistry().createParser(fallbackType, parameters).map(fallbackParser -> ParserDescriptor.of(fallbackParser, fallbackType)).orElseThrow(() -> new IllegalArgumentException(String.format("Parameter '%s' has parser 'Either<?, %s>' but no parser exists for that type", parameter.getName(), token.getType().getTypeName())));
            parser = EitherParser.eitherParser(primary, fallback).parser();
        } else {
            parser = descriptor.parserName() == null ? this.annotationParser.manager().parserRegistry().createParser(token, parameters).orElseThrow(() -> new IllegalArgumentException(String.format("Parameter '%s' has parser '%s' but no parser exists for that type", parameter.getName(), token.getType().getTypeName()))) : this.annotationParser.manager().parserRegistry().createParser(this.annotationParser.processString(descriptor.parserName()), parameters).orElseThrow(() -> new IllegalArgumentException(String.format("Parameter '%s' has parser '%s' but no parser exists for that type", parameter.getName(), token.getType().getTypeName())));
        }
        String argumentName = this.annotationParser.processString(descriptor.name());
        if (syntaxFragment.argumentMode() == ArgumentMode.LITERAL) {
            throw new IllegalArgumentException(String.format("Invalid command argument '%s': Missing syntax mapping", argumentName));
        }
        CommandComponent.Builder<Object, ?> componentBuilder = CommandComponent.builder();
        componentBuilder.commandManager(this.annotationParser.manager()).valueType(parameter.getType()).name(argumentName).parser(parser).required(syntaxFragment.argumentMode() == ArgumentMode.REQUIRED);
        Completions completions = parameter.getDeclaredAnnotation(Completions.class);
        if (completions != null) {
            List suggestions = Arrays.stream(completions.value().replace(" ", "").split(",")).map(Suggestion::suggestion).collect(Collectors.toList());
            componentBuilder.suggestionProvider(SuggestionProvider.suggesting(suggestions));
        } else if (descriptor.suggestions() != null) {
            String suggestionProviderName = this.annotationParser.processString(descriptor.suggestions());
            Optional<SuggestionProvider<C>> suggestionsFunction = this.annotationParser.manager().parserRegistry().getSuggestionProvider(suggestionProviderName);
            componentBuilder.suggestionProvider(suggestionsFunction.orElseThrow(() -> new IllegalArgumentException(String.format("There is no suggestion provider with name '%s'. Did you forget to register it?", suggestionProviderName))));
        }
        if (descriptor.description() != null) {
            componentBuilder.description(descriptor.description());
        }
        if (syntaxFragment.argumentMode() == ArgumentMode.OPTIONAL && descriptor.defaultValue() != null) {
            componentBuilder.defaultValue(descriptor.defaultValue());
        }
        for (Annotation annotation : annotations) {
            PreprocessorMapper<?, C> preprocessorMapper = this.annotationParser.preprocessorMappers().get(annotation.annotationType());
            if (preprocessorMapper == null) continue;
            ComponentPreprocessor<C> preprocessor = preprocessorMapper.mapAnnotation(annotation);
            componentBuilder.preprocessor(preprocessor);
        }
        return componentBuilder.build();
    }
}

