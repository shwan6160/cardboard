/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  com.bergerkiller.bukkit.common.offline.OfflineWorld
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  org.bukkit.World
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailJunction;
import com.bergerkiller.bukkit.tc.detector.DetectorRegion;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.rails.WorldRailLookup;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.utils.RailJunctionSwitcher;
import java.util.List;
import java.util.function.Predicate;
import org.bukkit.World;
import org.bukkit.block.Block;

public class RailPiece {
    public static final RailPiece NONE = new RailPieceNone();
    private final RailType type;
    private final OfflineBlock offlineBlock;
    private WorldRailLookup railLookup;
    private Block block;
    protected RailLookup.CachedRailPiece cached;

    protected RailPiece() {
        this.railLookup = WorldRailLookup.NONE;
        this.offlineBlock = null;
        this.block = null;
        this.type = RailType.NONE;
        this.cached = null;
    }

    private RailPiece(WorldRailLookup railLookup) {
        this.railLookup = railLookup;
        this.offlineBlock = null;
        this.block = null;
        this.type = RailType.NONE;
        this.cached = RailLookup.CachedRailPiece.NONE;
    }

    protected RailPiece(WorldRailLookup railLookup, OfflineBlock offlineBlock, Block block, RailType type) {
        this.railLookup = railLookup;
        this.offlineBlock = offlineBlock;
        this.block = block;
        this.type = type;
        this.cached = RailLookup.CachedRailPiece.NONE;
    }

    public RailType type() {
        return this.type;
    }

    public Block block() {
        this.railLookup();
        return this.block;
    }

    public OfflineBlock offlineBlock() {
        return this.offlineBlock;
    }

    public IntVector3 blockPosition() {
        try {
            return this.offlineBlock.getPosition();
        }
        catch (NullPointerException ex) {
            if (this.offlineBlock == null) {
                throw new IllegalStateException("This rail piece is a world placeholder and has no rail block");
            }
            throw ex;
        }
    }

    public boolean isSameBlock(RailPiece piece) {
        return this.offlineBlock.equals((Object)piece.offlineBlock);
    }

    public boolean isNone() {
        this.railLookup();
        return this.block == null;
    }

    public boolean hasBlockActivation() {
        return this.type.hasBlockActivation(this.block);
    }

    public List<RailJunction> getJunctions() {
        return this.type.getJunctions(this.block);
    }

    public void switchJunction(RailJunction from, RailJunction to) {
        new RailJunctionSwitcher(this).switchJunction(from, to);
    }

    public void switchJunction(RailJunction from, RailJunction to, Predicate<MinecartMember<?>> memberFilter) {
        new RailJunctionSwitcher(this, memberFilter).switchJunction(from, to);
    }

    public World world() {
        return this.railLookup.getWorld();
    }

    public OfflineWorld offlineWorld() {
        return this.railLookup.getOfflineWorld();
    }

    public WorldRailLookup railLookup() {
        WorldRailLookup lookup = this.railLookup;
        if (!lookup.isValid()) {
            if ((lookup = RailLookup.forWorld(lookup.getOfflineWorld().getLoadedWorld())).isValid()) {
                this.railLookup = lookup;
                this.block = BlockUtil.getBlock((World)lookup.getWorld(), (IntVector3)this.offlineBlock.getPosition());
            } else {
                this.block = null;
            }
        }
        return lookup;
    }

    protected RailLookup.CachedRailPiece accessCache() {
        RailLookup.CachedRailPiece cached = this.cached;
        if (cached.verify()) {
            return cached;
        }
        this.cached = this.railLookup().lookupCachedRailPiece(this.offlineBlock, this.block, this.type);
        return this.cached;
    }

    protected RailLookup.CachedRailPiece accessCacheExists() {
        RailLookup.CachedRailPiece cached = this.cached;
        if (cached.verifyExists()) {
            return cached;
        }
        this.cached = this.railLookup().lookupCachedRailPiece(this.offlineBlock, this.block, this.type);
        return this.cached;
    }

    public void forceCacheVerification() {
        RailLookup.CachedRailPiece cached = this.cached;
        if (!cached.verifyExists()) {
            this.cached = cached = this.railLookup().lookupCachedRailPieceIfCached(this.offlineBlock, this.type);
        }
        cached.forceCacheVerification();
    }

    public RailLookup.TrackedSign[] signs() {
        return this.accessCache().cachedSigns();
    }

    public void redetectSignActions() {
        RailLookup.CachedRailPiece cached = this.cached;
        if (cached != null) {
            cached.redetectSignActions();
        }
    }

    public DetectorRegion[] detectorRegions() {
        return this.accessCacheExists().cachedDetectorRegions();
    }

    public List<MinecartMember<?>> members() {
        return this.accessCache().cachedMembers();
    }

    public List<MinecartMember<?>> mutableMembers() {
        return this.accessCache().cachedMutableMembers();
    }

    public RailPiece asNoneType() {
        return new RailPiece(this.railLookup, this.offlineBlock, this.block, RailType.NONE);
    }

    public int hashCode() {
        return this.offlineBlock == null ? 0 : this.offlineBlock.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RailPiece) {
            RailPiece other = (RailPiece)o;
            if (this.offlineBlock != null) {
                return this.offlineBlock.equals((Object)other.offlineBlock) && this.type == other.type;
            }
            return other.offlineBlock == null && this.type == other.type;
        }
        return false;
    }

    public String toString() {
        if (this.block == null) {
            return "{" + this.type + " ?/?/?}";
        }
        return "{" + this.type + " " + this.block.getX() + "/" + this.block.getY() + "/" + this.block.getZ() + "}";
    }

    public static RailPiece create(RailType type, Block block) {
        WorldRailLookup railLookup = RailLookup.forWorld(block.getWorld());
        OfflineBlock offlineBlock = railLookup.getOfflineWorld().getBlockAt(block.getX(), block.getY(), block.getZ());
        return new RailPiece(railLookup, offlineBlock, block, type);
    }

    public static RailPiece create(RailType type, Block block, WorldRailLookup railLookup) {
        OfflineBlock offlineBlock = railLookup.getOfflineWorld().getBlockAt(block.getX(), block.getY(), block.getZ());
        return new RailPiece(railLookup, offlineBlock, block, type);
    }

    public static RailPiece create(Block block) {
        return RailPiece.create(RailType.getType(block), block);
    }

    public static RailPiece createWorldPlaceholder(World world) {
        return new RailPiece(RailLookup.forWorld(world));
    }

    public static RailPiece createWorldPlaceholder(WorldRailLookup railLookup) {
        return new RailPiece(railLookup);
    }

    private static class RailPieceNone
    extends RailPiece {
        public RailPieceNone() {
            super(WorldRailLookup.NONE);
        }

        @Override
        public boolean isNone() {
            return true;
        }

        @Override
        public WorldRailLookup railLookup() {
            return WorldRailLookup.NONE;
        }

        @Override
        public Block block() {
            return null;
        }

        @Override
        public IntVector3 blockPosition() {
            throw new IllegalStateException("This rail piece is a NONE and has no rail block");
        }

        @Override
        protected RailLookup.CachedRailPiece accessCache() {
            throw new IllegalStateException("This rail piece is a NONE and has no metadata");
        }

        @Override
        protected RailLookup.CachedRailPiece accessCacheExists() {
            throw new IllegalStateException("This rail piece is a NONE and has no metadata");
        }

        @Override
        public void forceCacheVerification() {
        }
    }
}

