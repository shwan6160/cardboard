/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.commands.selector.type;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentNameLookup;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorCondition;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorException;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorHandler;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorHandlerConditionOption;
import com.bergerkiller.bukkit.tc.commands.selector.TCSelectorHandlerRegistry;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayersInTrainSelector
implements SelectorHandler {
    private final TCSelectorHandlerRegistry registry;

    public PlayersInTrainSelector(TCSelectorHandlerRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Collection<String> handle(CommandSender sender, String selector, List<SelectorCondition> conditions) throws SelectorException {
        List<String> playerNames;
        ArrayList<SelectorCondition> seatConditions = new ArrayList<SelectorCondition>(2);
        for (SelectorCondition condition : conditions) {
            if (!condition.getKey().equalsIgnoreCase("seat") || condition.isBoolean() && !condition.getBoolean()) continue;
            seatConditions.add(condition);
        }
        Stream matchedCarts = this.registry.matchTrains(sender, conditions).stream().map(TrainProperties::getHolder).filter(Objects::nonNull).flatMap(Collection::stream);
        if (seatConditions.isEmpty()) {
            playerNames = matchedCarts.flatMap(member -> ((CommonMinecart)member.getEntity()).getPlayerPassengers().stream()).map(Player::getName).collect(Collectors.toList());
            if (playerNames.isEmpty()) {
                throw new SelectorException("No player passengers are inside any of the matched trains");
            }
        } else {
            playerNames = matchedCarts.flatMap(member -> {
                AttachmentNameLookup nameLookup = member.getAttachments().getNameLookup();
                return seatConditions.stream().flatMap(condition -> nameLookup.matchSeatSelector(sender, (SelectorCondition)condition).filter(e -> e instanceof Player));
            }).distinct().map(p -> ((Player)p).getName()).collect(Collectors.toList());
            if (playerNames.isEmpty()) {
                throw new SelectorException("No player passengers are inside any of the matched seats");
            }
        }
        return playerNames;
    }

    @Override
    public List<SelectorHandlerConditionOption> options(CommandSender sender, String selector, List<SelectorCondition> conditions) {
        return this.registry.matchOptions(sender, conditions);
    }
}

