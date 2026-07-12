/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.rails.logic;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogic;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class RailLogicHorizontal
extends RailLogic {
    private static final RailLogicHorizontal[] values = new RailLogicHorizontal[8];
    private static final RailLogicHorizontal[] values_upsidedown = new RailLogicHorizontal[8];
    private final boolean upside_down;
    protected final double dx;
    protected final double dz;
    protected final double startX;
    protected final double startZ;
    private final BlockFace horizontalCartDir;
    private final BlockFace[] cartFaces;
    private final BlockFace[] faces;
    private final BlockFace[] ends;
    public static final double Y_POS_OFFSET = 0.0625;
    public static final double Y_POS_OFFSET_UPSIDEDOWN = -0.0625;
    public static final double Y_POS_OFFSET_UPSIDEDOWN_SLOPE = -0.2;

    protected RailLogicHorizontal(BlockFace direction) {
        this(direction, false);
    }

    protected RailLogicHorizontal(BlockFace direction, boolean upsideDown) {
        super(direction);
        this.horizontalCartDir = FaceUtil.getRailsCartDirection((BlockFace)direction);
        this.upside_down = upsideDown;
        this.cartFaces = FaceUtil.getFaces((BlockFace)this.getCartDirection());
        this.ends = FaceUtil.getFaces((BlockFace)direction.getOppositeFace());
        direction = FaceUtil.toRailsDirection((BlockFace)direction);
        if (this.curved) {
            this.dx = 0.5 * (double)direction.getModX();
            this.dz = -0.5 * (double)direction.getModZ();
            direction = direction.getOppositeFace();
        } else {
            this.dx = direction.getModX();
            this.dz = direction.getModZ();
        }
        this.faces = FaceUtil.getFaces((BlockFace)direction);
        double startFactor = MathUtil.invert((double)0.5, (!this.curved ? 1 : 0) != 0);
        this.startX = startFactor * (double)this.faces[0].getModX();
        this.startZ = startFactor * (double)this.faces[0].getModZ();
        for (int i = 0; i < this.faces.length; ++i) {
            if (this.faces[i] != BlockFace.NORTH && this.faces[i] != BlockFace.SOUTH) continue;
            this.faces[i] = this.faces[i].getOppositeFace();
        }
    }

    public BlockFace getCartDirection() {
        return this.horizontalCartDir;
    }

    public static RailLogicHorizontal get(BlockFace direction) {
        return values[FaceUtil.faceToNotch((BlockFace)direction)];
    }

    public static RailLogicHorizontal get(BlockFace direction, boolean upsideDown) {
        if (upsideDown) {
            return values_upsidedown[FaceUtil.faceToNotch((BlockFace)direction)];
        }
        return values[FaceUtil.faceToNotch((BlockFace)direction)];
    }

    @Override
    protected RailPath createPath() {
        double base_y = this.isUpsideDown() ? -0.0625 : 0.0625;
        Vector p1 = new Vector(this.startX + 0.5, base_y, this.startZ + 0.5);
        Vector p2 = p1.clone();
        if (this.alongZ) {
            p2.setZ(p2.getZ() + this.dz);
        } else if (this.alongX) {
            p2.setX(p2.getX() + this.dx);
        } else {
            p2.setX(p2.getX() - this.dx);
            p2.setZ(p2.getZ() - this.dz);
        }
        this.getFixedPosition(p1, IntVector3.ZERO);
        this.getFixedPosition(p2, IntVector3.ZERO);
        return new RailPath.Builder().up(this.isUpsideDown() ? BlockFace.DOWN : BlockFace.UP).add(p1).add(p2).build();
    }

    @Override
    public boolean isUpsideDown() {
        return this.upside_down;
    }

    @Deprecated
    public void getFixedPosition(Vector position, IntVector3 railPos) {
    }

    @Override
    public void onPathAdjust(RailState state) {
        BlockFace enterFaceRot;
        if (this.isSloped() && ((enterFaceRot = state.enterFace()) == FaceUtil.rotate((BlockFace)this.horizontalCartDir, (int)2) || enterFaceRot == FaceUtil.rotate((BlockFace)this.horizontalCartDir, (int)-2))) {
            state.position().setMotion(this.horizontalCartDir.getOppositeFace());
        }
    }

    @Override
    public BlockFace getMovementDirection(BlockFace endDirection) {
        BlockFace direction;
        BlockFace raildirection = this.getDirection();
        if (this.isSloped()) {
            direction = endDirection == raildirection || endDirection == BlockFace.UP ? raildirection : raildirection.getOppositeFace();
        } else if (this.curved) {
            BlockFace targetFace = endDirection == this.ends[0] || endDirection == this.ends[1].getOppositeFace() ? this.ends[0] : this.ends[1];
            direction = this.getCartDirection();
            if (!LogicUtil.contains((Object)targetFace, (Object[])this.cartFaces)) {
                direction = direction.getOppositeFace();
            }
        } else {
            direction = endDirection == raildirection.getOppositeFace() ? raildirection.getOppositeFace() : raildirection;
        }
        return direction;
    }

    static {
        for (int i = 0; i < 8; ++i) {
            RailLogicHorizontal.values[i] = new RailLogicHorizontal(FaceUtil.notchToFace((int)i), false);
            RailLogicHorizontal.values_upsidedown[i] = new RailLogicHorizontal(FaceUtil.notchToFace((int)i), true);
        }
    }
}

