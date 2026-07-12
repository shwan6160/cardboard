/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.monster.ShulkerHandle
 *  org.bukkit.entity.EntityType
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.tc.attachments.VirtualEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentPlatform;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.monster.ShulkerHandle;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class CartAttachmentPlatformShulker
extends CartAttachmentPlatform {
    private VirtualEntity actual;
    private VirtualEntity entity;

    @Override
    public void onDetached() {
        super.onDetached();
        this.entity = null;
        this.actual = null;
    }

    @Override
    public void onAttached() {
        super.onAttached();
        this.actual = new VirtualEntity(this.getManager());
        this.actual.setEntityType(EntityType.SHULKER);
        this.actual.getMetaData().set(EntityHandle.DATA_FLAGS, (Object)32);
        this.actual.getMetaData().setClientByteDefault(ShulkerHandle.DATA_COLOR, CartAttachmentPlatform.Color.DEFAULT.ordinal());
        this.entity = new VirtualEntity(this.getManager());
        this.entity.setEntityType(EntityType.CHICKEN);
        this.entity.getMetaData().set(EntityHandle.DATA_FLAGS, (Object)32);
        this.entity.getMetaData().set(EntityHandle.DATA_NO_GRAVITY, (Object)true);
        this.entity.setRelativeOffset(0.0, -0.32, 0.0);
    }

    @Override
    public void onLoad(ConfigurationNode config) {
        CartAttachmentPlatform.Color color = (CartAttachmentPlatform.Color)((Object)config.getOrDefault("shulkerColor", (Object)CartAttachmentPlatform.Color.DEFAULT));
        this.actual.getMetaData().set(ShulkerHandle.DATA_COLOR, (Object)((byte)color.ordinal()));
    }

    @Override
    public boolean checkCanReload(ConfigurationNode config) {
        if (!super.checkCanReload(config)) {
            return false;
        }
        return CartAttachmentPlatformShulker.readPlatformMode(config) == CartAttachmentPlatform.PlatformMode.SHULKER;
    }

    @Override
    public boolean containsEntityId(int entityId) {
        return this.entity.getEntityId() == entityId || this.actual.getEntityId() == entityId;
    }

    @Override
    public int getMountEntityId() {
        return this.actual.getEntityId();
    }

    @Override
    public void applyPassengerSeatTransform(Matrix4x4 transform) {
        Matrix4x4 relativeMatrix = new Matrix4x4();
        relativeMatrix.translate(0.0, 1.0, 0.0);
        Matrix4x4.multiply((Matrix4x4)relativeMatrix, (Matrix4x4)transform, (Matrix4x4)transform);
    }

    @Override
    public void makeVisible(AttachmentViewer viewer) {
        this.actual.spawn(viewer, new Vector());
        this.entity.spawn(viewer, new Vector());
        viewer.getVehicleMountController().mount(this.entity.getEntityId(), this.actual.getEntityId());
    }

    @Override
    public void makeHidden(AttachmentViewer viewer) {
        this.actual.destroy(viewer);
        this.entity.destroy(viewer);
    }

    @Override
    public void onTransformChanged(Matrix4x4 transform) {
        this.entity.updatePosition(transform);
        this.actual.updatePosition(transform);
        this.actual.syncMetadata();
    }

    @Override
    public void onMove(boolean absolute) {
        this.entity.syncPosition(absolute);
        this.actual.syncPositionSilent();
    }
}

