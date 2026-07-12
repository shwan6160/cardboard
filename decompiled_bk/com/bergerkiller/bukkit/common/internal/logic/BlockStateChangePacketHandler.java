/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.internal.logic.BlockStateChangePacketHandler_1_8_to_1_9_2;
import com.bergerkiller.bukkit.common.internal.logic.BlockStateChangePacketHandler_1_9_3;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketBlockStateChangeListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import java.util.IdentityHashMap;
import java.util.Map;
import org.bukkit.entity.Player;

public abstract class BlockStateChangePacketHandler
implements LibraryComponent {
    public static final BlockStateChangePacketHandler INSTANCE = LibraryComponentSelector.forModule(BlockStateChangePacketHandler.class).addVersionOption(null, "1.9.2", BlockStateChangePacketHandler_1_8_to_1_9_2::new).addVersionOption("1.9.3", null, BlockStateChangePacketHandler_1_9_3::new).update();
    private final Handler _noopHandler = (player, packet, listener) -> true;
    private final Map<PacketType, Handler> _handlers = new IdentityHashMap<PacketType, Handler>();

    @Override
    public void disable() {
    }

    public final PacketType[] listenedTypes() {
        return (PacketType[])this._handlers.keySet().stream().toArray(PacketType[]::new);
    }

    public final boolean process(Player player, CommonPacket packet, PacketBlockStateChangeListener listener) {
        return this._handlers.getOrDefault(packet.getType(), this._noopHandler).handle(player, packet, listener);
    }

    protected void register(PacketType packetType, Handler handler) {
        this._handlers.put(packetType, handler);
    }

    @FunctionalInterface
    protected static interface Handler {
        public boolean handle(Player var1, CommonPacket var2, PacketBlockStateChangeListener var3);
    }
}

