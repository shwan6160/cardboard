/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.PlayerRespawnPoint;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import java.lang.ref.WeakReference;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerRespawnPointNearBlock
extends PlayerRespawnPoint {
    private final ServerPlayerHandle.RespawnConfigHandle handle;
    private WeakReference<World> cachedWorld = LogicUtil.nullWeakReference();

    public PlayerRespawnPointNearBlock(ResourceKey<World> dimensionKey, int blockX, int blockY, int blockZ, float angle, boolean forced) {
        this(ServerPlayerHandle.RespawnConfigHandle.of(dimensionKey, null, new IntVector3(blockX, blockY, blockZ), angle, forced));
    }

    public PlayerRespawnPointNearBlock(World world, int blockX, int blockY, int blockZ, float angle, boolean forced) {
        this(ServerPlayerHandle.RespawnConfigHandle.of(world, new IntVector3(blockX, blockY, blockZ), angle, forced));
    }

    public PlayerRespawnPointNearBlock(ServerPlayerHandle.RespawnConfigHandle respawnConfig) {
        if (respawnConfig == null) {
            throw new IllegalArgumentException("RespawnConfig handle cannot be null");
        }
        this.handle = respawnConfig;
    }

    public ServerPlayerHandle.RespawnConfigHandle getHandle() {
        return this.handle;
    }

    public IntVector3 getBlockPosition() {
        return this.handle.position();
    }

    @Override
    public World getWorld() {
        World world = (World)this.cachedWorld.get();
        if (world == null && (world = this.handle.world()) != null) {
            this.cachedWorld = new WeakReference<World>(world);
        }
        return world;
    }

    @Override
    public float getYaw() {
        return this.handle.yaw();
    }

    @Override
    public float getPitch() {
        return this.handle.pitch();
    }

    public Block getBlock() {
        World world = this.getWorld();
        return world == null ? null : this.getBlockPosition().toBlock(world);
    }

    public int getBlockX() {
        return this.getBlockPosition().x;
    }

    public int getBlockY() {
        return this.getBlockPosition().y;
    }

    public int getBlockZ() {
        return this.getBlockPosition().z;
    }

    public boolean isForced() {
        return this.handle.forced();
    }

    public PlayerRespawnPointNearBlock withForced(boolean forced) {
        return new PlayerRespawnPointNearBlock(ServerPlayerHandle.RespawnConfigHandle.of(this.handle.dimension(), this.handle.worldName(), this.handle.position(), this.handle.yaw(), this.handle.pitch(), forced));
    }

    @Override
    public void applyToPlayer(Player player) {
        ServerPlayerHandle.fromBukkit(player).setRespawnConfigSilent(this.handle);
    }

    @Override
    public void toNBT(CommonTagCompound nbt) {
        if (CommonCapabilities.IS_RESPAWN_POINT_PACKED) {
            nbt.remove("SpawnWorld");
            nbt.remove("SpawnDimension");
            nbt.remove("SpawnX");
            nbt.remove("SpawnY");
            nbt.remove("SpawnZ");
            nbt.remove("SpawnAngle");
            nbt.remove("SpawnForced");
            ServerPlayerHandle.RespawnConfigHandle.codecToNBT(this.handle, nbt);
        } else {
            if (CommonCapabilities.PLAYER_SPAWN_WORLD_IS_DIMENSION_KEY) {
                ResourceKey<World> dimension = this.handle.dimension();
                if (dimension == null) {
                    PlayerRespawnPoint.NONE.toNBT(nbt);
                    return;
                }
                nbt.putMinecraftKey("SpawnDimension", dimension.getName());
                nbt.remove("SpawnWorld");
            } else {
                String worldName = this.handle.worldName();
                if (worldName == null) {
                    World world = this.getWorld();
                    if (world == null) {
                        PlayerRespawnPoint.NONE.toNBT(nbt);
                        return;
                    }
                    worldName = world.getName();
                }
                nbt.putValue("SpawnWorld", worldName);
                nbt.remove("SpawnDimension");
            }
            IntVector3 blockPos = this.getBlockPosition();
            nbt.putValue("SpawnX", blockPos.x);
            nbt.putValue("SpawnY", blockPos.y);
            nbt.putValue("SpawnZ", blockPos.z);
            if (CommonCapabilities.PLAYER_SPAWN_HAS_ANGLE) {
                nbt.putValue("SpawnAngle", Float.valueOf(this.getAngle()));
            }
            nbt.putValue("SpawnForced", this.isForced());
        }
    }

    @Override
    public Location findSafeSpawn(boolean alsoWhenDestroyed, boolean isDeathRespawn) {
        World world = this.getWorld();
        if (world == null) {
            return null;
        }
        return ServerLevelHandle.fromBukkit(world).findSafeSpawn(this, alsoWhenDestroyed || this.isForced(), isDeathRespawn);
    }

    public String toString() {
        IntVector3 blockPos = this.getBlockPosition();
        World world = this.getWorld();
        return "PlayerRespawnPointNearBlock{" + (world == null ? "dimension=" + this.handle.dimension() : "world=" + world.getName()) + ", x=" + blockPos.x + ", y=" + blockPos.y + ", z=" + blockPos.z + ", angle=" + this.getAngle() + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof PlayerRespawnPointNearBlock) {
            return ((PlayerRespawnPointNearBlock)o).handle.equals(this.handle);
        }
        return false;
    }
}

