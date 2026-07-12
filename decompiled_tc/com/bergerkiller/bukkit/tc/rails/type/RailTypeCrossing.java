/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.rails.type;

import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogic;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicHorizontal;
import com.bergerkiller.bukkit.tc.rails.type.RailTypeHorizontal;
import com.bergerkiller.bukkit.tc.rails.type.RailTypeRegular;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class RailTypeCrossing
extends RailTypeHorizontal {
    @Override
    public boolean isRail(BlockData blockData) {
        return MaterialUtil.ISPRESSUREPLATE.get(blockData);
    }

    @Override
    public boolean hasBlockActivation(Block railBlock) {
        return true;
    }

    @Override
    public BlockFace getDirection(Block railBlock) {
        return Util.getPlateDirection(railBlock);
    }

    @Override
    public BlockFace[] getPossibleDirections(Block trackBlock) {
        BlockFace dir = this.getDirection(trackBlock);
        if (dir == BlockFace.SELF) {
            return FaceUtil.RADIAL;
        }
        return RailTypeRegular.getPossibleDirections(dir);
    }

    @Override
    public RailLogic getLogic(RailState state) {
        BlockFace dir = Util.getPlateDirection(state.railBlock());
        if (dir == BlockFace.SELF) {
            dir = FaceUtil.toRailsDirection((BlockFace)state.enterFace());
        }
        return RailLogicHorizontal.get(dir);
    }

    @Override
    public void onPostMove(MinecartMember<?> member) {
        super.onPostMove(member);
    }

    @Override
    public Location getSpawnLocation(Block railsBlock, BlockFace orientation) {
        BlockFace dir = Util.getPlateDirection(railsBlock);
        if (dir == BlockFace.SELF) {
            dir = orientation;
        }
        Location result = super.getSpawnLocation(railsBlock, dir);
        if (FaceUtil.isAlongX((BlockFace)dir)) {
            result.setYaw(0.0f);
        } else if (FaceUtil.isAlongZ((BlockFace)dir)) {
            result.setYaw(-90.0f);
        }
        return result;
    }
}

