/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 */
package com.bergerkiller.bukkit.tc.controller.persistence;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.persistence.PersistentCartAttribute;

public class DisplayedBlockPersistentCartAttribute
implements PersistentCartAttribute<CommonMinecart<?>> {
    @Override
    public void load(CommonMinecart<?> entity, ConfigurationNode data) {
        if (data.isNode("displayedBlock")) {
            BlockData type;
            ConfigurationNode displayedBlock = data.getNode("displayedBlock");
            if (displayedBlock.contains("offset")) {
                entity.setBlockOffset(((Integer)displayedBlock.get("offset", (Object)Util.getDefaultDisplayedBlockOffset())).intValue());
            }
            if (displayedBlock.contains("type") && (type = BlockData.fromCombinedId((int)((Integer)displayedBlock.get("type", (Object)0)))) != null && type != BlockData.AIR) {
                entity.setBlock(type);
            }
        }
    }

    @Override
    public void save(CommonMinecart<?> entity, ConfigurationNode data) {
        boolean hasBlock;
        int offset = entity.getBlockOffset();
        BlockData block = entity.getBlock();
        boolean hasOffset = offset != Util.getDefaultDisplayedBlockOffset();
        boolean bl = hasBlock = block != null && block != BlockData.AIR;
        if (hasOffset || hasBlock) {
            ConfigurationNode displayedBlock = data.getNode("displayedBlock");
            displayedBlock.set("offset", (Object)(hasOffset ? Integer.valueOf(offset) : null));
            displayedBlock.set("type", hasBlock ? Integer.valueOf(block.getCombinedId()) : null);
        } else {
            data.remove("displayedBlock");
        }
    }
}

