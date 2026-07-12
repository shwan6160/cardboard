/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.block.BlockState
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.generated.net.minecraft.world.level.chunk;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.blockstate.ChunkBlockStateConverter;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChunkSection;
import com.bergerkiller.bukkit.common.wrappers.HeightMap;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LightLayerHandle;
import com.bergerkiller.mountiplex.conversion.util.ConvertingCollection;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;
import java.util.List;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;

@Template.InstanceType(value="net.minecraft.world.level.chunk.LevelChunk")
public abstract class LevelChunkHandle
extends Template.Handle {
    public static final LevelChunkClass T = Template.Class.create(LevelChunkClass.class, Common.TEMPLATE_RESOLVER);

    public static LevelChunkHandle createHandle(Object handleInstance) {
        return (LevelChunkHandle)T.createHandle(handleInstance);
    }

    public abstract LevelHandle getWorld();

    public abstract int getLocX();

    public abstract int getLocZ();

    public abstract List<Integer> getLoadedSectionCoordinates();

    public abstract ChunkSection[] getSections();

    public abstract Object getSectionRaw(int var1);

    public abstract ChunkSection getSection(int var1);

    public abstract Collection<?> getRawTileEntities();

    public abstract List<Entity> getEntities();

    public abstract Chunk getBukkitChunk();

    public abstract BlockData getBlockData(IntVector3 var1);

    public abstract BlockData getBlockDataAtCoord(int var1, int var2, int var3);

    public abstract BlockData setBlockData(IntVector3 var1, BlockData var2, int var3);

    public abstract void addEntity(EntityHandle var1);

    public abstract HeightMap getLightHeightMap(boolean var1);

    public abstract int getBrightness(LightLayerHandle var1, IntVector3 var2);

    public abstract int getTopSliceY();

    public abstract void addEntities();

    public abstract boolean checkCanSave(boolean var1);

    public abstract void markDirty();

    public abstract void markEntitiesDirty();

    public Collection<BlockState> getTileEntities() {
        Collection<?> tileEntities = this.getRawTileEntities();
        ChunkBlockStateConverter chunkBlockStateConverter = new ChunkBlockStateConverter(this.getBukkitChunk());
        return new ConvertingCollection<BlockState>(tileEntities, chunkBlockStateConverter);
    }

    public static LevelChunkHandle fromBukkit(Chunk chunk) {
        if (chunk != null) {
            return LevelChunkHandle.createHandle(HandleConversion.toChunkHandle(chunk));
        }
        return null;
    }

    public static final class LevelChunkClass
    extends Template.Class<LevelChunkHandle> {
        public final Template.Method.Converted<LevelHandle> getWorld = new Template.Method.Converted();
        public final Template.Method<Integer> getLocX = new Template.Method();
        public final Template.Method<Integer> getLocZ = new Template.Method();
        public final Template.Method<List<Integer>> getLoadedSectionCoordinates = new Template.Method();
        public final Template.Method<ChunkSection[]> getSections = new Template.Method();
        public final Template.Method<Object> getSectionRaw = new Template.Method();
        public final Template.Method<ChunkSection> getSection = new Template.Method();
        public final Template.Method<Collection<?>> getRawTileEntities = new Template.Method();
        public final Template.Method.Converted<List<Entity>> getEntities = new Template.Method.Converted();
        public final Template.Method<Chunk> getBukkitChunk = new Template.Method();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted();
        public final Template.Method.Converted<BlockData> getBlockDataAtCoord = new Template.Method.Converted();
        public final Template.Method.Converted<BlockData> setBlockData = new Template.Method.Converted();
        public final Template.Method.Converted<Void> addEntity = new Template.Method.Converted();
        public final Template.Method.Converted<HeightMap> getLightHeightMap = new Template.Method.Converted();
        public final Template.Method.Converted<Integer> getBrightness = new Template.Method.Converted();
        public final Template.Method<Integer> getTopSliceY = new Template.Method();
        public final Template.Method<Void> addEntities = new Template.Method();
        public final Template.Method<Boolean> checkCanSave = new Template.Method();
        public final Template.Method<Void> markDirty = new Template.Method();
        public final Template.Method<Void> markEntitiesDirty = new Template.Method();
    }
}

