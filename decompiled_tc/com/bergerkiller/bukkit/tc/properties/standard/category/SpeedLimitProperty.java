/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.properties.standard.category;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.PropertyCheckPermission;
import com.bergerkiller.bukkit.tc.properties.api.PropertyInvalidInputException;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.PropertySelectorCondition;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedProperty;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedStandardTrainProperty;
import com.bergerkiller.bukkit.tc.utils.FormattedSpeed;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class SpeedLimitProperty
extends FieldBackedStandardTrainProperty.StandardDouble {
    @CommandTargetTrain
    @PropertyCheckPermission(value="maxspeed")
    @Command(value="train maxspeed|speedlimit <speed>")
    @CommandDescription(value="Sets a new  speed limit for the train")
    private void trainSetSpeedLimit(CommandSender sender, TrainProperties properties, @Argument(value="speed") FormattedSpeed speed) {
        properties.setSpeedLimit(speed.getValue());
        this.trainGetSpeedLimit(sender, properties);
    }

    @Command(value="train maxspeed|speedlimit")
    @CommandDescription(value="Reads the current speed limit set for the train")
    private void trainGetSpeedLimit(CommandSender sender, TrainProperties properties) {
        double currSpeed = properties.hasHolder() ? properties.getHolder().head().getRealSpeedLimited() : 0.0;
        sender.sendMessage(ChatColor.YELLOW + "Maximum speed: " + SpeedLimitProperty.formatSpeed(properties.getSpeedLimit(), ChatColor.WHITE));
        sender.sendMessage(ChatColor.YELLOW + "Current speed: " + SpeedLimitProperty.formatSpeed(currSpeed, currSpeed == properties.getSpeedLimit() ? ChatColor.RED : ChatColor.WHITE));
    }

    private static String formatSpeed(double speed, ChatColor baseColor) {
        double speedKMH = MathUtil.round((double)(speed * 72000.0 / 1000.0), (int)2);
        double speedMPH = MathUtil.round((double)(speed * 72000.0 / 1609.344), (int)2);
        return baseColor.toString() + MathUtil.round((double)speed, (int)4) + " blocks/tick (" + ChatColor.BLUE + speedKMH + " km/h" + baseColor + " / " + ChatColor.BLUE + speedMPH + " mph" + baseColor + ")";
    }

    @PropertyParser(value="maxspeed|speedlimit")
    public double parse(String input) {
        double result = Util.parseVelocity(input, Double.NaN);
        if (Double.isNaN(result)) {
            throw new PropertyInvalidInputException("Not a valid number or speed expression");
        }
        return result;
    }

    @PropertySelectorCondition(value="speedlimit")
    public double getSelectorDoubleValue(TrainProperties properties) {
        return this.getDouble(properties);
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_MAXSPEED.has(sender);
    }

    @Override
    public double getDoubleDefault() {
        return 0.4;
    }

    @Override
    public double getDoubleData(FieldBackedProperty.TrainInternalData data) {
        return data.speedLimit;
    }

    @Override
    public void setDoubleData(FieldBackedProperty.TrainInternalData data, double value) {
        data.speedLimit = value;
    }

    @Override
    public Optional<Double> readFromConfig(ConfigurationNode config) {
        return Util.getConfigOptional(config, "speedLimit", Double.TYPE);
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<Double> value) {
        Util.setConfigOptional(config, "speedLimit", value);
    }

    @Override
    public void set(TrainProperties properties, Double value) {
        double valuePrim = value;
        if (valuePrim < 0.0) {
            value = 0.0;
        } else if (valuePrim > TCConfig.maxVelocity) {
            value = TCConfig.maxVelocity;
        }
        super.set(properties, value);
    }
}

