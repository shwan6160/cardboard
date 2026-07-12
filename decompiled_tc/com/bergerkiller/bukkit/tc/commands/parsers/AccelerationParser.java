/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.cloud.CloudLocalizedException
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser
 *  com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider$Strings
 *  com.bergerkiller.bukkit.common.localization.ILocalizationEnum
 *  com.bergerkiller.mountiplex.MountiplexUtil
 *  org.bukkit.command.CommandSender
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.tc.commands.parsers;

import com.bergerkiller.bukkit.common.cloud.CloudLocalizedException;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.common.localization.ILocalizationEnum;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.mountiplex.MountiplexUtil;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class AccelerationParser
implements ArgumentParser<CommandSender, Double>,
BlockingSuggestionProvider.Strings<CommandSender> {
    public static final String NAME = "acceleration";
    private final boolean _greedy;

    public AccelerationParser(boolean greedy) {
        this._greedy = greedy;
    }

    public @NonNull ArgumentParseResult<@NonNull Double> parse(@NonNull CommandContext<@NonNull CommandSender> commandContext, @NonNull CommandInput commandInput) {
        double result;
        String input;
        String string = input = this._greedy ? commandInput.remainingInput() : commandInput.peekString();
        if (input.equalsIgnoreCase("nan")) {
            result = Double.NaN;
        } else {
            result = Util.parseAcceleration(input, Double.NaN);
            if (Double.isNaN(result)) {
                return ArgumentParseResult.failure((Throwable)new CloudLocalizedException(commandContext, (ILocalizationEnum)Localization.COMMAND_INPUT_ACCELERATION_INVALID, new String[]{input}));
            }
        }
        if (this._greedy) {
            commandInput.cursor(commandInput.length());
        } else {
            commandInput.readString();
        }
        return ArgumentParseResult.success((Object)result);
    }

    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<CommandSender> commandContext, @NonNull CommandInput commandInput) {
        String input = commandInput.lastRemainingToken();
        if (input.isEmpty()) {
            return Stream.concat(MountiplexUtil.toStream((Object)"-"), IntStream.range(0, 10).mapToObj(Integer::toString)).collect(Collectors.toList());
        }
        char lastChar = input.charAt(input.length() - 1);
        if (lastChar == '-' || lastChar == '.' || lastChar == ',') {
            return IntStream.range(0, 10).mapToObj(Integer::toString).map(s -> input + s).collect(Collectors.toList());
        }
        if (Character.isDigit(lastChar)) {
            Stream<String> suggestions = AccelerationParser.unitStream();
            if (!input.contains(".") && !input.contains(",")) {
                suggestions = Stream.concat(suggestions, MountiplexUtil.toStream((Object)"."));
            }
            suggestions = Stream.concat(suggestions, IntStream.range(0, 10).mapToObj(Integer::toString));
            return suggestions.map(s -> input + s).collect(Collectors.toList());
        }
        String unitPrefix = AccelerationParser.getUnitPrefix(input);
        String value = input.substring(0, input.length() - unitPrefix.length());
        return AccelerationParser.unitStream().filter(u -> u.startsWith(unitPrefix)).map(u -> value + u).collect(Collectors.toList());
    }

    private static String getUnitPrefix(String input) {
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            if (c == '-' || c == '.' || c == ',' || c == ' ' || Character.isDigit(c)) continue;
            return input.substring(i);
        }
        return "";
    }

    private static Stream<String> unitStream() {
        return Stream.of("G", "m/s/s", "km/h/s", "mi/h/s", "mph/s", "ft/s/s");
    }
}

