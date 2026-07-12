/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Animals
 *  org.bukkit.entity.Bat
 *  org.bukkit.entity.Blaze
 *  org.bukkit.entity.CaveSpider
 *  org.bukkit.entity.Chicken
 *  org.bukkit.entity.Cow
 *  org.bukkit.entity.Creeper
 *  org.bukkit.entity.EnderDragon
 *  org.bukkit.entity.EnderDragonPart
 *  org.bukkit.entity.Enderman
 *  org.bukkit.entity.Endermite
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.FallingBlock
 *  org.bukkit.entity.Ghast
 *  org.bukkit.entity.Golem
 *  org.bukkit.entity.Guardian
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.MagmaCube
 *  org.bukkit.entity.Monster
 *  org.bukkit.entity.MushroomCow
 *  org.bukkit.entity.NPC
 *  org.bukkit.entity.Pig
 *  org.bukkit.entity.PigZombie
 *  org.bukkit.entity.Rabbit
 *  org.bukkit.entity.Rabbit$Type
 *  org.bukkit.entity.Sheep
 *  org.bukkit.entity.Silverfish
 *  org.bukkit.entity.Skeleton
 *  org.bukkit.entity.Slime
 *  org.bukkit.entity.Spider
 *  org.bukkit.entity.Squid
 *  org.bukkit.entity.Tameable
 *  org.bukkit.entity.Villager
 *  org.bukkit.entity.Witch
 *  org.bukkit.entity.Wither
 *  org.bukkit.entity.Zombie
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.StringMapCaseInsensitive;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Golem;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Item;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Monster;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Zombie;

public class EntityGroupingUtil {
    private static final StringMapCaseInsensitive<Set<EntityCategory>> entityCategories = new StringMapCaseInsensitive();
    private static final EnumMap<EntityType, String> typeNames = new EnumMap(EntityType.class);

    @Deprecated
    public static EntityCategory getCategory(String name) {
        Set<EntityCategory> x = EntityGroupingUtil.getCategories(name);
        for (EntityCategory entityCategory : EntityCategory.values()) {
            if (!entityCategory.isLegacyMob() || !x.contains((Object)entityCategory)) continue;
            return entityCategory;
        }
        return EntityCategory.OTHER;
    }

    @Deprecated
    public static EntityCategory getCategory(EntityType type) {
        return EntityGroupingUtil.getCategory(type.getEntityClass());
    }

    @Deprecated
    public static EntityCategory getCategory(Class<? extends Entity> entityClass) {
        Set<EntityCategory> x = EntityGroupingUtil.getCategories(entityClass);
        for (EntityCategory entityCategory : EntityCategory.values()) {
            if (!entityCategory.isLegacyMob() || !x.contains((Object)entityCategory)) continue;
            return entityCategory;
        }
        return EntityCategory.OTHER;
    }

    public static boolean isMob(String name) {
        return !EntityGroupingUtil.isEntityType(name, EntityCategory.OTHER);
    }

    @Deprecated
    public static boolean isNPC(String name) {
        return EntityGroupingUtil.isEntityType(name, EntityCategory.NPC);
    }

    @Deprecated
    public static boolean isAnimal(String name) {
        return EntityGroupingUtil.isEntityType(name, EntityCategory.ANIMAL);
    }

    @Deprecated
    public static boolean isMonster(String name) {
        return EntityGroupingUtil.isEntityType(name, EntityCategory.MONSTER);
    }

    public static boolean isMob(Entity entity) {
        return entity != null && EntityGroupingUtil.isMob(entity.getClass());
    }

    public static boolean isMob(EntityType entityType) {
        return EntityGroupingUtil.isMob(entityType.getEntityClass());
    }

    public static boolean isMob(Class<? extends Entity> entityClass) {
        Set<EntityCategory> resultSet = EntityGroupingUtil.getCategories(entityClass);
        return !resultSet.contains((Object)EntityCategory.OTHER);
    }

    @Deprecated
    public static boolean isNPC(Entity entity) {
        return EntityGroupingUtil.isEntityType(entity, EntityCategory.NPC);
    }

    @Deprecated
    public static boolean isNPC(EntityType entityType) {
        return EntityGroupingUtil.isEntityType(entityType, EntityCategory.NPC);
    }

    @Deprecated
    public static boolean isNPC(Class<? extends Entity> entityClass) {
        return EntityGroupingUtil.isEntityTypeClass(entityClass, EntityCategory.NPC);
    }

    @Deprecated
    public static boolean isAnimal(Entity entity) {
        return EntityGroupingUtil.isEntityType(entity, EntityCategory.ANIMAL);
    }

    @Deprecated
    public static boolean isAnimal(EntityType entityType) {
        return EntityGroupingUtil.isEntityType(entityType, EntityCategory.ANIMAL);
    }

    @Deprecated
    public static boolean isAnimal(Class<? extends Entity> entityClass) {
        return EntityGroupingUtil.isEntityTypeClass(entityClass, EntityCategory.ANIMAL);
    }

    @Deprecated
    public static boolean isMonster(Entity entity) {
        return EntityGroupingUtil.isEntityType(entity, EntityCategory.MONSTER);
    }

    @Deprecated
    public static boolean isMonster(EntityType entityType) {
        return EntityGroupingUtil.isEntityType(entityType, EntityCategory.MONSTER);
    }

    @Deprecated
    public static boolean isMonster(Class<? extends Entity> entityClass) {
        return EntityGroupingUtil.isEntityTypeClass(entityClass, EntityCategory.MONSTER);
    }

    public static boolean isKillerBunny(Entity entity) {
        if (EntityGroupingUtil.isEntityTypeClass(entity, Rabbit.class)) {
            return ((Rabbit)entity).getRabbitType() == Rabbit.Type.THE_KILLER_BUNNY;
        }
        return false;
    }

    public static boolean isTamed(Entity entity) {
        if (EntityGroupingUtil.isEntityType(entity, EntityCategory.TAMEABLE)) {
            return ((Tameable)entity).isTamed();
        }
        return false;
    }

    public static boolean isJockey(Entity entity) {
        if (EntityGroupingUtil.isEntityTypeClass(entity, Spider.class)) {
            return EntityGroupingUtil.hasMobOnTopOrBottom(entity, Zombie.class);
        }
        if (EntityGroupingUtil.isEntityTypeClass(entity, Chicken.class)) {
            return EntityGroupingUtil.hasMobOnTopOrBottom(entity, Skeleton.class);
        }
        if (EntityGroupingUtil.isEntityTypeClass(entity, Zombie.class)) {
            return EntityGroupingUtil.hasMobOnTopOrBottom(entity, Chicken.class);
        }
        if (EntityGroupingUtil.isEntityTypeClass(entity, Skeleton.class)) {
            return EntityGroupingUtil.hasMobOnTopOrBottom(entity, Spider.class);
        }
        return false;
    }

    private static boolean hasMobOnTopOrBottom(Entity e, Class<?> searchMobType) {
        List mobs = e.getNearbyEntities(0.5, 1.0, 0.5);
        for (Entity mob : mobs) {
            if (!EntityGroupingUtil.isEntityTypeClass(mob, searchMobType)) continue;
            return true;
        }
        return false;
    }

    public static Set<EntityCategory> getCategories(String name) {
        HashSet<EntityCategory> x = new HashSet<EntityCategory>();
        x.add(EntityCategory.OTHER);
        return LogicUtil.fixNull(entityCategories.get(name), x);
    }

    public static Set<EntityCategory> getCategories(EntityType type) {
        return EntityGroupingUtil.getCategories(type.getEntityClass());
    }

    public static Set<EntityCategory> getCategories(Entity entity) {
        HashSet<EntityCategory> resultSet = new HashSet<EntityCategory>();
        if (EntityGroupingUtil.isKillerBunny(entity)) {
            resultSet.add(EntityCategory.KILLER_BUNNY);
            resultSet.add(EntityCategory.ANIMAL);
            resultSet.add(EntityCategory.HOSTILE);
        }
        if (EntityGroupingUtil.isTamed(entity)) {
            resultSet.add(EntityCategory.TAMED);
        }
        if (EntityGroupingUtil.isJockey(entity)) {
            resultSet.add(EntityCategory.JOCKEY);
            resultSet.add(EntityCategory.MONSTER);
            resultSet.add(EntityCategory.ANIMAL);
            resultSet.add(EntityCategory.HOSTILE);
        }
        EntityGroupingUtil.addMatchingCategories(resultSet, entity);
        if (resultSet.size() == 0) {
            resultSet.add(EntityCategory.OTHER);
        }
        return resultSet;
    }

    public static Set<EntityCategory> getCategories(Class<? extends Entity> entityClass) {
        HashSet<EntityCategory> resultSet = new HashSet<EntityCategory>();
        EntityGroupingUtil.addMatchingCategories(resultSet, entityClass);
        if (resultSet.size() == 0) {
            resultSet.add(EntityCategory.OTHER);
        }
        return resultSet;
    }

    private static void addMatchingCategories(Set<EntityCategory> resultSet, Entity e) {
        EntityGroupingUtil.addMatchingCategories(resultSet, e.getClass());
    }

    private static void addMatchingCategories(Set<EntityCategory> resultSet, Class<?> class1) {
        if (class1 == null) {
            return;
        }
        block0: for (EntityCategory entityCategory : EntityCategory.values()) {
            Set<Class<?>> entityClasses = entityCategory.getEntityClasses();
            for (Class<?> entityClass : entityClasses) {
                if (!entityClass.isAssignableFrom(class1)) continue;
                resultSet.add(entityCategory);
                continue block0;
            }
        }
    }

    public static boolean isEntityType(String name, EntityCategory entityCategory) {
        return EntityGroupingUtil.getCategories(name).contains((Object)entityCategory);
    }

    public static boolean isEntityTypeClass(Entity entity, Class<?> entityClass) {
        return entity != null && EntityGroupingUtil.isEntityTypeClass(entity.getClass(), entityClass);
    }

    public static boolean isEntityTypeClass(Entity entity, Set<Class<?>> entityClassSet) {
        if (entity != null) {
            for (Class<?> myClass : entityClassSet) {
                if (!EntityGroupingUtil.isEntityTypeClass(entity.getClass(), myClass)) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean isEntityType(Entity entity, EntityCategory entityCategory) {
        return entity != null && EntityGroupingUtil.getCategories(entity.getClass()).contains((Object)entityCategory);
    }

    public static boolean isEntityType(Entity entity, Set<EntityCategory> entityCategorySet) {
        if (entity != null) {
            Set<EntityCategory> thisEntityCategories = EntityGroupingUtil.getCategories(entity.getClass());
            if (thisEntityCategories.size() == 1 && thisEntityCategories.contains((Object)EntityCategory.OTHER)) {
                return entityCategorySet.size() == 1 && entityCategorySet.contains((Object)EntityCategory.OTHER);
            }
            for (EntityCategory x : entityCategorySet) {
                if (!thisEntityCategories.contains((Object)x)) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean isEntityType(EntityType entityType, EntityCategory entityCategory) {
        return EntityGroupingUtil.getCategories(entityType.getEntityClass()).contains((Object)entityCategory);
    }

    public static boolean isEntityTypeClass(Class<?> entityClass, Class<?> testEntityClass) {
        return testEntityClass.isAssignableFrom(entityClass);
    }

    public static boolean isEntityTypeClass(Class<?> entityClass, EntityCategory entityCategory) {
        for (Class<?> myClass : entityCategory.getEntityClasses()) {
            if (!myClass.isAssignableFrom(entityClass)) continue;
            return true;
        }
        return false;
    }

    public static String getName(Entity entity) {
        if (entity == null) {
            return "";
        }
        if (entity instanceof Item) {
            Material mat = ((Item)entity).getItemStack().getType();
            if (mat == null || mat == Material.AIR) {
                return "item";
            }
            return "item" + mat.toString().toLowerCase(Locale.ENGLISH);
        }
        if (entity instanceof FallingBlock) {
            Material mat = ((FallingBlock)entity).getMaterial();
            if (mat == null || mat == Material.AIR) {
                return "fallingblock";
            }
            return "falling" + mat.toString().toLowerCase(Locale.ENGLISH);
        }
        return EntityGroupingUtil.getName(entity.getType());
    }

    public static String getName(Class<? extends Entity> entityClass) {
        if (entityClass == null) {
            return "";
        }
        for (EntityType type : EntityType.values()) {
            Class typeEntityClass = type.getEntityClass();
            if (typeEntityClass == null || !typeEntityClass.isAssignableFrom(entityClass)) continue;
            return EntityGroupingUtil.getName(type);
        }
        return entityClass.getSimpleName().toLowerCase(Locale.ENGLISH);
    }

    public static String getName(EntityType type) {
        return typeNames.get(type);
    }

    static {
        for (EntityType type : EntityType.values()) {
            Set<Object> x;
            String name = type.getName();
            if (name == null) {
                Class clazz = type.getEntityClass();
                name = clazz == null ? "unknown" : clazz.getSimpleName();
            }
            if (entityCategories.containsKey(name = name.toLowerCase(Locale.ENGLISH))) {
                x = entityCategories.get(name);
                x.addAll(EntityGroupingUtil.getCategories(type));
                entityCategories.put(name, x);
            } else {
                x = new HashSet();
                x.addAll(EntityGroupingUtil.getCategories(type));
                entityCategories.put(name, x);
            }
            typeNames.put(type, name);
        }
    }

    public static enum EntityCategory {
        KILLER_BUNNY(true, false, "isKillerBunny"),
        TAMED(true, false, "isTamed"),
        JOCKEY(true, false, "isJockey"),
        ANIMAL(true, true, Animals.class, Squid.class, Bat.class, CommonUtil.getClass("org.bukkit.entity.Fish"), CommonUtil.getClass("org.bukkit.entity.Dolphin")),
        MONSTER(true, true, Monster.class, Slime.class, Ghast.class, Golem.class, CommonUtil.getClass("org.bukkit.entity.Phantom")),
        NPC(true, true, NPC.class),
        PASSIVE(true, false, Bat.class, Chicken.class, Cow.class, MushroomCow.class, Pig.class, Sheep.class, Squid.class, Villager.class, CommonUtil.getClass("org.bukkit.entity.AbstractVillager"), CommonUtil.getClass("org.bukkit.entity.Fish"), CommonUtil.getClass("org.bukkit.entity.Dolphin")),
        NEUTRAL(true, false, CaveSpider.class, Enderman.class, Spider.class, PigZombie.class),
        HOSTILE(true, false, Blaze.class, Creeper.class, Guardian.class, Endermite.class, Ghast.class, MagmaCube.class, Silverfish.class, Skeleton.class, Slime.class, Witch.class, Zombie.class, CommonUtil.getClass("org.bukkit.entity.Phantom")),
        TAMEABLE(true, false, Tameable.class),
        UTILITY(true, false, Golem.class),
        BOSS(true, false, EnderDragon.class, EnderDragonPart.class, Wither.class),
        OTHER(false, true);

        private final boolean isMob;
        private boolean isLegacyMob;
        private Set<Class<?>> entityClasses;
        private Method method;

        private EntityCategory(boolean isMob, boolean isLegacyMob) {
            this.isMob = isMob;
            this.isLegacyMob = isLegacyMob;
            this.setEntityClasses();
            this.setMethod(null);
        }

        private EntityCategory(boolean isMob, boolean isLegacyMob, Class<?> ... classes) {
            this.isMob = isMob;
            this.isLegacyMob = isLegacyMob;
            this.setMethod(null);
            this.setEntityClasses(classes);
        }

        private EntityCategory(boolean isMob, boolean isLegacyMob, String methodName) {
            this.isMob = isMob;
            this.isLegacyMob = isLegacyMob;
            this.setEntityClasses();
            try {
                Method methodTry = EntityGroupingUtil.class.getMethod(methodName, Entity.class);
                this.setMethod(methodTry);
            }
            catch (Exception e) {
                Logging.LOGGER.log(Level.WARNING, String.format("Unable to reference method %s(Entity) in EntityGroupingUtil: %s", methodName, e.getMessage()));
                this.setMethod(null);
            }
        }

        private void setEntityClasses() {
            this.entityClasses = new HashSet();
        }

        public boolean isMob() {
            return this.isMob;
        }

        public boolean isLegacyMob() {
            return this.isLegacyMob;
        }

        public Set<Class<?>> getEntityClasses() {
            return this.entityClasses;
        }

        public void setEntityClasses(Set<Class<?>> entityClasses) {
            this.entityClasses = entityClasses;
        }

        public void setEntityClasses(Class<?>[] entityClasses) {
            this.setEntityClasses(new HashSet(Arrays.asList(entityClasses)));
            this.entityClasses.remove(null);
        }

        public Method getMethod() {
            return this.method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public boolean contains(Class<?> entityClass) {
            return this.getEntityClasses().contains(entityClass);
        }
    }
}

