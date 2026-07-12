/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.utils.FormattedSpeed;
import com.bergerkiller.bukkit.tc.utils.LauncherConfig;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import org.bukkit.block.BlockFace;

public class SignActionLauncher
extends TrainCartsSignAction {
    public SignActionLauncher() {
        super("launch");
    }

    @Override
    public void execute(SignActionEvent info) {
        Direction direction;
        int launchEndIdx;
        if (!info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON) || !info.isPowered()) {
            return;
        }
        FormattedSpeed velocity = null;
        FormattedSpeed speedLimitToSet = null;
        String speedStr = info.getLine(2).trim();
        int spaceIdx = speedStr.indexOf(32);
        if (spaceIdx != -1) {
            FormattedSpeed speed = FormattedSpeed.parse(speedStr.substring(0, spaceIdx).trim(), null);
            FormattedSpeed energy = FormattedSpeed.parse(speedStr.substring(spaceIdx + 1).trim(), null);
            if (speed != null && energy != null) {
                speedLimitToSet = speed;
                velocity = FormattedSpeed.of(speed.getValue() + energy.getValue());
            }
        }
        if (velocity == null) {
            velocity = FormattedSpeed.parse(speedStr, FormattedSpeed.of(TCConfig.launchForce));
        }
        String launchConfigStr = (launchEndIdx = info.getLine(1).indexOf(32)) == -1 ? "" : info.getLine(1).substring(launchEndIdx + 1);
        LauncherConfig launchConfig = LauncherConfig.parse(launchConfigStr);
        if (info.isRCSign()) {
            direction = Direction.parse(info.getLine(3));
            for (MinecartGroup group : info.getRCTrainGroups()) {
                BlockFace cartDirection = group.head().getDirection();
                BlockFace directionFace = direction.getDirectionLegacy(cartDirection, cartDirection);
                this.initiateLaunch(group.head(), group, directionFace, launchConfig, velocity, speedLimitToSet);
            }
        } else if (info.hasRailedMember()) {
            direction = Direction.parse(info.getLine(3)).getDirectionLegacy(info.getFacing(), info.getCartEnterFace());
            if (!launchConfig.isValid()) {
                launchConfig.setDistance(Util.calculateStraightLength(info.getRails(), (BlockFace)direction));
            }
            this.initiateLaunch(info.getMember(), info.getGroup(), (BlockFace)direction, launchConfig, velocity, speedLimitToSet);
        }
    }

    private void initiateLaunch(MinecartMember<?> member, MinecartGroup group, BlockFace direction, LauncherConfig launchConfig, FormattedSpeed launchSpeed, FormattedSpeed speedLimitToSet) {
        double launchVelocityABS = launchSpeed.getValue();
        if (launchSpeed.isRelative()) {
            launchVelocityABS += member.getRealSpeed();
        }
        group.getActions().clear();
        if (speedLimitToSet != null) {
            double speedLimitValue = speedLimitToSet.getValue();
            if (speedLimitToSet.isRelative()) {
                speedLimitValue += group.getProperties().getSpeedLimit();
            }
            if (speedLimitValue < 0.0) {
                speedLimitValue = 0.0;
            }
            member.getActions().addActionLaunch(direction, launchConfig, launchVelocityABS, speedLimitValue);
        } else {
            member.getActions().addActionLaunch(direction, launchConfig, launchVelocityABS);
        }
    }

    @Override
    public boolean canSupportRC() {
        return true;
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        return SignBuildOptions.create().setPermission(Permission.BUILD_LAUNCHER).setName("launcher").setDescription("launch (or brake) trains at a desired speed").setTraincartsWIKIHelp("TrainCarts/Signs/Launcher").handle(event);
    }
}

