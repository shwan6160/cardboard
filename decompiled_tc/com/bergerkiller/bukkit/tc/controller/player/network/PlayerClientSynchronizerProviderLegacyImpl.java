/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.events.PacketReceiveEvent
 *  com.bergerkiller.bukkit.common.events.PacketSendEvent
 *  com.bergerkiller.bukkit.common.protocol.PacketListener
 *  com.bergerkiller.bukkit.common.protocol.PacketType
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.wrappers.RelativeFlags
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.common.ClientboundKeepAlivePacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerPositionPacketHandle
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.controller.player.network;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.RelativeFlags;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.controller.player.network.PlayerClientSynchronizer;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.common.ClientboundKeepAlivePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerPositionPacketHandle;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

final class PlayerClientSynchronizerProviderLegacyImpl
implements PlayerClientSynchronizer.Provider,
PacketListener {
    private final TrainCarts traincarts;
    private final Task cleanupTask;
    private final Map<Player, SyncQueue> queues = new IdentityHashMap<Player, SyncQueue>();
    private Map<Player, SyncQueue> queuesVisible = Collections.emptyMap();
    private boolean disabled = false;

    public PlayerClientSynchronizerProviderLegacyImpl(TrainCarts traincarts) {
        this.traincarts = traincarts;
        this.cleanupTask = new Task((JavaPlugin)traincarts){

            public void run() {
                PlayerClientSynchronizerProviderLegacyImpl.this.cleanupQuitPlayerQueues();
            }
        };
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PlayerClientSynchronizer forViewer(AttachmentViewer viewer) {
        PlayerClientSynchronizer queue = this.queuesVisible.get(viewer.getPlayer());
        if (queue == null) {
            PlayerClientSynchronizerProviderLegacyImpl playerClientSynchronizerProviderLegacyImpl = this;
            synchronized (playerClientSynchronizerProviderLegacyImpl) {
                if (this.disabled || !viewer.isConnected()) {
                    return this.createNoOp(viewer.getPlayer());
                }
                queue = this.queues.computeIfAbsent(viewer.getPlayer(), p -> new SyncQueue(viewer));
                this.updateVisibleQueueMap();
            }
        }
        return queue;
    }

    @Override
    public void enable() {
        this.traincarts.register(this, new PacketType[]{PacketType.IN_KEEP_ALIVE});
        this.traincarts.register(new Listener(){

            @EventHandler(priority=EventPriority.MONITOR)
            public void onPlayerQuit(PlayerQuitEvent event) {
                SyncQueue queue = (SyncQueue)PlayerClientSynchronizerProviderLegacyImpl.this.queuesVisible.get(event.getPlayer());
                if (queue != null) {
                    queue.setHasQuit();
                }
            }
        });
        this.cleanupTask.start(20L, 20L);
        this.disabled = false;
    }

    @Override
    public synchronized void disable() {
        this.cleanupTask.stop();
        this.queues.values().forEach(SyncQueue::setHasQuit);
        this.queues.clear();
        this.queuesVisible = Collections.emptyMap();
        this.disabled = true;
    }

    public void onPacketReceive(PacketReceiveEvent event) {
        long keepAliveId;
        PendingAcknowledgement ack;
        SyncQueue queue = this.queuesVisible.get(event.getPlayer());
        if (queue != null && queue.hasPendingAcknowledgements() && (ack = queue.acknowledge(keepAliveId = ((Long)event.getPacket().read(PacketType.IN_KEEP_ALIVE.key)).longValue())) != null) {
            ack.call();
            event.setCancelled(true);
        }
    }

    public void onPacketSend(PacketSendEvent event) {
    }

    private synchronized void cleanupQuitPlayerQueues() {
        if (this.queues.values().removeIf(SyncQueue::hasQuitSomeTimeAgo)) {
            this.updateVisibleQueueMap();
        }
    }

    private void updateVisibleQueueMap() {
        this.queuesVisible = this.queues.isEmpty() ? Collections.emptyMap() : new IdentityHashMap<Player, SyncQueue>(this.queues);
    }

    private static class SyncQueue
    implements PlayerClientSynchronizer {
        private static final RelativeFlags NO_CHANGE_RELATIVE_FLAGS = RelativeFlags.RELATIVE_POSITION_ROTATION.withRelativeDelta().withRelativeDeltaRotation();
        private static final IntFunction<ClientboundPlayerPositionPacketHandle> NO_CHANGE_ACK_PACKET = teleportId -> ClientboundPlayerPositionPacketHandle.createNew((double)0.0, (double)0.0, (double)0.0, (float)0.0f, (float)0.0f, (double)0.0, (double)0.0, (double)0.0, (RelativeFlags)NO_CHANGE_RELATIVE_FLAGS, (int)teleportId);
        private final AttachmentViewer viewer;
        private final Player player;
        private boolean hasQuit = false;
        private int quitTickTime;
        private final Deque<PendingAcknowledgement> pending = new LinkedList<PendingAcknowledgement>();

        public SyncQueue(AttachmentViewer viewer) {
            this.viewer = viewer;
            this.player = viewer.getPlayer();
        }

        public synchronized boolean hasQuitSomeTimeAgo() {
            return this.hasQuit && CommonUtil.getServerTicks() - this.quitTickTime > 2;
        }

        public synchronized void setHasQuit() {
            this.hasQuit = true;
            this.quitTickTime = CommonUtil.getServerTicks();
            this.pending.clear();
        }

        public synchronized boolean hasPendingAcknowledgements() {
            return !this.pending.isEmpty();
        }

        public synchronized PendingAcknowledgement acknowledge(long keepAliveId) {
            PendingAcknowledgement first = this.pending.pollFirst();
            if (first != null) {
                if (first.getKeepAliveId() == keepAliveId) {
                    return first;
                }
                this.pending.addFirst(first);
            }
            return null;
        }

        @Override
        public Player getPlayer() {
            return this.player;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void synchronize(IntFunction<ClientboundPlayerPositionPacketHandle> positionPacketMaker, Consumer<ClientboundPlayerPositionPacketHandle> callback) {
            if (this.hasQuit) {
                return;
            }
            long keepAliveId = SyncQueue.getSafeAwaitKeepAliveId();
            ClientboundPlayerPositionPacketHandle packet = positionPacketMaker.apply(0);
            ClientboundKeepAlivePacketHandle keepAlivePacket = ClientboundKeepAlivePacketHandle.createNew((long)keepAliveId);
            PendingAcknowledgement pendingAck = new PendingAcknowledgement(keepAliveId, packet, callback);
            SyncQueue syncQueue = this;
            synchronized (syncQueue) {
                if (this.hasQuit) {
                    return;
                }
                this.pending.addLast(pendingAck);
                this.viewer.send((PacketHandle)packet);
                this.viewer.send((PacketHandle)keepAlivePacket);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void synchronizeBundle(List<? extends PacketHandle> packets, Runnable startCallback, Runnable endCallback) {
            if (this.hasQuit) {
                return;
            }
            SyncQueue syncQueue = this;
            synchronized (syncQueue) {
                if (this.hasQuit) {
                    return;
                }
                this.synchronize(startCallback);
                for (PacketHandle packetHandle : packets) {
                    this.viewer.send(packetHandle);
                }
                this.synchronize(endCallback);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void synchronize(Runnable callback) {
            if (this.hasQuit) {
                return;
            }
            long keepAliveId = SyncQueue.getSafeAwaitKeepAliveId();
            ClientboundKeepAlivePacketHandle keepAlivePacket = ClientboundKeepAlivePacketHandle.createNew((long)keepAliveId);
            PendingAcknowledgement pendingAck = new PendingAcknowledgement(keepAliveId, null, np -> callback.run());
            SyncQueue syncQueue = this;
            synchronized (syncQueue) {
                if (this.hasQuit) {
                    return;
                }
                this.pending.addLast(pendingAck);
                this.viewer.send((PacketHandle)keepAlivePacket);
            }
        }

        private static long getSafeAwaitKeepAliveId() {
            long timeStampMillis = System.nanoTime() / 1000000L;
            return timeStampMillis -= 30000L;
        }
    }

    private static class PendingAcknowledgement {
        private final long keepAliveId;
        private final ClientboundPlayerPositionPacketHandle position;
        private final Consumer<ClientboundPlayerPositionPacketHandle> callback;

        public PendingAcknowledgement(long keepAliveId, ClientboundPlayerPositionPacketHandle position, Consumer<ClientboundPlayerPositionPacketHandle> callback) {
            this.keepAliveId = keepAliveId;
            this.position = position;
            this.callback = callback;
        }

        public long getKeepAliveId() {
            return this.keepAliveId;
        }

        public void call() {
            this.callback.accept(this.position);
        }
    }
}

