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
import com.bergerkiller.bukkit.tc.properties.api.ICartProperty;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyParseContext;
import com.bergerkiller.bukkit.tc.properties.standard.StandardProperties;
import java.util.Optional;
import org.bukkit.command.CommandSender;

public final class PlayerEnterAndExitProperty
implements ICartProperty<Boolean> {
    @PropertyParser(value="playerenterexit|playerexitenter")
    public boolean parsePlayerExit(PropertyParseContext<Boolean> context) {
        return context.inputBoolean();
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_PLAYEREXIT.has(sender) && Permission.PROPERTY_PLAYERENTER.has(sender);
    }

    @Override
    public boolean isAppliedAsDefault() {
        return false;
    }

    @Override
    public boolean isListed() {
        return false;
    }

    @Override
    public Boolean getDefault() {
        return Boolean.TRUE;
    }

    @Override
    public Optional<Boolean> readFromConfig(ConfigurationNode config) {
        Optional<Boolean> enter = StandardProperties.ALLOW_PLAYER_ENTER.readFromConfig(config);
        Optional<Boolean> exit = StandardProperties.ALLOW_PLAYER_EXIT.readFromConfig(config);
        if (enter.isPresent() || exit.isPresent()) {
            return Optional.of(enter.orElse(Boolean.TRUE) != false && exit.orElse(Boolean.TRUE) != false);
        }
        return Optional.empty();
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<Boolean> value) {
        StandardProperties.ALLOW_PLAYER_ENTER.writeToConfig(config, value);
        StandardProperties.ALLOW_PLAYER_EXIT.writeToConfig(config, value);
    }
}

