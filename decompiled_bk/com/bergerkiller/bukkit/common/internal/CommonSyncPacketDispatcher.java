/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.AsyncTask;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.common.ClientboundKeepAlivePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.common.ServerboundKeepAlivePacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.LockSupport;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Deprecated
public class CommonSyncPacketDispatcher
extends AsyncTask
implements PacketListener {
    private static final long TICK_SPAN_NANOS = 50000000L;
    private static final PacketType[] MONITORED_PACKET_TYPES = new PacketType[]{PacketType.IN_KEEP_ALIVE, PacketType.IN_POSITION, PacketType.IN_LOOK, PacketType.IN_POSITION_LOOK};
    private final Map<Player, State> _states = new IdentityHashMap<Player, State>();
    private final ArrayList<State> _states_list = new ArrayList();
    private Task _keepAliveTask;
    private CommonPlugin _plugin;

    private synchronized State getState(Player player) {
        return this._states.computeIfAbsent(player, x$0 -> new State((Player)x$0));
    }

    public void sendPacket(Player player, CommonPacket packet) {
        this.getState(player).addPacket(new QueuedPacket(packet.getType(), packet.getHandle()));
    }

    public void sendPacket(Player player, PacketHandle packet) {
        this.getState(player).addPacket(new QueuedPacket(packet.getPacketType(), packet.getRaw()));
    }

    @Override
    public synchronized void onPacketReceive(PacketReceiveEvent event) {
        State s = this._states.get(event.getPlayer());
        if (s == null) {
            return;
        }
        if (event.getType() == PacketType.IN_KEEP_ALIVE) {
            if (!s.syncTimeSentActive) {
                return;
            }
            if ((long)s.syncTimeSent != ServerboundKeepAlivePacketHandle.createHandle(event.getPacket().getHandle()).getKey()) {
                return;
            }
            s.syncTimeSentActive = false;
            s.latency = CommonSyncPacketDispatcher.getTime() - s.syncTimeSent >> 1;
            event.setCancelled(true);
        } else {
            long new_phase = Math.floorMod(System.nanoTime() - 10000000L - (long)s.latency, 50000000L);
            if (s.hasSync) {
                long diff = new_phase - s.syncTime;
                if (diff < -25000000L) {
                    diff += 50000000L;
                } else if (diff > 25000000L) {
                    diff -= 50000000L;
                }
                s.syncTime += diff / 10L;
            } else {
                s.hasSync = true;
                s.syncTime = new_phase;
            }
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
    }

    public void enable(CommonPlugin plugin) {
        this._plugin = plugin;
        plugin.getPacketHandler().addPacketListener((Plugin)plugin, this, MONITORED_PACKET_TYPES);
        this.start(true);
        this._keepAliveTask = new Task(plugin){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                Invoker createKeepAlive = ((Template.StaticMethod)ClientboundKeepAlivePacketHandle.T.createNew.raw).invoker;
                CommonSyncPacketDispatcher commonSyncPacketDispatcher = CommonSyncPacketDispatcher.this;
                synchronized (commonSyncPacketDispatcher) {
                    for (State s : CommonSyncPacketDispatcher.this._states.values()) {
                        s.syncTimeSent = CommonSyncPacketDispatcher.getTime();
                        s.syncTimeSentActive = true;
                        s.sendPacket(PacketType.OUT_KEEP_ALIVE, createKeepAlive.invoke(null, s.syncTimeSent));
                    }
                }
            }
        }.start(0L, 20L);
    }

    public void disable(CommonPlugin plugin) {
        plugin.getPacketHandler().removePacketListener(this);
        this.stop();
        this.waitFinished();
        Task.stop(this._keepAliveTask);
        this._keepAliveTask = null;
        this._plugin = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        CommonSyncPacketDispatcher commonSyncPacketDispatcher = this;
        synchronized (commonSyncPacketDispatcher) {
            this._states_list.clear();
            Iterator<State> iter = this._states.values().iterator();
            while (iter.hasNext()) {
                State s = iter.next();
                if (s.isValid()) {
                    this._states_list.add(s);
                    continue;
                }
                iter.remove();
            }
        }
        while (!this._states_list.isEmpty()) {
            long now = System.nanoTime();
            State next = null;
            long nanosUntilSync = 50000000L;
            int nextIndex = 0;
            if (this._states_list.size() == 1) {
                next = this._states_list.get(0);
                nanosUntilSync = next.getNanosUntilSync(now);
            } else {
                Iterator<State> iter = this._states_list.iterator();
                int i = 0;
                do {
                    State s;
                    long n;
                    if ((n = (s = iter.next()).getNanosUntilSync(now)) <= nanosUntilSync) {
                        nanosUntilSync = n;
                        next = s;
                        nextIndex = i;
                    }
                    ++i;
                } while (iter.hasNext());
            }
            this._states_list.remove(nextIndex);
            long remaining = nanosUntilSync;
            do {
                LockSupport.parkNanos(remaining);
            } while ((remaining = nanosUntilSync - (System.nanoTime() - now)) > 0L);
            State state = next;
            synchronized (state) {
                next.unsafe_sync();
                for (QueuedPacket packet : next.packets) {
                    next.sendPacket(packet.type, packet.packet);
                }
                next.packets.clear();
            }
        }
    }

    private static int getTime() {
        return (int)System.nanoTime();
    }

    private final class State {
        public final Player player;
        public final Queue<QueuedPacket> packets;
        public final Queue<QueuedPacket> packets_sync;
        public boolean syncTimeSentActive = false;
        public int syncTimeSent = 0;
        public long syncTime;
        public boolean hasSync = false;
        public int latency = 0;
        public volatile int tick;
        public long lastSendPacketTime;

        public long getNanosUntilSync(long nanos_now) {
            long nanos = Math.floorMod(nanos_now - this.syncTime, 50000000L);
            return 50000000L - nanos;
        }

        public State(Player player) {
            this.player = player;
            this.syncTime = CommonSyncPacketDispatcher.getTime();
            this.packets = new LinkedList<QueuedPacket>();
            this.packets_sync = new LinkedList<QueuedPacket>();
            this.tick = CommonUtil.getServerTicks();
            this.lastSendPacketTime = System.currentTimeMillis();
        }

        public void unsafe_sync() {
            int curr_tick = CommonUtil.getServerTicks();
            if (this.tick != curr_tick) {
                this.tick = curr_tick;
                this.packets.addAll(this.packets_sync);
                this.packets_sync.clear();
            }
        }

        public synchronized void addPacket(QueuedPacket packet) {
            this.unsafe_sync();
            this.packets_sync.add(packet);
            this.lastSendPacketTime = System.currentTimeMillis();
        }

        public void sendPacket(PacketType type, Object packet) {
            CommonSyncPacketDispatcher.this._plugin.getPacketHandler().sendPacket(this.player, type, packet, false);
        }

        public boolean isValid() {
            if (!this.player.isValid()) {
                return false;
            }
            return System.currentTimeMillis() - this.lastSendPacketTime <= 60000L;
        }
    }

    private static final class QueuedPacket {
        public final PacketType type;
        public final Object packet;

        public QueuedPacket(PacketType type, Object packet) {
            this.type = type;
            this.packet = packet;
        }
    }
}

