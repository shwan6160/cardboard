/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate;

import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.caption.StandardCaptionKeys;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.exception.parsing.ParserException;
import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKey;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParserBuilder;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParserPairBuilder;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParserTripletBuilder;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParsingContext;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateResultMapper;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateSuggestionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.type.tuple.Pair;
import com.bergerkiller.bukkit.common.dep.cloud.type.tuple.Triplet;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeFactory;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface AggregateParser<C, O>
extends ArgumentParser.FutureArgumentParser<C, O>,
ParserDescriptor<C, O> {
    public static <C> @NonNull AggregateParserBuilder<C> builder() {
        return new AggregateParserBuilder();
    }

    public static <C, U, V> @NonNull AggregateParserPairBuilder<C, U, V, Pair<U, V>> pairBuilder(@NonNull String firstName, @NonNull ParserDescriptor<C, U> firstParser, @NonNull String secondName, @NonNull ParserDescriptor<C, V> secondParser) {
        return new AggregateParserPairBuilder(CommandComponent.builder(firstName, firstParser).build(), CommandComponent.builder(secondName, secondParser).build(), AggregateParserPairBuilder.defaultMapper(), TypeToken.get(TypeFactory.parameterizedClass(Pair.class, GenericTypeReflector.box(firstParser.valueType().getType()), GenericTypeReflector.box(secondParser.valueType().getType()))));
    }

    public static <C, U, V, Z> @NonNull AggregateParserTripletBuilder<C, U, V, Z, Triplet<U, V, Z>> tripletBuilder(@NonNull String firstName, @NonNull ParserDescriptor<C, U> firstParser, @NonNull String secondName, @NonNull ParserDescriptor<C, V> secondParser, @NonNull String thirdName, @NonNull ParserDescriptor<C, Z> thirdParser) {
        return new AggregateParserTripletBuilder(CommandComponent.builder(firstName, firstParser).build(), CommandComponent.builder(secondName, secondParser).build(), CommandComponent.builder(thirdName, thirdParser).build(), AggregateParserTripletBuilder.defaultMapper(), TypeToken.get(TypeFactory.parameterizedClass(Triplet.class, GenericTypeReflector.box(firstParser.valueType().getType()), GenericTypeReflector.box(secondParser.valueType().getType()), GenericTypeReflector.box(thirdParser.valueType().getType()))));
    }

    public @NonNull List<@NonNull CommandComponent<C>> components();

    public @NonNull AggregateResultMapper<C, O> mapper();

    @Override
    default public @NonNull CompletableFuture<@NonNull ArgumentParseResult<O>> parseFuture(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        AggregateParsingContext aggregateParsingContext = AggregateParsingContext.argumentContext(this);
        CompletionStage<Object> future = CompletableFuture.completedFuture(null);
        for (CommandComponent component : this.components()) {
            future = future.thenCompose(result -> {
                if (result != null && result.failure().isPresent()) {
                    return ArgumentParseResult.failureFuture(result.failure().get());
                }
                commandInput.skipWhitespace(1);
                if (commandInput.isEmpty()) {
                    return ArgumentParseResult.failureFuture(new AggregateParseException(commandContext, component));
                }
                return component.parser().parseFuture(commandContext, commandInput).thenApply(value -> {
                    if (value.parsedValue().isPresent()) {
                        CloudKey<?> key = CloudKey.of(component.name(), component.valueType());
                        aggregateParsingContext.store(key, value.parsedValue().get());
                    } else if (value.failure().isPresent()) {
                        return ArgumentParseResult.failure(new AggregateParseException(commandContext, "", component, value.failure().get()));
                    }
                    return value;
                });
            });
        }
        return future.thenCompose(result -> {
            if (result != null && result.failure().isPresent()) {
                return result.asFuture();
            }
            return this.mapper().map(commandContext, aggregateParsingContext);
        });
    }

    @Override
    default public @NonNull SuggestionProvider<C> suggestionProvider() {
        return new AggregateSuggestionProvider(this);
    }

    @Override
    default public @NonNull ArgumentParser<C, O> parser() {
        return this;
    }

    @API(status=API.Status.STABLE)
    public static final class AggregateParseException
    extends ParserException {
        private AggregateParseException(@NonNull CommandContext<?> context, @NonNull String input, @NonNull CommandComponent<?> component, @NonNull Throwable cause) {
            super(cause, AggregateParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_AGGREGATE_COMPONENT_FAILURE, CaptionVariable.of("input", input), CaptionVariable.of("component", component.name()), CaptionVariable.of("failure", cause.getMessage()));
        }

        private AggregateParseException(@NonNull CommandContext<?> context, @NonNull CommandComponent<?> component) {
            super(AggregateParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_AGGREGATE_MISSING_INPUT, CaptionVariable.of("component", component.name()));
        }
    }
}

