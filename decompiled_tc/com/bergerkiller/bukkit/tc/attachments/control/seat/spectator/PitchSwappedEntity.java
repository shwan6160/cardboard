/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.controller.VehicleMountController
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat.spectator;

import com.bergerkiller.bukkit.common.controller.VehicleMountController;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.VirtualEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonView;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.bukkit.util.Vector;

class PitchSwappedEntity<E extends VirtualEntity> {
    private static final float MIN_PITCH = EntityTrackerEntryStateHandle.getRotationFromProtocol((int)-128);
    private static final float MAX_PITCH = EntityTrackerEntryStateHandle.getRotationFromProtocol((int)127);
    private final AttachmentViewer viewer;
    private final VehicleMountController vmc;
    private Consumer<E> beforeSwap = e -> {};
    private Runnable afterSwap = () -> {};
    public E entity;
    public E entityAlt;
    public E entityAltFlip;
    private boolean spectating = false;

    private PitchSwappedEntity(AttachmentViewer viewer, E entity, E entityAlt, E entityAltFlip) {
        this.viewer = viewer;
        this.vmc = viewer.getVehicleMountController();
        this.entity = entity;
        this.entityAlt = entityAlt;
        this.entityAltFlip = entityAltFlip;
    }

    public int getEntityId() {
        return ((VirtualEntity)this.entity).getEntityId();
    }

    public void beforeSwap(Consumer<E> action) {
        this.beforeSwap = action;
    }

    public void afterSwap(Runnable action) {
        this.afterSwap = action;
    }

    public void spawn(Matrix4x4 eyeTransform, Vector motion) {
        FirstPersonView.HeadRotation headRot = FirstPersonView.HeadRotation.compute(eyeTransform);
        ((VirtualEntity)this.entity).updatePosition(eyeTransform, headRot.pyr);
        ((VirtualEntity)this.entity).syncPosition(true);
        ((VirtualEntity)this.entityAlt).updatePosition(eyeTransform, new Vector((double)PitchSwappedEntity.computeAltPitch(headRot.pitch, MAX_PITCH), (double)headRot.yaw, 0.0));
        ((VirtualEntity)this.entityAlt).syncPosition(true);
        ((VirtualEntity)this.entityAltFlip).updatePosition(eyeTransform, headRot.flipVertical().pyr);
        ((VirtualEntity)this.entityAltFlip).syncPosition(true);
        ((VirtualEntity)this.entity).spawn(this.viewer, motion);
        ((VirtualEntity)this.entity).forceSyncRotation();
        ((VirtualEntity)this.entityAlt).spawn(this.viewer, motion);
        ((VirtualEntity)this.entityAlt).forceSyncRotation();
        ((VirtualEntity)this.entityAltFlip).spawn(this.viewer, motion);
        ((VirtualEntity)this.entityAltFlip).forceSyncRotation();
    }

    public void destroy() {
        if (this.spectating) {
            this.spectating = false;
            this.vmc.stopSpectating(((VirtualEntity)this.entity).getEntityId());
        }
        ((VirtualEntity)this.entity).destroy(this.viewer);
        ((VirtualEntity)this.entityAlt).destroy(this.viewer);
        ((VirtualEntity)this.entityAltFlip).destroy(this.viewer);
    }

    public void spectate() {
        this.vmc.startSpectating(((VirtualEntity)this.entity).getEntityId());
        this.spectating = true;
    }

    public void spectateFrom(int previousEntityId) {
        this.vmc.swapSpectating(previousEntityId, ((VirtualEntity)this.entity).getEntityId());
        this.spectating = true;
    }

    public void swapVisibility(E swapped) {
        ((VirtualEntity)this.entity).getMetaData().setFlag(EntityHandle.DATA_FLAGS, 32, true);
        ((VirtualEntity)this.entity).syncMetadata();
        ((VirtualEntity)swapped).getMetaData().setFlag(EntityHandle.DATA_FLAGS, 32, false);
        ((VirtualEntity)swapped).syncMetadata();
    }

    public void updatePosition(Matrix4x4 eyeTransform) {
        E tmp;
        Vector position = eyeTransform.toVector();
        FirstPersonView.HeadRotation headRot = FirstPersonView.HeadRotation.compute(eyeTransform);
        FirstPersonView.HeadRotation headRotFlipped = headRot.flipVertical();
        if (Util.isProtocolRotationGlitched(((VirtualEntity)this.entity).getSyncPitch(), headRot.pitch)) {
            if (this.spectating) {
                this.vmc.swapSpectating(((VirtualEntity)this.entity).getEntityId(), ((VirtualEntity)this.entityAlt).getEntityId());
            }
            this.beforeSwap.accept(this.entityAlt);
            tmp = this.entity;
            this.entity = this.entityAlt;
            this.entityAlt = tmp;
            ((VirtualEntity)this.entity).updatePosition(position, headRot.pyr);
            ((VirtualEntity)this.entity).syncPosition(true);
            ((VirtualEntity)this.entity).syncMetadata();
            ((VirtualEntity)this.entityAlt).syncMetadata();
            this.afterSwap.run();
        } else if (this.isCameraFlipped(headRot, headRotFlipped)) {
            if (this.spectating) {
                this.vmc.swapSpectating(((VirtualEntity)this.entity).getEntityId(), ((VirtualEntity)this.entityAltFlip).getEntityId());
            }
            this.beforeSwap.accept(this.entityAltFlip);
            tmp = this.entity;
            this.entity = this.entityAltFlip;
            this.entityAltFlip = tmp;
            ((VirtualEntity)this.entity).updatePosition(position, headRot.pyr);
            ((VirtualEntity)this.entity).syncPosition(true);
            ((VirtualEntity)this.entity).syncMetadata();
            ((VirtualEntity)this.entityAltFlip).syncMetadata();
            this.afterSwap.run();
        } else {
            ((VirtualEntity)this.entity).updatePosition(position, headRot.pyr);
        }
        boolean requiresRespawning = Util.isProtocolRotationGlitched(headRotFlipped.pitch, ((VirtualEntity)this.entityAltFlip).getLivePitch());
        ((VirtualEntity)this.entityAltFlip).updatePosition(position, headRotFlipped.pyr);
        if (requiresRespawning) {
            ((VirtualEntity)this.entityAltFlip).respawnForAll(new Vector());
            ((VirtualEntity)this.entityAltFlip).forceSyncRotation();
        }
        float newAltPitch = PitchSwappedEntity.computeAltPitch(headRot.pitch, ((VirtualEntity)this.entityAlt).getLivePitch());
        boolean requiresRespawning2 = Util.isProtocolRotationGlitched(newAltPitch, ((VirtualEntity)this.entityAlt).getLivePitch());
        ((VirtualEntity)this.entityAlt).updatePosition(position, new Vector(newAltPitch, headRot.yaw, headRot.roll));
        if (requiresRespawning2) {
            ((VirtualEntity)this.entityAlt).respawnForAll(new Vector());
            ((VirtualEntity)this.entityAlt).forceSyncRotation();
        }
    }

    private boolean isCameraFlipped(FirstPersonView.HeadRotation newRot, FirstPersonView.HeadRotation newRotFlipped) {
        return MathUtil.getAngleDifference((float)((VirtualEntity)this.entity).getLiveYaw(), (float)newRot.yaw) > 90.0f && MathUtil.getAngleDifference((float)((VirtualEntity)this.entity).getLivePitch(), (float)newRotFlipped.pitch) < MathUtil.getAngleDifference((float)((VirtualEntity)this.entity).getLivePitch(), (float)((VirtualEntity)this.entity).getLiveYaw());
    }

    public void syncPosition(boolean absolute) {
        ((VirtualEntity)this.entity).syncPosition(absolute);
        ((VirtualEntity)this.entityAlt).syncPosition(absolute);
        ((VirtualEntity)this.entityAltFlip).syncPosition(absolute);
    }

    public void onBeforeSwap() {
    }

    public static <E extends VirtualEntity> PitchSwappedEntity<E> create(AttachmentViewer viewer, E entity, E entityAlt, E entityAltFlip) {
        return new PitchSwappedEntity<E>(viewer, entity, entityAlt, entityAltFlip);
    }

    public static <E extends VirtualEntity> PitchSwappedEntity<E> create(AttachmentViewer viewer, Supplier<E> entityFactory) {
        return new PitchSwappedEntity<VirtualEntity>(viewer, (VirtualEntity)entityFactory.get(), (VirtualEntity)entityFactory.get(), (VirtualEntity)entityFactory.get());
    }

    static float computeAltPitch(float currPitch, float currAltPitch) {
        int protRot = EntityTrackerEntryStateHandle.getProtocolRotation((float)currPitch);
        if (protRot == -128) {
            return MAX_PITCH;
        }
        if (protRot == 127) {
            return MIN_PITCH;
        }
        if ((double)(currPitch = MathUtil.wrapAngle((float)currPitch)) > 90.0) {
            return MIN_PITCH;
        }
        if ((double)currPitch < -90.0) {
            return MAX_PITCH;
        }
        return currAltPitch;
    }
}

