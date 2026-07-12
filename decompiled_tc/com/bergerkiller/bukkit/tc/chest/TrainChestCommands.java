/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.FlagYielding
 *  com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Greedy
 *  com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Quoted
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Flag
 *  com.bergerkiller.bukkit.common.utils.ItemUtil
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 *  com.bergerkiller.bukkit.common.wrappers.HumanHand
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.chest;

import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.FlagYielding;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Greedy;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Quoted;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Flag;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.chest.TrainChestItemUtil;
import com.bergerkiller.bukkit.tc.commands.Commands;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandRequiresMultiplePermissions;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandRequiresPermission;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableGroup;
import com.bergerkiller.bukkit.tc.exception.command.NoTrainStorageChestItemException;
import com.bergerkiller.bukkit.tc.utils.FormattedSpeed;
import java.util.function.Consumer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TrainChestCommands {
    @CommandRequiresPermission(value=Permission.COMMAND_STORAGE_CHEST_CREATE)
    @Command(value="train chest give <target_player>")
    @CommandDescription(value="Gives a pre-configured train-storing chest item to another player")
    private void commandGiveChestItemToPlayer(TrainCarts plugin, CommandSender sender, @Argument(value="target_player", description="Who to give it to", suggestions="targetplayer") String targetPlayerName, @Flag(value="train", description="Initial train spawn configuration", suggestions="trainspawnpattern") String spawnConfig, @Flag(value="name", description="Display name of the train in the chest item") String name, @Flag(value="locked", description="Whether the train in the item can be changed") boolean locked, @Flag(value="finite", description="Whether to make the item empty when used") boolean finite, @Quoted @Flag(value="spawnmessage", description="Sets a custom successful spawn message") String spawnMessage) {
        Player targetPlayer = Util.findPlayer(sender, targetPlayerName);
        if (targetPlayer == null) {
            return;
        }
        ItemStack item = TrainChestItemUtil.createItem();
        if (spawnConfig != null && !spawnConfig.isEmpty()) {
            if (sender instanceof Player && !SpawnableGroup.parse(plugin, spawnConfig).checkSpawnPermissions((CommandSender)((Player)sender))) {
                Localization.SPAWN_FORBIDDEN_CONTENTS.message(sender, new String[0]);
                return;
            }
            TrainChestItemUtil.store(item, spawnConfig);
        }
        if (name != null) {
            TrainChestItemUtil.setName(item, name);
        }
        if (locked) {
            TrainChestItemUtil.setLocked(item, locked);
        }
        if (finite) {
            TrainChestItemUtil.setFiniteSpawns(item, finite);
        }
        if (spawnMessage != null) {
            TrainChestItemUtil.setSpawnMessage(item, StringUtil.ampToColor((String)spawnMessage));
        }
        targetPlayer.getInventory().addItem(new ItemStack[]{item});
        if (targetPlayer == sender) {
            Localization.CHEST_GIVE.message(sender, new String[0]);
        } else {
            Localization.CHEST_GIVE_TO.message(sender, new String[]{targetPlayer.getName()});
            Localization.CHEST_GIVE.message((CommandSender)targetPlayer, new String[0]);
        }
    }

    @CommandRequiresPermission(value=Permission.COMMAND_STORAGE_CHEST_CREATE)
    @Command(value="train chest [spawnconfig]")
    @CommandDescription(value="Gives a new train-storing chest item to the sender, train information to store can be specified")
    private void commandGiveChestItem(TrainCarts plugin, Player sender, @Argument(value="spawnconfig") @Greedy String spawnConfig) {
        ItemStack item = TrainChestItemUtil.createItem();
        if (spawnConfig != null && !spawnConfig.isEmpty()) {
            if (!SpawnableGroup.parse(plugin, spawnConfig).checkSpawnPermissions((CommandSender)sender)) {
                Localization.SPAWN_FORBIDDEN_CONTENTS.message((CommandSender)sender, new String[0]);
                return;
            }
            TrainChestItemUtil.store(item, spawnConfig);
        }
        sender.getInventory().addItem(new ItemStack[]{item});
        Localization.CHEST_GIVE.message((CommandSender)sender, new String[0]);
    }

    @CommandRequiresPermission(value=Permission.COMMAND_STORAGE_CHEST_CREATE)
    @Command(value="train chest set [spawnconfig]")
    @CommandDescription(value="Clears the train-storing chest item the player is currently holding")
    private void commandSetChestItem(TrainCarts plugin, Player player, @Argument(value="spawnconfig", suggestions="trainspawnpattern") @Greedy String spawnConfig) {
        if (spawnConfig != null && !spawnConfig.isEmpty() && !SpawnableGroup.parse(plugin, spawnConfig).checkSpawnPermissions((CommandSender)player)) {
            Localization.SPAWN_FORBIDDEN_CONTENTS.message((CommandSender)player, new String[0]);
            return;
        }
        this.updateChestItemInInventory(player, item -> TrainChestItemUtil.store(item, spawnConfig == null ? "" : spawnConfig));
    }

    @CommandRequiresPermission(value=Permission.COMMAND_STORAGE_CHEST_CREATE)
    @Command(value="train chest clear")
    @CommandDescription(value="Clears the train-storing chest item the player is currently holding")
    private void commandClearChestItem(Player player) {
        this.updateChestItemInInventory(player, TrainChestItemUtil::clear);
    }

    @CommandRequiresPermission(value=Permission.COMMAND_STORAGE_CHEST_CREATE)
    @Command(value="train chest lock")
    @CommandDescription(value="Locks the train-storing chest item so it can not pick up trains by right-clicking")
    private void commandLockChestItem(Player player) {
        this.updateChestItemInInventory(player, item -> TrainChestItemUtil.setLocked(item, true));
    }

    @CommandRequiresPermission(value=Permission.COMMAND_STORAGE_CHEST_CREATE)
    @Command(value="train chest unlock")
    @CommandDescription(value="Unlocks the train-storing chest item so it can pick up trains by right-clicking again")
    private void commandUnlockChestItem(Player player) {
        this.updateChestItemInInventory(player, item -> TrainChestItemUtil.setLocked(item, false));
    }

    @CommandRequiresPermission(value=Permission.COMMAND_STORAGE_CHEST_CREATE)
    @Command(value="train chest finite <finite_spawns>")
    @CommandDescription(value="Sets whether the train-storing chest item has only finite spawns, and becomes empty after spawning")
    private void commandChestItemSetFiniteSpawns(Player player, @Argument(value="finite_spawns") boolean finite) {
        this.updateChestItemInInventory(player, item -> TrainChestItemUtil.setFiniteSpawns(item, finite));
    }

    @CommandRequiresPermission(value=Permission.COMMAND_STORAGE_CHEST_CREATE)
    @Command(value="train chest speed <speed>")
    @CommandDescription(value="Sets the initial speed of the train when spawning")
    private void commandChestItemSetSpeed(Player player, @Argument(value="speed") FormattedSpeed speed) {
        this.updateChestItemInInventory(player, item -> TrainChestItemUtil.setSpeed(item, speed.getValue()));
    }

    @CommandRequiresPermission(value=Permission.COMMAND_STORAGE_CHEST_CREATE)
    @Command(value="train chest name <name>")
    @CommandDescription(value="Sets a descriptive name for the train-storing chest item")
    private void commandNameChestItem(Player player, @Argument(value="name") String name) {
        this.updateChestItemInInventory(player, item -> TrainChestItemUtil.setName(item, name));
    }

    @CommandRequiresPermission(value=Permission.COMMAND_STORAGE_CHEST_CREATE)
    @Command(value="train chest spawnmessage <message>")
    @CommandDescription(value="Sets the message displayed when successfully spawning using the chest item")
    private void commandSetShowMessage(Player player, @Greedy @Argument(value="message") String message) {
        this.updateChestItemInInventory(player, item -> TrainChestItemUtil.setSpawnMessage(item, StringUtil.ampToColor((String)message)));
    }

    @CommandRequiresPermission(value=Permission.COMMAND_STORAGE_CHEST_CREATE)
    @Command(value="train chest spawnmessage DEFAULT")
    @CommandDescription(value="Resets the message displayed when successfully spawning using the chest item")
    private void commandSetDefaultShowMessage(Player player) {
        this.updateChestItemInInventory(player, item -> TrainChestItemUtil.setSpawnMessage(item, null));
    }

    @CommandRequiresMultiplePermissions(value={@CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_IMPORT), @CommandRequiresPermission(value=Permission.COMMAND_STORAGE_CHEST_CREATE)})
    @Command(value="train chest import <url>")
    @CommandDescription(value="Imports a saved train into the chest item from an online hastebin server by url")
    private void commandImportChestItem(Player player, TrainCarts plugin, @Greedy @FlagYielding @Argument(value="url") String url, @Flag(value="force") boolean force, @Flag(value="import-models") boolean importModels) {
        ItemStack item_when_started = HumanHand.getItemInMainHand((HumanEntity)player);
        Commands.importTrain(plugin, (CommandSender)player, url, config -> {
            Commands.importTrainUsedModels(plugin, (CommandSender)player, config, importModels, force);
            if (TrainChestItemUtil.isItem(item_when_started)) {
                TrainChestItemUtil.store(item_when_started, config);
            } else {
                ItemStack newItem = TrainChestItemUtil.createItem();
                TrainChestItemUtil.store(newItem, config);
                player.getInventory().addItem(new ItemStack[]{newItem});
                Localization.CHEST_GIVE.message((CommandSender)player, new String[0]);
            }
            Localization.CHEST_IMPORTED.message((CommandSender)player, new String[0]);
        });
    }

    @CommandRequiresMultiplePermissions(value={@CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_EXPORT), @CommandRequiresPermission(value=Permission.COMMAND_STORAGE_CHEST_CREATE)})
    @Command(value="train chest export")
    @CommandDescription(value="Exports the train configuration in the chest item to a hastebin server")
    private void commandExportChestItem(TrainCarts plugin, Player player) {
        ItemStack item = HumanHand.getItemInMainHand((HumanEntity)player);
        if (!TrainChestItemUtil.isItem(item)) {
            throw new NoTrainStorageChestItemException();
        }
        SpawnableGroup spawnable = TrainChestItemUtil.getSpawnableGroup(plugin, item);
        if (spawnable == null) {
            Localization.CHEST_SPAWN_EMPTY.message((CommandSender)player, new String[0]);
            return;
        }
        Commands.exportTrain((CommandSender)player, spawnable.getSavedName(), spawnable.getFullConfig());
    }

    private void updateChestItemInInventory(Player player, Consumer<ItemStack> consumer) {
        ItemStack item = HumanHand.getItemInMainHand((HumanEntity)player);
        if (!TrainChestItemUtil.isItem(item)) {
            throw new NoTrainStorageChestItemException();
        }
        item = ItemUtil.cloneItem((ItemStack)item);
        consumer.accept(item);
        HumanHand.setItemInMainHand((HumanEntity)player, (ItemStack)item);
        Localization.CHEST_UPDATE.message((CommandSender)player, new String[0]);
    }
}

