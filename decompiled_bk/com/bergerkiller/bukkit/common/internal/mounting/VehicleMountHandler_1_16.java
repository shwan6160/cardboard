/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.internal.mounting;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.mounting.VehicleMountHandler_1_9_to_1_15_2;
import com.bergerkiller.bukkit.common.internal.mounting.VehicleMountHandler_BaseImpl;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundMovePlayerPacketHandle;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class VehicleMountHandler_1_16
extends VehicleMountHandler_1_9_to_1_15_2 {
    public static final PacketType[] LISTENED_PACKETS = new PacketType[]{PacketType.IN_PLAYER_COMMAND, PacketType.IN_STEER_VEHICLE, PacketType.IN_POSITION, PacketType.IN_POSITION_LOOK, PacketType.OUT_MOUNT, PacketType.OUT_CAMERA, PacketType.OUT_ENTITY_TELEPORT, PacketType.OUT_ENTITY_MOVE, PacketType.OUT_ENTITY_MOVE_LOOK, PacketType.OUT_POSITION};
    private boolean _is_sneaking = false;
    private Vector in_pos = null;
    private Vector last_pos = null;
    private final Vector curr_pos = new Vector();
    private int last_sync = -1;
    private int remount_sync = -1;

    public VehicleMountHandler_1_16(CommonPlugin plugin, Player player) {
        super(plugin, player);
    }

    @Override
    protected boolean isPositionTracked() {
        return this._plugin.teleportPlayersToSeat();
    }

    @Override
    protected void onPacketSend(CommonPacket packet) {
        super.onPacketSend(packet);
        if (packet.getType() == PacketType.OUT_POSITION && this._playerSpawnedEntity.vehicleMount != null) {
            this.onMountReady(this._playerSpawnedEntity.vehicleMount);
        }
    }

    @Override
    protected void onPacketReceive(CommonPacket packet) {
        super.onPacketReceive(packet);
        if (packet.getType() == PacketType.IN_STEER_VEHICLE) {
            if (packet.read(PacketType.IN_STEER_VEHICLE.unmount).booleanValue()) {
                this.remount_sync = this._currentTick + 4;
            }
        } else if (packet.getType() == PacketType.IN_PLAYER_COMMAND) {
            String actionId = ((Enum)packet.read(PacketType.IN_PLAYER_COMMAND.action)).name();
            if (actionId.equals("PRESS_SHIFT_KEY")) {
                if (!this._is_sneaking) {
                    this.remount_sync = this._currentTick + 2;
                }
                this._is_sneaking = true;
            } else if (actionId.equals("RELEASE_SHIFT_KEY") && this._is_sneaking) {
                this._is_sneaking = false;
                this.last_pos = null;
                this.remount_sync = -1;
                this.remount();
            }
        } else if (packet.getType() == PacketType.IN_POSITION || packet.getType() == PacketType.IN_POSITION_LOOK) {
            if (this.isTeleporting()) {
                ServerboundMovePlayerPacketHandle handle = ServerboundMovePlayerPacketHandle.createHandle(packet.getHandle());
                Vector v = new Vector(handle.getX(), handle.getY(), handle.getZ());
                if (this.in_pos == null || v.distanceSquared(this.in_pos) > 1.0) {
                    this.in_pos = v;
                } else {
                    this.in_pos.setX(this.in_pos.getX() + 0.05 * (v.getX() - this.in_pos.getX()));
                    this.in_pos.setY(this.in_pos.getY() + 0.05 * (v.getY() - this.in_pos.getY()));
                    this.in_pos.setZ(this.in_pos.getZ() + 0.05 * (v.getZ() - this.in_pos.getZ()));
                }
            } else {
                this.in_pos = null;
            }
        }
    }

    @Override
    public void update() {
        this.synchronizeAndQueuePackets(() -> {
            if (this.remount_sync == -1 && this.isTeleporting()) {
                this.teleport();
            } else {
                this.last_pos = null;
                this.in_pos = null;
            }
            if (this.remount_sync == this._currentTick) {
                this.remount_sync = -1;
                this.remount();
            }
        });
        super.update();
    }

    private boolean isTeleporting() {
        return this._plugin.teleportPlayersToSeat() && this._playerSpawnedEntity.vehicleMount != null && this._is_sneaking;
    }

    private void teleport() {
        Vector new_position = this._playerSpawnedEntity.position;
        if (new_position == null) {
            return;
        }
        if (this._playerSpawnedEntity.position_sync != this.last_sync) {
            this.last_sync = this._playerSpawnedEntity.position_sync;
            if (this.last_pos != null) {
                MathUtil.setVector(this.last_pos, this.curr_pos);
            }
        }
        if (this.last_pos == null) {
            this.last_pos = new_position.clone();
            MathUtil.setVector(this.curr_pos, this.last_pos);
        } else {
            VehicleMountHandler_BaseImpl.SpawnedEntity e = this._playerSpawnedEntity;
            while (e.vehicleMount != null) {
                e = e.vehicleMount.vehicle;
            }
            int elapsed = this._currentTick - this._playerSpawnedEntity.position_sync;
            int sync_time = e.type.moveTicks - 1;
            if (elapsed >= sync_time) {
                MathUtil.setVector(this.curr_pos, new_position);
            } else {
                double t = (double)(1 + elapsed) / (double)sync_time;
                this.curr_pos.setX(MathUtil.lerp(this.last_pos.getX(), new_position.getX(), t));
                this.curr_pos.setY(MathUtil.lerp(this.last_pos.getY(), new_position.getY(), t));
                this.curr_pos.setZ(MathUtil.lerp(this.last_pos.getZ(), new_position.getZ(), t));
            }
        }
        if (this.in_pos != null) {
            Vector diff = this.curr_pos.clone().subtract(this.in_pos);
            diff.setY(diff.getY() + 0.01);
            MathUtil.setVector(this.in_pos, this.curr_pos);
            if (diff.lengthSquared() > 4.0) {
                Location eye = this.getPlayer().getEyeLocation();
                this.queuePacket(ClientboundEntityPositionSyncPacketHandle.createNew(this.getPlayer().getEntityId(), this.curr_pos.getX(), this.curr_pos.getY(), this.curr_pos.getZ(), eye.getYaw(), eye.getPitch(), false));
            } else {
                this.queuePacket(ClientboundSetEntityMotionPacketHandle.createNew(this.getPlayer().getEntityId(), diff.getX(), diff.getY(), diff.getZ()));
            }
        }
    }

    private void remount() {
        if (!this._is_sneaking && this._playerSpawnedEntity.vehicleMount != null) {
            this.onMountReady(this._playerSpawnedEntity.vehicleMount);
        }
    }
}

