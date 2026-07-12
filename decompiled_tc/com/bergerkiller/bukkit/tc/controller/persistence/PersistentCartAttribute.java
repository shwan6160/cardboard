/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.entity.CommonEntity
 */
package com.bergerkiller.bukkit.tc.controller.persistence;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.entity.CommonEntity;

public interface PersistentCartAttribute<E extends CommonEntity<?>> {
    public void save(E var1, ConfigurationNode var2);

    public void load(E var1, ConfigurationNode var2);
}

