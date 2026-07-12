/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.collections.ImplicitlySharedList
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.common.collections.ImplicitlySharedList;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.SignTracker;
import com.bergerkiller.bukkit.tc.detector.DetectorRegion;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.modlist.ModificationTrackedArrayList;
import com.bergerkiller.bukkit.tc.utils.modlist.ModificationTrackedList;
import org.bukkit.block.Block;

public class SignTrackerMember
extends SignTracker {
    private final MinecartMember<?> owner;
    protected final ModificationTrackedList<SignTracker.ActiveSign> liveActiveSigns = new ModificationTrackedArrayList<SignTracker.ActiveSign>();

    public SignTrackerMember(MinecartMember<?> owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public MinecartMember<?> getOwner() {
        return this.owner;
    }

    public boolean addToDetectorRegion(DetectorRegion region) {
        if (!region.add(this.owner)) {
            return false;
        }
        this.detectorRegions.add((Object)region);
        ImplicitlySharedList groupRegions = this.owner.getGroup().getSignTracker().detectorRegions;
        if (!groupRegions.contains(region)) {
            groupRegions.add(region);
        }
        return true;
    }

    @Override
    public void addOfflineActiveSignKey(Object signUniqueKey) {
        super.addOfflineActiveSignKey(signUniqueKey);
        this.owner.getGroup().getSignTracker().addOfflineActiveSignKey(signUniqueKey);
    }

    @Override
    public void clear(SignTracker.ClearMode clearMode) {
        super.clear(clearMode);
        if (!this.detectorRegions.isEmpty()) {
            for (DetectorRegion region : this.detectorRegions.cloneAsIterable()) {
                region.remove(this.owner);
            }
            this.detectorRegions.clear();
        }
    }

    @Override
    @Deprecated
    public boolean isOnRails(Block railsBlock) {
        return this.owner.getRailTracker().isOnRails(railsBlock);
    }

    @Override
    protected void onSignChange(SignTracker.ActiveSign sign, boolean active) {
        sign.executeEventForMember(active ? SignActionType.MEMBER_ENTER : SignActionType.MEMBER_LEAVE, this.owner);
    }

    @Override
    protected void onLoadedChange(SignTracker.ActiveSign sign, boolean loaded) {
    }

    @Override
    public void update() {
        MinecartGroup group;
        super.update();
        if (!this.owner.isUnloaded() && (group = this.owner.getGroup()) != null) {
            group.getSignTracker().update();
        }
    }
}

