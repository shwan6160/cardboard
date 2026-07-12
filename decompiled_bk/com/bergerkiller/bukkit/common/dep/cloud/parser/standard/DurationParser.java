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
import java.time.Duration;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class DurationParser<C>
implements ArgumentParser<C, Duration>,
BlockingSuggestionProvider.Strings<C> {
    private static final Pattern DURATION_PATTERN = Pattern.compile("(([1-9][0-9]+|[1-9])[dhms])");

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Duration> durationParser() {
        return ParserDescriptor.of(new DurationParser<C>(), Duration.class);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull CommandComponent.Builder<C, Duration> durationComponent() {
        return CommandComponent.builder().parser(DurationParser.durationParser());
    }

    @Override
    public @NonNull ArgumentParseResult<Duration> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        String input = commandInput.readString();
        Matcher matcher = DURATION_PATTERN.matcher(input);
        Duration duration = Duration.ofNanos(0L);
        block12: while (matcher.find()) {
            String group = matcher.group();
            String timeUnit = String.valueOf(group.charAt(group.length() - 1));
            int timeValue = Integer.parseInt(group.substring(0, group.length() - 1));
            switch (timeUnit) {
                case "d": {
                    duration = duration.plusDays(timeValue);
                    continue block12;
                }
                case "h": {
                    duration = duration.plusHours(timeValue);
                    continue block12;
                }
                case "m": {
                    duration = duration.plusMinutes(timeValue);
                    continue block12;
                }
                case "s": {
                    duration = duration.plusSeconds(timeValue);
                    continue block12;
                }
            }
            return ArgumentParseResult.failure(new DurationParseException(input, commandContext));
        }
        if (duration.isZero()) {
            return ArgumentParseResult.failure(new DurationParseException(input, commandContext));
        }
        return ArgumentParseResult.success(duration);
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        if (input.isEmpty(true)) {
            return IntStream.range(1, 10).boxed().sorted().map(String::valueOf).collect(Collectors.toList());
        }
        if (Character.isLetter(input.lastRemainingCharacter())) {
            return Collections.emptyList();
        }
        String string = input.readString();
        return Stream.of("d", "h", "m", "s").filter(unit -> !string.contains((CharSequence)unit)).map(unit -> string + unit).collect(Collectors.toList());
    }

    @API(status=API.Status.STABLE)
    public static final class DurationParseException
    extends ParserException {
        private final String input;

        public DurationParseException(@NonNull String input, @NonNull CommandContext<?> context) {
            super(DurationParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_DURATION, CaptionVariable.of("input", input));
            this.input = input;
        }

        public @NonNull String input() {
            return this.input;
        }
    }
}

