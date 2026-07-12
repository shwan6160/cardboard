/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.Logging
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  com.bergerkiller.generated.net.minecraft.world.level.block.state.BlockStateHandle
 *  com.bergerkiller.mountiplex.reflection.util.FastConstructor
 *  com.bergerkiller.mountiplex.reflection.util.FastMethod
 *  org.bukkit.block.Block
 *  org.bukkit.event.block.BlockPhysicsEvent
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.BlockStateHandle;
import com.bergerkiller.mountiplex.reflection.util.FastConstructor;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.util.ArrayList;
import java.util.logging.Level;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPhysicsEvent;

public abstract class BlockPhysicsEventDataAccessor {
    public static BlockPhysicsEventDataAccessor INSTANCE = BlockPhysicsEventDataAccessor.createAccessor();

    private static BlockPhysicsEventDataAccessor createAccessor() {
        ArrayList<Throwable> failures = new ArrayList<Throwable>();
        try {
            return new BlockPhysicsEventDataAccessorEventField_1_17_to_1_21_11();
        }
        catch (Throwable t) {
            failures.add(t);
            try {
                if (Common.evaluateMCVersion((String)">=", (String)"1.13")) {
                    return new BlockPhysicsEventDataAccessorDefaultModern();
                }
                return new BlockPhysicsEventDataAccessorDefaultLegacy();
            }
            catch (Throwable t2) {
                failures.add(t2);
                TrainCarts.plugin.getLogger().severe("Failed to initialize block physics event data accessor. Failed initializations:");
                for (Throwable t3 : failures) {
                    TrainCarts.plugin.getLogger().log(Level.SEVERE, " - ", t3);
                }
                return new BlockPhysicsEventDataAccessorFallback();
            }
        }
    }

    public abstract BlockData get(BlockPhysicsEvent var1);

    public abstract BlockPhysicsEvent createEvent(Block var1, BlockData var2);

    private static final class BlockPhysicsEventDataAccessorEventField_1_17_to_1_21_11
    extends BlockPhysicsEventDataAccessorDefaultModern {
        private final FastMethod<Object> blockDataGetter;
        private final FastMethod<Object> blockDataGetState;

        public BlockPhysicsEventDataAccessorEventField_1_17_to_1_21_11() throws Throwable {
            Class cbd = CommonUtil.getClass((String)"org.bukkit.craftbukkit.block.data.CraftBlockData");
            this.blockDataGetter = new FastMethod(BlockPhysicsEvent.class.getDeclaredMethod("getChangedBlockData", new Class[0]));
            this.blockDataGetState = new FastMethod(cbd.getDeclaredMethod("getState", new Class[0]));
            this.blockDataGetter.forceInitialization();
            this.blockDataGetState.forceInitialization();
        }

        @Override
        public BlockData get(BlockPhysicsEvent event) {
            try {
                Object bukkit_blockdata = this.blockDataGetter.invoke((Object)event);
                Object iblockdata = this.blockDataGetState.invoke(bukkit_blockdata);
                return BlockData.fromBlockData((Object)iblockdata);
            }
            catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "BlockPhysicsEvent getChangedBlockData failed", t);
                INSTANCE = new BlockPhysicsEventDataAccessorFallback();
                return WorldUtil.getBlockData((Block)event.getBlock());
            }
        }
    }

    private static class BlockPhysicsEventDataAccessorDefaultModern
    extends BlockPhysicsEventDataAccessor {
        private final FastMethod<Object> toBukkitBlockData;
        private final FastConstructor<BlockPhysicsEvent> eventConstructor;

        public BlockPhysicsEventDataAccessorDefaultModern() throws Throwable {
            Class bd = CommonUtil.getClass((String)"org.bukkit.block.data.BlockData");
            Class cbd = CommonUtil.getClass((String)"org.bukkit.craftbukkit.block.data.CraftBlockData");
            this.toBukkitBlockData = new FastMethod(cbd.getDeclaredMethod(Common.IS_PAPERSPIGOT_SERVER && Common.evaluateMCVersion((String)">=", (String)"26.1") ? "createData" : "fromData", BlockStateHandle.T.getType()));
            this.eventConstructor = new FastConstructor(BlockPhysicsEvent.class.getConstructor(Block.class, bd));
            this.toBukkitBlockData.forceInitialization();
            this.eventConstructor.forceInitialization();
        }

        @Override
        public BlockData get(BlockPhysicsEvent event) {
            return WorldUtil.getBlockData((Block)event.getBlock());
        }

        @Override
        public BlockPhysicsEvent createEvent(Block block, BlockData blockData) {
            return (BlockPhysicsEvent)this.eventConstructor.newInstance((Object)block, this.toBukkitBlockData.invoke(null, blockData.getData()));
        }
    }

    private static final class BlockPhysicsEventDataAccessorDefaultLegacy
    extends BlockPhysicsEventDataAccessor {
        private final FastConstructor<BlockPhysicsEvent> eventConstructor = new FastConstructor(BlockPhysicsEvent.class.getDeclaredConstructor(Block.class, Integer.TYPE));

        @Override
        public BlockData get(BlockPhysicsEvent event) {
            return WorldUtil.getBlockData((Block)event.getBlock());
        }

        @Override
        public BlockPhysicsEvent createEvent(Block block, BlockData blockData) {
            return (BlockPhysicsEvent)this.eventConstructor.newInstance((Object)block, (Object)blockData.getType().getId());
        }
    }

    private static final class BlockPhysicsEventDataAccessorFallback
    extends BlockPhysicsEventDataAccessor {
        private BlockPhysicsEventDataAccessorFallback() {
        }

        @Override
        public BlockData get(BlockPhysicsEvent event) {
            return WorldUtil.getBlockData((Block)event.getBlock());
        }

        @Override
        public BlockPhysicsEvent createEvent(Block block, BlockData blockData) {
            throw new UnsupportedOperationException("Error initializing handler");
        }
    }
}

