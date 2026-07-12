/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.minecart.RideableMinecart
 */
package com.bergerkiller.bukkit.common.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.ClassMap;
import com.bergerkiller.bukkit.common.controller.DefaultEntityController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.LegacyObjectTypes;
import com.bergerkiller.bukkit.common.entity.TypeNameAliases;
import com.bergerkiller.bukkit.common.entity.TypeNameLookup;
import com.bergerkiller.bukkit.common.entity.type.CommonItem;
import com.bergerkiller.bukkit.common.entity.type.CommonLivingEntity;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartChest;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartCommandBlock;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartFurnace;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartHopper;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartMobSpawner;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartRideable;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartTNT;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecartUnknown;
import com.bergerkiller.bukkit.common.entity.type.CommonPlayer;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.hooks.EntityHook;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityTypeHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.AbstractMinecartHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import java.lang.reflect.Constructor;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.RideableMinecart;

public class CommonEntityType {
    private static final StampedLock lock;
    private static final ClassMap<CommonEntityType> byNMS;
    private static final EnumMap<EntityType, CommonEntityType> byEntityType;
    private static final Map<String, CommonEntityType> byName;
    private static final Map<Integer, CommonEntityType> byObjectTypeId;
    private static final Map<Integer, CommonEntityType> byEntityTypeId;
    private static final Map<Object, CommonEntityType> byNMSEntityType;
    private static final Map<Class<?>, EntityTypeHandle> entityTypesByClass;
    private static final CommonPair[] commonPairs;
    public static final CommonEntityType UNKNOWN;
    public static final CommonEntityType PLAYER;
    public static final CommonEntityType MINECART;
    public static final CommonEntityType CHEST_MINECART;
    public static final CommonEntityType FURNACE_MINECART;
    public static final CommonEntityType TNT_MINECART;
    public static final CommonEntityType HOPPER_MINECART;
    public static final CommonEntityType SPAWNER_MINECART;
    public static final CommonEntityType COMMAND_BLOCK_MINECART;
    public final ClassTemplate<?> nmsType;
    public final ClassTemplate<?> commonType;
    public final ClassTemplate<?> bukkitType;
    private final Function<Entity, CommonEntity<?>> commonConstructor;
    private final boolean hasWorldCoordConstructor;
    public final List<String> entityTypeNames;
    public final EntityType entityType;
    public final int entityTypeId;
    public final int objectTypeId;
    public final int objectExtraData;
    public final int moveTicks;
    public final EntityTypeHandle nmsEntityType;

    private CommonEntityType(EntityType entityType, boolean nullInitialize) {
        this.entityType = entityType;
        this.entityTypeNames = TypeNameAliases.getNames(entityType);
        if (nullInitialize) {
            this.nmsType = ClassTemplate.create((Class)null);
            this.commonType = ClassTemplate.create(CommonEntity.class);
            this.bukkitType = ClassTemplate.create(Entity.class);
            this.commonConstructor = CommonEntity::new;
            this.hasWorldCoordConstructor = false;
            this.entityTypeId = -1;
            this.nmsEntityType = null;
            this.objectTypeId = -1;
            this.objectExtraData = -1;
            this.moveTicks = 3;
            return;
        }
        Class<?> nmsType = null;
        String entityTypeName = entityType.getName();
        String entityTypeEnumName = entityType.name();
        if (this.entityTypeNames.contains("FURNACE_MINECART")) {
            if (Common.evaluateMCVersion(">=", "1.11")) {
                entityTypeName = "furnace_minecart";
            }
        } else if (this.entityTypeNames.contains("SPAWNER_MINECART")) {
            if (Common.evaluateMCVersion("<", "1.11")) {
                entityTypeName = "MinecartSpawner";
            }
        } else if (entityTypeEnumName.equals("TIPPED_ARROW")) {
            if (Common.evaluateMCVersion("<", "1.11")) {
                entityTypeName = "Arrow";
            }
        } else if (entityTypeEnumName.equals("PLAYER")) {
            entityTypeName = null;
        }
        if (entityTypeName != null) {
            nmsType = EntityTypeHandle.getEntityClass(entityTypeName);
        }
        if (nmsType == null) {
            String nmsName = null;
            if (entityTypeEnumName.equals("PLAYER")) {
                nmsName = "net.minecraft.server.level.ServerPlayer";
            } else if (entityTypeEnumName.equals("FISHING_HOOK")) {
                nmsName = "net.minecraft.world.entity.projectile.FishingHook";
            } else if (entityTypeEnumName.equals("LIGHTNING")) {
                nmsName = "net.minecraft.world.entity.LightningBolt";
            } else if (entityTypeEnumName.equals("WEATHER")) {
                nmsName = "net.minecraft.world.entity.EntityWeather";
            } else if (entityTypeEnumName.equals("COMPLEX_PART")) {
                nmsName = "net.minecraft.world.entity.boss.enderdragon.EnderDragonPart";
            }
            if (entityTypeEnumName.equals("EGG")) {
                nmsName = "net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEgg";
            } else if (entityTypeEnumName.equals("AREA_EFFECT_CLOUD")) {
                nmsName = "net.minecraft.world.entity.AreaEffectCloud";
            } else if (entityTypeEnumName.equals("SPLASH_POTION")) {
                nmsName = "net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion";
            }
            if (entityTypeEnumName.equals("TIPPED_ARROW")) {
                nmsName = "net.minecraft.world.entity.projectile.arrow.Arrow";
            } else if (entityTypeEnumName.equals("LINGERING_POTION")) {
                nmsName = "net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion";
            }
            if (entityTypeEnumName.equals("CAMEL")) {
                nmsName = "net.minecraft.world.entity.animal.camel.Camel";
            }
            if (entityTypeEnumName.equals("SNIFFER")) {
                nmsName = "net.minecraft.world.entity.animal.sniffer.Sniffer";
            }
            if (nmsName != null) {
                nmsType = CommonUtil.getClass(nmsName);
            }
            if (nmsType == null && !Common.SERVER.isCustomEntityType(entityType)) {
                if (nmsName == null) {
                    Logging.LOGGER_REGISTRY.log(Level.WARNING, "Entity type could not be registered: unknown type (" + entityType.toString() + ")");
                } else {
                    Logging.LOGGER_REGISTRY.log(Level.WARNING, "Entity type could not be registered: class not found (" + entityType.toString() + ") class=" + nmsName);
                }
            }
        }
        this.bukkitType = ClassTemplate.create(entityType.getEntityClass());
        Class<CommonEntity> commonType = CommonEntity.class;
        Class<Object> entityClass = CommonBootstrap.evaluateMCVersion("==", "1.21.5") && entityType == EntityType.MINECART ? RideableMinecart.class : this.bukkitType.getType();
        Function<Entity, CommonEntity> commonConstructor = CommonEntity::new;
        if (entityClass != null) {
            for (CommonPair pair : commonPairs) {
                if (!pair.bukkitType.isAssignableFrom(entityClass)) continue;
                commonType = pair.commonType;
                entityClass = pair.bukkitType;
                commonConstructor = pair.constructor;
                break;
            }
        }
        this.nmsType = ClassTemplate.create(nmsType);
        this.commonType = ClassTemplate.create(commonType);
        this.commonConstructor = commonConstructor;
        this.entityTypeId = EntityTypeHandle.getEntityTypeId(nmsType);
        if (this.entityTypeId != -1) {
            byEntityTypeId.put(this.entityTypeId, this);
        }
        if (EntityTypeHandle.T.fromEntityClass.isAvailable() && nmsType != null) {
            this.nmsEntityType = CommonEntityType.getNMSEntityTypeByEntityClass(nmsType);
            if (this.nmsEntityType != null) {
                byNMSEntityType.put(this.nmsEntityType.getRaw(), this);
            }
        } else {
            this.nmsEntityType = null;
        }
        if (nmsType != null) {
            boolean hasConstructor = false;
            try {
                nmsType.getConstructor(CommonUtil.getClass("net.minecraft.world.level.Level"), Double.TYPE, Double.TYPE, Double.TYPE);
                hasConstructor = true;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            this.hasWorldCoordConstructor = hasConstructor;
        } else {
            this.hasWorldCoordConstructor = false;
        }
        if (EntityTypeHandle.T.getTypeId.isAvailable()) {
            if (this.nmsEntityType != null) {
                this.objectTypeId = EntityTypeHandle.T.getTypeId.invoke(this.nmsEntityType.getRaw());
                this.objectExtraData = 0;
            } else {
                this.objectTypeId = -1;
                this.objectExtraData = -1;
            }
        } else {
            LegacyObjectTypes.ObjectTypeInfo objectInfo = LegacyObjectTypes.find(entityType);
            if (objectInfo != null) {
                this.objectTypeId = objectInfo.typeId;
                this.objectExtraData = objectInfo.extraData;
            } else {
                this.objectTypeId = -1;
                this.objectExtraData = -1;
            }
        }
        if (this.objectTypeId != -1) {
            byObjectTypeId.put(this.objectTypeId, this);
        }
        this.moveTicks = this.nmsType.getType() != null && AbstractMinecartHandle.T.isAssignableFrom(this.nmsType.getType()) ? 5 : 3;
    }

    public <T extends Entity> CommonEntity<T> createCommonEntity(T entity) {
        return this.commonConstructor.apply(entity);
    }

    public <T extends Entity> CommonEntity<T> createCommonEntityNull() {
        EntityHook hook = new EntityHook();
        hook.setStack(new Throwable());
        Object handle = hook.createInstance(this.nmsType.getType());
        CommonEntity<T> entity = this.createCommonEntityFromHandle(handle);
        DefaultEntityController controller = new DefaultEntityController();
        controller.bind(entity, false);
        return entity;
    }

    public <T extends Entity> CommonEntity<T> createCommonEntityFromHandle(Object handle) {
        Entity e = WrapperConversion.toEntity(handle);
        return this.createCommonEntity(e);
    }

    public <T extends Entity> CommonEntity<T> createNMSHookEntity(Location location) {
        Object handle;
        World world = location.getWorld();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        if (world == null) {
            throw new IllegalArgumentException("Location has a null World");
        }
        EntityHook hook = new EntityHook();
        hook.setStack(new Throwable());
        if (this.hasWorldCoordConstructor) {
            handle = hook.constructInstance(this.nmsType.getType(), new Class[]{LevelHandle.T.getType(), Double.TYPE, Double.TYPE, Double.TYPE}, new Object[]{Conversion.toWorldHandle.convert(world), x, y, z});
        } else if (CommonCapabilities.ENTITY_USES_ENTITYTYPES_IN_CONSTRUCTOR) {
            if (this.nmsEntityType == null) {
                throw new IllegalStateException("Type " + this.toString() + " cannot be constructed");
            }
            handle = hook.constructInstance(this.nmsType.getType(), new Class[]{EntityTypeHandle.T.getType(), LevelHandle.T.getType()}, new Object[]{this.nmsEntityType.getRaw(), Conversion.toWorldHandle.convert(world)});
        } else {
            handle = hook.constructInstance(this.nmsType.getType(), new Class[]{LevelHandle.T.getType()}, new Object[]{Conversion.toWorldHandle.convert(world)});
        }
        CommonEntity<T> entity = this.createCommonEntityFromHandle(handle);
        entity.loc.set(entity.last.set(location));
        if (!this.hasWorldCoordConstructor) {
            entity.setPosition(location.getX(), location.getY(), location.getZ());
        }
        DefaultEntityController controller = new DefaultEntityController();
        controller.bind(entity, false);
        return entity;
    }

    public Object createNMSHookFromEntity(CommonEntity<?> entity) {
        EntityHook hook = new EntityHook();
        hook.setStack(new Throwable());
        return hook.hook(entity.getHandle());
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("CommonEntityType{");
        str.append("nms=").append(this.nmsType == null || !this.nmsType.isValid() ? "null" : this.nmsType.getType().getSimpleName());
        str.append(", bukkit=").append(this.bukkitType == null || !this.bukkitType.isValid() ? "null" : this.bukkitType.getType().getSimpleName());
        str.append(", enum=").append(this.entityType);
        str.append('}');
        return str.toString();
    }

    @ConverterMethod
    public static CommonEntityType byEntityType(EntityType type) {
        return LogicUtil.fixNull(byEntityType.get(type), UNKNOWN);
    }

    public static CommonEntityType byEntity(Entity entity) {
        return CommonEntityType.byNMSEntity(HandleConversion.toEntityHandle(entity));
    }

    @ConverterMethod
    public static CommonEntityType byNMSEntityClass(Class<?> entityClass) {
        return LogicUtil.fixNull(byNMS.get(entityClass), UNKNOWN);
    }

    public static CommonEntityType byNMSEntity(Object entityHandle) {
        return LogicUtil.fixNull(byNMS.get(entityHandle), UNKNOWN);
    }

    public static CommonEntityType byObjectTypeId(int objectTypeId) {
        return LogicUtil.fixNull(byObjectTypeId.get(objectTypeId), UNKNOWN);
    }

    public static CommonEntityType byEntityTypeId(int entityTypeId) {
        return LogicUtil.fixNull(byEntityTypeId.get(entityTypeId), UNKNOWN);
    }

    @ConverterMethod(input="net.minecraft.world.entity.EntityType")
    public static CommonEntityType byNMSEntityTypeRaw(Object entityTypesHandleRaw) {
        if (entityTypesHandleRaw != null) {
            CommonEntityType type = byNMSEntityType.get(entityTypesHandleRaw);
            if (type != null) {
                return type;
            }
            if (EntityTypeHandle.T.getEntityClassInst.isAvailable()) {
                Class<?> nmsType = EntityTypeHandle.T.getEntityClassInst.invoke(entityTypesHandleRaw);
                return CommonEntityType.byNMSEntityClass(nmsType);
            }
        }
        return UNKNOWN;
    }

    @ConverterMethod(output="net.minecraft.world.entity.EntityType")
    public static Object toNMSEntityTypeRaw(CommonEntityType entityType) {
        return entityType.nmsEntityType == null ? null : entityType.nmsEntityType.getRaw();
    }

    public static CommonEntityType byName(String name) {
        return byName.getOrDefault(name, UNKNOWN);
    }

    @ConverterMethod
    public static CommonEntityType parseNameToCommonEntityType(String name) {
        TypeNameLookup.NamePair result = TypeNameLookup.lookupByName(name);
        return result == null ? null : result.commonEntityType;
    }

    @ConverterMethod
    public static EntityType parseNameToEntityType(String name) {
        TypeNameLookup.NamePair result = TypeNameLookup.lookupByName(name);
        return result == null ? null : result.entityType;
    }

    public static CommonEntityType byNMSEntityType(EntityTypeHandle handle) {
        return handle == null ? UNKNOWN : CommonEntityType.byNMSEntityTypeRaw(handle.getRaw());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static EntityTypeHandle getNMSEntityTypeByEntityClass(Class<?> entityClass) {
        EntityTypeHandle entityTypes;
        long stamp = lock.readLock();
        try {
            entityTypes = entityTypesByClass.get(entityClass);
            if (entityTypes != null) {
                EntityTypeHandle entityTypeHandle = entityTypes;
                return entityTypeHandle;
            }
        }
        finally {
            lock.unlockRead(stamp);
        }
        stamp = lock.writeLock();
        try {
            entityTypes = EntityTypeHandle.T.fromEntityClass.invoke(entityClass);
            entityTypesByClass.put(entityClass, entityTypes);
            EntityTypeHandle entityTypeHandle = entityTypes;
            return entityTypeHandle;
        }
        finally {
            lock.unlockWrite(stamp);
        }
    }

    private static void initRegistry() {
        int logLimitCtr = 0;
        for (EntityType entityType : EntityType.values()) {
            if (entityType == EntityType.UNKNOWN) continue;
            CommonEntityType commonEntityType = null;
            try {
                commonEntityType = new CommonEntityType(entityType, false);
            }
            catch (Throwable t) {
                if (++logLimitCtr <= 10) {
                    Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Failed to register entity type " + entityType.toString(), t);
                }
                Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Failed to register entity type " + entityType.toString());
            }
            if (commonEntityType == null) {
                commonEntityType = new CommonEntityType(entityType, true);
            }
            byNMS.put(commonEntityType.nmsType, commonEntityType);
            byEntityType.put(entityType, commonEntityType);
            for (String name : commonEntityType.entityTypeNames) {
                byName.put(name, commonEntityType);
            }
        }
    }

    static {
        CommonBootstrap.initCommonServerAssertCompatibility();
        lock = new StampedLock();
        byNMS = new ClassMap();
        byEntityType = new EnumMap(EntityType.class);
        byName = new HashMap<String, CommonEntityType>();
        byObjectTypeId = new HashMap<Integer, CommonEntityType>();
        byEntityTypeId = new HashMap<Integer, CommonEntityType>();
        byNMSEntityType = new HashMap<Object, CommonEntityType>();
        entityTypesByClass = new HashMap();
        commonPairs = new CommonPair[]{CommonPair.create(CommonPlayer.class, CommonPlayer::new), CommonPair.create(CommonMinecartChest.class, CommonMinecartChest::new), CommonPair.create(CommonMinecartCommandBlock.class, CommonMinecartCommandBlock::new), CommonPair.create(CommonMinecartFurnace.class, CommonMinecartFurnace::new), CommonPair.create(CommonMinecartHopper.class, CommonMinecartHopper::new), CommonPair.create(CommonMinecartMobSpawner.class, CommonMinecartMobSpawner::new), CommonPair.create(CommonMinecartTNT.class, CommonMinecartTNT::new), CommonPair.create(CommonMinecartRideable.class, CommonMinecartRideable::new), CommonPair.create(CommonMinecartCommandBlock.class, CommonMinecartCommandBlock::new), CommonPair.create(CommonMinecartUnknown.class, CommonMinecartUnknown::new), CommonPair.create(CommonItem.class, CommonItem::new), CommonPair.create(CommonMinecart.class, CommonMinecart::new), CommonPair.create(CommonLivingEntity.class, CommonLivingEntity::new)};
        UNKNOWN = new CommonEntityType(EntityType.UNKNOWN, true);
        CommonEntityType.initRegistry();
        PLAYER = CommonEntityType.byName("PLAYER");
        MINECART = CommonEntityType.byName("MINECART");
        CHEST_MINECART = CommonEntityType.byName("CHEST_MINECART");
        FURNACE_MINECART = CommonEntityType.byName("FURNACE_MINECART");
        TNT_MINECART = CommonEntityType.byName("TNT_MINECART");
        HOPPER_MINECART = CommonEntityType.byName("HOPPER_MINECART");
        SPAWNER_MINECART = CommonEntityType.byName("SPAWNER_MINECART");
        COMMAND_BLOCK_MINECART = CommonEntityType.byName("COMMAND_BLOCK_MINECART");
    }

    private static class CommonPair {
        public final Class<? extends Entity> bukkitType;
        public final Class<? extends CommonEntity> commonType;
        public final Function<Entity, CommonEntity<?>> constructor;

        private CommonPair(Class<? extends Entity> bukkitType, Class<? extends CommonEntity> commonType, Function<Entity, CommonEntity<?>> constructor) {
            this.bukkitType = bukkitType;
            this.commonType = commonType;
            this.constructor = constructor;
        }

        public static <B extends Entity> CommonPair create(Class<? extends CommonEntity> commonType, Function<B, ? extends CommonEntity<?>> constructor) {
            for (Constructor<?> c : commonType.getDeclaredConstructors()) {
                Class<?> inputType;
                if (c.getParameterCount() != 1 || !Entity.class.isAssignableFrom(inputType = c.getParameterTypes()[0])) continue;
                return new CommonPair(inputType, commonType, constructor);
            }
            throw new IllegalStateException("CommonType has no valid constructor: " + commonType.getSimpleName());
        }
    }
}

