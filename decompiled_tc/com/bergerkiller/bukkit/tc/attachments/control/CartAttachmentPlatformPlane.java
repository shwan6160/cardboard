/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.OrientedBoundingBox
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.math.Vector3
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  org.bukkit.ChatColor
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.OrientedBoundingBox;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentPlatform;
import com.bergerkiller.bukkit.tc.attachments.helper.HelperMethods;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualBoundingBox;
import com.bergerkiller.bukkit.tc.attachments.surface.CollisionSurface;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.util.Vector;

public class CartAttachmentPlatformPlane
extends CartAttachmentPlatform {
    private final OrientedBoundingBox bbox = new OrientedBoundingBox();
    private final List<PlayerSurface> playerSurfaces = new ArrayList<PlayerSurface>();
    private Plane plane = null;

    @Override
    public void onLoad(ConfigurationNode config) {
        Vector3 size = (Vector3)LogicUtil.fixNull((Object)this.getConfiguredPosition().size, (Object)DEFAULT_SIZE);
        this.bbox.setSize(new Vector(size.x, 0.0, size.z));
    }

    @Override
    public boolean checkCanReload(ConfigurationNode config) {
        if (!super.checkCanReload(config)) {
            return false;
        }
        return CartAttachmentPlatformPlane.readPlatformMode(config) == CartAttachmentPlatform.PlatformMode.PLANE;
    }

    @Override
    public void makeVisible(AttachmentViewer viewer) {
        if (this.plane != null) {
            this.plane.makeVisible(viewer);
        }
        PlayerSurface ps = new PlayerSurface(viewer, viewer.createCollisionSurface());
        ps.surface.setShape(this.bbox);
        this.playerSurfaces.add(ps);
    }

    @Override
    public void makeHidden(AttachmentViewer viewer) {
        if (this.plane != null) {
            this.plane.makeHidden(viewer);
        }
        this.playerSurfaces.removeIf(ps -> {
            if (ps.viewer.equals(viewer)) {
                ps.surface.remove();
                return true;
            }
            return false;
        });
    }

    public void setPlaneColor(ChatColor color) {
        if (color != null) {
            if (this.plane == null) {
                this.plane = new Plane(this.getManager(), this.bbox);
                this.plane.entity.setGlowColor(color);
                for (AttachmentViewer viewer : this.getAttachmentViewers()) {
                    this.plane.makeVisible(viewer);
                }
            } else {
                this.plane.entity.setGlowColor(color);
            }
        } else if (this.plane != null) {
            this.plane.entity.setGlowColor(null);
            this.plane.tickLastHidden = CommonUtil.getServerTicks();
        }
    }

    @Override
    public void onFocus() {
        this.setPlaneColor(HelperMethods.getFocusGlowColor(this));
    }

    @Override
    public void onBlur() {
        this.setPlaneColor(null);
    }

    @Override
    public void onTick() {
        if (this.plane != null && !this.isFocused() && CommonUtil.getServerTicks() - this.plane.tickLastHidden > 40) {
            this.plane.entity.destroyForAll();
            this.plane = null;
        }
    }

    @Override
    public void onTransformChanged(Matrix4x4 transform) {
        Quaternion orientation = transform.getRotation();
        this.bbox.setPosition(transform.toVector().add(orientation.upVector().multiply(0.5 * this.bbox.getSize().getY())));
        this.bbox.setOrientation(orientation);
        if (this.plane != null) {
            this.plane.update(this.bbox);
        }
        this.playerSurfaces.forEach(ps -> ps.surface.setShape(this.bbox));
    }

    @Override
    public void onMove(boolean absolute) {
        if (this.plane != null) {
            this.plane.sync();
        }
    }

    private static class Plane {
        public final VirtualBoundingBox entity;
        private int tickLastHidden = 0;

        public Plane(AttachmentManager manager, OrientedBoundingBox bbox) {
            this.entity = VirtualBoundingBox.createPlane(manager, MaterialUtil.getFirst((String[])new String[]{"ICE", "LEGACY_ICE"}));
            this.entity.update(bbox);
        }

        public void update(OrientedBoundingBox bbox) {
            this.entity.update(bbox);
        }

        public void sync() {
            this.entity.syncPosition(true);
        }

        public void makeVisible(AttachmentViewer viewer) {
            this.entity.spawn(viewer, new Vector(0.0, 0.0, 0.0));
        }

        public void makeHidden(AttachmentViewer viewer) {
            this.entity.destroy(viewer);
        }
    }

    private static class PlayerSurface {
        public final AttachmentViewer viewer;
        public final CollisionSurface surface;

        public PlayerSurface(AttachmentViewer viewer, CollisionSurface surface) {
            this.viewer = viewer;
            this.surface = surface;
        }
    }
}

