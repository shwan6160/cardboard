/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.collections.InterpolatedMap
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartFurnace
 *  com.bergerkiller.bukkit.common.resources.ResourceKey
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.common.collections.InterpolatedMap;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartFurnace;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.tc.controller.components.SoundLoop;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberFurnace;

public class PoweredCartSoundLoop
extends SoundLoop<MinecartMemberFurnace> {
    private static InterpolatedMap nodes = new InterpolatedMap();
    private int swooshSoundCounter = 0;

    public PoweredCartSoundLoop(MinecartMemberFurnace member) {
        super(member);
    }

    @Override
    public void onTick() {
        if (!((CommonMinecartFurnace)((MinecartMemberFurnace)this.member).getEntity()).hasFuel()) {
            return;
        }
        ++this.swooshSoundCounter;
        int interval = (int)nodes.get(((CommonMinecartFurnace)((MinecartMemberFurnace)this.member).getEntity()).getMovedDistance());
        if (this.swooshSoundCounter >= interval) {
            this.swooshSoundCounter = 0;
            this.play((ResourceKey<SoundEffect>)SoundEffect.WALK_CLOTH, 0.6f + 0.2f * this.random.nextFloat(), 0.2f);
            this.play((ResourceKey<SoundEffect>)SoundEffect.EXTINGUISH, 1.5f + 0.3f * this.random.nextFloat(), 0.05f + 0.1f * this.random.nextFloat());
        }
    }

    static {
        nodes.put(0.0, 2.147483647E9);
        nodes.put(0.001, 50.0);
        nodes.put(0.005, 23.0);
        nodes.put(0.01, 18.0);
        nodes.put(0.05, 16.0);
        nodes.put(0.1, 14.0);
        nodes.put(0.2, 8.0);
        nodes.put(0.4, 5.0);
    }
}

