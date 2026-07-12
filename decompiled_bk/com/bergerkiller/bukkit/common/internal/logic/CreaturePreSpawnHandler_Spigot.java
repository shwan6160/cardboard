/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.entity.EntityType
 *  org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.events.CommonEventFactory;
import com.bergerkiller.bukkit.common.events.CreaturePreSpawnEvent;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.logic.CreaturePreSpawnHandler;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.core.BlockPosHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerChunkCacheHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreaturePreSpawnHandler_Spigot
extends CreaturePreSpawnHandler {
    private final SafeField<Object> cpsChunkGeneratorField;
    private final Class<? extends ClassHook<?>> hookType;
    private final Function<World, ? extends ClassHook<?>> hookConstructor;
    private static final SpawnerDataHandle spawnerDataHandle = LogicUtil.tryCreate(() -> {
        SpawnerDataHandle h = Template.Class.create(SpawnerDataHandle.class, Common.TEMPLATE_RESOLVER);
        h.forceInitialization();
        return h;
    }, err -> {
        Logging.LOGGER.log(Level.SEVERE, "Failed to initialize template for the mob spawner data", (Throwable)err);
        return null;
    });

    public CreaturePreSpawnHandler_Spigot() throws Throwable {
        if (spawnerDataHandle == null) {
            throw new UnsupportedOperationException("Spawner data template is not available");
        }
        this.cpsChunkGeneratorField = CommonBootstrap.evaluateMCVersion(">=", "1.17") ? (SafeField)LogicUtil.unsafeCast(SafeField.create(ServerChunkCacheHandle.T.getType(), "generator", CommonUtil.getClass("net.minecraft.world.level.chunk.ChunkGenerator"))) : (CommonBootstrap.evaluateMCVersion(">=", "1.9") ? (SafeField)LogicUtil.unsafeCast(SafeField.create(ServerChunkCacheHandle.T.getType(), "chunkGenerator", CommonUtil.getClass("net.minecraft.world.level.chunk.ChunkGenerator"))) : (SafeField)LogicUtil.unsafeCast(SafeField.create(ServerChunkCacheHandle.T.getType(), "chunkProvider", CommonUtil.getClass("net.minecraft.world.level.chunk.ChunkSource"))));
        if (CommonBootstrap.evaluateMCVersion(">=", "1.21.5")) {
            this.hookType = ChunkGeneratorHook_1_21_5.class;
            this.hookConstructor = ChunkGeneratorHook_1_21_5::new;
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.17")) {
            this.hookType = ChunkGeneratorHook_1_17.class;
            this.hookConstructor = ChunkGeneratorHook_1_17::new;
        } else if (CommonBootstrap.evaluateMCVersion("<", "1.16")) {
            this.hookType = ChunkGeneratorHook_1_8_to_1_15_2.class;
            this.hookConstructor = ChunkGeneratorHook_1_8_to_1_15_2::new;
        } else {
            this.hookType = ChunkGeneratorHook_1_16.class;
            this.hookConstructor = ChunkGeneratorHook_1_16::new;
        }
    }

    @Override
    public void onEventHasHandlers() {
        for (World world : WorldUtil.getWorlds()) {
            this.onWorldEnabled(world);
        }
    }

    @Override
    public void onWorldEnabled(World world) {
        if (!CommonUtil.hasHandlers(CreaturePreSpawnEvent.getHandlerList())) {
            return;
        }
        Object cps = CreaturePreSpawnHandler_Spigot.getCPS(world);
        Object generator = this.cpsChunkGeneratorField.get(cps);
        ClassHook<?> hook = ClassHook.get(generator, this.hookType);
        if (hook == null && generator != null && !Modifier.isFinal(generator.getClass().getModifiers())) {
            try {
                hook = this.hookConstructor.apply(world);
                generator = hook.hook(generator);
                this.cpsChunkGeneratorField.set(cps, generator);
            }
            catch (Throwable t) {
                Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Error hooking CreaturePreSpawnHandler", t);
            }
        }
    }

    @Override
    public void onWorldDisabled(World world) {
        Object cps = CreaturePreSpawnHandler_Spigot.getCPS(world);
        Object generator = this.cpsChunkGeneratorField.get(cps);
        ClassHook<?> hook = ClassHook.get(generator, this.hookType);
        if (hook != null) {
            try {
                generator = ClassHook.unhook(generator);
                this.cpsChunkGeneratorField.set(cps, generator);
            }
            catch (Throwable t) {
                Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Error un-hooking CreaturePreSpawnHandler", t);
            }
        }
    }

    private static Object getCPS(World world) {
        return ServerLevelHandle.fromBukkit(world).getChunkProviderServer().getRaw();
    }

    private static List<Object> handleMobsFor(World world, Object blockposition, List<Object> mobs, Function<Object, CommonEntityType> entityTypeFunc) {
        try {
            if (LogicUtil.nullOrEmpty(mobs) || !CommonUtil.hasHandlers(CreaturePreSpawnEvent.getHandlerList())) {
                return mobs;
            }
            BlockPosHandle pos = BlockPosHandle.createHandle(blockposition);
            CommonEventFactory eventFactory = CommonPlugin.getInstance().getEventFactory();
            List<Object> result = mobs;
            int numMobs = mobs.size();
            for (int n = 0; n < numMobs; ++n) {
                Object rawSpawnCluster = mobs.get(n);
                EntityType entityType = entityTypeFunc.apply((Object)rawSpawnCluster).entityType;
                if (eventFactory.handleCreaturePreSpawn(world, pos.getX(), pos.getY(), pos.getZ(), entityType, CreatureSpawnEvent.SpawnReason.NATURAL)) {
                    if (result == mobs) continue;
                    result.add(rawSpawnCluster);
                    continue;
                }
                if (result != mobs) continue;
                result = new ArrayList<Object>(mobs.subList(0, n));
            }
            return result;
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to handle mob pre-spawn event", t);
            return mobs;
        }
    }

    @Template.ImportList(value={@Template.Import(value="com.bergerkiller.bukkit.common.entity.CommonEntityType"), @Template.Import(value="net.minecraft.world.entity.EntityType")})
    @Template.InstanceType(value="net.minecraft.world.level.biome.MobSpawnSettings$SpawnerData")
    public static abstract class SpawnerDataHandle
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static CommonEntityType getEntityType(MobSpawnSettings$SpawnerData spawnerData) {\n#if version >= 1.13\n    EntityType entityType;\n  #if version >= 1.21.5\n    entityType = spawnerData.type();\n  #else\n               #select version >=\n    #case 1.17: #require net.minecraft.world.level.biome.MobSpawnSettings$SpawnerData public net.minecraft.world.entity.EntityType entityType:type;\n    #case 1.16: #require net.minecraft.world.level.biome.MobSpawnSettings$SpawnerData public net.minecraft.world.entity.EntityType entityType:c;\n    #case else: #require net.minecraft.world.level.biome.MobSpawnSettings$SpawnerData public net.minecraft.world.entity.EntityType entityType:b;\n    #endselect\n    entityType = spawnerData#entityType;\n  #endif\n\n               return CommonEntityType.byNMSEntityTypeRaw(entityType);\n#else\n               #require net.minecraft.world.level.biome.MobSpawnSettings$SpawnerData public java.lang.Class<? extends net.minecraft.world.entity.Mob> entityClass:b;\n    Class entityClass = spawnerData#entityClass;\n    return CommonEntityType.byNMSEntityClass(entityClass);\n#endif\n           }")
        public abstract CommonEntityType getEntityType(Object var1);
    }

    @ClassHook.HookPackage(value="net.minecraft.world.level.chunk")
    @ClassHook.HookImportList(value={@ClassHook.HookImport(value="net.minecraft.util.random.WeightedList"), @ClassHook.HookImport(value="net.minecraft.core.Holder"), @ClassHook.HookImport(value="net.minecraft.world.level.biome.Biome"), @ClassHook.HookImport(value="net.minecraft.world.level.StructureManager"), @ClassHook.HookImport(value="net.minecraft.world.entity.MobCategory"), @ClassHook.HookImport(value="net.minecraft.core.BlockPos")})
    public static class ChunkGeneratorHook_1_21_5
    extends ClassHook<ChunkGeneratorHook_1_21_5> {
        private static final WeightedListHandle weightedListHandle = Template.Class.create(WeightedListHandle.class, Common.TEMPLATE_RESOLVER);
        private final World world;

        public ChunkGeneratorHook_1_21_5(World world) {
            this.world = world;
        }

        @ClassHook.HookMethod(value="public WeightedList getMobsAt(Holder<Biome> biomeBaseHolder, StructureManager structManager, MobCategory enumcreaturetype, BlockPos blockposition)")
        public Object getMobsAt_1_18_2(Object biomeBaseHolder, Object structureManager, Object enumcreaturetype, Object blockposition) {
            Object weightedList = ((ChunkGeneratorHook_1_21_5)this.base).getMobsAt_1_18_2(biomeBaseHolder, structureManager, enumcreaturetype, blockposition);
            return this.processWeightedList(weightedList, blockposition);
        }

        private Object processWeightedList(Object weightedList, Object blockposition) {
            List<Object> weights = weightedListHandle.extractList(weightedList);
            List newMobWeights = CreaturePreSpawnHandler_Spigot.handleMobsFor(this.world, blockposition, weights, weightedEntry -> {
                Object spawnerDataRaw = ChunkGeneratorHook_1_21_5.weightedListHandle.getWeightedEntryValue(weightedEntry);
                return spawnerDataHandle.getEntityType(spawnerDataRaw);
            });
            if (newMobWeights != weights) {
                return weightedListHandle.createWeightedList(newMobWeights);
            }
            return weightedList;
        }

        @Template.InstanceType(value="net.minecraft.util.random.WeightedList")
        @Template.Import(value="net.minecraft.util.random.Weighted")
        public static abstract class WeightedListHandle
        extends Template.Class<Template.Handle> {
            @Template.Generated(value="public static WeightedList createWeightedList(List<Weighted<?>> list) {\n    return WeightedList.of(list);\n}")
            public abstract Object createWeightedList(List<?> var1);

            @Template.Generated(value="public static List extractList(WeightedList weightedList) {\n    return weightedList.unwrap();\n}")
            public abstract List<Object> extractList(Object var1);

            @Template.Generated(value="public static Object getWeightedEntryValue(Weighted weighted) {\n    return weighted.value();\n}")
            public abstract Object getWeightedEntryValue(Object var1);
        }
    }

    @ClassHook.HookPackage(value="net.minecraft.world.level.chunk")
    @ClassHook.HookImportList(value={@ClassHook.HookImport(value="net.minecraft.util.random.WeightedRandomList"), @ClassHook.HookImport(value="net.minecraft.core.Holder"), @ClassHook.HookImport(value="net.minecraft.world.level.biome.Biome"), @ClassHook.HookImport(value="net.minecraft.world.level.StructureManager"), @ClassHook.HookImport(value="net.minecraft.world.entity.MobCategory"), @ClassHook.HookImport(value="net.minecraft.core.BlockPos")})
    public static class ChunkGeneratorHook_1_17
    extends ClassHook<ChunkGeneratorHook_1_17> {
        private static final WeightedRandomListHandle weightedRandomListHandle = Template.Class.create(WeightedRandomListHandle.class, Common.TEMPLATE_RESOLVER);
        private final World world;

        public ChunkGeneratorHook_1_17(World world) {
            this.world = world;
        }

        @ClassHook.HookMethodCondition(value="version >= 1.18.2")
        @ClassHook.HookMethod(value="public WeightedRandomList getMobsAt(Holder<Biome> biomeBaseHolder, StructureManager structManager, MobCategory enumcreaturetype, BlockPos blockposition)")
        public Object getMobsAt_1_18_2(Object biomeBaseHolder, Object structureManager, Object enumcreaturetype, Object blockposition) {
            Object weightedList = ((ChunkGeneratorHook_1_17)this.base).getMobsAt_1_18_2(biomeBaseHolder, structureManager, enumcreaturetype, blockposition);
            return this.processWeightedList(weightedList, blockposition);
        }

        @ClassHook.HookMethodCondition(value="version >= 1.18 && version < 1.18.2")
        @ClassHook.HookMethod(value="public WeightedRandomList getMobsAt(Biome biomeBase, StructureManager structManager, MobCategory enumcreaturetype, BlockPos blockposition)")
        public Object getMobsAt_1_18(Object biomeBase, Object structureManager, Object enumcreaturetype, Object blockposition) {
            Object weightedList = ((ChunkGeneratorHook_1_17)this.base).getMobsAt_1_18(biomeBase, structureManager, enumcreaturetype, blockposition);
            return this.processWeightedList(weightedList, blockposition);
        }

        @ClassHook.HookMethodCondition(value="version < 1.18")
        @ClassHook.HookMethod(value="public WeightedRandomList getMobsFor(Biome biome, StructureManager structManager, MobCategory enumcreaturetype, BlockPos blockposition)")
        public Object getMobsFor_1_17(Object biomeBase, Object structureManager, Object enumcreaturetype, Object blockposition) {
            Object weightedList = ((ChunkGeneratorHook_1_17)this.base).getMobsFor_1_17(biomeBase, structureManager, enumcreaturetype, blockposition);
            return this.processWeightedList(weightedList, blockposition);
        }

        private Object processWeightedList(Object weightedList, Object blockposition) {
            List<Object> mobs = weightedRandomListHandle.extractList(weightedList);
            List newMobs = CreaturePreSpawnHandler_Spigot.handleMobsFor(this.world, blockposition, mobs, spawnerDataHandle::getEntityType);
            if (newMobs != mobs) {
                return weightedRandomListHandle.createWeightedRandomList(newMobs);
            }
            return weightedList;
        }

        @Template.InstanceType(value="net.minecraft.util.random.WeightedRandomList")
        public static abstract class WeightedRandomListHandle
        extends Template.Class<Template.Handle> {
            @Template.Generated(value="public static WeightedRandomList createWeightedRandomList(List<?> list) {\n#if version >= 1.18\n    return WeightedRandomList.create(list);\n#else\n                   return WeightedRandomList.a(list);\n#endif\n               }")
            public abstract Object createWeightedRandomList(List<?> var1);

            @Template.Generated(value="public static List extractList(WeightedRandomList weightedRandomList) {\n#if version >= 1.18\n    return weightedRandomList.unwrap();\n#else\n                   return weightedRandomList.d();\n#endif\n               }")
            public abstract List<Object> extractList(Object var1);
        }
    }

    @ClassHook.HookPackage(value="net.minecraft.server")
    @ClassHook.HookImportList(value={@ClassHook.HookImport(value="net.minecraft.world.entity.MobCategory"), @ClassHook.HookImport(value="net.minecraft.core.BlockPos")})
    public static class ChunkGeneratorHook_1_8_to_1_15_2
    extends ClassHook<ChunkGeneratorHook_1_8_to_1_15_2> {
        private final World world;

        public ChunkGeneratorHook_1_8_to_1_15_2(World world) {
            this.world = world;
        }

        @ClassHook.HookMethod(value="public List getMobsFor(MobCategory enumcreaturetype, BlockPos blockposition)")
        public List<Object> getMobsFor(Object enumcreaturetype, Object blockposition) {
            List<Object> mobs = ((ChunkGeneratorHook_1_8_to_1_15_2)this.base).getMobsFor(enumcreaturetype, blockposition);
            return CreaturePreSpawnHandler_Spigot.handleMobsFor(this.world, blockposition, mobs, spawnerDataHandle::getEntityType);
        }
    }

    @ClassHook.HookPackage(value="net.minecraft.server")
    @ClassHook.HookImportList(value={@ClassHook.HookImport(value="net.minecraft.world.level.biome.Biome"), @ClassHook.HookImport(value="net.minecraft.world.level.StructureManager"), @ClassHook.HookImport(value="net.minecraft.world.entity.MobCategory"), @ClassHook.HookImport(value="net.minecraft.core.BlockPos")})
    public static class ChunkGeneratorHook_1_16
    extends ClassHook<ChunkGeneratorHook_1_16> {
        private final World world;

        public ChunkGeneratorHook_1_16(World world) {
            this.world = world;
        }

        @ClassHook.HookMethod(value="public List getMobsFor(Biome biomeBase, StructureManager structManager, MobCategory enumcreaturetype, BlockPos blockposition)")
        public List<Object> getMobsFor(Object biomeBase, Object structureManager, Object enumcreaturetype, Object blockposition) {
            List<Object> mobs = ((ChunkGeneratorHook_1_16)this.base).getMobsFor(biomeBase, structureManager, enumcreaturetype, blockposition);
            return CreaturePreSpawnHandler_Spigot.handleMobsFor(this.world, blockposition, mobs, spawnerDataHandle::getEntityType);
        }
    }
}

