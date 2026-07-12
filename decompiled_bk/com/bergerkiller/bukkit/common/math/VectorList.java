/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.math;

import com.bergerkiller.bukkit.common.math.VectorListFactorySelector;
import com.bergerkiller.bukkit.common.math.VectorListJoinedImpl;
import com.bergerkiller.bukkit.common.math.VectorListMutable;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.stream.Collector;
import org.bukkit.util.Vector;

public interface VectorList
extends Iterable<Vector> {
    public static final Factory FACTORY = VectorListFactorySelector.initFactory();

    public static VectorList copyOf(VectorList vectorValues) {
        return FACTORY.copyOf(vectorValues);
    }

    public static VectorList copyOf(Collection<Vector> vectorValues) {
        return FACTORY.copyOf(vectorValues);
    }

    public static VectorList copyOf(Vector ... vectorValues) {
        return FACTORY.copyOf(vectorValues);
    }

    public static VectorList createWith(int size, VectorIterator iterator) {
        return FACTORY.createWith(size, iterator);
    }

    public static VectorList join(VectorList first, VectorList second) {
        return new VectorListJoinedImpl(first, second);
    }

    public int size();

    public Vector get(int var1);

    public Vector get(int var1, Vector var2);

    public VectorIterator vectorIterator(int var1, int var2);

    default public VectorIterator vectorIterator() {
        return this.vectorIterator(0, this.size());
    }

    @Override
    default public Iterator<Vector> iterator() {
        return new Iterator<Vector>(){
            VectorIterator iter = null;
            boolean hasNext;

            @Override
            public boolean hasNext() {
                if (this.iter == null) {
                    this.iter = VectorList.this.vectorIterator();
                    this.hasNext = this.iter.advance();
                }
                return this.hasNext;
            }

            @Override
            public Vector next() {
                if (!this.hasNext) {
                    throw new NoSuchElementException();
                }
                Vector value = this.iter.toVector();
                this.hasNext = this.iter.advance();
                return value;
            }
        };
    }

    default public <R, A> R collect(Collector<? super Vector, A, R> collector) {
        return this.vectorIterator().collectRemaining(collector);
    }

    default public boolean forEach(VectorConsumer consumer) {
        return this.vectorIterator().forEachRemaining(consumer);
    }

    default public Projection projectAxis(Vector axis) {
        return this.projectAxis(axis.getX(), axis.getY(), axis.getZ());
    }

    default public Projection projectAxis(double axisX, double axisY, double axisZ) {
        double min = Double.MAX_VALUE;
        double max = -1.7976931348623157E308;
        VectorIterator iter = this.vectorIterator();
        while (iter.advance()) {
            double projection = iter.x() * axisX + iter.y() * axisY + iter.z() * axisZ;
            min = Math.min(min, projection);
            max = Math.max(max, projection);
        }
        return new Projection(min, max);
    }

    default public VectorList crossProduct(VectorList right) {
        VectorListMutable result = VectorListMutable.create(this.size());
        this.crossProduct(right, result);
        return result;
    }

    default public void crossProduct(VectorList right, VectorListMutable result) {
        try {
            this.crossProduct(right, result.vectorIterator());
        }
        catch (IllegalArgumentException ex) {
            if (result.size() < this.size()) {
                throw new IllegalArgumentException("Result mutable vector list (size=" + result.size() + ") can't store the cross product results (size=" + this.size() + ")");
            }
            throw ex;
        }
    }

    default public void crossProduct(VectorList right, VectorConsumer consumer) {
        try {
            VectorIterator.performCrossProduct(this.vectorIterator(), right.vectorIterator(), consumer);
        }
        catch (IllegalArgumentException ex) {
            if (this.size() != right.size()) {
                throw new IllegalArgumentException("Left list (size=" + this.size() + ") has a different size than the right size (size=" + right.size() + ")");
            }
            throw ex;
        }
    }

    public static boolean hasSeparatedAxes(VectorList vertexPoints1, VectorList vertexPoints2, double axisX, double axisY, double axisZ) {
        return Projection.isSeparated(vertexPoints1.projectAxis(axisX, axisY, axisZ), vertexPoints2.projectAxis(axisX, axisY, axisZ));
    }

    public static boolean hasSeparatedAxes(VectorList vertexPoints1, VectorList vertexPoints2, Vector axis) {
        return Projection.isSeparated(vertexPoints1.projectAxis(axis), vertexPoints2.projectAxis(axis));
    }

    public static boolean areVerticesOverlapping(VectorList vertexPoints1, VectorList vertexPoints2, VectorIterator axisIterator) {
        while (axisIterator.advance()) {
            if (!VectorList.hasSeparatedAxes(vertexPoints1, vertexPoints2, axisIterator.x(), axisIterator.y(), axisIterator.z())) continue;
            return false;
        }
        return true;
    }

    public static interface Factory {
        default public int getRequiredSize() {
            return -1;
        }

        default public boolean isOptimizedSize(int size) {
            return false;
        }

        public VectorList copyOf(VectorList var1);

        public VectorList createWith(int var1, VectorIterator var2);

        default public VectorList copyOf(Vector ... vectorValues) {
            return this.copyOf(Arrays.asList(vectorValues));
        }

        default public VectorList copyOf(Collection<Vector> vectorValues) {
            return this.createWith(vectorValues.size(), VectorIterator.iterate(vectorValues));
        }
    }

    public static abstract class VectorIterator {
        protected int index = -1;
        protected double x;
        protected double y;
        protected double z;

        public abstract boolean advance();

        public final int index() {
            return this.index;
        }

        public final double x() {
            return this.x;
        }

        public final double y() {
            return this.y;
        }

        public final double z() {
            return this.z;
        }

        public final Vector toVector() {
            return new Vector(this.x, this.y, this.z);
        }

        public final Vector toVector(Vector into) {
            into.setX(this.x);
            into.setY(this.y);
            into.setZ(this.z);
            return into;
        }

        public <R, A> R collectRemaining(Collector<? super Vector, A, R> collector) {
            A container = collector.supplier().get();
            BiConsumer<A, Vector> accumulator = collector.accumulator();
            while (this.advance()) {
                accumulator.accept(container, (Vector)this.toVector());
            }
            return collector.finisher().apply(container);
        }

        public boolean forEachRemaining(VectorConsumer consumer) {
            while (this.advance()) {
                if (consumer.acceptVector(this.x, this.y, this.z)) continue;
                return false;
            }
            return true;
        }

        protected void load(Vector v) {
            this.x = v.getX();
            this.y = v.getY();
            this.z = v.getZ();
        }

        protected void load(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        protected boolean tryLoadNormalizedCrossProduct(Vector left, Vector right) {
            double z;
            double y;
            double x = left.getY() * right.getZ() - right.getY() * left.getZ();
            double lengthSquared = x * x + (y = left.getZ() * right.getX() - right.getZ() * left.getX()) * y + (z = left.getX() * right.getY() - right.getX() * left.getY()) * z;
            if (lengthSquared < 1.0E-6) {
                return false;
            }
            double normFactor = MathUtil.getNormalizationFactorLS(lengthSquared);
            this.x = x * normFactor;
            this.y = y * normFactor;
            this.z = z * normFactor;
            return true;
        }

        public static void performCrossProduct(VectorIterator leftIter, VectorIterator rightIter, VectorConsumer consumer) {
            block3: {
                boolean accepted;
                do {
                    if (!leftIter.advance()) {
                        if (rightIter.advance()) {
                            throw new IllegalArgumentException("Cross-product right iterator advanced beyond the left iterator");
                        }
                        break block3;
                    }
                    if (rightIter.advance()) continue;
                    throw new IllegalArgumentException("Cross-product left iterator advanced beyond the right iterator");
                } while (accepted = consumer.acceptVector(leftIter.y() * rightIter.z() - rightIter.y() * leftIter.z(), leftIter.z() * rightIter.x() - rightIter.z() * leftIter.x(), leftIter.x() * rightIter.y() - rightIter.x() * leftIter.y()));
                throw new IllegalArgumentException("Vector consumer could not accept all of the cross-product results");
            }
        }

        public static VectorIterator iterate(final Iterable<Vector> vectors) {
            return new VectorIterator(){
                Iterator<Vector> iter = null;

                @Override
                public boolean advance() {
                    if (this.iter == null) {
                        this.iter = vectors.iterator();
                    }
                    if (this.iter.hasNext()) {
                        ++this.index;
                        this.load(this.iter.next());
                        return true;
                    }
                    return false;
                }
            };
        }

        public static VectorIterator iterateFilled(Vector value) {
            return VectorIterator.iterateFilled(value, -1);
        }

        public static VectorIterator iterateFilled(Vector value, int limit) {
            return new FilledVectorIterator(value.getX(), value.getY(), value.getZ(), limit);
        }

        public static VectorIterator iterateFilled(double x, double y, double z) {
            return VectorIterator.iterateFilled(x, y, z, -1);
        }

        public static VectorIterator iterateFilled(double x, double y, double z, int limit) {
            return new FilledVectorIterator(x, y, z, limit);
        }

        public static VectorIterator join(final VectorIterator first, final VectorIterator second) {
            return new VectorIterator(){
                VectorIterator curr;
                {
                    this.curr = first;
                }

                @Override
                public boolean advance() {
                    VectorIterator curr = this.curr;
                    if (!curr.advance()) {
                        if (curr == second) {
                            return false;
                        }
                        this.curr = curr = second;
                        if (!curr.advance()) {
                            return false;
                        }
                    }
                    this.x = curr.x();
                    this.y = curr.y();
                    this.z = curr.z();
                    ++this.index;
                    return true;
                }
            };
        }

        private static final class FilledVectorIterator
        extends VectorIterator {
            private final int lastIndex;

            public FilledVectorIterator(double x, double y, double z, int limit) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.lastIndex = limit - 1;
            }

            @Override
            public boolean advance() {
                int lastIndex = this.lastIndex;
                if (lastIndex != -2 && this.index >= lastIndex) {
                    return false;
                }
                ++this.index;
                return true;
            }
        }
    }

    @FunctionalInterface
    public static interface VectorConsumer {
        public boolean acceptVector(double var1, double var3, double var5);

        default public boolean acceptVector(Vector value) {
            return this.acceptVector(value.getX(), value.getY(), value.getZ());
        }
    }

    public static final class Projection {
        public final double min;
        public final double max;

        public Projection(double min, double max) {
            this.min = min;
            this.max = max;
        }

        public static boolean isSeparated(Projection proj1, Projection proj2) {
            return proj1.max < proj2.min || proj2.max < proj1.min;
        }

        public String toString() {
            return "Projection{min=" + this.min + ", max=" + this.max + "}";
        }
    }
}

