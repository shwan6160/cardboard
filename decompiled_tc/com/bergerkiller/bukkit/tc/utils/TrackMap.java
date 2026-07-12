/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.utils.TrackIterator;
import java.util.ArrayList;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class TrackMap
extends ArrayList<Block> {
    private static final long serialVersionUID = 1L;
    private final TrackIterator iterator;

    public TrackMap(Block start, BlockFace direction) {
        this(new TrackIterator(start, direction));
    }

    public TrackMap(Block start, BlockFace direction, int maxdistance) {
        this(new TrackIterator(start, direction, maxdistance, false));
    }

    public TrackMap(TrackIterator iterator) {
        this.iterator = iterator;
    }

    public TrackMap generate(int size) {
        while (this.getDistance() < size && this.hasNext()) {
            this.next();
        }
        return this;
    }

    public TrackMap generate(int size, double stepsize) {
        return this.generate((int)(stepsize * (double)size));
    }

    public boolean find(Block rail, int maxstepcount) {
        while (maxstepcount > 0) {
            Block next = this.next();
            if (next == null) {
                return false;
            }
            if (BlockUtil.equals((Block)rail, (Block)next)) {
                return true;
            }
            --maxstepcount;
        }
        return false;
    }

    public Block last() {
        return this.last(0);
    }

    public Block last(int index) {
        index = this.size() - index - 1;
        if (index < 0) {
            return null;
        }
        return (Block)this.get(index);
    }

    public int getDistance() {
        return this.iterator.getDistance();
    }

    public Block getBlock() {
        return this.iterator.current();
    }

    public RailPiece getRailPiece() {
        return this.iterator.currentRailPiece();
    }

    public RailType getRailType() {
        return this.iterator.currentRailType();
    }

    public Vector getDirection() {
        return this.iterator.currentDirection();
    }

    public TrackIterator getTrackIterator() {
        return this.iterator;
    }

    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    public Block next() {
        Block next = this.iterator.next();
        if (next != null) {
            this.add(next);
        }
        return next;
    }
}

