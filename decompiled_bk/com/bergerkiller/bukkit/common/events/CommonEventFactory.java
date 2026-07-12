/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.EntityType
 *  org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason
 */
package com.bergerkiller.bukkit.common.events;

import com.bergerkiller.bukkit.common.collections.SortedIdentityCache;
import com.bergerkiller.bukkit.common.events.CreaturePreSpawnEvent;
import com.bergerkiller.bukkit.common.events.EntityMoveEvent;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CommonEventFactory {
    private final EntityMoveEvent entityMoveEvent = new EntityMoveEvent();
    private final SortedIdentityCache<Object, EntityHandle> entityMoveEntities = SortedIdentityCache.create(EntityHandle::createHandle);
    private final CreaturePreSpawnEvent creaturePreSpawnEvent = new CreaturePreSpawnEvent();

    public void handleEntityMove() {
        if (!CommonUtil.hasHandlers(EntityMoveEvent.getHandlerList())) {
            this.entityMoveEntities.clear();
            return;
        }
        this.entityMoveEntities.sync(consumer -> {
            for (World world : WorldUtil.getWorlds()) {
                Object worldHandle = ServerLevelHandle.fromBukkit(world).getRaw();
                Iterable worldEntities = (Iterable)((Template.Method)ServerLevelHandle.T.getEntities.raw).invoke(worldHandle);
                worldEntities.forEach(consumer);
            }
        });
        this.entityMoveEntities.forEach(entity -> {
            if (entity.isLastAndCurrentPositionDifferent()) {
                this.entityMoveEvent.setEntity((EntityHandle)entity);
                CommonUtil.callEvent(this.entityMoveEvent);
            }
        });
    }

    public boolean handleCreaturePreSpawn(World world, int x, int y, int z, EntityType entityType, CreatureSpawnEvent.SpawnReason reason) {
        this.creaturePreSpawnEvent.cancelled = false;
        this.creaturePreSpawnEvent.spawnLocation.setWorld(world);
        this.creaturePreSpawnEvent.spawnLocation.setX((double)x);
        this.creaturePreSpawnEvent.spawnLocation.setY((double)y);
        this.creaturePreSpawnEvent.spawnLocation.setZ((double)z);
        this.creaturePreSpawnEvent.spawnLocation.setYaw(0.0f);
        this.creaturePreSpawnEvent.spawnLocation.setPitch(0.0f);
        this.creaturePreSpawnEvent.entityType = entityType;
        this.creaturePreSpawnEvent.reason = reason;
        return !CommonUtil.callEvent(this.creaturePreSpawnEvent).isCancelled();
    }

    public boolean handleCreaturePreSpawn(Location at, EntityType entityType, CreatureSpawnEvent.SpawnReason reason) {
        this.creaturePreSpawnEvent.cancelled = false;
        this.creaturePreSpawnEvent.spawnLocation.setWorld(at.getWorld());
        this.creaturePreSpawnEvent.spawnLocation.setX(at.getX());
        this.creaturePreSpawnEvent.spawnLocation.setY(at.getY());
        this.creaturePreSpawnEvent.spawnLocation.setZ(at.getZ());
        this.creaturePreSpawnEvent.spawnLocation.setYaw(at.getYaw());
        this.creaturePreSpawnEvent.spawnLocation.setPitch(at.getPitch());
        this.creaturePreSpawnEvent.entityType = entityType;
        this.creaturePreSpawnEvent.reason = reason;
        return !CommonUtil.callEvent(this.creaturePreSpawnEvent).isCancelled();
    }
}

