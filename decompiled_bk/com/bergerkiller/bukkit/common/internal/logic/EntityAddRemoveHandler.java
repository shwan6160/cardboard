/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.collections.EntityByIdWorldMap;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.events.ChunkLoadEntitiesEvent;
import com.bergerkiller.bukkit.common.events.ChunkUnloadEntitiesEvent;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler_1_14_to_1_16_5;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler_1_17;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler_1_21_Paper_ChunkSystem;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler_1_8_to_1_13_2;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.LevelChunkHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class EntityAddRemoveHandler
implements LazyInitializedObject,
LibraryComponent {
    public static final EntityAddRemoveHandler INSTANCE = LibraryComponentSelector.forModule(EntityAddRemoveHandler.class).addVersionOption(null, "1.13.2", EntityAddRemoveHandler_1_8_to_1_13_2::new).addVersionOption("1.14", "1.16.5", EntityAddRemoveHandler_1_14_to_1_16_5::new).addWhen("Paper Mixin ChunkSystem EntityAddRemoveHandler", e -> {
        try {
            Class.forName("ca.spottedleaf.moonrise.patches.chunk_system.level.entity.EntityLookup");
            return true;
        }
        catch (Throwable t) {
            return false;
        }
    }, EntityAddRemoveHandler_1_21_Paper_ChunkSystem::new).addWhen("Paper ChunkSystem EntityAddRemoveHandler", e -> {
        try {
            Class.forName("io.papermc.paper.chunk.system.entity.EntityLookup");
            return true;
        }
        catch (Throwable t) {
            return false;
        }
    }, EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem::new).addVersionOption("1.17", null, EntityAddRemoveHandler_1_17::new).update();
    private final EntityByIdWorldMap entitiesById = new EntityByIdWorldMap();
    private CommonPlugin plugin = null;
    private Task worldSyncTask = null;
    private static final List<Class<?>> listsWithImmutableListIterator;

    public final Entity getEntityById(World world, int entityId) {
        return this.entitiesById.get(world, entityId);
    }

    public void onEnabled(CommonPlugin plugin) {
        this.plugin = plugin;
        this.worldSyncTask = new WorldEntityByIdSyncTask(plugin);
        this.worldSyncTask.start(1200L, 1200L);
    }

    public void onDisabled() {
        Task.stop(this.worldSyncTask);
        this.worldSyncTask = null;
    }

    public final void onWorldEnabled(World world) {
        this.entitiesById.sync(world);
        this.hook(world);
    }

    public final void onWorldDisabled(World world) {
        this.unhook(world);
        this.entitiesById.clear(world);
    }

    public abstract void processEvents(World var1);

    public void processEventsForAllWorlds() {
        for (World world : Bukkit.getWorlds()) {
            this.processEvents(world);
        }
    }

    public abstract boolean isChunkEntitiesLoaded(World var1, int var2, int var3);

    public abstract boolean isChunkEntitiesLoaded(Chunk var1);

    public final void removeEntity(EntityHandle entity) {
        this.replace(entity, null);
    }

    public abstract void replace(EntityHandle var1, EntityHandle var2);

    public abstract void moveToChunk(EntityHandle var1);

    protected abstract void hook(World var1);

    protected abstract void unhook(World var1);

    protected final void notifyRemoved(World world, Entity entity) {
        this.entitiesById.remove(world, entity);
        this.plugin.notifyRemoved(world, entity);
    }

    protected final void notifyAddedEarly(World world, Entity entity) {
        this.entitiesById.add(world, entity);
        this.plugin.notifyAddedEarly(world, entity);
    }

    protected final void notifyChunkEntitiesLoaded(Chunk chunk) {
        this.processEvents(chunk.getWorld());
        CommonUtil.callEvent(new ChunkLoadEntitiesEvent(chunk));
    }

    protected final void notifyChunkEntitiesUnloaded(Chunk chunk) {
        this.processEvents(chunk.getWorld());
        CommonUtil.callEvent(new ChunkUnloadEntitiesEvent(chunk));
    }

    private static boolean replaceInList(List list, Object oldValue, Object newValue) {
        if (list == null) {
            return false;
        }
        if (newValue == null) {
            return list.remove(oldValue);
        }
        if (EntityAddRemoveHandler.canMutateListIterator(list)) {
            boolean changed = false;
            ListIterator<Object> iter = list.listIterator();
            while (iter.hasNext()) {
                if (iter.next() != oldValue) continue;
                iter.set(newValue);
                changed = true;
            }
            return changed;
        }
        int index = list.indexOf(oldValue);
        if (index >= 0 && index < list.size() && list.get(index) == oldValue) {
            list.remove(oldValue);
            list.add(index, newValue);
            return true;
        }
        return false;
    }

    private static boolean canMutateListIterator(List<?> list) {
        for (Class<?> type : listsWithImmutableListIterator) {
            if (!type.isInstance(list)) continue;
            return false;
        }
        return true;
    }

    static {
        ArrayList lists = new ArrayList();
        lists.add(CommonUtil.getClass("io.papermc.paper.util.maplist.ObjectMapList", false));
        listsWithImmutableListIterator = lists.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private final class WorldEntityByIdSyncTask
    extends Task {
        private final Queue<World> worldQueue;

        public WorldEntityByIdSyncTask(JavaPlugin plugin) {
            super(plugin);
            this.worldQueue = new LinkedList<World>();
        }

        @Override
        public void run() {
            if (this.worldQueue.isEmpty()) {
                this.worldQueue.addAll(WorldUtil.getWorlds());
            }
            World world = this.worldQueue.poll();
            while (true) {
                if (world == null) {
                    return;
                }
                if (Bukkit.getWorld((UUID)world.getUID()) == world) break;
                world = this.worldQueue.poll();
            }
            EntityAddRemoveHandler.this.entitiesById.sync(world);
        }
    }

    protected static class ChunkEntitySliceHandler {
        private final FastField<Object[]> chunkEntitySlicesField = new FastField();
        private final boolean chunkEntitySlicesFieldIsLists;
        private final HandlerLogic logic;

        public ChunkEntitySliceHandler() {
            boolean isLists = false;
            try {
                String fieldName = Resolver.resolveFieldName(LevelChunkHandle.T.getType(), "entitySlices");
                Field entitySlicesField = MPLType.getDeclaredField(LevelChunkHandle.T.getType(), fieldName);
                Class<?> fieldType = entitySlicesField.getType();
                boolean bl = isLists = fieldType == List[].class;
                if (!fieldType.isArray()) {
                    throw new IllegalArgumentException("Field type is not an array, but is " + fieldType);
                }
                this.chunkEntitySlicesField.init(entitySlicesField);
            }
            catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.WARNING, "Chunk entitySlices field not found", t);
                this.chunkEntitySlicesField.initUnavailable("Chunk entitySlices field not found");
            }
            this.chunkEntitySlicesFieldIsLists = isLists;
            this.logic = Template.Class.create(HandlerLogic.class, Common.TEMPLATE_RESOLVER);
            this.logic.forceInitialization();
        }

        public void moveToChunk(EntityHandle entity) {
            Chunk chunk;
            int oldcx = entity.getChunkX();
            int oldcy = entity.getChunkY();
            int oldcz = entity.getChunkZ();
            int newcx = MathUtil.toChunk(entity.getLocX());
            int newcy = MathUtil.toChunk(entity.getLocY());
            int newcz = MathUtil.toChunk(entity.getLocZ());
            World world = entity.getBukkitWorld();
            boolean changedChunks = oldcx != newcx || oldcy != newcy || oldcz != newcz;
            boolean isLoaded = entity.isLoadedInWorld();
            if (isLoaded && changedChunks && (chunk = WorldUtil.getChunk(world, oldcx, oldcz)) != null) {
                this.removeFromChunk(chunk, entity);
            }
            if (!isLoaded || changedChunks) {
                chunk = WorldUtil.getChunk(world, newcx, newcz);
                isLoaded = chunk != null;
                if (isLoaded) {
                    this.addToChunk(chunk, entity);
                }
                EntityHandle.T.setLoadedInWorld_pre_1_17.invoke(entity.getRaw(), isLoaded);
            }
        }

        public boolean replace(Object chunkHandle, EntityHandle oldEntity, EntityHandle newEntity) {
            Object[] slices = this.chunkEntitySlicesField.get(chunkHandle);
            int chunkY = oldEntity.getChunkY();
            if (chunkY < 0) {
                chunkY = 0;
            } else if (chunkY >= slices.length) {
                chunkY = slices.length - 1;
            }
            boolean found = false;
            if (this.replaceInSlice(slices[chunkY], oldEntity, newEntity)) {
                found = true;
            } else {
                for (int y = 0; y < slices.length; ++y) {
                    if (y == chunkY || !this.replaceInSlice(slices[y], oldEntity, newEntity)) continue;
                    found = true;
                    break;
                }
            }
            this.logic.replaceInChunkSpecial(chunkHandle, oldEntity.getRaw(), Template.Handle.getRaw(newEntity));
            return found;
        }

        public boolean removeFromChunk(Chunk chunk, EntityHandle entity) {
            return this.replace(HandleConversion.toChunkHandle(chunk), entity, null);
        }

        public void addToChunk(Chunk chunk, EntityHandle entity) {
            LevelChunkHandle.fromBukkit(chunk).addEntity(entity);
        }

        private List<Object> sliceToList(Object entitySlice) {
            return this.chunkEntitySlicesFieldIsLists ? (List<Object>)entitySlice : HandleConversion.cbEntitySliceToList(entitySlice);
        }

        private boolean replaceInSlice(Object slice, EntityHandle oldEntity, EntityHandle newEntity) {
            Object newRaw = newEntity == null ? null : newEntity.getRaw();
            return EntityAddRemoveHandler.replaceInList(this.sliceToList(slice), oldEntity.getRaw(), newRaw);
        }

        @Template.Optional
        @Template.Import(value="net.minecraft.world.entity.Entity")
        @Template.InstanceType(value="net.minecraft.world.level.chunk.LevelChunk")
        public static abstract class HandlerLogic
        extends Template.Class<Template.Handle> {
            @Template.Generated(value="public static void replaceInChunkSpecial(Chunk chunk, Entity oldEntity, Entity newEntity) {\n    // Paperspigot\n#if exists net.minecraft.world.level.chunk.LevelChunk public final com.destroystokyo.paper.util.maplist.EntityList entities;\n    if (chunk.entities.remove(oldEntity)) {\n        if (newEntity != null) {\n            chunk.entities.add(newEntity);\n        }\n                   }\n#endif\n\n                   // Tuinity\n#if exists net.minecraft.world.level.chunk.LevelChunk protected final com.tuinity.tuinity.world.ChunkEntitySlices entitySlicesManager;\n    #require net.minecraft.world.level.chunk.LevelChunk protected final com.tuinity.tuinity.world.ChunkEntitySlices entitySlicesManager;\n    com.tuinity.tuinity.world.ChunkEntitySlices slices = chunk#entitySlicesManager;\n    synchronized (slices) {\n        // Locate the old entity inside the \"allEntities\" slices to figure out if it is stored,\n        // and at what y-section it is stored. Looks between minSection and maxSection.\n        #require com.tuinity.tuinity.world.ChunkEntitySlices protected final (Object) ChunkEntitySlices.EntityCollectionBySection allEntities;\n        #require com.tuinity.tuinity.world.ChunkEntitySlices.EntityCollectionBySection protected final (Object[]) com.tuinity.tuinity.world.ChunkEntitySlices.BasicEntityList[] entitiesBySection;\n        Object allEntities = slices#allEntities;\n        Object[] sections = allEntities#entitiesBySection;\n\n        #require com.tuinity.tuinity.world.ChunkEntitySlices.BasicEntityList public boolean has(E extends net.minecraft.world.entity.Entity entity);\n        boolean found = false;\n        int relIdxFound = 0;\n        for (int i = 0; i < sections.length; i++) {\n            Object section = sections[i];\n            if (section != null) {\n                found = section#has(oldEntity);\n                if (found) {\n                    relIdxFound = i;\n                    break;\n                }\n            }\n                       }\n        if (found) {\n            #require com.tuinity.tuinity.world.ChunkEntitySlices protected final int minSection;\n            int sectionIdx = relIdxFound + slices#minSection;\n            slices.removeEntity(oldEntity, sectionIdx);\n            if (newEntity != null) {\n                slices.addEntity(newEntity, sectionIdx);\n            }\n                       }\n    }\n               #endif\n}")
            public abstract void replaceInChunkSpecial(Object var1, Object var2, Object var3);
        }
    }
}

