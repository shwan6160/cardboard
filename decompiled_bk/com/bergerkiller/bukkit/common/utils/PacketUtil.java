/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddPlayerPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import java.util.Collection;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PacketUtil {
    public static void receivePacket(Player player, PacketHandle packet) {
        if (packet != null) {
            CommonPlugin.getInstance().getPacketHandler().receivePacket(player, packet.getPacketType(), packet.getRaw());
        }
    }

    public static void receivePacket(Player player, CommonPacket packet) {
        if (packet != null) {
            CommonPlugin.getInstance().getPacketHandler().receivePacket(player, packet.getType(), packet.getHandle());
        }
    }

    @Deprecated
    public static void receivePacket(Player player, Object packet) {
        if (packet instanceof CommonPacket) {
            packet = ((CommonPacket)packet).getHandle();
        } else if (packet instanceof PacketHandle) {
            packet = ((PacketHandle)packet).getRaw();
        }
        if (packet == null) {
            return;
        }
        CommonPlugin.getInstance().getPacketHandler().receivePacket(player, PacketType.getType(packet), packet);
    }

    public static void sendPacket(Player player, CommonPacket packet) {
        PacketUtil.sendPacket(player, packet, true);
    }

    public static void sendPacket(Player player, CommonPacket packet, boolean throughListeners) {
        Object rawPacket;
        if (packet != null && (rawPacket = packet.getHandle()) != null) {
            CommonPlugin.getInstance().getPacketHandler().sendPacket(player, packet.getType(), rawPacket, throughListeners);
        }
    }

    public static void sendPacket(Player player, PacketHandle packet) {
        PacketUtil.sendPacket(player, packet, true);
    }

    public static void sendPacket(Player player, PacketHandle packet, boolean throughListeners) {
        if (packet != null) {
            CommonPlugin.getInstance().getPacketHandler().sendPacket(player, packet.getPacketType(), packet.getRaw(), throughListeners);
        }
    }

    public static void queuePacket(Player player, CommonPacket packet) {
        PacketUtil.queuePacket(player, packet, true);
    }

    public static void queuePacket(Player player, CommonPacket packet, boolean throughListeners) {
        Object rawPacket;
        if (packet != null && (rawPacket = packet.getHandle()) != null) {
            CommonPlugin.getInstance().getPacketHandler().queuePacket(player, packet.getType(), rawPacket, throughListeners);
        }
    }

    public static void queuePacket(Player player, PacketHandle packet) {
        PacketUtil.queuePacket(player, packet, true);
    }

    public static void queuePacket(Player player, PacketHandle packet, boolean throughListeners) {
        if (packet != null) {
            CommonPlugin.getInstance().getPacketHandler().queuePacket(player, packet.getPacketType(), packet.getRaw(), throughListeners);
        }
    }

    public static void sendEntityLivingSpawnPacket(Player player, ClientboundAddMobPacketHandle packet, DataWatcher metadata) {
        if (packet.hasDataWatcherSupport()) {
            packet.setDataWatcher(metadata);
            PacketUtil.sendPacket(player, packet);
        } else {
            PacketUtil.sendPacket(player, packet);
            PacketUtil.sendPacket(player, ClientboundSetEntityDataPacketHandle.createForSpawn(packet.getEntityId(), metadata));
        }
    }

    public static void sendNamedEntitySpawnPacket(Player player, ClientboundAddPlayerPacketHandle packet, DataWatcher metadata) {
        if (packet.hasDataWatcherSupport()) {
            packet.setDataWatcher(metadata);
            PacketUtil.sendPacket(player, packet);
        } else {
            PacketUtil.sendPacket(player, packet);
            PacketUtil.sendPacket(player, ClientboundSetEntityDataPacketHandle.createNew(packet.getEntityId(), metadata, true));
        }
    }

    @Deprecated
    public static void sendPacket(Player player, Object packet) {
        PacketUtil.sendPacket(player, packet, true);
    }

    @Deprecated
    public static void sendPacket(Player player, Object packet, boolean throughListeners) {
        if (packet instanceof CommonPacket) {
            packet = ((CommonPacket)packet).getHandle();
        } else if (packet instanceof PacketHandle) {
            packet = ((PacketHandle)packet).getRaw();
        }
        if (packet == null) {
            return;
        }
        CommonPlugin.getInstance().getPacketHandler().sendPacket(player, PacketType.getType(packet), packet, throughListeners);
    }

    public static void broadcastBlockPacket(Block block, Object packet, boolean throughListeners) {
        PacketUtil.broadcastBlockPacket(block.getWorld(), block.getX(), block.getZ(), packet, throughListeners);
    }

    public static void broadcastBlockPacket(World world, int x, int z, Object packet, boolean throughListeners) {
        if (packet instanceof CommonPacket) {
            packet = ((CommonPacket)packet).getHandle();
        }
        if (world == null || packet == null) {
            return;
        }
        for (Player player : WorldUtil.getPlayers(world)) {
            if (!EntityUtil.isNearBlock((Entity)player, x, z, CommonUtil.BLOCKVIEW)) continue;
            PacketUtil.sendPacket(player, packet, throughListeners);
        }
    }

    public static void broadcastChunkPacket(Chunk chunk, Object packet, boolean throughListeners) {
        if (packet instanceof CommonPacket) {
            packet = ((CommonPacket)packet).getHandle();
        }
        if (chunk == null || packet == null) {
            return;
        }
        for (Player player : WorldUtil.getPlayers(chunk.getWorld())) {
            if (!EntityUtil.isNearChunk((Entity)player, chunk.getX(), chunk.getZ(), CommonUtil.VIEW)) continue;
            PacketUtil.sendPacket(player, packet, throughListeners);
        }
    }

    public static void broadcastPacket(Object packet, boolean throughListeners) {
        if (packet instanceof CommonPacket) {
            packet = ((CommonPacket)packet).getHandle();
        }
        for (Player player : CommonUtil.getOnlinePlayers()) {
            PacketUtil.sendPacket(player, packet, throughListeners);
        }
    }

    public static void broadcastEntityPacket(Entity entity, CommonPacket packet) {
        PacketUtil.broadcastEntityPacket(entity, packet, true);
    }

    public static void broadcastEntityPacket(Entity entity, CommonPacket packet, boolean sendToSelf) {
        if (entity == null || packet == null) {
            return;
        }
        EntityTracker tracker = WorldUtil.getTracker(entity.getWorld());
        EntityTrackerEntryHandle entry = tracker.getEntry(entity);
        if (entry != null) {
            for (Player viewer : entry.getViewers()) {
                PacketUtil.sendPacket(viewer, packet);
            }
        }
        if (sendToSelf && entity instanceof Player) {
            PacketUtil.sendPacket((Player)entity, packet);
        }
    }

    public static void addPacketMonitor(Plugin plugin, PacketMonitor monitor, PacketType ... packets) {
        if (monitor == null || LogicUtil.nullOrEmpty(packets)) {
            return;
        }
        CommonPlugin.getInstance().getPacketHandler().addPacketMonitor(plugin, monitor, packets);
    }

    public static void addPacketListener(Plugin plugin, PacketListener listener, PacketType ... packets) {
        if (listener == null || LogicUtil.nullOrEmpty(packets)) {
            return;
        }
        CommonPlugin.getInstance().getPacketHandler().addPacketListener(plugin, listener, packets);
    }

    public static void removePacketListeners(Plugin plugin) {
        CommonPlugin.getInstance().getPacketHandler().removePacketListeners(plugin);
    }

    public static void removePacketListener(PacketListener listener) {
        CommonPlugin.getInstance().getPacketHandler().removePacketListener(listener);
    }

    public static void removePacketMonitor(PacketMonitor monitor) {
        CommonPlugin.getInstance().getPacketHandler().removePacketMonitor(monitor);
    }

    public static void broadcastPacketNearby(Location location, double radius, CommonPacket packet) {
        PacketUtil.broadcastPacketNearby(location, radius, packet.getHandle());
    }

    public static void broadcastPacketNearby(Location location, double radius, PacketHandle packet) {
        PacketUtil.broadcastPacketNearby(location, radius, packet.getRaw());
    }

    public static void broadcastPacketNearby(World world, double x, double y, double z, double radius, CommonPacket packet) {
        PacketUtil.broadcastPacketNearby(world, x, y, z, radius, packet.getHandle());
    }

    public static void broadcastPacketNearby(World world, double x, double y, double z, double radius, PacketHandle packet) {
        PacketUtil.broadcastPacketNearby(world, x, y, z, radius, packet.getRaw());
    }

    @Deprecated
    public static void broadcastPacketNearby(Location location, double radius, Object packet) {
        PacketUtil.broadcastPacketNearby(location.getWorld(), location.getX(), location.getY(), location.getZ(), radius, packet);
    }

    @Deprecated
    public static void broadcastPacketNearby(World world, double x, double y, double z, double radius, Object packet) {
        if (packet instanceof CommonPacket) {
            packet = ((CommonPacket)packet).getHandle();
        }
        CommonNMS.getPlayerList().sendRawPacketNearby(world, x, y, z, radius, packet);
    }

    public static Collection<Plugin> getListenerPlugins(PacketType packetType) {
        return CommonPlugin.getInstance().getPacketHandler().getListening(packetType);
    }
}

