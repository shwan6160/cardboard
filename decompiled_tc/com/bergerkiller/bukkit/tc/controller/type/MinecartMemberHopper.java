/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartHopper
 *  org.bukkit.block.Block
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.controller.type;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecartHopper;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.persistence.MinecartInventoryPersistentCartAttribute;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class MinecartMemberHopper
extends MinecartMember<CommonMinecartHopper> {
    public MinecartMemberHopper(TrainCarts plugin) {
        super(plugin);
        this.addPersistentCartAttribute(new MinecartInventoryPersistentCartAttribute());
    }

    @Override
    public void onAttached() {
        super.onAttached();
    }

    @Override
    public void onActivatorUpdate(boolean activated) {
        boolean activateSucking;
        boolean bl = activateSucking = !activated;
        if (((CommonMinecartHopper)this.entity).isSuckingItems() != activateSucking) {
            ((CommonMinecartHopper)this.entity).setSuckingItems(activateSucking);
        }
    }

    @Override
    public void onTick() {
        super.onTick();
        if (((CommonMinecartHopper)this.entity).isRemoved() || !((CommonMinecartHopper)this.entity).isSuckingItems()) {
            return;
        }
        ((CommonMinecartHopper)this.entity).setSuckingCooldown(0);
        ((CommonMinecartHopper)this.entity).suckItems();
    }

    @Override
    public void onBlockChange(Block from, Block to) {
        super.onBlockChange(from, to);
        ((CommonMinecartHopper)this.entity).setSuckingCooldown(0);
    }

    public void onItemSet(int index, ItemStack item) {
        super.onItemSet(index, item);
        this.onPropertiesChanged();
    }
}

