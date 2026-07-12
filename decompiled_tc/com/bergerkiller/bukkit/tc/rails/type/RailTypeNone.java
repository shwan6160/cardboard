/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.rails.type;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailJunction;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogic;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicAir;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicGround;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class RailTypeNone
extends RailType {
    @Override
    public boolean onCollide(MinecartMember<?> with, Block block, BlockFace hitFace) {
        double dx = ((CommonMinecart)with.getEntity()).loc.getX() - (double)block.getX();
        double dy = ((CommonMinecart)with.getEntity()).loc.getY() - (double)block.getY();
        double dz = ((CommonMinecart)with.getEntity()).loc.getZ() - (double)block.getZ();
        double vx = ((CommonMinecart)with.getEntity()).vel.getX();
        double vy = ((CommonMinecart)with.getEntity()).vel.getY();
        double vz = ((CommonMinecart)with.getEntity()).vel.getZ();
        double VEL_LIMIT = 0.05;
        if (vy < -0.05 && dx < 0.0 && vx < -0.05 || dx > 1.0 && vx > 0.05 || dz < 0.0 && vz < -0.05 || dz > 1.0 && vz > 0.05) {
            return false;
        }
        return !(vy > 0.05 && dy < -0.5 && dx < 0.0 && vx < -0.05 || dx > 1.0 && vx > 0.05 || dz < 0.0 && vz < -0.05) && (!(dz > 1.0) || !(vz > 0.05));
    }

    @Override
    public boolean isRail(BlockData blockData) {
        return false;
    }

    @Override
    public Block findMinecartPos(Block trackBlock) {
        return trackBlock;
    }

    @Override
    public BlockFace[] getPossibleDirections(Block trackBlock) {
        return new BlockFace[0];
    }

    @Override
    public Block findRail(Block pos) {
        return pos;
    }

    @Override
    public List<RailJunction> getJunctions(Block railBlock) {
        return Collections.emptyList();
    }

    @Override
    public BlockFace getDirection(Block railsBlock) {
        return BlockFace.SELF;
    }

    @Override
    public BlockFace getSignColumnDirection(Block railsBlock) {
        return BlockFace.SELF;
    }

    @Override
    public Location getSpawnLocation(Block railsBlock, BlockFace orientation) {
        Location loc = railsBlock.getLocation();
        loc.setX(0.5);
        loc.setY(0.5);
        loc.setZ(0.5);
        loc.setDirection(FaceUtil.faceToVector((BlockFace)orientation));
        return loc;
    }

    @Override
    public RailLogic getLogic(RailState state) {
        MinecartMember<?> member = state.member();
        if (member == null || member.isFlying()) {
            return RailLogicAir.INSTANCE;
        }
        return RailLogicGround.INSTANCE;
    }

    public String toString() {
        return "NONE";
    }
}

