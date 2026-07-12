/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.internal.network;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.ClassMap;
import com.bergerkiller.bukkit.common.internal.PacketHandler;
import com.bergerkiller.bukkit.common.internal.network.PacketHandlerRegistration;
import com.bergerkiller.bukkit.common.internal.network.SilentPacketQueue;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.server.network.ServerGamePacketListenerImplHandle;
import com.bergerkiller.mountiplex.reflection.SafeMethod;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class PacketHandlerHooked
implements PacketHandler {
    private final PacketHandlerRegistration handlers = new PacketHandlerRegistration();
    private final ClassMap<SafeMethod<?>> receiverMethods = new ClassMap();
    private final SilentPacketQueue silentQueue = new SilentPacketQueue();

    @Override
    public boolean onEnable() {
        Class<?> packetType = PacketHandle.T.getType();
        for (Method method : ServerGamePacketListenerImplHandle.T.getType().getDeclaredMethods()) {
            Class<?> arg;
            if (method.getReturnType() != Void.TYPE || method.getParameterTypes().length != 1 || !Modifier.isPublic(method.getModifiers()) || !packetType.isAssignableFrom(arg = method.getParameterTypes()[0]) || arg == packetType) continue;
            this.receiverMethods.put(arg, new SafeMethod(method));
        }
        return true;
    }

    @Override
    public void removePacketListeners(Plugin plugin) {
        this.handlers.removePacketListeners(plugin);
    }

    @Override
    public void removePacketMonitor(PacketMonitor monitor) {
        this.handlers.removePacketMonitor(monitor);
    }

    @Override
    public void removePacketListener(PacketListener listener) {
        this.handlers.removePacketListener(listener);
    }

    @Override
    public void addPacketMonitor(Plugin plugin, PacketMonitor monitor, PacketType[] types) {
        this.handlers.addPacketMonitor(plugin, monitor, types);
    }

    @Override
    public void addPacketListener(Plugin plugin, PacketListener listener, PacketType[] types) {
        this.handlers.addPacketListener(plugin, listener, types);
    }

    @Override
    public void receivePacket(Player player, PacketType type, Object packet) {
        if (!CommonUtil.isMainThread()) {
            CommonUtil.nextTick(() -> this.receivePacket(player, type, packet));
            return;
        }
        type.preprocess(packet);
        SafeMethod<?> method = this.receiverMethods.get(packet);
        if (method == null) {
            Logging.LOGGER_NETWORK.log(Level.WARNING, "Could not find suitable packet handler for " + packet.getClass().getSimpleName());
            return;
        }
        ServerGamePacketListenerImplHandle connection = ServerGamePacketListenerImplHandle.forPlayer(player);
        if (connection == null) {
            return;
        }
        PacketHandlerRegistration.HandlerResult result = this.handlePacketReceive(player, packet, false);
        if (result.isCancelled) {
            return;
        }
        if (result.packetType != type && (method = this.receiverMethods.get(result.packet)) == null) {
            Logging.LOGGER_NETWORK.log(Level.WARNING, "Could not find suitable packet handler for listener-changed " + result.packet.getClass().getSimpleName());
            return;
        }
        method.invoke(connection.getRaw(), result.packet);
    }

    @Override
    public void sendPacket(Player player, PacketType type, Object packet, boolean throughListeners) {
        type.preprocess(packet);
        ServerGamePacketListenerImplHandle connection = ServerGamePacketListenerImplHandle.forPlayer(player);
        if (connection == null) {
            return;
        }
        if (!throughListeners) {
            this.silentQueue.add(player, packet);
        }
        try {
            connection.sendPacket(packet);
        }
        catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Encountered an error during sendPacket()", t);
            try {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Packet: " + packet);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    @Override
    public void queuePacket(Player player, PacketType type, Object packet, boolean throughListeners) {
        type.preprocess(packet);
        ServerGamePacketListenerImplHandle connection = ServerGamePacketListenerImplHandle.forPlayer(player);
        if (connection == null) {
            return;
        }
        if (!throughListeners) {
            this.silentQueue.add(player, packet);
        }
        connection.queuePacket(packet);
    }

    @Override
    public Collection<Plugin> getListening(PacketType type) {
        return this.handlers.getListeningPlugins(type);
    }

    @Override
    public void transfer(PacketHandler to) {
        this.handlers.forAllListeners((p, listener) -> to.addPacketListener((Plugin)p, (PacketListener)listener, this.getListenerTypes((PacketListener)listener)));
        this.handlers.forAllMonitors((p, monitor) -> to.addPacketMonitor((Plugin)p, (PacketMonitor)monitor, this.getMonitorTypes((PacketMonitor)monitor)));
    }

    private PacketType[] getListenerTypes(PacketListener listener) {
        ArrayList<PacketType> list = new ArrayList<PacketType>();
        for (Map.Entry<PacketType, List<PacketListener>> entry : this.handlers.listeners.entrySet()) {
            if (!entry.getValue().contains(listener)) continue;
            list.add(entry.getKey());
        }
        return list.toArray(new PacketType[list.size()]);
    }

    private PacketType[] getMonitorTypes(PacketMonitor listener) {
        ArrayList<PacketType> list = new ArrayList<PacketType>();
        for (Map.Entry<PacketType, List<PacketMonitor>> entry : this.handlers.monitors.entrySet()) {
            if (!entry.getValue().contains(listener)) continue;
            list.add(entry.getKey());
        }
        return list.toArray(new PacketType[list.size()]);
    }

    public PacketHandlerRegistration.HandlerResult handlePacketSend(Player player, Object packet, boolean wasCancelled) {
        if (player == null || !PacketHandle.T.isAssignableFrom(packet)) {
            return new PacketHandlerRegistration.HandlerResult(packet, null, wasCancelled);
        }
        boolean is_silent = this.silentQueue.take(player, packet);
        PacketType type = PacketType.getType(packet);
        return this.handlers.handlePacketSend(player, type, packet, is_silent, wasCancelled);
    }

    public PacketHandlerRegistration.HandlerResult handlePacketReceive(Player player, Object packet, boolean wasCancelled) {
        return this.handlers.handlePacketReceive(player, packet, wasCancelled);
    }
}

