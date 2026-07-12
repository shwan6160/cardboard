/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.generated.net.minecraft.world.level.BaseSpawnerHandle;
import org.bukkit.block.Block;

public class MobSpawner
extends BasicWrapper<BaseSpawnerHandle> {
    public MobSpawner(Object mobSpawnerHandle) {
        this.setHandle(BaseSpawnerHandle.createHandle(mobSpawnerHandle));
    }

    public String getMobName() {
        return ((BaseSpawnerHandle)this.handle).getMobName().toString();
    }

    public void setMobName(String name) {
        ((BaseSpawnerHandle)this.handle).setMobName(IdentifierHandle.createNew(name));
    }

    public void performTickUpdate(Block position) {
        ((BaseSpawnerHandle)this.handle).onTick(position.getWorld(), new IntVector3(position));
    }

    public int getSpawnDelay() {
        return ((BaseSpawnerHandle)this.handle).getSpawnDelay();
    }

    public void setSpawnDelay(int tickDelay) {
        ((BaseSpawnerHandle)this.handle).setSpawnDelay(tickDelay);
    }

    public int getMinSpawnDelay() {
        return ((BaseSpawnerHandle)this.handle).getMinSpawnDelay();
    }

    public void setMinSpawnDelay(int tickInterval) {
        ((BaseSpawnerHandle)this.handle).setMinSpawnDelay(tickInterval);
    }

    public int getMaxSpawnDelay() {
        return ((BaseSpawnerHandle)this.handle).getMaxSpawnDelay();
    }

    public void setMaxSpawnDelay(int tickInterval) {
        ((BaseSpawnerHandle)this.handle).setMaxSpawnDelay(tickInterval);
    }

    public int getSpawnCount() {
        return ((BaseSpawnerHandle)this.handle).getSpawnCount();
    }

    public void setSpawnCount(int mobCount) {
        ((BaseSpawnerHandle)this.handle).setSpawnCount(mobCount);
    }
}

