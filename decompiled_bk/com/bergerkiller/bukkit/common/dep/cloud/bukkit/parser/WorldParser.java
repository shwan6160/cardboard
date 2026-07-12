/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser;

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCaptionKeys;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.exception.parsing.ParserException;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class WorldParser<C>
implements ArgumentParser<C, World>,
BlockingSuggestionProvider.Strings<C> {
    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, World> worldParser() {
        return ParserDescriptor.of(new WorldParser<C>(), World.class);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull CommandComponent.Builder<C, World> worldComponent() {
        return CommandComponent.builder().parser(WorldParser.worldParser());
    }

    @Override
    public @NonNull ArgumentParseResult<World> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        String input = commandInput.readString();
        World world = Bukkit.getWorld((String)input);
        if (world == null) {
            return ArgumentParseResult.failure(new WorldParseException(input, commandContext));
        }
        return ArgumentParseResult.success(world);
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
    }

    public static final class WorldParseException
    extends ParserException {
        private final String input;

        public WorldParseException(@NonNull String input, @NonNull CommandContext<?> context) {
            super(WorldParser.class, context, BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_WORLD, CaptionVariable.of("input", input));
            this.input = input;
        }

        public @NonNull String input() {
            return this.input;
        }
    }
}

