/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.exception.parsing;

import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.caption.StandardCaptionKeys;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.exception.parsing.ParserException;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.NumberParser;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.Range;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public abstract class NumberParseException
extends ParserException {
    private final String input;
    private final NumberParser<?, ?, ?> parser;

    protected NumberParseException(@NonNull String input, @NonNull NumberParser<?, ?, ?> parser, @NonNull CommandContext<?> context) {
        super(parser.getClass(), context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_NUMBER, CaptionVariable.of("input", input), CaptionVariable.of("min", String.valueOf(parser.range().min())), CaptionVariable.of("max", String.valueOf(parser.range().max())));
        this.input = input;
        this.parser = parser;
    }

    public abstract @NonNull String numberType();

    public final @NonNull NumberParser<?, ?, ?> parser() {
        return this.parser;
    }

    public final boolean hasMax() {
        return this.parser.hasMax();
    }

    public final boolean hasMin() {
        return this.parser.hasMax();
    }

    public @NonNull String input() {
        return this.input;
    }

    public final @NonNull Range<? extends Number> range() {
        return this.parser.range();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NumberParseException that = (NumberParseException)o;
        return this.parser().equals(that.parser());
    }

    public final int hashCode() {
        return Objects.hash(this.parser());
    }
}

