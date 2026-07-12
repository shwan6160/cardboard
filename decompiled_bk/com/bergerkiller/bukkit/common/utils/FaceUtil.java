/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.utils.MathUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class FaceUtil {
    public static final BlockFace[] AXIS;
    public static final BlockFace[] RADIAL;
    public static final BlockFace[] BLOCK_SIDES;
    public static final BlockFace[] ATTACHEDFACES;
    public static final BlockFace[] ATTACHEDFACESDOWN;
    private static final double SUBCARDINAL_LIMIT = 2.414213562373095;
    private static final int[] notches;

    public static int faceToNotch(BlockFace face) {
        return notches[face.ordinal()];
    }

    public static boolean isAlongX(BlockFace face) {
        return face.getModX() != 0 && face.getModZ() == 0;
    }

    public static boolean isAlongY(BlockFace face) {
        return FaceUtil.isVertical(face);
    }

    public static boolean isAlongZ(BlockFace face) {
        return face.getModZ() != 0 && face.getModX() == 0;
    }

    public static BlockFace notchToFace(int notch) {
        return RADIAL[notch & 7];
    }

    public static BlockFace notchFaceOffset(BlockFace face, int notchOffset) {
        return FaceUtil.notchToFace(FaceUtil.faceToNotch(face) + notchOffset);
    }

    public static BlockFace rotate(BlockFace from, int notchCount) {
        return FaceUtil.notchToFace(FaceUtil.faceToNotch(from) + notchCount);
    }

    public static BlockFace combine(BlockFace from, BlockFace to) {
        if (from == BlockFace.NORTH) {
            if (to == BlockFace.WEST) {
                return BlockFace.NORTH_WEST;
            }
            if (to == BlockFace.EAST) {
                return BlockFace.NORTH_EAST;
            }
        } else if (from == BlockFace.EAST) {
            if (to == BlockFace.NORTH) {
                return BlockFace.NORTH_EAST;
            }
            if (to == BlockFace.SOUTH) {
                return BlockFace.SOUTH_EAST;
            }
        } else if (from == BlockFace.SOUTH) {
            if (to == BlockFace.WEST) {
                return BlockFace.SOUTH_WEST;
            }
            if (to == BlockFace.EAST) {
                return BlockFace.SOUTH_EAST;
            }
        } else if (from == BlockFace.WEST) {
            if (to == BlockFace.NORTH) {
                return BlockFace.NORTH_WEST;
            }
            if (to == BlockFace.SOUTH) {
                return BlockFace.SOUTH_WEST;
            }
        }
        return from;
    }

    public static BlockFace[] getFaces(BlockFace main) {
        switch (main) {
            case SOUTH_EAST: {
                return new BlockFace[]{BlockFace.SOUTH, BlockFace.EAST};
            }
            case SOUTH_WEST: {
                return new BlockFace[]{BlockFace.SOUTH, BlockFace.WEST};
            }
            case NORTH_EAST: {
                return new BlockFace[]{BlockFace.NORTH, BlockFace.EAST};
            }
            case NORTH_WEST: {
                return new BlockFace[]{BlockFace.NORTH, BlockFace.WEST};
            }
        }
        return new BlockFace[]{main, main.getOppositeFace()};
    }

    public static BlockFace getRailsCartDirection(BlockFace raildirection) {
        switch (raildirection) {
            case SOUTH_WEST: 
            case NORTH_EAST: {
                return BlockFace.NORTH_WEST;
            }
            case SOUTH_EAST: 
            case NORTH_WEST: {
                return BlockFace.SOUTH_WEST;
            }
        }
        return raildirection;
    }

    public static BlockFace toRailsDirection(BlockFace direction) {
        switch (direction) {
            case NORTH: {
                return BlockFace.SOUTH;
            }
            case WEST: {
                return BlockFace.EAST;
            }
        }
        return direction;
    }

    public static boolean isSubCardinal(BlockFace face) {
        switch (face) {
            case SOUTH_EAST: 
            case SOUTH_WEST: 
            case NORTH_EAST: 
            case NORTH_WEST: {
                return true;
            }
        }
        return false;
    }

    public static boolean isVertical(BlockFace face) {
        return face == BlockFace.UP || face == BlockFace.DOWN;
    }

    public static BlockFace getVertical(boolean up) {
        return up ? BlockFace.UP : BlockFace.DOWN;
    }

    public static BlockFace getVertical(double dy) {
        return FaceUtil.getVertical(dy >= 0.0);
    }

    public static boolean hasSubDifference(BlockFace face1, BlockFace face2) {
        return FaceUtil.getFaceYawDifference(face1, face2) <= 45;
    }

    public static Vector faceToVector(BlockFace face, double length) {
        return FaceUtil.faceToVector(face).multiply(length);
    }

    public static Vector faceToVector(BlockFace face) {
        return new Vector(face.getModX(), face.getModY(), face.getModZ());
    }

    public static BlockFace getDirection(Location from, Location to, boolean useSubCardinalDirections) {
        return FaceUtil.getDirection(to.getX() - from.getX(), to.getZ() - from.getZ(), useSubCardinalDirections);
    }

    public static BlockFace getDirection(Block from, Block to, boolean useSubCardinalDirections) {
        return FaceUtil.getDirection(to.getX() - from.getX(), to.getZ() - from.getZ(), useSubCardinalDirections);
    }

    public static BlockFace getDirection(Vector movement) {
        return FaceUtil.getDirection(movement, true);
    }

    public static BlockFace getDirection(Vector movement, boolean useSubCardinalDirections) {
        return FaceUtil.getDirection(movement.getX(), movement.getZ(), useSubCardinalDirections);
    }

    public static BlockFace getDirection(double dx, double dz, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections) {
            double dz_lim = dz * 2.414213562373095;
            if (dz < 0.0) {
                if (dx < dz_lim) {
                    return BlockFace.WEST;
                }
                if (-dx < dz_lim) {
                    return BlockFace.EAST;
                }
                if (dx < 0.0) {
                    return dz < dx * 2.414213562373095 ? BlockFace.NORTH : BlockFace.NORTH_WEST;
                }
                return -dz >= dx * 2.414213562373095 ? BlockFace.NORTH : BlockFace.NORTH_EAST;
            }
            if (dx > dz_lim) {
                return BlockFace.EAST;
            }
            if (-dx > dz_lim) {
                return BlockFace.WEST;
            }
            if (dx > 0.0) {
                return dz > dx * 2.414213562373095 ? BlockFace.SOUTH : BlockFace.SOUTH_EAST;
            }
            return dz >= -(dx * 2.414213562373095) ? BlockFace.SOUTH : BlockFace.SOUTH_WEST;
        }
        if (dz < 0.0) {
            if (dx < dz) {
                return BlockFace.WEST;
            }
            if (-dx < dz) {
                return BlockFace.EAST;
            }
            return BlockFace.NORTH;
        }
        if (dx > dz) {
            return BlockFace.EAST;
        }
        if (-dx > dz) {
            return BlockFace.WEST;
        }
        return BlockFace.SOUTH;
    }

    public static int getFaceYawDifference(BlockFace face1, BlockFace face2) {
        return MathUtil.getAngleDifference(FaceUtil.faceToYaw(face1), FaceUtil.faceToYaw(face2));
    }

    public static double cos(BlockFace face) {
        switch (face) {
            case SOUTH_WEST: 
            case NORTH_WEST: {
                return -0.707106781;
            }
            case SOUTH_EAST: 
            case NORTH_EAST: {
                return 0.707106781;
            }
            case EAST: {
                return 1.0;
            }
            case WEST: {
                return -1.0;
            }
        }
        return 0.0;
    }

    public static double sin(BlockFace face) {
        switch (face) {
            case NORTH_EAST: 
            case NORTH_WEST: {
                return -0.707106781;
            }
            case SOUTH_EAST: 
            case SOUTH_WEST: {
                return 0.707106781;
            }
            case NORTH: {
                return -1.0;
            }
            case SOUTH: {
                return 1.0;
            }
        }
        return 0.0;
    }

    public static int faceToYaw(BlockFace face) {
        return MathUtil.wrapAngle(45 * FaceUtil.faceToNotch(face));
    }

    public static BlockFace yawToFace(float yaw) {
        return FaceUtil.yawToFace(yaw, true);
    }

    public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections) {
            return RADIAL[Math.round(yaw / 45.0f) & 7];
        }
        return AXIS[Math.round(yaw / 90.0f) & 3];
    }

    public static BlockFace vectorToBlockFace(Vector vector, boolean useSubCardinalDirections) {
        return FaceUtil.vectorToBlockFace(vector.getX(), vector.getY(), vector.getZ(), useSubCardinalDirections);
    }

    public static BlockFace vectorToBlockFace(double dx, double dy, double dz, boolean useSubCardinalDirections) {
        double sqleny = dy * dy;
        double sqlenxz = dx * dx + dz * dz;
        if (sqleny > sqlenxz + 1.0E-6) {
            return FaceUtil.getVertical(dy);
        }
        return FaceUtil.getDirection(dx, dz, useSubCardinalDirections);
    }

    static {
        int i;
        AXIS = new BlockFace[4];
        RADIAL = new BlockFace[]{BlockFace.WEST, BlockFace.NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST};
        BLOCK_SIDES = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
        ATTACHEDFACES = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP};
        ATTACHEDFACESDOWN = BLOCK_SIDES;
        notches = new int[BlockFace.values().length];
        for (i = 0; i < RADIAL.length; ++i) {
            FaceUtil.notches[FaceUtil.RADIAL[i].ordinal()] = i;
        }
        for (i = 0; i < AXIS.length; ++i) {
            FaceUtil.AXIS[i] = RADIAL[i << 1];
        }
    }
}

