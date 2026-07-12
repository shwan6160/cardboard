/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.minecart.HopperMinecart
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.entity.type;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecartInventory;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.MinecartHopperHandle;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.inventory.ItemStack;

public class CommonMinecartHopper
extends CommonMinecartInventory<HopperMinecart> {
    public CommonMinecartHopper(HopperMinecart base) {
        super(base);
    }

    @Override
    public List<ItemStack> getBrokenDrops() {
        return Arrays.asList(new ItemStack(Material.MINECART, 1), new ItemStack(Material.HOPPER, 1));
    }

    @Override
    public Material getCombinedItem() {
        return Material.HOPPER_MINECART;
    }

    public boolean suckItems() {
        return MinecartHopperHandle.T.suckItems.invoke(this.getHandle());
    }

    public boolean isSuckingItems() {
        return MinecartHopperHandle.T.isSuckingEnabled.invoke(this.getHandle());
    }

    public void setSuckingItems(boolean sucking) {
        MinecartHopperHandle.T.setSuckingEnabled.invoke(this.getHandle(), sucking);
    }

    @Deprecated
    public void setSuckingCooldown(int cooldownTicks) {
    }

    @Deprecated
    public int getSuckingCooldown() {
        return 0;
    }
}

