/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.wrappers.ChatText
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.pathfinding.PathNode;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.IProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SignActionDestination
extends TrainCartsSignAction {
    public SignActionDestination() {
        super("destination");
    }

    @Override
    public boolean click(SignActionEvent info, Player player) {
        IProperties prop;
        CartProperties cprop = info.getTrainCarts().getPlayer(player).getEditedCart();
        if (cprop == null) {
            if (Permission.COMMAND_PROPERTIES.has((CommandSender)player)) {
                Localization.EDIT_NOSELECT.message((CommandSender)player, new String[0]);
            } else {
                Localization.EDIT_NOTALLOWED.message((CommandSender)player, new String[0]);
            }
            return true;
        }
        if (info.isTrainSign()) {
            prop = cprop.getTrainProperties();
        } else if (info.isCartSign()) {
            prop = cprop;
        } else {
            return false;
        }
        if (!prop.hasOwnership(player)) {
            Localization.EDIT_NOTOWNED.message((CommandSender)player, new String[0]);
        } else {
            String dest = info.getLine(2);
            prop.setDestination(dest);
            Localization.SELECT_DESTINATION.message((CommandSender)player, new String[]{dest});
        }
        return true;
    }

    @Override
    public void execute(SignActionEvent info) {
        if (info.isRCSign()) {
            if (info.isAction(SignActionType.REDSTONE_ON)) {
                for (TrainProperties prop : info.getRCTrainProperties()) {
                    for (CartProperties cprop : prop) {
                        cprop.setDestination(info.getLine(3));
                    }
                }
            }
            return;
        }
        if (!info.hasRails()) {
            return;
        }
        if (!(info.isCartSign() && info.isAction(SignActionType.REDSTONE_ON, SignActionType.MEMBER_ENTER) || info.isTrainSign() && info.isAction(SignActionType.REDSTONE_ON, SignActionType.GROUP_ENTER))) {
            return;
        }
        PathNode node = PathNode.getOrCreate(info);
        for (MinecartMember<?> member : info.getMembers()) {
            member.getProperties().setLastPathNode(node.getName());
        }
        for (MinecartMember<?> member : info.getMembers()) {
            String nextDestination = this.getNextDestination(member.getProperties(), info);
            if (nextDestination == null) continue;
            if (nextDestination.isEmpty()) {
                member.getProperties().clearDestination();
                continue;
            }
            member.getProperties().setDestination(nextDestination);
        }
    }

    private String getNextDestination(CartProperties cart, SignActionEvent info) {
        String newDestination = info.getLine(3).trim();
        if (newDestination.isEmpty()) {
            newDestination = null;
        }
        if (info.isAction(SignActionType.REDSTONE_ON)) {
            return newDestination;
        }
        if (!info.isPowered()) {
            return null;
        }
        String signDestination = info.getLine(2);
        if (signDestination.isEmpty()) {
            return newDestination;
        }
        if (cart.hasDestination() && !cart.getDestination().equals(signDestination)) {
            return null;
        }
        String nextOnRoute = cart.getNextDestinationOnRoute(signDestination);
        if (nextOnRoute.isEmpty() && newDestination == null && !cart.getDestinationRoute().isEmpty()) {
            nextOnRoute = cart.getDestinationRoute().get(0);
        }
        return nextOnRoute.isEmpty() ? newDestination : nextOnRoute;
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        PathNode node;
        if (!event.getLine(2).isEmpty() && (node = event.getTrainCarts().getPathProvider().getWorld(event.getWorld()).getNodeByName(event.getLine(2))) != null) {
            event.getPlayer().sendMessage(ChatColor.RED + "Destination with name '" + event.getLine(2) + "' already exists on this world!");
            ChatText text = ChatText.fromMessage((String)(ChatColor.RED + "Find it at "));
            ChatText command = ChatText.fromMessage((String)(ChatColor.WHITE.toString() + ChatColor.UNDERLINE + "[" + node.location.x + " / " + node.location.y + " / " + node.location.z + "]"));
            command.setClickableSuggestedCommand("/tp @p " + node.location.x + " " + node.location.y + " " + node.location.z);
            text.append(command);
            text.sendTo(event.getPlayer());
            return false;
        }
        SignBuildOptions opt = SignBuildOptions.create().setPermission(Permission.BUILD_DESTINATION).setName(event.isCartSign() ? "cart destination" : "train destination").setTraincartsWIKIHelp("TrainCarts/Signs/Destination");
        if (event.isTrainSign()) {
            opt.setDescription("set a train destination and the next destination to set once it is reached");
        } else if (event.isCartSign()) {
            opt.setDescription("set a cart destination and the next destination to set once it is reached");
        } else if (event.isRCSign()) {
            opt.setDescription("set the destination on a remote train");
        }
        return opt.handle(event);
    }

    @Override
    public String getRailDestinationName(SignActionEvent info) {
        String name = info.getLine(2);
        return name.isEmpty() ? null : name;
    }

    @Override
    public boolean canSupportRC() {
        return true;
    }
}

