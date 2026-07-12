/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat.spectator;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.FakePlayerSpawner;
import com.bergerkiller.bukkit.tc.attachments.VirtualEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewMode;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewSpectator;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntityHead;
import com.bergerkiller.bukkit.tc.attachments.control.seat.spectator.FirstPersonSpectatedEntity;
import com.bergerkiller.bukkit.tc.attachments.control.seat.spectator.PitchSwappedEntity;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

class FirstPersonSpectatedEntityPlayerStanding
extends FirstPersonSpectatedEntity {
    private PitchSwappedEntity<FakeVirtualPlayer> fakePlayer;
    private BlindRespawn blindRespawn = null;
    private final ItemStack skullItem;

    public FirstPersonSpectatedEntityPlayerStanding(CartAttachmentSeat seat, FirstPersonViewSpectator view, AttachmentViewer player) {
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
        this.blindRespawn = new BlindRespawn();
        this.blindRespawn.spawn(eyeTransform);
    }

    @Override
    public void stop() {
        if (this.blindRespawn != null) {
            this.blindRespawn.despawn();
            this.blindRespawn = null;
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
    }

    @Override
    public void syncPosition(boolean absolute) {
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
            this.spectated = new VirtualEntity(FirstPersonSpectatedEntityPlayerStanding.this.seat.getManager());
            this.spectated.setEntityType(EntityType.VILLAGER);
            this.spectated.setSyncMode(VirtualEntity.SyncMode.NORMAL);
            this.spectated.setUseMinecartInterpolation(FirstPersonSpectatedEntityPlayerStanding.this.seat.isMinecartInterpolation());
            this.spectated.setRelativeOffset(0.0, -1.62, 0.0);
            this.spectated.getMetaData().set(EntityHandle.DATA_FLAGS, (Object)32);
            this.spectated.getMetaData().set(EntityHandle.DATA_NO_GRAVITY, (Object)true);
            this.timeout = System.currentTimeMillis() + 300L;
        }

        public void spawn(Matrix4x4 eyeTransform) {
            this.spectated.updatePosition(eyeTransform);
            this.spectated.syncPosition(true);
            this.spectated.spawn(FirstPersonSpectatedEntityPlayerStanding.this.player, FirstPersonSpectatedEntityPlayerStanding.this.seat.calcMotion());
            this.spectated.forceSyncRotation();
            FirstPersonSpectatedEntityPlayerStanding.this.player.getVehicleMountController().startSpectating(this.spectated.getEntityId());
        }

        public void despawn() {
            FirstPersonSpectatedEntityPlayerStanding.this.player.getVehicleMountController().stopSpectating(this.spectated.getEntityId());
            this.spectated.destroy(FirstPersonSpectatedEntityPlayerStanding.this.player);
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

        public FakeVirtualPlayer(AttachmentManager manager, FakePlayerSpawner fakeplayer) {
            super(manager);
            this.fakePlayer = fakeplayer;
            this.setEntityType(EntityType.PLAYER);
            this.setSyncMode(VirtualEntity.SyncMode.NORMAL);
            this.setRelativeOffset(0.0, -1.62, 0.0);
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

