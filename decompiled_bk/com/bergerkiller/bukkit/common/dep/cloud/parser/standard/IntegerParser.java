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
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.NumberParser;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.IntRange;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.Range;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class IntegerParser<C>
extends NumberParser<C, Integer, IntRange>
implements BlockingSuggestionProvider.Strings<C> {
    @API(status=API.Status.STABLE)
    public static final int DEFAULT_MINIMUM = Integer.MIN_VALUE;
    @API(status=API.Status.STABLE)
    public static final int DEFAULT_MAXIMUM = Integer.MAX_VALUE;
    private static final int MAX_SUGGESTIONS_INCREMENT = 10;
    private static final int NUMBER_SHIFT_MULTIPLIER = 10;

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Integer> integerParser() {
        return IntegerParser.integerParser(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Integer> integerParser(int minValue) {
        return ParserDescriptor.of(new IntegerParser<C>(minValue, Integer.MAX_VALUE), Integer.class);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Integer> integerParser(int minValue, int maxValue) {
        return ParserDescriptor.of(new IntegerParser<C>(minValue, maxValue), Integer.class);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull CommandComponent.Builder<C, Integer> integerComponent() {
        return CommandComponent.builder().parser(IntegerParser.integerParser());
    }

    public IntegerParser(int min, int max) {
        super(Range.intRange(min, max));
    }

    public static @NonNull List<@NonNull String> getSuggestions(@NonNull Range<? extends Number> range, @NonNull CommandInput input) {
        TreeSet<Long> numbers = new TreeSet<Long>();
        String token = input.peekString();
        try {
            long inputNum = Long.parseLong(token.equals("-") ? "-0" : (token.isEmpty() ? "0" : token));
            long inputNumAbsolute = Math.abs(inputNum);
            long min = range.min().longValue();
            long max = range.max().longValue();
            numbers.add(inputNumAbsolute);
            for (int i = 0; i < 10 && inputNum * 10L + (long)i <= max; ++i) {
                numbers.add(inputNumAbsolute * 10L + (long)i);
            }
            LinkedList<String> suggestions = new LinkedList<String>();
            Iterator iterator = numbers.iterator();
            while (iterator.hasNext()) {
                long number = (Long)iterator.next();
                if (token.startsWith("-")) {
                    number = -number;
                }
                if (number < min || number > max) continue;
                suggestions.add(String.valueOf(number));
            }
            return suggestions;
        }
        catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    @Override
    public @NonNull ArgumentParseResult<Integer> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        if (!commandInput.isValidInteger((IntRange)this.range())) {
            return ArgumentParseResult.failure(new IntegerParseException(commandInput.peekString(), this, commandContext));
        }
        return ArgumentParseResult.success(commandInput.readInteger());
    }

    @Override
    public boolean hasMax() {
        return ((IntRange)this.range()).maxInt() != Integer.MAX_VALUE;
    }

    @Override
    public boolean hasMin() {
        return ((IntRange)this.range()).minInt() != Integer.MIN_VALUE;
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        return IntegerParser.getSuggestions(this.range(), input);
    }

    @API(status=API.Status.STABLE)
    public static final class IntegerParseException
    extends NumberParseException {
        @API(status=API.Status.STABLE)
        public IntegerParseException(@NonNull String input, @NonNull IntegerParser<?> parser, @NonNull CommandContext<?> commandContext) {
            super(input, parser, commandContext);
        }

        @Override
        public @NonNull String numberType() {
            return "integer";
        }
    }
}

