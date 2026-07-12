/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jdk.incubator.vector.DoubleVector
 *  jdk.incubator.vector.Vector
 *  jdk.incubator.vector.VectorOperators
 *  jdk.incubator.vector.VectorSpecies
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.math;

import com.bergerkiller.bukkit.common.math.VectorList;
import com.bergerkiller.bukkit.common.math.VectorListBasicImpl;
import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.Vector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

final class VectorListSIMDImpl
implements VectorList {
    private final VectorSpecies<Double> species;
    private final DoubleVector xVec;
    private final DoubleVector yVec;
    private final DoubleVector zVec;

    public static VectorList.Factory createFactoryFor(final VectorSpecies<Double> species) {
        return new VectorList.Factory(){
            final int requiredSize;
            final ThreadLocal<double[]> threadLocalBuffer;
            {
                this.requiredSize = species.length();
                this.threadLocalBuffer = ThreadLocal.withInitial(() -> new double[3 * this.requiredSize]);
            }

            @Override
            public boolean isOptimizedSize(int size) {
                return size == this.requiredSize;
            }

            @Override
            public int getRequiredSize() {
                return this.requiredSize;
            }

            @Override
            public VectorList copyOf(VectorList vectorValues) {
                if (vectorValues instanceof VectorListSIMDImpl && ((VectorListSIMDImpl)vectorValues).species == species) {
                    return vectorValues;
                }
                if (vectorValues.size() != this.requiredSize) {
                    throw new IllegalArgumentException("This SIMD implementation requires exactly " + this.requiredSize + " vectors");
                }
                return new VectorListSIMDImpl((VectorSpecies<Double>)species, this.threadLocalBuffer.get(), vectorValues.vectorIterator());
            }

            @Override
            public VectorList createWith(int size, VectorList.VectorIterator iterator) {
                if (size != this.requiredSize) {
                    throw new IllegalArgumentException("This SIMD implementation requires exactly " + this.requiredSize + " vectors");
                }
                return new VectorListSIMDImpl((VectorSpecies<Double>)species, this.threadLocalBuffer.get(), iterator);
            }
        };
    }

    VectorListSIMDImpl(VectorSpecies<Double> species, double[] buffer, VectorList.VectorIterator iterator) {
        int size = species.length();
        for (int i = 0; i < size && iterator.advance(); ++i) {
            buffer[i] = iterator.x();
            buffer[i + size] = iterator.y();
            buffer[i + size + size] = iterator.z();
        }
        this.species = species;
        this.xVec = DoubleVector.fromArray(species, (double[])buffer, (int)0);
        this.yVec = DoubleVector.fromArray(species, (double[])buffer, (int)size);
        this.zVec = DoubleVector.fromArray(species, (double[])buffer, (int)(size + size));
    }

    @Override
    public int size() {
        return this.species.length();
    }

    @Override
    public org.bukkit.util.Vector get(int index) {
        return new org.bukkit.util.Vector(this.xVec.lane(index), this.yVec.lane(index), this.zVec.lane(index));
    }

    @Override
    public org.bukkit.util.Vector get(int index, org.bukkit.util.Vector into) {
        into.setX(this.xVec.lane(index));
        into.setY(this.yVec.lane(index));
        into.setZ(this.zVec.lane(index));
        return into;
    }

    @Override
    public VectorList.VectorIterator vectorIterator() {
        return new VectorList.VectorIterator(){

            @Override
            public boolean advance() {
                int nextIndex = this.index + 1;
                if (nextIndex >= VectorListSIMDImpl.this.size()) {
                    return false;
                }
                this.index = nextIndex;
                this.x = VectorListSIMDImpl.this.xVec.lane(this.index);
                this.y = VectorListSIMDImpl.this.yVec.lane(this.index);
                this.z = VectorListSIMDImpl.this.zVec.lane(this.index);
                return true;
            }
        };
    }

    @Override
    public VectorList.VectorIterator vectorIterator(final int offset, final int length) {
        return new VectorList.VectorIterator(this){
            final /* synthetic */ VectorListSIMDImpl this$0;
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
                int pos = this.index + offset;
                this.x = this.this$0.xVec.lane(pos);
                this.y = this.this$0.yVec.lane(pos);
                this.z = this.this$0.zVec.lane(pos);
                return true;
            }
        };
    }

    @Override
    public VectorList.Projection projectAxis(double axisX, double axisY, double axisZ) {
        VectorSpecies species = DoubleVector.SPECIES_PREFERRED;
        DoubleVector ax = DoubleVector.broadcast((VectorSpecies)species, (double)axisX);
        DoubleVector ay = DoubleVector.broadcast((VectorSpecies)species, (double)axisY);
        DoubleVector az = DoubleVector.broadcast((VectorSpecies)species, (double)axisZ);
        DoubleVector projection = this.xVec.mul((Vector)ax).add((Vector)this.yVec.mul((Vector)ay)).add((Vector)this.zVec.mul((Vector)az));
        double min = projection.reduceLanes(VectorOperators.MIN);
        double max = projection.reduceLanes(VectorOperators.MAX);
        return new VectorList.Projection(min, max);
    }

    @Override
    public void crossProduct(VectorList rightList, VectorList.VectorConsumer consumer) {
        if (!(rightList instanceof VectorListSIMDImpl)) {
            VectorList.super.crossProduct(rightList, consumer);
            return;
        }
        VectorListSIMDImpl right = (VectorListSIMDImpl)rightList;
        DoubleVector cx = this.yVec.mul((Vector)right.zVec).sub((Vector)right.yVec.mul((Vector)this.zVec));
        DoubleVector cy = this.zVec.mul((Vector)right.xVec).sub((Vector)right.zVec.mul((Vector)this.xVec));
        DoubleVector cz = this.xVec.mul((Vector)right.yVec).sub((Vector)right.xVec.mul((Vector)this.yVec));
        int size = this.species.length();
        for (int lane = 0; lane < size; ++lane) {
            if (consumer.acceptVector(cx.lane(lane), cy.lane(lane), cz.lane(lane))) continue;
            throw new IllegalArgumentException("Consumer can't store the full cross-product result");
        }
    }

    public String toString() {
        return VectorListBasicImpl.genericToString(this);
    }
}

