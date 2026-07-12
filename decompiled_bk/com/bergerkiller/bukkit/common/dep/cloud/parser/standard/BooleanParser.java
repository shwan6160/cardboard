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
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class BooleanParser<C>
implements ArgumentParser<C, Boolean>,
BlockingSuggestionProvider.Strings<C> {
    private static final List<String> STRICT_LOWER = CommandInput.BOOLEAN_STRICT.stream().map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
    private static final List<String> LIBERAL_LOWER = CommandInput.BOOLEAN_LIBERAL.stream().map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
    private final boolean liberal;

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Boolean> booleanParser() {
        return BooleanParser.booleanParser(false);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Boolean> booleanParser(boolean liberal) {
        return ParserDescriptor.of(new BooleanParser<C>(liberal), Boolean.class);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull CommandComponent.Builder<C, Boolean> booleanComponent() {
        return CommandComponent.builder().parser(BooleanParser.booleanParser());
    }

    public BooleanParser(boolean liberal) {
        this.liberal = liberal;
    }

    @Override
    public @NonNull ArgumentParseResult<Boolean> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        if (!commandInput.isValidBoolean(this.liberal)) {
            return ArgumentParseResult.failure(new BooleanParseException(commandInput.peekString(), this.liberal, commandContext));
        }
        return ArgumentParseResult.success(commandInput.readBoolean());
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        if (!this.liberal) {
            return STRICT_LOWER;
        }
        return LIBERAL_LOWER;
    }

    @API(status=API.Status.STABLE)
    public static final class BooleanParseException
    extends ParserException {
        private final String input;
        private final boolean liberal;

        public BooleanParseException(@NonNull String input, boolean liberal, @NonNull CommandContext<?> context) {
            super(BooleanParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_BOOLEAN, CaptionVariable.of("input", input));
            this.input = input;
            this.liberal = liberal;
        }

        public @NonNull String input() {
            return this.input;
        }

        public boolean liberal() {
            return this.liberal;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            BooleanParseException that = (BooleanParseException)o;
            return this.liberal == that.liberal && this.input.equals(that.input);
        }

        public int hashCode() {
            return Objects.hash(this.input, this.liberal);
        }
    }
}

