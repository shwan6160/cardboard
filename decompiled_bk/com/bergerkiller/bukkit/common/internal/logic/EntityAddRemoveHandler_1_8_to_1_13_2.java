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
import com.bergerkiller.bukkit.common.conversion.Conversion;
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
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.util.IntHashMapHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import com.bergerkiller.mountiplex.reflection.util.fast.NullInvoker;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

class EntityAddRemoveHandler_1_8_to_1_13_2
extends EntityAddRemoveHandler {
    private final Class<?> iWorldAccessType;
    private final SafeField<?> entitiesByIdField;
    private final FastField<Map<UUID, Object>> entitiesByUUIDField = new FastField();
    private final FastField<List<Object>> entityListField;
    private final SafeField<List<Object>> accessListField;
    private final SafeField<Collection<Object>> entityRemoveQueue;
    private final EntityAddRemoveHandler.ChunkEntitySliceHandler chunkEntitySliceHandler;

    public EntityAddRemoveHandler_1_8_to_1_13_2() {
        this.iWorldAccessType = CommonUtil.getClass("net.minecraft.world.level.IWorldAccess");
        this.entitiesByIdField = SafeField.create(LevelHandle.T.getType(), "entitiesById", IntHashMapHandle.T.getType());
        this.entityListField = new FastField();
        try {
            this.entityListField.init(LevelHandle.T.getType().getDeclaredField("entityList"));
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
        try {
            String fieldName = Resolver.resolveFieldName(ServerLevelHandle.T.getType(), "entitiesByUUID");
            this.entitiesByUUIDField.init(MPLType.getDeclaredField(ServerLevelHandle.T.getType(), fieldName));
            if (!Map.class.isAssignableFrom(this.entitiesByUUIDField.getType())) {
                throw new IllegalStateException("Field not assignable to Map");
            }
        }
        catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.WARNING, "Failed to initialize WorldServer entitiesByUUID field: " + t.getMessage(), t);
            this.entitiesByUUIDField.initUnavailable("entitiesByUUID");
        }
        this.accessListField = CommonBootstrap.evaluateMCVersion(">=", "1.13") ? (SafeField)LogicUtil.unsafeCast(SafeField.create(LevelHandle.T.getType(), "v", List.class)) : (SafeField)LogicUtil.unsafeCast(SafeField.create(LevelHandle.T.getType(), "u", List.class));
        Field entityRemoveQueueField = null;
        try {
            entityRemoveQueueField = CommonBootstrap.evaluateMCVersion(">=", "1.13") ? LevelHandle.T.getType().getDeclaredField("g") : (CommonBootstrap.evaluateMCVersion(">=", "1.9") ? LevelHandle.T.getType().getDeclaredField("f") : LevelHandle.T.getType().getDeclaredField("g"));
            if (!Collection.class.isAssignableFrom(entityRemoveQueueField.getType())) {
                Logging.LOGGER_REFLECTION.warning("Entity remove queue field " + entityRemoveQueueField.toString() + " is of incompatible type");
                entityRemoveQueueField = null;
            }
        }
        catch (Throwable t) {
            Logging.LOGGER_REFLECTION.warning("Entity remove queue field not found");
        }
        this.entityRemoveQueue = entityRemoveQueueField == null ? SafeField.createNull("World Entity Remove Queue") : new SafeField(entityRemoveQueueField);
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
        this.entitiesByUUIDField.forceInitialization();
        this.entityListField.forceInitialization();
    }

    @Override
    public void onEnabled(CommonPlugin plugin) {
        super.onEnabled(plugin);
        plugin.register(new Listener(){

            @EventHandler(priority=EventPriority.LOWEST)
            public void onChunkLoad(ChunkLoadEvent event) {
                EntityAddRemoveHandler_1_8_to_1_13_2.this.notifyChunkEntitiesLoaded(event.getChunk());
            }

            @EventHandler(priority=EventPriority.LOWEST)
            public void onChunkUnload(ChunkUnloadEvent event) {
                EntityAddRemoveHandler_1_8_to_1_13_2.this.notifyChunkEntitiesUnloaded(event.getChunk());
            }
        });
    }

    @Override
    protected void hook(World world) {
        if (!this.accessListField.isValid()) {
            Logging.LOGGER_REFLECTION.warning("Failed to hook world " + world.getName() + " with entity listener hook, Entity Add/Remove events will not work");
            return;
        }
        List<Object> accessList = this.accessListField.get(Conversion.toWorldHandle.convert(world));
        if (accessList == null) {
            Logging.LOGGER_REFLECTION.warning("Failed to hook world " + world.getName() + " with entity listener hook (null), Entity Add/Remove events will not work");
            return;
        }
        for (Object o : accessList) {
            if (WorldListenerHook.get(o, WorldListenerHook.class) == null) continue;
            return;
        }
        accessList.add(new WorldListenerHook(this, world).createInstance(this.iWorldAccessType));
    }

    @Override
    protected void unhook(World world) {
        if (!this.accessListField.isValid()) {
            return;
        }
        List<Object> accessList = this.accessListField.get(Conversion.toWorldHandle.convert(world));
        if (accessList != null) {
            Iterator<Object> iter = accessList.iterator();
            while (iter.hasNext()) {
                if (WorldListenerHook.get(iter.next(), WorldListenerHook.class) == null) continue;
                iter.remove();
            }
        }
    }

    @Override
    public void processEvents(World world) {
    }

    @Override
    public void processEventsForAllWorlds() {
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
        IntHashMapHandle entitiesById;
        ServerLevelHandle world = oldEntity.getWorldServer();
        if (newEntity == null) {
            if (world != null) {
                world.removeEntity(oldEntity);
                world.getEntityTracker().stopTracking(oldEntity.getBukkitEntity());
            }
            return;
        }
        Object worldHandle = world.getRaw();
        Map<UUID, Object> entitiesByUUID = this.entitiesByUUIDField.get(worldHandle);
        Object storedEntityHandle = entitiesByUUID.get(oldEntity.getUniqueID());
        if (storedEntityHandle != null && storedEntityHandle != newEntity.getRaw()) {
            if (!oldEntity.getUniqueID().equals(newEntity.getUniqueID())) {
                entitiesByUUID.remove(oldEntity.getUniqueID());
            }
            entitiesByUUID.put(newEntity.getUniqueID(), newEntity.getRaw());
        }
        if ((storedEntityHandle = (entitiesById = IntHashMapHandle.createHandle(this.entitiesByIdField.get(worldHandle))).get(oldEntity.getId())) != null && storedEntityHandle != newEntity.getRaw()) {
            if (oldEntity.getId() != newEntity.getId()) {
                entitiesById.remove(oldEntity.getId());
            }
            entitiesById.put(newEntity.getId(), newEntity.getRaw());
        }
        entitiesByUUID = this.entitiesByUUIDField.get(worldHandle);
        entitiesByUUID.put(newEntity.getUniqueID(), newEntity.getRaw());
        IntHashMapHandle entitiesById2 = IntHashMapHandle.createHandle(this.entitiesByIdField.get(worldHandle));
        entitiesById2.put(newEntity.getId(), newEntity.getRaw());
        EntityAddRemoveHandler_1_8_to_1_13_2.replaceInEntityTracker(newEntity.getId(), newEntity);
        if (newEntity.getVehicle() != null) {
            EntityAddRemoveHandler_1_8_to_1_13_2.replaceInEntityTracker(newEntity.getVehicle().getId(), newEntity);
        }
        if (newEntity.getPassengers() != null) {
            for (EntityHandle passenger : newEntity.getPassengers()) {
                EntityAddRemoveHandler_1_8_to_1_13_2.replaceInEntityTracker(passenger.getId(), newEntity);
            }
        }
        EntityAddRemoveHandler_1_8_to_1_13_2.replaceInList(this.entityListField.get(worldHandle), newEntity);
        int chunkX = newEntity.getChunkX();
        int chunkZ = newEntity.getChunkZ();
        Object chunkHandle = HandleConversion.toChunkHandle(WorldUtil.getChunk(newEntity.getWorld().getWorld(), chunkX, chunkZ));
        if (chunkHandle != null) {
            this.chunkEntitySliceHandler.replace(chunkHandle, oldEntity, newEntity);
        }
    }

    @Override
    public void moveToChunk(EntityHandle entity) {
        this.chunkEntitySliceHandler.moveToChunk(entity);
    }

    private static void replaceInEntityTracker(int entityId, EntityHandle newInstance) {
        EntityTracker trackerMap = WorldUtil.getTracker(newInstance.getWorld().getWorld());
        EntityTrackerEntryHandle entry = trackerMap.getEntry(entityId);
        if (entry != null) {
            EntityHandle tracker = entry.getState().getEntity();
            if (tracker != null && tracker.getId() == newInstance.getId()) {
                entry.setEntity(newInstance);
            }
            ArrayList<EntityHandle> passengers = new ArrayList<EntityHandle>(tracker.getPassengers());
            EntityAddRemoveHandler_1_8_to_1_13_2.replaceInList(passengers, newInstance);
            tracker.setPassengers(passengers);
        }
    }

    private static boolean replaceInList(List list, EntityHandle entity) {
        if (list == null) {
            return false;
        }
        ListIterator<Object> iter = list.listIterator();
        while (iter.hasNext()) {
            int obj_id;
            Object obj = iter.next();
            if (obj instanceof EntityHandle) {
                EntityHandle obj_e = (EntityHandle)obj;
                if (obj_e.getIdField() != entity.getIdField()) continue;
                iter.set(entity);
                continue;
            }
            if (!EntityHandle.T.isAssignableFrom(obj) || (obj_id = EntityHandle.T.idField.getInteger(obj)) != entity.getIdField()) continue;
            iter.set(entity.getRaw());
        }
        return false;
    }

    @ClassHook.HookPackage(value="net.minecraft.server")
    @ClassHook.HookImport(value="net.minecraft.world.entity.Entity")
    public static class WorldListenerHook
    extends ClassHook<WorldListenerHook> {
        private final EntityAddRemoveHandler_1_8_to_1_13_2 handler;
        private final World world;

        public WorldListenerHook(EntityAddRemoveHandler_1_8_to_1_13_2 handler, World world) {
            this.handler = handler;
            this.world = world;
        }

        @Override
        protected Invoker<?> getCallback(Class<?> hookedType, Method method) {
            Invoker<?> result = super.getCallback(hookedType, method);
            if (result != null) {
                return result;
            }
            if (method.getDeclaringClass().equals(Object.class)) {
                return null;
            }
            return new NullInvoker(method.getReturnType());
        }

        @ClassHook.HookMethod(value="public void onEntityAdded:a(Entity entity)")
        public void onEntityAdded(Object entity) {
            Entity bEntity = WrapperConversion.toEntity(entity);
            this.handler.notifyAddedEarly(this.world, bEntity);
            CommonPlugin.getInstance().notifyAdded(this.world, bEntity);
        }

        @ClassHook.HookMethod(value="public void onEntityRemoved:b(Entity entity)")
        public void onEntityRemoved(Object entity) {
            Collection removeQueue;
            Entity bEntity = WrapperConversion.toEntity(entity);
            this.handler.notifyRemoved(this.world, bEntity);
            if (this.handler.entityRemoveQueue.isValid() && !Common.IS_PAPERSPIGOT_SERVER && (removeQueue = (Collection)this.handler.entityRemoveQueue.get(HandleConversion.toWorldHandle(this.world))) != null && removeQueue.contains(entity)) {
                CommonPlugin.getInstance().notifyRemovedFromServer(this.world, bEntity, true);
            }
        }
    }
}

