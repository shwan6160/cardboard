/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.EntityType
 */
package com.bergerkiller.bukkit.common.entity;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.EntityType;

class LegacyObjectTypes {
    private static final Map<String, ObjectTypeInfo> objectTypes = new HashMap<String, ObjectTypeInfo>();

    LegacyObjectTypes() {
    }

    public static ObjectTypeInfo find(EntityType entityType) {
        return entityType == null ? null : objectTypes.get(entityType.name());
    }

    private static void registerObjectType(String entityTypeName, int objectId, int extraData) {
        ObjectTypeInfo info = new ObjectTypeInfo();
        info.typeId = objectId;
        info.extraData = extraData;
        objectTypes.put(entityTypeName, info);
    }

    static {
        LegacyObjectTypes.registerObjectType("BOAT", 1, -1);
        LegacyObjectTypes.registerObjectType("DROPPED_ITEM", 2, 1);
        LegacyObjectTypes.registerObjectType("AREA_EFFECT_CLOUD", 3, -1);
        LegacyObjectTypes.registerObjectType("MINECART", 10, 0);
        LegacyObjectTypes.registerObjectType("MINECART_CHEST", 10, 1);
        LegacyObjectTypes.registerObjectType("MINECART_FURNACE", 10, 2);
        LegacyObjectTypes.registerObjectType("MINECART_TNT", 10, 3);
        LegacyObjectTypes.registerObjectType("MINECART_MOB_SPAWNER", 10, 4);
        LegacyObjectTypes.registerObjectType("MINECART_HOPPER", 10, 5);
        LegacyObjectTypes.registerObjectType("MINECART_COMMAND", 10, 6);
        LegacyObjectTypes.registerObjectType("PRIMED_TNT", 50, -1);
        LegacyObjectTypes.registerObjectType("ENDER_CRYSTAL", 51, -1);
        LegacyObjectTypes.registerObjectType("ARROW", 60, -1);
        LegacyObjectTypes.registerObjectType("TIPPED_ARROW", 60, -1);
        LegacyObjectTypes.registerObjectType("SNOWBALL", 61, -1);
        LegacyObjectTypes.registerObjectType("EGG", 62, -1);
        LegacyObjectTypes.registerObjectType("FIREBALL", 63, -1);
        LegacyObjectTypes.registerObjectType("SMALL_FIREBALL", 64, -1);
        LegacyObjectTypes.registerObjectType("ENDER_PEARL", 65, -1);
        LegacyObjectTypes.registerObjectType("WITHER_SKULL", 66, -1);
        LegacyObjectTypes.registerObjectType("SHULKER_BULLET", 67, 0);
        LegacyObjectTypes.registerObjectType("LLAMA_SPIT", 68, 0);
        LegacyObjectTypes.registerObjectType("FALLING_BLOCK", 70, -1);
        LegacyObjectTypes.registerObjectType("ITEM_FRAME", 71, -1);
        LegacyObjectTypes.registerObjectType("ENDER_SIGNAL", 72, -1);
        LegacyObjectTypes.registerObjectType("LINGERING_POTION", 73, 0);
        LegacyObjectTypes.registerObjectType("SPLASH_POTION", 73, 0);
        LegacyObjectTypes.registerObjectType("THROWN_EXP_BOTTLE", 75, 0);
        LegacyObjectTypes.registerObjectType("FIREWORK", 76, -1);
        LegacyObjectTypes.registerObjectType("LEASH_HITCH", 77, -1);
        LegacyObjectTypes.registerObjectType("ARMOR_STAND", 78, -1);
        LegacyObjectTypes.registerObjectType("EVOKER_FANGS", 79, -1);
        LegacyObjectTypes.registerObjectType("FISHING_HOOK", 90, -1);
        LegacyObjectTypes.registerObjectType("SPECTRAL_ARROW", 91, -1);
        LegacyObjectTypes.registerObjectType("DRAGON_FIREBALL", 92, -1);
    }

    public static class ObjectTypeInfo {
        public int typeId;
        public int extraData;
    }
}

