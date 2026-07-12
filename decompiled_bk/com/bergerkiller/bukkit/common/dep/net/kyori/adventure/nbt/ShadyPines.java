/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt;

final class ShadyPines {
    private ShadyPines() {
    }

    static int floor(double dv) {
        int iv = (int)dv;
        return dv < (double)iv ? iv - 1 : iv;
    }

    static int floor(float fv) {
        int iv = (int)fv;
        return fv < (float)iv ? iv - 1 : iv;
    }
}

