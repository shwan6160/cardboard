/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud;

import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.component.DefaultValue;
import com.bergerkiller.bukkit.common.dep.cloud.component.TypedCommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.description.CommandDescription;
import com.bergerkiller.bukkit.common.dep.cloud.description.Description;
import com.bergerkiller.bukkit.common.dep.cloud.execution.CommandExecutionHandler;
import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKey;
import com.bergerkiller.bukkit.common.dep.cloud.meta.CommandMeta;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.flag.CommandFlag;
import com.bergerkiller.bukkit.common.dep.cloud.parser.flag.CommandFlagParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.LiteralParser;
import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import com.bergerkiller.bukkit.common.dep.cloud.permission.PredicatePermission;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.type.tuple.Pair;
import com.bergerkiller.bukkit.common.dep.cloud.type.tuple.Triplet;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.STABLE)
public class Command<C> {
    private final List<@NonNull CommandComponent<C>> components;
    private final @Nullable CommandComponent<C> flagComponent;
    private final CommandExecutionHandler<C> commandExecutionHandler;
    private final Type senderType;
    private final Permission permission;
    private final CommandMeta commandMeta;
    private final CommandDescription commandDescription;

    @API(status=API.Status.INTERNAL)
    public Command(@NonNull List<@NonNull CommandComponent<C>> commandComponents, @NonNull CommandExecutionHandler<@NonNull C> commandExecutionHandler, @Nullable Type senderType, @NonNull Permission permission, @NonNull CommandMeta commandMeta, @NonNull CommandDescription commandDescription) {
        this.components = Objects.requireNonNull(commandComponents, "Command components may not be null");
        if (this.components.isEmpty()) {
            throw new IllegalArgumentException("At least one command component is required");
        }
        this.flagComponent = this.components.stream().filter(ca -> ca.type() == CommandComponent.ComponentType.FLAG).findFirst().orElse(null);
        boolean foundOptional = false;
        for (CommandComponent<C> component : this.components) {
            if (component.name().isEmpty()) {
                throw new IllegalArgumentException("Component names may not be empty");
            }
            if (foundOptional && component.required()) {
                throw new IllegalArgumentException(String.format("Command component '%s' cannot be placed after an optional argument", component.name()));
            }
            if (component.required()) continue;
            foundOptional = true;
        }
        this.commandExecutionHandler = commandExecutionHandler;
        this.senderType = senderType;
        this.permission = permission;
        this.commandMeta = commandMeta;
        this.commandDescription = commandDescription;
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull Builder<C> newBuilder(@NonNull String commandName, @NonNull CommandMeta commandMeta, @NonNull Description description, String ... aliases) {
        ArrayList commands = new ArrayList();
        ParserDescriptor staticParser = LiteralParser.literal(commandName, aliases);
        commands.add(CommandComponent.builder(commandName, staticParser).description(description).build());
        return new Builder(null, commandMeta, null, commands, CommandExecutionHandler.noOpCommandExecutionHandler(), Permission.empty(), Collections.emptyList(), CommandDescription.empty());
    }

    public static <C> @NonNull Builder<C> newBuilder(@NonNull String commandName, @NonNull CommandMeta commandMeta, String ... aliases) {
        ArrayList commands = new ArrayList();
        ParserDescriptor staticParser = LiteralParser.literal(commandName, aliases);
        commands.add(CommandComponent.builder().name(commandName).parser(staticParser).build());
        return new Builder(null, commandMeta, null, commands, CommandExecutionHandler.noOpCommandExecutionHandler(), Permission.empty(), Collections.emptyList(), CommandDescription.empty());
    }

    @API(status=API.Status.STABLE)
    public @NonNull List<CommandComponent<C>> components() {
        return new ArrayList<CommandComponent<C>>(this.components);
    }

    @API(status=API.Status.STABLE)
    public @NonNull CommandComponent<C> rootComponent() {
        return this.components.get(0);
    }

    @API(status=API.Status.EXPERIMENTAL)
    public @NonNull List<CommandComponent<C>> nonFlagArguments() {
        ArrayList<CommandComponent<C>> components = new ArrayList<CommandComponent<C>>(this.components);
        if (this.flagComponent() != null) {
            components.remove(this.flagComponent());
        }
        return components;
    }

    @API(status=API.Status.STABLE)
    public @Nullable CommandComponent<C> flagComponent() {
        return this.flagComponent;
    }

    @API(status=API.Status.STABLE)
    public @Nullable CommandFlagParser<@NonNull C> flagParser() {
        CommandComponent<C> flagComponent = this.flagComponent();
        if (flagComponent == null) {
            return null;
        }
        return (CommandFlagParser)flagComponent.parser();
    }

    @API(status=API.Status.STABLE)
    public @NonNull CommandExecutionHandler<@NonNull C> commandExecutionHandler() {
        return this.commandExecutionHandler;
    }

    @API(status=API.Status.STABLE)
    public @NonNull Optional<TypeToken<? extends C>> senderType() {
        if (this.senderType == null) {
            return Optional.empty();
        }
        return Optional.of(TypeToken.get(this.senderType));
    }

    @API(status=API.Status.STABLE)
    public @NonNull Permission commandPermission() {
        return this.permission;
    }

    @API(status=API.Status.STABLE)
    public @NonNull CommandMeta commandMeta() {
        return this.commandMeta;
    }

    @API(status=API.Status.STABLE)
    public @NonNull CommandDescription commandDescription() {
        return this.commandDescription;
    }

    public final String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (CommandComponent<C> component : this.components()) {
            stringBuilder.append(component.name()).append(' ');
        }
        String build = stringBuilder.toString();
        return build.substring(0, build.length() - 1);
    }

    @API(status=API.Status.STABLE)
    public static final class Builder<C> {
        private final CommandMeta commandMeta;
        private final List<CommandComponent<C>> commandComponents;
        private final CommandExecutionHandler<C> commandExecutionHandler;
        private final Type senderType;
        private final Permission permission;
        private final CommandManager<C> commandManager;
        private final Collection<CommandFlag<?>> flags;
        private final CommandDescription commandDescription;

        private Builder(@Nullable CommandManager<C> commandManager, @NonNull CommandMeta commandMeta, @Nullable Type senderType, @NonNull List<@NonNull CommandComponent<C>> commandComponents, @NonNull CommandExecutionHandler<@NonNull C> commandExecutionHandler, @NonNull Permission permission, @NonNull Collection<CommandFlag<?>> flags, @NonNull CommandDescription commandDescription) {
            this.commandManager = commandManager;
            this.senderType = senderType;
            this.commandComponents = Objects.requireNonNull(commandComponents, "Components may not be null");
            this.commandExecutionHandler = Objects.requireNonNull(commandExecutionHandler, "Execution handler may not be null");
            this.permission = Objects.requireNonNull(permission, "Permission may not be null");
            this.commandMeta = Objects.requireNonNull(commandMeta, "Meta may not be null");
            this.flags = Objects.requireNonNull(flags, "Flags may not be null");
            this.commandDescription = Objects.requireNonNull(commandDescription, "Command description may not be null");
        }

        @API(status=API.Status.STABLE)
        public @Nullable TypeToken<? extends C> senderType() {
            if (this.senderType == null) {
                return null;
            }
            return TypeToken.get(this.senderType);
        }

        @API(status=API.Status.STABLE)
        public @NonNull Permission commandPermission() {
            return this.permission;
        }

        @API(status=API.Status.STABLE)
        public @NonNull CommandMeta meta() {
            return this.commandMeta;
        }

        @API(status=API.Status.STABLE)
        public @NonNull Builder<@NonNull C> apply(@NonNull Applicable<@NonNull C> applicable) {
            return applicable.applyToCommandBuilder(this);
        }

        @API(status=API.Status.STABLE)
        public <V> @NonNull Builder<C> meta(@NonNull CloudKey<V> key, @NonNull V value) {
            CommandMeta commandMeta = CommandMeta.builder().with(this.commandMeta).with(key, value).build();
            return new Builder<C>(this.commandManager, commandMeta, this.senderType, this.commandComponents, this.commandExecutionHandler, this.permission, this.flags, this.commandDescription);
        }

        public @NonNull Builder<C> manager(@Nullable CommandManager<C> commandManager) {
            return new Builder<C>(commandManager, this.commandMeta, this.senderType, this.commandComponents, this.commandExecutionHandler, this.permission, this.flags, this.commandDescription);
        }

        @API(status=API.Status.STABLE)
        public @NonNull Builder<C> commandDescription(@NonNull CommandDescription commandDescription) {
            return new Builder<C>(this.commandManager, this.commandMeta, this.senderType, this.commandComponents, this.commandExecutionHandler, this.permission, this.flags, commandDescription);
        }

        public @NonNull CommandDescription commandDescription() {
            return this.commandDescription;
        }

        public @NonNull Builder<C> commandDescription(@NonNull Description commandDescription) {
            return this.commandDescription(CommandDescription.commandDescription(commandDescription));
        }

        public @NonNull Builder<C> commandDescription(@NonNull Description commandDescription, @NonNull Description verboseCommandDescription) {
            return this.commandDescription(CommandDescription.commandDescription(commandDescription, verboseCommandDescription));
        }

        public @NonNull Builder<C> literal(@NonNull String main, String ... aliases) {
            return this.required(main, LiteralParser.literal(main, aliases));
        }

        @API(status=API.Status.STABLE)
        public @NonNull Builder<C> literal(@NonNull String main, @NonNull Description description, String ... aliases) {
            return this.required(main, LiteralParser.literal(main, aliases), description);
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> required(@NonNull String name, @NonNull CommandComponent.Builder<? super C, T> builder) {
            return this.argument(builder.name(name).required());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> optional(@NonNull String name, @NonNull CommandComponent.Builder<? super C, T> builder) {
            return this.argument(builder.name(name).optional());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> required(@NonNull CommandComponent.Builder<? super C, T> builder) {
            return this.argument(builder.required());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> optional(@NonNull CommandComponent.Builder<? super C, T> builder) {
            return this.argument(builder.optional());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> required(@NonNull String name, @NonNull ParserDescriptor<? super C, T> parser) {
            return this.argument(CommandComponent.builder(name, parser).build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> required(@NonNull String name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull SuggestionProvider<? super C> suggestions) {
            return this.argument(CommandComponent.builder(name, parser).suggestionProvider(suggestions).build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> required(@NonNull CloudKey<T> name, @NonNull ParserDescriptor<? super C, T> parser) {
            return this.argument(CommandComponent.builder(name, parser).build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> required(@NonNull CloudKey<T> name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull SuggestionProvider<? super C> suggestions) {
            return this.argument(CommandComponent.builder(name, parser).suggestionProvider(suggestions).build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> required(@NonNull CloudKey<T> name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull Description description) {
            return this.argument(CommandComponent.builder(name, parser).description(description).build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> required(@NonNull CloudKey<T> name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull Description description, @NonNull SuggestionProvider<? super C> suggestions) {
            return this.argument(CommandComponent.builder(name, parser).description(description).suggestionProvider(suggestions).build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> required(@NonNull String name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull Description description) {
            return this.argument(CommandComponent.builder(name, parser).description(description).build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> required(@NonNull String name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull Description description, @NonNull SuggestionProvider<? super C> suggestions) {
            return this.argument(CommandComponent.builder(name, parser).description(description).suggestionProvider(suggestions).build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> optional(@NonNull String name, @NonNull ParserDescriptor<? super C, T> parser) {
            return this.argument(CommandComponent.builder(name, parser).optional().build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> optional(@NonNull String name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull SuggestionProvider<? super C> suggestions) {
            return this.argument(CommandComponent.builder(name, parser).optional().suggestionProvider(suggestions).build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> optional(@NonNull CloudKey<T> name, @NonNull ParserDescriptor<? super C, T> parser) {
            return this.argument(CommandComponent.builder(name, parser).optional().build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> optional(@NonNull CloudKey<T> name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull SuggestionProvider<? super C> suggestions) {
            return this.argument(CommandComponent.builder(name, parser).optional().suggestionProvider(suggestions).build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> optional(@NonNull String name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull Description description) {
            return this.argument(CommandComponent.builder(name, parser).description(description).optional().build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> optional(@NonNull String name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull Description description, @NonNull SuggestionProvider<? super C> suggestions) {
            return this.argument(CommandComponent.builder(name, parser).description(description).optional().suggestionProvider(suggestions).build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> optional(@NonNull CloudKey<T> name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull Description description) {
            return this.argument(CommandComponent.builder(name, parser).description(description).optional().build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> optional(@NonNull CloudKey<T> name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull Description description, @NonNull SuggestionProvider<? super C> suggestions) {
            return this.argument(CommandComponent.builder(name, parser).description(description).optional().suggestionProvider(suggestions).build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> optional(@NonNull String name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull DefaultValue<? super C, T> defaultValue) {
            return this.argument(CommandComponent.builder(name, parser).optional(defaultValue).build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> optional(@NonNull String name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull DefaultValue<? super C, T> defaultValue, @NonNull SuggestionProvider<? super C> suggestions) {
            return this.argument(CommandComponent.builder(name, parser).optional(defaultValue).suggestionProvider(suggestions).build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> optional(@NonNull CloudKey<T> name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull DefaultValue<? super C, T> defaultValue) {
            return this.argument(CommandComponent.builder(name, parser).optional(defaultValue).build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> optional(@NonNull CloudKey<T> name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull DefaultValue<? super C, T> defaultValue, @NonNull SuggestionProvider<? super C> suggestions) {
            return this.argument(CommandComponent.builder(name, parser).optional(defaultValue).suggestionProvider(suggestions).build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> optional(@NonNull String name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull DefaultValue<? super C, T> defaultValue, @NonNull Description description) {
            return this.argument(CommandComponent.builder(name, parser).optional(defaultValue).description(description).build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> optional(@NonNull String name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull DefaultValue<? super C, T> defaultValue, @NonNull Description description, @NonNull SuggestionProvider<? super C> suggestions) {
            return this.argument(CommandComponent.builder(name, parser).optional(defaultValue).description(description).suggestionProvider(suggestions).build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> optional(@NonNull CloudKey<T> name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull DefaultValue<? super C, T> defaultValue, @NonNull Description description) {
            return this.argument(CommandComponent.builder(name, parser).optional(defaultValue).description(description).build());
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> optional(@NonNull CloudKey<T> name, @NonNull ParserDescriptor<? super C, T> parser, @NonNull DefaultValue<? super C, T> defaultValue, @NonNull Description description, @NonNull SuggestionProvider<? super C> suggestions) {
            return this.argument(CommandComponent.builder(name, parser).optional(defaultValue).description(description).suggestionProvider(suggestions).build());
        }

        @API(status=API.Status.STABLE)
        public @NonNull Builder<C> argument(@NonNull CommandComponent<? super C> argument) {
            ArrayList<CommandComponent<C>> commandComponents = new ArrayList<CommandComponent<C>>(this.commandComponents);
            commandComponents.add(argument);
            return new Builder<C>(this.commandManager, this.commandMeta, this.senderType, commandComponents, this.commandExecutionHandler, this.permission, this.flags, this.commandDescription);
        }

        @API(status=API.Status.STABLE)
        public <T> @NonNull Builder<C> argument(CommandComponent.Builder<? super C, T> builder) {
            if (this.commandManager != null) {
                return this.argument(builder.commandManager(this.commandManager).build());
            }
            return this.argument(builder.build());
        }

        @API(status=API.Status.STABLE)
        public <U, V> @NonNull Builder<C> requiredArgumentPair(@NonNull String name, @NonNull String firstName, @NonNull ParserDescriptor<C, U> firstParser, @NonNull String secondName, @NonNull ParserDescriptor<C, V> secondParser, @NonNull Description description) {
            if (this.commandManager == null) {
                throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
            }
            return this.required(name, AggregateParser.pairBuilder(firstName, firstParser, secondName, secondParser).build(), description);
        }

        @API(status=API.Status.STABLE)
        public <U, V> @NonNull Builder<C> requiredArgumentPair(@NonNull CloudKey<Pair<U, V>> name, @NonNull String firstName, @NonNull ParserDescriptor<C, U> firstParser, @NonNull String secondName, @NonNull ParserDescriptor<C, V> secondParser, @NonNull Description description) {
            if (this.commandManager == null) {
                throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
            }
            return this.required(name, AggregateParser.pairBuilder(firstName, firstParser, secondName, secondParser).build(), description);
        }

        @API(status=API.Status.STABLE)
        public <U, V> @NonNull Builder<C> optionalArgumentPair(@NonNull String name, @NonNull String firstName, @NonNull ParserDescriptor<C, U> firstParser, @NonNull String secondName, @NonNull ParserDescriptor<C, V> secondParser, @NonNull Description description) {
            if (this.commandManager == null) {
                throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
            }
            return this.optional(name, AggregateParser.pairBuilder(firstName, firstParser, secondName, secondParser).build(), description);
        }

        @API(status=API.Status.STABLE)
        public <U, V> @NonNull Builder<C> optionalArgumentPair(@NonNull CloudKey<Pair<U, V>> name, @NonNull String firstName, @NonNull ParserDescriptor<C, U> firstParser, @NonNull String secondName, @NonNull ParserDescriptor<C, V> secondParser, @NonNull Description description) {
            if (this.commandManager == null) {
                throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
            }
            return this.optional(name, AggregateParser.pairBuilder(firstName, firstParser, secondName, secondParser).build(), description);
        }

        @API(status=API.Status.STABLE)
        public <U, V, O> @NonNull Builder<C> requiredArgumentPair(@NonNull String name, @NonNull TypeToken<O> outputType, @NonNull String firstName, @NonNull ParserDescriptor<C, U> firstParser, @NonNull String secondName, @NonNull ParserDescriptor<C, V> secondParser,  @NonNull AggregateParserPairBuilder.Mapper<C, U, V, O> mapper, @NonNull Description description) {
            if (this.commandManager == null) {
                throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
            }
            return this.required(name, AggregateParser.pairBuilder(firstName, firstParser, secondName, secondParser).withMapper(outputType, mapper).build(), description);
        }

        @API(status=API.Status.STABLE)
        public <U, V, O> @NonNull Builder<C> requiredArgumentPair(@NonNull CloudKey<O> name, @NonNull TypeToken<O> outputType, @NonNull String firstName, @NonNull ParserDescriptor<C, U> firstParser, @NonNull String secondName, @NonNull ParserDescriptor<C, V> secondParser,  @NonNull AggregateParserPairBuilder.Mapper<C, U, V, O> mapper, @NonNull Description description) {
            if (this.commandManager == null) {
                throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
            }
            return this.required(name, AggregateParser.pairBuilder(firstName, firstParser, secondName, secondParser).withMapper(outputType, mapper).build(), description);
        }

        @API(status=API.Status.STABLE)
        public <U, V, O> @NonNull Builder<C> optionalArgumentPair(@NonNull String name, @NonNull TypeToken<O> outputType, @NonNull String firstName, @NonNull ParserDescriptor<C, U> firstParser, @NonNull String secondName, @NonNull ParserDescriptor<C, V> secondParser,  @NonNull AggregateParserPairBuilder.Mapper<C, U, V, O> mapper, @NonNull Description description) {
            if (this.commandManager == null) {
                throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
            }
            return this.optional(name, AggregateParser.pairBuilder(firstName, firstParser, secondName, secondParser).withMapper(outputType, mapper).build(), description);
        }

        @API(status=API.Status.STABLE)
        public <U, V, O> @NonNull Builder<C> optionalArgumentPair(@NonNull CloudKey<O> name, @NonNull TypeToken<O> outputType, @NonNull String firstName, @NonNull ParserDescriptor<C, U> firstParser, @NonNull String secondName, @NonNull ParserDescriptor<C, V> secondParser,  @NonNull AggregateParserPairBuilder.Mapper<C, U, V, O> mapper, @NonNull Description description) {
            if (this.commandManager == null) {
                throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
            }
            return this.optional(name, AggregateParser.pairBuilder(firstName, firstParser, secondName, secondParser).withMapper(outputType, mapper).build(), description);
        }

        @API(status=API.Status.STABLE)
        public <U, V, W> @NonNull Builder<C> requiredArgumentTriplet(@NonNull String name, @NonNull String firstName, @NonNull ParserDescriptor<C, U> firstParser, @NonNull String secondName, @NonNull ParserDescriptor<C, V> secondParser, @NonNull String thirdName, @NonNull ParserDescriptor<C, W> thirdParser, @NonNull Description description) {
            if (this.commandManager == null) {
                throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
            }
            return this.required(name, AggregateParser.tripletBuilder(firstName, firstParser, secondName, secondParser, thirdName, thirdParser).build(), description);
        }

        @API(status=API.Status.STABLE)
        public <U, V, W> @NonNull Builder<C> requiredArgumentTriplet(@NonNull CloudKey<Triplet<U, V, W>> name, @NonNull String firstName, @NonNull ParserDescriptor<C, U> firstParser, @NonNull String secondName, @NonNull ParserDescriptor<C, V> secondParser, @NonNull String thirdName, @NonNull ParserDescriptor<C, W> thirdParser, @NonNull Description description) {
            if (this.commandManager == null) {
                throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
            }
            return this.required(name, AggregateParser.tripletBuilder(firstName, firstParser, secondName, secondParser, thirdName, thirdParser).build(), description);
        }

        @API(status=API.Status.STABLE)
        public <U, V, W> @NonNull Builder<C> optionalArgumentTriplet(@NonNull String name, @NonNull String firstName, @NonNull ParserDescriptor<C, U> firstParser, @NonNull String secondName, @NonNull ParserDescriptor<C, V> secondParser, @NonNull String thirdName, @NonNull ParserDescriptor<C, W> thirdParser, @NonNull Description description) {
            if (this.commandManager == null) {
                throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
            }
            return this.optional(name, AggregateParser.tripletBuilder(firstName, firstParser, secondName, secondParser, thirdName, thirdParser).build(), description);
        }

        @API(status=API.Status.STABLE)
        public <U, V, W> @NonNull Builder<C> optionalArgumentTriplet(@NonNull CloudKey<Triplet<U, V, W>> name, @NonNull String firstName, @NonNull ParserDescriptor<C, U> firstParser, @NonNull String secondName, @NonNull ParserDescriptor<C, V> secondParser, @NonNull String thirdName, @NonNull ParserDescriptor<C, W> thirdParser, @NonNull Description description) {
            if (this.commandManager == null) {
                throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
            }
            return this.optional(name, AggregateParser.tripletBuilder(firstName, firstParser, secondName, secondParser, thirdName, thirdParser).build(), description);
        }

        @API(status=API.Status.STABLE)
        public <U, V, W, O> @NonNull Builder<C> requiredArgumentTriplet(@NonNull String name, @NonNull TypeToken<O> outputType, @NonNull String firstName, @NonNull ParserDescriptor<C, U> firstParser, @NonNull String secondName, @NonNull ParserDescriptor<C, V> secondParser, @NonNull String thirdName, @NonNull ParserDescriptor<C, W> thirdParser,  @NonNull AggregateParserTripletBuilder.Mapper<C, U, V, W, O> mapper, @NonNull Description description) {
            if (this.commandManager == null) {
                throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
            }
            return this.required(name, AggregateParser.tripletBuilder(firstName, firstParser, secondName, secondParser, thirdName, thirdParser).withMapper(outputType, mapper).build(), description);
        }

        @API(status=API.Status.STABLE)
        public <U, V, W, O> @NonNull Builder<C> requiredArgumentTriplet(@NonNull CloudKey<O> name, @NonNull TypeToken<O> outputType, @NonNull String firstName, @NonNull ParserDescriptor<C, U> firstParser, @NonNull String secondName, @NonNull ParserDescriptor<C, V> secondParser, @NonNull String thirdName, @NonNull ParserDescriptor<C, W> thirdParser,  @NonNull AggregateParserTripletBuilder.Mapper<C, U, V, W, O> mapper, @NonNull Description description) {
            if (this.commandManager == null) {
                throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
            }
            return this.required(name, AggregateParser.tripletBuilder(firstName, firstParser, secondName, secondParser, thirdName, thirdParser).withMapper(outputType, mapper).build(), description);
        }

        @API(status=API.Status.STABLE)
        public <U, V, W, O> @NonNull Builder<C> optionalArgumentTriplet(@NonNull String name, @NonNull TypeToken<O> outputType, @NonNull String firstName, @NonNull ParserDescriptor<C, U> firstParser, @NonNull String secondName, @NonNull ParserDescriptor<C, V> secondParser, @NonNull String thirdName, @NonNull ParserDescriptor<C, W> thirdParser,  @NonNull AggregateParserTripletBuilder.Mapper<C, U, V, W, O> mapper, @NonNull Description description) {
            if (this.commandManager == null) {
                throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
            }
            return this.optional(name, AggregateParser.tripletBuilder(firstName, firstParser, secondName, secondParser, thirdName, thirdParser).withMapper(outputType, mapper).build(), description);
        }

        @API(status=API.Status.STABLE)
        public <U, V, W, O> @NonNull Builder<C> optionalArgumentTriplet(@NonNull CloudKey<O> name, @NonNull TypeToken<O> outputType, @NonNull String firstName, @NonNull ParserDescriptor<C, U> firstParser, @NonNull String secondName, @NonNull ParserDescriptor<C, V> secondParser, @NonNull String thirdName, @NonNull ParserDescriptor<C, W> thirdParser,  @NonNull AggregateParserTripletBuilder.Mapper<C, U, V, W, O> mapper, @NonNull Description description) {
            if (this.commandManager == null) {
                throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
            }
            return this.optional(name, AggregateParser.tripletBuilder(firstName, firstParser, secondName, secondParser, thirdName, thirdParser).withMapper(outputType, mapper).build(), description);
        }

        public @NonNull Builder<C> handler(@NonNull CommandExecutionHandler<C> commandExecutionHandler) {
            return new Builder<C>(this.commandManager, this.commandMeta, this.senderType, this.commandComponents, commandExecutionHandler, this.permission, this.flags, this.commandDescription);
        }

        public @NonNull Builder<C> futureHandler(@NonNull CommandExecutionHandler.FutureCommandExecutionHandler<C> commandExecutionHandler) {
            return this.handler(commandExecutionHandler);
        }

        @API(status=API.Status.STABLE)
        public @NonNull CommandExecutionHandler<C> handler() {
            return this.commandExecutionHandler;
        }

        @API(status=API.Status.STABLE)
        public @NonNull Builder<C> prependHandler(@NonNull CommandExecutionHandler<C> handler) {
            return this.handler(CommandExecutionHandler.delegatingExecutionHandler(Arrays.asList(handler, this.handler())));
        }

        @API(status=API.Status.STABLE)
        public @NonNull Builder<C> appendHandler(@NonNull CommandExecutionHandler<C> handler) {
            return this.handler(CommandExecutionHandler.delegatingExecutionHandler(Arrays.asList(this.handler(), handler)));
        }

        public <N extends C> @NonNull Builder<N> senderType(@NonNull Class<? extends N> senderType) {
            return this.senderType(TypeToken.get(senderType));
        }

        public <N extends C> @NonNull Builder<N> senderType(@NonNull TypeToken<? extends N> senderType) {
            return new Builder<C>(this.commandManager, this.commandMeta, senderType.getType(), this.commandComponents, this.commandExecutionHandler, this.permission, this.flags, this.commandDescription);
        }

        public @NonNull Builder<C> permission(@NonNull Permission permission) {
            return new Builder<C>(this.commandManager, this.commandMeta, this.senderType, this.commandComponents, this.commandExecutionHandler, permission, this.flags, this.commandDescription);
        }

        public @NonNull Builder<C> permission(@NonNull PredicatePermission<C> permission) {
            return new Builder<C>(this.commandManager, this.commandMeta, this.senderType, this.commandComponents, this.commandExecutionHandler, permission, this.flags, this.commandDescription);
        }

        public @NonNull Builder<C> permission(@NonNull String permission) {
            return new Builder<C>(this.commandManager, this.commandMeta, this.senderType, this.commandComponents, this.commandExecutionHandler, Permission.of(permission), this.flags, this.commandDescription);
        }

        public <N extends C> @NonNull Builder<N> proxies(@NonNull Command<N> command) {
            Builder<N> builder = command.senderType().isPresent() ? this.senderType(command.senderType().get()) : this;
            for (CommandComponent<N> component : command.components()) {
                if (component.type() == CommandComponent.ComponentType.LITERAL) continue;
                builder = builder.argument(component);
            }
            if (this.permission.permissionString().isEmpty()) {
                builder = builder.permission(command.commandPermission());
            }
            return builder.handler(((Command)command).commandExecutionHandler);
        }

        public <T> @NonNull Builder<C> flag(@NonNull CommandFlag<T> flag) {
            ArrayList flags = new ArrayList(this.flags);
            flags.add(flag);
            return new Builder<C>(this.commandManager, this.commandMeta, this.senderType, this.commandComponents, this.commandExecutionHandler, this.permission, Collections.unmodifiableList(flags), this.commandDescription);
        }

        public <T> @NonNull Builder<C> flag(@NonNull CommandFlag.Builder<C, T> builder) {
            return this.flag(builder.build());
        }

        public @NonNull Command<C> build() {
            ArrayList<CommandComponent<C>> commandComponents = new ArrayList<CommandComponent<C>>(this.commandComponents);
            if (!this.flags.isEmpty()) {
                CommandFlagParser flagParser = new CommandFlagParser(this.flags);
                TypedCommandComponent flagComponent = CommandComponent.builder().name("flags").parser(flagParser).valueType(Object.class).description(Description.of("Command flags")).build();
                commandComponents.add(flagComponent);
            }
            return new Command<C>(Collections.unmodifiableList(commandComponents), this.commandExecutionHandler, this.senderType, this.permission, this.commandMeta, this.commandDescription);
        }

        @API(status=API.Status.STABLE)
        @FunctionalInterface
        public static interface Applicable<C> {
            @API(status=API.Status.STABLE)
            public @NonNull Builder<C> applyToCommandBuilder(@NonNull Builder<C> var1);
        }
    }
}

