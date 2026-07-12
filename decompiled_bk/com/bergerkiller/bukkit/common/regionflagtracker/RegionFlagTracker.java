/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.regionflagtracker;

import com.bergerkiller.bukkit.common.regionflagtracker.RegionFlag;
import com.bergerkiller.bukkit.common.regionflagtracker.RegionFlagRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class RegionFlagTracker<T> {
    private final Plugin plugin;
    private final Player player;
    private final RegionFlag<T> flag;
    private List<ChangeListener<T>> listeners = Collections.emptyList();
    private T value = null;

    public static <T> RegionFlagTracker<T> track(Player player, RegionFlag<T> flag) {
        return RegionFlagRegistry.instance().track(player, flag);
    }

    RegionFlagTracker(Plugin plugin, Player player, RegionFlag<T> flag) {
        this.plugin = plugin;
        this.player = player;
        this.flag = flag;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public Player getPlayer() {
        return this.player;
    }

    public RegionFlag<T> getFlag() {
        return this.flag;
    }

    public Optional<T> getValue() {
        return Optional.ofNullable(this.value);
    }

    public synchronized void addListener(ChangeListener<T> listener) {
        ArrayList<ChangeListener<T>> newListeners = new ArrayList<ChangeListener<T>>(this.listeners);
        newListeners.add(listener);
        this.listeners = newListeners;
    }

    void updateValue(T value) {
        if (!Objects.equals(this.value, value)) {
            this.value = value;
            this.listeners.forEach(l -> l.onValueChanged(this));
        }
    }

    @FunctionalInterface
    public static interface ChangeListener<T> {
        public void onValueChanged(RegionFlagTracker<T> var1);
    }
}

