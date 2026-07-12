/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.PlayerRespawnPointNearBlock;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ChunkMapHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerChunkCacheHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.ForcedChunksSavedDataHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import java.util.UUID;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

@Template.InstanceType(value="net.minecraft.server.level.ServerLevel")
public abstract class ServerLevelHandle
extends LevelHandle {
    public static final ServerLevelClass T = Template.Class.create(ServerLevelClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerLevelHandle createHandle(Object handleInstance) {
        return (ServerLevelHandle)T.createHandle(handleInstance);
    }

    public static World getByDimensionKey(ResourceKey<World> key) {
        return ServerLevelHandle.T.getByDimensionKey.invoke(key);
    }

    public abstract ChunkMapHandle getPlayerChunkMap();

    public abstract ServerChunkCacheHandle getChunkProviderServer();

    public abstract boolean isLoaded();

    public abstract List<ServerPlayerHandle> getPlayers();

    public abstract Entity getEntityByUUID(UUID var1);

    public abstract EntityTracker getEntityTracker();

    public abstract Object getEntityTrackerHandle();

    public abstract void setEntityTrackerHandle(Object var1);

    public abstract ResourceKey<World> getDimensionKey();

    public abstract Chunk getChunkIfLoaded(int var1, int var2);

    public abstract void setForceLoadedAsync(int var1, int var2, Plugin var3, boolean var4, int var5);

    public abstract Iterable<Entity> getEntities();

    public abstract void removeEntity(EntityHandle var1);

    public abstract void removeEntityWithoutDeath(EntityHandle var1);

    public abstract boolean addEntity(EntityHandle var1);

    public abstract MinecraftServerHandle getMinecraftServer();

    public abstract void saveLevel();

    public abstract Location findSafeSpawn(PlayerRespawnPointNearBlock var1, boolean var2);

    @Deprecated
    public Location findSafeSpawn(PlayerRespawnPointNearBlock respawnPoint, boolean alsoWhenDestroyed, boolean isDeathRespawn) {
        return this.findSafeSpawn(respawnPoint.withForced(alsoWhenDestroyed), isDeathRespawn);
    }

    public static ServerLevelHandle fromBukkit(World world) {
        return ServerLevelHandle.createHandle(Conversion.toWorldHandle.convert(world));
    }

    public void setChunkProviderServer(ServerChunkCacheHandle chunkProviderServerHandle) {
        if (ServerLevelHandle.T.field_chunkProviderServer.isAvailable()) {
            ServerLevelHandle.T.field_chunkProviderServer.set(this.getRaw(), chunkProviderServerHandle);
        }
        if (LevelHandle.T.field_chunkProvider.isAvailable()) {
            LevelHandle.T.field_chunkProvider.set(this.getRaw(), chunkProviderServerHandle.getRaw());
        }
    }

    public static final class ServerLevelClass
    extends Template.Class<ServerLevelHandle> {
        @Template.Optional
        public final Template.Field.Converted<ServerChunkCacheHandle> field_chunkProviderServer = new Template.Field.Converted();
        public final Template.StaticMethod.Converted<World> getByDimensionKey = new Template.StaticMethod.Converted();
        public final Template.Method.Converted<ChunkMapHandle> getPlayerChunkMap = new Template.Method.Converted();
        public final Template.Method.Converted<ServerChunkCacheHandle> getChunkProviderServer = new Template.Method.Converted();
        public final Template.Method<Boolean> isLoaded = new Template.Method();
        public final Template.Method.Converted<List<ServerPlayerHandle>> getPlayers = new Template.Method.Converted();
        public final Template.Method.Converted<Entity> getEntityByUUID = new Template.Method.Converted();
        public final Template.Method<EntityTracker> getEntityTracker = new Template.Method();
        public final Template.Method<Object> getEntityTrackerHandle = new Template.Method();
        public final Template.Method.Converted<Void> setEntityTrackerHandle = new Template.Method.Converted();
        public final Template.Method.Converted<ResourceKey<World>> getDimensionKey = new Template.Method.Converted();
        public final Template.Method.Converted<Chunk> getChunkIfLoaded = new Template.Method.Converted();
        public final Template.Method<Void> setForceLoadedAsync = new Template.Method();
        @Template.Optional
        public final Template.Method.Converted<ForcedChunksSavedDataHandle> getForcedChunk = new Template.Method.Converted();
        public final Template.Method.Converted<Iterable<Entity>> getEntities = new Template.Method.Converted();
        public final Template.Method.Converted<Void> removeEntity = new Template.Method.Converted();
        public final Template.Method.Converted<Void> removeEntityWithoutDeath = new Template.Method.Converted();
        public final Template.Method.Converted<Boolean> addEntity = new Template.Method.Converted();
        public final Template.Method.Converted<MinecraftServerHandle> getMinecraftServer = new Template.Method.Converted();
        public final Template.Method<Void> saveLevel = new Template.Method();
        public final Template.Method.Converted<Location> findSafeSpawn = new Template.Method.Converted();
    }
}

