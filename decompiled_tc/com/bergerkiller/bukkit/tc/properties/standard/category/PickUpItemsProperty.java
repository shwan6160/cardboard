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
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.PropertyCheckPermission;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyParseContext;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedProperty;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedStandardCartProperty;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class PickUpItemsProperty
extends FieldBackedStandardCartProperty<Boolean> {
    @CommandTargetTrain
    @PropertyCheckPermission(value="pickupitems")
    @Command(value="train pickupitems|pickup <pickup>")
    @CommandDescription(value="Sets whether the train picks up items off the ground")
    private void setProperty(CommandSender sender, TrainProperties properties, @Argument(value="pickup") boolean pickup) {
        properties.set(this, pickup);
        this.getProperty(sender, properties);
    }

    @Command(value="train pickupitems|pickup")
    @CommandDescription(value="Displays whether the train picks up items off the ground")
    private void getProperty(CommandSender sender, TrainProperties properties) {
        sender.sendMessage(ChatColor.YELLOW + "Train picks up items off the ground: " + Localization.boolStr(properties.get(this)));
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="pickupitems")
    @Command(value="cart pickupitems|pickup <pickup>")
    @CommandDescription(value="Sets whether the cart picks up items off the ground")
    private void setProperty(CommandSender sender, CartProperties properties, @Argument(value="pickup") boolean pickup) {
        properties.set(this, pickup);
        this.getProperty(sender, properties);
    }

    @Command(value="cart pickupitems|pickup")
    @CommandDescription(value="Displays whether the cart picks up items off the ground")
    private void getProperty(CommandSender sender, CartProperties properties) {
        sender.sendMessage(ChatColor.YELLOW + "Cart picks up items off the ground: " + Localization.boolStr(properties.get(this)));
    }

    @PropertyParser(value="pickup|pickupitems")
    public boolean parsePickupItems(PropertyParseContext<Boolean> context) {
        return context.inputBoolean();
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_PICKUPITEMS.has(sender);
    }

    @Override
    public Boolean getDefault() {
        return Boolean.FALSE;
    }

    @Override
    public Boolean getData(FieldBackedProperty.CartInternalData data) {
        return data.pickUpItems;
    }

    @Override
    public void setData(FieldBackedProperty.CartInternalData data, Boolean value) {
        data.pickUpItems = value;
    }

    @Override
    public Optional<Boolean> readFromConfig(ConfigurationNode config) {
        return Util.getConfigOptional(config, "pickUp", Boolean.TYPE);
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<Boolean> value) {
        Util.setConfigOptional(config, "pickUp", value);
    }
}

