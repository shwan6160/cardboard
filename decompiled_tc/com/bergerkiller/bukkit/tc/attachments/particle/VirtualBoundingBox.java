/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.internal.CommonCapabilities
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.OrientedBoundingBox
 *  org.bukkit.Material
 */
package com.bergerkiller.bukkit.tc.attachments.particle;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.OrientedBoundingBox;
import com.bergerkiller.bukkit.tc.attachments.VirtualSpawnableObject;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualFishingBoundingBox;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualFishingBoundingPlane;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualHybridBoundingBox;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualHybridBoundingPlane;
import org.bukkit.Material;

public abstract class VirtualBoundingBox
extends VirtualSpawnableObject {
    protected VirtualBoundingBox(AttachmentManager manager) {
        super(manager);
    }

    public abstract void update(OrientedBoundingBox var1);

    @Override
    @Deprecated
    public final void updatePosition(Matrix4x4 transform) {
        throw new UnsupportedOperationException("Must specify a bounding box");
    }

    public static VirtualBoundingBox create(AttachmentManager manager) {
        if (CommonCapabilities.HAS_DISPLAY_ENTITY) {
            return new VirtualHybridBoundingBox(manager);
        }
        return new VirtualFishingBoundingBox(manager);
    }

    public static VirtualBoundingBox createPlane(AttachmentManager manager) {
        return VirtualBoundingBox.createPlane(manager, null);
    }

    public static VirtualBoundingBox createPlane(AttachmentManager manager, Material solidFloorMaterial) {
        if (CommonCapabilities.HAS_DISPLAY_ENTITY) {
            return new VirtualHybridBoundingPlane(manager, solidFloorMaterial);
        }
        return new VirtualFishingBoundingPlane(manager);
    }
}

