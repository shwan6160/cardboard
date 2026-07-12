/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 */
package com.bergerkiller.bukkit.common.events;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public abstract class PacketEvent
implements Cancellable {
    private boolean cancelled = false;
    private Player player;
    private CommonPacket packet;

    public PacketEvent(Player player, CommonPacket packet) {
        this.player = player;
        this.packet = packet;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    public PacketType getType() {
        return this.packet.getType();
    }

    public Player getPlayer() {
        return this.player;
    }

    public CommonPacket getPacket() {
        return this.packet;
    }

    public void setPacket(CommonPacket packet) {
        if (packet == null) {
            throw new IllegalArgumentException("Cannot set null packet");
        }
        this.packet = packet;
    }

    public void setPacket(PacketHandle packet) {
        if (packet == null) {
            throw new IllegalArgumentException("Cannot set null packet");
        }
        this.packet = packet.toCommonPacket();
    }
}

