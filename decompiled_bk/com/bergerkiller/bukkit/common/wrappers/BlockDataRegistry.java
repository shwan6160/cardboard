/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.BlockState
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.material.MaterialData
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataSerializer;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataWrapperHook;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockDataImpl;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.generated.org.bukkit.block.BlockStateHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.data.CraftBlockDataHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class BlockDataRegistry {
    public static final BlockData AIR = BlockDataRegistry.fromMaterial(Material.AIR);

    public static BlockData create() {
        return new BlockDataImpl();
    }

    public static BlockData fromBlock(Object block) {
        return BlockDataRegistry.fromBlockData(((Template.Method)BlockHandle.T.getBlockData.raw).invoke(block));
    }

    public static BlockData fromBlockData(Object iBlockData) {
        Object accessor;
        BlockDataWrapperHook hook = BlockDataWrapperHook.INSTANCE;
        if (iBlockData == null) {
            return null;
        }
        try {
            accessor = hook.getAccessor(iBlockData);
            if (accessor instanceof BlockDataWrapperHook.Accessor) {
                return ((BlockDataWrapperHook.Accessor)accessor).bkcGetBlockData();
            }
        }
        catch (Throwable t) {
            BlockDataWrapperHook.disableHook();
            Logging.LOGGER.log(Level.SEVERE, "Failed to read IBlockData accessor field", t);
            return BlockDataImpl.BY_BLOCK_DATA.getOrCreate(iBlockData);
        }
        BlockDataImpl.BlockDataConstant blockData = BlockDataImpl.BY_BLOCK_DATA.getOrCreate(iBlockData);
        try {
            hook.hook(iBlockData, accessor, blockData);
        }
        catch (Throwable t) {
            BlockDataWrapperHook.disableHook();
            Logging.LOGGER.log(Level.SEVERE, "Failed to hook IBlockData accessor", t);
        }
        return blockData;
    }

    public static BlockData fromBlockState(BlockState state) {
        if (BlockStateHandle.T.getBlockData.isAvailable()) {
            return BlockStateHandle.T.getBlockData.invoke(state);
        }
        return BlockDataRegistry.fromMaterialData(state.getType(), state.getRawData());
    }

    public static BlockData fromBukkit(Object bukkitBlockData) {
        return CraftBlockDataHandle.T.getState.invoke(bukkitBlockData);
    }

    public static BlockData fromItemStack(CommonItemStack itemStack) {
        Material type = itemStack.getType();
        if (CommonLegacyMaterials.isLegacy(type)) {
            return BlockDataRegistry.fromMaterialData(type, itemStack.getDamage());
        }
        return BlockDataRegistry.fromMaterial(itemStack.getType());
    }

    public static BlockData fromItemStack(ItemStack itemStack) {
        if (CommonLegacyMaterials.isLegacy(itemStack.getType())) {
            return BlockDataRegistry.fromMaterialData(itemStack.getType(), itemStack.getDurability());
        }
        return BlockDataRegistry.fromMaterial(itemStack.getType());
    }

    public static BlockData fromMaterial(Material material) {
        return BlockDataImpl.BY_MATERIAL.get(material);
    }

    public static BlockData fromMaterialData(MaterialData materialData) {
        return BlockDataRegistry.fromMaterialData(materialData.getItemType(), materialData.getData());
    }

    public static BlockData fromMaterialData(Material type, MaterialData materialData) {
        return BlockDataRegistry.fromMaterialData(type, materialData.getData());
    }

    @Deprecated
    public static BlockData fromMaterialData(Material material, int data) {
        int index = CommonLegacyMaterials.getOrdinal(material);
        return BlockDataImpl.BY_LEGACY_MAT_DATA[index |= data << BlockDataImpl.BY_LEGACY_MAT_DATA_SHIFT];
    }

    @Deprecated
    public static BlockData fromCombinedId_1_8_8(int combinedId) {
        return BlockDataImpl.BY_ID_AND_DATA[combinedId & BlockDataImpl.REGISTRY_MASK];
    }

    @Deprecated
    public static BlockData fromCombinedId(int combinedId) {
        return BlockDataRegistry.fromBlockData(((Template.StaticMethod)BlockHandle.T.getByCombinedId.raw).invoke(combinedId));
    }

    public static BlockData fromString(String serializedString) {
        return BlockDataSerializer.INSTANCE.deserialize(serializedString);
    }

    public static Collection<BlockData> values() {
        return BlockDataImpl.BY_BLOCK_DATA.getAll();
    }
}

