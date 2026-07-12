/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.wrappers;

public enum InventoryClickType {
    PICKUP(0),
    QUICK_MOVE(1),
    SWAP(2),
    CLONE(3),
    THROW(4),
    QUICK_CRAFT(5),
    PICKUP_ALL(6);

    private int _id;

    private InventoryClickType(int id) {
        this._id = id;
    }

    public int getId() {
        return this._id;
    }

    public static InventoryClickType byId(int id) {
        for (InventoryClickType value : InventoryClickType.values()) {
            if (value.getId() != id) continue;
            return value;
        }
        return PICKUP;
    }
}

