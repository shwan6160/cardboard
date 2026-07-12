/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.rails.logic;

import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicVerticalSlopeBase;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class RailLogicVerticalSlopeUpsideDownB
extends RailLogicVerticalSlopeBase {
    private static final RailLogicVerticalSlopeUpsideDownB[] values = new RailLogicVerticalSlopeUpsideDownB[4];

    private RailLogicVerticalSlopeUpsideDownB(BlockFace direction) {
        super(direction, true);
    }

    public static RailLogicVerticalSlopeUpsideDownB get(BlockFace direction) {
        return values[FaceUtil.faceToNotch((BlockFace)direction) >> 1];
    }

    @Override
    public RailPath getPath() {
        double dx = 0.5 - 0.4375 * (double)this.getDirection().getModX();
        double dz = 0.5 - 0.4375 * (double)this.getDirection().getModZ();
        Vector p1 = new Vector(dx, 1.0, dz);
        Vector p2 = new Vector(dx, -0.2, dz);
        Vector p3 = new Vector(dx, -0.2625, dz);
        if (this.alongZ) {
            p3.setZ(0.5 - 0.5 * (double)this.getDirection().getModZ());
        } else if (this.alongX) {
            p3.setX(0.5 - 0.5 * (double)this.getDirection().getModX());
        }
        return new RailPath.Builder().add(p1, this.getDirection()).add(p2, this.getDirection()).add(p3, BlockFace.DOWN).build();
    }

    static {
        for (int i = 0; i < 4; ++i) {
            RailLogicVerticalSlopeUpsideDownB.values[i] = new RailLogicVerticalSlopeUpsideDownB(FaceUtil.notchToFace((int)(i << 1)));
        }
    }
}

