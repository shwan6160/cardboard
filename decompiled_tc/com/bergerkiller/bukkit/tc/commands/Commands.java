/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.BlockLocation
 *  com.bergerkiller.bukkit.common.Hastebin$DownloadResult
 *  com.bergerkiller.bukkit.common.Hastebin$UploadResult
 *  com.bergerkiller.bukkit.common.MessageBuilder
 *  com.bergerkiller.bukkit.common.cloud.CloudSimpleHandler
 *  com.bergerkiller.bukkit.common.config.BasicConfiguration
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.dep.cloud.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.CommandManager
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.BuilderModifier
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext
 *  com.bergerkiller.bukkit.common.dep.cloud.meta.CommandMeta
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.StandardParameters
 *  com.bergerkiller.bukkit.common.dep.cloud.permission.Permission
 *  com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider$Strings
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.permissions.NoPermissionException
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 *  com.bergerkiller.bukkit.common.wrappers.ChatText
 *  com.bergerkiller.mountiplex.MountiplexUtil
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.commands;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.Hastebin;
import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.cloud.CloudSimpleHandler;
import com.bergerkiller.bukkit.common.config.BasicConfiguration;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.BuilderModifier;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.meta.CommandMeta;
import com.bergerkiller.bukkit.common.dep.cloud.parser.StandardParameters;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.permissions.NoPermissionException;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.config.SavedAttachmentModel;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.chest.TrainChestCommands;
import com.bergerkiller.bukkit.tc.commands.AttachmentCommands;
import com.bergerkiller.bukkit.tc.commands.CartCommands;
import com.bergerkiller.bukkit.tc.commands.GlobalCommands;
import com.bergerkiller.bukkit.tc.commands.ModelStoreCommands;
import com.bergerkiller.bukkit.tc.commands.SavedTrainCommands;
import com.bergerkiller.bukkit.tc.commands.TCSyntaxFormatter;
import com.bergerkiller.bukkit.tc.commands.TicketCommands;
import com.bergerkiller.bukkit.tc.commands.TrainAnimationCommands;
import com.bergerkiller.bukkit.tc.commands.TrainCommands;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandRequiresMultiplePermissions;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandRequiresPermission;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.commands.argument.AttachmentsByName;
import com.bergerkiller.bukkit.tc.commands.argument.DirectionOrFormattedSpeed;
import com.bergerkiller.bukkit.tc.commands.parsers.AccelerationParser;
import com.bergerkiller.bukkit.tc.commands.parsers.AttachmentByNameParser;
import com.bergerkiller.bukkit.tc.commands.parsers.ChunkLoadOptionsModeParser;
import com.bergerkiller.bukkit.tc.commands.parsers.DirectionParser;
import com.bergerkiller.bukkit.tc.commands.parsers.FormattedSpeedParser;
import com.bergerkiller.bukkit.tc.commands.parsers.TrainNameFormatParser;
import com.bergerkiller.bukkit.tc.commands.parsers.TrainTargetingFlags;
import com.bergerkiller.bukkit.tc.commands.suggestions.AnimationNameSuggestionProvider;
import com.bergerkiller.bukkit.tc.commands.suggestions.AnimationSceneSuggestionProvider;
import com.bergerkiller.bukkit.tc.commands.suggestions.TrainListFilterSuggestionProvider;
import com.bergerkiller.bukkit.tc.commands.suggestions.TrainNameSuggestionProvider;
import com.bergerkiller.bukkit.tc.commands.suggestions.TrainSpawnPatternSuggestionProvider;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.global.TrainCartsPlayer;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableGroup;
import com.bergerkiller.bukkit.tc.debug.DebugCommands;
import com.bergerkiller.bukkit.tc.exception.IllegalNameException;
import com.bergerkiller.bukkit.tc.exception.command.CommandOnlyForPlayersException;
import com.bergerkiller.bukkit.tc.exception.command.InvalidClaimPlayerNameException;
import com.bergerkiller.bukkit.tc.exception.command.NoPermissionForAnyPropertiesException;
import com.bergerkiller.bukkit.tc.exception.command.NoPermissionForPropertyException;
import com.bergerkiller.bukkit.tc.exception.command.NoTicketSelectedException;
import com.bergerkiller.bukkit.tc.exception.command.NoTrainNearbyException;
import com.bergerkiller.bukkit.tc.exception.command.NoTrainSelectedException;
import com.bergerkiller.bukkit.tc.exception.command.NoTrainStorageChestItemException;
import com.bergerkiller.bukkit.tc.exception.command.SelectedTrainNotLoadedException;
import com.bergerkiller.bukkit.tc.exception.command.SelectedTrainNotOwnedException;
import com.bergerkiller.bukkit.tc.locator.TrainLocatorCommands;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.IProperties;
import com.bergerkiller.bukkit.tc.properties.IPropertiesHolder;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.standard.StandardProperties;
import com.bergerkiller.bukkit.tc.properties.standard.type.TrainNameFormat;
import com.bergerkiller.bukkit.tc.utils.FormattedSpeed;
import com.bergerkiller.mountiplex.MountiplexUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Commands {
    private final CloudSimpleHandler cloud = new CloudSimpleHandler();
    private final CartCommands commands_cart = new CartCommands();
    private final TrainCommands commands_train = new TrainCommands();
    private final GlobalCommands commands_train_global = new GlobalCommands();
    private final AttachmentCommands commands_train_attachments = new AttachmentCommands();
    private final DebugCommands commands_train_debug = new DebugCommands();
    private final TrainChestCommands commands_train_chest = new TrainChestCommands();
    private final TrainLocatorCommands commands_train_locator = new TrainLocatorCommands();
    private final TicketCommands commands_train_ticket = new TicketCommands();
    private final TrainAnimationCommands commands_train_animation = new TrainAnimationCommands();
    private final SavedTrainCommands commands_savedtrain = new SavedTrainCommands();
    private final ModelStoreCommands commands_modelstore = new ModelStoreCommands();

    public CloudSimpleHandler getHandler() {
        return this.cloud;
    }

    public void enable(TrainCarts plugin) {
        this.cloud.enable((Plugin)plugin);
        this.cloud.getManager().commandSyntaxFormatter(new TCSyntaxFormatter(this.cloud.getManager()));
        this.cloud.captionFromLocalization(Localization.class);
        this.cloud.getParser().registerBuilderModifier(CommandRequiresPermission.class, (perm, builder) -> builder.permission(perm.value().cloudPermission()));
        this.cloud.getParser().registerBuilderModifier(CommandRequiresMultiplePermissions.class, (multi, builder) -> {
            List perms = Stream.of(multi.value()).map(CommandRequiresPermission::value).map(Permission::cloudPermission).collect(Collectors.toList());
            if (perms.isEmpty()) {
                return builder;
            }
            if (perms.size() == 1) {
                return builder.permission((com.bergerkiller.bukkit.common.dep.cloud.permission.Permission)perms.get(0));
            }
            return builder.permission(com.bergerkiller.bukkit.common.dep.cloud.permission.Permission.allOf(perms));
        });
        this.cloud.getParser().registerBuilderModifier(CommandTargetTrain.class, (BuilderModifier)TrainTargetingFlags.INSTANCE);
        this.cloud.injector(TrainCartsPlayer.class, (context, annotations) -> {
            if (context.sender() instanceof Player) {
                return plugin.getPlayer((Player)context.sender());
            }
            throw new CommandOnlyForPlayersException();
        });
        this.cloud.injector(CartProperties.class, (context, annotations) -> {
            Player p;
            CartProperties cartProperties = TrainTargetingFlags.INSTANCE.findCartProperties((CommandContext<CommandSender>)context);
            if (context.sender() instanceof Player && !cartProperties.hasOwnership(p = (Player)context.sender())) {
                throw new SelectedTrainNotOwnedException();
            }
            return cartProperties;
        });
        this.cloud.injector(TrainProperties.class, (context, annotations) -> {
            Player p;
            CartProperties cartProperties = TrainTargetingFlags.INSTANCE.findCartProperties((CommandContext<CommandSender>)context);
            TrainProperties trainProperties = cartProperties.getTrainProperties();
            if (context.sender() instanceof Player && !trainProperties.hasOwnership(p = (Player)context.sender())) {
                throw new SelectedTrainNotOwnedException();
            }
            return trainProperties;
        });
        this.cloud.injector(MinecartMember.class, (context, annotations) -> {
            CartProperties properties = (CartProperties)context.inject(CartProperties.class).get();
            IPropertiesHolder member = properties.getHolder();
            if (member == null || ((MinecartMember)member).isUnloaded()) {
                throw new SelectedTrainNotLoadedException();
            }
            return member;
        });
        this.cloud.injector(MinecartGroup.class, (context, annotations) -> {
            TrainProperties properties = (TrainProperties)context.inject(TrainProperties.class).get();
            MinecartGroup group = properties.getHolder();
            if (group == null) {
                throw new SelectedTrainNotLoadedException();
            }
            return group;
        });
        this.cloud.parse(FormattedSpeed.class, parameters -> {
            boolean greedy = (Boolean)parameters.get(StandardParameters.GREEDY, (Object)false);
            return new FormattedSpeedParser(greedy);
        });
        this.cloud.parse("acceleration", parameters -> {
            boolean greedy = (Boolean)parameters.get(StandardParameters.GREEDY, (Object)false);
            return new AccelerationParser(greedy);
        });
        this.cloud.parse(DirectionParser.directionParser());
        this.cloud.parse(ChunkLoadOptionsModeParser.chunkLoadOptionsModeParser());
        this.cloud.parse(TrainNameFormatParser.trainNameFormatParser());
        this.cloud.parse(DirectionOrFormattedSpeed.directionOrFormattedSpeedParser());
        this.cloud.parse("cartItemDisplayAttachments", p -> AttachmentByNameParser.all(Attachment.ItemDisplayAttachment.class, false).createParser());
        this.cloud.parse("trainItemDisplayAttachments", p -> AttachmentByNameParser.all(Attachment.ItemDisplayAttachment.class, true).createParser());
        this.cloud.parse("cartTextDisplayAttachments", p -> AttachmentByNameParser.all(Attachment.TextDisplayAttachment.class, false).createParser());
        this.cloud.parse("trainTextDisplayAttachments", p -> AttachmentByNameParser.all(Attachment.TextDisplayAttachment.class, true).createParser());
        this.cloud.parse("cartSeatAttachments", p -> AttachmentByNameParser.seats(false).createParser());
        this.cloud.parse("trainSeatAttachments", p -> AttachmentByNameParser.seats(true).createParser());
        this.cloud.parse("cartEffectAttachments", p -> AttachmentByNameParser.effects(false).createParser());
        this.cloud.parse("trainEffectAttachments", p -> AttachmentByNameParser.effects(true).createParser());
        this.cloud.handleMessage(NoPermissionException.class, Localization.COMMAND_NOPERM.getName());
        this.cloud.handleMessage(NoTrainSelectedException.class, Localization.EDIT_NOSELECT.getName());
        this.cloud.handleMessage(SelectedTrainNotOwnedException.class, Localization.EDIT_NOTOWNED.getName());
        this.cloud.handleMessage(SelectedTrainNotLoadedException.class, Localization.EDIT_NOTLOADED.getName());
        this.cloud.handleMessage(NoTrainNearbyException.class, Localization.COMMAND_CART_NOT_FOUND_NEARBY.getName());
        this.cloud.handleMessage(NoTrainStorageChestItemException.class, Localization.CHEST_NOITEM.getName());
        this.cloud.handleMessage(NoTicketSelectedException.class, Localization.COMMAND_TICKET_NOTEDITING.getName());
        this.cloud.handleMessage(NoPermissionForAnyPropertiesException.class, Localization.PROPERTY_NOPERM_ANY.getName());
        this.cloud.handle(NoPermissionForPropertyException.class, (sender, ex) -> Localization.PROPERTY_NOPERM.message((CommandSender)sender, new String[]{ex.getName()}));
        this.cloud.handle(InvalidClaimPlayerNameException.class, (sender, exception) -> Localization.COMMAND_SAVEDTRAIN_CLAIM_INVALID.message((CommandSender)sender, new String[]{exception.getArgument()}));
        this.cloud.handle(CommandOnlyForPlayersException.class, (sender, exception) -> sender.sendMessage("Only players can execute this command"));
        this.cloud.suggest("cartAnimationName", (BlockingSuggestionProvider.Strings)AnimationNameSuggestionProvider.CART_ANIMATION_NAME);
        this.cloud.suggest("trainAnimationName", (BlockingSuggestionProvider.Strings)AnimationNameSuggestionProvider.TRAIN_ANIMATION_NAME);
        this.cloud.suggest("cartAnimationScene", (BlockingSuggestionProvider.Strings)AnimationSceneSuggestionProvider.CART_ANIMATION_SCENE);
        this.cloud.suggest("trainAnimationScene", (BlockingSuggestionProvider.Strings)AnimationSceneSuggestionProvider.TRAIN_ANIMATION_SCENE);
        this.cloud.getManager().parserRegistry().registerSuggestionProvider("quoted_trainnames", new TrainNameSuggestionProvider().quoteEscaped());
        this.cloud.suggest("trainlistfilter", (BlockingSuggestionProvider.Strings)new TrainListFilterSuggestionProvider());
        this.cloud.suggest("trainspawnpattern", (BlockingSuggestionProvider.Strings)new TrainSpawnPatternSuggestionProvider());
        this.cloud.suggest("destinations", (context, input) -> {
            Stream worlds;
            if (context.sender() instanceof Player) {
                World world2 = ((Player)context.sender()).getWorld();
                worlds = MountiplexUtil.toStream((Object)plugin.getPathProvider().getWorld(world2));
            } else {
                worlds = plugin.getPathProvider().getWorlds().stream();
            }
            return worlds.flatMap(world -> world.getNodes().stream()).flatMap(node -> node.getNames().stream()).distinct().collect(Collectors.toList());
        });
        this.cloud.suggest("targetplayer", (context, input) -> {
            List result = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toCollection(ArrayList::new));
            result.add("@p");
            return result;
        });
        this.commands_train_global.init((CommandManager<CommandSender>)this.cloud.getManager());
        this.commands_train_ticket.init((CommandManager<CommandSender>)this.cloud.getManager());
        this.commands_savedtrain.init((CommandManager<CommandSender>)this.cloud.getManager());
        this.commands_modelstore.init((CommandManager<CommandSender>)this.cloud.getManager());
        this.cloud.annotations((Object)this.commands_cart);
        this.cloud.annotations((Object)this.commands_train);
        this.cloud.annotations((Object)this.commands_train_global);
        this.cloud.annotations((Object)this.commands_train_attachments);
        this.cloud.annotations((Object)this.commands_train_debug);
        this.cloud.annotations((Object)this.commands_train_chest);
        this.cloud.annotations((Object)this.commands_train_locator);
        this.cloud.annotations((Object)this.commands_train_ticket);
        this.cloud.annotations((Object)this.commands_train_animation);
        this.cloud.annotations((Object)this.commands_savedtrain);
        this.cloud.annotations((Object)this.commands_modelstore);
        this.cloud.annotations((Object)this);
        this.cloud.helpCommand(Collections.singletonList("cart"), "Shows help for commands that target carts");
        this.cloud.helpCommand(Collections.singletonList("train"), "Shows help for global commands and commands that target trains");
        this.cloud.helpCommand(Collections.singletonList("savedtrain"), "Shows help for commands that manage saved trains");
        this.cloud.helpCommand(Arrays.asList("cart", "route"), "Shows help for commands that modify the route set for carts");
        this.cloud.helpCommand(Arrays.asList("train", "route"), "Shows help for commands that modify the route set for trains");
        com.bergerkiller.bukkit.common.dep.cloud.Command debugHelpCommand = this.cloud.helpCommand(Arrays.asList("train", "debug"), "Shows help about the debugging commands", builder -> builder.permission(Permission.DEBUG_COMMAND_DEBUG.getName()));
        this.cloud.getManager().command(com.bergerkiller.bukkit.common.dep.cloud.Command.newBuilder((String)"train", (CommandMeta)CommandMeta.empty(), (String[])new String[0]).literal("debug", new String[0]).proxies(debugHelpCommand));
    }

    @Command(value="train")
    @CommandDescription(value="Displays the TrainCarts plugin about message, with version information")
    private void commandShowAbout(TrainCarts plugin, CommandSender sender) {
        sender.sendMessage(ChatColor.BLUE + "TrainCarts " + plugin.getDebugVersion());
        String message = Localization.COMMAND_USAGE.get(new String[0]);
        Pattern urlPattern = Pattern.compile("\\[(.*)\\]\\(([\\w\\/\\.:\\&\\?=]+)\\)");
        Matcher matcher = urlPattern.matcher(message);
        ChatText text = ChatText.empty();
        int currentIndex = 0;
        while (matcher.find()) {
            int startIndex = matcher.start();
            if (startIndex > currentIndex) {
                text.append(message.substring(currentIndex, startIndex));
            }
            text.appendClickableURL(matcher.group(1), matcher.group(2));
            currentIndex = matcher.end();
        }
        if (currentIndex < message.length()) {
            text.append(message.substring(currentIndex));
        }
        if (sender instanceof Player) {
            text.sendTo((Player)sender);
        } else {
            sender.sendMessage(text.getMessage());
        }
    }

    public static void info(MessageBuilder message, IProperties prop) {
        message.newLine();
        StandardProperties.OWNERS.addOwnerInfo(message, prop);
        message.newLine().yellow(new Object[]{"Tags: "}).white(new Object[]{prop.hasTags() ? StringUtil.combineNames(prop.getTags()) : "None"});
        if (prop.hasDestination()) {
            message.newLine().yellow(new Object[]{"This minecart will attempt to reach: "}).white(new Object[]{prop.getDestination()});
        }
        message.newLine().yellow(new Object[]{"Players entering trains: "}).white(new Object[]{prop.getPlayersEnter() ? "Allowed" : "Denied"});
        message.newLine().yellow(new Object[]{"Players exiting trains: "}).white(new Object[]{prop.getPlayersExit() ? "Allowed" : "Denied"});
        BlockLocation loc = prop.getLocation();
        if (loc != null) {
            message.newLine().yellow(new Object[]{"Current location: "}).white(new Object[]{"[", loc.x, "/", loc.y, "/", loc.z, "] in world ", loc.world});
        }
    }

    public static boolean checkSavePermissions(TrainCarts plugin, CommandSender sender, String trainName, boolean force) {
        TrainNameFormat.VerifyResult verify = TrainNameFormat.verify(trainName);
        if (verify != TrainNameFormat.VerifyResult.OK) {
            verify.getMessage().message(sender, new String[]{trainName});
            return false;
        }
        if (!plugin.getSavedTrains().hasPermission(sender, trainName)) {
            if (!Permission.COMMAND_SAVEDTRAIN_GLOBAL.has(sender)) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to overwrite saved train " + trainName);
                return false;
            }
            if (!force) {
                sender.sendMessage(ChatColor.RED + "The saved train '" + trainName + "' already exists, but it is not yours!");
                sender.sendMessage(ChatColor.RED + "Here are some options:");
                sender.sendMessage(ChatColor.RED + "/savedtrain " + trainName + " info  -  See who claimed it");
                sender.sendMessage(ChatColor.RED + "/savedtrain " + trainName + " claim  -  Claim it yourself");
                sender.sendMessage(ChatColor.RED + "/train save " + trainName + " --force  -  Force a save and overwrite");
                return false;
            }
        }
        return true;
    }

    public static boolean checkSavePermissionsOverwrite(TrainCarts plugin, CommandSender sender, String trainName, boolean force) {
        TrainNameFormat.VerifyResult verify = TrainNameFormat.verify(trainName);
        if (verify != TrainNameFormat.VerifyResult.OK) {
            verify.getMessage().message(sender, new String[]{trainName});
            return false;
        }
        if (!plugin.getSavedTrains().containsTrain(trainName)) {
            return true;
        }
        boolean isFromOtherPlayer = false;
        if (!plugin.getSavedTrains().hasPermission(sender, trainName)) {
            if (!Permission.COMMAND_SAVEDTRAIN_GLOBAL.has(sender)) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to overwrite saved train " + trainName);
                return false;
            }
            isFromOtherPlayer = true;
        }
        if (!force) {
            if (isFromOtherPlayer) {
                sender.sendMessage(ChatColor.RED + "The saved train '" + trainName + "' already exists, and it is not yours!");
            } else {
                sender.sendMessage(ChatColor.RED + "The saved train '" + trainName + "' already exists!");
            }
            sender.sendMessage(ChatColor.RED + "/savedtrain " + trainName + " info  -  View saved train details");
            sender.sendMessage(ChatColor.RED + "If you are sure you want to overwrite it, pass --force");
            return false;
        }
        return true;
    }

    public static void importModel(final TrainCarts plugin, final CommandSender sender, final String url, final Consumer<ConfigurationNode> callback) {
        TCConfig.hastebin.download(url).thenAccept(new Consumer<Hastebin.DownloadResult>(){

            @Override
            public void accept(Hastebin.DownloadResult result) {
                BasicConfiguration config;
                if (!result.success()) {
                    sender.sendMessage(ChatColor.RED + "Failed to import model: " + result.error());
                    return;
                }
                try {
                    config = result.contentYAML();
                }
                catch (IOException ex) {
                    sender.sendMessage(ChatColor.RED + "Failed to import model configuration because of YAML decode error: " + ex.getMessage());
                    return;
                }
                catch (Throwable t) {
                    sender.sendMessage(ChatColor.RED + "An error occurred trying to import the model configuration YAML: " + t.getMessage());
                    plugin.getLogger().log(Level.SEVERE, "Import error for " + url, t);
                    return;
                }
                callback.accept(config);
            }
        });
    }

    public static void importTrain(final TrainCarts plugin, final CommandSender sender, final String url, final Consumer<ConfigurationNode> callback) {
        TCConfig.hastebin.download(url).thenAccept(new Consumer<Hastebin.DownloadResult>(){

            @Override
            public void accept(Hastebin.DownloadResult result) {
                BasicConfiguration config;
                if (!result.success()) {
                    Localization.COMMAND_IMPORT_ERROR.message(sender, new String[]{result.error()});
                    return;
                }
                try {
                    config = result.contentYAML();
                }
                catch (IOException ex) {
                    Localization.COMMAND_IMPORT_ERROR.message(sender, new String[]{"YAML decode error: " + ex.getMessage()});
                    return;
                }
                catch (Throwable t) {
                    Localization.COMMAND_IMPORT_ERROR.message(sender, new String[]{t.getMessage()});
                    plugin.getLogger().log(Level.SEVERE, "Import error for " + url, t);
                    return;
                }
                SpawnableGroup group = SpawnableGroup.fromConfig(plugin, (ConfigurationNode)config);
                if (group.getMembers().isEmpty()) {
                    Localization.COMMAND_IMPORT_NO_CARTS.message(sender, new String[0]);
                    return;
                }
                if (!group.checkSpawnPermissions(sender)) {
                    Localization.COMMAND_IMPORT_FORBIDDEN_CONTENTS.message(sender, new String[0]);
                    return;
                }
                callback.accept(config);
            }
        });
    }

    public static void importTrainUsedModels(TrainCarts plugin, CommandSender sender, ConfigurationNode savedTrainConfig, boolean doImport, boolean force) {
        ConfigurationNode usedModels = savedTrainConfig.getNodeIfExists("usedModels");
        if (usedModels == null) {
            return;
        }
        usedModels.remove();
        if (!doImport) {
            String message = usedModels.getKeys().stream().map(name -> (plugin.getSavedAttachmentModels().containsModel((String)name) ? ChatColor.GREEN : ChatColor.RED) + name).collect(Collectors.joining(ChatColor.YELLOW + ", "));
            Localization.COMMAND_IMPORT_MISSING_MODELS.message(sender, new String[]{message});
            return;
        }
        if (!Permission.COMMAND_MODEL_CONFIG_LIST.has(sender)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to read or write model configurations");
            return;
        }
        boolean checkPerms = !force || !Permission.COMMAND_MODEL_CONFIG_GLOBAL.has(sender);
        boolean warnedAboutForce = false;
        ArrayList<String> imported = new ArrayList<String>();
        for (String key : usedModels.getKeys()) {
            SavedAttachmentModel model = plugin.getSavedAttachmentModels().getModel(key);
            if (model != null && checkPerms && !model.hasPermission(sender)) {
                if (force && !warnedAboutForce) {
                    Localization.COMMAND_MODEL_CONFIG_GLOBAL_NOPERM.message(sender, new String[0]);
                    warnedAboutForce = true;
                }
                Localization.COMMAND_MODEL_CONFIG_FORCE.message(sender, new String[]{key});
                continue;
            }
            try {
                plugin.getSavedAttachmentModels().setConfigAsPlayer(key, usedModels.getNode(key), sender);
                imported.add(key);
            }
            catch (IllegalNameException e) {
                Localization.COMMAND_MODEL_CONFIG_INVALID_NAME.message(sender, new String[]{key});
            }
        }
        if (!imported.isEmpty()) {
            Localization.COMMAND_IMPORT_UPDATED_MODELS.message(sender, new String[]{String.join((CharSequence)", ", imported)});
        }
    }

    public static void exportModel(final CommandSender sender, final String name, ConfigurationNode exportedConfig) {
        TCConfig.hastebin.upload(exportedConfig.toString()).thenAccept(new Consumer<Hastebin.UploadResult>(){

            @Override
            public void accept(Hastebin.UploadResult t) {
                if (t.success()) {
                    sender.sendMessage(ChatColor.GREEN + "Model configuration '" + ChatColor.YELLOW + name + ChatColor.GREEN + "' exported: " + ChatColor.WHITE + ChatColor.UNDERLINE + t.url());
                } else {
                    sender.sendMessage(ChatColor.RED + "Failed to export model configuration '" + name + "': " + t.error());
                }
            }
        });
    }

    public static void exportTrain(final CommandSender sender, final String name, ConfigurationNode exportedConfig) {
        TCConfig.hastebin.upload(exportedConfig.toString()).thenAccept(new Consumer<Hastebin.UploadResult>(){

            @Override
            public void accept(Hastebin.UploadResult t) {
                if (t.success()) {
                    sender.sendMessage(ChatColor.GREEN + "Train '" + ChatColor.YELLOW + name + ChatColor.GREEN + "' exported: " + ChatColor.WHITE + ChatColor.UNDERLINE + t.url());
                } else {
                    sender.sendMessage(ChatColor.RED + "Failed to export train '" + name + "': " + t.error());
                }
            }
        });
    }

    public static void ejectSeats(CommandSender sender, AttachmentsByName<CartAttachmentSeat> seats) {
        boolean success = false;
        boolean seatHadPassengers = false;
        for (CartAttachmentSeat seat : seats.attachments()) {
            seatHadPassengers |= seat.getEntity() != null;
            success |= seat.eject();
        }
        if (success) {
            sender.sendMessage(ChatColor.GREEN + "Seats with name '" + seats.name() + "' ejected!");
        } else if (seatHadPassengers) {
            sender.sendMessage(ChatColor.RED + "Seats with name '" + seats.name() + "' could not be ejected (cancelled)!");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Seats with name '" + seats.name() + "' have no passengers!");
        }
    }

    public static void enterMember(Player player, MinecartMember<?> member) {
        if (member != null && member.getAvailableSeatCount((Entity)player) > 0) {
            boolean mustTeleport;
            Location entityLoc = ((CommonMinecart)member.getEntity()).getLocation();
            boolean bl = mustTeleport = player.getWorld() != entityLoc.getWorld() || player.getLocation().distance(entityLoc) > 64.0;
            if (!mustTeleport || player.teleport(((CommonMinecart)member.getEntity()).getLocation())) {
                if (member.addPassengerForced((Entity)player)) {
                    player.sendMessage(ChatColor.GREEN + "You entered a seat of train '" + member.getGroup().getProperties().getTrainName() + "'!");
                } else if (mustTeleport) {
                    player.sendMessage(ChatColor.YELLOW + "Selected cart has no available seat. Teleported to the train instead.");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Selected cart has no available seat.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Failed to enter train: teleport was denied");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Failed to enter train: no free seat available");
        }
    }

    public static void enterSeats(Player player, String seatName, List<CartAttachmentSeat> seats) {
        boolean hadEmptySeats = false;
        for (CartAttachmentSeat seat : seats) {
            if (seat.enter((Entity)player)) {
                player.sendMessage(ChatColor.GREEN + "Seat with name '" + seatName + "' entered!");
                return;
            }
            if (seat.getEntity() != null) continue;
            hadEmptySeats = true;
        }
        if (hadEmptySeats) {
            player.sendMessage(ChatColor.RED + "Seat with name '" + seatName + "' could not be entered (cancelled)!");
        } else if (seats.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Seat with name '" + seatName + "' does not exist!");
        } else {
            player.sendMessage(ChatColor.RED + "Seats with name '" + seatName + "' are all occupied!");
        }
    }
}

