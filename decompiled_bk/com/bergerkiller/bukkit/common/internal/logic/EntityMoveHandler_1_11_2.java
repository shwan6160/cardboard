/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.logic.EntityMoveHandler;
import com.bergerkiller.bukkit.common.internal.logic.EntityMoveHandler_1_8;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import java.util.List;
import java.util.function.Supplier;

class EntityMoveHandler_1_11_2
extends EntityMoveHandler_1_8 {
    EntityMoveHandler_1_11_2() {
    }

    public static Supplier<EntityMoveHandler> initialize() throws Throwable {
        LevelHandle.T.getBlockCollisions.forceInitialization();
        return EntityMoveHandler_1_11_2::new;
    }

    @Override
    protected boolean world_getBlockCubes(EntityHandle entity, AABBHandle movedBounds, List<AABBHandle> cubes) {
        return LevelHandle.T.getBlockCollisions.invoke(entity.getWorld().getRaw(), entity, movedBounds, false, cubes);
    }
}

