/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.OrientedBoundingBox
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.particle;

import com.bergerkiller.bukkit.common.math.OrientedBoundingBox;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualBoundingBox;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualDisplayBoundingPlane;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualFishingBoundingPlane;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class VirtualHybridBoundingPlane
extends VirtualBoundingBox {
    private final Material solidFloorMaterial;
    private OrientedBoundingBox lastBB;
    private VirtualFishingBoundingPlane fishPlane;
    private VirtualDisplayBoundingPlane displayPlane;

    public VirtualHybridBoundingPlane(AttachmentManager manager) {
        this(manager, null);
    }

    public VirtualHybridBoundingPlane(AttachmentManager manager, Material solidFloorMaterial) {
        super(manager);
        this.solidFloorMaterial = solidFloorMaterial;
    }

    @Override
    public void update(OrientedBoundingBox boundingBox) {
        this.lastBB = boundingBox;
        if (this.displayPlane != null) {
            this.displayPlane.update(boundingBox);
        }
        if (this.fishPlane != null) {
            this.fishPlane.update(boundingBox);
        }
    }

    @Override
    protected void applyGlowing(ChatColor color) {
        if (this.displayPlane != null) {
            this.displayPlane.setGlowColor(color);
        }
        if (this.fishPlane != null) {
            this.fishPlane.setGlowColor(color);
        }
    }

    @Override
    protected void sendSpawnPackets(AttachmentViewer viewer, Vector motion) {
        if (viewer.supportsDisplayEntities()) {
            if (this.displayPlane == null) {
                this.displayPlane = new VirtualDisplayBoundingPlane(this.manager, this.solidFloorMaterial);
                this.displayPlane.setGlowColor(this.getGlowColor());
                this.displayPlane.update(this.lastBB);
            }
            this.displayPlane.spawn(viewer, motion);
        } else {
            if (this.fishPlane == null) {
                this.fishPlane = new VirtualFishingBoundingPlane(this.manager);
                this.fishPlane.setGlowColor(this.getGlowColor());
                this.fishPlane.update(this.lastBB);
            }
            this.fishPlane.spawn(viewer, motion);
        }
    }

    @Override
    protected void sendDestroyPackets(AttachmentViewer viewer) {
        if (viewer.supportsDisplayEntities()) {
            if (this.displayPlane != null) {
                this.displayPlane.destroy(viewer);
            }
        } else if (this.fishPlane != null) {
            this.fishPlane.destroy(viewer);
        }
    }

    @Override
    public void syncPosition(boolean absolute) {
        if (this.displayPlane != null) {
            this.displayPlane.syncPosition(absolute);
        }
        if (this.fishPlane != null) {
            this.fishPlane.syncPosition(absolute);
        }
    }

    @Override
    public boolean containsEntityId(int entityId) {
        if (this.displayPlane != null && this.displayPlane.containsEntityId(entityId)) {
            return true;
        }
        return this.fishPlane != null && this.fishPlane.containsEntityId(entityId);
    }
}

