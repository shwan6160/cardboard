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
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableGroup;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class TrainSpawnPatternSuggestionProvider
implements BlockingSuggestionProvider.Strings<CommandSender> {
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<CommandSender> commandContext, @NonNull CommandInput commandInput) {
        String input = commandInput.lastRemainingToken();
        TrainCarts plugin = (TrainCarts)((Object)commandContext.inject(TrainCarts.class).get());
        if (input.isEmpty() || Character.isDigit(input.charAt(input.length() - 1))) {
            Stream<Object> result = plugin.getSavedTrains().getNames().stream();
            result = Stream.concat(result, Stream.of(SpawnableGroup.VanillaCartType.values()).map(SpawnableGroup.VanillaCartType::toString));
            result = Stream.concat(result, IntStream.range(0, 10).mapToObj(Integer::toString));
            if (!input.isEmpty()) {
                result = result.map(name -> input + name);
            }
            return result.collect(Collectors.toList());
        }
        int nameStart = 0;
        for (int i = input.length() - 1; i >= 0; --i) {
            if (!Character.isDigit(input.charAt(i))) continue;
            nameStart = i + 1;
            break;
        }
        String prefix = input.substring(0, nameStart);
        String typedName = input.substring(nameStart);
        List<String> filtered = plugin.getSavedTrains().getNames().stream().filter(n -> n.length() > typedName.length() && n.startsWith(typedName)).map(n -> prefix + n).collect(Collectors.toList());
        if (filtered.isEmpty()) {
            Stream<String> result = Stream.of(SpawnableGroup.VanillaCartType.values()).map(SpawnableGroup.VanillaCartType::toString);
            result = Stream.concat(result, IntStream.range(0, 10).mapToObj(Integer::toString));
            result = result.map(n -> input + n);
            return result.collect(Collectors.toList());
        }
        return filtered;
    }
}

