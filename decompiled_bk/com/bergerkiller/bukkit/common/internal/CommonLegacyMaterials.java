/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.legacy.IBlockDataToMaterialData;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.Material;

public class CommonLegacyMaterials
extends MaterialsByName {
    private static final HashMap<Integer, Material> idToMaterial = new HashMap();
    private static final EnumMap<Material, Integer> materialToId = new EnumMap(Material.class);
    private static final Material[] allLegacyMaterialValues;

    public static Material toLegacy(Material material) {
        return IBlockDataToMaterialData.toLegacy(material);
    }

    public static int getOrdinal(Material material) {
        return material.ordinal();
    }

    public static Material[] getAllLegacyMaterials() {
        return allLegacyMaterialValues;
    }

    @Deprecated
    public static Material getMaterialFromId(int id) {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            throw new UnsupportedOperationException("Material Ids are no longer supported on Minecraft 1.13 and onwards");
        }
        return idToMaterial.get(id);
    }

    @Deprecated
    public static int getIdFromMaterial(Material type) {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            throw new UnsupportedOperationException("Material Ids are no longer supported on Minecraft 1.13 and onwards");
        }
        return materialToId.get(type);
    }

    static {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            ArrayList<Material> legacyMaterials = new ArrayList<Material>();
            for (Material material : MaterialsByName.getAllMaterials()) {
                if (!CommonLegacyMaterials.isLegacy(material)) continue;
                legacyMaterials.add(material);
            }
            allLegacyMaterialValues = LogicUtil.toArray(legacyMaterials, Material.class);
        } else {
            allLegacyMaterialValues = MaterialsByName.getAllMaterials();
        }
        if (!CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            try {
                Method m = Material.class.getDeclaredMethod("getId", new Class[0]);
                for (Material mat : CommonLegacyMaterials.getAllMaterials()) {
                    int id = (Integer)m.invoke((Object)mat, new Object[0]);
                    idToMaterial.put(id, mat);
                    materialToId.put(mat, id);
                }
            }
            catch (Throwable t) {
                Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Failed to initialize material mappings", t);
            }
        }
    }
}

