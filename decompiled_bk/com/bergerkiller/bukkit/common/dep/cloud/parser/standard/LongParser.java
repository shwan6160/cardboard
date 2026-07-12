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
import com.bergerkiller.bukkit.common.dep.cloud.type.range.LongRange;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.Range;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class LongParser<C>
extends NumberParser<C, Long, LongRange>
implements BlockingSuggestionProvider.Strings<C> {
    @API(status=API.Status.STABLE)
    public static final long DEFAULT_MINIMUM = Long.MIN_VALUE;
    @API(status=API.Status.STABLE)
    public static final long DEFAULT_MAXIMUM = Long.MAX_VALUE;

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Long> longParser() {
        return LongParser.longParser(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Long> longParser(long minValue) {
        return ParserDescriptor.of(new LongParser<C>(minValue, Long.MAX_VALUE), Long.class);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Long> longParser(long minValue, long maxValue) {
        return ParserDescriptor.of(new LongParser<C>(minValue, maxValue), Long.class);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull CommandComponent.Builder<C, Long> longComponent() {
        return CommandComponent.builder().parser(LongParser.longParser());
    }

    public LongParser(long min, long max) {
        super(Range.longRange(min, max));
    }

    @Override
    public @NonNull ArgumentParseResult<Long> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        if (!commandInput.isValidLong((LongRange)this.range())) {
            return ArgumentParseResult.failure(new LongParseException(commandInput.peekString(), this, commandContext));
        }
        return ArgumentParseResult.success(commandInput.readLong());
    }

    @Override
    public boolean hasMax() {
        return ((LongRange)this.range()).maxLong() != Long.MAX_VALUE;
    }

    @Override
    public boolean hasMin() {
        return ((LongRange)this.range()).minLong() != Long.MIN_VALUE;
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        return IntegerParser.getSuggestions(this.range(), input);
    }

    @API(status=API.Status.STABLE)
    public static final class LongParseException
    extends NumberParseException {
        @API(status=API.Status.STABLE)
        public LongParseException(@NonNull String input, @NonNull LongParser<?> parser, @NonNull CommandContext<?> commandContext) {
            super(input, parser, commandContext);
        }

        @Override
        public @NonNull String numberType() {
            return "long";
        }
    }
}

