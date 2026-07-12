/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.wrappers;

public enum BoatWoodType {
    OAK,
    SPRUCE,
    BIRCH,
    JUNGLE,
    ACACIA,
    DARK_OAK;

    private static final BoatWoodType[] VALUES;

    public int getId() {
        return this.ordinal();
    }

    public static BoatWoodType byId(int id) {
        if (id < 0 || id >= VALUES.length) {
            return OAK;
        }
        return VALUES[id];
    }

    static {
        VALUES = BoatWoodType.values();
    }
}

