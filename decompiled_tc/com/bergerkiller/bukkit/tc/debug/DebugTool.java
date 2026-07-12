/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.nbt.CommonTagCompound
 *  com.bergerkiller.bukkit.common.offline.OfflineWorld
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.wrappers.HumanHand
 *  org.bukkit.ChatColor
 *  org.bukkit.Color
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.debug;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.debug.DebugToolType;
import com.bergerkiller.bukkit.tc.debug.DebugToolTypeRegistry;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZone;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneCache;
import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.WeakHashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DebugTool {
    private static final WeakHashMap<Player, DebounceLogic> debounce = new WeakHashMap();

    public static void showMutexZones(TrainCarts traincarts, Player player) {
        Location loc = player.getEyeLocation();
        List<MutexZone> zones = MutexZoneCache.findNearbyZones(OfflineWorld.of((World)loc.getWorld()), new IntVector3(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), 32);
        if (zones.isEmpty()) {
            return;
        }
        Random r = new Random();
        for (MutexZone zone : zones) {
            if (zone.slot.isAnonymous()) {
                r.setSeed(zone.showDebugColorSeed());
            } else {
                r.setSeed(zone.slot.getName().hashCode());
            }
            Color awt_color = Color.getHSBColor(r.nextFloat(), 1.0f, 1.0f);
            org.bukkit.Color color = org.bukkit.Color.fromRGB((int)awt_color.getRed(), (int)awt_color.getGreen(), (int)awt_color.getBlue());
            zone.showDebug(player, color);
        }
    }

    public static boolean updateToolItem(Player player, CommonItemStack item) {
        return DebugTool.updateToolItem(player, item.toBukkit());
    }

    public static boolean updateToolItem(Player player, ItemStack item) {
        CommonItemStack inMainHand = CommonItemStack.of((ItemStack)HumanHand.getItemInMainHand((HumanEntity)player));
        if (!inMainHand.isEmpty() && inMainHand.hasCustomData() && inMainHand.getCustomData().containsKey((Object)"TrainCartsDebug")) {
            HumanHand.setItemInMainHand((HumanEntity)player, (ItemStack)item);
            return true;
        }
        return false;
    }

    public static boolean onDebugInteract(TrainCarts traincarts, Player player, Block clickedBlock, ItemStack item, boolean isRightClick) {
        return DebugTool.onDebugInteract(traincarts, player, clickedBlock, CommonItemStack.of((ItemStack)item), isRightClick);
    }

    public static boolean onDebugInteract(TrainCarts traincarts, Player player, Block clickedBlock, CommonItemStack item, boolean isRightClick) {
        if (item.isEmpty() || !item.hasCustomData()) {
            return false;
        }
        CommonTagCompound tag = item.getCustomData();
        String debugType = (String)tag.getValue("TrainCartsDebug", String.class);
        if (debugType == null) {
            return false;
        }
        if (!Permission.DEBUG_COMMAND_DEBUG.has((CommandSender)player)) {
            if (DebugTool.debounce(player)) {
                player.sendMessage(ChatColor.RED + "No permission to use this item!");
            }
            return true;
        }
        Optional<DebugToolType> match = DebugToolTypeRegistry.match(debugType);
        if (!match.isPresent()) {
            if (DebugTool.debounce(player)) {
                player.sendMessage(ChatColor.RED + "Item has an unknown debug mode: " + debugType);
            }
            return true;
        }
        match.get().loadMetadata(tag);
        if (!isRightClick && !match.get().handlesLeftClick()) {
            return false;
        }
        if (DebugTool.debounce(player)) {
            match.get().onBlockInteract(traincarts, player, clickedBlock, item, isRightClick);
        }
        return true;
    }

    private static boolean debounce(Player player) {
        return debounce.computeIfAbsent(player, DebounceLogic::new).check();
    }

    private static final class DebounceLogic {
        private int lastActivation = 0;
        private int clickStart = 0;

        public DebounceLogic(Player player) {
        }

        public boolean check() {
            int ticks = CommonUtil.getServerTicks();
            int timeSinceActivation = ticks - this.lastActivation;
            this.lastActivation = ticks;
            if (timeSinceActivation > 10) {
                this.clickStart = ticks;
                return true;
            }
            if (timeSinceActivation == 0) {
                return false;
            }
            return ticks - this.clickStart > 10;
        }
    }
}

