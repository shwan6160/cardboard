/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.collections.FastIdentityHashMap
 *  com.bergerkiller.bukkit.common.protocol.PlayerGameInfo
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.controller.player;

import com.bergerkiller.bukkit.common.collections.FastIdentityHashMap;
import com.bergerkiller.bukkit.common.protocol.PlayerGameInfo;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.player.TrainCartsAttachmentViewer;
import com.bergerkiller.bukkit.tc.controller.player.network.PacketQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TrainCartsAttachmentViewerMap {
    private final TrainCarts plugin;
    private final FastIdentityHashMap<Player, TrainCartsAttachmentViewer> viewers = new FastIdentityHashMap();
    private final List<PacketQueue> queuesList = new ArrayList<PacketQueue>();

    public TrainCartsAttachmentViewerMap(TrainCarts plugin) {
        this.plugin = plugin;
    }

    public synchronized TrainCartsAttachmentViewer getViewer(Player player) {
        TrainCartsAttachmentViewer viewer = (TrainCartsAttachmentViewer)this.viewers.get((Object)player);
        if (viewer == null) {
            PlayerGameInfo playerGameInfo = PlayerGameInfo.of((Player)player);
            if (player.isValid() || Bukkit.getPlayer((UUID)player.getUniqueId()) == player) {
                PacketQueue packetQueue = PacketQueue.create(this.plugin, player, playerGameInfo);
                viewer = new TrainCartsAttachmentViewer(this.plugin, player, playerGameInfo, packetQueue);
                this.viewers.put((Object)player, (Object)viewer);
                this.queuesList.add(packetQueue);
            } else {
                PacketQueue packetQueue = PacketQueue.createNoOp(this.plugin, player);
                viewer = new TrainCartsAttachmentViewer(this.plugin, player, playerGameInfo, packetQueue);
            }
        }
        return viewer;
    }

    public synchronized void remove(Player player) {
        TrainCartsAttachmentViewer viewer = (TrainCartsAttachmentViewer)this.viewers.remove((Object)player);
        if (viewer != null) {
            viewer.stopControllingMovement();
            PacketQueue packetQueue = viewer.getPacketQueue();
            this.queuesList.remove(packetQueue);
            packetQueue.abort();
        }
    }

    public synchronized void forAllPacketQueues(Consumer<PacketQueue> operation) {
        this.queuesList.forEach(operation);
    }

    public synchronized void updateCollisionSurfaces() {
        this.viewers.values().forEach(v -> {
            if (v.collisionSurfaceTracker != null) {
                v.collisionSurfaceTracker.update();
            }
        });
    }
}

