/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.event.block.BlockPhysicsEvent
 *  org.bukkit.material.Rails
 */
package com.bergerkiller.bukkit.tc.rails.type;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.rails.type.RailTypeRegular;
import com.bergerkiller.bukkit.tc.utils.PoweredTrackLogic;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.material.Rails;

public class RailTypePowered
extends RailTypeRegular {
    public static final double START_BOOST = 0.02;
    private final boolean isPowered;

    protected RailTypePowered(boolean isPowered) {
        this.isPowered = isPowered;
    }

    public boolean isPowered() {
        return this.isPowered;
    }

    @Override
    public String toString() {
        return "RailTypePowered{powered=" + this.isPowered + "}";
    }

    @Override
    public void onBlockPlaced(Block railsBlock) {
        super.onBlockPlaced(railsBlock);
        Rails rails = Util.getRailsRO(railsBlock);
        if (rails != null && this.isUpsideDown(railsBlock)) {
            BlockUtil.applyPhysics((Block)railsBlock.getRelative(rails.getDirection()), (Material)Material.POWERED_RAIL);
            BlockUtil.applyPhysics((Block)railsBlock.getRelative(rails.getDirection().getOppositeFace()), (Material)Material.POWERED_RAIL);
        }
    }

    @Override
    public void onBlockPhysics(BlockPhysicsEvent event) {
        super.onBlockPhysics(event);
        if (this.isUpsideDown(event.getBlock())) {
            PoweredTrackLogic logic = new PoweredTrackLogic(Material.POWERED_RAIL);
            logic.updateRedstone(event.getBlock());
        }
    }

    @Override
    public void onPreMove(MinecartMember<?> member) {
        if (member.isMovementControlled()) {
            return;
        }
        CommonMinecart entity = (CommonMinecart)member.getEntity();
        if (!this.isPowered) {
            if (entity.vel.xz.lengthSquared() < 9.0E-4) {
                entity.vel.multiply(0.0);
            } else if (TCConfig.legacySpeedLimiting) {
                entity.vel.multiply(0.5);
            } else {
                Rails rails = Util.getRailsRO(member.getBlock());
                if (rails == null || !rails.isOnSlope()) {
                    entity.vel.multiply(0.5);
                } else {
                    entity.vel.multiply(0.6464466095);
                }
            }
        } else {
            double motLength = entity.vel.xz.length();
            if (motLength > 0.01) {
                entity.vel.xz.add(entity.vel.xz, TCConfig.poweredRailBoost / motLength);
            } else {
                Block block = member.getBlock();
                BlockFace dir = this.getDirection(block);
                if (this.isUpsideDown(block)) {
                    block = block.getRelative(BlockFace.DOWN);
                }
                boolean pushFrom1 = BlockUtil.isSuffocating((Block)block.getRelative(dir.getOppositeFace()));
                boolean pushFrom2 = BlockUtil.isSuffocating((Block)block.getRelative(dir));
                if (pushFrom1 && pushFrom2) {
                    entity.vel.xz.setZero();
                } else if (pushFrom1 != pushFrom2) {
                    double boost = MathUtil.invert((double)0.02, (boolean)pushFrom2);
                    entity.vel.xz.set(boost * (double)dir.getModX(), boost * (double)dir.getModZ());
                }
            }
        }
    }

    @Override
    public boolean isRail(BlockData blockData) {
        return blockData.isType(RailTypeRegular.RailMaterials.POWERED) && (blockData.getRawData() & 8) == 8 == this.isPowered;
    }
}

