/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.material.Rails
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.utils.TrackMovingPoint;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Rails;
import org.bukkit.util.Vector;

@Deprecated
public class TrackIterator
implements Iterator<Block> {
    private final int maxdistance;
    private final boolean onlyInLoadedChunks;
    private TrackMovingPoint movingPoint;
    private int distance;
    private double cartDistance;
    private Set<IntVector3> coordinates = new HashSet<IntVector3>();

    public TrackIterator(Block startblock, BlockFace direction) {
        this(startblock, direction, false);
    }

    public TrackIterator(Block startblock, BlockFace direction, boolean onlyInLoadedChunks) {
        this(startblock, direction, 16000, onlyInLoadedChunks);
    }

    public TrackIterator(Block startblock, BlockFace direction, int maxdistance, boolean onlyInLoadedChunks) {
        this.maxdistance = maxdistance;
        this.onlyInLoadedChunks = onlyInLoadedChunks;
        this.reset(startblock, direction);
    }

    public static TrackIterator createFinder(Block startBlock, BlockFace direction, Block destination) {
        int maxDistance = BlockUtil.getManhattanDistance((Block)startBlock, (Block)destination, (boolean)true) + 2;
        return new TrackIterator(startBlock, direction, maxDistance, false);
    }

    public static boolean canReach(Block rail, BlockFace direction, Block destination) {
        return TrackIterator.createFinder(rail, direction, destination).tryFind(destination);
    }

    public static boolean isConnected(Block rail1, Block rail2, boolean bothways) {
        if (rail1 == null || rail2 == null) {
            return false;
        }
        if (BlockUtil.equals((Block)rail1, (Block)rail2)) {
            return true;
        }
        RailType rail1type = RailType.getType(rail1);
        RailType rail2type = RailType.getType(rail2);
        if (rail1type == RailType.NONE || rail2type == RailType.NONE) {
            return false;
        }
        BlockFace[] rail1dirs = rail1type.getPossibleDirections(rail1);
        BlockFace[] rail2dirs = rail2type.getPossibleDirections(rail2);
        if (rail1dirs.length == 0 || rail2dirs.length == 0) {
            return false;
        }
        Block pos1 = rail1type.findMinecartPos(rail1);
        Block pos2 = rail1type.findMinecartPos(rail2);
        BlockFace dir1 = TrackIterator.getPreferredDirection(rail1dirs, pos1, pos2);
        BlockFace dir2 = TrackIterator.getPreferredDirection(rail2dirs, pos2, pos1);
        int maxDistance = BlockUtil.getManhattanDistance((Block)pos1, (Block)pos2, (boolean)true) + 2;
        TrackIterator iter = new TrackIterator(null, null, maxDistance, false);
        if (bothways) {
            return iter.canReach(rail1, rail2, rail1dirs, dir1) && iter.canReach(rail2, rail1, rail2dirs, dir2);
        }
        return iter.canReach(rail1, rail2, rail1dirs, dir1) || iter.canReach(rail2, rail1, rail2dirs, dir2);
    }

    private static BlockFace getPreferredDirection(BlockFace[] directions, Block from, Block to) {
        for (BlockFace dir : directions) {
            boolean preferred;
            if (FaceUtil.isVertical((BlockFace)dir)) {
                preferred = dir == Util.getVerticalFace(to.getY() > from.getY());
            } else {
                boolean bl = preferred = dir == FaceUtil.getDirection((Block)from, (Block)to, (boolean)false);
            }
            if (!preferred) continue;
            return dir;
        }
        return directions[0];
    }

    public TrackIterator reset(Block startBlock, BlockFace startDirection) {
        this.coordinates.clear();
        this.distance = 0;
        this.cartDistance = 0.0;
        this.movingPoint = new TrackMovingPoint(startBlock, startDirection);
        return this;
    }

    public int getDistance() {
        return this.distance;
    }

    public double getCartDistance() {
        return this.cartDistance;
    }

    @Override
    public boolean hasNext() {
        return this.movingPoint.hasNext() && this.distance <= this.maxdistance;
    }

    private void genNextBlock() {
        if (this.onlyInLoadedChunks) {
            int x = (int)((double)this.movingPoint.current.getX() + this.movingPoint.currentDirection.getX());
            int z = (int)((double)this.movingPoint.current.getZ() + this.movingPoint.currentDirection.getZ());
            if (!this.movingPoint.current.getWorld().isChunkLoaded(x >> 4, z >> 4)) {
                this.movingPoint.next(false);
                return;
            }
        }
        this.movingPoint.next();
        if (this.movingPoint.hasNext() && !this.coordinates.add(new IntVector3(this.movingPoint.next))) {
            this.movingPoint.clearNext();
        }
    }

    public void stop() {
        this.movingPoint.clearNext();
    }

    public Vector currentDirection() {
        return this.movingPoint.currentDirection;
    }

    public Location currentLocation() {
        return this.movingPoint.currentLocation;
    }

    public Block current() {
        return this.movingPoint.current;
    }

    public RailPiece currentRailPiece() {
        return this.movingPoint.currentRailPiece;
    }

    public RailType currentRailType() {
        return this.movingPoint.currentRail;
    }

    public Rails currentRails() {
        return BlockUtil.getRails((Block)this.current());
    }

    public Vector peekNextDirection() {
        return this.movingPoint.nextDirection;
    }

    public Block peekNext() {
        return this.movingPoint.next;
    }

    @Override
    public Block next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException("No next track is available");
        }
        Vector oldDirection = this.currentDirection();
        this.genNextBlock();
        Vector newDirection = this.currentDirection();
        ++this.distance;
        this.cartDistance = Math.abs(oldDirection.dot(newDirection)) >= 0.999999 ? (this.cartDistance += 1.0) : (this.cartDistance += 0.707106781);
        return this.current();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("TrackIterator.remove is not supported");
    }

    public boolean tryFind(Block railsBlock) {
        while (this.hasNext()) {
            if (!BlockUtil.equals((Block)this.next(), (Block)railsBlock)) continue;
            return true;
        }
        return false;
    }

    private boolean canReach(Block rail, Block destination, BlockFace[] faces, BlockFace preferredFace) {
        for (BlockFace face : faces) {
            if (face != preferredFace) continue;
            if (!this.reset(rail, face).tryFind(destination)) break;
            return true;
        }
        for (BlockFace face : faces) {
            if (face == preferredFace || !this.reset(rail, face).tryFind(destination)) continue;
            return true;
        }
        return false;
    }
}

