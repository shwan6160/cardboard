/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.block.SignChangeTracker
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.global;

import com.bergerkiller.bukkit.common.block.SignChangeTracker;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.VirtualDisplayBlockEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

final class SignDebugHighlight
implements Runnable {
    private final AttachmentViewer viewer;
    private VirtualDisplayBlockEntity entity = null;

    SignDebugHighlight(AttachmentViewer viewer) {
        this.viewer = viewer;
    }

    public void spawn(SignChangeTracker sign, RailLookup.TrackedSign.DebugDisplayOptions options) {
        Block block = sign.getBlock();
        AABBHandle bbox = BlockUtil.getInteractableBox((Block)block);
        if (bbox == null) {
            this.viewer.getTrainCarts().getLogger().warning("Could not display bbox of sign because its interactable box is null!");
            return;
        }
        Matrix4x4 m = new Matrix4x4();
        m.translate((double)block.getX(), (double)block.getY(), (double)block.getZ());
        m.translate(bbox.getMinX() + 0.5, bbox.getMinY(), bbox.getMinZ() + 0.5);
        this.entity = new VirtualDisplayBlockEntity(null);
        this.entity.updatePosition(m);
        this.entity.setBlockData(BlockData.fromMaterial((Material)Material.BLACK_STAINED_GLASS));
        this.entity.getMetadata().set(DisplayHandle.DATA_GLOW_COLOR_OVERRIDE, (Object)Util.toColor(options.getTeamColor()).asRGB());
        this.entity.getMetadata().setFlag(EntityHandle.DATA_FLAGS, 64, true);
        this.entity.setScale(new Vector(bbox.getMaxX() - bbox.getMinX(), bbox.getMaxY() - bbox.getMinY(), bbox.getMaxZ() - bbox.getMinZ()));
        this.entity.spawn(this.viewer, new Vector());
    }

    @Override
    public void run() {
        this.entity.destroy(this.viewer);
    }
}

