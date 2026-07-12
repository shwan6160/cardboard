/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Minecart
 */
package com.bergerkiller.bukkit.tc.controller.persistence;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.tc.controller.persistence.PersistentCartAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;

public class EntityTagsPersistentCartAttribute
implements PersistentCartAttribute<CommonMinecart<?>> {
    @Override
    public void save(CommonMinecart<?> entity, ConfigurationNode data) {
        Set tags = ((Minecart)entity.getEntity()).getScoreboardTags();
        if (!tags.isEmpty()) {
            data.set("entityTags", new ArrayList(tags));
        } else {
            data.remove("entityTags");
        }
    }

    @Override
    public void load(CommonMinecart<?> commonEntity, ConfigurationNode data) {
        if (data.contains("entityTags")) {
            Entity entity = commonEntity.getEntity();
            Set existingTags = entity.getScoreboardTags();
            List tags = data.getList("entityTags", String.class);
            for (String existingTag : existingTags) {
                if (tags.contains(existingTag)) continue;
                entity.removeScoreboardTag(existingTag);
            }
            for (String tag : tags) {
                if (existingTags.contains(tag)) continue;
                entity.addScoreboardTag(tag);
            }
        }
    }
}

