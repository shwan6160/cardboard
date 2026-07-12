/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.mountiplex.reflection.util.FastMethod
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.properties.standard.category;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.IPropertiesHolder;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.ICartProperty;
import com.bergerkiller.bukkit.tc.properties.api.PropertyCheckPermission;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyParseContext;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.lang.reflect.Method;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class PaperTrackingRangeProperty
implements ICartProperty<Integer> {
    public static final PaperTrackingRangeProperty INSTANCE = new PaperTrackingRangeProperty();
    private final FastMethod<Void> setCustomTrackingRange = new FastMethod();

    @CommandTargetTrain
    @PropertyCheckPermission(value="trackingrange")
    @Command(value="train trackingrange reset")
    @CommandDescription(value="Resets the view distance players inside the train have to the defaults")
    private void resetProperty(CommandSender sender, TrainProperties properties) {
        this.setProperty(sender, properties, -1);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="trackingrange")
    @Command(value="train trackingrange <num_blocks>")
    @CommandDescription(value="Sets the view distance players inside the train have")
    private void setProperty(CommandSender sender, TrainProperties properties, @Argument(value="num_blocks") int distance) {
        properties.set(this, distance);
        this.getProperty(sender, properties);
    }

    @Command(value="train trackingrange")
    @CommandDescription(value="Displays the view distance players inside the train have")
    private void getProperty(CommandSender sender, TrainProperties properties) {
        int distance = properties.get(this);
        if (distance >= 0) {
            sender.sendMessage(ChatColor.YELLOW + "Train is visible from: " + ChatColor.WHITE + distance + " blocks");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Train is visible from: " + ChatColor.RED + "Default (not set)");
        }
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="trackingrange")
    @Command(value="cart trackingrange reset")
    @CommandDescription(value="Resets the view distance players inside the cart have to the defaults")
    private void resetProperty(CommandSender sender, CartProperties properties) {
        this.setProperty(sender, properties, -1);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="trackingrange")
    @Command(value="cart trackingrange <num_blocks>")
    @CommandDescription(value="Sets the view distance players inside the cart have")
    private void setProperty(CommandSender sender, CartProperties properties, @Argument(value="num_blocks") int distance) {
        properties.set(this, distance);
        this.getProperty(sender, properties);
    }

    @Command(value="cart trackingrange")
    @CommandDescription(value="Displays the view distance players inside the cart have")
    private void getProperty(CommandSender sender, CartProperties properties) {
        int distance = properties.get(this);
        if (distance >= 0) {
            sender.sendMessage(ChatColor.YELLOW + "Cart is visible from: " + ChatColor.WHITE + distance + " blocks");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Cart is visible from: " + ChatColor.RED + "Default (not set)");
        }
    }

    @PropertyParser(value="trackingrange")
    public int parseTrackingRange(PropertyParseContext<Integer> context) {
        return context.inputInteger();
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_VIEW_DISTANCE.has(sender);
    }

    @Override
    public Integer getDefault() {
        return -1;
    }

    @Override
    public Optional<Integer> readFromConfig(ConfigurationNode config) {
        return Util.getConfigOptional(config, "paperTrackingRange", Integer.TYPE);
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<Integer> value) {
        Util.setConfigOptional(config, "paperTrackingRange", value);
    }

    @Override
    public void set(CartProperties properties, Integer value) {
        ICartProperty.super.set(properties, value);
        IPropertiesHolder member = properties.getHolder();
        if (member != null && !((MinecartMember)member).isUnloaded()) {
            this.setCustomTrackingRange.invoke((Object)((CommonMinecart)member.getEntity()).getEntity(), (Object)value);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                Method method = Player.class.getMethod("setSendViewDistance", Integer.TYPE);
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public void enable(TrainCarts plugin) throws Throwable {
        this.setCustomTrackingRange.init(Entity.class.getMethod("setCustomTrackingRange", Integer.TYPE));
        this.setCustomTrackingRange.forceInitialization();
    }

    public void disable(TrainCarts plugin) {
    }
}

