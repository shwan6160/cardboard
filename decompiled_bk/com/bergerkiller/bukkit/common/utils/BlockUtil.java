/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Chest
 *  org.bukkit.block.Sign
 *  org.bukkit.event.block.BlockRedstoneEvent
 *  org.bukkit.event.block.SignChangeEvent
 *  org.bukkit.material.Directional
 *  org.bukkit.material.Lever
 *  org.bukkit.material.MaterialData
 *  org.bukkit.material.Rails
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion;
import com.bergerkiller.bukkit.common.conversion.blockstate.ChunkBlockStateConverter;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.core.BlockPosHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.BlockEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.LevelChunkHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import com.bergerkiller.generated.org.bukkit.block.SignHandle;
import com.bergerkiller.generated.org.bukkit.event.block.BlockCanBuildEventHandle;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.Directional;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Rails;

public class BlockUtil
extends MaterialUtil {
    private static final ArrayList<BlockState> blockStateBuff = new ArrayList();

    public static AABBHandle getBoundingBox(Block block) {
        return WorldUtil.getBlockData(block).getBoundingBox(block);
    }

    public static AABBHandle getInteractableBox(Block block) {
        return WorldUtil.getBlockData(block).getInteractableBox(block);
    }

    public static boolean canBuildBlock(Block block, Material type) {
        return BlockUtil.canBuildBlock(block, type, true);
    }

    public static boolean canBuildBlock(Block block, BlockData type) {
        return BlockUtil.canBuildBlock(block, type, true);
    }

    public static boolean canBuildBlock(Block block, Material type, boolean isBuildable) {
        return BlockUtil.canBuildBlock(block, BlockData.fromMaterial(type), isBuildable);
    }

    public static boolean canBuildBlock(Block block, BlockData type, boolean isBuildable) {
        return CommonUtil.callEvent(BlockCanBuildEventHandle.create(block, type, isBuildable)).isBuildable();
    }

    public static void setTypeAndData(Block block, Material type, MaterialData data) {
        BlockUtil.setTypeAndData(block, type, data, true);
    }

    public static void setTypeAndData(Block block, Material type, MaterialData data, boolean update) {
        BlockData blockData = BlockData.fromMaterialData(type, data);
        if (update) {
            WorldUtil.setBlockData(block, blockData);
        } else {
            WorldUtil.setBlockDataFast(block, blockData);
        }
    }

    public static void setTypeAndRawData(Block block, Material type, int data) {
        BlockUtil.setTypeAndRawData(block, type, data, true);
    }

    public static void setTypeAndRawData(Block block, Material type, int data, boolean update) {
        BlockData blockData = BlockData.fromMaterialData(type, data);
        if (update) {
            WorldUtil.setBlockData(block, blockData);
        } else {
            WorldUtil.setBlockDataFast(block, blockData);
        }
    }

    public static void setData(Block block, MaterialData materialData) {
        BlockUtil.setData(block, materialData, true);
    }

    public static void setData(Block block, MaterialData materialData, boolean doPhysics) {
        BlockData data = BlockData.fromMaterialData(materialData);
        if (doPhysics) {
            WorldUtil.setBlockData(block, data);
        } else {
            WorldUtil.setBlockDataFast(block, data);
        }
    }

    public static MaterialData getData(Block block) {
        return WorldUtil.getBlockData(block).newMaterialData();
    }

    public static <T> T getData(Block block, Class<T> type) {
        return LogicUtil.tryCast(BlockUtil.getData(block), type);
    }

    public static int getManhattanDistance(Location b1, Location b2, boolean checkY) {
        int d = Math.abs(b1.getBlockX() - b2.getBlockX());
        d += Math.abs(b1.getBlockZ() - b2.getBlockZ());
        if (checkY) {
            d += Math.abs(b1.getBlockY() - b2.getBlockY());
        }
        return d;
    }

    public static int getManhattanDistance(Block b1, Block b2, boolean checkY) {
        int d = Math.abs(b1.getX() - b2.getX());
        d += Math.abs(b1.getZ() - b2.getZ());
        if (checkY) {
            d += Math.abs(b1.getY() - b2.getY());
        }
        return d;
    }

    public static boolean equals(Block block1, Block block2) {
        if (block1 == null || block2 == null) {
            return false;
        }
        if (block1 == block2) {
            return true;
        }
        return block1.getX() == block2.getX() && block1.getZ() == block2.getZ() && block1.getY() == block2.getY() && block1.getWorld() == block2.getWorld();
    }

    public static Block[] getRelative(Block main, BlockFace ... faces) {
        if (main == null) {
            return new Block[0];
        }
        Block[] rval = new Block[faces.length];
        for (int i = 0; i < rval.length; ++i) {
            rval[i] = main.getRelative(faces[i]);
        }
        return rval;
    }

    public static Block getBlock(World world, IntVector3 at) {
        return world.getBlockAt(at.x, at.y, at.z);
    }

    public static BlockFace getAttachedFace(Block attachable) {
        return WorldUtil.getBlockData(attachable).getAttachedFace();
    }

    public static Block getAttachedBlock(Block attachable) {
        return attachable.getRelative(BlockUtil.getAttachedFace(attachable));
    }

    public static BlockFace getFacing(Block directional) {
        Directional data = BlockUtil.getData(directional, Directional.class);
        return data == null ? BlockFace.NORTH : data.getFacing();
    }

    public static void setFacing(Block block, BlockFace facing) {
        MaterialData data = BlockUtil.getData(block);
        if (data != null && data instanceof Directional) {
            ((Directional)data).setFacingDirection(facing);
            BlockUtil.setData(block, data, true);
        }
    }

    public static void setLeversAroundBlock(Block block, boolean down) {
        for (BlockFace dir : FaceUtil.BLOCK_SIDES) {
            Block b = block.getRelative(dir);
            if (!BlockUtil.isType(b, Material.LEVER) || BlockUtil.getAttachedFace(b) != dir.getOppositeFace()) continue;
            BlockUtil.setLever(b, down);
        }
    }

    public static boolean isLeverDown(Block lever) {
        int dat = BlockUtil.getRawData(lever);
        return dat == (dat | 8);
    }

    public static void setLever(Block lever, boolean down) {
        int data = BlockUtil.getRawData(lever);
        Lever newMaterialData = (Lever)BlockUtil.getData(Material.LEVER, data);
        newMaterialData.setPowered(down);
        if (BlockUtil.getRawData((MaterialData)newMaterialData) != data) {
            int old = !down ? 1 : 0;
            int current = down ? 1 : 0;
            BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(lever, old, current);
            CommonUtil.callEvent(eventRedstone);
            if (eventRedstone.getNewCurrent() > 0 != down) {
                return;
            }
            BlockUtil.setData(lever, (MaterialData)newMaterialData, true);
            BlockUtil.applyPhysics(BlockUtil.getAttachedBlock(lever), Material.LEVER);
        }
    }

    public static void applyPhysics(Block block, Material callerType) {
        BlockUtil.applyPhysics(block, callerType, true);
    }

    public static void applyPhysics(Block block, Material callerType, boolean updateSelf) {
        ServerLevelHandle.fromBukkit(block.getWorld()).applyBlockPhysics(new IntVector3(block), BlockData.fromMaterial(callerType), updateSelf);
    }

    public static CommonPacket getUpdatePacket(BlockState blockState) {
        if (blockState == null) {
            return null;
        }
        Object tileEntity = HandleConversion.toTileEntityHandle(blockState);
        return tileEntity == null ? null : BlockEntityHandle.T.getUpdatePacket.invoke(tileEntity);
    }

    public static void setRails(Block rails, BlockFace from, BlockFace to) {
        BlockUtil.setRails(rails, FaceUtil.combine(from, to).getOppositeFace());
    }

    public static void setRails(Block rails, BlockFace direction) {
        BlockData data = WorldUtil.getBlockData(rails);
        if (MaterialUtil.ISRAILS.get(data).booleanValue()) {
            int olddata = data.getRawData();
            Rails r = data.newMaterialData(Rails.class);
            r.setDirection(FaceUtil.toRailsDirection(direction), r.isOnSlope());
            if (MaterialUtil.getRawData((MaterialData)r) != olddata) {
                BlockUtil.setData(rails, (MaterialData)r);
            }
        }
    }

    public static BlockState getState(Block block) {
        return BlockStateConversion.INSTANCE.blockToBlockState(block);
    }

    public static <T extends BlockState> T getState(Block block, Class<T> type) {
        return (T)((BlockState)LogicUtil.tryCast(BlockUtil.getState(block), type));
    }

    public static Rails getRails(Block railsblock) {
        return BlockUtil.getData(railsblock, Rails.class);
    }

    public static Sign getSign(Block signblock) {
        return BlockUtil.getState(signblock, Sign.class);
    }

    public static Chest getChest(Block chestblock) {
        return BlockUtil.getState(chestblock, Chest.class);
    }

    public static Collection<BlockState> getBlockStates(Block middle) {
        return BlockUtil.getBlockStates(middle, 0, 0, 0);
    }

    public static Collection<BlockState> getBlockStates(Block middle, int radius) {
        return BlockUtil.getBlockStates(middle, radius, radius, radius);
    }

    public static Collection<BlockState> getBlockStates(Block middle, int radiusX, int radiusY, int radiusZ) {
        return BlockUtil.getBlockStates(middle.getWorld(), middle.getX(), middle.getY(), middle.getZ(), radiusX, radiusY, radiusZ);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Collection<BlockState> getBlockStates(World world, int x, int y, int z, int radiusX, int radiusY, int radiusZ) {
        try {
            if (radiusX == 0 && radiusY == 0 && radiusZ == 0) {
                Object blockPosition = ((Template.Constructor)BlockPosHandle.T.constr_x_y_z.raw).newInstance(x, y, z);
                Object tile = ((Template.Method)LevelHandle.T.getTileEntity.raw).invoke(HandleConversion.toWorldHandle(world), blockPosition);
                if (tile != null) {
                    blockStateBuff.add(WrapperConversion.toBlockState(tile));
                }
            } else {
                int xMin = x - radiusX;
                int yMin = y - radiusY;
                int zMin = z - radiusZ;
                int xMax = x + radiusX;
                int yMax = y + radiusY;
                int zMax = z + radiusZ;
                int chunk_xMin = MathUtil.toChunk(xMin);
                int chunk_zMin = MathUtil.toChunk(zMin);
                int chunk_xMax = MathUtil.toChunk(xMax);
                int chunk_zMax = MathUtil.toChunk(zMax);
                for (int cx = chunk_xMin; cx <= chunk_xMax; ++cx) {
                    for (int cz = chunk_zMin; cz <= chunk_zMax; ++cz) {
                        Chunk chunk = WorldUtil.getChunk(world, cx, cz);
                        if (chunk == null) continue;
                        Converter converter = null;
                        Collection<?> rawTiles = LevelChunkHandle.T.getRawTileEntities.invoke(HandleConversion.toChunkHandle(chunk));
                        for (Object tile : rawTiles) {
                            BlockPosHandle blockPosition = BlockPosHandle.createHandle(((Template.Field)BlockEntityHandle.T.position_field.raw).get(tile));
                            if (!blockPosition.isPositionInBox(xMin, yMin, zMin, xMax, yMax, zMax)) continue;
                            if (converter == null) {
                                converter = new ChunkBlockStateConverter(chunk);
                            }
                            blockStateBuff.add((BlockState)converter.convert(tile));
                        }
                    }
                }
            }
            ArrayList<BlockState> arrayList = new ArrayList<BlockState>(blockStateBuff);
            return arrayList;
        }
        finally {
            blockStateBuff.clear();
        }
    }

    public static boolean isSolid(Block block) {
        return WorldUtil.getBlockData(block).isOccluding(block);
    }

    public static boolean isSuffocating(Block block) {
        return WorldUtil.getBlockData(block).isSuffocating(block);
    }

    public static boolean canSupportTop(Block block) {
        return WorldUtil.getBlockData(block).canSupportTop(block);
    }

    public static boolean isChangingFrontLines(SignChangeEvent event) {
        return SignHandle.isChangingFrontLines(event);
    }
}

