/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.math;

import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.math.Vector3;
import org.bukkit.util.Vector;

public interface Rotatable {
    public void rotateByQuaternion(double var1, double var3, double var5, double var7);

    default public void rotate(Quaternion quat) {
        this.rotateByQuaternion(quat.getX(), quat.getY(), quat.getZ(), quat.getW());
    }

    default public void rotateX(double angleDegrees) {
        if (angleDegrees != 0.0) {
            double r = 0.5 * Math.toRadians(angleDegrees);
            double fy = Math.cos(r);
            double fz = Math.sin(r);
            this.rotateByQuaternion(fz, 0.0, 0.0, fy);
        }
    }

    default public void rotateXFlip() {
        this.rotateByQuaternion(1.0, 0.0, 0.0, 0.0);
    }

    default public void rotateY(double angleDegrees) {
        if (angleDegrees != 0.0) {
            double r = 0.5 * Math.toRadians(angleDegrees);
            double fx = Math.cos(r);
            double fz = Math.sin(r);
            this.rotateByQuaternion(0.0, fz, 0.0, fx);
        }
    }

    default public void rotateYFlip() {
        this.rotateByQuaternion(0.0, 1.0, 0.0, 0.0);
    }

    default public void rotateZ(double angleDegrees) {
        if (angleDegrees != 0.0) {
            double r = 0.5 * Math.toRadians(angleDegrees);
            double fx = Math.cos(r);
            double fy = Math.sin(r);
            this.rotateByQuaternion(0.0, 0.0, fy, fx);
        }
    }

    default public void rotateZFlip() {
        this.rotateByQuaternion(0.0, 0.0, 1.0, 0.0);
    }

    default public void rotateYawPitchRoll(Vector3 rotation) {
        this.rotateYawPitchRoll(rotation.x, rotation.y, rotation.z);
    }

    default public void rotateYawPitchRoll(Vector rotation) {
        this.rotateYawPitchRoll(rotation.getX(), rotation.getY(), rotation.getZ());
    }

    default public void rotateYawPitchRoll(double pitch, double yaw, double roll) {
        this.rotateY(-yaw);
        this.rotateX(pitch);
        this.rotateZ(roll);
    }

    default public void rotateYawPitchRoll(float pitch, float yaw, float roll) {
        this.rotateY(-yaw);
        this.rotateX(pitch);
        this.rotateZ(roll);
    }

    default public void rotateAxis(Vector axis, double angleDegrees) {
        this.rotateAxis(axis.getX(), axis.getY(), axis.getZ(), angleDegrees);
    }

    default public void rotateAxis(double axisX, double axisY, double axisZ, double angleDegrees) {
        this.rotate(Quaternion.fromAxisAngles(axisX, axisY, axisZ, angleDegrees));
    }
}

