/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.controller.VehicleMountController
 *  com.bergerkiller.bukkit.common.events.PacketReceiveEvent
 *  com.bergerkiller.bukkit.common.events.PacketSendEvent
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.protocol.PacketListener
 *  com.bergerkiller.bukkit.common.protocol.PacketType
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.wrappers.RelativeFlags
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerPositionPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerRotationPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundMovePlayerPacketHandle
 *  com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat;

import com.bergerkiller.bukkit.common.controller.VehicleMountController;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.RelativeFlags;
import com.bergerkiller.bukkit.tc.attachments.VirtualEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonView;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewLockMode;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewMode;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SpectatorInput;
import com.bergerkiller.bukkit.tc.attachments.control.seat.spectator.FirstPersonSpectatedEntity;
import com.bergerkiller.bukkit.tc.controller.player.network.PlayerPacketListener;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerPositionPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerRotationPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundMovePlayerPacketHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle;
import java.util.Collections;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FirstPersonViewSpectator
extends FirstPersonView {
    private static final double GHOST_Y_OFFSET = 64.0;
    private static final float PITCH_ADJ_THRESHOLD = 15.0f;
    private int vehicleEntityId = -1;
    private FirstPersonSpectatedEntity _spectatedEntity = null;
    private VirtualEntity _playerMount = null;
    private final SpectatorInput _input = new SpectatorInput();
    private PlayerPacketListener<?> _spectatorPacketListener = null;

    public FirstPersonViewSpectator(CartAttachmentSeat seat, AttachmentViewer player) {
        super(seat, player);
    }

    public int prepareVehicleEntityId() {
        if (this.vehicleEntityId == -1) {
            this.vehicleEntityId = this.seat.seated.spawnVehicleMount(this.player);
        }
        return this.vehicleEntityId;
    }

    @Override
    public boolean doesViewModeChangeRequireReset(FirstPersonViewMode newViewMode) {
        return newViewMode == FirstPersonViewMode.THIRD_P || this.getLiveMode() == FirstPersonViewMode.THIRD_P;
    }

    @Override
    protected Matrix4x4 getEyeTransform() {
        Matrix4x4 base = super.getEyeTransform();
        this._input.applyTo(base);
        return base;
    }

    protected Quaternion getCurrentHeadRotation(Matrix4x4 transform) {
        transform = transform.clone();
        if (!this._eyePosition.isDefault()) {
            transform.multiply(this._eyePosition.transform);
        }
        this._input.applyTo(transform);
        return transform.getRotation();
    }

    @Override
    public void makeVisible(AttachmentViewer viewer, boolean isReload) {
        FirstPersonViewSpectator.setPlayerVisible(viewer, false);
        this.vehicleEntityId = -1;
        if (this.getLockMode() == FirstPersonViewLockMode.SPECTATOR_FREE) {
            this._input.start(viewer, this.seat.isRotationLocked() ? 70.0f : 360.0f);
        } else {
            this._input.startLocked();
        }
        Matrix4x4 eyeTransform = this.getEyeTransform();
        this._spectatedEntity = FirstPersonSpectatedEntity.create(this.seat, this, viewer);
        this._spectatedEntity.start(eyeTransform);
        if (this._spectatorPacketListener != null) {
            this._spectatorPacketListener.terminate();
            this._spectatorPacketListener = null;
        }
        this._spectatorPacketListener = viewer.supportRelativeRotationUpdate() ? viewer.createPacketListener(new ViewControlPacketListenerRelativePitch(), new PacketType[]{PacketType.IN_POSITION_LOOK, PacketType.IN_POSITION, PacketType.IN_LOOK}) : viewer.createPacketListener(new ViewControlPacketListenerAbsoluteRotation(), new PacketType[]{PacketType.IN_POSITION_LOOK, PacketType.IN_POSITION, PacketType.IN_LOOK});
        if (this._playerMount == null) {
            this._playerMount = new VirtualEntity(this.seat.getManager());
            this._playerMount.setEntityType(EntityType.ARMOR_STAND);
            this._playerMount.setSyncMode(VirtualEntity.SyncMode.SEAT);
            this._playerMount.setRelativeOffset(0.0, 64.0, 0.0);
            this._playerMount.updatePosition(eyeTransform);
            this._playerMount.syncPosition(true);
            this._playerMount.getMetaData().set(EntityHandle.DATA_FLAGS, (Object)32);
            this._playerMount.getMetaData().set(LivingEntityHandle.DATA_HEALTH, (Object)Float.valueOf(10.0f));
            this._playerMount.getMetaData().set(ArmorStandHandle.DATA_ARMORSTAND_FLAGS, (Object)25);
            this._playerMount.spawn(viewer, new Vector());
            Vector pos = this._playerMount.getSyncPos();
            viewer.getClientSynchronizer().synchronize(teleportId -> ClientboundPlayerPositionPacketHandle.createNew((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (float)this._playerMount.getSyncYaw(), (float)this._playerMount.getSyncPitch(), (double)0.0, (double)0.0, (double)0.0, (RelativeFlags)RelativeFlags.ABSOLUTE_POSITION, (int)teleportId), p -> this._spectatorPacketListener.enable());
            viewer.getVehicleMountController().mount(this._playerMount.getEntityId(), viewer.getEntityId());
        }
        if (this.getLiveMode() == FirstPersonViewMode.THIRD_P) {
            this.seat.seated.makeVisibleFirstPerson(viewer);
        }
    }

    @Override
    public void makeHidden(AttachmentViewer viewer, boolean isReload) {
        if (this.getLiveMode() == FirstPersonViewMode.THIRD_P) {
            this.seat.seated.makeHiddenFirstPerson(viewer);
        }
        if (this._spectatorPacketListener != null) {
            viewer.getClientSynchronizer().synchronize(this._spectatorPacketListener::terminate);
            this._spectatorPacketListener = null;
        }
        if (this._playerMount != null) {
            VehicleMountController vmc = viewer.getVehicleMountController();
            vmc.unmount(this._playerMount.getEntityId(), viewer.getEntityId());
            this._playerMount.destroy(viewer);
            this._playerMount = null;
            if (this._spectatedEntity != null) {
                VirtualEntity entity = this._spectatedEntity.getCurrentEntity();
                Vector pos = entity.getSyncPos();
                ServerPlayerHandle playerHandle = ServerPlayerHandle.fromBukkit((Player)viewer.getPlayer());
                playerHandle.setPositionRotation(pos.getX(), pos.getY(), pos.getZ(), entity.getSyncYaw(), entity.getSyncPitch());
                playerHandle.setFallDistance(0.0f);
                viewer.send((PacketHandle)ClientboundPlayerPositionPacketHandle.createAbsolute((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (float)entity.getSyncYaw(), (float)entity.getSyncPitch()));
            }
        }
        if (this._spectatedEntity != null) {
            this._spectatedEntity.stop();
            this._spectatedEntity = null;
        }
        if (this.vehicleEntityId != -1) {
            this.seat.seated.despawnVehicleMount(viewer);
            this.vehicleEntityId = -1;
        }
        if (this.getLiveMode() != FirstPersonViewMode.THIRD_P) {
            FirstPersonViewSpectator.setPlayerVisible(viewer, true);
        }
        this._input.stop(this.getEyeTransform());
    }

    @Override
    public void onTick() {
        if (this._spectatedEntity != null) {
            Matrix4x4 baseTransform = this.getEyeTransform();
            this._playerMount.updatePosition(baseTransform);
            this._spectatedEntity.updatePosition(baseTransform);
        }
        this._input.update();
    }

    @Override
    public void onMove(boolean absolute) {
        if (this._spectatedEntity != null) {
            this._playerMount.syncPosition(absolute);
            this._spectatedEntity.syncPosition(absolute);
        }
    }

    private class ViewControlPacketListenerRelativePitch
    extends ViewControlPacketListener {
        private ViewControlPacketListenerRelativePitch() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void ackPitchAdjustDone(float pitchChange) {
            Object object = this.stateLock;
            synchronized (object) {
                if (!this.isAdjustingPitch) {
                    return;
                }
                this.inFlightPitchCorrection -= pitchChange;
                if (this.lastYawPitch != null) {
                    this.lastYawPitch = new SpectatorInput.YawPitch(this.lastYawPitch.yaw, this.lastYawPitch.pitch + pitchChange);
                }
                SpectatorInput.YawPitch yawPitchDuringAdjustment = this.yawPitchDuringAdjustment;
                this.yawPitchDuringAdjustment = null;
                this.isAdjustingPitch = false;
                if (yawPitchDuringAdjustment != null) {
                    this.detectLookChanges(yawPitchDuringAdjustment);
                }
            }
        }

        @Override
        protected void makeAdjustment(SpectatorInput.YawPitch newYawPitch, float pitchAdjustment) {
            FirstPersonViewSpectator.this.player.getClientSynchronizer().synchronizeBundle(Collections.singletonList(ClientboundPlayerRotationPacketHandle.createRelative((float)0.0f, (float)pitchAdjustment)), this::ackPitchAdjustStart, () -> this.ackPitchAdjustDone(pitchAdjustment));
        }
    }

    private class ViewControlPacketListenerAbsoluteRotation
    extends ViewControlPacketListener {
        private ViewControlPacketListenerAbsoluteRotation() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void ackAbsoluteRotationAdjust(float absoluteYaw, float pitchChange) {
            Object object = this.stateLock;
            synchronized (object) {
                if (!this.isAdjustingPitch) {
                    return;
                }
                this.inFlightPitchCorrection -= pitchChange;
                if (this.lastYawPitch != null) {
                    this.lastYawPitch = new SpectatorInput.YawPitch(absoluteYaw, 0.0f);
                }
                SpectatorInput.YawPitch yawPitchDuringAdjustment = this.yawPitchDuringAdjustment;
                this.yawPitchDuringAdjustment = null;
                this.isAdjustingPitch = false;
                if (yawPitchDuringAdjustment != null) {
                    this.detectLookChanges(yawPitchDuringAdjustment);
                }
            }
        }

        @Override
        protected void makeAdjustment(SpectatorInput.YawPitch newYawPitch, float pitchAdjustment) {
            FirstPersonViewSpectator.this.player.getClientSynchronizer().synchronizeBundle(Collections.singletonList(ClientboundPlayerRotationPacketHandle.createAbsolute((float)newYawPitch.yaw, (float)0.0f)), this::ackPitchAdjustStart, () -> this.ackAbsoluteRotationAdjust(newYawPitch.yaw, pitchAdjustment));
        }
    }

    private abstract class ViewControlPacketListener
    implements PacketListener {
        protected final Object stateLock = new Object();
        protected SpectatorInput.YawPitch lastYawPitch = null;
        protected boolean isAdjustingPitch = false;
        protected SpectatorInput.YawPitch yawPitchDuringAdjustment = null;
        protected float inFlightPitchCorrection = 0.0f;
        protected int inFlightPitchCorrectionsCurrTick = -1;

        private ViewControlPacketListener() {
        }

        protected abstract void makeAdjustment(SpectatorInput.YawPitch var1, float var2);

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void detectLookChanges(SpectatorInput.YawPitch newYawPitch) {
            SpectatorInput.YawPitch lookChange = null;
            Float pitchAdjustment = null;
            Object object = this.stateLock;
            synchronized (object) {
                int currTick;
                if (this.isAdjustingPitch) {
                    this.yawPitchDuringAdjustment = newYawPitch;
                    return;
                }
                if (this.lastYawPitch != null) {
                    lookChange = SpectatorInput.YawPitch.subtract(newYawPitch, this.lastYawPitch);
                }
                this.lastYawPitch = newYawPitch;
                float pitchErrorFromZero = MathUtil.wrapAngle((float)(-newYawPitch.pitch - this.inFlightPitchCorrection));
                if (Math.abs(pitchErrorFromZero) > 15.0f && (currTick = CommonUtil.getServerTicks()) != this.inFlightPitchCorrectionsCurrTick) {
                    this.inFlightPitchCorrectionsCurrTick = currTick;
                    this.inFlightPitchCorrection += pitchErrorFromZero;
                    pitchAdjustment = Float.valueOf(pitchErrorFromZero);
                }
            }
            if (lookChange != null) {
                FirstPersonViewSpectator.this._input.addInputRotation(lookChange);
            }
            if (pitchAdjustment != null) {
                this.makeAdjustment(newYawPitch, pitchAdjustment.floatValue());
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void ackPitchAdjustStart() {
            Object object = this.stateLock;
            synchronized (object) {
                this.isAdjustingPitch = true;
                this.yawPitchDuringAdjustment = null;
            }
        }

        public void onPacketReceive(PacketReceiveEvent event) {
            ServerboundMovePlayerPacketHandle p = ServerboundMovePlayerPacketHandle.createHandle((Object)event.getPacket().getHandle());
            if (event.getType() != PacketType.IN_LOOK) {
                p.setY(p.getY() - 64.0);
            }
            if (event.getType() != PacketType.IN_POSITION) {
                this.detectLookChanges(new SpectatorInput.YawPitch(p.getYaw(), p.getPitch()));
            }
        }

        public void onPacketSend(PacketSendEvent event) {
        }
    }
}

