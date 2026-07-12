/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.generated.net.minecraft.world.entity.MoverTypeHandle;

public enum MoveType {
    PISTON(MoverTypeHandle.PISTON),
    PLAYER(MoverTypeHandle.PLAYER),
    SELF(MoverTypeHandle.SELF),
    SHULKER(MoverTypeHandle.SHULKER),
    SHULKER_BOX(MoverTypeHandle.SHULKER_BOX);

    private final Object handle;

    private MoveType(MoverTypeHandle handle) {
        this.handle = MoverTypeHandle.T.isValid() ? handle.getRaw() : new Object();
    }

    public Object getHandle() {
        return this.handle;
    }

    public static MoveType getFromHandle(Object handle) {
        for (MoveType type : MoveType.values()) {
            if (type.handle != handle) continue;
            return type;
        }
        return null;
    }
}

