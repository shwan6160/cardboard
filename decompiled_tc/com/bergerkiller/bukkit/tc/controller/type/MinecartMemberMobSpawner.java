/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartMobSpawner
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  com.bergerkiller.bukkit.common.wrappers.MobSpawner
 */
package com.bergerkiller.bukkit.tc.controller.type;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecartMobSpawner;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.wrappers.MobSpawner;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.exception.GroupUnloadedException;
import com.bergerkiller.bukkit.tc.exception.MemberMissingException;

public class MinecartMemberMobSpawner
extends MinecartMember<CommonMinecartMobSpawner> {
    public MinecartMemberMobSpawner(TrainCarts plugin) {
        super(plugin);
    }

    @Override
    public void onPhysicsPostMove() throws MemberMissingException, GroupUnloadedException {
        super.onPhysicsPostMove();
        ((CommonMinecartMobSpawner)this.getEntity()).getMobSpawner().performTickUpdate(((CommonMinecartMobSpawner)this.entity).loc.toBlock());
    }

    public MobSpawner getSpawner() {
        return ((CommonMinecartMobSpawner)this.getEntity()).getMobSpawner();
    }

    public boolean parseAndSet(String name, String input) {
        if (LogicUtil.contains((Object)name, (Object[])new String[]{"mobtype"})) {
            if (Util.isValidEntity(input)) {
                this.getSpawner().setMobName(input);
            }
        } else if (LogicUtil.contains((Object)name, (Object[])new String[]{"delay", "minspawndelay"})) {
            this.getSpawner().setSpawnDelay(ParseUtil.parseInt((String)input, (int)this.getSpawner().getSpawnDelay()));
        } else if (LogicUtil.contains((Object)name, (Object[])new String[]{"mindelay", "minspawndelay"})) {
            this.getSpawner().setMinSpawnDelay(ParseUtil.parseInt((String)input, (int)this.getSpawner().getMinSpawnDelay()));
        } else if (LogicUtil.contains((Object)name, (Object[])new String[]{"maxdelay", "maxspawndelay"})) {
            this.getSpawner().setMaxSpawnDelay(ParseUtil.parseInt((String)input, (int)this.getSpawner().getMaxSpawnDelay()));
        } else {
            return false;
        }
        return true;
    }
}

