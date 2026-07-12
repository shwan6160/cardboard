/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput
 *  com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider$Strings
 *  org.bukkit.command.CommandSender
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.tc.commands.suggestions;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorHandler;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorHandlerConditionOption;
import java.util.Collections;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class TrainListFilterSuggestionProvider
implements BlockingSuggestionProvider.Strings<CommandSender> {
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<CommandSender> context, @NonNull CommandInput input) {
        CommandSender sender = (CommandSender)context.sender();
        if (input.remainingInput().startsWith("@train[")) {
            TrainCarts plugin = (TrainCarts)((Object)context.inject(TrainCarts.class).get());
            SelectorHandler handler = plugin.getSelectorHandlerRegistry().find("train");
            if (handler == null) {
                return Collections.singletonList("@train[]");
            }
            return handler.options(sender, "train", Collections.emptyList()).stream().map(SelectorHandlerConditionOption::name).map(s -> "@train[" + s + "=").collect(Collectors.toList());
        }
        return Collections.singletonList("@train[");
    }
}

