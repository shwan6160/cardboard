/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat;

public enum FirstPersonViewMode {
    DYNAMIC(false, false),
    DEFAULT(false, false),
    INVISIBLE(true, true),
    HEAD(false, true),
    STANDING(false, false),
    THIRD_P(true, true);

    private final boolean _fakePlayer;
    private final boolean _realPlayerInvisible;
    private final boolean _selectable;

    private FirstPersonViewMode(boolean fakePlayer, boolean realPlayerInvisible) {
        this(fakePlayer, realPlayerInvisible, true);
    }

    private FirstPersonViewMode(boolean fakePlayer, boolean realPlayerInvisible, boolean selectable) {
        this._fakePlayer = fakePlayer;
        this._realPlayerInvisible = realPlayerInvisible;
        this._selectable = selectable;
    }

    public boolean hasFakePlayer() {
        return this._fakePlayer;
    }

    public boolean isRealPlayerInvisible() {
        return this._realPlayerInvisible;
    }

    public boolean isSelectable() {
        return this._selectable;
    }
}

