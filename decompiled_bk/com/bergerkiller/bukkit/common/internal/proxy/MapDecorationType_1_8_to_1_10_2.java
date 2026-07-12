/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.proxy;

public enum MapDecorationType_1_8_to_1_10_2 {
    PLAYER(false),
    FRAME(true),
    RED_MARKER(false),
    BLUE_MARKER(false),
    TARGET_X(true),
    TARGET_POINT(true),
    PLAYER_OFF_MAP(false);

    private final byte k = (byte)this.ordinal();
    private final boolean l;
    private final int m;

    private MapDecorationType_1_8_to_1_10_2(boolean flag) {
        this(flag, -1);
    }

    private MapDecorationType_1_8_to_1_10_2(boolean flag, int i) {
        this.l = flag;
        this.m = i;
    }

    public byte a() {
        return this.k;
    }

    public boolean c() {
        return this.m >= 0;
    }

    public int d() {
        return this.m;
    }

    public static MapDecorationType_1_8_to_1_10_2 a(byte b0) {
        return MapDecorationType_1_8_to_1_10_2.values()[b0];
    }
}

