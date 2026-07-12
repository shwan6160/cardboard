/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.map;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;

class CreativeDraggedMapItem {
    public static final int CACHED_ITEM_MAX_LIFE = 12000;
    public static final int CACHED_ITEM_CLEAN_INTERVAL = 60;
    public int life;
    public final CommonItemStack item;

    public CreativeDraggedMapItem(CommonItemStack item) {
        this.item = item;
        this.life = 12000;
    }
}

