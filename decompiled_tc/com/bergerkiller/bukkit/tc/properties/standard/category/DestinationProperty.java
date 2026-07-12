/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Quoted
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.properties.standard.category;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Quoted;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.ICartProperty;
import com.bergerkiller.bukkit.tc.properties.api.PropertyCheckPermission;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.PropertySelectorCondition;
import com.bergerkiller.bukkit.tc.properties.standard.StandardProperties;
import java.util.List;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class DestinationProperty
implements ICartProperty<String> {
    @CommandTargetTrain
    @Command(value="train destination|dest none")
    @CommandDescription(value="Clears the destination set for a train")
    private void commandClearProperty(CommandSender sender, TrainProperties properties) {
        this.commandSetProperty(sender, properties, "");
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="destination")
    @Command(value="train destination|dest <destination>")
    @CommandDescription(value="Sets a new destination for the train to go to")
    private void commandSetProperty(CommandSender sender, TrainProperties properties, @Quoted @Argument(value="destination", suggestions="destinations") String destination) {
        properties.setDestination(destination);
        this.commandGetProperty(sender, properties);
    }

    @Command(value="train destination|dest")
    @CommandDescription(value="Displays the current destination set for the train")
    private void commandGetProperty(CommandSender sender, TrainProperties properties) {
        if (properties.hasDestination()) {
            sender.sendMessage(ChatColor.YELLOW + "Train destination is set to: " + ChatColor.WHITE + properties.getDestination());
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Train destination is set to: " + ChatColor.RED + "None");
        }
    }

    @CommandTargetTrain
    @Command(value="cart destination|dest none")
    @CommandDescription(value="Clears the destination set for a cart")
    private void commandClearProperty(CommandSender sender, CartProperties properties) {
        this.commandSetProperty(sender, properties, "");
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="destination")
    @Command(value="cart destination|dest <destination>")
    @CommandDescription(value="Sets a new destination for the cart to go to")
    private void commandSetProperty(CommandSender sender, CartProperties properties, @Quoted @Argument(value="destination", suggestions="destinations") String destination) {
        properties.setDestination(destination);
        this.commandGetProperty(sender, properties);
    }

    @Command(value="cart destination|dest")
    @CommandDescription(value="Displays the current destination set for the cart")
    private void commandGetProperty(CommandSender sender, CartProperties properties) {
        if (properties.hasDestination()) {
            sender.sendMessage(ChatColor.YELLOW + "Cart destination is set to: " + ChatColor.WHITE + properties.getDestination());
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Cart destination is set to: " + ChatColor.RED + "None");
        }
    }

    @PropertyParser(value="destination")
    public String parseDestination(String input) {
        return input;
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_DESTINATION.has(sender);
    }

    @Override
    public String getDefault() {
        return "";
    }

    @Override
    public Optional<String> readFromConfig(ConfigurationNode config) {
        return Util.getConfigOptional(config, "destination", String.class);
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<String> value) {
        Util.setConfigOptional(config, "destination", value);
    }

    @Override
    public void set(CartProperties properties, String value) {
        int nextIndex;
        List route;
        int prior_route_index = properties.getCurrentRouteDestinationIndex();
        ICartProperty.super.set(properties, value);
        if (!value.isEmpty() && prior_route_index != -1 && value.equals((route = (List)StandardProperties.DESTINATION_ROUTE.get(properties)).get(nextIndex = (prior_route_index + 1) % route.size()))) {
            StandardProperties.DESTINATION_ROUTE_INDEX.set(properties, Integer.valueOf(nextIndex));
        }
    }

    @Override
    @PropertySelectorCondition(value="destination")
    public String get(TrainProperties properties) {
        for (CartProperties cprop : properties) {
            String destination = (String)this.get(cprop);
            if (destination.isEmpty()) continue;
            return destination;
        }
        return "";
    }
}

