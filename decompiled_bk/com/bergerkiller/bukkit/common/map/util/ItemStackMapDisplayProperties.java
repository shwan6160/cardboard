/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.map.MapDisplayProperties;

public class ItemStackMapDisplayProperties
extends MapDisplayProperties {
    private final CommonItemStack item;

    public ItemStackMapDisplayProperties(CommonItemStack item) {
        this.item = item;
    }

    @Override
    public CommonItemStack getCommonMapItem() {
        return this.item;
    }
}

