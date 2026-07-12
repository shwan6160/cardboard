/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.World$Environment
 *  org.bukkit.WorldBorder
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.BlockState
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.block.BlockPhysicsEvent
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntCuboid;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.block.BlockRayTrace;
import com.bergerkiller.bukkit.common.collections.WorldBlockStateCollection;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.logic.BlockPhysicsEventDataAccessor;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler;
import com.bergerkiller.bukkit.common.internal.logic.PortalHandler;
import com.bergerkiller.bukkit.common.internal.logic.RegionHandler;
import com.bergerkiller.bukkit.common.lighting.LightingHandler;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.ChunkUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.WeatherState;
import com.bergerkiller.generated.net.minecraft.core.BlockPosHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ChunkHolderHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ChunkMapHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.util.RandomSourceHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import com.bergerkiller.generated.org.bukkit.WorldHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockHandle;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftServer;
import java.io.File;
import java.lang.invoke.LambdaMetafactory;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.util.Vector;

public class WorldUtil
extends ChunkUtil {
    private static final Template.Method<Object> getBlockData_raw = (Template.Method)CraftBlockHandle.T.getBlockData.raw;
    private static final WeakHashMap<World, CachedWorldBlockBorder> worldBorderCache = new WeakHashMap();
    public static final int CHUNKS_PER_REGION_AXIS = 32;

    public static boolean hasFeatureFlag(World world, String featureFlagName) {
        return (Boolean)WorldHandle.T.hasFeatureFlag.invoker.invoke(world, featureFlagName);
    }

    public static boolean isLoaded(World world) {
        Object nmsWorldServerHandle = HandleConversion.toWorldHandle(world);
        return (Boolean)ServerLevelHandle.T.isLoaded.invoker.invoke(nmsWorldServerHandle);
    }

    public static BlockData getBlockData(BlockPhysicsEvent event) {
        return BlockPhysicsEventDataAccessor.INSTANCE.get(event);
    }

    public static BlockPhysicsEvent createBlockPhysicsEvent(Block block, BlockData blockData) {
        return BlockPhysicsEventDataAccessor.INSTANCE.createEvent(block, blockData);
    }

    public static BlockData getBlockData(Block block) {
        return BlockData.fromBlockData(WorldUtil.getBlockData_raw.invoker.invoke(block));
    }

    public static BlockData getBlockData(World world, IntVector3 coordinates) {
        return WorldUtil.getBlockData(world, coordinates.x, coordinates.y, coordinates.z);
    }

    public static BlockData getBlockData(World world, int x, int y, int z) {
        return (BlockData)LevelHandle.T.getBlockDataAtCoord.invoker.invoke(HandleConversion.toWorldHandle(world), x, y, z);
    }

    public static Material getBlockType(Block block) {
        return WorldUtil.getBlockData(block).getType();
    }

    public static Material getBlockType(World world, int x, int y, int z) {
        return WorldUtil.getBlockData(world, x, y, z).getType();
    }

    public static void setBlockData(Block block, BlockData data) {
        Object worldHandle = HandleConversion.toWorldHandle(block.getWorld());
        Object blockPosition = BlockPosHandle.T.fromBukkitBlockRaw.invoke(block);
        ((Template.Method)LevelHandle.T.setBlockData.raw).invoke(worldHandle, blockPosition, data.getData(), 3);
    }

    public static void setBlockData(World world, IntVector3 position, BlockData data) {
        Object worldHandle = HandleConversion.toWorldHandle(world);
        Object blockPosition = BlockPosHandle.T.fromIntVector3Raw.invoke(position);
        ((Template.Method)LevelHandle.T.setBlockData.raw).invoke(worldHandle, blockPosition, data.getData(), 3);
    }

    public static void setBlockData(World world, int x, int y, int z, BlockData data) {
        Object worldHandle = HandleConversion.toWorldHandle(world);
        Object blockPosition = ((Template.Constructor)BlockPosHandle.T.constr_x_y_z.raw).newInstance(x, y, z);
        ((Template.Method)LevelHandle.T.setBlockData.raw).invoke(worldHandle, blockPosition, data.getData(), 3);
    }

    public static void setBlockType(Block block, Material type) {
        WorldUtil.setBlockData(block, BlockData.fromMaterial(type));
    }

    public static void setBlockType(World world, int x, int y, int z, Material type) {
        WorldUtil.setBlockData(world, x, y, z, BlockData.fromMaterial(type));
    }

    public static void setBlockDataFast(Block block, BlockData data) {
        ChunkUtil.setBlockFast(block.getChunk(), block, data);
    }

    public static void setBlockDataFast(World world, int x, int y, int z, BlockData data) {
        ChunkUtil.setBlockFast(world.getChunkAt(x >> 4, z >> 4), x, y, z, data);
    }

    public static void setBlockTypeFast(Block block, Material type) {
        WorldUtil.setBlockDataFast(block, BlockData.fromMaterial(type));
    }

    public static void setBlockTypeFast(World world, int x, int y, int z, Material type) {
        WorldUtil.setBlockDataFast(world, x, y, z, BlockData.fromMaterial(type));
    }

    public static RandomSourceHandle getRandom(World world) {
        return CommonNMS.getHandle(world).getRandom();
    }

    @Deprecated
    public static void setKeepSpawnInMemory(World world, boolean value) {
        CommonNMS.getHandle(world).setKeepSpawnInMemoryDuringInit(value);
    }

    public static void removeEntity(Entity entity) {
        EntityAddRemoveHandler.INSTANCE.removeEntity(CommonNMS.getHandle(entity));
    }

    public static Collection<World> getWorlds() {
        return CBCraftServer.worlds.get(Bukkit.getServer()).values();
    }

    public static boolean isChunkEntitiesLoaded(Chunk chunk) {
        return EntityAddRemoveHandler.INSTANCE.isChunkEntitiesLoaded(chunk);
    }

    public static boolean isChunkEntitiesLoaded(World world, int cx, int cz) {
        return EntityAddRemoveHandler.INSTANCE.isChunkEntitiesLoaded(world, cx, cz);
    }

    public static Iterable<Entity> getEntities(World world) {
        return ServerLevelHandle.fromBukkit(world).getEntities();
    }

    public static Collection<Player> getPlayers(World world) {
        return (Collection)DuplexConversion.playerList.convert(((Template.Method)ServerLevelHandle.T.getPlayers.raw).invoke(HandleConversion.toWorldHandle(world)));
    }

    @Deprecated
    public static File getWorldFolder(World world) {
        return WorldUtil.getWorldFolder(world.getName());
    }

    @Deprecated
    public static File getWorldLevelFile(World world) {
        return WorldUtil.getWorldLevelFile(world.getName());
    }

    @Deprecated
    public static boolean isLoadableWorld(String worldName) {
        return Common.SERVER.isLoadableWorld(worldName);
    }

    @Deprecated
    public static File getWorldFolder(String worldName) {
        return Common.SERVER.getWorldFolder(worldName);
    }

    @Deprecated
    public static File getWorldLevelFile(String worldName) {
        return Common.SERVER.getWorldLevelFile(worldName);
    }

    @Deprecated
    public static File getWorldRegionFolder(String worldName) {
        return Common.SERVER.getWorldRegionFolder(worldName);
    }

    @Deprecated
    public static Collection<String> getLoadableWorlds() {
        return Common.SERVER.getLoadableWorldsLegacy();
    }

    @Deprecated
    public static Location findSpawnLocation(Location startLocation) {
        return WorldUtil.findSpawnLocation(startLocation, true);
    }

    @Deprecated
    public static Location findSpawnLocation(Location startLocation, boolean createPortals) {
        Block portal;
        Block startBlock = startLocation.getBlock();
        if (startBlock.getWorld().getEnvironment() == World.Environment.THE_END) {
            portal = WorldUtil.findEndPlatform(startBlock.getWorld());
            if (portal == null && createPortals) {
                portal = WorldUtil.createEndPlatform(startBlock.getWorld(), null);
            }
        } else if (startBlock.getWorld().getEnvironment() == World.Environment.NETHER) {
            portal = WorldUtil.findNetherPortal(startBlock, 16);
            if (portal == null && createPortals) {
                portal = WorldUtil.createNetherPortal(startBlock, BlockFace.SELF, null);
            }
        } else {
            portal = WorldUtil.findNetherPortal(startBlock, 128);
            if (portal == null && createPortals) {
                portal = WorldUtil.createNetherPortal(startBlock, BlockFace.SELF, null);
            }
        }
        if (portal == null) {
            return startLocation.clone();
        }
        return portal.getLocation().add(0.5, 0.0, 0.5);
    }

    public static void markNetherPortal(Block netherPortalBlock) {
        PortalHandler.INSTANCE.markNetherPortal(netherPortalBlock);
    }

    public static int getNetherPortalSearchRadius(World world) {
        return ServerLevelHandle.fromBukkit(world).getNetherPortalSearchRadius();
    }

    public static Block findNetherPortal(Block searchStart, int searchRadius) {
        return PortalHandler.INSTANCE.findNetherPortal(searchStart, searchRadius);
    }

    public static Block createNetherPortal(Block searchStart, BlockFace orientation, Entity initiator) {
        if (searchStart == null) {
            throw new IllegalArgumentException("Start block can not be null");
        }
        return PortalHandler.INSTANCE.createNetherPortal(searchStart, orientation, initiator);
    }

    public static Block findEndPlatform(World world) {
        return PortalHandler.INSTANCE.findEndPlatform(world);
    }

    public static Block createEndPlatform(World world, Entity initiator) {
        return PortalHandler.INSTANCE.createEndPlatform(world, initiator);
    }

    public static boolean isMainEndWorld(World world) {
        return PortalHandler.INSTANCE.isMainEndWorld(world);
    }

    public static DimensionType getDimensionType(World world) {
        return LevelHandle.fromBukkit(world).getDimensionType();
    }

    public static ResourceKey<World> getDimensionKey(World world) {
        return ServerLevelHandle.fromBukkit(world).getDimensionKey();
    }

    public static World getWorldByDimensionKey(ResourceKey<World> dimensionKey) {
        return ServerLevelHandle.getByDimensionKey(dimensionKey);
    }

    public static Server getServer(World world) {
        return LevelHandle.T.getServer.invoke(HandleConversion.toWorldHandle(world));
    }

    public static EntityTracker getTracker(World world) {
        return ServerLevelHandle.T.getEntityTracker.invoke(HandleConversion.toWorldHandle(world));
    }

    public static EntityTrackerEntryHandle getTrackerEntry(Entity entity) {
        return WorldUtil.getTracker(entity.getWorld()).getEntry(entity);
    }

    public static EntityTrackerEntryHandle setTrackerEntry(Entity entity, EntityTrackerEntryHandle entityTrackerEntry) {
        return WorldUtil.getTracker(entity.getWorld()).setEntry(entity, entityTrackerEntry);
    }

    public static List<Entity> getEntities(World world, Entity ignore, double xmin, double ymin, double zmin, double xmax, double ymax, double zmax) {
        Object worldHandle = HandleConversion.toWorldHandle(world);
        Object ignoreHandle = ignore == null ? null : HandleConversion.toEntityHandle(ignore);
        Object axisAlignedBB = ((Template.Constructor)AABBHandle.T.constr_x1_y1_z1_x2_y2_z2.raw).newInstanceVA(xmin, ymin, zmin, xmax, ymax, zmax);
        List entityHandles = (List)((Template.Method)LevelHandle.T.getNearbyEntities.raw).invoke(worldHandle, ignoreHandle, axisAlignedBB);
        return new ConvertingList<Entity>(entityHandles, DuplexConversion.entity);
    }

    public static List<Entity> getNearbyEntities(Entity entity, double radX, double radY, double radZ) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity is null");
        }
        Object worldHandle = HandleConversion.toWorldHandle(entity.getWorld());
        Object entityHandle = HandleConversion.toEntityHandle(entity);
        Object entityBounds = ((Template.Method)EntityHandle.T.getBoundingBox.raw).invoke(entityHandle);
        Object axisAlignedBB = ((Template.Method)AABBHandle.T.grow.raw).invoke(entityBounds, radX, radY, radZ);
        List entityHandles = (List)((Template.Method)LevelHandle.T.getNearbyEntities.raw).invoke(worldHandle, entityHandle, axisAlignedBB);
        return new ConvertingList<Entity>(entityHandles, DuplexConversion.entity);
    }

    public static List<Entity> getNearbyEntities(Location location, double radX, double radY, double radZ) {
        double xmin = location.getX() - radX;
        double ymin = location.getY() - radY;
        double zmin = location.getZ() - radZ;
        double xmax = location.getX() + radX;
        double ymax = location.getY() + radY;
        double zmax = location.getZ() + radZ;
        return WorldUtil.getEntities(location.getWorld(), null, xmin, ymin, zmin, xmax, ymax, zmax);
    }

    public static void saveToDisk(World world) {
        if (CommonCapabilities.ASYNCHRONOUS_CHUNK_LOADER && !CommonUtil.isMainThread()) {
            CompletableFuture future = new CompletableFuture();
            CommonUtil.nextTick(() -> {
                WorldUtil.saveToDisk(world);
                future.complete(null);
            });
            future.join();
        } else {
            if (CommonCapabilities.ASYNCHRONOUS_CHUNK_LOADER && !Bukkit.getWorlds().contains(world)) {
                return;
            }
            CommonNMS.getHandle(world).saveLevel();
        }
    }

    public static void loadChunks(Location location, int radius) {
        WorldUtil.loadChunks(location.getWorld(), location.getX(), location.getZ(), radius);
    }

    public static void loadChunks(World world, double xmid, double zmid, int radius) {
        WorldUtil.loadChunks(world, MathUtil.toChunk(xmid), MathUtil.toChunk(zmid), radius);
    }

    public static void loadChunks(World world, int xmid, int zmid, int radius) {
        for (int cx = xmid - radius; cx <= xmid + radius; ++cx) {
            for (int cz = zmid - radius; cz <= zmid + radius; ++cz) {
                world.getChunkAt(cx, cz);
            }
        }
    }

    public static boolean isLoaded(Location location) {
        return WorldUtil.isLoaded(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static boolean isLoaded(Block block) {
        return WorldUtil.isLoaded(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    public static boolean isLoaded(World world, double x, double y, double z) {
        return WorldUtil.isLoaded(world, MathUtil.toChunk(x), MathUtil.toChunk(z));
    }

    public static boolean isLoaded(World world, int x, int y, int z) {
        return WorldUtil.isLoaded(world, x >> 4, z >> 4);
    }

    public static boolean isLoaded(World world, int chunkX, int chunkZ) {
        return world != null && ((Template.Method)ServerLevelHandle.T.getChunkIfLoaded.raw).invoke(HandleConversion.toWorldHandle(world), chunkX, chunkZ) != null;
    }

    public static boolean areChunksLoaded(World world, int chunkCenterX, int chunkCenterZ, int chunkDistance) {
        return WorldUtil.areBlocksLoaded(world, chunkCenterX << 4, chunkCenterZ << 4, chunkDistance << 4);
    }

    public static boolean areBlocksLoaded(World world, int blockCenterX, int blockCenterZ, int distance) {
        return CommonNMS.getHandle(world).areChunksLoaded(new IntVector3(blockCenterX, 0, blockCenterZ), distance);
    }

    public static boolean queueChunkSendLight(Chunk chunk) {
        return WorldUtil.queueChunkSendLight(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    public static boolean queueChunkSendLight(World world, int chunkX, int chunkZ) {
        ChunkMapHandle playerChunkMap = CommonNMS.getHandle(world).getPlayerChunkMap();
        ChunkHolderHandle playerChunk = playerChunkMap.getVisibleChunk(chunkX, chunkZ);
        return playerChunk != null && playerChunk.resendAllLighting();
    }

    public static boolean queueChunkSend(Chunk chunk) {
        return WorldUtil.queueChunkSend(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    public static boolean queueChunkSend(World world, int chunkX, int chunkZ) {
        ChunkMapHandle playerChunkMap = CommonNMS.getHandle(world).getPlayerChunkMap();
        ChunkHolderHandle playerChunk = playerChunkMap.getVisibleChunk(chunkX, chunkZ);
        return playerChunk != null && playerChunk.resendChunk();
    }

    public static void queueBlockSend(Block block) {
        WorldUtil.queueBlockSend(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    public static void queueBlockSend(World world, int blockX, int blockY, int blockZ) {
        ServerLevelHandle.fromBukkit(world).getChunkProviderServer().markBlockDirty(BlockPosHandle.createNew(blockX, blockY, blockZ));
    }

    @Deprecated
    public static Block rayTraceBlock(World world, double startX, double startY, double startZ, double endX, double endY, double endZ) {
        BlockRayTrace.HitResult hit = BlockRayTrace.between(world, new Vector(startX, startY, startZ), new Vector(endX, endY, endZ)).rayTrace();
        return hit == null ? null : hit.getHitBlock();
    }

    @Deprecated
    public static Block rayTraceBlock(Location startLocation, Vector direction, double maxLength) {
        BlockRayTrace.HitResult hit = BlockRayTrace.fromInto(startLocation.getWorld(), startLocation.toVector(), direction, maxLength).rayTrace();
        return hit == null ? null : hit.getHitBlock();
    }

    @Deprecated
    public static Block rayTraceBlock(Location startLocation, double maxLength) {
        BlockRayTrace.HitResult hit = BlockRayTrace.fromEye(startLocation, maxLength).rayTrace();
        return hit == null ? null : hit.getHitBlock();
    }

    public static Collection<BlockState> getBlockStates(World world) {
        return new WorldBlockStateCollection(WorldUtil.getChunks(world));
    }

    public static WeatherState getWeatherState(World world) {
        if (world.hasStorm()) {
            if (world.isThundering()) {
                return WeatherState.STORM;
            }
            return WeatherState.RAIN;
        }
        return WeatherState.CLEAR;
    }

    public static int getWeatherDuration(World world) {
        if (world.hasStorm()) {
            return Math.min(world.getWeatherDuration(), world.getThunderDuration());
        }
        return world.getWeatherDuration();
    }

    public static void setWeatherState(World world, WeatherState state) {
        WorldHandle.T.setClearWeatherDuration.invoker.invoke(world, 0);
        if (state == WeatherState.CLEAR) {
            if (world.hasStorm()) {
                world.setStorm(false);
                world.setThundering(new Random().nextBoolean());
            }
        } else if (state == WeatherState.RAIN) {
            if (!world.hasStorm() || world.isThundering()) {
                world.setStorm(true);
                world.setThundering(false);
            }
        } else if (!(state != WeatherState.STORM || world.hasStorm() && world.isThundering())) {
            world.setStorm(true);
            world.setThundering(true);
        }
    }

    public static void setWeatherDuration(World world, int durationInTicks) {
        world.setWeatherDuration(durationInTicks);
        world.setThunderDuration(durationInTicks);
    }

    public static WeatherState getFutureWeatherState(World world) {
        int rainDuration = world.getWeatherDuration();
        int stormDuration = world.getThunderDuration();
        WeatherState currentState = WorldUtil.getWeatherState(world);
        if (currentState == WeatherState.CLEAR) {
            boolean storm = world.isThundering();
            if (stormDuration < rainDuration) {
                storm = !storm;
            }
            return storm ? WeatherState.STORM : WeatherState.RAIN;
        }
        if (currentState == WeatherState.RAIN) {
            if (stormDuration < rainDuration) {
                return WeatherState.STORM;
            }
            return WeatherState.CLEAR;
        }
        if (currentState == WeatherState.STORM) {
            if (stormDuration < rainDuration) {
                return WeatherState.RAIN;
            }
            return WeatherState.CLEAR;
        }
        return WeatherState.CLEAR;
    }

    public static void setFutureWeatherState(World world, WeatherState state) {
        WeatherState currentState = state;
        if (currentState == state || WorldUtil.getFutureWeatherState(world) == state) {
            return;
        }
        int rainDuration = world.getWeatherDuration();
        int stormDuration = world.getThunderDuration();
        if (currentState == WeatherState.CLEAR) {
            world.setThundering(!world.isThundering());
            world.setThunderDuration(stormDuration);
        } else if (currentState == WeatherState.RAIN) {
            if (state == WeatherState.CLEAR) {
                world.setThunderDuration(rainDuration + 1);
            } else if (state == WeatherState.STORM) {
                world.setThunderDuration(rainDuration);
                world.setWeatherDuration(rainDuration + new Random().nextInt(12000) + 3600);
            }
        } else if (currentState == WeatherState.STORM) {
            if (state == WeatherState.CLEAR) {
                world.setThunderDuration(rainDuration + 1);
            } else if (state == WeatherState.RAIN) {
                int durationOfRainAfterStorm = new Random().nextInt(12000) + 12000;
                world.setThunderDuration(rainDuration);
                world.setWeatherDuration(rainDuration + durationOfRainAfterStorm);
            }
        }
    }

    public static void playSound(Location location, ResourceKey<SoundEffect> soundKey, float volume, float pitch) {
        WorldHandle.T.playSound.invoke(location.getWorld(), location, soundKey.getName(), Float.valueOf(volume), Float.valueOf(pitch));
    }

    public static Entity getEntityById(World world, int entityId) {
        return EntityAddRemoveHandler.INSTANCE.getEntityById(world, entityId);
    }

    public static void closeWorldStreams(World world) {
        WorldUtil.saveToDisk(world);
        RegionHandler.INSTANCE.closeStreams(world);
    }

    public static int chunkToRegionIndex(int chunkIndex) {
        return chunkIndex >> 5;
    }

    public static int regionToChunkIndex(int regionIndex) {
        return regionIndex << 5;
    }

    @Deprecated
    public static Set<IntVector2> getWorldRegions(World world) {
        return RegionHandler.INSTANCE.getRegions(world);
    }

    public static Set<IntVector3> getWorldRegions3ForXZ(World world, Set<IntVector2> regionXZCoordinates) {
        return RegionHandler.INSTANCE.getRegions3ForXZ(world, regionXZCoordinates);
    }

    public static Set<IntVector3> getWorldRegions3(World world) {
        return RegionHandler.INSTANCE.getRegions3(world);
    }

    @Deprecated
    public static BitSet getWorldSavedRegionChunks(World world, int rx, int rz) {
        return RegionHandler.INSTANCE.getRegionChunks(world, rx, rz);
    }

    public static BitSet getWorldSavedRegionChunks3(World world, int rx, int ry, int rz) {
        return RegionHandler.INSTANCE.getRegionChunks3(world, rx, ry, rz);
    }

    public static int getWorldMinimumHeight(World world) {
        return RegionHandler.INSTANCE.getMinHeight(world);
    }

    public static int getWorldMaximumHeight(World world) {
        return RegionHandler.INSTANCE.getMaxHeight(world);
    }

    public static byte[] getSectionSkyLight(World world, int cx, int cy, int cz) {
        return LightingHandler.instance().getSectionSkyLight(world, cx, cy, cz);
    }

    public static byte[] getSectionBlockLight(World world, int cx, int cy, int cz) {
        return LightingHandler.instance().getSectionBlockLight(world, cx, cy, cz);
    }

    @Deprecated
    public static void setSectionSkyLight(World world, int cx, int cy, int cz, byte[] data) {
        WorldUtil.setSectionSkyLightAsync(world, cx, cy, cz, data);
    }

    @Deprecated
    public static void setSectionBlockLight(World world, int cx, int cy, int cz, byte[] data) {
        WorldUtil.setSectionBlockLightAsync(world, cx, cy, cz, data);
    }

    public static CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        return LightingHandler.instance().setSectionSkyLightAsync(world, cx, cy, cz, data);
    }

    public static CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        return LightingHandler.instance().setSectionBlockLightAsync(world, cx, cy, cz, data);
    }

    public static IntCuboid getBlockBorder(World world) {
        return WorldUtil.worldBorderCache.compute((World)world, (BiFunction<World, CachedWorldBlockBorder, CachedWorldBlockBorder>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;, lambda$getBlockBorder$0(org.bukkit.World org.bukkit.World com.bergerkiller.bukkit.common.utils.WorldUtil$CachedWorldBlockBorder ), (Lorg/bukkit/World;Lcom/bergerkiller/bukkit/common/utils/WorldUtil$CachedWorldBlockBorder;)Lcom/bergerkiller/bukkit/common/utils/WorldUtil$CachedWorldBlockBorder;)((World)world)).border;
    }

    private static /* synthetic */ CachedWorldBlockBorder lambda$getBlockBorder$0(World world, World w, CachedWorldBlockBorder c) {
        if (c == null) {
            return CachedWorldBlockBorder.create(world);
        }
        WorldBorder border = world.getWorldBorder();
        Location center = border.getCenter();
        double size = border.getSize();
        if (c.center.equals((Object)center) && c.size == size) {
            return c;
        }
        return c.withNewBorder(center, size);
    }

    private static final class CachedWorldBlockBorder {
        public final Location center;
        public final double size;
        public final IntCuboid border;

        private CachedWorldBlockBorder(Location center, double size, IntCuboid border) {
            this.center = center;
            this.size = size;
            this.border = border;
        }

        public CachedWorldBlockBorder withNewBorder(Location center, double size) {
            return new CachedWorldBlockBorder(center, size, IntCuboid.createWorldBorder(center, size, this.border.min.y, this.border.max.y));
        }

        public static CachedWorldBlockBorder create(World world) {
            WorldBorder border = world.getWorldBorder();
            Location center = border.getCenter();
            double size = border.getSize();
            return new CachedWorldBlockBorder(center, size, RegionHandler.INSTANCE.createWorldBorder(center, size));
        }
    }
}

