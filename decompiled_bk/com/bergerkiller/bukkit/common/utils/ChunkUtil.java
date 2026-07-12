/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.chunk.ForcedChunk;
import com.bergerkiller.bukkit.common.collections.FilteredCollection;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.logic.RegionHandler;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChunkSection;
import com.bergerkiller.bukkit.common.wrappers.HeightMap;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LightLayerHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.LevelChunkHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.LevelChunkSectionHandle;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ChunkUtil {
    public static ForcedChunk forceChunkLoaded(World world, int chunkX, int chunkZ) {
        return CommonPlugin.getInstance().getForcedChunkManager().newForcedChunk(world, chunkX, chunkZ);
    }

    public static ForcedChunk forceChunkLoaded(Chunk chunk) {
        return ChunkUtil.forceChunkLoaded(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    public static ForcedChunk forceChunkLoaded(World world, int chunkX, int chunkZ, int radius) {
        return CommonPlugin.getInstance().getForcedChunkManager().newForcedChunk(world, chunkX, chunkZ, radius);
    }

    public static ForcedChunk forceChunkLoaded(Chunk chunk, int radius) {
        return ChunkUtil.forceChunkLoaded(chunk.getWorld(), chunk.getX(), chunk.getZ(), radius);
    }

    @Deprecated
    public static ChunkSection[] getSections(Chunk chunk) {
        return LevelChunkHandle.T.getSections.invoke(HandleConversion.toChunkHandle(chunk));
    }

    public static List<Integer> getLoadedSectionCoordinates(Chunk chunk) {
        return LevelChunkHandle.T.getLoadedSectionCoordinates.invoke(HandleConversion.toChunkHandle(chunk));
    }

    public static ChunkSection getSection(Chunk chunk, int cy) {
        return LevelChunkHandle.T.getSection.invoke(HandleConversion.toChunkHandle(chunk), cy);
    }

    public static HeightMap getLightHeightMap(Chunk chunk) {
        return ChunkUtil.getLightHeightMap(chunk, false);
    }

    public static HeightMap getLightHeightMap(Chunk chunk, boolean initialize) {
        return LevelChunkHandle.fromBukkit(chunk).getLightHeightMap(initialize);
    }

    public static int getBlockLight(Chunk chunk, int x, int y, int z) {
        if (y < 0) {
            return 0;
        }
        if (y >= chunk.getWorld().getMaxHeight()) {
            return 0;
        }
        return LevelChunkHandle.fromBukkit(chunk).getBrightness(LightLayerHandle.BLOCK, new IntVector3(x & 0xF, y, z & 0xF));
    }

    public static int getSkyLight(Chunk chunk, int x, int y, int z) {
        if (y < 0) {
            return 0;
        }
        if (y >= chunk.getWorld().getMaxHeight()) {
            return 15;
        }
        return LevelChunkHandle.fromBukkit(chunk).getBrightness(LightLayerHandle.SKY, new IntVector3(x & 0xF, y, z & 0xF));
    }

    public static Material getBlockType(Chunk chunk, int x, int y, int z) {
        return ChunkUtil.getBlockData(chunk, x, y, z).getType();
    }

    public static BlockData getBlockData(Chunk chunk, int x, int y, int z) {
        return LevelChunkHandle.T.getBlockDataAtCoord.invoke(HandleConversion.toChunkHandle(chunk), x, y, z);
    }

    public static void setBlockFast(Chunk chunk, Block block, BlockData data) {
        int secIndex = block.getY() >> 4;
        Object section = LevelChunkHandle.T.getSectionRaw.invoke(HandleConversion.toChunkHandle(chunk), secIndex);
        if (section != null) {
            LevelChunkSectionHandle.T.setBlockDataAtBlock.invoke(section, block, data);
        } else {
            WorldUtil.setBlockData(block, data);
        }
    }

    public static void setBlockFast(Chunk chunk, int x, int y, int z, BlockData data) {
        int secIndex = y >> 4;
        Object chunkHandle = HandleConversion.toChunkHandle(chunk);
        Object section = LevelChunkHandle.T.getSectionRaw.invoke(chunkHandle, secIndex);
        if (section != null) {
            LevelChunkSectionHandle.T.setBlockData.invoke(section, x & 0xF, y & 0xF, z & 0xF, data);
            LevelChunkHandle.T.markDirty.invoker.invoke(chunkHandle);
        } else {
            WorldUtil.setBlockData(chunk.getWorld(), chunk.getX() << 4 | x & 0xF, y, chunk.getZ() << 4 | z & 0xF, data);
        }
    }

    public static List<Entity> getEntities(Chunk chunk) {
        return LevelChunkHandle.fromBukkit(chunk).getEntities();
    }

    public static Collection<Player> getChunkViewers(Chunk chunk) {
        return ChunkUtil.getChunkViewers(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    public static Collection<Player> getChunkViewers(World world, int chunkX, int chunkZ) {
        return ServerLevelHandle.fromBukkit(world).getPlayerChunkMap().getChunkEnteredPlayers(chunkX, chunkZ);
    }

    @Deprecated
    public static boolean isLoadRequested(Player player, int cx, int cz) {
        throw new RuntimeException("BROKEN");
    }

    public static boolean isChunkAvailable(World world, int x, int z) {
        if (WorldUtil.isLoaded(world, x, z)) {
            return true;
        }
        return RegionHandler.INSTANCE.isChunkSaved(world, x, z);
    }

    public static Collection<Chunk> getChunks(World world) {
        return FilteredCollection.createNullFilter(Arrays.asList(world.getLoadedChunks()));
    }

    public static Chunk getChunk(World world, int x, int z) {
        return ServerLevelHandle.T.getChunkIfLoaded.invoke(HandleConversion.toWorldHandle(world), x, z);
    }

    public static CompletableFuture<Chunk> getChunkAsync(World world, int x, int z) {
        CompletableFuture<Chunk> result = new CompletableFuture<Chunk>();
        ForcedChunk forced = WorldUtil.forceChunkLoaded(world, x, z);
        forced.getChunkAsync().whenComplete((chunk, exception) -> {
            try {
                if (exception != null) {
                    result.completeExceptionally((Throwable)exception);
                } else {
                    result.complete((Chunk)chunk);
                }
            }
            finally {
                forced.close();
            }
        });
        return result;
    }

    @Deprecated
    public static void getChunkAsync(World world, int x, int z, Runnable runnable) {
        ChunkUtil.getChunkAsync(world, x, z).thenRun(runnable);
    }

    public static void saveChunk(Chunk chunk) {
        CommonNMS.getHandle(chunk.getWorld()).getChunkProviderServer().saveLoadedChunk(CommonNMS.getHandle(chunk));
    }

    public static boolean needsSaving(Chunk chunk) {
        return LevelChunkHandle.T.checkCanSave.invoke(HandleConversion.toChunkHandle(chunk));
    }

    public static Collection<BlockState> getBlockStates(Chunk chunk) {
        return LevelChunkHandle.fromBukkit(chunk).getTileEntities();
    }
}

