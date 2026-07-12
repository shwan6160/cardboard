/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.block.SignChangeTracker
 *  com.bergerkiller.bukkit.common.chunk.ChunkFutureProvider
 *  com.bergerkiller.bukkit.common.chunk.ChunkFutureProvider$ChunkNeighbourList
 *  com.bergerkiller.bukkit.common.chunk.ChunkFutureProvider$ChunkStateListener
 *  com.bergerkiller.bukkit.common.chunk.ChunkFutureProvider$ChunkStateTracker
 *  com.bergerkiller.bukkit.common.offline.OfflineWorld
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.ChunkUtil
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  com.bergerkiller.bukkit.common.wrappers.LongHashMap
 *  org.bukkit.Chunk
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Sign
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.controller.global;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.block.SignChangeTracker;
import com.bergerkiller.bukkit.common.chunk.ChunkFutureProvider;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.ChunkUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.global.SignController;
import com.bergerkiller.bukkit.tc.controller.global.SignControllerChunk;
import com.bergerkiller.bukkit.tc.utils.LongBlockCoordinates;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.LongUnaryOperator;
import java.util.function.Predicate;
import java.util.logging.Level;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class SignControllerWorld {
    private static final Material WALL_SIGN_TYPE = MaterialUtil.getMaterial((String)"LEGACY_WALL_SIGN");
    private static final Material SIGN_POST_TYPE = MaterialUtil.getMaterial((String)"LEGACY_SIGN_POST");
    private final SignController controller;
    private final World world;
    private final OfflineWorld offlineWorld;
    private final LongHashMap<SignControllerChunk> signChunks = new LongHashMap();
    private final LongHashMap<SignController.EntryList> signsByNeighbouringBlock = new LongHashMap();
    private final ChunkFutureProvider chunkFutureProvider;
    private boolean needsInitialization;

    SignControllerWorld(SignController controller) {
        this.controller = controller;
        this.world = null;
        this.offlineWorld = OfflineWorld.NONE;
        this.chunkFutureProvider = null;
        this.needsInitialization = true;
    }

    SignControllerWorld(SignController controller, World world) {
        this.controller = controller;
        this.world = world;
        this.offlineWorld = OfflineWorld.of((World)world);
        this.chunkFutureProvider = ChunkFutureProvider.of((Plugin)controller.getPlugin());
        this.needsInitialization = true;
    }

    public World getWorld() {
        return this.world;
    }

    public SignController getGlobalController() {
        return this.controller;
    }

    public TrainCarts getPlugin() {
        return this.controller.getPlugin();
    }

    public boolean isValid() {
        return this.offlineWorld.getLoadedWorld() == this.world;
    }

    public boolean isEnabled() {
        return true;
    }

    public void initialize() {
        if (this.needsInitialization) {
            this.needsInitialization = false;
            if (this.isEnabled()) {
                for (Chunk chunk : this.world.getLoadedChunks()) {
                    this.loadChunk(chunk);
                }
            }
        }
    }

    public SignController.Entry[] findNearby(Block block, boolean mustHaveSignActions) {
        if (!this.initializeNearbySigns(block.getX(), block.getY(), block.getZ(), 1, mustHaveSignActions)) {
            return SignController.EntryList.NONE.values();
        }
        return this.getNearbySignsUnsafe(LongBlockCoordinates.map(block.getX(), block.getY(), block.getZ()));
    }

    SignController.Entry[] getNearbySignsUnsafe(long blockCoordinatesKey) {
        return ((SignController.EntryList)this.signsByNeighbouringBlock.getOrDefault(blockCoordinatesKey, (Object)SignController.EntryList.NONE)).values();
    }

    public SignController.Entry findForSign(Block signBlock, boolean mustHaveSignActions) {
        for (SignController.Entry entry : this.findNearby(signBlock, mustHaveSignActions)) {
            if (!entry.sign.getBlock().equals((Object)signBlock)) continue;
            return entry;
        }
        return null;
    }

    public void forEachNearbyVerify(Block block, boolean mustHaveSignActions, Consumer<SignController.Entry> handler) {
        for (SignController.Entry entry : this.findNearby(block, mustHaveSignActions)) {
            if (mustHaveSignActions && !entry.hasSignActionEvents()) continue;
            handler.accept(entry);
        }
    }

    public void forEachSignInColumn(Block block, BlockFace direction, boolean mustHaveSignActions, Consumer<SignChangeTracker> handler) {
        int checkBorder;
        int bx = block.getX();
        int by = block.getY();
        int bz = block.getZ();
        int n = checkBorder = FaceUtil.isVertical((BlockFace)direction) ? 0 : 1;
        if (!this.initializeNearbySigns(bx, by, bz, checkBorder, mustHaveSignActions)) {
            return;
        }
        long key = LongBlockCoordinates.map(block.getX(), block.getY(), block.getZ());
        LongUnaryOperator shift = LongBlockCoordinates.shiftOperator(direction);
        int steps = 0;
        do {
            boolean foundSigns = false;
            for (SignController.Entry entry : this.getNearbySignsUnsafe(key)) {
                if (!this.verifySignColumnSlice(key, direction, steps == 0, entry)) continue;
                foundSigns = true;
                if (mustHaveSignActions && !entry.hasSignActionEvents()) continue;
                handler.accept(entry.sign);
            }
            if (!foundSigns && steps > 1) break;
            key = shift.applyAsLong(key);
            ++steps;
        } while (FaceUtil.isVertical((BlockFace)direction) || this.initializeNearbySigns(bx += direction.getModX(), by, bz += direction.getModZ(), checkBorder, mustHaveSignActions));
    }

    public boolean hasSignsAroundColumn(Block block, BlockFace direction, boolean mustHaveSignActions) {
        int checkBorder;
        int n = checkBorder = FaceUtil.isVertical((BlockFace)direction) ? 0 : 1;
        if (!this.initializeNearbySigns(block.getX(), block.getY(), block.getZ(), checkBorder, mustHaveSignActions)) {
            return false;
        }
        long key = LongBlockCoordinates.map(block.getX(), block.getY(), block.getZ());
        for (SignController.Entry entry : this.getNearbySignsUnsafe(key)) {
            if (!this.verifySignColumnSlice(key, direction, true, entry)) continue;
            return true;
        }
        return false;
    }

    private boolean initializeNearbySigns(int x, int y, int z, int border, boolean mustHaveSignActions) {
        int serverTick = CommonUtil.getServerTicks();
        int cx = x >> 4;
        int cz = z >> 4;
        boolean result = this.getSignChunk(cx, cz).checkMayHaveSigns(x, y, z, mustHaveSignActions, serverTick);
        int bx = x & 0xF;
        if (bx <= border) {
            result |= this.getSignChunk(cx - 1, cz).checkMayHaveSigns(x, y, z, mustHaveSignActions, serverTick);
        } else if (bx >= 15 - border) {
            result |= this.getSignChunk(cx + 1, cz).checkMayHaveSigns(x, y, z, mustHaveSignActions, serverTick);
        }
        int bz = z & 0xF;
        if (bz <= border) {
            result |= this.getSignChunk(cx, cz - 1).checkMayHaveSigns(x, y, z, mustHaveSignActions, serverTick);
        } else if (bz >= 15 - border) {
            result |= this.getSignChunk(cx, cz + 1).checkMayHaveSigns(x, y, z, mustHaveSignActions, serverTick);
        }
        return result;
    }

    private boolean verifySignColumnSlice(long key, BlockFace direction, boolean firstLayer, SignController.Entry entry) {
        BlockFace offset = LongBlockCoordinates.findDirection(entry.blockKey, key);
        if (offset == null) {
            return false;
        }
        BlockData blockData = entry.sign.getBlockData();
        if (blockData.isType(SIGN_POST_TYPE)) {
            if (direction == BlockFace.DOWN) {
                return offset == BlockFace.SELF || firstLayer && offset == BlockFace.DOWN;
            }
            return offset == BlockFace.DOWN || firstLayer && offset == BlockFace.SELF;
        }
        if (blockData.isType(WALL_SIGN_TYPE)) {
            if (offset == direction || offset == direction.getOppositeFace()) {
                return false;
            }
            if (direction.getModY() == 0 && offset.getModY() != 0) {
                return false;
            }
            BlockFace facing = blockData.getAttachedFace();
            return facing == offset || facing == direction.getOppositeFace();
        }
        entry.removeInvalidEntry();
        return false;
    }

    public void detectNewSigns(Block around) {
        long blockKey = LongBlockCoordinates.map(around);
        SignController.Entry[] nearby = this.initializeNearbySigns(around.getX(), around.getY(), around.getZ(), 1, false) ? this.getNearbySignsUnsafe(blockKey) : SignController.EntryList.NONE.unsortedValues();
        LongBlockCoordinates.forAllBlockSidesAndSelf(blockKey, (face, key) -> {
            for (SignController.Entry e : nearby) {
                if (e.blockKey != key) continue;
                return;
            }
            int bx = around.getX() + face.getModX();
            int by = around.getY() + face.getModY();
            int bz = around.getZ() + face.getModZ();
            Chunk chunk = WorldUtil.getChunk((World)this.world, (int)(bx >> 4), (int)(bz >> 4));
            if (chunk == null) {
                return;
            }
            if (!MaterialUtil.ISSIGN.get(ChunkUtil.getBlockData((Chunk)chunk, (int)bx, (int)by, (int)bz)).booleanValue()) {
                return;
            }
            final Block potentialSign = around.getRelative(face);
            new Task(this, (JavaPlugin)this.controller.getPlugin()){
                final /* synthetic */ SignControllerWorld this$0;
                {
                    this.this$0 = this$0;
                    super(arg0);
                }

                public void run() {
                    if (((Boolean)MaterialUtil.ISSIGN.get(potentialSign)).booleanValue()) {
                        this.this$0.addSign(potentialSign, false, true);
                    }
                }
            }.start();
        });
    }

    public void redetectSignActions() {
        for (SignControllerChunk chunk : this.signChunks.values()) {
            for (SignController.Entry entry : chunk.getEntries()) {
                entry.redetectSignActions();
            }
        }
    }

    public SignController.Entry addSign(Block signBlock, boolean isSignChange, boolean frontText) {
        Sign sign;
        SignController.Entry existing = this.findForSign(signBlock, false);
        if (existing != null) {
            if (isSignChange) {
                if (existing.verifyBeforeSignChange(frontText)) {
                    return existing;
                }
                existing.removeInvalidEntry();
                existing = null;
            }
            if (existing != null) {
                if (existing.verify()) {
                    this.controller.activateEntry(existing, true, true);
                    return existing;
                }
                existing.removeInvalidEntry();
                existing = null;
            }
        }
        if ((sign = BlockUtil.getSign((Block)signBlock)) == null) {
            return null;
        }
        return this.createNewSign(sign, isSignChange);
    }

    private SignController.Entry createNewSign(Sign sign, boolean isSignChange) {
        Block signBlock = sign.getBlock();
        SignControllerChunk signChunk = this.getSignChunk(MathUtil.toChunk((int)signBlock.getX()), MathUtil.toChunk((int)signBlock.getZ()));
        SignController.Entry entry = this.controller.createEntry(sign, this, signChunk, LongBlockCoordinates.map(signBlock.getX(), signBlock.getY(), signBlock.getZ()));
        signChunk.addEntry(entry);
        this.controller.activateEntry(entry, true, !isSignChange);
        return entry;
    }

    public RefreshResult refreshInChunk(Chunk chunk) {
        long chunkKey = SignControllerChunk.getKeyOf(chunk);
        int numRemoved = 0;
        SignControllerChunk signChunk = (SignControllerChunk)this.signChunks.get(chunkKey);
        if (signChunk != null && signChunk.hasSigns()) {
            for (SignController.Entry entry : signChunk.getEntries()) {
                if (entry.verify()) continue;
                signChunk.removeEntry(entry);
                this.controller.getPlugin().getOfflineSigns().removeAll(entry.sign.getBlock());
                ++numRemoved;
                entry.onRemoved();
            }
        }
        int numAdded = 0;
        for (BlockState blockState : this.getBlockStatesSafe(chunk)) {
            if (!(blockState instanceof Sign)) continue;
            Block signBlock = blockState.getBlock();
            SignController.Entry existing = this.findForSign(signBlock, false);
            if (existing != null) {
                this.controller.activateEntry(existing);
                continue;
            }
            this.createNewSign((Sign)blockState, false);
            ++numAdded;
        }
        return new RefreshResult(numAdded, numRemoved);
    }

    void clear() {
        for (SignControllerChunk chunk : this.signChunks.values()) {
            for (SignController.Entry e : chunk.getEntries()) {
                e.onRemoved();
            }
        }
        this.signChunks.clear();
        this.signsByNeighbouringBlock.clear();
    }

    private void activateSignsInChunk(Chunk chunk) {
        this.changeActiveForEntriesInChunk(chunk, true, SignController.Entry::verify, this.controller::activateEntry);
    }

    private void deactivateSignsInChunk(Chunk chunk) {
        this.changeActiveForEntriesInChunk(chunk, false, e -> !e.sign.isRemoved(), SignController.Entry::deactivate);
    }

    /*
     * Enabled aggressive block sorting
     */
    private void changeActiveForEntriesInChunk(Chunk chunk, boolean activating, Predicate<SignController.Entry> verify, Consumer<SignController.Entry> handler) {
        SignControllerChunk signChunk = (SignControllerChunk)this.signChunks.get(chunk.getX(), chunk.getZ());
        if (signChunk == null) return;
        if (!signChunk.hasSigns()) {
            return;
        }
        int retryLimit = 100;
        block0: while (true) {
            int n;
            SignController.Entry[] entries = signChunk.getEntries();
            boolean hasEntriesToHandle = false;
            for (SignController.Entry entry : entries) {
                if (entry.front.activated == activating && entry.back.activated == activating) continue;
                hasEntriesToHandle = true;
                break;
            }
            if (!hasEntriesToHandle) {
                return;
            }
            if (--retryLimit == 0) {
                this.controller.getPlugin().log(Level.SEVERE, "Infinite loop " + (activating ? "activating" : "de-activating") + " signs in chunk [" + chunk.getX() + "/" + chunk.getZ() + "]. Signs:");
                SignController.Entry[] entryArray = entries;
                int n2 = entryArray.length;
                n = 0;
                while (n < n2) {
                    SignController.Entry entry = entryArray[n];
                    this.controller.getPlugin().log(Level.SEVERE, "- at " + entry.sign.getBlock());
                    ++n;
                }
                return;
            }
            SignController.Entry[] entryArray = entries;
            int n3 = entryArray.length;
            n = 0;
            while (true) {
                if (n >= n3) continue block0;
                SignController.Entry entry = entryArray[n];
                if (entry.front.activated != activating || entry.back.activated != activating) {
                    if (verify.test(entry)) {
                        handler.accept(entry);
                    } else {
                        entry.removeInvalidEntry();
                    }
                }
                ++n;
            }
            break;
        }
    }

    private SignControllerChunk getSignChunk(int cx, int cz) {
        long key = MathUtil.longHashToLong((int)cx, (int)cz);
        SignControllerChunk signChunk = (SignControllerChunk)this.signChunks.get(key);
        if (signChunk == null) {
            signChunk = this.loadChunk(this.world.getChunkAt(cx, cz));
        }
        return signChunk;
    }

    SignControllerChunk loadChunk(Chunk chunk) {
        long chunkKey = SignControllerChunk.getKeyOf(chunk);
        if (this.needsInitialization) {
            return new SignControllerChunk(chunkKey);
        }
        SignControllerChunk existingSignChunk = (SignControllerChunk)this.signChunks.get(chunkKey);
        if (existingSignChunk != null) {
            return existingSignChunk;
        }
        SignControllerChunk newSignChunk = new SignControllerChunk(chunkKey);
        List<SignController.Entry> newEntriesAtChunk = Collections.emptyList();
        for (BlockState blockState : this.getBlockStatesSafe(chunk)) {
            if (!(blockState instanceof Sign)) continue;
            SignController.Entry entry = this.controller.createEntry((Sign)blockState, this, newSignChunk, LongBlockCoordinates.map(blockState.getX(), blockState.getY(), blockState.getZ()));
            if (newEntriesAtChunk.isEmpty()) {
                newEntriesAtChunk = new ArrayList<SignController.Entry>();
            }
            newEntriesAtChunk.add(entry);
        }
        newSignChunk.initialize(newEntriesAtChunk);
        this.signChunks.put(chunkKey, (Object)newSignChunk);
        this.chunkFutureProvider.trackNeighboursLoaded(chunk, ChunkFutureProvider.ChunkNeighbourList.neighboursOf((Chunk)chunk, (int)1), new ChunkFutureProvider.ChunkStateListener(){

            public void onRegistered(ChunkFutureProvider.ChunkStateTracker tracker) {
                if (tracker.isLoaded()) {
                    this.onLoaded(tracker);
                }
            }

            public void onCancelled(ChunkFutureProvider.ChunkStateTracker tracker) {
            }

            public void onLoaded(ChunkFutureProvider.ChunkStateTracker tracker) {
                SignControllerWorld.this.activateSignsInChunk(tracker.getChunk());
            }

            public void onUnloaded(ChunkFutureProvider.ChunkStateTracker tracker) {
                SignControllerWorld.this.deactivateSignsInChunk(tracker.getChunk());
            }
        });
        return newSignChunk;
    }

    private Collection<BlockState> getBlockStatesSafe(Chunk chunk) {
        try {
            return WorldUtil.getBlockStates((Chunk)chunk);
        }
        catch (Throwable t) {
            this.controller.getPlugin().getLogger().log(Level.SEVERE, "Error reading sign block states in chunk " + chunk.getWorld().getName() + " [" + chunk.getX() + "/" + chunk.getZ() + "]", t);
            return Collections.emptyList();
        }
    }

    void addChunkByBlockEntry(SignController.Entry entry, long key) {
        this.signsByNeighbouringBlock.merge(key, (Object)entry.singletonList, (a, b) -> a.add(entry));
    }

    void unloadChunk(Chunk chunk) {
        if (this.needsInitialization) {
            return;
        }
        SignControllerChunk signChunk = (SignControllerChunk)this.signChunks.remove(chunk.getX(), chunk.getZ());
        if (signChunk != null && signChunk.hasSigns()) {
            for (SignController.Entry entry : signChunk.getEntries()) {
                if (!entry.sign.isRemoved()) {
                    entry.deactivate();
                }
                entry.unregisterInNeighbouringBlocks(true);
            }
        }
    }

    protected void removeChunkByBlockEntry(SignController.Entry entry, long key) {
        this.removeChunkByBlockEntry(entry, key, false);
    }

    protected void removeChunkByBlockEntry(SignController.Entry entry, long key, boolean purgeAllInSameChunk) {
        SignController.EntryList oldEntryList = (SignController.EntryList)this.signsByNeighbouringBlock.remove(key);
        if (oldEntryList == null || purgeAllInSameChunk && LongBlockCoordinates.getChunkEdgeDistance(key) >= 2) {
            return;
        }
        SignController.EntryList newEntryList = oldEntryList.filter(e -> e != entry && (!purgeAllInSameChunk || e.chunk != entry.chunk));
        if (newEntryList.count() > 0) {
            this.signsByNeighbouringBlock.put(key, (Object)newEntryList);
        }
    }

    public static class RefreshResult {
        public static final RefreshResult NONE = new RefreshResult(0, 0);
        public final int numAdded;
        public final int numRemoved;

        public RefreshResult(int numAdded, int numRemoved) {
            this.numAdded = numAdded;
            this.numRemoved = numRemoved;
        }

        public RefreshResult add(RefreshResult other) {
            return new RefreshResult(this.numAdded + other.numAdded, this.numRemoved + other.numRemoved);
        }
    }

    static class SignControllerWorldDisabled
    extends SignControllerWorld {
        SignControllerWorldDisabled(SignController controller, World world) {
            super(controller, world);
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public SignController.Entry[] findNearby(Block block, boolean mustHaveSignActions) {
            return SignController.EntryList.NONE.values();
        }

        @Override
        public SignController.Entry addSign(Block signBlock, boolean handleLoadChange, boolean frontText) {
            return null;
        }

        @Override
        public RefreshResult refreshInChunk(Chunk chunk) {
            return RefreshResult.NONE;
        }

        @Override
        SignControllerChunk loadChunk(Chunk chunk) {
            return new SignControllerChunk(SignControllerChunk.getKeyOf(chunk));
        }

        @Override
        void unloadChunk(Chunk chunk) {
        }
    }
}

