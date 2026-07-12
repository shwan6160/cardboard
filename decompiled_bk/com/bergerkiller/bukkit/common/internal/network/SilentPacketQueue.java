/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.internal.network;

import java.util.LinkedList;
import java.util.ListIterator;
import org.bukkit.entity.Player;

public class SilentPacketQueue {
    private final LinkedList<SilentPacket> _queue = new LinkedList();

    public synchronized void add(Player player, Object packet) {
        this._queue.add(new SilentPacket(player, packet));
    }

    public synchronized boolean take(Player player, Object packet) {
        if (this._queue.isEmpty()) {
            return false;
        }
        long time = System.currentTimeMillis();
        ListIterator iter = this._queue.listIterator();
        while (iter.hasNext()) {
            SilentPacket sp = (SilentPacket)iter.next();
            if (time >= sp.timeout) {
                iter.remove();
                continue;
            }
            if (sp.player != player || sp.packet != packet) continue;
            iter.remove();
            return true;
        }
        return false;
    }

    public synchronized boolean cleanup() {
        if (this._queue.isEmpty()) {
            return true;
        }
        long time = System.currentTimeMillis();
        ListIterator iter = this._queue.listIterator();
        while (iter.hasNext()) {
            SilentPacket sp = (SilentPacket)iter.next();
            if (time < sp.timeout) continue;
            iter.remove();
        }
        return this._queue.isEmpty();
    }

    private static class SilentPacket {
        public final Player player;
        public final Object packet;
        public final long timeout;

        public SilentPacket(Player player, Object packet) {
            this.player = player;
            this.packet = packet;
            this.timeout = System.currentTimeMillis() + 5000L;
        }
    }
}

