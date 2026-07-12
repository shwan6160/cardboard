/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.material.Rails
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.rails.type;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Rails;
import org.bukkit.util.Vector;

public abstract class RailTypeHorizontal
extends RailType {
    @Override
    public abstract BlockFace getDirection(Block var1);

    @Override
    public Block findMinecartPos(Block trackBlock) {
        if (this.isUpsideDown(trackBlock)) {
            return trackBlock.getRelative(BlockFace.DOWN);
        }
        return trackBlock;
    }

    @Override
    public boolean onBlockCollision(MinecartMember<?> member, Block railsBlock, Block hitBlock, BlockFace hitFace) {
        if (!super.onBlockCollision(member, railsBlock, hitBlock, hitFace)) {
            return false;
        }
        boolean upsideDown = this.isUpsideDown(railsBlock);
        Block posBlock = this.findMinecartPos(railsBlock);
        int dx = hitBlock.getX() - posBlock.getX();
        int dy = hitBlock.getY() - posBlock.getY();
        int dz = hitBlock.getZ() - posBlock.getZ();
        if (dx < -1 || dx > 1 || dy < -1 || dy > 1 || dz < -1 || dz > 1) {
            return false;
        }
        if (upsideDown) {
            BlockFace railDir = this.getDirection(railsBlock);
            Block blockFwd = posBlock.getRelative(railDir);
            if (BlockUtil.equals((Block)posBlock, (Block)hitBlock) || BlockUtil.equals((Block)blockFwd, (Block)hitBlock)) {
                return true;
            }
            Block blockBwd = posBlock.getRelative(railDir.getOppositeFace());
            if (BlockUtil.equals((Block)blockBwd, (Block)hitBlock)) {
                if (!member.isOnSlope()) {
                    return true;
                }
                if (!RailType.VERTICAL.isRail(posBlock.getRelative(BlockFace.DOWN)) && !RailType.VERTICAL.isRail(blockBwd.getRelative(railDir))) {
                    return true;
                }
            }
            if (member.isOnSlope() && dx == 0 && dy == -1 && dz == 0) {
                return true;
            }
            return member.isOnSlope() && railDir.getModX() == -dx && railDir.getModZ() == -dz && dy == -1;
        }
        if (hitBlock.getY() < posBlock.getY()) {
            return false;
        }
        hitFace = FaceUtil.getDirection((Block)hitBlock, (Block)posBlock, (boolean)false);
        BlockFace hitToFace = hitFace.getOppositeFace();
        if (posBlock.getY() == hitBlock.getY()) {
            if (Math.abs(dx) > 0 && Math.abs(dz) > 0) {
                BlockFace railDir = this.getDirection(railsBlock);
                if (FaceUtil.isSubCardinal((BlockFace)railDir)) {
                    BlockFace f = FaceUtil.rotate((BlockFace)railDir, (int)2);
                    BlockFace hitDir = null;
                    if (f.getModX() == dx && f.getModZ() == dz) {
                        hitDir = FaceUtil.rotate((BlockFace)railDir, (int)3);
                    } else if (f.getModX() == -dx && f.getModZ() == -dz) {
                        hitDir = FaceUtil.rotate((BlockFace)railDir, (int)-3);
                    }
                    if (hitDir != null) {
                        Block dirBlock = railsBlock.getRelative(hitDir);
                        RailType dirRail = RailType.getType(dirBlock);
                        if (dirRail == RailType.NONE) {
                            dirBlock = dirBlock.getRelative(BlockFace.DOWN);
                            dirRail = RailType.getType(dirBlock);
                        }
                        if (dirRail != RailType.NONE) {
                            Block nextPosBlock = Util.getNextPos(dirBlock, hitDir);
                            if (nextPosBlock != null && hitBlock.equals((Object)nextPosBlock)) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                    return false;
                }
                return false;
            }
            Object[] possible = this.getPossibleDirections(railsBlock);
            if (!LogicUtil.contains((Object)hitToFace, (Object[])possible)) {
                return false;
            }
        }
        if (member.isOnSlope()) {
            if (hitBlock.getX() == posBlock.getX() && hitBlock.getZ() == posBlock.getZ() && dy >= 2) {
                return false;
            }
            BlockFace railDirection = this.getDirection(railsBlock);
            if (hitToFace == railDirection) {
                Block above;
                if (Util.isVerticalAbove(posBlock, railDirection)) {
                    return false;
                }
                if (posBlock.getY() == hitBlock.getY() && !BlockUtil.isSolid((Block)(above = hitBlock.getRelative(BlockFace.UP)))) {
                    return false;
                }
            }
            if (FaceUtil.isAlongX((BlockFace)railDirection) && dz != 0) {
                return false;
            }
            if (FaceUtil.isAlongZ((BlockFace)railDirection) && dx != 0) {
                return false;
            }
            if (!TCConfig.enableCeilingBlockCollision) {
                IntVector3 diff = new IntVector3(hitBlock).subtract(posBlock.getX(), posBlock.getY(), posBlock.getZ());
                if (diff.x == hitToFace.getModX() && diff.z == hitToFace.getModZ() && (diff.y > 1 || diff.y == 1 && railDirection != hitToFace)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Location getSpawnLocation(Block railsBlock, BlockFace orientation) {
        BlockFace[] faces = this.getPossibleDirections(railsBlock);
        if (faces != null && faces.length >= 2) {
            if (faces[0] == faces[1].getOppositeFace()) {
                if (orientation != faces[0] && orientation != faces[1]) {
                    orientation = faces[0];
                }
            } else {
                int diff_b;
                BlockFace direction = FaceUtil.combine((BlockFace)faces[0], (BlockFace)faces[1]);
                int diff_a = FaceUtil.getFaceYawDifference((BlockFace)(direction = FaceUtil.rotate((BlockFace)direction, (int)2)), (BlockFace)orientation);
                orientation = diff_a < (diff_b = FaceUtil.getFaceYawDifference((BlockFace)direction.getOppositeFace(), (BlockFace)orientation)) ? direction : direction.getOppositeFace();
            }
        }
        Location at = this.findMinecartPos(railsBlock).getLocation();
        at.setDirection(FaceUtil.faceToVector((BlockFace)orientation));
        at.setYaw(at.getYaw() - 90.0f);
        if (this.isUpsideDown(railsBlock)) {
            at.add(0.5, 0.9375, 0.5);
            at.setPitch(-180.0f);
        } else {
            at.add(0.5, 0.0625, 0.5);
            at.setPitch(0.0f);
        }
        return at;
    }

    @Override
    public boolean isHeadOnCollision(MinecartMember<?> member, Block railsBlock, Block hitBlock) {
        if (super.isHeadOnCollision(member, railsBlock, hitBlock)) {
            return true;
        }
        Block minecartPos = this.findMinecartPos(railsBlock);
        IntVector3 delta = new IntVector3(hitBlock).subtract(new IntVector3(minecartPos));
        BlockFace direction = FaceUtil.getDirection((Vector)((CommonMinecart)member.getEntity()).getVelocity(), (boolean)false);
        if (delta.x == direction.getModX() && delta.z == direction.getModZ()) {
            return true;
        }
        if (member.isOnSlope() && delta.x == 0 && delta.z == 0 && delta.y == 1 && direction == this.getDirection(railsBlock)) {
            return true;
        }
        return delta.x == 0 && delta.z == 0 && delta.y == 0 && this.isUpsideDown(railsBlock);
    }

    @Override
    public BlockFace getSignColumnDirection(Block railsBlock) {
        if (this.isUpsideDown(railsBlock)) {
            return BlockFace.UP;
        }
        return BlockFace.DOWN;
    }

    @Override
    public BlockFace[] getSignTriggerDirections(Block railBlock, Block signBlock, BlockFace signFacing) {
        signFacing = Util.snapFace(signFacing);
        RailPiece rail = RailPiece.create(this, railBlock);
        HashSet<BlockFace> watchedFaces = new HashSet<BlockFace>(4);
        if (FaceUtil.isSubCardinal((BlockFace)signFacing)) {
            BlockFace[] faces;
            for (BlockFace face : faces = FaceUtil.getFaces((BlockFace)signFacing)) {
                if (!Util.isConnectedRailsFrom(rail, face)) continue;
                watchedFaces.add(face.getOppositeFace());
            }
            if (watchedFaces.isEmpty()) {
                for (BlockFace face : faces) {
                    if (!Util.isConnectedRailsFrom(rail, face.getOppositeFace())) continue;
                    watchedFaces.add(face);
                }
            }
        } else {
            BlockFace facing;
            Rails rails = Util.getRailsRO(railBlock);
            if (rails != null && rails.isOnSlope()) {
                watchedFaces.add(BlockFace.UP);
                watchedFaces.add(BlockFace.DOWN);
            }
            if (Util.isConnectedRailsFrom(rail, facing = signFacing)) {
                watchedFaces.add(facing.getOppositeFace());
            } else if (Util.isConnectedRailsFrom(rail, facing.getOppositeFace())) {
                watchedFaces.add(facing);
            } else {
                watchedFaces.add(FaceUtil.rotate((BlockFace)facing, (int)-2));
                watchedFaces.add(FaceUtil.rotate((BlockFace)facing, (int)2));
            }
        }
        return watchedFaces.toArray(new BlockFace[watchedFaces.size()]);
    }

    @Override
    public Block findRail(Block pos) {
        Block tmp = pos;
        if (this.isRail(tmp)) {
            return tmp;
        }
        tmp = pos.getRelative(0, -1, 0);
        if (this.isRail(tmp) && !this.isUpsideDown(tmp)) {
            return tmp;
        }
        tmp = pos.getRelative(0, 1, 0);
        if (this.isRail(tmp) && this.isUpsideDown(tmp)) {
            return tmp;
        }
        tmp = pos.getRelative(0, 2, 0);
        if (this.isRail(tmp) && this.isUpsideDown(tmp)) {
            return tmp;
        }
        return null;
    }
}

