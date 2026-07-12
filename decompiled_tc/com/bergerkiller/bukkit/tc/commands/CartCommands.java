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
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
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
import com.bergerkiller.bukkit.common.utils.StringUtil;
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
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableGroup;
import com.bergerkiller.bukkit.tc.exception.IllegalNameException;
import com.bergerkiller.bukkit.tc.exception.command.NoPermissionForPropertyException;
import com.bergerkiller.bukkit.tc.offline.train.OfflineMember;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.CartPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.IPropertiesHolder;
import com.bergerkiller.bukkit.tc.properties.api.IPropertyRegistry;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParseResult;
import com.bergerkiller.bukkit.tc.signactions.SignActionBlockChanger;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CartCommands {
    @CommandTargetTrain
    @Command(value="cart info")
    @CommandDescription(value="Displays the properties of the cart")
    private void commandInfo(CommandSender sender, CartProperties properties) {
        CartCommands.info(sender, properties);
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_DESTROY)
    @Command(value="cart remove")
    @CommandDescription(value="Destroys the single cart that is selected")
    private void commandRemove(CommandSender sender, CartProperties properties) {
        this.commandDestroy(sender, properties);
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_DESTROY)
    @Command(value="cart destroy")
    @CommandDescription(value="Destroys the single cart that is selected")
    private void commandDestroy(CommandSender sender, CartProperties properties) {
        IPropertiesHolder member = properties.getHolder();
        if (member == null) {
            CartPropertiesStore.remove(properties.getUUID());
            properties.getTrainCarts().getOfflineGroups().removeMember(properties.getUUID());
        } else {
            ((MinecartMember)member).onDie(true);
        }
        sender.sendMessage(ChatColor.YELLOW + "The selected cart has been destroyed!");
    }

    @CommandRequiresMultiplePermissions(value={@CommandRequiresPermission(value=Permission.COMMAND_SAVE_TRAIN), @CommandRequiresPermission(value=Permission.COMMAND_SAVEDTRAIN_EXPORT)})
    @Command(value="cart export")
    @CommandDescription(value="Exports the selected cart's train configuration to a hastebin server")
    private void commandExport(CommandSender sender, MinecartMember<?> member) {
        String name = member.getGroup().getProperties().getTrainName();
        ConfigurationNode exportedConfig = this.saveMemberConfig(member);
        exportedConfig.remove("claims");
        Commands.exportTrain(sender, name, exportedConfig);
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_SAVE_TRAIN)
    @Command(value="cart save <name>")
    @CommandDescription(value="Saves the selected cart as a train under a name")
    private void commandSave(TrainCarts plugin, CommandSender sender, MinecartMember<?> member, @Quoted @Argument(value="name") String name, @Flag(value="force", description="Force saving when the train is claimed by someone else") boolean force, @Flag(value="module", description="Module to move the saved train to") String module) {
        if (!Commands.checkSavePermissions(plugin, sender, name, force)) {
            return;
        }
        boolean wasContained = plugin.getSavedTrains().getConfig(name) != null;
        try {
            ConfigurationNode memberConfig = this.saveMemberConfig(member);
            if (!SpawnableGroup.fromConfig(plugin, memberConfig).checkSpawnPermissions(sender)) {
                Localization.COMMAND_SAVE_FORBIDDEN_CONTENTS.message(sender, new String[0]);
                return;
            }
            plugin.getSavedTrains().setConfig(name, memberConfig);
            String moduleString = "";
            if (module != null && !module.isEmpty()) {
                moduleString = " in module " + module;
                plugin.getSavedTrains().setModuleNameOfTrain(name, module);
            }
            if (wasContained) {
                sender.sendMessage(ChatColor.GREEN + "The cart was saved as train " + name + moduleString + ", a previous train was overwritten");
            } else {
                sender.sendMessage(ChatColor.GREEN + "The cart was saved as train " + name + moduleString);
                if (TCConfig.claimNewSavedTrains && sender instanceof Player) {
                    plugin.getSavedTrains().setClaim(name, (Player)sender);
                }
            }
        }
        catch (IllegalNameException ex) {
            sender.sendMessage(ChatColor.RED + "The cart could not be saved under this name: " + ex.getMessage());
        }
    }

    private ConfigurationNode saveMemberConfig(MinecartMember<?> member) {
        ConfigurationNode exportedConfig = member.getGroup().getProperties().saveToConfig().clone();
        exportedConfig.remove("carts");
        exportedConfig.setNodeList("carts", Collections.singletonList(member.saveConfig()));
        return exportedConfig;
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_TELEPORT)
    @Command(value="cart teleport")
    @CommandDescription(value="Teleports the player to where the cart is")
    private void commandTeleport(Player player, CartProperties properties) {
        OfflineMember member = properties.getTrainCarts().getOfflineGroups().findMember(properties.getTrainProperties().getTrainName(), properties.getUUID());
        if (member != null && !member.group.world.isLoaded()) {
            player.sendMessage(ChatColor.RED + "Cart is on a world that is not loaded");
            return;
        }
        properties.restore().thenAccept(success -> {
            if (!success.booleanValue()) {
                player.sendMessage(ChatColor.RED + "Cart location could not be found: Train is lost");
            } else {
                IPropertiesHolder member = properties.getHolder();
                Location location = ((CommonMinecart)member.getEntity()).getLocation();
                EntityUtil.teleport((Entity)player, (Location)location);
                player.sendMessage(ChatColor.YELLOW + "Teleported to cart of '" + properties.getTrainProperties().getTrainName() + "'");
            }
        });
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_EFFECT)
    @Command(value="cart effect <effect_name>")
    @CommandDescription(value="Plays an effect for a cart")
    private void commandEffect(CommandSender sender, @Argument(value="effect_name", parserName="cartEffectAttachments") AttachmentsByName<Attachment.EffectAttachment> effectAttachments, @Flag(value="volume", aliases={"v"}, description="Playback volume of the effect, 1.0 is the default") Double volume, @Flag(value="speed", aliases={"s"}, description="Playback speed of the effect, 1.0 is default") Double speed, @Flag(value="replay", description="Stops and replays the effect") boolean replay, @Flag(value="stop", description="Stops playing the effect") boolean stop) {
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
    @Command(value="cart animate <animation_name>")
    @CommandDescription(value="Plays an animation for the cart")
    private void commandAnimate(CommandSender sender, MinecartMember<?> member, @Quoted @Argument(value="animation_name", suggestions="cartAnimationName", description="Name of the animation to play") String animationName, @Flag(value="speed", aliases={"s"}, description="Speed of the animation, 1.0 is default") Double speed, @Flag(value="delay", aliases={"d"}, description="Delay of the animation, 0.0 is default") Double delay, @Flag(value="loop", aliases={"l"}, description="Loop the animation") boolean setLooping, @Flag(value="noloop", description="Disable looping the animation") boolean setNotLooping, @Flag(value="reset", aliases={"r"}, description="Reset the animation to the beginning") boolean setReset, @Flag(value="queue", aliases={"q"}, description="Play the animation once previous animations have finished") boolean setQueued, @Flag(value="scene", suggestions="cartAnimationScene", aliases={"m"}, description="Sets the scene marker name of the animation to play") String sceneMarker, @Flag(value="scene_begin", suggestions="cartAnimationScene", description="Sets the scene marker name from which to start playing") String sceneMarkerBegin, @Flag(value="scene_end", suggestions="cartAnimationScene", description="Sets the scene marker name at which to stop playing (inclusive)") String sceneMarkerEnd) {
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
        if (member.playNamedAnimation(opt)) {
            sender.sendMessage(opt.getCommandSuccessMessage());
        } else {
            sender.sendMessage(opt.getCommandFailureMessage());
        }
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_CHANGEBLOCK)
    @Command(value="cart displayedblock clear")
    @CommandDescription(value="Clears the displayed block in the Minecart, making it empty")
    private void commandClearDisplayedBlock(CommandSender sender, CartProperties properties) {
        IPropertiesHolder member = properties.getHolder();
        if (member == null) {
            Localization.EDIT_NOTLOADED.message(sender, new String[0]);
        } else {
            ((CommonMinecart)member.getEntity()).setBlock(Material.AIR);
            sender.sendMessage(ChatColor.YELLOW + "The selected minecart has its displayed block cleared!");
        }
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_CHANGEBLOCK)
    @Command(value="cart displayedblock type <block>")
    @CommandDescription(value="Sets the displayed block type in the Minecart")
    private void commandChangeDisplayedBlock(CommandSender sender, CartProperties properties, @Argument(value="block") @Greedy String blockName) {
        IPropertiesHolder member = properties.getHolder();
        if (member == null) {
            Localization.EDIT_NOTLOADED.message(sender, new String[0]);
        } else {
            ArrayList members = new ArrayList(1);
            members.add((MinecartMember<?>)member);
            SignActionBlockChanger.setBlocks(members, blockName, Integer.MAX_VALUE);
            sender.sendMessage(ChatColor.YELLOW + "The selected minecart has its displayed block updated!");
        }
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_CHANGEBLOCK)
    @Command(value="cart displayedblock offset reset")
    @CommandDescription(value="Resets the height offset at which a block is displayed in a Minecart to the defaults")
    private void commandResetDisplayedBlockOffset(CommandSender sender, CartProperties properties) {
        IPropertiesHolder member = properties.getHolder();
        if (member == null) {
            Localization.EDIT_NOTLOADED.message(sender, new String[0]);
        } else {
            ((CommonMinecart)member.getEntity()).setBlockOffset(Util.getDefaultDisplayedBlockOffset());
            sender.sendMessage(ChatColor.YELLOW + "The selected minecart has its block offset reset!");
        }
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_CHANGEBLOCK)
    @Command(value="cart displayedblock offset <offset>")
    @CommandDescription(value="Sets the height offset at which a block is displayed in a Minecart")
    private void commandSetDisplayedBlockOffset(CommandSender sender, CartProperties properties, @Argument(value="offset") int offset) {
        IPropertiesHolder member = properties.getHolder();
        if (member == null) {
            Localization.EDIT_NOTLOADED.message(sender, new String[0]);
        } else {
            ((CommonMinecart)member.getEntity()).setBlockOffset(offset);
            sender.sendMessage(ChatColor.YELLOW + "The selected minecart has its displayed block offset updated!");
        }
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_ENTER)
    @Command(value="cart enter")
    @CommandDescription(value="Teleports the player to the cart and enters an available seat")
    private void commandEnter(Player player, CartProperties cartProperties, @Flag(value="seat", parserName="cartSeatAttachments") AttachmentsByName<CartAttachmentSeat> seatAttachments) {
        if (cartProperties.getHolder() == null) {
            player.sendMessage(ChatColor.RED + "Can not enter the train: it is not loaded");
            return;
        }
        if (seatAttachments != null) {
            seatAttachments.validate();
            for (CartAttachmentSeat seat : seatAttachments.attachments()) {
                if (seat.getEntity() != player) continue;
                return;
            }
            Commands.enterSeats(player, seatAttachments.name(), seatAttachments.attachments());
            return;
        }
        Commands.enterMember(player, cartProperties.getHolder());
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_EJECT)
    @Command(value="cart eject")
    @CommandDescription(value="Ejects the passengers of a cart, ignoring the allow player exit property")
    private void commandEject(CommandSender sender, CartProperties cartProperties, @Flag(value="seat", parserName="cartSeatAttachments") AttachmentsByName<CartAttachmentSeat> seatAttachments) {
        IPropertiesHolder member = cartProperties.getHolder();
        if (member == null || ((MinecartMember)member).isUnloaded()) {
            sender.sendMessage(ChatColor.RED + "Can not eject the cart: it is not loaded");
            return;
        }
        if (seatAttachments != null) {
            seatAttachments.validate();
            Commands.ejectSeats(sender, seatAttachments);
        } else if (((CommonMinecart)member.getEntity()).hasPassenger()) {
            ((MinecartMember)member).eject();
            sender.sendMessage(ChatColor.GREEN + "Selected cart ejected!");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Selected cart has no passengers!");
        }
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_FLIP)
    @Command(value="cart flip")
    @CommandDescription(value="Flips the orientation of a cart 180 degrees")
    private void commandFlip(CommandSender sender, CartProperties cartProperties) {
        IPropertiesHolder member = cartProperties.getHolder();
        if (member == null || ((MinecartMember)member).isUnloaded()) {
            sender.sendMessage(ChatColor.RED + "Can not flip the cart: it is not loaded");
            return;
        }
        ((MinecartMember)member).flipOrientation();
        sender.sendMessage(ChatColor.GREEN + "Selected cart flipped!");
    }

    @CommandTargetTrain
    @Command(value="cart <property> <value>")
    @CommandDescription(value="Updates the value of a property of a cart by name")
    private void commandCart(CommandSender sender, CartProperties properties, @Argument(value="property") String propertyName, @Quoted @Argument(value="value") String value) {
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
                CartCommands.help(new MessageBuilder()).send(sender);
            }
        }
    }

    public static MessageBuilder help(MessageBuilder builder) {
        builder.green(new Object[]{"Available commands: "}).yellow(new Object[]{"/cart "}).red(new Object[]{"[info"});
        builder.setSeparator(ChatColor.WHITE, "/").setIndent(10);
        builder.red(new Object[]{"mobenter"}).red(new Object[]{"playerenter"}).red(new Object[]{"playerexit"}).red(new Object[]{"claim"}).red(new Object[]{"addowners"}).red(new Object[]{"setowners"});
        builder.red(new Object[]{"addtags"}).red(new Object[]{"settags"}).red(new Object[]{"destination"}).red(new Object[]{"destroy"}).red(new Object[]{"public"}).red(new Object[]{"private"});
        builder.red(new Object[]{"pickup"}).red(new Object[]{"break"});
        return builder.setSeparator(null).red(new Object[]{"]"});
    }

    public static void info(CommandSender sender, CartProperties prop) {
        Location loc;
        MessageBuilder message = new MessageBuilder();
        message.yellow(new Object[]{"UUID: "}).white(new Object[]{prop.getUUID().toString()}).newLine();
        if (prop.hasOwners()) {
            message.newLine().yellow(new Object[]{"Note: This minecart is not owned, claim it using /cart claim!"});
        }
        message.yellow(new Object[]{"Picks up nearby items: "}).white(new Object[]{prop.canPickup()});
        if (prop.hasBlockBreakTypes()) {
            message.newLine().yellow(new Object[]{"Breaks blocks: "}).white(new Object[]{StringUtil.combineNames(prop.getBlockBreakTypes())});
        }
        message.newLine().yellow(new Object[]{"Enter message: "}).white(new Object[]{prop.hasEnterMessage() ? prop.getEnterMessage() : "None"});
        Commands.info(message, prop);
        IPropertiesHolder member = prop.getHolder();
        if (member == null) {
            message.newLine().red(new Object[]{"The train of this cart is unloaded! To keep it loaded, use:"});
            message.newLine().yellow(new Object[]{"   /train keepchunksloaded true"});
        }
        if (member != null && (loc = ((MinecartMember)member).getFirstKnownDerailedPosition()) != null) {
            message.newLine().red(new Object[]{"This cart is derailed!"});
            message.newLine().yellow(new Object[]{"   It likely happened at x=", loc.getBlockX(), " y=", loc.getBlockY(), " z=", loc.getBlockZ()});
        }
        sender.sendMessage(" ");
        message.send(sender);
    }
}

