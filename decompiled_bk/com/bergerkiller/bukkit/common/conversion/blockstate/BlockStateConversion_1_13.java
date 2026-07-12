/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 */
package com.bergerkiller.bukkit.common.conversion.blockstate;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.utils.ChunkUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.BlockEntityHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftWorldHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockStateHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.NullInstantiator;
import com.bergerkiller.mountiplex.reflection.util.fast.ConstantReturningInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public class BlockStateConversion_1_13
extends BlockStateConversion {
    private TileState input_state;
    private final World proxy_world;
    private final Object proxy_nms_world;
    private final Object proxy_nms_world_ticklist;
    private final Block proxy_block;
    private final Map<Material, NullInstantiator<BlockState>> blockStateInstantiators;

    private static Invoker<Object> makeNonInstrumentedInvoker(Method method) {
        return (instance, args) -> {
            String name = instance.getClass().getSuperclass().getSimpleName();
            throw new UnsupportedOperationException("Method not instrumented by the " + name + " proxy: " + method);
        };
    }

    public BlockStateConversion_1_13() throws Throwable {
        Field worldField;
        Class<?> craftBlock_type = CommonUtil.getClass("org.bukkit.craftbukkit.block.CraftBlock");
        try {
            worldField = craftBlock_type.getDeclaredField("level");
        }
        catch (NoSuchFieldException ex) {
            worldField = craftBlock_type.getDeclaredField("world");
        }
        worldField.setAccessible(true);
        this.blockStateInstantiators = new EnumMap<Material, NullInstantiator<BlockState>>(Material.class);
        this.proxy_nms_world_ticklist = new ClassInterceptor(){

            @Override
            protected Invoker<?> getCallback(Method method) {
                if (method.getReturnType().equals(Boolean.TYPE)) {
                    return (instance, args) -> Boolean.FALSE;
                }
                if (method.getReturnType().equals(Void.TYPE)) {
                    return (instance, args) -> null;
                }
                return BlockStateConversion_1_13.makeNonInstrumentedInvoker(method);
            }
        }.createInstance(CommonUtil.getClass("net.minecraft.world.ticks.LevelTicks"));
        this.proxy_nms_world = new WorldServerHook(this).createInstance(ServerLevelHandle.T.getType());
        this.proxy_world = (World)new ClassInterceptor(){
            private final Class<?> tileEntityType = CommonUtil.getClass("net.minecraft.world.level.block.entity.BlockEntity");

            @Override
            protected Invoker<?> getCallback(Method method) {
                if (method.getReturnType().equals(this.tileEntityType)) {
                    return (instance, args) -> ((BlockStateConversion_1_13)BlockStateConversion_1_13.this).input_state.tileEntity;
                }
                if (method.getName().equals("getHandle")) {
                    return ConstantReturningInvoker.of(BlockStateConversion_1_13.this.proxy_nms_world);
                }
                if (method.getName().equals("getName")) {
                    return (instance, args) -> ((BlockStateConversion_1_13)BlockStateConversion_1_13.this).input_state.world.getName();
                }
                return BlockStateConversion_1_13.makeNonInstrumentedInvoker(method);
            }
        }.createInstance(CraftWorldHandle.T.getType());
        this.proxy_block = (Block)new ClassInterceptor(){

            @Override
            protected Invoker<?> getCallback(Method method) {
                String name = method.getName();
                if (name.equals("getWorld")) {
                    return ConstantReturningInvoker.of(BlockStateConversion_1_13.this.proxy_world);
                }
                if (name.equals("getChunk")) {
                    return (instance, args) -> ((BlockStateConversion_1_13)BlockStateConversion_1_13.this).input_state.chunk;
                }
                if (name.equals("getType")) {
                    return (instance, args) -> ((BlockStateConversion_1_13)BlockStateConversion_1_13.this).input_state.blockData.getType();
                }
                if (name.equals("getData")) {
                    return (instance, args) -> ((BlockStateConversion_1_13)BlockStateConversion_1_13.this).input_state.blockData.getRawData();
                }
                if (name.equals("getLightLevel")) {
                    return (instance, args) -> ((BlockStateConversion_1_13)BlockStateConversion_1_13.this).input_state.block.getLightLevel();
                }
                if (name.equals("getX")) {
                    return (instance, args) -> ((BlockStateConversion_1_13)BlockStateConversion_1_13.this).input_state.block.getX();
                }
                if (name.equals("getY")) {
                    return (instance, args) -> ((BlockStateConversion_1_13)BlockStateConversion_1_13.this).input_state.block.getY();
                }
                if (name.equals("getZ")) {
                    return (instance, args) -> ((BlockStateConversion_1_13)BlockStateConversion_1_13.this).input_state.block.getZ();
                }
                if (name.equals("getPosition")) {
                    return (instance, args) -> CraftBlockHandle.getBlockPosition(((BlockStateConversion_1_13)BlockStateConversion_1_13.this).input_state.block);
                }
                if (name.equals("getNMS") || name.equals("getBlockState")) {
                    return (instance, args) -> ((BlockStateConversion_1_13)BlockStateConversion_1_13.this).input_state.blockData.getData();
                }
                if (name.equals("getNMSBlock")) {
                    return (instance, args) -> ((BlockStateConversion_1_13)BlockStateConversion_1_13.this).input_state.blockData.getBlockRaw();
                }
                if (name.equals("getState")) {
                    return null;
                }
                if (name.equals("getHandle") || name.equals("getLevel")) {
                    return (instance, args) -> BlockStateConversion_1_13.this.proxy_nms_world;
                }
                if (name.equals("toString")) {
                    return (instance, args) -> {
                        StringBuilder str = new StringBuilder();
                        str.append("CraftBlock{pos=");
                        str.append("BlockPosition{x=").append(((BlockStateConversion_1_13)BlockStateConversion_1_13.this).input_state.block.getX());
                        str.append(",y=").append(((BlockStateConversion_1_13)BlockStateConversion_1_13.this).input_state.block.getY());
                        str.append(",z=").append(((BlockStateConversion_1_13)BlockStateConversion_1_13.this).input_state.block.getY()).append('}');
                        str.append(",type=").append(((BlockStateConversion_1_13)BlockStateConversion_1_13.this).input_state.blockData.getType());
                        str.append(",data=").append(((BlockStateConversion_1_13)BlockStateConversion_1_13.this).input_state.blockData.toString());
                        str.append('}');
                        return str.toString();
                    };
                }
                if (name.equals("getState0")) {
                    return null;
                }
                return BlockStateConversion_1_13.makeNonInstrumentedInvoker(method);
            }
        }.createInstance(craftBlock_type);
        worldField.set(this.proxy_block, this.proxy_nms_world);
    }

    @Override
    public Object blockStateToTileEntity(BlockState state) {
        BlockStateCache cache = BlockStateCache.get(state.getClass());
        Object nmsTileEntity = null;
        if (cache.tileEntityField != null) {
            nmsTileEntity = cache.tileEntityField.get(state);
        }
        if (nmsTileEntity == null) {
            nmsTileEntity = this.getTileEntityFromWorld(state.getBlock());
        }
        return nmsTileEntity;
    }

    @Override
    public BlockState blockToBlockState(Block block) {
        if (!CommonUtil.isMainThread()) {
            throw new IllegalStateException("Asynchronous access is not permitted");
        }
        Object tileEntity = this.getTileEntityFromWorld(block);
        if (tileEntity != null) {
            return this.tileEntityToBlockState(null, block, tileEntity);
        }
        return block.getState();
    }

    @Override
    public BlockState tileEntityToBlockState(Chunk chunk, Object nmsTileEntity) {
        if (nmsTileEntity == null) {
            throw new IllegalArgumentException("Tile Entity is null");
        }
        return this.tileEntityToBlockState(chunk, CraftBlockHandle.createBlockAtTileEntity(nmsTileEntity), nmsTileEntity);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized BlockState tileEntityToBlockState(Chunk chunk, Block block, Object nmsTileEntity) {
        NullInstantiator<Object> state_instantiator;
        BlockData blockData;
        if (chunk == null) {
            chunk = block.getChunk();
        }
        if ((blockData = BlockEntityHandle.T.getBlockDataIfCached.invoke(nmsTileEntity)) == null) {
            blockData = ChunkUtil.getBlockData(chunk, block.getX(), block.getY(), block.getZ());
        }
        if ((state_instantiator = this.blockStateInstantiators.get(blockData.getType())) == null) {
            TileState old_state = this.input_state;
            try {
                this.input_state = new TileState(chunk, block, nmsTileEntity, blockData);
                BlockState result = this.proxy_block.getState();
                state_instantiator = NullInstantiator.of(result.getClass());
                this.blockStateInstantiators.put(blockData.getType(), state_instantiator);
            }
            catch (Throwable t) {
                Logging.LOGGER_CONVERSION.once(Level.SEVERE, "Failed to convert " + nmsTileEntity.getClass().getName() + " to CraftBlockState", t);
                BlockState blockState = CraftBlockStateHandle.createNew(this.input_state.block);
                return blockState;
            }
            finally {
                this.input_state = old_state;
            }
        }
        BlockState result = state_instantiator.create();
        CraftBlockStateHandle.T.init.invoke(result, block, chunk, blockData.getData(), nmsTileEntity);
        return result;
    }

    @Override
    public Object getTileEntityFromWorld(Block block) {
        return this.getTileEntityFromWorld(block.getWorld(), HandleConversion.toBlockPositionHandle(block));
    }

    public Object getTileEntityFromWorld(World world, Object blockPosition) {
        return ((Template.Method)LevelHandle.T.getTileEntity.raw).invoke(HandleConversion.toWorldHandle(world), blockPosition);
    }

    private static final class TileState {
        public final Block block;
        public final Chunk chunk;
        public final World world;
        public final Object tileEntity;
        public final BlockData blockData;

        public TileState(Chunk chunk, Block block, Object nmsTileEntity, BlockData blockData) {
            this.block = block;
            this.chunk = chunk == null ? block.getChunk() : chunk;
            this.world = block.getWorld();
            this.tileEntity = nmsTileEntity;
            this.blockData = blockData;
        }
    }

    @ClassHook.HookPackage(value="net.minecraft.world.level")
    @ClassHook.HookImportList(value={@ClassHook.HookImport(value="net.minecraft.core.BlockPos"), @ClassHook.HookImport(value="net.minecraft.server.MinecraftServer"), @ClassHook.HookImport(value="net.minecraft.world.level.block.Block"), @ClassHook.HookImport(value="net.minecraft.world.level.block.entity.BlockEntity"), @ClassHook.HookImport(value="net.minecraft.world.level.block.state.BlockState")})
    @ClassHook.HookLoadVariables(value="com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER")
    public static class WorldServerHook
    extends ClassHook<WorldServerHook> {
        private final BlockStateConversion_1_13 conversion;
        private static final Class<?> tileEntityType = CommonUtil.getClass("net.minecraft.world.level.block.entity.BlockEntity");
        private static final Class<?> iBlockDataType = CommonUtil.getClass("net.minecraft.world.level.block.state.BlockState");
        private static final Class<?> customRegistryType = CommonUtil.getClass("net.minecraft.core.RegistryAccess");
        private static final Class<?> minecraftServerType = CommonUtil.getClass("net.minecraft.server.MinecraftServer");
        private static final Class<?> tickAccessType = CommonUtil.getClass("net.minecraft.world.ticks.TickAccess");

        public WorldServerHook(BlockStateConversion_1_13 conversion) {
            this.conversion = conversion;
        }

        @Override
        protected Invoker<?> getCallback(Class<?> declaringClass, Method method) {
            Invoker<?> callback = super.getCallback(declaringClass, method);
            if (callback != null) {
                return callback;
            }
            Class<?>[] params = method.getParameterTypes();
            if (params.length == 0 && tickAccessType.isAssignableFrom(method.getReturnType())) {
                return ConstantReturningInvoker.of(this.conversion.proxy_nms_world_ticklist);
            }
            if (method.getReturnType() == Void.TYPE) {
                return ConstantReturningInvoker.of(null);
            }
            if (method.getReturnType().equals(tileEntityType)) {
                return (instance, args) -> ((BlockStateConversion_1_13)this.conversion).input_state.tileEntity;
            }
            if (method.getReturnType().equals(iBlockDataType)) {
                return (instance, args) -> ((BlockStateConversion_1_13)this.conversion).input_state.blockData.getData();
            }
            if (method.getReturnType().equals(customRegistryType)) {
                return (instance, args) -> {
                    try {
                        return method.invoke(HandleConversion.toWorldHandle(((BlockStateConversion_1_13)this.conversion).input_state.world), args);
                    }
                    catch (Throwable t) {
                        throw MountiplexUtil.uncheckedRethrow(t);
                    }
                };
            }
            if (minecraftServerType.isAssignableFrom(method.getReturnType())) {
                return ConstantReturningInvoker.of(MinecraftServerHandle.instance().getRaw());
            }
            return BlockStateConversion_1_13.makeNonInstrumentedInvoker(method);
        }

        @ClassHook.HookMethod(value="public boolean setBlockData:???(BlockPos blockposition, BlockState iblockdata, int updateFlags)", optional=true)
        public boolean setBlockData(Object blockPosition, Object iblockdata, int updateFlags) {
            return true;
        }

        @ClassHook.HookMethodCondition(value="version >= 1.16 && version <= 1.17.1")
        @ClassHook.HookMethod(value="public boolean a(BlockPos blockposition, BlockState iblockdata, int i, int j)")
        public boolean setBlockData(Object blockPosition, Object iblockdata, int updateFlags, int otherFlags) {
            return true;
        }
    }

    private static final class BlockStateCache {
        private static final HashMap<Class<?>, BlockStateCache> cache = new HashMap();
        public final FastField<Object> tileEntityField;

        private BlockStateCache(Class<?> type) {
            this.tileEntityField = ReflectionUtil.getAllNonStaticFields(type).filter(f -> BlockEntityHandle.T.isAssignableFrom(f.getType())).reduce((first, second) -> second).map(FastField::new).orElse(null);
        }

        public static BlockStateCache get(Class<?> type) {
            BlockStateCache result = cache.get(type);
            if (result == null) {
                result = new BlockStateCache(type);
                cache.put(type, result);
            }
            return result;
        }
    }
}

