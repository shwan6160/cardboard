/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.resources.ResourceKey
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  com.bergerkiller.bukkit.common.utils.PlayerUtil
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SignActionSound
extends TrainCartsSignAction {
    public SignActionSound() {
        super("sound", "msound");
    }

    public ResourceKey<SoundEffect> getSound(SignActionEvent info) {
        try {
            return SoundEffect.fromName((String)(info.getLine(2) + info.getLine(3)));
        }
        catch (Throwable t) {
            return null;
        }
    }

    @Override
    public void execute(SignActionEvent info) {
        boolean move = info.isType("msound");
        if (!info.isPowered()) {
            return;
        }
        SoundArgs args = new SoundArgs();
        args.sound = this.getSound(info);
        if (args.sound == null) {
            return;
        }
        Object str_args = StringUtil.getAfter((String)info.getLine(1), (String)" ").trim().split(" ", -1);
        for (int i = 0; i < ((String[])str_args).length; ++i) {
            if (!str_args[i].equalsIgnoreCase("in")) continue;
            args.onlyInside = true;
            str_args = StringUtil.remove((String[])str_args, (int)i);
            --i;
        }
        try {
            if (((String[])str_args).length >= 1) {
                args.pitch = (float)ParseUtil.parseDouble((String)str_args[0], (double)1.0);
            }
            if (((String[])str_args).length == 2) {
                args.volume = (float)ParseUtil.parseDouble((String)str_args[1], (double)1.0);
            }
        }
        catch (NumberFormatException i) {
            // empty catch block
        }
        if (info.isAction(SignActionType.MEMBER_MOVE)) {
            if (move) {
                if (info.isTrainSign()) {
                    for (MinecartMember member : info.getGroup()) {
                        args.play(member);
                    }
                } else if (info.isCartSign()) {
                    args.play(info.getMember());
                }
            }
            return;
        }
        if (info.isTrainSign() && info.isAction(SignActionType.REDSTONE_ON, SignActionType.GROUP_ENTER) && info.hasGroup()) {
            for (MinecartMember member : info.getGroup()) {
                args.play(member);
            }
        } else if (info.isCartSign() && info.isAction(SignActionType.REDSTONE_ON, SignActionType.MEMBER_ENTER) && info.hasMember()) {
            args.play(info.getMember());
        } else if (info.isRCSign() && info.isAction(SignActionType.REDSTONE_ON)) {
            for (MinecartGroup group : info.getRCTrainGroups()) {
                for (MinecartMember<?> member : group) {
                    args.play(member);
                }
            }
        } else if (info.isAction(SignActionType.REDSTONE_ON)) {
            Location location = info.hasRails() ? info.getCenterLocation() : info.getLocation().add(0.0, 2.0, 0.0);
            args.play(location);
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        if (this.getSound(event) == null) {
            event.getPlayer().sendMessage(ChatColor.RED + "Sound name '" + event.getLine(2) + event.getLine(3) + "' is invalid!");
            return false;
        }
        String app = event.isType("msound") ? " while moving" : "";
        SignBuildOptions opt = SignBuildOptions.create().setPermission(Permission.BUILD_SOUND).setName(event.isCartSign() ? "cart sound player" : "train sound player").setTraincartsWIKIHelp("TrainCarts/Signs/Sound");
        if (event.isCartSign()) {
            opt.setDescription("play a sound in the minecart" + app);
        } else if (event.isTrainSign()) {
            opt.setDescription("play a sound in all minecarts of the train" + app);
        } else if (event.isRCSign()) {
            opt.setDescription("play a sound in all minecarts of the train" + app);
        }
        return opt.handle(event);
    }

    @Override
    public boolean isMemberMoveHandled(SignActionEvent info) {
        return info.isType("msound");
    }

    @Override
    public boolean canSupportRC() {
        return true;
    }

    private static class SoundArgs {
        public float volume = 1.0f;
        public float pitch = 1.0f;
        public boolean onlyInside = false;
        public ResourceKey<SoundEffect> sound;

        private SoundArgs() {
        }

        public void play(MinecartMember<?> member) {
            if (this.onlyInside) {
                for (Player passenger : ((CommonMinecart)member.getEntity()).getPlayerPassengers()) {
                    PlayerUtil.playSound((Player)passenger, (Location)passenger.getEyeLocation(), this.sound, (float)this.volume, (float)this.pitch);
                }
            } else {
                this.play(((CommonMinecart)member.getEntity()).getLocation());
            }
        }

        public void play(Location location) {
            WorldUtil.playSound((Location)location, this.sound, (float)this.volume, (float)this.pitch);
        }
    }
}

