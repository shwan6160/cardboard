/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonPlayerMeta;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import java.util.logging.Level;
import org.bukkit.entity.Player;

@Deprecated
class CommonPacketMonitor
implements PacketMonitor {
    public static final PacketType[] TYPES = new PacketType[]{PacketType.OUT_MAP_CHUNK, PacketType.OUT_UNLOAD_CHUNK, PacketType.OUT_RESPAWN};
    private boolean listenError = false;

    CommonPacketMonitor() {
    }

    @Override
    public void onMonitorPacketReceive(CommonPacket packet, Player player) {
    }

    @Override
    public void onMonitorPacketSend(CommonPacket packet, Player player) {
        CommonPlayerMeta meta = CommonPlugin.getInstance().getPlayerMeta(player);
        if (packet.getType() == PacketType.OUT_MAP_CHUNK) {
            int chunkX = packet.read(PacketType.OUT_MAP_CHUNK.x);
            int chunkZ = packet.read(PacketType.OUT_MAP_CHUNK.z);
            meta.setChunkVisible(chunkX, chunkZ, true);
        } else if (packet.getType() == PacketType.OUT_UNLOAD_CHUNK) {
            int chunkX = packet.read(PacketType.OUT_UNLOAD_CHUNK.x);
            int chunkZ = packet.read(PacketType.OUT_UNLOAD_CHUNK.z);
            meta.setChunkVisible(chunkX, chunkZ, false);
        } else if (packet.getType() == PacketType.OUT_RESPAWN) {
            meta.clearVisibleChunks();
        } else if (!this.listenError) {
            this.listenError = true;
            Logging.LOGGER_NETWORK.log(Level.WARNING, "Packet entered listener that the listener was not registered for: type=" + packet.getType());
        }
    }
}

