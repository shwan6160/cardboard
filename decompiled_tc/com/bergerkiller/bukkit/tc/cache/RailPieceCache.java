/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 */
package com.bergerkiller.bukkit.tc.cache;

import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.rails.RailLookup;

@Deprecated
public class RailPieceCache {
    public static RailPiece[] find(RailState state) {
        return RailLookup.findAtBlockPosition(state.positionOfflineBlock());
    }

    public static RailPiece[] find(OfflineBlock block) {
        return RailLookup.findAtBlockPosition(block);
    }

    public static void reset() {
        RailLookup.forceRecalculation();
    }
}

