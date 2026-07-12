/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartFurnace
 */
package com.bergerkiller.bukkit.tc.controller.persistence;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartFurnace;
import com.bergerkiller.bukkit.tc.controller.persistence.PersistentCartAttribute;

public class FuelPersistentCartAttribute
implements PersistentCartAttribute<CommonMinecartFurnace> {
    @Override
    public void save(CommonMinecartFurnace entity, ConfigurationNode data) {
        if (entity.getFuelTicks() > 0) {
            data.set("fuel", (Object)entity.getFuelTicks());
        } else {
            data.remove("fuel");
        }
    }

    @Override
    public void load(CommonMinecartFurnace entity, ConfigurationNode data) {
        if (data.contains("fuel")) {
            entity.setFuelTicks(((Integer)data.get("fuel", (Object)0)).intValue());
        } else {
            entity.setFuelTicks(0);
        }
        entity.setSmoking(entity.getFuelTicks() > 0);
    }
}

