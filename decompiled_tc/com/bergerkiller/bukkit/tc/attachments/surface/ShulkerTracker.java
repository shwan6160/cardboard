/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.attachments.surface;

import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.surface.PlayerPusher;
import com.bergerkiller.bukkit.tc.attachments.surface.Shulker;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.block.BlockFace;

final class ShulkerTracker {
    private static final int ID_CHUNK_SIZE = 16;
    private Shulker[] cache = new Shulker[16];
    private int pos = -1;
    final List<Shulker> shulkersToDestroy = new ArrayList<Shulker>();
    final List<Shulker> shulkersToSpawn = new ArrayList<Shulker>();
    final List<Shulker> shulkersToMove = new ArrayList<Shulker>();

    ShulkerTracker() {
    }

    public Shulker spawn(BlockFace pushDirection) {
        int pos;
        Shulker[] cache = this.cache;
        if ((pos = this.pos--) < 0) {
            for (int i = 0; i < 16; ++i) {
                cache[i] = new Shulker(this);
            }
            this.cache = cache;
            this.pos += 16;
            pos += 16;
        }
        Shulker shulker = cache[pos];
        shulker.pushDirection = pushDirection;
        shulker.scheduleSpawn();
        return shulker;
    }

    public void destroy(Shulker shulker) {
        int pos;
        Shulker[] cache = this.cache;
        if ((pos = ++this.pos) >= cache.length) {
            this.cache = cache = Arrays.copyOf(cache, cache.length + 16);
        }
        shulker.scheduleDestroy();
        cache[pos] = shulker;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update(AttachmentViewer viewer, PlayerPusher pusher) {
        try {
            this.shulkersToDestroy.removeIf(Shulker::clearDestroy);
            this.shulkersToSpawn.removeIf(Shulker::clearSpawn);
            this.shulkersToMove.removeIf(Shulker::clearMove);
            ShulkerTracker.sendDestroyPackets(viewer, this.shulkersToDestroy);
            if (!this.shulkersToSpawn.isEmpty()) {
                boolean wasPushed;
                pusher.reset();
                do {
                    wasPushed = false;
                    for (Shulker shulker : this.shulkersToSpawn) {
                        wasPushed |= pusher.shulkerSpawned(shulker);
                    }
                } while (wasPushed);
                pusher.sendPush();
            }
            for (Shulker shulker : this.shulkersToSpawn) {
                shulker.spawn(viewer);
            }
            for (Shulker shulker : this.shulkersToMove) {
                shulker.syncPosition(viewer);
            }
        }
        finally {
            this.shulkersToDestroy.clear();
            this.shulkersToSpawn.clear();
            this.shulkersToMove.clear();
        }
    }

    private static void sendDestroyPackets(AttachmentViewer viewer, List<Shulker> shulkers) {
        if (shulkers.isEmpty()) {
            return;
        }
        if (ClientboundRemoveEntitiesPacketHandle.canDestroyMultiple()) {
            int[] ids = new int[shulkers.size() * 2];
            int idx = 0;
            for (Shulker shulker : shulkers) {
                ids[idx++] = shulker.entityId;
                ids[idx++] = shulker.mountEntityId;
            }
            viewer.send((PacketHandle)ClientboundRemoveEntitiesPacketHandle.createNewMultiple((int[])ids));
        } else {
            for (Shulker shulker : shulkers) {
                viewer.send((PacketHandle)ClientboundRemoveEntitiesPacketHandle.createNewSingle((int)shulker.entityId));
                viewer.send((PacketHandle)ClientboundRemoveEntitiesPacketHandle.createNewSingle((int)shulker.mountEntityId));
            }
        }
    }
}

