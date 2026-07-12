/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.protocol;

import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;

public interface PacketListener {
    public void onPacketReceive(PacketReceiveEvent var1);

    public void onPacketSend(PacketSendEvent var1);
}

