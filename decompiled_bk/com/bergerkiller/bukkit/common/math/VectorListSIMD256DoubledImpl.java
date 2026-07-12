/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jdk.incubator.vector.DoubleVector
 *  jdk.incubator.vector.VectorSpecies
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.math;

import com.bergerkiller.bukkit.common.math.VectorList;
import com.bergerkiller.bukkit.common.math.VectorListBasicImpl;
import com.bergerkiller.bukkit.common.math.VectorListJoinedImpl;
import com.bergerkiller.bukkit.common.math.VectorListSIMDImpl;
import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorSpecies;
import org.bukkit.util.Vector;

final class VectorListSIMD256DoubledImpl
implements VectorList {
    public static final VectorList.Factory FACTORY = new VectorList.Factory(){

        @Override
        public int getRequiredSize() {
            return 8;
        }

        @Override
        public VectorList copyOf(VectorList vectorValues) {
            if (vectorValues instanceof VectorListSIMD256DoubledImpl) {
                return vectorValues;
            }
            if (vectorValues.size() != 8) {
                throw new IllegalArgumentException("This SIMD Doubled implementation requires exactly 8 points");
            }
            return new VectorListSIMD256DoubledImpl(vectorValues.vectorIterator());
        }

        @Override
        public VectorList createWith(int size, VectorList.VectorIterator iterator) {
            if (size != 8) {
                throw new IllegalArgumentException("This SIMD Doubled implementation requires exactly 8 points");
            }
            return new VectorListSIMD256DoubledImpl(iterator);
        }
    };
    private final VectorListSIMDImpl a;
    private final VectorListSIMDImpl b;
    private static final ThreadLocal<double[]> threadLocalBuffer = ThreadLocal.withInitial(() -> new double[12]);

    private VectorListSIMD256DoubledImpl(VectorList.VectorIterator iterator) {
        double[] buffer = threadLocalBuffer.get();
        this.a = new VectorListSIMDImpl((VectorSpecies<Double>)DoubleVector.SPECIES_256, buffer, iterator);
        this.b = new VectorListSIMDImpl((VectorSpecies<Double>)DoubleVector.SPECIES_256, buffer, iterator);
    }

    @Override
    public int size() {
        return 8;
    }

    @Override
    public Vector get(int index) {
        if (index < 4) {
            return this.a.get(index);
        }
        return this.b.get(index - 4);
    }

    @Override
    public Vector get(int index, Vector into) {
        if (index < 4) {
            this.a.get(index, into);
        } else {
            this.b.get(index - 4, into);
        }
        return into;
    }

    @Override
    public VectorList.VectorIterator vectorIterator() {
        return VectorList.VectorIterator.join(this.a.vectorIterator(), this.b.vectorIterator());
    }

    @Override
    public VectorList.VectorIterator vectorIterator(int offset, int length) {
        return VectorListJoinedImpl.rangeVectorIterator(this.a, this.b, offset, length);
    }

    @Override
    public VectorList.Projection projectAxis(double axisX, double axisY, double axisZ) {
        VectorList.Projection pa = this.a.projectAxis(axisX, axisY, axisZ);
        VectorList.Projection pb = this.b.projectAxis(axisX, axisY, axisZ);
        return new VectorList.Projection(Math.min(pa.min, pb.min), Math.max(pa.max, pb.max));
    }

    @Override
    public void crossProduct(VectorList rightList, VectorList.VectorConsumer consumer) {
        if (!(rightList instanceof VectorListSIMD256DoubledImpl)) {
            VectorList.super.crossProduct(rightList, consumer);
            return;
        }
        VectorListSIMD256DoubledImpl right = (VectorListSIMD256DoubledImpl)rightList;
        this.a.crossProduct((VectorList)right.a, consumer);
        this.b.crossProduct((VectorList)right.b, consumer);
    }

    public String toString() {
        return VectorListBasicImpl.genericToString(this);
    }
}

