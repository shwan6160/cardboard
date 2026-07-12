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
import com.bergerkiller.bukkit.tc.properties.api.ICartProperty;
import com.bergerkiller.bukkit.tc.properties.api.PropertyCheckPermission;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyParseContext;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class PlayerEnterProperty
implements ICartProperty<Boolean> {
    @CommandTargetTrain
    @PropertyCheckPermission(value="playerenter")
    @Command(value="train playerenter|allowplayerenter <allow>")
    @CommandDescription(value="Sets whether players can enter carts of this train")
    private void commandSetProperty(CommandSender sender, TrainProperties properties, @Argument(value="allow") boolean allow) throws Throwable {
        properties.setPlayersEnter(allow);
        this.commandGetProperty(sender, properties);
    }

    @Command(value="train playerenter|allowplayerenter")
    @CommandDescription(value="Gets whether players can enter carts of this train")
    private void commandGetProperty(CommandSender sender, TrainProperties properties) {
        sender.sendMessage(ChatColor.YELLOW + "Players can enter the train: " + Localization.boolStr(properties.getPlayersEnter()));
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="playerenter")
    @Command(value="cart playerenter|allowplayerenter <allow>")
    @CommandDescription(value="Sets whether players can enter the cart")
    private void commandSetProperty(CommandSender sender, CartProperties properties, @Argument(value="allow") boolean allow) {
        properties.setPlayersEnter(allow);
        this.commandGetProperty(sender, properties);
    }

    @Command(value="cart playerenter|allowplayerenter")
    @CommandDescription(value="Gets whether players can enter the cart")
    private void commandGetProperty(CommandSender sender, CartProperties properties) {
        sender.sendMessage(ChatColor.YELLOW + "Players can enter the cart: " + Localization.boolStr(properties.getPlayersEnter()));
    }

    @PropertyParser(value="allowplayerenter|playerenter")
    public boolean parsePlayerEnter(PropertyParseContext<Boolean> context) {
        return context.inputBoolean();
    }

    @Override
    public String getListedName() {
        return "playerenter";
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_PLAYERENTER.has(sender);
    }

    @Override
    public Boolean getDefault() {
        return Boolean.TRUE;
    }

    @Override
    public Optional<Boolean> readFromConfig(ConfigurationNode config) {
        return Util.getConfigOptional(config, "allowPlayerEnter", Boolean.TYPE);
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<Boolean> value) {
        Util.setConfigOptional(config, "allowPlayerEnter", value);
    }
}

