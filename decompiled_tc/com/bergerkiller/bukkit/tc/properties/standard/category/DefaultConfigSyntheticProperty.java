/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.properties.standard.category;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.TrainPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.api.ISyntheticProperty;
import com.bergerkiller.bukkit.tc.properties.api.PropertyCheckPermission;
import com.bergerkiller.bukkit.tc.properties.api.PropertyInvalidInputException;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.defaults.DefaultProperties;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class DefaultConfigSyntheticProperty
implements ISyntheticProperty<DefaultProperties> {
    @CommandTargetTrain
    @PropertyCheckPermission(value="setdefault")
    @Command(value="train defaults apply <defaultname>")
    @CommandDescription(value="Applies defaults from DefaultTrainProperties to a train")
    private void commandApplyDefaults(CommandSender sender, TrainProperties properties, @Argument(value="defaultname") String defaultName) {
        DefaultProperties defaults = TrainPropertiesStore.getDefaultsByName(defaultName);
        if (defaults == null) {
            sender.sendMessage(ChatColor.RED + "Train Property Defaults by key " + ChatColor.BLUE + "'" + defaultName + "' " + ChatColor.RED + " does not exist!");
            return;
        }
        properties.apply(defaults);
        sender.sendMessage(ChatColor.GREEN + "Default properties '" + defaultName + "' applied!");
    }

    @PropertyParser(value="applydefault|setdefault|default")
    public DefaultProperties parseDefaultConfig(String defaultName) {
        DefaultProperties defaults = TrainPropertiesStore.getDefaultsByName(defaultName);
        if (defaults == null) {
            throw new PropertyInvalidInputException("Train Property Defaults by key '" + defaultName + "' does not exist");
        }
        return defaults;
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_APPLYDEFAULTS.has(sender);
    }

    @Override
    public DefaultProperties getDefault() {
        return TrainPropertiesStore.getDefaultsByName("default");
    }

    @Override
    public DefaultProperties get(CartProperties properties) {
        return this.getDefault();
    }

    @Override
    public DefaultProperties get(TrainProperties properties) {
        return this.getDefault();
    }

    @Override
    public void set(CartProperties properties, DefaultProperties config) {
        if (config != null) {
            config.applyTo(properties);
        }
    }

    @Override
    public void set(TrainProperties properties, DefaultProperties config) {
        if (config != null) {
            config.applyTo(properties);
        }
    }
}

