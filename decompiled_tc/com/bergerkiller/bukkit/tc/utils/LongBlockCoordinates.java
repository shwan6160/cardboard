/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import java.util.function.LongUnaryOperator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class LongBlockCoordinates {
    public static final int PACKED_X_LENGTH = 26;
    public static final int PACKED_Z_LENGTH = 26;
    public static final int PACKED_Y_LENGTH = 12;
    public static final long PACKED_X_MASK = 0x3FFFFFFL;
    public static final long PACKED_Y_MASK = 4095L;
    public static final long PACKED_Z_MASK = 0x3FFFFFFL;
    public static final int Y_OFFSET = 0;
    public static final int Z_OFFSET = 12;
    public static final int X_OFFSET = 38;

    public static long map(Block block) {
        return LongBlockCoordinates.map(block.getX(), block.getY(), block.getZ());
    }

    public static long map(int x, int y, int z) {
        long l = 0L;
        l |= ((long)x & 0x3FFFFFFL) << 38;
        l |= ((long)y & 0xFFFL) << 0;
        return l |= ((long)z & 0x3FFFFFFL) << 12;
    }

    public static long shiftEast(long key) {
        return key & 0x3FFFFFFFFFL | key + 0x4000000000L & 0xFFFFFFC000000000L;
    }

    public static long shiftWest(long key) {
        return key & 0x3FFFFFFFFFL | key - 0x4000000000L & 0xFFFFFFC000000000L;
    }

    public static long shiftUp(long key) {
        return key & 0xFFFFFFFFFFFFF000L | key + 1L & 0xFFFL;
    }

    public static long shiftDown(long key) {
        return key & 0xFFFFFFFFFFFFF000L | key - 1L & 0xFFFL;
    }

    public static long shiftSouth(long key) {
        return key & 0xFFFFFFC000000FFFL | key + 4096L & 0x3FFFFFF000L;
    }

    public static long shiftNorth(long key) {
        return key & 0xFFFFFFC000000FFFL | key - 4096L & 0x3FFFFFF000L;
    }

    public static LongUnaryOperator shiftOperator(BlockFace face) {
        switch (face) {
            case DOWN: {
                return LongBlockCoordinates::shiftDown;
            }
            case UP: {
                return LongBlockCoordinates::shiftUp;
            }
            case NORTH: {
                return LongBlockCoordinates::shiftNorth;
            }
            case EAST: {
                return LongBlockCoordinates::shiftEast;
            }
            case SOUTH: {
                return LongBlockCoordinates::shiftSouth;
            }
            case WEST: {
                return LongBlockCoordinates::shiftWest;
            }
            case SELF: {
                return LongUnaryOperator.identity();
            }
        }
        BlockFace f = face;
        return key -> LongBlockCoordinates.map(LongBlockCoordinates.getX(key) + f.getModX(), LongBlockCoordinates.getY(key) + f.getModY(), LongBlockCoordinates.getZ(key) + f.getModZ());
    }

    public static void forAllBlockSidesAndSelf(long key, BlockSideConsumer consumer) {
        consumer.accept(BlockFace.SELF, key);
        consumer.accept(BlockFace.NORTH, LongBlockCoordinates.shiftNorth(key));
        consumer.accept(BlockFace.EAST, LongBlockCoordinates.shiftEast(key));
        consumer.accept(BlockFace.SOUTH, LongBlockCoordinates.shiftSouth(key));
        consumer.accept(BlockFace.WEST, LongBlockCoordinates.shiftWest(key));
        consumer.accept(BlockFace.UP, LongBlockCoordinates.shiftUp(key));
        consumer.accept(BlockFace.DOWN, LongBlockCoordinates.shiftDown(key));
    }

    public static BlockFace findDirection(long from, long to) {
        long diff = to - (from & 0xFFFFFFC000000000L) & 0xFFFFFFC000000000L | to - (from & 0xFFFL) & 0xFFFL | to - (from & 0x3FFFFFF000L) & 0x3FFFFFF000L;
        switch ((int)(diff ^ diff >> 32)) {
            case 0: {
                if (diff == 0L) {
                    return BlockFace.SELF;
                }
            }
            case -4033: {
                if (diff == 0x3FFFFFF000L) {
                    return BlockFace.NORTH;
                }
            }
            case 64: {
                if (diff == 0x4000000000L) {
                    return BlockFace.EAST;
                }
            }
            case 4096: {
                if (diff == 4096L) {
                    return BlockFace.SOUTH;
                }
            }
            case -64: {
                if (diff == -274877906944L) {
                    return BlockFace.WEST;
                }
            }
            case 1: {
                if (diff == 1L) {
                    return BlockFace.UP;
                }
            }
            case 4095: {
                if (diff != 4095L) break;
                return BlockFace.DOWN;
            }
        }
        return null;
    }

    public static int getChunkEdgeDistance(long key) {
        int relx = (int)(key >> 38) & 0xF;
        int relz = (int)(key >> 12) & 0xF;
        if ((relx & 8) != 0) {
            relx = 15 - relx;
        }
        if ((relz & 8) != 0) {
            relz = 15 - relz;
        }
        return Math.min(relx, relz);
    }

    public static int getX(long i) {
        return (int)(i << 0 >> 38);
    }

    public static int getY(long i) {
        return (int)(i << 52 >> 52);
    }

    public static int getZ(long i) {
        return (int)(i << 26 >> 38);
    }

    public static IntVector3 get(long i) {
        return new IntVector3(LongBlockCoordinates.getX(i), LongBlockCoordinates.getY(i), LongBlockCoordinates.getZ(i));
    }

    @FunctionalInterface
    public static interface BlockSideConsumer {
        public void accept(BlockFace var1, long var2);
    }
}

