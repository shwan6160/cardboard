/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.utils.DebugUtil
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  org.bukkit.Color
 *  org.bukkit.Location
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.utils.DebugUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.VirtualEntity;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachment;
import com.bergerkiller.bukkit.tc.debug.DebugToolUtil;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class CartAttachmentPlatformTestVersion
extends CartAttachment {
    public static final AttachmentType TYPE = new AttachmentType(){

        @Override
        public String getID() {
            return "PLATFORM";
        }

        @Override
        public MapTexture getIcon(ConfigurationNode config) {
            return MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/platform.png");
        }

        @Override
        public Attachment createController(ConfigurationNode config) {
            return new CartAttachmentPlatformTestVersion();
        }
    };
    private List<Grounded> grounded = new ArrayList<Grounded>();
    private double width = 5.0;
    private double length = 5.0;

    @Override
    public void onDetached() {
        super.onDetached();
    }

    @Override
    public void onAttached() {
        super.onAttached();
    }

    @Override
    public void applyPassengerSeatTransform(Matrix4x4 transform) {
        Matrix4x4 relativeMatrix = new Matrix4x4();
        relativeMatrix.translate(0.0, 1.0, 0.0);
        Matrix4x4.multiply((Matrix4x4)relativeMatrix, (Matrix4x4)transform, (Matrix4x4)transform);
    }

    @Override
    @Deprecated
    public void makeVisible(Player player) {
        this.makeVisible(this.getManager().asAttachmentViewer(player));
    }

    @Override
    @Deprecated
    public void makeHidden(Player player) {
        this.makeHidden(this.getManager().asAttachmentViewer(player));
    }

    @Override
    public void makeVisible(AttachmentViewer viewer) {
    }

    @Override
    public void makeHidden(AttachmentViewer viewer) {
        Iterator<Grounded> iter = this.grounded.iterator();
        while (iter.hasNext()) {
            Grounded g = iter.next();
            if (g.player != viewer) continue;
            g.destroy();
            iter.remove();
        }
    }

    @Override
    public void onTransformChanged(Matrix4x4 transform) {
        Matrix4x4 transform_inv = transform.clone();
        transform_inv.invert();
        double half_width = 0.5 * this.width;
        double half_length = 0.5 * this.length;
        Iterator<Grounded> grounded_iter = this.grounded.iterator();
        while (grounded_iter.hasNext()) {
            Grounded g = grounded_iter.next();
            Vector p = g.player.getLocation().toVector();
            double player_y = p.getY();
            transform_inv.transformPoint(p);
            if (p.getX() < -half_width || p.getX() > half_width || p.getZ() < -half_length || p.getZ() > half_length) {
                g.destroy();
                grounded_iter.remove();
                continue;
            }
            Matrix4x4 t_copy = transform.clone();
            t_copy.translate(p.getX(), 0.0, p.getZ());
            g.update(this.getPreviousTransform(), transform, t_copy, player_y);
            Color color = p.getY() > 0.1 ? Color.GREEN : Color.RED;
            Vector p1 = new Vector(-half_width, 0.0, -half_length);
            Vector p2 = new Vector(-half_width, 0.0, half_length);
            Vector p3 = new Vector(half_width, 0.0, half_length);
            Vector p4 = new Vector(half_width, 0.0, -half_length);
            transform.transformPoint(p1);
            transform.transformPoint(p2);
            transform.transformPoint(p3);
            transform.transformPoint(p4);
            DebugToolUtil.showLineParticles(g.player, color, p1, p2);
            DebugToolUtil.showLineParticles(g.player, color, p2, p3);
            DebugToolUtil.showLineParticles(g.player, color, p3, p4);
            DebugToolUtil.showLineParticles(g.player, color, p4, p1);
            Vector p1_mid = new Vector(0.0, 0.0, -half_length);
            Vector p2_mid = new Vector(0.0, 0.0, half_length);
            Vector p3_mid = new Vector(half_width, 0.0, 0.0);
            Vector p4_mid = new Vector(-half_width, 0.0, 0.0);
            transform.transformPoint(p1_mid);
            transform.transformPoint(p2_mid);
            transform.transformPoint(p3_mid);
            transform.transformPoint(p4_mid);
            DebugToolUtil.showLineParticles(g.player, color, p1_mid, p2_mid);
            DebugToolUtil.showLineParticles(g.player, color, p3_mid, p4_mid);
        }
        for (AttachmentViewer viewer : this.getAttachmentViewers()) {
            boolean isGrounded = false;
            for (Grounded g : this.grounded) {
                if (!g.viewer.equals(viewer)) continue;
                isGrounded = true;
                break;
            }
            if (isGrounded) continue;
            Vector p = viewer.getPlayer().getLocation().toVector();
            double player_y = p.getY();
            transform_inv.transformPoint(p);
            if (p.getY() <= 0.0 || p.getX() < -half_width || p.getX() > half_width || p.getZ() < -half_length || p.getZ() > half_length) continue;
            Matrix4x4 t_copy = transform.clone();
            t_copy.translate(p.getX(), 0.0, p.getZ());
            Grounded gnew = new Grounded(viewer, this.getManager());
            gnew.update(this.getPreviousTransform(), transform, t_copy, player_y);
            gnew.spawn();
            this.grounded.add(gnew);
        }
    }

    @Override
    public void onMove(boolean absolute) {
        for (Grounded g : this.grounded) {
            g.syncPosition(absolute);
        }
    }

    @Override
    public void onTick() {
    }

    private static boolean hasChanges(Vector vel) {
        return vel.getX() < -0.001 || vel.getX() > 0.001 || vel.getZ() < -0.001 || vel.getZ() > 0.001;
    }

    static /* synthetic */ boolean access$000(Vector x0) {
        return CartAttachmentPlatformTestVersion.hasChanges(x0);
    }

    private static class Grounded {
        public final Player player;
        public final AttachmentViewer viewer;
        public Location prev_pos = null;
        public Vector old_pos = null;
        public Vector on_platform_pos = null;
        public Vector old_player_pos = null;
        private boolean is_on_platform = false;
        private boolean did_adjust = false;
        private final AttachmentManager manager;
        private final VirtualEntity actual;
        private final VirtualEntity entity;

        public Grounded(AttachmentViewer viewer, AttachmentManager manager) {
            this.player = viewer.getPlayer();
            this.viewer = viewer;
            this.manager = manager;
            this.actual = new VirtualEntity(manager);
            this.actual.setEntityType(EntityType.SHULKER);
            this.actual.getMetaData().set(EntityHandle.DATA_FLAGS, (Object)32);
            this.entity = new VirtualEntity(manager);
            this.entity.setEntityType(EntityType.CHICKEN);
            this.entity.getMetaData().set(EntityHandle.DATA_FLAGS, (Object)32);
            this.entity.getMetaData().set(EntityHandle.DATA_NO_GRAVITY, (Object)true);
        }

        public void spawn() {
            this.actual.spawn(this.viewer, new Vector());
            this.entity.spawn(this.viewer, new Vector());
            this.viewer.getVehicleMountController().mount(this.entity.getEntityId(), this.actual.getEntityId());
        }

        public void destroy() {
            this.actual.destroy(this.viewer);
            this.entity.destroy(this.viewer);
        }

        public void update(Matrix4x4 platform_old_transform, Matrix4x4 platform_transform, Matrix4x4 transform, double player_y) {
            double shulker_offset = DebugUtil.getDoubleValue((String)"a", (double)0.1);
            Vector pos = transform.toVector();
            Vector ypr = new Vector();
            this.entity.setRelativeOffset(0.0, -1.3499 + shulker_offset, 0.0);
            this.entity.updatePosition(pos, ypr);
            this.actual.updatePosition(pos, ypr);
        }

        public void syncPosition(boolean absolute) {
        }
    }
}

