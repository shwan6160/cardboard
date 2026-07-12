/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.ResourceCategory;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.PlayerRespawnPointNearBlock;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public abstract class PlayerRespawnPoint {
    public static final PlayerRespawnPoint NONE = new PlayerRespawnPoint(){

        @Override
        public boolean isNone() {
            return true;
        }

        @Override
        public World getWorld() {
            return null;
        }

        @Override
        public float getYaw() {
            return 0.0f;
        }

        @Override
        public float getPitch() {
            return 0.0f;
        }

        @Override
        public void toNBT(CommonTagCompound nbt) {
            nbt.removeValue("respawn");
            nbt.removeValue("SpawnWorld");
            nbt.removeValue("SpawnDimension");
            nbt.removeValue("SpawnForced");
            nbt.removeValue("SpawnX");
            nbt.removeValue("SpawnY");
            nbt.removeValue("SpawnZ");
            nbt.removeValue("SpawnAngle");
        }

        @Override
        public void applyToPlayer(Player player) {
            ServerPlayerHandle.fromBukkit(player).setRespawnConfigSilent(null);
        }

        @Override
        public Location findSafeSpawn() {
            return null;
        }

        @Override
        public Location findSafeSpawn(boolean alsoWhenDestroyed, boolean isDeathRespawn) {
            return null;
        }

        public String toString() {
            return "NONE";
        }

        public boolean equals(Object o) {
            return this == o;
        }
    };

    public static PlayerRespawnPoint create(Block block) {
        return PlayerRespawnPoint.create(block, 0.0f);
    }

    public static PlayerRespawnPoint create(Block block, float angle) {
        return PlayerRespawnPoint.create(block, angle, false);
    }

    public static PlayerRespawnPoint create(Block block, float angle, boolean forced) {
        if (block == null) {
            return NONE;
        }
        return new PlayerRespawnPointNearBlock(block.getWorld(), block.getX(), block.getY(), block.getZ(), angle, forced);
    }

    public static PlayerRespawnPoint create(World world, int x, int y, int z) {
        return PlayerRespawnPoint.create(world, x, y, z, 0.0f);
    }

    public static PlayerRespawnPoint create(World world, int x, int y, int z, float angle) {
        return PlayerRespawnPoint.create(world, x, y, z, angle, false);
    }

    public static PlayerRespawnPoint create(World world, int x, int y, int z, float angle, boolean forced) {
        if (world == null) {
            return NONE;
        }
        return new PlayerRespawnPointNearBlock(world, x, y, z, angle, forced);
    }

    public static PlayerRespawnPoint create(ResourceKey<World> dimensionKey, int x, int y, int z, float angle, boolean forced) {
        if (dimensionKey == null) {
            return NONE;
        }
        return new PlayerRespawnPointNearBlock(dimensionKey, x, y, z, angle, forced);
    }

    public boolean isNone() {
        return false;
    }

    public abstract World getWorld();

    @Deprecated
    public final float getAngle() {
        return this.getYaw();
    }

    public abstract float getYaw();

    public abstract float getPitch();

    public abstract void toNBT(CommonTagCompound var1);

    public abstract void applyToPlayer(Player var1);

    public Location findSafeSpawn() {
        return this.findSafeSpawn(false, false);
    }

    public abstract Location findSafeSpawn(boolean var1, boolean var2);

    public static PlayerRespawnPoint fromNBT(CommonTagCompound nbt) {
        IdentifierHandle spawnDimensionName;
        ServerPlayerHandle.RespawnConfigHandle respawnConfig;
        World world = null;
        if (CommonCapabilities.IS_RESPAWN_POINT_PACKED && (respawnConfig = ServerPlayerHandle.RespawnConfigHandle.codecFromNBT(nbt)) != null) {
            return new PlayerRespawnPointNearBlock(respawnConfig);
        }
        if (CommonCapabilities.PLAYER_SPAWN_WORLD_IS_DIMENSION_KEY && (spawnDimensionName = nbt.getMinecraftKey("SpawnDimension")) != null) {
            world = WorldUtil.getWorldByDimensionKey(ResourceCategory.dimension.createKey(spawnDimensionName));
        }
        if (world == null && nbt.containsKey("SpawnWorld")) {
            world = Bukkit.getWorld((String)((String)((Object)nbt.getValue("SpawnWorld", String.class))));
        }
        if (!(world != null && nbt.containsKey("SpawnX") && nbt.containsKey("SpawnY") && nbt.containsKey("SpawnZ"))) {
            return NONE;
        }
        int x = nbt.getValue("SpawnX", 0);
        int y = nbt.getValue("SpawnY", 0);
        int z = nbt.getValue("SpawnZ", 0);
        float angle = nbt.getValue("SpawnAngle", Float.valueOf(0.0f)).floatValue();
        return PlayerRespawnPoint.create(world, x, y, z, angle);
    }

    public static PlayerRespawnPoint forPlayer(Player player) {
        ServerPlayerHandle handle = ServerPlayerHandle.fromBukkit(player);
        ServerPlayerHandle.RespawnConfigHandle respawnConfig = handle.getRespawnConfig();
        if (respawnConfig != null) {
            return new PlayerRespawnPointNearBlock(respawnConfig);
        }
        return NONE;
    }
}

