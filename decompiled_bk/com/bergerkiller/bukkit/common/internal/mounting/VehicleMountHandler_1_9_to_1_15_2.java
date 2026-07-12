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
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetPassengersPacketHandle;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;

public class VehicleMountHandler_1_9_to_1_15_2
extends VehicleMountHandler_BaseImpl {
    public static final PacketType[] LISTENED_PACKETS = new PacketType[]{PacketType.OUT_MOUNT, PacketType.OUT_CAMERA};

    public VehicleMountHandler_1_9_to_1_15_2(CommonPlugin plugin, Player player) {
        super(plugin, player);
    }

    private void processMountPacket(ClientboundSetPassengersPacketHandle packet) {
        VehicleMountHandler_BaseImpl.SpawnedEntity vehicle = this.getSpawnedEntity(packet.getEntityId(), false);
        boolean passsengerIdsChanged = false;
        int[] passengerIds = packet.getMountedEntityIds();
        int passengerIdsLength = passengerIds.length;
        int i = 0;
        while (i < passengerIdsLength) {
            VehicleMountHandler_BaseImpl.SpawnedEntity passenger = this.getSpawnedEntity(passengerIds[i], false);
            if (passenger != null && passenger.vehicleMount != null && passenger.vehicleMount.vehicle != vehicle) {
                passsengerIdsChanged = true;
                --passengerIdsLength;
                for (int j = i; j < passengerIdsLength; ++j) {
                    passengerIds[j] = passengerIds[j + 1];
                }
                continue;
            }
            ++i;
        }
        if (vehicle != null) {
            block2: for (VehicleMountHandler_BaseImpl.Mount mount : vehicle.passengerMounts) {
                if (!mount.sent) continue;
                int passengerId = mount.passenger.id;
                for (int j = 0; j < passengerIdsLength; ++j) {
                    if (passengerIds[j] == passengerId) continue block2;
                }
                if (passengerIdsLength == passengerIds.length) {
                    passengerIds = Arrays.copyOf(passengerIds, passengerIdsLength + 1);
                }
                passengerIds[passengerIdsLength++] = passengerId;
                passsengerIdsChanged = true;
            }
        }
        if (passsengerIdsChanged) {
            if (passengerIds.length == passengerIdsLength) {
                packet.setMountedEntityIds(passengerIds);
            } else {
                packet.setMountedEntityIds(Arrays.copyOf(passengerIds, passengerIdsLength));
            }
        }
    }

    @Override
    protected void onMountReady(VehicleMountHandler_BaseImpl.Mount mount) {
        this.sendVehicleMounts(mount.vehicle, true);
    }

    @Override
    protected void onUnmountVehicle(VehicleMountHandler_BaseImpl.SpawnedEntity vehicle, List<VehicleMountHandler_BaseImpl.Mount> passengerMounts) {
        this.sendVehicleMounts(vehicle, true);
    }

    @Override
    protected void onSpawned(VehicleMountHandler_BaseImpl.SpawnedEntity entity) {
        if (entity.vehicleMount != null && entity.vehicleMount.vehicle.state.isSpawned()) {
            entity.vehicleMount.sent = true;
            this.sendVehicleMounts(entity.vehicleMount.vehicle, true);
        }
        for (VehicleMountHandler_BaseImpl.Mount m : entity.passengerMounts) {
            if (!m.passenger.state.isSpawned()) continue;
            m.sent = true;
        }
        this.sendVehicleMounts(entity, false);
    }

    @Override
    protected void onPacketReceive(CommonPacket packet) {
    }

    @Override
    protected void onPacketSend(CommonPacket packet) {
        if (packet.getType() == PacketType.OUT_MOUNT) {
            this.processMountPacket(ClientboundSetPassengersPacketHandle.createHandle(packet.getHandle()));
        }
    }

    private final void sendVehicleMounts(VehicleMountHandler_BaseImpl.SpawnedEntity vehicle, boolean sendEmptyList) {
        int[] passengerIds = vehicle.collectSentPassengerIds();
        if (sendEmptyList || passengerIds.length > 0) {
            this.queuePacket(ClientboundSetPassengersPacketHandle.createNew(vehicle.id, vehicle.collectSentPassengerIds()));
        }
    }
}

