/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.ToggledState
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.collections.ImplicitlySharedList
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.common.ToggledState;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.ImplicitlySharedList;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailTracker;
import com.bergerkiller.bukkit.tc.controller.components.SignTracker;
import com.bergerkiller.bukkit.tc.controller.components.SignTrackerMember;
import com.bergerkiller.bukkit.tc.detector.DetectorRegion;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.modlist.ModificationTrackedEmptyList;
import com.bergerkiller.bukkit.tc.utils.modlist.ModificationTrackedList;
import com.bergerkiller.bukkit.tc.utils.modlist.ModificationTrackedList2D;
import java.util.Iterator;
import java.util.List;
import org.bukkit.block.Block;

public class SignTrackerGroup
extends SignTracker {
    private final MinecartGroup owner;
    private final ToggledState needsPositionUpdate = new ToggledState(true);
    private final ModificationTrackedList2D<SignTracker.ActiveSign> liveActiveSigns = new ModificationTrackedList2D();

    public SignTrackerGroup(MinecartGroup owner) {
        super(owner);
        this.owner = owner;
    }

    @Override
    public MinecartGroup getOwner() {
        return this.owner;
    }

    @Override
    protected void onSignChange(SignTracker.ActiveSign sign, boolean active) {
        sign.executeEventForGroup(active ? SignActionType.GROUP_ENTER : SignActionType.GROUP_LEAVE, this.owner);
    }

    @Override
    protected void onLoadedChange(SignTracker.ActiveSign sign, boolean loaded) {
        sign.executeEventForGroup(loaded ? SignActionType.GROUP_RELOAD : SignActionType.GROUP_UNLOAD, this.owner);
    }

    @Deprecated
    public MinecartMember<?> getMemberFromRails(Block railsBlock) {
        return this.owner.getRailTracker().getMemberFromRails(railsBlock);
    }

    @Deprecated
    public MinecartMember<?> getMemberFromRails(IntVector3 railsBlockPosition) {
        return this.owner.getRailTracker().getMemberFromRails(railsBlockPosition);
    }

    @Override
    public void clear(SignTracker.ClearMode clearMode) {
        for (MinecartMember<?> member : this.owner) {
            member.getSignTracker().clear(clearMode);
        }
        super.clear(clearMode);
        this.detectorRegions.clear();
    }

    public void unload(SignTracker.ClearMode clearMode) {
        if (!this.detectorRegions.isEmpty()) {
            for (DetectorRegion region : this.detectorRegions) {
                region.unload(this.owner);
            }
            this.detectorRegions.clear();
        }
        this.clear(clearMode);
        this.signSkipTracker.unloadSigns();
        for (MinecartMember<?> member : this.owner) {
            member.getSignTracker().signSkipTracker.unloadSigns();
        }
    }

    @Override
    @Deprecated
    public boolean isOnRails(Block railsBlock) {
        return this.owner.getRailTracker().isOnRails(railsBlock);
    }

    public void onMemberRemoved(MinecartMember<?> member) {
        this.removeDetectorRegionsOf(member);
        this.updatePosition();
        this.liveActiveSigns.removeList(member.getSignTracker().liveActiveSigns);
    }

    private void removeDetectorRegionsOf(MinecartMember<?> member) {
        if (this.detectorRegions.isEmpty()) {
            return;
        }
        for (DetectorRegion region : member.getSignTracker().detectorRegions.cloneAsIterable()) {
            region.remove(member);
        }
        member.getSignTracker().detectorRegions.clear();
        Iterator iter = this.detectorRegions.iterator();
        while (iter.hasNext()) {
            DetectorRegion region;
            region = (DetectorRegion)iter.next();
            boolean used = false;
            for (MinecartMember<?> otherMember : this.owner) {
                if (otherMember == member || !otherMember.getSignTracker().detectorRegions.contains((Object)region)) continue;
                used = true;
                break;
            }
            if (used) continue;
            iter.remove();
        }
    }

    public void updatePosition() {
        this.needsPositionUpdate.set();
    }

    @Override
    @Deprecated
    public boolean removeSign(Block signBlock) {
        if (super.removeSign(signBlock)) {
            for (MinecartMember<?> member : this.owner) {
                member.getSignTracker().removeSign(signBlock);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean removeSign(RailLookup.TrackedSign sign) {
        if (super.removeSign(sign)) {
            for (MinecartMember<?> member : this.owner) {
                member.getSignTracker().removeSign(sign);
            }
            return true;
        }
        return false;
    }

    @Override
    protected void clearOfflineActiveSignKeys() {
        super.clearOfflineActiveSignKeys();
        for (MinecartMember<?> member : this.owner) {
            member.getSignTracker().clearOfflineActiveSignKeys();
        }
    }

    /*
     * WARNING - void declaration
     */
    public void refresh() {
        if (this.owner.isEmpty()) {
            this.clearOfflineActiveSignKeys();
            this.clear();
            return;
        }
        if (this.needsPositionUpdate.clear()) {
            for (MinecartMember member : this.owner) {
                member.getSignTracker().liveActiveSigns.clear();
            }
            for (RailTracker.TrackedRail info : this.owner.getRailTracker().getRailInformation()) {
                RailLookup.TrackedSign[] signs;
                if (info.state.railType() == RailType.NONE || (signs = info.state.railSigns()).length <= 0) continue;
                ModificationTrackedList<SignTracker.ActiveSign> modificationTrackedList = info.member.getSignTracker().liveActiveSigns;
                for (RailLookup.TrackedSign sign : signs) {
                    if (sign.getAction() == null && sign.getHeader().isEmpty()) continue;
                    modificationTrackedList.add(new SignTracker.ActiveSign(sign, info.state));
                }
            }
            for (MinecartMember member : this.owner) {
                member.getSignTracker().onSignVisitStart(member.getSignTracker().liveActiveSigns);
            }
            this.liveActiveSigns.resetLists();
            for (MinecartMember member : this.owner) {
                this.liveActiveSigns.addListIfNotEmpty(member.getSignTracker().liveActiveSigns);
            }
            this.onSignVisitStart(this.liveActiveSigns);
            for (Iterator<Object> iterator : this.owner.toArray()) {
                if (((MinecartMember)((Object)iterator)).isUnloaded() || ((MinecartMember)((Object)iterator)).getGroup() != this.owner) continue;
                SignTrackerMember tracker = ((MinecartMember)((Object)iterator)).getSignTracker();
                tracker.updateActiveSigns(() -> ((MinecartMember)tracker.getOwner()).isUnloaded() ? ModificationTrackedEmptyList.emptyList() : tracker.liveActiveSigns);
            }
            this.updateActiveSigns(() -> this.owner.isUnloaded() ? ModificationTrackedEmptyList.emptyList() : this.liveActiveSigns);
            List<RailTracker.TrackedRail> rails = this.getOwner().getRailTracker().getRailInformation();
            if (!this.detectorRegions.isEmpty()) {
                void var4_16;
                MinecartMember<?>[] members;
                for (MinecartMember<?> member : members = this.getOwner().toArray()) {
                    member.getSignTracker().detectorRegions.clear();
                }
                String currentWorldName = this.getOwner().getWorld().getName();
                int n = this.detectorRegions.size() - 1;
                while (var4_16 >= 0) {
                    DetectorRegion region = (DetectorRegion)this.detectorRegions.get((int)var4_16);
                    if (!region.getWorldName().equals(currentWorldName)) {
                        for (MinecartMember<?> member : members) {
                            region.remove(member);
                        }
                        this.detectorRegions.remove((int)var4_16);
                    }
                    --var4_16;
                }
                for (RailTracker.TrackedRail rail : rails) {
                    for (DetectorRegion region : this.detectorRegions.cloneAsIterable()) {
                        ImplicitlySharedList memberRegions;
                        if (!region.getCoordinates().contains(rail.state.railPiece().blockPosition()) || (memberRegions = rail.member.getSignTracker().detectorRegions).contains(region)) continue;
                        memberRegions.add(region);
                        region.add(rail.member);
                    }
                }
                Iterator iterator = this.detectorRegions.iterator();
                while (iterator.hasNext()) {
                    DetectorRegion region = (DetectorRegion)iterator.next();
                    boolean foundMember = false;
                    for (MinecartMember<?> member : members) {
                        if (member.getSignTracker().detectorRegions.contains((Object)region)) {
                            foundMember = true;
                            continue;
                        }
                        region.remove(member);
                    }
                    if (foundMember) continue;
                    iterator.remove();
                }
            }
            for (RailTracker.TrackedRail rail : rails) {
                for (DetectorRegion region : rail.state.railPiece().detectorRegions()) {
                    rail.member.getSignTracker().addToDetectorRegion(region);
                }
            }
        }
        if (this.needsUpdate.clear()) {
            for (SignTracker.ActiveSign activeSign : this.getActiveTrackedSigns().cloneAsIterable()) {
                activeSign.executeEventForGroup(SignActionType.GROUP_UPDATE, this.owner);
            }
            for (DetectorRegion region : this.detectorRegions.cloneAsIterable()) {
                region.update(this.owner);
            }
            for (MinecartMember member : this.owner) {
                SignTrackerMember tracker = member.getSignTracker();
                if (!tracker.needsUpdate.clear()) continue;
                for (SignTracker.ActiveSign activeSign : tracker.getActiveTrackedSigns()) {
                    activeSign.executeEventForMember(SignActionType.MEMBER_UPDATE, (MinecartMember<?>)tracker.getOwner());
                }
                for (DetectorRegion region : tracker.detectorRegions.cloneAsIterable()) {
                    region.update((MinecartMember<?>)tracker.getOwner());
                }
            }
        }
        this.clearOfflineActiveSignKeys();
    }
}

