/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailAABB;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.rails.WorldRailLookup;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogic;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class RailState {
    private RailPiece _railPiece = RailPiece.NONE;
    private final Vector _enterDirection = new Vector(Double.NaN, Double.NaN, Double.NaN);
    private final Vector _enterPosition = new Vector(Double.NaN, Double.NaN, Double.NaN);
    private MinecartMember<?> _member = null;
    private final RailPath.Position _position = new RailPath.Position();

    public RailState() {
        this._position.relative = false;
    }

    public void setTo(RailState state) {
        state.position().copyTo(this.position());
        this.setRailPiece(state.railPiece());
        this.setMember(state.member());
        this._enterDirection.setX(state._enterDirection.getX());
        this._enterDirection.setY(state._enterDirection.getY());
        this._enterDirection.setZ(state._enterDirection.getZ());
        this._enterPosition.setX(state._enterPosition.getX());
        this._enterPosition.setY(state._enterPosition.getY());
        this._enterPosition.setZ(state._enterPosition.getZ());
    }

    public RailPath.Position position() {
        return this._position;
    }

    public void setPosition(RailPath.Position position) {
        position.copyTo(this._position);
    }

    public Block positionBlock() {
        if (this._position.relative) {
            Block railBlock = this.railBlock();
            if (railBlock == null) {
                throw new IllegalStateException("Rails Block must be set before positionBlock can be obtained");
            }
            return railBlock.getWorld().getBlockAt(railBlock.getX() + MathUtil.floor((double)this._position.posX), railBlock.getY() + MathUtil.floor((double)this._position.posY), railBlock.getZ() + MathUtil.floor((double)this._position.posZ));
        }
        World railWorld = this.railWorld();
        if (railWorld == null) {
            throw new IllegalStateException("Rails Block or World must be set before positionBlock can be obtained");
        }
        return railWorld.getBlockAt(MathUtil.floor((double)this._position.posX), MathUtil.floor((double)this._position.posY), MathUtil.floor((double)this._position.posZ));
    }

    public Location positionLocation() {
        if (this._position.relative) {
            Block railBlock = this.railBlock();
            if (railBlock == null) {
                throw new IllegalStateException("Rails Block must be set before positionLocation can be obtained");
            }
            return new Location(railBlock.getWorld(), (double)railBlock.getX() + this._position.posX, (double)railBlock.getY() + this._position.posY, (double)railBlock.getZ() + this._position.posZ, MathUtil.getLookAtYaw((double)this._position.motX, (double)this._position.motZ), MathUtil.getLookAtPitch((double)this._position.motX, (double)this._position.motY, (double)this._position.motZ));
        }
        World railWorld = this.railWorld();
        if (railWorld == null) {
            throw new IllegalStateException("Rails Block or World must be set before positionLocation can be obtained");
        }
        return new Location(railWorld, this._position.posX, this._position.posY, this._position.posZ, MathUtil.getLookAtYaw((double)this._position.motX, (double)this._position.motZ), MathUtil.getLookAtPitch((double)this._position.motX, (double)this._position.motY, (double)this._position.motZ));
    }

    public OfflineBlock positionOfflineBlock() {
        RailPath.Position pos = this._position;
        if (pos.relative) {
            OfflineBlock railBlock = this._railPiece.offlineBlock();
            return railBlock.getRelative(MathUtil.floor((double)pos.posX), MathUtil.floor((double)pos.posY), MathUtil.floor((double)pos.posZ));
        }
        return this._railPiece.offlineWorld().getBlockAt(MathUtil.floor((double)pos.posX), MathUtil.floor((double)pos.posY), MathUtil.floor((double)pos.posZ));
    }

    public Vector motionVector() {
        return this._position.getMotion();
    }

    public void setMotionVector(Vector motionVector) {
        this._position.setMotion(motionVector);
    }

    public RailPiece railPiece() {
        return this._railPiece;
    }

    public void setRailPiece(RailPiece railPiece) {
        this._railPiece = railPiece;
    }

    public final World railWorld() {
        return this._railPiece.world();
    }

    public final WorldRailLookup railLookup() {
        return this._railPiece.railLookup();
    }

    public final Block railBlock() {
        return this._railPiece.block();
    }

    public final RailType railType() {
        return this._railPiece.type();
    }

    public final RailLookup.TrackedSign[] railSigns() {
        return this._railPiece.signs();
    }

    @Deprecated
    public void setRailBlock(Block railsBlock) {
        this.setRailPiece(RailPiece.create(this._railPiece.type(), railsBlock));
    }

    @Deprecated
    public void setRailType(RailType type) {
        if (this._railPiece.type() == type) {
            return;
        }
        this.setRailPiece(RailPiece.create(type, this._railPiece.block()));
    }

    public Vector railPosition() {
        if (this._position.relative) {
            return new Vector(this._position.posX, this._position.posY, this._position.posZ);
        }
        Block railBlock = this.railBlock();
        if (railBlock == null) {
            throw new IllegalStateException("Rails Block must be set before relative railPosition can be obtained");
        }
        return new Vector(this._position.posX - (double)railBlock.getX(), this._position.posY - (double)railBlock.getY(), this._position.posZ - (double)railBlock.getZ());
    }

    public boolean hasEnterDirection() {
        return !Double.isNaN(this._enterDirection.getX());
    }

    public Vector enterDirection() {
        if (!this.hasEnterDirection()) {
            throw new IllegalStateException("Enter direction has not been initialized");
        }
        return this._enterDirection;
    }

    public Vector enterPosition() {
        if (!this.hasEnterDirection()) {
            throw new IllegalStateException("Enter direction has not been initialized");
        }
        return this._enterPosition;
    }

    public void initEnterDirection() {
        if (Double.isNaN(this._position.motX)) {
            throw new IllegalStateException("Position motion vector is NaN");
        }
        this._enterDirection.setX(this._position.motX);
        this._enterDirection.setY(this._position.motY);
        this._enterDirection.setZ(this._position.motZ);
        if (this._position.relative) {
            this._enterPosition.setX(this._position.posX + (double)this._railPiece.block().getX());
            this._enterPosition.setY(this._position.posY + (double)this._railPiece.block().getY());
            this._enterPosition.setZ(this._position.posZ + (double)this._railPiece.block().getZ());
        } else {
            this._enterPosition.setX(this._position.posX);
            this._enterPosition.setY(this._position.posY);
            this._enterPosition.setZ(this._position.posZ);
        }
    }

    public BlockFace enterFace() {
        Vector d = this.enterDirection();
        double ls = d.lengthSquared();
        if (ls < 1.0E-20) {
            return BlockFace.DOWN;
        }
        if (ls == d.getX() * d.getX()) {
            return d.getX() >= 0.0 ? BlockFace.EAST : BlockFace.WEST;
        }
        if (ls == d.getZ() * d.getZ()) {
            return d.getZ() >= 0.0 ? BlockFace.SOUTH : BlockFace.NORTH;
        }
        if (ls == d.getY() * d.getY()) {
            return d.getY() >= 0.0 ? BlockFace.UP : BlockFace.DOWN;
        }
        Vector p = this._enterPosition;
        Vector pos = new Vector(p.getX() - (double)p.getBlockX(), p.getY() - (double)p.getBlockY(), p.getZ() - (double)p.getBlockZ());
        return RailAABB.BLOCK.calculateEnterFace(pos, d);
    }

    public MinecartMember<?> member() {
        return this._member;
    }

    public void setMember(MinecartMember<?> member) {
        this._member = member;
    }

    public boolean isSameRails(RailState other) {
        return this.railPiece().equals(other.railPiece());
    }

    public RailLogic loadRailLogic() {
        RailLogic logic = this.railType().getLogic(this);
        logic.onPathAdjust(this);
        return logic;
    }

    public RailState clone() {
        RailState state = new RailState();
        state.setTo(this);
        return state;
    }

    public RailState cloneAndInvertMotion() {
        RailState reverse = this.clone();
        reverse.position().invertMotion();
        return reverse;
    }

    public String toString() {
        String pos_str = this._position.toString();
        pos_str = pos_str.substring(1, pos_str.length() - 1);
        return "{rail=" + this._railPiece.toString() + ", " + pos_str + "}";
    }

    @Deprecated
    public static RailState getSpawnState(RailType railType, Block railBlock) {
        return RailState.getSpawnState(RailPiece.create(railType, railBlock));
    }

    public static RailState getSpawnState(RailPiece railPiece) {
        RailState state = new RailState();
        state.setRailPiece(railPiece);
        state.position().setLocation(railPiece.type().getSpawnLocation(railPiece.block(), BlockFace.NORTH));
        RailType.loadRailInformation(state);
        state.loadRailLogic().getPath().snap(state.position(), state.railBlock());
        return state;
    }
}

