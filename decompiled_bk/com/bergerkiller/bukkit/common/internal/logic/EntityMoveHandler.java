/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Vehicle
 *  org.bukkit.event.Event
 *  org.bukkit.event.vehicle.VehicleBlockCollisionEvent
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.logic.EntityMoveHandler_1_11_2;
import com.bergerkiller.bukkit.common.internal.logic.EntityMoveHandler_1_13;
import com.bergerkiller.bukkit.common.internal.logic.EntityMoveHandler_1_14;
import com.bergerkiller.bukkit.common.internal.logic.EntityMoveHandler_1_8;
import com.bergerkiller.bukkit.common.io.StreamAccumulator;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.MoveType;
import com.bergerkiller.generated.net.minecraft.CrashReportCategoryHandle;
import com.bergerkiller.generated.net.minecraft.CrashReportHandle;
import com.bergerkiller.generated.net.minecraft.ReportedExceptionHandle;
import com.bergerkiller.generated.net.minecraft.core.DirectionHandle;
import com.bergerkiller.generated.net.minecraft.util.RandomSourceHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.PlayerHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlocksHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.FenceBlockHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.FenceGateBlockHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.WallBlockHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.shapes.VoxelShapeHandle;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.util.Vector;

public abstract class EntityMoveHandler {
    private static final Supplier<EntityMoveHandler> moveHandlerFactory = EntityMoveHandler.initMoveHandlerFactory();
    protected boolean blockCollisionEnabled = true;
    protected boolean blockActivationEnabled = true;
    protected boolean entityCollisionEnabled = true;
    protected Vector customBlockCollisionBounds = null;
    protected EntityController<?> controller;
    protected EntityHandle that;
    private CommonEntity<?> entity;
    private final StreamAccumulator<VoxelShapeHandle> shapeAccumulator = new StreamAccumulator();

    private static Supplier<EntityMoveHandler> initMoveHandlerFactory() {
        try {
            if (Common.evaluateMCVersion(">=", "1.14")) {
                return EntityMoveHandler_1_14.initialize();
            }
            if (CommonCapabilities.HAS_VOXELSHAPE_LOGIC) {
                return EntityMoveHandler_1_13.initialize();
            }
            if (LevelHandle.T.getBlockCollisions.isAvailable()) {
                return EntityMoveHandler_1_11_2.initialize();
            }
            return EntityMoveHandler_1_8.initialize();
        }
        catch (Throwable t) {
            return new ErrorFactory(t);
        }
    }

    public static void assertInitialized() {
        if (moveHandlerFactory instanceof ErrorFactory) {
            throw new IllegalStateException("Failed to initialize entity movement handler", ((ErrorFactory)EntityMoveHandler.moveHandlerFactory).cause);
        }
    }

    protected EntityMoveHandler() {
    }

    public static EntityMoveHandler create(EntityController<?> controller) {
        EntityMoveHandler handler = moveHandlerFactory.get();
        handler.controller = controller;
        return handler;
    }

    public void setBlockCollisionEnabled(boolean enabled) {
        this.blockCollisionEnabled = enabled;
    }

    public void setBlockActivationEnabled(boolean enabled) {
        this.blockActivationEnabled = enabled;
    }

    public void setEntityCollisionEnabled(boolean enabled) {
        this.entityCollisionEnabled = enabled;
    }

    public void setCustomBlockCollisionBounds(Vector bounds) {
        this.customBlockCollisionBounds = bounds == null ? null : bounds.clone();
    }

    protected AABBHandle getBlockBoundingBox(EntityHandle entity) {
        AABBHandle boundingBox = entity.getBoundingBox();
        if (this.blockCollisionEnabled && this.customBlockCollisionBounds != null) {
            double x = 0.5 * (boundingBox.getMinX() + boundingBox.getMaxX());
            double y = boundingBox.getMinY();
            double z = 0.5 * (boundingBox.getMinZ() + boundingBox.getMaxZ());
            return AABBHandle.createNew(x - 0.5 * this.customBlockCollisionBounds.getX(), y, z - 0.5 * this.customBlockCollisionBounds.getZ(), x + 0.5 * this.customBlockCollisionBounds.getX(), y + this.customBlockCollisionBounds.getY(), z + 0.5 * this.customBlockCollisionBounds.getZ());
        }
        return boundingBox;
    }

    protected static ClassResolver createCollisionHandlerClassResolver() {
        ClassResolver resolver = new ClassResolver();
        resolver.setVariable("version", Common.MC_VERSION);
        resolver.addImport("net.minecraft.core.BlockPos");
        resolver.addImport("net.minecraft.core.BlockPos$MutableBlockPos");
        resolver.addImport("net.minecraft.core.BlockPos$PooledBlockPosition");
        resolver.addImport("net.minecraft.core.Direction");
        resolver.addImport("net.minecraft.core.Direction$Axis");
        resolver.addImport("net.minecraft.util.Mth");
        resolver.addImport("net.minecraft.world.phys.AABB");
        resolver.addImport("net.minecraft.world.phys.shapes.BooleanOp");
        resolver.addImport("net.minecraft.world.phys.shapes.VoxelShape");
        resolver.addImport("net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape");
        resolver.addImport("net.minecraft.world.phys.shapes.VoxelShapeWorldRegion");
        resolver.addImport("net.minecraft.world.phys.shapes.Shapes");
        resolver.addImport("net.minecraft.world.level.border.WorldBorder");
        resolver.addImport("net.minecraft.world.level.BlockGetter");
        resolver.addImport(MathUtil.class.getName());
        resolver.setDeclaredClassName("net.minecraft.world.level.Level");
        return resolver;
    }

    protected abstract Stream<VoxelShapeHandle> world_getCollisionShapes(EntityHandle var1, double var2, double var4, double var6);

    public abstract boolean onBlockCollided(Block var1);

    public void move(EntityHandle entity, MoveType movetype, double d0, double d1, double d2) {
        this.that = entity;
        this.moveImpl(movetype, d0, d1, d2);
        this.that = null;
    }

    public void moveImpl(MoveType movetype, double d0, double d1, double d2) {
        this.entity = this.controller.getEntity();
        if (this.entity == null) {
            throw new IllegalStateException("Entity Controller is not attached to an Entity");
        }
        this.that = EntityHandle.createHandle(this.entity.getHandle());
        RandomSourceHandle this_random = this.that.getRandom();
        LevelHandle world = this.that.getWorld();
        if (this.that.isNoclip()) {
            this.that.setBoundingBox(this.that.getBoundingBox().translate(d0, d1, d2));
            this.that.setPositionFromBoundingBox();
        } else {
            IntVector3 blockposition1;
            BlockData iblockdata1;
            BlockHandle block;
            boolean flag;
            try {
                if (this.blockActivationEnabled) {
                    this.that.applyEffectsFromBlocks();
                }
            }
            catch (Throwable throwable) {
                CrashReportHandle crashreport = CrashReportHandle.create(throwable, "Checking entity block collision");
                CrashReportCategoryHandle crashreportsystemdetails = crashreport.getSystemDetails("Entity being checked for collision");
                this.that.appendEntityCrashDetails(crashreportsystemdetails);
                throw (RuntimeException)ReportedExceptionHandle.createNew(crashreport).getRaw();
            }
            if (d0 == 0.0 && d1 == 0.0 && d2 == 0.0 && this.that.isVehicle() && this.that.isPassenger()) {
                return;
            }
            if (CommonCapabilities.ENTITY_MOVE_VER2 && movetype == MoveType.PISTON) {
                double d3;
                int j;
                long i = world.getTime();
                double[] that_aI = EntityHandle.T.move_SomeArray.get(this.entity.getHandle());
                if (i != EntityHandle.T.move_SomeState.getLong(this.entity.getHandle())) {
                    Arrays.fill(that_aI, 0.0);
                    EntityHandle.T.move_SomeState.setLong(this.entity.getHandle(), i);
                }
                if (d0 != 0.0) {
                    j = DirectionHandle.AxisHandle.X.ordinal();
                    d3 = MathUtil.clamp(d0 + that_aI[j], -0.51, 0.51);
                    d0 = d3 - that_aI[j];
                    that_aI[j] = d3;
                    if (Math.abs(d0) <= (double)1.0E-5f) {
                        return;
                    }
                } else if (d1 != 0.0) {
                    j = DirectionHandle.AxisHandle.Y.ordinal();
                    d3 = MathUtil.clamp(d1 + that_aI[j], -0.51, 0.51);
                    d1 = d3 - that_aI[j];
                    that_aI[j] = d3;
                    if (Math.abs(d1) <= (double)1.0E-5f) {
                        return;
                    }
                } else {
                    if (d2 == 0.0) {
                        return;
                    }
                    j = DirectionHandle.AxisHandle.Z.ordinal();
                    d3 = MathUtil.clamp(d2 + that_aI[j], -0.51, 0.51);
                    d2 = d3 - that_aI[j];
                    that_aI[j] = d3;
                    if (Math.abs(d2) <= (double)1.0E-5f) {
                        return;
                    }
                }
            }
            world.method_profiler_begin("move");
            double d4 = this.that.getLocX();
            double d5 = this.that.getLocY();
            double d6 = this.that.getLocZ();
            if (this.that.isCollidingWithBlock()) {
                if (this.blockCollisionEnabled) {
                    Vector multiplier = this.that.getBlockCollisionMultiplier();
                    d0 *= multiplier.getX();
                    d1 *= multiplier.getY();
                    d2 *= multiplier.getZ();
                    this.that.setMot(0.0, 0.0, 0.0);
                }
                this.that.setNotCollidingWithBlock();
            }
            double d7 = d0;
            double d8 = d1;
            double d9 = d2;
            if ((movetype == MoveType.SELF || movetype == MoveType.PLAYER) && this.that.isOnGround() && this.that.isSneaking() && this.that.isInstanceOf(PlayerHandle.T)) {
                while (d0 != 0.0 && world.isNotCollidingWithBlocks(this.that, this.that.getBoundingBox().translate(d0, -this.that.getHeightOffset(), 0.0))) {
                    d0 = d0 < 0.05 && d0 >= -0.05 ? 0.0 : (d0 > 0.0 ? (d0 -= 0.05) : (d0 += 0.05));
                    d7 = d0;
                }
                while (d2 != 0.0 && world.isNotCollidingWithBlocks(this.that, this.that.getBoundingBox().translate(0.0, -this.that.getHeightOffset(), d2))) {
                    d2 = d2 < 0.05 && d2 >= -0.05 ? 0.0 : (d2 > 0.0 ? (d2 -= 0.05) : (d2 += 0.05));
                    d9 = d2;
                }
                while (d0 != 0.0 && d2 != 0.0 && world.isNotCollidingWithBlocks(this.that, this.that.getBoundingBox().translate(d0, -this.that.getHeightOffset(), d2))) {
                    d0 = d0 < 0.05 && d0 >= -0.05 ? 0.0 : (d0 > 0.0 ? (d0 -= 0.05) : (d0 += 0.05));
                    d7 = d0;
                    d2 = d2 < 0.05 && d2 >= -0.05 ? 0.0 : (d2 > 0.0 ? (d2 -= 0.05) : (d2 += 0.05));
                    d9 = d2;
                }
            }
            AABBHandle axisalignedbb = this.that.getBoundingBox();
            if (d0 != 0.0 || d1 != 0.0 || d2 != 0.0) {
                this.shapeAccumulator.open(this.world_getCollisionShapes(this.that, d0, d1, d2));
                if (!this.shapeAccumulator.isEmpty()) {
                    if ((d1 <= -1.0E-7 || d1 >= 1.0E-7) && (d1 = VoxelShapeHandle.traceAxis(DirectionHandle.AxisHandle.Y, this.that.getBoundingBox(), this.shapeAccumulator.stream(), d1)) != 0.0) {
                        this.that.setBoundingBox(this.that.getBoundingBox().translate(0.0, d1, 0.0));
                    }
                    if ((d0 <= -1.0E-7 || d0 >= 1.0E-7) && (d0 = VoxelShapeHandle.traceAxis(DirectionHandle.AxisHandle.X, this.that.getBoundingBox(), this.shapeAccumulator.stream(), d0)) != 0.0) {
                        this.that.setBoundingBox(this.that.getBoundingBox().translate(d0, 0.0, 0.0));
                    }
                    if ((d2 <= -1.0E-7 || d2 >= 1.0E-7) && (d2 = VoxelShapeHandle.traceAxis(DirectionHandle.AxisHandle.Z, this.that.getBoundingBox(), this.shapeAccumulator.stream(), d2)) != 0.0) {
                        this.that.setBoundingBox(this.that.getBoundingBox().translate(0.0, 0.0, d2));
                    }
                } else {
                    this.that.setBoundingBox(this.that.getBoundingBox().translate(d0, d1, d2));
                }
            }
            boolean bl = flag = this.that.isOnGround() || d1 != d8 && d1 < 0.0;
            if (this.that.getHeightOffset() > 0.0f && flag && (d7 != d0 || d9 != d2)) {
                double d21;
                double d20;
                double d12 = d0;
                double d13 = d1;
                double d14 = d2;
                AABBHandle axisalignedbb1 = this.that.getBoundingBox();
                this.that.setBoundingBox(axisalignedbb);
                d1 = this.that.getHeightOffset();
                this.shapeAccumulator.open(this.world_getCollisionShapes(this.that, d7, d1, d9));
                AABBHandle axisalignedbb2 = this.that.getBoundingBox();
                AABBHandle axisalignedbb3 = axisalignedbb2.transformB(d7, 0.0, d9);
                double d11 = d1;
                d11 = VoxelShapeHandle.traceAxis(DirectionHandle.AxisHandle.Y, axisalignedbb3, this.shapeAccumulator.stream(), d11);
                if (d11 != 0.0) {
                    axisalignedbb2 = axisalignedbb2.translate(0.0, d11, 0.0);
                }
                double d15 = d7;
                d15 = VoxelShapeHandle.traceAxis(DirectionHandle.AxisHandle.X, axisalignedbb2, this.shapeAccumulator.stream(), d15);
                if (d15 != 0.0) {
                    axisalignedbb2 = axisalignedbb2.translate(d15, 0.0, 0.0);
                }
                double d16 = d9;
                d16 = VoxelShapeHandle.traceAxis(DirectionHandle.AxisHandle.Z, axisalignedbb2, this.shapeAccumulator.stream(), d16);
                if (d16 != 0.0) {
                    axisalignedbb2 = axisalignedbb2.translate(0.0, 0.0, d16);
                }
                AABBHandle axisalignedbb4 = this.that.getBoundingBox();
                double d17 = d1;
                d17 = VoxelShapeHandle.traceAxis(DirectionHandle.AxisHandle.Y, axisalignedbb4, this.shapeAccumulator.stream(), d17);
                if (d17 != 0.0) {
                    axisalignedbb4 = axisalignedbb4.translate(0.0, d17, 0.0);
                }
                double d18 = d7;
                d18 = VoxelShapeHandle.traceAxis(DirectionHandle.AxisHandle.X, axisalignedbb4, this.shapeAccumulator.stream(), d18);
                if (d18 != 0.0) {
                    axisalignedbb4 = axisalignedbb4.translate(d18, 0.0, 0.0);
                }
                double d19 = d9;
                d19 = VoxelShapeHandle.traceAxis(DirectionHandle.AxisHandle.Z, axisalignedbb4, this.shapeAccumulator.stream(), d19);
                if (d19 != 0.0) {
                    axisalignedbb4 = axisalignedbb4.translate(0.0, 0.0, d19);
                }
                if ((d20 = d15 * d15 + d16 * d16) > (d21 = d18 * d18 + d19 * d19)) {
                    d0 = d15;
                    d2 = d16;
                    d1 = -d11;
                    this.that.setBoundingBox(axisalignedbb2);
                } else {
                    d0 = d18;
                    d2 = d19;
                    d1 = -d17;
                    this.that.setBoundingBox(axisalignedbb4);
                }
                d1 = VoxelShapeHandle.traceAxis(DirectionHandle.AxisHandle.Y, this.that.getBoundingBox(), this.shapeAccumulator.stream(), d1);
                if (d1 != 0.0) {
                    this.that.setBoundingBox(this.that.getBoundingBox().translate(0.0, d1, 0.0));
                }
                if (d12 * d12 + d14 * d14 >= d0 * d0 + d2 * d2) {
                    d0 = d12;
                    d1 = d13;
                    d2 = d14;
                    this.that.setBoundingBox(axisalignedbb1);
                }
            }
            world.method_profiler_end();
            world.method_profiler_begin("rest");
            this.that.setPositionFromBoundingBox();
            this.that.setHorizontalMovementBlocked(d7 != d0 || d9 != d2);
            this.that.setVerticalMovementBlocked(d1 != d8);
            this.that.setOnGround(this.that.isVerticalMovementBlocked() && d8 < 0.0);
            int l = MathUtil.floor(this.that.getLocX());
            int k4 = MathUtil.floor(this.that.getLocY() - 0.2);
            int l4 = MathUtil.floor(this.that.getLocZ());
            IntVector3 blockposition = new IntVector3(l, k4, l4);
            BlockData iblockdata = world.getBlockData(blockposition);
            if (iblockdata.isType(Material.AIR) && ((block = (iblockdata1 = world.getBlockData(blockposition1 = blockposition.add(0, -1, 0))).getBlock()).isInstanceOf(FenceBlockHandle.T) || block.isInstanceOf(WallBlockHandle.T) || block.isInstanceOf(FenceGateBlockHandle.T))) {
                iblockdata = iblockdata1;
                blockposition = blockposition1;
            }
            this.that.updateFalling(d1, this.that.isOnGround(), iblockdata, blockposition);
            this.that.restituteMovementAfterCollisions(iblockdata, d7 != d0, d9 != d2, new Vector());
            if (this.that.isHorizontalMovementBlocked() && this.that.getBukkitEntity() instanceof Vehicle) {
                World bl_world = this.entity.getWorld();
                int bl_x = MathUtil.floor(this.that.getLocX());
                int bl_y = MathUtil.floor(this.that.getLocY());
                int bl_z = MathUtil.floor(this.that.getLocZ());
                if (d7 > d0) {
                    ++bl_x;
                } else if (d7 < d0) {
                    --bl_x;
                } else if (d9 > d2) {
                    ++bl_z;
                } else if (d9 < d2) {
                    --bl_z;
                }
                if (!WorldUtil.getBlockData(bl_world, bl_x, bl_y, bl_z).isType(Material.AIR)) {
                    Block bl2 = bl_world.getBlockAt(bl_x, bl_y, bl_z);
                    Vehicle vehicle = (Vehicle)this.that.getBukkitEntity();
                    VehicleBlockCollisionEvent event = new VehicleBlockCollisionEvent(vehicle, bl2);
                    Bukkit.getPluginManager().callEvent((Event)event);
                }
            }
            if (!(!this.that.hasMovementSound() || this.that.isOnGround() && this.that.isSneaking() && this.that.isInstanceOf(PlayerHandle.T) || this.that.isPassenger())) {
                double d22 = this.that.getLocX() - d4;
                double d23 = this.that.getLocY() - d5;
                double d11 = this.that.getLocZ() - d6;
                BlockHandle block1 = iblockdata.getBlock();
                if (block1.getRaw() != BlocksHandle.LADDER) {
                    d23 = 0.0;
                }
                if (block1 != null && this.that.isOnGround()) {
                    iblockdata.stepOn(this.entity.getWorld(), blockposition, (Entity)this.entity.getEntity());
                }
                this.that.handleMovementEmissionAndPlaySound(new Vector(d22, d23, d11), blockposition, iblockdata);
            }
            boolean flag1 = this.that.isWet();
            if (this.blockCollisionEnabled && world.isBurnArea(this.that.getBoundingBox().shrinkUniform(0.001))) {
                this.that.burn(1.0f);
                if (!flag1) {
                    this.that.handleFireBlockTick();
                }
            } else if (this.that.getFireTicks() <= 0) {
                this.that.setFireTicks(-this.that.getMaxFireTicks());
            }
            if (flag1 && this.that.isBurning()) {
                this.that.makeSound(SoundEffect.EXTINGUISH, 0.7f, 1.6f + (this_random.nextFloat() - this_random.nextFloat()) * 0.4f);
                this.that.setFireTicks(-this.that.getMaxFireTicks());
            }
            world.method_profiler_end();
        }
    }

    private static final class ErrorFactory
    implements Supplier<EntityMoveHandler> {
        public final Throwable cause;

        public ErrorFactory(Throwable cause) {
            this.cause = cause;
        }

        @Override
        public EntityMoveHandler get() {
            throw new UnsupportedOperationException("Failed to initialize the entity movement handler", this.cause);
        }
    }
}

