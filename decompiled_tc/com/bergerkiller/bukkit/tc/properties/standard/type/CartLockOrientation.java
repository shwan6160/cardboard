/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.properties.standard.type;

public enum CartLockOrientation {
    NONE(false),
    LOCKED_NOT_FLIPPED(false),
    LOCKED_FLIPPED(true);

    private final boolean flipped;

    private CartLockOrientation(boolean flipped) {
        this.flipped = flipped;
    }

    public boolean isFlipped() {
        return this.flipped;
    }

    public static CartLockOrientation locked(boolean flipped) {
        return flipped ? LOCKED_FLIPPED : LOCKED_NOT_FLIPPED;
    }
}

