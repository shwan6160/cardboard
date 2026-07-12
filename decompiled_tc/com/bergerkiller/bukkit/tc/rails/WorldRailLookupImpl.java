/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  com.bergerkiller.bukkit.common.offline.OfflineWorld
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.rails;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.controller.global.SignControllerWorld;
import com.bergerkiller.bukkit.tc.detector.DetectorRegion;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.rails.WorldRailLookup;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneCache;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneCacheWorld;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

final class WorldRailLookupImpl
implements WorldRailLookup {
    private static final Bucket[] NO_RAILS_AT_POSITION = new Bucket[0];
    private static final TrackedSignList SIGN_LIST_CACHE = new TrackedSignList();
    private static final Material WALL_SIGN_TYPE = MaterialUtil.getMaterial((String)"LEGACY_WALL_SIGN");
    private static final Material SIGN_POST_TYPE = MaterialUtil.getMaterial((String)"LEGACY_SIGN_POST");
    private static BlockFace[] SIGN_FACES_ORDERED = new BlockFace[]{BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.DOWN};
    private final TrainCarts traincarts;
    private World world;
    private OfflineWorld offlineWorld;
    private Map<IntVector3, Bucket> cache;
    private List<Bucket> cacheValues;
    private MutexZoneCacheWorld mutexZones;
    private SignControllerWorld signController;
    private int ticksWithEmptyCache;

    WorldRailLookupImpl(TrainCarts traincarts, World world) {
        this.traincarts = traincarts;
        this.offlineWorld = OfflineWorld.of((World)world);
        this.world = world;
        this.cache = new HashMap<IntVector3, Bucket>();
        this.cacheValues = new ArrayList<Bucket>();
        this.mutexZones = MutexZoneCache.forWorld(this.offlineWorld);
        this.signController = traincarts.getSignController().forWorldSkipInitialization(this.world);
        this.ticksWithEmptyCache = 0;
    }

    void initialize() {
        this.signController.initialize();
        DetectorRegion.fillRailLookup(this);
    }

    @Override
    public World getWorld() {
        World w = this.world;
        return w == null ? this.offlineWorld.getLoadedWorld() : w;
    }

    @Override
    public OfflineWorld getOfflineWorld() {
        return this.offlineWorld;
    }

    @Override
    public MutexZoneCacheWorld getMutexZones() {
        return this.mutexZones;
    }

    @Override
    public SignControllerWorld getSignController() {
        return this.signController;
    }

    @Override
    public boolean isValid() {
        return this.world != null;
    }

    @Override
    public boolean isValidForWorld(World world) {
        return this.world == world;
    }

    boolean checkCanBeRemoved() {
        if (this.offlineWorld.getLoadedWorld() != this.world) {
            return true;
        }
        if (!this.cache.isEmpty()) {
            this.ticksWithEmptyCache = 0;
            return false;
        }
        return ++this.ticksWithEmptyCache > 12000;
    }

    void close() {
        if (!this.cache.isEmpty()) {
            this.forAllBuckets(b -> {
                b.rail_life = 0;
            });
            this.cache.clear();
            this.cacheValues.clear();
        }
        this.cache = Collections.emptyMap();
        this.cacheValues = Collections.emptyList();
        this.world = null;
    }

    @Override
    public RailPiece[] findAtStatePosition(RailState state) {
        RailPath.Position pos = state.position();
        IntVector3 coordinates = pos.relative ? state.railPiece().blockPosition().add(MathUtil.floor((double)pos.posX), MathUtil.floor((double)pos.posY), MathUtil.floor((double)pos.posZ)) : new IntVector3(MathUtil.floor((double)pos.posX), MathUtil.floor((double)pos.posY), MathUtil.floor((double)pos.posZ));
        IntVector3 cacheKey = WorldRailLookupImpl.createCacheKey(coordinates);
        Bucket inCache = this.cache.get(cacheKey);
        if (inCache != null) {
            return inCache.getRailsAtPosition();
        }
        return this.discoverBucketsAtPositionBlock(cacheKey, this.offlineWorld.getBlockAt(coordinates));
    }

    @Override
    public RailPiece[] findAtBlockPosition(OfflineBlock positionBlock) {
        IntVector3 cacheKey = WorldRailLookupImpl.createCacheKey(positionBlock);
        Bucket inCache = this.cache.get(cacheKey);
        if (inCache != null) {
            return inCache.getRailsAtPosition();
        }
        return this.discoverBucketsAtPositionBlock(cacheKey, positionBlock);
    }

    @Override
    public RailLookup.CachedRailPiece lookupCachedRailPieceIfCached(OfflineBlock railOfflineBlock, RailType railType) {
        IntVector3 cacheKey = WorldRailLookupImpl.createCacheKey(railOfflineBlock);
        Bucket inCache = this.cache.get(cacheKey);
        if (inCache != null) {
            RailType inCacheType = inCache.type();
            if (inCacheType == railType) {
                return inCache;
            }
            if (inCacheType != RailType.NONE) {
                while ((inCache = inCache.next) != null) {
                    if (inCache.type() != railType) continue;
                    return inCache;
                }
            }
        }
        return RailLookup.CachedRailPiece.NONE;
    }

    @Override
    public List<RailLookup.CachedRailPiece> lookupCachedRailPieces(OfflineBlock railOfflineBlock) {
        IntVector3 cacheKey = WorldRailLookupImpl.createCacheKey(railOfflineBlock);
        Bucket inCache = this.cache.get(cacheKey);
        if (inCache == null) {
            return Collections.emptyList();
        }
        if (inCache.next == null) {
            return Collections.singletonList(inCache);
        }
        ArrayList<RailLookup.CachedRailPiece> result = new ArrayList<RailLookup.CachedRailPiece>(5);
        result.add(inCache);
        while ((inCache = inCache.next) != null) {
            result.add(inCache);
        }
        return result;
    }

    @Override
    public RailLookup.CachedRailPiece lookupCachedRailPiece(OfflineBlock railOfflineBlock, Block railBlock, RailType railType) {
        return this.lookupRailBucket(railOfflineBlock, railBlock, railType);
    }

    private Bucket lookupRailBucket(OfflineBlock railOfflineBlock, Block railBlock, RailType railType) {
        IntVector3 cacheKey = WorldRailLookupImpl.createCacheKey(railOfflineBlock);
        Bucket inCache = this.cache.get(cacheKey);
        if (inCache == null) {
            if (!railType.isRegistered()) {
                throw new RailLookup.RailTypeNotRegisteredException(railType);
            }
            inCache = new Bucket(railOfflineBlock, railBlock, railType);
            this.addToCache(cacheKey, inCache);
            inCache.signs = RailLookup.discoverSignsAtRailPiece(inCache);
            return inCache;
        }
        RailType inCacheType = inCache.type();
        if (inCacheType == railType) {
            return inCache;
        }
        if (inCacheType == RailType.NONE) {
            return inCache.swapOutNoneType(railType);
        }
        return inCache.findOrAppendToChain(railType);
    }

    @Override
    public List<MinecartMember<?>> findMembersOnRail(IntVector3 railCoordinates) {
        Bucket bucket = this.cache.get(WorldRailLookupImpl.createCacheKey(railCoordinates));
        return bucket == null ? Collections.emptyList() : bucket.members;
    }

    @Override
    public List<MinecartMember<?>> findMembersOnRail(OfflineBlock railOfflineBlock) {
        Bucket bucket = this.cache.get(WorldRailLookupImpl.createCacheKey(railOfflineBlock));
        return bucket == null ? Collections.emptyList() : bucket.members;
    }

    @Override
    public void removeMemberFromAll(MinecartMember<?> member) {
        this.forAllBuckets(b -> {
            List members = b.members;
            if (!members.isEmpty()) {
                members.remove(member);
            }
        });
    }

    @Override
    public RailLookup.TrackedSign[] discoverSignsAtRailPiece(RailPiece rail) {
        TrackedSignList cache = SIGN_LIST_CACHE.start(rail);
        try {
            RailType type = rail.type();
            if (!type.isRegistered()) {
                throw new RailLookup.RailTypeNotRegisteredException(type);
            }
            type.discoverSigns(rail, this.signController, cache.signs);
            RailLookup.TrackedSign[] trackedSignArray = cache.build();
            if (cache != null) {
                cache.close();
            }
            return trackedSignArray;
        }
        catch (Throwable t) {
            try {
                this.traincarts.getLogger().log(Level.SEVERE, "Failed discover signs for " + rail, t);
                RailLookup.TrackedSign[] trackedSignArray = RailLookup.NO_SIGNS;
                return trackedSignArray;
            }
            finally {
                if (cache != null) {
                    cache.close();
                }
            }
        }
    }

    @Override
    public RailPiece discoverRailPieceFromSign(Block signblock) {
        boolean isSignPost;
        Block mainBlock;
        if (signblock == null) {
            return RailPiece.NONE;
        }
        BlockData signblock_data = WorldUtil.getBlockData((Block)signblock);
        if (signblock_data.isType(WALL_SIGN_TYPE)) {
            mainBlock = signblock.getRelative(signblock_data.getAttachedFace());
            isSignPost = false;
        } else if (signblock_data.isType(SIGN_POST_TYPE)) {
            mainBlock = signblock.getRelative(signblock_data.getAttachedFace());
            isSignPost = true;
        } else {
            return RailPiece.NONE;
        }
        RailType railType = RailType.getType(mainBlock);
        if (railType != RailType.NONE) {
            return RailPiece.create(railType, mainBlock);
        }
        block0: for (BlockFace dir : SIGN_FACES_ORDERED) {
            Block block = mainBlock;
            if (isSignPost && dir == BlockFace.DOWN) {
                block = signblock;
            }
            boolean hasSigns = true;
            BlockData blockData;
            BlockFace columnDir;
            while (dir != (columnDir = (railType = RailType.getType(block = block.getRelative(dir), blockData = WorldUtil.getBlockData((Block)block))).getSignColumnDirection(block)).getOppositeFace()) {
                if (!hasSigns) continue block0;
                if (blockData.isType(SIGN_POST_TYPE)) {
                    hasSigns = true;
                    continue;
                }
                hasSigns = this.signController.hasSignsAroundColumn(block, dir.getOppositeFace(), false);
            }
            return RailPiece.create(railType, block);
        }
        return RailPiece.NONE;
    }

    @Override
    public void redetectSignActions() {
        this.forAllBuckets(RailLookup.CachedRailPiece::redetectSignActions);
    }

    private void forAllBuckets(Consumer<Bucket> callback) {
        Iterator<Bucket> iterator = this.cacheValues.iterator();
        while (iterator.hasNext()) {
            Bucket bucket;
            Bucket next = bucket = iterator.next();
            while (next != null) {
                callback.accept(next);
                next = next.next;
            }
        }
    }

    void unloadRailType(RailType type) {
        this.refreshBuckets(bucket -> bucket.type() != type, true);
    }

    void refreshAllBuckets() {
        this.refreshBuckets(bucket -> {
            bucket.rail_life = 1;
            bucket.rails_at_position_life = 0;
            bucket.rails_at_position = NO_RAILS_AT_POSITION;
            bucket.signs = RailLookup.MISSING_RAILS_NO_SIGNS;
            return false;
        }, false);
    }

    void update(int deadTimeout) {
        this.refreshBuckets(b -> b.checkStillValid(deadTimeout), false);
    }

    private void refreshBuckets(Predicate<Bucket> validChecker, boolean ignoreCanBePurged) {
        ListIterator<Bucket> iter = this.cacheValues.listIterator();
        block0: while (iter.hasNext()) {
            Bucket bucket = iter.next();
            if (validChecker.test(bucket) || !ignoreCanBePurged && !bucket.canBePurged(bucket.next == null)) {
                bucket.removeInvalidBucketsFromChain(validChecker, ignoreCanBePurged);
                continue;
            }
            IntVector3 cacheKey = WorldRailLookupImpl.createCacheKey(bucket.blockPosition());
            do {
                bucket.rail_life = 0;
                bucket = bucket.next;
                if (bucket != null) continue;
                iter.remove();
                this.cache.remove(cacheKey);
                continue block0;
            } while (!validChecker.test(bucket) && (ignoreCanBePurged || bucket.canBePurged(true)));
            bucket.removeInvalidBucketsFromChain(validChecker, ignoreCanBePurged);
            iter.set(bucket);
            this.cache.put(cacheKey, bucket);
        }
    }

    @Override
    public void storeDetectorRegions(IntVector3 coordinates, DetectorRegion[] regions) {
        Bucket b = this.getOrCreateAtCoordinates(coordinates);
        while (b != null) {
            b.detectorRegions = regions == null || regions.length == 0 ? RailLookup.NO_DETECTOR_REGIONS : regions;
            b = b.next;
        }
    }

    @Override
    public DetectorRegion[] getDetectorRegions(IntVector3 coordinates) {
        Bucket bucket = this.cache.get(WorldRailLookupImpl.createCacheKey(coordinates));
        return bucket == null ? RailLookup.NO_DETECTOR_REGIONS : bucket.detectorRegions;
    }

    @Override
    public Collection<IntVector3> getBlockIndex() {
        return this.cache.keySet();
    }

    private Bucket getOrCreateAtCoordinates(IntVector3 coordinates) {
        IntVector3 cacheKey = WorldRailLookupImpl.createCacheKey(coordinates);
        Bucket bucket = this.cache.get(cacheKey);
        if (bucket == null) {
            bucket = new Bucket(this.offlineWorld.getBlockAt(coordinates), BlockUtil.getBlock((World)this.world, (IntVector3)coordinates));
            this.cache.put(cacheKey, bucket);
            this.cacheValues.add(bucket);
        }
        return bucket;
    }

    private Bucket[] discoverBucketsAtPositionBlock(IntVector3 cacheKey, OfflineBlock positionOfflineBlock) {
        Block positionBlock = positionOfflineBlock.getLoadedBlock();
        if (positionBlock == null) {
            if (this.isValid()) {
                return NO_RAILS_AT_POSITION;
            }
            throw new WorldRailLookup.ClosedException();
        }
        for (RailType type : RailType.values()) {
            try {
                List<Block> rails = type.findRails(positionBlock);
                if (rails.isEmpty()) continue;
                Bucket bucketInCache = null;
                Bucket[] newRailsAtPosition = new Bucket[rails.size()];
                int index = 0;
                for (Block railsBlock : rails) {
                    if (railsBlock.getX() == positionBlock.getX() && railsBlock.getY() == positionBlock.getY() && railsBlock.getZ() == positionBlock.getZ()) {
                        bucketInCache = new Bucket(positionOfflineBlock, positionBlock, type);
                        newRailsAtPosition[index++] = bucketInCache;
                        continue;
                    }
                    OfflineBlock railsOfflineBlock = this.offlineWorld.getBlockAt(railsBlock.getX(), railsBlock.getY(), railsBlock.getZ());
                    newRailsAtPosition[index++] = this.lookupRailBucket(railsOfflineBlock, railsBlock, type);
                }
                if (bucketInCache == null) {
                    bucketInCache = new Bucket(positionOfflineBlock, positionBlock);
                }
                this.addToCache(cacheKey, bucketInCache);
                bucketInCache.rails_at_position = newRailsAtPosition;
                bucketInCache.signs = RailLookup.discoverSignsAtRailPiece(bucketInCache);
                return newRailsAtPosition;
            }
            catch (Throwable t) {
                RailType.handleCriticalError(type, t);
            }
        }
        this.addToCache(cacheKey, new Bucket(positionOfflineBlock, positionBlock));
        return NO_RAILS_AT_POSITION;
    }

    private void addToCache(IntVector3 cacheKey, Bucket bucket) {
        this.cache.put(cacheKey, bucket);
        this.cacheValues.add(bucket);
    }

    private static IntVector3 createCacheKey(OfflineBlock block) {
        return block.getPosition();
    }

    private static IntVector3 createCacheKey(IntVector3 coordinates) {
        return coordinates;
    }

    private final class Bucket
    extends RailLookup.CachedRailPiece {
        public Bucket next;
        public int rail_life;
        public int rails_at_position_life;
        public Bucket[] rails_at_position;

        public Bucket(OfflineBlock offlineBlock, Block block) {
            this(offlineBlock, block, (RailType)RailType.NONE);
        }

        public Bucket(OfflineBlock offlineBlock, Block block, RailType type) {
            super(WorldRailLookupImpl.this, offlineBlock, block, type);
            this.next = null;
            this.signs = RailLookup.MISSING_RAILS_NO_SIGNS;
            this.rail_life = RailLookup.lifeTimer;
            this.rails_at_position_life = 0;
            this.rails_at_position = NO_RAILS_AT_POSITION;
        }

        public boolean checkStillValid(int timeoutTicks) {
            if (this.rail_life >= timeoutTicks || this.rails_at_position_life >= timeoutTicks) {
                return true;
            }
            List members = this.members;
            if (!members.isEmpty()) {
                Iterator iter = members.iterator();
                while (iter.hasNext()) {
                    MinecartMember member = (MinecartMember)iter.next();
                    if (!member.isUnloaded() && !((CommonMinecart)member.getEntity()).isRemoved()) continue;
                    iter.remove();
                    WorldRailLookupImpl.this.traincarts.log(Level.WARNING, "Purged unloaded minecart from rail cache at " + this.offlineBlock().getPosition());
                }
            }
            return false;
        }

        private boolean canBePurged(boolean isOnlyBucketAtBlock) {
            if (!this.members.isEmpty()) {
                return false;
            }
            return !isOnlyBucketAtBlock || this.detectorRegions == RailLookup.NO_DETECTOR_REGIONS;
        }

        public Bucket swapOutNoneType(RailType railType) {
            Bucket newBucket = this.cloneAsType(railType);
            newBucket.rails_at_position = this.rails_at_position;
            if (this.members.isEmpty()) {
                this.rail_life = 0;
            } else {
                newBucket.next = this;
            }
            boolean found = false;
            ListIterator<Bucket> iter = WorldRailLookupImpl.this.cacheValues.listIterator();
            while (iter.hasNext()) {
                if (iter.next() != this) continue;
                iter.set(newBucket);
                found = true;
                break;
            }
            if (!found) {
                WorldRailLookupImpl.this.cacheValues.add(newBucket);
            }
            WorldRailLookupImpl.this.cache.put(WorldRailLookupImpl.createCacheKey(newBucket.blockPosition()), newBucket);
            return newBucket;
        }

        public Bucket findOrAppendToChain(RailType railType) {
            Bucket current = this;
            while (true) {
                Bucket next;
                if ((next = current.next) == null) {
                    Bucket newBucket;
                    current.next = newBucket = current.cloneAsType(railType);
                    newBucket.signs = RailLookup.discoverSignsAtRailPiece(newBucket);
                    return newBucket;
                }
                if (next.type() == railType) {
                    return next;
                }
                current = next;
            }
        }

        public void removeInvalidBucketsFromChain(Predicate<Bucket> validChecker, boolean ignoreCanBePurged) {
            Bucket next;
            Bucket curr = this;
            while ((next = curr.next) != null) {
                if (validChecker.test(next) || !ignoreCanBePurged && !next.canBePurged(false)) {
                    curr = next;
                    continue;
                }
                next.rail_life = 0;
                curr.next = next.next;
            }
        }

        public Bucket[] getRailsAtPosition() {
            int lifeTimerAtPosition = RailLookup.lifeTimerAtPosition;
            if (this.rails_at_position_life >= lifeTimerAtPosition) {
                return this.rails_at_position;
            }
            this.rails_at_position_life = lifeTimerAtPosition;
            Bucket[] currAtPosition = this.rails_at_position;
            if (currAtPosition.length == 0) {
                return this.computeRailsAtPosition();
            }
            for (Bucket b : currAtPosition) {
                if (b.verify()) continue;
                return this.computeRailsAtPosition();
            }
            return currAtPosition;
        }

        private Bucket cloneAsType(RailType railType) {
            if (!railType.isRegistered()) {
                throw new RailLookup.RailTypeNotRegisteredException(railType);
            }
            Bucket newBucket = new Bucket(this.offlineBlock(), this.block(), railType);
            newBucket.detectorRegions = this.detectorRegions;
            return newBucket;
        }

        private Bucket[] computeRailsAtPosition() {
            OfflineWorld offlineWorld = this.offlineWorld();
            Block positionBlock = this.block();
            Bucket[] newRailsAtPosition = NO_RAILS_AT_POSITION;
            Bucket bucketInCache = this;
            for (RailType type : RailType.values()) {
                try {
                    List<Block> rails = type.findRails(positionBlock);
                    if (rails.isEmpty()) continue;
                    RailType bucketInCacheType = bucketInCache.type();
                    int index = newRailsAtPosition.length;
                    newRailsAtPosition = Arrays.copyOf(newRailsAtPosition, index + rails.size());
                    for (Block railsBlock : rails) {
                        if (railsBlock.getX() == positionBlock.getX() && railsBlock.getY() == positionBlock.getY() && railsBlock.getZ() == positionBlock.getZ()) {
                            if (bucketInCacheType == type) {
                                newRailsAtPosition[index++] = bucketInCache;
                                continue;
                            }
                            if (bucketInCacheType == RailType.NONE) {
                                bucketInCache = bucketInCache.swapOutNoneType(type);
                                bucketInCacheType = type;
                                newRailsAtPosition[index++] = bucketInCache;
                                continue;
                            }
                            newRailsAtPosition[index++] = bucketInCache.findOrAppendToChain(type);
                            continue;
                        }
                        OfflineBlock railsOfflineBlock = offlineWorld.getBlockAt(railsBlock.getX(), railsBlock.getY(), railsBlock.getZ());
                        newRailsAtPosition[index++] = WorldRailLookupImpl.this.lookupRailBucket(railsOfflineBlock, railsBlock, type);
                    }
                }
                catch (Throwable t) {
                    RailType.handleCriticalError(type, t);
                }
            }
            bucketInCache.rails_at_position = newRailsAtPosition;
            return newRailsAtPosition;
        }

        @Override
        public boolean verify() {
            int currLife = this.rail_life;
            if (currLife >= RailLookup.lifeTimer) {
                return true;
            }
            if (currLife == 0) {
                return false;
            }
            if (!this.type().isRail(this.block())) {
                this.signs = RailLookup.MISSING_RAILS_NO_SIGNS;
                this.rail_life = 1;
                return false;
            }
            this.rail_life = RailLookup.verifyTimer;
            RailLookup.TrackedSign[] signs = this.signs;
            if (signs == RailLookup.MISSING_RAILS_NO_SIGNS) {
                this.signs = RailLookup.discoverSignsAtRailPiece(this);
            } else {
                for (RailLookup.TrackedSign sign : signs) {
                    if (sign.verify()) continue;
                    this.signs = RailLookup.discoverSignsAtRailPiece(this);
                    break;
                }
            }
            return true;
        }

        @Override
        public boolean verifyExists() {
            return this.rail_life != 0;
        }

        @Override
        public void forceCacheVerification() {
            this.rail_life = 1;
            this.signs = RailLookup.MISSING_RAILS_NO_SIGNS;
        }
    }

    private static final class TrackedSignList
    implements AutoCloseable {
        private final List<RailLookup.TrackedSign> signs = new ArrayList<RailLookup.TrackedSign>();
        private RailPiece rail = null;

        private TrackedSignList() {
        }

        public TrackedSignList start(RailPiece rail) {
            if (this.rail == null) {
                this.rail = rail;
                return this;
            }
            TrackedSignList copy = new TrackedSignList();
            copy.rail = rail;
            return copy;
        }

        @Override
        public void close() {
            this.signs.clear();
            this.rail = null;
        }

        public RailLookup.TrackedSign[] build() {
            List<RailLookup.TrackedSign> signs = this.signs;
            return signs.isEmpty() ? RailLookup.NO_SIGNS : signs.toArray(new RailLookup.TrackedSign[signs.size()]);
        }
    }
}

