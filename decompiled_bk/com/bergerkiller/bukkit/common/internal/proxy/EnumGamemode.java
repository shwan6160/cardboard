/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.proxy;

public enum EnumGamemode {
    NOT_SET(-1),
    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2),
    SPECTATOR(3);

    int f;

    private EnumGamemode(int id) {
        this.f = id;
    }

    public static EnumGamemode getById(int id) {
        for (EnumGamemode mode : EnumGamemode.values()) {
            if (mode.f != id) continue;
            return mode;
        }
        return SURVIVAL;
    }
}

