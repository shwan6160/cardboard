/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.tc.utils;

import java.util.HashSet;
import java.util.IdentityHashMap;
import org.bukkit.World;

public class ConfiguredWorldSet {
    private final HashSet<String> lowercaseNames = new HashSet();
    private final IdentityHashMap<World, Boolean> byWorld = new IdentityHashMap();
    private World lastContainsWorld = null;
    private boolean lastContainsResult = false;

    public void onWorldUnloaded(World world) {
        this.byWorld.remove(world);
        this.lastContainsWorld = null;
    }

    public void clear() {
        this.lowercaseNames.clear();
        this.byWorld.clear();
        this.lastContainsWorld = null;
    }

    public void add(String name) {
        this.lowercaseNames.add(name.toLowerCase());
        this.byWorld.clear();
        this.lastContainsWorld = null;
    }

    public boolean contains(String worldName) {
        return this.lowercaseNames.contains(worldName.toLowerCase());
    }

    public boolean contains(World world) {
        if (world == this.lastContainsWorld) {
            return this.lastContainsResult;
        }
        boolean result = this.byWorld.computeIfAbsent(world, w -> this.lowercaseNames.contains(w.getName().toLowerCase()));
        this.lastContainsWorld = world;
        this.lastContainsResult = result;
        return result;
    }

    public boolean isEmpty() {
        return this.lowercaseNames.isEmpty();
    }
}

