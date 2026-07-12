/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.regionflagtracker;

import com.bergerkiller.bukkit.common.regionflagtracker.RegionFlag;
import com.bergerkiller.bukkit.common.regionflagtracker.RegionFlagTracker;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class RegionFlagRegistry {
    protected final List<RegisteredRegionFlag<?>> registeredFlags = new ArrayList();
    protected final Map<PlayerFlagKey, RegionFlagTracker<?>> trackers = new HashMap();
    private static final RegionFlagRegistry instance = RegionFlagRegistry.initRegistryInstance();

    public static RegionFlagRegistry instance() {
        return instance;
    }

    public final synchronized void register(Plugin plugin, RegionFlag<?> flag) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin is null");
        }
        if (flag == null) {
            throw new IllegalArgumentException("RegionFlag is null");
        }
        if (plugin.isEnabled()) {
            throw new IllegalStateException("Region flags can only be registered inside onLoad()");
        }
        for (RegisteredRegionFlag<?> registeredFlag : this.registeredFlags) {
            if (!registeredFlag.flag.name().equals(flag.name())) continue;
            throw new IllegalStateException("Flag is already registered by plugin " + plugin.getName() + ": " + flag);
        }
        RegisteredRegionFlag<?> registeredRegionFlag = this.createNewFlag(plugin, flag);
        this.registeredFlags.add(registeredRegionFlag);
        this.onFlagRegistered(registeredRegionFlag);
    }

    public synchronized <T> RegionFlagTracker<T> track(Player player, RegionFlag<T> flag) {
        if (RegionFlagRegistry.hasPlayerQuit(player)) {
            RegionFlagTracker<T> existing = this.trackers.get(new PlayerFlagKey(player, flag));
            return existing != null ? existing : new RegionFlagTracker<T>(this.getFlagOwnerVerify(flag), player, flag);
        }
        return this.trackers.computeIfAbsent(new PlayerFlagKey(player, flag), k -> new RegionFlagTracker(this.getFlagOwnerVerify(((PlayerFlagKey)k).flag), ((PlayerFlagKey)k).player, ((PlayerFlagKey)k).flag));
    }

    private Plugin getFlagOwnerVerify(RegionFlag<?> flag) {
        for (RegisteredRegionFlag<?> registeredFlag : this.registeredFlags) {
            if (registeredFlag.flag != flag) continue;
            return registeredFlag.plugin;
        }
        throw new IllegalArgumentException("Flag " + flag + " was not registered");
    }

    protected <T> RegisteredRegionFlag<T> createNewFlag(Plugin plugin, RegionFlag<T> flag) {
        return new RegisteredRegionFlag<T>(plugin, flag);
    }

    protected abstract void onFlagRegistered(RegisteredRegionFlag<?> var1);

    private static RegionFlagRegistry initRegistryInstance() {
        try {
            Class<?> initializerHelper = Class.forName(RegionFlagRegistry.class.getName() + "Initializer");
            Method m = initializerHelper.getDeclaredMethod("initialize", new Class[0]);
            m.setAccessible(true);
            return (RegionFlagRegistry)m.invoke(null, new Object[0]);
        }
        catch (Throwable t) {
            throw new UnsupportedOperationException("RegionFlagRegistry API could not be initialized", t);
        }
    }

    static boolean hasPlayerQuit(Player player) {
        return !player.isValid() && Bukkit.getPlayer((UUID)player.getUniqueId()) != player;
    }

    protected static class RegisteredRegionFlag<T> {
        public final Plugin plugin;
        public final RegionFlag<T> flag;

        public RegisteredRegionFlag(Plugin plugin, RegionFlag<T> flag) {
            this.plugin = plugin;
            this.flag = flag;
        }

        public void registerHandler() {
        }

        public void unregisterHandler() {
        }
    }

    protected static final class PlayerFlagKey {
        private final Player player;
        private final RegionFlag<?> flag;

        public PlayerFlagKey(Player player, RegionFlag<?> flag) {
            this.player = player;
            this.flag = flag;
        }

        public int hashCode() {
            return this.player.hashCode() + 31 * this.flag.hashCode();
        }

        public boolean equals(Object o) {
            if (o instanceof PlayerFlagKey) {
                PlayerFlagKey other = (PlayerFlagKey)o;
                return this.player == other.player && this.flag == other.flag;
            }
            return false;
        }

        public String toString() {
            return "{player=" + this.player.getName() + ", flag=" + this.flag + "}";
        }
    }
}

