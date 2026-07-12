/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.DebugUtil
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.projectile.FishingHookHandle
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.particle;

import com.bergerkiller.bukkit.common.utils.DebugUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.attachments.FakePlayerSpawner;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.projectile.FishingHookHandle;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.stream.IntStream;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class VirtualFishingLine {
    private static final Offsets OFFSETS_1_8 = new Offsets(new Vector(0.35, -1.17, -0.8), new Vector(0.35, -1.04, -0.8), new Vector(0.0, -0.49, 0.0));
    private static final Offsets OFFSETS_1_11 = new Offsets(new Vector(-0.35, -1.17, -0.8), new Vector(-0.35, -1.04, -0.8), new Vector(0.0, -0.49, 0.0));
    private static final Offsets OFFSETS_1_20_2 = new Offsets(new Vector(-0.35, -1.17, -0.8), new Vector(-0.35, -0.807, -0.8), new Vector(0.0, -0.49, 0.0));
    private final int hookedEntityId = EntityUtil.getUniqueEntityId();
    private final int holderEntityId;
    private final int holderPlayerEntityId;
    private final int hookEntityId;

    public VirtualFishingLine() {
        this(false);
    }

    public VirtualFishingLine(boolean useViewerAsHolder) {
        this.holderEntityId = useViewerAsHolder ? -1 : EntityUtil.getUniqueEntityId();
        this.holderPlayerEntityId = useViewerAsHolder ? -1 : EntityUtil.getUniqueEntityId();
        this.hookEntityId = EntityUtil.getUniqueEntityId();
    }

    private Offsets offsets(AttachmentViewer viewer) {
        if (viewer.evaluateGameVersion(">=", "1.20.2")) {
            return OFFSETS_1_20_2;
        }
        if (viewer.evaluateGameVersion(">=", "1.11")) {
            return OFFSETS_1_11;
        }
        return OFFSETS_1_8;
    }

    public void spawn(Player viewer, Vector positionA, Vector positionB) {
        this.spawn(AttachmentViewer.fallback(viewer), positionA, positionB);
    }

    public void spawn(AttachmentViewer viewer, Vector positionA, Vector positionB) {
        this.spawnWithoutLine(viewer, positionA, positionB);
        this.spawnLine(viewer, positionA, positionB);
    }

    public void spawnWithoutLine(AttachmentViewer viewer, Vector positionA, Vector positionB) {
        ArrayList<UUID> uuids = new ArrayList<UUID>(3);
        this.spawnWithoutLineCollectUUIDs(viewer, positionA, positionB, uuids);
        viewer.sendDisableCollision(uuids);
    }

    void spawnWithoutLineCollectUUIDs(AttachmentViewer viewer, Vector positionA, Vector positionB, List<UUID> uuids) {
        DataWatcher meta2;
        ClientboundAddMobPacketHandle spawnPacket2;
        UUID uuid;
        Offsets OFFSET = this.offsets(viewer);
        if (this.holderPlayerEntityId != -1) {
            FakePlayerSpawner.NO_NAMETAG_RANDOM.spawnPlayerSimple(viewer, viewer.getPlayer(), this.holderPlayerEntityId, spawnPacket -> {
                spawnPacket.setPosX(positionA.getX() + OFFSET.PLAYER.getX());
                spawnPacket.setPosY(positionA.getY() + OFFSET.PLAYER.getY());
                spawnPacket.setPosZ(positionA.getZ() + OFFSET.PLAYER.getZ());
            }, meta -> {
                meta.set(EntityHandle.DATA_NO_GRAVITY, (Object)true);
                meta.setFlag(EntityHandle.DATA_FLAGS, 32, true);
            });
        }
        if (this.holderEntityId != -1) {
            uuid = UUID.randomUUID();
            uuids.add(uuid);
            spawnPacket2 = ClientboundAddMobPacketHandle.createNew();
            spawnPacket2.setEntityId(this.holderEntityId);
            spawnPacket2.setEntityUUID(uuid);
            spawnPacket2.setEntityType(EntityType.SILVERFISH);
            spawnPacket2.setPosX(positionA.getX() + OFFSET.HOLDER.getX());
            spawnPacket2.setPosY(positionA.getY() + OFFSET.HOLDER.getY());
            spawnPacket2.setPosZ(positionA.getZ() + OFFSET.HOLDER.getZ());
            meta2 = new DataWatcher();
            meta2.set(EntityHandle.DATA_NO_GRAVITY, (Object)true);
            meta2.setFlag(EntityHandle.DATA_FLAGS, 32, true);
            viewer.sendEntityLivingSpawnPacket(spawnPacket2, meta2);
            viewer.getVehicleMountController().mount(this.holderEntityId, this.holderPlayerEntityId);
        }
        uuid = UUID.randomUUID();
        uuids.add(uuid);
        spawnPacket2 = ClientboundAddMobPacketHandle.createNew();
        spawnPacket2.setEntityId(this.hookedEntityId);
        spawnPacket2.setEntityUUID(uuid);
        spawnPacket2.setEntityType(EntityType.SILVERFISH);
        spawnPacket2.setPosX(positionB.getX() + OFFSET.HOOKED.getX());
        spawnPacket2.setPosY(positionB.getY() + OFFSET.HOOKED.getY());
        spawnPacket2.setPosZ(positionB.getZ() + OFFSET.HOOKED.getZ());
        meta2 = new DataWatcher();
        meta2.set(EntityHandle.DATA_NO_GRAVITY, (Object)true);
        meta2.setFlag(EntityHandle.DATA_FLAGS, 32, true);
        viewer.sendEntityLivingSpawnPacket(spawnPacket2, meta2);
    }

    public void update(Iterable<Player> viewers, Vector positionA, Vector positionB) {
        this.updateViewers(AttachmentViewer.fallbackIterable(viewers), positionA, positionB);
    }

    public void updateViewers(Iterable<AttachmentViewer> viewers, Vector positionA, Vector positionB) {
        for (AttachmentViewer viewer : viewers) {
            ClientboundEntityPositionSyncPacketHandle packet;
            Offsets OFFSET = this.offsets(viewer);
            if (this.holderEntityId != -1) {
                packet = ClientboundEntityPositionSyncPacketHandle.createNew((int)this.holderEntityId, (double)(positionA.getX() + OFFSET.HOLDER.getX()), (double)(positionA.getY() + OFFSET.HOLDER.getY()), (double)(positionA.getZ() + OFFSET.HOLDER.getZ()), (float)0.0f, (float)0.0f, (boolean)false);
                viewer.send((PacketHandle)packet);
            }
            packet = ClientboundEntityPositionSyncPacketHandle.createNew((int)this.hookedEntityId, (double)(positionB.getX() + OFFSET.HOOKED.getX()), (double)(positionB.getY() + OFFSET.HOOKED.getY()), (double)(positionB.getZ() + OFFSET.HOOKED.getZ()), (float)0.0f, (float)0.0f, (boolean)false);
            viewer.send((PacketHandle)packet);
        }
    }

    public void destroy(Player viewer) {
        this.destroy(AttachmentViewer.fallback(viewer));
    }

    public void destroy(AttachmentViewer viewer) {
        int[] entityIds = IntStream.of(this.hookedEntityId, this.holderEntityId, this.holderPlayerEntityId, this.hookEntityId).filter(id -> id != -1).toArray();
        if (ClientboundRemoveEntitiesPacketHandle.canDestroyMultiple()) {
            viewer.send((PacketHandle)ClientboundRemoveEntitiesPacketHandle.createNewMultiple((int[])entityIds));
        } else {
            for (int entityId : entityIds) {
                viewer.send((PacketHandle)ClientboundRemoveEntitiesPacketHandle.createNewSingle((int)entityId));
            }
        }
    }

    public void spawnLine(AttachmentViewer viewer, Vector positionA, Vector positionB) {
        ClientboundAddEntityPacketHandle spawnPacket = ClientboundAddEntityPacketHandle.createNew();
        spawnPacket.setEntityId(this.hookEntityId);
        spawnPacket.setEntityUUID(UUID.randomUUID());
        spawnPacket.setEntityType(EntityType.FISHING_HOOK);
        spawnPacket.setPosX(positionB.getX());
        spawnPacket.setPosY(positionB.getY() - 0.25);
        spawnPacket.setPosZ(positionB.getZ());
        spawnPacket.setExtraData(this.holderPlayerEntityId == -1 ? viewer.getEntityId() : this.holderPlayerEntityId);
        viewer.send((PacketHandle)spawnPacket);
        DataWatcher meta = new DataWatcher();
        meta.set(FishingHookHandle.DATA_HOOKED_ENTITY_ID, (Object)OptionalInt.of(this.hookedEntityId));
        meta.setFlag(EntityHandle.DATA_FLAGS, 32, true);
        viewer.send(ClientboundSetEntityDataPacketHandle.createNew((int)this.hookEntityId, (DataWatcher)meta, (boolean)true).toCommonPacket());
    }

    public void destroyLine(AttachmentViewer viewer) {
        viewer.send((PacketHandle)ClientboundRemoveEntitiesPacketHandle.createNewSingle((int)this.hookEntityId));
    }

    private static class Offsets {
        public final Vector PLAYER;
        public final Vector HOLDER;
        public final Vector HOOKED;

        public Offsets(Vector OFFSET_PLAYER, Vector OFFSET_HOLDER, Vector OFFSET_HOOKED) {
            this.PLAYER = OFFSET_PLAYER;
            this.HOLDER = OFFSET_HOLDER;
            this.HOOKED = OFFSET_HOOKED;
        }

        public Offsets debug() {
            return new Offsets(new Vector(DebugUtil.getDoubleValue((String)"ax", (double)this.PLAYER.getX()), DebugUtil.getDoubleValue((String)"ay", (double)this.PLAYER.getY()), DebugUtil.getDoubleValue((String)"az", (double)this.PLAYER.getZ())), new Vector(DebugUtil.getDoubleValue((String)"bx", (double)this.HOLDER.getX()), DebugUtil.getDoubleValue((String)"by", (double)this.HOLDER.getY()), DebugUtil.getDoubleValue((String)"bz", (double)this.HOLDER.getZ())), new Vector(DebugUtil.getDoubleValue((String)"cx", (double)this.HOOKED.getX()), DebugUtil.getDoubleValue((String)"cy", (double)this.HOOKED.getY()), DebugUtil.getDoubleValue((String)"cz", (double)this.HOOKED.getZ())));
        }
    }
}

