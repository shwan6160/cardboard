/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;

public final class Brightness {
    private final int block;
    private final int sky;
    private final int packed;
    public static final Brightness UNSET = new Brightness(-1, -1, -1);
    public static final Brightness NONE = new Brightness(0, 0);
    public static final Brightness FULL_ALL = new Brightness(15, 15);
    public static final Brightness FULL_BLOCK = new Brightness(15, 0);
    public static final Brightness FULL_SKY = new Brightness(0, 15);

    private Brightness(int block, int sky) {
        this(block, sky, block << 4 | sky << 20);
    }

    private Brightness(int block, int sky, int packed) {
        this.block = block;
        this.sky = sky;
        this.packed = packed;
    }

    public int blockLight() {
        return this.block;
    }

    public int skyLight() {
        return this.sky;
    }

    public Brightness withSkyLight(int skyLightLevel) {
        return new Brightness(this.block, skyLightLevel);
    }

    public Brightness withBlockLight(int blockLightLevel) {
        return new Brightness(blockLightLevel, this.sky);
    }

    @ConverterMethod
    public static int pack(Brightness brightness) {
        return brightness.packed;
    }

    @ConverterMethod
    public static Brightness unpack(int packed) {
        if (packed == -1) {
            return UNSET;
        }
        int j = packed >> 4 & 0xFFFF;
        int k = packed >> 20 & 0xFFFF;
        return new Brightness(j, k);
    }

    public static Brightness blockAndSkyLight(int blockLightLevel, int skyLightlevel) {
        return new Brightness(blockLightLevel, skyLightlevel);
    }

    public static Brightness blockLight(int blockLightLevel) {
        return new Brightness(blockLightLevel, 0);
    }

    public static Brightness skyLight(int skyLightlevel) {
        return new Brightness(0, skyLightlevel);
    }

    public int hashCode() {
        return this.packed;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Brightness) {
            Brightness b = (Brightness)o;
            return this.block == b.block && this.sky == b.sky;
        }
        return false;
    }
}

