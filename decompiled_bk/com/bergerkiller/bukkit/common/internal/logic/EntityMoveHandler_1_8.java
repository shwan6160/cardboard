/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.logic.EntityMoveHandler;
import com.bergerkiller.bukkit.common.internal.proxy.VoxelShapeProxy;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.shapes.VoxelShapeHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

class EntityMoveHandler_1_8
extends EntityMoveHandler {
    private static boolean loggedEntityCollisionUndoFailure = false;
    private static final List<AABBHandle> collisions_buffer = new ArrayList<AABBHandle>();

    EntityMoveHandler_1_8() {
    }

    public static Supplier<EntityMoveHandler> initialize() throws Throwable {
        LevelHandle.T.opt_getCubes_1_8.forceInitialization();
        return EntityMoveHandler_1_8::new;
    }

    @Override
    protected Stream<VoxelShapeHandle> world_getCollisionShapes(EntityHandle entity, double mx, double my, double mz) {
        if (!this.blockCollisionEnabled && !this.entityCollisionEnabled) {
            return Stream.empty();
        }
        List<AABBHandle> cubes = this.world_getCubes(entity, mx, my, mz);
        if (cubes.isEmpty()) {
            return Stream.empty();
        }
        return MountiplexUtil.toStream(VoxelShapeHandle.createHandle(VoxelShapeProxy.fromAABBHandles(cubes)));
    }

    @Override
    public boolean onBlockCollided(Block block) {
        return true;
    }

    protected boolean world_getBlockCubes(EntityHandle entity, AABBHandle movedBounds, List<AABBHandle> cubes) {
        List<AABBHandle> foundBounds = LevelHandle.T.opt_getCubes_1_8.invoke(entity.getWorld().getRaw(), entity, movedBounds);
        if (foundBounds.isEmpty()) {
            return false;
        }
        List<EntityHandle> list = entity.getWorld().getNearbyEntities(entity, movedBounds.growUniform(0.25));
        for (EntityHandle entity1 : list) {
            AABBHandle axisalignedbb2;
            AABBHandle axisalignedbb1;
            if (!CommonCapabilities.VEHICLES_COLLIDE_WITH_PASSENGERS && entity.isInSameVehicle(entity1)) continue;
            if (entity.canCollideWith(entity1) && (axisalignedbb1 = entity1.getBoundingBox()) != null && axisalignedbb1.bbTransformA(movedBounds)) {
                this.removeFromList(foundBounds, axisalignedbb1);
            }
            if ((axisalignedbb2 = entity.getEntityBoundingBox(entity1)) == null || !axisalignedbb2.bbTransformA(movedBounds)) continue;
            this.removeFromList(foundBounds, axisalignedbb2);
        }
        cubes.addAll(foundBounds);
        return true;
    }

    private void world_addEntityCubes(EntityHandle entity, AABBHandle axisalignedbb) {
        if (entity != null && this.entityCollisionEnabled) {
            List<EntityHandle> list = entity.getWorld().getNearbyEntities(entity, axisalignedbb.growUniform(0.25));
            for (EntityHandle entity1 : list) {
                AABBHandle axisalignedbb2;
                AABBHandle axisalignedbb1;
                if (entity.isInSameVehicle(entity1)) continue;
                if (entity.canCollideWith(entity1) && (axisalignedbb1 = entity1.getBoundingBox()) != null && axisalignedbb1.bbTransformA(axisalignedbb) && this.controller.onEntityCollision(entity1.getBukkitEntity())) {
                    collisions_buffer.add(axisalignedbb1);
                }
                if ((axisalignedbb2 = entity.getEntityBoundingBox(entity1)) == null || !axisalignedbb2.bbTransformA(axisalignedbb) || !this.controller.onEntityCollision(entity1.getBukkitEntity())) continue;
                collisions_buffer.add(axisalignedbb2);
            }
        }
    }

    private List<AABBHandle> world_getCubes(EntityHandle entity, double mx, double my, double mz) {
        AABBHandle axisalignedbb_old = entity.getBoundingBox();
        collisions_buffer.clear();
        AABBHandle entityBlockAABB = this.getBlockBoundingBox(entity);
        entity.setBoundingBoxField(entityBlockAABB);
        this.world_getBlockCollisions(entity, entityBlockAABB, entityBlockAABB.transformB(mx, my, mz));
        entity.setBoundingBoxField(axisalignedbb_old);
        this.world_addEntityCubes(entity, axisalignedbb_old.transformB(mx, my, mz));
        return collisions_buffer;
    }

    private boolean world_getBlockCollisions(EntityHandle entity, AABBHandle entityBounds, AABBHandle movedBounds) {
        if (!this.blockCollisionEnabled) {
            return true;
        }
        if (!this.world_getBlockCubes(entity, movedBounds, collisions_buffer)) {
            return false;
        }
        World bWorld = entity.getWorld().getWorld();
        Iterator<AABBHandle> iter = collisions_buffer.iterator();
        while (iter.hasNext()) {
            BlockFace hitFace;
            AABBHandle blockBounds = iter.next();
            Block block = bWorld.getBlockAt(MathUtil.floor(blockBounds.getMinX()), MathUtil.floor(blockBounds.getMinY()), MathUtil.floor(blockBounds.getMinZ()));
            if (movedBounds.getMaxY() > blockBounds.getMaxY()) {
                hitFace = BlockFace.UP;
            } else if (movedBounds.getMinY() < blockBounds.getMinY()) {
                hitFace = BlockFace.DOWN;
            } else {
                double dx = entity.getLocX() - (double)block.getX() - 0.5;
                double dz = entity.getLocZ() - (double)block.getZ() - 0.5;
                hitFace = FaceUtil.getDirection(dx, dz, false);
            }
            if (this.controller.onBlockCollision(block, hitFace)) continue;
            iter.remove();
        }
        return true;
    }

    private void removeFromList(List<AABBHandle> bounds, AABBHandle toRemove) {
        ListIterator<AABBHandle> iter = bounds.listIterator(bounds.size());
        while (iter.hasPrevious()) {
            AABBHandle b = iter.previous();
            if (b.getMinX() != toRemove.getMinX() || b.getMinY() != toRemove.getMinY() || b.getMinZ() != toRemove.getMinZ() || b.getMaxX() != toRemove.getMaxX() || b.getMaxY() != toRemove.getMaxY() || b.getMaxZ() != toRemove.getMaxZ()) continue;
            iter.remove();
            return;
        }
        if (!loggedEntityCollisionUndoFailure) {
            loggedEntityCollisionUndoFailure = true;
            Logging.LOGGER_DEBUG.severe("EntityMoveHandler failed to undo Entity Collision");
        }
    }
}

