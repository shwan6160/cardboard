/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.tc.attachments.VirtualArmorStandItemEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.config.transform.ArmorStandItemTransformType;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class FirstPersonEyePositionArrow {
    private final CartAttachmentSeat seat;
    private VirtualArmorStandItemEntity arrow;
    private int timeout;

    public FirstPersonEyePositionArrow(CartAttachmentSeat seat) {
        this.seat = seat;
        this.arrow = null;
        this.timeout = 0;
    }

    public void start(AttachmentViewer viewer, int tickDuration) {
        if (this.arrow == null) {
            this.arrow = new VirtualArmorStandItemEntity(this.seat.getManager());
            this.arrow.setItem(ArmorStandItemTransformType.HEAD, new ItemStack(MaterialUtil.getFirst((String[])new String[]{"ARROW", "LEGACY_ARROW"})));
            this.arrow.getMetaData().setFlag(EntityHandle.DATA_FLAGS, 1, true);
            this.arrow.getMetaData().setFlag(ArmorStandHandle.DATA_ARMORSTAND_FLAGS, 16, true);
            this.arrow.updatePosition(FirstPersonEyePositionArrow.adjust(this.seat.firstPerson.getEyeTransform()));
            this.arrow.syncPosition(true);
        }
        if (!this.arrow.isViewer(viewer)) {
            this.arrow.spawn(viewer, new Vector());
        }
        this.timeout = Math.max(this.timeout, tickDuration);
    }

    public void stop(AttachmentViewer viewer) {
        if (this.arrow != null && this.arrow.isViewer(viewer)) {
            this.arrow.destroy(viewer);
            if (!this.arrow.hasViewers()) {
                this.arrow = null;
            }
        }
    }

    public void stop() {
        if (this.arrow != null) {
            this.arrow.destroyForAll();
            this.arrow = null;
        }
        this.timeout = 0;
    }

    public void updatePosition() {
        if (this.timeout > 0) {
            if (this.timeout == 1) {
                this.stop();
            } else {
                --this.timeout;
            }
        }
        if (this.arrow != null) {
            this.arrow.updatePosition(FirstPersonEyePositionArrow.adjust(this.seat.firstPerson.getEyeTransform()));
        }
    }

    public void syncPosition(boolean absolute) {
        if (this.arrow != null) {
            this.arrow.syncPosition(true);
        }
    }

    private static Matrix4x4 adjust(Matrix4x4 eyeTransform) {
        Vector pos = eyeTransform.toVector();
        Quaternion rot = eyeTransform.getRotation();
        Vector v = new Vector(-0.27, -0.5, -0.2);
        rot.transformPoint(v);
        pos.add(v);
        rot.rotateY(-90.0);
        rot.rotateZ(-45.0);
        Matrix4x4 result = new Matrix4x4();
        result.translate(pos);
        result.rotate(rot);
        return result;
    }
}

