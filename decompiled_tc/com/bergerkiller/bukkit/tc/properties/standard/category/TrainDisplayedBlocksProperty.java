/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.properties.standard.category;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.ITrainProperty;
import com.bergerkiller.bukkit.tc.properties.standard.type.TrainDisplayedBlocks;
import com.bergerkiller.bukkit.tc.signactions.SignActionBlockChanger;
import java.util.Optional;
import org.bukkit.command.CommandSender;

public class TrainDisplayedBlocksProperty
implements ITrainProperty<TrainDisplayedBlocks> {
    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.BUILD_BLOCKCHANGER.has(sender);
    }

    @Override
    public TrainDisplayedBlocks getDefault() {
        return TrainDisplayedBlocks.DEFAULT;
    }

    @Override
    public void set(TrainProperties properties, TrainDisplayedBlocks value) {
        MinecartGroup group;
        ITrainProperty.super.set(properties, value);
        if (value != null && (group = properties.getHolder()) != null) {
            SignActionBlockChanger.setBlocks(group, value);
        }
    }

    @Override
    public Optional<TrainDisplayedBlocks> readFromConfig(ConfigurationNode config) {
        if (config.contains("blockTypes") || config.contains("blockOffset")) {
            String blockTypes = (String)config.getOrDefault("blockTypes", (Object)"");
            int blockOffset = (Integer)config.getOrDefault("blockOffset", (Object)Integer.MAX_VALUE);
            return Optional.of(TrainDisplayedBlocks.of(blockTypes, blockOffset));
        }
        return Optional.empty();
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<TrainDisplayedBlocks> value) {
        if (value.isPresent()) {
            TrainDisplayedBlocks displayedBlocks = value.get();
            config.set("blockTypes", (Object)displayedBlocks.getBlockTypesPattern());
            config.set("blockOffset", (Object)displayedBlocks.getOffset());
        } else {
            config.remove("blockTypes");
            config.remove("blockOffset");
        }
    }
}

