/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SignActionEnter
extends TrainCartsSignAction {
    private static boolean canEnter(Entity entity, boolean enterPlayers, boolean enterMobs, boolean enterMisc) {
        if (entity instanceof Player) {
            return enterPlayers;
        }
        if (EntityUtil.isMob((Entity)entity)) {
            return enterMobs;
        }
        if (MinecartMemberStore.getFromEntity(entity) != null) {
            return false;
        }
        return enterMisc;
    }

    public SignActionEnter() {
        super("enter");
    }

    @Override
    public void execute(SignActionEvent info) {
        if (!info.isAction(SignActionType.REDSTONE_ON)) {
            if (info.isCartSign()) {
                if (!info.isAction(SignActionType.MEMBER_ENTER)) {
                    return;
                }
            } else if (info.isTrainSign()) {
                if (!info.isAction(SignActionType.GROUP_ENTER)) {
                    return;
                }
            } else {
                return;
            }
        }
        if (!info.isPowered()) {
            return;
        }
        double radiusXZ = Double.min(TCConfig.maxEnterDistance, ParseUtil.parseDouble((String)info.getLine(1), (double)2.0));
        double radiusY = 1.0;
        if (info.getLine(1).toLowerCase(Locale.ENGLISH).endsWith("s")) {
            radiusY = radiusXZ;
        }
        boolean enterPlayers = false;
        boolean enterMobs = false;
        boolean enterMisc = false;
        if (!info.getLine(2).isEmpty()) {
            String mode = info.getLine(2).toLowerCase(Locale.ENGLISH);
            if (mode.contains("mob")) {
                enterMobs = true;
            }
            if (mode.contains("player")) {
                enterPlayers = true;
            }
            if (mode.contains("misc")) {
                enterMisc = true;
            }
        } else {
            enterPlayers = true;
        }
        boolean aroundSign = ParseUtil.parseBool((String)info.getLine(3));
        Collection<MinecartMember<?>> members = info.getMembers();
        if (aroundSign) {
            Location center = info.hasRails() ? info.getRailLocation() : info.getLocation();
            for (Entity entity : WorldUtil.getNearbyEntities((Location)center, (double)radiusXZ, (double)radiusY, (double)radiusXZ)) {
                if (entity.getVehicle() != null || !SignActionEnter.canEnter(entity, enterPlayers, enterMobs, enterMisc)) continue;
                for (MinecartMember<?> member : members) {
                    if (member.getAvailableSeatCount(entity) > 0 && member.addPassengerForced(entity)) break;
                }
            }
        } else {
            block2: for (MinecartMember<?> member : members) {
                List nearby = ((CommonMinecart)member.getEntity()).getNearbyEntities(radiusXZ, radiusY, radiusXZ);
                while (!nearby.isEmpty()) {
                    double lastDistance = Double.MAX_VALUE;
                    Entity selectedEntity = null;
                    for (Entity entity : nearby) {
                        double distance;
                        if (entity.getVehicle() != null || !SignActionEnter.canEnter(entity, enterPlayers, enterMobs, enterMisc) || member.getAvailableSeatCount(entity) == 0 || !((distance = ((CommonMinecart)member.getEntity()).loc.distanceSquared(entity)) < lastDistance)) continue;
                        lastDistance = distance;
                        selectedEntity = entity;
                    }
                    if (selectedEntity == null) continue block2;
                    nearby.remove(selectedEntity);
                    member.addPassengerForced(selectedEntity);
                }
            }
        }
    }

    @Override
    public boolean canSupportRC() {
        return true;
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        return SignBuildOptions.create().setPermission(Permission.BUILD_ENTER).setName("train enter sign").setDescription("cause nearby players/mobs to enter the train").setTraincartsWIKIHelp("TrainCarts/Signs/Enter").handle(event);
    }
}

