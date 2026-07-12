/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.inventory.CommonItemMaterials
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.map.MapDisplay
 *  com.bergerkiller.bukkit.common.nbt.CommonTagCompound
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 */
package com.bergerkiller.bukkit.tc.editor;

import com.bergerkiller.bukkit.common.inventory.CommonItemMaterials;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.tc.editor.TCMapEditor;
import java.util.UUID;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class TCMapControl {
    public static void updateMapItem(Player player, boolean opened) {
        PlayerInventory inv = player.getInventory();
        ItemStack heldItem = inv.getItem(inv.getHeldItemSlot());
        if (!TCMapControl.isTCMapItem(heldItem)) {
            return;
        }
        TCMapControl.updateMapItem(player, heldItem, opened);
    }

    public static void updateMapItem(Player player, ItemStack item, boolean opened) {
        TCMapControl.updateMapItem(player, CommonItemStack.of((ItemStack)item), opened);
    }

    public static void updateMapItem(Player player, CommonItemStack item, boolean opened) {
        if (!TCMapControl.isTCMapItem(item) || !player.isValid()) {
            return;
        }
        PlayerInventory inv = player.getInventory();
        UUID uuid = item.getCustomData().getUUID("editor");
        for (int i = 0; i < inv.getSize(); ++i) {
            CommonItemStack playerItem = CommonItemStack.of((ItemStack)inv.getItem(i));
            if (!TCMapControl.isTCMapItem(playerItem) || !playerItem.getCustomData().getUUID("editor").equals(uuid)) continue;
            CommonItemStack newItem = playerItem.clone();
            if (opened) {
                newItem.setType(CommonItemMaterials.FILLED_MAP);
            } else {
                newItem.setType(CommonItemMaterials.EMPTY_MAP);
            }
            inv.setItem(i, newItem.toBukkit());
            return;
        }
    }

    public static boolean isTCMapItem(ItemStack item) {
        return TCMapControl.isTCMapItem(CommonItemStack.of((ItemStack)item));
    }

    public static boolean isTCMapItem(CommonItemStack item) {
        if (item == null || !item.isType(CommonItemMaterials.FILLED_MAP) && item.isType(CommonItemMaterials.EMPTY_MAP)) {
            return false;
        }
        CommonTagCompound tag = item.getCustomData();
        if (tag == null) {
            return false;
        }
        return tag.getUUID("editor") != null && ((String)tag.getValue("plugin", (Object)"")).equals("TrainCarts");
    }

    public static ItemStack createTCMapItem() {
        CommonItemStack item = CommonItemStack.of((ItemStack)MapDisplay.createMapItem(TCMapEditor.class));
        item.setType(CommonItemMaterials.EMPTY_MAP);
        item.setCustomNameMessage("TrainCarts Editor");
        item.updateCustomData(tag -> {
            tag.putValue("plugin", (Object)"TrainCarts");
            tag.putUUID("editor", UUID.randomUUID());
        });
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        return item.toBukkit();
    }
}

