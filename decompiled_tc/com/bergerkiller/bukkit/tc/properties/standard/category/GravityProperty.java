/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.properties.standard.category;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.PropertyCheckPermission;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.PropertySelectorCondition;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyParseContext;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedProperty;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedStandardTrainProperty;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class GravityProperty
extends FieldBackedStandardTrainProperty.StandardDouble {
    @CommandTargetTrain
    @PropertyCheckPermission(value="gravity")
    @Command(value="train gravity <multiplier>")
    @CommandDescription(value="Sets a gravity effect multiplier for the train")
    private void trainSetProperty(CommandSender sender, TrainProperties properties, @Argument(value="multiplier") double multiplier) {
        properties.setGravity(multiplier);
        this.trainGetProperty(sender, properties);
    }

    @Command(value="train gravity")
    @CommandDescription(value="Displays the gravity multiplier currently set for the train")
    private void trainGetProperty(CommandSender sender, TrainProperties properties) {
        if (properties.getGravity() == 1.0) {
            sender.sendMessage(ChatColor.YELLOW + "Gravity multiplier: " + ChatColor.WHITE + "1 X (default)");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Gravity multiplier: " + ChatColor.WHITE + properties.getGravity() + " X");
        }
    }

    @PropertyParser(value="gravity")
    public double parseGravity(PropertyParseContext<Double> context) {
        return context.inputDouble();
    }

    @PropertySelectorCondition(value="gravity")
    public double selectorgetValue(TrainProperties properties) {
        return this.getDouble(properties);
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_GRAVITY.has(sender);
    }

    @Override
    public double getDoubleDefault() {
        return 1.0;
    }

    @Override
    public double getDoubleData(FieldBackedProperty.TrainInternalData data) {
        return data.gravity;
    }

    @Override
    public void setDoubleData(FieldBackedProperty.TrainInternalData data, double value) {
        data.gravity = value;
    }

    @Override
    public Optional<Double> readFromConfig(ConfigurationNode config) {
        return Util.getConfigOptional(config, "gravity", Double.TYPE);
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<Double> value) {
        Util.setConfigOptional(config, "gravity", value);
    }
}

