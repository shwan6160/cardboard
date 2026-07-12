/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.utils.MathUtil;

public class ProtocolMath {
    public static final int serializeRotation(float rotation) {
        return MathUtil.floor(rotation * 256.0f / 360.0f);
    }

    public static final float deserializeRotation(int protRotation) {
        return (float)protRotation * 360.0f / 256.0f;
    }

    public static final float deserializeRotation(byte protRotation) {
        return (float)protRotation * 360.0f / 256.0f;
    }

    public static final int serializeVelocity(double velocity) {
        return (int)(MathUtil.clamp(velocity, 3.9) * 8000.0);
    }

    public static final double deserializeVelocity(int protVelocity) {
        return (double)protVelocity / 8000.0;
    }

    public static final double deserializeVelocity(byte protVelocity) {
        return (double)protVelocity / 8000.0;
    }

    public static final int serializePosition_1_8_8(double position) {
        return MathUtil.floor(position * 32.0);
    }

    public static final double deserializePosition_1_8_8(byte protPosition) {
        return (double)protPosition / 32.0;
    }

    public static final double deserializePosition_1_8_8(int protPosition) {
        return (double)protPosition / 32.0;
    }

    public static final int serializePosition_1_10_2(double position) {
        return MathUtil.floor(position * 4096.0);
    }

    public static final double deserializePosition_1_10_2(short protPosition) {
        return (double)protPosition / 4096.0;
    }

    public static final double deserializePosition_1_10_2(int protPosition) {
        return (double)protPosition / 4096.0;
    }
}

