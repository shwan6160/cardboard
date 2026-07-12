/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.block;

import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.HitResultHandle;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class BlockRayTrace {
    private final World world;
    private final Vector from;
    private final Vector to;

    public static BlockRayTrace fromEyeOf(LivingEntity entity) {
        double reach = entity instanceof Player && ((Player)entity).getGameMode() == GameMode.CREATIVE ? 6.0 : 5.0;
        return BlockRayTrace.fromEye(entity.getEyeLocation(), reach);
    }

    public static BlockRayTrace fromEye(Location eyeLocation, double maxDistance) {
        return BlockRayTrace.fromInto(eyeLocation.getWorld(), eyeLocation.toVector(), eyeLocation.getDirection(), maxDistance);
    }

    public static BlockRayTrace fromInto(World world, Vector from, Vector direction, double maxDistance) {
        Vector to = direction.clone().multiply(maxDistance).add(from);
        return new BlockRayTrace(world, from, to);
    }

    public static BlockRayTrace between(World world, Vector from, Vector to) {
        return new BlockRayTrace(world, from, to);
    }

    private BlockRayTrace(World world, Vector from, Vector to) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }
        if (from == null) {
            throw new IllegalArgumentException("From start position cannot be null");
        }
        if (to == null) {
            throw new IllegalArgumentException("To end vector cannot be null");
        }
        this.world = world;
        this.from = from;
        this.to = to;
    }

    public World getWorld() {
        return this.world;
    }

    public Vector getStartPosition() {
        return this.from;
    }

    public Vector getEndPosition() {
        return this.to;
    }

    public Vector getDirection() {
        Vector direction = this.to.clone().subtract(this.from);
        direction.normalize();
        if (Double.isNaN(direction.getX())) {
            direction.setX(0.0);
            direction.setY(-1.0);
            direction.setZ(0.0);
        }
        return direction;
    }

    public double getMaximumDistance() {
        return this.from.distance(this.to);
    }

    public HitResult rayTrace() {
        HitResultHandle mop = LevelHandle.fromBukkit(this.world).rayTrace(this.from, this.to);
        return mop == null ? null : new HitResult(this, mop);
    }

    public static final class HitResult {
        private final BlockRayTrace rayTrace;
        private final Vector absolutePosition;
        private final BlockFace hitFace;
        private final Block block;

        private HitResult(BlockRayTrace rayTrace, HitResultHandle mop) {
            this.rayTrace = rayTrace;
            this.absolutePosition = mop.getPos();
            this.hitFace = mop.getDirection();
            Vector posBeyondBlock = this.absolutePosition.clone();
            MathUtil.nextForward(posBeyondBlock, rayTrace.getDirection());
            this.block = rayTrace.getWorld().getBlockAt(posBeyondBlock.getBlockX(), posBeyondBlock.getBlockY(), posBeyondBlock.getBlockZ());
        }

        public BlockRayTrace getRayTrace() {
            return this.rayTrace;
        }

        public Block getHitBlock() {
            return this.block;
        }

        public BlockFace getHitFace() {
            return this.hitFace;
        }

        public Vector getHitPosition() {
            Block block = this.block;
            Vector pos = this.absolutePosition;
            return new Vector(pos.getX() - (double)block.getX(), pos.getY() - (double)block.getY(), pos.getZ() - (double)block.getZ());
        }

        public Vector getAbsolutePosition() {
            return this.absolutePosition;
        }
    }
}

