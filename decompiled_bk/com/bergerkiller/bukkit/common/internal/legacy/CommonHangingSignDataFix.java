/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.BlockFace
 *  org.bukkit.material.Sign
 */
package com.bergerkiller.bukkit.common.internal.legacy;

import java.util.EnumMap;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Sign;

@Deprecated
public class CommonHangingSignDataFix
extends Sign {
    private final boolean isAlignedSign;
    private static final BlockFace[] ROTATION_FACES = new BlockFace[]{BlockFace.SOUTH, BlockFace.SOUTH_SOUTH_WEST, BlockFace.SOUTH_WEST, BlockFace.WEST_SOUTH_WEST, BlockFace.WEST, BlockFace.WEST_NORTH_WEST, BlockFace.NORTH_WEST, BlockFace.NORTH_NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_EAST, BlockFace.EAST_NORTH_EAST, BlockFace.EAST, BlockFace.EAST_SOUTH_EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_SOUTH_EAST};
    private static final EnumMap<BlockFace, Byte> DATA_BY_ROTATION_FACE = new EnumMap(BlockFace.class);

    public CommonHangingSignDataFix(Material legacy_data_type, byte legacy_data_value, boolean isAlignedSign) {
        super(legacy_data_type, legacy_data_value);
        this.isAlignedSign = isAlignedSign;
    }

    public boolean isWallSign() {
        return false;
    }

    public BlockFace getFacing() {
        if (this.isAlignedSign) {
            byte data = this.getData();
            switch (data) {
                case 2: {
                    return BlockFace.NORTH;
                }
                case 3: {
                    return BlockFace.SOUTH;
                }
                case 4: {
                    return BlockFace.WEST;
                }
            }
            return BlockFace.EAST;
        }
        return ROTATION_FACES[super.getData() & 0xF];
    }

    public void setFacingDirection(BlockFace face) {
        if (this.isAlignedSign) {
            byte data;
            switch (face) {
                case NORTH: {
                    data = 2;
                    break;
                }
                default: {
                    data = 5;
                    break;
                }
                case SOUTH: {
                    data = 3;
                    break;
                }
                case WEST: {
                    data = 4;
                }
            }
            this.setData(data);
        } else {
            this.setData(DATA_BY_ROTATION_FACE.getOrDefault(face, (byte)0));
        }
    }

    public BlockFace getAttachedFace() {
        return BlockFace.UP;
    }

    public Sign clone() {
        return new CommonHangingSignDataFix(super.getItemType(), super.getData(), this.isAlignedSign);
    }

    static {
        for (int i = 0; i < ROTATION_FACES.length; ++i) {
            DATA_BY_ROTATION_FACE.put(ROTATION_FACES[i], (byte)i);
        }
    }
}

