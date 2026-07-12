/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.math;

import com.bergerkiller.bukkit.common.internal.CommonTrigMath;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Rotatable;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.util.Vector;

public class Quaternion
implements Rotatable,
Cloneable {
    private double x;
    private double y;
    private double z;
    private double w;

    public Quaternion() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.w = 1.0;
    }

    public Quaternion(Quaternion quat) {
        this.x = quat.x;
        this.y = quat.y;
        this.z = quat.z;
        this.w = quat.w;
    }

    public Quaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.normalize();
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public double getW() {
        return this.w;
    }

    public void setIdentity() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.w = 1.0;
    }

    public void setTo(Quaternion q) {
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
        this.w = q.w;
    }

    public double dot(Quaternion q) {
        return this.x * q.x + this.y * q.y + this.z * q.z + this.w * q.w;
    }

    public void transformPoint(Vector point) {
        double px = point.getX();
        double py = point.getY();
        double pz = point.getZ();
        point.setX(px + 2.0 * (px * (-this.y * this.y - this.z * this.z) + py * (this.x * this.y - this.z * this.w) + pz * (this.x * this.z + this.y * this.w)));
        point.setY(py + 2.0 * (px * (this.x * this.y + this.z * this.w) + py * (-this.x * this.x - this.z * this.z) + pz * (this.y * this.z - this.x * this.w)));
        point.setZ(pz + 2.0 * (px * (this.x * this.z - this.y * this.w) + py * (this.y * this.z + this.x * this.w) + pz * (-this.x * this.x - this.y * this.y)));
    }

    public void invTransformPoint(Vector point) {
        double px = point.getX();
        double py = point.getY();
        double pz = point.getZ();
        point.setX(px + 2.0 * (px * (-this.y * this.y - this.z * this.z) + py * (this.x * this.y + this.z * this.w) + pz * (this.x * this.z - this.y * this.w)));
        point.setY(py + 2.0 * (px * (this.x * this.y - this.z * this.w) + py * (-this.x * this.x - this.z * this.z) + pz * (this.y * this.z + this.x * this.w)));
        point.setZ(pz + 2.0 * (px * (this.x * this.z + this.y * this.w) + py * (this.y * this.z - this.x * this.w) + pz * (-this.x * this.x - this.y * this.y)));
    }

    public Vector rightVector() {
        return new Vector(1.0 + 2.0 * (-this.y * this.y - this.z * this.z), 2.0 * (this.x * this.y + this.z * this.w), 2.0 * (this.x * this.z - this.y * this.w));
    }

    public Vector upVector() {
        return new Vector(2.0 * (this.x * this.y - this.z * this.w), 1.0 + 2.0 * (-this.x * this.x - this.z * this.z), 2.0 * (this.y * this.z + this.x * this.w));
    }

    public Vector forwardVector() {
        return new Vector(2.0 * (this.x * this.z + this.y * this.w), 2.0 * (this.y * this.z - this.x * this.w), 1.0 + 2.0 * (-this.x * this.x - this.y * this.y));
    }

    public void divide(Quaternion quat) {
        double x = this.w * -quat.x + this.x * quat.w + this.y * -quat.z - this.z * -quat.y;
        double y = this.w * -quat.y + this.y * quat.w + this.z * -quat.x - this.x * -quat.z;
        double z = this.w * -quat.z + this.z * quat.w + this.x * -quat.y - this.y * -quat.x;
        double w = this.w * quat.w - this.x * -quat.x - this.y * -quat.y - this.z * -quat.z;
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.normalize();
    }

    public void multiply(Quaternion quat) {
        this.rotateByQuaternion(quat.x, quat.y, quat.z, quat.w);
    }

    @Override
    public void rotate(Quaternion quat) {
        this.rotateByQuaternion(quat.getX(), quat.getY(), quat.getZ(), quat.getW());
    }

    @Override
    public void rotateByQuaternion(double qx, double qy, double qz, double qw) {
        double x = this.w * qx + this.x * qw + this.y * qz - this.z * qy;
        double y = this.w * qy + this.y * qw + this.z * qx - this.x * qz;
        double z = this.w * qz + this.z * qw + this.x * qy - this.y * qx;
        double w = this.w * qw - this.x * qx - this.y * qy - this.z * qz;
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.normalize();
    }

    @Override
    public final void rotateAxis(Vector axis, double angleDegrees) {
        Rotatable.super.rotateAxis(axis, angleDegrees);
    }

    @Override
    public final void rotateAxis(double axisX, double axisY, double axisZ, double angleDegrees) {
        Rotatable.super.rotateAxis(axisX, axisY, axisZ, angleDegrees);
    }

    @Override
    public final void rotateYawPitchRoll(Vector3 rotation) {
        Rotatable.super.rotateYawPitchRoll(rotation);
    }

    @Override
    public final void rotateYawPitchRoll(Vector rotation) {
        Rotatable.super.rotateYawPitchRoll(rotation);
    }

    @Override
    public final void rotateYawPitchRoll(double pitch, double yaw, double roll) {
        Rotatable.super.rotateYawPitchRoll(pitch, yaw, roll);
    }

    public final Vector getYawPitchRoll() {
        return Quaternion.getYawPitchRoll(this.x, this.y, this.z, this.w);
    }

    public final double getPitch() {
        return Quaternion.getPitch(this.x, this.y, this.z, this.w);
    }

    public final double getYaw() {
        return Quaternion.getYaw(this.x, this.y, this.z, this.w);
    }

    public final double getRoll() {
        return Quaternion.getRoll(this.x, this.y, this.z, this.w);
    }

    protected static double getYaw(double x, double y, double z, double w) {
        double test = 2.0 * (w * x - y * z);
        if (Math.abs(test) < 0.999999999999999) {
            double yaw = CommonTrigMath.atan2(-2.0 * (w * y + z * x), 1.0 - 2.0 * (x * x + y * y));
            double roll_x = 0.5 - (x * x + z * z);
            if (roll_x <= 0.0 && Math.abs(w * z + x * y) > roll_x) {
                yaw += yaw < 0.0 ? Math.PI : -Math.PI;
            }
            return Math.toDegrees(yaw);
        }
        if (test < 0.0) {
            return Math.toDegrees(-2.0 * CommonTrigMath.atan2(z, w));
        }
        return Math.toDegrees(2.0 * CommonTrigMath.atan2(z, w));
    }

    protected static double getPitch(double x, double y, double z, double w) {
        double test = 2.0 * (w * x - y * z);
        if (Math.abs(test) < 0.999999999999999) {
            double pitch = Math.asin(test);
            double roll_x = 0.5 - (x * x + z * z);
            if (roll_x <= 0.0 && Math.abs(w * z + x * y) > roll_x) {
                pitch += (pitch = -pitch) < 0.0 ? Math.PI : -Math.PI;
            }
            return Math.toDegrees(pitch);
        }
        if (test < 0.0) {
            return -90.0;
        }
        return 90.0;
    }

    protected static double getRoll(double x, double y, double z, double w) {
        double test = 2.0 * (w * x - y * z);
        if (Math.abs(test) < 0.999999999999999) {
            double roll = CommonTrigMath.atan2(2.0 * (w * z + x * y), 1.0 - 2.0 * (x * x + z * z));
            if (Math.abs(roll) > 1.5707963267948966) {
                roll += roll < 0.0 ? Math.PI : -Math.PI;
            }
            return Math.toDegrees(roll);
        }
        return 0.0;
    }

    protected static Vector getYawPitchRoll(double x, double y, double z, double w) {
        double test = 2.0 * (w * x - y * z);
        if (Math.abs(test) < 0.999999999999999) {
            double roll = CommonTrigMath.atan2(2.0 * (w * z + x * y), 1.0 - 2.0 * (x * x + z * z));
            double pitch = Math.asin(test);
            double yaw = CommonTrigMath.atan2(-2.0 * (w * y + z * x), 1.0 - 2.0 * (x * x + y * y));
            if (Math.abs(roll) > 1.5707963267948966) {
                roll += roll < 0.0 ? Math.PI : -Math.PI;
                yaw += yaw < 0.0 ? Math.PI : -Math.PI;
                pitch = -pitch;
                pitch += pitch < 0.0 ? Math.PI : -Math.PI;
            }
            return new Vector(Math.toDegrees(pitch), Math.toDegrees(yaw), Math.toDegrees(roll));
        }
        if (test < 0.0) {
            return new Vector(-90.0, Math.toDegrees(-2.0 * CommonTrigMath.atan2(z, w)), 0.0);
        }
        return new Vector(90.0, Math.toDegrees(2.0 * CommonTrigMath.atan2(z, w)), 0.0);
    }

    @Override
    public final void rotateXFlip() {
        double w;
        double x = this.x;
        double y = this.y;
        double z = this.z;
        this.x = w = this.w;
        this.y = z;
        this.z = -y;
        this.w = -x;
    }

    @Override
    public final void rotateX(double angleDegrees) {
        if (angleDegrees != 0.0) {
            double r = 0.5 * Math.toRadians(angleDegrees);
            this.rotateX_unsafe(Math.cos(r), Math.sin(r));
        }
    }

    public final void rotateX(double y, double z) {
        double r = Quaternion.halfcosatan2(z, y);
        this.rotateX_unsafe(Math.sqrt(0.5 + r), Math.sqrt(0.5 - r));
    }

    private final void rotateX_unsafe(double fy, double fz) {
        double x = this.x * fy + this.w * fz;
        double y = this.y * fy + this.z * fz;
        double z = this.z * fy - this.y * fz;
        double w = this.w * fy - this.x * fz;
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.normalize();
    }

    @Override
    public final void rotateYFlip() {
        double x = this.x;
        double y = this.y;
        double z = this.z;
        double w = this.w;
        this.x = -z;
        this.y = w;
        this.z = x;
        this.w = -y;
    }

    @Override
    public final void rotateY(double angleDegrees) {
        if (angleDegrees != 0.0) {
            double r = 0.5 * Math.toRadians(angleDegrees);
            this.rotateY_unsafe(Math.cos(r), Math.sin(r));
        }
    }

    public final void rotateY(double x, double z) {
        double r = Quaternion.halfcosatan2(z, x);
        this.rotateY_unsafe(Math.sqrt(0.5 + r), Math.sqrt(0.5 - r));
    }

    private final void rotateY_unsafe(double fx, double fz) {
        double x = this.x * fx - this.z * fz;
        double y = this.y * fx + this.w * fz;
        double z = this.z * fx + this.x * fz;
        double w = this.w * fx - this.y * fz;
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.normalize();
    }

    @Override
    public final void rotateZFlip() {
        double x = this.x;
        double y = this.y;
        double z = this.z;
        double w = this.w;
        this.x = y;
        this.y = -x;
        this.z = w;
        this.w = -z;
    }

    @Override
    public final void rotateZ(double angleDegrees) {
        if (angleDegrees != 0.0) {
            double r = 0.5 * Math.toRadians(angleDegrees);
            this.rotateZ_unsafe(Math.cos(r), Math.sin(r));
        }
    }

    public final void rotateZ(double x, double y) {
        double r = Quaternion.halfcosatan2(y, x);
        this.rotateZ_unsafe(Math.sqrt(0.5 + r), Math.sqrt(0.5 - r));
    }

    private final void rotateZ_unsafe(double fx, double fy) {
        double x = this.x * fx + this.y * fy;
        double y = this.y * fx - this.x * fy;
        double z = this.z * fx + this.w * fy;
        double w = this.w * fx - this.z * fy;
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.normalize();
    }

    public Matrix4x4 toMatrix4x4() {
        return new Matrix4x4(1.0 - 2.0 * this.y * this.y - 2.0 * this.z * this.z, 2.0 * this.x * this.y - 2.0 * this.z * this.w, 2.0 * this.x * this.z + 2.0 * this.y * this.w, 0.0, 2.0 * this.x * this.y + 2.0 * this.z * this.w, 1.0 - 2.0 * this.x * this.x - 2.0 * this.z * this.z, 2.0 * this.y * this.z - 2.0 * this.x * this.w, 0.0, 2.0 * this.x * this.z - 2.0 * this.y * this.w, 2.0 * this.y * this.z + 2.0 * this.x * this.w, 1.0 - 2.0 * this.x * this.x - 2.0 * this.y * this.y, 0.0, 0.0, 0.0, 0.0, 1.0);
    }

    public void invert() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    private void normalize() {
        double f = MathUtil.getNormalizationFactor(this.x, this.y, this.z, this.w);
        this.x *= f;
        this.y *= f;
        this.z *= f;
        this.w *= f;
    }

    public Quaternion clone() {
        return new Quaternion(this);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Quaternion) {
            Quaternion q = (Quaternion)o;
            return q.x == this.x && q.y == this.y && q.z == this.z && q.w == this.w;
        }
        return false;
    }

    public String toString() {
        return "{x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", w=" + this.w + "}";
    }

    public static Quaternion identity() {
        return new Quaternion();
    }

    public static Quaternion multiply(Quaternion q1, Quaternion q2) {
        Quaternion result = q1.clone();
        result.multiply(q2);
        return result;
    }

    public static Quaternion divide(Quaternion q1, Quaternion q2) {
        Quaternion result = q1.clone();
        result.divide(q2);
        return result;
    }

    public static Quaternion diff(Quaternion q1, Quaternion q2) {
        Quaternion diff = q1.clone();
        diff.invert();
        diff.multiply(q2);
        return diff;
    }

    public static Quaternion fromAxisAngles(Vector3 axis, double angleDegrees) {
        return Quaternion.fromAxisAngles(axis.x, axis.y, axis.z, angleDegrees);
    }

    public static Quaternion fromAxisAngles(Vector axis, double angleDegrees) {
        return Quaternion.fromAxisAngles(axis.getX(), axis.getY(), axis.getZ(), angleDegrees);
    }

    public static Quaternion fromAxisAngles(double axisX, double axisY, double axisZ, double angleDegrees) {
        double r = 0.5 * Math.toRadians(angleDegrees);
        double f = Math.sin(r);
        return new Quaternion(f * axisX, f * axisY, f * axisZ, Math.cos(r));
    }

    public static Quaternion fromYawPitchRoll(Vector rotation) {
        return Quaternion.fromYawPitchRoll(rotation.getX(), rotation.getY(), rotation.getZ());
    }

    public static Quaternion fromYawPitchRoll(double pitch, double yaw, double roll) {
        Quaternion quat = new Quaternion();
        quat.rotateYawPitchRoll(pitch, yaw, roll);
        return quat;
    }

    public static Quaternion fromToRotation(Vector u, Vector v, Vector d) {
        double dot = u.dot(v);
        Quaternion q = new Quaternion();
        q.x = u.getY() * v.getZ() - v.getY() * u.getZ();
        q.y = u.getZ() * v.getX() - v.getZ() * u.getX();
        q.z = u.getX() * v.getY() - v.getX() * u.getY();
        q.w = dot;
        q.w += Math.sqrt(q.x * q.x + q.y * q.y + q.z * q.z + q.w * q.w);
        q.normalize();
        if (Double.isNaN(q.w)) {
            q.x = d.getX();
            q.y = d.getY();
            q.z = d.getZ();
            q.w = 0.0;
            q.normalize();
        }
        return q;
    }

    public static Quaternion fromToRotation(Vector u, Vector v) {
        double dot = u.dot(v);
        Quaternion q = new Quaternion();
        q.x = u.getY() * v.getZ() - v.getY() * u.getZ();
        q.y = u.getZ() * v.getX() - v.getZ() * u.getX();
        q.z = u.getX() * v.getY() - v.getX() * u.getY();
        q.w = dot;
        q.w += Math.sqrt(q.x * q.x + q.y * q.y + q.z * q.z + q.w * q.w);
        q.normalize();
        if (Double.isNaN(q.w)) {
            if (dot > 0.0) {
                q.setIdentity();
            } else {
                double norm = MathUtil.getNormalizationFactor(u.getZ(), u.getY());
                if (Double.isInfinite(norm)) {
                    norm = MathUtil.getNormalizationFactor(u.getZ(), u.getX());
                    q.x = norm * u.getZ();
                    q.y = 0.0;
                    q.z = norm * -u.getX();
                    q.w = 0.0;
                } else {
                    q.x = 0.0;
                    q.y = norm * -u.getZ();
                    q.z = norm * u.getY();
                    q.w = 0.0;
                }
            }
        }
        return q;
    }

    public static Quaternion fromLookDirection(Vector dir) {
        Quaternion q = new Quaternion(-dir.getY(), dir.getX(), 0.0, dir.getZ() + dir.length());
        if (Double.isNaN(q.w)) {
            q.x = 0.0;
            q.y = 1.0;
            q.z = 0.0;
            q.w = 0.0;
        }
        return q;
    }

    public static Quaternion fromLookDirection(Vector dir, Vector up) {
        Vector D = dir.clone().normalize();
        Vector S = up.clone().crossProduct(dir).normalize();
        Vector U = D.clone().crossProduct(S);
        Quaternion result = Matrix4x4.fromColumns3x3(S, U, D).getRotation();
        if (Double.isNaN(result.x)) {
            return Quaternion.fromLookDirection(dir);
        }
        return result;
    }

    public static Quaternion lerp(Quaternion q0, Quaternion q1, double t0, double t1) {
        return new Quaternion(t0 * q0.x + t1 * q1.x, t0 * q0.y + t1 * q1.y, t0 * q0.z + t1 * q1.z, t0 * q0.w + t1 * q1.w);
    }

    public static Quaternion lerp(Quaternion q0, Quaternion q1, double theta) {
        return Quaternion.lerp(q0, q1, 1.0 - theta, theta);
    }

    public static Quaternion slerp(Quaternion q0, Quaternion q1, double theta) {
        Quaternion qs = q1.clone();
        double dot = q0.dot(q1);
        if (dot < 0.0) {
            dot = -dot;
            qs.x = -qs.x;
            qs.y = -qs.y;
            qs.z = -qs.z;
            qs.w = -qs.w;
        }
        if (dot >= 0.95) {
            return Quaternion.lerp(q0, qs, theta);
        }
        double angle = Math.acos(dot);
        double qd = 1.0 / Math.sin(angle);
        double q0f = qd * Math.sin(angle * (1.0 - theta));
        double qsf = qd * Math.sin(angle * theta);
        return Quaternion.lerp(q0, qs, q0f, qsf);
    }

    public static Quaternion random() {
        return Quaternion.random(ThreadLocalRandom.current());
    }

    public static Quaternion random(Random random) {
        double u1 = random.nextDouble();
        double u2 = random.nextDouble();
        double u3 = random.nextDouble();
        double sqrt1MinusU1 = Math.sqrt(1.0 - u1);
        double sqrtU1 = Math.sqrt(u1);
        double theta1 = Math.PI * 2 * u2;
        double theta2 = Math.PI * 2 * u3;
        double x = Math.sin(theta1) * sqrt1MinusU1;
        double y = Math.cos(theta1) * sqrt1MinusU1;
        double z = Math.sin(theta2) * sqrtU1;
        double w = Math.cos(theta2) * sqrtU1;
        return new Quaternion(x, y, z, w);
    }

    public static Quaternion average(Iterable<Quaternion> values) {
        Iterator<Quaternion> iter = values.iterator();
        if (!iter.hasNext()) {
            return Quaternion.identity();
        }
        Quaternion first = iter.next();
        if (!iter.hasNext()) {
            return first.clone();
        }
        int num_values = 1;
        Quaternion result = first.clone();
        do {
            Quaternion next;
            if (first.dot(next = iter.next()) >= 0.0) {
                result.x += next.x;
                result.y += next.y;
                result.z += next.z;
                result.w += next.w;
            } else {
                result.x -= next.x;
                result.y -= next.y;
                result.z -= next.z;
                result.w -= next.w;
            }
            ++num_values;
        } while (iter.hasNext());
        double fact = 1.0 / (double)num_values;
        result.x *= fact;
        result.y *= fact;
        result.z *= fact;
        result.w *= fact;
        result.normalize();
        return result;
    }

    private static final double halfcosatan2(double y, double x) {
        double tmp = y / x;
        tmp *= tmp;
        return (x < 0.0 ? -0.5 : 0.5) / Math.sqrt(tmp += 1.0);
    }
}

