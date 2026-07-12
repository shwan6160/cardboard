/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Entity
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.internal.CommonTrigMath;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class MathUtil {
    private static final int CHUNK_BITS = 4;
    private static final int CHUNK_VALUES = 16;
    public static final float DEGTORAD = (float)Math.PI / 180;
    public static final float RADTODEG = 57.29578f;
    public static final double HALFROOTOFTWO = 0.707106781;

    public static double lengthSquared(double ... values) {
        double rval = 0.0;
        for (double value : values) {
            rval += value * value;
        }
        return rval;
    }

    public static double length(double ... values) {
        return Math.sqrt(MathUtil.lengthSquared(values));
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return MathUtil.length(x1 - x2, y1 - y2);
    }

    public static double distanceSquared(double x1, double y1, double x2, double y2) {
        return MathUtil.lengthSquared(x1 - x2, y1 - y2);
    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        return MathUtil.length(x1 - x2, y1 - y2, z1 - z2);
    }

    public static double distanceSquared(double x1, double y1, double z1, double x2, double y2, double z2) {
        return MathUtil.lengthSquared(x1 - x2, y1 - y2, z1 - z2);
    }

    public static double getPercentage(int subtotal, int total, int decimals) {
        return MathUtil.round(MathUtil.getPercentage(subtotal, total), decimals);
    }

    public static double getPercentage(int subtotal, int total) {
        return (float)subtotal / (float)total * 100.0f;
    }

    public static int getAngleDifference(int angle1, int angle2) {
        return Math.abs(MathUtil.wrapAngle(angle1 - angle2));
    }

    public static float getAngleDifference(float angle1, float angle2) {
        return Math.abs(MathUtil.wrapAngle(angle1 - angle2));
    }

    public static double getAngleDifference(double angle1, double angle2) {
        return Math.abs(MathUtil.wrapAngle(angle1 - angle2));
    }

    public static int wrapAngle(int angle) {
        int wrappedAngle;
        for (wrappedAngle = angle; wrappedAngle <= -180; wrappedAngle += 360) {
        }
        while (wrappedAngle > 180) {
            wrappedAngle -= 360;
        }
        return wrappedAngle;
    }

    public static float wrapAngle(float angle) {
        float wrappedAngle;
        for (wrappedAngle = angle; wrappedAngle <= -180.0f; wrappedAngle += 360.0f) {
        }
        while (wrappedAngle > 180.0f) {
            wrappedAngle -= 360.0f;
        }
        return wrappedAngle;
    }

    public static double wrapAngle(double angle) {
        double wrappedAngle;
        for (wrappedAngle = angle; wrappedAngle <= -180.0; wrappedAngle += 360.0) {
        }
        while (wrappedAngle > 180.0) {
            wrappedAngle -= 360.0;
        }
        return wrappedAngle;
    }

    public static double normalize(double x, double z, double reqx, double reqz) {
        return Math.sqrt(MathUtil.lengthSquared(reqx, reqz) / MathUtil.lengthSquared(x, z));
    }

    public static float getLookAtYaw(Entity loc, Entity lookat) {
        return MathUtil.getLookAtYaw(loc.getLocation(), lookat.getLocation());
    }

    public static float getLookAtYaw(Block loc, Block lookat) {
        return MathUtil.getLookAtYaw(loc.getLocation(), lookat.getLocation());
    }

    public static float getLookAtYaw(Location loc, Location lookat) {
        return MathUtil.getLookAtYaw(lookat.getX() - loc.getX(), lookat.getZ() - loc.getZ());
    }

    public static float getLookAtYaw(Vector motion) {
        return MathUtil.getLookAtYaw(motion.getX(), motion.getZ());
    }

    public static float getLookAtYaw(double dx, double dz) {
        return MathUtil.atan2(dz, dx) - 180.0f;
    }

    public static float getLookAtPitch(double dX, double dY, double dZ) {
        return MathUtil.getLookAtPitch(dY, MathUtil.length(dX, dZ));
    }

    public static float getLookAtPitch(double dY, double dXZ) {
        return -MathUtil.atan(dY / dXZ);
    }

    public static void nextForward(Vector position, Vector motion) {
        MathUtil.nextForward(position, motion, 1.0E-6);
    }

    public static void nextForward(Vector position, Vector motion, double epsilon) {
        if (motion.getX() > epsilon) {
            position.setX(Math.nextUp(position.getX()));
        } else if (motion.getX() < -epsilon) {
            position.setX(Math.nextDown(position.getX()));
        }
        if (motion.getY() > epsilon) {
            position.setY(Math.nextUp(position.getY()));
        } else if (motion.getY() < -epsilon) {
            position.setY(Math.nextDown(position.getY()));
        }
        if (motion.getZ() > epsilon) {
            position.setZ(Math.nextUp(position.getZ()));
        } else if (motion.getZ() < -epsilon) {
            position.setZ(Math.nextDown(position.getZ()));
        }
    }

    public static float atan(double value) {
        return (float)Math.toDegrees(CommonTrigMath.atan(value));
    }

    public static float atan2(double y, double x) {
        return (float)Math.toDegrees(CommonTrigMath.atan2(y, x));
    }

    public static long longFloor(double value) {
        long l = (long)value;
        return value < (double)l ? l - 1L : l;
    }

    public static int floor(double value) {
        int i = (int)value;
        return value < (double)i ? i - 1 : i;
    }

    public static int floor(float value) {
        int i = (int)value;
        return value < (float)i ? i - 1 : i;
    }

    public static int ceil(double value) {
        int i = (int)value;
        return value > (double)i ? i + 1 : i;
    }

    public static int ceil(float value) {
        int i = (int)value;
        return value > (float)i ? i + 1 : i;
    }

    public static Location move(Location loc, Vector offset) {
        return MathUtil.move(loc, offset.getX(), offset.getY(), offset.getZ());
    }

    public static Location move(Location loc, double dx, double dy, double dz) {
        Vector off = MathUtil.rotate(loc.getYaw(), loc.getPitch(), dx, dy, dz);
        double x = loc.getX() + off.getX();
        double y = loc.getY() + off.getY();
        double z = loc.getZ() + off.getZ();
        return new Location(loc.getWorld(), x, y, z, loc.getYaw(), loc.getPitch());
    }

    public static Vector rotate(float yaw, float pitch, Vector vector) {
        return MathUtil.rotate(yaw, pitch, vector.getX(), vector.getY(), vector.getZ());
    }

    public static Vector rotate(float yaw, float pitch, double x, double y, double z) {
        double angle = Math.toRadians(yaw);
        double sinyaw = Math.sin(angle);
        double cosyaw = Math.cos(angle);
        angle = Math.toRadians(pitch);
        double sinpitch = Math.sin(angle);
        double cospitch = Math.cos(angle);
        Vector vector = new Vector();
        vector.setX(x * sinyaw - y * cosyaw * sinpitch - z * cosyaw * cospitch);
        vector.setY(y * cospitch - z * sinpitch);
        vector.setZ(-(x * cosyaw) - y * sinyaw * sinpitch - z * sinyaw * cospitch);
        return vector;
    }

    public static int floorMod(int x, int y) {
        return Math.floorMod(x, y);
    }

    public static long floorMod(long x, long y) {
        return Math.floorMod(x, y);
    }

    public static int floorDiv(int x, int y) {
        return Math.floorDiv(x, y);
    }

    public static long floorDiv(long x, long y) {
        return Math.floorDiv(x, y);
    }

    public static double round(double value, int decimals) {
        double p = Math.pow(10.0, decimals);
        return (double)Math.round(value * p) / p;
    }

    public static double fixNaN(double value) {
        return MathUtil.fixNaN(value, 0.0);
    }

    public static float fixNaN(float value) {
        return MathUtil.fixNaN(value, 0.0f);
    }

    public static double fixNaN(double value, double def) {
        return Double.isNaN(value) ? def : value;
    }

    public static float fixNaN(float value, float def) {
        return Float.isNaN(value) ? def : value;
    }

    public static int toChunk(double loc) {
        return MathUtil.floor(loc / 16.0);
    }

    public static int toChunk(int loc) {
        return loc >> 4;
    }

    public static double useOld(double oldvalue, double newvalue, double peruseold) {
        return oldvalue + peruseold * (newvalue - oldvalue);
    }

    public static double lerp(double d1, double d2, double stage) {
        if (Double.isNaN(stage) || stage > 1.0) {
            return d2;
        }
        if (stage < 0.0) {
            return d1;
        }
        return d1 * (1.0 - stage) + d2 * stage;
    }

    public static Vector lerp(Vector vec1, Vector vec2, double stage) {
        Vector newvec = new Vector();
        newvec.setX(MathUtil.lerp(vec1.getX(), vec2.getX(), stage));
        newvec.setY(MathUtil.lerp(vec1.getY(), vec2.getY(), stage));
        newvec.setZ(MathUtil.lerp(vec1.getZ(), vec2.getZ(), stage));
        return newvec;
    }

    public static Location lerp(Location loc1, Location loc2, double stage) {
        Location newloc = new Location(loc1.getWorld(), 0.0, 0.0, 0.0);
        newloc.setX(MathUtil.lerp(loc1.getX(), loc2.getX(), stage));
        newloc.setY(MathUtil.lerp(loc1.getY(), loc2.getY(), stage));
        newloc.setZ(MathUtil.lerp(loc1.getZ(), loc2.getZ(), stage));
        newloc.setYaw((float)MathUtil.lerp(loc1.getYaw(), loc2.getYaw(), stage));
        newloc.setPitch((float)MathUtil.lerp(loc1.getPitch(), loc2.getPitch(), stage));
        return newloc;
    }

    public static boolean isInverted(double value1, double value2) {
        return value1 > 0.0 && value2 < 0.0 || value1 < 0.0 && value2 > 0.0;
    }

    public static Vector getDirection(float yaw, float pitch) {
        Vector vector = new Vector();
        double rotX = Math.toRadians(yaw);
        double rotY = Math.toRadians(pitch);
        vector.setY(-Math.sin(rotY));
        double h = Math.cos(rotY);
        vector.setX(-h * Math.sin(rotX));
        vector.setZ(h * Math.cos(rotX));
        return vector;
    }

    public static double clamp(double value, double limit) {
        return MathUtil.clamp(value, -limit, limit);
    }

    public static double clamp(double value, double min, double max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static float clamp(float value, float limit) {
        return MathUtil.clamp(value, -limit, limit);
    }

    public static float clamp(float value, float min, float max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static int clamp(int value, int limit) {
        return MathUtil.clamp(value, -limit, limit);
    }

    public static int clamp(int value, int min, int max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static long clamp(long value, long limit) {
        return MathUtil.clamp(value, -limit, limit);
    }

    public static long clamp(long value, long min, long max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static int invert(int value, boolean negative) {
        return negative ? -value : value;
    }

    public static float invert(float value, boolean negative) {
        return negative ? -value : value;
    }

    public static double invert(double value, boolean negative) {
        return negative ? -value : value;
    }

    public static long toLong(int msw, int lsw) {
        return ((long)msw << 32) + (long)lsw - Integer.MIN_VALUE;
    }

    public static long longHashToLong(int msw, int lsw) {
        return ((long)msw << 32) + (long)lsw - Integer.MIN_VALUE;
    }

    public static int longHashMsw(long key) {
        return (int)(key >> 32);
    }

    public static int longHashLsw(long key) {
        return (int)(key & 0xFFFFFFFFFFFFFFFFL) + Integer.MIN_VALUE;
    }

    public static long longHashSumW(long keyA, long keyB) {
        long sum_msw = (keyA & 0xFFFFFFFF00000000L) + (keyB & 0xFFFFFFFF00000000L);
        long sum_lsw = (keyA & 0xFFFFFFFFFFFFFFFFL) + (keyB & 0xFFFFFFFFFFFFFFFFL);
        return sum_msw + (long)((int)sum_lsw) - Integer.MIN_VALUE;
    }

    public static long longHashAdd(long key_a, long key_b) {
        return key_a + key_b + Integer.MIN_VALUE;
    }

    public static void setVectorLength(Vector vector, double length) {
        MathUtil.setVectorLengthSquared(vector, Math.signum(length) * length * length);
    }

    public static void setVectorLengthSquared(Vector vector, double lengthsquared) {
        double vlength = vector.lengthSquared();
        if (Math.abs(vlength) > 1.0E-4) {
            if (lengthsquared < 0.0) {
                vector.multiply(-Math.sqrt(-lengthsquared / vlength));
            } else {
                vector.multiply(Math.sqrt(lengthsquared / vlength));
            }
        }
    }

    public static boolean isHeadingTo(BlockFace direction, Vector velocity) {
        return MathUtil.isHeadingTo(FaceUtil.faceToVector(direction), velocity);
    }

    public static boolean isHeadingTo(Location from, Location to, Vector velocity) {
        return MathUtil.isHeadingTo(new Vector(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ()), velocity);
    }

    public static boolean isHeadingTo(Vector offset, Vector velocity) {
        double dbefore = offset.lengthSquared();
        if (dbefore < 1.0E-4) {
            return true;
        }
        Vector clonedVelocity = velocity.clone();
        MathUtil.setVectorLengthSquared(clonedVelocity, dbefore);
        return dbefore > clonedVelocity.subtract(offset).lengthSquared();
    }

    public static double getNormalizationFactor(Vector v) {
        return MathUtil.getNormalizationFactorLS(v.lengthSquared());
    }

    public static double getNormalizationFactor(double x, double y, double z, double w) {
        return MathUtil.getNormalizationFactorLS(x * x + y * y + z * z + w * w);
    }

    public static double getNormalizationFactor(double x, double y, double z) {
        return MathUtil.getNormalizationFactorLS(x * x + y * y + z * z);
    }

    public static double getNormalizationFactor(double x, double y) {
        return MathUtil.getNormalizationFactorLS(x * x + y * y);
    }

    public static double getNormalizationFactorLS(double lengthSquared) {
        if (Math.abs(1.0 - lengthSquared) < 2.107342E-8) {
            return 2.0 / (1.0 + lengthSquared);
        }
        return 1.0 / Math.sqrt(lengthSquared);
    }

    public static double getAngleDifference(Vector v0, Vector v1) {
        double dot = v0.dot(v1);
        dot *= MathUtil.getNormalizationFactor(v0);
        return Math.toDegrees(Math.acos(dot *= MathUtil.getNormalizationFactor(v1)));
    }

    public static Vector setVector(Vector vector, Vector value) {
        return vector.copy(value);
    }

    public static Vector setVector(Vector vector, double x, double y, double z) {
        vector.setX(x);
        vector.setY(y);
        vector.setZ(z);
        return vector;
    }

    public static Vector addToVector(Vector vector, double ax, double ay, double az) {
        vector.setX(vector.getX() + ax);
        vector.setY(vector.getY() + ay);
        vector.setZ(vector.getZ() + az);
        return vector;
    }

    public static Vector subtractFromVector(Vector vector, double sx, double sy, double sz) {
        vector.setX(vector.getX() - sx);
        vector.setY(vector.getY() - sy);
        vector.setZ(vector.getZ() - sz);
        return vector;
    }

    public static Vector multiplyVector(Vector vector, double mx, double my, double mz) {
        vector.setX(vector.getX() * mx);
        vector.setY(vector.getY() * my);
        vector.setZ(vector.getZ() * mz);
        return vector;
    }

    public static Vector divideVector(Vector vector, double mx, double my, double mz) {
        vector.setX(vector.getX() / mx);
        vector.setY(vector.getY() / my);
        vector.setZ(vector.getZ() / mz);
        return vector;
    }

    public static double max(double x, double y, double z) {
        return x > z ? Math.max(x, y) : Math.max(y, z);
    }

    public static double min(double x, double y, double z) {
        return x < z ? Math.min(x, y) : Math.min(y, z);
    }

    public static boolean vectorEquals(Vector v0, Vector v1) {
        return v0.getX() == v1.getX() && v0.getY() == v1.getY() && v0.getZ() == v1.getZ();
    }

    public static boolean vectorEquals(Vector3 v0, Vector v1) {
        return v0.equals(v1);
    }

    public static boolean vectorEquals(Vector v0, Vector3 v1) {
        return v1.equals(v0);
    }
}

