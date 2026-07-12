/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockState
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.inventory.EntityEquipment
 *  org.bukkit.inventory.Inventory
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.logic.PlayerFileDataHandler;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectInstanceHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.InventoryHandle;
import com.bergerkiller.generated.net.minecraft.world.food.FoodDataHandle;
import com.bergerkiller.generated.net.minecraft.world.inventory.PlayerEnderChestContainerHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.BlockEntityHandle;
import com.bergerkiller.generated.org.bukkit.block.BlockStateHandle;
import java.util.Iterator;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;

public class NBTUtil {
    public static MobEffectInstanceHandle loadMobEffect(CommonTagCompound compound) {
        return MobEffectInstanceHandle.fromNBT(compound);
    }

    public static CommonTagCompound saveEntity(Entity entity, CommonTagCompound compound) {
        if (compound == null) {
            compound = new CommonTagCompound();
        }
        EntityHandle.fromBukkit(entity).saveToNBT(compound);
        return compound;
    }

    public static void loadEntity(Entity entity, CommonTagCompound compound) {
        EntityHandle.fromBukkit(entity).loadFromNBT(compound);
    }

    public static CommonTagCompound migratePlayerProfileData(CommonTagCompound playerProfileData) {
        return PlayerFileDataHandler.INSTANCE.migratePlayerData(playerProfileData);
    }

    public static CommonTagCompound saveFoodMetaData(Object foodMetaData, CommonTagCompound compound) {
        if (compound == null) {
            compound = new CommonTagCompound();
        }
        FoodDataHandle.createHandle(foodMetaData).saveToNBT(compound);
        return compound;
    }

    public static void loadFoodMetaData(Object foodMetaData, CommonTagCompound compound) {
        FoodDataHandle.createHandle(foodMetaData).loadFromNBT(compound);
    }

    public static CommonTagList saveInventory(Inventory inventory, CommonTagList list) {
        Object inventoryHandle = Conversion.toInventoryHandle.convert(inventory);
        if (inventoryHandle == null) {
            throw new IllegalArgumentException("This kind of inventory lacks a handle to load");
        }
        if (InventoryHandle.T.isAssignableFrom(inventoryHandle)) {
            if (list == null) {
                list = new CommonTagList();
            }
        } else {
            if (PlayerEnderChestContainerHandle.T.isAssignableFrom(inventoryHandle)) {
                CommonTagList listSaved = PlayerEnderChestContainerHandle.createHandle(inventoryHandle).saveToNBT();
                if (listSaved != null) {
                    Iterator iterator = listSaved.getData().iterator();
                    while (iterator.hasNext()) {
                        CommonTag savedItem = (CommonTag)iterator.next();
                        list.add(savedItem);
                    }
                }
                return list;
            }
            throw new IllegalArgumentException("This kind of inventory has an unknown type of handle: " + inventoryHandle.getClass().getName());
        }
        InventoryHandle.createHandle(inventoryHandle).saveToNBT(list);
        return list;
    }

    public static void loadInventory(Inventory inventory, CommonTagList list) {
        Object inventoryHandle = Conversion.toInventoryHandle.convert(inventory);
        if (inventoryHandle == null) {
            throw new IllegalArgumentException("This kind of inventory lacks a handle to save");
        }
        if (InventoryHandle.T.isAssignableFrom(inventoryHandle)) {
            InventoryHandle.createHandle(inventoryHandle).loadFromNBT(list);
        } else if (PlayerEnderChestContainerHandle.T.isAssignableFrom(inventoryHandle)) {
            PlayerEnderChestContainerHandle.createHandle(inventoryHandle).loadFromNBT(list);
        } else {
            throw new IllegalArgumentException("This kind of inventory has an unknown type of handle: " + inventoryHandle.getClass().getName());
        }
    }

    public static CommonTagCompound saveEquipment(EntityEquipment equipment) {
        if (equipment == null) {
            return CommonTagCompound.EMPTY;
        }
        Entity holder = equipment.getHolder();
        if (!(holder instanceof LivingEntity)) {
            throw new UnsupportedOperationException("Cannot save equipment of a non-living entity: " + holder);
        }
        LivingEntityHandle handle = LivingEntityHandle.fromBukkit((LivingEntity)holder);
        CommonTagCompound nbt = handle.saveEquipment();
        if (nbt == null) {
            nbt = CommonTagCompound.EMPTY;
        }
        return nbt;
    }

    public static void loadEquipment(EntityEquipment equipment, CommonTagCompound data) {
        if (equipment == null) {
            return;
        }
        Entity holder = equipment.getHolder();
        if (!(holder instanceof LivingEntity)) {
            throw new UnsupportedOperationException("Cannot load equipment for a non-living entity: " + holder);
        }
        if (data == null) {
            data = CommonTagCompound.EMPTY;
        }
        LivingEntityHandle handle = LivingEntityHandle.fromBukkit((LivingEntity)holder);
        handle.loadEquipment(data);
    }

    public static void resetAttributes(LivingEntity livingEntity) {
        LivingEntityHandle.fromBukkit(livingEntity).resetAttributes();
    }

    public static void loadAttributes(LivingEntity livingEntity, CommonTagList data) {
        if (data == null) {
            throw new IllegalArgumentException("Data can not be null");
        }
        LivingEntityHandle.fromBukkit(livingEntity).getAttributeMap().loadFromNBT(data);
    }

    public static CommonTagList saveAttributes(LivingEntity livingEntity) {
        return LivingEntityHandle.fromBukkit(livingEntity).getAttributeMap().saveToNBT();
    }

    public static void loadBlockState(BlockState blockState, CommonTagCompound data) {
        BlockEntityHandle.fromBukkit(blockState).load(BlockStateHandle.T.getBlockData.invoke(blockState), data);
    }

    public static CommonTagCompound saveBlockState(BlockState blockState) {
        return BlockEntityHandle.fromBukkit(blockState).save();
    }

    @Deprecated
    public static CommonTagCompound saveBlockState(BlockState blockState, CommonTagCompound data) {
        CommonTagCompound savedData = BlockEntityHandle.fromBukkit(blockState).save();
        if (data != null) {
            data.putAll(savedData);
            return data;
        }
        return savedData;
    }
}

