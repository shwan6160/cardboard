/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.cloud.parsers.QuotedArgumentParser
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult
 *  com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider$Strings
 *  com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.tc.commands.suggestions;

import com.bergerkiller.bukkit.common.cloud.parsers.QuotedArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorHandler;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorHandlerConditionOption;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class TrainNameSuggestionProvider
implements BlockingSuggestionProvider.Strings<CommandSender> {
    public SuggestionProvider<CommandSender> quoteEscaped() {
        return new QuotedArgumentParser<CommandSender, String>(){

            public boolean isStrictQuoteEscaping() {
                return true;
            }

            public ArgumentParseResult<String> parseQuotedString(CommandContext<CommandSender> commandContext, String inputString) {
                return ArgumentParseResult.success((Object)inputString);
            }

            public SuggestionProvider<CommandSender> suggestionProvider() {
                return TrainNameSuggestionProvider.this;
            }
        }.createParser().suggestionProvider();
    }

    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<CommandSender> commandContext, @NonNull CommandInput commandInput) {
        CommandSender sender = (CommandSender)commandContext.sender();
        String input = commandInput.lastRemainingToken();
        if (input.startsWith("@train[")) {
            TrainCarts plugin = (TrainCarts)((Object)commandContext.inject(TrainCarts.class).get());
            SelectorHandler handler = plugin.getSelectorHandlerRegistry().find("train");
            if (handler == null) {
                return Collections.singletonList("@train[]");
            }
            return handler.options(sender, "train", Collections.emptyList()).stream().map(SelectorHandlerConditionOption::name).map(s -> "@train[" + s + "=").collect(Collectors.toList());
        }
        Stream<String> stream = TrainProperties.getAll().stream().filter(p -> !(sender instanceof Player) || p.hasOwnership((Player)sender)).map(TrainProperties::getTrainName).map(ChatColor::stripColor);
        if ("@train[".startsWith(input)) {
            stream = Stream.concat(stream, Stream.of("@train["));
        }
        return stream.collect(Collectors.toList());
    }
}

