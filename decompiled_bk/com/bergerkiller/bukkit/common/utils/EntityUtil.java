/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Hanging
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityPropertyUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributesHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.HangingEntityHandle;
import com.bergerkiller.generated.org.bukkit.entity.EntityHandle;
import com.bergerkiller.generated.org.bukkit.inventory.PlayerInventoryHandle;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class EntityUtil
extends EntityPropertyUtil {
    public static <T extends Entity> T getEntity(World world, UUID uid, Class<T> type) {
        return (T)((Entity)LogicUtil.tryCast(EntityUtil.getEntity(world, uid), type));
    }

    public static Entity getEntity(World world, UUID uuid) {
        return ServerLevelHandle.fromBukkit(world).getEntityByUUID(uuid);
    }

    public static void addEntity(Entity entity) {
        World world = entity.getWorld();
        com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle nmsentity = CommonNMS.getHandle(entity);
        ServerLevelHandle nmsworld = nmsentity.getWorldServer();
        world.getChunkAt(MathUtil.toChunk(nmsentity.getLocX()), MathUtil.toChunk(nmsentity.getLocZ()));
        nmsentity.setDestroyed(false);
        nmsworld.getEntityTracker().stopTracking(entity);
        nmsworld.addEntity(nmsentity);
        EntityAddRemoveHandler.INSTANCE.processEvents(world);
    }

    public static void setSpeed(LivingEntity entity, double speed) {
        LivingEntityHandle nmsEntity = CommonNMS.getHandle(entity);
        nmsEntity.getAttribute(AttributesHandle.MOVEMENT_SPEED).setBaseValue(speed);
    }

    public static double getSpeed(LivingEntity entity) {
        LivingEntityHandle nmsEntity = CommonNMS.getHandle(entity);
        return nmsEntity.getAttribute(AttributesHandle.MOVEMENT_SPEED).getBaseValue();
    }

    @Deprecated
    public static boolean isIgnored(Entity entity) {
        return false;
    }

    public static boolean isNearChunk(Entity entity, int cx, int cz, int chunkview) {
        com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle handle = com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle.fromBukkit(entity);
        int x = MathUtil.toChunk(handle.getLocX()) - cx;
        int z = MathUtil.toChunk(handle.getLocZ()) - cz;
        return Math.abs(x) <= chunkview && Math.abs(z) <= chunkview;
    }

    public static boolean isNearBlock(Entity entity, int bx, int bz, int blockview) {
        com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle handle = com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle.fromBukkit(entity);
        int x = MathUtil.floor(handle.getLocX() - (double)bx);
        int z = MathUtil.floor(handle.getLocZ() - (double)bz);
        return Math.abs(x) <= blockview && Math.abs(z) <= blockview;
    }

    public static void doCollision(Entity entity, Entity with) {
        com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle.fromBukkit(entity).collide(com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle.fromBukkit(with));
    }

    public static void teleportNextTick(Entity entity, Location to) {
        CommonUtil.nextTick(() -> EntityUtil.teleport(entity, to));
    }

    public static boolean teleport(Entity entity, Location to) {
        return CommonEntity.get(entity).teleport(to);
    }

    public static int getUniqueEntityId() {
        return com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle.incrementEntityCounter();
    }

    public static Block getHangingBlock(Hanging entityHanging) {
        IntVector3 pos = HangingEntityHandle.T.getBlockPosition.invoke(HandleConversion.toEntityHandle((Entity)entityHanging));
        return pos.toBlock(entityHanging.getWorld());
    }

    public static DataWatcher getDataWatcher(Entity entity) {
        return com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle.T.getDataWatcher.invoke(HandleConversion.toEntityHandle(entity));
    }

    public static void detectEquipmentChanges(LivingEntity livingEntity) {
        LivingEntityHandle.fromBukkit(livingEntity).detectEquipmentChanges();
    }

    public static ItemStack getEquipment(HumanEntity humanEntity, EquipmentSlot slot) {
        return PlayerInventoryHandle.T.getItem.invoke(humanEntity.getInventory(), slot);
    }

    public static void setEquipment(HumanEntity humanEntity, EquipmentSlot slot, ItemStack item) {
        PlayerInventoryHandle.T.setItem.invoke(humanEntity.getInventory(), slot, item);
    }

    public static boolean isEquipmentSupported(Entity entity, EquipmentSlot slot) {
        return EntityHandle.T.isEquipmentSlotSupported.invoke(entity, slot);
    }
}

