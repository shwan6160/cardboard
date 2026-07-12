/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.MessageBuilder
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.FlagYielding
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.properties.standard.category;

import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.FlagYielding;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.IProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.IStringSetProperty;
import com.bergerkiller.bukkit.tc.properties.api.PropertyCheckPermission;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyParseContext;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedProperty;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedStandardCartProperty;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class OwnerPermissionSet
extends FieldBackedStandardCartProperty<Set<String>>
implements IStringSetProperty {
    public void addOwnerPermInfo(MessageBuilder message, IProperties properties) {
        if (properties.hasOwnerPermissions()) {
            message.yellow(new Object[]{"Owned by players with the permissions:"});
            for (String ownerPerm : properties.getOwnerPermissions()) {
                message.newLine().yellow(new Object[]{"  - "}).white(new Object[]{ownerPerm});
            }
        } else {
            message.yellow(new Object[]{"No owner permission rules are set."});
        }
    }

    @PropertyCheckPermission(value="ownerperms")
    @Command(value="cart owners permission")
    @CommandDescription(value="Display the owner permissions set for a cart")
    private void getProperty(CommandSender sender, CartProperties properties) {
        MessageBuilder message = new MessageBuilder();
        this.addOwnerPermInfo(message, properties);
        message.send(sender);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="ownerperms")
    @Command(value="cart owners permission add <permissions>")
    @CommandDescription(value="Adds permissions players need to access a cart")
    private void setPropertyAdd(CommandSender sender, CartProperties properties, @FlagYielding @Argument(value="permissions") String[] permissions) {
        if (permissions != null && permissions.length > 0) {
            sender.sendMessage(ChatColor.GREEN + "Adding permission rules to cart: " + StringUtil.combineNames((String[])permissions));
            for (String permission : permissions) {
                properties.addOwnerPermission(permission);
            }
        }
        this.getProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="ownerperms")
    @Command(value="cart owners permission remove <permissions>")
    @CommandDescription(value="Removes permissions players need to access a cart")
    private void setPropertyRemove(CommandSender sender, CartProperties properties, @FlagYielding @Argument(value="permissions") String[] permissions) {
        if (permissions != null && permissions.length > 0) {
            sender.sendMessage(ChatColor.GREEN + "Removing permission rules from cart: " + StringUtil.combineNames((String[])permissions));
            for (String permission : permissions) {
                properties.removeOwnerPermission(permission);
            }
        }
        this.getProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="ownerperms")
    @Command(value="cart owners permission set <permissions>")
    @CommandDescription(value="Discards previous owner permissions and sets new permissions players need to access a cart")
    private void setProperty(CommandSender sender, CartProperties properties, @FlagYielding @Argument(value="permissions") String[] permissions) {
        if (permissions != null && permissions.length > 0) {
            properties.clearOwnerPermissions();
            sender.sendMessage(ChatColor.GREEN + "Set new permission rules for cart: " + StringUtil.combineNames((String[])permissions));
            for (String permission : permissions) {
                properties.addOwnerPermission(permission);
            }
        } else {
            this.setPropertyClear(sender, properties);
        }
    }

    @PropertyCheckPermission(value="ownerperms")
    @Command(value="cart owners permission clear")
    @CommandDescription(value="Clears all owner permissions set for a cart")
    private void setPropertyClear(CommandSender sender, CartProperties properties) {
        properties.clearOwnerPermissions();
        sender.sendMessage(ChatColor.GREEN + "Permission rules cleared.");
    }

    @PropertyCheckPermission(value="ownerperms")
    @Command(value="train owners permission")
    @CommandDescription(value="Display the owner permissions set for a train")
    private void getProperty(CommandSender sender, TrainProperties properties) {
        MessageBuilder message = new MessageBuilder();
        this.addOwnerPermInfo(message, properties);
        message.send(sender);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="ownerperms")
    @Command(value="train owners permission add <permissions>")
    @CommandDescription(value="Adds permissions players need to access a cart")
    private void setPropertyAdd(CommandSender sender, TrainProperties properties, @FlagYielding @Argument(value="permissions") String[] permissions) {
        if (permissions != null && permissions.length > 0) {
            sender.sendMessage(ChatColor.GREEN + "Adding permission rules to train: " + StringUtil.combineNames((String[])permissions));
            for (String permission : permissions) {
                properties.addOwnerPermission(permission);
            }
        }
        this.getProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="ownerperms")
    @Command(value="train owners permission remove <permissions>")
    @CommandDescription(value="Removes permissions players need to access a cart")
    private void setPropertyRemove(CommandSender sender, TrainProperties properties, @FlagYielding @Argument(value="permissions") String[] permissions) {
        if (permissions != null && permissions.length > 0) {
            sender.sendMessage(ChatColor.GREEN + "Removing permission rules from train: " + StringUtil.combineNames((String[])permissions));
            for (String permission : permissions) {
                properties.removeOwnerPermission(permission);
            }
        }
        this.getProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="ownerperms")
    @Command(value="train owners permission set <permissions>")
    @CommandDescription(value="Discards previous owner permissions and sets new permissions players need to access a train")
    private void setProperty(CommandSender sender, TrainProperties properties, @FlagYielding @Argument(value="permissions") String[] permissions) {
        if (permissions != null && permissions.length > 0) {
            properties.clearOwnerPermissions();
            sender.sendMessage(ChatColor.GREEN + "Set new permission rules for train: " + StringUtil.combineNames((String[])permissions));
            for (String permission : permissions) {
                properties.addOwnerPermission(permission);
            }
        } else {
            this.setPropertyClear(sender, properties);
        }
    }

    @PropertyCheckPermission(value="ownerperms")
    @Command(value="train owners permission clear")
    @CommandDescription(value="Clears all owner permissions set for a train")
    private void setPropertyClear(CommandSender sender, TrainProperties properties) {
        properties.clearOwnerPermissions();
        sender.sendMessage(ChatColor.GREEN + "Permission rules cleared.");
    }

    @PropertyParser(value="setownerperm|ownerperms set")
    public Set<String> parseSet(String input) {
        return input.isEmpty() ? Collections.emptySet() : Collections.singleton(input);
    }

    @PropertyParser(value="clearownerperm|ownerperms clear")
    public Set<String> parseClear(String input) {
        return Collections.emptySet();
    }

    @PropertyParser(value="addownerperm|ownerperms add", processPerCart=true)
    public Set<String> parseAdd(PropertyParseContext<Set<String>> context) {
        if (context.input().isEmpty() || context.current().contains(context.input())) {
            return context.current();
        }
        HashSet<String> newPerms = new HashSet<String>((Collection)context.current());
        newPerms.add(context.input());
        return Collections.unmodifiableSet(newPerms);
    }

    @PropertyParser(value="remownerperm|ownerperm rem|ownerperms remove", processPerCart=true)
    public Set<String> parseRemove(PropertyParseContext<Set<String>> context) {
        if (context.input().isEmpty() || !context.current().contains(context.input())) {
            return context.current();
        }
        HashSet newPerms = new HashSet(context.current());
        newPerms.remove(context.input());
        return Collections.unmodifiableSet(newPerms);
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_OWNERS.has(sender);
    }

    @Override
    public Set<String> getDefault() {
        return Collections.emptySet();
    }

    @Override
    public String getListedName() {
        return "owner perms";
    }

    @Override
    public Set<String> getData(FieldBackedProperty.CartInternalData data) {
        return data.ownerPermissions;
    }

    @Override
    public void setData(FieldBackedProperty.CartInternalData data, Set<String> value) {
        data.ownerPermissions = value;
    }

    @Override
    public Optional<Set<String>> readFromConfig(ConfigurationNode config) {
        return Util.getConfigStringSetOptional(config, "ownerPermissions");
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<Set<String>> value) {
        Util.setConfigStringCollectionOptional(config, "ownerPermissions", value);
    }

    @Override
    public Set<String> get(TrainProperties properties) {
        return FieldBackedProperty.TrainInternalData.get((TrainProperties)properties).ownerPermissions.update(properties, this);
    }
}

