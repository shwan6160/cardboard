/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.Bukkit
 *  org.bukkit.NamespacedKey
 *  org.bukkit.World
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper.parser;

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.CraftBukkitReflection;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.WorldParser;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apiguardian.api.API;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class KeyedWorldParser<C>
implements ArgumentParser<C, World>,
SuggestionProvider<C> {
    private final ArgumentParser<C, World> parser;

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, World> keyedWorldParser() {
        return ParserDescriptor.of(new KeyedWorldParser<C>(), World.class);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull CommandComponent.Builder<C, World> keyedWorldComponent() {
        return CommandComponent.builder().parser(KeyedWorldParser.keyedWorldParser());
    }

    public KeyedWorldParser() {
        Class<World> keyed = CraftBukkitReflection.findClass("org.bukkit.Keyed");
        this.parser = keyed != null && keyed.isAssignableFrom(World.class) ? null : new WorldParser();
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull World> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        if (this.parser != null) {
            return this.parser.parse(commandContext, commandInput);
        }
        String input = commandInput.readString();
        NamespacedKey key = NamespacedKey.fromString((String)input);
        if (key == null) {
            return ArgumentParseResult.failure(new WorldParser.WorldParseException(input, commandContext));
        }
        World world = Bukkit.getWorld((NamespacedKey)key);
        if (world == null) {
            return ArgumentParseResult.failure(new WorldParser.WorldParseException(input, commandContext));
        }
        return ArgumentParseResult.success(world);
    }

    @Override
    public @NonNull CompletableFuture<? extends @NonNull Iterable<? extends @NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        if (this.parser != null) {
            return this.parser.suggestionProvider().suggestionsFuture(commandContext, input);
        }
        List worlds = Bukkit.getWorlds();
        ArrayList<Suggestion> completions = new ArrayList<Suggestion>(worlds.size() * 2);
        for (World world : worlds) {
            NamespacedKey key = world.getKey();
            if (input.hasRemainingInput() && key.getNamespace().equals("minecraft")) {
                completions.add(Suggestion.suggestion(key.getKey()));
            }
            completions.add(Suggestion.suggestion(key.getNamespace() + ':' + key.getKey()));
        }
        return CompletableFuture.completedFuture(completions);
    }
}

