/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapBlendMode
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.event.block.BlockPhysicsEvent
 *  org.bukkit.material.MaterialData
 *  org.bukkit.material.Rails
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.rails.type;

import com.bergerkiller.bukkit.common.map.MapBlendMode;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.components.RailJunction;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.editor.RailsTexture;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogic;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicGround;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicHorizontal;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicSloped;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicVerticalSlopeNormalA;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicVerticalSlopeUpsideDownA;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.rails.type.RailTypeHorizontal;
import com.bergerkiller.bukkit.tc.utils.MinecartTrackLogic;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Rails;
import org.bukkit.plugin.java.JavaPlugin;

public class RailTypeRegular
extends RailTypeHorizontal {
    public static BlockFace[] getPossibleDirections(BlockFace railDirection) {
        return FaceUtil.getFaces((BlockFace)railDirection.getOppositeFace());
    }

    @Override
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (this.isUpsideDown(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onBlockPlaced(Block railsBlock) {
        Rails rails;
        if (this.isUpsideDown(railsBlock)) {
            MinecartTrackLogic logic = new MinecartTrackLogic(railsBlock);
            logic.update(railsBlock.isBlockIndirectlyPowered(), true);
        }
        if ((rails = BlockUtil.getRails((Block)railsBlock)) == null || rails.isCurve() || rails.isOnSlope()) {
            return;
        }
        Block above = railsBlock.getRelative(BlockFace.UP);
        if (((Boolean)Util.ISVERTRAIL.get(above)).booleanValue()) {
            BlockFace dir;
            BlockFace railDir = rails.getDirection();
            if (railDir != (dir = Util.getVerticalRailDirection(above)) && railDir != dir.getOppositeFace()) {
                if (Util.getRailsBlock(railsBlock.getRelative(railDir)) != null) {
                    return;
                }
                if (Util.getRailsBlock(railsBlock.getRelative(railDir.getOppositeFace())) != null) {
                    return;
                }
            }
            if (BlockUtil.isSuffocating((Block)railsBlock.getRelative(dir))) {
                rails.setDirection(dir, true);
                TrainCarts.plugin.setBlockDataWithoutBreaking(railsBlock, BlockData.fromMaterialData((MaterialData)rails));
            }
        }
        if (this.isUpsideDown(railsBlock, rails)) {
            for (BlockFace face : FaceUtil.AXIS) {
                Block aboveAt = above.getRelative(face);
                if (!((Boolean)Util.ISVERTRAIL.get(aboveAt)).booleanValue()) continue;
                rails.setDirection(face, true);
                TrainCarts.plugin.setBlockDataWithoutBreaking(railsBlock, BlockData.fromMaterialData((MaterialData)rails));
                break;
            }
        }
    }

    public boolean isSlopeUpwardsTo(Block railsBlock, BlockFace direction) {
        if (!TCConfig.allowUpsideDownRails) {
            return false;
        }
        Rails rails = Util.getRailsRO(railsBlock);
        return rails != null && rails.isOnSlope() && rails.getDirection() == direction;
    }

    @Override
    public boolean isUpsideDown(Block railsBlock) {
        return this.isUpsideDown(railsBlock, null);
    }

    protected boolean isUpsideDown(Block railsBlock, Rails rails) {
        if (!TCConfig.allowUpsideDownRails) {
            return false;
        }
        Block blockAbove = railsBlock.getRelative(BlockFace.UP);
        if (!Util.isUpsideDownRailSupport(blockAbove)) {
            return false;
        }
        if (BlockUtil.canSupportTop((Block)railsBlock.getRelative(BlockFace.DOWN))) {
            return false;
        }
        if (rails == null) {
            rails = Util.getRailsRO(railsBlock);
        }
        if (rails == null) {
            return false;
        }
        if (rails.isOnSlope()) {
            RailType railType;
            Block nextBlock = railsBlock.getRelative(rails.getDirection().getOppositeFace());
            BlockData nextBlockData = WorldUtil.getBlockData((Block)nextBlock);
            return nextBlockData.isSuffocating(nextBlock) || (railType = RailType.getType(nextBlock, nextBlockData)) != RailType.NONE;
        }
        return true;
    }

    public static Block getNextPos(Block currentTrack, BlockFace currentDirection, BlockFace railDirection, boolean sloped) {
        return RailTypeRegular.getNextPos(currentTrack, currentDirection, railDirection, sloped, false);
    }

    public static Block getNextPos(Block currentTrack, BlockFace currentDirection, BlockFace railDirection, boolean sloped, boolean upsideDown) {
        Block result;
        if (FaceUtil.isSubCardinal((BlockFace)railDirection)) {
            BlockFace dir;
            BlockFace[] possible = FaceUtil.getFaces((BlockFace)railDirection.getOppositeFace());
            boolean isSimpleForward = false;
            for (BlockFace newdir : possible) {
                if (newdir != currentDirection) continue;
                isSimpleForward = true;
                break;
            }
            if (isSimpleForward) {
                result = currentTrack.getRelative(currentDirection);
            } else if (FaceUtil.isVertical((BlockFace)currentDirection)) {
                dir = possible[1];
                result = currentTrack.getRelative(dir);
            } else {
                dir = currentDirection.getOppositeFace();
                BlockFace nextDir = possible[0].equals((Object)dir) ? possible[1] : (possible[1].equals((Object)dir) ? possible[0] : (possible[0] == BlockFace.SOUTH || possible[0] == BlockFace.EAST ? possible[0] : possible[1]));
                result = currentTrack.getRelative(nextDir);
            }
        } else if (sloped) {
            if (railDirection == currentDirection || currentDirection == BlockFace.UP) {
                Block above = currentTrack.getRelative(BlockFace.UP);
                result = RailType.VERTICAL.isRail(above) && (currentDirection == BlockFace.UP || Util.getVerticalRailDirection(above) == currentDirection) ? above : above.getRelative(railDirection);
            } else {
                Block below = currentTrack.getRelative(BlockFace.DOWN);
                result = upsideDown && ((Boolean)Util.ISVERTRAIL.get(below)).booleanValue() ? currentTrack : currentTrack.getRelative(railDirection.getOppositeFace());
            }
        } else {
            result = railDirection == currentDirection || railDirection.getOppositeFace() == currentDirection ? currentTrack.getRelative(currentDirection) : currentTrack.getRelative(railDirection);
        }
        if (upsideDown) {
            result = result.getRelative(BlockFace.DOWN);
        }
        return result;
    }

    @Override
    public boolean isRail(BlockData blockData) {
        return blockData.isType(RailMaterials.REGULAR);
    }

    @Override
    public BlockFace[] getPossibleDirections(Block trackBlock) {
        Rails rails = Util.getRailsRO(trackBlock);
        if (rails == null) {
            return new BlockFace[0];
        }
        if (rails.isOnSlope() && Util.isVerticalAbove(trackBlock, rails.getDirection())) {
            return new BlockFace[]{rails.getDirection().getOppositeFace(), BlockFace.UP};
        }
        return RailTypeRegular.getPossibleDirections(rails.getDirection());
    }

    @Override
    public List<RailJunction> getJunctions(Block railBlock) {
        Rails rails = Util.getRailsRO(railBlock);
        if (rails == null || rails.isOnSlope()) {
            return super.getJunctions(railBlock);
        }
        return Arrays.asList(new RailJunction("n", RailLogicHorizontal.get(BlockFace.NORTH).getPath().getStartPosition()), new RailJunction("e", RailLogicHorizontal.get(BlockFace.EAST).getPath().getEndPosition()), new RailJunction("s", RailLogicHorizontal.get(BlockFace.SOUTH).getPath().getEndPosition()), new RailJunction("w", RailLogicHorizontal.get(BlockFace.WEST).getPath().getStartPosition()));
    }

    @Override
    public void switchJunction(Block railBlock, RailJunction from, RailJunction to) {
        TrainCarts.plugin.getSignController().suppressRedstonePhysicsDuring(() -> BlockUtil.setRails((Block)railBlock, (BlockFace)RailTypeRegular.juncToFace(from), (BlockFace)RailTypeRegular.juncToFace(to)));
    }

    private static final BlockFace juncToFace(RailJunction junction) {
        switch (junction.name()) {
            case "n": {
                return BlockFace.NORTH;
            }
            case "e": {
                return BlockFace.EAST;
            }
            case "s": {
                return BlockFace.SOUTH;
            }
            case "w": {
                return BlockFace.WEST;
            }
        }
        return BlockFace.NORTH;
    }

    public RailLogicHorizontal getLogicForRails(Block railsBlock, Rails rails, BlockFace enterFace) {
        BlockFace direction = rails.getDirection();
        boolean upsideDown = this.isUpsideDown(railsBlock, rails);
        if (rails.isOnSlope()) {
            if (Util.isVerticalAbove(railsBlock, direction)) {
                return RailLogicVerticalSlopeNormalA.get(direction);
            }
            if (upsideDown && Util.isVerticalBelow(railsBlock, direction.getOppositeFace())) {
                return RailLogicVerticalSlopeUpsideDownA.get(direction);
            }
            return RailLogicSloped.get(direction, upsideDown);
        }
        if (rails.isCurve()) {
            BlockFace[] faces = FaceUtil.getFaces((BlockFace)direction);
            if (enterFace == faces[0].getOppositeFace() || enterFace == faces[1].getOppositeFace()) {
                return RailLogicHorizontal.get(enterFace);
            }
        } else {
            BlockFace sideFace = FaceUtil.rotate((BlockFace)direction, (int)2);
            if (enterFace == sideFace || enterFace == sideFace.getOppositeFace()) {
                BlockFace curvedDir = FaceUtil.combine((BlockFace)enterFace, (BlockFace)direction.getOppositeFace());
                return RailLogicHorizontal.get(curvedDir);
            }
        }
        return RailLogicHorizontal.get(direction, upsideDown);
    }

    @Override
    public RailLogic getLogic(RailState state) {
        Rails rails = Util.getRailsRO(state.railBlock());
        if (rails == null) {
            return RailLogicGround.INSTANCE;
        }
        return this.getLogicForRails(state.railBlock(), rails, state.enterFace());
    }

    @Override
    public BlockFace getDirection(Block railsBlock) {
        Rails rails = Util.getRailsRO(railsBlock);
        return rails == null ? BlockFace.SELF : rails.getDirection();
    }

    @Override
    public Location getSpawnLocation(Block railsBlock, BlockFace orientation) {
        Rails rails = Util.getRailsRO(railsBlock);
        if (rails == null) {
            return super.getSpawnLocation(railsBlock, orientation);
        }
        BlockFace dir = FaceUtil.getRailsCartDirection((BlockFace)rails.getDirection());
        if (FaceUtil.getFaceYawDifference((BlockFace)dir.getOppositeFace(), (BlockFace)orientation) < 90) {
            dir = dir.getOppositeFace();
        }
        Location result = super.getSpawnLocation(railsBlock, dir);
        if (rails.isOnSlope()) {
            result.setPitch(result.getPitch() - 45.0f);
            result.setY(result.getY() + 0.5);
        }
        return result;
    }

    @Override
    public RailsTexture getRailsTexture(Block railsBlock) {
        Rails rails = Util.getRailsRO(railsBlock);
        if (rails == null) {
            return super.getRailsTexture(railsBlock);
        }
        BlockFace direction = rails.getDirection();
        if (FaceUtil.isSubCardinal((BlockFace)direction)) {
            int yaw = 45 - FaceUtil.faceToYaw((BlockFace)direction);
            MapTexture top = MapTexture.rotate((MapCanvas)this.getResource(rails, "top"), (int)yaw);
            MapTexture back = top.clone();
            back.setBlendMode(MapBlendMode.MULTIPLY).fill(MapColorPalette.getColor((int)160, (int)160, (int)160));
            back = MapTexture.flipV((MapCanvas)back);
            RailsTexture result = new RailsTexture().set(BlockFace.UP, top).set(BlockFace.DOWN, back);
            for (BlockFace face : FaceUtil.AXIS) {
                result.set(face, MapTexture.rotate((MapCanvas)top, (int)FaceUtil.faceToYaw((BlockFace)face)));
            }
            return result;
        }
        if (rails.isOnSlope()) {
            MapTexture side = this.getResource(rails, "side");
            MapTexture top = this.getResource(rails, "top");
            MapTexture back = top.clone();
            back.setBlendMode(MapBlendMode.MULTIPLY).fill(MapColorPalette.getColor((int)160, (int)160, (int)160));
            return new RailsTexture().set(direction.getOppositeFace(), top).set(direction, MapTexture.flipV((MapCanvas)back)).set(BlockFace.UP, MapTexture.rotate((MapCanvas)top, (int)(-FaceUtil.faceToYaw((BlockFace)direction)))).set(BlockFace.DOWN, MapTexture.rotate((MapCanvas)back, (int)FaceUtil.faceToYaw((BlockFace)direction))).setOpposites(FaceUtil.rotate((BlockFace)direction, (int)2), side);
        }
        MapTexture front = this.getResource(rails, "front");
        MapTexture side = this.getResource(rails, "side");
        MapTexture top = MapTexture.rotate((MapCanvas)this.getResource(rails, "top"), (int)FaceUtil.faceToYaw((BlockFace)direction));
        MapTexture back = top.clone();
        back.setBlendMode(MapBlendMode.MULTIPLY).fill(MapColorPalette.getColor((int)160, (int)160, (int)160));
        return new RailsTexture().set(BlockFace.UP, top).set(BlockFace.DOWN, back).setOpposites(direction, front).setOpposites(FaceUtil.rotate((BlockFace)direction, (int)2), side);
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    protected String getRailsTexturePath(Rails rails, String name) {
        if (rails.isCurve()) {
            return "com/bergerkiller/bukkit/tc/textures/rails/regular_curved_" + name + ".png";
        }
        if (rails.isOnSlope()) {
            return "com/bergerkiller/bukkit/tc/textures/rails/regular_sloped_" + name + ".png";
        }
        return "com/bergerkiller/bukkit/tc/textures/rails/regular_straight_" + name + ".png";
    }

    private MapTexture getResource(Rails rails, String name) {
        return MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)this.getRailsTexturePath(rails, name));
    }

    protected static class RailMaterials {
        public static final Material REGULAR = MaterialUtil.getFirst((String[])new String[]{"RAIL", "LEGACY_RAILS"});
        public static final Material DETECTOR = Material.DETECTOR_RAIL;
        public static final Material POWERED = Material.POWERED_RAIL;
        public static final Material ACTIVATOR = Material.ACTIVATOR_RAIL;

        protected RailMaterials() {
        }
    }
}

