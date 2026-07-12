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
import com.bergerkiller.bukkit.common.dep.cloud.util.StringUtils;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class StringParser<C>
implements ArgumentParser<C, String> {
    private static final Pattern QUOTED_DOUBLE = Pattern.compile("\"(?<inner>(?:[^\"\\\\]|\\\\.)*)\"");
    private static final Pattern QUOTED_SINGLE = Pattern.compile("'(?<inner>(?:[^'\\\\]|\\\\.)*)'");
    private static final Pattern FLAG_PATTERN = Pattern.compile("(-[A-Za-z_\\-0-9])|(--[A-Za-z_\\-0-9]*)");
    private final StringMode stringMode;

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, String> stringParser(@NonNull StringMode mode) {
        return ParserDescriptor.of(new StringParser<C>(mode), String.class);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, String> stringParser() {
        return StringParser.stringParser(StringMode.SINGLE);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, String> greedyStringParser() {
        return StringParser.stringParser(StringMode.GREEDY);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, String> greedyFlagYieldingStringParser() {
        return StringParser.stringParser(StringMode.GREEDY_FLAG_YIELDING);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, String> quotedStringParser() {
        return StringParser.stringParser(StringMode.QUOTED);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull CommandComponent.Builder<C, String> stringComponent(@NonNull StringMode mode) {
        return CommandComponent.builder().parser(StringParser.stringParser(mode));
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull CommandComponent.Builder<C, String> stringComponent() {
        return CommandComponent.builder().parser(StringParser.stringParser(StringMode.SINGLE));
    }

    public StringParser(@NonNull StringMode stringMode) {
        this.stringMode = stringMode;
    }

    @Override
    public @NonNull ArgumentParseResult<String> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        if (this.stringMode == StringMode.SINGLE) {
            return ArgumentParseResult.success(commandInput.readString());
        }
        if (this.stringMode == StringMode.QUOTED) {
            return this.parseQuoted(commandContext, commandInput);
        }
        return this.parseGreedy(commandInput);
    }

    private @NonNull ArgumentParseResult<String> parseQuoted(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        char peek = commandInput.peek();
        if (peek != '\'' && peek != '\"') {
            return ArgumentParseResult.success(commandInput.readString());
        }
        String string = commandInput.remainingInput();
        Matcher doubleMatcher = QUOTED_DOUBLE.matcher(string);
        String doubleMatch = null;
        if (doubleMatcher.find()) {
            doubleMatch = doubleMatcher.group("inner");
        }
        Matcher singleMatcher = QUOTED_SINGLE.matcher(string);
        String singleMatch = null;
        if (singleMatcher.find()) {
            singleMatch = singleMatcher.group("inner");
        }
        String inner = null;
        if (singleMatch != null && doubleMatch != null) {
            int singleIndex = string.indexOf(singleMatch);
            int doubleIndex = string.indexOf(doubleMatch);
            inner = doubleIndex < singleIndex ? doubleMatch : singleMatch;
        } else if (singleMatch == null && doubleMatch != null) {
            inner = doubleMatch;
        } else if (singleMatch != null) {
            inner = singleMatch;
        }
        if (inner != null) {
            int numSpaces = StringUtils.countCharOccurrences(inner, ' ');
            for (int i = 0; i <= numSpaces; ++i) {
                commandInput.readString();
            }
        } else {
            inner = commandInput.peekString();
            if (inner.startsWith("\"") || inner.startsWith("'")) {
                return ArgumentParseResult.failure(new StringParseException(commandInput.remainingInput(), StringMode.QUOTED, commandContext));
            }
            commandInput.readString();
        }
        inner = inner.replace("\\\"", "\"").replace("\\'", "'");
        return ArgumentParseResult.success(inner);
    }

    private @NonNull ArgumentParseResult<String> parseGreedy(@NonNull CommandInput commandInput) {
        String string;
        int size = commandInput.remainingTokens();
        StringJoiner stringJoiner = new StringJoiner(" ");
        for (int i = 0; !(i >= size || (string = commandInput.peekString()).isEmpty() || this.stringMode == StringMode.GREEDY_FLAG_YIELDING && FLAG_PATTERN.matcher(string).matches()); ++i) {
            stringJoiner.add(commandInput.readStringSkipWhitespace(false));
        }
        return ArgumentParseResult.success(stringJoiner.toString());
    }

    public @NonNull StringMode stringMode() {
        return this.stringMode;
    }

    @API(status=API.Status.STABLE)
    public static enum StringMode {
        SINGLE,
        QUOTED,
        GREEDY,
        GREEDY_FLAG_YIELDING;

    }

    @API(status=API.Status.STABLE)
    public static final class StringParseException
    extends ParserException {
        private final String input;
        private final StringMode stringMode;

        public StringParseException(@NonNull String input, @NonNull StringMode stringMode, @NonNull CommandContext<?> context) {
            super(StringParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_STRING, CaptionVariable.of("input", input), CaptionVariable.of("stringMode", stringMode.name()));
            this.input = input;
            this.stringMode = stringMode;
        }

        public @NonNull String input() {
            return this.input;
        }

        public @NonNull StringMode stringMode() {
            return this.stringMode;
        }
    }
}

