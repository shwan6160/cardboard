/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.collections.EntityMap
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.wrappers.HumanHand
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.chest;

import com.bergerkiller.bukkit.common.collections.EntityMap;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCListener;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.chest.TrainChestItemUtil;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableGroup;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TrainChestListener
implements Listener {
    private static final int INTERACT_TIMEOUT_TICKS = 5;
    private final TrainCarts plugin;
    private final EntityMap<Player, Integer> ticksSinceLastAction = new EntityMap();

    public TrainChestListener(TrainCarts plugin) {
        this.plugin = plugin;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean spamCheck(Player player) {
        int currentTick = CommonUtil.getServerTicks();
        try {
            Integer t = (Integer)this.ticksSinceLastAction.get((Object)player);
            boolean bl = t == null || currentTick - t >= 5;
            return bl;
        }
        finally {
            this.ticksSinceLastAction.put((Object)player, (Object)currentTick);
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        TrainChestItemUtil.SpawnResult result;
        if (TrainCarts.isWorldDisabled(event.getPlayer().getWorld())) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        CommonItemStack heldItem = CommonItemStack.of((ItemStack)HumanHand.getItemInMainHand((HumanEntity)event.getPlayer()));
        if (!TrainChestItemUtil.isItem(heldItem)) {
            return;
        }
        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);
        event.setCancelled(true);
        if (!this.spamCheck(event.getPlayer())) {
            return;
        }
        if (!Permission.COMMAND_STORAGE_CHEST_USE.has((CommandSender)event.getPlayer())) {
            Localization.CHEST_NOPERM.message((CommandSender)event.getPlayer(), new String[0]);
            return;
        }
        SpawnableGroup group = TrainChestItemUtil.getSpawnableGroup(this.plugin, heldItem);
        TrainChestItemUtil.SpawnOptions spawnOptions = new TrainChestItemUtil.SpawnOptions(event.getPlayer());
        spawnOptions.initialSpeed = TrainChestItemUtil.getSpeed(heldItem);
        boolean bl = spawnOptions.tryExtendTrains = !event.getPlayer().isSneaking();
        if (group == null) {
            result = TrainChestItemUtil.SpawnResult.FAIL_EMPTY;
        } else if (!group.checkSpawnPermissions((CommandSender)event.getPlayer())) {
            result = TrainChestItemUtil.SpawnResult.FAIL_NO_PERM;
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            result = TrainChestItemUtil.spawnAtBlock(group, event.getClickedBlock(), spawnOptions);
            if (result == TrainChestItemUtil.SpawnResult.FAIL_NORAIL && (result = TrainChestItemUtil.spawnLookingAt(group, event.getPlayer(), event.getPlayer().getEyeLocation(), spawnOptions)) == TrainChestItemUtil.SpawnResult.FAIL_NORAIL_LOOK) {
                result = TrainChestItemUtil.SpawnResult.FAIL_NORAIL;
            }
        } else {
            result = event.getAction() == Action.RIGHT_CLICK_AIR ? TrainChestItemUtil.spawnLookingAt(group, event.getPlayer(), event.getPlayer().getEyeLocation(), spawnOptions) : TrainChestItemUtil.SpawnResult.FAIL_NORAIL_LOOK;
        }
        if (result == TrainChestItemUtil.SpawnResult.SUCCESS && TrainChestItemUtil.isFiniteSpawns(heldItem)) {
            if (TrainChestItemUtil.isLocked(heldItem)) {
                HumanHand.setItemInMainHand((HumanEntity)event.getPlayer(), (ItemStack)heldItem.clone().subtractAmount(1).toBukkit());
            } else {
                heldItem = heldItem.clone();
                TrainChestItemUtil.clear(heldItem);
                HumanHand.setItemInMainHand((HumanEntity)event.getPlayer(), (ItemStack)heldItem.toBukkit());
            }
        }
        if (result.hasMessage()) {
            String customSpawnMessage = null;
            if (result == TrainChestItemUtil.SpawnResult.SUCCESS) {
                customSpawnMessage = TrainChestItemUtil.getSpawnMessage(heldItem);
            }
            if (customSpawnMessage == null) {
                result.getLocale().message((CommandSender)event.getPlayer(), new String[0]);
            } else if (!customSpawnMessage.isEmpty()) {
                event.getPlayer().sendMessage(customSpawnMessage);
            }
        }
        if (result == TrainChestItemUtil.SpawnResult.SUCCESS) {
            TrainChestItemUtil.playSoundSpawn(event.getPlayer());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        CommonItemStack heldItem = CommonItemStack.of((ItemStack)HumanHand.getItemInMainHand((HumanEntity)event.getPlayer()));
        if (TrainChestItemUtil.isItem(heldItem)) {
            event.setCancelled(true);
            if (!this.spamCheck(event.getPlayer())) {
                return;
            }
            if (!Permission.COMMAND_STORAGE_CHEST_USE.has((CommandSender)event.getPlayer())) {
                Localization.CHEST_NOPERM.message((CommandSender)event.getPlayer(), new String[0]);
                return;
            }
            if (TrainChestItemUtil.isLocked(heldItem)) {
                Localization.CHEST_LOCKED.message((CommandSender)event.getPlayer(), new String[0]);
                return;
            }
            if (!TrainChestItemUtil.isEmpty(heldItem) && TrainChestItemUtil.isFiniteSpawns(heldItem)) {
                Localization.CHEST_FULL.message((CommandSender)event.getPlayer(), new String[0]);
                return;
            }
            MinecartMember<?> member = MinecartMemberStore.getFromEntity(event.getRightClicked());
            if (member == null || member.isUnloaded() || member.getGroup() == null) {
                return;
            }
            if (!member.getProperties().hasOwnership(event.getPlayer())) {
                Localization.EDIT_NOTOWNED.message((CommandSender)event.getPlayer(), new String[0]);
                return;
            }
            heldItem = heldItem.clone();
            TrainChestItemUtil.store(heldItem, member.getGroup());
            HumanHand.setItemInMainHand((HumanEntity)event.getPlayer(), (ItemStack)heldItem.toBukkit());
            Localization.CHEST_PICKUP.message((CommandSender)event.getPlayer(), new String[0]);
            TrainChestItemUtil.playSoundStore(event.getPlayer());
            if (!event.getPlayer().isSneaking() || TrainChestItemUtil.isFiniteSpawns(heldItem)) {
                boolean wasCancelled = TCListener.cancelNextDrops;
                try {
                    TCListener.cancelNextDrops = true;
                    member.getGroup().destroy();
                }
                finally {
                    TCListener.cancelNextDrops = wasCancelled;
                }
            }
            return;
        }
    }
}

