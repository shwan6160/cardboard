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

public final class OnlyOwnersCanEnterProperty
extends FieldBackedStandardCartProperty<Boolean> {
    @CommandTargetTrain
    @PropertyCheckPermission(value="onlyownerscanenter")
    @Command(value="train onlyownerscanenter <state>")
    @CommandDescription(value="Sets whether only owners can enter the train")
    private void setProperty(CommandSender sender, TrainProperties properties, @Argument(value="state") boolean state) {
        properties.setCanOnlyOwnersEnter(state);
        this.getProperty(sender, properties);
    }

    @Command(value="train onlyownerscanenter")
    @CommandDescription(value="Displays whether only owners can enter the train")
    private void getProperty(CommandSender sender, TrainProperties properties) {
        sender.sendMessage(ChatColor.YELLOW + "Only owners can enter the train: " + Localization.boolStr(properties.getCanOnlyOwnersEnter()));
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="onlyownerscanenter")
    @Command(value="cart onlyownerscanenter <state>")
    @CommandDescription(value="Sets whether only owners can enter the cart")
    private void setProperty(CommandSender sender, CartProperties properties, @Argument(value="state") boolean state) {
        properties.setCanOnlyOwnersEnter(state);
        this.getProperty(sender, properties);
    }

    @Command(value="cart onlyownerscanenter")
    @CommandDescription(value="Displays whether only owners can enter the cart")
    private void getProperty(CommandSender sender, CartProperties properties) {
        sender.sendMessage(ChatColor.YELLOW + "Only owners can enter the cart: " + Localization.boolStr(properties.getCanOnlyOwnersEnter()));
    }

    @PropertyParser(value="onlyownerscanenter")
    public boolean parseCanEnter(PropertyParseContext<Boolean> context) {
        return context.inputBoolean();
    }

    @Override
    public boolean isListed() {
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_ONLYOWNERSCANENTER.has(sender);
    }

    @Override
    public Boolean getDefault() {
        return Boolean.FALSE;
    }

    @Override
    public Boolean getData(FieldBackedProperty.CartInternalData data) {
        return data.canOnlyOwnersEnter;
    }

    @Override
    public void setData(FieldBackedProperty.CartInternalData data, Boolean value) {
        data.canOnlyOwnersEnter = value;
    }

    @Override
    public Optional<Boolean> readFromConfig(ConfigurationNode config) {
        if (config.contains("public")) {
            return Optional.of((Boolean)config.get("public", (Object)true) == false);
        }
        return Util.getConfigOptional(config, "onlyOwnersCanEnter", Boolean.TYPE);
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<Boolean> value) {
        config.remove("public");
        Util.setConfigOptional(config, "onlyOwnersCanEnter", value);
    }

    @Override
    public Boolean get(TrainProperties properties) {
        for (CartProperties cProp : properties) {
            if (((Boolean)this.get(cProp)).booleanValue()) continue;
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}

