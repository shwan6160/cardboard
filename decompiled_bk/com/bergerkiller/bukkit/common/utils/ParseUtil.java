/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.DyeColor
 *  org.bukkit.GrassSpecies
 *  org.bukkit.Material
 *  org.bukkit.TreeSpecies
 *  org.bukkit.material.Leaves
 *  org.bukkit.material.LongGrass
 *  org.bukkit.material.MaterialData
 *  org.bukkit.material.TexturedMaterial
 *  org.bukkit.material.Tree
 *  org.bukkit.material.Wool
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.StringReplaceBundle;
import com.bergerkiller.bukkit.common.collections.StringMap;
import com.bergerkiller.bukkit.common.collections.StringMapCaseInsensitive;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import java.util.Locale;
import org.bukkit.DyeColor;
import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.material.Leaves;
import org.bukkit.material.LongGrass;
import org.bukkit.material.MaterialData;
import org.bukkit.material.TexturedMaterial;
import org.bukkit.material.Tree;
import org.bukkit.material.Wool;

public class ParseUtil {
    private static final StringMapCaseInsensitive<Boolean> BOOL_NAME_MAP = new StringMapCaseInsensitive();

    public static String filterNumeric(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder rval = new StringBuilder(text.length());
        boolean hasComma = false;
        boolean hasDigit = false;
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (Character.isDigit(c)) {
                rval.append(c);
                hasDigit = true;
                continue;
            }
            if (c == ' ') {
                if (!hasDigit) continue;
                break;
            }
            if (!(c != ',' && c != '.' || hasComma)) {
                rval.append('.');
                hasComma = true;
                continue;
            }
            if (c != '-' || rval.length() != 0) continue;
            rval.append(c);
        }
        return rval.toString();
    }

    public static boolean isNumeric(String text) {
        if (LogicUtil.nullOrEmpty(text)) {
            return false;
        }
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (Character.isDigit(c) || c == '.' || c == ',' || c == '-' && i <= 0) continue;
            return false;
        }
        return true;
    }

    public static boolean isBool(String text) {
        return BOOL_NAME_MAP.containsKey(text);
    }

    public static boolean parseBool(String text) {
        return ParseUtil.parseBool(text, Boolean.FALSE);
    }

    public static Boolean parseBool(String text, Boolean def) {
        return LogicUtil.fixNull(BOOL_NAME_MAP.get(text), def);
    }

    public static float parseFloat(String text, float def) {
        return ParseUtil.parseFloat(text, Float.valueOf(def)).floatValue();
    }

    public static Float parseFloat(String text, Float def) {
        return Conversion.toFloat.convert(text, def);
    }

    public static double parseDouble(String text, double def) {
        return ParseUtil.parseDouble(text, (Double)def);
    }

    public static Double parseDouble(String text, Double def) {
        return Conversion.toDouble.convert(text, def);
    }

    public static long parseLong(String text, long def) {
        return ParseUtil.parseLong(text, (Long)def);
    }

    public static Long parseLong(String text, Long def) {
        return Conversion.toLong.convert(text, def);
    }

    public static int parseInt(String text, int def) {
        return ParseUtil.parseInt(text, (Integer)def);
    }

    public static Integer parseInt(String text, Integer def) {
        return Conversion.toInt.convert(text, def);
    }

    public static float parseShort(String text, short def) {
        return ParseUtil.parseShort(text, (Short)def).shortValue();
    }

    public static Short parseShort(String text, Short def) {
        return Conversion.toShort.convert(text, def);
    }

    public static float parseByte(String text, byte def) {
        return ParseUtil.parseByte(text, (Byte)def).byteValue();
    }

    public static Byte parseByte(String text, Byte def) {
        return Conversion.toByte.convert(text, def);
    }

    public static long parseTime(String timestring) {
        long rval = 0L;
        if (!LogicUtil.nullOrEmpty(timestring)) {
            String[] parts = timestring.split(":");
            if (parts.length == 1) {
                rval = (long)(ParseUtil.parseDouble(parts[0], 0.0) * 1000.0);
            } else if (parts.length == 2) {
                rval = ParseUtil.parseLong(parts[0], 0L) * 60000L;
                rval += ParseUtil.parseLong(parts[1], 0L) * 1000L;
            } else if (parts.length == 3) {
                rval = ParseUtil.parseLong(parts[0], 0L) * 3600000L;
                rval += ParseUtil.parseLong(parts[1], 0L) * 60000L;
                rval += ParseUtil.parseLong(parts[2], 0L) * 1000L;
            }
        }
        return rval;
    }

    public static <T> T parseArray(T[] values, String text, T def) {
        int i;
        if (LogicUtil.nullOrEmpty(text)) {
            return def;
        }
        text = text.toUpperCase(Locale.ENGLISH).replace("_", "").replace(" ", "");
        String[] names = new String[values.length];
        for (i = 0; i < names.length; ++i) {
            names[i] = values[i].toString().toUpperCase(Locale.ENGLISH).replace("_", "");
            if (!names[i].equals(text)) continue;
            return values[i];
        }
        for (i = 0; i < names.length; ++i) {
            if (!names[i].contains(text)) continue;
            return values[i];
        }
        for (i = 0; i < names.length; ++i) {
            if (!text.contains(names[i])) continue;
            return values[i];
        }
        return def;
    }

    public static <T> T parseEnum(String text, T def) {
        return (T)ParseUtil.parseEnum(def.getClass(), text, def);
    }

    public static <T> T parseEnum(Class<T> enumClass, String text, T def) {
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException("Class '" + enumClass.getSimpleName() + "' is not an Enumeration!");
        }
        return ParseUtil.parseArray(enumClass.getEnumConstants(), text, def);
    }

    public static TreeSpecies parseTreeSpecies(String text, TreeSpecies def) {
        if ((text = text.toLowerCase(Locale.ENGLISH)).contains("oak")) {
            return TreeSpecies.GENERIC;
        }
        if (text.contains("pine") || text.contains("spruce")) {
            return TreeSpecies.REDWOOD;
        }
        return ParseUtil.parseEnum(TreeSpecies.class, text, def);
    }

    public static Material parseMaterial(String text, Material def) {
        return ParseUtil.parseMaterial(text, def, false);
    }

    @Deprecated
    public static Material parseMaterial(String text, Material def, boolean legacy) {
        String matName;
        if (LogicUtil.nullOrEmpty(text)) {
            return def;
        }
        if (!CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            try {
                return LogicUtil.fixNull(CommonLegacyMaterials.getMaterialFromId(Integer.parseInt(text)), def);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        String matName_legacy = matName = Materials.MAT_ALIASES.replace(text.trim().toUpperCase(Locale.ENGLISH));
        if (legacy) {
            matName = "LEGACY_" + matName;
        }
        while (true) {
            Material mat;
            if ((mat = (Material)Materials.MAT_NAME_MAP.get(matName)) != null) {
                return mat;
            }
            mat = legacy && !CommonCapabilities.MATERIAL_ENUM_CHANGES ? (Material)ParseUtil.parseEnum(Material.class, matName_legacy, null) : (Material)ParseUtil.parseEnum(Material.class, matName, null);
            if (mat != null) {
                return mat;
            }
            if (!matName.endsWith("S")) break;
            matName = matName.substring(0, matName.length() - 1);
            matName_legacy = matName_legacy.substring(0, matName_legacy.length() - 1);
        }
        return def;
    }

    @Deprecated
    public static Byte parseMaterialData(String text, Material material, Byte def) {
        int data = ParseUtil.parseMaterialData(text, material, -1);
        if (data < 0 || data > 255) {
            return def;
        }
        return (byte)data;
    }

    @Deprecated
    public static int parseMaterialData(String text, Material material, int def) {
        if (!MaterialUtil.isLegacyType(material)) {
            return def;
        }
        try {
            return Integer.parseInt(text);
        }
        catch (NumberFormatException ex) {
            if (material != null && CommonLegacyMaterials.getMaterialName(material).equals("LEGACY_WOOD")) {
                TreeSpecies ts = ParseUtil.parseTreeSpecies(text, null);
                if (ts != null) {
                    return ts.getData();
                }
                return def;
            }
            MaterialData dat = BlockData.fromMaterialData(material, 0).newMaterialData();
            if (dat instanceof TexturedMaterial) {
                TexturedMaterial tdat = (TexturedMaterial)dat;
                Material mat = ParseUtil.parseMaterial(text, null);
                if (mat == null) {
                    return def;
                }
                tdat.setMaterial(mat);
            } else if (dat instanceof Wool) {
                Wool wdat = (Wool)dat;
                DyeColor color = ParseUtil.parseEnum(DyeColor.class, text, null);
                if (color == null) {
                    return def;
                }
                wdat.setColor(color);
            } else if (dat instanceof Tree) {
                Tree tdat = (Tree)dat;
                TreeSpecies species = ParseUtil.parseTreeSpecies(text, null);
                if (species == null) {
                    return def;
                }
                tdat.setSpecies(species);
            } else if (dat instanceof Leaves) {
                Leaves tdat = (Leaves)dat;
                TreeSpecies species = ParseUtil.parseTreeSpecies(text, null);
                if (species == null) {
                    return def;
                }
                tdat.setSpecies(species);
            } else if (dat instanceof LongGrass) {
                LongGrass ldat = (LongGrass)dat;
                GrassSpecies species = ParseUtil.parseEnum(GrassSpecies.class, text, null);
                if (species == null) {
                    return def;
                }
                ldat.setSpecies(species);
            } else {
                return def;
            }
            return dat.getData();
        }
    }

    public static <T> T convert(Object object, Class<T> type) {
        return ParseUtil.convert(object, type, null);
    }

    public static <T> T convert(Object object, T def) {
        return (T)ParseUtil.convert(object, def.getClass(), def);
    }

    public static <T> T convert(Object object, Class<T> type, T def) {
        return Conversion.convert(object, type, def);
    }

    static {
        CommonBootstrap.initCommonServerAssertCompatibility();
        for (String trueValue : new String[]{"yes", "allow", "allowed", "true", "ye", "y", "t", "on", "enabled", "enable"}) {
            BOOL_NAME_MAP.put(trueValue, Boolean.TRUE);
        }
        for (String falseValue : new String[]{"no", "none", "deny", "denied", "false", "n", "f", "off", "disabled", "disable"}) {
            BOOL_NAME_MAP.put(falseValue, Boolean.FALSE);
        }
    }

    private static class Materials {
        private static final StringMap<Material> MAT_NAME_MAP = new StringMap();
        private static final StringReplaceBundle MAT_ALIASES = new StringReplaceBundle();

        private Materials() {
        }

        static {
            for (Material material : MaterialsByName.getAllMaterials()) {
                MAT_NAME_MAP.putUpper(MaterialsByName.getMaterialName(material), material);
            }
            MAT_NAME_MAP.put("REDSTONETORCH", MaterialUtil.getFirst("REDSTONE_TORCH", "LEGACY_REDSTONE_TORCH_ON"));
            MAT_NAME_MAP.put("BUTTON", Material.STONE_BUTTON);
            MAT_NAME_MAP.put("PISTON", MaterialUtil.getFirst("PISTON", "LEGACY_PISTON_BASE"));
            MAT_NAME_MAP.put("STICKPISTON", MaterialUtil.getFirst("STICKY_PISTON", "LEGACY_PISTON_STICKY_BASE"));
            MAT_NAME_MAP.put("MOSSSTONE", Material.MOSSY_COBBLESTONE);
            MAT_NAME_MAP.put("STONESTAIR", Material.COBBLESTONE_STAIRS);
            MAT_NAME_MAP.put("SANDSTAIR", Material.SANDSTONE_STAIRS);
            MAT_NAME_MAP.put("GOLDAPPLE", Material.GOLDEN_APPLE);
            MAT_NAME_MAP.put("APPLEGOLD", Material.GOLDEN_APPLE);
            MAT_NAME_MAP.put("COBBLEFENCE", MaterialUtil.getFirst("COBBLESTONE_WALL", "LEGACY_COBBLE_WALL"));
            MAT_NAME_MAP.put("STONEFENCE", MaterialUtil.getFirst("COBBLESTONE_WALL", "LEGACY_COBBLE_WALL"));
            MAT_NAME_MAP.put("COBBLEWALL", MaterialUtil.getFirst("COBBLESTONE_WALL", "LEGACY_COBBLE_WALL"));
            MAT_NAME_MAP.put("STONEWALL", MaterialUtil.getFirst("COBBLESTONE_WALL", "LEGACY_COBBLE_WALL"));
            MAT_NAME_MAP.put("LEGACY_REDSTONE_TORCH", MaterialUtil.getMaterial("LEGACY_REDSTONE_TORCH_ON"));
            MAT_NAME_MAP.put("LEGACY_STONE_BUTTON", MaterialUtil.getMaterial("LEGACY_STONE_BUTTON"));
            MAT_NAME_MAP.put("LEGACY_PISTON", MaterialUtil.getMaterial("LEGACY_PISTON_BASE"));
            MAT_NAME_MAP.put("LEGACY_STICKPISTON", MaterialUtil.getMaterial("LEGACY_PISTON_STICKY_BASE"));
            MAT_NAME_MAP.put("LEGACY_MOSSSTONE", MaterialUtil.getMaterial("LEGACY_MOSSY_COBBLESTONE"));
            MAT_NAME_MAP.put("LEGACY_STONESTAIR", MaterialUtil.getMaterial("LEGACY_COBBLESTONE_STAIRS"));
            MAT_NAME_MAP.put("LEGACY_SANDSTAIR", MaterialUtil.getMaterial("LEGACY_SANDSTONE_STAIRS"));
            MAT_NAME_MAP.put("LEGACY_GOLDAPPLE", MaterialUtil.getMaterial("LEGACY_GOLDEN_APPLE"));
            MAT_NAME_MAP.put("LEGACY_APPLEGOLD", MaterialUtil.getMaterial("LEGACY_GOLDEN_APPLE"));
            MAT_NAME_MAP.put("LEGACY_COBBLEFENCE", MaterialUtil.getMaterial("LEGACY_COBBLE_WALL"));
            MAT_NAME_MAP.put("LEGACY_STONEFENCE", MaterialUtil.getMaterial("LEGACY_COBBLE_WALL"));
            MAT_NAME_MAP.put("LEGACY_COBBLEWALL", MaterialUtil.getMaterial("LEGACY_COBBLE_WALL"));
            MAT_NAME_MAP.put("LEGACY_STONEWALL", MaterialUtil.getMaterial("LEGACY_COBBLE_WALL"));
            MAT_NAME_MAP.put("LEGACY_SLAB", MaterialUtil.getMaterial("LEGACY_STEP"));
            MAT_NAME_MAP.put("LEGACY_DOUBLE_SLAB", MaterialUtil.getMaterial("LEGACY_DOUBLE_STEP"));
            MAT_NAME_MAP.put("LEGACY_STONE_BRICK", MaterialUtil.getMaterial("LEGACY_SMOOTH_BRICK"));
            if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
                MAT_ALIASES.add("SPADE", "SHOVEL");
                MAT_ALIASES.add("REDSTONEREPEATER", "REPEATER");
                MAT_ALIASES.add("FIREWORK", "FIREWORK_ROCKET");
            } else {
                MAT_ALIASES.add("SLAB", "STEP");
                MAT_ALIASES.add("STONEBRICK", "SMOOTHBRICK");
                MAT_ALIASES.add("PLANK", "WOOD");
                MAT_ALIASES.add("SHOVEL", "SPADE");
                MAT_ALIASES.add("REDSTONEREPEATER", "DIODE");
                MAT_ALIASES.add("REPEATER", "DIODE");
                MAT_ALIASES.add("PRESSUREPLATE", "PLATE");
            }
            MAT_ALIASES.add(" ", "_").add("DIAM_", "DIAMOND").add("LEAT_", "LEATHER").add("_", "");
            MAT_ALIASES.add("PANTS", "LEGGINGS").add("REDSTONEDUST", "REDSTONE");
            MAT_ALIASES.add("SULPHER", "SULPHUR").add("SULPHOR", "SULPHUR").add("DOORBLOCK", "DOOR");
            MAT_ALIASES.add("LIGHTER", "FLINTANDSTEEL").add("LITPUMPKIN", "JACKOLANTERN");
        }
    }
}

