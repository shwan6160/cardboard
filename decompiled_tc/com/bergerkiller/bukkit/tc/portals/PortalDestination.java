/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.portals;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.utils.TrackWalkingPoint;
import java.util.HashSet;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class PortalDestination {
    private static final Material PORTAL_TYPE = MaterialUtil.getFirst((String[])new String[]{"NETHER_PORTAL", "LEGACY_PORTAL"});
    private final Block railsBlock;
    private final BlockFace[] directions;

    public PortalDestination(Block railsBlock, BlockFace[] directions) {
        this.railsBlock = railsBlock;
        this.directions = directions;
    }

    public Block getRailsBlock() {
        return this.railsBlock;
    }

    public BlockFace[] getDirections() {
        return this.directions;
    }

    public boolean hasDirections() {
        return this.directions != null && this.directions.length > 0;
    }

    public String toString() {
        String s = "{";
        s = s + "world=" + this.railsBlock.getWorld().getName();
        s = s + ", x=" + this.railsBlock.getX();
        s = s + ", y=" + this.railsBlock.getY();
        s = s + ", z=" + this.railsBlock.getZ();
        s = s + ", dirs=[";
        boolean f = true;
        for (BlockFace dir : this.directions) {
            if (f) {
                f = false;
            } else {
                s = s + ", ";
            }
            s = s + dir.name();
        }
        s = s + "]}";
        return s;
    }

    public static PortalDestination findDestinationAtNetherPortal(Block portalBlock, Direction direction) {
        if (portalBlock == null) {
            return null;
        }
        HashSet<IntVector3> blocks = new HashSet<IntVector3>();
        IntVector3 pos = new IntVector3(portalBlock.getX(), portalBlock.getY(), portalBlock.getZ());
        World world = portalBlock.getWorld();
        PortalDestination.discoverPortals(blocks, world, pos);
        if (blocks.isEmpty()) {
            return PortalDestination.findDestination(portalBlock, portalBlock, direction);
        }
        int minX = pos.x;
        int minY = pos.y;
        int minZ = pos.z;
        int maxX = pos.x;
        int maxY = pos.y;
        int maxZ = pos.z;
        for (IntVector3 block : blocks) {
            if (block.x < minX) {
                minX = block.x;
            }
            if (block.x > maxX) {
                maxX = block.x;
            }
            if (block.y < minY) {
                minY = block.y;
            }
            if (block.y > maxY) {
                maxY = block.y;
            }
            if (block.z < minZ) {
                minZ = block.z;
            }
            if (block.z <= maxZ) continue;
            maxZ = block.z;
        }
        return PortalDestination.findDestination(world.getBlockAt(minX, minY, minZ), world.getBlockAt(maxX, maxY, maxZ), direction);
    }

    private static void discoverPortals(HashSet<IntVector3> blocks, World world, IntVector3 pos) {
        if (WorldUtil.getBlockData((World)world, (IntVector3)pos).isType(PORTAL_TYPE) && blocks.add(pos)) {
            for (BlockFace face : FaceUtil.BLOCK_SIDES) {
                PortalDestination.discoverPortals(blocks, world, pos.add(face));
            }
        }
    }

    public static PortalDestination findDestination(Block regionMin, Block regionMax, Direction direction) {
        return PortalDestination.findDestination(regionMin, regionMax, direction, 0.0);
    }

    public static PortalDestination findDestination(Block regionMin, Block regionMax, Direction direction, double requiredTrainLength) {
        int dx = regionMax.getX() - regionMin.getX();
        int dy = regionMax.getY() - regionMin.getY();
        int dz = regionMax.getZ() - regionMin.getZ();
        BlockFace portalFacing = dx > dy && dz > dy ? BlockFace.UP : (dx > dz ? BlockFace.SOUTH : BlockFace.EAST);
        BlockFace spawnDirection = direction.getDirection(portalFacing);
        PortalDestination dest = null;
        for (int y = regionMin.getY(); y <= regionMax.getY(); ++y) {
            for (int x = regionMin.getX(); x <= regionMax.getX(); ++x) {
                for (int z = regionMin.getZ(); z <= regionMax.getZ(); ++z) {
                    Block block = regionMin.getWorld().getBlockAt(x, y, z);
                    dest = PortalDestination.findRailDestination(block, spawnDirection, requiredTrainLength);
                    if (dest == null) continue;
                    return dest;
                }
            }
        }
        dest = PortalDestination.findAgainst(regionMin, regionMax, spawnDirection, requiredTrainLength);
        if (dest != null) {
            return dest;
        }
        dest = PortalDestination.findAgainst(regionMin, regionMax, portalFacing, requiredTrainLength);
        if (dest != null) {
            return dest;
        }
        dest = PortalDestination.findAgainst(regionMin, regionMax, portalFacing.getOppositeFace(), requiredTrainLength);
        if (dest != null) {
            return dest;
        }
        return null;
    }

    private static PortalDestination findAgainst(Block regionMin, Block regionMax, BlockFace spawnDirection, double requiredTrainLength) {
        int x1 = regionMin.getX();
        int y1 = regionMin.getY();
        int z1 = regionMin.getZ();
        int x2 = regionMax.getX();
        int y2 = regionMax.getY();
        int z2 = regionMax.getZ();
        if (spawnDirection.getModY() != 0) {
            y1 = y2 = (spawnDirection.getModY() > 0 ? regionMax.getY() : regionMin.getY()) + spawnDirection.getModY();
        } else if (spawnDirection.getModX() != 0) {
            x1 = x2 = (spawnDirection.getModX() > 0 ? regionMax.getX() : regionMin.getX()) + spawnDirection.getModX();
        } else {
            z1 = z2 = (spawnDirection.getModZ() > 0 ? regionMax.getZ() : regionMin.getZ()) + spawnDirection.getModZ();
        }
        for (int y = y1; y <= y2; ++y) {
            for (int x = x1; x <= x2; ++x) {
                for (int z = z1; z <= z2; ++z) {
                    Block block = regionMin.getWorld().getBlockAt(x, y, z);
                    PortalDestination dest = PortalDestination.findRailDestination(block, spawnDirection, requiredTrainLength);
                    if (dest == null) continue;
                    return dest;
                }
            }
        }
        return null;
    }

    private static PortalDestination findRailDestination(Block rails, BlockFace direction, double requiredTrainLength) {
        double distBackward;
        RailType railType = RailType.getType(rails);
        if (railType == RailType.NONE) {
            return null;
        }
        RailState state = RailState.getSpawnState(RailPiece.create(railType, rails));
        TrackDistanceCalculator forwards = new TrackDistanceCalculator(state);
        TrackDistanceCalculator backwards = new TrackDistanceCalculator(state.cloneAndInvertMotion());
        if (forwards.direction == direction && forwards.computeDistance(requiredTrainLength) >= requiredTrainLength) {
            return forwards.destination;
        }
        if (backwards.direction == direction && backwards.computeDistance(requiredTrainLength) >= requiredTrainLength) {
            return backwards.destination;
        }
        double distForward = forwards.computeDistance(Math.max(16.0, requiredTrainLength));
        if (distForward >= (distBackward = backwards.computeDistance(Math.max(16.0, requiredTrainLength)))) {
            return forwards.destination;
        }
        return backwards.destination;
    }

    private static class TrackDistanceCalculator {
        final TrackWalkingPoint wp;
        final BlockFace direction;
        final PortalDestination destination;

        public TrackDistanceCalculator(RailState state) {
            state.initEnterDirection();
            this.wp = new TrackWalkingPoint(state);
            this.direction = state.enterFace();
            this.destination = new PortalDestination(state.railBlock(), new BlockFace[]{this.direction});
        }

        public double computeDistance(double minimal) {
            while (this.wp.moveFull() && !(this.wp.movedTotal >= minimal)) {
            }
            return this.wp.movedTotal;
        }
    }
}

