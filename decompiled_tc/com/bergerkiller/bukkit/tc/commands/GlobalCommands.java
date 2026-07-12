/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.MessageBuilder
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.cloud.parsers.SoundEffectParser
 *  com.bergerkiller.bukkit.common.dep.cloud.CommandManager
 *  com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Greedy
 *  com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Quoted
 *  com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Range
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Flag
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Permission
 *  com.bergerkiller.bukkit.common.dep.cloud.description.Description
 *  com.bergerkiller.bukkit.common.entity.CommonEntity
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.internal.CommonPlugin
 *  com.bergerkiller.bukkit.common.map.MapDisplay
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.resources.ResourceKey
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.ChatText
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Effect
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Minecart
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.commands;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.cloud.parsers.SoundEffectParser;
import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Greedy;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Quoted;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Range;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Flag;
import com.bergerkiller.bukkit.common.dep.cloud.description.Description;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.ui.AttachmentEditor;
import com.bergerkiller.bukkit.tc.attachments.ui.SetValueTarget;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandRequiresPermission;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorCondition;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorException;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.editor.TCMapControl;
import com.bergerkiller.bukkit.tc.pathfinding.PathNode;
import com.bergerkiller.bukkit.tc.pathfinding.PathWorld;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.statements.Statement;
import com.bergerkiller.bukkit.tc.tickets.TicketStore;
import com.bergerkiller.bukkit.tc.utils.QuoteEscapedString;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class GlobalCommands {
    @Command(value="train version")
    @CommandDescription(value="Shows installed version of TrainCarts and BKCommonLib")
    private void commandShowVersion(CommandSender sender, TrainCarts plugin) {
        plugin.onVersionCommand("version", sender);
    }

    @Command(value="train startuplog")
    @CommandDescription(value="Views everything logged during startup of TrainCarts")
    @com.bergerkiller.bukkit.common.dep.cloud.annotations.Permission(value={"bkcommonlib.command.startuplog"})
    private void commandShowStartupLog(CommandSender sender, TrainCarts plugin) {
        plugin.onStartupLogCommand(sender, "startuplog", new String[0]);
    }

    @Command(value="train list destinations")
    @CommandDescription(value="Lists all the destination names that exist on the server")
    private void commandListDestinations(CommandSender sender, TrainCarts plugin) {
        Collection<PathWorld> worlds;
        MessageBuilder builder = new MessageBuilder();
        builder.yellow(new Object[]{"The following train destinations are available:"});
        builder.newLine().setSeparator(ChatColor.WHITE, " / ");
        if (sender instanceof Player) {
            World playerWorld = ((Player)sender).getWorld();
            worlds = Collections.singleton(plugin.getPathProvider().getWorld(playerWorld));
        } else {
            worlds = plugin.getPathProvider().getWorlds();
        }
        for (PathWorld world : worlds) {
            for (PathNode node : world.getNodes()) {
                if (node.containsOnlySwitcher()) continue;
                builder.green(new Object[]{node.getName()});
            }
        }
        builder.send(sender);
    }

    @Command(value="train list [filter]")
    @CommandDescription(value="Lists all the trains on the server that match the specified statement")
    private void commandListTrains(TrainCarts plugin, CommandSender sender, @Argument(value="filter", suggestions="trainlistfilter") @Greedy String filter) {
        if (filter == null || filter.isEmpty()) {
            int count = 0;
            int moving = 0;
            for (Object group : MinecartGroupStore.getGroups()) {
                ++count;
                if (((MinecartGroup)group).isMoving()) {
                    ++moving;
                }
                ((MinecartGroup)group).getProperties();
            }
            count += plugin.getOfflineGroups().getStoredCountInLoadedWorlds();
            int minecartCount = 0;
            for (World world : WorldUtil.getWorlds()) {
                for (Entity e : WorldUtil.getEntities((World)world)) {
                    if (!(e instanceof Minecart)) continue;
                    ++minecartCount;
                }
            }
            MessageBuilder builder = new MessageBuilder();
            builder.green(new Object[]{"There are "}).yellow(new Object[]{count}).green(new Object[]{" trains on this server (of which "});
            builder.yellow(new Object[]{moving}).green(new Object[]{" are moving)"});
            builder.newLine().green(new Object[]{"There are "}).yellow(new Object[]{minecartCount}).green(new Object[]{" minecart entities"});
            builder.send(sender);
        }
        GlobalCommands.listTrains(plugin, sender, filter == null ? "" : filter);
    }

    @CommandRequiresPermission(value=Permission.COMMAND_MESSAGE)
    @Command(value="train message <key>")
    @CommandDescription(value="Checks what value is assigned to a given message key")
    private void commandGetMessage(CommandSender sender, @Argument(value="key") String key) {
        String value = TCConfig.messageShortcuts.get(key);
        if (value == null) {
            sender.sendMessage(ChatColor.RED + "No shortcut is set for key '" + key + "'");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Shortcut value of '" + key + "' = " + ChatColor.WHITE + value);
        }
    }

    @CommandRequiresPermission(value=Permission.COMMAND_MESSAGE)
    @Command(value="train message <key> <value>")
    @CommandDescription(value="Checks what value is assigned to a given message key")
    private void commandSetMessage(CommandSender sender, TrainCarts plugin, @Argument(value="key") String key, @Argument(value="value") @Greedy String value) {
        String conv_value = StringUtil.ampToColor((String)value);
        TCConfig.messageShortcuts.remove(key);
        TCConfig.messageShortcuts.add(key, conv_value);
        plugin.saveShortcuts();
        sender.sendMessage(ChatColor.GREEN + "Shortcut '" + key + "' set to: " + ChatColor.WHITE + conv_value);
    }

    @CommandRequiresPermission(value=Permission.COMMAND_DESTROYALL)
    @Command(value="train removeall")
    @CommandDescription(value="Destroys all trains on the server or world")
    private void commandRemoveAll(CommandSender sender, TrainCarts plugin, @Flag(value="world") World world, @Flag(value="vanilla", description="Whether to destroy non-Traincarts vanilla Minecarts too") boolean destroyVanilla) {
        this.commandDestroyAll(sender, plugin, world, destroyVanilla);
    }

    @CommandRequiresPermission(value=Permission.COMMAND_DESTROYALL)
    @Command(value="train destroyall")
    @CommandDescription(value="Destroys all trains on the server or world")
    private void commandDestroyAll(CommandSender sender, TrainCarts plugin, @Flag(value="world") World world, @Flag(value="vanilla", description="Whether to destroy non-Traincarts vanilla Minecarts too") boolean destroyVanilla) {
        CompletableFuture<Integer> future = world == null ? plugin.getOfflineGroups().destroyAllAsync(destroyVanilla) : plugin.getOfflineGroups().destroyAllAsync(world, destroyVanilla);
        future.thenAccept(count -> sender.sendMessage(ChatColor.RED.toString() + count + " (visible) trains have been destroyed!"));
    }

    public void init(CommandManager<CommandSender> manager) {
        this.initTrainMenuSetSoundCommand(manager);
    }

    private void initTrainMenuSetSoundCommand(CommandManager<CommandSender> manager) {
        if (!Common.hasCapability((String)"Common:Sound:CloudParser")) {
            return;
        }
        manager.command(manager.commandBuilder("train", new String[0]).literal("menu", new String[0]).literal("sound", Description.of((String)"Sets a sound effect in the TrainCarts editor map"), new String[0]).required("path", SoundEffectParser.soundEffectParser()).permission(Permission.COMMAND_GIVE_EDITOR.cloudPermission()).senderType(Player.class).handler(context -> {
            Player sender = (Player)context.sender();
            ResourceKey effect = (ResourceKey)context.get("path");
            this.commandMenuSet(sender, SetValueTarget.Operation.SET, effect.getPath());
        }));
    }

    @CommandRequiresPermission(value=Permission.COMMAND_GIVE_EDITOR)
    @Command(value="train menu <operation> <value>")
    @CommandDescription(value="Updates a menu item in a TrainCarts editor map using commands")
    private void commandMenuSet(Player sender, @Argument(value="operation") SetValueTarget.Operation operation, @Argument(value="value") @Greedy String value) {
        MapDisplay display = MapDisplay.getHeldDisplay((Player)sender, AttachmentEditor.class);
        if (display == null && (display = MapDisplay.getHeldDisplay((Player)sender)) == null) {
            sender.sendMessage(ChatColor.RED + "You do not have an editor menu open");
            return;
        }
        MapWidget focused = display.getFocusedWidget();
        if (!(focused instanceof SetValueTarget)) {
            focused = display.getActivatedWidget();
        }
        if (!(focused instanceof SetValueTarget)) {
            sender.sendMessage(ChatColor.RED + "No suitable menu item is active!");
            return;
        }
        SetValueTarget target = (SetValueTarget)focused;
        boolean success = target.acceptTextValue(operation, value);
        String propname = target.getAcceptedPropertyName();
        if (success) {
            sender.sendMessage(ChatColor.GREEN + propname + " has been updated");
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to update " + propname + "!");
        }
    }

    @CommandRequiresPermission(value=Permission.COMMAND_REROUTE)
    @Command(value="train reroute")
    @CommandDescription(value="Recalculates all path finding information on the server")
    private void commandReroute(CommandSender sender, TrainCarts plugin, @Flag(value="lazy", description="Delays recalculating routes until a train needs it") boolean lazy, @Flag(value="stop", description="Stops all ongoing path route discovery operations") boolean stop, @Flag(value="status", description="Displays what the routing manager is currently doing") boolean status, @Flag(value="from", repeatable=true, description="Destination name(s) to reroute from", suggestions="destinations") List<String> fromDestinations) {
        if (status) {
            if (!plugin.getPathProvider().isProcessing()) {
                sender.sendMessage(ChatColor.GREEN + "No train routings are being calculated right now");
            } else {
                int numNodes = plugin.getPathProvider().getNumPendingNodes();
                int numTasks = plugin.getPathProvider().getNumPendingOperations();
                sender.sendMessage(ChatColor.YELLOW + "Train routings are being calculated right now:");
                sender.sendMessage(ChatColor.YELLOW + "Number of switchers/destinations remaining: " + ChatColor.RED + numNodes);
                sender.sendMessage(ChatColor.YELLOW + "Number of paths remaining: " + ChatColor.RED + numTasks);
            }
        } else if (stop) {
            plugin.getPathProvider().stopRouting();
            sender.sendMessage(ChatColor.YELLOW + "Cancelled all ongoing train route discovery operations");
        } else if (lazy) {
            PathNode.clearAll();
            sender.sendMessage(ChatColor.YELLOW + "All train routings will be recalculated when needed");
        } else if (fromDestinations != null && !fromDestinations.isEmpty()) {
            boolean hasDestinationsThatExist = false;
            for (String destination : fromDestinations) {
                if (plugin.getPathProvider().nodeExistsOnAnyWorld(destination)) {
                    hasDestinationsThatExist = true;
                    continue;
                }
                sender.sendMessage(ChatColor.RED + "Destination with name '" + ChatColor.YELLOW + destination + ChatColor.RED + "' does not exist!");
            }
            if (!hasDestinationsThatExist) {
                sender.sendMessage(ChatColor.RED + "No valid destination names were specified to reroute from!");
            } else {
                plugin.getPathProvider().notifyOfCompletion(sender);
                plugin.getPathProvider().rerouteFrom(fromDestinations);
                sender.sendMessage(ChatColor.YELLOW + "All train routings will be recalculated from the destination(s) specified");
                sender.sendMessage(ChatColor.YELLOW + "If some destinations no longer exist, they will be removed");
            }
        } else {
            plugin.getPathProvider().notifyOfCompletion(sender);
            plugin.getPathProvider().reroute();
            sender.sendMessage(ChatColor.YELLOW + "All train routings will be recalculated");
        }
    }

    @CommandRequiresPermission(value=Permission.COMMAND_RELOAD)
    @Command(value="train globalconfig reload")
    @CommandDescription(value="Reloads one or more global TrainCarts configuration files from disk")
    private void commandReloadConfig(CommandSender sender, TrainCarts traincarts, @Flag(value="config", description="Reload config.yml") boolean config, @Flag(value="routes", description="Reload routes.yml") boolean routes, @Flag(value="defaulttrainproperties", description="Reload DefaultTrainProperties.yml") boolean defaultTrainproperties, @Flag(value="savedtrainproperties", description="Reload SavedTrainProperties.yml and modules") boolean savedTrainproperties, @Flag(value="modelstore", description="Reload SavedModels.yml and modules") boolean modelStore, @Flag(value="tickets", description="Reload tickets.yml") boolean tickets) {
        if (!(config || routes || defaultTrainproperties || savedTrainproperties || modelStore || tickets)) {
            sender.sendMessage(ChatColor.RED + "Please specify one or more configuration files to reload:");
            sender.sendMessage(ChatColor.RED + "/train globalconfig reload --config");
            sender.sendMessage(ChatColor.RED + "/train globalconfig reload --routes");
            sender.sendMessage(ChatColor.RED + "/train globalconfig reload --defaulttrainproperties");
            sender.sendMessage(ChatColor.RED + "/train globalconfig reload --savedtrainproperties");
            sender.sendMessage(ChatColor.RED + "/train globalconfig reload --modelstore");
            sender.sendMessage(ChatColor.RED + "/train globalconfig reload --tickets");
            return;
        }
        if (config) {
            traincarts.loadConfig();
        }
        if (routes) {
            traincarts.getRouteManager().load();
        }
        if (defaultTrainproperties) {
            TrainProperties.loadDefaults(traincarts);
        }
        if (savedTrainproperties) {
            traincarts.getSavedTrains().reload();
        }
        if (modelStore) {
            traincarts.getSavedAttachmentModels().reload();
        }
        if (tickets) {
            TicketStore.load(traincarts);
        }
        sender.sendMessage(ChatColor.YELLOW + "Configuration has been reloaded!");
    }

    @CommandRequiresPermission(value=Permission.COMMAND_SAVEALL)
    @Command(value="train globalconfig save")
    @CommandDescription(value="Forces a save of all configuration to disk")
    private void commandReloadConfig(CommandSender sender, TrainCarts plugin) {
        plugin.save(TrainCarts.SaveMode.COMMAND);
        sender.sendMessage(ChatColor.YELLOW + "TrainCarts' information has been saved to file.");
    }

    @CommandRequiresPermission(value=Permission.COMMAND_EDIT)
    @Command(value="train edit")
    @CommandDescription(value="Selects a train the player is looking at for editing")
    private void commandEditLookingAt(TrainCarts plugin, final Player player) {
        World playerWorld = player.getWorld();
        Matrix4x4 cameraTransform = new Matrix4x4();
        cameraTransform.translateRotate(Util.getRealEyeLocation(player));
        cameraTransform.invert();
        MinecartMember<?> bestMember = null;
        Vector bestPos = null;
        double bestDistance = Double.MAX_VALUE;
        for (MinecartGroup group : MinecartGroup.getGroups().cloneAsIterable()) {
            if (group.getWorld() != playerWorld) continue;
            for (MinecartMember<?> member : group) {
                Vector pos = ((CommonMinecart)member.getEntity()).loc.vector();
                cameraTransform.transformPoint(pos);
                if (pos.getZ() < 0.0 || pos.getZ() > TCConfig.maxTrainEditdistance) continue;
                double lim = Math.max(1.0, 0.707106781 * pos.getZ());
                if (Math.abs(pos.getX()) > lim || Math.abs(pos.getY()) > lim) continue;
                double distance = Math.sqrt(pos.getX() * pos.getX() + pos.getY() * pos.getY()) / lim;
                if (bestPos != null && !(distance < bestDistance)) continue;
                bestPos = pos;
                bestDistance = distance;
                bestMember = member;
            }
        }
        if (bestMember != null && !bestMember.getProperties().hasOwnership(player)) {
            Localization.EDIT_NOTOWNED.message((CommandSender)player, new String[0]);
        } else if (bestMember != null) {
            final Entity memberEntity = ((CommonMinecart)bestMember.getEntity()).getEntity();
            new Task(this, (JavaPlugin)plugin){
                final int batch_ctr = 5;
                double dy;
                final /* synthetic */ GlobalCommands this$0;
                {
                    this.this$0 = this$0;
                    super(arg0);
                    this.batch_ctr = 5;
                    this.dy = 0.0;
                }

                public void run() {
                    for (int i = 0; i < 5; ++i) {
                        if (this.dy > 50.0 || !player.isValid() || memberEntity.isDead()) {
                            this.stop();
                            return;
                        }
                        Location loc = memberEntity.getLocation();
                        loc.add(0.0, this.dy, 0.0);
                        player.playEffect(loc, Effect.SMOKE, 4);
                        this.dy += 1.0;
                    }
                }
            }.start(1L, 1L);
            plugin.getPlayer(player).editMember(bestMember);
            Localization.EDIT_SUCCESS.message((CommandSender)player, new String[]{bestMember.getGroup().getProperties().getTrainName()});
        } else {
            player.sendMessage(ChatColor.RED + "You are not looking at any Minecart right now");
            player.sendMessage(ChatColor.RED + "Please enter the exact name of the train to edit");
            this.commandListTrains(plugin, (CommandSender)player, null);
        }
    }

    @Command(value="train edit <trainname>")
    @CommandDescription(value="Forcibly removes minecarts and trackers that have glitched out")
    private void commandEditByName(TrainCarts plugin, Player sender, @Quoted @Argument(value="trainname", suggestions="quoted_trainnames") String trainName) {
        TrainProperties prop = TrainProperties.get(trainName);
        if (prop == null) {
            prop = TrainProperties.getRelaxed(trainName);
        }
        if (prop != null && !prop.isEmpty()) {
            if (prop.hasOwnership(sender)) {
                plugin.getPlayer(sender).editCart(prop.get(0));
                Localization.EDIT_SUCCESS.message((CommandSender)sender, new String[]{prop.getTrainName()});
            } else {
                Localization.EDIT_NOTOWNED.message((CommandSender)sender, new String[0]);
            }
        } else {
            Localization.EDIT_NOTFOUND.message((CommandSender)sender, new String[]{trainName});
            this.commandListTrains(plugin, (CommandSender)sender, null);
        }
    }

    @CommandRequiresPermission(value=Permission.COMMAND_CHANGETICK)
    @Command(value="train tick disable")
    @CommandDescription(value="Disables ticking of all trains, causing all physics to pause")
    private void commandTickDisable(CommandSender sender, TrainCarts plugin) {
        this.commandSetTickEnabled(sender, plugin, false);
    }

    @CommandRequiresPermission(value=Permission.COMMAND_CHANGETICK)
    @Command(value="train tick enable")
    @CommandDescription(value="Enables ticking of all trains, causing all physics to resume")
    private void commandTickEnable(CommandSender sender, TrainCarts plugin) {
        this.commandSetTickEnabled(sender, plugin, true);
    }

    @CommandRequiresPermission(value=Permission.COMMAND_CHANGETICK)
    @Command(value="train tick toggle")
    @CommandDescription(value="Toggles ticking of all trains, causing all physics to pause or resume")
    private void commandTickToggle(CommandSender sender, TrainCarts plugin) {
        this.commandSetTickEnabled(sender, plugin, plugin.getTrainUpdateController().getTickDivider() == Integer.MAX_VALUE);
    }

    private void commandSetTickEnabled(CommandSender sender, TrainCarts plugin, boolean enabled) {
        plugin.getTrainUpdateController().setTickDivider(enabled ? 1 : Integer.MAX_VALUE);
        sender.sendMessage(ChatColor.YELLOW + "Train tick updates have been globally " + (enabled ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
    }

    @CommandRequiresPermission(value=Permission.COMMAND_CHANGETICK)
    @Command(value="train tick div")
    @CommandDescription(value="Checks what kind of tick divider configuration is configured")
    private void commandGetTickDivider(CommandSender sender, TrainCarts plugin) {
        int divider = plugin.getTrainUpdateController().getTickDivider();
        if (divider == Integer.MAX_VALUE) {
            sender.sendMessage(ChatColor.YELLOW + "Automatic train tick updates are globally disabled");
        } else {
            sender.sendMessage(ChatColor.GREEN + "The tick rate divider is currently set to " + ChatColor.YELLOW + divider);
        }
    }

    @CommandRequiresPermission(value=Permission.COMMAND_CHANGETICK)
    @Command(value="train tick div reset")
    @CommandDescription(value="Resets any previous global tick divider, resuming physics as normal")
    private void commandResetTickDivider(CommandSender sender, TrainCarts plugin) {
        this.commandSetTickDivider(sender, plugin, 1);
    }

    @CommandRequiresPermission(value=Permission.COMMAND_CHANGETICK)
    @Command(value="train tick div <divider>")
    @CommandDescription(value="Configures a global tick divider, causing all physics to run more slowly")
    private void commandSetTickDivider(CommandSender sender, TrainCarts plugin, @Argument(value="divider") int divider) {
        if (divider > 1) {
            plugin.getTrainUpdateController().setTickDivider(divider);
            sender.sendMessage(ChatColor.GREEN + "The tick rate divider has been set to " + ChatColor.YELLOW + divider);
        } else {
            plugin.getTrainUpdateController().setTickDivider(1);
            sender.sendMessage(ChatColor.GREEN + "The tick rate divider has been reset to the default");
        }
    }

    @CommandRequiresPermission(value=Permission.COMMAND_CHANGETICK)
    @Command(value="train tick")
    @CommandDescription(value="Performs a single update tick. Useful when automatic ticking is disabled or slowed down.")
    private void commandPerformTick(CommandSender sender, TrainCarts plugin) {
        this.commandPerformTick(sender, plugin, 1);
    }

    @CommandRequiresPermission(value=Permission.COMMAND_CHANGETICK)
    @Command(value="train tick <times>")
    @CommandDescription(value="Performs a burst of update ticks. Useful when automatic ticking is disabled or slowed down.")
    private void commandPerformTick(CommandSender sender, TrainCarts plugin, @Argument(value="times") @Range(min="1") int number) {
        plugin.getTrainUpdateController().step(number);
        if (number <= 1) {
            sender.sendMessage(ChatColor.GREEN + "Trains ticked once");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Trains ticked " + number + " times");
        }
    }

    @CommandRequiresPermission(value=Permission.COMMAND_ISSUE)
    @Command(value="train issue")
    @CommandDescription(value="Shows helpful information for posting an issue ticket on our Github")
    private void commandIssueTicket(CommandSender sender, TrainCarts plugin) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            ChatText chatText = ChatText.fromMessage((String)(ChatColor.YELLOW.toString() + "Click one of the below options to open an issue on GitHub:"));
            chatText.sendTo(player);
            try {
                String bugReport = "## Info\nPlease provide the following information:\n\n- BKCommonLib Version: " + CommonPlugin.getInstance().getDebugVersion() + "\n- TrainCarts Version: " + plugin.getDebugVersion() + "\n- Server Type and Version: " + Bukkit.getVersion() + "\n\n----\n## Bug\n\n### Description\n\n### Expected Behaviour\n\n### Actual Behaviour\n\n### Steps to reproduce\n\n### Additional Information\n*This issue was created using the `/train issue` command!*";
                String featureRequest = "## Feature Request\n\n### Description\n\n### Examples";
                chatText = ChatText.empty().appendClickableURL(ChatColor.RED.toString() + ChatColor.UNDERLINE.toString() + "Bug Report", "https://github.com/bergerhealer/TrainCarts/issues/new?body=" + URLEncoder.encode(bugReport, "UTF-8"), "Click to open a Bug Report");
                chatText.sendTo(player);
                chatText = ChatText.empty().appendClickableURL(ChatColor.GREEN.toString() + ChatColor.UNDERLINE.toString() + "Feature Request", "https://github.com/bergerhealer/TrainCarts/issues/new?body=" + URLEncoder.encode(featureRequest, "UTF-8"), "Click to open a Feature Request");
                chatText.sendTo(player);
            }
            catch (UnsupportedEncodingException ex) {
                chatText = ChatText.empty().appendClickableURL(ChatColor.RED.toString() + ChatColor.UNDERLINE.toString() + "Bug Report", "https://github.com/bergerhealer/TrainCarts/issues/new?template=bug_report.md", "Click to open a Bug Report");
                chatText.sendTo(player);
                chatText = ChatText.empty().appendClickableURL(ChatColor.GREEN.toString() + ChatColor.UNDERLINE.toString() + "Feature Request", "https://github.com/bergerhealer/TrainCarts/issues/new?template=feature_request.md", "Click to open a Feature Request");
                chatText.sendTo(player);
            }
        } else {
            MessageBuilder builder = new MessageBuilder();
            builder.white(new Object[]{"Click one of the below URLs to open an issue on GitHub:"});
            try {
                String bugReport = "## Info\nPlease provide the following information:\n\n- BKCommonLib Version: " + CommonPlugin.getInstance().getDebugVersion() + "\n- TrainCarts Version: " + plugin.getDebugVersion() + "\n- Server Type and Version: " + Bukkit.getVersion() + "\n\n----\n## Bug\n\n### Description\n\n### Expected Behaviour\n\n### Actual Behaviour\n\n### Steps to reproduce\n\n### Additional Information\n*This issue was created using the `/train issue` command!*";
                String featureRequest = "## Feature Request\n\n### Description\n\n### Examples";
                builder.white(new Object[]{"Bug Report: https://github.com/bergerhealer/TrainCarts/issues/new?body=" + URLEncoder.encode(bugReport, "UTF-8")}).append(new String[]{"Feature Request: https://github.com/bergerhealer/TrainCarts/issues/new?body=" + URLEncoder.encode(featureRequest, "UTF-8")});
            }
            catch (UnsupportedEncodingException ex) {
                builder.white(new Object[]{"Bug Report: https://github.com/bergerhealer/TrainCarts/issues/new?template=bug_report.md"}).append(new String[]{"Feature Request: https://github.com/bergerhealer/TrainCarts/issues/new?template=feature_request.md"});
            }
            builder.send(sender);
        }
    }

    @CommandRequiresPermission(value=Permission.COMMAND_GIVE_EDITOR)
    @Command(value="train debug editor")
    @CommandDescription(value="Gives a legacy editor map item (broken)")
    private void commandGiveEditor(Player sender) {
        sender.getInventory().addItem(new ItemStack[]{TCMapControl.createTCMapItem()});
        sender.sendMessage("Given editor map item (note: broken)");
    }

    public static void listTrains(TrainCarts plugin, CommandSender sender, String filter) {
        MessageBuilder builder = new MessageBuilder();
        builder.setSeparator(" / ");
        if (filter.startsWith("@train[")) {
            while (filter.endsWith(" ")) {
                filter = filter.substring(0, filter.length() - 1);
            }
            if (!filter.endsWith("]")) {
                Localization.COMMAND_INPUT_SELECTOR_INVALID.message(sender, new String[]{filter.substring(7)});
                return;
            }
            String conditionsString = filter.substring(7, filter.length() - 1);
            List<SelectorCondition> conditions = SelectorCondition.parseAll(conditionsString);
            if (conditions == null) {
                Localization.COMMAND_INPUT_SELECTOR_INVALID.message(sender, new String[]{conditionsString});
                return;
            }
            try {
                Collection<String> collection = plugin.getSelectorHandlerRegistry().find("train").handle(sender, "train", conditions);
                MessageBuilder messageBuilder = builder;
                Objects.requireNonNull(messageBuilder);
                MessageBuilder messageBuilder2 = messageBuilder;
                collection.forEach(xva$0 -> messageBuilder2.append(new String[]{xva$0}));
            }
            catch (SelectorException ex) {
                sender.sendMessage(ChatColor.RED + "[TrainCarts] " + ex.getMessage());
                return;
            }
            ChatText.fromMessage((String)(ChatColor.YELLOW + "The ")).append(ChatText.fromClickableContent((String)(ChatColor.BLUE.toString() + ChatColor.UNDERLINE + "selector"), (String)filter).setHoverText("Click to copy selector to Clipboard")).append(ChatColor.YELLOW + " matches the following trains:").sendTo(sender);
        } else {
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.YELLOW + "You are the proud owner of the following trains:");
            } else {
                sender.sendMessage(ChatColor.YELLOW + "The following trains exist on this server:");
            }
            boolean found = false;
            for (TrainProperties prop : TrainProperties.getAll()) {
                MinecartGroup group;
                if (sender instanceof Player && !prop.hasOwnership((Player)sender) || !prop.hasHolder() && !prop.getTrainCarts().getOfflineGroups().containsInLoadedWorld(prop.getTrainName()) || prop.hasHolder() && !filter.isEmpty() && !Statement.has(group = prop.getHolder(), filter, null)) continue;
                found = true;
                builder.append(new String[]{prop.getTrainName()});
            }
            if (!found) {
                Localization.EDIT_NONEFOUND.message(sender, new String[0]);
                return;
            }
        }
        for (String line : builder.lines()) {
            String[] trainNames = line.split(Pattern.quote(" / "));
            ChatText combined = ChatText.empty();
            for (int i = 0; i < trainNames.length; ++i) {
                if (i > 0) {
                    combined.append(ChatColor.WHITE + " / ");
                }
                combined.append(GlobalCommands.listFormatTrainName(trainNames[i]));
            }
            combined.sendTo(sender);
        }
    }

    private static ChatText listFormatTrainName(String name) {
        ChatText text;
        TrainProperties properties = TrainProperties.get(name);
        if (properties == null) {
            return ChatText.fromMessage((String)(ChatColor.RED + name));
        }
        if (properties.isLoaded() && !properties.isEmpty()) {
            CommonEntity head = properties.getHolder().head().getEntity();
            String worldName = head.getWorld().getName();
            IntVector3 block = head.loc.block();
            text = ChatText.fromMessage((String)(ChatColor.GREEN.toString() + ChatColor.UNDERLINE + name));
            text.setHoverText(ChatColor.GREEN + "Loaded in world " + ChatColor.YELLOW + worldName + ChatColor.GREEN + " at " + ChatColor.WHITE + block.x + "/" + block.y + "/" + block.z);
        } else {
            text = ChatText.fromMessage((String)(ChatColor.RED.toString() + ChatColor.UNDERLINE + name));
            text.setHoverText(ChatColor.RED + "Not loaded");
        }
        String safeEditName = StringUtil.stripChatStyle((String)name);
        text.setClickableRunCommand("/train edit " + QuoteEscapedString.quoteEscape(safeEditName).getEscaped());
        return text;
    }
}

