/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecartCommandBlock
 */
package com.bergerkiller.bukkit.tc.controller.persistence;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartCommandBlock;
import com.bergerkiller.bukkit.tc.controller.persistence.PersistentCartAttribute;

public class CommandPersistentCartAttribute
implements PersistentCartAttribute<CommonMinecartCommandBlock> {
    @Override
    public void save(CommonMinecartCommandBlock entity, ConfigurationNode data) {
        data.set("command", entity.metaCommand.get());
    }

    @Override
    public void load(CommonMinecartCommandBlock entity, ConfigurationNode data) {
        if (data.contains("command")) {
            entity.metaCommand.set((Object)((String)data.get("command", (Object)"")));
        }
    }
}

