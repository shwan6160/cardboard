/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.config.FileConfiguration
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.StreamUtil
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.properties;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.tc.CollisionMode;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.CartPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.TrainPropertiesMap;
import com.bergerkiller.bukkit.tc.properties.defaults.DefaultProperties;
import com.bergerkiller.bukkit.tc.properties.defaults.DefaultPropertiesLookup;
import com.bergerkiller.bukkit.tc.properties.standard.StandardProperties;
import com.bergerkiller.bukkit.tc.properties.standard.type.CollisionMobCategory;
import com.bergerkiller.bukkit.tc.properties.standard.type.TrainNameFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TrainPropertiesStore
extends LinkedHashSet<CartProperties> {
    private static boolean hasChanges = false;
    private static final long serialVersionUID = 1L;
    private static final String propertiesFile = "TrainProperties.yml";
    private static FileConfiguration config = null;
    private static DefaultPropertiesLookup defaultProperties = null;
    private static TrainPropertiesMap trainProperties = new TrainPropertiesMap();

    public static Collection<TrainProperties> getAll() {
        return trainProperties.values();
    }

    public static Collection<TrainProperties> matchAll(String expression) {
        if (expression != null && !expression.isEmpty()) {
            String[] elements = expression.split("\\*", -1);
            boolean first = expression.startsWith("*");
            boolean last = expression.endsWith("*");
            return (Collection)trainProperties.values().stream().filter(p -> p.matchName(elements, first, last)).collect(StreamUtil.toUnmodifiableList());
        }
        return Collections.emptySet();
    }

    public static void rename(TrainProperties properties, String newTrainName) {
        if (properties.getTrainName().equals(newTrainName)) {
            return;
        }
        if (TrainPropertiesStore.exists(newTrainName)) {
            throw new IllegalArgumentException("Another train with name '" + newTrainName + "' already exists");
        }
        properties.getTrainCarts().getOfflineGroups().rename(properties.getTrainName(), newTrainName);
        ConfigurationNode oldConfig = properties.getConfig();
        trainProperties.remove(properties.getTrainName());
        config.remove(properties.getTrainName());
        properties.trainname = newTrainName;
        trainProperties.add(newTrainName, properties);
        config.set(newTrainName, (Object)oldConfig);
        hasChanges = true;
    }

    public static void remove(String trainName) {
        TrainProperties prop = trainProperties.remove(trainName);
        if (prop == null) {
            return;
        }
        hasChanges = true;
        config.remove(trainName);
        if (!prop.isEmpty()) {
            for (CartProperties cProp : new ArrayList<CartProperties>(prop)) {
                prop.remove(cProp);
                if (cProp.getHolder() != null && cProp.getHolder().getEntity() != null && !((CommonMinecart)cProp.getHolder().getEntity()).isRemoved()) continue;
                CartPropertiesStore.remove(cProp.getUUID());
            }
        }
    }

    public static TrainProperties get(String trainName) {
        if (trainName == null) {
            return null;
        }
        return trainProperties.get(trainName);
    }

    public static TrainProperties getRelaxed(String trainName) {
        if (trainName == null) {
            return null;
        }
        return trainProperties.getRelaxed(trainName);
    }

    public static TrainProperties create(String trainname) {
        if (trainname == null) {
            return null;
        }
        TrainProperties prop = trainProperties.get(trainname);
        return prop != null ? prop : TrainPropertiesStore.createDefaultWithName(trainname);
    }

    public static String generateTrainName() {
        return TrainNameFormat.DEFAULT.search(TrainPropertiesStore::isUseableName);
    }

    public static String generateTrainName(String format) {
        return TrainNameFormat.parse(format).search(TrainPropertiesStore::isUseableName);
    }

    public static boolean isMatchingTrainNameFormat(String trainName, String format) {
        return TrainNameFormat.parse(format).matches(trainName);
    }

    public static String generateSplitTrainName(String trainName) {
        String splitName;
        int split_idx = trainName.indexOf(126);
        int index = -1;
        if (split_idx != -1 && (index = TrainPropertiesStore.fromAlphabeticRadix(trainName.substring(split_idx + 1))) != -1) {
            trainName = trainName.substring(0, split_idx);
        }
        trainName = trainName + "~";
        while (TrainPropertiesStore.exists(splitName = trainName + TrainPropertiesStore.toAlphabeticRadix(++index))) {
        }
        return splitName;
    }

    private static String toAlphabeticRadix(int num) {
        char[] str = Integer.toString(num, 26).toCharArray();
        for (int i = 0; i < str.length; ++i) {
            int n = i;
            str[n] = (char)(str[n] + (str[i] > '9' ? 10 : 49));
        }
        return new String(str);
    }

    private static int fromAlphabeticRadix(String radixStr) {
        if (radixStr.isEmpty()) {
            return -1;
        }
        char[] str = radixStr.toCharArray();
        for (int i = 0; i < str.length; ++i) {
            char c = str[i];
            if (c < 'a' || c > 'z') {
                return -1;
            }
            int n = i;
            str[n] = (char)(str[n] - (c > 'j' ? 10 : 49));
        }
        try {
            return Integer.parseInt(new String(str), 26);
        }
        catch (NumberFormatException ex) {
            return -1;
        }
    }

    public static TrainProperties create() {
        return TrainPropertiesStore.createDefaultWithName(TrainPropertiesStore.generateTrainName());
    }

    private static TrainProperties createDefaultWithName(String newTrainName) {
        ConfigurationNode newTrainConfig = config.getNode(newTrainName);
        TrainProperties prop = new TrainProperties(TrainCarts.plugin, newTrainName, newTrainConfig);
        trainProperties.add(newTrainName, prop);
        prop.onConfigurationChanged(true);
        prop.setDefault();
        hasChanges = true;
        return prop;
    }

    public static TrainProperties createSplitFrom(TrainProperties fromTrainProperties) {
        String name = TrainPropertiesStore.generateSplitTrainName(fromTrainProperties.getTrainName());
        ConfigurationNode newTrainConfig = config.getNode(name);
        fromTrainProperties.saveToConfig().cloneIntoExcept(newTrainConfig, Collections.singleton("carts"));
        TrainProperties prop = new TrainProperties(fromTrainProperties.getTrainCarts(), name, newTrainConfig);
        trainProperties.add(name, prop);
        prop.onConfigurationChanged(false);
        hasChanges = true;
        return prop;
    }

    public static TrainProperties createFromConfig(ConfigurationNode savedTrainConfig) {
        String name = StandardProperties.TRAIN_NAME_FORMAT.readFromConfig(savedTrainConfig).orElse(TrainNameFormat.DEFAULT).search(TrainPropertiesStore::isUseableName);
        ConfigurationNode newTrainConfig = config.getNode(name);
        savedTrainConfig.cloneIntoExcept(newTrainConfig, Collections.singleton("carts"));
        TrainProperties prop = new TrainProperties(TrainCarts.plugin, name, newTrainConfig);
        trainProperties.add(name, prop);
        prop.onConfigurationChanged(false);
        hasChanges = true;
        return prop;
    }

    public static boolean exists(String trainname) {
        return trainProperties != null && trainProperties.containsKey(trainname);
    }

    public static boolean isUseableName(String trainName) {
        return !TrainPropertiesStore.exists(trainName);
    }

    public static void clearAll() {
        trainProperties.clear();
        config.clear();
        CartPropertiesStore.clearAllCarts();
        hasChanges = true;
    }

    public static void load(TrainCarts traincarts) {
        TrainPropertiesStore.loadDefaults(traincarts);
        config = new FileConfiguration((JavaPlugin)traincarts, propertiesFile);
        config.load();
        if (TrainPropertiesStore.fixDeprecation(config)) {
            config.save();
        }
        for (ConfigurationNode node : config.getNodes()) {
            TrainProperties prop = new TrainProperties(traincarts, node.getName(), node);
            if (prop.isEmpty()) {
                config.remove(node.getName());
                traincarts.log(Level.WARNING, "Train properties with name " + prop.getTrainName() + " has no carts!");
                continue;
            }
            trainProperties.add(prop.getTrainName(), prop);
            prop.onConfigurationChanged(true);
        }
        hasChanges = false;
        config.addChangeListener(path -> {
            hasChanges = true;
        });
    }

    public static boolean fixDeprecation(FileConfiguration config) {
        boolean changed = false;
        for (ConfigurationNode node : config.getNodes()) {
            String mobType;
            if (node.contains("allowLinking")) {
                node.set("collision.train", (Object)CollisionMode.fromLinking((Boolean)node.get("allowLinking", (Object)true)));
                node.remove("allowLinking");
                changed = true;
            }
            if (node.contains("collision.mobs")) {
                for (CollisionMobCategory collisionConfigObject : CollisionMobCategory.values()) {
                    if (!collisionConfigObject.isMobCategory()) continue;
                    node.set("collision." + collisionConfigObject.getMobType(), (Object)((CollisionMode)((Object)node.get("collision.mobs", (Object)CollisionMode.DEFAULT))).toString());
                }
                node.remove("collision.mobs");
                changed = true;
            }
            if (node.contains("pushAway")) {
                for (CollisionMobCategory collisionConfigObject : CollisionMobCategory.values()) {
                    if (!collisionConfigObject.isMobCategory()) continue;
                    mobType = collisionConfigObject.getMobType();
                    node.set("collision." + mobType, (Object)CollisionMode.fromPushing((Boolean)node.get("pushAway." + mobType, (Object)false)).toString());
                }
                node.set("collision.players", (Object)CollisionMode.fromPushing((Boolean)node.get("pushAway.players", (Object)false)).toString());
                node.set("collision.misc", (Object)CollisionMode.fromPushing((Boolean)node.get("pushAway.misc", (Object)true)).toString());
                node.remove("pushAway");
                changed = true;
            }
            if (node.contains("allowMobsEnter")) {
                if (((Boolean)node.get("allowMobsEnter", (Object)false)).booleanValue()) {
                    for (CollisionMobCategory collisionConfigObject : CollisionMobCategory.values()) {
                        if (!collisionConfigObject.isMobCategory()) continue;
                        mobType = collisionConfigObject.getMobType();
                        node.set("collision." + mobType, (Object)CollisionMode.ENTER.toString());
                    }
                }
                node.remove("allowMobsEnter");
                changed = true;
            }
            if (!node.contains("mobenter") && !node.contains("mobsenter")) continue;
            if ((((Boolean)node.get("mobenter", (Object)false)).booleanValue() || ((Boolean)node.get("mobsenter", (Object)false)).booleanValue()) && ((Boolean)node.get("allowMobsEnter", (Object)false)).booleanValue()) {
                for (CollisionMobCategory collisionConfigObject : CollisionMobCategory.values()) {
                    if (!collisionConfigObject.isMobCategory()) continue;
                    mobType = collisionConfigObject.getMobType();
                    node.set("collision." + mobType, (Object)CollisionMode.ENTER.toString());
                }
            }
            node.remove("mobenter");
            node.remove("mobenters");
            changed = true;
        }
        return changed;
    }

    public static void loadDefaults(TrainCarts traincarts) {
        defaultProperties = DefaultPropertiesLookup.load(traincarts);
    }

    public static void save(boolean autosave) {
        if (autosave && !hasChanges) {
            return;
        }
        List<TrainProperties> removedTrainProperties = trainProperties.values().stream().filter(prop -> !prop.hasHolder() && !prop.getTrainCarts().getOfflineGroups().contains(prop.getTrainName())).collect(Collectors.toList());
        removedTrainProperties.forEach(prop -> TrainPropertiesStore.remove(prop.getTrainName()));
        config.save();
        hasChanges = false;
    }

    public static DefaultProperties getDefaultsByName(String name) {
        return defaultProperties.getByName(name);
    }

    public static DefaultProperties getDefaultsByPlayer(Player player) {
        return defaultProperties.getForPlayer(player);
    }

    public static void bindGroupToProperties(TrainProperties properties, MinecartGroup group) {
        properties.updateHolder(group, true);
    }

    public static void unbindGroupFromProperties(TrainProperties properties, MinecartGroup group) {
        properties.updateHolder(group, false);
    }
}

