/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.math;

import com.bergerkiller.bukkit.common.math.Vector3;
import org.bukkit.util.Vector;

public interface Translatable {
    public void translate(double var1, double var3, double var5);

    default public void translate(Vector3 delta) {
        this.translate(delta.x, delta.y, delta.z);
    }

    default public void translate(Vector delta) {
        this.translate(delta.getX(), delta.getY(), delta.getZ());
    }
}

