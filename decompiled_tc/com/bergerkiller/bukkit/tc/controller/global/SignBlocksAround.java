/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.controller.global;

import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.tc.controller.global.SignController;
import com.bergerkiller.bukkit.tc.utils.LongBlockCoordinates;
import java.util.EnumMap;
import java.util.function.LongUnaryOperator;
import org.bukkit.block.BlockFace;

abstract class SignBlocksAround {
    private static final EnumMap<BlockFace, SignBlocksAround> cache = new EnumMap(BlockFace.class);
    private SignBlocksAround opposite;
    private final LongUnaryOperator operator;
    private final BlockFace attachedFace;

    public static SignBlocksAround of(BlockFace attachedFace) {
        return cache.get(attachedFace);
    }

    private SignBlocksAround(BlockFace attachedFace) {
        this.attachedFace = attachedFace;
        this.operator = LongBlockCoordinates.shiftOperator(attachedFace);
    }

    public final BlockFace getAttachedFace() {
        return this.attachedFace;
    }

    public abstract void forAllNeighboursExceptDirection(long var1, SignController.Entry var3, EntryBlockConsumer var4);

    public final void forAllBlocks(SignController.Entry entry, EntryBlockConsumer consumer) {
        long blockKey = entry.blockKey;
        this.forAllNeighboursExceptDirection(blockKey, entry, consumer);
        long blockKeyNeighbour = this.operator.applyAsLong(blockKey);
        this.opposite.forAllNeighboursExceptDirection(blockKeyNeighbour, entry, consumer);
    }

    static {
        cache.put(BlockFace.SELF, new SignBlocksAround(BlockFace.SELF){

            @Override
            public void forAllNeighboursExceptDirection(long blockKey, SignController.Entry entry, EntryBlockConsumer consumer) {
                consumer.accept(entry, blockKey);
                consumer.accept(entry, LongBlockCoordinates.shiftUp(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftDown(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftEast(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftWest(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftSouth(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftNorth(blockKey));
            }
        });
        SignBlocksAround.cache.get((Object)BlockFace.SELF).opposite = new SignBlocksAround(BlockFace.SELF){

            @Override
            public void forAllNeighboursExceptDirection(long blockKey, SignController.Entry entry, EntryBlockConsumer consumer) {
            }
        };
        cache.put(BlockFace.NORTH, new SignBlocksAround(BlockFace.NORTH){

            @Override
            public void forAllNeighboursExceptDirection(long blockKey, SignController.Entry entry, EntryBlockConsumer consumer) {
                consumer.accept(entry, blockKey);
                consumer.accept(entry, LongBlockCoordinates.shiftUp(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftDown(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftEast(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftWest(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftSouth(blockKey));
            }
        });
        cache.put(BlockFace.EAST, new SignBlocksAround(BlockFace.EAST){

            @Override
            public void forAllNeighboursExceptDirection(long blockKey, SignController.Entry entry, EntryBlockConsumer consumer) {
                consumer.accept(entry, blockKey);
                consumer.accept(entry, LongBlockCoordinates.shiftUp(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftDown(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftWest(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftSouth(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftNorth(blockKey));
            }
        });
        cache.put(BlockFace.SOUTH, new SignBlocksAround(BlockFace.SOUTH){

            @Override
            public void forAllNeighboursExceptDirection(long blockKey, SignController.Entry entry, EntryBlockConsumer consumer) {
                consumer.accept(entry, blockKey);
                consumer.accept(entry, LongBlockCoordinates.shiftUp(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftDown(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftEast(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftWest(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftNorth(blockKey));
            }
        });
        cache.put(BlockFace.WEST, new SignBlocksAround(BlockFace.WEST){

            @Override
            public void forAllNeighboursExceptDirection(long blockKey, SignController.Entry entry, EntryBlockConsumer consumer) {
                consumer.accept(entry, blockKey);
                consumer.accept(entry, LongBlockCoordinates.shiftUp(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftDown(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftEast(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftSouth(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftNorth(blockKey));
            }
        });
        cache.put(BlockFace.UP, new SignBlocksAround(BlockFace.UP){

            @Override
            public void forAllNeighboursExceptDirection(long blockKey, SignController.Entry entry, EntryBlockConsumer consumer) {
                consumer.accept(entry, blockKey);
                consumer.accept(entry, LongBlockCoordinates.shiftDown(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftEast(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftWest(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftSouth(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftNorth(blockKey));
            }
        });
        cache.put(BlockFace.DOWN, new SignBlocksAround(BlockFace.DOWN){

            @Override
            public void forAllNeighboursExceptDirection(long blockKey, SignController.Entry entry, EntryBlockConsumer consumer) {
                consumer.accept(entry, blockKey);
                consumer.accept(entry, LongBlockCoordinates.shiftUp(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftEast(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftWest(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftSouth(blockKey));
                consumer.accept(entry, LongBlockCoordinates.shiftNorth(blockKey));
            }
        });
        for (BlockFace face : FaceUtil.BLOCK_SIDES) {
            SignBlocksAround.cache.get((Object)face).opposite = cache.get(face.getOppositeFace());
        }
        for (BlockFace other : BlockFace.values()) {
            if (cache.containsKey(other)) continue;
            cache.put(other, cache.get(BlockFace.SELF));
        }
    }

    @FunctionalInterface
    public static interface EntryBlockConsumer {
        public void accept(SignController.Entry var1, long var2);
    }
}

