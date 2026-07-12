/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.properties.api;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Optional;
import org.bukkit.command.CommandSender;

public interface IProperty<T> {
    public T getDefault();

    public Optional<T> readFromConfig(ConfigurationNode var1);

    public void writeToConfig(ConfigurationNode var1, Optional<T> var2);

    default public void onConfigurationChanged(CartProperties properties) {
    }

    default public void onConfigurationChanged(TrainProperties properties) {
    }

    public T get(CartProperties var1);

    public void set(CartProperties var1, T var2);

    public T get(TrainProperties var1);

    public void set(TrainProperties var1, T var2);

    default public boolean isAppliedAsDefault() {
        return true;
    }

    default public String getPermissionName() {
        for (Method m : this.getClass().getDeclaredMethods()) {
            PropertyParser parser = m.getAnnotation(PropertyParser.class);
            if (parser == null) continue;
            String name = parser.value();
            int sepIdx = name.indexOf(124);
            if (sepIdx > 0) {
                name = name.substring(0, sepIdx);
            }
            return name;
        }
        String name = this.getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
        if (name.endsWith("property")) {
            name = name.substring(0, name.length() - 8);
        }
        return name;
    }

    default public String getListedName() {
        return this.getPermissionName();
    }

    default public boolean isListed() {
        return true;
    }

    default public boolean hasPermission(CommandSender sender, String name) {
        return true;
    }
}

