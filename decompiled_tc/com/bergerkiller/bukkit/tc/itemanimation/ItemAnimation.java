/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.DoubleChest
 *  org.bukkit.entity.Entity
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.itemanimation;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.itemanimation.VirtualItem;
import com.bergerkiller.bukkit.tc.utils.GroundItemsInventory;
import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class ItemAnimation {
    private static final ArrayList<ItemAnimation> runningAnimations = new ArrayList();
    private static Task task;
    private final Object from;
    private final Object to;
    private final VirtualItem item;
    public int ticksToFinish = 10;

    private ItemAnimation(Object from, Object to, ItemStack data) {
        this.from = ItemAnimation.fixObject(from);
        this.to = ItemAnimation.fixObject(to);
        Location f = this.getFrom();
        Location t = this.getTo();
        if (f.getWorld() != t.getWorld()) {
            throw new IllegalArgumentException("Locations are on different worlds!");
        }
        this.item = new VirtualItem(f, data);
    }

    public static void start(Object from, Object to, ItemStack data) {
        ItemAnimation.start(from, to, CommonItemStack.of((ItemStack)data));
    }

    public static void start(Object from, Object to, CommonItemStack data) {
        if (from == null || to == null || data.isEmpty()) {
            return;
        }
        data = data.clone();
        Location l1 = ItemAnimation.getLocation(ItemAnimation.fixObject(from));
        for (ItemAnimation anim : runningAnimations) {
            CommonItemStack thisdata;
            Location l2 = ItemAnimation.getLocation(ItemAnimation.fixObject(anim.item));
            if (l2 == null || l1.getWorld() != l2.getWorld() || !(l1.distanceSquared(l2) < 4.0) || (thisdata = CommonItemStack.of((ItemStack)anim.item.getItemStack())).isEmpty()) continue;
            data.transferTo(thisdata, -1);
            if (!data.isEmpty()) continue;
            return;
        }
        runningAnimations.add(new ItemAnimation(from, to, data.toBukkit()));
        if (task == null) {
            task = new Task((JavaPlugin)TrainCarts.plugin){

                public void run() {
                    Iterator iter = runningAnimations.iterator();
                    while (iter.hasNext()) {
                        ItemAnimation anim = (ItemAnimation)iter.next();
                        if (!anim.update()) continue;
                        anim.item.die();
                        iter.remove();
                    }
                    if (runningAnimations.isEmpty()) {
                        Task.stop((Task)task);
                        task = null;
                    }
                }
            }.start(1L, 1L);
        }
    }

    public static void deinit() {
        for (ItemAnimation anim : runningAnimations) {
            anim.item.die();
        }
        runningAnimations.clear();
        Task.stop((Task)task);
        task = null;
    }

    private static Object fixObject(Object object) {
        if (object instanceof GroundItemsInventory) {
            return ((GroundItemsInventory)((Object)object)).getLocation();
        }
        if (object instanceof BlockState) {
            object = ((BlockState)object).getBlock();
        }
        if (object instanceof DoubleChest) {
            return ((DoubleChest)object).getLocation();
        }
        if (object instanceof Block) {
            return ((Block)object).getLocation().add(0.5, 0.5, 0.5);
        }
        if (object instanceof MinecartMember) {
            return ((CommonMinecart)((MinecartMember)object).getEntity()).getEntity();
        }
        if (object instanceof VirtualItem) {
            object = ((VirtualItem)object).item.getEntity();
        }
        return object;
    }

    private static Location getLocation(Object object) {
        if (object instanceof Entity) {
            return ((Entity)object).getLocation();
        }
        if (object instanceof Location) {
            return (Location)object;
        }
        throw new IllegalArgumentException("Unable to find the location of " + object.getClass().getName());
    }

    public Location getTo() {
        return ItemAnimation.getLocation(this.to);
    }

    public Location getFrom() {
        return ItemAnimation.getLocation(this.from);
    }

    public boolean update() {
        if (--this.ticksToFinish <= 0) {
            return true;
        }
        Vector dir = this.item.item.loc.offsetTo(this.getTo());
        double distancePerTick = dir.length();
        dir.normalize().multiply(distancePerTick /= (double)this.ticksToFinish);
        this.item.update(dir);
        return false;
    }
}

