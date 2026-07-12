/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.logic.EntityMoveHandler;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.shapes.VoxelShapeHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

class EntityMoveHandler_1_14
extends EntityMoveHandler {
    private final HandlerLogic logic;

    private EntityMoveHandler_1_14(HandlerLogic logic) {
        this.logic = logic;
    }

    public static Supplier<EntityMoveHandler> initialize() throws Throwable {
        return new HandlerLogic();
    }

    @Override
    protected Stream<VoxelShapeHandle> world_getCollisionShapes(EntityHandle entity, double mx, double my, double mz) {
        if (!this.blockCollisionEnabled && !this.entityCollisionEnabled) {
            return Stream.empty();
        }
        Stream<VoxelShapeHandle> shape_blockCollisions = this.world_getBlockCollisionShapes(entity, mx, my, mz);
        Stream<VoxelShapeHandle> shape_entityCollisions = this.world_getEntityCollisionShapes(entity, mx, my, mz);
        return Stream.concat(shape_blockCollisions, shape_entityCollisions);
    }

    @Override
    public boolean onBlockCollided(Block block) {
        BlockFace hitFace;
        AABBHandle entityBounds = this.that.getBoundingBox();
        if (entityBounds.getMaxY() > (double)block.getY() + 1.0) {
            hitFace = BlockFace.UP;
        } else if (entityBounds.getMinY() < (double)block.getY()) {
            hitFace = BlockFace.DOWN;
        } else {
            double dx = this.that.getLocX() - (double)block.getX() - 0.5;
            double dz = this.that.getLocZ() - (double)block.getZ() - 0.5;
            hitFace = FaceUtil.getDirection(dx, dz, false);
        }
        return this.controller.onBlockCollision(block, hitFace);
    }

    private Stream<VoxelShapeHandle> world_getBlockCollisionShapes(EntityHandle entity, double mx, double my, double mz) {
        if (!this.blockCollisionEnabled) {
            return Stream.empty();
        }
        AABBHandle entityBounds = this.getBlockBoundingBox(entity);
        double MIN_MOVE = 1.0E-7;
        VoxelShapeHandle voxelshapeAABB = VoxelShapeHandle.fromAABB(entityBounds);
        VoxelShapeHandle voxelshapeAABBMoved = VoxelShapeHandle.fromAABB(entityBounds.translate(mx > 0.0 ? -1.0E-7 : 1.0E-7, my > 0.0 ? -1.0E-7 : 1.0E-7, mz > 0.0 ? -1.0E-7 : 1.0E-7));
        VoxelShapeHandle voxelshapeBounds = VoxelShapeHandle.mergeOnlyFirst(VoxelShapeHandle.fromAABB(entityBounds.transformB(mx, my, mz).growUniform(1.0E-7)), voxelshapeAABBMoved);
        if (voxelshapeBounds.isEmpty()) {
            return Stream.empty();
        }
        LevelHandle world = entity.getWorld();
        return this.logic.getBlockCollisions(world, this, voxelshapeBounds, voxelshapeAABB);
    }

    private Stream<VoxelShapeHandle> world_getEntityCollisionShapes(EntityHandle entity, double mx, double my, double mz) {
        if (!this.entityCollisionEnabled) {
            return Stream.empty();
        }
        AABBHandle entityBounds = entity.getBoundingBox();
        double MIN_MOVE = 1.0E-7;
        VoxelShapeHandle voxelshapeAABBMoved = VoxelShapeHandle.fromAABB(entityBounds.translate(mx > 0.0 ? -1.0E-7 : 1.0E-7, my > 0.0 ? -1.0E-7 : 1.0E-7, mz > 0.0 ? -1.0E-7 : 1.0E-7));
        VoxelShapeHandle voxelshapeBounds = VoxelShapeHandle.mergeOnlyFirst(VoxelShapeHandle.fromAABB(entityBounds.transformB(mx, my, mz).growUniform(1.0E-7)), voxelshapeAABBMoved);
        if (voxelshapeBounds.isEmpty()) {
            return Stream.empty();
        }
        AABBHandle axisalignedbb = voxelshapeBounds.getBoundingBox();
        VoxelShapeHandle shape = VoxelShapeHandle.empty();
        if (this.entityCollisionEnabled) {
            List<EntityHandle> list = entity.getWorld().getNearbyEntities(entity, axisalignedbb.growUniform(0.25));
            for (EntityHandle entity1 : list) {
                AABBHandle axisalignedbb2;
                AABBHandle axisalignedbb1;
                if (entity.isInSameVehicle(entity1)) continue;
                if (entity.canCollideWith(entity1) && (axisalignedbb1 = entity1.getBoundingBox()) != null && axisalignedbb1.bbTransformA(axisalignedbb) && this.controller.onEntityCollision(entity1.getBukkitEntity())) {
                    shape = VoxelShapeHandle.merge(shape, VoxelShapeHandle.fromAABB(axisalignedbb1));
                }
                if ((axisalignedbb2 = entity.getEntityBoundingBox(entity1)) == null || !axisalignedbb2.bbTransformA(axisalignedbb) || !this.controller.onEntityCollision(entity1.getBukkitEntity())) continue;
                shape = VoxelShapeHandle.merge(shape, VoxelShapeHandle.fromAABB(axisalignedbb2));
            }
        }
        if (shape.isEmpty()) {
            return Stream.empty();
        }
        return MountiplexUtil.toStream(shape);
    }

    private static class HandlerLogic
    implements Supplier<EntityMoveHandler> {
        private final FastMethod<Stream<?>> getBlockCollisions_method = new FastMethod();
        private final Converter<Stream<?>, Stream<VoxelShapeHandle>> streamConverter = (Converter)LogicUtil.unsafeCast(Conversion.find(TypeDeclaration.createGeneric(Stream.class, VoxelShapeHandle.T.getType()), TypeDeclaration.createGeneric(Stream.class, VoxelShapeHandle.class)));

        public HandlerLogic() throws Throwable {
            ClassResolver resolver = EntityMoveHandler.createCollisionHandlerClassResolver();
            String method_path = CommonBootstrap.evaluateMCVersion(">=", "1.18") ? "/com/bergerkiller/bukkit/common/internal/logic/EntityMoveHandler_1_18_getBlockCollisions.txt" : (CommonBootstrap.evaluateMCVersion(">=", "1.16") ? "/com/bergerkiller/bukkit/common/internal/logic/EntityMoveHandler_1_16_getBlockCollisions.txt" : (CommonBootstrap.evaluateMCVersion(">=", "1.14.1") ? "/com/bergerkiller/bukkit/common/internal/logic/EntityMoveHandler_1_14_1_getBlockCollisions.txt" : "/com/bergerkiller/bukkit/common/internal/logic/EntityMoveHandler_1_14_getBlockCollisions.txt"));
            try (InputStream input = EntityMoveHandler_1_14.class.getResourceAsStream(method_path);
                 Scanner scanner = new Scanner(input, "UTF-8");){
                scanner.useDelimiter("\\A");
                String method_body = scanner.next();
                method_body = SourceDeclaration.preprocess(method_body, resolver);
                method_body = method_body.replaceAll("this", "instance");
                method_body = method_body.trim();
                this.getBlockCollisions_method.init(new MethodDeclaration(resolver, method_body));
                this.getBlockCollisions_method.forceInitialization();
            }
        }

        public Stream<VoxelShapeHandle> getBlockCollisions(LevelHandle world, EntityMoveHandler handler, VoxelShapeHandle voxelshapeBounds, VoxelShapeHandle voxelshapeAABB) {
            return this.streamConverter.convertInput(this.getBlockCollisions_method.invoke(world.getRaw(), handler, voxelshapeBounds.getRaw(), voxelshapeAABB.getRaw(), false));
        }

        @Override
        public EntityMoveHandler get() {
            return new EntityMoveHandler_1_14(this);
        }
    }
}

