/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.OrientedBoundingBox
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  org.bukkit.ChatColor
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.particle;

import com.bergerkiller.bukkit.common.math.OrientedBoundingBox;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualBoundingBox;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualFishingLine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import org.bukkit.ChatColor;
import org.bukkit.util.Vector;

public class VirtualFishingBoundingPlane
extends VirtualBoundingBox {
    protected final BBOXLine line_btm_nx = new BBOXLine(c -> c.btm_nx_nz, c -> c.btm_nx_pz);
    protected final BBOXLine line_btm_px = new BBOXLine(c -> c.btm_px_nz, c -> c.btm_nx_nz);
    protected final BBOXLine line_btm_nz = new BBOXLine(c -> c.btm_px_pz, c -> c.btm_px_nz);
    protected final BBOXLine line_btm_pz = new BBOXLine(c -> c.btm_nx_pz, c -> c.btm_px_pz);
    protected List<BBOXLine> lines = Arrays.asList(this.line_btm_nx, this.line_btm_nz, this.line_btm_px, this.line_btm_pz);
    protected ComputedCorners corners = null;
    private boolean linesSpawned = false;

    public VirtualFishingBoundingPlane(AttachmentManager manager) {
        super(manager);
    }

    @Override
    public void update(OrientedBoundingBox boundingBox) {
        this.corners = new ComputedCorners(boundingBox);
    }

    @Override
    public boolean containsEntityId(int entityId) {
        return false;
    }

    @Override
    protected void sendSpawnPackets(AttachmentViewer viewer, Vector motion) {
        ArrayList<UUID> uuids = new ArrayList<UUID>(24);
        if (this.linesSpawned) {
            this.lines.forEach(bboxline -> bboxline.spawn(viewer, this.corners, uuids));
        } else {
            this.lines.forEach(bboxline -> bboxline.spawnWithoutLine(viewer, this.corners, uuids));
        }
        viewer.sendDisableCollision(uuids);
    }

    @Override
    protected void sendDestroyPackets(AttachmentViewer viewer) {
        this.lines.forEach(line -> line.destroy(viewer));
    }

    @Override
    protected void applyGlowing(ChatColor color) {
        if (this.linesSpawned != (color != null)) {
            boolean bl = this.linesSpawned = !this.linesSpawned;
            if (this.hasViewers()) {
                if (this.linesSpawned) {
                    this.forAllViewers(v -> this.lines.forEach(line -> line.spawnLine((AttachmentViewer)v, this.corners)));
                } else {
                    this.forAllViewers(v -> this.lines.forEach(line -> line.destroyLine((AttachmentViewer)v)));
                }
            }
        }
    }

    @Override
    public void syncPosition(boolean absolute) {
        this.lines.forEach(bboxline -> bboxline.updateViewers(this.getViewers(), this.corners));
    }

    protected static class BBOXLine
    extends VirtualFishingLine {
        private final Function<ComputedCorners, Vector> pos1func;
        private final Function<ComputedCorners, Vector> pos2func;

        public BBOXLine(Function<ComputedCorners, Vector> pos1func, Function<ComputedCorners, Vector> pos2func) {
            this.pos1func = pos1func;
            this.pos2func = pos2func;
        }

        public void spawn(AttachmentViewer viewer, ComputedCorners corners, List<UUID> uuids) {
            Vector p1 = this.pos1func.apply(corners);
            Vector p2 = this.pos2func.apply(corners);
            this.spawnWithoutLineCollectUUIDs(viewer, p1, p2, uuids);
            this.spawnLine(viewer, p1, p2);
        }

        public void spawnWithoutLine(AttachmentViewer viewer, ComputedCorners corners, List<UUID> uuids) {
            this.spawnWithoutLineCollectUUIDs(viewer, this.pos1func.apply(corners), this.pos2func.apply(corners), uuids);
        }

        public void spawnLine(AttachmentViewer viewer, ComputedCorners corners) {
            this.spawnLine(viewer, this.pos1func.apply(corners), this.pos2func.apply(corners));
        }

        public void updateViewers(Iterable<AttachmentViewer> viewers, ComputedCorners corners) {
            this.updateViewers(viewers, this.pos1func.apply(corners), this.pos2func.apply(corners));
        }
    }

    protected static class ComputedCorners {
        public final Vector btm_nx_nz;
        public final Vector btm_px_nz;
        public final Vector btm_px_pz;
        public final Vector btm_nx_pz;
        public final Vector top_nx_nz;
        public final Vector top_px_nz;
        public final Vector top_px_pz;
        public final Vector top_nx_pz;

        public ComputedCorners(OrientedBoundingBox boundingBox) {
            Vector hsize = boundingBox.getSize().clone().multiply(0.5);
            this.btm_nx_nz = new Vector(-hsize.getX(), -hsize.getY(), -hsize.getZ());
            this.btm_px_nz = new Vector(hsize.getX(), -hsize.getY(), -hsize.getZ());
            this.btm_px_pz = new Vector(hsize.getX(), -hsize.getY(), hsize.getZ());
            this.btm_nx_pz = new Vector(-hsize.getX(), -hsize.getY(), hsize.getZ());
            this.top_nx_nz = new Vector(-hsize.getX(), hsize.getY(), -hsize.getZ());
            this.top_px_nz = new Vector(hsize.getX(), hsize.getY(), -hsize.getZ());
            this.top_px_pz = new Vector(hsize.getX(), hsize.getY(), hsize.getZ());
            this.top_nx_pz = new Vector(-hsize.getX(), hsize.getY(), hsize.getZ());
            Quaternion orientation = boundingBox.getOrientation();
            orientation.transformPoint(this.btm_nx_nz);
            orientation.transformPoint(this.btm_px_nz);
            orientation.transformPoint(this.btm_px_pz);
            orientation.transformPoint(this.btm_nx_pz);
            orientation.transformPoint(this.top_nx_nz);
            orientation.transformPoint(this.top_px_nz);
            orientation.transformPoint(this.top_px_pz);
            orientation.transformPoint(this.top_nx_pz);
            Vector offset = boundingBox.getPosition();
            this.btm_nx_nz.add(offset);
            this.btm_px_nz.add(offset);
            this.btm_px_pz.add(offset);
            this.btm_nx_pz.add(offset);
            this.top_nx_nz.add(offset);
            this.top_px_nz.add(offset);
            this.top_px_pz.add(offset);
            this.top_nx_pz.add(offset);
        }
    }
}

