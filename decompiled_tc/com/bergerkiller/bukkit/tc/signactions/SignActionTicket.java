/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  net.milkbowl.vault.economy.Economy
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SignActionTicket
extends TrainCartsSignAction {
    public SignActionTicket() {
        super("ticket");
    }

    @Override
    public void execute(SignActionEvent info) {
        boolean isTrain;
        Economy economy = info.getTrainCarts().getEconomy();
        if (economy == null) {
            return;
        }
        if (info.isCartSign() && info.isAction(SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON)) {
            isTrain = false;
        } else if (info.isTrainSign() && info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON)) {
            isTrain = true;
        } else {
            return;
        }
        if (info.hasMember() && info.isPowered()) {
            ArrayList members;
            String mode = info.getLine(2).toLowerCase(Locale.ENGLISH).trim();
            double money = ParseUtil.parseDouble((String)info.getLine(3), (double)0.0);
            if (isTrain) {
                members = info.getGroup();
            } else {
                members = new ArrayList(1);
                members.add(info.getMember());
            }
            for (MinecartMember member : members) {
                if (!((CommonMinecart)member.getEntity()).hasPlayerPassenger()) continue;
                Set<String> owners = member.getProperties().getOwners();
                for (Player player : ((CommonMinecart)member.getEntity()).getPlayerPassengers()) {
                    if (mode.equals("add") && money > 0.0) {
                        economy.depositPlayer((OfflinePlayer)player, money);
                        Localization.TICKET_ADD.message((CommandSender)player, new String[]{TrainCarts.getCurrencyText(money)});
                        continue;
                    }
                    if (mode.equals("check")) {
                        Localization.TICKET_CHECK.message((CommandSender)player, new String[]{TrainCarts.getCurrencyText(economy.getBalance((OfflinePlayer)player))});
                        continue;
                    }
                    if (mode.equals("buy") && money > 0.0) {
                        if (economy.has((OfflinePlayer)player, money)) {
                            economy.withdrawPlayer((OfflinePlayer)player, money);
                            Localization.TICKET_BUY.message((CommandSender)player, new String[]{TrainCarts.getCurrencyText(money)});
                            continue;
                        }
                        Localization.TICKET_BUYFAIL.message((CommandSender)player, new String[]{TrainCarts.getCurrencyText(money)});
                        ((CommonMinecart)member.getEntity()).removePassenger((Entity)player);
                        continue;
                    }
                    if (!mode.equals("pay") || !(money > 0.0) || member.getProperties().isOwner(player)) continue;
                    if (economy.has((OfflinePlayer)player, money)) {
                        economy.withdrawPlayer((OfflinePlayer)player, money);
                        Localization.TICKET_BUY.message((CommandSender)player, new String[]{TrainCarts.getCurrencyText(money)});
                        if (owners.size() <= 0) continue;
                        double ownerPayment = money / (double)owners.size();
                        for (String owner : owners) {
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer((String)owner);
                            economy.depositPlayer(offlinePlayer, ownerPayment);
                            if (!offlinePlayer.isOnline()) continue;
                            Localization.TICKET_BUYOWNER.message((CommandSender)offlinePlayer.getPlayer(), new String[]{player.getDisplayName(), TrainCarts.getCurrencyText(money), member.getProperties().getTrainProperties().getTrainName()});
                        }
                        continue;
                    }
                    Localization.TICKET_BUYFAIL.message((CommandSender)player, new String[]{TrainCarts.getCurrencyText(money)});
                    ((CommonMinecart)member.getEntity()).removePassenger((Entity)player);
                }
            }
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        return SignBuildOptions.create().setPermission(Permission.BUILD_TICKET).setName("ticket system").setDescription("charges the passengers of a train").setTraincartsWIKIHelp("TrainCarts/Signs/Ticket").handle(event);
    }
}

