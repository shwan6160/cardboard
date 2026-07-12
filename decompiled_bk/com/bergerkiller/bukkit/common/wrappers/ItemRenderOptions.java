/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.map.util.ModelInfoLookup;
import com.bergerkiller.bukkit.common.wrappers.RenderOptions;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class ItemRenderOptions
extends RenderOptions {
    private CommonItemStack item;

    public ItemRenderOptions(ItemStack item, String optionsToken) {
        this(CommonItemStack.of(item), optionsToken);
    }

    public ItemRenderOptions(ItemStack item, Map<String, String> optionsMap) {
        this(CommonItemStack.of(item), optionsMap);
    }

    public ItemRenderOptions(CommonItemStack item, String optionsToken) {
        super(optionsToken);
        this.item = item;
    }

    public ItemRenderOptions(CommonItemStack item, Map<String, String> optionsMap) {
        super(optionsMap);
        this.item = item;
    }

    public final ItemStack getItem() {
        return this.item.toBukkit();
    }

    public final CommonItemStack getCommonItem() {
        return this.item;
    }

    @Override
    public final String lookupModelName() {
        return ModelInfoLookup.lookupItem(this);
    }

    @Override
    public ItemRenderOptions clone() {
        if (this.optionsMap != null) {
            return new ItemRenderOptions(this.item.clone(), (Map<String, String>)this.optionsMap);
        }
        return new ItemRenderOptions(this.item.clone(), this.optionsToken);
    }
}

