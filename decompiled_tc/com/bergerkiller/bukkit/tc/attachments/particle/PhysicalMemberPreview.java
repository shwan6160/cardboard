/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.OrientedBoundingBox
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil$ItemSynchronizer
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.particle;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.OrientedBoundingBox;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualBoundingBox;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualMemberWheelPreview;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualTrainCoupler;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PhysicalMemberPreview {
    private final VirtualBoundingBox boundingBox = VirtualBoundingBox.create(null);
    private final VirtualTrainCoupler trainCouplerFront = VirtualTrainCoupler.create(null);
    private final VirtualTrainCoupler trainCouplerBack = VirtualTrainCoupler.create(null);
    private final VirtualMemberWheelPreview wheelsFront = new VirtualMemberWheelPreview();
    private final VirtualMemberWheelPreview wheelsBack = new VirtualMemberWheelPreview();
    private final Set<Player> viewers = new HashSet<Player>();
    private final MinecartMember<?> member;
    private final Supplier<Collection<Player>> viewerSupplier;

    public PhysicalMemberPreview(MinecartMember<?> member, Supplier<Collection<Player>> viewerSupplier) {
        this.member = member;
        this.viewerSupplier = viewerSupplier;
        this.boundingBox.setGlowColor(ChatColor.WHITE);
        this.trainCouplerFront.setGlowColor(ChatColor.WHITE);
        this.trainCouplerBack.setGlowColor(ChatColor.WHITE);
        this.updateFromCart();
    }

    public void update() {
        this.updateViewers(this.viewerSupplier.get().stream().filter(p -> p.getWorld() == this.member.getWorld()).collect(Collectors.toSet()));
        this.update(this.viewers);
    }

    public void hide() {
        this.updateViewers(Collections.emptySet());
    }

    private void updateViewers(Collection<Player> new_players) {
        LogicUtil.synchronizeUnordered(this.viewers, new_players, (LogicUtil.ItemSynchronizer)new LogicUtil.ItemSynchronizer<Player, Player>(){

            public boolean isItem(Player item, Player value) {
                return item == value;
            }

            public Player onAdded(Player value) {
                PhysicalMemberPreview.this.spawn(TrainCarts.plugin.getAttachmentViewer(value));
                return value;
            }

            public void onRemoved(Player item) {
                PhysicalMemberPreview.this.destroy(TrainCarts.plugin.getAttachmentViewer(item));
            }
        });
    }

    private void spawn(AttachmentViewer viewer) {
        this.boundingBox.spawn(viewer, new Vector(0.0, 0.0, 0.0));
        this.trainCouplerFront.spawn(viewer, new Vector(0.0, 0.0, 0.0));
        this.trainCouplerBack.spawn(viewer, new Vector(0.0, 0.0, 0.0));
        this.wheelsFront.spawn(viewer.getPlayer(), 1.0, this.member.getWheels().front());
        this.wheelsBack.spawn(viewer.getPlayer(), 1.0, this.member.getWheels().back());
    }

    private void updateFromCart() {
        OrientedBoundingBox bb = this.member.getHitBox();
        double couplerLength = this.member.getCartCouplerLength();
        this.boundingBox.update(bb);
        Matrix4x4 transform = Matrix4x4.translation((Vector)bb.getPosition());
        transform.rotate(bb.getOrientation());
        transform.translate(0.0, -0.5 * bb.getSize().getY(), 0.5 * bb.getSize().getZ());
        this.trainCouplerFront.update(transform, couplerLength);
        transform = Matrix4x4.translation((Vector)bb.getPosition());
        transform.rotate(bb.getOrientation());
        transform.rotateYFlip();
        transform.translate(0.0, -0.5 * bb.getSize().getY(), 0.5 * bb.getSize().getZ());
        this.trainCouplerBack.update(transform, couplerLength);
    }

    private void update(Iterable<Player> viewers) {
        this.updateFromCart();
        this.boundingBox.syncPosition(true);
        this.trainCouplerFront.syncPosition(true);
        this.trainCouplerBack.syncPosition(true);
        this.wheelsFront.update(viewers, 1.0, this.member.getWheels().front());
        this.wheelsBack.update(viewers, 1.0, this.member.getWheels().back());
    }

    private void destroy(AttachmentViewer viewer) {
        this.boundingBox.destroy(viewer);
        this.trainCouplerFront.destroy(viewer);
        this.trainCouplerBack.destroy(viewer);
        this.wheelsFront.destroy(viewer.getPlayer());
        this.wheelsBack.destroy(viewer.getPlayer());
    }
}

