/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartInventory
 */
package com.bergerkiller.bukkit.tc.controller.persistence;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartInventory;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.persistence.PersistentCartAttribute;

public class MinecartInventoryPersistentCartAttribute
implements PersistentCartAttribute<CommonMinecartInventory<?>> {
    @Override
    public void save(CommonMinecartInventory<?> entity, ConfigurationNode data) {
        Util.saveInventoryToConfig(entity.getInventory(), data);
    }

    @Override
    public void load(CommonMinecartInventory<?> entity, ConfigurationNode data) {
        Util.loadInventoryFromConfig(entity.getInventory(), data);
    }
}

