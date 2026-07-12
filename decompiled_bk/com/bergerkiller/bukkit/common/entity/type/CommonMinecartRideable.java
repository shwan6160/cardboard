/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.minecart.RideableMinecart
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.entity.type;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.inventory.ItemStack;

public class CommonMinecartRideable
extends CommonMinecart<RideableMinecart> {
    public CommonMinecartRideable(RideableMinecart base) {
        super(base);
    }

    @Override
    public List<ItemStack> getBrokenDrops() {
        return Arrays.asList(new ItemStack(Material.MINECART, 1));
    }

    @Override
    public Material getCombinedItem() {
        return Material.MINECART;
    }
}

