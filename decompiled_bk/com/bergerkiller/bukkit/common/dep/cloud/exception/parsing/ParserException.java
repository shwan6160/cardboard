/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.exception.parsing;

import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionFormatter;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import java.util.Arrays;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.STABLE)
public class ParserException
extends IllegalArgumentException {
    private final Class<?> argumentParser;
    private final CommandContext<?> context;
    private final Caption errorCaption;
    private final CaptionVariable[] captionVariables;

    protected ParserException(@Nullable Throwable cause, @NonNull Class<?> argumentParser, @NonNull CommandContext<?> context, @NonNull Caption errorCaption, CaptionVariable ... captionVariables) {
        super(cause);
        this.argumentParser = argumentParser;
        this.context = context;
        this.errorCaption = errorCaption;
        this.captionVariables = captionVariables;
    }

    protected ParserException(@NonNull Class<?> argumentParser, @NonNull CommandContext<?> context, @NonNull Caption errorCaption, CaptionVariable ... captionVariables) {
        this(null, argumentParser, context, errorCaption, captionVariables);
    }

    @Override
    public final String getMessage() {
        return this.context.formatCaption(this.errorCaption, this.captionVariables);
    }

    @API(status=API.Status.STABLE)
    public final <T> @NonNull T formatCaption(@NonNull CaptionFormatter<?, T> formatter) {
        return this.context.formatCaption(formatter, this.errorCaption, this.captionVariables());
    }

    @API(status=API.Status.STABLE)
    public @NonNull Caption errorCaption() {
        return this.errorCaption;
    }

    @API(status=API.Status.STABLE)
    public @NonNull CaptionVariable @NonNull [] captionVariables() {
        return Arrays.copyOf(this.captionVariables, this.captionVariables.length);
    }

    public final @NonNull Class<?> argumentParserClass() {
        return this.argumentParser;
    }

    public final @NonNull CommandContext<?> context() {
        return this.context;
    }
}

