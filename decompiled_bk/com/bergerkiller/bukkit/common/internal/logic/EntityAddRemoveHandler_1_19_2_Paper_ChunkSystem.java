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
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.world.ChunkEvent
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
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

class EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem
extends EntityAddRemoveHandler {
    private final Class<?> levelCallbackType;
    private final FastMethod<Object> getEntityLookupMethod = new FastMethod();
    private final FastField<Object> callbacksField = new FastField();
    private final Map<World, LevelCallbackHandler> hooks = new ConcurrentHashMap<World, LevelCallbackHandler>();
    private final AddRemoveHandlerLogic removeHandler;
    private final ChunkEntitiesLoadedHandler chunkEntitiesLoadedHandler;
    private final Class<?> levelCallbackHookType;

    public EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem() {
        Class<?> entityLookupClass;
        this.levelCallbackType = Resolver.loadClass("net.minecraft.world.level.entity.LevelCallback", false);
        if (this.levelCallbackType == null) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to find LevelCallback class");
        }
        if ((entityLookupClass = Resolver.loadClass("io.papermc.paper.chunk.system.entity.EntityLookup", false)) == null) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to find EntityLookup class");
        }
        try {
            this.getEntityLookupMethod.init(ServerLevelHandle.T.getType().getDeclaredMethod("getEntityLookup", new Class[0]));
            if (!entityLookupClass.isAssignableFrom(this.getEntityLookupMethod.getMethod().getReturnType())) {
                throw new IllegalStateException("Return type of getEntityLookup not assignable to EntityLookup");
            }
            this.getEntityLookupMethod.forceInitialization();
        }
        catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize WorldServer getEntityLookup method: " + t.getMessage(), t);
            this.getEntityLookupMethod.initUnavailable("getEntityLookup");
        }
        try {
            this.callbacksField.init(entityLookupClass.getDeclaredField("worldCallback"));
            if (!this.levelCallbackType.equals(this.callbacksField.getType())) {
                throw new IllegalStateException("Field not assignable to LevelCallback");
            }
            this.callbacksField.forceInitialization();
        }
        catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize EntityLookup worldCallback field: " + t.getMessage(), t);
            this.callbacksField.initUnavailable("worldCallback field not found");
        }
        this.removeHandler = Template.Class.create(AddRemoveHandlerLogic.class, Common.TEMPLATE_RESOLVER);
        this.levelCallbackHookType = EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem.generateLevelCallbackHookType(this.callbacksField);
        this.chunkEntitiesLoadedHandler = new ChunkEntitiesLoadedUsingEventHandler();
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public void forceInitialization() {
        this.getEntityLookupMethod.forceInitialization();
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
        Object sectionManager = this.getEntityLookupMethod.invoke(HandleConversion.toWorldHandle(world));
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
        Object sectionManager = this.getEntityLookupMethod.invoke(HandleConversion.toWorldHandle(world));
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
            EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem.replaceInEntityTracker(oldEntity, oldEntity, newEntity);
            if (oldEntity.getVehicle() != null) {
                EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem.replaceInEntityTracker(oldEntity.getVehicle(), oldEntity, newEntity);
            }
            if (oldEntity.getPassengers() != null) {
                for (EntityHandle passenger : oldEntity.getPassengers()) {
                    EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem.replaceInEntityTracker(passenger, oldEntity, newEntity);
                }
            }
        }
        Object newEntityRaw = Template.Handle.getRaw(newEntity);
        if (world != null) {
            this.removeHandler.replaceInWorldStorage(world.getRaw(), oldEntity.getRaw(), newEntityRaw);
        }
        this.removeHandler.replaceInSectionStorage(oldEntity.getRaw(), newEntityRaw);
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
            EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem.replaceInList(statePassengers, oldEntity, newEntity);
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
            ExtendedClassWriter cw = ExtendedClassWriter.builder(levelCallbackType).setFlags(1).setExactName(EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem.class.getName() + "$Hook").build();
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
    @Template.ImportList(value={@Template.Import(value="net.minecraft.server.level.ServerLevel"), @Template.Import(value="net.minecraft.world.entity.Entity"), @Template.Import(value="net.minecraft.world.level.entity.Visibility"), @Template.Import(value="net.minecraft.world.level.ChunkPos"), @Template.Import(value="net.minecraft.core.BlockPos"), @Template.Import(value="net.minecraft.world.entity.Visibility"), @Template.Import(value="java.util.concurrent.locks.StampedLock"), @Template.Import(value="it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap"), @Template.Import(value="it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap"), @Template.Import(value="it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap"), @Template.Import(value="net.minecraft.world.level.entity.EntityTickList"), @Template.Import(value="io.papermc.paper.world.ChunkEntitySlices")})
    @Template.InstanceType(value="io.papermc.paper.chunk.system.entity.EntityLookup")
    public static abstract class AddRemoveHandlerLogic
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static boolean isChunkEntitiesLoaded(ServerLevel world, int cx, int cz) {\n    // Just checks that the chunk is loaded, really\n    return world.areEntitiesLoaded(ChunkPos.asLong(cx, cz));\n}")
        public abstract boolean isChunkEntitiesLoaded(Object var1, int var2, int var3);

        @Template.Generated(value="public static void replaceInWorldStorage(ServerLevel world, Entity oldEntity, Entity newEntity) {\n    #require net.minecraft.world.entity.Entity private int entityId:id;\n    #require net.minecraft.world.entity.Entity protected UUID entityUUID:uuid;\n    int entityId = oldEntity#entityId;\n    UUID entityUUID = oldEntity#entityUUID;\n\n    EntityLookup entityLookup = world.getEntityLookup();\n\n    // Make sure we have the entityByLock lock locked while modifying this data\n    // Swap the entity in the by-id and by-uuid maps\n    #require EntityLookup private final StampedLock entityByLock;\n    StampedLock entityByLock = entityLookup#entityByLock;\n    entityByLock.writeLock();\n    try {\n                   #require EntityLookup private final Int2ReferenceOpenHashMap<Entity> entityById;\n        Int2ReferenceOpenHashMap byIdMap = entityLookup#entityById;\n        if (byIdMap.get(entityId) == oldEntity) {\n            if (newEntity == null) {\n                byIdMap.remove(entityId);\n            } else {\n                byIdMap.put(entityId, newEntity);\n            }\n        }\n\n                   #require EntityLookup private final Object2ReferenceOpenHashMap<UUID, Entity> entityByUUID;\n        Object2ReferenceOpenHashMap byUUIDMap = entityLookup#entityByUUID;\n        if (byUUIDMap.get(entityUUID) == oldEntity) {\n            if (newEntity == null) {\n                byUUIDMap.remove(entityUUID);\n            } else {\n                byUUIDMap.put(entityUUID, newEntity);\n            }\n        }\n               } finally {\n        entityByLock.tryUnlockWrite();\n    }\n\n               #require net.minecraft.server.level.ServerLevel final net.minecraft.world.level.entity.EntityTickList entityTickList;\n    net.minecraft.world.level.entity.EntityTickList tickList = world#entityTickList;\n\n    #require net.minecraft.world.level.entity.EntityTickList private final io.papermc.paper.util.maplist.IteratorSafeOrderedReferenceSet<Entity> entities;\n    io.papermc.paper.util.maplist.IteratorSafeOrderedReferenceSet set = tickList#entities;\n    if (set.remove(oldEntity)) {\n        if (newEntity != null) {\n            set.add(newEntity);\n        }\n               }\n\n    #require EntityLookup private final int minSection;\n    #require EntityLookup private final int maxSection;\n    final int minSection = entityLookup#minSection;\n    final int maxSection = entityLookup#maxSection;\n\n    // First check whether the new entity is already stored. If so, no ticking mode changes\n    boolean isNewEntityStored = false;\n    if (newEntity != null) {\n        final BlockPos pos = newEntity.blockPosition();\n        final int sectionX = pos.getX() >> 4;\n        final int sectionY = net.minecraft.util.Mth.clamp(pos.getY() >> 4, minSection, maxSection);\n        final int sectionZ = pos.getZ() >> 4;\n\n        // Runs just the chunk-adding logic. ID/UUID is done earlier.\n        newEntity.sectionX = sectionX;\n        newEntity.sectionY = sectionY;\n        newEntity.sectionZ = sectionZ;\n\n        ChunkEntitySlices slices = entityLookup.getChunk(sectionX, sectionZ);\n        if (slices != null) {\n            #require io.papermc.paper.world.ChunkEntitySlices private java.util.List<net.minecraft.world.entity.Entity> getAllEntities();\n            java.util.List allEntities = slices#getAllEntities();\n            java.util.Iterator iter = allEntities.iterator();\n            while (iter.hasNext()) {\n                if (iter.next() == newEntity) {\n                    isNewEntityStored = true;\n                    break;\n                }\n            }\n        }\n               }\n\n    // bug: if chunk doesn't exist, error occurs\n    //entitySliceManager.removeEntity(oldEntity);\n    io.papermc.paper.world.ChunkEntitySlices slices = entityLookup.getChunk(oldEntity.sectionX, oldEntity.sectionZ);\n    if (slices != null) {\n        slices.removeEntity(oldEntity, oldEntity.sectionY);\n        if (slices.isEmpty()) {\n            //TODO: Not done in the server now. Bug?\n            //entityLookup.removeChunk(oldEntity.sectionX, oldEntity.sectionZ);\n        }\n               }\n\n    // Add new entity (might not be the same chunk)\n    // Note: we cannot call addEntity as this initializes the tracker/other logic/events\n    ChunkEntitySlices sectionOfEntity = null;\n    if (newEntity != null) {\n        final BlockPos pos = newEntity.blockPosition();\n        final int sectionX = pos.getX() >> 4;\n        final int sectionY = net.minecraft.util.Mth.clamp(pos.getY() >> 4, minSection, maxSection);\n        final int sectionZ = pos.getZ() >> 4;\n\n        // Runs just the chunk-adding logic. ID/UUID is done earlier.\n        newEntity.sectionX = sectionX;\n        newEntity.sectionY = sectionY;\n        newEntity.sectionZ = sectionZ;\n\n        sectionOfEntity = entityLookup.getOrCreateChunk(sectionX, sectionZ);\n        sectionOfEntity.addEntity(newEntity, sectionY);\n    }\n\n               // Update the \"all entities\" list\n    #require EntityLookup private final com.destroystokyo.paper.util.maplist.EntityList accessibleEntities;\n    com.destroystokyo.paper.util.maplist.EntityList lookupAccEntities = entityLookup#accessibleEntities;\n    if (newEntity == null) {\n        lookupAccEntities.remove(oldEntity);\n    } else {\n        // Swap the Entity in the internal array\n        #require com.destroystokyo.paper.util.maplist.EntityList protected final Int2IntOpenHashMap entityListEntityToIndex:entityToIndex;\n        #require com.destroystokyo.paper.util.maplist.EntityList protected Entity[] entityListEntities:entities;\n        Int2IntOpenHashMap el_entityToIndex = lookupAccEntities#entityListEntityToIndex;\n        Entity[] el_entities = lookupAccEntities#entityListEntities;\n        int index = el_entityToIndex.get(newEntity.getId());\n        if (index >= 0 && index < el_entities.length) {\n            el_entities[index] = newEntity;\n        }\n               }\n\n    // If isAlwaysTicking() of the old and new entity differs, we may have to stop/start ticking ourselves\n    // This is because of a bug in the persistent entity section manager that, if isAlwaysTicking() is true,\n    // the updateStatus function does not work anymore to update this state.\n    // Only do this when the entity is first replaced. Not the second time around.\n    if (!isNewEntityStored && sectionOfEntity != null) {\n        boolean wasTicking = EntityLookup.getEntityStatus(oldEntity).isTicking();\n        boolean isAlwaysTicking = newEntity.isAlwaysTicking();\n\n        // Start ticking when section is not ticking, and we go from not always ticking\n        // to always ticking. This is because this 'load' trigger already fired, and so it\n        // presumes startTicking() was already performed.\n        if (isAlwaysTicking && !wasTicking) {\n            // Force a status change from TRACKED to TICKING, which starts ticking the entity\n            // Do not cause a change from a visibility below TRACKED, that will break things\n            entityLookup.entityStatusChange(newEntity, sectionOfEntity, Visibility.TRACKED, Visibility.TICKING,\n                     false, true, false);\n        }\n               }\n}")
        public abstract void replaceInWorldStorage(Object var1, Object var2, Object var3);

        @Template.Generated(value="public static void replaceInSectionStorage(Entity oldEntity, Entity newEntity) {\n    #require net.minecraft.world.entity.Entity private net.minecraft.world.level.entity.EntityInLevelCallback levelCallback;\n    net.minecraft.world.level.entity.EntityInLevelCallback callback = oldEntity#levelCallback;\n    if (callback != net.minecraft.world.level.entity.EntityInLevelCallback.NULL) {\n        if (newEntity == null) {\n            callback.onRemove(net.minecraft.world.entity.Entity$RemovalReason.DISCARDED);\n        } else {\n            #require EntityLookup.EntityCallback public final Entity entity;\n            if ( callback#entity == oldEntity ) {\n                callback#entity = newEntity;\n            }\n        }\n               }\n}")
        public abstract void replaceInSectionStorage(Object var1, Object var2);
    }

    private static class ChunkEntitiesLoadedUsingEventHandler
    implements ChunkEntitiesLoadedHandler,
    Listener {
        private ChunkEntitiesLoadedUsingEventHandler() {
        }

        @Override
        public void enable(final EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem handler, CommonPlugin plugin) {
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
        public void hook(EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem handler, World world, Object sectionManager) {
        }

        @Override
        public void unhook(EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem handler, World world, Object sectionManager) {
        }

        private void onEntitiesLoaded(EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem handler, ChunkEvent event) {
            handler.notifyChunkEntitiesLoaded(event.getChunk());
        }

        private void onEntitiesUnloaded(EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem handler, ChunkEvent event) {
            handler.notifyChunkEntitiesUnloaded(event.getChunk());
        }
    }

    private static interface ChunkEntitiesLoadedHandler {
        public void enable(EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem var1, CommonPlugin var2);

        public void hook(EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem var1, World var2, Object var3);

        public void unhook(EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem var1, World var2, Object var3);
    }

    public static class LevelCallbackHandler {
        private final EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem handler;
        private final World world;
        private final Queue<Entity> pendingAddEvents = new LinkedList<Entity>();

        public LevelCallbackHandler(EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem handler, World world) {
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

