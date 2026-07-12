/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Flag
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.properties.standard.category;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Flag;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorCondition;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.PropertyCheckPermission;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.PropertySelectorCondition;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyParseContext;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedProperty;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedStandardTrainProperty;
import com.bergerkiller.bukkit.tc.properties.standard.type.ChunkLoadOptions;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class ChunkLoadOptionsProperty
extends FieldBackedStandardTrainProperty<ChunkLoadOptions> {
    @CommandTargetTrain
    @PropertyCheckPermission(value="keeploaded")
    @Command(value="train keepchunksloaded|keeploaded|loadchunks <mode>")
    @CommandDescription(value="Sets whether the train keeps chunks loaded, how and optionally with what radius")
    private void commandSetProperty(CommandSender sender, TrainProperties properties, @Argument(value="mode") ChunkLoadOptions.Mode mode, @Flag(value="radius") Integer radius) {
        ChunkLoadOptions options = properties.getChunkLoadOptions();
        options = options.withMode(mode);
        if (radius != null) {
            int radInt = radius;
            if (radInt > TCConfig.maxKeepChunksLoadedRadius) {
                sender.sendMessage(ChatColor.RED + "Radius " + radInt + " is too big (max: " + TCConfig.maxKeepChunksLoadedRadius + ")");
                radInt = TCConfig.maxKeepChunksLoadedRadius;
            }
            options = options.withRadius(radInt);
        }
        properties.setChunkLoadOptions(options);
        this.commandGetProperty(sender, properties);
    }

    @Command(value="train keepchunksloaded|keeploaded|loadchunks")
    @CommandDescription(value="Gets the chunk loader configuration of the train")
    private void commandGetProperty(CommandSender sender, TrainProperties properties) {
        ChunkLoadOptions options = properties.getChunkLoadOptions();
        int rad = Math.min(TCConfig.maxKeepChunksLoadedRadius, options.radius()) * 2 + 1;
        String radInfo = options.keepLoaded() ? "" + ChatColor.WHITE + " (" + rad + " x " + rad + " chunks)" : "";
        sender.sendMessage(ChatColor.YELLOW + "Train keeps nearby chunks loaded: " + Localization.boolStr(options.keepLoaded()) + radInfo);
        if (options.keepLoaded()) {
            switch (options.mode()) {
                case FULL: {
                    sender.sendMessage(ChatColor.YELLOW + "The loaded chunks will simulate entities and redstone");
                    break;
                }
                case REDSTONE: {
                    sender.sendMessage(ChatColor.YELLOW + "The loaded chunks will only simulate redstone, not entities");
                    break;
                }
                case MINIMAL: {
                    sender.sendMessage(ChatColor.YELLOW + "The loaded chunks will " + ChatColor.RED + "not" + ChatColor.YELLOW + " simulate redstone and entities");
                }
            }
        }
    }

    @PropertyParser(value="keepchunksloaded|keeploaded|keepcloaded|loadchunks")
    public ChunkLoadOptions parseChunkLoadOptions(PropertyParseContext<ChunkLoadOptions> context) {
        ChunkLoadOptions options = context.current();
        for (String word : context.input().split(" ")) {
            Optional<ChunkLoadOptions.Mode> newMode = ChunkLoadOptions.Mode.fromName(word);
            if (newMode.isPresent()) {
                options = options.withMode(newMode.get());
                continue;
            }
            Integer radius = ParseUtil.parseInt((String)word, null);
            if (radius == null) continue;
            options = options.withRadius(radius);
        }
        return options;
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_KEEPCHUNKSLOADED.has(sender);
    }

    @PropertySelectorCondition(value="keepchunksloaded")
    public boolean selectorMatchesKeepChunksLoaded(TrainProperties properties, SelectorCondition condition) {
        return condition.matchesBoolean(properties.isKeepingChunksLoaded());
    }

    @Override
    public ChunkLoadOptions getDefault() {
        return ChunkLoadOptions.DEFAULT;
    }

    @Override
    public ChunkLoadOptions getData(FieldBackedProperty.TrainInternalData data) {
        return data.chunkLoadOptions;
    }

    @Override
    public void setData(FieldBackedProperty.TrainInternalData data, ChunkLoadOptions value) {
        data.chunkLoadOptions = value;
    }

    @Override
    public Optional<ChunkLoadOptions> readFromConfig(ConfigurationNode config) {
        if (config.contains("keepChunksLoaded")) {
            if (config.isNode("keepChunksLoaded")) {
                ConfigurationNode node = config.getNode("keepChunksLoaded");
                ChunkLoadOptions.Mode mode = ChunkLoadOptions.Mode.fromName((String)node.get("mode", (Object)"disabled")).orElse(ChunkLoadOptions.Mode.DISABLED);
                int radius = Math.min(TCConfig.maxKeepChunksLoadedRadius, (Integer)node.get("radius", (Object)2));
                return Optional.of(ChunkLoadOptions.of(mode, radius));
            }
            return Optional.of((Boolean)config.get("keepChunksLoaded", (Object)false) != false ? ChunkLoadOptions.LEGACY_TRUE : ChunkLoadOptions.LEGACY_FALSE);
        }
        return Optional.empty();
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<ChunkLoadOptions> value) {
        if (value.isPresent()) {
            ChunkLoadOptions options = value.get();
            if (options.equals(ChunkLoadOptions.LEGACY_TRUE)) {
                config.set("keepChunksLoaded", (Object)true);
            } else if (options.equals(ChunkLoadOptions.LEGACY_FALSE)) {
                config.set("keepChunksLoaded", (Object)false);
            } else {
                ConfigurationNode node = config.getNode("keepChunksLoaded");
                node.set("mode", (Object)options.mode().getNames().get(0));
                node.set("radius", (Object)options.radius());
            }
        } else {
            config.remove("keepChunksLoaded");
        }
    }

    @Override
    public void onConfigurationChanged(TrainProperties properties) {
        super.onConfigurationChanged(properties);
        this.updateState(properties, (ChunkLoadOptions)this.get(properties));
    }

    @Override
    public void set(TrainProperties properties, ChunkLoadOptions value) {
        super.set(properties, value);
        this.updateState(properties, value);
    }

    private void updateState(TrainProperties properties, ChunkLoadOptions options) {
        if (options.keepLoaded()) {
            properties.restore().thenAccept(result -> {
                MinecartGroup group;
                if (result.booleanValue() && (group = properties.getHolder()) != null) {
                    group.keepChunksLoaded(group.getProperties().getChunkLoadOptions().mode());
                }
            });
        } else {
            MinecartGroup group = properties.getHolder();
            if (group != null) {
                group.keepChunksLoaded(options.mode());
            }
        }
    }
}

