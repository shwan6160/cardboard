/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.protocol;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import org.bukkit.entity.Player;

public interface PacketMonitor {
    public void onMonitorPacketSend(CommonPacket var1, Player var2);

    public void onMonitorPacketReceive(CommonPacket var1, Player var2);
}

