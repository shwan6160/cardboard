/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Default
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.properties.standard.category;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Default;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.PropertyCheckPermission;
import com.bergerkiller.bukkit.tc.properties.api.PropertyInvalidInputException;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyParseContext;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedProperty;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedStandardTrainProperty;
import com.bergerkiller.bukkit.tc.properties.standard.type.WaitOptions;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class WaitOptionsProperty
extends FieldBackedStandardTrainProperty<WaitOptions> {
    @CommandTargetTrain
    @PropertyCheckPermission(value="waitdistance")
    @Command(value="train wait distance <blocks>")
    @CommandDescription(value="Sets the distance to keep to other trains")
    private void setDistanceProperty(CommandSender sender, TrainProperties properties, @Argument(value="blocks", description="Number of blocks distance") double distance) {
        properties.update(this, options -> WaitOptions.create(distance, options.delay(), options.acceleration(), options.deceleration(), options.predict()));
        this.getDistanceProperty(sender, properties);
    }

    @Command(value="train wait distance")
    @CommandDescription(value="Displays the distance to keep to other trains")
    private void getDistanceProperty(CommandSender sender, TrainProperties properties) {
        sender.sendMessage(ChatColor.YELLOW + "Distance to keep to other trains: " + ChatColor.GREEN + properties.getWaitDistance() + " blocks");
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="waitdelay")
    @Command(value="train wait delay <time>")
    @CommandDescription(value="Sets the time a train waits when fully stopped to wait")
    private void setDelayProperty(CommandSender sender, TrainProperties properties, @Argument(value="time", description="Time to wait in seconds") double delay) {
        properties.update(this, options -> WaitOptions.create(options.distance(), delay, options.acceleration(), options.deceleration(), options.predict()));
        this.getDelayProperty(sender, properties);
    }

    @Command(value="train wait delay")
    @CommandDescription(value="Displays the time a train waits when fully stopped to wait")
    private void getDelayProperty(CommandSender sender, TrainProperties properties) {
        sender.sendMessage(ChatColor.YELLOW + "Wait delay when stopped: " + ChatColor.GREEN + properties.getWaitDelay() + " seconds");
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="waitacceleration")
    @Command(value="train wait acceleration <acceleration> [deceleration]")
    @CommandDescription(value="Sets the rate of acceleration (and deceleration) of the train")
    private void setAccelerationProperty(CommandSender sender, TrainProperties properties, @Argument(value="acceleration", parserName="acceleration", description="Acceleration in blocks/tick\u00b2") double acceleration, @Argument(value="deceleration", parserName="acceleration", description="De-acceleration in blocks/tick\u00b2") @Default(value="NaN") double deceleration) {
        properties.update(this, options -> WaitOptions.create(options.distance(), options.delay(), acceleration, Double.isNaN(deceleration) ? acceleration : deceleration, options.predict()));
        this.getAccelerationProperty(sender, properties);
    }

    @Command(value="train wait acceleration")
    @CommandDescription(value="Displays the rate of acceleration (and deceleration) of the train")
    private void getAccelerationProperty(CommandSender sender, TrainProperties properties) {
        if (properties.getWaitAcceleration() == properties.getWaitDeceleration()) {
            sender.sendMessage(ChatColor.YELLOW + "Speeds up and slows down to wait at: " + ChatColor.GREEN + properties.getWaitAcceleration() + " blocks/tick\u00b2");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Slows down to wait at: " + ChatColor.GREEN + properties.getWaitDeceleration() + " blocks/tick\u00b2");
            sender.sendMessage(ChatColor.YELLOW + "Speeds up after waiting at: " + ChatColor.GREEN + properties.getWaitAcceleration() + " blocks/tick\u00b2");
        }
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="waitprediction")
    @Command(value="train wait predict <predict>")
    @CommandDescription(value="Sets whether the train will predict routing up ahead")
    private void setPredictProperty(CommandSender sender, TrainProperties properties, @Argument(value="predict", description="Whether to predict") boolean predict) {
        properties.update(this, options -> WaitOptions.create(options.distance(), options.delay(), options.acceleration(), options.deceleration(), predict));
        this.getPredictProperty(sender, properties);
    }

    @Command(value="train wait predict")
    @CommandDescription(value="Displays whether the train will predict routing up ahead")
    private void getPredictProperty(CommandSender sender, TrainProperties properties) {
        sender.sendMessage(ChatColor.YELLOW + "Predict path up ahead: " + Localization.boolStr(properties.isWaitPredicted()));
    }

    @PropertyParser(value="waitdistance|wait distance")
    public WaitOptions parseWaitDistance(PropertyParseContext<WaitOptions> context) {
        return WaitOptions.create(context.inputDouble(), context.current().delay(), context.current().acceleration(), context.current().deceleration(), context.current().predict());
    }

    @PropertyParser(value="waitdelay|wait delay")
    public WaitOptions parseWaitDelay(PropertyParseContext<WaitOptions> context) {
        return WaitOptions.create(context.current().distance(), context.inputDouble(), context.current().acceleration(), context.current().deceleration(), context.current().predict());
    }

    @PropertyParser(value="waitacceleration|wait acceleration")
    public WaitOptions parseWaitAcceleration(PropertyParseContext<WaitOptions> context) {
        double newDeceleration;
        double newAcceleration;
        String[] args = context.input().trim().split(" ");
        if (args.length >= 2) {
            newAcceleration = Util.parseAcceleration(args[0], Double.NaN);
            newDeceleration = Util.parseAcceleration(args[1], Double.NaN);
        } else {
            newAcceleration = newDeceleration = Util.parseAcceleration(context.input(), Double.NaN);
        }
        if (Double.isNaN(newAcceleration)) {
            throw new PropertyInvalidInputException("Acceleration is not a number or acceleration expression");
        }
        if (Double.isNaN(newDeceleration)) {
            throw new PropertyInvalidInputException("Deceleration is not a number or acceleration expression");
        }
        return WaitOptions.create(context.current().distance(), context.current().delay(), newAcceleration, newDeceleration, context.current().predict());
    }

    @PropertyParser(value="waitpredicted|waitprediction|wait predicted|wait predict")
    public WaitOptions parseWaitPrediction(PropertyParseContext<WaitOptions> context) {
        return WaitOptions.create(context.current().distance(), context.current().delay(), context.current().acceleration(), context.current().deceleration(), context.inputBoolean());
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_WAIT.has(sender);
    }

    @Override
    public WaitOptions getDefault() {
        return WaitOptions.DEFAULT;
    }

    @Override
    public WaitOptions getData(FieldBackedProperty.TrainInternalData data) {
        return data.waitOptionsData;
    }

    @Override
    public void setData(FieldBackedProperty.TrainInternalData data, WaitOptions value) {
        data.waitOptionsData = value;
    }

    @Override
    public Optional<WaitOptions> readFromConfig(ConfigurationNode config) {
        if (!config.isNode("wait")) {
            if (config.contains("waitDistance")) {
                return Optional.of(WaitOptions.create((Double)config.get("waitDistance", (Object)0.0)));
            }
            return Optional.empty();
        }
        ConfigurationNode waitConfig = config.getNode("wait");
        double distance = (Double)waitConfig.get("distance", (Object)0.0);
        double delay = (Double)waitConfig.get("delay", (Object)0.0);
        double accel = (Double)waitConfig.get("acceleration", (Object)0.0);
        double decel = (Double)waitConfig.get("deceleration", (Object)0.0);
        boolean predict = (Boolean)waitConfig.get("predict", (Object)true);
        return Optional.of(WaitOptions.create(distance, delay, accel, decel, predict));
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<WaitOptions> value) {
        config.remove("waitDistance");
        if (value.isPresent()) {
            WaitOptions data = value.get();
            ConfigurationNode node = config.getNode("wait");
            node.set("distance", (Object)data.distance());
            node.set("delay", (Object)data.delay());
            node.set("acceleration", (Object)data.acceleration());
            node.set("deceleration", (Object)data.deceleration());
            node.set("predict", (Object)data.predict());
        } else {
            config.remove("wait");
        }
    }
}

