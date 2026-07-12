/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.MessageBuilder
 *  com.bergerkiller.bukkit.common.cloud.CloudLocalizedException
 *  com.bergerkiller.bukkit.common.cloud.parsers.QuotedArgumentParser
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.dep.cloud.CommandManager
 *  com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.FlagYielding
 *  com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Greedy
 *  com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Quoted
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Flag
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.suggestion.Suggestions
 *  com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ParserParameters
 *  com.bergerkiller.bukkit.common.dep.cloud.services.type.ConsumerService
 *  com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider$Strings
 *  com.bergerkiller.bukkit.common.dep.typetoken.TypeToken
 *  com.bergerkiller.bukkit.common.localization.ILocalizationEnum
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.tc.commands;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.cloud.CloudLocalizedException;
import com.bergerkiller.bukkit.common.cloud.parsers.QuotedArgumentParser;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.FlagYielding;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Greedy;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Quoted;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Flag;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.suggestion.Suggestions;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserParameters;
import com.bergerkiller.bukkit.common.dep.cloud.services.type.ConsumerService;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import com.bergerkiller.bukkit.common.localization.ILocalizationEnum;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.commands.Commands;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandRequiresMultiplePermissions;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandRequiresPermission;
import com.bergerkiller.bukkit.tc.commands.annotations.SavedTrainImplicitlyCreated;
import com.bergerkiller.bukkit.tc.commands.annotations.SavedTrainRequiresAccess;
import com.bergerkiller.bukkit.tc.exception.IllegalNameException;
import com.bergerkiller.bukkit.tc.properties.SavedClaim;
import com.bergerkiller.bukkit.tc.properties.SavedTrainProperties;
import com.bergerkiller.bukkit.tc.properties.SavedTrainPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.standard.type.TrainNameFormat;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SavedTrainCommands {
    public static final Function<ArgumentParser<?, ?>, QuotedArgumentParser<?, ?>> TO_QUOTED_ARGUMENT_PARSER = SavedTrainCommands.getToQuotedArgumentParserMethod();

    private static Function<ArgumentParser<?, ?>, QuotedArgumentParser<?, ?>> getToQuotedArgumentParserMethod() {
        if (Common.hasCapability((String)"Common:Cloud:QuotedArgumentParserFromParser")) {
            return QuotedArgumentParser::getFromParser;
        }
        return parser -> {
            if (parser == null) {
                return null;
            }
            if (!parser.getClass().getName().equals("com.bergerkiller.bukkit.common.cloud.parsers.QuotedArgumentParserProxy")) {
                return null;
            }
            try {
                Field f = parser.getClass().getDeclaredField("parser");
                f.setAccessible(true);
                return (QuotedArgumentParser)f.get(parser);
            }
            catch (Throwable t) {
                return null;
            }
        };
    }

    @Suggestions(value="savedtrainmodules")
    public List<String> getSavedTrainModuleNames(CommandContext<CommandSender> context, String input) {
        TrainCarts plugin = (TrainCarts)((Object)context.inject(TrainCarts.class).get());
        return new ArrayList<String>(plugin.getSavedTrains().getModuleNames());
    }

    @Suggestions(value="savedtrainname")
    public List<String> getSavedTrainNames(CommandContext<CommandSender> context, String input) {
        TrainCarts plugin = (TrainCarts)((Object)context.inject(TrainCarts.class).get());
        return plugin.getSavedTrains().getNames();
    }

    public void init(CommandManager<CommandSender> manager) {
        manager.registerCommandPostProcessor(postProcessContext -> {
            CommandContext context = postProcessContext.commandContext();
            Object raw_arg = context.getOrDefault("savedtrainname", null);
            if (!(raw_arg instanceof SavedTrainProperties)) {
                return;
            }
            SavedTrainPropertiesParser parser = postProcessContext.command().components().stream().map(CommandComponent::parser).map(TO_QUOTED_ARGUMENT_PARSER).filter(SavedTrainPropertiesParser.class::isInstance).map(SavedTrainPropertiesParser.class::cast).findFirst().orElse(null);
            if (parser == null) {
                return;
            }
            SavedTrainProperties savedTrain = (SavedTrainProperties)raw_arg;
            if (savedTrain.isNone()) {
                if (parser.isImplicitlyCreated()) {
                    TrainCarts plugin = (TrainCarts)((Object)((Object)context.inject(TrainCarts.class).get()));
                    try {
                        savedTrain = plugin.getSavedTrains().setConfig(savedTrain.getName(), new ConfigurationNode());
                        context.set("savedtrainname", (Object)savedTrain);
                        if (TCConfig.claimNewSavedTrains && context.sender() instanceof Player) {
                            savedTrain.setClaims(Collections.singleton(new SavedClaim((OfflinePlayer)((Player)context.sender()))));
                        }
                    }
                    catch (IllegalNameException e) {
                        Localization.COMMAND_SAVEDTRAIN_INVALID_NAME.message((CommandSender)context.sender(), new String[]{savedTrain.getName()});
                        ConsumerService.interrupt();
                    }
                } else {
                    Localization.COMMAND_SAVEDTRAIN_NOTFOUND.message((CommandSender)context.sender(), new String[]{savedTrain.getName()});
                    ConsumerService.interrupt();
                }
            } else if (parser.isMustHaveAccess()) {
                CommandSender sender = (CommandSender)context.sender();
                if (savedTrain.hasPermission(sender)) {
                    return;
                }
                boolean force = context.flags().hasFlag("force");
                if (!this.checkAccess(sender, savedTrain, force)) {
                    ConsumerService.interrupt();
                }
            }
        });
        manager.parserRegistry().registerAnnotationMapper(SavedTrainRequiresAccess.class, (a, typeToken) -> ParserParameters.single(SavedTrainRequiresAccess.PARAM, (Object)Boolean.TRUE));
        manager.parserRegistry().registerAnnotationMapper(SavedTrainImplicitlyCreated.class, (a, typeToken) -> ParserParameters.single(SavedTrainImplicitlyCreated.PARAM, (Object)Boolean.TRUE));
        manager.parserRegistry().registerParserSupplier(TypeToken.get(SavedTrainProperties.class), parameters -> {
            boolean access = (Boolean)parameters.get(SavedTrainRequiresAccess.PARAM, (Object)Boolean.FALSE);
            boolean implicitlyCreated = (Boolean)parameters.get(SavedTrainImplicitlyCreated.PARAM, (Object)Boolean.FALSE);
            return new SavedTrainPropertiesParser(access, implicitlyCreated).createParser();
        });
    }

    @Command(value="savedtrain")
    @CommandDescription(value="Shows command usage of /savedtrain, lists saved trains")
    @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST)
    private void commandUsage(CommandSender sender, TrainCarts plugin) {
        sender.sendMessage(ChatColor.YELLOW + "Use /savedtrain <trainname> [command] to modify saved trains");
        sender.sendMessage(ChatColor.YELLOW + "Use /savedtrain list to list all trains");
        this.commandShowInfo(sender, plugin, false, null);
    }

    @Command(value="savedtrain <savedtrainname> info")
    @CommandDescription(value="Shows detailed information about a saved train")
    @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST)
    private void commandShowInfo(CommandSender sender, @Argument(value="savedtrainname") SavedTrainProperties savedTrain) {
        MessageBuilder builder = new MessageBuilder();
        builder.newLine();
        builder.green(new Object[]{"Properties of saved train '"}).white(new Object[]{savedTrain.getName()}).green(new Object[]{"':"}).newLine();
        if (!savedTrain.getModule().isDefault()) {
            builder.yellow(new Object[]{"Stored in module: "}).white(new Object[]{savedTrain.getModule().getName()}).newLine();
        }
        if (savedTrain.hasSpawnPattern()) {
            builder.yellow(new Object[]{"Spawning this saved train will spawn other trains:"}).newLine();
            builder.yellow(new Object[]{"Pattern: "}).white(new Object[]{savedTrain.getSpawnPattern()}).newLine();
            builder.yellow(new Object[]{"Reversed: "}).white(new Object[]{(Boolean)savedTrain.getConfig().getOrDefault("flipped", (Object)false) != false ? "Yes" : "No"}).newLine();
        } else {
            builder.yellow(new Object[]{"Number of carts: "}).white(new Object[]{savedTrain.getNumberOfCarts()}).newLine();
            builder.yellow(new Object[]{"Number of seats: "}).white(new Object[]{savedTrain.getNumberOfSeats()}).newLine();
            builder.yellow(new Object[]{"Total train length: "}).white(new Object[]{savedTrain.getTotalTrainLength()}).newLine();
        }
        builder.yellow(new Object[]{"Claimed by: "});
        SavedClaim.buildClaimList(builder, savedTrain.getClaims());
        builder.send(sender);
    }

    @Command(value="savedtrain <savedtrainname> defaultmodule")
    @CommandDescription(value="Moves a saved train to the default module")
    @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST)
    private void commandSetDefaultModule(CommandSender sender, TrainCarts plugin, @SavedTrainRequiresAccess @Argument(value="savedtrainname") SavedTrainProperties savedTrain) {
        if (savedTrain.getModule().isDefault()) {
            sender.sendMessage(ChatColor.YELLOW + "Train '" + savedTrain.getName() + "' is already stored in the default module");
        } else {
            plugin.getSavedTrains().setModuleNameOfTrain(savedTrain.getName(), null);
            sender.sendMessage(ChatColor.GREEN + "Train '" + savedTrain.getName() + "' is now stored in the default module!");
        }
    }

    @Command(value="savedtrain <savedtrainname> module <newmodulename>")
    @CommandDescription(value="Moves a saved train to a new or existing module")
    @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST)
    private void commandSetModule(CommandSender sender, TrainCarts plugin, @SavedTrainRequiresAccess @Argument(value="savedtrainname") SavedTrainProperties savedTrain, @Argument(value="newmodulename", suggestions="savedtrainmodules") String newModuleName, @Flag(value="force", description="Force changing the module for a train claimed by someone else") boolean force) {
        if (newModuleName.isEmpty()) {
            this.commandSetDefaultModule(sender, plugin, savedTrain);
        } else if (newModuleName.equals(savedTrain.getModule().getName())) {
            sender.sendMessage(ChatColor.YELLOW + "Train '" + savedTrain.getName() + "' is already stored in module '" + newModuleName + "'");
        } else {
            plugin.getSavedTrains().setModuleNameOfTrain(savedTrain.getName(), newModuleName);
            sender.sendMessage(ChatColor.GREEN + "Train '" + savedTrain.getName() + "' is now stored in module '" + newModuleName + "'!");
        }
    }

    @Command(value="savedtrain <savedtrainname> export")
    @CommandDescription(value="Exports the saved train configuration to a hastebin server")
    @CommandRequiresMultiplePermissions(value={@CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST), @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_EXPORT)})
    private void commandExport(CommandSender sender, @Argument(value="savedtrainname") SavedTrainProperties savedTrain) {
        Commands.exportTrain(sender, savedTrain.getName(), savedTrain.getExportedConfig());
    }

    @Command(value="savedtrain <savedtrainname> rename <newsavedtrainname>")
    @CommandDescription(value="Renames a saved train")
    @CommandRequiresMultiplePermissions(value={@CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST), @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_RENAME)})
    private void commandRename(CommandSender sender, TrainCarts plugin, @SavedTrainRequiresAccess @Argument(value="savedtrainname") SavedTrainProperties savedTrain, @Quoted @Argument(value="newsavedtrainname") String newSavedTrainName, @Flag(value="force") boolean force) {
        if (savedTrain.getName().equals(newSavedTrainName)) {
            sender.sendMessage(ChatColor.RED + "The new name is the same as the current name");
            return;
        }
        if (!Commands.checkSavePermissionsOverwrite(plugin, sender, newSavedTrainName, force)) {
            return;
        }
        if (!savedTrain.toSpawnableGroup().checkSpawnPermissions(sender)) {
            Localization.COMMAND_SAVE_FORBIDDEN_CONTENTS.message(sender, new String[0]);
            return;
        }
        String oldName = savedTrain.getName();
        plugin.getSavedTrains().rename(oldName, newSavedTrainName);
        sender.sendMessage(ChatColor.YELLOW + "Saved train '" + ChatColor.WHITE + oldName + ChatColor.YELLOW + "' has been renamed to '" + ChatColor.WHITE + newSavedTrainName + ChatColor.YELLOW + "'!");
    }

    @Command(value="savedtrain <savedtrainname> copy <targetsavedtrainname>")
    @CommandDescription(value="Copies an existing saved train and saves it as the target saved train name")
    @CommandRequiresMultiplePermissions(value={@CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST), @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_COPY)})
    private void commandCopy(CommandSender sender, TrainCarts plugin, @Argument(value="savedtrainname") SavedTrainProperties savedTrain, @Quoted @Argument(value="targetsavedtrainname", suggestions="savedtrainname") String targetSavedTrainName, @Flag(value="force") boolean force) {
        if (savedTrain.getName().equals(targetSavedTrainName)) {
            sender.sendMessage(ChatColor.RED + "The target name is the same as the source name");
            return;
        }
        if (!Commands.checkSavePermissionsOverwrite(plugin, sender, targetSavedTrainName, force)) {
            return;
        }
        if (!savedTrain.toSpawnableGroup().checkSpawnPermissions(sender)) {
            Localization.COMMAND_SAVE_FORBIDDEN_CONTENTS.message(sender, new String[0]);
            return;
        }
        try {
            plugin.getSavedTrains().setConfig(targetSavedTrainName, savedTrain.getConfig().clone());
        }
        catch (IllegalNameException e) {
            Localization.COMMAND_INPUT_NAME_INVALID.message(sender, new String[]{targetSavedTrainName});
            return;
        }
        sender.sendMessage(ChatColor.YELLOW + "Saved train '" + ChatColor.WHITE + savedTrain.getName() + ChatColor.YELLOW + "' copied and saved as '" + ChatColor.WHITE + targetSavedTrainName + ChatColor.YELLOW + "'!");
    }

    @Command(value="savedtrain <savedtrainname> reverse")
    @CommandDescription(value="Reverse and flips the carts so it is moving in reverse when spawned")
    @CommandRequiresMultiplePermissions(value={@CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST), @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_REVERSE)})
    private void commandReverse(CommandSender sender, @SavedTrainRequiresAccess @Argument(value="savedtrainname") SavedTrainProperties savedTrain, @Flag(value="force") boolean force) {
        savedTrain.reverse();
        sender.sendMessage(ChatColor.GREEN + "Saved train '" + ChatColor.WHITE + savedTrain.getName() + ChatColor.GREEN + "' has been reversed!");
    }

    @Command(value="savedtrain <savedtrainname> lockorientation <locked>")
    @CommandDescription(value="Sets whether the train orientation is locked, so future saved can't change it")
    @CommandRequiresMultiplePermissions(value={@CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST), @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_REVERSE)})
    private void commandLockOrientation(CommandSender sender, @SavedTrainRequiresAccess @Argument(value="savedtrainname") SavedTrainProperties savedTrain, @Argument(value="locked") boolean locked, @Flag(value="force") boolean force) {
        savedTrain.setOrientationLocked(locked);
        sender.sendMessage(ChatColor.GREEN + "Saved train '" + ChatColor.WHITE + savedTrain.getName() + ChatColor.GREEN + "' spawn orientation is now " + (locked ? ChatColor.RED + "LOCKED" : ChatColor.GREEN + "UNLOCKED"));
        if (locked) {
            sender.sendMessage(ChatColor.GREEN + "When this saved train is spawned, and players try to save that train, then the forward-orientation of the train will remain unchanged. Regardless of movement direction");
        } else {
            sender.sendMessage(ChatColor.GREEN + "When this saved train is spawned, and players try to save that train, then the movement direction of the train is used to decide the forward-orientation.");
        }
    }

    @Command(value="savedtrain <savedtrainname> spawnlimit unlimited")
    @CommandDescription(value="Disables any set spawn limit, allowing the saved train to be spawned an unlimited number of times")
    @CommandRequiresMultiplePermissions(value={@CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST), @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_SPAWNLIMIT)})
    private void commandSetUnlimitedSpawnLimit(CommandSender sender, @SavedTrainRequiresAccess @Argument(value="savedtrainname") SavedTrainProperties savedTrain) {
        this.commandSetSpawnLimit(sender, savedTrain, -1);
    }

    @Command(value="savedtrain <savedtrainname> spawnlimit <limit>")
    @CommandDescription(value="Sets the maximum number of times this saved train can be spawned using spawn signs or spawn chest")
    @CommandRequiresMultiplePermissions(value={@CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST), @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_SPAWNLIMIT)})
    private void commandSetSpawnLimit(CommandSender sender, @SavedTrainRequiresAccess @Argument(value="savedtrainname") SavedTrainProperties savedTrain, @Argument(value="limit") int spawnLimit) {
        savedTrain.setSpawnLimit(spawnLimit);
        sender.sendMessage(ChatColor.GREEN + "Saved train '" + ChatColor.WHITE + savedTrain.getName() + ChatColor.GREEN + "' now has a spawn limit of " + (spawnLimit >= 0 ? ChatColor.WHITE.toString() + spawnLimit : ChatColor.RED + "UNLIMITED"));
        if (spawnLimit >= 0) {
            int current = savedTrain.getSpawnLimitCurrentCount();
            ChatColor numberColor = current >= spawnLimit ? ChatColor.RED : ChatColor.WHITE;
            sender.sendMessage(ChatColor.GREEN + "This train has been spawned " + numberColor + savedTrain.getSpawnLimitCurrentCount() + ChatColor.GREEN + " times so far");
        }
    }

    @Command(value="savedtrain <savedtrainname> spawnlimit")
    @CommandDescription(value="Gets the currently configured saved train spawn limit and how many trains have spawned")
    @CommandRequiresMultiplePermissions(value={@CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST), @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_SPAWNLIMIT)})
    private void commandGetSpawnLimit(CommandSender sender, @SavedTrainRequiresAccess @Argument(value="savedtrainname") SavedTrainProperties savedTrain) {
        int spawnLimit = savedTrain.getSpawnLimit();
        sender.sendMessage(ChatColor.GREEN + "Saved train '" + ChatColor.WHITE + savedTrain.getName() + ChatColor.GREEN + "' has a spawn limit of " + (spawnLimit >= 0 ? ChatColor.WHITE.toString() + spawnLimit : ChatColor.RED + "UNLIMITED"));
        if (spawnLimit >= 0) {
            int current = savedTrain.getSpawnLimitCurrentCount();
            ChatColor numberColor = current >= spawnLimit ? ChatColor.RED : ChatColor.WHITE;
            sender.sendMessage(ChatColor.GREEN + "This train has been spawned " + numberColor + savedTrain.getSpawnLimitCurrentCount() + ChatColor.GREEN + " times so far");
        }
    }

    @Command(value="savedtrain <savedtrainname> delete")
    @CommandDescription(value="Deletes a saved train permanently")
    @CommandRequiresMultiplePermissions(value={@CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST), @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_DELETE)})
    private void commandDelete(CommandSender sender, TrainCarts plugin, @SavedTrainRequiresAccess @Argument(value="savedtrainname") SavedTrainProperties savedTrain, @Flag(value="force") boolean force) {
        String name = savedTrain.getName();
        if (plugin.getSavedTrains().remove(name)) {
            sender.sendMessage(ChatColor.YELLOW + "Saved train '" + ChatColor.WHITE + name + ChatColor.YELLOW + "' has been deleted!");
        } else {
            sender.sendMessage(ChatColor.RED + "Saved train '" + ChatColor.WHITE + name + ChatColor.RED + "' cannot be removed! (read-only)");
            sender.sendMessage(ChatColor.RED + "You can only override these properties by saving a new configuration");
        }
    }

    @Command(value="savedtrain <savedtrainname> claim")
    @CommandDescription(value="Claims a saved train so that the player has exclusive access")
    @CommandRequiresMultiplePermissions(value={@CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST), @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_CLAIM)})
    private void commandClaimSelf(Player sender, @SavedTrainRequiresAccess @Argument(value="savedtrainname") SavedTrainProperties savedTrain, @Flag(value="force") boolean force) {
        SavedClaim selfClaim;
        Set<SavedClaim> oldClaims = savedTrain.getClaims();
        if (oldClaims.contains(selfClaim = new SavedClaim((OfflinePlayer)sender))) {
            sender.sendMessage(ChatColor.RED + "You have already claimed this saved train!");
        } else {
            HashSet<SavedClaim> newClaims = new HashSet<SavedClaim>(oldClaims);
            newClaims.add(selfClaim);
            SavedTrainCommands.updateClaimList((CommandSender)sender, savedTrain, oldClaims, newClaims);
        }
    }

    @Command(value="savedtrain <savedtrainname> claim add <player>")
    @CommandDescription(value="Adds a claim to a saved train so that the added player has exclusive access")
    @CommandRequiresMultiplePermissions(value={@CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST), @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_CLAIM)})
    private void commandClaimAdd(CommandSender sender, @SavedTrainRequiresAccess @Argument(value="savedtrainname") SavedTrainProperties savedTrain, @Quoted @Argument(value="player", suggestions="playername") String player, @Flag(value="force") boolean force) {
        Set<SavedClaim> oldClaims = savedTrain.getClaims();
        HashSet<SavedClaim> newClaims = new HashSet<SavedClaim>(oldClaims);
        for (SavedClaim addedClaim : SavedClaim.parseClaims(oldClaims, new String[]{player})) {
            if (newClaims.add(addedClaim)) continue;
            sender.sendMessage(ChatColor.RED + "- Player " + addedClaim.description() + " was already on the claim list!");
        }
        SavedTrainCommands.updateClaimList(sender, savedTrain, oldClaims, newClaims);
    }

    @Command(value="savedtrain <savedtrainname> claim remove <player>")
    @CommandDescription(value="Removes a claim from a saved train so that the player no longer has exclusive access")
    @CommandRequiresMultiplePermissions(value={@CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST), @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_CLAIM)})
    private void commandClaimRemove(CommandSender sender, @SavedTrainRequiresAccess @Argument(value="savedtrainname") SavedTrainProperties savedTrain, @Quoted @Argument(value="player", suggestions="playername") String player, @Flag(value="force") boolean force) {
        Set<SavedClaim> oldClaims = savedTrain.getClaims();
        HashSet<SavedClaim> newClaims = new HashSet<SavedClaim>(oldClaims);
        for (SavedClaim removedClaim : SavedClaim.parseClaims(oldClaims, new String[]{player})) {
            if (newClaims.remove(removedClaim)) continue;
            sender.sendMessage(ChatColor.RED + "- Player " + removedClaim.description() + " was not on the claim list");
        }
        SavedTrainCommands.updateClaimList(sender, savedTrain, oldClaims, newClaims);
    }

    @Command(value="savedtrain <savedtrainname> claim clear")
    @CommandDescription(value="Clears all the claims set for the saved train, allowing anyone to access it")
    @CommandRequiresMultiplePermissions(value={@CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST), @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_CLAIM)})
    private void commandClaimClear(CommandSender sender, @SavedTrainRequiresAccess @Argument(value="savedtrainname") SavedTrainProperties savedTrain, @Flag(value="force") boolean force) {
        Set<SavedClaim> oldClaims = savedTrain.getClaims();
        SavedTrainCommands.updateClaimList(sender, savedTrain, oldClaims, Collections.emptySet());
    }

    @Command(value="savedtrain <savedtrainname> spawn [spawnconfig]")
    @CommandDescription(value="Sets a pattern of other saved trains to be spawned when this saved train is spawned")
    @CommandRequiresMultiplePermissions(value={@CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST), @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_IMPORT)})
    private void commandSetSpawn(CommandSender sender, TrainCarts plugin, @SavedTrainRequiresAccess @SavedTrainImplicitlyCreated @Argument(value="savedtrainname") SavedTrainProperties savedTrain, @FlagYielding @Argument(value="spawnconfig", suggestions="trainspawnpattern") @Greedy String spawnConfig, @Flag(value="force") boolean force, @Flag(value="reverse") boolean reverse) {
        boolean isNewTrain = savedTrain.isEmpty();
        try {
            ConfigurationNode config = new ConfigurationNode();
            config.set("spawnPattern", (Object)spawnConfig);
            if (reverse) {
                config.set("flipped", (Object)true);
            }
            plugin.getSavedTrains().setConfig(savedTrain.getName(), config);
        }
        catch (IllegalNameException e) {
            Localization.COMMAND_INPUT_NAME_INVALID.message(sender, new String[]{savedTrain.getName()});
            return;
        }
        if (isNewTrain) {
            sender.sendMessage(ChatColor.GREEN + "The train spawning pattern was set and saved as " + savedTrain.getName());
        } else {
            sender.sendMessage(ChatColor.GREEN + "The train spawning pattern was set and saved as " + savedTrain.getName() + ", a previous train was overwritten");
        }
    }

    @Command(value="savedtrain <savedtrainname> import <url>")
    @CommandDescription(value="Imports a saved train from an online hastebin server by url")
    @CommandRequiresMultiplePermissions(value={@CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST), @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_IMPORT)})
    private void commandImport(CommandSender sender, TrainCarts plugin, @SavedTrainRequiresAccess @SavedTrainImplicitlyCreated @Argument(value="savedtrainname") SavedTrainProperties savedTrain, @Greedy @FlagYielding @Argument(value="url", description="The URL to a Hastebin-hosted paste to download from") String url, @Flag(value="force") boolean force, @Flag(value="import-models") boolean importModels) {
        Commands.importTrain(plugin, sender, url, config -> {
            Commands.importTrainUsedModels(plugin, sender, config, importModels, force);
            boolean isNewTrain = savedTrain.isEmpty();
            try {
                plugin.getSavedTrains().setConfig(savedTrain.getName(), (ConfigurationNode)config);
            }
            catch (IllegalNameException e) {
                Localization.COMMAND_INPUT_NAME_INVALID.message(sender, new String[]{savedTrain.getName()});
                return;
            }
            if (isNewTrain) {
                sender.sendMessage(ChatColor.GREEN + "The train was imported and saved as " + savedTrain.getName());
            } else {
                sender.sendMessage(ChatColor.GREEN + "The train was imported and saved as " + savedTrain.getName() + ", a previous train was overwritten");
            }
        });
    }

    @Command(value="savedtrain list")
    @CommandDescription(value="Lists all the saved trains that exist on the server that a player can modify")
    @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST)
    private void commandShowInfo(CommandSender sender, TrainCarts plugin, @Flag(value="all", description="Show all trains on this server, not just those owned by the player") boolean showAll, @Quoted @Flag(value="module", suggestions="savedtrainmodules", description="Selects a module to list the saved trains of") String moduleName) {
        SavedTrainPropertiesStore module = plugin.getSavedTrains();
        MessageBuilder builder = new MessageBuilder();
        builder.newLine();
        if (moduleName != null) {
            module = moduleName.isEmpty() ? module.getDefaultModule() : module.getModule(moduleName);
            if (module == null) {
                sender.sendMessage(ChatColor.RED + "Module '" + moduleName + "' does not exist");
                return;
            }
            if (moduleName.isEmpty()) {
                builder.blue(new Object[]{"The following saved trains are not stored in any specific module:"});
            } else {
                builder.blue(new Object[]{"The following saved trains are stored in module '" + moduleName + "':"});
            }
        } else {
            builder.yellow(new Object[]{"The following saved trains are available:"});
        }
        builder.newLine().setSeparator(ChatColor.WHITE, " / ");
        for (String name : module.getNames()) {
            if (module.hasPermission(sender, name)) {
                builder.green(new Object[]{name});
                continue;
            }
            if (!showAll) continue;
            builder.red(new Object[]{name});
        }
        builder.send(sender);
    }

    @Command(value="savedtrain list modules")
    @CommandDescription(value="Lists all modules in which saved trains are saved")
    @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_LIST)
    private void commandListModules(CommandSender sender, TrainCarts plugin) {
        MessageBuilder builder = new MessageBuilder();
        builder.newLine();
        builder.blue(new Object[]{"The following modules are available:"});
        builder.newLine().setSeparator(ChatColor.WHITE, " / ");
        for (String moduleName : plugin.getSavedTrains().getModuleNames()) {
            builder.aqua(new Object[]{moduleName});
        }
        builder.send(sender);
    }

    private static void updateClaimList(CommandSender sender, SavedTrainProperties savedTrain, Set<SavedClaim> oldClaims, Set<SavedClaim> newClaims) {
        for (SavedClaim claim : newClaims) {
            if (oldClaims.contains(claim)) continue;
            sender.sendMessage(ChatColor.GREEN + "- Player " + claim.description() + " added to claim list");
        }
        for (SavedClaim claim : oldClaims) {
            if (newClaims.contains(claim)) continue;
            sender.sendMessage(ChatColor.YELLOW + "- Player " + claim.description() + " " + ChatColor.RED + "removed" + ChatColor.YELLOW + " from claim list");
        }
        savedTrain.setClaims(newClaims);
        MessageBuilder builder = new MessageBuilder();
        builder.newLine();
        if (newClaims.size() > 1) {
            builder.newLine();
        }
        builder.yellow(new Object[]{"Saved train '"}).white(new Object[]{savedTrain.getName()}).yellow(new Object[]{"' is now claimed by: "});
        SavedClaim.buildClaimList(builder, newClaims);
        builder.send(sender);
    }

    public boolean checkAccess(CommandSender sender, SavedTrainProperties savedTrain, boolean force) {
        if (Permission.COMMAND_SAVEDTRAIN_GLOBAL.has(sender)) {
            if (force) {
                return true;
            }
            Localization.COMMAND_SAVEDTRAIN_FORCE.message(sender, new String[]{savedTrain.getName()});
            return false;
        }
        if (force) {
            Localization.COMMAND_SAVEDTRAIN_GLOBAL_NOPERM.message(sender, new String[0]);
        } else {
            Localization.COMMAND_SAVEDTRAIN_CLAIMED.message(sender, new String[]{savedTrain.getName()});
        }
        return false;
    }

    private static class SavedTrainPropertiesParser
    implements QuotedArgumentParser<CommandSender, SavedTrainProperties>,
    BlockingSuggestionProvider.Strings<CommandSender> {
        private final boolean mustHaveAccess;
        private final boolean implicitlyCreated;

        public SavedTrainPropertiesParser(boolean mustHaveAccess, boolean implicitlyCreated) {
            this.mustHaveAccess = mustHaveAccess;
            this.implicitlyCreated = implicitlyCreated;
        }

        public boolean isMustHaveAccess() {
            return this.mustHaveAccess;
        }

        public boolean isImplicitlyCreated() {
            return this.implicitlyCreated;
        }

        public @NonNull ArgumentParseResult<@NonNull SavedTrainProperties> parseQuotedString(@NonNull CommandContext<@NonNull CommandSender> commandContext, @NonNull String input) {
            TrainCarts plugin = (TrainCarts)((Object)commandContext.inject(TrainCarts.class).get());
            TrainNameFormat.VerifyResult verify = TrainNameFormat.verify(input);
            if (verify != TrainNameFormat.VerifyResult.OK) {
                return ArgumentParseResult.failure((Throwable)new CloudLocalizedException(commandContext, (ILocalizationEnum)verify.getMessage(), new String[]{input}));
            }
            return ArgumentParseResult.success((Object)plugin.getSavedTrains().getPropertiesOrNone(input));
        }

        public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<CommandSender> commandContext, @NonNull CommandInput commandInput) {
            TrainCarts plugin = (TrainCarts)((Object)commandContext.inject(TrainCarts.class).get());
            String input = commandInput.lastRemainingToken();
            List<String> filtered = input.isEmpty() ? plugin.getSavedTrains().getNames() : plugin.getSavedTrains().getNames().stream().filter(s -> s.startsWith(input)).collect(Collectors.toList());
            List<String> claimed = filtered.stream().filter(name -> plugin.getSavedTrains().hasPermission((CommandSender)commandContext.sender(), (String)name)).collect(Collectors.toList());
            if (claimed.isEmpty()) {
                return filtered;
            }
            return claimed;
        }
    }
}

