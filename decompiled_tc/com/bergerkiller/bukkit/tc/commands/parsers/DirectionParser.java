/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.cloud.CloudLocalizedException
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor
 *  com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider$Strings
 *  com.bergerkiller.bukkit.common.localization.ILocalizationEnum
 *  org.bukkit.command.CommandSender
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.tc.commands.parsers;

import com.bergerkiller.bukkit.common.cloud.CloudLocalizedException;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.common.localization.ILocalizationEnum;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.Localization;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DirectionParser
implements ArgumentParser<CommandSender, Direction>,
BlockingSuggestionProvider.Strings<CommandSender> {
    public static ParserDescriptor<CommandSender, Direction> directionParser() {
        return ParserDescriptor.of((ArgumentParser)new DirectionParser(), Direction.class);
    }

    public @NonNull ArgumentParseResult<@NonNull Direction> parse(@NonNull CommandContext<@NonNull CommandSender> commandContext, @NonNull CommandInput commandInput) {
        Direction result = Direction.parse(commandInput.peekString());
        if (result == Direction.NONE) {
            return ArgumentParseResult.failure((Throwable)new CloudLocalizedException(commandContext, (ILocalizationEnum)Localization.COMMAND_INPUT_DIRECTION_INVALID, new String[]{commandInput.peekString()}));
        }
        commandInput.readString();
        return ArgumentParseResult.success((Object)((Object)result));
    }

    public @NonNull List<@NonNull String> stringSuggestions(@NonNull CommandContext<CommandSender> commandContext, @NonNull CommandInput commandInput) {
        String input = commandInput.lastRemainingToken();
        List<String> recommended = Arrays.asList("north", "east", "south", "west", "up", "down", "left", "right", "forward", "backward", "continue", "reverse");
        if (recommended.stream().anyMatch(s -> s.startsWith(input))) {
            return recommended;
        }
        return Stream.of(Direction.values()).filter(s -> s != Direction.NONE).flatMap(s -> Stream.of(s.aliases())).collect(Collectors.toList());
    }
}

