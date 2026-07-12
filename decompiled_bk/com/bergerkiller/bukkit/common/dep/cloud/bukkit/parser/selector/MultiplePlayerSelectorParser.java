/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.selector;

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.data.MultiplePlayerSelector;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.PlayerParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.selector.SelectorUtils;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apiguardian.api.API;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class MultiplePlayerSelectorParser<C>
extends SelectorUtils.PlayerSelectorParser<C, MultiplePlayerSelector> {
    private final boolean allowEmpty;

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, MultiplePlayerSelector> multiplePlayerSelectorParser() {
        return MultiplePlayerSelectorParser.multiplePlayerSelectorParser(true);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, MultiplePlayerSelector> multiplePlayerSelectorParser(boolean allowEmpty) {
        return ParserDescriptor.of(new MultiplePlayerSelectorParser<C>(allowEmpty), MultiplePlayerSelector.class);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull CommandComponent.Builder<C, MultiplePlayerSelector> multiplePlayerSelectorComponent() {
        return CommandComponent.builder().parser(MultiplePlayerSelectorParser.multiplePlayerSelectorParser());
    }

    @API(status=API.Status.STABLE, since="1.8.0")
    public MultiplePlayerSelectorParser(boolean allowEmpty) {
        super(false);
        this.allowEmpty = allowEmpty;
    }

    public MultiplePlayerSelectorParser() {
        this(true);
    }

    @Override
    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    public MultiplePlayerSelector mapResult(final @NonNull String input,  @NonNull SelectorUtils.EntitySelectorWrapper wrapper) {
        final List<Player> players = wrapper.players();
        if (players.isEmpty() && !this.allowEmpty) {
            new SelectorUtils.SelectorParser.Thrower(NO_PLAYERS_EXCEPTION_TYPE.get()).throwIt();
        }
        return new MultiplePlayerSelector(){

            @Override
            public @NonNull String inputString() {
                return input;
            }

            @Override
            public @NonNull Collection<Player> values() {
                return Collections.unmodifiableCollection(players);
            }
        };
    }

    @Override
    protected @NonNull CompletableFuture<ArgumentParseResult<MultiplePlayerSelector>> legacyParse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        String input = commandInput.peekString();
        final @Nullable Player player = Bukkit.getPlayer((String)input);
        if (player == null) {
            return CompletableFuture.completedFuture(ArgumentParseResult.failure(new PlayerParser.PlayerParseException(input, commandContext)));
        }
        final String pop = commandInput.readString();
        return ArgumentParseResult.successFuture(new MultiplePlayerSelector(){

            @Override
            public @NonNull String inputString() {
                return pop;
            }

            @Override
            public @NonNull Collection<Player> values() {
                return Collections.singletonList(player);
            }
        });
    }
}

