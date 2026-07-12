/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.event.Event
 *  org.bukkit.event.block.BlockRedstoneEvent
 *  org.bukkit.material.MaterialData
 *  org.bukkit.material.PoweredRail
 *  org.bukkit.material.Rails
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.TrainCarts;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PoweredRail;
import org.bukkit.material.Rails;

public class PoweredTrackLogic {
    private final Material railType;

    public PoweredTrackLogic(Material railType) {
        this.railType = railType;
    }

    public void updateRedstone(Block railsBlock) {
        boolean newPowered;
        BlockData railsBlockData = WorldUtil.getBlockData((Block)railsBlock);
        if (!(railsBlockData.getMaterialData() instanceof PoweredRail)) {
            return;
        }
        PoweredRail rails = (PoweredRail)railsBlockData.newMaterialData();
        boolean oldPowered = rails.isPowered();
        if (oldPowered != (newPowered = this.checkPowered(railsBlock))) {
            BlockRedstoneEvent redstoneEvent = new BlockRedstoneEvent(railsBlock, oldPowered ? 15 : 0, newPowered ? 15 : 0);
            CommonUtil.callEvent((Event)redstoneEvent);
            if (redstoneEvent.getNewCurrent() > 0 == oldPowered) {
                return;
            }
            rails.setPowered(newPowered);
            WorldUtil.setBlockDataFast((Block)railsBlock, (BlockData)BlockData.fromMaterialData((MaterialData)rails));
            WorldUtil.queueBlockSend((Block)railsBlock);
            TrainCarts.plugin.applyBlockPhysics(railsBlock.getRelative(rails.getDirection()), railsBlockData);
            TrainCarts.plugin.applyBlockPhysics(railsBlock.getRelative(rails.getDirection().getOppositeFace()), railsBlockData);
        }
    }

    public boolean checkPowered(Block railsBlock) {
        World world = railsBlock.getWorld();
        IntVector3 blockposition = new IntVector3(railsBlock);
        BlockData iblockdata = WorldUtil.getBlockData((Block)railsBlock);
        return railsBlock.isBlockIndirectlyPowered() || this.checkEnd(world, blockposition, iblockdata, true, 0) || this.checkEnd(world, blockposition, iblockdata, false, 0);
    }

    public boolean checkEnd(World world, IntVector3 blockposition, BlockData iblockdata, boolean directionMode, int iterCtr) {
        boolean isSlopeUp;
        MaterialData data = iblockdata.getMaterialData();
        if (!(data instanceof Rails)) {
            return false;
        }
        Rails rails = (Rails)data;
        BlockFace railDirection = rails.getDirection();
        BlockFace checkDirection = FaceUtil.isAlongX((BlockFace)railDirection) ? BlockFace.EAST : BlockFace.SOUTH;
        BlockFace walkDirection = directionMode ? checkDirection.getOppositeFace() : checkDirection;
        IntVector3 nextPos = blockposition.add(walkDirection);
        boolean bl = isSlopeUp = rails.isOnSlope() && railDirection == walkDirection;
        if (isSlopeUp) {
            nextPos = nextPos.add(BlockFace.UP);
        }
        if (this.checkStep(world, nextPos, directionMode, iterCtr, checkDirection)) {
            return true;
        }
        return !isSlopeUp && this.checkStep(world, nextPos.add(BlockFace.DOWN), directionMode, iterCtr, checkDirection);
    }

    public boolean checkStep(World world, IntVector3 blockposition, boolean directionMode, int iterCtr, BlockFace walkDirection) {
        BlockData iblockdata = WorldUtil.getBlockData((World)world, (IntVector3)blockposition);
        if (!iblockdata.isType(this.railType)) {
            return false;
        }
        MaterialData blockData = iblockdata.getMaterialData();
        if (!(blockData instanceof Rails)) {
            return false;
        }
        Rails rails = (Rails)blockData;
        BlockFace railDirection = rails.getDirection();
        if (FaceUtil.isAlongX((BlockFace)walkDirection) != FaceUtil.isAlongX((BlockFace)railDirection)) {
            return false;
        }
        if (!(blockData instanceof PoweredRail) || !((PoweredRail)blockData).isPowered()) {
            return false;
        }
        if (blockposition.toBlock(world).isBlockIndirectlyPowered()) {
            return true;
        }
        if (++iterCtr >= 8) {
            return false;
        }
        return this.checkEnd(world, blockposition, iblockdata, directionMode, iterCtr);
    }
}

