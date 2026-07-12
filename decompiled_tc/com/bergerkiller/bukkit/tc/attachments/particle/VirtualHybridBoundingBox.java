/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.OrientedBoundingBox
 *  org.bukkit.ChatColor
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.particle;

import com.bergerkiller.bukkit.common.math.OrientedBoundingBox;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualBoundingBox;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualDisplayBoundingBox;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualFishingBoundingBox;
import org.bukkit.ChatColor;
import org.bukkit.util.Vector;

public class VirtualHybridBoundingBox
extends VirtualBoundingBox {
    private OrientedBoundingBox lastBB;
    private VirtualFishingBoundingBox fishBox;
    private VirtualDisplayBoundingBox displayBox;

    public VirtualHybridBoundingBox(AttachmentManager manager) {
        super(manager);
    }

    @Override
    public void update(OrientedBoundingBox boundingBox) {
        this.lastBB = boundingBox;
        if (this.displayBox != null) {
            this.displayBox.update(boundingBox);
        }
        if (this.fishBox != null) {
            this.fishBox.update(boundingBox);
        }
    }

    @Override
    protected void applyGlowing(ChatColor color) {
        if (this.displayBox != null) {
            this.displayBox.setGlowColor(color);
        }
        if (this.fishBox != null) {
            this.fishBox.setGlowColor(color);
        }
    }

    @Override
    protected void sendSpawnPackets(AttachmentViewer viewer, Vector motion) {
        if (viewer.supportsDisplayEntities()) {
            if (this.displayBox == null) {
                this.displayBox = new VirtualDisplayBoundingBox(this.manager);
                this.displayBox.setGlowColor(this.getGlowColor());
                this.displayBox.update(this.lastBB);
            }
            this.displayBox.spawn(viewer, motion);
        } else {
            if (this.fishBox == null) {
                this.fishBox = new VirtualFishingBoundingBox(this.manager);
                this.fishBox.setGlowColor(this.getGlowColor());
                this.fishBox.update(this.lastBB);
            }
            this.fishBox.spawn(viewer, motion);
        }
    }

    @Override
    protected void sendDestroyPackets(AttachmentViewer viewer) {
        if (viewer.supportsDisplayEntities()) {
            if (this.displayBox != null) {
                this.displayBox.destroy(viewer);
            }
        } else if (this.fishBox != null) {
            this.fishBox.destroy(viewer);
        }
    }

    @Override
    public void syncPosition(boolean absolute) {
        if (this.displayBox != null) {
            this.displayBox.syncPosition(absolute);
        }
        if (this.fishBox != null) {
            this.fishBox.syncPosition(absolute);
        }
    }

    @Override
    public boolean containsEntityId(int entityId) {
        if (this.displayBox != null && this.displayBox.containsEntityId(entityId)) {
            return true;
        }
        return this.fishBox != null && this.fishBox.containsEntityId(entityId);
    }
}

