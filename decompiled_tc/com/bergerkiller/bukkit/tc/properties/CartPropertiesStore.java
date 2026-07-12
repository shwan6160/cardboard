/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.properties;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.entity.Player;

public class CartPropertiesStore {
    private static HashMap<UUID, CartProperties> properties = new HashMap();

    public static CartProperties getByUUID(UUID uuid) {
        return properties.get(uuid);
    }

    public static CartProperties getEditing(Player player) {
        return TrainCarts.plugin.getPlayer(player).getEditedCart();
    }

    public static CartProperties getEditing(UUID playerUUID) {
        return TrainCarts.plugin.getPlayer(playerUUID).getEditedCart();
    }

    public static void setEditing(Player player, CartProperties properties) {
        TrainCarts.plugin.getPlayer(player).editCart(properties);
    }

    public static void setEditing(UUID playerUUID, CartProperties properties) {
        TrainCarts.plugin.getPlayer(playerUUID).editCart(properties);
    }

    public static void remove(UUID uuid) {
        CartProperties prop = properties.remove(uuid);
        if (prop != null) {
            prop.removed = true;
            TrainProperties tprop = prop.getTrainProperties();
            if (tprop != null) {
                tprop.remove(prop);
            }
        }
    }

    protected static void clearAllCarts() {
        properties.values().forEach(p -> {
            p.removed = true;
        });
        properties.clear();
    }

    protected static CartProperties createNew(TrainProperties train, ConfigurationNode config, UUID uuid) {
        CartProperties prop = properties.get(uuid);
        if (prop != null) {
            prop.reassign(train, config);
        } else {
            prop = new CartProperties(TrainCarts.plugin, train, config, uuid);
            properties.put(uuid, prop);
        }
        return prop;
    }

    public static CartProperties createForMember(MinecartMember<?> member) {
        UUID uuid = ((CommonMinecart)member.getEntity()).getUniqueId();
        CartProperties prop = properties.get(uuid);
        if (prop != null) {
            prop.setHolder(member);
            return prop;
        }
        TrainProperties trainProperties = member.isUnloaded() ? null : member.getGroup().getProperties();
        prop = new CartProperties(member.getTrainCarts(), trainProperties, new ConfigurationNode(), uuid);
        properties.put(uuid, prop);
        prop.setHolder(member);
        prop.onConfigurationChanged();
        return prop;
    }
}

