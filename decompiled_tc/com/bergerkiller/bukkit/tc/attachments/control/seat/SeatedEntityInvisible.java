/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  org.bukkit.entity.Entity
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntity;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class SeatedEntityInvisible
extends SeatedEntity {
    public SeatedEntityInvisible(CartAttachmentSeat seat) {
        super(seat);
    }

    @Override
    public Vector getThirdPersonCameraOffset() {
        return new Vector(0.0, 0.0, 0.0);
    }

    @Override
    public Vector getFirstPersonCameraOffset() {
        return new Vector(0.0, 0.0, 0.0);
    }

    @Override
    public void makeVisible(AttachmentViewer viewer) {
        if (this.isPlayer() || this.isDummyPlayerDisplayed()) {
            if (this.entity != viewer.getPlayer() && !this.isDummyPlayerDisplayed()) {
                this.hideRealPlayer(viewer);
            }
        } else if (!this.isEmpty()) {
            DataWatcher metaTmp = new DataWatcher();
            metaTmp.set(EntityHandle.DATA_FLAGS, (Object)32);
            viewer.send(ClientboundSetEntityDataPacketHandle.createNew((int)this.entity.getEntityId(), (DataWatcher)metaTmp, (boolean)true).toCommonPacket());
            viewer.getVehicleMountController().mount(this.spawnVehicleMount(viewer), this.entity.getEntityId());
        }
    }

    @Override
    public void makeHidden(AttachmentViewer viewer) {
        if (this.isPlayer() || this.isDummyPlayerDisplayed()) {
            if (this.entity != viewer.getPlayer() && !this.isDummyPlayerDisplayed()) {
                this.showRealPlayer(viewer);
            }
        } else if (!this.isEmpty()) {
            viewer.getVehicleMountController().unmount(this.parentMountId, this.entity.getEntityId());
            this.despawnVehicleMount(viewer);
            DataWatcher metaTmp = EntityHandle.fromBukkit((Entity)this.entity).getDataWatcher();
            viewer.send(ClientboundSetEntityDataPacketHandle.createNew((int)this.entity.getEntityId(), (DataWatcher)metaTmp, (boolean)true).toCommonPacket());
        }
    }

    @Override
    public void updatePosition(Matrix4x4 transform) {
        this.updateVehicleMountPosition(transform);
    }

    @Override
    public void syncPosition(boolean absolute) {
        this.syncVehicleMountPosition(absolute);
    }

    @Override
    public void updateFocus(boolean focused) {
    }

    @Override
    public boolean containsEntityId(int entityId) {
        return false;
    }
}

