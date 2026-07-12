/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.lighting.LightingHandler;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.level.ChunkMapHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ThreadedLevelLightEngineHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;
import java.util.logging.Level;
import org.bukkit.World;

class LightingHandler_1_14
implements LightingHandler {
    private final LightEngineHandle handle;
    private final Field light_layer_block;
    private final Field light_layer_sky;
    private final Field light_storage;
    private final Field light_storage_array_live;
    private final Object light_engine_pre_update;
    private final Object light_engine_post_update;
    private final Method light_engine_schedule;
    private final Map<World, EngineUpdateTaskLists> task_lists = new HashMap<World, EngineUpdateTaskLists>();
    private final IntSupplier golden_ticket;

    public LightingHandler_1_14() throws Throwable {
        String golden_ticket_name;
        String light_storage_array_live_name;
        String light_storage_name;
        String light_layer_sky_name;
        String light_layer_block_name;
        this.handle = Template.Class.create(LightEngineHandle.class, Common.TEMPLATE_RESOLVER);
        this.handle.forceInitialization();
        Class<?> lightEngineType = CommonUtil.getClass("net.minecraft.world.level.lighting.LightEngine");
        if (lightEngineType == null) {
            throw new IllegalStateException("LightEngine class not found");
        }
        Class<?> lightEngineLayerType = CommonUtil.getClass("net.minecraft.world.level.lighting.LightEngineLayer");
        if (lightEngineLayerType == null) {
            throw new IllegalStateException("LightEngineLayer class not found");
        }
        Class<?> lightEngineStorageType = CommonUtil.getClass("net.minecraft.world.level.lighting.LayerLightSectionStorage");
        if (lightEngineStorageType == null) {
            throw new IllegalStateException("LayerLightSectionStorage class not found");
        }
        Class<?> lightEngineStorageArrayType = CommonUtil.getClass("net.minecraft.world.level.lighting.DataLayerStorageMap");
        if (lightEngineStorageArrayType == null) {
            throw new IllegalStateException("DataLayerStorageMap class not found");
        }
        if (CommonBootstrap.evaluateMCVersion(">=", "1.17")) {
            light_layer_block_name = "blockEngine";
            light_layer_sky_name = "skyEngine";
            light_storage_name = "storage";
            light_storage_array_live_name = "updatingSectionData";
            golden_ticket_name = "MAX_CHUNK_DISTANCE";
        } else {
            light_layer_block_name = "a";
            light_layer_sky_name = "b";
            light_storage_name = "c";
            light_storage_array_live_name = "f";
            golden_ticket_name = "GOLDEN_TICKET";
        }
        this.light_layer_block = MPLType.getDeclaredField(lightEngineType, Resolver.resolveFieldName(lightEngineType, light_layer_block_name));
        if (!lightEngineLayerType.isAssignableFrom(this.light_layer_block.getType())) {
            throw new IllegalStateException("LightEngine light_layer_block is not of type LightEngineLayer");
        }
        this.light_layer_sky = MPLType.getDeclaredField(lightEngineType, Resolver.resolveFieldName(lightEngineType, light_layer_sky_name));
        if (!lightEngineLayerType.isAssignableFrom(this.light_layer_sky.getType())) {
            throw new IllegalStateException("LightEngine light_layer_sky is not of type LightEngineLayer");
        }
        this.light_storage = MPLType.getDeclaredField(lightEngineLayerType, Resolver.resolveFieldName(lightEngineLayerType, light_storage_name));
        if (!lightEngineStorageType.isAssignableFrom(this.light_storage.getType())) {
            throw new IllegalStateException("LightEngineLayer light_storage field is not of type LightEngineStorage");
        }
        this.light_storage_array_live = MPLType.getDeclaredField(lightEngineStorageType, Resolver.resolveFieldName(lightEngineStorageType, light_storage_array_live_name));
        if (!lightEngineStorageArrayType.isAssignableFrom(this.light_storage_array_live.getType())) {
            throw new IllegalStateException("LayerLightSectionStorage light_storage_array_live field is not of type DataLayerStorageMap");
        }
        String name = Resolver.resolveFieldName(ChunkMapHandle.T.getType(), golden_ticket_name);
        Field f = ChunkMapHandle.T.getType().getDeclaredField(name);
        int golden_ticket_value = f.getInt(null);
        this.golden_ticket = () -> golden_ticket_value;
        Class<?> updateType = CommonUtil.getClass("net.minecraft.server.level.ThreadedLevelLightEngine$TaskType");
        Object preUpdate = null;
        Object postUpdate = null;
        for (Object constant : updateType.getEnumConstants()) {
            String name2 = ((Enum)constant).name();
            if (name2.equals("PRE_UPDATE")) {
                preUpdate = constant;
                continue;
            }
            if (!name2.equals("POST_UPDATE")) continue;
            postUpdate = constant;
        }
        if (preUpdate == null) {
            throw new IllegalStateException("ThreadedLevelLightEngine.TaskType has no PRE_UPDATE constant");
        }
        if (postUpdate == null) {
            throw new IllegalStateException("ThreadedLevelLightEngine.TaskType has no POST_UPDATE constant");
        }
        this.light_engine_pre_update = preUpdate;
        this.light_engine_post_update = postUpdate;
        this.light_engine_schedule = CommonUtil.getClass("net.minecraft.server.level.LightEngineThreaded").getDeclaredMethod("a", Integer.TYPE, Integer.TYPE, IntSupplier.class, updateType, Runnable.class);
        this.light_layer_block.setAccessible(true);
        this.light_layer_sky.setAccessible(true);
        this.light_storage.setAccessible(true);
        this.light_storage_array_live.setAccessible(true);
        this.light_engine_schedule.setAccessible(true);
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public boolean isSupported(World world) {
        return true;
    }

    @Override
    public byte[] getSectionBlockLight(World world, int cx, int cy, int cz) {
        ThreadedLevelLightEngineHandle engine = ThreadedLevelLightEngineHandle.forWorld(world);
        try {
            Object layer = this.light_layer_block.get(engine.getRaw());
            return this.handle.getLightData(layer, cx, cy, cz);
        }
        catch (Throwable ex) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to read sky light of [" + cx + "/" + cy + "/" + cz + "]", ex);
            return null;
        }
    }

    @Override
    public byte[] getSectionSkyLight(World world, int cx, int cy, int cz) {
        ThreadedLevelLightEngineHandle engine = ThreadedLevelLightEngineHandle.forWorld(world);
        try {
            Object layer = this.light_layer_sky.get(engine.getRaw());
            return this.handle.getLightData(layer, cx, cy, cz);
        }
        catch (Throwable ex) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to read sky light of [" + cx + "/" + cy + "/" + cz + "]", ex);
            return null;
        }
    }

    @Override
    public CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        return this.scheduleUpdate(world, new UpdateTask(cx, cy, cz, data){

            @Override
            public void run(LightEngineData data) {
                if (data.block_storage_array == null) {
                    throw new UnsupportedOperationException("World does not store block light data");
                }
                LightingHandler_1_14.this.handle.storeBlockLightData(data.lightEngine, this.cx, this.cy, this.cz, this.data);
            }
        });
    }

    @Override
    public CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        return this.scheduleUpdate(world, new UpdateTask(cx, cy, cz, data){

            @Override
            public void run(LightEngineData data) {
                if (data.sky_storage_array == null) {
                    throw new UnsupportedOperationException("World does not store sky light data");
                }
                LightingHandler_1_14.this.handle.storeSkyLightData(data.lightEngine, this.cx, this.cy, this.cz, this.data);
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CompletableFuture<Void> scheduleUpdate(World world, UpdateTask task) {
        Map<World, EngineUpdateTaskLists> map = this.task_lists;
        synchronized (map) {
            EngineUpdateTaskLists lists = this.task_lists.computeIfAbsent(world, x$0 -> new EngineUpdateTaskLists((World)x$0));
            lists.tasks.add(task);
        }
        return task.future;
    }

    @Template.Optional
    @Template.ImportList(value={@Template.Import(value="com.destroystokyo.paper.util.map.QueuedChangesMapLong2Object"), @Template.Import(value="net.minecraft.server.MCUtil"), @Template.Import(value="net.minecraft.core.SectionPos"), @Template.Import(value="net.minecraft.world.level.chunk.DataLayer"), @Template.Import(value="net.minecraft.world.level.LightLayer")})
    @Template.InstanceType(value="net.minecraft.world.level.lighting.LightEngine")
    public static abstract class LightEngineHandle
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static byte[] getLayerData(LightEngineLayer layer, int cx, int cy, int cz) {\n   if (layer == null) {\n       return null;\n   }\n           #if version >= 1.18\n   DataLayer array = layer.getDataLayerData(SectionPos.of(cx, cy, cz));\n   if (array == null) {\n       return null;\n   }\n              return array.getData();\n#else\n              DataLayer array = layer.a(SectionPos.a(cx, cy, cz));\n   if (array == null) {\n       return null;\n   }\n              return array.asBytes();\n#endif\n           }")
        public abstract byte[] getLightData(Object var1, int var2, int var3, int var4);

        @Template.Generated(value="public static void storeSkyLightData(LightEngine engine, int cx, int cy, int cz, byte[] data_bytes) {\n#if version >= 1.18\n    final SectionPos pos = SectionPos.of(cx, cy, cz);\n#else\n               final SectionPos pos = SectionPos.a(cx, cy, cz);\n#endif\n\n           #if version >= 1.18\n    engine.queueSectionData(LightLayer.SKY, pos, new DataLayer(data_bytes), true);\n#elseif version >= 1.16\n    engine.a(LightLayer.SKY, pos, new DataLayer(data_bytes), true);\n#else\n               engine.a(LightLayer.SKY, pos, new DataLayer(data_bytes));\n#endif\n           }")
        public abstract void storeSkyLightData(Object var1, int var2, int var3, int var4, byte[] var5);

        @Template.Generated(value="public static void storeBlockLightData(LightEngine engine, int cx, int cy, int cz, byte[] data_bytes) {\n#if version >= 1.18\n    final SectionPos pos = SectionPos.of(cx, cy, cz);\n#else\n               final SectionPos pos = SectionPos.a(cx, cy, cz);\n#endif\n\n           #if version >= 1.18\n    engine.queueSectionData(LightLayer.BLOCK, pos, new DataLayer(data_bytes), true);\n#elseif version >= 1.16\n    engine.a(LightLayer.BLOCK, pos, new DataLayer(data_bytes), true);\n#else\n               engine.a(LightLayer.BLOCK, pos, new DataLayer(data_bytes));\n#endif\n           }")
        public abstract void storeBlockLightData(Object var1, int var2, int var3, int var4, byte[] var5);
    }

    private static abstract class UpdateTask {
        public final CompletableFuture<Void> future = new CompletableFuture();
        public final int cx;
        public final int cy;
        public final int cz;
        public final byte[] data;
        public Throwable error = null;

        public UpdateTask(int cx, int cy, int cz, byte[] data) {
            this.cx = cx;
            this.cy = cy;
            this.cz = cz;
            this.data = data;
        }

        public abstract void run(LightEngineData var1);
    }

    private final class EngineUpdateTaskLists {
        public final World world;
        public final ThreadedLevelLightEngineHandle engine;
        public final List<UpdateTask> tasks;
        private final AtomicInteger stage;

        public EngineUpdateTaskLists(World world) {
            this.world = world;
            this.engine = ThreadedLevelLightEngineHandle.forWorld(world);
            this.tasks = new ArrayList<UpdateTask>();
            this.stage = new AtomicInteger(0);
            this.schedule();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void preRun() {
            Map map = LightingHandler_1_14.this.task_lists;
            synchronized (map) {
                LightingHandler_1_14.this.task_lists.remove(this.world);
            }
            if (this.stage.compareAndSet(0, 1)) {
                LightEngineData data = new LightEngineData(this.engine.getRaw());
                for (UpdateTask task : this.tasks) {
                    try {
                        task.run(data);
                    }
                    catch (Throwable t) {
                        task.error = t;
                    }
                }
            }
        }

        public void postRun() {
            block5: {
                if (this.stage.compareAndSet(1, 2)) {
                    CommonUtil.nextTick(() -> {
                        for (UpdateTask task : this.tasks) {
                            if (task.error != null) {
                                task.future.completeExceptionally(task.error);
                                continue;
                            }
                            task.future.complete(null);
                        }
                    });
                } else if (this.stage.get() == 0) {
                    try {
                        this.schedule();
                    }
                    catch (IllegalStateException ex) {
                        if (!this.stage.compareAndSet(1, 2)) break block5;
                        CommonUtil.nextTick(() -> {
                            for (UpdateTask task : this.tasks) {
                                task.future.completeExceptionally(ex);
                            }
                        });
                    }
                }
            }
        }

        private void schedule() throws IllegalStateException {
            try {
                IntSupplier priority = LightingHandler_1_14.this.golden_ticket;
                LightingHandler_1_14.this.light_engine_schedule.invoke(this.engine.getRaw(), 0, 0, priority, LightingHandler_1_14.this.light_engine_pre_update, this::preRun);
                LightingHandler_1_14.this.light_engine_schedule.invoke(this.engine.getRaw(), 0, 0, priority, LightingHandler_1_14.this.light_engine_post_update, this::postRun);
            }
            catch (Throwable t) {
                throw new IllegalStateException("Failed to schedule updates", t);
            }
        }
    }

    private final class LightEngineData {
        public final Object lightEngine;
        public final Object block_storage;
        public final Object block_storage_array;
        public final Object sky_storage;
        public final Object sky_storage_array;

        public LightEngineData(Object lightEngine) {
            this.lightEngine = lightEngine;
            Object layer = null;
            Object storage = null;
            Object array = null;
            try {
                layer = LightingHandler_1_14.this.light_layer_block.get(lightEngine);
                if (layer != null) {
                    storage = LightingHandler_1_14.this.light_storage.get(layer);
                    array = LightingHandler_1_14.this.light_storage_array_live.get(storage);
                }
            }
            catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to access block light layer data", t);
            }
            this.block_storage = storage;
            this.block_storage_array = array;
            layer = null;
            storage = null;
            array = null;
            try {
                layer = LightingHandler_1_14.this.light_layer_sky.get(lightEngine);
                if (layer != null) {
                    storage = LightingHandler_1_14.this.light_storage.get(layer);
                    array = LightingHandler_1_14.this.light_storage_array_live.get(storage);
                }
            }
            catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to access sky light layer data", t);
            }
            this.sky_storage = storage;
            this.sky_storage_array = array;
        }
    }
}

