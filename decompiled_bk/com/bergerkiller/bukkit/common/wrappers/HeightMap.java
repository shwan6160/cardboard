/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.net.minecraft.world.level.levelgen.HeightmapHandle;

public class HeightMap
extends BasicWrapper<HeightmapHandle> {
    public HeightMap(HeightmapHandle handle) {
        this.setHandle(handle);
    }

    public void initialize() {
        ((HeightmapHandle)this.handle).initialize();
    }

    public int getHeight(int x, int z) {
        return ((HeightmapHandle)this.handle).getHeight(x, z) - 1;
    }

    public void setHeight(int x, int z, int height) {
        ((HeightmapHandle)this.handle).setHeight(x, z, height);
    }
}

