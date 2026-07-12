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
import com.bergerkiller.bukkit.tc.rails.WorldRailLookupNone;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneCacheWorld;
import java.util.Collection;
import java.util.List;
import org.bukkit.World;
import org.bukkit.block.Block;

public interface WorldRailLookup {
    public static final WorldRailLookup NONE = new WorldRailLookupNone();

    public World getWorld();

    public OfflineWorld getOfflineWorld();

    public MutexZoneCacheWorld getMutexZones();

    public SignControllerWorld getSignController();

    public boolean isValid();

    public boolean isValidForWorld(World var1);

    public RailPiece[] findAtStatePosition(RailState var1);

    public RailPiece[] findAtBlockPosition(OfflineBlock var1);

    public RailLookup.CachedRailPiece lookupCachedRailPieceIfCached(OfflineBlock var1, RailType var2);

    public List<RailLookup.CachedRailPiece> lookupCachedRailPieces(OfflineBlock var1);

    public RailLookup.CachedRailPiece lookupCachedRailPiece(OfflineBlock var1, Block var2, RailType var3);

    public List<MinecartMember<?>> findMembersOnRail(IntVector3 var1);

    public List<MinecartMember<?>> findMembersOnRail(OfflineBlock var1);

    public void removeMemberFromAll(MinecartMember<?> var1);

    public RailLookup.TrackedSign[] discoverSignsAtRailPiece(RailPiece var1);

    public RailPiece discoverRailPieceFromSign(Block var1);

    public void redetectSignActions();

    public void storeDetectorRegions(IntVector3 var1, DetectorRegion[] var2);

    public DetectorRegion[] getDetectorRegions(IntVector3 var1);

    public Collection<IntVector3> getBlockIndex();

    public static class ClosedException
    extends IllegalStateException {
        private static final long serialVersionUID = -5457138086475585185L;

        public ClosedException() {
            super("World Rail Lookup cache is closed");
        }
    }
}

