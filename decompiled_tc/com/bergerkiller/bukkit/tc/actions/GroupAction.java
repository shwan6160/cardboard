/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.tc.actions;

import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.actions.Action;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import org.bukkit.World;

public class GroupAction
extends Action {
    private MinecartGroup group;

    @Override
    public TrainCarts getTrainCarts() {
        return this.group.getTrainCarts();
    }

    @Override
    public boolean doTick() {
        return this.group.isEmpty() || super.doTick();
    }

    @Override
    public MinecartGroup getGroup() {
        return this.group;
    }

    public void setGroup(MinecartGroup group) {
        this.group = group;
    }

    public World getWorld() {
        return this.group.getWorld();
    }
}

