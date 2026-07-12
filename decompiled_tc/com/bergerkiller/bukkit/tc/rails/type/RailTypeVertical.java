/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.material.MaterialData
 *  org.bukkit.material.Rails
 */
package com.bergerkiller.bukkit.tc.rails.type;

import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogic;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicVertical;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicVerticalSlopeNormalB;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicVerticalSlopeUpsideDownB;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicVerticalSlopeUpsideDownC;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicVerticalSlopeUpsideDownD;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Rails;

public class RailTypeVertical
extends RailType {
    private static final BlockFace[] SIGN_TRIGGER_DIRS = new BlockFace[]{BlockFace.UP, BlockFace.DOWN};

    @Override
    public boolean isRail(BlockData blockData) {
        return Util.ISVERTRAIL.get(blockData);
    }

    @Override
    public Block findRail(Block pos) {
        if (this.isRail(pos)) {
            return pos;
        }
        Block below = pos.getRelative(BlockFace.DOWN);
        if (this.isRail(below) && this.getAfterSlope(below) != null) {
            return below;
        }
        Block above = pos.getRelative(BlockFace.UP);
        if (this.isRail(above) && this.isVerticalSlopeUpsideDownB(above)) {
            return above;
        }
        Block twoAbove = pos.getRelative(0, 2, 0);
        if (this.isRail(twoAbove) && this.isVerticalSlopeUpsideDownB(twoAbove)) {
            return twoAbove;
        }
        return null;
    }

    @Override
    public Block findMinecartPos(Block trackBlock) {
        return trackBlock;
    }

    @Override
    public boolean onBlockCollision(MinecartMember<?> member, Block railsBlock, Block hitBlock, BlockFace hitFace) {
        if (!super.onBlockCollision(member, railsBlock, hitBlock, hitFace)) {
            return false;
        }
        Block minecartPos = this.findMinecartPos(railsBlock);
        if (hitBlock.getX() != minecartPos.getX() || hitBlock.getZ() != minecartPos.getZ()) {
            return false;
        }
        int dy = hitBlock.getY() - minecartPos.getY();
        if (dy < -1 || dy > 1) {
            return false;
        }
        return !this.isRail(hitBlock);
    }

    @Override
    public boolean isHeadOnCollision(MinecartMember<?> member, Block railsBlock, Block hitBlock) {
        if (super.isHeadOnCollision(member, railsBlock, hitBlock)) {
            return true;
        }
        Block minecartPos = this.findMinecartPos(railsBlock);
        return hitBlock.getY() - minecartPos.getY() == member.getDirectionTo().getModY();
    }

    @Override
    public BlockFace[] getPossibleDirections(Block trackBlock) {
        return new BlockFace[]{BlockFace.UP, BlockFace.DOWN};
    }

    @Override
    public boolean onCollide(MinecartMember<?> with, Block block, BlockFace hitFace) {
        return false;
    }

    @Override
    public BlockFace getDirection(Block railsBlock) {
        return BlockFace.UP;
    }

    @Override
    public BlockFace getSignColumnDirection(Block railsBlock) {
        return Util.getVerticalRailDirection(railsBlock);
    }

    @Override
    public BlockFace[] getSignTriggerDirections(Block railBlock, Block signBlock, BlockFace signFacing) {
        return SIGN_TRIGGER_DIRS;
    }

    @Override
    public RailLogic getLogic(RailState state) {
        BlockFace dir = Util.getVerticalRailDirection(state.railBlock());
        if (this.isVerticalSlopeUpsideDown(state.railBlock())) {
            if (state.railPosition().getY() < 0.0 && this.isVerticalSlopeUpsideDownB(state.railBlock())) {
                return RailLogicVerticalSlopeUpsideDownB.get(dir.getOppositeFace());
            }
            if (this.isVerticalSlopeUpsideDownB(state.railBlock())) {
                return RailLogicVerticalSlopeUpsideDownD.get(dir.getOppositeFace());
            }
            return RailLogicVerticalSlopeUpsideDownC.get(dir.getOppositeFace());
        }
        if (this.isVerticalSlopeUpsideDownB(state.railBlock())) {
            return RailLogicVerticalSlopeUpsideDownB.get(dir.getOppositeFace());
        }
        if (this.getAfterSlope(state.railBlock()) != null) {
            return RailLogicVerticalSlopeNormalB.get(dir);
        }
        return RailLogicVertical.get(dir);
    }

    @Override
    public Location getSpawnLocation(Block railsBlock, BlockFace orientation) {
        BlockFace dir = Util.getVerticalRailDirection(railsBlock);
        double dx = 0.5 + 0.4375 * (double)dir.getModX();
        double dz = 0.5 + 0.4375 * (double)dir.getModZ();
        return new Location(railsBlock.getWorld(), (double)railsBlock.getX() + dx, (double)railsBlock.getY() + 0.5, (double)railsBlock.getZ() + dz, (float)FaceUtil.faceToYaw((BlockFace)dir), -90.0f);
    }

    private boolean isVerticalSlopeUpsideDown(Block railsBlock) {
        Block above = railsBlock.getRelative(BlockFace.UP);
        return this.isUpsideDownRail(above);
    }

    private boolean isVerticalSlopeUpsideDownB(Block railsBlock) {
        BlockFace dir = Util.getVerticalRailDirection(railsBlock);
        Block slopeBlock = railsBlock.getRelative(dir.getModX(), -1, dir.getModZ());
        return this.isUpsideDownRail(slopeBlock);
    }

    private boolean isUpsideDownRail(Block railsBlock) {
        MaterialData materialData = WorldUtil.getBlockData((Block)railsBlock).getMaterialData();
        if (materialData instanceof Rails) {
            return RailType.REGULAR.isUpsideDown(railsBlock, (Rails)materialData);
        }
        return false;
    }

    private Block getAfterSlope(Block verticalRail) {
        if (!this.isRail(verticalRail)) {
            return null;
        }
        Block above = verticalRail.getRelative(BlockFace.UP);
        if (BlockUtil.isSolid((Block)above)) {
            return null;
        }
        BlockFace dir = Util.getVerticalRailDirection(verticalRail);
        Block possible = above.getRelative(dir);
        RailPiece railPiece = RailType.findRailPiece(possible);
        if (railPiece != null && LogicUtil.contains((Object)dir.getOppositeFace(), (Object[])railPiece.type().getPossibleDirections(railPiece.block()))) {
            return railPiece.block();
        }
        return null;
    }
}

