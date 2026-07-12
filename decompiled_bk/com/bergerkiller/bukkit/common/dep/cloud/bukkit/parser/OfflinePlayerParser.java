/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser;

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCaptionKeys;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCommandContextKeys;
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
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class OfflinePlayerParser<C>
implements ArgumentParser<C, OfflinePlayer>,
BlockingSuggestionProvider.Strings<C> {
    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, OfflinePlayer> offlinePlayerParser() {
        return ParserDescriptor.of(new OfflinePlayerParser<C>(), OfflinePlayer.class);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull CommandComponent.Builder<C, OfflinePlayer> offlinePlayerComponent() {
        return CommandComponent.builder().parser(OfflinePlayerParser.offlinePlayerParser());
    }

    @Override
    public @NonNull ArgumentParseResult<OfflinePlayer> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        OfflinePlayer player;
        String input = commandInput.readString();
        if (input.length() > 16) {
            return ArgumentParseResult.failure(new OfflinePlayerParseException(input, commandContext));
        }
        try {
            player = Bukkit.getOfflinePlayer((String)input);
        }
        catch (Exception e) {
            return ArgumentParseResult.failure(new OfflinePlayerParseException(input, commandContext));
        }
        return ArgumentParseResult.success(player);
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        CommandSender bukkit = commandContext.get(BukkitCommandContextKeys.BUKKIT_COMMAND_SENDER);
        return Bukkit.getOnlinePlayers().stream().filter(player -> !(bukkit instanceof Player) || ((Player)bukkit).canSee(player)).map(OfflinePlayer::getName).collect(Collectors.toList());
    }

    public static final class OfflinePlayerParseException
    extends ParserException {
        private final String input;

        public OfflinePlayerParseException(@NonNull String input, @NonNull CommandContext<?> context) {
            super(OfflinePlayerParser.class, context, BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_OFFLINEPLAYER, CaptionVariable.of("input", input));
            this.input = input;
        }

        public @NonNull String input() {
            return this.input;
        }
    }
}

