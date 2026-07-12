/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 */
package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class TileEntityTypesSerializedIds_1_8_to_1_17_1 {
    private static final BiMap<Integer, Object> idsToMinecraftKey = HashBiMap.create((int)16);
    private static final BiMap<Object, Integer> minecraftKeyToId = idsToMinecraftKey.inverse();
    private static final BiMap<IdentifierHandle, String> minecraftKeyToLegacyName = HashBiMap.create((int)32);
    private static final BiMap<String, IdentifierHandle> legacyNameToMinecraftKey = minecraftKeyToLegacyName.inverse();

    private static void register(int id, String name, String pre_1_10_2_Name) {
        IdentifierHandle keyHandle = IdentifierHandle.createNew(name);
        Object minecraftKey = keyHandle.getRaw();
        if (id != -1) {
            idsToMinecraftKey.put((Object)id, minecraftKey);
        }
        minecraftKeyToLegacyName.put((Object)keyHandle, (Object)pre_1_10_2_Name);
    }

    public static Set<Map.Entry<Integer, Object>> allEntries() {
        return idsToMinecraftKey.entrySet();
    }

    public static Object toMinecraftKey(int id) {
        return idsToMinecraftKey.get((Object)id);
    }

    public static int getId(Object nmsMinecraftKeyHandle) {
        return (Integer)minecraftKeyToId.getOrDefault(nmsMinecraftKeyHandle, (Object)-1);
    }

    public static String getLegacyName(IdentifierHandle minecraftKeyHandle) {
        String name = (String)minecraftKeyToLegacyName.get((Object)minecraftKeyHandle);
        if (name == null && minecraftKeyHandle != null) {
            String name_key = minecraftKeyHandle.getName();
            StringBuilder str = new StringBuilder();
            for (String part : name_key.split("_")) {
                if (part.isEmpty()) continue;
                str.append(Character.toUpperCase(part.charAt(0)));
                str.append(part.substring(1).toLowerCase(Locale.ENGLISH));
            }
            name = str.toString();
        }
        return name;
    }

    public static IdentifierHandle toMinecraftKeyFromLegacyName(String legacyName) {
        IdentifierHandle minecraftKey = (IdentifierHandle)legacyNameToMinecraftKey.get((Object)legacyName);
        if (minecraftKey == null && legacyName != null) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < legacyName.length(); ++i) {
                char c = legacyName.charAt(i);
                if (i > 0 && Character.isUpperCase(c)) {
                    str.append('_');
                }
                str.append(Character.toLowerCase(c));
            }
            minecraftKey = IdentifierHandle.createNew(str.toString());
        }
        return minecraftKey;
    }

    static {
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(1, "mob_spawner", "MobSpawner");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(2, "command_block", "Control");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(3, "beacon", "Beacon");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(4, "skull", "Skull");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(5, "flower_pot", "FlowerPot");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(6, "banner", "Banner");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(7, "structure_block", "Structure");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(8, "end_gateway", "EndGateway");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(9, "sign", "Sign");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(10, "shulker_box", "ShulkerBox");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(11, "bed", "Bed");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(-1, "furnace", "Furnace");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(-1, "chest", "Chest");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(-1, "ender_chest", "EnderChest");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(-1, "jukebox", "RecordPlayer");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(-1, "dispenser", "Trap");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(-1, "dropper", "Dropper");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(-1, "noteblock", "Music");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(-1, "piston", "Piston");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(-1, "brewing_stand", "Cauldron");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(-1, "enchanting_table", "EnchantTable");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(-1, "end_portal", "Airportal");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(-1, "daylight_detector", "DLDetector");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(-1, "hopper", "Hopper");
        TileEntityTypesSerializedIds_1_8_to_1_17_1.register(-1, "comparator", "Comparator");
    }
}

