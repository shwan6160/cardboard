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
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class EnterMessageProperty
implements ICartProperty<String> {
    @CommandTargetTrain
    @PropertyCheckPermission(value="entermessage")
    @Command(value="train entermessage <message>")
    @CommandDescription(value="Sets the message displayed to players when they enter the train")
    private void setProperty(CommandSender sender, TrainProperties properties, @Quoted @Argument(value="message") String message) {
        properties.set(this, message);
        this.getProperty(sender, properties);
    }

    @Command(value="train entermessage")
    @CommandDescription(value="Displays the message that will be displayed to players when they enter the train")
    private void getProperty(CommandSender sender, TrainProperties properties) {
        String message = properties.get(this);
        if (message.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "Message displayed: " + ChatColor.RED + "NONE");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Message displayed: " + ChatColor.WHITE + message);
        }
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="entermessage")
    @Command(value="cart entermessage <message>")
    @CommandDescription(value="Sets the message displayed to players when they enter the cart")
    private void setProperty(CommandSender sender, CartProperties properties, @Quoted @Argument(value="message") String message) {
        properties.set(this, message);
        this.getProperty(sender, properties);
    }

    @Command(value="cart entermessage")
    @CommandDescription(value="Displays the message that will be displayed to players when they enter the cart")
    private void getProperty(CommandSender sender, CartProperties properties) {
        String message = properties.get(this);
        if (message.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "Message displayed: " + ChatColor.RED + "NONE");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Message displayed: " + ChatColor.WHITE + message);
        }
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_ENTER_MESSAGE.has(sender);
    }

    @PropertyParser(value="entermessage|entermsg")
    public String parseMessage(String input) {
        return input;
    }

    @Override
    public String getDefault() {
        return "";
    }

    @Override
    public Optional<String> readFromConfig(ConfigurationNode config) {
        return Util.getConfigOptional(config, "enterMessage", String.class);
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<String> value) {
        Util.setConfigOptional(config, "enterMessage", value);
    }
}

