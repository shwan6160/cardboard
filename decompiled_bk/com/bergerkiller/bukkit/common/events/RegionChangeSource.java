/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.events;

public enum RegionChangeSource {
    FASTASYNCWORLDEDIT(true),
    WORLDEDIT(true);

    private final boolean isWorldedit;

    private RegionChangeSource(boolean isWorldEdit) {
        this.isWorldedit = isWorldEdit;
    }

    public boolean isWorldedit() {
        return this.isWorldedit;
    }
}

