/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.BlockFace
 *  org.bukkit.material.MaterialData
 *  org.bukkit.material.Sign
 */
package com.bergerkiller.bukkit.common.internal.legacy;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.legacy.IBlockDataToMaterialData;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlocksHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.BlockStateHandle;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Sign;

public class MaterialDataToIBlockData {
    private static final FastMethod<Object> craftBukkitgetIBlockData = new FastMethod();
    private static final Map<Material, IBlockDataBuilder<?>> iblockdataBuilders = new EnumMap(Material.class);

    private static void initBuilders() {
        if (MaterialsByName.getLegacyMaterial("REDSTONE_COMPARATOR_OFF") != null) {
            iblockdataBuilders.put(MaterialsByName.getLegacyMaterial("REDSTONE_COMPARATOR_OFF"), (iblockdata, comparator) -> {
                iblockdata = iblockdata.set("powered", (Object)false);
                iblockdata = iblockdata.set("facing", (Object)comparator.getFacing());
                iblockdata = iblockdata.set("mode", (Object)(comparator.isSubtractionMode() ? "subtract" : "compare"));
                return iblockdata;
            });
        }
        if (MaterialsByName.getLegacyMaterial("REDSTONE_COMPARATOR_ON") != null) {
            iblockdataBuilders.put(MaterialsByName.getLegacyMaterial("REDSTONE_COMPARATOR_ON"), (iblockdata, comparator) -> {
                iblockdata = iblockdata.set("powered", (Object)true);
                iblockdata = iblockdata.set("facing", (Object)comparator.getFacing());
                iblockdata = iblockdata.set("mode", (Object)(comparator.isSubtractionMode() ? "subtract" : "compare"));
                return iblockdata;
            });
        }
        iblockdataBuilders.put(MaterialsByName.getLegacyMaterial("DOUBLE_STEP"), (iblockdata, step) -> iblockdata.set("type", (Object)"double"));
        iblockdataBuilders.put(MaterialsByName.getLegacyMaterial("MOB_SPAWNER"), (iblockdata, spawner) -> MaterialsByName.getBlockDataFromMaterialName("SPAWNER"));
        iblockdataBuilders.put(MaterialsByName.getLegacyMaterial("TORCH"), (iblockdata, torch) -> {
            if (torch.getAttachedFace() == BlockFace.DOWN) {
                iblockdata = MaterialsByName.getBlockDataFromMaterialName("TORCH");
                return iblockdata;
            }
            iblockdata = MaterialsByName.getBlockDataFromMaterialName("WALL_TORCH");
            return iblockdata.set("facing", (Object)torch.getFacing());
        });
        MaterialDataToIBlockData.storeLegacyRemap("MELON_BLOCK", "MELON");
        IBlockDataBuilder<Sign> builder = new IBlockDataBuilder<Sign>(){
            final BlockStateHandle wall_sign_data = MaterialsByName.getBlockDataFromMaterialName(CommonCapabilities.HAS_MATERIAL_SIGN_TYPES ? "OAK_WALL_SIGN" : "WALL_SIGN");
            final BlockStateHandle sign_post_data = MaterialsByName.getBlockDataFromMaterialName(CommonCapabilities.HAS_MATERIAL_SIGN_TYPES ? "OAK_SIGN" : "SIGN");

            @Override
            public BlockStateHandle create(BlockStateHandle iblockdata, Sign sign) {
                if (CommonLegacyMaterials.isLegacy(sign.getItemType())) {
                    BlockStateHandle blockStateHandle = iblockdata = sign.isWallSign() ? this.wall_sign_data : this.sign_post_data;
                }
                if (sign.isWallSign()) {
                    return iblockdata.set("facing", (Object)sign.getFacing());
                }
                return iblockdata.set("rotation", (Object)sign.getData());
            }
        };
        iblockdataBuilders.put(MaterialsByName.getLegacyMaterial("WALL_SIGN"), builder);
        iblockdataBuilders.put(MaterialsByName.getLegacyMaterial("SIGN_POST"), builder);
        Material[] legacy_types = MaterialsByName.getAllByName("LEGACY_CHEST", "LEGACY_ENDER_CHEST", "LEGACY_TRAPPED_CHEST");
        String[] modern_names = new String[]{"CHEST", "ENDER_CHEST", "TRAPPED_CHEST"};
        for (int n = 0; n < legacy_types.length; ++n) {
            Material legacy_type = legacy_types[n];
            BlockStateHandle modern_data = MaterialsByName.getBlockDataFromMaterialName(modern_names[n]);
            iblockdataBuilders.put(legacy_type, (iblockdata, directional) -> modern_data.set("facing", (Object)directional.getFacing()));
        }
    }

    private static void storeLegacyRemap(String legacy_name, String modern_name) {
        BlockStateHandle modern_data = MaterialsByName.getBlockDataFromMaterialName(modern_name);
        iblockdataBuilders.put(MaterialsByName.getLegacyMaterial(legacy_name), (iblockdata, materialData) -> modern_data);
    }

    public static BlockStateHandle getIBlockData(MaterialData materialdata) {
        if (materialdata == null) {
            throw new IllegalArgumentException("MaterialData == null");
        }
        if (materialdata.getItemType() == null) {
            throw new IllegalArgumentException("MaterialData getItemType() == null");
        }
        Material legacyType = IBlockDataToMaterialData.toLegacy(materialdata.getItemType());
        IBlockDataBuilder builder = (IBlockDataBuilder)LogicUtil.unsafeCast(iblockdataBuilders.get(legacyType));
        try {
            BlockStateHandle blockData = BlockStateHandle.createHandle(craftBukkitgetIBlockData.invoke(null, materialdata));
            if (builder != null) {
                materialdata = IBlockDataToMaterialData.createMaterialData(materialdata.getItemType(), legacyType, materialdata.getData());
                blockData = builder.create(blockData, materialdata);
            }
            return blockData;
        }
        catch (Throwable t) {
            Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Failed to retrieve IBlockData for " + materialdata, t);
            return BlockHandle.T.getBlockData.invoke(BlocksHandle.AIR);
        }
    }

    static {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClass(CommonUtil.getClass("org.bukkit.craftbukkit.util.CraftMagicNumbers"));
        resolver.setAllVariables(Common.TEMPLATE_RESOLVER);
        if (Common.evaluateMCVersion(">=", "1.18")) {
            craftBukkitgetIBlockData.init(new MethodDeclaration(resolver, SourceDeclaration.preprocess("public static net.minecraft.world.level.block.state.BlockState getIBlockData(org.bukkit.material.MaterialData materialdata) {\n    org.bukkit.Material legacy_type = org.bukkit.craftbukkit.legacy.CraftLegacy.toLegacy(materialdata.getItemType());\n    net.minecraft.world.level.block.state.BlockState result = org.bukkit.craftbukkit.legacy.CraftLegacy.fromLegacyData(legacy_type, materialdata.getData());\n    if (result == net.minecraft.world.level.block.Blocks.AIR.defaultBlockState()) {\n        org.bukkit.Material type = org.bukkit.craftbukkit.legacy.CraftLegacy.fromLegacy(materialdata.getItemType());\n        net.minecraft.world.level.block.Block block = CraftMagicNumbers.getBlock(type);\n        if (block != null) {\n            result = block.defaultBlockState();\n        }\n    }\n    return result;\n}")));
        } else if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            craftBukkitgetIBlockData.init(new MethodDeclaration(resolver, SourceDeclaration.preprocess("public static net.minecraft.world.level.block.state.BlockState getIBlockData(org.bukkit.material.MaterialData materialdata) {\n    org.bukkit.Material legacy_type = org.bukkit.craftbukkit.legacy.CraftLegacy.toLegacy(materialdata.getItemType());\n#if exists org.bukkit.craftbukkit.legacy.CraftLegacy public static net.minecraft.world.level.block.state.BlockState fromLegacyData(org.bukkit.Material material, byte data);\n    net.minecraft.world.level.block.state.BlockState result = org.bukkit.craftbukkit.legacy.CraftLegacy.fromLegacyData(legacy_type, materialdata.getData());\n    if (result == net.minecraft.world.level.block.Blocks.AIR.getBlockData()) {\n        org.bukkit.Material type = org.bukkit.craftbukkit.legacy.CraftLegacy.fromLegacy(materialdata.getItemType());\n        net.minecraft.world.level.block.Block block = CraftMagicNumbers.getBlock(type);\n        if (block != null) {\n            result = block.getBlockData();\n        }\n    }\n    return result;\n#else\n    org.bukkit.Material type = org.bukkit.craftbukkit.legacy.CraftLegacy.fromLegacy(materialdata.getItemType());\n    net.minecraft.world.level.block.Block block = CraftMagicNumbers.getBlock(type);\n    if (block == null) {\n        return net.minecraft.world.level.block.Blocks.AIR.getBlockData();\n    }\n    return org.bukkit.craftbukkit.legacy.CraftLegacy.fromLegacyData(legacy_type, block, materialdata.getData());\n#endif\n}")));
        } else {
            craftBukkitgetIBlockData.init(new MethodDeclaration(resolver, "public static net.minecraft.world.level.block.state.BlockState getIBlockData(org.bukkit.material.MaterialData materialdata) {\n    #require net.minecraft.world.level.block.Block public net.minecraft.world.level.block.state.BlockState fromLegacyData(int legacyData);\n    #require net.minecraft.world.level.block.Block public net.minecraft.world.level.block.state.BlockState getBlockData();\n    net.minecraft.world.level.block.Block block = CraftMagicNumbers.getBlock(materialdata.getItemType());\n    net.minecraft.world.level.block.state.BlockState result;\n    try {\n        result = block#fromLegacyData((int) materialdata.getData());\n        // Returns null on TacoSpigot when last set() fails\n        if (result != null) {\n            return result;\n        }\n    } catch (RuntimeException ex) {\n        // Occurs on TacoSpigot and Forge (and mods!) when intermediate set() fails\n    } catch (Throwable t) {\n        com.bergerkiller.bukkit.common.Logging.LOGGER_REFLECTION.log(java.util.logging.Level.SEVERE,\n                \"An error occurred inside fromLegacyData(\"+Byte.toString(materialdata.getData())+\"):\", t);\n    }\n    result = block#getBlockData();\n    if (result == null) {\n        com.bergerkiller.bukkit.common.Logging.LOGGER_REFLECTION.log(java.util.logging.Level.WARNING,\n                \"Block \"+block.toString()+\" getBlockData() returned null!\");\n    }\n    return result;\n}"));
        }
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            MaterialDataToIBlockData.initBuilders();
        }
    }

    private static interface IBlockDataBuilder<M extends MaterialData> {
        public BlockStateHandle create(BlockStateHandle var1, M var2);
    }
}

