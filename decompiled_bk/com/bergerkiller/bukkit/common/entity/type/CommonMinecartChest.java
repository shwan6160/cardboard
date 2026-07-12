/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.minecart.StorageMinecart
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.entity.type;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecartInventory;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.ItemStack;

public class CommonMinecartChest
extends CommonMinecartInventory<StorageMinecart> {
    private static final Material _COMBINED_ITEM = CommonCapabilities.MATERIAL_ENUM_CHANGES ? Material.getMaterial((String)"CHEST_MINECART") : Material.getMaterial((String)"STORAGE_MINECART");

    public CommonMinecartChest(StorageMinecart base) {
        super(base);
    }

    @Override
    public List<ItemStack> getBrokenDrops() {
        return Arrays.asList(new ItemStack(Material.MINECART, 1), new ItemStack(Material.CHEST, 1));
    }

    @Override
    public Material getCombinedItem() {
        return _COMBINED_ITEM;
    }
}

