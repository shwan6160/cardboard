/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.controller;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public interface EntityPositionApplier {
    public void setPosition(double var1, double var3, double var5);

    public Vector getPosition();

    public void setBodyYaw(float var1);

    public void setHeadYaw(float var1);

    public void setHeadPitch(float var1);

    public float getBodyYaw();

    public float getHeadYaw();

    public float getHeadPitch();

    default public void setPosition(Location position) {
        this.setPosition(position.getX(), position.getY(), position.getZ());
    }

    default public void setPosition(Vector position) {
        this.setPosition(position.getX(), position.getY(), position.getZ());
    }
}

