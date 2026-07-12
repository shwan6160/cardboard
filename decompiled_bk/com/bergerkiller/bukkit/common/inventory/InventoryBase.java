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
package com.bergerkiller.bukkit.common.inventory;

import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.hooks.IInventoryProxyHook;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.generated.net.minecraft.world.ContainerHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryHandle;
import com.bergerkiller.generated.org.bukkit.inventory.InventoryHandle;
import java.lang.reflect.Method;
import java.util.Collections;
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

public abstract class InventoryBase
implements Inventory {
    private final Object nmsProxy = new IInventoryProxyHook(this).createInstance(ContainerHandle.T.getType());
    private final Inventory cbProxy = CraftInventoryHandle.createNew(this.nmsProxy);
    private int maxstacksize = 64;

    public final Object getRawHandle() {
        return this.nmsProxy;
    }

    public abstract int getSize();

    public abstract ItemStack getItem(int var1);

    public abstract void setItem(int var1, ItemStack var2);

    public ItemStack[] getContents() {
        ItemStack[] contents = new ItemStack[this.getSize()];
        for (int i = 0; i < contents.length; ++i) {
            contents[i] = this.getItem(i);
        }
        return contents;
    }

    public void setContents(ItemStack[] items) {
        if (this.getSize() < items.length) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + this.getSize() + " or less");
        }
        for (int i = 0; i < this.getSize(); ++i) {
            if (i >= items.length) {
                this.setItem(i, null);
                continue;
            }
            this.setItem(i, items[i]);
        }
    }

    public InventoryType getType() {
        return InventoryType.CHEST;
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public String getTitle() {
        return this.getName();
    }

    public InventoryHolder getHolder() {
        return null;
    }

    public List<HumanEntity> getViewers() {
        return Collections.emptyList();
    }

    public int getMaxStackSize() {
        return this.maxstacksize;
    }

    public void setMaxStackSize(int size) {
        this.maxstacksize = size;
    }

    public HashMap<Integer, ItemStack> addItem(ItemStack ... items) throws IllegalArgumentException {
        return this.cbProxy.addItem(items);
    }

    @Deprecated
    public HashMap<Integer, ? extends ItemStack> all(int materialId) {
        return this.cbProxy.all(CommonLegacyMaterials.getMaterialFromId(materialId));
    }

    public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException {
        return this.cbProxy.all(material);
    }

    public HashMap<Integer, ? extends ItemStack> all(ItemStack item) {
        return this.cbProxy.all(item);
    }

    public void clear() {
        this.cbProxy.clear();
    }

    public void clear(int index) {
        this.cbProxy.clear(index);
    }

    @Deprecated
    public boolean contains(int materialId) {
        return this.cbProxy.contains(CommonLegacyMaterials.getMaterialFromId(materialId));
    }

    public boolean contains(Material material) throws IllegalArgumentException {
        return this.cbProxy.contains(material);
    }

    public boolean contains(ItemStack item) {
        return this.cbProxy.contains(item);
    }

    @Deprecated
    public boolean contains(int materialId, int amount) {
        return this.cbProxy.contains(CommonLegacyMaterials.getMaterialFromId(materialId), amount);
    }

    public boolean contains(Material material, int amount) throws IllegalArgumentException {
        return this.cbProxy.contains(material, amount);
    }

    public boolean contains(ItemStack item, int amount) {
        return this.cbProxy.contains(item, amount);
    }

    public boolean containsAtLeast(ItemStack item, int amount) {
        return this.cbProxy.containsAtLeast(item, amount);
    }

    @Deprecated
    public int first(int materialId) {
        return this.cbProxy.first(CommonLegacyMaterials.getMaterialFromId(materialId));
    }

    public int first(Material material) throws IllegalArgumentException {
        return this.cbProxy.first(material);
    }

    public int first(ItemStack item) {
        return this.cbProxy.first(item);
    }

    public int firstEmpty() {
        return this.cbProxy.firstEmpty();
    }

    public ListIterator<ItemStack> iterator() {
        return this.cbProxy.iterator();
    }

    public ListIterator<ItemStack> iterator(int index) {
        return this.cbProxy.iterator(index);
    }

    @Deprecated
    public void remove(int materialId) {
        this.remove(CommonLegacyMaterials.getMaterialFromId(materialId));
    }

    public void remove(Material material) throws IllegalArgumentException {
        this.cbProxy.remove(material);
    }

    public void remove(ItemStack item) {
        this.cbProxy.remove(item);
    }

    public HashMap<Integer, ItemStack> removeItem(ItemStack ... items) throws IllegalArgumentException {
        return this.cbProxy.removeItem(items);
    }

    public HashMap<Integer, ItemStack> removeItemAnySlot(ItemStack ... items) throws IllegalArgumentException {
        if (InventoryHandle.T.removeItemAnySlot.isAvailable()) {
            return InventoryHandle.T.removeItemAnySlot.invoke(this.cbProxy);
        }
        return new HashMap<Integer, ItemStack>();
    }

    public int hashCode() {
        return this.cbProxy.hashCode();
    }

    public boolean equals(Object obj) {
        return this.cbProxy.equals(obj);
    }

    public String toString() {
        return this.cbProxy.toString();
    }

    public Location getLocation() {
        return null;
    }

    public ItemStack[] getStorageContents() {
        if (InventoryHandle.T.getStorageContents.isAvailable()) {
            return InventoryHandle.T.getStorageContents.invoke(this.cbProxy);
        }
        return new ItemStack[0];
    }

    public void setStorageContents(ItemStack[] items) throws IllegalArgumentException {
        if (InventoryHandle.T.setStorageContents.isAvailable()) {
            InventoryHandle.T.setStorageContents.invoke(this.cbProxy, items);
        }
    }

    public InventoryHolder getHolder(boolean b) {
        return this.getHolder();
    }

    public boolean isEmpty() {
        int size = this.getSize();
        for (int i = 0; i < size; ++i) {
            if (ItemUtil.isEmpty(this.getItem(i))) continue;
            return false;
        }
        return true;
    }

    public int close() {
        return 0;
    }

    static {
        for (Method m : Inventory.class.getDeclaredMethods()) {
            try {
                InventoryBase.class.getDeclaredMethod(m.getName(), m.getParameterTypes());
            }
            catch (Throwable t) {
                throw new RuntimeException("Method " + m.toString() + " is not implemented in InventoryBase");
            }
        }
    }
}

