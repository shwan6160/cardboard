/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.TreeSpecies
 *  org.bukkit.block.Block
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.material.MaterialData
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.MaterialBlockProperty;
import com.bergerkiller.bukkit.common.MaterialBooleanProperty;
import com.bergerkiller.bukkit.common.MaterialProperty;
import com.bergerkiller.bukkit.common.MaterialTypeProperty;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.RecipeUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.world.item.ItemHandle;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class MaterialUtil {
    private static final Map<String, MaterialTypeProperty> TYPE_PROPERTIES = new HashMap<String, MaterialTypeProperty>();
    public static final MaterialTypeProperty ISAIR;
    public static final MaterialTypeProperty ISDOOR;
    public static final MaterialTypeProperty ISPISTONBASE;
    public static final MaterialTypeProperty ISREDSTONETORCH;
    public static final MaterialTypeProperty ISDIODE;
    public static final MaterialTypeProperty ISBUTTON;
    public static final MaterialTypeProperty ISCOMPARATOR;
    public static final MaterialTypeProperty ISBUCKET;
    public static final MaterialTypeProperty ISRAILS;
    public static final MaterialTypeProperty ISSIGN;
    public static final MaterialTypeProperty ISPRESSUREPLATE;
    public static final MaterialTypeProperty ISMINECART;
    public static final MaterialTypeProperty ISSWORD;
    public static final MaterialTypeProperty ISBOOTS;
    public static final MaterialTypeProperty ISLEGGINGS;
    public static final MaterialTypeProperty ISCHESTPLATE;
    public static final MaterialTypeProperty ISHELMET;
    public static final MaterialTypeProperty ISARMOR;
    public static final MaterialTypeProperty ISNETHERPORTAL;
    public static final MaterialTypeProperty ISENDPORTAL;
    public static final MaterialTypeProperty ISLEATHERARMOR;
    public static final MaterialTypeProperty ISINTERACTABLE;
    public static final MaterialTypeProperty ISWATER;
    public static final MaterialTypeProperty ISLAVA;
    public static final MaterialTypeProperty ISLIQUID;
    public static final MaterialTypeProperty ISLEAVES;
    public static final MaterialTypeProperty ISPOTION;
    public static final MaterialProperty<Boolean> ISHEATABLE;
    public static final MaterialProperty<Boolean> ISFUEL;
    public static final MaterialProperty<Boolean> ISPOWERSOURCE;
    public static final MaterialProperty<Boolean> HASDATA;
    public static final MaterialProperty<Integer> EMISSION;

    @Deprecated
    public static int getRawData(TreeSpecies treeSpecies) {
        return treeSpecies.getData();
    }

    @Deprecated
    public static int getRawData(Block block) {
        return WorldUtil.getBlockData(block).getRawData();
    }

    public static int getRawData(ItemStack item) {
        return item.getDurability();
    }

    @Deprecated
    public static int getRawData(MaterialData materialData) {
        return materialData.getData();
    }

    @Deprecated
    public static MaterialData getData(Material type, int rawData) {
        return BlockData.fromMaterialData(type, rawData).newMaterialData();
    }

    public static boolean isBlock(Material type) {
        return MaterialsByName.isBlock(type);
    }

    public static boolean isType(ItemStack itemStack, Material ... types) {
        return itemStack != null && MaterialUtil.isType(itemStack.getType(), types);
    }

    public static boolean isType(Material material, Material ... types) {
        return LogicUtil.contains(material, types);
    }

    public static boolean isType(Block block, Material ... types) {
        return block != null && WorldUtil.getBlockData(block).isType(types);
    }

    public static boolean isLegacyType(Material type) {
        return CommonLegacyMaterials.isLegacy(type);
    }

    public static Material getFirst(String ... names) {
        for (String name : names) {
            Material m = MaterialUtil.getMaterial(name);
            if (m == null) continue;
            return m;
        }
        throw new RuntimeException("None of the materials '" + String.join((CharSequence)", ", names) + "' could be found");
    }

    public static Material getMaterial(String name) {
        return MaterialsByName.getMaterial(name);
    }

    public static Material[] getAllMaterials() {
        return MaterialsByName.getAllMaterials();
    }

    public static Material[] getAllBlocks() {
        return MaterialsByName.getAllBlocks();
    }

    static {
        String material_categories_str = "";
        try {
            String mat_cat_path = "/com/bergerkiller/bukkit/common/internal/resources/material_categories.txt";
            try (InputStream input = MaterialUtil.class.getResourceAsStream(mat_cat_path);
                 Scanner scanner = new Scanner(input, "UTF-8");){
                scanner.useDelimiter("\\A");
                material_categories_str = "#set version " + CommonBootstrap.initCommonServer().getMinecraftVersionMajor() + "\n" + scanner.next();
                material_categories_str = SourceDeclaration.preprocess(material_categories_str);
            }
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to initialize material categories table", t);
        }
        String key = null;
        ArrayList<Material> values = new ArrayList<Material>();
        for (String line : material_categories_str.split("\\r?\\n")) {
            if ((line = line.trim()).startsWith("#") || line.isEmpty()) continue;
            if (line.endsWith(":")) {
                if (key != null) {
                    TYPE_PROPERTIES.put(key, new MaterialTypeProperty(values.toArray(new Material[values.size()])));
                }
                key = line.substring(0, line.length() - 1);
                values.clear();
                continue;
            }
            Material mat = MaterialUtil.getMaterial(line);
            if (mat == null) {
                throw new RuntimeException("Material type not found: " + line);
            }
            values.add(mat);
        }
        if (key != null) {
            TYPE_PROPERTIES.put(key, new MaterialTypeProperty(values.toArray(new Material[values.size()])));
        }
        ISAIR = TYPE_PROPERTIES.get("ISAIR");
        ISDOOR = TYPE_PROPERTIES.get("ISDOOR");
        ISPISTONBASE = TYPE_PROPERTIES.get("ISPISTONBASE");
        ISREDSTONETORCH = TYPE_PROPERTIES.get("ISREDSTONETORCH");
        ISDIODE = TYPE_PROPERTIES.get("ISDIODE");
        ISBUTTON = TYPE_PROPERTIES.get("ISBUTTON");
        ISCOMPARATOR = TYPE_PROPERTIES.get("ISCOMPARATOR");
        ISBUCKET = TYPE_PROPERTIES.get("ISBUCKET");
        ISRAILS = TYPE_PROPERTIES.get("ISRAILS");
        ISSIGN = TYPE_PROPERTIES.get("ISSIGN");
        ISPRESSUREPLATE = TYPE_PROPERTIES.get("ISPRESSUREPLATE");
        ISMINECART = TYPE_PROPERTIES.get("ISMINECART");
        ISSWORD = TYPE_PROPERTIES.get("ISSWORD");
        ISBOOTS = TYPE_PROPERTIES.get("ISBOOTS");
        ISLEGGINGS = TYPE_PROPERTIES.get("ISLEGGINGS");
        ISCHESTPLATE = TYPE_PROPERTIES.get("ISCHESTPLATE");
        ISHELMET = TYPE_PROPERTIES.get("ISHELMET");
        ISARMOR = new MaterialTypeProperty(ISBOOTS, ISLEGGINGS, ISCHESTPLATE, ISHELMET);
        ISNETHERPORTAL = TYPE_PROPERTIES.get("ISNETHERPORTAL");
        ISENDPORTAL = TYPE_PROPERTIES.get("ISENDPORTAL");
        ISLEATHERARMOR = TYPE_PROPERTIES.get("ISLEATHERARMOR");
        ISINTERACTABLE = TYPE_PROPERTIES.get("ISINTERACTABLE");
        ISWATER = TYPE_PROPERTIES.get("ISWATER");
        ISLAVA = TYPE_PROPERTIES.get("ISLAVA");
        ISLIQUID = new MaterialTypeProperty(ISWATER, ISLAVA);
        ISLEAVES = TYPE_PROPERTIES.get("ISLEAVES");
        ISPOTION = TYPE_PROPERTIES.get("ISPOTION");
        ISHEATABLE = new MaterialBooleanProperty(){

            @Override
            public Boolean get(Material type) {
                return RecipeUtil.isHeatableItem(type);
            }
        };
        ISFUEL = new MaterialBooleanProperty(){

            @Override
            public Boolean get(Material type) {
                return RecipeUtil.isFuelItem(type);
            }
        };
        ISPOWERSOURCE = new MaterialBlockProperty<Boolean>(){

            @Override
            public Boolean get(BlockData blockData) {
                return blockData.isPowerSource();
            }
        };
        HASDATA = new MaterialBooleanProperty(){

            @Override
            public Boolean get(Material type) {
                ItemHandle item = CommonNMS.getItem(type);
                return item == null ? false : item.usesDurability();
            }
        };
        EMISSION = new MaterialBlockProperty<Integer>(){

            @Override
            public Integer get(BlockData blockData) {
                return blockData.getEmission();
            }
        };
    }
}

