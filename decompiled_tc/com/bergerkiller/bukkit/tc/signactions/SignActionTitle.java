/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.entity.Player;

public class SignActionTitle
extends TrainCartsSignAction {
    private static void sendTitle(MinecartGroup group, TitleMessage message) {
        for (MinecartMember<?> member : group) {
            SignActionTitle.sendTitle(member, message);
        }
    }

    private static void sendTitle(MinecartMember<?> member, TitleMessage message) {
        for (Player player : ((CommonMinecart)member.getEntity()).getPlayerPassengers()) {
            player.sendTitle(message.title, message.subtitle, message.fadeIn, message.stay, message.fadeOut);
        }
    }

    public SignActionTitle() {
        super("title");
    }

    @Override
    public void execute(SignActionEvent info) {
        TitleMessage message = new TitleMessage(info);
        if (info.isTrainSign() && info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON)) {
            if (info.hasRailedMember() && info.isPowered()) {
                SignActionTitle.sendTitle(info.getGroup(), message);
            }
        } else if (info.isCartSign() && info.isAction(SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON)) {
            if (info.hasRailedMember() && info.isPowered()) {
                SignActionTitle.sendTitle(info.getMember(), message);
            }
        } else if (info.isRCSign() && info.isAction(SignActionType.REDSTONE_ON)) {
            for (MinecartGroup group : info.getRCTrainGroups()) {
                SignActionTitle.sendTitle(group, message);
            }
        }
    }

    @Override
    public boolean canSupportRC() {
        return true;
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        return SignBuildOptions.create().setPermission(Permission.BUILD_TITLE).setName("title").setDescription(event.isRCSign() ? "remotely send title to all the players in the train" : "send a title to players in a train").setTraincartsWIKIHelp("TrainCarts/Signs/Title").handle(event.getPlayer());
    }

    private static class TitleMessage {
        public String title;
        public String subtitle;
        public int fadeIn = 10;
        public int stay = 70;
        public int fadeOut = 10;
        private final Pattern TITLE_PATTERN = Pattern.compile("title\\s(\\d+)(?:\\s(\\d+))?(?:\\s(\\d+))?");

        public TitleMessage(SignActionEvent info) {
            this.title = TrainCarts.getMessage(info.getLine(2));
            this.subtitle = TrainCarts.getMessage(info.getLine(3));
            Matcher matcher = this.TITLE_PATTERN.matcher(info.getLine(1));
            if (matcher.find()) {
                this.fadeIn = ParseUtil.parseInt((String)matcher.group(1), (int)10);
                this.stay = ParseUtil.parseInt((String)matcher.group(2), (int)70);
                this.fadeOut = ParseUtil.parseInt((String)matcher.group(3), (int)10);
            }
        }
    }
}

