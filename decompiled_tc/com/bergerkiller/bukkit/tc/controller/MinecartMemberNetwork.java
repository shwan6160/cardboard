/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.RunOnceTask
 *  com.bergerkiller.bukkit.common.controller.EntityNetworkController
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.EntityTracker
 *  com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.controller;

import com.bergerkiller.bukkit.common.RunOnceTask;
import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import java.util.List;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Deprecated
public class MinecartMemberNetwork
extends EntityNetworkController<CommonMinecart<?>> {
    private final TrainCarts plugin;
    private final RunOnceTask verifyExistsCheck;
    private MinecartMember<?> member = null;
    private boolean isInProcessOfSpawning = false;

    public MinecartMemberNetwork(TrainCarts plugin) {
        this.plugin = plugin;
        this.verifyExistsCheck = new RunOnceTask((Plugin)plugin){

            public void run() {
                EntityTracker tracker;
                EntityTrackerEntryHandle entry;
                World world;
                MinecartMember<?> member = MinecartMemberNetwork.this.getMember();
                if (MinecartMemberNetwork.this.isInProcessOfSpawning) {
                    return;
                }
                if (!(MinecartMemberNetwork.this.entity == null || member != null && ((CommonMinecart)member.getEntity()).isSpawned() || (world = ((CommonMinecart)MinecartMemberNetwork.this.entity).getWorld()) == null || (entry = (tracker = WorldUtil.getTracker((World)world)).getEntry(((CommonMinecart)MinecartMemberNetwork.this.entity).getEntity())) == null || MinecartMemberNetwork.this.getHandle() != entry.getRaw())) {
                    tracker.stopTracking(((CommonMinecart)MinecartMemberNetwork.this.entity).getEntity());
                }
            }
        };
    }

    public void setInProcessOfSpawning(boolean spawning) {
        this.isInProcessOfSpawning = spawning;
    }

    public Attachment getRootAttachment() {
        MinecartMember<?> member = this.getMember();
        return member != null && member.getAttachments().isAttached() ? member.getAttachments().getRootAttachment() : null;
    }

    public CartAttachmentSeat findSeat(Entity passenger) {
        MinecartMember<?> member = this.getMember();
        return member == null ? null : member.getAttachments().findSeat(passenger);
    }

    public Matrix4x4 getLiveTransform() {
        MinecartMember<?> member = this.getMember();
        return member == null ? null : member.getAttachments().getLiveTransform();
    }

    public void onAttached() {
        super.onAttached();
        this.getMember().getAttachments().onAttached();
    }

    public void onDetached() {
        super.onDetached();
        if (this.member != null) {
            this.member.getAttachments().onDetached();
        }
        this.verifyExistsCheck.cancel();
    }

    protected void onPassengersChanged(List<Entity> oldPassengers, List<Entity> newPassengers) {
        if (this.member != null) {
            this.member.getAttachments().onPassengersChanged(oldPassengers, newPassengers);
        }
    }

    public void makeVisible(Player viewer) {
        this.verifyExistsCheck.start();
        this.member.getAttachments().makeVisible(viewer);
    }

    public void makeHidden(Player viewer, boolean instant) {
        MinecartMember<?> member = this.getMember();
        if (member != null) {
            member.getAttachments().makeHidden(viewer);
        }
    }

    public void onTick() {
        MinecartMember<?> member = this.getMember();
        if (member != null && member.isUnloaded()) {
            member.getAttachments().syncUnloaded();
        }
        this.locSynched.set(this.locLive);
        this.syncPassengers();
    }

    public MinecartMember<?> getMember() {
        if (this.entity == null) {
            this.member = null;
        } else if (this.member == null) {
            this.member = (MinecartMember)((CommonMinecart)this.entity).getController(MinecartMember.class);
        }
        return this.member;
    }
}

