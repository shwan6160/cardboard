/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.generated.net.minecraft.core.RegistryHandle;
import java.util.IdentityHashMap;

public enum WindowType {
    GENERIC_9X1("minecraft:chest", "generic_9x1", 9),
    GENERIC_9X2("minecraft:chest", "generic_9x2", 18),
    GENERIC_9X3("minecraft:chest", "generic_9x3", 27),
    GENERIC_9X4("minecraft:chest", "generic_9x4", 36),
    GENERIC_9X5("minecraft:chest", "generic_9x5", 45),
    GENERIC_9X6("minecraft:chest", "generic_9x6", 54),
    GENERIC_3X3("minecraft:dispenser", "generic_3x3", 9),
    ANVIL("minecraft:anvil", "anvil", 0),
    BEACON("minecraft:beacon", "beacon", 1),
    FURNACE("minecraft:furnace", "furnace", 3),
    BLAST_FURNACE("minecraft:furnace", "blast_furnace", 3),
    BREWING_STAND("minecraft:brewing_stand", "brewing_stand", 4),
    CRAFTING("minecraft:crafting_table", "crafting", 10),
    ENCHANTMENT("minecraft:enchanting_table", "enchantment", 0),
    GRINDSTONE("minecraft:container", "grindstone", 3),
    HOPPER("minecraft:hopper", "hopper", 5),
    LECTERN("minecraft:container", "lectern", 3),
    LOOM("minecraft:container", "loom", 3),
    MERCHANT("minecraft:villager", "merchant", 3),
    SHULKER_BOX("minecraft:shulker_box", "shulker_box", 27),
    SMOKER("minecraft:container", "smoker", 0),
    CARTOGRAPHY("minecraft:container", "cartography", 3),
    STONECUTTER("minecraft:container", "stonecutter", 11),
    UNKNOWN(null, null, 0);

    private final String name_1_8;
    private final Object nmsWindowType_1_14;
    private final int id_1_14;
    private final int slotCount;
    private static final IdentityHashMap<Object, WindowType> byNMSWindowType;

    private WindowType(String name_1_8, String name_1_14, int slotCount) {
        this.name_1_8 = name_1_8;
        this.slotCount = slotCount;
        if (CommonCapabilities.HAS_WINDOW_TYPE_REGISTRY) {
            this.nmsWindowType_1_14 = RegistryHandle.getWindowTypeByName(name_1_14);
            this.id_1_14 = RegistryHandle.getWindowTypeId(this.nmsWindowType_1_14);
        } else {
            this.nmsWindowType_1_14 = null;
            this.id_1_14 = this.ordinal();
        }
    }

    public int getInventorySlots() {
        return this.slotCount;
    }

    public int getTypeId() {
        return this.id_1_14;
    }

    public String getLegacyName_1_8() {
        return this.name_1_8;
    }

    public Object getNMSType() {
        return this.nmsWindowType_1_14;
    }

    public boolean isSupported() {
        if (CommonCapabilities.HAS_WINDOW_TYPE_REGISTRY) {
            return this.id_1_14 != -1;
        }
        return this.name_1_8 != null;
    }

    public static WindowType fromNMSType(Object nmsWindowType) {
        return byNMSWindowType.getOrDefault(nmsWindowType, UNKNOWN);
    }

    public static WindowType fromLegacyName_1_8(String legacyName, int slotCount) {
        if ("minecraft:dropper".equals(legacyName)) {
            return GENERIC_3X3;
        }
        WindowType result = UNKNOWN;
        for (WindowType type : WindowType.values()) {
            if (!type.name_1_8.equals(legacyName)) continue;
            result = type;
            if (type.slotCount != slotCount) continue;
            return result;
        }
        return result;
    }

    public static WindowType fromWindowTypeId(int windowTypeId) {
        for (WindowType type : WindowType.values()) {
            if (type.id_1_14 != windowTypeId) continue;
            return type;
        }
        return UNKNOWN;
    }

    static {
        byNMSWindowType = new IdentityHashMap();
        if (CommonCapabilities.HAS_WINDOW_TYPE_REGISTRY) {
            for (WindowType type : WindowType.values()) {
                if (type.nmsWindowType_1_14 == null) continue;
                byNMSWindowType.put(type.nmsWindowType_1_14, type);
            }
        }
    }
}

