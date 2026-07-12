/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.protocol.CommonPacket
 *  com.bergerkiller.bukkit.common.protocol.PlayerGameInfo
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundBundlePacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.controller.player.network;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PlayerGameInfo;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.player.network.PacketQueue;
import com.bergerkiller.bukkit.tc.utils.CircularFIFOQueue;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundBundlePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;
import org.bukkit.entity.Player;

class BundlerPacketQueue
extends PacketQueue {
    private static final int MAX_PACKETS_PER_BUNDLE = 4092;
    private final StampedLock lock = new StampedLock();
    private final AtomicInteger bufferIndex = new AtomicInteger(Integer.MIN_VALUE);
    private final ArrayList<Object> fallbackBuffer = new ArrayList();
    private Object[] buffer = new Object[256];

    protected BundlerPacketQueue(TrainCarts plugin, Player player, PlayerGameInfo playerGameInfo, CircularFIFOQueue<CommonPacket> queue) {
        super(plugin, player, playerGameInfo, queue);
    }

    public void startBundling() {
        long writeLock = this.lock.writeLock();
        if (this.bufferIndex.get() < 0) {
            this.bufferIndex.set(0);
        }
        this.lock.unlockWrite(writeLock);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stopBundling() {
        long writeLock = this.lock.writeLock();
        try {
            int numBufferPackets = Math.min(this.buffer.length, this.bufferIndex.getAndSet(Integer.MIN_VALUE));
            if (numBufferPackets > 0) {
                int i;
                int numPackets = numBufferPackets + this.fallbackBuffer.size();
                Object[] bundlePackets = new Object[numPackets];
                for (i = numBufferPackets - 1; i >= 0; --i) {
                    Object rawPacket;
                    while ((rawPacket = this.buffer[i]) == null) {
                        Thread.yield();
                    }
                    bundlePackets[i] = rawPacket;
                }
                if (this.fallbackBuffer.isEmpty()) {
                    Arrays.fill(this.buffer, 0, numPackets, null);
                } else {
                    i = this.buffer.length;
                    int j = 0;
                    while (i < numPackets) {
                        bundlePackets[i] = this.fallbackBuffer.get(j);
                        ++i;
                        ++j;
                    }
                    this.fallbackBuffer.clear();
                    this.fallbackBuffer.trimToSize();
                    this.buffer = new Object[numPackets * 2];
                }
                if (numPackets > 4092) {
                    int endIndex;
                    i = 0;
                    do {
                        endIndex = Math.min(i + 4092, numPackets);
                        Object[] singleBundlePackets = Arrays.copyOfRange(bundlePackets, i, endIndex);
                        super.send((PacketHandle)ClientboundBundlePacketHandle.createNew(Arrays.asList(singleBundlePackets)));
                    } while ((i = endIndex) < numPackets);
                } else {
                    super.send((PacketHandle)ClientboundBundlePacketHandle.createNew(Arrays.asList(bundlePackets)));
                }
            }
        }
        finally {
            this.lock.unlockWrite(writeLock);
        }
    }

    @Override
    public void syncBegin() {
        super.syncBegin();
        this.startBundling();
    }

    @Override
    public void syncEnd() {
        super.syncEnd();
        this.stopBundling();
    }

    @Override
    public void send(CommonPacket packet) {
        this.handleSend(packet.getHandle(), () -> super.send(packet));
    }

    @Override
    public void send(PacketHandle packet) {
        this.handleSend(packet.getRaw(), () -> super.send(packet));
    }

    @Override
    public void sendSilent(CommonPacket packet) {
        this.handleSend(packet.getHandle(), () -> super.sendSilent(packet));
    }

    @Override
    public void sendSilent(PacketHandle packet) {
        this.handleSend(packet.getRaw(), () -> super.sendSilent(packet));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleSend(Object rawPacket, Runnable fallbackAction) {
        Object[] buffer;
        int index = this.bufferIndex.getAndIncrement();
        if (index >= 0 && index < (buffer = this.buffer).length) {
            Iterable bundleSubPackets = PacketHandle.tryUnwrapBundlePacket((Object)rawPacket);
            if (bundleSubPackets != null) {
                Iterator iter = bundleSubPackets.iterator();
                if (iter.hasNext()) {
                    buffer[index] = iter.next();
                    while (iter.hasNext()) {
                        this.handleSend(iter.next(), fallbackAction);
                    }
                } else {
                    buffer[index] = BundlerPacketQueue.createDummyPacket();
                }
            } else {
                buffer[index] = rawPacket;
            }
            return;
        }
        long readLock = this.lock.readLock();
        try {
            index = this.bufferIndex.getAndIncrement();
            if (index >= 0) {
                block27: {
                    Object[] buffer2 = this.buffer;
                    Iterable bundleSubPackets = PacketHandle.tryUnwrapBundlePacket((Object)rawPacket);
                    if (bundleSubPackets != null) {
                        Iterator iter = bundleSubPackets.iterator();
                        if (iter.hasNext()) {
                            while (true) {
                                if (index >= buffer2.length) {
                                    ArrayList<Object> arrayList = this.fallbackBuffer;
                                    synchronized (arrayList) {
                                        do {
                                            this.fallbackBuffer.add(iter.next());
                                        } while (iter.hasNext());
                                        break block27;
                                    }
                                }
                                buffer2[index] = iter.next();
                                if (iter.hasNext()) {
                                    index = this.bufferIndex.getAndIncrement();
                                    continue;
                                }
                                break block27;
                                break;
                            }
                        }
                        if (index < buffer2.length) {
                            buffer2[index] = BundlerPacketQueue.createDummyPacket();
                        }
                    } else if (index < buffer2.length) {
                        buffer2[index] = rawPacket;
                    } else {
                        ArrayList<Object> arrayList = this.fallbackBuffer;
                        synchronized (arrayList) {
                            this.fallbackBuffer.add(rawPacket);
                        }
                    }
                }
                return;
            }
            this.bufferIndex.set(Integer.MIN_VALUE);
            fallbackAction.run();
        }
        finally {
            this.lock.unlockRead(readLock);
        }
    }

    private static Object createDummyPacket() {
        return ClientboundRemoveEntitiesPacketHandle.createNewMultiple((int[])new int[0]).getRaw();
    }
}

