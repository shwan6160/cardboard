/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.internal.CommonCapabilities
 *  com.bergerkiller.bukkit.common.protocol.CommonPacket
 *  com.bergerkiller.bukkit.common.protocol.PacketType
 *  com.bergerkiller.bukkit.common.protocol.PlayerGameInfo
 *  com.bergerkiller.bukkit.common.utils.PacketUtil
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.common.ClientboundCustomPayloadPacketHandle
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.controller.player.network;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.protocol.PlayerGameInfo;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.player.network.BundlerPacketQueue;
import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.NetworkInterface;
import com.bergerkiller.bukkit.tc.utils.CircularFIFOQueue;
import com.bergerkiller.bukkit.tc.utils.CircularFIFOQueueStampedRW;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.common.ClientboundCustomPayloadPacketHandle;
import org.bukkit.entity.Player;

public class PacketQueue
implements NetworkInterface {
    private final TrainCarts plugin;
    private final Player player;
    private final CircularFIFOQueue<CommonPacket> queue;
    private volatile Thread thread;

    public static PacketQueue create(TrainCarts plugin, Player player, PlayerGameInfo playerGameInfo) {
        CircularFIFOQueueStampedRW<CommonPacket> fifoQueue = new CircularFIFOQueueStampedRW<CommonPacket>();
        if (CommonCapabilities.HAS_BUNDLE_PACKET && playerGameInfo.evaluateVersion(">=", "1.19.4")) {
            return new BundlerPacketQueue(plugin, player, playerGameInfo, fifoQueue);
        }
        return new PacketQueue(plugin, player, playerGameInfo, fifoQueue);
    }

    public static PacketQueue createNoOp(TrainCarts plugin, Player player) {
        return new PacketQueue(plugin, player);
    }

    private PacketQueue(TrainCarts plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.queue = CircularFIFOQueue.forward(this::processPacket);
        this.thread = null;
    }

    protected PacketQueue(TrainCarts plugin, Player player, PlayerGameInfo playerGameInfo, CircularFIFOQueue<CommonPacket> queue) {
        this.plugin = plugin;
        this.player = player;
        this.queue = queue;
        this.queue.setWakeCallback(this::startProcessingPackets);
        this.thread = null;
    }

    public void send(PacketHandle packet) {
        this.queue.put(packet.toCommonPacket());
    }

    public void send(CommonPacket packet) {
        this.queue.put(packet);
    }

    public void sendSilent(CommonPacket packet) {
        this.queue.put(new SilentCommonPacket(packet.getHandle(), packet.getType()));
    }

    public void sendSilent(PacketHandle packet) {
        this.queue.put(new SilentCommonPacket(packet.getRaw(), packet.getPacketType()));
    }

    @Override
    public void sendMessage(Player player, String channel, byte[] message) {
        if (player != this.player) {
            throw new IllegalArgumentException("Wrong network interface used, interface is of " + this.player.getName() + " but updated " + player.getName());
        }
        if (this.plugin.getSmoothCoastersAPI().getVersion(player) < 5) {
            this.queue.put(ClientboundCustomPayloadPacketHandle.createNew((String)channel, (byte[])message).toCommonPacket());
        } else {
            this.send((PacketHandle)ClientboundCustomPayloadPacketHandle.createNew((String)channel, (byte[])message));
        }
    }

    public void abort() {
        this.queue.abort();
    }

    public void syncBegin() {
        while (!this.queue.isEmpty()) {
            Thread.yield();
        }
    }

    public void syncEnd() {
    }

    private void startProcessingPackets() {
        if (this.thread == null && !this.queue.isAborted()) {
            Thread newThread = new Thread(this::processPacketsThread, "TC-PacketWriterThread-" + this.player.getEntityId());
            newThread.setDaemon(true);
            this.thread = newThread;
            newThread.start();
        }
    }

    private void processPacketsThread() {
        CircularFIFOQueue<CommonPacket> queue = this.queue;
        while (true) {
            try {
                while (true) {
                    this.processPacket(queue.take(60000L));
                }
            }
            catch (CircularFIFOQueue.EmptyQueueException e) {
                if (!queue.runIfEmpty(() -> {
                    this.thread = null;
                })) continue;
                return;
            }
            break;
        }
    }

    private void processPacket(CommonPacket packet) {
        PacketUtil.sendPacket((Player)this.player, (CommonPacket)packet, (!(packet instanceof SilentCommonPacket) ? 1 : 0) != 0);
    }

    public String toString() {
        return "PacketQueue{player=" + this.player.getName() + "}";
    }

    public static final class SilentCommonPacket
    extends CommonPacket {
        public SilentCommonPacket(Object packetHandle, PacketType packetType) {
            super(packetHandle, packetType);
        }
    }
}

