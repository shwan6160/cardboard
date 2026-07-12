/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser.flag;

import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.caption.StandardCaptionKeys;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.exception.parsing.ParserException;
import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKey;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.flag.CommandFlag;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
public final class CommandFlagParser<C>
implements ArgumentParser.FutureArgumentParser<C, Object>,
SuggestionProvider<C> {
    public static final Object FLAG_PARSE_RESULT_OBJECT = new Object();
    public static final CloudKey<String> FLAG_META_KEY = CloudKey.of("__last_flag__", TypeToken.get(String.class));
    public static final CloudKey<Integer> FLAG_CURSOR_KEY = CloudKey.of("__flag_cursor__", TypeToken.get(Integer.class));
    public static final CloudKey<Set<CommandFlag<?>>> PARSED_FLAGS = CloudKey.of("__parsed_flags__", new TypeToken<Set<CommandFlag<?>>>(){});
    private static final Pattern FLAG_PRIMARY_PATTERN = Pattern.compile(" --(?<name>([A-Za-z]+))");
    private static final Pattern FLAG_ALIAS_PATTERN = Pattern.compile(" -(?<name>([A-Za-z]+))");
    private final Collection<@NonNull CommandFlag<?>> flags;

    public CommandFlagParser(@NonNull Collection<@NonNull CommandFlag<?>> flags) {
        this.flags = flags;
    }

    @API(status=API.Status.STABLE)
    public @NonNull Collection<@NonNull CommandFlag<?>> flags() {
        return Collections.unmodifiableCollection(this.flags);
    }

    @Override
    public @NonNull CompletableFuture<@NonNull ArgumentParseResult<Object>> parseFuture(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        return new FlagParser().parse(commandContext, commandInput);
    }

    /*
     * Issues handling annotations - annotations may be inaccurate
     */
    @API(status=API.Status.STABLE)
    public @NonNull CompletableFuture<Optional<String>> parseCurrentFlag(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput, @NonNull Executor completionExecutor) {
        if (commandInput.isEmpty()) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        String lastInputValue = commandInput.lastRemainingToken();
        FlagParser parser = new FlagParser();
        @NonNull CompletableFuture result = parser.parse(commandContext, commandInput);
        return result.thenApplyAsync(parseResult -> {
            if (commandContext.contains(FLAG_CURSOR_KEY)) {
                commandInput.cursor(commandContext.get(FLAG_CURSOR_KEY));
            } else if (parser.lastParsedFlag() == null && commandInput.isEmpty()) {
                int count = lastInputValue.length();
                commandInput.moveCursor(-count);
            }
            return Optional.ofNullable(parser.lastParsedFlag());
        }, completionExecutor);
    }

    @Override
    public @NonNull CompletableFuture<Iterable<@NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        CommandFlag<?> currentFlag;
        block19: {
            String flagName;
            String lastArg = Objects.requireNonNull(commandContext.getOrDefault(FLAG_META_KEY, ""));
            if (!lastArg.startsWith("-")) {
                String currentFlag2;
                String readInput = input.readInput();
                LinkedList<Object> usedFlags = new LinkedList<Object>();
                Matcher primaryMatcher = FLAG_PRIMARY_PATTERN.matcher(readInput);
                block0: while (primaryMatcher.find()) {
                    String name = primaryMatcher.group("name");
                    for (CommandFlag<?> flag : this.flags) {
                        if (!flag.name().equalsIgnoreCase(name)) continue;
                        usedFlags.add(flag);
                        continue block0;
                    }
                }
                Matcher aliasMatcher = FLAG_ALIAS_PATTERN.matcher(readInput);
                while (aliasMatcher.find()) {
                    String name = aliasMatcher.group("name");
                    block3: for (CommandFlag commandFlag : this.flags) {
                        for (String string : commandFlag.aliases()) {
                            if (!name.contains(string)) continue;
                            usedFlags.add(commandFlag);
                            continue block3;
                        }
                    }
                }
                String nextToken = input.peekString();
                if (nextToken.length() > 1) {
                    String currentFlag3 = nextToken.substring(1);
                } else {
                    currentFlag2 = "";
                }
                LinkedList<Suggestion> linkedList = new LinkedList<Suggestion>();
                for (CommandFlag commandFlag : this.flags) {
                    if (usedFlags.contains(commandFlag) && commandFlag.mode() != CommandFlag.FlagMode.REPEATABLE || !commandContext.hasPermission(commandFlag.permission())) continue;
                    linkedList.add(Suggestion.suggestion(String.format("--%s", commandFlag.name())));
                }
                boolean suggestCombined = nextToken.length() > 1 && nextToken.startsWith("-") && !nextToken.startsWith("--");
                for (CommandFlag<?> flag : this.flags) {
                    if (usedFlags.contains(flag) && flag.mode() != CommandFlag.FlagMode.REPEATABLE || !commandContext.hasPermission(flag.permission())) continue;
                    for (String alias : flag.aliases()) {
                        if (alias.equalsIgnoreCase(currentFlag2)) continue;
                        if (suggestCombined && flag.commandComponent() == null) {
                            linkedList.add(Suggestion.suggestion(String.format("%s%s", input.peekString(), alias)));
                            continue;
                        }
                        linkedList.add(Suggestion.suggestion(String.format("-%s", alias)));
                    }
                }
                if (suggestCombined) {
                    linkedList.add(Suggestion.suggestion(input.peekString()));
                }
                return CompletableFuture.completedFuture(linkedList);
            }
            currentFlag = null;
            if (lastArg.startsWith("--")) {
                flagName = lastArg.substring(2);
                for (CommandFlag<?> flag : this.flags) {
                    if (!flagName.equalsIgnoreCase(flag.name())) continue;
                    currentFlag = flag;
                    break;
                }
            } else {
                flagName = lastArg.substring(1);
                for (CommandFlag<?> flag : this.flags) {
                    for (String alias : flag.aliases()) {
                        if (!alias.equalsIgnoreCase(flagName)) continue;
                        currentFlag = flag;
                        break block19;
                    }
                }
            }
        }
        if (currentFlag != null && commandContext.hasPermission(currentFlag.permission()) && currentFlag.commandComponent() != null) {
            SuggestionProvider<?> suggestionProvider = currentFlag.commandComponent().suggestionProvider();
            return suggestionProvider.suggestionsFuture(commandContext, input);
        }
        commandContext.store(FLAG_META_KEY, "");
        return this.suggestionsFuture(commandContext, input);
    }

    private final class FlagParser {
        private String lastParsedFlag;

        private FlagParser() {
        }

        private @NonNull CompletableFuture<@NonNull ArgumentParseResult<Object>> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
            CompletionStage<Object> result = CompletableFuture.completedFuture(null);
            Set parsedFlags = commandContext.computeIfAbsent(PARSED_FLAGS, k -> new HashSet());
            int remainingTokens = commandInput.remainingTokens();
            for (int i = 0; i <= remainingTokens; ++i) {
                result = result.thenCompose(parseResult -> {
                    CommandFlag flag;
                    String string;
                    block22: {
                        commandInput.skipWhitespace();
                        if (parseResult != null || commandInput.isEmpty()) {
                            return CompletableFuture.completedFuture(parseResult);
                        }
                        string = commandInput.peekString();
                        if (!string.startsWith("-")) {
                            return CompletableFuture.completedFuture(ArgumentParseResult.success(FLAG_PARSE_RESULT_OBJECT));
                        }
                        this.lastParsedFlag = null;
                        if (string.startsWith("--")) {
                            commandInput.moveCursor(2);
                        } else {
                            commandInput.moveCursor(1);
                        }
                        String flagName = commandInput.readStringSkipWhitespace();
                        flag = null;
                        if (string.startsWith("--")) {
                            for (CommandFlag flagCandidate : CommandFlagParser.this.flags) {
                                if (!flagName.equalsIgnoreCase(flagCandidate.name())) continue;
                                flag = flagCandidate;
                                break;
                            }
                        } else if (flagName.length() == 1) {
                            for (CommandFlag flagCandidate : CommandFlagParser.this.flags) {
                                for (String alias : flagCandidate.aliases()) {
                                    if (!alias.equalsIgnoreCase(flagName)) continue;
                                    flag = flagCandidate;
                                    break block22;
                                }
                            }
                        } else {
                            boolean flagFound = false;
                            for (int j = 0; j < flagName.length(); ++j) {
                                String parsedFlag = Character.toString(flagName.charAt(j)).toLowerCase(Locale.ENGLISH);
                                for (CommandFlag candidateFlag : CommandFlagParser.this.flags) {
                                    if (candidateFlag.commandComponent() != null || !candidateFlag.aliases().contains(parsedFlag)) continue;
                                    if (parsedFlags.contains(candidateFlag) && candidateFlag.mode() != CommandFlag.FlagMode.REPEATABLE) {
                                        return this.fail(new FlagParseException(string, FailureReason.DUPLICATE_FLAG, commandContext));
                                    }
                                    if (!commandContext.hasPermission(candidateFlag.permission())) {
                                        return this.fail(new FlagParseException(string, FailureReason.NO_PERMISSION, commandContext));
                                    }
                                    commandContext.flags().addPresenceFlag(candidateFlag);
                                    parsedFlags.add(candidateFlag);
                                    flagFound = true;
                                }
                            }
                            if (!flagFound) {
                                return this.fail(new FlagParseException(string, FailureReason.NO_FLAG_STARTED, commandContext));
                            }
                            return CompletableFuture.completedFuture(null);
                        }
                    }
                    if (flag == null) {
                        return this.fail(new FlagParseException(string, FailureReason.UNKNOWN_FLAG, commandContext));
                    }
                    if (parsedFlags.contains(flag) && flag.mode() != CommandFlag.FlagMode.REPEATABLE) {
                        return this.fail(new FlagParseException(string, FailureReason.DUPLICATE_FLAG, commandContext));
                    }
                    if (!commandContext.hasPermission(flag.permission())) {
                        return this.fail(new FlagParseException(string, FailureReason.NO_PERMISSION, commandContext));
                    }
                    if (flag.commandComponent() == null) {
                        commandContext.remove(FLAG_CURSOR_KEY);
                        commandContext.flags().addPresenceFlag(flag);
                        parsedFlags.add(flag);
                        return CompletableFuture.completedFuture(null);
                    }
                    if (commandInput.hasRemainingInput() && commandInput.peek() == ' ') {
                        this.lastParsedFlag = string;
                    }
                    if (commandInput.isEmpty(true)) {
                        return this.fail(new FlagParseException(flag.name(), FailureReason.MISSING_ARGUMENT, commandContext));
                    }
                    this.lastParsedFlag = string;
                    CommandFlag parsingFlag = flag;
                    CommandInput commandInputCopy = commandInput.copy();
                    return flag.commandComponent().parser().parseFuture(commandContext, commandInput).thenApply(parsedValue -> {
                        if (parsedValue.failure().isPresent() || commandInput.isEmpty() || commandInput.peek() != ' ') {
                            commandContext.store(FLAG_CURSOR_KEY, Integer.valueOf(commandInputCopy.cursor()));
                        }
                        if (parsedValue.failure().isPresent()) {
                            return parsedValue;
                        }
                        commandContext.flags().addValueFlag(parsingFlag, parsedValue.parsedValue().get());
                        parsedFlags.add(parsingFlag);
                        if (!commandInput.isEmpty(false) && commandInput.peek() == ' ') {
                            this.lastParsedFlag = null;
                        }
                        return null;
                    });
                });
            }
            return result.thenApply(r -> r == null ? ArgumentParseResult.success(FLAG_PARSE_RESULT_OBJECT) : r);
        }

        private @Nullable String lastParsedFlag() {
            return this.lastParsedFlag;
        }

        private @NonNull CompletableFuture<ArgumentParseResult<Object>> fail(@NonNull Throwable exception) {
            return ArgumentParseResult.failureFuture(exception);
        }
    }

    @API(status=API.Status.STABLE)
    public static final class FlagParseException
    extends ParserException {
        private final String input;
        private final FailureReason failureReason;

        public FlagParseException(@NonNull String input, @NonNull FailureReason failureReason, @NonNull CommandContext<?> context) {
            super(CommandFlagParser.class, context, failureReason.caption(), CaptionVariable.of("input", input), CaptionVariable.of("flag", input));
            this.input = input;
            this.failureReason = failureReason;
        }

        public String input() {
            return this.input;
        }

        @API(status=API.Status.STABLE)
        public @NonNull FailureReason failureReason() {
            return this.failureReason;
        }
    }

    @API(status=API.Status.STABLE)
    public static enum FailureReason {
        UNKNOWN_FLAG(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_UNKNOWN_FLAG),
        DUPLICATE_FLAG(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_DUPLICATE_FLAG),
        NO_FLAG_STARTED(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_NO_FLAG_STARTED),
        MISSING_ARGUMENT(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_MISSING_ARGUMENT),
        NO_PERMISSION(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_NO_PERMISSION);

        private final Caption caption;

        private FailureReason(Caption caption) {
            this.caption = caption;
        }

        public @NonNull Caption caption() {
            return this.caption;
        }
    }
}

