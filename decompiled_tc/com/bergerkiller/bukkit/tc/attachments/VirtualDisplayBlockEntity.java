/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher$Prototype
 *  com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle$BlockDisplayHandle
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments;

import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.VirtualDisplayEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle;
import org.bukkit.util.Vector;

public class VirtualDisplayBlockEntity
extends VirtualDisplayEntity {
    private BlockData blockData = null;
    public static final DataWatcher.Prototype BLOCK_DISPLAY_METADATA = BASE_DISPLAY_METADATA.modify().set(DisplayHandle.DATA_WIDTH, (Object)Float.valueOf(1.4142135f)).set(DisplayHandle.DATA_HEIGHT, (Object)Float.valueOf(1.4142135f)).create();

    public VirtualDisplayBlockEntity(AttachmentManager manager) {
        super(manager, BLOCK_DISPLAY_ENTITY_TYPE, BLOCK_DISPLAY_METADATA.create());
    }

    @Override
    protected void onScaleUpdated() {
        super.onScaleUpdated();
        float bb = (float)(1.41421356274619 * Util.absMaxAxis(this.scale));
        this.metadata.set(DisplayHandle.DATA_WIDTH, (Object)Float.valueOf(bb));
        this.metadata.set(DisplayHandle.DATA_HEIGHT, (Object)Float.valueOf(bb));
    }

    @Override
    protected Vector computeTranslation(Quaternion rotation) {
        Vector s = this.getScale();
        Vector v = new Vector(-0.5, 0.0, -0.5);
        v.setX(v.getX() * s.getX());
        v.setY(v.getY() * s.getY());
        v.setZ(v.getZ() * s.getZ());
        rotation.transformPoint(v);
        return v;
    }

    public BlockData getBlockData() {
        return this.blockData;
    }

    public void setBlockData(BlockData blockData) {
        if (this.blockData != blockData) {
            this.blockData = blockData;
            this.metadata.set(DisplayHandle.BlockDisplayHandle.DATA_BLOCK_STATE, (Object)blockData);
            this.syncMeta();
        }
    }
}

