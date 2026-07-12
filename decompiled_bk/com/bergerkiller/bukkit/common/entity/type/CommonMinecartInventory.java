/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Minecart
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 */
package com.bergerkiller.bukkit.common.entity.type;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.generated.net.minecraft.world.ContainerHandle;
import org.bukkit.entity.Minecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class CommonMinecartInventory<T extends Minecart & InventoryHolder>
extends CommonMinecart<T> {
    public CommonMinecartInventory(T base) {
        super(base);
    }

    public Inventory getInventory() {
        return ((InventoryHolder)((Minecart)this.entity)).getInventory();
    }

    public void update() {
        this.handle.cast(ContainerHandle.T).update();
    }
}

