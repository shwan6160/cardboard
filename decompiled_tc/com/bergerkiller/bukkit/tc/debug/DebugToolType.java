/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.nbt.CommonTagCompound
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.debug;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.debug.DebugTool;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface DebugToolType {
    public String getIdentifier();

    public String getTitle();

    public String getDescription();

    public String getInstructions();

    default public boolean handlesLeftClick() {
        return false;
    }

    public void onBlockInteract(TrainCarts var1, Player var2, Block var3, CommonItemStack var4, boolean var5);

    default public void giveToPlayer(Player player) {
        CommonItemStack item = CommonItemStack.create((Material)Material.STICK, (int)1);
        item.updateCustomData(tag -> {
            tag.putValue("TrainCartsDebug", (Object)this.getIdentifier());
            this.saveMetadata((CommonTagCompound)tag);
        });
        item.setCustomNameMessage(this.getTitle());
        item.addLoreMessage(this.getDescription());
        if (DebugTool.updateToolItem(player, item.toBukkit())) {
            player.sendMessage(ChatColor.GREEN + "Debug tool updates to a " + this.getTitle());
            player.sendMessage(ChatColor.YELLOW + this.getDescription());
            return;
        }
        player.getInventory().addItem(new ItemStack[]{item.toBukkit()});
        player.sendMessage(ChatColor.GREEN + "Given a " + this.getTitle());
        player.sendMessage(ChatColor.YELLOW + this.getDescription());
    }

    default public void loadMetadata(CommonTagCompound metadata) {
    }

    default public void saveMetadata(CommonTagCompound metadata) {
    }
}

