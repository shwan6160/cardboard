/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.controller.player.pmc;

import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.controller.player.pmc.BlockShapeProvider;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import org.bukkit.block.BlockFace;

class SweptAABB {
    SweptAABB() {
    }

    public static CollisionResult sweepTest(AABBHandle movingBoxStart, AABBHandle movingBoxEnd, AABBHandle blockBox, int bx, int by, int bz) {
        double exitTime;
        double vx = movingBoxEnd.getMinX() - movingBoxStart.getMinX();
        double vy = movingBoxEnd.getMinY() - movingBoxStart.getMinY();
        double vz = movingBoxEnd.getMinZ() - movingBoxStart.getMinZ();
        double[] minMoving = new double[]{movingBoxStart.getMinX(), movingBoxStart.getMinY(), movingBoxStart.getMinZ()};
        double[] maxMoving = new double[]{movingBoxStart.getMaxX(), movingBoxStart.getMaxY(), movingBoxStart.getMaxZ()};
        double[] minBlock = new double[]{blockBox.getMinX(), blockBox.getMinY(), blockBox.getMinZ()};
        double[] maxBlock = new double[]{blockBox.getMaxX(), blockBox.getMaxY(), blockBox.getMaxZ()};
        double[] v = new double[]{vx, vy, vz};
        double[] entry = new double[3];
        double[] exit = new double[3];
        for (int axis = 0; axis < 3; ++axis) {
            if (v[axis] > 0.0) {
                entry[axis] = (minBlock[axis] - maxMoving[axis]) / v[axis];
                exit[axis] = (maxBlock[axis] - minMoving[axis]) / v[axis];
                continue;
            }
            if (v[axis] < 0.0) {
                entry[axis] = (maxBlock[axis] - minMoving[axis]) / v[axis];
                exit[axis] = (minBlock[axis] - maxMoving[axis]) / v[axis];
                continue;
            }
            entry[axis] = Double.NEGATIVE_INFINITY;
            exit[axis] = Double.POSITIVE_INFINITY;
        }
        double entryTime = Math.max(Math.max(entry[0], entry[1]), entry[2]);
        if (entryTime > (exitTime = Math.min(Math.min(exit[0], exit[1]), exit[2])) || entryTime < 0.0 || entryTime > 1.0) {
            return null;
        }
        BlockFace hitFace = entryTime == entry[0] ? (vx > 0.0 ? BlockFace.WEST : BlockFace.EAST) : (entryTime == entry[1] ? (vy > 0.0 ? BlockFace.DOWN : BlockFace.UP) : (vz > 0.0 ? BlockFace.NORTH : BlockFace.SOUTH));
        double minX = movingBoxStart.getMinX() + (movingBoxEnd.getMinX() - movingBoxStart.getMinX()) * entryTime;
        double minY = movingBoxStart.getMinY() + (movingBoxEnd.getMinY() - movingBoxStart.getMinY()) * entryTime;
        double minZ = movingBoxStart.getMinZ() + (movingBoxEnd.getMinZ() - movingBoxStart.getMinZ()) * entryTime;
        double maxX = movingBoxStart.getMaxX() + (movingBoxEnd.getMaxX() - movingBoxStart.getMaxX()) * entryTime;
        double maxY = movingBoxStart.getMaxY() + (movingBoxEnd.getMaxY() - movingBoxStart.getMaxY()) * entryTime;
        double maxZ = movingBoxStart.getMaxZ() + (movingBoxEnd.getMaxZ() - movingBoxStart.getMaxZ()) * entryTime;
        AABBHandle movingAtCollision = AABBHandle.createNew((double)minX, (double)minY, (double)minZ, (double)maxX, (double)maxY, (double)maxZ);
        return new CollisionResult(blockBox, bx, by, bz, entryTime, hitFace, movingAtCollision);
    }

    public static CollisionResult findFirstBlockCollision(AABBHandle movingBoxStart, AABBHandle movingBoxEnd, BlockShapeProvider provider, AttachmentViewer viewer) {
        int minX = MathUtil.floor((double)Math.min(movingBoxStart.getMinX(), movingBoxEnd.getMinX()));
        int minY = MathUtil.floor((double)Math.min(movingBoxStart.getMinY(), movingBoxEnd.getMinY()));
        int minZ = MathUtil.floor((double)Math.min(movingBoxStart.getMinZ(), movingBoxEnd.getMinZ()));
        int maxX = MathUtil.ceil((double)Math.max(movingBoxStart.getMaxX(), movingBoxEnd.getMaxX()));
        int maxY = MathUtil.ceil((double)Math.max(movingBoxStart.getMaxY(), movingBoxEnd.getMaxY()));
        int maxZ = MathUtil.ceil((double)Math.max(movingBoxStart.getMaxZ(), movingBoxEnd.getMaxZ()));
        BestCollisionResult best = new BestCollisionResult();
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    AABBHandle blockBox = provider.getShape(x, y, z);
                    if (blockBox == null) continue;
                    best.promote(SweptAABB.sweepTest(movingBoxStart, movingBoxEnd, blockBox, x, y, z));
                }
            }
        }
        viewer.forAllStationaryCollisionElements(minX, minY, minZ, maxX, maxY, maxZ, element -> {
            AABBHandle blockBox = element.getBoundingBox();
            best.promote(SweptAABB.sweepTest(movingBoxStart, movingBoxEnd, blockBox, 0, 0, 0));
        });
        return best.get();
    }

    public static class CollisionResult {
        public final AABBHandle block;
        public final int blockX;
        public final int blockY;
        public final int blockZ;
        public final double theta;
        public final BlockFace face;
        public final AABBHandle movingAtCollision;

        public CollisionResult(AABBHandle block, int bx, int by, int bz, double theta, BlockFace face, AABBHandle movingAtCollision) {
            this.block = block;
            this.blockX = bx;
            this.blockY = by;
            this.blockZ = bz;
            this.theta = theta;
            this.face = face;
            this.movingAtCollision = movingAtCollision;
        }

        public String toString() {
            return "CollisionResult{block=[" + this.blockX + "," + this.blockY + "," + this.blockZ + "], block_bbox=" + this.block + ", theta=" + this.theta + ", face=" + this.face + "}";
        }
    }

    private static class BestCollisionResult {
        private CollisionResult best = null;

        private BestCollisionResult() {
        }

        public CollisionResult get() {
            return this.best;
        }

        public void promote(CollisionResult result) {
            if (result != null && (this.best == null || result.theta < this.best.theta)) {
                this.best = result;
            }
        }
    }
}

