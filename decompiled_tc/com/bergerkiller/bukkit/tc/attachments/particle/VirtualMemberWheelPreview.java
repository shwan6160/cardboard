/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.particle;

import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualArrowItem;
import com.bergerkiller.bukkit.tc.controller.components.WheelTrackerMember;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class VirtualMemberWheelPreview {
    private static final ItemStack WHEEL_ITEM = new ItemStack(Material.ENDER_PEARL);
    private int leftEntityId = -1;
    private int rightEntityId = -1;

    public void spawn(Player viewer, double width, WheelTrackerMember.Wheel wheel) {
        ComputedPositions computed = new ComputedPositions(width, wheel);
        this.leftEntityId = VirtualArrowItem.create(this.leftEntityId).position(computed.pLeft, computed.orientation).item(WHEEL_ITEM).glowing(true).spawn(viewer);
        this.rightEntityId = VirtualArrowItem.create(this.rightEntityId).position(computed.pRight, computed.orientation).item(WHEEL_ITEM).glowing(true).spawn(viewer);
    }

    public void update(Iterable<Player> viewers, double width, WheelTrackerMember.Wheel wheel) {
        ComputedPositions computed = new ComputedPositions(width, wheel);
        VirtualArrowItem.create(this.leftEntityId).position(computed.pLeft, computed.orientation).move(viewers);
        VirtualArrowItem.create(this.rightEntityId).position(computed.pRight, computed.orientation).move(viewers);
    }

    public void destroy(Player viewer) {
        VirtualArrowItem.create(this.leftEntityId).destroy(viewer);
        VirtualArrowItem.create(this.rightEntityId).destroy(viewer);
    }

    private static class ComputedPositions {
        public final Quaternion orientation;
        public final Vector pLeft;
        public final Vector pRight;

        public ComputedPositions(double width, WheelTrackerMember.Wheel wheel) {
            this.orientation = Quaternion.fromLookDirection((Vector)wheel.getForward(), (Vector)wheel.getUp());
            Vector position = wheel.getAbsolutePosition();
            position.add(this.orientation.forwardVector().multiply(-0.03));
            this.pLeft = position.clone().add(this.orientation.rightVector().multiply(-0.5 * width));
            this.pRight = position.clone().add(this.orientation.rightVector().multiply(0.5 * width));
            this.orientation.rotateY(90.0);
        }
    }
}

