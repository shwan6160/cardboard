/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.config.FileConfiguration
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.properties.defaults;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.properties.TrainPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.api.IProperty;
import com.bergerkiller.bukkit.tc.properties.api.IPropertyRegistry;
import com.bergerkiller.bukkit.tc.properties.defaults.DefaultProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DefaultPropertiesLookup {
    private static final String defaultPropertiesFile = "DefaultTrainProperties.yml";
    private final FileConfiguration config;
    private final Map<String, CachedDefaultProperties> defaultPropertiesByName;
    private final List<CachedDefaultProperties> namedDefaults;
    private final CachedDefaultProperties defaultProperties;
    private Collection<IProperty<Object>> allPropertiesAtTimeOfCaching;

    private DefaultPropertiesLookup(FileConfiguration config) {
        this.config = config;
        this.defaultPropertiesByName = new HashMap<String, CachedDefaultProperties>(config.getNodes().size());
        for (ConfigurationNode node : config.getNodes()) {
            this.defaultPropertiesByName.put(node.getName(), new CachedDefaultProperties(node));
        }
        this.defaultProperties = this.defaultPropertiesByName.get("default");
        if (this.defaultProperties == null) {
            throw new IllegalStateException("No default configuration is included");
        }
        this.namedDefaults = new ArrayList<CachedDefaultProperties>(this.defaultPropertiesByName.size());
        for (CachedDefaultProperties props : this.defaultPropertiesByName.values()) {
            if (LogicUtil.contains((Object)props.name(), (Object[])new String[]{"default", "spawner"})) continue;
            this.namedDefaults.add(props);
        }
        this.namedDefaults.sort(Comparator.comparing(CachedDefaultProperties::name));
        this.allPropertiesAtTimeOfCaching = IPropertyRegistry.instance().all();
    }

    private void invalidateIfPropertiesChanged() {
        Collection<IProperty<Object>> all = IPropertyRegistry.instance().all();
        if (this.allPropertiesAtTimeOfCaching != all) {
            this.allPropertiesAtTimeOfCaching = all;
            this.defaultPropertiesByName.values().forEach(CachedDefaultProperties::invalidate);
        }
    }

    public DefaultProperties getByName(String name) {
        this.invalidateIfPropertiesChanged();
        CachedDefaultProperties props = this.defaultPropertiesByName.get(name);
        return props == null ? null : props.get();
    }

    public DefaultProperties getForPlayer(Player player) {
        this.invalidateIfPropertiesChanged();
        for (CachedDefaultProperties props : this.namedDefaults) {
            if (!props.hasPermission(player)) continue;
            return props.get();
        }
        return this.defaultProperties.get();
    }

    public static DefaultPropertiesLookup load(TrainCarts traincarts) {
        ConfigurationNode node;
        FileConfiguration defconfig = new FileConfiguration((JavaPlugin)traincarts, defaultPropertiesFile);
        defconfig.load();
        boolean changed = false;
        if (!defconfig.contains("default")) {
            node = defconfig.getNode("default");
            for (IProperty<Object> iProperty : IPropertyRegistry.instance().all()) {
                Object value;
                if (!iProperty.isAppliedAsDefault() || (value = iProperty.getDefault()) == null) continue;
                iProperty.writeToConfig(node, Optional.of(value));
            }
            node.set("blockTypes", (Object)"");
            node.set("blockOffset", (Object)"unset");
            changed = true;
        }
        if (!defconfig.contains("admin")) {
            node = defconfig.getNode("admin");
            for (Map.Entry entry : defconfig.getNode("default").getValues().entrySet()) {
                node.set((String)entry.getKey(), entry.getValue());
            }
            changed = true;
        }
        if (!defconfig.contains("spawner")) {
            node = defconfig.getNode("spawner");
            for (Map.Entry entry : defconfig.getNode("default").getValues().entrySet()) {
                node.set((String)entry.getKey(), entry.getValue());
            }
            changed = true;
        }
        if (TrainPropertiesStore.fixDeprecation(defconfig)) {
            changed = true;
        }
        if (changed) {
            defconfig.save();
        }
        return new DefaultPropertiesLookup(defconfig);
    }

    private static class CachedDefaultProperties {
        private final ConfigurationNode config;
        private final String permNode;
        private DefaultProperties cachedProperties;

        public CachedDefaultProperties(ConfigurationNode config) {
            this.config = config;
            this.permNode = "train.properties." + config.getName();
            this.cachedProperties = null;
        }

        public String name() {
            return this.config.getName();
        }

        public boolean hasPermission(Player player) {
            return CommonUtil.hasPermission((CommandSender)player, (String)this.permNode);
        }

        public void invalidate() {
            this.cachedProperties = null;
        }

        public DefaultProperties get() {
            DefaultProperties cached = this.cachedProperties;
            if (cached == null) {
                this.cachedProperties = cached = DefaultProperties.of(this.config);
            }
            return cached;
        }
    }
}

