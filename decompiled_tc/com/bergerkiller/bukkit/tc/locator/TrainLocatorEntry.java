/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.locator;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualFishingLine;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import java.util.Collections;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

class TrainLocatorEntry {
    private static final double MAX_DISTANCE = 50.0;
    public final Player player;
    public final MinecartMember<?> member;
    private final VirtualFishingLine line;
    public int timeoutTickTime;

    private TrainLocatorEntry(Player player, MinecartMember<?> member) {
        this.player = player;
        this.member = member;
        this.line = new VirtualFishingLine(true);
        this.timeoutTickTime = Integer.MAX_VALUE;
    }

    public void spawn() {
        this.line.spawn(this.player, null, this.calcTarget());
    }

    public void update() {
        this.line.update(Collections.singleton(this.player), null, this.calcTarget());
    }

    public void despawn() {
        this.line.destroy(this.player);
    }

    private Vector calcTarget() {
        Vector targetPos = ((CommonMinecart)this.member.getEntity()).loc.vector();
        Vector diff = targetPos.clone().subtract(this.player.getLocation().toVector());
        double distance = diff.length();
        if (distance > 50.0) {
            targetPos.subtract(diff.multiply(1.0 - 50.0 / distance));
        }
        return targetPos;
    }

    public static TrainLocatorEntry create(Player player, MinecartMember<?> member) {
        TrainLocatorEntry locator = new TrainLocatorEntry(player, member);
        locator.spawn();
        return locator;
    }
}

