/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.attachments.surface;

public interface CollisionFloorTileShape
extends Comparable<CollisionFloorTileShape> {
    public static final double DIFF_THRESHOLD = 0.05;

    public double getMinY();

    public double getMaxY();

    public ComplexTile toComplexTile();

    public int shulkerCount();

    public void forEachShulker(int var1, int var2, ShulkerPositionConsumer var3);

    public CollisionFloorTileShape mergeWith(CollisionFloorTileShape var1);

    @Override
    default public int compareTo(CollisionFloorTileShape other) {
        return -Double.compare(this.getMaxY(), other.getMaxY());
    }

    @FunctionalInterface
    public static interface ShulkerPositionConsumer {
        public void accept(double var1, double var3, double var5);
    }

    public static enum AlignedAxis {
        X(0.25, 0.0),
        Z(0.0, 0.25);

        private final double dx;
        private final double dz;

        private AlignedAxis(double dx, double dz) {
            this.dx = dx;
            this.dz = dz;
        }

        public double getDx() {
            return this.dx;
        }

        public double getDz() {
            return this.dz;
        }
    }

    public static class ComplexTile
    implements CollisionFloorTileShape {
        public final double y_xn_zn;
        public final double y_xn_zp;
        public final double y_xp_zn;
        public final double y_xp_zp;
        private final double y_min;
        private final double y_max;

        public ComplexTile(double y_xn_zn, double y_xn_zp, double y_xp_zn, double y_xp_zp) {
            this.y_xn_zn = y_xn_zn;
            this.y_xn_zp = y_xn_zp;
            this.y_xp_zn = y_xp_zn;
            this.y_xp_zp = y_xp_zp;
            this.y_min = Math.min(Math.min(y_xn_zn, y_xn_zp), Math.min(y_xp_zn, y_xp_zp));
            this.y_max = Math.max(Math.max(y_xn_zn, y_xn_zp), Math.max(y_xp_zn, y_xp_zp));
        }

        @Override
        public double getMinY() {
            return this.y_min;
        }

        @Override
        public double getMaxY() {
            return this.y_max;
        }

        @Override
        public ComplexTile toComplexTile() {
            return this;
        }

        @Override
        public int shulkerCount() {
            return 4;
        }

        @Override
        public void forEachShulker(int x, int z, ShulkerPositionConsumer consumer) {
            consumer.accept((double)x + 0.5 - 0.25, this.y_xn_zn, (double)z + 0.5 - 0.25);
            consumer.accept((double)x + 0.5 - 0.25, this.y_xn_zp, (double)z + 0.5 + 0.25);
            consumer.accept((double)x + 0.5 + 0.25, this.y_xp_zn, (double)z + 0.5 - 0.25);
            consumer.accept((double)x + 0.5 + 0.25, this.y_xp_zp, (double)z + 0.5 + 0.25);
        }

        @Override
        public CollisionFloorTileShape mergeWith(CollisionFloorTileShape other) {
            return this.mergeWith(other.toComplexTile());
        }

        public CollisionFloorTileShape mergeWith(ComplexTile complex) {
            boolean zp_same;
            double new_y_xn_zn = Math.max(this.y_xn_zn, complex.y_xn_zn);
            double new_y_xn_zp = Math.max(this.y_xn_zp, complex.y_xn_zp);
            double new_y_xp_zn = Math.max(this.y_xp_zn, complex.y_xp_zn);
            double new_y_xp_zp = Math.max(this.y_xp_zp, complex.y_xp_zp);
            boolean xn_same = Math.abs(new_y_xn_zn - new_y_xn_zp) < 0.05;
            boolean xp_same = Math.abs(new_y_xp_zn - new_y_xp_zp) < 0.05;
            boolean zn_same = Math.abs(new_y_xn_zn - new_y_xp_zn) < 0.05;
            boolean bl = zp_same = Math.abs(new_y_xn_zp - new_y_xp_zp) < 0.05;
            if (xn_same && xp_same) {
                double yn = Math.max(new_y_xn_zn, new_y_xn_zp);
                double yp = Math.max(new_y_xp_zn, new_y_xp_zp);
                if (zn_same && zp_same) {
                    return new Level(Math.max(yn, yp));
                }
                return new AlignedSlope(AlignedAxis.X, yn, yp);
            }
            if (zn_same && zp_same) {
                return new AlignedSlope(AlignedAxis.Z, Math.max(new_y_xn_zn, new_y_xp_zn), Math.max(new_y_xn_zp, new_y_xp_zp));
            }
            return new ComplexTile(new_y_xn_zn, new_y_xn_zp, new_y_xp_zn, new_y_xp_zp);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof ComplexTile) {
                ComplexTile complex = (ComplexTile)o;
                return this.y_xn_zn == complex.y_xn_zn && this.y_xn_zp == complex.y_xn_zp && this.y_xp_zn == complex.y_xp_zn && this.y_xp_zp == complex.y_xp_zp;
            }
            return false;
        }
    }

    public static class AlignedSlope
    implements CollisionFloorTileShape {
        public final AlignedAxis axis;
        public final double yp;
        public final double yn;

        public AlignedSlope(AlignedAxis axis, double yp, double yn) {
            this.axis = axis;
            this.yp = yp;
            this.yn = yn;
        }

        @Override
        public double getMinY() {
            return Math.min(this.yp, this.yn);
        }

        @Override
        public double getMaxY() {
            return Math.max(this.yn, this.yp);
        }

        @Override
        public ComplexTile toComplexTile() {
            return this.axis == AlignedAxis.X ? new ComplexTile(this.yn, this.yn, this.yp, this.yp) : new ComplexTile(this.yn, this.yp, this.yn, this.yp);
        }

        @Override
        public int shulkerCount() {
            return 2;
        }

        @Override
        public void forEachShulker(int x, int z, ShulkerPositionConsumer consumer) {
            consumer.accept((double)x + 0.5 - this.axis.getDx(), this.yn, (double)z + 0.5 - this.axis.getDz());
            consumer.accept((double)x + 0.5 + this.axis.getDx(), this.yp, (double)z + 0.5 + this.axis.getDz());
        }

        @Override
        public CollisionFloorTileShape mergeWith(CollisionFloorTileShape other) {
            if (other instanceof Level) {
                double new_yn;
                Level level = (Level)other;
                double new_yp = Math.max(this.yp, level.y);
                if (Math.abs(new_yp - (new_yn = Math.max(this.yn, level.y))) < 0.05) {
                    return new Level(Math.max(new_yp, new_yn));
                }
                return new AlignedSlope(this.axis, new_yp, new_yn);
            }
            if (other instanceof AlignedSlope) {
                AlignedSlope slope = (AlignedSlope)other;
                if (this.axis == slope.axis) {
                    double new_yn;
                    double new_yp = Math.max(this.yp, slope.yp);
                    if (Math.abs(new_yp - (new_yn = Math.max(this.yn, slope.yn))) < 0.05) {
                        return new Level(Math.max(new_yp, new_yn));
                    }
                    return new AlignedSlope(this.axis, new_yp, new_yn);
                }
            }
            return this.toComplexTile().mergeWith(other.toComplexTile());
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof AlignedSlope) {
                AlignedSlope slope = (AlignedSlope)o;
                return this.axis == slope.axis && this.yn == slope.yn && this.yp == slope.yp;
            }
            return false;
        }
    }

    public static class Level
    implements CollisionFloorTileShape {
        public final double y;

        public Level(double y) {
            this.y = y;
        }

        @Override
        public double getMinY() {
            return this.y;
        }

        @Override
        public double getMaxY() {
            return this.y;
        }

        @Override
        public ComplexTile toComplexTile() {
            return new ComplexTile(this.y, this.y, this.y, this.y);
        }

        @Override
        public int shulkerCount() {
            return 1;
        }

        @Override
        public void forEachShulker(int x, int z, ShulkerPositionConsumer consumer) {
            consumer.accept((double)x + 0.5, this.y, (double)z + 0.5);
        }

        @Override
        public CollisionFloorTileShape mergeWith(CollisionFloorTileShape other) {
            if (other instanceof Level) {
                return ((Level)other).y > this.y ? other : this;
            }
            return other.mergeWith(this);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Level) {
                return ((Level)o).y == this.y;
            }
            return false;
        }
    }
}

