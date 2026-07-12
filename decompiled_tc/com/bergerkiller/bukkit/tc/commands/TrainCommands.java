/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.MessageBuilder
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Greedy
 *  com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Quoted
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Flag
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.BlockFace
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.commands;

import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Greedy;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Quoted;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Flag;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationOptions;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.commands.Commands;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandRequiresMultiplePermissions;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandRequiresPermission;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.commands.argument.AttachmentsByName;
import com.bergerkiller.bukkit.tc.commands.argument.DirectionOrFormattedSpeed;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableGroup;
import com.bergerkiller.bukkit.tc.controller.status.TrainStatus;
import com.bergerkiller.bukkit.tc.exception.IllegalNameException;
import com.bergerkiller.bukkit.tc.exception.command.NoPermissionForPropertyException;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroup;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.IPropertiesHolder;
import com.bergerkiller.bukkit.tc.properties.SaveLockOrientationMode;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.IPropertyRegistry;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParseResult;
import com.bergerkiller.bukkit.tc.properties.standard.StandardProperties;
import com.bergerkiller.bukkit.tc.signactions.SignActionBlockChanger;
import com.bergerkiller.bukkit.tc.utils.FormattedSpeed;
import com.bergerkiller.bukkit.tc.utils.LauncherConfig;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TrainCommands {
    @CommandTargetTrain
    @Command(value="train info")
    @CommandDescription(value="Displays the properties of the train")
    private void commandInfo(CommandSender sender, TrainProperties properties) {
        MessageBuilder message = new MessageBuilder();
        if (sender instanceof Player && !properties.isOwner((Player)sender) && !properties.hasOwners()) {
            message.yellow(new Object[]{"Note: This train is not owned, claim it using /train claim!"});
            message.newLine().newLine();
        }
        StandardProperties.TRAIN_NAME_FORMAT.appendNameInfo(message, properties, "Train name: ");
        StandardProperties.SLOWDOWN.appendSlowdownInfo(message.newLine(), properties);
        StandardProperties.COLLISION.appendCollisionInfo(message.newLine(), properties);
        message.newLine().yellow(new Object[]{"Keep nearby chunks loaded: "}).white(new Object[]{properties.isKeepingChunksLoaded()});
        if (properties.getHolder() != null) {
            message.newLine().yellow(new Object[]{"Current speed: "});
            double speedUnclipped = properties.getHolder().getAverageForce();
            double speedClipped = Math.min(speedUnclipped, properties.getSpeedLimit());
            double speedMomentum = speedUnclipped - speedClipped;
            message.white(new Object[]{MathUtil.round((double)speedClipped, (int)3)});
            message.white(new Object[]{" blocks/tick"});
            if (speedMomentum > 0.0) {
                message.white(new Object[]{" (+" + MathUtil.round((double)speedMomentum, (int)3) + " energy)"});
            }
        }
        message.newLine().yellow(new Object[]{"Maximum speed: "}).white(new Object[]{properties.getSpeedLimit(), " blocks/tick"});
        message.newLine().yellow(new Object[]{"Realtime physics: "});
        if (properties.hasRealtimePhysics()) {
            message.green(new Object[]{"Enabled"});
        } else {
            message.red(new Object[]{"Disabled"});
        }
        Commands.info(message, properties);
        MinecartGroup group = properties.getHolder();
        if (group == null) {
            message.newLine().red(new Object[]{"This train is unloaded! To keep it loaded, use:"});
            message.newLine().yellow(new Object[]{"   /train keepchunksloaded true"});
        }
        if (group != null) {
            for (MinecartMember<?> member : group) {
                Location loc = member.getFirstKnownDerailedPosition();
                if (loc == null) continue;
                message.newLine().red(new Object[]{"A cart of this train is derailed!"});
                message.newLine().yellow(new Object[]{"   It likely happened at x=", loc.getBlockX(), " y=", loc.getBlockY(), " z=", loc.getBlockZ()});
            }
        }
        message.send(sender);
    }

    @CommandTargetTrain
    @Command(value="train status")
    @CommandDescription(value="Gives a summary about the train's behavior and actions")
    private void commandTrainStatus(CommandSender sender, TrainProperties properties) {
        MinecartGroup group = properties.getHolder();
        if (group == null) {
            sender.sendMessage(ChatColor.RED + "The train is not loaded");
        } else {
            sender.sendMessage("");
            sender.sendMessage(ChatColor.YELLOW + "---- Status of " + properties.getTrainName() + " ----");
            for (TrainStatus status : group.getStatusInfo()) {
                status.getChatMessage().sendTo(sender);
            }
        }
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_DESTROY)
    @Command(value="train remove")
    @CommandDescription(value="Destroys the train, removing all carts")
    private void commandRemove(CommandSender sender, TrainProperties properties) {
        this.commandDestroy(sender, properties);
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_DESTROY)
    @Command(value="train destroy")
    @CommandDescription(value="Destroys the train, removing all carts")
    private void commandDestroy(CommandSender sender, TrainProperties properties) {
        CompletableFuture<Boolean> future;
        MinecartGroup group = properties.getHolder();
        if (group == null) {
            future = properties.getTrainCarts().getOfflineGroups().destroyGroupAsync(properties.getTrainName());
        } else {
            group.destroy();
            future = CompletableFuture.completedFuture(Boolean.TRUE);
        }
        future.thenAccept(success -> {
            if (success.booleanValue()) {
                sender.sendMessage(ChatColor.YELLOW + "The selected train has been destroyed!");
            } else {
                sender.sendMessage(ChatColor.RED + "The selected train could not be located! Mapping removed.");
            }
        });
    }

    @CommandRequiresMultiplePermissions(value={@CommandRequiresPermission(value=Permission.COMMAND_SAVE_TRAIN), @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_EXPORT)})
    @Command(value="train export")
    @CommandDescription(value="Exports the train configuration to a hastebin server")
    private void commandExport(CommandSender sender, MinecartGroup group) {
        String name = group.getProperties().getTrainName();
        Commands.exportTrain(sender, name, group.exportConfig());
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_SAVE_TRAIN)
    @Command(value="train save <name>")
    @CommandDescription(value="Saves the train under a name")
    private void commandSave(CommandSender sender, TrainCarts plugin, MinecartGroup group, @Quoted @Argument(value="name") String name, @Flag(value="force", description="Force saving when the train is claimed by someone else") boolean force, @Flag(value="lockorientation", description="Locks the current forward direction of the train so future saves use it") boolean lockOrientation, @Flag(value="module", description="Module to move the saved train to", suggestions="savedtrainmodules") String module) {
        if (!Commands.checkSavePermissions(plugin, sender, name, force)) {
            return;
        }
        boolean wasContained = plugin.getSavedTrains().getConfig(name) != null;
        try {
            ConfigurationNode config = group.saveConfig(lockOrientation ? SaveLockOrientationMode.ENABLED_OVERRIDE : SaveLockOrientationMode.AUTOMATIC);
            if (!SpawnableGroup.fromConfig(plugin, config).checkSpawnPermissions(sender)) {
                Localization.COMMAND_SAVE_FORBIDDEN_CONTENTS.message(sender, new String[0]);
                return;
            }
            plugin.getSavedTrains().setConfig(name, config);
            String moduleString = "";
            if (module != null && !module.isEmpty()) {
                moduleString = " in module " + module;
                plugin.getSavedTrains().setModuleNameOfTrain(name, module);
            }
            if (wasContained) {
                Localization.COMMAND_SAVE_OVERWRITTEN.message(sender, new String[]{name + moduleString});
            } else {
                Localization.COMMAND_SAVE_NEW.message(sender, new String[]{name + moduleString});
                if (TCConfig.claimNewSavedTrains && sender instanceof Player) {
                    plugin.getSavedTrains().setClaim(name, (Player)sender);
                }
            }
            if (lockOrientation) {
                Localization.COMMAND_SAVE_LOCK_ORIENTATION.message(sender, new String[]{name});
            }
        }
        catch (IllegalNameException ex) {
            sender.sendMessage(ChatColor.RED + "The train could not be saved under this name: " + ex.getMessage());
        }
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_TELEPORT)
    @Command(value="train teleport")
    @CommandDescription(value="Teleports the player to where the train is")
    private void commandTeleport(Player player, TrainProperties properties) {
        OfflineGroup group = properties.getTrainCarts().getOfflineGroups().findGroup(properties.getTrainName());
        if (group != null && !group.world.isLoaded()) {
            player.sendMessage(ChatColor.RED + "Train is on a world that is not loaded");
            return;
        }
        properties.restore().thenAccept(success -> {
            MinecartGroup group = properties.getHolder();
            if (!success.booleanValue() || group.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Train location could not be found: Train is lost");
            } else {
                Location location = ((CommonMinecart)group.head().getEntity()).getLocation();
                EntityUtil.teleport((Entity)player, (Location)location);
                player.sendMessage(ChatColor.YELLOW + "Teleported to train '" + properties.getTrainName() + "'");
            }
        });
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_ENTER)
    @Command(value="train enter")
    @CommandDescription(value="Teleports the player to the train and enters an available seat")
    private void commandEnter(Player player, CartProperties cartProperties, TrainProperties trainProperties, @Flag(value="seat", parserName="trainSeatAttachments") AttachmentsByName<CartAttachmentSeat> seatAttachments) {
        IPropertiesHolder member;
        if (!trainProperties.isLoaded()) {
            player.sendMessage(ChatColor.RED + "Can not enter the train: it is not loaded");
            return;
        }
        if (seatAttachments != null) {
            seatAttachments.validate();
            for (CartAttachmentSeat seat : seatAttachments.attachments()) {
                if (seat.getEntity() != player) continue;
                return;
            }
            List<CartAttachmentSeat> selectedSeats = seatAttachments.attachments();
            if (selectedSeats.size() > 1) {
                List seatsOfMember;
                IPropertiesHolder member2;
                IPropertiesHolder iPropertiesHolder = member2 = cartProperties == null ? null : cartProperties.getHolder();
                if (member2 != null && !(seatsOfMember = selectedSeats.stream().filter(arg_0 -> TrainCommands.lambda$commandEnter$0((MinecartMember)member2, arg_0)).collect(Collectors.toList())).isEmpty()) {
                    selectedSeats = Stream.concat(seatsOfMember.stream(), selectedSeats.stream().filter(s -> !seatsOfMember.contains(s))).collect(Collectors.toList());
                }
            }
            Commands.enterSeats(player, seatAttachments.name(), selectedSeats);
            return;
        }
        IPropertiesHolder iPropertiesHolder = member = cartProperties == null ? null : cartProperties.getHolder();
        if (member == null || ((MinecartMember)member).getAvailableSeatCount((Entity)player) == 0) {
            for (MinecartMember<?> groupMember : trainProperties.getHolder()) {
                if (groupMember.getAvailableSeatCount((Entity)player) <= 0) continue;
                Commands.enterMember(player, groupMember);
                return;
            }
        }
        Commands.enterMember(player, member);
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_EJECT)
    @Command(value="train eject")
    @CommandDescription(value="Ejects the passengers of all the carts of a train, ignoring the allow player exit property")
    private void commandEject(CommandSender sender, TrainProperties trainProperties, @Flag(value="seat", parserName="trainSeatAttachments") AttachmentsByName<CartAttachmentSeat> seatAttachments) {
        if (!trainProperties.isLoaded()) {
            sender.sendMessage(ChatColor.RED + "Can not eject the train: it is not loaded");
            return;
        }
        MinecartGroup group = trainProperties.getHolder();
        if (seatAttachments != null) {
            seatAttachments.validate();
            Commands.ejectSeats(sender, seatAttachments);
        } else if (group.hasPassenger()) {
            group.eject();
            sender.sendMessage(ChatColor.GREEN + "Selected train ejected!");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Selected train has no passengers!");
        }
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_FLIP)
    @Command(value="train flip")
    @CommandDescription(value="Flips the orientation of an entire train 180 degrees")
    private void commandFlip(CommandSender sender, TrainProperties trainProperties, @Flag(value="percart", description="Flip individual carts instead of the entire train") boolean perCart) {
        MinecartGroup group = trainProperties.getHolder();
        if (group == null || group.isUnloaded()) {
            sender.sendMessage(ChatColor.RED + "Can not flip the train: it is not loaded");
            return;
        }
        if (perCart) {
            for (MinecartMember<?> member : group) {
                member.flipOrientation();
            }
            sender.sendMessage(ChatColor.GREEN + "Carts of the selected train flipped!");
        } else {
            group.flipOrientation();
            sender.sendMessage(ChatColor.GREEN + "Selected train flipped!");
        }
    }

    @CommandRequiresPermission(value=Permission.COMMAND_LAUNCH)
    @Command(value="train launch")
    @CommandDescription(value="Launches the train forwards at station launch speed")
    private void commandTrainLaunchNoArg(CommandSender sender, TrainProperties properties) {
        this.commandTrainLaunch(sender, properties, new DirectionOrFormattedSpeed(Direction.FORWARD), null, null, null, null);
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_LAUNCH)
    @Command(value="train launch <speed_or_direction>")
    @CommandDescription(value="Launches the train into a direction")
    private void commandTrainLaunch(CommandSender sender, TrainProperties properties, @Argument(value="speed_or_direction") DirectionOrFormattedSpeed directionOrSpeed, @Flag(value="direction", aliases={"d"}) Direction directionFlag, @Flag(value="speed", aliases={"s"}) FormattedSpeed speedFlag, @Flag(value="limit", aliases={"l"}) FormattedSpeed speedLimitFlag, @Flag(value="options", aliases={"o"}) String launchOptions) {
        if (!properties.isLoaded()) {
            sender.sendMessage(ChatColor.RED + "Can not launch the train: it is not loaded");
            return;
        }
        MinecartGroup group = properties.getHolder();
        this.commandCartLaunch(sender, group.head().getProperties(), directionOrSpeed, directionFlag, speedFlag, speedLimitFlag, launchOptions);
    }

    @CommandRequiresPermission(value=Permission.COMMAND_LAUNCH)
    @Command(value="cart launch")
    @CommandDescription(value="Launches the cart forwards at station launch speed")
    private void commandCartLaunchNoArg(CommandSender sender, CartProperties properties) {
        this.commandCartLaunch(sender, properties, new DirectionOrFormattedSpeed(Direction.FORWARD), null, null, null, null);
    }

    @CommandRequiresPermission(value=Permission.COMMAND_LAUNCH)
    @Command(value="cart launch <speed_or_direction>")
    @CommandDescription(value="Launches the train into a direction")
    private void commandCartLaunch(CommandSender sender, CartProperties properties, @Argument(value="speed_or_direction") DirectionOrFormattedSpeed directionOrSpeed, @Flag(value="direction", aliases={"d"}) Direction directionFlag, @Flag(value="speed", aliases={"s"}) FormattedSpeed speedFlag, @Flag(value="limit", aliases={"l"}) FormattedSpeed speedLimitFlag, @Flag(value="options", aliases={"o"}) String launchOptions) {
        FormattedSpeed speed;
        IPropertiesHolder member = properties.getHolder();
        if (member == null) {
            sender.sendMessage(ChatColor.RED + "Can not launch the cart: it is not loaded");
            return;
        }
        FormattedSpeed formattedSpeed = speedFlag != null ? speedFlag : (speed = directionOrSpeed.hasFormattedSpeed() ? directionOrSpeed.getFormattedSpeed() : FormattedSpeed.of(TCConfig.launchForce));
        Direction direction = directionFlag != null ? directionFlag : (directionOrSpeed.hasDirection() ? directionOrSpeed.getDirection() : Direction.FORWARD);
        double velocity = speed.getValue();
        if (speed.isRelative()) {
            velocity += ((MinecartMember)member).getGroup().getAverageForce();
        }
        LauncherConfig launchConfig = LauncherConfig.createDefault();
        if (launchOptions != null && !launchOptions.isEmpty()) {
            launchConfig = LauncherConfig.parse(launchOptions);
        }
        BlockFace facing = sender instanceof Player ? Util.vecToFace(Util.getRealEyeLocation((Player)sender).getDirection(), false).getOppositeFace() : BlockFace.UP;
        BlockFace directionFace = direction.getDirection(facing, ((MinecartMember)member).getDirectionTo());
        properties.getGroup().getActions().clear();
        if (speedLimitFlag != null) {
            double newSpeedLimit = speedLimitFlag.getValue();
            if (speedLimitFlag.isRelative()) {
                newSpeedLimit += ((MinecartMember)member).getGroup().getProperties().getSpeedLimit();
            }
            ((MinecartMember)member).getActions().addActionLaunch(directionFace, launchConfig, velocity, newSpeedLimit);
        } else {
            ((MinecartMember)member).getActions().addActionLaunch(directionFace, launchConfig, velocity);
        }
        MessageBuilder msg = new MessageBuilder();
        msg.green(new Object[]{"Launching the train "}).yellow(new Object[]{direction.name().toLowerCase(Locale.ENGLISH)});
        msg.green(new Object[]{" to a speed of "}).yellow(new Object[]{TrainCommands.formatNumber(velocity)});
        if (launchConfig.hasDistance()) {
            msg.green(new Object[]{" over the course of "}).yellow(new Object[]{TrainCommands.formatNumber(launchConfig.getDistance())}).green(new Object[]{" blocks"});
        } else if (launchConfig.hasDuration()) {
            msg.green(new Object[]{" over a period of "}).yellow(new Object[]{TrainCommands.formatNumber(launchConfig.getDuration())}).green(new Object[]{" ticks"});
        } else if (launchConfig.hasAcceleration()) {
            msg.green(new Object[]{" at an acceleration of "}).yellow(new Object[]{TrainCommands.formatNumber(launchConfig.getAcceleration())}).green(new Object[]{" b/t\u00b2"});
        }
        msg.send(sender);
    }

    private static String formatNumber(double value) {
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(value);
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_EFFECT)
    @Command(value="train effect <effect_name>")
    @CommandDescription(value="Plays an effect for the entire train")
    private void commandEffect(CommandSender sender, @Argument(value="effect_name", parserName="trainEffectAttachments") AttachmentsByName<Attachment.EffectAttachment> effectAttachments, @Flag(value="volume", aliases={"v"}, description="Playback volume of the effect, 1.0 is the default") Double volume, @Flag(value="speed", aliases={"s"}, description="Playback speed of the effect, 1.0 is default") Double speed, @Flag(value="replay", description="Stops and replays the effect") boolean replay, @Flag(value="stop", description="Stops playing the effect") boolean stop) {
        effectAttachments.validate();
        Attachment.EffectAttachment.EffectOptions opt = Attachment.EffectAttachment.EffectOptions.of((Double)LogicUtil.fixNull((Object)volume, (Object)1.0), (Double)LogicUtil.fixNull((Object)speed, (Object)1.0));
        if (stop) {
            effectAttachments.attachments().forEach(Attachment.EffectAttachment::stopEffect);
            Localization.COMMAND_EFFECT_STOP.message(sender, new String[]{effectAttachments.name()});
        } else if (replay) {
            effectAttachments.attachments().forEach(e -> {
                e.stopEffect();
                e.playEffect(opt);
            });
            Localization.COMMAND_EFFECT_REPLAY.message(sender, new String[]{effectAttachments.name()});
        } else {
            effectAttachments.attachments().forEach(e -> e.playEffect(opt));
            Localization.COMMAND_EFFECT_PLAY.message(sender, new String[]{effectAttachments.name()});
        }
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_ANIMATE)
    @Command(value="train animate <animation_name>")
    @CommandDescription(value="Plays an animation for the entire train")
    private void commandAnimate(CommandSender sender, MinecartGroup group, @Quoted @Argument(value="animation_name", suggestions="trainAnimationName", description="Name of the animation to play") String animationName, @Flag(value="speed", aliases={"s"}, description="Speed of the animation, 1.0 is default") Double speed, @Flag(value="delay", aliases={"d"}, description="Delay of the animation, 0.0 is default") Double delay, @Flag(value="loop", aliases={"l"}, description="Loop the animation") boolean setLooping, @Flag(value="noloop", description="Disable looping the animation") boolean setNotLooping, @Flag(value="reset", aliases={"r"}, description="Reset the animation to the beginning") boolean setReset, @Flag(value="queue", aliases={"q"}, description="Play the animation once previous animations have finished") boolean setQueued, @Flag(value="scene", suggestions="trainAnimationScene", aliases={"m"}, description="Sets the scene marker name of the animation to play") String sceneMarker, @Flag(value="scene_begin", suggestions="trainAnimationScene", description="Sets the scene marker name from which to start playing") String sceneMarkerBegin, @Flag(value="scene_end", suggestions="trainAnimationScene", description="Sets the scene marker name at which to stop playing (inclusive)") String sceneMarkerEnd) {
        AnimationOptions opt = new AnimationOptions();
        opt.setName(animationName);
        if (speed != null) {
            opt.setSpeed(speed);
        }
        if (delay != null) {
            opt.setDelay(delay);
        }
        if (setReset) {
            opt.setReset(true);
        }
        if (setQueued) {
            opt.setQueue(true);
        }
        if (setLooping) {
            opt.setLooped(true);
        }
        if (setNotLooping) {
            opt.setLooped(false);
        }
        if (sceneMarker != null) {
            opt.setScene(sceneMarker);
        }
        if (sceneMarkerBegin != null) {
            opt.setScene(sceneMarkerBegin, opt.getSceneEnd());
        }
        if (sceneMarkerEnd != null) {
            opt.setScene(opt.getSceneBegin(), sceneMarkerEnd);
        }
        if (group.playNamedAnimation(opt)) {
            sender.sendMessage(opt.getCommandSuccessMessage());
        } else {
            sender.sendMessage(opt.getCommandFailureMessage());
        }
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_CHANGEBLOCK)
    @Command(value="train displayedblock clear")
    @CommandDescription(value="Clears the displayed block in Minecart carts of the train, making it empty")
    private void commandClearDisplayedBlock(CommandSender sender, TrainProperties properties) {
        MinecartGroup members = properties.getHolder();
        if (members == null) {
            Localization.EDIT_NOTLOADED.message(sender, new String[0]);
        } else {
            for (MinecartMember<?> member : members) {
                ((CommonMinecart)member.getEntity()).setBlock(Material.AIR);
            }
            sender.sendMessage(ChatColor.YELLOW + "The selected train has its displayed blocks cleared!");
        }
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_CHANGEBLOCK)
    @Command(value="train displayedblock type <blocks>")
    @CommandDescription(value="Sets the displayed block in the Minecart carts of the train")
    private void commandChangeDisplayedBlock(CommandSender sender, TrainProperties properties, @Argument(value="blocks") @Greedy String blockNames) {
        MinecartGroup members = properties.getHolder();
        if (members == null) {
            Localization.EDIT_NOTLOADED.message(sender, new String[0]);
        } else {
            SignActionBlockChanger.setBlocks(members, blockNames, Integer.MAX_VALUE);
            sender.sendMessage(ChatColor.YELLOW + "The selected train has its displayed blocks updated!");
        }
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_CHANGEBLOCK)
    @Command(value="train displayedblock offset reset")
    @CommandDescription(value="Resets the height offset at which blocks are displayed in Minecarts of a train to the defaults")
    private void commandResetDisplayedBlockOffset(CommandSender sender, TrainProperties properties) {
        MinecartGroup members = properties.getHolder();
        if (members == null) {
            Localization.EDIT_NOTLOADED.message(sender, new String[0]);
        } else {
            for (MinecartMember<?> member : members) {
                ((CommonMinecart)member.getEntity()).setBlockOffset(Util.getDefaultDisplayedBlockOffset());
            }
            sender.sendMessage(ChatColor.YELLOW + "The selected train has its block offset reset!");
        }
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_CHANGEBLOCK)
    @Command(value="train displayedblock offset <offset>")
    @CommandDescription(value="Sets the height offset at which blocks are displayed in Minecarts of a train")
    private void commandSetDisplayedBlockOffset(CommandSender sender, TrainProperties properties, @Argument(value="offset") int offset) {
        MinecartGroup members = properties.getHolder();
        if (members == null) {
            Localization.EDIT_NOTLOADED.message(sender, new String[0]);
        } else {
            for (MinecartMember<?> member : members) {
                ((CommonMinecart)member.getEntity()).setBlockOffset(offset);
            }
            sender.sendMessage(ChatColor.YELLOW + "The selected train has its displayed block offset updated!");
        }
    }

    @CommandTargetTrain
    @Command(value="train activespawnlimits clear")
    @CommandDescription(value="Makes this train stop taking part in active saved train spawn limits")
    @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_SPAWNLIMIT)
    private void commandClearActiveSpawnLimits(CommandSender sender, TrainProperties properties) {
        List<String> activeLimits = properties.get(StandardProperties.ACTIVE_SAVED_TRAIN_SPAWN_LIMITS);
        if (activeLimits.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "This train is not counted in any saved train spawn limits");
            return;
        }
        properties.set(StandardProperties.ACTIVE_SAVED_TRAIN_SPAWN_LIMITS, Collections.emptyList());
        for (String savedTrainName : activeLimits) {
            sender.sendMessage(ChatColor.GREEN + "This train is no longer counted in the spawn limits of " + savedTrainName);
        }
    }

    @CommandTargetTrain
    @Command(value="train <property> <value>")
    @CommandDescription(value="Updates the value of a property of a train by name")
    private void commandCart(CommandSender sender, TrainProperties properties, @Argument(value="property") String propertyName, @Quoted @Argument(value="value") String value) {
        PropertyParseResult parseResult = IPropertyRegistry.instance().parseAndSet(properties, propertyName, value, result -> {
            if (!result.hasPermission(sender)) {
                throw new NoPermissionForPropertyException(result.getName());
            }
        });
        if (parseResult.isSuccessful()) {
            sender.sendMessage(ChatColor.GREEN + "Property has been updated!");
        } else {
            sender.sendMessage(parseResult.getMessage());
            if (parseResult.getReason() == PropertyParseResult.Reason.PROPERTY_NOT_FOUND) {
                TrainCommands.help(new MessageBuilder()).send(sender);
            }
        }
    }

    public static MessageBuilder help(MessageBuilder builder) {
        builder.green(new Object[]{"Available commands: "}).yellow(new Object[]{"/train "}).red(new Object[]{"["});
        builder.setSeparator(ChatColor.WHITE, "/").setIndent(10);
        builder.red(new Object[]{"info"}).red(new Object[]{"linking"}).red(new Object[]{"keepchunksloaded"}).red(new Object[]{"claim"}).red(new Object[]{"addowners"}).red(new Object[]{"setowners"});
        builder.red(new Object[]{"addtags"}).red(new Object[]{"settags"}).red(new Object[]{"destination"}).red(new Object[]{"destroy"}).red(new Object[]{"public"}).red(new Object[]{"private"});
        builder.red(new Object[]{"pickup"}).red(new Object[]{"break"}).red(new Object[]{"default"}).red(new Object[]{"rename"}).red(new Object[]{"speedlimit"}).red(new Object[]{"setcollide"}).red(new Object[]{"slowdown"});
        builder.red(new Object[]{"mobcollision"}).red(new Object[]{"animalcollision"}).red(new Object[]{"monstercollision"}).red(new Object[]{"npccollision"});
        builder.red(new Object[]{"passivecollision"}).red(new Object[]{"neutralcollision"}).red(new Object[]{"hostilecollision"}).red(new Object[]{"tameablecollision"});
        builder.red(new Object[]{"utilitycollision"}).red(new Object[]{"bosscollision"}).red(new Object[]{"jockeycollision"}).red(new Object[]{"petcollision"}).red(new Object[]{"killer_bunnycollision"});
        return builder.red(new Object[]{"pushplayers"}).red(new Object[]{"pushmobs"}).red(new Object[]{"pushmisc"}).setSeparator(null).red(new Object[]{"]"});
    }

    private static /* synthetic */ boolean lambda$commandEnter$0(MinecartMember member, CartAttachmentSeat s) {
        return s.getMember() == member;
    }
}

