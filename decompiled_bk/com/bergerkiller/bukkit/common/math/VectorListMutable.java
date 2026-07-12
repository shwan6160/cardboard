/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.math;

import com.bergerkiller.bukkit.common.math.Rotatable;
import com.bergerkiller.bukkit.common.math.Translatable;
import com.bergerkiller.bukkit.common.math.VectorList;
import com.bergerkiller.bukkit.common.math.VectorListMutableBasicImpl;
import java.util.Collection;
import java.util.Iterator;
import org.bukkit.util.Vector;

public interface VectorListMutable
extends VectorList,
Rotatable,
Translatable {
    public static VectorListMutable create(int size) {
        return new VectorListMutableBasicImpl(size);
    }

    public static VectorListMutable createBoxVertices(Vector size) {
        return VectorListMutable.createBoxVertices(size.getX(), size.getY(), size.getZ());
    }

    public static VectorListMutable createBoxVertices(double sx, double sy, double sz) {
        return VectorListMutable.createBoxVerticesWithHalfSize(0.5 * sx, 0.5 * sy, 0.5 * sz);
    }

    public static VectorListMutable createBoxVerticesWithHalfSize(Vector halfSize) {
        return VectorListMutable.createBoxVerticesWithHalfSize(halfSize.getX(), halfSize.getY(), halfSize.getZ());
    }

    public static VectorListMutable createBoxVerticesWithHalfSize(double hsx, double hsy, double hsz) {
        VectorListMutable list = VectorListMutable.create(8);
        for (int n = 0; n < 8; ++n) {
            list.set(n, (n & 4) != 0 ? hsx : -hsx, (n & 2) != 0 ? hsy : -hsy, (n & 1) != 0 ? hsz : -hsz);
        }
        return list;
    }

    public static VectorListMutable copyOf(VectorList vectorValues) {
        return new VectorListMutableBasicImpl(vectorValues);
    }

    public static VectorListMutable copyOf(Collection<Vector> vectorValues) {
        return VectorListMutable.createWith(vectorValues.size(), VectorList.VectorIterator.iterate(vectorValues));
    }

    public static VectorListMutable createWith(int size, VectorList.VectorIterator iterator) {
        return new VectorListMutableBasicImpl(size, iterator);
    }

    public void set(int var1, double var2, double var4, double var6);

    @Override
    public MutableVectorIterator vectorIterator(int var1, int var2);

    default public void set(int index, Vector value) {
        this.set(index, value.getX(), value.getY(), value.getZ());
    }

    @Override
    default public MutableVectorIterator vectorIterator() {
        return this.vectorIterator(0, this.size());
    }

    default public VectorListMutable copy() {
        return VectorListMutable.copyOf(this);
    }

    default public VectorList immutable() {
        return VectorList.copyOf(this);
    }

    @Override
    default public void rotateByQuaternion(double qx, double qy, double qz, double qw) {
        MutableVectorIterator iter = this.vectorIterator();
        while (iter.advance()) {
            iter.rotateByQuaternion(qx, qy, qz, qw);
        }
    }

    @Override
    default public void translate(double dx, double dy, double dz) {
        MutableVectorIterator iter = this.vectorIterator();
        while (iter.advance()) {
            iter.translate(dx, dy, dz);
        }
    }

    public static abstract class MutableVectorIterator
    extends VectorList.VectorIterator
    implements Rotatable,
    Translatable,
    VectorList.VectorConsumer {
        public abstract void set(double var1, double var3, double var5);

        @Override
        public boolean acceptVector(double x, double y, double z) {
            if (this.advance()) {
                this.set(x, y, z);
                return true;
            }
            return false;
        }

        @Override
        public boolean acceptVector(Vector value) {
            if (this.advance()) {
                this.set(value);
                return true;
            }
            return false;
        }

        public void multiply(double mx, double my, double mz) {
            this.set(this.x() * mx, this.y() * my, this.z() * mz);
        }

        public void add(double dx, double dy, double dz) {
            this.set(this.x() + dx, this.y() + dy, this.z() + dz);
        }

        public void set(Vector value) {
            this.set(value.getX(), value.getY(), value.getZ());
        }

        @Override
        public void rotateByQuaternion(double qx, double qy, double qz, double qw) {
            double px = this.x();
            double py = this.y();
            double pz = this.z();
            this.set(px + 2.0 * (px * (-qy * qy - qz * qz) + py * (qx * qy - qz * qw) + pz * (qx * qz + qy * qw)), py + 2.0 * (px * (qx * qy + qz * qw) + py * (-qx * qx - qz * qz) + pz * (qy * qz - qx * qw)), pz + 2.0 * (px * (qx * qz - qy * qw) + py * (qy * qz + qx * qw) + pz * (-qx * qx - qy * qy)));
        }

        @Override
        public void translate(double dx, double dy, double dz) {
            this.set(this.x() + dx, this.y() + dy, this.z() + dz);
        }

        public static MutableVectorIterator iterate(final Iterable<Vector> vectors) {
            return new MutableVectorIterator(){
                Iterator<Vector> iter = null;
                Vector lastValue = null;

                @Override
                public boolean advance() {
                    if (this.iter == null) {
                        this.iter = vectors.iterator();
                    }
                    if (this.iter.hasNext()) {
                        ++this.index;
                        this.lastValue = this.iter.next();
                        this.load(this.lastValue);
                        return true;
                    }
                    return false;
                }

                @Override
                public void set(double x, double y, double z) {
                    this.lastValue.setX(x);
                    this.lastValue.setY(y);
                    this.lastValue.setZ(z);
                    this.x = x;
                    this.y = y;
                    this.z = z;
                }
            };
        }

        public static MutableVectorIterator join(final MutableVectorIterator first, final MutableVectorIterator second) {
            return new MutableVectorIterator(){
                MutableVectorIterator curr;
                {
                    this.curr = first;
                }

                @Override
                public void set(double x, double y, double z) {
                    this.curr.set(x, y, z);
                }

                @Override
                public boolean advance() {
                    MutableVectorIterator curr = this.curr;
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
    }
}

