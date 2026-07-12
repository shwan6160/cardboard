/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.common.collections;

import java.util.ArrayList;
import java.util.Locale;
import org.bukkit.block.BlockFace;

public final class BlockFaceSet {
    private final int _mask;
    private final BlockFace[] _faces;
    private static final BlockFaceSet[] cache = new BlockFaceSet[64];
    public static final int MASK_NORTH = 1;
    public static final int MASK_EAST = 2;
    public static final int MASK_SOUTH = 4;
    public static final int MASK_WEST = 8;
    public static final int MASK_UP = 16;
    public static final int MASK_DOWN = 32;
    public static final BlockFaceSet NONE;
    public static final BlockFaceSet ALL;

    public static BlockFaceSet byMask(int mask) {
        return cache[mask];
    }

    public static BlockFaceSet of(BlockFace ... faces) {
        int mask = 0;
        for (BlockFace face : faces) {
            mask |= 1 << face.ordinal();
        }
        return BlockFaceSet.byMask(mask);
    }

    public static int getMask(BlockFace face) {
        return 1 << face.ordinal();
    }

    private BlockFaceSet(int mask) {
        this._mask = mask;
        BlockFace[] all_faces = BlockFace.values();
        ArrayList<BlockFace> tmp = new ArrayList<BlockFace>();
        for (int i = 0; i < 6; ++i) {
            if ((mask & 1 << i) == 0) continue;
            tmp.add(all_faces[i]);
        }
        this._faces = tmp.toArray(new BlockFace[tmp.size()]);
    }

    public int mask() {
        return this._mask;
    }

    public BlockFace[] getFaces() {
        return this._faces;
    }

    public boolean get(BlockFace face) {
        return this.get(BlockFaceSet.getMask(face));
    }

    public boolean get(int faceMask) {
        return (this._mask & faceMask) != 0;
    }

    public BlockFaceSet set(BlockFace face) {
        int new_mask = this._mask | BlockFaceSet.getMask(face);
        return this._mask == new_mask ? this : BlockFaceSet.byMask(new_mask);
    }

    public BlockFaceSet clear(BlockFace face) {
        int new_mask = this._mask & ~BlockFaceSet.getMask(face);
        return this._mask == new_mask ? this : BlockFaceSet.byMask(new_mask);
    }

    public BlockFaceSet set(BlockFace face, boolean set) {
        return this.setMask(BlockFaceSet.getMask(face), set);
    }

    public BlockFaceSet setMask(int bits, boolean set) {
        if ((this._mask & bits) != 0 != set) {
            return BlockFaceSet.byMask(this._mask ^ bits);
        }
        return this;
    }

    public BlockFaceSet setNorth(boolean north) {
        return this.setMask(1, north);
    }

    public BlockFaceSet setEast(boolean east) {
        return this.setMask(2, east);
    }

    public BlockFaceSet setSouth(boolean south) {
        return this.setMask(4, south);
    }

    public BlockFaceSet setWest(boolean west) {
        return this.setMask(8, west);
    }

    public BlockFaceSet setUp(boolean up) {
        return this.setMask(16, up);
    }

    public BlockFaceSet setDown(boolean down) {
        return this.setMask(32, down);
    }

    public boolean north() {
        return (this._mask & 1) != 0;
    }

    public boolean east() {
        return (this._mask & 2) != 0;
    }

    public boolean south() {
        return (this._mask & 4) != 0;
    }

    public boolean west() {
        return (this._mask & 8) != 0;
    }

    public boolean up() {
        return (this._mask & 0x10) != 0;
    }

    public boolean down() {
        return (this._mask & 0x20) != 0;
    }

    public int hashCode() {
        return this._mask;
    }

    public boolean equals(Object obj) {
        return this == obj;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append('[');
        for (int i = 0; i < this._faces.length; ++i) {
            if (i > 0) {
                str.append(',');
            }
            str.append(this._faces[i].name().toLowerCase(Locale.ENGLISH));
        }
        str.append(']');
        return str.toString();
    }

    static {
        for (int i = 0; i < cache.length; ++i) {
            BlockFaceSet.cache[i] = new BlockFaceSet(i);
        }
        NONE = BlockFaceSet.byMask(0);
        ALL = BlockFaceSet.byMask(63);
    }
}

