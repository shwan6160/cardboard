/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc;

import com.bergerkiller.bukkit.common.utils.FaceUtil;
import java.util.LinkedHashSet;
import org.bukkit.block.BlockFace;

public enum Direction {
    NORTH(true, "n", "north"),
    EAST(true, "e", "east"),
    SOUTH(true, "s", "south"),
    WEST(true, "w", "west"),
    LEFT(false, "l", "left"),
    RIGHT(false, "r", "right"),
    IMPLICIT_LEFT(false, "implicit_left"),
    IMPLICIT_RIGHT(false, "implicit_right"),
    FORWARD(false, "f", "front", "forward", "forwards"),
    BACKWARD(false, "b", "back", "backward", "backwards"),
    UP(true, "u", "up", "upwards", "above"),
    DOWN(true, "d", "down", "downwards", "below"),
    CONTINUE(false, "continue"),
    REVERSE(false, "reverse"),
    NONE(true, "", "n", "none");

    private final boolean absolute;
    private final String[] aliases;

    private Direction(boolean absolute, String ... aliases) {
        this.absolute = absolute;
        this.aliases = aliases;
    }

    public boolean isAbsolute() {
        return this.absolute;
    }

    public String[] aliases() {
        return this.aliases;
    }

    public BlockFace getDirection(BlockFace signfacing) {
        return this.getDirectionLegacy(signfacing, signfacing.getOppositeFace());
    }

    public BlockFace getDirection(BlockFace signfacing, BlockFace cartdirection) {
        switch (this.ordinal()) {
            case 0: {
                return BlockFace.NORTH;
            }
            case 1: {
                return BlockFace.EAST;
            }
            case 2: {
                return BlockFace.SOUTH;
            }
            case 3: {
                return BlockFace.WEST;
            }
            case 11: {
                return BlockFace.DOWN;
            }
            case 10: {
                return BlockFace.UP;
            }
            case 4: 
            case 6: {
                return FaceUtil.rotate((BlockFace)signfacing, (int)2);
            }
            case 5: 
            case 7: {
                return FaceUtil.rotate((BlockFace)signfacing, (int)-2);
            }
            case 8: {
                return signfacing.getOppositeFace();
            }
            case 9: {
                return signfacing;
            }
            case 12: {
                return cartdirection;
            }
            case 13: {
                return cartdirection.getOppositeFace();
            }
        }
        return cartdirection;
    }

    public BlockFace getDirectionLegacy(BlockFace signfacing, BlockFace cartdirection) {
        switch (this.ordinal()) {
            case 0: {
                return BlockFace.NORTH;
            }
            case 1: {
                return BlockFace.EAST;
            }
            case 2: {
                return BlockFace.SOUTH;
            }
            case 3: {
                return BlockFace.WEST;
            }
            case 11: {
                return BlockFace.DOWN;
            }
            case 10: {
                return BlockFace.UP;
            }
            case 4: 
            case 6: {
                return FaceUtil.rotate((BlockFace)signfacing, (int)2);
            }
            case 5: 
            case 7: {
                return FaceUtil.rotate((BlockFace)signfacing, (int)-2);
            }
            case 8: 
            case 12: {
                return cartdirection;
            }
            case 9: 
            case 13: {
                return cartdirection.getOppositeFace();
            }
        }
        return cartdirection;
    }

    public boolean match(char character) {
        for (String alias : this.aliases) {
            if (alias.length() != 1 || alias.charAt(0) != character) continue;
            return true;
        }
        return false;
    }

    public boolean match(String text) {
        for (String alias : this.aliases) {
            if (!alias.equalsIgnoreCase(text)) continue;
            return true;
        }
        return false;
    }

    public static Direction parse(char character) {
        for (Direction dir : Direction.values()) {
            if (!dir.match(character)) continue;
            return dir;
        }
        return NONE;
    }

    public static Direction parse(String text) {
        for (Direction dir : Direction.values()) {
            if (!dir.match(text)) continue;
            return dir;
        }
        return NONE;
    }

    public static Direction fromFace(BlockFace face) {
        switch (face) {
            case NORTH: {
                return NORTH;
            }
            case EAST: {
                return EAST;
            }
            case SOUTH: {
                return SOUTH;
            }
            case WEST: {
                return WEST;
            }
            case UP: {
                return UP;
            }
            case DOWN: {
                return DOWN;
            }
            case SELF: {
                return CONTINUE;
            }
        }
        return NONE;
    }

    public static Direction[] parseAll(String text) {
        if (text.equalsIgnoreCase("all") || text.equals("*")) {
            Direction[] dirs = new Direction[FaceUtil.BLOCK_SIDES.length];
            for (int i = 0; i < dirs.length; ++i) {
                dirs[i] = Direction.fromFace(FaceUtil.BLOCK_SIDES[i]);
            }
            return dirs;
        }
        LinkedHashSet<Direction> faces = new LinkedHashSet<Direction>();
        Direction dir = Direction.parse(text);
        if (dir == NONE) {
            for (char c : text.toCharArray()) {
                dir = Direction.parse(c);
                if (dir == NONE) {
                    return new Direction[0];
                }
                faces.add(dir);
            }
        } else {
            faces.add(dir);
        }
        return faces.toArray(new Direction[0]);
    }

    public static BlockFace[] parseAll(String text, BlockFace absoluteDirection) {
        Direction[] dirs = Direction.parseAll(text);
        BlockFace[] faces = new BlockFace[dirs.length];
        for (int i = 0; i < faces.length; ++i) {
            faces[i] = dirs[i].getDirection(absoluteDirection);
        }
        return faces;
    }
}

