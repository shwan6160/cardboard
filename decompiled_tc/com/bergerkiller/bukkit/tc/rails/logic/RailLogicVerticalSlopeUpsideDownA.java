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

public class RailLogicVerticalSlopeUpsideDownA
extends RailLogicVerticalSlopeBase {
    private static final RailLogicVerticalSlopeUpsideDownA[] values = new RailLogicVerticalSlopeUpsideDownA[4];

    protected RailLogicVerticalSlopeUpsideDownA(BlockFace direction) {
        super(direction, true);
    }

    public static RailLogicVerticalSlopeUpsideDownA get(BlockFace direction) {
        return values[FaceUtil.faceToNotch((BlockFace)direction) >> 1];
    }

    @Override
    protected RailPath createPath() {
        Vector p2;
        Vector p1;
        double base_y = -0.2625;
        switch (this.getDirection()) {
            case NORTH: {
                p1 = new Vector(0.5, base_y + 1.0, 0.0);
                p2 = new Vector(0.5, base_y, 1.0);
                break;
            }
            case EAST: {
                p1 = new Vector(1.0, base_y + 1.0, 0.5);
                p2 = new Vector(0.0, base_y, 0.5);
                break;
            }
            case SOUTH: {
                p1 = new Vector(0.5, base_y + 1.0, 1.0);
                p2 = new Vector(0.5, base_y, 0.0);
                break;
            }
            default: {
                p1 = new Vector(0.0, base_y + 1.0, 0.5);
                p2 = new Vector(1.0, base_y, 0.5);
            }
        }
        if (p2.getY() < 0.0) {
            Vector d = p2.clone().subtract(p1).normalize();
            d.multiply(p2.getY() / d.getY());
            p2.subtract(d);
        }
        return new RailPath.Builder().up(BlockFace.DOWN).add(p1).add(p2).build();
    }

    static {
        for (int i = 0; i < 4; ++i) {
            RailLogicVerticalSlopeUpsideDownA.values[i] = new RailLogicVerticalSlopeUpsideDownA(FaceUtil.notchToFace((int)(i << 1)));
        }
    }
}

