/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.math;

import com.bergerkiller.bukkit.common.math.VectorList;
import org.bukkit.util.Vector;

class VectorListBasicImpl
implements VectorList {
    protected final int size;
    protected final double[] points;

    protected VectorListBasicImpl(VectorList vectors) {
        this.size = vectors.size();
        if (vectors instanceof VectorListBasicImpl) {
            VectorListBasicImpl vectorsImpl = (VectorListBasicImpl)vectors;
            this.points = (double[])vectorsImpl.points.clone();
        } else {
            this.points = new double[3 * this.size];
            this.load(vectors.vectorIterator());
        }
    }

    protected VectorListBasicImpl(int size, VectorList.VectorIterator iter) {
        this(size);
        this.load(iter);
    }

    protected VectorListBasicImpl(int size) {
        this.size = size;
        this.points = new double[3 * size];
    }

    private void load(VectorList.VectorIterator iter) {
        int pos = 0;
        double[] points = this.points;
        int end_pos = 3 * this.size;
        while (pos < end_pos && iter.advance()) {
            points[pos++] = iter.x();
            points[pos++] = iter.y();
            points[pos++] = iter.z();
        }
    }

    @Override
    public final int size() {
        return this.size;
    }

    @Override
    public final Vector get(int index) {
        double[] points = this.points;
        int pos = 3 * index;
        double x = points[pos++];
        double y = points[pos++];
        double z = points[pos];
        return new Vector(x, y, z);
    }

    @Override
    public final Vector get(int index, Vector into) {
        double[] points = this.points;
        int pos = 3 * index;
        into.setX(points[pos++]);
        into.setY(points[pos++]);
        into.setZ(points[pos]);
        return into;
    }

    @Override
    public VectorList.VectorIterator vectorIterator() {
        return new VectorList.VectorIterator(){

            @Override
            public boolean advance() {
                int nextIndex = this.index + 1;
                if (nextIndex >= VectorListBasicImpl.this.size()) {
                    return false;
                }
                this.index = nextIndex;
                int pos = 3 * nextIndex;
                double[] points = VectorListBasicImpl.this.points;
                this.x = points[pos++];
                this.y = points[pos++];
                this.z = points[pos];
                return true;
            }
        };
    }

    @Override
    public VectorList.VectorIterator vectorIterator(final int offset, final int length) {
        return new VectorList.VectorIterator(this){
            final /* synthetic */ VectorListBasicImpl this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public boolean advance() {
                int nextIndex = this.index + 1;
                if (nextIndex >= length) {
                    return false;
                }
                this.index = nextIndex;
                int pos = 3 * (nextIndex + offset);
                double[] points = this.this$0.points;
                this.x = points[pos++];
                this.y = points[pos++];
                this.z = points[pos];
                return true;
            }
        };
    }

    public String toString() {
        return VectorListBasicImpl.genericToString(this);
    }

    public static String genericToString(VectorList vectorList) {
        StringBuilder str = new StringBuilder();
        VectorList.VectorIterator iter = vectorList.vectorIterator();
        str.append(vectorList.getClass().getSimpleName()).append("<size=").append(vectorList.size()).append("> [");
        while (iter.advance()) {
            str.append("\n  {").append(iter.x()).append(" / ").append(iter.y()).append(" / ").append(iter.z()).append("}");
        }
        str.append("\n]");
        return str.toString();
    }
}

