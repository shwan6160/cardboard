/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.properties.standard.type;

import org.bukkit.util.Vector;

public final class ExitOffset {
    public static final ExitOffset DEFAULT = new ExitOffset(false, 0.0, 0.0, 0.0, Float.NaN, Float.NaN);
    private final boolean absolute;
    private final double rx;
    private final double ry;
    private final double rz;
    private final float yaw;
    private final float pitch;

    private ExitOffset(boolean absolute, double rx, double ry, double rz, float yaw, float pitch) {
        this.absolute = absolute;
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public boolean isAbsolute() {
        return this.absolute;
    }

    public Vector getPosition() {
        return new Vector(this.rx, this.ry, this.rz);
    }

    public double getX() {
        return this.rx;
    }

    public double getY() {
        return this.ry;
    }

    public double getZ() {
        return this.rz;
    }

    public float getYaw() {
        return this.yaw;
    }

    @Deprecated
    public Vector getRelativePosition() {
        return this.getPosition();
    }

    @Deprecated
    public double getRelativeX() {
        return this.rx;
    }

    @Deprecated
    public double getRelativeY() {
        return this.ry;
    }

    @Deprecated
    public double getRelativeZ() {
        return this.rz;
    }

    public float getPitch() {
        return this.pitch;
    }

    public boolean hasLockedYaw() {
        return !Float.isNaN(this.yaw);
    }

    public boolean hasLockedPitch() {
        return !Float.isNaN(this.pitch);
    }

    public int hashCode() {
        return Double.hashCode(this.rx) ^ Double.hashCode(this.rz);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ExitOffset) {
            ExitOffset other = (ExitOffset)o;
            return this.absolute == other.absolute && this.rx == other.rx && this.ry == other.ry && this.rz == other.rz && (this.hasLockedYaw() ? this.yaw == other.yaw : !other.hasLockedYaw()) && (this.hasLockedPitch() ? this.pitch == other.pitch : !other.hasLockedPitch());
        }
        return true;
    }

    public String toString() {
        if (this.absolute) {
            return "ExitLocation{x=" + this.rx + ", y=" + this.ry + ", z=" + this.rz + ", yaw=" + this.yaw + ", pitch=" + this.pitch + "}";
        }
        return "ExitOffset{dx=" + this.rx + ", dy=" + this.ry + ", dz=" + this.rz + ", yaw=" + this.yaw + ", pitch=" + this.pitch + "}";
    }

    public static ExitOffset createAbsolute(Vector absolutePosition, float yaw, float pitch) {
        return ExitOffset.createAbsolute(absolutePosition.getX(), absolutePosition.getY(), absolutePosition.getZ(), yaw, pitch);
    }

    public static ExitOffset createAbsolute(double posX, double posY, double posZ, float yaw, float pitch) {
        return new ExitOffset(true, posX, posY, posZ, yaw, pitch);
    }

    public static ExitOffset create(boolean positionIsAbsolute, Vector position, float yaw, float pitch) {
        return new ExitOffset(positionIsAbsolute, position.getX(), position.getY(), position.getZ(), yaw, pitch);
    }

    public static ExitOffset create(Vector relativePosition, float yaw, float pitch) {
        return ExitOffset.create(relativePosition.getX(), relativePosition.getY(), relativePosition.getZ(), yaw, pitch);
    }

    public static ExitOffset create(double rx, double ry, double rz, float yaw, float pitch) {
        return new ExitOffset(false, rx, ry, rz, yaw, pitch);
    }
}

