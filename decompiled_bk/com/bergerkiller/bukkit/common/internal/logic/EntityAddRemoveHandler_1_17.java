/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventException
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.world.ChunkEvent
 *  org.bukkit.event.world.ChunkUnloadEvent
 *  org.bukkit.plugin.EventExecutor
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.FieldVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

class EntityAddRemoveHandler_1_17
extends EntityAddRemoveHandler {
    private final Class<?> levelCallbackType;
    private final FastField<Object> entityManagerField = new FastField();
    private final FastField<Object> callbacksField = new FastField();
    private final Map<World, LevelCallbackHandler> hooks = new ConcurrentHashMap<World, LevelCallbackHandler>();
    private final AddRemoveHandlerLogic removeHandler;
    private final ChunkEntitiesLoadedHandler chunkEntitiesLoadedHandler;
    private final Class<?> levelCallbackHookType;

    public EntityAddRemoveHandler_1_17() {
        String fieldName;
        Class<Object> sectionManagerClass;
        this.levelCallbackType = Resolver.loadClass("net.minecraft.world.level.entity.LevelCallback", false);
        if (this.levelCallbackType == null) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to find LevelCallback class");
        }
        if ((sectionManagerClass = Resolver.loadClass("net.minecraft.world.level.entity.PersistentEntitySectionManager", false)) == null) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to find PersistentEntitySectionManager class");
        }
        try {
            fieldName = Resolver.resolveFieldName(ServerLevelHandle.T.getType(), "entityManager");
            this.entityManagerField.init(ServerLevelHandle.T.getType().getDeclaredField(fieldName));
            if (!sectionManagerClass.isAssignableFrom(this.entityManagerField.getType())) {
                throw new IllegalStateException("Field not assignable to PersistentEntitySectionManager");
            }
            this.entityManagerField.forceInitialization();
        }
        catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize WorldServer entityManager field: " + t.getMessage(), t);
            this.entityManagerField.initUnavailable("entityManager");
        }
        try {
            fieldName = Resolver.resolveFieldName(sectionManagerClass, "callbacks");
            this.callbacksField.init(sectionManagerClass.getDeclaredField(fieldName));
            if (!this.levelCallbackType.equals(this.callbacksField.getType())) {
                throw new IllegalStateException("Field not assignable to LevelCallback");
            }
            this.callbacksField.forceInitialization();
        }
        catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize PersistentEntitySectionManager callbacks field: " + t.getMessage(), t);
            this.callbacksField.initUnavailable("callbacks field not found");
        }
        this.removeHandler = Template.Class.create(AddRemoveHandlerLogic.class, Common.TEMPLATE_RESOLVER);
        this.levelCallbackHookType = EntityAddRemoveHandler_1_17.generateLevelCallbackHookType(this.callbacksField);
        this.chunkEntitiesLoadedHandler = CommonUtil.getClass("org.bukkit.event.world.EntitiesLoadEvent") != null ? new ChunkEntitiesLoadedUsingEventHandler() : new ChunkEntitiesLoadedUsingHookHandler(sectionManagerClass);
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public void forceInitialization() {
        this.entityManagerField.forceInitialization();
        this.callbacksField.forceInitialization();
        this.removeHandler.forceInitialization();
    }

    @Override
    public void processEvents(World world) {
        LevelCallbackHandler hook = this.hooks.get(world);
        if (hook != null) {
            hook.processEvents();
        }
    }

    @Override
    public boolean isChunkEntitiesLoaded(World world, int cx, int cz) {
        return this.removeHandler.isChunkEntitiesLoaded(HandleConversion.toWorldHandle(world), cx, cz);
    }

    @Override
    public boolean isChunkEntitiesLoaded(Chunk chunk) {
        return this.removeHandler.isChunkEntitiesLoaded(HandleConversion.toWorldHandle(chunk.getWorld()), chunk.getX(), chunk.getZ());
    }

    @Override
    public void onEnabled(CommonPlugin plugin) {
        super.onEnabled(plugin);
        this.chunkEntitiesLoadedHandler.enable(this, plugin);
    }

    @Override
    protected void hook(World world) {
        Object sectionManager = this.entityManagerField.get(HandleConversion.toWorldHandle(world));
        Object callbacks = this.callbacksField.get(sectionManager);
        if (callbacks != null && callbacks.getClass() != this.levelCallbackHookType) {
            try {
                LevelCallbackHandler handler = new LevelCallbackHandler(this, world);
                Object hook = this.levelCallbackHookType.getConstructors()[0].newInstance(callbacks, handler);
                this.callbacksField.set(sectionManager, hook);
                this.hooks.put(world, handler);
            }
            catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to instantiate a level hook callback", t);
            }
        }
        this.chunkEntitiesLoadedHandler.hook(this, world, sectionManager);
    }

    @Override
    protected void unhook(World world) {
        Object sectionManager = this.entityManagerField.get(HandleConversion.toWorldHandle(world));
        Object callbacks = this.callbacksField.get(sectionManager);
        if (callbacks != null && callbacks.getClass() == this.levelCallbackHookType) {
            Object base;
            LevelCallbackHandler handler = SafeField.get(callbacks, "callback", LevelCallbackHandler.class);
            if (handler != null) {
                this.hooks.remove(world, handler);
            }
            if ((base = SafeField.get(callbacks, "base", this.callbacksField.getType())) != null) {
                this.callbacksField.set(sectionManager, base);
            }
        }
        this.chunkEntitiesLoadedHandler.unhook(this, world, sectionManager);
    }

    @Override
    public void replace(EntityHandle oldEntity, EntityHandle newEntity) {
        ServerLevelHandle world = oldEntity.getWorldServer();
        if (newEntity == null && world != null) {
            world.removeEntity(oldEntity);
            world.getEntityTracker().stopTracking(oldEntity.getBukkitEntity());
        }
        if (newEntity == null) {
            world.getEntityTracker().removeEntry(oldEntity.getIdField());
        } else {
            EntityAddRemoveHandler_1_17.replaceInEntityTracker(oldEntity, oldEntity, newEntity);
            if (oldEntity.getVehicle() != null) {
                EntityAddRemoveHandler_1_17.replaceInEntityTracker(oldEntity.getVehicle(), oldEntity, newEntity);
            }
            if (oldEntity.getPassengers() != null) {
                for (EntityHandle passenger : oldEntity.getPassengers()) {
                    EntityAddRemoveHandler_1_17.replaceInEntityTracker(passenger, oldEntity, newEntity);
                }
            }
        }
        Object newEntityRaw = Template.Handle.getRaw(newEntity);
        if (world != null) {
            this.removeHandler.replaceInWorldStorage(world.getRaw(), oldEntity.getRaw(), newEntityRaw);
        }
        this.removeHandler.replaceInSectionStorage(oldEntity.getRaw(), newEntityRaw);
        this.removeHandler.replaceInChunkStorage(oldEntity.getRaw(), newEntityRaw);
    }

    @Override
    public void moveToChunk(EntityHandle entity) {
    }

    private static void replaceInEntityTracker(EntityHandle entity, EntityHandle oldEntity, EntityHandle newEntity) {
        EntityTracker trackerMap = WorldUtil.getTracker(newEntity.getBukkitWorld());
        EntityTrackerEntryHandle entry = trackerMap.getEntry(entity.getIdField());
        if (entry != null) {
            EntityTrackerEntryStateHandle stateHandle;
            EntityHandle stateEntity;
            EntityHandle entryEntity = entry.getEntity();
            if (entryEntity != null && entryEntity.getIdField() == oldEntity.getIdField()) {
                entry.setEntity(newEntity);
            }
            if ((stateEntity = (stateHandle = entry.getState()).getEntity()) != null && stateEntity.getIdField() == oldEntity.getIdField() && stateEntity.getRaw() != newEntity.getRaw()) {
                stateHandle.setEntity(newEntity);
            }
            List statePassengers = (List)((Template.Field)EntityTrackerEntryStateHandle.T.opt_passengers.raw).get(stateHandle.getRaw());
            EntityAddRemoveHandler_1_17.replaceInList(statePassengers, oldEntity, newEntity);
        }
    }

    private static boolean replaceInList(List list, EntityHandle oldEntity, EntityHandle newEntity) {
        if (list == null) {
            return false;
        }
        ListIterator<Object> iter = list.listIterator();
        while (iter.hasNext()) {
            int obj_id;
            Object obj = iter.next();
            if (obj instanceof EntityHandle) {
                EntityHandle obj_e = (EntityHandle)obj;
                if (obj_e.getIdField() != oldEntity.getIdField()) continue;
                iter.set(newEntity);
                continue;
            }
            if (!EntityHandle.T.isAssignableFrom(obj) || (obj_id = EntityHandle.T.idField.getInteger(obj)) != oldEntity.getIdField()) continue;
            iter.set(newEntity.getRaw());
        }
        return false;
    }

    private static Class<?> generateLevelCallbackHookType(FastField<Object> callbacksField) {
        try {
            String entityAddedMethodName;
            String entityRemovedMethodName;
            Class<Object> levelCallbackType = callbacksField.getType();
            String levelCallbackDesc = MPLType.getDescriptor(levelCallbackType);
            String bkcCallbackDesc = MPLType.getDescriptor(LevelCallbackHandler.class);
            ExtendedClassWriter cw = ExtendedClassWriter.builder(levelCallbackType).setFlags(1).setExactName(EntityAddRemoveHandler_1_17.class.getName() + "$Hook").build();
            FieldVisitor fv = cw.visitField(17, "base", levelCallbackDesc, null, null);
            fv.visitEnd();
            fv = cw.visitField(17, "callback", bkcCallbackDesc, null, null);
            fv.visitEnd();
            MethodVisitor mv = cw.visitMethod(1, "<init>", "(" + levelCallbackDesc + bkcCallbackDesc + ")V", null, null);
            mv.visitCode();
            mv.visitVarInsn(25, 0);
            mv.visitMethodInsn(183, "java/lang/Object", "<init>", "()V", false);
            mv.visitVarInsn(25, 0);
            mv.visitVarInsn(25, 1);
            mv.visitFieldInsn(181, cw.getInternalName(), "base", levelCallbackDesc);
            mv.visitVarInsn(25, 0);
            mv.visitVarInsn(25, 2);
            mv.visitFieldInsn(181, cw.getInternalName(), "callback", bkcCallbackDesc);
            mv.visitInsn(177);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            if (CommonBootstrap.evaluateMCVersion(">=", "1.18")) {
                entityRemovedMethodName = Resolver.resolveMethodName(levelCallbackType, "onTrackingEnd", new Class[]{Object.class});
                entityAddedMethodName = Resolver.resolveMethodName(levelCallbackType, "onTrackingStart", new Class[]{Object.class});
            } else {
                entityRemovedMethodName = Resolver.resolveMethodName(levelCallbackType, "a", new Class[]{Object.class});
                entityAddedMethodName = Resolver.resolveMethodName(levelCallbackType, "b", new Class[]{Object.class});
            }
            for (Method method : levelCallbackType.getMethods()) {
                String name = MPLType.getName(method);
                Class<?>[] params = method.getParameterTypes();
                String callbackMethodName = name.equals(entityAddedMethodName) && params.length == 1 && params[0] == Object.class ? "onEntityAdded" : (name.equals(entityRemovedMethodName) && params.length == 1 && params[0] == Object.class ? "onEntityRemoved" : null);
                mv = cw.visitMethod(1, MPLType.getName(method), MPLType.getMethodDescriptor(method), null, null);
                mv.visitCode();
                if (callbackMethodName != null) {
                    Method callbackMethod = Stream.of(LevelCallbackHandler.class.getMethods()).filter(m -> m.getName().equals(callbackMethodName)).findFirst().get();
                    mv.visitVarInsn(25, 0);
                    mv.visitFieldInsn(180, cw.getInternalName(), "base", levelCallbackDesc);
                    int reg = MPLType.visitVarILoad(mv, 1, method.getParameterTypes());
                    ExtendedClassWriter.visitInvoke(mv, levelCallbackType, method);
                    if (method.getReturnType() != Void.TYPE) {
                        mv.visitVarInsn(MPLType.getOpcode(method.getReturnType(), 54), reg);
                    }
                    mv.visitVarInsn(25, 0);
                    mv.visitFieldInsn(180, cw.getInternalName(), "callback", bkcCallbackDesc);
                    MPLType.visitVarILoad(mv, 1, method.getParameterTypes());
                    ExtendedClassWriter.visitInvoke(mv, LevelCallbackHandler.class, callbackMethod);
                    if (method.getReturnType() != Void.TYPE) {
                        mv.visitVarInsn(MPLType.getOpcode(method.getReturnType(), 21), reg);
                    }
                } else {
                    mv.visitVarInsn(25, 0);
                    mv.visitFieldInsn(180, cw.getInternalName(), "base", levelCallbackDesc);
                    MPLType.visitVarILoad(mv, 1, method.getParameterTypes());
                    ExtendedClassWriter.visitInvoke(mv, levelCallbackType, method);
                }
                mv.visitInsn(MPLType.getOpcode(method.getReturnType(), 172));
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }
            return cw.generate();
        }
        catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize level hook callback proxy class", t);
            return null;
        }
    }

    @Template.Optional
    @Template.ImportList(value={@Template.Import(value="net.minecraft.server.level.ServerLevel"), @Template.Import(value="net.minecraft.world.level.chunk.LevelChunk"), @Template.Import(value="net.minecraft.world.entity.Entity"), @Template.Import(value="net.minecraft.util.ClassInstanceMultiMap"), @Template.Import(value="net.minecraft.world.level.ChunkPos"), @Template.Import(value="net.minecraft.world.level.entity.EntityLookup")})
    @Template.InstanceType(value="net.minecraft.world.level.entity.PersistentEntitySectionManager")
    public static abstract class AddRemoveHandlerLogic
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static boolean isChunkEntitiesLoaded(ServerLevel world, int cx, int cz) {\n#if version >= 1.17.1\n    #require net.minecraft.server.level.ServerLevel public final net.minecraft.world.level.entity.PersistentEntitySectionManager entityManager;\n#else\n               #require net.minecraft.server.level.ServerLevel private final net.minecraft.world.level.entity.PersistentEntitySectionManager entityManager;\n#endif\n               PersistentEntitySectionManager manager = world#entityManager;\n#if version >= 26.1\n    long key = ChunkPos.pack(cx, cz);\n    return manager.areEntitiesLoaded(key);\n#elseif version >= 1.18\n    long key = ChunkPos.asLong(cx, cz);\n    return manager.areEntitiesLoaded(key);\n#else\n               long key = ChunkPos.pair(cx, cz);\n    return manager.a(key);\n#endif\n           }")
        public abstract boolean isChunkEntitiesLoaded(Object var1, int var2, int var3);

        @Template.Generated(value="public static void replaceInWorldStorage(ServerLevel world, Entity oldEntity, Entity newEntity) {\n    #require net.minecraft.world.entity.Entity private int entityId:id;\n    #require net.minecraft.world.entity.Entity protected UUID entityUUID:uuid;\n    int entityId = oldEntity#entityId;\n    UUID entityUUID = oldEntity#entityUUID;\n\n    #require net.minecraft.server.level.ServerLevel private final net.minecraft.world.level.entity.PersistentEntitySectionManager entityManager;\n    #require net.minecraft.world.level.entity.PersistentEntitySectionManager private final EntityLookup visibleEntityStorage;\n    PersistentEntitySectionManager entitySectionManager = world#entityManager;\n    EntityLookup entityLookup = entitySectionManager#visibleEntityStorage;\n\n    #require net.minecraft.world.level.entity.EntityLookup private final it.unimi.dsi.fastutil.ints.Int2ObjectMap byId;\n    #require net.minecraft.world.level.entity.EntityLookup private final Map byUUID:byUuid;\n    it.unimi.dsi.fastutil.ints.Int2ObjectMap byIdMap = entityLookup#byId;\n    Map byUUIDMap = entityLookup#byUUID;\n\n    if (byIdMap.get(entityId) == oldEntity) {\n        if (newEntity == null) {\n            byIdMap.remove(entityId);\n        } else {\n            byIdMap.put(entityId, newEntity);\n        }\n               }\n    if (byUUIDMap.get(entityUUID) == oldEntity) {\n        if (newEntity == null) {\n            byUUIDMap.remove(entityUUID);\n        } else {\n            byUUIDMap.put(entityUUID, newEntity);\n        }\n               }\n\n    #require net.minecraft.server.level.ServerLevel final net.minecraft.world.level.entity.EntityTickList entityTickList;\n    EntityTickList tickList = world#entityTickList;\n\n#if exists net.minecraft.world.level.entity.EntityTickList private final io.papermc.paper.util.maplist.IteratorSafeOrderedReferenceSet<Entity> entities;\n    // Tuinity was later ported to Paper with a new class package\n    #require net.minecraft.world.level.entity.EntityTickList private final io.papermc.paper.util.maplist.IteratorSafeOrderedReferenceSet<Entity> entities;\n    io.papermc.paper.util.maplist.IteratorSafeOrderedReferenceSet set = tickList#entities;\n    if (set.remove(oldEntity)) {\n        if (newEntity != null) {\n            set.add(newEntity);\n        }\n               }\n#elseif exists net.minecraft.world.level.entity.EntityTickList private final com.tuinity.tuinity.util.maplist.IteratorSafeOrderedReferenceSet<Entity> entities;\n    // Tuinity\n    #require net.minecraft.world.level.entity.EntityTickList private final com.tuinity.tuinity.util.maplist.IteratorSafeOrderedReferenceSet<Entity> entities;\n    com.tuinity.tuinity.util.maplist.IteratorSafeOrderedReferenceSet set = tickList#entities;\n    if (set.remove(oldEntity)) {\n        if (newEntity != null) {\n            set.add(newEntity);\n        }\n               }\n#else\n               // Paper/Spigot\n    #require net.minecraft.world.level.entity.EntityTickList private it.unimi.dsi.fastutil.ints.Int2ObjectMap<Entity> active;\n    #require net.minecraft.world.level.entity.EntityTickList private it.unimi.dsi.fastutil.ints.Int2ObjectMap<Entity> passive;\n    it.unimi.dsi.fastutil.ints.Int2ObjectMap tickListActive = tickList#active;\n    it.unimi.dsi.fastutil.ints.Int2ObjectMap tickListPassive = tickList#passive;\n    if (tickListActive.get(entityId) == oldEntity) {\n        if (newEntity == null) {\n            tickListActive.remove(entityId);\n        } else {\n            tickListActive.put(entityId, newEntity);\n        }\n               }\n    if (tickListPassive.get(entityId) == oldEntity) {\n        if (newEntity == null) {\n            tickListPassive.remove(entityId);\n        } else {\n            tickListPassive.put(entityId, newEntity);\n        }\n               }\n#endif\n\n               // Tuinity, now in paper: entitySliceManager which stores entity lists by region index\n#if exists net.minecraft.world.level.Level protected final io.papermc.paper.world.EntitySliceManager entitySliceManager;\n    #require net.minecraft.world.level.Level protected final io.papermc.paper.world.EntitySliceManager entitySliceManager;\n    io.papermc.paper.world.EntitySliceManager entitySliceManager = world#entitySliceManager;\n\n    // Spigot bug: if chunk doesn't exist, error occurs\n    //entitySliceManager.removeEntity(oldEntity);\n    io.papermc.paper.world.ChunkEntitySlices slices = entitySliceManager.getChunk(oldEntity.sectionX, oldEntity.sectionZ);\n    if (slices != null) {\n        slices.removeEntity(oldEntity, oldEntity.sectionY);\n        if (slices.isEmpty()) {\n            entitySliceManager.removeChunk(oldEntity.sectionX, oldEntity.sectionZ);\n        }\n               }\n\n    // Add new entity (might not be the same chunk)\n    if (newEntity != null) {\n        entitySliceManager.addEntity(newEntity);\n    }\n           #endif\n}")
        public abstract void replaceInWorldStorage(Object var1, Object var2, Object var3);

        @Template.Generated(value="public static void replaceInChunkStorage(Entity oldEntity, Entity newEntity) {\n    // Paper: added an entities field to Chunk\n#if exists net.minecraft.world.level.chunk.LevelChunk public final com.destroystokyo.paper.util.maplist.EntityList entities;\n  #if version >= 1.18\n    net.minecraft.core.BlockPos pos = oldEntity.blockPosition();\n    net.minecraft.world.level.Level world = oldEntity.getLevel();\n  #else\n               net.minecraft.core.BlockPos pos = oldEntity.getChunkCoordinates();\n    net.minecraft.world.level.Level world = oldEntity.getWorld();\n  #endif\n               if (world == null || pos == null) {\n        return;\n    }\n               LevelChunk chunk = ((ServerLevel)world).getChunkIfLoaded(pos.getX() >> 4, pos.getZ() >> 4);\n    if (chunk == null) {\n        return;\n    }\n               if (chunk.entities.remove(oldEntity)) {\n        if (newEntity != null) {\n            chunk.entities.add(newEntity);\n        }\n               }\n#endif\n           }")
        public abstract void replaceInChunkStorage(Object var1, Object var2);

        @Template.Generated(value="public static void replaceInSectionStorage(Entity oldEntity, Entity newEntity) {\n    #require net.minecraft.world.entity.Entity private net.minecraft.world.level.entity.EntityInLevelCallback levelCallback;\n    EntityInLevelCallback callback = oldEntity#levelCallback;\n    if (callback != EntityInLevelCallback.NULL) {\n        if (newEntity == null) {\n#if version >= 1.18\n            callback.onRemove(net.minecraft.world.entity.Entity$RemovalReason.DISCARDED);\n#else\n                       callback.a(net.minecraft.world.entity.Entity$RemovalReason.DISCARDED);\n#endif\n                   } else {\n            #require net.minecraft.world.level.entity.PersistentEntitySectionManager.Callback private final net.minecraft.world.level.entity.EntityAccess entity;\n            if ( callback#entity == oldEntity ) {\n                callback#entity = newEntity;\n            }\n        }\n\n                   #require net.minecraft.world.level.entity.PersistentEntitySectionManager.Callback private EntitySection currentSection;\n#if exists net.minecraft.world.level.entity.PersistentEntitySectionManager.Callback private PersistentEntitySectionManager b;\n        #require net.minecraft.world.level.entity.PersistentEntitySectionManager.Callback private PersistentEntitySectionManager callbackPESM:b;\n#else\n                   #require net.minecraft.world.level.entity.PersistentEntitySectionManager.Callback private PersistentEntitySectionManager callbackPESM:this$0;\n#endif\n                   net.minecraft.world.level.entity.EntitySection section = callback#currentSection;\n\n        boolean checkStartTickingNewEntity = false;\n\n        #require net.minecraft.world.level.entity.EntitySection private final net.minecraft.util.ClassInstanceMultiMap storage;\n        ClassInstanceMultiMap slice = section#storage;\n        List sliceList = com.bergerkiller.bukkit.common.conversion.type.HandleConversion.cbEntitySliceToList(slice);\n        java.util.ListIterator iter = sliceList.listIterator();\n        while (iter.hasNext()) {\n            if (iter.next() == oldEntity) {\n                if (newEntity == null) {\n                    iter.remove();\n                } else {\n                    iter.set(newEntity);\n                    checkStartTickingNewEntity = true;\n                }\n            }\n        }\n\n                   // If isAlwaysTicking() of the old and new entity differs, we may have to stop/start ticking ourselves\n        // This is because of a bug in the persistent entity section manager that, if isAlwaysTicking() is true,\n        // the updateStatus function does not work anymore to update this state.\n        if (checkStartTickingNewEntity) {\n#if version >= 1.18\n            boolean wasAlwaysTicking = oldEntity.isAlwaysTicking();\n            boolean isAlwaysTicking = newEntity.isAlwaysTicking();\n            boolean sectionTicking = section.getStatus().isTicking();\n#else\n                       boolean wasAlwaysTicking = oldEntity.dn();\n            boolean isAlwaysTicking = newEntity.dn();\n            boolean sectionTicking = section.c().a();\n#endif\n\n                       // Start ticking when section is not ticking, and we go from not always ticking\n            // to always ticking. This is because this 'load' trigger already fired, and so it\n            // presumes startTicking() was already performed.\n            if (isAlwaysTicking && !wasAlwaysTicking && !sectionTicking) {\n#if version >= 1.18\n                #require net.minecraft.world.level.entity.PersistentEntitySectionManager void pesmStartTicking:startTicking(T t0);\n#else\n                           #require net.minecraft.world.level.entity.PersistentEntitySectionManager void pesmStartTicking:c(T t0);\n#endif\n                           PersistentEntitySectionManager pesm = callback#callbackPESM;\n                pesm#pesmStartTicking(newEntity);\n            }\n\n            // Stop ticking when section is not ticking, and we go from always ticking to\n            // not always ticking. For same reason as before.\n            // TODO: Implement this? No use case as of right now.\n            //if (!isAlwaysTicking && wasAlwaysTicking && !sectionTicking) {\n            //}\n        }\n               }\n}")
        public abstract void replaceInSectionStorage(Object var1, Object var2);
    }

    private static class ChunkEntitiesLoadedUsingEventHandler
    implements ChunkEntitiesLoadedHandler,
    Listener {
        private ChunkEntitiesLoadedUsingEventHandler() {
        }

        @Override
        public void enable(final EntityAddRemoveHandler_1_17 handler, CommonPlugin plugin) {
            Class entitiesLoadEventType = (Class)LogicUtil.unsafeCast(CommonUtil.getClass("org.bukkit.event.world.EntitiesLoadEvent"));
            Bukkit.getPluginManager().registerEvent(entitiesLoadEventType, (Listener)this, EventPriority.LOWEST, new EventExecutor(){
                final /* synthetic */ ChunkEntitiesLoadedUsingEventHandler this$0;
                {
                    this.this$0 = this$0;
                }

                public void execute(Listener listener, Event event) throws EventException {
                    this.this$0.onEntitiesLoaded(handler, (ChunkEvent)event);
                }
            }, (Plugin)plugin);
            Class entitiesUnloadEventType = (Class)LogicUtil.unsafeCast(CommonUtil.getClass("org.bukkit.event.world.EntitiesUnloadEvent"));
            Bukkit.getPluginManager().registerEvent(entitiesUnloadEventType, (Listener)this, EventPriority.LOWEST, new EventExecutor(){
                final /* synthetic */ ChunkEntitiesLoadedUsingEventHandler this$0;
                {
                    this.this$0 = this$0;
                }

                public void execute(Listener listener, Event event) throws EventException {
                    this.this$0.onEntitiesUnloaded(handler, (ChunkEvent)event);
                }
            }, (Plugin)plugin);
        }

        @Override
        public void hook(EntityAddRemoveHandler_1_17 handler, World world, Object sectionManager) {
        }

        @Override
        public void unhook(EntityAddRemoveHandler_1_17 handler, World world, Object sectionManager) {
        }

        private void onEntitiesLoaded(EntityAddRemoveHandler_1_17 handler, ChunkEvent event) {
            handler.notifyChunkEntitiesLoaded(event.getChunk());
        }

        private void onEntitiesUnloaded(EntityAddRemoveHandler_1_17 handler, ChunkEvent event) {
            handler.notifyChunkEntitiesUnloaded(event.getChunk());
        }
    }

    private static interface ChunkEntitiesLoadedHandler {
        public void enable(EntityAddRemoveHandler_1_17 var1, CommonPlugin var2);

        public void hook(EntityAddRemoveHandler_1_17 var1, World var2, Object var3);

        public void unhook(EntityAddRemoveHandler_1_17 var1, World var2, Object var3);
    }

    private static class ChunkEntitiesLoadedUsingHookHandler
    implements ChunkEntitiesLoadedHandler {
        private final FastField<Object> chunkLoadStatusesField = new FastField();

        public ChunkEntitiesLoadedUsingHookHandler(Class<?> sectionManagerClass) {
            try {
                String fieldName = Resolver.resolveFieldName(sectionManagerClass, "chunkLoadStatuses");
                this.chunkLoadStatusesField.init(sectionManagerClass.getDeclaredField(fieldName));
                if (!CommonUtil.getClass("it.unimi.dsi.fastutil.longs.Long2ObjectMap").equals(this.chunkLoadStatusesField.getType())) {
                    throw new IllegalStateException("Field not assignable to Long2ObjectMap");
                }
            }
            catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize PersistentEntitySectionManager chunkLoadStatuses field: " + t.getMessage(), t);
                this.chunkLoadStatusesField.initUnavailable("chunkLoadStatuses field not found");
            }
        }

        @Override
        public void enable(final EntityAddRemoveHandler_1_17 handler, CommonPlugin plugin) {
            plugin.register(new Listener(){
                final /* synthetic */ ChunkEntitiesLoadedUsingHookHandler this$0;
                {
                    this.this$0 = this$0;
                }

                @EventHandler(priority=EventPriority.LOWEST)
                public void onChunkUnload(ChunkUnloadEvent event) {
                    handler.notifyChunkEntitiesUnloaded(event.getChunk());
                }
            });
        }

        @Override
        public void hook(EntityAddRemoveHandler_1_17 handler, World world, Object sectionManager) {
            Object chunkLoadStatuses = this.chunkLoadStatusesField.get(sectionManager);
            if (ClassHook.get(chunkLoadStatuses, ChunkLoadStatusHook.class) == null) {
                ChunkLoadStatusHook hook = new ChunkLoadStatusHook(handler, world);
                Object hooked = hook.hook(chunkLoadStatuses);
                ChunkEntitiesLoadedUsingHookHandler.swapValueRecursive(hooked, chunkLoadStatuses, hooked, 4);
                this.chunkLoadStatusesField.set(sectionManager, hooked);
            }
        }

        @Override
        public void unhook(EntityAddRemoveHandler_1_17 handler, World world, Object sectionManager) {
            Object chunkLoadStatuses = this.chunkLoadStatusesField.get(sectionManager);
            if (ClassHook.get(chunkLoadStatuses, ChunkLoadStatusHook.class) != null) {
                Object base = ClassHook.unhook(chunkLoadStatuses);
                ChunkEntitiesLoadedUsingHookHandler.swapValueRecursive(base, chunkLoadStatuses, base, 4);
                this.chunkLoadStatusesField.set(sectionManager, base);
            }
        }

        private static void swapValueRecursive(Object start, Object expected, Object replacement, int depthLimit) {
            if (depthLimit <= 0) {
                return;
            }
            ReflectionUtil.getAllClasses(start.getClass()).filter(c -> c.getName().contains("it.unimi.dsi.fastutil")).flatMap(c -> Stream.of(c.getDeclaredFields())).filter(f -> !Modifier.isStatic(f.getModifiers())).filter(f -> {
                Class<?> type = f.getType();
                return !type.isPrimitive() && !type.isArray();
            }).map(f -> {
                f.setAccessible(true);
                try {
                    Object currentValue = f.get(start);
                    if (currentValue != expected) {
                        return currentValue;
                    }
                    f.set(start, replacement);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                return null;
            }).filter(Objects::nonNull).forEachOrdered(object -> ChunkEntitiesLoadedUsingHookHandler.swapValueRecursive(object, expected, replacement, depthLimit - 1));
        }

        public static class ChunkLoadStatusHook
        extends ClassHook<ChunkLoadStatusHook> {
            private final EntityAddRemoveHandler_1_17 handler;
            private final World world;

            public ChunkLoadStatusHook(EntityAddRemoveHandler_1_17 handler, World world) {
                this.handler = handler;
                this.world = world;
            }

            @ClassHook.HookMethod(value="public V put(long key, V value)")
            public void onPut(Object rawKey, Object value) {
                int cz;
                long key;
                int cx;
                Chunk chunk;
                ((ChunkLoadStatusHook)this.base).onPut(rawKey, value);
                if (value.toString().equals("LOADED") && (chunk = WorldUtil.getChunk(this.world, cx = (int)((key = ((Long)rawKey).longValue()) & 0xFFFFFFFFL), cz = (int)(key >>> 32 & 0xFFFFFFFFL))) != null) {
                    this.handler.notifyChunkEntitiesLoaded(chunk);
                }
            }
        }
    }

    public static class LevelCallbackHandler {
        private final EntityAddRemoveHandler_1_17 handler;
        private final World world;
        private final Queue<Entity> pendingAddEvents = new LinkedList<Entity>();

        public LevelCallbackHandler(EntityAddRemoveHandler_1_17 handler, World world) {
            this.handler = handler;
            this.world = world;
        }

        public void onEntityRemoved(Object entityHandle) {
            Entity entity = WrapperConversion.toEntity(entityHandle);
            this.pendingAddEvents.remove(entity);
            this.handler.notifyRemoved(this.world, entity);
        }

        public void onEntityAdded(Object entityHandle) {
            Entity entity = WrapperConversion.toEntity(entityHandle);
            this.pendingAddEvents.add(entity);
            this.handler.notifyAddedEarly(this.world, entity);
        }

        public void processEvents() {
            while (!this.pendingAddEvents.isEmpty()) {
                CommonPlugin.getInstance().notifyAdded(this.world, this.pendingAddEvents.poll());
            }
        }
    }
}

