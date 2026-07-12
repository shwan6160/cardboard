/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook_1_14;
import com.bergerkiller.bukkit.common.internal.logic.EntityTypingHandler;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityTypeHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.ConstantReturningInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.logging.Level;
import org.bukkit.entity.Entity;

class EntityTypingHandler_1_14
extends EntityTypingHandler {
    private final IdentityHashMap<Object, Class<?>> _cache = new IdentityHashMap();
    private final Class<?> entityTypesClass = CommonUtil.getClass("net.minecraft.world.entity.EntityTypes");
    private final Handler _handler = Template.Class.create(Handler.class, Common.TEMPLATE_RESOLVER);
    private Object nmsWorldHandle;

    @Override
    public void enable() {
        this._handler.forceInitialization();
        if (this.entityTypesClass == null) {
            throw new IllegalStateException("net.minecraft.world.entity.EntityTypes class not found");
        }
        ClassInterceptor interceptor = new ClassInterceptor(){

            @Override
            protected Invoker<?> getCallback(Method method) {
                if (method.getName().equals("getNextEntityId")) {
                    return ConstantReturningInvoker.of(1);
                }
                if (method.getName().equals("getSeaLevel")) {
                    return ConstantReturningInvoker.of(64);
                }
                return null;
            }
        };
        this.nmsWorldHandle = interceptor.hook(ServerLevelHandle.T.newInstanceNull());
        Object primaryLevelData = this._handler.createPrimaryLevelData();
        this._handler.initWorldServer(this.nmsWorldHandle, primaryLevelData);
        this.registerEntityTypes("AREA_EFFECT_CLOUD", "net.minecraft.world.entity.AreaEffectCloud");
        this.registerEntityTypes("ENDER_DRAGON", "net.minecraft.world.entity.boss.enderdragon.EnderDragon");
        this.registerEntityTypes("FIREBALL", "net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball");
        this.registerEntityTypes("FISHING_BOBBER", "net.minecraft.world.entity.projectile.FishingHook");
        this.registerEntityTypes("LIGHTNING_BOLT", "net.minecraft.world.entity.LightningBolt");
        this.registerEntityTypes("PLAYER", "net.minecraft.server.level.ServerPlayer");
        this.registerEntityTypes("WITHER", "net.minecraft.world.entity.boss.wither.WitherBoss");
        for (Field f : EntityTypeHandle.T.getType().getDeclaredFields()) {
            Object nmsEntityTypes;
            if (!Modifier.isStatic(f.getModifiers())) continue;
            TypeDeclaration typeDec = TypeDeclaration.fromType(f.getGenericType());
            if (!EntityTypeHandle.T.getType().isAssignableFrom(typeDec.type) || typeDec.genericTypes.length != 1) continue;
            try {
                if (!Modifier.isPublic(f.getModifiers())) {
                    f.setAccessible(true);
                }
                nmsEntityTypes = f.get(null);
            }
            catch (Throwable t) {
                continue;
            }
            if (this._cache.containsKey(nmsEntityTypes)) continue;
            this._cache.put(nmsEntityTypes, typeDec.genericTypes[0].type);
        }
    }

    @Override
    public void disable() {
    }

    private void registerEntityTypes(String name, String nmsClassName) {
        String realName = Resolver.resolveFieldName(this.entityTypesClass, name);
        String s = name.equals(realName) ? name : name + ":" + realName;
        try {
            Object nmsEntityTypes;
            Field field = MPLType.getDeclaredField(this.entityTypesClass, realName);
            if ((field.getModifiers() & 8) == 0) {
                throw new IllegalStateException("EntityType field " + s + " is not static");
            }
            if ((field.getModifiers() & 1) == 0) {
                field.setAccessible(true);
                nmsEntityTypes = field.get(null);
                field.setAccessible(false);
            } else {
                nmsEntityTypes = field.get(null);
            }
            Class<?> type = CommonUtil.getClass(nmsClassName);
            if (type == null) {
                throw new IllegalStateException("EntityType type " + nmsClassName + " not found");
            }
            this._cache.put(nmsEntityTypes, type);
        }
        catch (Throwable t) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to find EntityTypes field " + s, t);
        }
    }

    @Override
    public Class<?> getClassFromEntityTypes(Object nmsEntityTypesInstance) {
        Class<?> result = this._cache.get(nmsEntityTypesInstance);
        if (result == null) {
            result = this._handler.findClassFromEntityTypes(nmsEntityTypesInstance, this.nmsWorldHandle);
            this._cache.put(nmsEntityTypesInstance, result);
        }
        return result;
    }

    @Override
    public EntityTrackerEntryHandle createEntityTrackerEntry(EntityTracker entityTracker, Entity entity) {
        Object handle = this._handler.createEntry(entityTracker.getRawHandle(), HandleConversion.toEntityHandle(entity));
        EntityTrackerEntryHandle entry = EntityTrackerEntryHandle.createHandle(handle);
        EntityTrackerEntryStateHandle.T.opt_passengers.set(entry.getState().getRaw(), new ExtendedEntity<Entity>(entity).getPassengers());
        return entry;
    }

    @Override
    public EntityTrackerEntryHook getEntityTrackerEntryHook(Object entityTrackerEntryHandle) {
        return EntityTrackerEntryHook_1_14.get(entityTrackerEntryHandle, EntityTrackerEntryHook_1_14.class);
    }

    @Override
    public Object hookEntityTrackerEntry(Object entityTrackerEntryHandle) {
        return new EntityTrackerEntryHook_1_14().hook(entityTrackerEntryHandle);
    }

    @Template.Package(value="net.minecraft.server.level")
    @Template.ImportList(value={@Template.Import(value="net.minecraft.world.entity.EntityType"), @Template.Import(value="net.minecraft.server.level.ServerLevel"), @Template.Import(value="net.minecraft.world.level.LevelSettings"), @Template.Import(value="net.minecraft.world.level.storage.PrimaryLevelData"), @Template.Import(value="net.minecraft.world.entity.Entity"), @Template.Import(value="net.minecraft.world.level.Level"), @Template.Import(value="net.minecraft.core.registries.BuiltInRegistries"), @Template.Import(value="net.minecraft.core.RegistryAccess"), @Template.Import(value="net.minecraft.resources.Identifier"), @Template.Import(value="net.minecraft.world.attribute.EnvironmentAttributeSystem"), @Template.Import(value="net.minecraft.world.level.dimension.DimensionType"), @Template.Import(value="net.minecraft.world.clock.ServerClockManager"), @Template.Import(value="com.bergerkiller.bukkit.common.utils.CommonUtil"), @Template.Import(value="com.bergerkiller.mountiplex.reflection.ClassTemplate"), @Template.Import(value="com.bergerkiller.mountiplex.reflection.util.NullInstantiator")})
    public static abstract class Handler
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static Class<?> findClassFromEntityTypes((Object) EntityType entityTypes, (Object) Level world) {\n    Object entity;\n    try {\n           #if version >= 1.21.2\n        entity = entityTypes.create(world, net.minecraft.world.entity.EntitySpawnReason.COMMAND);\n#elseif version >= 1.18\n        entity = entityTypes.create(world);\n#else\n                   entity = entityTypes.a(world);\n#endif\n               } catch (Throwable t) {\n        // Try to find some descriptive name for this entity type\n        // This could fail if the type is not registered\n        Identifier name = (Identifier) BuiltInRegistries.ENTITY_TYPE.getKey(entityTypes);\n        if (name == null) {\n            throw new IllegalStateException(\"Failed to find entity class of unregistered entity type (\" +\n                    entityTypes.getClass().getName() + \")\", t);\n        }\n\n                   // Look the same entity type object up again by this same key\n        // If this returns a different object, try with that instead\n#if version >= 26.2\n        EntityType entityTypesAlter = (EntityType) BuiltInRegistries.ENTITY_TYPE.getValue(name);\n#else\n                   EntityType entityTypesAlter = (EntityType) BuiltInRegistries.ENTITY_TYPE.get(name);\n#endif\n                   if (entityTypes == entityTypesAlter || entityTypesAlter == null) {\n            throw new IllegalStateException(\"Failed to construct entity of type \" + name, t);\n        }\n\n                   // Try again, but this time with the one from the registry\n        try {\n#if version >= 1.21.2\n            entity = entityTypesAlter.create(world, net.minecraft.world.entity.EntitySpawnReason.COMMAND);\n#elseif version >= 1.18\n            entity = entityTypesAlter.create(world);\n#else\n                       entity = entityTypesAlter.a(world);\n#endif\n                   } catch (Throwable t2) {\n            // Throw the original error\n            throw new IllegalStateException(\"Failed to construct entity of (adjusted) type \" + name, t);\n        }\n               }\n\n    if (entity == null) {\n        return null;\n    } else {\n        return entity.getClass();\n    }\n           }")
        public abstract Class<?> findClassFromEntityTypes(Object var1, Object var2);

        @Template.Generated(value="public static Object createPrimaryLevelData() {\n    // Create WorldData instance by null-constructing it\n    PrimaryLevelData primaryLevelData;\n    Class worldDataServerType = CommonUtil.getClass(\"net.minecraft.world.level.storage.PrimaryLevelData\");\n#if version >= 1.16\n    primaryLevelData = (PrimaryLevelData) NullInstantiator.of(worldDataServerType).create();\n#else\n               primaryLevelData = (PrimaryLevelData) ClassTemplate.create(worldDataServerType)\n            .getConstructor(new Class[0])\n            .newInstance(new Object[0]);\n#endif\n\n               // Also assign the LevelSettings, required on 26.2+\n#if version >= 26.1\n    net.minecraft.world.level.LevelSettings$DifficultySettings difficultySettings = new net.minecraft.world.level.LevelSettings$DifficultySettings(\n            net.minecraft.world.Difficulty.NORMAL, false, false);\n    LevelSettings levelSettings = new LevelSettings(\n            \"zzdummyzz\", // levelName\n            net.minecraft.world.level.GameType.SURVIVAL,\n            difficultySettings,\n            false, // allowCommands\n            net.minecraft.world.level.WorldDataConfiguration.DEFAULT\n    );\n               #require PrimaryLevelData public LevelSettings settings;\n    primaryLevelData#settings = levelSettings;\n#endif\n               return primaryLevelData;\n}")
        public abstract Object createPrimaryLevelData();

        @Template.Generated(value="public static void initWorldServer((Object) ServerLevel worldserver, (Object) PrimaryLevelData worldData) {\n    String dummyWorldName = \"zzdummyzz\";\n\n    // Ensure dimensionTypeRegistration is initialized, environment attributes init needs this\n#if version >= 1.21.11\n    net.minecraft.server.MinecraftServer minecraftServer = net.minecraft.server.MinecraftServer.getServer();\n    net.minecraft.core.RegistryAccess$Frozen registry = minecraftServer.getServer().registryAccess();\n    net.minecraft.core.Registry dimensionRegistry = registry.lookupOrThrow(net.minecraft.core.registries.Registries.DIMENSION_TYPE);\n    DimensionType dimensionType = (DimensionType) dimensionRegistry.getValue(net.minecraft.world.level.dimension.BuiltinDimensionTypes.OVERWORLD);\n    net.minecraft.core.Holder dimensionTypeHolder = dimensionRegistry.wrapAsHolder(dimensionType);\n    if (dimensionType == null || dimensionTypeHolder == null) {\n        throw new IllegalStateException(\"Overworld dimension type not found\");\n    }\n               if (dimensionTypeHolder.value() == null) {\n        throw new IllegalStateException(\"DimensionType wrapasholder returned a holder with a null value\");\n    }\n               #require net.minecraft.world.level.Level  private final net.minecraft.core.Holder<net.minecraft.world.level.dimension.DimensionType> dimensionTypeRegistration;\n    worldserver#dimensionTypeRegistration = dimensionTypeHolder;\n\n    // Universe Spigot\n  #if exists net.minecraft.world.level.Level private net.minecraft.world.level.dimension.DimensionType cachedDimensionType\n    #require net.minecraft.world.level.Level private net.minecraft.world.level.dimension.DimensionType cachedDimensionType;\n    worldserver#cachedDimensionType = dimensionType;\n  #endif\n           #endif\n\n    // Ensure registryAccess is initialized, some entities call this in the constructor to lookup stuff\n#if version >= 1.21.6\n    RegistryAccess registryAccess = org.bukkit.craftbukkit.CraftRegistry.getMinecraftRegistry();\n    #require net.minecraft.world.level.Level private final net.minecraft.core.RegistryAccess registryAccess;\n    worldserver#registryAccess = registryAccess;\n#endif\n\n               // Ensure ServerClockManager is initialized, EnvironmentAttributeSystem depends on this\n#if version >= 26.2\n    ServerClockManager serverClockManager = (ServerClockManager) ServerClockManager.TYPE.constructor().get();\n  #if paper\n               serverClockManager.init(minecraftServer, worldserver);\n  #else\n               serverClockManager.init(worldserver);\n  #endif\n               #require ServerLevel private final ServerClockManager clockManager;\n    worldserver#clockManager = serverClockManager;\n#endif\n\n               // Ensure EnvironmentAttributes is initialized, entities use it in their 'Brain' AI logic\n#if version >= 1.21.11\n    EnvironmentAttributeSystem environmentAttributes = EnvironmentAttributeSystem.builder().addDefaultLayers(worldserver).build();\n    #require ServerLevel private EnvironmentAttributeSystem environmentAttributes;\n    worldserver#environmentAttributes = environmentAttributes;\n#endif\n\n               // Spigot World configuration\n#if fieldexists net.minecraft.world.level.Level public final org.spigotmc.SpigotWorldConfig spigotConfig;\n    org.spigotmc.SpigotWorldConfig spigotConfig;\n\n    // While loading set verbose to false, and later restore, to avoid logging a bunch of crap about this dummy world\n    String spigotDummyWorldConfigKey = \"world-settings.\" + dummyWorldName;\n    String spigotConfigVerboseKey = spigotDummyWorldConfigKey + \".verbose\";\n    boolean hadDummyWorldConfig = org.spigotmc.SpigotConfig.config.contains(spigotDummyWorldConfigKey);\n    boolean hadVerboseConfigOption = org.spigotmc.SpigotConfig.config.contains(spigotConfigVerboseKey, true);\n    boolean prevVerboseConfigOption = org.spigotmc.SpigotConfig.config.getBoolean(spigotConfigVerboseKey, true);\n    org.spigotmc.SpigotConfig.config.set(spigotConfigVerboseKey, Boolean.FALSE);\n    try {\n                   #require net.minecraft.world.level.Level public final org.spigotmc.SpigotWorldConfig spigotConfig;\n  #if exists org.spigotmc.SpigotWorldConfig public SpigotWorldConfig(String legacyWorldName, net.kyori.adventure.key.Key key);\n        spigotConfig = new org.spigotmc.SpigotWorldConfig(dummyWorldName, net.kyori.adventure.key.Key.key(dummyWorldName));\n  #else\n                   spigotConfig = new org.spigotmc.SpigotWorldConfig(dummyWorldName);\n  #endif\n                   worldserver#spigotConfig = spigotConfig;\n    } finally {\n        // Restore\n        if (hadVerboseConfigOption) {\n            org.spigotmc.SpigotConfig.config.set(spigotConfigVerboseKey, Boolean.valueOf(prevVerboseConfigOption));\n        } else {\n            org.spigotmc.SpigotConfig.config.set(spigotConfigVerboseKey, null);\n        }\n                   // This will almost always be the case, unless some idiot named his world exactly like that...\n        // Don't underestimate stupidity! So check for that 1/1000000 chance.\n        if (!hadDummyWorldConfig) {\n            org.spigotmc.SpigotConfig.config.set(spigotDummyWorldConfigKey, null);\n        }\n               }\n#endif\n\n           // Paper(Spigot) World configuration\n#if fieldexists net.minecraft.world.level.Level private final io.papermc.paper.configuration.WorldConfiguration paperConfig;\n    #require net.minecraft.world.level.Level private final io.papermc.paper.configuration.WorldConfiguration paperConfig;\n    #require io.papermc.paper.configuration.WorldConfiguration WorldConfiguration create_paper_wc:<init>(org.spigotmc.SpigotWorldConfig spigotWC, net.minecraft.resources.Identifier worldKey);\n  #if version >= 1.21\n    net.minecraft.resources.Identifier worldKey = net.minecraft.resources.Identifier.parse(dummyWorldName);\n  #else\n               net.minecraft.resources.Identifier worldKey = new net.minecraft.resources.Identifier(dummyWorldName);\n  #endif\n               io.papermc.paper.configuration.WorldConfiguration paperWorldConfig = #create_paper_wc(spigotConfig, worldKey);\n    com.bergerkiller.bukkit.common.internal.logic.EntityTypingHandler.initConfigurationPartRecurse(paperWorldConfig);\n\n    worldserver#paperConfig = paperWorldConfig;\n#elseif fieldexists net.minecraft.world.level.Level public final com.destroystokyo.paper.PaperWorldConfig paperConfig;\n    #require net.minecraft.world.level.Level public final com.destroystokyo.paper.PaperWorldConfig paperConfig;\n    com.destroystokyo.paper.PaperWorldConfig paperConfig = new com.destroystokyo.paper.PaperWorldConfig(dummyWorldName, spigotConfig);\n    worldserver#paperConfig = paperConfig;\n#elseif fieldexists net.minecraft.world.level.Level public final com.destroystokyo.paper.PaperWorldConfig paperSpigotConfig;\n    #require net.minecraft.world.level.Level public final com.destroystokyo.paper.PaperWorldConfig paperSpigotConfig;\n    com.destroystokyo.paper.PaperWorldConfig paperConfig = new com.destroystokyo.paper.PaperWorldConfig(dummyWorldName, spigotConfig);\n    worldserver#paperSpigotConfig = paperConfig;\n#endif\n\n           // Purpur World configuration\n#if fieldexists net.minecraft.world.level.Level public final org.purpurmc.purpur.PurpurWorldConfig purpurConfig;\n    #require net.minecraft.world.level.Level public final org.purpurmc.purpur.PurpurWorldConfig purpurConfig;\n    org.purpurmc.purpur.PurpurWorldConfig purpurConfig;\n  #if exists org.purpurmc.purpur.PurpurWorldConfig public PurpurWorldConfig(String legacyWorldName, org.bukkit.World.Environment environment, net.kyori.adventure.key.Key worldKey);\n    purpurConfig = new org.purpurmc.purpur.PurpurWorldConfig(dummyWorldName, org.bukkit.World$Environment.NORMAL, net.kyori.adventure.key.Key.key(dummyWorldName));\n  #elseif exists org.purpurmc.purpur.PurpurWorldConfig public PurpurWorldConfig(net.minecraft.server.level.ServerLevel level, String worldName, org.bukkit.World.Environment environment);\n    purpurConfig = new org.purpurmc.purpur.PurpurWorldConfig(worldserver, dummyWorldName, org.bukkit.World$Environment.NORMAL);\n  #elseif exists org.purpurmc.purpur.PurpurWorldConfig public PurpurWorldConfig(String worldName, org.bukkit.World.Environment environment);\n    purpurConfig = new org.purpurmc.purpur.PurpurWorldConfig(dummyWorldName, org.bukkit.World$Environment.NORMAL);\n  #elseif exists org.purpurmc.purpur.PurpurWorldConfig public org.purpurmc.purpur.PurpurWorldConfig(String worldName);\n    purpurConfig = new org.purpurmc.purpur.PurpurWorldConfig(dummyWorldName);\n  #else\n               purpurConfig = new org.purpurmc.purpur.PurpurWorldConfig(dummyWorldName, paperConfig, spigotConfig);\n  #endif\n               worldserver#purpurConfig = purpurConfig;\n#endif\n\n           #if version >= 1.17\n    // WritableLevelData field\n    #require net.minecraft.world.level.Level protected final net.minecraft.world.level.storage.WritableLevelData levelData;\n    worldserver#levelData = worldData;\n\n    // PrimaryLevelData field (on some servers, it uses the WritableLevelData field instead)\n  #if exists net.minecraft.server.level.ServerLevel public final net.minecraft.world.level.storage.PrimaryLevelData serverLevelData;\n    #require net.minecraft.server.level.ServerLevel public final net.minecraft.world.level.storage.PrimaryLevelData serverLevelData;\n    worldserver#serverLevelData = worldData;\n  #endif\n           #elseif version >= 1.16\n    // WritableLevelData field\n    #require net.minecraft.world.level.Level public final net.minecraft.world.level.storage.WritableLevelData worldData;\n    worldserver#worldData = worldData;\n\n    // PrimaryLevelData field (on some servers, it uses the WritableLevelData field instead)\n  #if exists net.minecraft.server.level.ServerLevel public final net.minecraft.world.level.storage.PrimaryLevelData worldDataServer;\n    #require net.minecraft.server.level.ServerLevel public final net.minecraft.world.level.storage.PrimaryLevelData worldDataServer;\n    worldserver#worldDataServer = worldData;\n  #endif\n           #else\n    // worldProvider field\n    int envId = org.bukkit.World.Environment.NORMAL.getId();\n    worldserver.worldProvider = net.minecraft.world.level.dimension.DimensionType.a(envId).getWorldProvider((Level) worldserver);\n\n    // worldData field\n    #require net.minecraft.world.level.Level public final net.minecraft.world.level.storage.LevelData worldData;\n    worldserver#worldData = worldData;\n#endif\n\n               // Random field\n#if version >= 1.19\n  #if version >= 26.1\n    #require net.minecraft.world.level.Level protected final net.minecraft.util.RandomSource random;\n  #else\n               #require net.minecraft.world.level.Level public final net.minecraft.util.RandomSource random;\n  #endif\n               net.minecraft.util.RandomSource newRandom = net.minecraft.util.RandomSource.create();\n    worldserver#random = newRandom;\n#else\n               #require net.minecraft.world.level.Level public final java.util.Random random;\n    java.util.Random newRandom = new java.util.Random();\n    worldserver#random = newRandom;\n#endif\n\n               // server field (for enabledFeatures() call on 1.19.3+)\n    #require net.minecraft.server.level.ServerLevel private final net.minecraft.server.MinecraftServer server;\n    worldserver#server = net.minecraft.server.MinecraftServer.getServer();\n}")
        public abstract void initWorldServer(Object var1, Object var2);

        @Template.Generated(value="public static Object createEntry((Object) ChunkMap playerChunkMap, (Object) Entity entity) {\n#if version >= 1.18\n    EntityType entitytypes = entity.getType();\n    int i = entitytypes.clientTrackingRange() * 16;\n#else\n               EntityType entitytypes = entity.getEntityType();\n    int i = entitytypes.getChunkRange() * 16;\n#endif\n\n           #if exists org.spigotmc.TrackingRange\n    i = org.spigotmc.TrackingRange.getEntityTrackingRange(entity, i);\n#endif\n\n           #if version >= 1.18\n    int j = entitytypes.updateInterval();\n    boolean trackDeltas = entitytypes.trackDeltas();\n#else\n               int j = entitytypes.getUpdateInterval();\n    boolean trackDeltas = entitytypes.isDeltaTracking();\n#endif\n\n               return new ChunkMap$TrackedEntity(playerChunkMap, entity, i, j, trackDeltas);\n}")
        public abstract Object createEntry(Object var1, Object var2);
    }
}

