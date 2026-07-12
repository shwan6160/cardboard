/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.internal.hooks;

import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.org.bukkit.inventory.InventoryHandle;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

@ClassHook.HookPackage(value="net.minecraft.server")
@ClassHook.HookImportList(value={@ClassHook.HookImport(value="org.bukkit.craftbukkit.entity.CraftHumanEntity"), @ClassHook.HookImport(value="net.minecraft.world.item.ItemStack")})
@ClassHook.HookLoadVariables(value="com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER")
public class IInventoryProxyHook
extends ClassHook<IInventoryProxyHook> {
    private final Inventory inventory;

    public IInventoryProxyHook(Inventory inventory) {
        this.inventory = inventory;
    }

    @ClassHook.HookMethod(value="public abstract int getSize:???()")
    public int getSize() {
        return this.inventory.getSize();
    }

    @ClassHook.HookMethod(value="public abstract int getMaxStackSize()")
    public int getMaxStackSize() {
        return this.inventory.getMaxStackSize();
    }

    @ClassHook.HookMethod(value="public abstract void setMaxStackSize(int paramInt)")
    public void setMaxStackSize(int size) {
        this.inventory.setMaxStackSize(size);
    }

    @ClassHook.HookMethod(value="public abstract void setItem(int paramInt, ItemStack paramItemStack)")
    public void setItem(int index, Object nmsItemStack) {
        this.inventory.setItem(index, WrapperConversion.toItemStack(nmsItemStack));
    }

    @ClassHook.HookMethod(value="public abstract ItemStack getItem(int paramInt)")
    public Object getItem(int index) {
        return HandleConversion.toItemStackHandle(this.inventory.getItem(index));
    }

    @ClassHook.HookMethod(value="public abstract void clear:???()")
    public void clear() {
        this.inventory.clear();
    }

    @ClassHook.HookMethodCondition(value="version >= 1.11")
    @ClassHook.HookMethod(value="public abstract List<ItemStack> getContents()")
    public List<?> getContents() {
        return new ConvertingList<Object>(Arrays.asList(this.inventory.getContents()), DuplexConversion.itemStack.reverse());
    }

    @ClassHook.HookMethodCondition(value="version <= 1.10.2")
    @ClassHook.HookMethod(value="public abstract ItemStack[] getContents()")
    public Object[] getContents_old() {
        return LogicUtil.toArray(this.getContents(), ItemStackHandle.T.getType());
    }

    @ClassHook.HookMethod(value="public abstract List<org.bukkit.entity.HumanEntity> getViewers()")
    public List<?> getViewers() {
        return new ConvertingList<Object>(this.inventory.getViewers(), DuplexConversion.entity.reverse());
    }

    @ClassHook.HookMethod(value="public abstract org.bukkit.inventory.InventoryHolder getOwner()")
    public InventoryHolder getOwner() {
        return this.inventory.getHolder();
    }

    @ClassHook.HookMethodCondition(value="version >= 1.10.2")
    @ClassHook.HookMethod(value="public abstract org.bukkit.Location getLocation()")
    public Location getLocation() {
        if (InventoryHandle.T.getLocation.isAvailable()) {
            return InventoryHandle.T.getLocation.invoke(this.inventory);
        }
        return null;
    }

    @ClassHook.HookMethod(value="public ItemStack splitStack:???(int i, int j)")
    public Object splitStack(int i, int j) {
        if (i < 0 || i >= this.inventory.getSize() || j <= 0) {
            return ItemStackHandle.EMPTY_ITEM.getRaw();
        }
        ItemStack item = this.inventory.getItem(i);
        if (ItemUtil.isEmpty(item)) {
            return ItemStackHandle.EMPTY_ITEM.getRaw();
        }
        ItemStackHandle nmsItem = ItemStackHandle.fromBukkit(item);
        if (nmsItem == null) {
            return ItemStackHandle.EMPTY_ITEM.getRaw();
        }
        return ItemStackHandle.getRaw(nmsItem.cloneAndSubtract(j));
    }

    @ClassHook.HookMethod(value="public ItemStack splitWithoutUpdate:???(int i)")
    public Object splitWithoutUpdate(int i) {
        if (i < 0 || i >= this.inventory.getSize()) {
            return ItemStackHandle.EMPTY_ITEM.getRaw();
        }
        ItemStack item = this.inventory.getItem(i);
        if (ItemUtil.isEmpty(item)) {
            return ItemStackHandle.EMPTY_ITEM.getRaw();
        }
        this.setItem(i, ItemStackHandle.EMPTY_ITEM.getRaw());
        return HandleConversion.toItemStackHandle(item);
    }

    @ClassHook.HookMethod(value="public abstract void update:???()")
    public void update() {
    }

    @ClassHook.HookMethod(value="public abstract void onOpen(CraftHumanEntity paramCraftHumanEntity)")
    public void onOpen(Object entity) {
    }

    @ClassHook.HookMethod(value="public abstract void onClose(CraftHumanEntity paramCraftHumanEntity)")
    public void onClose(Object entity) {
    }

    @ClassHook.HookMethod(value="public abstract void startOpen:???(net.minecraft.world.entity.player.Player paramEntityHuman)")
    public void startOpen(Object entityHuman) {
    }

    @ClassHook.HookMethodCondition(value="version < 1.18")
    @ClassHook.HookMethod(value="public abstract void stopOpen:closeContainer(net.minecraft.world.entity.player.Player paramEntityHuman)")
    public void closeContainerV1(Object entityHuman) {
    }

    @ClassHook.HookMethodCondition(value="version >= 1.18 && version < 1.21.9")
    @ClassHook.HookMethod(value="public abstract void stopOpen(net.minecraft.world.entity.player.Player paramEntityHuman)")
    public void closeContainerV2(Object entityHuman) {
    }

    @ClassHook.HookMethodCondition(value="version >= 1.21.9")
    @ClassHook.HookMethod(value="public abstract void stopOpen(net.minecraft.world.entity.ContainerUser containerUser)")
    public void closeContainerV3(Object containerUser) {
    }

    @ClassHook.HookMethodCondition(value="version <= 1.13.2")
    @ClassHook.HookMethod(value="public abstract int getProperty:???(int key)")
    public int getProperty(int key) {
        return 0;
    }

    @ClassHook.HookMethodCondition(value="version <= 1.13.2")
    @ClassHook.HookMethod(value="public abstract void setProperty:???(int key, int value)")
    public void setProperty(int key, int value) {
    }

    @ClassHook.HookMethodCondition(value="version <= 1.13.2")
    @ClassHook.HookMethod(value="public abstract int someFunction:???()")
    public int someFunction() {
        return 0;
    }

    @ClassHook.HookMethod(value="public abstract boolean canOpen:???(net.minecraft.world.entity.player.Player paramEntityHuman)")
    public boolean canOpen(Object human) {
        return true;
    }

    @ClassHook.HookMethod(value="public abstract boolean canStoreItem:???(int paramInt, ItemStack paramItemStack)")
    public boolean canStoreItem(int paramInt, Object paramItemStack) {
        return true;
    }

    @ClassHook.HookMethodCondition(value="version >= 1.11 && version <= 1.14.4")
    @ClassHook.HookMethod(value="public abstract boolean isNotEmptyOpt:???()")
    public boolean isNotEmpty() {
        return true;
    }
}

