/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.localization;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import java.util.Locale;

public interface ILocalizationDefault {
    public String getName();

    public String getDefault();

    default public void initDefaults(ConfigurationNode config) {
        String path = this.getName().toLowerCase(Locale.ENGLISH);
        if (!config.contains(path)) {
            this.writeDefaults(config, path);
        }
    }

    default public void writeDefaults(ConfigurationNode config, String path) {
        config.set(path, (Object)this.getDefault());
    }
}

