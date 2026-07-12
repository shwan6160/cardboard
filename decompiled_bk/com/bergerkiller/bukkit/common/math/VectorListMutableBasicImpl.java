/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.math;

import com.bergerkiller.bukkit.common.math.VectorList;
import com.bergerkiller.bukkit.common.math.VectorListBasicImpl;
import com.bergerkiller.bukkit.common.math.VectorListMutable;

class VectorListMutableBasicImpl
extends VectorListBasicImpl
implements VectorListMutable {
    public VectorListMutableBasicImpl(VectorList vectors) {
        super(vectors);
    }

    public VectorListMutableBasicImpl(int size, VectorList.VectorIterator iter) {
        super(size, iter);
    }

    public VectorListMutableBasicImpl(int size) {
        super(size);
    }

    @Override
    public void set(int index, double x, double y, double z) {
        double[] points = this.points;
        int pos = 3 * index;
        points[pos++] = x;
        points[pos++] = y;
        points[pos] = z;
    }

    @Override
    public VectorListMutable.MutableVectorIterator vectorIterator() {
        return new VectorListMutable.MutableVectorIterator(){

            @Override
            public void set(double x, double y, double z) {
                VectorListMutableBasicImpl.this.set(this.index, x, y, z);
            }

            @Override
            public boolean advance() {
                int nextIndex = this.index + 1;
                if (nextIndex >= VectorListMutableBasicImpl.this.size()) {
                    return false;
                }
                this.index = nextIndex;
                int pos = 3 * nextIndex;
                double[] points = VectorListMutableBasicImpl.this.points;
                this.x = points[pos++];
                this.y = points[pos++];
                this.z = points[pos];
                return true;
            }
        };
    }

    @Override
    public VectorListMutable.MutableVectorIterator vectorIterator(final int offset, final int length) {
        return new VectorListMutable.MutableVectorIterator(this){
            int position = -1;
            final /* synthetic */ VectorListMutableBasicImpl this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void set(double x, double y, double z) {
                double[] points = this.this$0.points;
                int pos = this.position;
                points[pos++] = x;
                points[pos++] = y;
                points[pos] = z;
            }

            @Override
            public boolean advance() {
                int position;
                int nextIndex = this.index + 1;
                if (nextIndex >= length) {
                    return false;
                }
                this.index = nextIndex;
                this.position = position = 3 * (nextIndex + offset);
                double[] points = this.this$0.points;
                this.x = points[position++];
                this.y = points[position++];
                this.z = points[position];
                return true;
            }
        };
    }
}

