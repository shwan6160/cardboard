/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.math;

import com.bergerkiller.bukkit.common.math.Vector2;
import org.bukkit.util.Vector;

public class Vector3 {
    public double x;
    public double y;
    public double z;

    public Vector3() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }

    public Vector3(Vector vector) {
        this(vector.getX(), vector.getY(), vector.getZ());
    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector2 getXY() {
        return new Vector2(this.x, this.y);
    }

    public Vector2 getXZ() {
        return new Vector2(this.x, this.z);
    }

    public Vector3 negate() {
        return new Vector3(-this.x, -this.y, -this.z);
    }

    public Vector3 normalize() {
        double len = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        return new Vector3(this.x / len, this.y / len, this.z / len);
    }

    public double distanceSquared(Vector3 v) {
        double dx = this.x - v.x;
        double dy = this.y - v.y;
        double dz = this.z - v.z;
        return dx * dx + dy * dy + dz * dz;
    }

    public Vector3 clone() {
        return new Vector3(this.x, this.y, this.z);
    }

    public String toString() {
        return "{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
    }

    public boolean equals(Vector3 p) {
        return p.x == this.x && p.y == this.y && p.z == this.z;
    }

    public boolean equals(Vector v) {
        return v.getX() == this.x && v.getY() == this.y && v.getZ() == this.z;
    }

    public static Vector3 cross(Vector3 v1, Vector3 v2) {
        return new Vector3(v1.y * v2.z - v1.z * v2.y, v2.x * v1.z - v2.z * v1.x, v1.x * v2.y - v1.y * v2.x);
    }

    public static Vector3 subtract(Vector3 v1, Vector3 v2) {
        return new Vector3(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }

    public static Vector3 add(Vector3 v1, Vector3 v2) {
        return new Vector3(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
    }

    public static Vector3 average(Vector3 v1, Vector3 v2) {
        return new Vector3((v1.x + v2.x) / 2.0, (v1.y + v2.y) / 2.0, (v1.z + v2.z) / 2.0);
    }

    public static double dot(Vector3 v1, Vector3 v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }
}

