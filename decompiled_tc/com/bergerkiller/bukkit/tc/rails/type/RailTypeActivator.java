/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.event.block.BlockPhysicsEvent
 *  org.bukkit.material.Rails
 */
package com.bergerkiller.bukkit.tc.rails.type;

import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.rails.type.RailTypeRegular;
import com.bergerkiller.bukkit.tc.utils.PoweredTrackLogic;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.material.Rails;

public class RailTypeActivator
extends RailTypeRegular {
    private final boolean isPowered;

    protected RailTypeActivator(boolean isPowered) {
        this.isPowered = isPowered;
    }

    public boolean isPowered() {
        return this.isPowered;
    }

    @Override
    public void onBlockPlaced(Block railsBlock) {
        Rails rails;
        super.onBlockPlaced(railsBlock);
        BlockData blockData = WorldUtil.getBlockData((Block)railsBlock);
        if (blockData.getMaterialData() instanceof Rails && this.isUpsideDown(railsBlock, rails = (Rails)blockData.getMaterialData())) {
            TrainCarts.plugin.applyBlockPhysics(railsBlock.getRelative(rails.getDirection()), blockData);
            TrainCarts.plugin.applyBlockPhysics(railsBlock.getRelative(rails.getDirection().getOppositeFace()), blockData);
        }
    }

    @Override
    public void onBlockPhysics(BlockPhysicsEvent event) {
        super.onBlockPhysics(event);
        if (this.isUpsideDown(event.getBlock())) {
            PoweredTrackLogic logic = new PoweredTrackLogic(Material.ACTIVATOR_RAIL);
            logic.updateRedstone(event.getBlock());
        }
    }

    @Override
    public boolean isRail(BlockData blockData) {
        return blockData.isType(RailTypeRegular.RailMaterials.ACTIVATOR) && (blockData.getRawData() & 8) == 8 == this.isPowered;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(Powered=" + this.isPowered() + ")";
    }
}

