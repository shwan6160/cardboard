/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.util.RandomSourceHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.BlockGetterHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.BlockEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.HitResultHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

@Template.InstanceType(value="net.minecraft.world.level.Level")
public abstract class LevelHandle
extends BlockGetterHandle {
    public static final LevelClass T = Template.Class.create(LevelClass.class, Common.TEMPLATE_RESOLVER);
    public static final int UPDATE_PHYSICS = 1;
    public static final int UPDATE_NOTIFY = 2;
    public static final int UPDATE_DEFAULT = 3;

    public static LevelHandle createHandle(Object handleInstance) {
        return (LevelHandle)T.createHandle(handleInstance);
    }

    public abstract void setKeepSpawnInMemoryDuringInit(boolean var1);

    public abstract void method_profiler_begin(String var1);

    public abstract void method_profiler_end();

    public abstract World getWorld();

    public abstract Server getServer();

    public abstract BlockData getBlockData(IntVector3 var1);

    public abstract BlockData getBlockDataAtCoord(int var1, int var2, int var3);

    public abstract boolean setBlockData(IntVector3 var1, BlockData var2, int var3);

    public abstract long getTime();

    public abstract DimensionType getDimensionType();

    public abstract boolean isWithinWorldBorder(EntityHandle var1);

    public abstract boolean isNotCollidingWithBlocks(EntityHandle var1, AABBHandle var2);

    public abstract List<?> getRawEntitiesOfType(Class<?> var1, AABBHandle var2);

    public abstract List<EntityHandle> getNearbyEntities(EntityHandle var1, AABBHandle var2);

    public abstract BlockEntityHandle getTileEntity(IntVector3 var1);

    public abstract boolean isBurnArea(AABBHandle var1);

    public abstract Entity getEntityById(int var1);

    public abstract boolean areChunksLoaded(IntVector3 var1, int var2);

    public abstract HitResultHandle rayTrace(Vector var1, Vector var2);

    public abstract void applyBlockPhysicsAround(IntVector3 var1, BlockData var2);

    public abstract void applyBlockPhysics(IntVector3 var1, BlockData var2);

    public abstract int getMinBuildHeight();

    public abstract int getMaxBuildHeight();

    public abstract int getNetherPortalSearchRadius();

    public abstract int getNetherPortalCreateRadius();

    public void applyBlockPhysics(IntVector3 position, BlockData causeType, boolean self) {
        this.applyBlockPhysicsAround(position, causeType);
        if (self) {
            this.applyBlockPhysics(position, causeType);
        }
    }

    public World toBukkit() {
        return (World)Conversion.toWorld.convert(this.getRaw());
    }

    public static LevelHandle fromBukkit(World world) {
        return LevelHandle.createHandle(Conversion.toWorldHandle.convert(world));
    }

    public abstract RandomSourceHandle getRandom();

    public abstract void setRandom(RandomSourceHandle var1);

    public abstract World getBukkitWorld();

    public abstract void setBukkitWorld(World var1);

    public static final class LevelClass
    extends Template.Class<LevelHandle> {
        public final Template.Field.Converted<RandomSourceHandle> random = new Template.Field.Converted();
        @Template.Optional
        public final Template.Field.Converted<Object> field_chunkProvider = new Template.Field.Converted();
        public final Template.Field.Converted<World> bukkitWorld = new Template.Field.Converted();
        public final Template.Method<Void> setKeepSpawnInMemoryDuringInit = new Template.Method();
        public final Template.Method<Void> method_profiler_begin = new Template.Method();
        public final Template.Method<Void> method_profiler_end = new Template.Method();
        public final Template.Method.Converted<World> getWorld = new Template.Method.Converted();
        public final Template.Method.Converted<Server> getServer = new Template.Method.Converted();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted();
        public final Template.Method<BlockData> getBlockDataAtCoord = new Template.Method();
        @Template.Optional
        public final Template.Method<Object> getChunkProvider = new Template.Method();
        public final Template.Method.Converted<Boolean> setBlockData = new Template.Method.Converted();
        public final Template.Method<Long> getTime = new Template.Method();
        public final Template.Method.Converted<DimensionType> getDimensionType = new Template.Method.Converted();
        public final Template.Method.Converted<Boolean> isWithinWorldBorder = new Template.Method.Converted();
        @Template.Optional
        public final Template.Method.Converted<Boolean> getBlockCollisions = new Template.Method.Converted();
        public final Template.Method.Converted<Boolean> isNotCollidingWithBlocks = new Template.Method.Converted();
        @Template.Optional
        public final Template.Method.Converted<List<AABBHandle>> opt_getCubes_1_8 = new Template.Method.Converted();
        public final Template.Method.Converted<List<?>> getRawEntitiesOfType = new Template.Method.Converted();
        public final Template.Method.Converted<List<EntityHandle>> getNearbyEntities = new Template.Method.Converted();
        public final Template.Method.Converted<BlockEntityHandle> getTileEntity = new Template.Method.Converted();
        public final Template.Method.Converted<Boolean> isBurnArea = new Template.Method.Converted();
        public final Template.Method.Converted<Entity> getEntityById = new Template.Method.Converted();
        public final Template.Method.Converted<Boolean> areChunksLoaded = new Template.Method.Converted();
        public final Template.Method.Converted<HitResultHandle> rayTrace = new Template.Method.Converted();
        public final Template.Method.Converted<Void> applyBlockPhysicsAround = new Template.Method.Converted();
        public final Template.Method.Converted<Void> applyBlockPhysics = new Template.Method.Converted();
        public final Template.Method<Integer> getMinBuildHeight = new Template.Method();
        public final Template.Method<Integer> getMaxBuildHeight = new Template.Method();
        public final Template.Method<Integer> getNetherPortalSearchRadius = new Template.Method();
        public final Template.Method<Integer> getNetherPortalCreateRadius = new Template.Method();
    }
}

