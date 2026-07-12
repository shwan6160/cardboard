/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.OrientedBoundingBox
 */
package com.bergerkiller.bukkit.tc.attachments.surface;

import com.bergerkiller.bukkit.common.math.OrientedBoundingBox;

public interface CollisionSurface {
    public static final CollisionSurface DISABLED = new CollisionSurface(){

        @Override
        public int getUpdateCounter() {
            return 0;
        }

        @Override
        public void setShape(OrientedBoundingBox surface) {
        }

        @Override
        public void remove() {
        }
    };
    public static final int DEFAULT_SHULKER_VIEW_DISTANCE = 8;

    public int getUpdateCounter();

    public void setShape(OrientedBoundingBox var1);

    public void remove();
}

