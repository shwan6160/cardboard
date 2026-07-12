/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.MonotonicNonNull
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.checkerframework.common.returnsreceiver.qual.This
 */
package com.bergerkiller.bukkit.common.dep.cloud.component;

import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.component.DefaultValue;
import com.bergerkiller.bukkit.common.dep.cloud.component.TypedCommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.component.preprocessor.ComponentPreprocessor;
import com.bergerkiller.bukkit.common.dep.cloud.component.preprocessor.PreprocessorHolder;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.description.Describable;
import com.bergerkiller.bukkit.common.dep.cloud.description.Description;
import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKey;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserParameters;
import com.bergerkiller.bukkit.common.dep.cloud.parser.flag.CommandFlagParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.LiteralParser;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.returnsreceiver.qual.This;

@API(status=API.Status.STABLE)
public class CommandComponent<C>
implements Comparable<CommandComponent<C>>,
PreprocessorHolder<C>,
Describable {
    private final String name;
    private final ArgumentParser<C, ?> parser;
    private final Description description;
    private final ComponentType componentType;
    private final DefaultValue<C, ?> defaultValue;
    private final TypeToken<?> valueType;
    private final SuggestionProvider<C> suggestionProvider;
    private final Collection<@NonNull ComponentPreprocessor<C>> componentPreprocessors;

    public static <C, T> @NonNull Builder<C, T> builder() {
        return new Builder();
    }

    public static <C, T> @NonNull Builder<C, T> builder(@NonNull String name, @NonNull ParserDescriptor<? super C, T> parserDescriptor) {
        return CommandComponent.builder().name(name).parser(parserDescriptor);
    }

    public static <C, T> @NonNull Builder<C, T> builder(@NonNull CloudKey<T> name, @NonNull ParserDescriptor<? super C, T> parserDescriptor) {
        return CommandComponent.builder().key(name).parser(parserDescriptor);
    }

    public static <C, T> @NonNull Builder<C, T> ofType(@NonNull Class<T> clazz, @NonNull String name) {
        return CommandComponent.builder().valueType(clazz).name(name);
    }

    CommandComponent(@NonNull String name, @NonNull ArgumentParser<C, ?> parser, @NonNull TypeToken<?> valueType, @NonNull Description description, @NonNull ComponentType componentType, @Nullable DefaultValue<C, ?> defaultValue, @NonNull SuggestionProvider<C> suggestionProvider, @NonNull Collection<@NonNull ComponentPreprocessor<C>> componentPreprocessors) {
        this.name = name;
        this.parser = parser;
        this.valueType = valueType;
        this.componentType = componentType;
        this.description = description;
        this.defaultValue = defaultValue;
        this.suggestionProvider = suggestionProvider;
        this.componentPreprocessors = new ArrayList<ComponentPreprocessor<C>>(componentPreprocessors);
    }

    public @NonNull TypeToken<?> valueType() {
        return this.valueType;
    }

    public @NonNull ArgumentParser<C, ?> parser() {
        return this.parser;
    }

    public final @NonNull String name() {
        return this.name;
    }

    public final @NonNull Collection<@NonNull String> aliases() {
        if (this.parser() instanceof LiteralParser) {
            return ((LiteralParser)this.parser()).aliases();
        }
        return Collections.emptyList();
    }

    public final @NonNull Collection<@NonNull String> alternativeAliases() {
        if (this.parser() instanceof LiteralParser) {
            return ((LiteralParser)this.parser()).alternativeAliases();
        }
        return Collections.emptyList();
    }

    @Override
    public final @NonNull Description description() {
        return this.description;
    }

    public final boolean required() {
        return this.componentType.required();
    }

    public final boolean optional() {
        return this.componentType.optional();
    }

    public final @NonNull ComponentType type() {
        return this.componentType;
    }

    public @Nullable DefaultValue<C, ?> defaultValue() {
        return this.defaultValue;
    }

    public final boolean hasDefaultValue() {
        return this.optional() && this.defaultValue() != null;
    }

    public final @NonNull SuggestionProvider<C> suggestionProvider() {
        return this.suggestionProvider;
    }

    public final @This @NonNull CommandComponent<C> addPreprocessor(@NonNull ComponentPreprocessor<C> preprocessor) {
        this.componentPreprocessors.add(Objects.requireNonNull(preprocessor, "preprocessor"));
        return this;
    }

    public final @NonNull ArgumentParseResult<Boolean> preprocess(@NonNull CommandContext<C> context, @NonNull CommandInput input) {
        for (ComponentPreprocessor<C> preprocessor : this.componentPreprocessors) {
            ArgumentParseResult<Boolean> result = preprocessor.preprocess(context, input);
            if (!result.failure().isPresent()) continue;
            return result;
        }
        return ArgumentParseResult.success(true);
    }

    @Override
    public final @NonNull Collection<@NonNull ComponentPreprocessor<C>> preprocessors() {
        return Collections.unmodifiableCollection(this.componentPreprocessors);
    }

    public final int hashCode() {
        return Objects.hash(this.name(), this.valueType());
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof CommandComponent) {
            CommandComponent that = (CommandComponent)o;
            return this.name().equals(that.name()) && this.valueType().equals(that.valueType());
        }
        return false;
    }

    public final @NonNull String toString() {
        return String.format("%s{name=%s,type=%s,valueType=%s", new Object[]{this.getClass().getSimpleName(), this.name(), this.type(), this.valueType().getType().getTypeName()});
    }

    @Override
    public final int compareTo(@NonNull CommandComponent<C> other) {
        if (this.type() == ComponentType.LITERAL) {
            if (other.type() == ComponentType.LITERAL) {
                return this.name().compareTo(other.name());
            }
            return -1;
        }
        if (other.type() == ComponentType.LITERAL) {
            return 1;
        }
        return 0;
    }

    @API(status=API.Status.STABLE)
    public static class Builder<C, T> {
        private CommandManager<C> commandManager;
        private String name;
        private ArgumentParser<C, T> parser;
        private Description description = Description.empty();
        private boolean required = true;
        private DefaultValue<C, ?> defaultValue;
        private TypeToken<T> valueType;
        private SuggestionProvider<C> suggestionProvider;
        private final Collection<@NonNull ComponentPreprocessor<C>> componentPreprocessors = new ArrayList<ComponentPreprocessor<C>>();

        protected Builder() {
        }

        public @This @NonNull Builder<C, T> commandManager(@Nullable CommandManager<C> commandManager) {
            this.commandManager = commandManager;
            return this;
        }

        public @This @NonNull Builder<C, T> key(@NonNull CloudKey<T> cloudKey) {
            return this.name(cloudKey.name()).valueType(cloudKey.type());
        }

        public @MonotonicNonNull String name() {
            return this.name;
        }

        public @This @NonNull Builder<C, T> name(@NonNull String name) {
            this.name = Objects.requireNonNull(name, "name");
            return this;
        }

        public @This @NonNull Builder<C, T> valueType(@NonNull TypeToken<T> valueType) {
            this.valueType = Objects.requireNonNull(valueType, "valueType");
            return this;
        }

        public @This @NonNull Builder<C, T> valueType(@NonNull Class<T> valueType) {
            return this.valueType(TypeToken.get(valueType));
        }

        public @MonotonicNonNull ParserDescriptor<C, T> parser() {
            if (this.valueType == null || this.parser == null) {
                return null;
            }
            return ParserDescriptor.of(this.parser, this.valueType);
        }

        public @This @NonNull Builder<C, T> parser(@NonNull ParserDescriptor<? super C, T> parserDescriptor) {
            return this.parser(parserDescriptor.parser()).valueType(parserDescriptor.valueType());
        }

        public @Nullable DefaultValue<C, T> defaultValue() {
            if (this.defaultValue == null) {
                return null;
            }
            return this.defaultValue;
        }

        public @This @NonNull Builder<C, T> defaultValue(@Nullable DefaultValue<? super C, T> defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public @This @NonNull Builder<C, T> required(boolean required) {
            this.required = required;
            return this;
        }

        public @This @NonNull Builder<C, T> required() {
            return this.required(true);
        }

        public @This @NonNull Builder<C, T> optional() {
            return this.required(false);
        }

        public @This @NonNull Builder<C, T> optional(@Nullable DefaultValue<? super C, T> defaultValue) {
            return this.optional().defaultValue(defaultValue);
        }

        public @MonotonicNonNull Description description() {
            return this.description;
        }

        public @This @NonNull Builder<C, T> description(@NonNull Description description) {
            this.description = Objects.requireNonNull(description, "description");
            return this;
        }

        public @MonotonicNonNull SuggestionProvider<C> suggestionProvider() {
            return this.suggestionProvider;
        }

        public @This @NonNull Builder<C, T> suggestionProvider(@Nullable SuggestionProvider<? super C> suggestionProvider) {
            this.suggestionProvider = suggestionProvider;
            return this;
        }

        public @This @NonNull Builder<C, T> preprocessor(@NonNull ComponentPreprocessor<? super C> preprocessor) {
            this.componentPreprocessors.add(Objects.requireNonNull(preprocessor, "preprocessor"));
            return this;
        }

        public @This @NonNull Builder<C, T> preprocessors(@NonNull Collection<ComponentPreprocessor<C>> preprocessors) {
            this.componentPreprocessors.addAll(preprocessors);
            return this;
        }

        public @This @NonNull Builder<C, T> parser(@NonNull ArgumentParser<? super C, T> parser) {
            this.parser = Objects.requireNonNull(parser, "parser");
            return this;
        }

        public @NonNull TypedCommandComponent<C, T> build() {
            ArgumentParser parser = null;
            if (this.parser != null) {
                parser = this.parser;
            } else if (this.commandManager != null) {
                parser = this.commandManager.parserRegistry().createParser(this.valueType, ParserParameters.empty()).orElse(null);
            }
            if (parser == null) {
                parser = (ctx, input) -> ArgumentParseResult.failure(new UnsupportedOperationException("No parser was specified"));
            }
            ComponentType componentType = this.parser instanceof LiteralParser ? ComponentType.LITERAL : (this.parser instanceof CommandFlagParser ? ComponentType.FLAG : (this.required ? ComponentType.REQUIRED_VARIABLE : ComponentType.OPTIONAL_VARIABLE));
            SuggestionProvider suggestionProvider = this.suggestionProvider == null ? parser.suggestionProvider() : this.suggestionProvider;
            return new TypedCommandComponent(Objects.requireNonNull(this.name, "name"), parser, Objects.requireNonNull(this.valueType, "valueType"), Objects.requireNonNull(this.description, "description"), componentType, this.defaultValue, suggestionProvider, Objects.requireNonNull(this.componentPreprocessors, "componentPreprocessors"));
        }
    }

    @API(status=API.Status.STABLE)
    public static enum ComponentType {
        LITERAL(true),
        REQUIRED_VARIABLE(true),
        OPTIONAL_VARIABLE(false),
        FLAG(false);

        private final boolean required;

        private ComponentType(boolean required) {
            this.required = required;
        }

        public boolean required() {
            return this.required;
        }

        public boolean optional() {
            return !this.required;
        }
    }
}

