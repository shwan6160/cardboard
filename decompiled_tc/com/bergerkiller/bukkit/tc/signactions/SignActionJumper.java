/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class SignActionJumper
extends TrainCartsSignAction {
    public static void jump(MinecartMember<?> member, Vector offset) {
        ((CommonMinecart)member.getEntity()).vel.set(offset);
    }

    public SignActionJumper() {
        super("jump");
    }

    @Override
    public void execute(SignActionEvent info) {
        boolean isTrain;
        if (!info.isPowered() || !info.hasMember()) {
            return;
        }
        boolean isCart = info.isCartSign() && info.isAction(SignActionType.MEMBER_ENTER);
        boolean bl = isTrain = info.isTrainSign() && info.isAction(SignActionType.GROUP_ENTER);
        if (!isCart && !isTrain) {
            return;
        }
        Vector offset = Util.parseVector(info.getLine(2), new Vector(0.0, 0.0, 0.0));
        if (offset.lengthSquared() == 0.0) {
            return;
        }
        float yaw = FaceUtil.faceToYaw((BlockFace)info.getFacing().getOppositeFace());
        offset = MathUtil.rotate((float)yaw, (float)0.0f, (Vector)offset);
        if (isCart) {
            SignActionJumper.jump(info.getMember(), offset);
        } else {
            for (MinecartMember<?> member : info.getGroup()) {
                SignActionJumper.jump(member, offset.clone());
            }
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        SignBuildOptions opt = SignBuildOptions.create().setPermission(Permission.BUILD_JUMPER).setName(event.isCartSign() ? "cart jumper" : "train jumper");
        if (event.isTrainSign()) {
            opt.setDescription("cause a minecart to jump towards a certain direction");
        } else {
            opt.setDescription("cause an entire train to jump towards a certain direction");
        }
        return opt.handle(event);
    }
}

