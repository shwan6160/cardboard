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
import com.bergerkiller.bukkit.common.dep.cloud.type.range.DoubleRange;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.Range;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class DoubleParser<C>
extends NumberParser<C, Double, DoubleRange> {
    @API(status=API.Status.STABLE)
    public static final double DEFAULT_MINIMUM = Double.NEGATIVE_INFINITY;
    @API(status=API.Status.STABLE)
    public static final double DEFAULT_MAXIMUM = Double.POSITIVE_INFINITY;

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Double> doubleParser() {
        return DoubleParser.doubleParser(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Double> doubleParser(double minValue) {
        return ParserDescriptor.of(new DoubleParser<C>(minValue, Double.POSITIVE_INFINITY), Double.class);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Double> doubleParser(double minValue, double maxValue) {
        return ParserDescriptor.of(new DoubleParser<C>(minValue, maxValue), Double.class);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull CommandComponent.Builder<C, Double> doubleComponent() {
        return CommandComponent.builder().parser(DoubleParser.doubleParser());
    }

    public DoubleParser(double min, double max) {
        super(Range.doubleRange(min, max));
    }

    @Override
    public @NonNull ArgumentParseResult<Double> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        if (!commandInput.isValidDouble((DoubleRange)this.range())) {
            return ArgumentParseResult.failure(new DoubleParseException(commandInput.peekString(), this, commandContext));
        }
        return ArgumentParseResult.success(commandInput.readDouble());
    }

    @Override
    public boolean hasMax() {
        return ((DoubleRange)this.range()).maxDouble() != Double.POSITIVE_INFINITY;
    }

    @Override
    public boolean hasMin() {
        return ((DoubleRange)this.range()).minDouble() != Double.NEGATIVE_INFINITY;
    }

    @API(status=API.Status.STABLE)
    public static final class DoubleParseException
    extends NumberParseException {
        @API(status=API.Status.STABLE)
        public DoubleParseException(@NonNull String input, @NonNull DoubleParser<?> parser, @NonNull CommandContext<?> commandContext) {
            super(input, parser, commandContext);
        }

        @Override
        public @NonNull String numberType() {
            return "double";
        }
    }
}

