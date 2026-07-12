/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.world.ChunkLoadEvent
 *  org.bukkit.event.world.ChunkUnloadEvent
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.server.level.ChunkHolderHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ChunkMapHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.util.IntHashMapHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

class EntityAddRemoveHandler_1_14_to_1_16_5
extends EntityAddRemoveHandler {
    private final FastField<?> entitiesByIdField = new FastField();
    private final FastField<Map<UUID, Object>> entitiesByUUIDField = new FastField();
    private final SafeField<Queue<Object>> entitiesToAddField;
    private final Map<World, EntitiesByUUIDMapHook> hooks = new ConcurrentHashMap<World, EntitiesByUUIDMapHook>();
    private final FastMethod<Object> tuinitySwapEntityInWorldEntityListMethod = new FastMethod();
    private final FastMethod<Object> tuinitySwapEntityInWorldEntityIterationSetMethod = new FastMethod();
    private final EntityAddRemoveHandler.ChunkEntitySliceHandler chunkEntitySliceHandler;
    private final AddRemoveHandlerLogic addRemoveHandler = Template.Class.create(AddRemoveHandlerLogic.class, Common.TEMPLATE_RESOLVER);

    public EntityAddRemoveHandler_1_14_to_1_16_5() {
        ClassResolver resolver;
        String fieldName;
        try {
            fieldName = Resolver.resolveFieldName(ServerLevelHandle.T.getType(), "entitiesById");
            this.entitiesByIdField.init(MPLType.getDeclaredField(ServerLevelHandle.T.getType(), fieldName));
            if (!IntHashMapHandle.T.isAssignableFrom(this.entitiesByIdField.getType())) {
                throw new IllegalStateException("Field not assignable to IntHashmap");
            }
        }
        catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.WARNING, "Failed to initialize ServerLevel entitiesById field: " + t.getMessage(), t);
            this.entitiesByIdField.initUnavailable("entitiesById");
        }
        try {
            fieldName = Resolver.resolveFieldName(ServerLevelHandle.T.getType(), "entitiesByUUID");
            this.entitiesByUUIDField.init(MPLType.getDeclaredField(ServerLevelHandle.T.getType(), fieldName));
            if (!Map.class.isAssignableFrom(this.entitiesByUUIDField.getType())) {
                throw new IllegalStateException("Field not assignable to Map");
            }
        }
        catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.WARNING, "Failed to initialize ServerLevel entitiesByUUID field: " + t.getMessage(), t);
            this.entitiesByUUIDField.initUnavailable("entitiesByUUID");
        }
        fieldName = Resolver.resolveFieldName(ServerLevelHandle.T.getType(), "entitiesToAdd");
        this.entitiesToAddField = (SafeField)LogicUtil.unsafeCast(SafeField.create(ServerLevelHandle.T.getType(), fieldName, Queue.class));
        try {
            Class<?> entityListType = Class.forName("com.tuinity.tuinity.util.EntityList");
            if (SafeField.contains(ServerLevelHandle.T.getType(), "loadedEntities", entityListType)) {
                resolver = new ClassResolver();
                resolver.addImport("net.minecraft.world.entity.Entity");
                resolver.setDeclaredClassName("net.minecraft.server.level.ServerLevel");
                this.tuinitySwapEntityInWorldEntityListMethod.init(new MethodDeclaration(resolver, "public void swap(Entity oldEntity, Entity newEntity) {\n    if (instance.loadedEntities.remove(oldEntity)) {\n        instance.loadedEntities.add(newEntity);\n    }\n}"));
            }
        }
        catch (ClassNotFoundException entityListType) {
            // empty catch block
        }
        try {
            Class<?> entitySetType = Class.forName("com.tuinity.tuinity.util.maplist.IteratorSafeOrderedReferenceSet");
            if (SafeField.contains(ServerLevelHandle.T.getType(), "entitiesForIteration", entitySetType)) {
                resolver = new ClassResolver();
                resolver.addImport("net.minecraft.world.entity.Entity");
                resolver.setDeclaredClassName("net.minecraft.server.level.ServerLevel");
                this.tuinitySwapEntityInWorldEntityIterationSetMethod.init(new MethodDeclaration(resolver, "public void swap(Entity oldEntity, Entity newEntity) {\n    #require net.minecraft.server.level.ServerLevel final com.tuinity.tuinity.util.maplist.IteratorSafeOrderedReferenceSet<net.minecraft.world.entity.Entity> entitiesForIteration;\n    com.tuinity.tuinity.util.maplist.IteratorSafeOrderedReferenceSet set = instance#entitiesForIteration;\n    if (set.remove(oldEntity)) {\n        set.add(newEntity);\n    }\n}"));
            }
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        this.chunkEntitySliceHandler = new EntityAddRemoveHandler.ChunkEntitySliceHandler();
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public void forceInitialization() {
        this.addRemoveHandler.forceInitialization();
        this.entitiesByIdField.forceInitialization();
        this.entitiesByUUIDField.forceInitialization();
        if (this.tuinitySwapEntityInWorldEntityListMethod.isAvailable()) {
            this.tuinitySwapEntityInWorldEntityListMethod.forceInitialization();
            this.tuinitySwapEntityInWorldEntityIterationSetMethod.forceInitialization();
        }
    }

    @Override
    public void onEnabled(CommonPlugin plugin) {
        super.onEnabled(plugin);
        plugin.register(new Listener(){

            @EventHandler(priority=EventPriority.LOWEST)
            public void onChunkLoad(ChunkLoadEvent event) {
                EntityAddRemoveHandler_1_14_to_1_16_5.this.notifyChunkEntitiesLoaded(event.getChunk());
            }

            @EventHandler(priority=EventPriority.LOWEST)
            public void onChunkUnload(ChunkUnloadEvent event) {
                EntityAddRemoveHandler_1_14_to_1_16_5.this.notifyChunkEntitiesUnloaded(event.getChunk());
            }
        });
    }

    @Override
    protected void hook(World world) {
        Object nmsWorldHandle = LevelHandle.fromBukkit(world).getRaw();
        Map<UUID, Object> base = this.entitiesByUUIDField.get(nmsWorldHandle);
        if (!(base instanceof EntitiesByUUIDMapHook)) {
            EntitiesByUUIDMapHook hook = new EntitiesByUUIDMapHook(this, world, base);
            this.entitiesByUUIDField.set(nmsWorldHandle, hook);
            this.hooks.put(world, hook);
        }
    }

    @Override
    protected void unhook(World world) {
        Object nmsWorldHandle = LevelHandle.fromBukkit(world).getRaw();
        Map<UUID, Object> value = this.entitiesByUUIDField.get(nmsWorldHandle);
        if (value instanceof EntitiesByUUIDMapHook) {
            this.entitiesByUUIDField.set(nmsWorldHandle, ((EntitiesByUUIDMapHook)value).getBase());
            this.hooks.remove(world, value);
        }
    }

    @Override
    public void processEvents(World world) {
        EntitiesByUUIDMapHook hook = this.hooks.get(world);
        if (hook != null) {
            hook.processEvents();
        }
    }

    @Override
    public boolean isChunkEntitiesLoaded(World world, int cx, int cz) {
        return WorldUtil.isLoaded(world, cx, cz);
    }

    @Override
    public boolean isChunkEntitiesLoaded(Chunk chunk) {
        return chunk.isLoaded();
    }

    @Override
    public void replace(EntityHandle oldEntity, EntityHandle newEntity) {
        Object entitiesById;
        ServerLevelHandle world = oldEntity.getWorldServer();
        if (newEntity == null) {
            if (world != null) {
                world.removeEntity(oldEntity);
                world.getEntityTracker().stopTracking(oldEntity.getBukkitEntity());
            }
            return;
        }
        Object worldHandle = world.getRaw();
        Queue<Object> entitiesToAdd = this.entitiesToAddField.get(oldEntity.getWorld().getRaw());
        entitiesToAdd.remove(oldEntity.getRaw());
        Map<UUID, Object> entitiesByUUID = this.entitiesByUUIDField.get(worldHandle);
        Object storedEntityHandle = entitiesByUUID.get(oldEntity.getUniqueID());
        if (storedEntityHandle != null && storedEntityHandle != newEntity.getRaw()) {
            if (!oldEntity.getUniqueID().equals(newEntity.getUniqueID())) {
                entitiesByUUID.remove(oldEntity.getUniqueID());
            }
            entitiesByUUID.put(newEntity.getUniqueID(), newEntity.getRaw());
        }
        if ((storedEntityHandle = ((IntHashMapHandle)(entitiesById = IntHashMapHandle.createHandle(this.entitiesByIdField.get(worldHandle)))).get(oldEntity.getIdField())) != null && storedEntityHandle != newEntity.getRaw()) {
            if (oldEntity.getIdField() != newEntity.getIdField()) {
                ((IntHashMapHandle)entitiesById).remove(oldEntity.getIdField());
            }
            ((IntHashMapHandle)entitiesById).put(newEntity.getIdField(), newEntity.getRaw());
        }
        if (this.tuinitySwapEntityInWorldEntityListMethod.isAvailable()) {
            this.tuinitySwapEntityInWorldEntityListMethod.invoke(worldHandle, oldEntity.getRaw(), newEntity.getRaw());
        }
        if (this.tuinitySwapEntityInWorldEntityIterationSetMethod.isAvailable()) {
            this.tuinitySwapEntityInWorldEntityIterationSetMethod.invoke(worldHandle, oldEntity.getRaw(), newEntity.getRaw());
        }
        EntityAddRemoveHandler_1_14_to_1_16_5.replaceInEntityTracker(oldEntity, oldEntity, newEntity);
        if (oldEntity.getVehicle() != null) {
            EntityAddRemoveHandler_1_14_to_1_16_5.replaceInEntityTracker(oldEntity.getVehicle(), oldEntity, newEntity);
        }
        if (oldEntity.getPassengers() != null) {
            for (EntityHandle passenger : oldEntity.getPassengers()) {
                EntityAddRemoveHandler_1_14_to_1_16_5.replaceInEntityTracker(passenger, oldEntity, newEntity);
            }
        }
        int chunkX = newEntity.getChunkX();
        int chunkZ = newEntity.getChunkZ();
        ChunkMapHandle playerChunks = ServerLevelHandle.T.getPlayerChunkMap.invoke(worldHandle);
        Chunk loadedChunk = WorldUtil.getChunk(newEntity.getBukkitWorld(), chunkX, chunkZ);
        if (loadedChunk != null) {
            this.replaceInChunk(loadedChunk, oldEntity, newEntity);
        } else {
            Chunk loadedUpdatingChunk;
            ChunkHolderHandle updatingChunk = playerChunks.getUpdatingChunk(chunkX, chunkZ);
            Chunk chunk = loadedUpdatingChunk = updatingChunk == null ? null : updatingChunk.getChunkIfLoaded();
            if (loadedUpdatingChunk == null && updatingChunk != null) {
                loadedUpdatingChunk = this.addRemoveHandler.getChunkTryHard(updatingChunk.getRaw());
            }
            this.replaceInChunk(loadedUpdatingChunk, oldEntity, newEntity);
        }
    }

    @Override
    public void moveToChunk(EntityHandle entity) {
        this.chunkEntitySliceHandler.moveToChunk(entity);
    }

    private void replaceInChunk(Chunk chunk, EntityHandle oldEntity, EntityHandle newEntity) {
        Object chunkHandle = HandleConversion.toChunkHandle(chunk);
        if (chunkHandle != null) {
            this.chunkEntitySliceHandler.replace(chunkHandle, oldEntity, newEntity);
        }
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
            EntityAddRemoveHandler_1_14_to_1_16_5.replaceInList(statePassengers, oldEntity, newEntity);
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

    @Template.Optional
    @Template.Import(value="net.minecraft.world.level.chunk.LevelChunk")
    public static abstract class AddRemoveHandlerLogic
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static org.bukkit.Chunk getChunkTryHard(net.minecraft.server.level.ChunkHolder playerChunk) {\n    #require net.minecraft.server.level.ChunkHolder private static final java.util.List<net.minecraft.world.level.chunk.status.ChunkStatus> CHUNK_STATUSES;\n    java.util.List chunk_statuses = ChunkHolder#CHUNK_STATUSES;\n    for (int i = chunk_statuses.size() - 1; i >= 0; --i) {\n        java.util.concurrent.CompletableFuture future;\n#if version >= 1.14.1\n        future = playerChunk.getStatusFutureUnchecked((net.minecraft.world.level.chunk.status.ChunkStatus) chunk_statuses.get(i));\n#else\n                   future = playerChunk.a((net.minecraft.world.level.chunk.status.ChunkStatus) chunk_statuses.get(i));\n#endif\n                   if (!future.isCompletedExceptionally()) {\n            com.mojang.datafixers.util.Either either = (com.mojang.datafixers.util.Either) future.getNow(null);\n            if (either != null) {\n                java.util.Optional chunkOpt = either.left();\n                if (chunkOpt != null) {\n                    LevelChunk c = (LevelChunk) chunkOpt.get();\n                    return c.getBukkitChunk();\n                }\n            }\n        }\n               }\n    return null;\n}")
        public abstract Chunk getChunkTryHard(Object var1);
    }

    private static final class EntitiesByUUIDMapHook
    implements Map<UUID, Object> {
        private final EntityAddRemoveHandler_1_14_to_1_16_5 handler;
        private final World world;
        private final Map<UUID, Object> base;
        private final Queue<Entity> pendingAddEvents = new LinkedList<Entity>();

        public EntitiesByUUIDMapHook(EntityAddRemoveHandler_1_14_to_1_16_5 handler, World world, Map<UUID, Object> base) {
            this.handler = handler;
            this.world = world;
            this.base = base;
        }

        public Map<UUID, Object> getBase() {
            return this.base;
        }

        public void processEvents() {
            while (!this.pendingAddEvents.isEmpty()) {
                CommonPlugin.getInstance().notifyAdded(this.world, this.pendingAddEvents.poll());
            }
        }

        private void onAdded(Object entity) {
            Entity bEntity = WrapperConversion.toEntity(entity);
            this.handler.notifyAddedEarly(this.world, bEntity);
            this.pendingAddEvents.add(bEntity);
        }

        private void onRemoved(Object entity) {
            Entity bEntity = WrapperConversion.toEntity(entity);
            this.handler.notifyRemoved(this.world, bEntity);
        }

        @Override
        public int size() {
            return this.base.size();
        }

        @Override
        public boolean isEmpty() {
            return this.base.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return this.base.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return this.base.containsValue(value);
        }

        @Override
        public Object get(Object key) {
            return this.base.get(key);
        }

        @Override
        public Object put(UUID key, Object value) {
            Object rval = this.base.put(key, value);
            if (value != rval) {
                if (rval != null) {
                    this.onRemoved(rval);
                }
                if (value != null) {
                    this.onAdded(value);
                }
            }
            return rval;
        }

        @Override
        public Object remove(Object key) {
            Object removed = this.base.remove(key);
            if (removed != null) {
                this.onRemoved(removed);
            }
            return removed;
        }

        @Override
        public void putAll(Map<? extends UUID, ? extends Object> m) {
            for (Map.Entry<? extends UUID, ? extends Object> entry : m.entrySet()) {
                this.put(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public void clear() {
            if (this.base.isEmpty()) {
                return;
            }
            ArrayList<Object> old_values = new ArrayList<Object>(this.values());
            this.base.clear();
            for (Object removed : old_values) {
                this.onRemoved(removed);
            }
        }

        @Override
        public Set<UUID> keySet() {
            return this.base.keySet();
        }

        @Override
        public Collection<Object> values() {
            return this.base.values();
        }

        @Override
        public Set<Map.Entry<UUID, Object>> entrySet() {
            return this.base.entrySet();
        }
    }
}

