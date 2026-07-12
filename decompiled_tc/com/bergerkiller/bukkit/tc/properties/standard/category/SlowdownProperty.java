/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.MessageBuilder
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.properties.standard.category;

import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.PropertyCheckPermission;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyParseContext;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedProperty;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedStandardTrainProperty;
import com.bergerkiller.bukkit.tc.properties.standard.type.SlowdownMode;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class SlowdownProperty
extends FieldBackedStandardTrainProperty<Set<SlowdownMode>> {
    private final Set<SlowdownMode> ALL = Collections.unmodifiableSet(EnumSet.allOf(SlowdownMode.class));
    private final Set<SlowdownMode> NONE = Collections.unmodifiableSet(EnumSet.noneOf(SlowdownMode.class));

    public void appendSlowdownInfo(MessageBuilder message, TrainProperties properties) {
        message.yellow(new Object[]{"Slow down over time: "});
        if (properties.isSlowingDownAll()) {
            message.green(new Object[]{"Yes (All)"});
        } else if (properties.isSlowingDownNone()) {
            message.red(new Object[]{"No (None)"});
        } else {
            message.setSeparator(", ");
            for (SlowdownMode mode : SlowdownMode.values()) {
                if (properties.isSlowingDown(mode)) {
                    message.green(new Object[]{mode.getKey() + "[Yes]"});
                    continue;
                }
                message.red(new Object[]{mode.getKey() + "[No]"});
            }
            message.clearSeparator();
        }
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="slowdown")
    @Command(value="train slowdown <mode> <enabled>")
    @CommandDescription(value="Sets whether trains slow down and speed up due to a particular type of slow-down mode")
    private void trainSetSlowdownMode(CommandSender sender, TrainProperties properties, @Argument(value="mode") SlowdownMode mode, @Argument(value="enabled") boolean enabled) {
        properties.setSlowingDown(mode, enabled);
        this.trainGetSlowdownMode(sender, properties, mode);
    }

    @Command(value="train slowdown <mode>")
    @CommandDescription(value="Gets whether trains slow down and speed up for a particular slow-down mode")
    private void trainGetSlowdownMode(CommandSender sender, TrainProperties properties, @Argument(value="mode") SlowdownMode mode) {
        sender.sendMessage(ChatColor.YELLOW + "Train slows down over time due to " + ChatColor.BLUE + mode.getKey() + ChatColor.YELLOW + ": " + (properties.isSlowingDown(mode) ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="slowdown")
    @Command(value="train slowdown all|true|enable|enabled")
    @CommandDescription(value="Enables all default modes of slowing down")
    private void trainSetSlowdownAll(CommandSender sender, TrainProperties properties) {
        properties.setSlowingDown(true);
        this.trainGetSlowdownModes(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="slowdown")
    @Command(value="train slowdown none|false|disable|disabled")
    @CommandDescription(value="Disables all default modes of slowing down")
    private void trainSetSlowdownNone(CommandSender sender, TrainProperties properties) {
        properties.setSlowingDown(false);
        this.trainGetSlowdownModes(sender, properties);
    }

    @Command(value="train slowdown")
    @CommandDescription(value="Gets what types of slow-down are enabled for a train")
    private void trainGetSlowdownModes(CommandSender sender, TrainProperties properties) {
        MessageBuilder message = new MessageBuilder();
        this.appendSlowdownInfo(message, properties);
        message.send(sender);
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_SLOWDOWN.has(sender);
    }

    private Set<SlowdownMode> wrapAndOptimize(Set<SlowdownMode> result) {
        if (result.isEmpty()) {
            return this.NONE;
        }
        if (result.size() == this.ALL.size()) {
            return this.ALL;
        }
        return Collections.unmodifiableSet(result);
    }

    @PropertyParser(value="slowdown")
    public Set<SlowdownMode> parseSlowdownAll(PropertyParseContext<Set<SlowdownMode>> context) {
        if (context.input().equalsIgnoreCase("all")) {
            return this.ALL;
        }
        if (context.input().equalsIgnoreCase("none")) {
            return this.NONE;
        }
        return context.inputBoolean() ? this.ALL : this.NONE;
    }

    @PropertyParser(value="slowfriction")
    public Set<SlowdownMode> parseSlowdownFriction(PropertyParseContext<Set<SlowdownMode>> context) {
        EnumSet<SlowdownMode> values = EnumSet.noneOf(SlowdownMode.class);
        values.addAll((Collection<SlowdownMode>)context.current());
        LogicUtil.addOrRemove(values, (Object)((Object)SlowdownMode.FRICTION), (boolean)context.inputBoolean());
        return this.wrapAndOptimize(values);
    }

    @PropertyParser(value="slowgravity")
    public Set<SlowdownMode> parseSlowdownGravity(PropertyParseContext<Set<SlowdownMode>> context) {
        EnumSet<SlowdownMode> values = EnumSet.noneOf(SlowdownMode.class);
        values.addAll((Collection<SlowdownMode>)context.current());
        LogicUtil.addOrRemove(values, (Object)((Object)SlowdownMode.GRAVITY), (boolean)context.inputBoolean());
        return this.wrapAndOptimize(values);
    }

    @Override
    public Set<SlowdownMode> getDefault() {
        return this.ALL;
    }

    @Override
    public Set<SlowdownMode> getData(FieldBackedProperty.TrainInternalData data) {
        return data.slowdown;
    }

    @Override
    public void setData(FieldBackedProperty.TrainInternalData data, Set<SlowdownMode> value) {
        data.slowdown = value;
    }

    @Override
    public Optional<Set<SlowdownMode>> readFromConfig(ConfigurationNode config) {
        if (!config.contains("slowDown")) {
            return Optional.empty();
        }
        if (!config.isNode("slowDown")) {
            return Optional.of((Boolean)config.get("slowDown", (Object)true) != false ? this.ALL : this.NONE);
        }
        EnumSet<SlowdownMode> modes = EnumSet.noneOf(SlowdownMode.class);
        ConfigurationNode slowDownNode = config.getNode("slowDown");
        for (SlowdownMode mode : SlowdownMode.values()) {
            if (!slowDownNode.contains(mode.getKey()) || !((Boolean)slowDownNode.get(mode.getKey(), (Object)true)).booleanValue()) continue;
            modes.add(mode);
        }
        return Optional.of(this.wrapAndOptimize(modes));
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<Set<SlowdownMode>> value) {
        if (value.isPresent()) {
            Set<SlowdownMode> modes = value.get();
            if (modes.isEmpty()) {
                config.set("slowDown", (Object)false);
            } else if (modes.equals(this.ALL)) {
                config.set("slowDown", (Object)true);
            } else {
                ConfigurationNode slowDownNode = config.getNode("slowDown");
                slowDownNode.clear();
                for (SlowdownMode mode : SlowdownMode.values()) {
                    slowDownNode.set(mode.getKey(), (Object)modes.contains((Object)mode));
                }
            }
        } else {
            config.remove("slowDown");
        }
    }
}

