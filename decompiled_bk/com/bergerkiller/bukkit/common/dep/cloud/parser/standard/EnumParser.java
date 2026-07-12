/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser.standard;

import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.caption.StandardCaptionKeys;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.exception.parsing.ParserException;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class EnumParser<C, E extends Enum<E>>
implements ArgumentParser<C, E>,
BlockingSuggestionProvider.Strings<C> {
    private final Class<E> enumClass;
    private final EnumSet<E> acceptedValues;

    @API(status=API.Status.STABLE)
    public static <C, E extends Enum<E>> @NonNull ParserDescriptor<C, E> enumParser(@NonNull Class<E> enumClass) {
        return ParserDescriptor.of(new EnumParser<C, E>(enumClass), enumClass);
    }

    @API(status=API.Status.STABLE)
    public static <C, E extends Enum<E>> @NonNull CommandComponent.Builder<C, E> enumComponent(@NonNull Class<E> enumClass) {
        return CommandComponent.builder().parser(EnumParser.enumParser(enumClass));
    }

    public EnumParser(@NonNull Class<E> enumClass) {
        this.enumClass = enumClass;
        this.acceptedValues = EnumSet.allOf(enumClass);
    }

    public @NonNull Class<E> enumClass() {
        return this.enumClass;
    }

    public @NonNull Collection<@NonNull E> acceptedValues() {
        return Collections.unmodifiableSet(this.acceptedValues);
    }

    @Override
    public @NonNull ArgumentParseResult<E> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        String input = commandInput.readString();
        for (Enum value : this.acceptedValues) {
            if (!value.name().equalsIgnoreCase(input)) continue;
            return ArgumentParseResult.success(value);
        }
        return ArgumentParseResult.failure(new EnumParseException(input, this.enumClass, commandContext));
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        return EnumSet.allOf(this.enumClass).stream().map(e -> e.name().toLowerCase(Locale.ROOT)).collect(Collectors.toList());
    }

    @API(status=API.Status.STABLE)
    public static final class EnumParseException
    extends ParserException {
        private final String input;
        private final Class<? extends Enum<?>> enumClass;

        public EnumParseException(@NonNull String input, @NonNull Class<? extends Enum<?>> enumClass, @NonNull CommandContext<?> context) {
            super(EnumParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_ENUM, CaptionVariable.of("input", input), CaptionVariable.of("acceptableValues", EnumParseException.join(enumClass)));
            this.input = input;
            this.enumClass = enumClass;
        }

        private static @NonNull String join(@NonNull Class<? extends Enum> clazz) {
            EnumSet<? extends Enum> enumSet = EnumSet.allOf(clazz);
            return enumSet.stream().map(e -> e.toString().toLowerCase(Locale.ROOT)).collect(Collectors.joining(", "));
        }

        public @NonNull String input() {
            return this.input;
        }

        public @NonNull Class<? extends Enum<?>> enumClass() {
            return this.enumClass;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            EnumParseException that = (EnumParseException)o;
            return this.input.equals(that.input) && this.enumClass.equals(that.enumClass);
        }

        public int hashCode() {
            return Objects.hash(this.input, this.enumClass);
        }
    }
}

