/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  com.bergerkiller.bukkit.common.offline.OfflineWorld
 *  org.bukkit.World
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.rails;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.controller.global.SignControllerWorld;
import com.bergerkiller.bukkit.tc.detector.DetectorRegion;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.rails.WorldRailLookup;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneCacheWorld;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.bukkit.World;
import org.bukkit.block.Block;

final class WorldRailLookupNone
implements WorldRailLookup {
    WorldRailLookupNone() {
    }

    @Override
    public World getWorld() {
        return null;
    }

    @Override
    public OfflineWorld getOfflineWorld() {
        return OfflineWorld.NONE;
    }

    @Override
    public MutexZoneCacheWorld getMutexZones() {
        throw new UnsupportedOperationException("World Rail Lookup cache is closed");
    }

    @Override
    public SignControllerWorld getSignController() {
        throw new UnsupportedOperationException("World Rail Lookup cache is closed");
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public boolean isValidForWorld(World world) {
        return false;
    }

    @Override
    public RailPiece[] findAtStatePosition(RailState state) {
        throw new WorldRailLookup.ClosedException();
    }

    @Override
    public RailPiece[] findAtBlockPosition(OfflineBlock positionBlock) {
        throw new WorldRailLookup.ClosedException();
    }

    @Override
    public RailLookup.CachedRailPiece lookupCachedRailPieceIfCached(OfflineBlock railOfflineBlock, RailType railType) {
        return RailLookup.CachedRailPiece.NONE;
    }

    @Override
    public List<RailLookup.CachedRailPiece> lookupCachedRailPieces(OfflineBlock railOfflineBlock) {
        return Collections.emptyList();
    }

    @Override
    public RailLookup.CachedRailPiece lookupCachedRailPiece(OfflineBlock railOfflineBlock, Block railBlock, RailType railType) {
        throw new WorldRailLookup.ClosedException();
    }

    @Override
    public List<MinecartMember<?>> findMembersOnRail(IntVector3 railCoordinates) {
        return Collections.emptyList();
    }

    @Override
    public List<MinecartMember<?>> findMembersOnRail(OfflineBlock railOfflineBlock) {
        return Collections.emptyList();
    }

    @Override
    public void removeMemberFromAll(MinecartMember<?> member) {
        throw new WorldRailLookup.ClosedException();
    }

    @Override
    public RailLookup.TrackedSign[] discoverSignsAtRailPiece(RailPiece rail) {
        throw new WorldRailLookup.ClosedException();
    }

    @Override
    public RailPiece discoverRailPieceFromSign(Block signblock) {
        throw new WorldRailLookup.ClosedException();
    }

    @Override
    public void redetectSignActions() {
    }

    @Override
    public void storeDetectorRegions(IntVector3 coordinates, DetectorRegion[] regions) {
        throw new WorldRailLookup.ClosedException();
    }

    @Override
    public DetectorRegion[] getDetectorRegions(IntVector3 coordinates) {
        throw new WorldRailLookup.ClosedException();
    }

    @Override
    public Collection<IntVector3> getBlockIndex() {
        return Collections.emptySet();
    }
}

