/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.proxy;

public final class DimensionManager_1_8_8 {
    public static final DimensionManager_1_8_8 OVERWORLD = new DimensionManager_1_8_8(0, "");
    public static final DimensionManager_1_8_8 NETHER = new DimensionManager_1_8_8(-1, "_nether");
    public static final DimensionManager_1_8_8 THE_END = new DimensionManager_1_8_8(1, "_end");
    private final int id;
    private final String suffix;

    private DimensionManager_1_8_8(int id, String suffix) {
        this.id = id;
        this.suffix = suffix;
    }

    public int getDimensionID() {
        return this.id;
    }

    public int hashCode() {
        return this.id;
    }

    public boolean equals(Object o) {
        if (o instanceof DimensionManager_1_8_8) {
            return ((DimensionManager_1_8_8)o).id == this.id;
        }
        return false;
    }

    public String c() {
        return this.suffix;
    }

    public static DimensionManager_1_8_8 a(int i) {
        switch (i) {
            case 0: {
                return OVERWORLD;
            }
            case -1: {
                return NETHER;
            }
            case 1: {
                return THE_END;
            }
        }
        return new DimensionManager_1_8_8(i, "");
    }
}

