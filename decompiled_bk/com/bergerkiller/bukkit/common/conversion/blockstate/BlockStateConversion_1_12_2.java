/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 */
package com.bergerkiller.bukkit.common.conversion.blockstate;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.core.BlockPosHandle;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.BlockEntityHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftChunkHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftWorldHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockStateHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.fast.ConstantReturningInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import com.bergerkiller.mountiplex.reflection.util.fast.NullInvoker;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public class BlockStateConversion_1_12_2
extends BlockStateConversion {
    private TileState input_state;
    private final Object proxy_nms_world;
    private final World proxy_world;
    private final Chunk proxy_chunk;
    private final Block proxy_block;

    private static Invoker<Object> makeNonInstrumentedInvoker(Method method) {
        return (instance, args) -> {
            String name = instance.getClass().getSuperclass().getSimpleName();
            throw new UnsupportedOperationException("Method not instrumented by the " + name + " proxy: " + method);
        };
    }

    public BlockStateConversion_1_12_2() throws Throwable {
        Class<?> craftBlock_type = CommonUtil.getClass("org.bukkit.craftbukkit.block.CraftBlock");
        Field chunkField = craftBlock_type.getDeclaredField("chunk");
        chunkField.setAccessible(true);
        this.proxy_chunk = (Chunk)new ClassInterceptor(){

            @Override
            protected Invoker<?> getCallback(Method method) {
                if (method.getName().equals("getCraftWorld")) {
                    return (instance, args) -> BlockStateConversion_1_12_2.this.proxy_world;
                }
                return BlockStateConversion_1_12_2.makeNonInstrumentedInvoker(method);
            }
        }.createInstance(CraftChunkHandle.T.getType());
        this.proxy_nms_world = new NMSWorldHook().createInstance(ServerLevelHandle.T.getType());
        this.proxy_world = (World)new ClassInterceptor(){
            final Class<?> tileEntityType = CommonUtil.getClass("net.minecraft.world.level.block.entity.TileEntity");

            @Override
            protected Invoker<?> getCallback(Method method) {
                if (method.getReturnType().equals(this.tileEntityType)) {
                    return (instance, args) -> ((BlockStateConversion_1_12_2)BlockStateConversion_1_12_2.this).input_state.tileEntity;
                }
                if (method.getName().equals("getHandle")) {
                    return ConstantReturningInvoker.of(BlockStateConversion_1_12_2.this.proxy_nms_world);
                }
                return BlockStateConversion_1_12_2.makeNonInstrumentedInvoker(method);
            }
        }.createInstance(CraftWorldHandle.T.getType());
        this.proxy_block = (Block)new ClassInterceptor(){

            @Override
            protected Invoker<?> getCallback(Method method) {
                String name = method.getName();
                if (name.equals("getWorld")) {
                    return new NullInvoker<World>(BlockStateConversion_1_12_2.this.proxy_world);
                }
                if (name.equals("getChunk")) {
                    return (instance, args) -> ((BlockStateConversion_1_12_2)BlockStateConversion_1_12_2.this).input_state.block.getChunk();
                }
                if (name.equals("getType")) {
                    return (instance, args) -> ((BlockStateConversion_1_12_2)BlockStateConversion_1_12_2.this).input_state.blockData.getType();
                }
                if (name.equals("getTypeId")) {
                    return (instance, args) -> CommonLegacyMaterials.getIdFromMaterial(((BlockStateConversion_1_12_2)BlockStateConversion_1_12_2.this).input_state.blockData.getType());
                }
                if (name.equals("getData")) {
                    return (instance, args) -> (byte)((BlockStateConversion_1_12_2)BlockStateConversion_1_12_2.this).input_state.blockData.getRawData();
                }
                if (name.equals("getLightLevel")) {
                    return (instance, args) -> ((BlockStateConversion_1_12_2)BlockStateConversion_1_12_2.this).input_state.block.getLightLevel();
                }
                if (name.equals("getX")) {
                    return (instance, args) -> ((BlockStateConversion_1_12_2)BlockStateConversion_1_12_2.this).input_state.block.getX();
                }
                if (name.equals("getY")) {
                    return (instance, args) -> ((BlockStateConversion_1_12_2)BlockStateConversion_1_12_2.this).input_state.block.getY();
                }
                if (name.equals("getZ")) {
                    return (instance, args) -> ((BlockStateConversion_1_12_2)BlockStateConversion_1_12_2.this).input_state.block.getZ();
                }
                if (name.equals("getState")) {
                    return null;
                }
                if (name.equals("getState0")) {
                    return null;
                }
                return BlockStateConversion_1_12_2.makeNonInstrumentedInvoker(method);
            }
        }.createInstance(craftBlock_type);
        chunkField.set(this.proxy_block, this.proxy_chunk);
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
        return block.getState();
    }

    @Override
    public synchronized BlockState tileEntityToBlockState(Chunk chunk, Object nmsTileEntity) {
        Block block;
        if (nmsTileEntity == null) {
            throw new IllegalArgumentException("Tile Entity is null");
        }
        BlockPosHandle pos = BlockEntityHandle.T.getPosition.invoke(nmsTileEntity);
        if (chunk == null) {
            Object world = ((Template.Method)BlockEntityHandle.T.getWorld.raw).invoke(nmsTileEntity);
            if (world == null) {
                throw new IllegalArgumentException("Tile Entity has no world set");
            }
            block = WrapperConversion.toWorld(world).getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        } else {
            block = chunk.getBlock(pos.getX(), pos.getY(), pos.getZ());
        }
        return this.tileEntityToBlockState(block, nmsTileEntity);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized BlockState tileEntityToBlockState(Block block, Object nmsTileEntity) {
        TileState old_state = this.input_state;
        try {
            this.input_state = new TileState(block, nmsTileEntity);
            BlockState result = this.proxy_block.getState();
            BlockStateCache cache = BlockStateCache.get(result.getClass());
            for (FastField<World> fastField : cache.worldFields) {
                fastField.set(result, this.input_state.block.getWorld());
            }
            for (BlockState blockState : cache.chunkFields) {
                blockState.set(result, this.input_state.block.getChunk());
            }
            BlockState blockState = result;
            return blockState;
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

    @Override
    public Object getTileEntityFromWorld(Block block) {
        return this.getTileEntityFromWorld(block.getWorld(), HandleConversion.toBlockPositionHandle(block));
    }

    public Object getTileEntityFromWorld(World world, Object blockPosition) {
        return ((Template.Method)LevelHandle.T.getTileEntity.raw).invoke(HandleConversion.toWorldHandle(world), blockPosition);
    }

    private static final class TileState {
        public final Object tileEntity;
        public final BlockData blockData;
        public final Block block;

        public TileState(Block block, Object nmsTileEntity) {
            this.block = block;
            this.tileEntity = nmsTileEntity;
            this.blockData = BlockEntityHandle.T.getBlockData.invoke(nmsTileEntity);
        }
    }

    @ClassHook.HookPackage(value="net.minecraft.server")
    @ClassHook.HookImportList(value={@ClassHook.HookImport(value="net.minecraft.world.level.block.state.BlockState"), @ClassHook.HookImport(value="net.minecraft.core.BlockPos")})
    public class NMSWorldHook
    extends ClassHook<NMSWorldHook> {
        @Override
        protected Invoker<?> getCallback(Class<?> hookedType, Method method) {
            Invoker callback = super.getCallback(hookedType, method);
            return callback != null ? callback : BlockStateConversion_1_12_2.makeNonInstrumentedInvoker(method);
        }

        @ClassHook.HookMethod(value="public net.minecraft.world.level.block.entity.BlockEntity getTileEntity(BlockPos blockposition)")
        public Object getTileEntity(Object blockPosition) {
            return ((BlockStateConversion_1_12_2)BlockStateConversion_1_12_2.this).input_state.tileEntity;
        }

        @ClassHook.HookMethod(value="public MinecraftServer getMinecraftServer()")
        public Object getMinecraftServer() {
            return MinecraftServerHandle.instance().getRaw();
        }

        @ClassHook.HookMethod(value="public BlockState getType(BlockPos blockposition)")
        public Object getType(Object blockPosition) {
            return ((BlockStateConversion_1_12_2)BlockStateConversion_1_12_2.this).input_state.blockData.getData();
        }

        @ClassHook.HookMethod(value="public boolean setTypeAndData(BlockPos blockposition, BlockState iblockdata, int i)")
        public boolean setTypeAndData(Object blockPosition, Object iblockdata, int i) {
            return true;
        }
    }

    private static final class BlockStateCache {
        private static final HashMap<Class<?>, BlockStateCache> cache = new HashMap();
        public final FastField<Chunk>[] chunkFields;
        public final FastField<World>[] worldFields;
        public final FastField<Object> tileEntityField;

        private BlockStateCache(Class<?> type) {
            List allFields = ReflectionUtil.getAllNonStaticFields(type).collect(Collectors.toList());
            this.chunkFields = (FastField[])allFields.stream().filter(f -> Chunk.class.isAssignableFrom(f.getType())).map(FastField::new).toArray(FastField[]::new);
            this.worldFields = (FastField[])allFields.stream().filter(f -> World.class.isAssignableFrom(f.getType())).map(FastField::new).toArray(FastField[]::new);
            this.tileEntityField = allFields.stream().filter(f -> BlockEntityHandle.T.isAssignableFrom(f.getType())).reduce((first, second) -> second).map(FastField::new).orElse(null);
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

