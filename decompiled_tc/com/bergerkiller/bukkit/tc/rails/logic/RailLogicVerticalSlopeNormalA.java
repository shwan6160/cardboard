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

public class RailLogicVerticalSlopeNormalA
extends RailLogicVerticalSlopeBase {
    private static final RailLogicVerticalSlopeNormalA[] values = new RailLogicVerticalSlopeNormalA[4];

    private RailLogicVerticalSlopeNormalA(BlockFace direction) {
        super(direction, false);
    }

    public static RailLogicVerticalSlopeNormalA get(BlockFace direction) {
        return values[FaceUtil.faceToNotch((BlockFace)direction) >> 1];
    }

    @Override
    protected RailPath createPath() {
        double dx = 0.5 + 0.4375 * (double)this.getDirection().getModX();
        double dz = 0.5 + 0.4375 * (double)this.getDirection().getModZ();
        Vector p1 = new Vector(dx, 0.0625, dz);
        Vector p2 = new Vector(dx, 1.0, dz);
        if (this.alongZ) {
            p1.setZ(0.5 - 0.5 * (double)this.getDirection().getModZ());
        } else if (this.alongX) {
            p1.setX(0.5 - 0.5 * (double)this.getDirection().getModX());
        }
        return new RailPath.Builder().add(p1, BlockFace.UP).add(p2, this.getDirection().getOppositeFace()).build();
    }

    static {
        for (int i = 0; i < 4; ++i) {
            RailLogicVerticalSlopeNormalA.values[i] = new RailLogicVerticalSlopeNormalA(FaceUtil.notchToFace((int)(i << 1)));
        }
    }
}

