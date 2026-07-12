/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 */
package com.bergerkiller.bukkit.common.internal.legacy;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.BlockStateHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Material;

public class MaterialsByName {
    private static final Material[] allMaterialValues;
    private static final Material[] allBlockMaterialValues;
    private static final Map<String, Material> allMaterialValuesByName;
    private static final FastMethod<Boolean> isLegacyMethod;

    public static Material[] getAllMaterials() {
        return allMaterialValues;
    }

    public static Material[] getAllBlocks() {
        return allBlockMaterialValues;
    }

    public static String getMaterialName(Material type) {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            return type.name();
        }
        return "LEGACY_" + type.name();
    }

    public static Material[] getAllByName(String ... names) {
        Material[] result = new Material[names.length];
        for (int i = 0; i < names.length; ++i) {
            Material m = MaterialsByName.getMaterial(names[i]);
            if (m == null) {
                throw new RuntimeException("Material not found: " + names[i]);
            }
            result[i] = m;
        }
        return result;
    }

    public static Material getMaterial(String name) {
        return allMaterialValuesByName.get(name);
    }

    public static Material getLegacyMaterial(String name) {
        return allMaterialValuesByName.get("LEGACY_" + name);
    }

    public static boolean isLegacy(Material material) {
        return isLegacyMethod.invoke(material);
    }

    public static boolean isBlock(Material type) {
        if (MaterialsByName.isLegacy(type)) {
            int id = type.getId();
            return id >= 0 && id < 256;
        }
        return type.isBlock();
    }

    public static BlockStateHandle getBlockDataFromMaterialName(String name) {
        return CraftMagicNumbersHandle.getBlockDataFromMaterial(MaterialsByName.getMaterial(name));
    }

    static {
        allMaterialValuesByName = new HashMap<String, Material>();
        isLegacyMethod = new FastMethod();
        String template = "public boolean isLegacy() {\n";
        template = CommonCapabilities.MATERIAL_ENUM_CHANGES ? template + "return instance.isLegacy();\n}" : template + "return true;\n}";
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClass(Material.class);
        isLegacyMethod.init(new MethodDeclaration(resolver, template));
        Material[] values = null;
        try {
            values = (Material[])Material.class.getMethod("values", new Class[0]).invoke(null, new Object[0]);
        }
        catch (Throwable t) {
            Logging.LOGGER_REGISTRY.log(Level.WARNING, "Material values() method not found, trying fallback", t);
            values = (Material[])Material.class.getEnumConstants();
        }
        if (values != null && CommonBootstrap.evaluateMCVersion("==", "1.8")) {
            for (int index = 0; index < values.length; ++index) {
                if (!MaterialsByName.getMaterialName(values[index]).equals("LEGACY_LOCKED_CHEST")) continue;
                values = LogicUtil.removeArrayElement(values, index);
                break;
            }
        }
        allMaterialValues = values;
        try {
            for (Material material : MaterialsByName.getAllMaterials()) {
                allMaterialValuesByName.put(MaterialsByName.getMaterialName(material), material);
            }
        }
        catch (Throwable t) {
            Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Error while initializing allMaterialValuesByName", t);
        }
        ArrayList<Material> blocks = new ArrayList<Material>();
        for (Object block : BlockHandle.getRegistry()) {
            Material mat = WrapperConversion.toMaterialFromBlockHandle(block);
            if (mat == null) continue;
            blocks.add(mat);
        }
        allBlockMaterialValues = blocks.toArray(new Material[0]);
    }
}

