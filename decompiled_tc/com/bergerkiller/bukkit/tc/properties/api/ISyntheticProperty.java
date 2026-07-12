/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 */
package com.bergerkiller.bukkit.tc.properties.api;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.properties.api.IProperty;
import java.util.Optional;

public interface ISyntheticProperty<T>
extends IProperty<T> {
    @Override
    default public Optional<T> readFromConfig(ConfigurationNode config) {
        return Optional.empty();
    }

    @Override
    default public void writeToConfig(ConfigurationNode config, Optional<T> value) {
    }

    @Override
    default public boolean isAppliedAsDefault() {
        return false;
    }
}

