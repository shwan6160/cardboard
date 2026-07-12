/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.logic.RegionHandler_Vanilla_1_14;
import com.bergerkiller.generated.org.bukkit.WorldHandle;
import org.bukkit.World;

class RegionHandler_Vanilla_1_17
extends RegionHandler_Vanilla_1_14 {
    RegionHandler_Vanilla_1_17() {
    }

    @Override
    public int getMinHeight(World world) {
        WorldHandle w = WorldHandle.createHandle(world);
        return w.getMinHeight();
    }
}

