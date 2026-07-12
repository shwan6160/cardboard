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
import com.bergerkiller.bukkit.common.dep.cloud.type.range.FloatRange;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.Range;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class FloatParser<C>
extends NumberParser<C, Float, FloatRange> {
    @API(status=API.Status.STABLE)
    public static final float DEFAULT_MINIMUM = Float.NEGATIVE_INFINITY;
    @API(status=API.Status.STABLE)
    public static final float DEFAULT_MAXIMUM = Float.POSITIVE_INFINITY;

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Float> floatParser() {
        return FloatParser.floatParser(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Float> floatParser(float minValue) {
        return ParserDescriptor.of(new FloatParser<C>(minValue, Float.POSITIVE_INFINITY), Float.class);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Float> floatParser(float minValue, float maxValue) {
        return ParserDescriptor.of(new FloatParser<C>(minValue, maxValue), Float.class);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull CommandComponent.Builder<C, Float> floatComponent() {
        return CommandComponent.builder().parser(FloatParser.floatParser());
    }

    public FloatParser(float min, float max) {
        super(Range.floatRange(min, max));
    }

    @Override
    public @NonNull ArgumentParseResult<Float> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        if (!commandInput.isValidFloat((FloatRange)this.range())) {
            return ArgumentParseResult.failure(new FloatParseException(commandInput.peekString(), this, commandContext));
        }
        return ArgumentParseResult.success(Float.valueOf(commandInput.readFloat()));
    }

    @Override
    public boolean hasMax() {
        return ((FloatRange)this.range()).maxFloat() != Float.POSITIVE_INFINITY;
    }

    @Override
    public boolean hasMin() {
        return ((FloatRange)this.range()).minFloat() != Float.NEGATIVE_INFINITY;
    }

    @API(status=API.Status.STABLE)
    public static final class FloatParseException
    extends NumberParseException {
        @API(status=API.Status.STABLE)
        public FloatParseException(@NonNull String input, @NonNull FloatParser<?> parser, @NonNull CommandContext<?> commandContext) {
            super(input, parser, commandContext);
        }

        @Override
        public @NonNull String numberType() {
            return "float";
        }
    }
}

