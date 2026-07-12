/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.tc.actions;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.actions.Action;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import org.bukkit.World;

public class MemberAction
extends Action {
    private MinecartMember<?> member;

    @Override
    public TrainCarts getTrainCarts() {
        return this.member.getTrainCarts();
    }

    @Override
    public boolean doTick() {
        return this.getEntity().isRemoved() || super.doTick();
    }

    @Override
    public MinecartGroup getGroup() {
        return this.member.getGroup();
    }

    public MinecartMember<?> getMember() {
        return this.member;
    }

    public void setMember(MinecartMember<?> member) {
        this.member = member;
    }

    public CommonMinecart<?> getEntity() {
        return (CommonMinecart)this.member.getEntity();
    }

    public World getWorld() {
        return this.getEntity().getWorld();
    }
}

