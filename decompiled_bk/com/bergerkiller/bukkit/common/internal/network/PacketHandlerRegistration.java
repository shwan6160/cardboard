/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.internal.network;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundBundlePacketHandle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

class PacketHandlerRegistration {
    public final Map<PacketType, List<PacketListener>> listeners = new HashMap<PacketType, List<PacketListener>>();
    public final Map<PacketType, List<PacketMonitor>> monitors = new HashMap<PacketType, List<PacketMonitor>>();
    public final Map<Plugin, List<PacketListener>> listenerPlugins = new HashMap<Plugin, List<PacketListener>>();
    public final Map<Plugin, List<PacketMonitor>> monitorPlugins = new HashMap<Plugin, List<PacketMonitor>>();

    PacketHandlerRegistration() {
    }

    public void forAllListeners(BiConsumer<Plugin, PacketListener> action) {
        for (Map.Entry<Plugin, List<PacketListener>> entry : this.listenerPlugins.entrySet()) {
            for (PacketListener listener : entry.getValue()) {
                action.accept(entry.getKey(), listener);
            }
        }
    }

    public void forAllMonitors(BiConsumer<Plugin, PacketMonitor> action) {
        for (Map.Entry<Plugin, List<PacketMonitor>> entry : this.monitorPlugins.entrySet()) {
            for (PacketMonitor monitor : entry.getValue()) {
                action.accept(entry.getKey(), monitor);
            }
        }
    }

    public Collection<Plugin> getListeningPlugins(PacketType type) {
        List<PacketListener> listenerList = this.listeners.get(type);
        if (listenerList == null) {
            return Collections.emptySet();
        }
        ArrayList<Plugin> plugins = new ArrayList<Plugin>();
        block0: for (Map.Entry<Plugin, List<PacketListener>> entry : this.listenerPlugins.entrySet()) {
            for (PacketListener listener : listenerList) {
                if (!entry.getValue().contains(listener)) continue;
                plugins.add(entry.getKey());
                continue block0;
            }
        }
        return plugins;
    }

    public void addPacketMonitor(Plugin plugin, PacketMonitor monitor, PacketType[] types) {
        if (monitor == null) {
            throw new IllegalArgumentException("Monitor is not allowed to be null");
        }
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin is not allowed to be null");
        }
        for (PacketType type : types) {
            List<PacketMonitor> monitorList = this.monitors.get(type);
            if (monitorList == null) {
                monitorList = new ArrayList<PacketMonitor>();
                this.monitors.put(type, monitorList);
            }
            monitorList.add(monitor);
            List<PacketMonitor> list = this.monitorPlugins.get(plugin);
            if (list == null) {
                list = new ArrayList<PacketMonitor>(2);
                this.monitorPlugins.put(plugin, list);
            }
            list.add(monitor);
        }
    }

    public void addPacketListener(Plugin plugin, PacketListener listener, PacketType[] types) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener is not allowed to be null");
        }
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin is not allowed to be null");
        }
        for (PacketType type : types) {
            List<PacketListener> listenerList = this.listeners.get(type);
            if (listenerList == null) {
                listenerList = new ArrayList<PacketListener>();
                this.listeners.put(type, listenerList);
            }
            listenerList.add(listener);
            List<PacketListener> list = this.listenerPlugins.get(plugin);
            if (list == null) {
                list = new ArrayList<PacketListener>(2);
                this.listenerPlugins.put(plugin, list);
            }
            list.add(listener);
        }
    }

    public void removePacketListeners(Plugin plugin) {
        List<PacketMonitor> monitors;
        List<PacketListener> listeners = this.listenerPlugins.get(plugin);
        if (listeners != null) {
            for (PacketListener listener : listeners) {
                this.removePacketListener(listener, false);
            }
        }
        if ((monitors = this.monitorPlugins.get(plugin)) != null) {
            for (PacketMonitor monitor : monitors) {
                this.removePacketMonitor(monitor, false);
            }
        }
    }

    public void removePacketMonitor(PacketMonitor monitor) {
        this.removePacketMonitor(monitor, true);
    }

    public void removePacketMonitor(PacketMonitor monitor, boolean fromPlugins) {
        if (monitor == null) {
            return;
        }
        for (List<PacketMonitor> monitorList : this.monitors.values()) {
            monitorList.remove(monitor);
        }
        if (fromPlugins) {
            for (Plugin plugin : this.monitorPlugins.keySet().toArray(new Plugin[0])) {
                List<PacketMonitor> list = this.monitorPlugins.get(plugin);
                if (list == null || !list.remove(monitor) || !list.isEmpty()) continue;
                this.monitorPlugins.remove(plugin);
            }
        }
    }

    public void removePacketListener(PacketListener listener) {
        this.removePacketListener(listener, true);
    }

    public void removePacketListener(PacketListener listener, boolean fromPlugins) {
        if (listener == null) {
            return;
        }
        for (List<PacketListener> listenerList : this.listeners.values()) {
            listenerList.remove(listener);
        }
        if (fromPlugins) {
            for (Plugin plugin : this.listenerPlugins.keySet().toArray(new Plugin[0])) {
                List<PacketListener> list = this.listenerPlugins.get(plugin);
                if (list == null || !list.remove(listener) || !list.isEmpty()) continue;
                this.listenerPlugins.remove(plugin);
            }
        }
    }

    public HandlerResult handlePacketSend(Player player, PacketType packetType, Object packet, boolean is_silent, boolean wasCancelled) {
        HandlerResult result;
        if (packetType == PacketType.OUT_BUNDLE) {
            ClientboundBundlePacketHandle bundle = ClientboundBundlePacketHandle.createHandle(packet);
            if (is_silent) {
                for (Object subPacket : bundle.subPackets()) {
                    this.handlePacketSendMonitor(player, packetType, subPacket);
                }
                return new HandlerResult(packet, packetType, false);
            }
            if (bundle.filterSubPackets(p -> this.handlePacketSend(player, PacketType.getType(p), p, false, wasCancelled) != null)) {
                return new HandlerResult(packet, packetType, false);
            }
            return new HandlerResult(packet, packetType, true);
        }
        if (is_silent) {
            result = new HandlerResult(packet, packetType, false);
        } else {
            result = this.handlePacketSendListener(player, packetType, packet, wasCancelled);
            if (result.isCancelled) {
                return result;
            }
            packet = result.packet;
            packetType = result.packetType;
        }
        this.handlePacketSendMonitor(player, packetType, packet);
        return result;
    }

    public HandlerResult handlePacketSendListener(Player player, PacketType packetType, Object packet, boolean wasCancelled) {
        List<PacketListener> listenerList = this.listeners.get(packetType);
        if (listenerList != null) {
            CommonPacket cp = new CommonPacket(packet, packetType);
            PacketSendEvent ev = new PacketSendEvent(player, cp);
            ev.setCancelled(wasCancelled);
            for (PacketListener listener : listenerList) {
                try {
                    listener.onPacketSend(ev);
                    if (ev.getPacket() == cp) continue;
                    cp = ev.getPacket();
                    packet = cp.getHandle();
                    PacketType newType = ev.getType();
                    if (newType == packetType) continue;
                    packetType = newType;
                    break;
                }
                catch (Throwable t) {
                    Logging.LOGGER_NETWORK.log(Level.SEVERE, "Error occurred in onPacketSend handling " + packetType + ":", t);
                }
            }
            if (ev.isCancelled()) {
                return new HandlerResult(packet, packetType, true);
            }
        }
        return new HandlerResult(packet, packetType, false);
    }

    public void handlePacketSendMonitor(Player player, PacketType packetType, Object packet) {
        List<PacketMonitor> monitorList = this.monitors.get(packetType);
        if (monitorList != null) {
            CommonPacket cp = new CommonPacket(packet, packetType);
            for (PacketMonitor monitor : monitorList) {
                try {
                    monitor.onMonitorPacketSend(cp, player);
                }
                catch (Throwable t) {
                    Logging.LOGGER_NETWORK.log(Level.SEVERE, "Error occurred in onMonitorPacketSend handling " + packetType + ":", t);
                }
            }
        }
    }

    public HandlerResult handlePacketReceive(Player player, Object packet, boolean wasCancelled) {
        List<PacketMonitor> monitorList;
        if (player == null || !PacketHandle.T.isAssignableFrom(packet)) {
            return new HandlerResult(packet, null, wasCancelled);
        }
        PacketType type = PacketType.getType(packet);
        List<PacketListener> listenerList = this.listeners.get(type);
        if (listenerList != null) {
            CommonPacket cp = new CommonPacket(packet, type);
            PacketReceiveEvent ev = new PacketReceiveEvent(player, cp);
            ev.setCancelled(wasCancelled);
            for (PacketListener listener : listenerList) {
                try {
                    listener.onPacketReceive(ev);
                    if (ev.getPacket() == cp) continue;
                    cp = ev.getPacket();
                    packet = cp.getHandle();
                    PacketType newType = ev.getType();
                    if (newType == type) continue;
                    type = newType;
                    break;
                }
                catch (Throwable t) {
                    Logging.LOGGER_NETWORK.log(Level.SEVERE, "Error occurred in onPacketReceive handling " + type + ":", t);
                }
            }
            if (ev.isCancelled()) {
                return new HandlerResult(packet, type, true);
            }
        }
        if ((monitorList = this.monitors.get(type)) != null) {
            CommonPacket cp = new CommonPacket(packet, type);
            for (PacketMonitor monitor : monitorList) {
                try {
                    monitor.onMonitorPacketReceive(cp, player);
                }
                catch (Throwable t) {
                    Logging.LOGGER_NETWORK.log(Level.SEVERE, "Error occurred in onMonitorPacketReceive handling " + type + ":", t);
                }
            }
        }
        return new HandlerResult(packet, type, false);
    }

    public static class HandlerResult {
        public final Object packet;
        public final PacketType packetType;
        public final boolean isCancelled;

        public HandlerResult(Object packet, PacketType packetType, boolean isCancelled) {
            this.packet = packet;
            this.packetType = packetType;
            this.isCancelled = isCancelled;
        }
    }
}

