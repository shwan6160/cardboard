/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.logic.ItemVariantListHandler;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.inventory.InventoryBaseImpl;
import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.item.ItemEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;
import com.bergerkiller.generated.org.bukkit.inventory.InventoryViewHandle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class ItemUtil {
    private static final Function<InventoryClickEvent, Inventory> getClickedInventoryFunc = ItemUtil.findGetClickedInventoryFunc();

    public static Inventory getClickedInventory(InventoryClickEvent event) {
        return getClickedInventoryFunc.apply(event);
    }

    public static void closeView(InventoryView view) {
        InventoryViewHandle.createHandle(view).close();
    }

    public static Player getViewPlayer(InventoryView view) {
        return (Player)InventoryViewHandle.createHandle(view).getPlayer();
    }

    public static ItemStack getViewItem(InventoryView view, int index) {
        return InventoryViewHandle.createHandle(view).getItem(index);
    }

    public static void setViewItem(InventoryView view, int index, ItemStack item) {
        InventoryViewHandle.createHandle(view).setItem(index, item);
    }

    public static Inventory getTopInventory(InventoryView view) {
        return InventoryViewHandle.createHandle(view).getTopInventory();
    }

    public static Inventory getBottomInventory(InventoryView view) {
        return InventoryViewHandle.createHandle(view).getBottomInventory();
    }

    private static Function<InventoryClickEvent, Inventory> findGetClickedInventoryFunc() {
        try {
            InventoryClickEvent.class.getDeclaredMethod("getClickedInventory", new Class[0]);
            return InventoryClickEvent::getClickedInventory;
        }
        catch (Throwable throwable) {
            return event -> {
                Inventory topInventory = ItemUtil.getTopInventory(event.getView());
                if (event.getRawSlot() < topInventory.getSize()) {
                    return topInventory;
                }
                return ItemUtil.getBottomInventory(event.getView());
            };
        }
    }

    @Deprecated
    public static ItemStack createPlayerHeadItem(GameProfileHandle gameProfile) {
        return CommonItemStack.createPlayerSkull(gameProfile).toBukkit();
    }

    public static boolean canTransferAll(ItemStack[] from, Inventory to) {
        return ItemUtil.canTransferAll(from, to.getContents());
    }

    public static boolean canTransferAll(ItemStack[] from, ItemStack[] to) {
        InventoryBaseImpl invto = new InventoryBaseImpl(to, true);
        for (ItemStack item : from) {
            CommonItemStack fromItem = CommonItemStack.copyOf(item);
            fromItem.transferTo(invto, -1);
            if (fromItem.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Deprecated
    public static int testTransfer(ItemStack from, Inventory to) {
        return CommonItemStack.of(from).testTransferTo(to);
    }

    @Deprecated
    public static int testTransfer(ItemStack from, ItemStack to) {
        return CommonItemStack.of(from).testTransferTo(CommonItemStack.of(to));
    }

    @Deprecated
    public static int transfer(Inventory from, Inventory to, ItemParser parser, int maxAmount) {
        return CommonItemStack.transfer(from, to, (Predicate<CommonItemStack>)parser, maxAmount);
    }

    public static Inventory cloneInventory(Inventory inventory) {
        return new InventoryBaseImpl(inventory.getContents(), true);
    }

    public static boolean equalsIgnoreAmount(ItemStack item1, ItemStack item2) {
        return item1 == null ? item2 == null : item1.isSimilar(item2);
    }

    public static void removeItems(Inventory inventory, ItemStack item) {
        ItemUtil.removeItems(inventory, item.getType(), MaterialUtil.getRawData(item), item.getAmount());
    }

    public static void removeItems(Inventory inventory, Material type, int data, int amount) {
        int countToRemove = amount < 0 ? Integer.MAX_VALUE : amount;
        for (int i = 0; i < inventory.getSize(); ++i) {
            ItemStack item = inventory.getItem(i);
            if (LogicUtil.nullOrEmpty(item) || item.getType() != type || data != -1 && MaterialUtil.getRawData(item) != data) continue;
            if (item.getAmount() <= countToRemove) {
                countToRemove -= item.getAmount();
                inventory.setItem(i, null);
                continue;
            }
            ItemUtil.subtractAmount(item, countToRemove);
            countToRemove = 0;
            inventory.setItem(i, item);
            break;
        }
    }

    public static ItemStack emptyItem() {
        return ItemUtil.createItem(Material.AIR, 0);
    }

    public static ItemStack createItem(ItemStack item) {
        return CraftItemStackHandle.asCraftCopy(item);
    }

    public static ItemStack createItem(Material type, int amount) {
        ItemStackHandle stack = ItemStackHandle.newInstance(type);
        stack.setAmountField(amount);
        return stack.toBukkit();
    }

    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getAmount() == 0 || item.getType() == Material.AIR;
    }

    public static Item respawnItem(Item item) {
        item.remove();
        ItemEntityHandle oldItemHandle = CommonNMS.getHandle(item);
        ItemEntityHandle newItemHandle = ItemEntityHandle.createNew(oldItemHandle.getWorld(), oldItemHandle.getLocX(), oldItemHandle.getLocY(), oldItemHandle.getLocZ(), oldItemHandle.getItemStack());
        newItemHandle.setFallDistance(oldItemHandle.getFallDistance());
        newItemHandle.setFireTicks(oldItemHandle.getFireTicks());
        newItemHandle.setPickupDelay(oldItemHandle.getPickupDelay());
        newItemHandle.setMotX(oldItemHandle.getMotX());
        newItemHandle.setMotY(oldItemHandle.getMotY());
        newItemHandle.setMotZ(oldItemHandle.getMotZ());
        newItemHandle.setAge(oldItemHandle.getAge());
        newItemHandle.getWorldServer().addEntity(newItemHandle);
        return (Item)Conversion.toItem.convert(newItemHandle.getRaw());
    }

    public static ItemStack[] getClonedContents(Inventory inventory) {
        ItemStack[] rval = new ItemStack[inventory.getSize()];
        for (int i = 0; i < rval.length; ++i) {
            rval[i] = ItemUtil.cloneItem(inventory.getItem(i));
        }
        return rval;
    }

    public static ItemStack cloneItem(ItemStack stack) {
        return stack == null ? null : stack.clone();
    }

    public static ItemStack[] cloneItems(ItemStack[] input) {
        return LogicUtil.cloneAll(input);
    }

    public static void subtractAmount(ItemStack item, int amount) {
        ItemUtil.addAmount(item, -amount);
    }

    public static void addAmount(ItemStack item, int amount) {
        item.setAmount(Math.max(item.getAmount() + amount, 0));
    }

    @Deprecated
    public static ItemStack findItem(Inventory inventory, Material type, int data) {
        ItemStack rval = null;
        int itemData = data;
        Material itemType = type;
        for (ItemStack item : inventory.getContents()) {
            if (LogicUtil.nullOrEmpty(item)) continue;
            if (itemType == null) {
                itemType = item.getType();
            } else if (itemType != item.getType()) continue;
            if (itemData == -1) {
                itemData = MaterialUtil.getRawData(item);
            } else if (MaterialUtil.getRawData(item) != itemData) continue;
            if (rval == null) {
                rval = item.clone();
                continue;
            }
            ItemUtil.addAmount(rval, item.getAmount());
        }
        return rval;
    }

    public static int getItemCount(Inventory inventory, Material type, int data) {
        Stream<CommonItemStack> stream = CommonItemStack.streamOfContents(inventory);
        if (type != null) {
            stream = stream.filter(item -> item.isType(type));
            if (data != -1) {
                stream = stream.filter(item -> MaterialUtil.getRawData(item.toBukkit()) == data);
            }
        }
        return stream.mapToInt(CommonItemStack::getAmount).sum();
    }

    public static int getMaxDurability(Material itemType, int def) {
        ItemHandle item = CommonNMS.getItem(itemType);
        return item == null || !item.usesDurability() ? def : item.getMaxDurability();
    }

    public static int getMaxDurability(ItemStack stack) {
        if (LogicUtil.nullOrEmpty(stack)) {
            return 0;
        }
        return ItemUtil.getMaxDurability(stack.getType(), 0);
    }

    public static boolean hasDurability(Material itemType) {
        ItemHandle item = CommonNMS.getItem(itemType);
        return item != null && item.usesDurability();
    }

    public static boolean hasDurability(ItemStack stack) {
        return !LogicUtil.nullOrEmpty(stack) && ItemUtil.hasDurability(stack.getType());
    }

    public static int getMaxSize(Material itemType, int def) {
        ItemHandle item = CommonNMS.getItem(itemType);
        return item == null ? def : item.getMaxStackSize();
    }

    public static int getMaxSize(ItemStack stack) {
        if (LogicUtil.nullOrEmpty(stack)) {
            return 0;
        }
        return ItemUtil.getMaxSize(stack.getType(), 0);
    }

    public static void setRepairCost(ItemStack stack, int repairCost) {
        CommonNMS.getHandle(stack).setRepairCost(repairCost);
    }

    public static int getRepairCost(ItemStack stack) {
        return CommonNMS.getHandle(stack).getRepairCost();
    }

    @Deprecated
    public static boolean hasDisplayName(ItemStack stack) {
        return CommonItemStack.of(stack).hasCustomName();
    }

    @Deprecated
    public static String getDisplayName(ItemStack stack) {
        return CommonItemStack.of(stack).getDisplayNameMessage();
    }

    @Deprecated
    public static ChatText getDisplayChatText(ItemStack stack) {
        return CommonItemStack.of(stack).getDisplayName();
    }

    @Deprecated
    public static void setDisplayName(ItemStack stack, String displayName) {
        ItemUtil.setDisplayChatText(stack, ChatText.fromMessage(displayName));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Deprecated
    public static void setDisplayChatText(ItemStack stack, ChatText displayName) {
        if (displayName != null) {
            if (!CraftItemStackHandle.T.isAssignableFrom(stack)) throw new RuntimeException("This item is not a CraftItemStack! Please create one using createItem(ItemStack)");
            CommonNMS.getHandle(stack).setCustomName(displayName);
            return;
        } else {
            if (!ItemUtil.hasDisplayName(stack)) return;
            if (!CraftItemStackHandle.T.isAssignableFrom(stack)) throw new RuntimeException("This item is not a CraftItemStack! Please create one using createItem(ItemStack)");
            CommonNMS.getHandle(stack).setCustomName(null);
        }
    }

    @Deprecated
    public static void clearLoreNames(ItemStack itemStack) {
        CommonItemStack.of(itemStack).clearLores();
    }

    @Deprecated
    public static void addLoreName(ItemStack itemStack, String name) {
        CommonItemStack.of(itemStack).addLoreMessage(name);
    }

    @Deprecated
    public static void addLoreChatText(ItemStack itemStack, ChatText lore) {
        CommonItemStack.of(itemStack).addLore(lore);
    }

    public static List<ItemStack> getItemVariants(Material itemType) {
        Object itemHandle = HandleConversion.toItemHandle(itemType);
        if (itemHandle == null) {
            return new ArrayList<ItemStack>(0);
        }
        return ItemVariantListHandler.INSTANCE.getVariants(itemHandle);
    }

    public static List<Material> getItemTypes() {
        ArrayList<Material> result = new ArrayList<Material>(500);
        for (Object itemRawHandle : ItemHandle.getRegistry()) {
            Material type = WrapperConversion.toMaterialFromItemHandle(itemRawHandle);
            if (type == Material.AIR) continue;
            result.add(type);
        }
        return result;
    }
}

