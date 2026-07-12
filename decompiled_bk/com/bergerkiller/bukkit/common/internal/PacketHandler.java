/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import java.util.Collection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface PacketHandler {
    public void removePacketListeners(Plugin var1);

    public void removePacketListener(PacketListener var1);

    public void removePacketMonitor(PacketMonitor var1);

    public void addPacketListener(Plugin var1, PacketListener var2, PacketType[] var3);

    public void addPacketMonitor(Plugin var1, PacketMonitor var2, PacketType[] var3);

    public void sendPacket(Player var1, PacketType var2, Object var3, boolean var4);

    public void queuePacket(Player var1, PacketType var2, Object var3, boolean var4);

    public void receivePacket(Player var1, PacketType var2, Object var3);

    public Collection<Plugin> getListening(PacketType var1);

    public void transfer(PacketHandler var1);

    public String getName();

    public boolean onEnable();

    public boolean onDisable();

    public void onPlayerJoin(Player var1);
}

