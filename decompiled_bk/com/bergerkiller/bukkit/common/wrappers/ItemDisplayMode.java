/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;

public enum ItemDisplayMode {
    NONE(0, "none"),
    HEAD(5, "head"),
    THIRD_PERSON_LEFT_HAND(1, "3P left hand"),
    THIRD_PERSON_RIGHT_HAND(2, "3P right hand"),
    FIRST_PERSON_LEFT_HAND(3, "1P left hand"),
    FIRST_PERSON_RIGHT_HAND(4, "1P right hand"),
    GROUND(7, "ground"),
    GUI(6, "gui"),
    FIXED(8, "fixed");

    private final byte bId;
    private final int id;
    private final String description;
    static final ItemDisplayMode[] VALUES_BY_ID;

    private ItemDisplayMode(int id, String description) {
        this.id = id;
        this.bId = (byte)id;
        this.description = description;
    }

    public String description() {
        return this.description;
    }

    public String toString() {
        return this.description();
    }

    @ConverterMethod
    public static ItemDisplayMode byId(byte id) {
        int int_id = id & 0xFF;
        ItemDisplayMode[] values = VALUES_BY_ID;
        return int_id >= values.length ? NONE : values[int_id];
    }

    @ConverterMethod
    public static byte getId(ItemDisplayMode mode) {
        return mode.bId;
    }

    static {
        VALUES_BY_ID = LogicUtil.make(() -> {
            int max = 0;
            for (ItemDisplayMode mode : ItemDisplayMode.values()) {
                max = Math.max(max, mode.id);
            }
            ItemDisplayMode[] result = new ItemDisplayMode[max + 1];
            ItemDisplayMode[] arr$ = ItemDisplayMode.values();
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; ++i$) {
                ItemDisplayMode mode;
                result[mode.id] = mode = arr$[i$];
            }
            return result;
        });
    }
}

