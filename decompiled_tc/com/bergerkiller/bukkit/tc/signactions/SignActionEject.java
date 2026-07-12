/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.block.BlockFace
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import java.util.Locale;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SignActionEject
extends TrainCartsSignAction {
    public SignActionEject() {
        super("eject");
    }

    @Override
    public boolean click(SignActionEvent info, Player player) {
        MinecartMember<?> member = MinecartMemberStore.getFromEntity(player.getVehicle());
        if (member == null) {
            return false;
        }
        info.setMember(member);
        this.eject(info);
        return true;
    }

    public void eject(SignActionEvent info) {
        boolean hasSettings = !info.getLine(2).isEmpty() || !info.getLine(3).isEmpty();
        Vector offset = new Vector();
        float yaw = 0.0f;
        float pitch = 0.0f;
        if (hasSettings) {
            boolean isAbsolute = info.getLine(1).toLowerCase(Locale.ENGLISH).contains(" at");
            offset = Util.parseVector(info.getLine(2), null);
            if (offset == null) {
                isAbsolute = false;
                offset = new Vector();
            } else if (!isAbsolute && offset.length() > TCConfig.maxEjectDistance) {
                offset.normalize().multiply(TCConfig.maxEjectDistance);
            }
            boolean retainEntityRotation = false;
            if (!info.getLine(3).isEmpty()) {
                String[] angletext = Util.splitBySeparator(info.getLine(3));
                if (angletext.length == 2) {
                    yaw = ParseUtil.parseFloat((String)angletext[0], (float)0.0f);
                    pitch = ParseUtil.parseFloat((String)angletext[1], (float)0.0f);
                } else if (angletext.length == 1) {
                    yaw = ParseUtil.parseFloat((String)angletext[0], (float)0.0f);
                }
            } else {
                retainEntityRotation = true;
            }
            if (!isAbsolute) {
                float signyawoffset = FaceUtil.faceToYaw((BlockFace)info.getFacing().getOppositeFace());
                offset = MathUtil.rotate((float)signyawoffset, (float)0.0f, (Vector)offset);
                yaw += signyawoffset + 90.0f;
            }
            if (isAbsolute) {
                Location at = new Location(info.getWorld(), offset.getX(), offset.getY(), offset.getZ(), yaw, pitch);
                for (MinecartMember<?> mm : info.getMembers()) {
                    mm.eject(at, retainEntityRotation);
                }
            } else if (retainEntityRotation) {
                for (MinecartMember<?> mm : info.getMembers()) {
                    mm.eject(offset);
                }
            } else {
                for (MinecartMember<?> mm : info.getMembers()) {
                    mm.eject(offset, yaw, pitch);
                }
            }
        } else {
            for (MinecartMember<?> mm : info.getMembers()) {
                mm.eject();
            }
        }
    }

    @Override
    public void execute(SignActionEvent info) {
        boolean isRemote = false;
        if (!(info.isCartSign() && info.isAction(SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON) || info.isTrainSign() && info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON))) {
            if (info.isRCSign() && info.isAction(SignActionType.REDSTONE_ON)) {
                isRemote = true;
            } else {
                return;
            }
        }
        if (isRemote || info.hasMember() && info.isPowered()) {
            this.eject(info);
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        if (event.getLine(1).toLowerCase(Locale.ENGLISH).contains(" at") && !Permission.BUILD_EJECTOR_ABSOLUTE.handleMsg((CommandSender)event.getPlayer(), ChatColor.RED + "You do not have permission to build eject signs that teleport to world coordinates")) {
            return false;
        }
        return SignBuildOptions.create().setPermission(Permission.BUILD_EJECTOR).setName("train ejector").setDescription("eject the passengers of a " + (event.isRCSign() ? "remote train" : "train")).setTraincartsWIKIHelp("TrainCarts/Signs/Ejector").handle(event);
    }

    @Override
    public boolean canSupportRC() {
        return true;
    }
}

