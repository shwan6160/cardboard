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
 *  org.bukkit.entity.Player
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
import com.bergerkiller.bukkit.tc.properties.standard.StandardProperties;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedProperty;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedStandardCartProperty;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class OwnerSetProperty
extends FieldBackedStandardCartProperty<Set<String>>
implements IStringSetProperty {
    public void addOwnerInfo(MessageBuilder message, IProperties properties) {
        if (!properties.hasOwners() && !properties.hasOwnerPermissions()) {
            message.yellow(new Object[]{"Owned by: "}).green(new Object[]{"Everyone"}).white(new Object[]{" (use /train claim)"});
        } else {
            if (properties.hasOwners()) {
                message.yellow(new Object[]{"Owned by: "});
                message.setSeparator(ChatColor.YELLOW, " / ").setIndent(4);
                for (String owner : properties.getOwners()) {
                    message.white(new Object[]{owner});
                }
                message.clearSeparator().setIndent(0);
            }
            if (properties.hasOwnerPermissions()) {
                StandardProperties.OWNER_PERMISSIONS.addOwnerPermInfo(message, properties);
            }
        }
    }

    @Command(value="cart owners")
    @CommandDescription(value="Display the owners set for the cart")
    private void getProperty(CommandSender sender, CartProperties properties) {
        MessageBuilder message = new MessageBuilder();
        this.addOwnerInfo(message, properties);
        message.send(sender);
    }

    @PropertyCheckPermission(value="owners")
    @Command(value="cart claim")
    @CommandDescription(value="Sets the caller as the sole owner of a cart")
    private void setPropertyClaim(Player sender, CartProperties properties) {
        properties.clearOwners();
        properties.setOwner(sender.getName(), true);
        sender.sendMessage(ChatColor.GREEN + "You are now the only owner of this cart!");
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="owners")
    @Command(value="cart owners add <player_names>")
    @CommandDescription(value="Adds players as owners of a cart")
    private void setPropertyAdd(CommandSender sender, CartProperties properties, @FlagYielding @Argument(value="player_names") String[] playerNames) {
        if (playerNames != null && playerNames.length > 0) {
            sender.sendMessage(ChatColor.GREEN + "Adding owners to cart: " + StringUtil.combineNames((String[])playerNames));
            for (String playerName : playerNames) {
                properties.setOwner(playerName, true);
            }
        }
        this.getProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="owners")
    @Command(value="cart owners remove <player_names>")
    @CommandDescription(value="Removes players as owners of a cart")
    private void setPropertyRemove(CommandSender sender, CartProperties properties, @FlagYielding @Argument(value="player_names") String[] playerNames) {
        if (playerNames != null && playerNames.length > 0) {
            sender.sendMessage(ChatColor.GREEN + "Removing owners from cart: " + StringUtil.combineNames((String[])playerNames));
            for (String playerName : playerNames) {
                properties.setOwner(playerName, false);
            }
        }
        this.getProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="owners")
    @Command(value="cart owners set <player_names>")
    @CommandDescription(value="Discards previous owners and sets players as owners of a cart")
    private void setProperty(CommandSender sender, CartProperties properties, @FlagYielding @Argument(value="player_names") String[] playerNames) {
        if (playerNames != null && playerNames.length > 0) {
            properties.clearOwners();
            sender.sendMessage(ChatColor.GREEN + "Set new owners of cart: " + StringUtil.combineNames((String[])playerNames));
            for (String playerName : playerNames) {
                properties.setOwner(playerName, true);
            }
        } else {
            this.setPropertyClear(sender, properties);
        }
    }

    @PropertyCheckPermission(value="owners")
    @Command(value="cart owners clear")
    @CommandDescription(value="Clears all owners set for a cart, allowing everyone access")
    private void setPropertyClear(CommandSender sender, CartProperties properties) {
        properties.clearOwners();
        sender.sendMessage(ChatColor.GREEN + "Owners cleared! Everyone can now modify the cart.");
        if (properties.hasOwnerPermissions()) {
            this.getProperty(sender, properties);
        }
    }

    @Command(value="train owners")
    @CommandDescription(value="Display the owners set for carts of the train")
    private void getProperty(CommandSender sender, TrainProperties properties) {
        MessageBuilder message = new MessageBuilder();
        this.addOwnerInfo(message, properties);
        message.send(sender);
    }

    @PropertyCheckPermission(value="owners")
    @Command(value="train claim")
    @CommandDescription(value="Sets the caller as the sole owner of a train")
    private void setPropertyClaim(Player sender, TrainProperties properties) {
        properties.clearOwners();
        properties.setOwner(sender.getName(), true);
        sender.sendMessage(ChatColor.GREEN + "You are now the only owner of this train!");
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="owners")
    @Command(value="train owners add <player_names>")
    @CommandDescription(value="Adds players as owners of all carts of a train")
    private void setPropertyAdd(CommandSender sender, TrainProperties properties, @FlagYielding @Argument(value="player_names") String[] playerNames) {
        if (playerNames != null && playerNames.length > 0) {
            sender.sendMessage(ChatColor.GREEN + "Adding owners to train: " + StringUtil.combineNames((String[])playerNames));
            for (String playerName : playerNames) {
                properties.setOwner(playerName, true);
            }
        }
        this.getProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="owners")
    @Command(value="train owners remove <player_names>")
    @CommandDescription(value="Removes players as owners of all carts of a train")
    private void setPropertyRemove(CommandSender sender, TrainProperties properties, @FlagYielding @Argument(value="player_names") String[] playerNames) {
        if (playerNames != null && playerNames.length > 0) {
            sender.sendMessage(ChatColor.GREEN + "Removing owners from train: " + StringUtil.combineNames((String[])playerNames));
            for (String playerName : playerNames) {
                properties.setOwner(playerName, false);
            }
        }
        this.getProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="owners")
    @Command(value="train owners set <player_names>")
    @CommandDescription(value="Discards previous owners and sets players as owners of all carts of a train")
    private void setProperty(CommandSender sender, TrainProperties properties, @FlagYielding @Argument(value="player_names") String[] playerNames) {
        if (playerNames != null && playerNames.length > 0) {
            properties.clearOwners();
            sender.sendMessage(ChatColor.GREEN + "Set new owners of train: " + StringUtil.combineNames((String[])playerNames));
            for (String playerName : playerNames) {
                properties.setOwner(playerName, true);
            }
        } else {
            this.setPropertyClear(sender, properties);
        }
    }

    @PropertyCheckPermission(value="owners")
    @Command(value="train owners clear")
    @CommandDescription(value="Clears all owners set for a train, allowing everyone access")
    private void setPropertyClear(CommandSender sender, TrainProperties properties) {
        properties.clearOwners();
        sender.sendMessage(ChatColor.GREEN + "Owners cleared! Everyone can now modify the train.");
        if (properties.hasOwnerPermissions()) {
            this.getProperty(sender, properties);
        }
    }

    @PropertyParser(value="setowner|owners set")
    public Set<String> parseSet(String input) {
        return input.isEmpty() ? Collections.emptySet() : Collections.singleton(input.toLowerCase());
    }

    @PropertyParser(value="clearowner|clearowners|owners clear")
    public Set<String> parseClear(String input) {
        return Collections.emptySet();
    }

    @PropertyParser(value="addowner|owners add", processPerCart=true)
    public Set<String> parseAdd(PropertyParseContext<Set<String>> context) {
        String name_lc = context.input().toLowerCase();
        if (name_lc.isEmpty() || context.current().contains(name_lc)) {
            return context.current();
        }
        HashSet<String> newPerms = new HashSet<String>((Collection)context.current());
        newPerms.add(name_lc);
        return Collections.unmodifiableSet(newPerms);
    }

    @PropertyParser(value="remowner|owners rem|owners remove", processPerCart=true)
    public Set<String> parseRemove(PropertyParseContext<Set<String>> context) {
        String name_lc = context.input().toLowerCase();
        if (name_lc.isEmpty() || !context.current().contains(name_lc)) {
            return context.current();
        }
        HashSet newPerms = new HashSet(context.current());
        newPerms.remove(name_lc);
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
        return "owners";
    }

    @Override
    public Set<String> getData(FieldBackedProperty.CartInternalData data) {
        return data.owners;
    }

    @Override
    public void setData(FieldBackedProperty.CartInternalData data, Set<String> value) {
        data.owners = value;
    }

    @Override
    public Optional<Set<String>> readFromConfig(ConfigurationNode config) {
        return Util.getConfigStringSetOptional(config, "owners");
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<Set<String>> value) {
        Util.setConfigStringCollectionOptional(config, "owners", value);
    }

    @Override
    public Set<String> get(TrainProperties properties) {
        return FieldBackedProperty.TrainInternalData.get((TrainProperties)properties).owners.update(properties, this);
    }
}

