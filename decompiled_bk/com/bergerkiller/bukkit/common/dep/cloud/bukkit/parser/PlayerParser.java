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
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class PlayerParser<C>
implements ArgumentParser<C, Player>,
BlockingSuggestionProvider<C> {
    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, Player> playerParser() {
        return ParserDescriptor.of(new PlayerParser<C>(), Player.class);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull CommandComponent.Builder<C, Player> playerComponent() {
        return CommandComponent.builder().parser(PlayerParser.playerParser());
    }

    @Override
    public @NonNull ArgumentParseResult<Player> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        String input = commandInput.readString();
        Player player = Bukkit.getPlayer((String)input);
        if (player == null) {
            return ArgumentParseResult.failure(new PlayerParseException(input, commandContext));
        }
        return ArgumentParseResult.success(player);
    }

    @Override
    public @NonNull Iterable<@NonNull Suggestion> suggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        CommandSender bukkit = commandContext.get(BukkitCommandContextKeys.BUKKIT_COMMAND_SENDER);
        return Bukkit.getOnlinePlayers().stream().filter(player -> !(bukkit instanceof Player) || ((Player)bukkit).canSee(player)).map(OfflinePlayer::getName).map(Suggestion::suggestion).collect(Collectors.toList());
    }

    public static final class PlayerParseException
    extends ParserException {
        private final String input;

        public PlayerParseException(@NonNull String input, @NonNull CommandContext<?> context) {
            super(PlayerParser.class, context, BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER, CaptionVariable.of("input", input));
            this.input = input;
        }

        public @NonNull String input() {
            return this.input;
        }
    }
}

