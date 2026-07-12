/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.math;

import com.bergerkiller.bukkit.common.math.VectorList;
import org.bukkit.util.Vector;

final class VectorListJoinedImpl
implements VectorList {
    private final VectorList first;
    private final VectorList second;
    private final int size;

    public VectorListJoinedImpl(VectorList first, VectorList second) {
        this.first = first;
        this.second = second;
        this.size = first.size() + second.size();
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Vector get(int index) {
        VectorList first = this.first;
        int firstSize = first.size();
        if (index < firstSize) {
            return first.get(index);
        }
        return this.second.get(index - firstSize);
    }

    @Override
    public Vector get(int index, Vector into) {
        VectorList first = this.first;
        int firstSize = first.size();
        if (index < firstSize) {
            return first.get(index, into);
        }
        return this.second.get(index - firstSize, into);
    }

    @Override
    public VectorList.VectorIterator vectorIterator(int offset, int length) {
        return VectorListJoinedImpl.rangeVectorIterator(this.first, this.second, offset, length);
    }

    @Override
    public VectorList.VectorIterator vectorIterator() {
        return VectorList.VectorIterator.join(this.first.vectorIterator(), this.second.vectorIterator());
    }

    public static VectorList.VectorIterator rangeVectorIterator(VectorList first, VectorList second, int offset, int length) {
        int firstSize = first.size();
        if (offset >= firstSize) {
            return second.vectorIterator(offset - firstSize, length - firstSize);
        }
        if (length + offset <= firstSize) {
            return first.vectorIterator(offset, length);
        }
        VectorList.VectorIterator ja = first.vectorIterator(offset, firstSize - offset);
        VectorList.VectorIterator jb = second.vectorIterator(firstSize, length - firstSize);
        return VectorList.VectorIterator.join(ja, jb);
    }
}

