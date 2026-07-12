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
import com.bergerkiller.bukkit.common.dep.cloud.type.range.ByteRange;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.Range;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class ByteParser<C>
extends NumberParser<C, Byte, ByteRange>
implements BlockingSuggestionProvider.Strings<C> {
    @API(status=API.Status.STABLE)
    public static final byte DEFAULT_MINIMUM = -128;
    @API(status=API.Status.STABLE)
    public static final byte DEFAULT_MAXIMUM = 127;

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Byte> byteParser() {
        return ByteParser.byteParser((byte)-128, (byte)127);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Byte> byteParser(byte minValue) {
        return ParserDescriptor.of(new ByteParser<C>(minValue, 127), Byte.class);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Byte> byteParser(byte minValue, byte maxValue) {
        return ParserDescriptor.of(new ByteParser<C>(minValue, maxValue), Byte.class);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull CommandComponent.Builder<C, Byte> byteComponent() {
        return CommandComponent.builder().parser(ByteParser.byteParser());
    }

    public ByteParser(byte min, byte max) {
        super(Range.byteRange(min, max));
    }

    @Override
    public @NonNull ArgumentParseResult<Byte> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        if (!commandInput.isValidByte((ByteRange)this.range())) {
            return ArgumentParseResult.failure(new ByteParseException(commandInput.peekString(), this, commandContext));
        }
        return ArgumentParseResult.success(commandInput.readByte());
    }

    @Override
    public boolean hasMax() {
        return ((ByteRange)this.range()).maxByte() != 127;
    }

    @Override
    public boolean hasMin() {
        return ((ByteRange)this.range()).minByte() != -128;
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        return IntegerParser.getSuggestions(this.range(), input);
    }

    @API(status=API.Status.STABLE)
    public static final class ByteParseException
    extends NumberParseException {
        @API(status=API.Status.STABLE)
        public ByteParseException(@NonNull String input, @NonNull ByteParser<?> parser, @NonNull CommandContext<?> commandContext) {
            super(input, parser, commandContext);
        }

        @Override
        public @NonNull String numberType() {
            return "byte";
        }
    }
}

