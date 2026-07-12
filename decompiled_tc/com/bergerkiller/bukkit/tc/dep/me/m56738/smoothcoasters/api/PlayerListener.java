/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerRegisterChannelEvent
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.messaging.PluginMessageListener
 */
package com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api;

import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.PlayerEntry;
import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.SmoothCoastersAPI;
import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.Util;
import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.event.PlayerSmoothCoastersHandshakeEvent;
import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.implementation.Implementation;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PlayerListener
implements Listener,
PluginMessageListener {
    private static final String CHANNEL = "smoothcoasters:hs";
    private static final String SUPPORTED_VERSIONS = "smoothcoasters_supported_versions";
    private static final String OFFERED_VERSIONS = "smoothcoasters_offered_versions";
    private final SmoothCoastersAPI api;

    public PlayerListener(SmoothCoastersAPI api) {
        this.api = api;
        Bukkit.getPluginManager().registerEvents((Listener)this, api.getPlugin());
        Bukkit.getMessenger().registerIncomingPluginChannel(api.getPlugin(), CHANNEL, (PluginMessageListener)this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(api.getPlugin(), CHANNEL);
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerRegisterChannelStart(PlayerRegisterChannelEvent event) {
        if (!event.getChannel().equals(CHANNEL)) {
            return;
        }
        event.getPlayer().setMetadata(SUPPORTED_VERSIONS, (MetadataValue)new FixedMetadataValue(this.api.getPlugin(), this.api.getImplementations().keySet()));
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerRegisterChannel(PlayerRegisterChannelEvent event) {
        Set set;
        if (!event.getChannel().equals(CHANNEL)) {
            return;
        }
        if (event.getPlayer().hasMetadata(OFFERED_VERSIONS)) {
            return;
        }
        TreeSet versions = null;
        for (MetadataValue value : event.getPlayer().getMetadata(SUPPORTED_VERSIONS)) {
            set = (Set)value.value();
            if (versions == null) {
                versions = new TreeSet(set);
                continue;
            }
            versions.retainAll(set);
        }
        event.getPlayer().setMetadata(OFFERED_VERSIONS, (MetadataValue)new FixedMetadataValue(this.api.getPlugin(), versions));
        if (versions == null || versions.isEmpty()) {
            this.api.getPlugin().getLogger().warning("[SmoothCoastersAPI] Plugins have no supported SmoothCoasters versions in common");
            for (MetadataValue value : event.getPlayer().getMetadata(SUPPORTED_VERSIONS)) {
                set = (Set)value.value();
                this.api.getPlugin().getLogger().warning("[SmoothCoastersAPI] " + value.getOwningPlugin().getName() + ": " + set.stream().map(String::valueOf).collect(Collectors.joining(", ")));
            }
            return;
        }
        byte[] message = new byte[versions.size() + 1];
        message[0] = (byte)versions.size();
        int i = 1;
        for (Byte version : versions) {
            message[i++] = version;
        }
        event.getPlayer().sendPluginMessage(this.api.getPlugin(), CHANNEL, message);
    }

    public void onPluginMessageReceived(String channel, Player player, byte[] payload) {
        String version;
        Implementation implementation;
        if (!channel.equals(CHANNEL) || payload.length < 1) {
            return;
        }
        ByteBuffer buffer = ByteBuffer.wrap(payload);
        try {
            implementation = this.api.getImplementations().get(buffer.get());
            version = buffer.hasRemaining() ? Util.readString(buffer, 32) : null;
        }
        catch (Exception e) {
            this.api.getPlugin().getLogger().log(Level.SEVERE, "[SmoothCoastersAPI] Received invalid handshake from " + player.getName(), e);
            return;
        }
        PlayerEntry entry = this.api.getOrCreateEntry(player);
        entry.setImplementation(implementation);
        entry.setVersion(version);
        if (implementation != null) {
            Bukkit.getPluginManager().callEvent((Event)new PlayerSmoothCoastersHandshakeEvent(player, implementation, version));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.removeMetadata(SUPPORTED_VERSIONS, this.api.getPlugin());
        player.removeMetadata(OFFERED_VERSIONS, this.api.getPlugin());
        this.api.removeEntry(player);
    }

    public void unregister() {
        HandlerList.unregisterAll((Listener)this);
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this.api.getPlugin(), CHANNEL, (PluginMessageListener)this);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this.api.getPlugin(), CHANNEL);
    }
}

