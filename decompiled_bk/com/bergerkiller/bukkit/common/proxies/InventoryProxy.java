/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.proxies;

import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.proxies.ProxyBase;
import com.bergerkiller.generated.org.bukkit.inventory.InventoryHandle;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class InventoryProxy
extends ProxyBase<Inventory>
implements Inventory {
    public InventoryProxy(Inventory base) {
        super(base);
    }

    public int getSize() {
        return ((Inventory)this.base).getSize();
    }

    public String getName() {
        if (InventoryHandle.T.getName.isAvailable()) {
            return InventoryHandle.T.getName.invoke(this.base);
        }
        return "";
    }

    public String getTitle() {
        if (InventoryHandle.T.getTitle.isAvailable()) {
            return InventoryHandle.T.getTitle.invoke(this.base);
        }
        return "";
    }

    public ItemStack getItem(int index) {
        return ((Inventory)this.base).getItem(index);
    }

    public ItemStack[] getContents() {
        return ((Inventory)this.base).getContents();
    }

    public void setContents(ItemStack[] items) {
        ((Inventory)this.base).setContents(items);
    }

    public ItemStack[] getStorageContents() {
        if (InventoryHandle.T.getStorageContents.isAvailable()) {
            return InventoryHandle.T.getStorageContents.invoke(this.base);
        }
        return new ItemStack[0];
    }

    public void setStorageContents(ItemStack[] itemStacks) throws IllegalArgumentException {
        if (InventoryHandle.T.setStorageContents.isAvailable()) {
            InventoryHandle.T.setStorageContents.invoke(this.base, itemStacks);
        }
    }

    public Location getLocation() {
        if (InventoryHandle.T.getLocation.isAvailable()) {
            return InventoryHandle.T.getLocation.invoke(this.base);
        }
        return null;
    }

    public void setItem(int index, ItemStack item) {
        ((Inventory)this.base).setItem(index, item);
    }

    @Deprecated
    public boolean contains(int materialId) {
        return ((Inventory)this.base).contains(CommonLegacyMaterials.getMaterialFromId(materialId));
    }

    public boolean contains(Material material) {
        return ((Inventory)this.base).contains(material);
    }

    public boolean contains(ItemStack item) {
        return ((Inventory)this.base).contains(item);
    }

    @Deprecated
    public boolean contains(int materialId, int amount) {
        return ((Inventory)this.base).contains(CommonLegacyMaterials.getMaterialFromId(materialId), amount);
    }

    public boolean contains(Material material, int amount) {
        return ((Inventory)this.base).contains(material, amount);
    }

    public boolean contains(ItemStack item, int amount) {
        return ((Inventory)this.base).contains(item, amount);
    }

    public boolean containsAtLeast(ItemStack item, int amount) {
        return ((Inventory)this.base).containsAtLeast(item, amount);
    }

    @Deprecated
    public HashMap<Integer, ? extends ItemStack> all(int materialId) {
        return ((Inventory)this.base).all(CommonLegacyMaterials.getMaterialFromId(materialId));
    }

    public HashMap<Integer, ? extends ItemStack> all(Material material) {
        return ((Inventory)this.base).all(material);
    }

    public HashMap<Integer, ? extends ItemStack> all(ItemStack item) {
        return ((Inventory)this.base).all(item);
    }

    @Deprecated
    public int first(int materialId) {
        return ((Inventory)this.base).first(CommonLegacyMaterials.getMaterialFromId(materialId));
    }

    public int first(Material material) {
        return ((Inventory)this.base).first(material);
    }

    public int first(ItemStack item) {
        return ((Inventory)this.base).first(item);
    }

    public int firstEmpty() {
        return ((Inventory)this.base).firstEmpty();
    }

    public HashMap<Integer, ItemStack> addItem(ItemStack ... items) {
        return ((Inventory)this.base).addItem(items);
    }

    public HashMap<Integer, ItemStack> removeItem(ItemStack ... items) {
        return ((Inventory)this.base).removeItem(items);
    }

    @Deprecated
    public void remove(int materialId) {
        ((Inventory)this.base).remove(CommonLegacyMaterials.getMaterialFromId(materialId));
    }

    public void remove(Material material) {
        ((Inventory)this.base).remove(material);
    }

    public void remove(ItemStack item) {
        ((Inventory)this.base).remove(item);
    }

    public void clear(int index) {
        ((Inventory)this.base).clear(index);
    }

    public void clear() {
        ((Inventory)this.base).clear();
    }

    public ListIterator<ItemStack> iterator() {
        return ((Inventory)this.base).iterator();
    }

    public ListIterator<ItemStack> iterator(int index) {
        return ((Inventory)this.base).iterator(index);
    }

    public List<HumanEntity> getViewers() {
        return ((Inventory)this.base).getViewers();
    }

    public InventoryType getType() {
        return ((Inventory)this.base).getType();
    }

    public InventoryHolder getHolder() {
        return ((Inventory)this.base).getHolder();
    }

    public int getMaxStackSize() {
        return ((Inventory)this.base).getMaxStackSize();
    }

    public void setMaxStackSize(int size) {
        ((Inventory)this.base).setMaxStackSize(size);
    }

    public boolean isEmpty() {
        return ((Inventory)this.base).isEmpty();
    }

    static {
        InventoryProxy.validate(InventoryProxy.class);
    }
}

