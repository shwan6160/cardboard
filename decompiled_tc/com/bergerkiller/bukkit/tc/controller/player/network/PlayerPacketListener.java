/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.component.LibraryComponent
 *  com.bergerkiller.bukkit.common.protocol.PacketListener
 *  com.bergerkiller.bukkit.common.protocol.PacketType
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.controller.player.network;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.player.network.PlayerPacketListenerProviderImpl;
import org.bukkit.entity.Player;

public interface PlayerPacketListener<L extends PacketListener> {
    public static <L extends PacketListener> PlayerPacketListener<L> createNoOp(final Player player, final L packetListener) {
        return new PlayerPacketListener<L>(){

            @Override
            public Player getPlayer() {
                return player;
            }

            @Override
            public L getListener() {
                return packetListener;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public PlayerPacketListener<L> enable() {
                return this;
            }

            @Override
            public PlayerPacketListener<L> disable() {
                return this;
            }

            @Override
            public void terminate() {
            }
        };
    }

    public Player getPlayer();

    public L getListener();

    public boolean isEnabled();

    public PlayerPacketListener<L> enable();

    public PlayerPacketListener<L> disable();

    public void terminate();

    public static interface Provider
    extends LibraryComponent {
        public static Provider create(TrainCarts traincarts) {
            return new PlayerPacketListenerProviderImpl(traincarts);
        }

        public <L extends PacketListener> PlayerPacketListener<L> create(Player var1, L var2, PacketType ... var3);

        public void enable();

        public void disable();
    }
}

