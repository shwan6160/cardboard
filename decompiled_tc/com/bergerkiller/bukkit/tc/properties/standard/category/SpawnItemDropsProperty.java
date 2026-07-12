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

public final class SpawnItemDropsProperty
implements ICartProperty<Boolean> {
    @CommandTargetTrain
    @PropertyCheckPermission(value="spawnitemdrops")
    @Command(value="train spawnitemdrops <spawn>")
    @CommandDescription(value="Sets whether the train drops items when destroyed")
    private void setProperty(CommandSender sender, TrainProperties properties, @Argument(value="spawn") boolean spawn) {
        properties.set(this, spawn);
        this.getProperty(sender, properties);
    }

    @Command(value="train spawnitemdrops")
    @CommandDescription(value="Displays whether the train drops items when destroyed")
    private void getProperty(CommandSender sender, TrainProperties properties) {
        sender.sendMessage(ChatColor.YELLOW + "Train drops items when destroyed: " + Localization.boolStr(properties.get(this)));
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="spawnitemdrops")
    @Command(value="cart spawnitemdrops <spawn>")
    @CommandDescription(value="Sets whether the cart drops items when destroyed")
    private void setProperty(CommandSender sender, CartProperties properties, @Argument(value="spawn") boolean spawn) {
        properties.set(this, spawn);
        this.getProperty(sender, properties);
    }

    @Command(value="cart spawnitemdrops")
    @CommandDescription(value="Displays whether the cart drops items when destroyed")
    private void getProperty(CommandSender sender, CartProperties properties) {
        sender.sendMessage(ChatColor.YELLOW + "Cart drops items when destroyed: " + Localization.boolStr(properties.get(this)));
    }

    @PropertyParser(value="spawnitemdrops|spawndrops|killdrops")
    public boolean parseSpawnItemDrops(PropertyParseContext<Boolean> context) {
        return context.inputBoolean();
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_SPAWNITEMDROPS.has(sender);
    }

    @Override
    public Boolean getDefault() {
        return Boolean.TRUE;
    }

    @Override
    public Optional<Boolean> readFromConfig(ConfigurationNode config) {
        return Util.getConfigOptional(config, "spawnItemDrops", Boolean.TYPE);
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<Boolean> value) {
        Util.setConfigOptional(config, "spawnItemDrops", value);
    }
}

