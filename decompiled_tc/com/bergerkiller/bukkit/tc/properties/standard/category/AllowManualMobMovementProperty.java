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
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.PropertyCheckPermission;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyParseContext;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedProperty;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedStandardTrainProperty;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class AllowManualMobMovementProperty
extends FieldBackedStandardTrainProperty<Boolean> {
    @CommandTargetTrain
    @PropertyCheckPermission(value="allowmobmanual")
    @Command(value="train manualmovement mob <enabled>")
    @CommandDescription(value="Sets whether mobs seated in the train can cause the train to move")
    private void getProperty(CommandSender sender, TrainProperties properties, @Argument(value="enabled") boolean enabled) {
        properties.set(this, enabled);
        this.getProperty(sender, properties);
    }

    @Command(value="train manualmovement mob")
    @CommandDescription(value="Displays whether mobs seated in the train can cause the train to move")
    private void getProperty(CommandSender sender, TrainProperties properties) {
        sender.sendMessage(ChatColor.YELLOW + "Mobs in the train can set the train in motion: " + Localization.boolStr(properties.get(this)));
    }

    @PropertyParser(value="allowmobmanual|mobmanualmove|mobmanual|manualmovement mob")
    public boolean parseAllowMovement(PropertyParseContext<Boolean> context) {
        return context.inputBoolean();
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_ALLOWMOBMANUALMOVEMENT.has(sender);
    }

    @Override
    public Boolean getDefault() {
        return Boolean.FALSE;
    }

    @Override
    public Boolean getData(FieldBackedProperty.TrainInternalData data) {
        return data.allowMobManualMovement;
    }

    @Override
    public void setData(FieldBackedProperty.TrainInternalData data, Boolean value) {
        data.allowMobManualMovement = value;
    }

    @Override
    public Optional<Boolean> readFromConfig(ConfigurationNode config) {
        return Util.getConfigOptional(config, "allowMobManualMovement", Boolean.TYPE);
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<Boolean> value) {
        Util.setConfigOptional(config, "allowMobManualMovement", value);
    }
}

