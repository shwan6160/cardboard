/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.rails.logic;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicHorizontal;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class RailLogicSloped
extends RailLogicHorizontal {
    private static final RailLogicSloped[] values = new RailLogicSloped[4];
    private static final RailLogicSloped[] values_upsideDown = new RailLogicSloped[4];
    protected final double step;

    protected RailLogicSloped(BlockFace direction) {
        this(direction, false);
    }

    protected RailLogicSloped(BlockFace direction, boolean upsideDown) {
        super(direction, upsideDown);
        this.step = direction == BlockFace.SOUTH || direction == BlockFace.EAST ? 1.0 : -1.0;
    }

    public static RailLogicSloped get(BlockFace direction) {
        return values[FaceUtil.faceToNotch((BlockFace)direction) >> 1];
    }

    public static RailLogicSloped get(BlockFace direction, boolean upsideDown) {
        if (upsideDown) {
            return values_upsideDown[FaceUtil.faceToNotch((BlockFace)direction) >> 1];
        }
        return values[FaceUtil.faceToNotch((BlockFace)direction) >> 1];
    }

    @Override
    public boolean isSloped() {
        return true;
    }

    @Override
    protected RailPath createPath() {
        Vector p2;
        Vector p1;
        double base_y = this.isUpsideDown() ? -0.2625 : 0.0625;
        switch (this.getDirection()) {
            case NORTH: {
                p1 = new Vector(0.5, base_y + 1.0, 0.0);
                p2 = new Vector(0.5, base_y, 1.0);
                break;
            }
            case EAST: {
                p1 = new Vector(0.0, base_y, 0.5);
                p2 = new Vector(1.0, base_y + 1.0, 0.5);
                break;
            }
            case SOUTH: {
                p1 = new Vector(0.5, base_y, 0.0);
                p2 = new Vector(0.5, base_y + 1.0, 1.0);
                break;
            }
            default: {
                p1 = new Vector(0.0, base_y + 1.0, 0.5);
                p2 = new Vector(1.0, base_y, 0.5);
            }
        }
        this.getFixedPosition(p1, IntVector3.ZERO);
        this.getFixedPosition(p2, IntVector3.ZERO);
        return new RailPath.Builder().up(this.isUpsideDown() ? BlockFace.DOWN : BlockFace.UP).add(p1).add(p2).build();
    }

    @Override
    public void onPreMove(MinecartMember<?> member) {
        super.onPreMove(member);
        if (this.checkSlopeBlockCollisions()) {
            Block above;
            CommonMinecart entity = (CommonMinecart)member.getEntity();
            Block inside = member.getRailType().findMinecartPos(member.getBlock());
            double blockedDistance = Double.MAX_VALUE;
            Block heading = inside.getRelative(this.getDirection().getOppositeFace());
            if (!member.isMoving() || member.isHeadingTo(this.getDirection().getOppositeFace())) {
                if (BlockUtil.isSuffocating((Block)heading)) {
                    blockedDistance = entity.loc.xz.distance(heading) - 1.0;
                }
            } else if (member.isHeadingTo(this.getDirection()) && BlockUtil.isSuffocating((Block)(above = inside.getRelative(BlockFace.UP)))) {
                blockedDistance = entity.loc.xz.distance(above);
            }
            if (entity.vel.xz.length() > blockedDistance) {
                member.getGroup().setForwardForce(blockedDistance);
            }
        }
    }

    protected boolean checkSlopeBlockCollisions() {
        return true;
    }

    static {
        for (int i = 0; i < 4; ++i) {
            RailLogicSloped.values[i] = new RailLogicSloped(FaceUtil.notchToFace((int)(i << 1)), false);
            RailLogicSloped.values_upsideDown[i] = new RailLogicSloped(FaceUtil.notchToFace((int)(i << 1)), true);
        }
    }
}

