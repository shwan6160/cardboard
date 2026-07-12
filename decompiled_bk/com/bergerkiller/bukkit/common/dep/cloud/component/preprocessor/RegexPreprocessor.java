/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.component.preprocessor;

import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.caption.StandardCaptionKeys;
import com.bergerkiller.bukkit.common.dep.cloud.component.preprocessor.ComponentPreprocessor;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class RegexPreprocessor<C>
implements ComponentPreprocessor<C> {
    private final String rawPattern;
    private final Predicate<@NonNull String> predicate;
    private final Caption failureCaption;

    private RegexPreprocessor(@NonNull String pattern, @NonNull Caption failureCaption) {
        this.rawPattern = pattern;
        this.predicate = Pattern.compile(pattern).asPredicate();
        this.failureCaption = failureCaption;
    }

    public static <C> @NonNull RegexPreprocessor<C> of(@NonNull String pattern) {
        return RegexPreprocessor.of(pattern, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_REGEX);
    }

    public static <C> @NonNull RegexPreprocessor<C> of(@NonNull String pattern, @NonNull Caption failureCaption) {
        return new RegexPreprocessor<C>(pattern, failureCaption);
    }

    @Override
    public @NonNull ArgumentParseResult<Boolean> preprocess(@NonNull CommandContext<C> context, @NonNull CommandInput commandInput) {
        String head = commandInput.peekString();
        if (this.predicate.test(head)) {
            return ArgumentParseResult.success(true);
        }
        return ArgumentParseResult.failure(new RegexValidationException(this.rawPattern, head, this.failureCaption, context));
    }

    @API(status=API.Status.STABLE)
    public static final class RegexValidationException
    extends IllegalArgumentException {
        private final String pattern;
        private final String failedString;
        private final Caption failureCaption;
        private final CommandContext<?> commandContext;

        private RegexValidationException(@NonNull String pattern, @NonNull String failedString, @NonNull Caption failureCaption, @NonNull CommandContext<?> commandContext) {
            this.pattern = pattern;
            this.failedString = failedString;
            this.failureCaption = failureCaption;
            this.commandContext = commandContext;
        }

        @Override
        public String getMessage() {
            return this.commandContext.formatCaption(this.failureCaption, CaptionVariable.of("input", this.failedString), CaptionVariable.of("pattern", this.pattern));
        }

        public @NonNull String failedInput() {
            return this.failedString;
        }

        public @NonNull String pattern() {
            return this.pattern;
        }
    }
}

