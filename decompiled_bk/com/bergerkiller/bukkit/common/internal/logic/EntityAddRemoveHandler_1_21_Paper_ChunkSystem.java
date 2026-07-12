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
 *  org.bukkit.event.entity.EntityEvent
 *  org.bukkit.event.world.ChunkEvent
 *  org.bukkit.plugin.EventExecutor
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
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
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.world.ChunkEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

class EntityAddRemoveHandler_1_21_Paper_ChunkSystem
extends EntityAddRemoveHandler {
    private final AddRemoveHandlerLogic removeHandler = Template.Class.create(AddRemoveHandlerLogic.class, Common.TEMPLATE_RESOLVER);
    private final ChunkEntitiesLoadedHandler chunkEntitiesLoadedHandler = new ChunkEntitiesLoadedUsingEventHandler();
    private final EntityAddRemoveEventHandlerUsingPaperWorldEntityEvents addRemoveEventHandler = new EntityAddRemoveEventHandlerUsingPaperWorldEntityEvents(this);

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public void forceInitialization() {
        this.removeHandler.forceInitialization();
        this.addRemoveEventHandler.forceInitialization();
    }

    @Override
    public void processEvents(World world) {
        this.addRemoveEventHandler.processEvents(world);
    }

    @Override
    public void processEventsForAllWorlds() {
        this.addRemoveEventHandler.processEventsForAllWorlds();
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
        this.addRemoveEventHandler.enable(plugin);
        this.chunkEntitiesLoadedHandler.enable(this, plugin);
    }

    @Override
    protected void hook(World world) {
        this.chunkEntitiesLoadedHandler.hook(this, world);
    }

    @Override
    protected void unhook(World world) {
        this.chunkEntitiesLoadedHandler.unhook(this, world);
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
            EntityAddRemoveHandler_1_21_Paper_ChunkSystem.replaceInEntityTracker(oldEntity, oldEntity, newEntity);
            if (oldEntity.getVehicle() != null) {
                EntityAddRemoveHandler_1_21_Paper_ChunkSystem.replaceInEntityTracker(oldEntity.getVehicle(), oldEntity, newEntity);
            }
            if (oldEntity.getPassengers() != null) {
                for (EntityHandle passenger : oldEntity.getPassengers()) {
                    EntityAddRemoveHandler_1_21_Paper_ChunkSystem.replaceInEntityTracker(passenger, oldEntity, newEntity);
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
            EntityAddRemoveHandler_1_21_Paper_ChunkSystem.replaceInList(statePassengers, oldEntity, newEntity);
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
    @Template.ImportList(value={@Template.Import(value="net.minecraft.server.level.ServerLevel"), @Template.Import(value="net.minecraft.server.level.ServerChunkCache"), @Template.Import(value="net.minecraft.world.entity.Entity"), @Template.Import(value="net.minecraft.world.level.entity.Visibility"), @Template.Import(value="net.minecraft.world.level.ChunkPos"), @Template.Import(value="net.minecraft.core.BlockPos"), @Template.Import(value="net.minecraft.world.entity.Visibility"), @Template.Import(value="java.util.concurrent.locks.StampedLock"), @Template.Import(value="it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap"), @Template.Import(value="it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap"), @Template.Import(value="it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap"), @Template.Import(value="net.minecraft.world.level.entity.EntityTickList"), @Template.Import(value="ca.spottedleaf.moonrise.patches.chunk_system.level.entity.ChunkEntitySlices"), @Template.Import(value="ca.spottedleaf.moonrise.common.util.WorldUtil")})
    @Template.InstanceType(value="ca.spottedleaf.moonrise.patches.chunk_system.level.entity.EntityLookup")
    public static abstract class AddRemoveHandlerLogic
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static boolean isChunkEntitiesLoaded(ServerLevel world, int cx, int cz) {\n    // This no longer works: entity data can load without the chunk being a 'full' chunk yet\n    // It causes incompatibility with Bukkit isChunkLoaded\n    //return world.areEntitiesLoaded(ChunkPos.asLong(cx, cz));\n\n    // Just checks that the chunk is loaded, really\n    ServerChunkCache cps = (ServerChunkCache) world.getChunkSource();\n    return cps.isChunkLoaded(cx, cz);\n}")
        public abstract boolean isChunkEntitiesLoaded(Object var1, int var2, int var3);

        @Template.Generated(value="public static void replaceInWorldStorage(ServerLevel world, Entity oldEntity, Entity newEntity) {\n    #require net.minecraft.world.entity.Entity private int entityId:id;\n    #require net.minecraft.world.entity.Entity protected UUID entityUUID:uuid;\n    int entityId = oldEntity#entityId;\n    UUID entityUUID = oldEntity#entityUUID;\n\n    EntityLookup entityLookup = world.moonrise$getEntityLookup();\n\n    // Entities by ID lookup table\n#if version >= 26.1\n    #require EntityLookup protected final ca.spottedleaf.concurrentutil.map.concurrent.longs.ConcurrentChainedLong2ReferenceHashTable<Entity> entityById;\n    ca.spottedleaf.concurrentutil.map.concurrent.longs.ConcurrentChainedLong2ReferenceHashTable byIdMap = entityLookup#entityById;\n#else\n               #require EntityLookup protected final ca.spottedleaf.concurrentutil.map.ConcurrentLong2ReferenceChainedHashTable<Entity> entityById;\n    ca.spottedleaf.concurrentutil.map.ConcurrentLong2ReferenceChainedHashTable byIdMap = entityLookup#entityById;\n#endif\n               if (byIdMap.get((long) entityId) == oldEntity) {\n        if (newEntity == null) {\n            byIdMap.remove((long) entityId);\n        } else {\n            byIdMap.put((long) entityId, newEntity);\n        }\n               }\n\n    // Entities by UUID lookup table\n    #require EntityLookup protected final java.util.concurrent.ConcurrentHashMap<UUID, Entity> entityByUUID;\n    java.util.concurrent.ConcurrentHashMap byUUIDMap = entityLookup#entityByUUID;\n    if (byUUIDMap.get(entityUUID) == oldEntity) {\n        if (newEntity == null) {\n            byUUIDMap.remove(entityUUID);\n        } else {\n            byUUIDMap.put(entityUUID, newEntity);\n        }\n               }\n\n    // ReferenceList of tracked entities in moonrise mapping\n    if (entityLookup instanceof ca.spottedleaf.moonrise.patches.chunk_system.level.entity.server.ServerEntityLookup) {\n        ca.spottedleaf.moonrise.patches.chunk_system.level.entity.server.ServerEntityLookup serverEntityLookup;\n        serverEntityLookup = (ca.spottedleaf.moonrise.patches.chunk_system.level.entity.server.ServerEntityLookup) entityLookup;\n\n        if (serverEntityLookup.trackerEntities.remove(oldEntity)) {\n            if (newEntity != null) {\n                serverEntityLookup.trackerEntities.add(newEntity);\n            }\n        }\n\n           #if version < 1.21.2\n        if (serverEntityLookup.trackerUnloadedEntities.remove(oldEntity)) {\n            if (newEntity != null) {\n                serverEntityLookup.trackerUnloadedEntities.add(newEntity);\n            }\n        }\n           #endif\n    }\n\n               #require net.minecraft.server.level.ServerLevel final net.minecraft.world.level.entity.EntityTickList entityTickList;\n    EntityTickList tickList = world#entityTickList;\n\n    #require net.minecraft.world.level.entity.EntityTickList private final ca.spottedleaf.moonrise.common.list.IteratorSafeOrderedReferenceSet<Entity> entities;\n    ca.spottedleaf.moonrise.common.list.IteratorSafeOrderedReferenceSet set = tickList#entities;\n    if (set.remove(oldEntity)) {\n        if (newEntity != null) {\n            set.add(newEntity);\n        }\n               }\n\n#if version >= 1.21.2\n    final int minSection = WorldUtil.getMinSection(world);\n    final int maxSection = WorldUtil.getMaxSection(world);\n#else\n               #require EntityLookup private final int minSection;\n    #require EntityLookup private final int maxSection;\n    final int minSection = entityLookup#minSection;\n    final int maxSection = entityLookup#maxSection;\n#endif\n\n               // First check whether the new entity is already stored. If so, no ticking mode changes\n    boolean isNewEntityStored = false;\n    if (newEntity != null) {\n        final BlockPos pos = newEntity.blockPosition();\n        final int sectionX = pos.getX() >> 4;\n        final int sectionY = net.minecraft.util.Mth.clamp(pos.getY() >> 4, minSection, maxSection);\n        final int sectionZ = pos.getZ() >> 4;\n\n        // Runs just the chunk-adding logic. ID/UUID is done earlier.\n        newEntity.moonrise$setSectionX(sectionX);\n        newEntity.moonrise$setSectionY(sectionY);\n        newEntity.moonrise$setSectionZ(sectionZ);\n\n        ChunkEntitySlices slices = entityLookup.getChunk(sectionX, sectionZ);\n        if (slices != null) {\n            #require ca.spottedleaf.moonrise.patches.chunk_system.level.entity.ChunkEntitySlices private java.util.List<net.minecraft.world.entity.Entity> getAllEntities();\n            java.util.List allEntities = slices#getAllEntities();\n            java.util.Iterator iter = allEntities.iterator();\n            while (iter.hasNext()) {\n                if (iter.next() == newEntity) {\n                    isNewEntityStored = true;\n                    break;\n                }\n            }\n        }\n               }\n\n    // bug: if chunk doesn't exist, error occurs\n    //entitySliceManager.removeEntity(oldEntity);\n    ChunkEntitySlices slices = entityLookup.getChunk(oldEntity.moonrise$getSectionX(), oldEntity.moonrise$getSectionZ());\n    if (slices != null) {\n        int oldEntitySectionY = oldEntity.moonrise$getSectionY();\n        slices.removeEntity(oldEntity, oldEntitySectionY);\n        if (slices.isEmpty()) {\n            //TODO: Not done in the server now. Bug?\n            //entityLookup.removeChunk(oldEntity.moonrise$getSectionX(), oldEntity.moonrise$getSectionZ());\n        }\n               }\n\n    // Add new entity (might not be the same chunk)\n    // Note: we cannot call addEntity as this initializes the tracker/other logic/events\n    ChunkEntitySlices sectionOfEntity = null;\n    if (newEntity != null) {\n        final BlockPos pos = newEntity.blockPosition();\n        final int sectionX = pos.getX() >> 4;\n        final int sectionY = net.minecraft.util.Mth.clamp(pos.getY() >> 4, minSection, maxSection);\n        final int sectionZ = pos.getZ() >> 4;\n\n        // Runs just the chunk-adding logic. ID/UUID is done earlier.\n        newEntity.moonrise$setSectionX(sectionX);\n        newEntity.moonrise$setSectionY(sectionY);\n        newEntity.moonrise$setSectionZ(sectionZ);\n\n        sectionOfEntity = entityLookup.getOrCreateChunk(sectionX, sectionZ);\n        sectionOfEntity.addEntity(newEntity, sectionY);\n    }\n\n               // Update the \"all entities\" list\n    #require EntityLookup private final ca.spottedleaf.moonrise.common.list.EntityList accessibleEntities;\n    ca.spottedleaf.moonrise.common.list.EntityList lookupAccEntities = entityLookup#accessibleEntities;\n    if (newEntity == null) {\n        lookupAccEntities.remove(oldEntity);\n    } else {\n        // Swap the Entity in the internal array\n        #require ca.spottedleaf.moonrise.common.list.EntityList protected final Int2IntOpenHashMap entityListEntityToIndex:entityToIndex;\n        #require ca.spottedleaf.moonrise.common.list.EntityList protected Entity[] entityListEntities:entities;\n        Int2IntOpenHashMap el_entityToIndex = lookupAccEntities#entityListEntityToIndex;\n        Entity[] el_entities = lookupAccEntities#entityListEntities;\n        int index = el_entityToIndex.get(newEntity.getId());\n        if (index >= 0 && index < el_entities.length) {\n            el_entities[index] = newEntity;\n        }\n               }\n\n    // If isAlwaysTicking() of the old and new entity differs, we may have to stop/start ticking ourselves\n    // This is because of a bug in the persistent entity section manager that, if isAlwaysTicking() is true,\n    // the updateStatus function does not work anymore to update this state.\n    // Only do this when the entity is first replaced. Not the second time around.\n    if (!isNewEntityStored && sectionOfEntity != null) {\n        boolean wasTicking = EntityLookup.getEntityStatus(oldEntity).isTicking();\n        boolean isAlwaysTicking = newEntity.isAlwaysTicking();\n\n        // Start ticking when section is not ticking, and we go from not always ticking\n        // to always ticking. This is because this 'load' trigger already fired, and so it\n        // presumes startTicking() was already performed.\n        if (isAlwaysTicking && !wasTicking) {\n            // Force a status change from TRACKED to TICKING, which starts ticking the entity\n            // Do not cause a change from a visibility below TRACKED, that will break things\n            entityLookup.entityStatusChange(newEntity, sectionOfEntity, Visibility.TRACKED, Visibility.TICKING,\n                     false, true, false);\n        }\n               }\n}")
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
        public void enable(final EntityAddRemoveHandler_1_21_Paper_ChunkSystem handler, CommonPlugin plugin) {
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
        public void hook(EntityAddRemoveHandler_1_21_Paper_ChunkSystem handler, World world) {
        }

        @Override
        public void unhook(EntityAddRemoveHandler_1_21_Paper_ChunkSystem handler, World world) {
        }

        private void onEntitiesLoaded(EntityAddRemoveHandler_1_21_Paper_ChunkSystem handler, ChunkEvent event) {
            handler.notifyChunkEntitiesLoaded(event.getChunk());
        }

        private void onEntitiesUnloaded(EntityAddRemoveHandler_1_21_Paper_ChunkSystem handler, ChunkEvent event) {
            handler.notifyChunkEntitiesUnloaded(event.getChunk());
        }
    }

    private static interface ChunkEntitiesLoadedHandler {
        public void enable(EntityAddRemoveHandler_1_21_Paper_ChunkSystem var1, CommonPlugin var2);

        public void hook(EntityAddRemoveHandler_1_21_Paper_ChunkSystem var1, World var2);

        public void unhook(EntityAddRemoveHandler_1_21_Paper_ChunkSystem var1, World var2);
    }

    private static class EntityAddRemoveEventHandlerUsingPaperWorldEntityEvents
    implements LazyInitializedObject,
    Listener {
        private final EntityAddRemoveHandler_1_21_Paper_ChunkSystem handler;
        private final Map<World, Queue<Entity>> pendingAddEvents = new HashMap<World, Queue<Entity>>();
        private final FastMethod<World> addToWorldGetWorldMethod = new FastMethod();
        private final FastMethod<World> removeFromWorldGetWorldMethod = new FastMethod();

        public EntityAddRemoveEventHandlerUsingPaperWorldEntityEvents(EntityAddRemoveHandler_1_21_Paper_ChunkSystem handler) {
            this.handler = handler;
        }

        public void enable(CommonPlugin plugin) {
            Class entitiesLoadEventType = (Class)LogicUtil.unsafeCast(CommonUtil.getClass("com.destroystokyo.paper.event.entity.EntityAddToWorldEvent"));
            try {
                this.addToWorldGetWorldMethod.init(entitiesLoadEventType.getMethod("getWorld", new Class[0]));
            }
            catch (Throwable t) {
                this.addToWorldGetWorldMethod.initUnavailable("Method getWorld of EntityAddToWorldEvent not found");
            }
            Bukkit.getPluginManager().registerEvent(entitiesLoadEventType, (Listener)this, EventPriority.LOWEST, new EventExecutor(){

                public void execute(Listener listener, Event event) throws EventException {
                    this.onEntityAddedToWorld((EntityEvent)event);
                }
            }, (Plugin)plugin);
            Class entitiesUnloadEventType = (Class)LogicUtil.unsafeCast(CommonUtil.getClass("com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent"));
            try {
                this.removeFromWorldGetWorldMethod.init(entitiesUnloadEventType.getMethod("getWorld", new Class[0]));
            }
            catch (Throwable t) {
                this.removeFromWorldGetWorldMethod.initUnavailable("Method getWorld of EntityRemoveFromWorldEvent not found");
            }
            Bukkit.getPluginManager().registerEvent(entitiesUnloadEventType, (Listener)this, EventPriority.LOWEST, new EventExecutor(){

                public void execute(Listener listener, Event event) throws EventException {
                    this.onEntityRemovedFromWorld((EntityEvent)event);
                }
            }, (Plugin)plugin);
        }

        private synchronized Queue<Entity> getPendingAddQueue(World world) {
            return this.pendingAddEvents.computeIfAbsent(world, w -> new ConcurrentLinkedQueue());
        }

        private synchronized void removePendingIfNotInWorlds(List<World> loadedWorlds) {
            this.pendingAddEvents.keySet().removeIf(world -> !loadedWorlds.contains(world));
        }

        private void onEntityAddedToWorld(EntityEvent event) {
            Entity entity = event.getEntity();
            World world = this.addToWorldGetWorldMethod.invoke(event);
            this.getPendingAddQueue(world).add(entity);
            this.handler.notifyAddedEarly(world, entity);
        }

        private void onEntityRemovedFromWorld(EntityEvent event) {
            Entity entity = event.getEntity();
            World world = this.removeFromWorldGetWorldMethod.invoke(event);
            this.getPendingAddQueue(world).remove(entity);
            this.handler.notifyRemoved(world, entity);
        }

        public void processEventsForAllWorlds() {
            List loadedWorlds = Bukkit.getWorlds();
            this.removePendingIfNotInWorlds(loadedWorlds);
            for (World world : loadedWorlds) {
                this.processEvents(world);
            }
        }

        public void processEvents(World world) {
            Queue<Entity> pendingInWorld = this.getPendingAddQueue(world);
            while (!pendingInWorld.isEmpty()) {
                Entity pendingEntity = pendingInWorld.poll();
                if (pendingEntity == null) continue;
                CommonPlugin.getInstance().notifyAdded(world, pendingEntity);
            }
        }

        @Override
        public void forceInitialization() {
            this.addToWorldGetWorldMethod.forceInitialization();
            this.removeFromWorldGetWorldMethod.forceInitialization();
        }
    }
}

