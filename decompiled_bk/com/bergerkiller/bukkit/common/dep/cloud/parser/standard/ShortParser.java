/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser.standard;

import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.exception.parsing.NumberParseException;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.IntegerParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.NumberParser;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.Range;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.ShortRange;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class ShortParser<C>
extends NumberParser<C, Short, ShortRange>
implements BlockingSuggestionProvider.Strings<C> {
    @API(status=API.Status.STABLE)
    public static final short DEFAULT_MINIMUM = Short.MIN_VALUE;
    @API(status=API.Status.STABLE)
    public static final short DEFAULT_MAXIMUM = Short.MAX_VALUE;

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Short> shortParser() {
        return ShortParser.shortParser((short)Short.MIN_VALUE, (short)Short.MAX_VALUE);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Short> shortParser(short minValue) {
        return ParserDescriptor.of(new ShortParser<C>(minValue, Short.MAX_VALUE), Short.class);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Short> shortParser(short minValue, short maxValue) {
        return ParserDescriptor.of(new ShortParser<C>(minValue, maxValue), Short.class);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull CommandComponent.Builder<C, Short> shortComponent() {
        return CommandComponent.builder().parser(ShortParser.shortParser());
    }

    public ShortParser(short min, short max) {
        super(Range.shortRange(min, max));
    }

    @Override
    public @NonNull ArgumentParseResult<Short> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        if (!commandInput.isValidShort((ShortRange)this.range())) {
            return ArgumentParseResult.failure(new ShortParseException(commandInput.peekString(), this, commandContext));
        }
        return ArgumentParseResult.success(commandInput.readShort());
    }

    @Override
    public boolean hasMax() {
        return ((ShortRange)this.range()).maxShort() != Short.MAX_VALUE;
    }

    @Override
    public boolean hasMin() {
        return ((ShortRange)this.range()).minShort() != Short.MIN_VALUE;
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        return IntegerParser.getSuggestions(this.range(), input);
    }

    @API(status=API.Status.STABLE)
    public static final class ShortParseException
    extends NumberParseException {
        @API(status=API.Status.STABLE)
        public ShortParseException(@NonNull String input, @NonNull ShortParser<?> parser, @NonNull CommandContext<?> commandContext) {
            super(input, parser, commandContext);
        }

        @Override
        public @NonNull String numberType() {
            return "short";
        }
    }
}

