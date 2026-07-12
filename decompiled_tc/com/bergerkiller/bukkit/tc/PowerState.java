/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.material.Diode
 *  org.bukkit.material.Lever
 *  org.bukkit.material.MaterialData
 *  org.bukkit.material.PressureSensor
 *  org.bukkit.material.Redstone
 *  org.bukkit.material.RedstoneTorch
 *  org.bukkit.material.RedstoneWire
 */
package com.bergerkiller.bukkit.tc;

import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import java.util.EnumMap;
import java.util.Locale;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Diode;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PressureSensor;
import org.bukkit.material.Redstone;
import org.bukkit.material.RedstoneTorch;
import org.bukkit.material.RedstoneWire;

public enum PowerState {
    ON,
    OFF,
    NONE;

    private static EnumMap<BlockFace, String> redstoneWireSideKey;

    private static boolean isDistractingColumn(Block main, BlockFace face) {
        BlockData up_data;
        Block side = main.getRelative(face);
        BlockData side_data = WorldUtil.getBlockData((Block)side);
        if (((Boolean)MaterialUtil.ISPOWERSOURCE.get(side_data)).booleanValue()) {
            return true;
        }
        if (side_data.isType(Material.AIR)) {
            if (((Boolean)MaterialUtil.ISPOWERSOURCE.get(side.getRelative(BlockFace.DOWN))).booleanValue()) {
                return true;
            }
        } else if (MaterialUtil.ISDIODE.get(side_data).booleanValue()) {
            BlockFace facing = BlockUtil.getFacing((Block)side);
            return facing == face;
        }
        if ((up_data = WorldUtil.getBlockData((Block)main.getRelative(BlockFace.UP))).isType(Material.AIR)) {
            return (Boolean)MaterialUtil.ISPOWERSOURCE.get(side.getRelative(BlockFace.UP));
        }
        return false;
    }

    private static boolean isDistracted(Block wire, BlockFace face) {
        return PowerState.isDistractingColumn(wire, FaceUtil.rotate((BlockFace)face, (int)-2)) || PowerState.isDistractingColumn(wire, FaceUtil.rotate((BlockFace)face, (int)2));
    }

    public static PowerState get(Block block, BlockFace from) {
        return PowerState.get(block, from, Options.SIGN);
    }

    public static PowerState get(Block block, BlockFace from, boolean useSignLogic) {
        return PowerState.get(block, from, useSignLogic ? Options.SIGN : Options.FAR);
    }

    public static PowerState get(Block block, BlockFace from, Options options) {
        Block fromBlock = block.getRelative(from);
        BlockData fromBlockInfo = WorldUtil.getBlockData((Block)fromBlock);
        MaterialData fromBlockData = fromBlockInfo.getMaterialData();
        if (fromBlockData instanceof RedstoneTorch) {
            if (options.isNextToSign() || from == BlockFace.DOWN) {
                return ((RedstoneTorch)fromBlockData).isPowered() ? ON : OFF;
            }
            return NONE;
        }
        if (fromBlockData instanceof Diode && !FaceUtil.isVertical((BlockFace)from)) {
            Diode diode = (Diode)fromBlockData;
            if (diode.getFacing().getOppositeFace() == from) {
                return fromBlockInfo.isType(MaterialUtil.getMaterial((String)"LEGACY_DIODE_BLOCK_ON")) ? ON : OFF;
            }
            return NONE;
        }
        if (fromBlockData instanceof RedstoneWire) {
            BlockData updated;
            String sideKey;
            if (options == Options.SIGN_CONNECT_WIRE && !FaceUtil.isVertical((BlockFace)from) && (sideKey = redstoneWireSideKey.get(from.getOppositeFace())) != null && fromBlockInfo != (updated = fromBlockInfo.setProperty(sideKey, (Object)"side"))) {
                WorldUtil.setBlockDataFast((Block)fromBlock, (BlockData)updated);
            }
            if (options.isNextToSign() || from == BlockFace.UP || from != BlockFace.DOWN && !PowerState.isDistracted(fromBlock, from)) {
                return ((RedstoneWire)fromBlockData).isPowered() ? ON : OFF;
            }
            return NONE;
        }
        if (fromBlockData instanceof Lever && !options.isNextToSign()) {
            return NONE;
        }
        if (fromBlockInfo.isPowerSource()) {
            if (fromBlockData instanceof Redstone) {
                return ((Redstone)fromBlockData).isPowered() ? ON : OFF;
            }
            if (fromBlockData instanceof PressureSensor) {
                return ((PressureSensor)fromBlockData).isPressed() ? ON : OFF;
            }
        }
        if (options.isNextToSign() && BlockUtil.getAttachedFace((Block)block) == from) {
            PowerState state = NONE;
            for (BlockFace attFace : FaceUtil.BLOCK_SIDES) {
                PowerState attState;
                if (attFace != from.getOppositeFace() && (attState = PowerState.get(fromBlock, attFace, Options.FAR)) != NONE && (state = attState) == ON) break;
            }
            return state;
        }
        return NONE;
    }

    public static boolean isSignPowered(Block signBlock) {
        return PowerState.isSignPowered(signBlock, false);
    }

    public static boolean isSignPowered(Block signBlock, boolean inverted) {
        return PowerState.isSignPowered(signBlock, Options.SIGN_CONNECT_WIRE, inverted);
    }

    public static boolean isSignPowered(Block signBlock, Options options) {
        return PowerState.isSignPowered(signBlock, options, false);
    }

    public static boolean isSignPowered(Block signBlock, Options options, boolean inverted) {
        if (inverted) {
            boolean result = true;
            for (BlockFace face : FaceUtil.BLOCK_SIDES) {
                result &= PowerState.get(signBlock, face, options) != ON;
            }
            return result;
        }
        boolean result = false;
        for (BlockFace face : FaceUtil.BLOCK_SIDES) {
            result |= PowerState.get(signBlock, face, options).hasPower();
        }
        return result;
    }

    public boolean hasPower() {
        switch (this.ordinal()) {
            case 0: {
                return true;
            }
        }
        return false;
    }

    static {
        redstoneWireSideKey = new EnumMap(BlockFace.class);
        for (BlockFace face : FaceUtil.AXIS) {
            redstoneWireSideKey.put(face, face.name().toLowerCase(Locale.ENGLISH));
        }
    }

    public static enum Options {
        FAR(false),
        SIGN(true),
        SIGN_CONNECT_WIRE(true);

        private final boolean isNextToSign;

        private Options(boolean isNextToSign) {
            this.isNextToSign = isNextToSign;
        }

        public boolean isNextToSign() {
            return this.isNextToSign;
        }
    }
}

