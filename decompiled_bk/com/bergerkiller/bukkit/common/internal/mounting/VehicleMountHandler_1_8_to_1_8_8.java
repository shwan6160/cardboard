/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.internal.mounting;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.mounting.VehicleMountHandler_BaseImpl;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacketHandle;
import java.util.List;
import org.bukkit.entity.Player;

public class VehicleMountHandler_1_8_to_1_8_8
extends VehicleMountHandler_BaseImpl {
    public static final PacketType[] LISTENED_PACKETS = new PacketType[]{PacketType.OUT_ENTITY_ATTACH, PacketType.OUT_CAMERA};

    public VehicleMountHandler_1_8_to_1_8_8(CommonPlugin plugin, Player player) {
        super(plugin, player);
    }

    private void processAttachEntityPacket(ClientboundSetEntityLinkPacketHandle packet) {
        VehicleMountHandler_BaseImpl.SpawnedEntity passenger = this.getSpawnedEntity(packet.getPassengerId(), false);
        VehicleMountHandler_BaseImpl.SpawnedEntity vehicle = this.getSpawnedEntity(packet.getVehicleId(), false);
        if (passenger != null && passenger.vehicleMount != null) {
            if (passenger.vehicleMount.vehicle != vehicle) {
                packet.setVehicleId(passenger.vehicleMount.vehicle.id);
            }
        } else if (vehicle != null && !vehicle.passengerMounts.isEmpty()) {
            packet.setPassengerId(vehicle.passengerMounts.get((int)0).passenger.id);
        }
    }

    @Override
    protected void onMountReady(VehicleMountHandler_BaseImpl.Mount mount) {
        this.sendAttach(mount.vehicle, mount.passenger);
    }

    @Override
    protected void onUnmountVehicle(VehicleMountHandler_BaseImpl.SpawnedEntity vehicle, List<VehicleMountHandler_BaseImpl.Mount> passengerMounts) {
        for (VehicleMountHandler_BaseImpl.Mount m : passengerMounts) {
            this.sendAttach(null, m.passenger);
        }
    }

    @Override
    protected void onSpawned(VehicleMountHandler_BaseImpl.SpawnedEntity entity) {
        if (entity.vehicleMount != null && entity.vehicleMount.vehicle.state.isSpawned()) {
            entity.vehicleMount.sent = true;
            this.sendAttach(entity.vehicleMount.vehicle, entity);
        }
        if (!entity.passengerMounts.isEmpty()) {
            VehicleMountHandler_BaseImpl.Mount m = entity.passengerMounts.get(0);
            if (m.passenger.state.isSpawned()) {
                m.sent = true;
                this.sendAttach(entity, m.passenger);
            }
        }
    }

    @Override
    protected void onPacketReceive(CommonPacket packet) {
    }

    @Override
    protected void onPacketSend(CommonPacket packet) {
        ClientboundSetEntityLinkPacketHandle packet_ae;
        if (packet.getType() == PacketType.OUT_ENTITY_ATTACH && !(packet_ae = ClientboundSetEntityLinkPacketHandle.createHandle(packet.getHandle())).isLeash()) {
            this.processAttachEntityPacket(packet_ae);
        }
    }

    private final void sendAttach(VehicleMountHandler_BaseImpl.SpawnedEntity vehicle, VehicleMountHandler_BaseImpl.SpawnedEntity passenger) {
        this.queuePacket(ClientboundSetEntityLinkPacketHandle.createNewMount(passenger.id, vehicle == null ? -1 : vehicle.id));
    }
}

