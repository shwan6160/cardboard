/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.entity.PlayerInstancePhase;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.mounting.VehicleMountHandler_1_16;
import com.bergerkiller.bukkit.common.internal.mounting.VehicleMountHandler_1_17;
import com.bergerkiller.bukkit.common.internal.mounting.VehicleMountHandler_1_8_to_1_8_8;
import com.bergerkiller.bukkit.common.internal.mounting.VehicleMountHandler_1_9_to_1_15_2;
import com.bergerkiller.bukkit.common.internal.mounting.VehicleMountHandler_BaseImpl;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommonVehicleMountManager {
    private final Map<Player, VehicleMountHandler_BaseImpl> _players = new IdentityHashMap<Player, VehicleMountHandler_BaseImpl>();
    private final PacketMonitor monitor;
    private final PacketListener listener;
    private final CommonPlugin plugin;
    private final Task cleanupTask;
    private final Task updateTask;
    private final BiFunction<CommonPlugin, Player, VehicleMountHandler_BaseImpl> _handlerMaker;
    private final PacketType[] _listenedPackets;

    public CommonVehicleMountManager(CommonPlugin plugin) {
        this.plugin = plugin;
        this.cleanupTask = new Task(plugin){

            @Override
            public void run() {
                CommonVehicleMountManager.this.cleanup();
            }
        };
        this.updateTask = new Task(plugin){
            private final List<VehicleMountHandler_BaseImpl> tmp;
            {
                this.tmp = new ArrayList<VehicleMountHandler_BaseImpl>();
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                CommonVehicleMountManager commonVehicleMountManager = CommonVehicleMountManager.this;
                synchronized (commonVehicleMountManager) {
                    this.tmp.clear();
                    this.tmp.addAll(CommonVehicleMountManager.this._players.values());
                }
                for (VehicleMountHandler_BaseImpl handler : this.tmp) {
                    handler.update();
                }
            }
        };
        this.monitor = new PacketMonitor(){

            @Override
            public void onMonitorPacketReceive(CommonPacket packet, Player player) {
            }

            @Override
            public void onMonitorPacketSend(CommonPacket packet, Player player) {
                CommonVehicleMountManager.this.get(player).handlePacketSend(packet);
            }
        };
        this.listener = new PacketListener(){

            @Override
            public void onPacketReceive(PacketReceiveEvent event) {
                CommonVehicleMountManager.this.get(event.getPlayer()).handlePacketReceive(event.getPacket());
            }

            @Override
            public void onPacketSend(PacketSendEvent event) {
                CommonVehicleMountManager.this.get(event.getPlayer()).handlePacketSend(event.getPacket());
            }
        };
        if (CommonBootstrap.evaluateMCVersion(">=", "1.17")) {
            this._handlerMaker = VehicleMountHandler_1_17::new;
            this._listenedPackets = VehicleMountHandler_1_17.LISTENED_PACKETS;
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.16")) {
            this._handlerMaker = VehicleMountHandler_1_16::new;
            this._listenedPackets = VehicleMountHandler_1_16.LISTENED_PACKETS;
        } else if (VehicleMountHandler_BaseImpl.SUPPORTS_MULTIPLE_PASSENGERS) {
            this._handlerMaker = VehicleMountHandler_1_9_to_1_15_2::new;
            this._listenedPackets = VehicleMountHandler_1_9_to_1_15_2.LISTENED_PACKETS;
        } else {
            this._handlerMaker = VehicleMountHandler_1_8_to_1_8_8::new;
            this._listenedPackets = VehicleMountHandler_1_8_to_1_8_8.LISTENED_PACKETS;
        }
    }

    public void enable() {
        this.cleanupTask.start(100L, 100L);
        this.updateTask.start(1L, 1L);
        PacketUtil.addPacketMonitor((Plugin)this.plugin, this.monitor, PacketType.OUT_ENTITY_SPAWN, PacketType.OUT_ENTITY_SPAWN_LIVING, PacketType.OUT_ENTITY_SPAWN_NAMED, PacketType.OUT_ENTITY_DESTROY, PacketType.OUT_RESPAWN);
        PacketUtil.addPacketListener((Plugin)this.plugin, this.listener, this._listenedPackets);
    }

    public void disable() {
        this.cleanupTask.stop();
        this.updateTask.stop();
        PacketUtil.removePacketMonitor(this.monitor);
        PacketUtil.removePacketListener(this.listener);
    }

    public synchronized void cleanup() {
        Iterator<Map.Entry<Player, VehicleMountHandler_BaseImpl>> iter = this._players.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Player, VehicleMountHandler_BaseImpl> entry = iter.next();
            if (PlayerInstancePhase.of(entry.getKey()).isConnected()) continue;
            VehicleMountHandler_BaseImpl handler = entry.getValue();
            iter.remove();
            handler.handleRemoved();
        }
    }

    public synchronized void remove(Player player) {
        VehicleMountHandler_BaseImpl handler = this._players.remove(player);
        if (handler != null) {
            handler.handleRemoved();
        }
    }

    public synchronized VehicleMountHandler_BaseImpl get(Player player) {
        return this._players.computeIfAbsent(player, p -> this._handlerMaker.apply(this.plugin, (Player)p));
    }
}

