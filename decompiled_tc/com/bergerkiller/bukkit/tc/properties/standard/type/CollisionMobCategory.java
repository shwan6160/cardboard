/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.EntityGroupingUtil
 *  com.bergerkiller.bukkit.common.utils.EntityGroupingUtil$EntityCategory
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.bukkit.tc.properties.standard.type;

import com.bergerkiller.bukkit.common.utils.EntityGroupingUtil;
import com.bergerkiller.bukkit.tc.CollisionMode;
import java.util.Set;
import org.bukkit.entity.Entity;

public enum CollisionMobCategory {
    PETS("pet", "pets", EntityGroupingUtil.EntityCategory.TAMED.getEntityClasses(), "Pets", null),
    JOCKEYS("jockey", "jockeys", EntityGroupingUtil.EntityCategory.JOCKEY.getEntityClasses(), "Jockeys", null),
    KILLER_BUNNIES("killer_bunny", "killer_bunnies", EntityGroupingUtil.EntityCategory.KILLER_BUNNY.getEntityClasses(), "Killer Bunnies", null),
    NPCS("npc", "npcs", EntityGroupingUtil.EntityCategory.NPC.getEntityClasses(), "NPCs", null),
    ANIMALS("animal", "animals", EntityGroupingUtil.EntityCategory.ANIMAL.getEntityClasses(), "Animals", null),
    MONSTERS("monster", "monsters", EntityGroupingUtil.EntityCategory.MONSTER.getEntityClasses(), "Monsters", null),
    PASSIVE_MOBS("passive", "passives", EntityGroupingUtil.EntityCategory.PASSIVE.getEntityClasses(), "Passive Mobs", CollisionMode.DEFAULT),
    NEUTRAL_MOBS("neutral", "neutrals", EntityGroupingUtil.EntityCategory.NEUTRAL.getEntityClasses(), "Neutral Mobs", CollisionMode.DEFAULT),
    HOSTILE_MOBS("hostile", "hostiles", EntityGroupingUtil.EntityCategory.HOSTILE.getEntityClasses(), "Hostile Mobs", CollisionMode.DEFAULT),
    TAMEABLE_MOBS("tameable", "tameables", EntityGroupingUtil.EntityCategory.TAMEABLE.getEntityClasses(), "Tameable Mobs", CollisionMode.DEFAULT),
    UTILITY_MOBS("utility", "utilities", EntityGroupingUtil.EntityCategory.UTILITY.getEntityClasses(), "Utility Mobs", CollisionMode.DEFAULT),
    BOSS_MOBS("boss", "bosses", EntityGroupingUtil.EntityCategory.BOSS.getEntityClasses(), "Boss Mobs", CollisionMode.DEFAULT);

    private final String mobType;
    private final String pluralMobType;
    private final String friendlyMobName;
    private CollisionMode defaultCollisionMode;
    private Set<Class<?>> entityClasses;

    private CollisionMobCategory(String mobType, String pluralMobType, Set<Class<?>> entityClasses, String friendlyMobName, CollisionMode defaultCollisionMode) {
        this.mobType = mobType;
        this.pluralMobType = pluralMobType;
        this.friendlyMobName = friendlyMobName;
        this.setDefaultCollisionMode(defaultCollisionMode);
        this.setEntityClasses(entityClasses);
    }

    public boolean isMobType(Entity entity) {
        return this.entityClasses != null && !this.entityClasses.isEmpty() && EntityGroupingUtil.isEntityTypeClass((Entity)entity, this.entityClasses);
    }

    public String getMobType() {
        return this.mobType;
    }

    public boolean isMobCategory() {
        return this.name().endsWith("_MOBS");
    }

    public static CollisionMobCategory findMobType(Entity entity) {
        for (CollisionMobCategory collisionConfigObject : CollisionMobCategory.values()) {
            if (!collisionConfigObject.isMobType(entity)) continue;
            return collisionConfigObject;
        }
        return null;
    }

    public static CollisionMobCategory findMobType(String entityType) {
        for (CollisionMobCategory collisionConfigObject : CollisionMobCategory.values()) {
            if (!collisionConfigObject.getMobType().equals(entityType)) continue;
            return collisionConfigObject;
        }
        return null;
    }

    public static CollisionMobCategory findMobType(String entityType, String prefix) {
        if (prefix == null) {
            return CollisionMobCategory.findMobType(entityType);
        }
        return CollisionMobCategory.findMobType(entityType.substring(prefix.length()));
    }

    public static CollisionMobCategory findMobType(String entityType, String prefix, String suffix) {
        if (prefix == null && suffix == null) {
            return CollisionMobCategory.findMobType(entityType);
        }
        if (suffix == null) {
            return CollisionMobCategory.findMobType(entityType, prefix);
        }
        return CollisionMobCategory.findMobType(entityType.substring(0, entityType.length() - suffix.length()), prefix);
    }

    public String getFriendlyMobName() {
        return this.friendlyMobName;
    }

    public String getPluralMobType() {
        return this.pluralMobType;
    }

    public CollisionMode getDefaultCollisionMode() {
        return this.defaultCollisionMode;
    }

    public void setDefaultCollisionMode(CollisionMode defaultCollisionMode) {
        this.defaultCollisionMode = defaultCollisionMode;
    }

    public Set<Class<?>> getEntityClasses() {
        return this.entityClasses;
    }

    public void setEntityClasses(Set<Class<?>> entityClasses) {
        this.entityClasses = entityClasses;
    }
}

