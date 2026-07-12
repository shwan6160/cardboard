/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.inventory.InventoryBase
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.ItemUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.generated.net.minecraft.util.RandomSourceHandle
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Item
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.inventory.InventoryBase;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.util.RandomSourceHandle;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class GroundItemsInventory
extends InventoryBase {
    private final List<Item> items = new ArrayList<Item>();
    private final Location location;

    public GroundItemsInventory(Block block, double range) {
        this(block.getLocation().add(0.5, 0.5, 0.5), range);
    }

    public GroundItemsInventory(Location location, double range) {
        this.location = location;
        double rangeSquared = range * range;
        for (Entity e : WorldUtil.getEntities((World)location.getWorld())) {
            if (!(e instanceof Item) || !(e.getLocation().distanceSquared(location) <= rangeSquared)) continue;
            this.items.add((Item)e);
        }
    }

    public int getSize() {
        return this.items.size() + 1;
    }

    public Location getLocation() {
        return this.location;
    }

    public Item getEntity(int index) {
        return this.items.get(index);
    }

    public void setItem(int index, ItemStack stack) {
        if (index == this.items.size()) {
            if (!LogicUtil.nullOrEmpty((ItemStack)stack)) {
                RandomSourceHandle random = WorldUtil.getRandom((World)this.location.getWorld());
                Location spawnLoc = this.location.clone().add(-0.45, -0.45, -0.45);
                spawnLoc = spawnLoc.add((double)(0.9f * random.nextFloat()), (double)(0.9f * random.nextFloat()), (double)(0.9f * random.nextFloat()));
                Item item = this.location.getWorld().dropItem(spawnLoc, stack);
                item.setVelocity(new Vector(0, 0, 0));
                this.items.add(item);
            }
        } else {
            Item item = this.items.get(index);
            EntityUtil.setDestroyed((Entity)item, (boolean)LogicUtil.nullOrEmpty((ItemStack)stack));
            if (!item.isDead()) {
                item.setItemStack(stack);
                this.items.set(index, ItemUtil.respawnItem((Item)item));
            }
        }
    }

    public ItemStack getItem(int index) {
        if (index == this.items.size()) {
            return null;
        }
        Item item = this.items.get(index);
        if (item.isDead()) {
            return null;
        }
        return item.getItemStack();
    }

    public String getName() {
        return "Ground Items";
    }
}

