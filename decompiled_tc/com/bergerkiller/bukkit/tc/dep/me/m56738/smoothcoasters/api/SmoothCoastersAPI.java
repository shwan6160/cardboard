/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api;

import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.DefaultNetworkInterface;
import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.Feature;
import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.NetworkInterface;
import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.PlayerEntry;
import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.PlayerListener;
import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.implementation.ImplV4;
import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.implementation.ImplV5;
import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.implementation.ImplV6;
import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.implementation.Implementation;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SmoothCoastersAPI {
    private final Plugin plugin;
    private final PlayerListener playerListener;
    private final Map<Byte, Implementation> implementations = new TreeMap<Byte, Implementation>();
    private final Map<UUID, PlayerEntry> players = new ConcurrentHashMap<UUID, PlayerEntry>();
    private final NetworkInterface defaultNetwork;

    public SmoothCoastersAPI(Plugin plugin) {
        this.plugin = plugin;
        this.playerListener = new PlayerListener(this);
        this.defaultNetwork = new DefaultNetworkInterface(plugin);
        this.registerImplementation(new ImplV4(plugin));
        this.registerImplementation(new ImplV5(plugin));
        this.registerImplementation(new ImplV6(plugin));
    }

    public void registerImplementation(Implementation implementation) {
        this.implementations.put(implementation.getVersion(), implementation);
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    Map<Byte, Implementation> getImplementations() {
        return this.implementations;
    }

    PlayerEntry getEntry(Player player) {
        return this.players.get(player.getUniqueId());
    }

    PlayerEntry getOrCreateEntry(Player player) {
        return this.players.computeIfAbsent(player.getUniqueId(), u -> new PlayerEntry());
    }

    void removeEntry(Player player) {
        this.players.remove(player.getUniqueId());
    }

    private Implementation getImplementation(Player player) {
        PlayerEntry entry = this.getEntry(player);
        if (entry == null) {
            return null;
        }
        return entry.getImplementation();
    }

    public boolean isEnabled(Player player) {
        return this.getImplementation(player) != null;
    }

    public byte getVersion(Player player) {
        Implementation implementation = this.getImplementation(player);
        if (implementation != null) {
            return implementation.getVersion();
        }
        return -1;
    }

    public String getModVersion(Player player) {
        PlayerEntry entry = this.getEntry(player);
        if (entry != null) {
            return entry.getVersion();
        }
        return null;
    }

    public boolean isSupported(Player player, Feature feature) {
        Implementation implementation = this.getImplementation(player);
        if (implementation == null) {
            return false;
        }
        return implementation.isSupported(feature);
    }

    public boolean resetRotation(NetworkInterface network, Player player) {
        return this.setRotation(network, player, 0.0f, 0.0f, 0.0f, 1.0f, (byte)0);
    }

    public boolean setRotation(NetworkInterface network, Player player, float x, float y, float z, float w, byte ticks) {
        Implementation implementation;
        if (network == null) {
            network = this.defaultNetwork;
        }
        if ((implementation = this.getImplementation(player)) == null || !implementation.isSupported(Feature.ROTATION)) {
            return false;
        }
        implementation.sendRotation(network, player, x, y, z, w, ticks);
        return true;
    }

    @Deprecated
    public boolean setEntityRotation(NetworkInterface network, Player player, int entity, float x, float y, float z, float w, byte ticks) {
        Implementation implementation;
        if (network == null) {
            network = this.defaultNetwork;
        }
        if ((implementation = this.getImplementation(player)) == null || !implementation.isSupported(Feature.ENTITY_ROTATION)) {
            return false;
        }
        implementation.sendEntityRotation(network, player, entity, x, y, z, w, ticks);
        return true;
    }

    @Deprecated
    public boolean setEntityLerpTicks(NetworkInterface network, Player player, int entity, byte ticks) {
        Implementation implementation;
        if (network == null) {
            network = this.defaultNetwork;
        }
        if ((implementation = this.getImplementation(player)) == null || !implementation.isSupported(Feature.ENTITY_PROPERTIES)) {
            return false;
        }
        implementation.sendEntityProperties(network, player, entity, ticks);
        return true;
    }

    public boolean setRotationLimit(NetworkInterface network, Player player, float minYaw, float maxYaw, float minPitch, float maxPitch) {
        Implementation implementation;
        if (network == null) {
            network = this.defaultNetwork;
        }
        if ((implementation = this.getImplementation(player)) == null || !implementation.isSupported(Feature.ROTATION_LIMIT)) {
            return false;
        }
        implementation.sendRotationLimit(network, player, minYaw, maxYaw, minPitch, maxPitch);
        return true;
    }

    public boolean resetRotationLimit(NetworkInterface network, Player player) {
        return this.setRotationLimit(network, player, -180.0f, 180.0f, -90.0f, 90.0f);
    }

    public void unregister() {
        this.implementations.clear();
        this.playerListener.unregister();
        this.players.clear();
    }
}

