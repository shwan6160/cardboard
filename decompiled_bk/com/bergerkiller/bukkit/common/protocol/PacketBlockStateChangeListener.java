/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.protocol;

import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.logic.BlockStateChangePacketHandler;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.wrappers.BlockStateChange;
import org.bukkit.entity.Player;

public interface PacketBlockStateChangeListener
extends PacketListener {
    public static final PacketType[] LISTENED_TYPES = BlockStateChangePacketHandler.INSTANCE.listenedTypes();

    public boolean onBlockChange(Player var1, BlockStateChange var2);

    @Override
    default public void onPacketReceive(PacketReceiveEvent event) {
    }

    @Override
    default public void onPacketSend(PacketSendEvent event) {
        if (!BlockStateChangePacketHandler.INSTANCE.process(event.getPlayer(), event.getPacket(), this)) {
            event.setCancelled(true);
        }
    }

    public static boolean process(Player player, CommonPacket packet, PacketBlockStateChangeListener listener) {
        return BlockStateChangePacketHandler.INSTANCE.process(player, packet, listener);
    }
}

