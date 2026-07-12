/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.controller;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface VehicleMountController {
    public Player getPlayer();

    public boolean mount(int var1, int var2);

    public void unmount(int var1, int var2);

    public void remove(int var1);

    public void clear(int var1);

    public int getVehicle(int var1);

    public int[] getPassengers(int var1);

    public void despawn(int var1);

    public void respawn(int var1, RespawnFunctionWithEntityId var2);

    public void startSpectating(int var1);

    public void stopSpectating(int var1);

    public void swapSpectating(int var1, int var2);

    public boolean isSpectating(int var1);

    public <T extends Entity> void respawn(T var1, RespawnFunctionWithEntity<T> var2);

    public void respawn(int var1, Runnable var2);

    public static interface RespawnFunctionWithEntity<T extends Entity> {
        public void respawn(Player var1, T var2);
    }

    public static interface RespawnFunctionWithEntityId {
        public void respawn(Player var1, int var2);
    }
}

