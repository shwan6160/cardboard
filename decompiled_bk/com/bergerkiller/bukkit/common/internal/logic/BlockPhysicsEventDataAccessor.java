/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.event.block.BlockPhysicsEvent
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.BlockStateHandle;
import com.bergerkiller.mountiplex.reflection.util.FastConstructor;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.lang.reflect.Method;
import java.util.logging.Level;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPhysicsEvent;

public abstract class BlockPhysicsEventDataAccessor {
    public static BlockPhysicsEventDataAccessor INSTANCE;

    public static void init() {
    }

    public abstract BlockData get(BlockPhysicsEvent var1);

    public abstract BlockPhysicsEvent createEvent(Block var1, BlockData var2);

    static {
        try {
            INSTANCE = new BlockPhysicsEventDataAccessorEventField();
        }
        catch (Throwable t) {
            try {
                INSTANCE = Common.evaluateMCVersion(">=", "1.13") ? new BlockPhysicsEventDataAccessorDefaultModern() : new BlockPhysicsEventDataAccessorDefaultLegacy();
            }
            catch (Throwable t2) {
                CommonPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to initialize block physics event data accessor", t);
                INSTANCE = new BlockPhysicsEventDataAccessorFallback();
            }
        }
    }

    private static final class BlockPhysicsEventDataAccessorEventField
    extends BlockPhysicsEventDataAccessorDefaultModern {
        private final FastMethod<Object> blockDataGetter;
        private final FastMethod<Object> blockDataGetState;

        public BlockPhysicsEventDataAccessorEventField() throws Throwable {
            Class<?> cbd = CommonUtil.getClass("org.bukkit.craftbukkit.block.data.CraftBlockData");
            this.blockDataGetter = new FastMethod(BlockPhysicsEvent.class.getDeclaredMethod("getChangedBlockData", new Class[0]));
            this.blockDataGetState = new FastMethod(cbd.getDeclaredMethod("getState", new Class[0]));
            this.blockDataGetter.forceInitialization();
            this.blockDataGetState.forceInitialization();
        }

        @Override
        public BlockData get(BlockPhysicsEvent event) {
            try {
                Object bukkit_blockdata = this.blockDataGetter.invoke(event);
                Object iblockdata = this.blockDataGetState.invoke(bukkit_blockdata);
                return BlockData.fromBlockData(iblockdata);
            }
            catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "BlockPhysicsEvent getChangedBlockData failed", t);
                INSTANCE = new BlockPhysicsEventDataAccessorFallback();
                return WorldUtil.getBlockData(event.getBlock());
            }
        }
    }

    private static class BlockPhysicsEventDataAccessorDefaultModern
    extends BlockPhysicsEventDataAccessor {
        private final FastMethod<Object> toBukkitBlockData;
        private final FastConstructor<BlockPhysicsEvent> eventConstructor;

        public BlockPhysicsEventDataAccessorDefaultModern() throws Throwable {
            Method fromDataMethod;
            Class<?> bd = CommonUtil.getClass("org.bukkit.block.data.BlockData");
            Class<?> cbd = CommonUtil.getClass("org.bukkit.craftbukkit.block.data.CraftBlockData");
            try {
                fromDataMethod = cbd.getDeclaredMethod("createData", BlockStateHandle.T.getType());
            }
            catch (NoSuchMethodException ex) {
                fromDataMethod = cbd.getDeclaredMethod("fromData", BlockStateHandle.T.getType());
            }
            this.toBukkitBlockData = new FastMethod(fromDataMethod);
            this.eventConstructor = new FastConstructor(BlockPhysicsEvent.class.getConstructor(Block.class, bd));
            this.toBukkitBlockData.forceInitialization();
            this.eventConstructor.forceInitialization();
        }

        @Override
        public BlockData get(BlockPhysicsEvent event) {
            return WorldUtil.getBlockData(event.getBlock());
        }

        @Override
        public BlockPhysicsEvent createEvent(Block block, BlockData blockData) {
            return this.eventConstructor.newInstance(block, this.toBukkitBlockData.invoke(null, blockData.getData()));
        }
    }

    private static final class BlockPhysicsEventDataAccessorDefaultLegacy
    extends BlockPhysicsEventDataAccessor {
        private final FastConstructor<BlockPhysicsEvent> eventConstructor = new FastConstructor(BlockPhysicsEvent.class.getDeclaredConstructor(Block.class, Integer.TYPE));

        @Override
        public BlockData get(BlockPhysicsEvent event) {
            return WorldUtil.getBlockData(event.getBlock());
        }

        @Override
        public BlockPhysicsEvent createEvent(Block block, BlockData blockData) {
            return this.eventConstructor.newInstance(block, blockData.getType().getId());
        }
    }

    private static final class BlockPhysicsEventDataAccessorFallback
    extends BlockPhysicsEventDataAccessor {
        private BlockPhysicsEventDataAccessorFallback() {
        }

        @Override
        public BlockData get(BlockPhysicsEvent event) {
            return WorldUtil.getBlockData(event.getBlock());
        }

        @Override
        public BlockPhysicsEvent createEvent(Block block, BlockData blockData) {
            throw new UnsupportedOperationException("Error initializing handler");
        }
    }
}

