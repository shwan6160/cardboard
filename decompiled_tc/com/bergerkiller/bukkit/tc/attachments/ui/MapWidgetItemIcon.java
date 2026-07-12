/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.attachments.ui;

import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.tc.TCConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class MapWidgetItemIcon
extends MapWidget {
    private ItemStack item = new ItemStack(Material.AIR);

    public MapWidgetItemIcon() {
        this.setFocusable(true);
        this.setSize(16, 16);
    }

    public ItemStack getItemStack() {
        return this.item;
    }

    public MapWidgetItemIcon setItemStack(ItemStack item) {
        this.item = item;
        this.invalidate();
        return this;
    }

    public void onDraw() {
        this.view.fillItem(TCConfig.resourcePack, this.item);
        if (this.isFocused()) {
            this.view.drawRectangle(0, 0, this.getWidth(), this.getHeight(), (byte)18);
        }
    }

    public void onActivate() {
        this.onClick();
    }

    public abstract void onClick();
}

