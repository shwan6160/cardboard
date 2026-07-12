/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.controller.VehicleMountController
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetPassengersPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacketHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat.spectator;

import com.bergerkiller.bukkit.common.controller.VehicleMountController;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.FakePlayerSpawner;
import com.bergerkiller.bukkit.tc.attachments.VirtualEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewMode;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewSpectator;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntity;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntityHead;
import com.bergerkiller.bukkit.tc.attachments.control.seat.spectator.FirstPersonSpectatedEntity;
import com.bergerkiller.bukkit.tc.attachments.control.seat.spectator.PitchSwappedEntity;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetPassengersPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

class FirstPersonSpectatedEntityPlayerSitting
extends FirstPersonSpectatedEntity {
    private static final VirtualEntity[] NO_FAKE_MOUNTS = new VirtualEntity[0];
    private VirtualEntity[] fakeMounts = NO_FAKE_MOUNTS;
    private PitchSwappedEntity<FakeVirtualPlayer> fakePlayer;
    private BlindRespawn blindRespawn = null;
    private final ItemStack skullItem;

    public FirstPersonSpectatedEntityPlayerSitting(CartAttachmentSeat seat, FirstPersonViewSpectator view, AttachmentViewer player) {
        super(seat, view, player);
        this.skullItem = view.getLiveMode() == FirstPersonViewMode.HEAD ? SeatedEntityHead.createSkullItem((Entity)player.getPlayer()) : null;
    }

    @Override
    public void start(Matrix4x4 eyeTransform) {
        this.fakePlayer = PitchSwappedEntity.create(this.player, new FakeVirtualPlayer(this.seat.getManager(), FakePlayerSpawner.NO_NAMETAG), new FakeVirtualPlayer(this.seat.getManager(), FakePlayerSpawner.NO_NAMETAG_SECONDARY), new FakeVirtualPlayer(this.seat.getManager(), FakePlayerSpawner.NO_NAMETAG_TERTIARY));
        this.fakePlayer.beforeSwap(swapped -> {
            if (this.blindRespawn == null) {
                if (this.view.getLiveMode() == FirstPersonViewMode.HEAD) {
                    this.player.sendSilent((PacketHandle)Util.createPlayerEquipmentPacket(((FakeVirtualPlayer)this.fakePlayer.entity).getEntityId(), EquipmentSlot.HEAD, null));
                    this.player.sendSilent((PacketHandle)Util.createPlayerEquipmentPacket(swapped.getEntityId(), EquipmentSlot.HEAD, this.skullItem));
                } else {
                    this.fakePlayer.swapVisibility((FakeVirtualPlayer)swapped);
                }
            }
        });
        this.fakePlayer.spawn(eyeTransform, this.seat.calcMotion());
        if (!this.seat.firstPerson.getEyePosition().isDefault() || this.seat.seated.getDisplayMode() == SeatedEntity.DisplayMode.HEAD || this.seat.seated.getDisplayMode() == SeatedEntity.DisplayMode.INVISIBLE || !ClientboundSetPassengersPacketHandle.T.isAvailable()) {
            this.prepareFakeMounts(eyeTransform);
        } else {
            this.mountInVehicle();
        }
        this.blindRespawn = new BlindRespawn();
        this.blindRespawn.spawn(eyeTransform);
    }

    private void mountInVehicle() {
        VehicleMountController vmc = this.player.getVehicleMountController();
        int vehicleId = this.view.prepareVehicleEntityId();
        ((FakeVirtualPlayer)this.fakePlayer.entity).mount(vmc, vehicleId);
        ((FakeVirtualPlayer)this.fakePlayer.entityAlt).mount(vmc, vehicleId);
        ((FakeVirtualPlayer)this.fakePlayer.entityAltFlip).mount(vmc, vehicleId);
    }

    private void prepareFakeMounts(Matrix4x4 baseTransform) {
        VehicleMountController vmc = this.player.getVehicleMountController();
        if (ClientboundSetPassengersPacketHandle.T.isAvailable()) {
            VirtualEntity fakeMount = this.createFakeMount(baseTransform);
            ((FakeVirtualPlayer)this.fakePlayer.entity).mount(vmc, fakeMount.getEntityId());
            ((FakeVirtualPlayer)this.fakePlayer.entityAlt).mount(vmc, fakeMount.getEntityId());
            ((FakeVirtualPlayer)this.fakePlayer.entityAltFlip).mount(vmc, fakeMount.getEntityId());
            this.fakeMounts = new VirtualEntity[]{fakeMount};
        } else {
            VirtualEntity[] fakeMounts = new VirtualEntity[]{this.createFakeMount(baseTransform), this.createFakeMount(baseTransform), this.createFakeMount(baseTransform)};
            ((FakeVirtualPlayer)this.fakePlayer.entity).mount(vmc, fakeMounts[0].getEntityId());
            ((FakeVirtualPlayer)this.fakePlayer.entityAlt).mount(vmc, fakeMounts[1].getEntityId());
            ((FakeVirtualPlayer)this.fakePlayer.entityAltFlip).mount(vmc, fakeMounts[2].getEntityId());
            this.fakeMounts = fakeMounts;
        }
    }

    private VirtualEntity createFakeMount(Matrix4x4 baseTransform) {
        VirtualEntity fakeMount = new VirtualEntity(this.seat.getManager());
        fakeMount.setEntityType(EntityType.ARMOR_STAND);
        fakeMount.setSyncMode(VirtualEntity.SyncMode.SEAT);
        fakeMount.setUseMinecartInterpolation(this.seat.isMinecartInterpolation());
        fakeMount.setByViewerPositionAdjustment((viewer, pos) -> pos.setY(pos.getY() - viewer.getArmorStandButtOffset() - 1.0));
        fakeMount.updatePosition(baseTransform);
        fakeMount.getMetaData().set(EntityHandle.DATA_FLAGS, (Object)32);
        fakeMount.getMetaData().set(EntityHandle.DATA_NO_GRAVITY, (Object)true);
        fakeMount.getMetaData().set(LivingEntityHandle.DATA_HEALTH, (Object)Float.valueOf(10.0f));
        fakeMount.getMetaData().set(ArmorStandHandle.DATA_ARMORSTAND_FLAGS, (Object)25);
        fakeMount.syncPosition(true);
        fakeMount.spawn(this.player, this.seat.calcMotion());
        this.player.send((PacketHandle)ClientboundUpdateAttributesPacketHandle.createZeroMaxHealth((int)fakeMount.getEntityId()));
        return fakeMount;
    }

    @Override
    public void stop() {
        if (this.blindRespawn != null) {
            this.blindRespawn.despawn();
            this.blindRespawn = null;
        }
        VehicleMountController vmc = this.player.getVehicleMountController();
        ((FakeVirtualPlayer)this.fakePlayer.entity).unmount(vmc);
        ((FakeVirtualPlayer)this.fakePlayer.entityAlt).unmount(vmc);
        ((FakeVirtualPlayer)this.fakePlayer.entityAltFlip).unmount(vmc);
        for (VirtualEntity fakeMount : this.fakeMounts) {
            fakeMount.destroy(this.player);
        }
        this.fakePlayer.destroy();
    }

    @Override
    public void updatePosition(Matrix4x4 eyeTransform) {
        if (this.blindRespawn != null) {
            if (System.currentTimeMillis() > this.blindRespawn.timeout) {
                this.fakePlayer.spectateFrom(this.blindRespawn.spectated.getEntityId());
                if (this.view.getLiveMode() == FirstPersonViewMode.HEAD) {
                    this.player.sendSilent((PacketHandle)Util.createPlayerEquipmentPacket(((FakeVirtualPlayer)this.fakePlayer.entity).getEntityId(), EquipmentSlot.HEAD, this.skullItem));
                } else {
                    ((FakeVirtualPlayer)this.fakePlayer.entity).getMetaData().setFlag(EntityHandle.DATA_FLAGS, 32, false);
                }
                this.blindRespawn.despawn();
                this.blindRespawn = null;
            } else {
                this.blindRespawn.updatePosition(eyeTransform);
            }
        }
        this.fakePlayer.updatePosition(eyeTransform);
        for (VirtualEntity fakeMount : this.fakeMounts) {
            fakeMount.updatePosition(eyeTransform);
        }
    }

    @Override
    public void syncPosition(boolean absolute) {
        for (VirtualEntity fakeMount : this.fakeMounts) {
            fakeMount.syncPosition(absolute);
        }
        this.fakePlayer.syncPosition(absolute);
        if (this.blindRespawn != null) {
            this.blindRespawn.syncPosition(absolute);
        }
    }

    @Override
    public VirtualEntity getCurrentEntity() {
        return this.fakePlayer.entity;
    }

    private class BlindRespawn {
        public final VirtualEntity spectated;
        public final long timeout;

        public BlindRespawn() {
            this.spectated = new VirtualEntity(FirstPersonSpectatedEntityPlayerSitting.this.seat.getManager());
            this.spectated.setEntityType(EntityType.VILLAGER);
            this.spectated.setSyncMode(VirtualEntity.SyncMode.NORMAL);
            this.spectated.setUseMinecartInterpolation(FirstPersonSpectatedEntityPlayerSitting.this.seat.isMinecartInterpolation());
            this.spectated.setRelativeOffset(0.0, -1.62, 0.0);
            this.spectated.getMetaData().set(EntityHandle.DATA_FLAGS, (Object)32);
            this.spectated.getMetaData().set(EntityHandle.DATA_NO_GRAVITY, (Object)true);
            this.timeout = System.currentTimeMillis() + 300L;
        }

        public void spawn(Matrix4x4 eyeTransform) {
            this.spectated.updatePosition(eyeTransform);
            this.spectated.syncPosition(true);
            this.spectated.spawn(FirstPersonSpectatedEntityPlayerSitting.this.player, FirstPersonSpectatedEntityPlayerSitting.this.seat.calcMotion());
            this.spectated.forceSyncRotation();
            FirstPersonSpectatedEntityPlayerSitting.this.player.getVehicleMountController().startSpectating(this.spectated.getEntityId());
        }

        public void despawn() {
            FirstPersonSpectatedEntityPlayerSitting.this.player.getVehicleMountController().stopSpectating(this.spectated.getEntityId());
            this.spectated.destroy(FirstPersonSpectatedEntityPlayerSitting.this.player);
        }

        public void updatePosition(Matrix4x4 eyeTransform) {
            this.spectated.updatePosition(eyeTransform);
        }

        public void syncPosition(boolean absolute) {
            this.spectated.syncPosition(absolute);
        }
    }

    private static class FakeVirtualPlayer
    extends VirtualEntity {
        public final FakePlayerSpawner fakePlayer;
        public int mountedVehicleId = -1;

        public FakeVirtualPlayer(AttachmentManager manager, FakePlayerSpawner fakeplayer) {
            super(manager);
            this.fakePlayer = fakeplayer;
            this.setEntityType(EntityType.PLAYER);
            this.setSyncMode(VirtualEntity.SyncMode.NORMAL);
            this.mountedVehicleId = -1;
        }

        public void mount(VehicleMountController vmc, int mountedVehicleId) {
            this.mountedVehicleId = mountedVehicleId;
            vmc.mount(mountedVehicleId, this.getEntityId());
        }

        public void unmount(VehicleMountController vmc) {
            if (this.mountedVehicleId != -1) {
                vmc.unmount(this.mountedVehicleId, this.getEntityId());
                this.mountedVehicleId = -1;
            }
        }

        @Override
        protected void sendSpawnPackets(AttachmentViewer viewer, Vector motion) {
            FakePlayerSpawner.FakePlayerPosition orientation = FakePlayerSpawner.FakePlayerPosition.create(this.getPosX(), this.getPosY(), this.getPosZ(), (float)this.getYawPitchRoll().getY(), this.getLivePitch(), (float)this.getYawPitchRoll().getY());
            this.addViewerWithoutSpawning(viewer);
            this.fakePlayer.spawnPlayer(viewer, viewer.getPlayer(), this.getEntityId(), orientation, meta -> {
                meta.setFlag(EntityHandle.DATA_FLAGS, 32, true);
                meta.set(EntityHandle.DATA_NO_GRAVITY, (Object)true);
                this.metaData = meta;
            });
        }
    }
}

