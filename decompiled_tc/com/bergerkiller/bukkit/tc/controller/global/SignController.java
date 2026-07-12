/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.bases.IntVector2
 *  com.bergerkiller.bukkit.common.block.SignChangeTracker
 *  com.bergerkiller.bukkit.common.block.SignSide
 *  com.bergerkiller.bukkit.common.collections.FastTrackedUpdateSet
 *  com.bergerkiller.bukkit.common.collections.FastTrackedUpdateSet$Tracker
 *  com.bergerkiller.bukkit.common.component.LibraryComponent
 *  com.bergerkiller.bukkit.common.events.MultiBlockChangeEvent
 *  com.bergerkiller.bukkit.common.events.SignEditTextEvent
 *  com.bergerkiller.bukkit.common.events.SignEditTextEvent$EditReason
 *  com.bergerkiller.bukkit.common.internal.CommonCapabilities
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.ItemUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  com.bergerkiller.bukkit.common.wrappers.HumanHand
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.Sign
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockEvent
 *  org.bukkit.event.block.BlockPhysicsEvent
 *  org.bukkit.event.block.BlockRedstoneEvent
 *  org.bukkit.event.world.ChunkLoadEvent
 *  org.bukkit.event.world.ChunkUnloadEvent
 *  org.bukkit.event.world.WorldInitEvent
 *  org.bukkit.event.world.WorldUnloadEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.controller.global;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.block.SignChangeTracker;
import com.bergerkiller.bukkit.common.block.SignSide;
import com.bergerkiller.bukkit.common.collections.FastTrackedUpdateSet;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.events.MultiBlockChangeEvent;
import com.bergerkiller.bukkit.common.events.SignEditTextEvent;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.tc.PowerState;
import com.bergerkiller.bukkit.tc.SignActionHeader;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.global.SignBlocksAround;
import com.bergerkiller.bukkit.tc.controller.global.SignControllerChunk;
import com.bergerkiller.bukkit.tc.controller.global.SignControllerWorld;
import com.bergerkiller.bukkit.tc.controller.global.SignDebugHighlight;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignBuildEvent;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.util.SignActionLookupMap;
import com.bergerkiller.bukkit.tc.utils.RecursionGuard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class SignController
implements LibraryComponent,
Listener {
    private static final int MAX_REDSTONE_UPDATES_PER_TICK = 100000;
    private static final boolean CAN_DEBUG_DISPLAY_SIGNS = Common.hasCapability((String)"Common:BlockData:GetInteractableBox");
    private final TrainCarts plugin;
    private final SignControllerWorld NONE = new SignControllerWorld(this);
    private final IdentityHashMap<World, SignControllerWorld> byWorld = new IdentityHashMap();
    private int pendingRedstoneUpdatesThisTick = 0;
    private final FastTrackedUpdateSet<Entry> pendingRedstoneUpdates = new FastTrackedUpdateSet();
    private final FastTrackedUpdateSet<Entry> ignoreRedstoneUpdates = new FastTrackedUpdateSet();
    private final boolean blockPhysicsFireForSigns;
    private boolean enabled = true;
    private SignControllerWorld byWorldLastGet = this.NONE;
    private final RedstoneUpdateTask updateTask;
    private boolean redstonePhysicsSuppressed = false;
    private final RecursionGuard<ChunkLoadEvent> loadChunkRecursionGuard;

    public SignController(TrainCarts plugin) {
        this.plugin = plugin;
        this.updateTask = new RedstoneUpdateTask((JavaPlugin)plugin);
        this.blockPhysicsFireForSigns = SignController.doesBlockPhysicsFireForSigns();
        this.loadChunkRecursionGuard = RecursionGuard.handleOnce(event -> {
            if (!TCConfig.logSyncChunkLoads) {
                return;
            }
            plugin.getLogger().log(Level.WARNING, "Sync chunk load detected loading signs in chunk " + event.getWorld().getName() + " [" + event.getChunk().getX() + ", " + event.getChunk().getZ() + "]", new RuntimeException("Stack"));
        });
    }

    public TrainCarts getPlugin() {
        return this.plugin;
    }

    public void updateEnabled() {
        if (TCConfig.enableVanillaActionSigns) {
            this.plugin.register(this);
            this.updateTask.start(1L, 1L);
            if (!this.enabled) {
                this.enabled = true;
                for (World world : Bukkit.getWorlds()) {
                    this.forWorld(world);
                }
            }
        } else {
            this.disable();
        }
    }

    public void enable() {
        this.updateEnabled();
    }

    public void disable() {
        if (this.enabled) {
            CommonUtil.unregisterListener((Listener)this);
            this.byWorld.values().forEach(SignControllerWorld::clear);
            this.byWorld.clear();
            this.pendingRedstoneUpdates.clear();
            this.byWorldLastGet = this.NONE;
            this.updateTask.stop();
            this.enabled = false;
        }
    }

    public void suppressRedstonePhysicsDuring(Runnable runnable) {
        if (this.redstonePhysicsSuppressed) {
            runnable.run();
        } else {
            try {
                this.redstonePhysicsSuppressed = true;
                runnable.run();
            }
            finally {
                this.redstonePhysicsSuppressed = false;
            }
        }
    }

    public SignControllerWorld forWorld(World world) {
        SignControllerWorld c = this.byWorldLastGet;
        if (c.getWorld() == world) {
            return c;
        }
        c = this.byWorld.get(world);
        if (c != null) {
            this.byWorldLastGet = c;
            return this.byWorldLastGet;
        }
        if (!this.enabled) {
            return new SignControllerWorld.SignControllerWorldDisabled(this, world);
        }
        c = TrainCarts.isWorldDisabled(world) ? new SignControllerWorld.SignControllerWorldDisabled(this, world) : new SignControllerWorld(this, world);
        this.byWorld.put(world, c);
        this.byWorldLastGet = c;
        c.initialize();
        return c;
    }

    public SignControllerWorld forWorldSkipInitialization(World world) {
        SignControllerWorld c = this.byWorldLastGet;
        if (c.getWorld() == world) {
            return c;
        }
        c = this.byWorld.get(world);
        if (c != null) {
            this.byWorldLastGet = c;
            return this.byWorldLastGet;
        }
        if (!this.enabled) {
            return new SignControllerWorld.SignControllerWorldDisabled(this, world);
        }
        c = TrainCarts.isWorldDisabled(world) ? new SignControllerWorld.SignControllerWorldDisabled(this, world) : new SignControllerWorld(this, world);
        this.byWorld.put(world, c);
        this.byWorldLastGet = c;
        return c;
    }

    private SignControllerWorld tryGetForWorld(World world) {
        SignControllerWorld c = this.byWorldLastGet;
        if (c.getWorld() != world && (c = this.byWorld.get(world)) != null) {
            this.byWorldLastGet = c;
        }
        return c;
    }

    public void forEachNearbyVerify(Block block, boolean mustHaveSignActions, Consumer<Entry> handler) {
        this.forWorld(block.getWorld()).forEachNearbyVerify(block, mustHaveSignActions, handler);
    }

    public void ignoreOutputLever(Block lever) {
        Block att = BlockUtil.getAttachedBlock((Block)lever);
        this.forEachNearbyVerify(att, true, entry -> {
            if (entry.sign.isAttachedTo(att)) {
                entry.ignoreRedstone();
            }
        });
    }

    public SignControllerWorld.RefreshResult refreshInChunk(Chunk chunk) {
        return this.forWorld(chunk.getWorld()).refreshInChunk(chunk);
    }

    public void notifySignChanged(SignChangeTracker tracker) {
        SignControllerWorld worldController = this.forWorld(tracker.getWorld());
        Entry entry = worldController.findForSign(tracker.getBlock(), false);
        if (entry != null) {
            if (entry.sign != tracker) {
                entry.sign.update();
            }
            if (!entry.verifyAfterUpdate(true, true)) {
                entry.removeInvalidEntry();
            }
        }
    }

    private void cleanupUnloaded() {
        Iterator<SignControllerWorld> iter = this.byWorld.values().iterator();
        while (iter.hasNext()) {
            SignControllerWorld controller = iter.next();
            if (controller.isValid()) continue;
            iter.remove();
            this.byWorldLastGet = this.NONE;
            controller.clear();
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    private void onWorldInit(WorldInitEvent event) {
        this.forWorld(event.getWorld());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    private void onWorldUnload(WorldUnloadEvent event) {
        World world = event.getWorld();
        CommonUtil.nextTick(() -> {
            SignControllerWorld controller = this.byWorld.remove(world);
            if (controller != null) {
                controller.clear();
                this.byWorldLastGet = this.NONE;
            }
        });
    }

    @EventHandler(priority=EventPriority.MONITOR)
    private void onChunkLoad(ChunkLoadEvent event) {
        try (RecursionGuard.Token t = this.loadChunkRecursionGuard.open(event);){
            this.forWorld(event.getWorld()).loadChunk(event.getChunk());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    private void onChunkUnload(ChunkUnloadEvent event) {
        SignControllerWorld controller = this.tryGetForWorld(event.getWorld());
        if (controller != null) {
            controller.unloadChunk(event.getChunk());
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    private void onSignEditText(SignEditTextEvent event) {
        if (TrainCarts.isWorldDisabled((BlockEvent)event)) {
            return;
        }
        this.handleSignChange(SignBuildEvent.BKCLSignEditBuildEvent.create(event, true), event.getBlock(), event.getSide(), event.getEditReason() != SignEditTextEvent.EditReason.CTRL_PICK_PLACE);
    }

    protected void handleSignChange(SignBuildEvent event, Block signBlock, SignSide signSide, boolean isSignEdit) {
        SignControllerWorld controller = this.forWorld(event.getBlock().getWorld());
        Entry newSignEntry = controller.addSign(event.getBlock(), true, signSide.isFront());
        SignAction.handleBuild(event);
        if (newSignEntry != null && !event.isCancelled()) {
            newSignEntry.updateRedstoneLater();
        }
        if (event.isCancelled() && !CommonCapabilities.HAS_SIGN_BACK_TEXT) {
            Material signBlockType = signBlock.getType();
            if (!Util.canInstantlyBuild((Entity)event.getPlayer()) && MaterialUtil.ISSIGN.get(signBlockType).booleanValue()) {
                Material signItemType;
                if (signBlockType == MaterialUtil.getMaterial((String)"LEGACY_SIGN_POST") || signBlockType == MaterialUtil.getMaterial((String)"LEGACY_WALL_SIGN")) {
                    signItemType = MaterialUtil.getFirst((String[])new String[]{"OAK_SIGN", "LEGACY_SIGN"});
                } else if (signBlockType.name().contains("_WALL_")) {
                    signItemType = MaterialUtil.getMaterial((String)signBlockType.name().replace("_WALL_", "_"));
                    if (signItemType == null) {
                        signItemType = MaterialUtil.getFirst((String[])new String[]{"OAK_SIGN", "LEGACY_SIGN"});
                    }
                } else {
                    signItemType = signBlockType;
                }
                ItemStack item = HumanHand.getItemInMainHand((HumanEntity)event.getPlayer());
                if (LogicUtil.nullOrEmpty((ItemStack)item)) {
                    HumanHand.setItemInMainHand((HumanEntity)event.getPlayer(), (ItemStack)new ItemStack(signItemType, 1));
                } else if (MaterialUtil.isType((ItemStack)item, (Material[])new Material[]{signItemType}) && item.getAmount() < ItemUtil.getMaxSize((ItemStack)item)) {
                    ItemUtil.addAmount((ItemStack)item, (int)1);
                    HumanHand.setItemInMainHand((HumanEntity)event.getPlayer(), (ItemStack)item);
                } else {
                    Location loc = signBlock.getLocation().add(0.5, 0.5, 0.5);
                    loc.getWorld().dropItemNaturally(loc, new ItemStack(signItemType, 1));
                }
            }
            signBlock.setType(Material.AIR);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        SignControllerWorld controller = this.forWorld(block.getWorld());
        Entry e = controller.findForSign(block, false);
        if (e != null) {
            if (!e.verify()) {
                return;
            }
            e.updateRedstoneLater();
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    private void onBlockPhysics(BlockPhysicsEvent event) {
        if (this.redstonePhysicsSuppressed) {
            return;
        }
        Block block = event.getBlock();
        SignControllerWorld controller = this.forWorld(block.getWorld());
        if (MaterialUtil.ISSIGN.get(event.getChangedType()).booleanValue()) {
            controller.detectNewSigns(block);
        }
        if (this.blockPhysicsFireForSigns) {
            Entry e = controller.findForSign(block, true);
            if (e != null) {
                e.updateRedstoneLater();
            }
        } else {
            for (Entry e : controller.findNearby(block, true)) {
                e.updateRedstoneLater();
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    private void onBlockRedstoneChange(BlockRedstoneEvent event) {
        if (this.redstonePhysicsSuppressed || TrainCarts.isWorldDisabled((BlockEvent)event)) {
            return;
        }
        Block block = event.getBlock();
        for (Entry e : this.forWorld(block.getWorld()).findNearby(block, true)) {
            e.updateRedstoneLater();
        }
        BlockData event_block_data = WorldUtil.getBlockData((Block)event.getBlock());
        if (event_block_data.isType(Material.LEVER)) {
            Block leverBlock = event.getBlock();
            boolean isPowered = event.getNewCurrent() > 0;
            this.forEachNearbyVerify(leverBlock, true, entry -> {
                Block signBlock = entry.getBlock();
                if (leverBlock.getX() == signBlock.getX() && leverBlock.getZ() == signBlock.getZ() && Math.abs(leverBlock.getY() - signBlock.getY()) == 1) {
                    entry.updateRedstonePowerVerify(isPowered);
                }
            });
            this.ignoreOutputLever(event.getBlock());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    private void onMultiBlockChange(MultiBlockChangeEvent event) {
        SignControllerWorld worldController = this.forWorld(event.getWorld());
        for (IntVector2 chunkCoord : event.getChunkCoordinates()) {
            Chunk chunk = WorldUtil.getChunk((World)event.getWorld(), (int)chunkCoord.x, (int)chunkCoord.z);
            if (chunk == null) continue;
            worldController.refreshInChunk(chunk);
        }
    }

    Entry createEntry(Sign sign, SignControllerWorld world, SignControllerChunk chunk, long blockKey) {
        return new Entry(sign, world, chunk, blockKey, this);
    }

    void activateEntry(Entry entry) {
        this.activateEntry(entry, false, true);
    }

    void activateEntry(Entry entry, boolean refreshRailSigns, boolean handleLoadChange) {
        RailLookup.TrackedSign frontTrackedSign = null;
        RailLookup.TrackedSign backTrackedSign = null;
        if (refreshRailSigns) {
            if (entry.front.hasLoadedChangeHandler()) {
                frontTrackedSign = RailLookup.TrackedSign.forRealSign(entry.sign.getSign(), true, null);
            }
            if (entry.back.hasLoadedChangeHandler()) {
                backTrackedSign = RailLookup.TrackedSign.forRealSign(entry.sign.getSign(), false, null);
            }
            if (frontTrackedSign != null) {
                frontTrackedSign.getRail().forceCacheVerification();
            } else if (backTrackedSign != null) {
                backTrackedSign.getRail().forceCacheVerification();
            }
        }
        boolean wasFrontActivated = entry.front.activated;
        boolean wasBackActivated = entry.back.activated;
        if (wasFrontActivated && wasBackActivated) {
            return;
        }
        Block b = entry.sign.getBlock();
        try {
            entry.activate();
            if (handleLoadChange) {
                if (refreshRailSigns) {
                    if (frontTrackedSign != null && !wasFrontActivated) {
                        SignAction.handleLoadChange(frontTrackedSign, true);
                    }
                    if (backTrackedSign != null && !wasBackActivated) {
                        SignAction.handleLoadChange(backTrackedSign, true);
                    }
                } else {
                    if (!wasFrontActivated) {
                        entry.front.handleLoadChange(true);
                    }
                    if (!wasBackActivated) {
                        entry.back.handleLoadChange(true);
                    }
                }
            }
        }
        catch (Throwable t) {
            this.plugin.getLogger().log(Level.SEVERE, "Error while initializing sign in world " + b.getWorld().getName() + " at " + b.getX() + " / " + b.getY() + " / " + b.getZ(), t);
        }
    }

    private void updateRedstoneNow(Entry entry) {
        ++this.pendingRedstoneUpdatesThisTick;
        if (this.pendingRedstoneUpdatesThisTick >= 100000) {
            Block b = entry.sign.getBlock();
            this.plugin.getLogger().warning("Too many Redstone updates! Skipped sign at world=" + b.getWorld().getName() + " x=" + b.getX() + " y=" + b.getY() + " z=" + b.getZ());
            return;
        }
        if (entry.ignoreRedstoneUpdateTracker.isSet()) {
            return;
        }
        if (!entry.verify()) {
            return;
        }
        entry.updateRedstonePower();
    }

    public void redetectSignActions() {
        for (SignControllerWorld world : new ArrayList<SignControllerWorld>(this.byWorld.values())) {
            world.redetectSignActions();
        }
    }

    private static boolean doesBlockPhysicsFireForSigns() {
        if (Common.evaluateMCVersion((String)"<=", (String)"1.18.2")) {
            return true;
        }
        if (Common.IS_PAPERSPIGOT_SERVER) {
            if (Common.evaluateMCVersion((String)">=", (String)"1.19.3")) {
                return true;
            }
            if (Common.evaluateMCVersion((String)"==", (String)"1.19.2")) {
                if (SignController.checkBuildNumberLessThan("Paper", 165)) {
                    return false;
                }
                return !SignController.checkBuildNumberLessThan("Purpur", 1788);
            }
        }
        return false;
    }

    private static boolean checkBuildNumberLessThan(String serverName, int buildNumberThreshold) {
        Matcher m;
        String version = Bukkit.getVersion();
        if (version != null && (m = Pattern.compile("^git-" + serverName + "-(\\d+)\\s.*$").matcher(version)).matches()) {
            try {
                int build = Integer.parseInt(m.group(1));
                if (build < buildNumberThreshold) {
                    return true;
                }
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return false;
    }

    public static Runnable spawnDebugHighlight(AttachmentViewer viewer, SignChangeTracker sign, RailLookup.TrackedSign.DebugDisplayOptions options) {
        if (!CAN_DEBUG_DISPLAY_SIGNS || !viewer.supportsDisplayEntities()) {
            return () -> {};
        }
        SignDebugHighlight highlight = new SignDebugHighlight(viewer);
        highlight.spawn(sign, options);
        return highlight;
    }

    public static final class Entry {
        public final SignChangeTracker sign;
        private SignChangeTracker signLastState;
        public final SignControllerWorld world;
        public final SignControllerChunk chunk;
        public final SignSide front;
        public final SignSide back;
        private final FastTrackedUpdateSet.Tracker<Entry> redstoneUpdateTracker;
        private final FastTrackedUpdateSet.Tracker<Entry> ignoreRedstoneUpdateTracker;
        final long blockKey;
        SignBlocksAround blocks;
        private boolean registeredInNeighbouringBlocks;
        final EntryList singletonList;

        private Entry(Sign sign, SignControllerWorld world, SignControllerChunk chunk, long blockKey, SignController controller) {
            this.sign = SignChangeTracker.track((Sign)sign);
            this.world = world;
            this.chunk = chunk;
            this.front = new SignSide(true, SignChangeTracker::getFrontLine);
            this.back = new SignSide(false, SignChangeTracker::getBackLine);
            this.redstoneUpdateTracker = controller.pendingRedstoneUpdates.track((Object)this);
            this.ignoreRedstoneUpdateTracker = controller.ignoreRedstoneUpdates.track((Object)this);
            this.blockKey = blockKey;
            this.blocks = SignBlocksAround.of(this.sign.getAttachedFace());
            this.registeredInNeighbouringBlocks = false;
            this.singletonList = EntryList.createSingleton(this);
            this.updateLastSignState();
        }

        void updateLastSignState() {
            this.signLastState = this.sign.clone();
        }

        void updateSignFacing() {
            if (this.sign.getAttachedFace() != this.blocks.getAttachedFace()) {
                if (this.registeredInNeighbouringBlocks) {
                    this.blocks.forAllBlocks(this, this.world::removeChunkByBlockEntry);
                    this.blocks = SignBlocksAround.of(this.sign.getAttachedFace());
                    this.blocks.forAllBlocks(this, this.world::addChunkByBlockEntry);
                } else {
                    this.blocks = SignBlocksAround.of(this.sign.getAttachedFace());
                }
            }
        }

        public Block getBlock() {
            return this.sign.getBlock();
        }

        public SignActionHeader getFrontHeader() {
            return this.front.getHeader();
        }

        public SignActionHeader getBackHeader() {
            return this.back.getHeader();
        }

        public RailLookup.TrackedSign createFrontTrackedSign(RailPiece rail) {
            return this.front.createTrackedSign(rail);
        }

        public RailLookup.TrackedSign createBackTrackedSign(RailPiece rail) {
            return this.back.createTrackedSign(rail);
        }

        void removeInvalidEntry() {
            this.chunk.removeEntry(this);
            this.unregisterInNeighbouringBlocks();
            this.onRemoved();
        }

        void onRemoved() {
            this.redstoneUpdateTracker.untrack();
            this.ignoreRedstoneUpdateTracker.untrack();
        }

        void deactivate() {
            try {
                this.front.deactivate();
                this.back.deactivate();
            }
            catch (Throwable t) {
                Block b = this.sign.getBlock();
                this.world.getPlugin().getLogger().log(Level.SEVERE, "Error while unloading sign in world " + b.getWorld().getName() + " at " + b.getX() + " / " + b.getY() + " / " + b.getZ(), t);
            }
        }

        boolean verify() {
            boolean changed = this.sign.update();
            return this.verifyAfterUpdate(changed, changed);
        }

        boolean verifyAfterUpdate(boolean frontChanged, boolean backChanged) {
            boolean nowHasSignActions;
            if (this.sign.isRemoved() && this.signLastState != null && !this.signLastState.isRemoved() && WorldUtil.isLoaded((Block)this.sign.getBlock())) {
                this.handleDestroy(frontChanged, backChanged);
                return false;
            }
            if (frontChanged || backChanged || this.signLastState == null && !this.sign.isRemoved()) {
                this.updateLastSignState();
            }
            if (this.sign.isRemoved()) {
                return false;
            }
            if (frontChanged && backChanged) {
                this.world.getPlugin().getOfflineSigns().verifySign(this.sign.getSign());
            } else if (frontChanged) {
                this.world.getPlugin().getOfflineSigns().verifySign(this.sign.getSign(), true, null);
            } else if (backChanged) {
                this.world.getPlugin().getOfflineSigns().verifySign(this.sign.getSign(), false, null);
            }
            boolean hadSignActions = this.hasSignActionEvents();
            if (frontChanged) {
                this.front.updateSignAction();
            }
            if (backChanged) {
                this.back.updateSignAction();
            }
            if (hadSignActions != (nowHasSignActions = this.hasSignActionEvents())) {
                this.chunk.updateEntryHasSignActions(this, nowHasSignActions);
            }
            this.updateSignFacing();
            return true;
        }

        void redetectSignActions() {
            if (this.sign.isRemoved()) {
                return;
            }
            boolean hadSignActions = this.hasSignActionEvents();
            this.front.detectSignAction();
            this.back.detectSignAction();
            boolean nowHasSignActions = this.hasSignActionEvents();
            if (hadSignActions != nowHasSignActions) {
                this.chunk.updateEntryHasSignActions(this, nowHasSignActions);
            }
        }

        boolean verifyBeforeSignChange(boolean frontText) {
            this.sign.update();
            if (this.sign.isRemoved()) {
                this.verifyAfterUpdate(frontText, !frontText);
                return false;
            }
            this.handleDestroy(frontText, !frontText);
            this.updateSignFacing();
            this.updateLastSignState();
            return true;
        }

        private void handleDestroy(boolean destroyFront, boolean destroyBack) {
            RailLookup.TrackedSign sign;
            if (this.signLastState == null || this.signLastState.isRemoved()) {
                return;
            }
            if (destroyFront && !this.front.cachedHeader.isEmpty()) {
                sign = RailLookup.TrackedSign.forRealSign(this.signLastState, true, RailPiece.NONE);
                SignAction.handleDestroy(new SignActionEvent(sign));
            }
            if (destroyBack && !this.back.cachedHeader.isEmpty()) {
                sign = RailLookup.TrackedSign.forRealSign(this.signLastState, false, RailPiece.NONE);
                SignAction.handleDestroy(new SignActionEvent(sign));
            }
            if (destroyFront && destroyBack) {
                this.world.getPlugin().getOfflineSigns().removeAll(this.signLastState.getBlock());
                this.signLastState = null;
            } else if (destroyFront) {
                this.world.getPlugin().getOfflineSigns().removeAll(this.signLastState.getBlock(), true);
            } else if (destroyBack) {
                this.world.getPlugin().getOfflineSigns().removeAll(this.signLastState.getBlock(), false);
            }
        }

        public boolean hasSignActionEvents() {
            return !TCConfig.onlyRegisteredSignsHandleRedstone || this.front.hasSignAction() || this.back.hasSignAction();
        }

        void registerInNeighbouringBlocks() {
            if (!this.registeredInNeighbouringBlocks) {
                this.registeredInNeighbouringBlocks = true;
                this.blocks.forAllBlocks(this, this.world::addChunkByBlockEntry);
            }
        }

        void unregisterInNeighbouringBlocks() {
            this.unregisterInNeighbouringBlocks(false);
        }

        void unregisterInNeighbouringBlocks(boolean purgeAllInSameChunk) {
            if (this.registeredInNeighbouringBlocks) {
                this.registeredInNeighbouringBlocks = false;
                if (purgeAllInSameChunk) {
                    this.blocks.forAllBlocks(this, (e, key) -> this.world.removeChunkByBlockEntry(e, key, true));
                } else {
                    this.blocks.forAllBlocks(this, this.world::removeChunkByBlockEntry);
                }
            }
        }

        public void ignoreRedstone() {
            this.ignoreRedstoneUpdateTracker.set(true);
        }

        public void updateRedstoneLater() {
            this.redstoneUpdateTracker.set(true);
        }

        private static boolean skipReadingPower(SignActionHeader header) {
            return header.isEmpty() || header.isAlwaysOn() || header.isAlwaysOff();
        }

        private boolean checkIsSignPowered() {
            PowerState.Options opt = this.front.hasSignAction() || this.back.hasSignAction() ? PowerState.Options.SIGN_CONNECT_WIRE : PowerState.Options.SIGN;
            return PowerState.isSignPowered(this.sign.getBlock(), opt);
        }

        void activate() {
            boolean powered = Entry.skipReadingPower(this.front.getHeader()) && Entry.skipReadingPower(this.back.getHeader()) ? false : this.checkIsSignPowered();
            this.front.activated = true;
            this.front.setInitialPower(powered);
            this.back.activated = true;
            this.back.setInitialPower(powered);
        }

        public void updateRedstonePower() {
            boolean powered;
            SignActionHeader frontHeader = this.getFrontHeader();
            SignActionHeader backHeader = this.getBackHeader();
            boolean bl = powered = (!Entry.skipReadingPower(frontHeader) || !Entry.skipReadingPower(backHeader)) && this.checkIsSignPowered();
            if (!frontHeader.isEmpty()) {
                if (frontHeader.isAlwaysOn() || frontHeader.isAlwaysOff()) {
                    this.front.setRedstonePowerChanged(frontHeader);
                } else {
                    this.front.setRedstonePower(frontHeader, powered);
                }
            }
            if (!backHeader.isEmpty()) {
                if (backHeader.isAlwaysOn() || backHeader.isAlwaysOff()) {
                    this.back.setRedstonePowerChanged(backHeader);
                } else {
                    this.back.setRedstonePower(backHeader, powered);
                }
            }
        }

        public void updateRedstonePowerVerify(boolean isPowered) {
            boolean powerStateCorrect;
            SignActionHeader frontHeader = this.getFrontHeader();
            SignActionHeader backHeader = this.getBackHeader();
            boolean bl = powerStateCorrect = (!Entry.skipReadingPower(frontHeader) || !Entry.skipReadingPower(backHeader)) && isPowered == this.checkIsSignPowered();
            if (!frontHeader.isEmpty()) {
                if (frontHeader.isAlwaysOn() || frontHeader.isAlwaysOff()) {
                    this.front.setRedstonePowerChanged(frontHeader);
                } else if (powerStateCorrect) {
                    this.front.setRedstonePower(frontHeader, isPowered);
                }
            }
            if (!backHeader.isEmpty()) {
                if (backHeader.isAlwaysOn() || backHeader.isAlwaysOff()) {
                    this.back.setRedstonePowerChanged(backHeader);
                } else if (powerStateCorrect) {
                    this.back.setRedstonePower(backHeader, isPowered);
                }
            }
        }

        public class SignSide {
            private final boolean front;
            private final GetLineFunction lineFunc;
            public String headerLine;
            private SignActionHeader cachedHeader;
            private boolean hasSignAction;
            private boolean hasLoadedChangeHandler;
            public boolean powered;
            public boolean activated;

            public SignSide(boolean front, GetLineFunction lineFunc) {
                this.front = front;
                this.lineFunc = lineFunc;
                this.headerLine = lineFunc.getLine(Entry.this.sign, 0);
                this.cachedHeader = SignActionHeader.parse(Util.cleanSignLine(this.headerLine));
                this.detectSignAction();
                this.powered = false;
                this.activated = false;
            }

            public SignActionHeader getHeader() {
                return this.syncAndGetHeader(false);
            }

            private SignActionHeader syncAndGetHeader(boolean alwaysCheckHasSignAction) {
                String headerLine = this.lineFunc.getLine(Entry.this.sign, 0);
                if (headerLine.equals(this.headerLine)) {
                    if (alwaysCheckHasSignAction) {
                        this.detectSignAction(this.cachedHeader);
                    }
                    return this.cachedHeader;
                }
                this.headerLine = headerLine;
                SignActionHeader header = this.cachedHeader = SignActionHeader.parse(Util.cleanSignLine(headerLine));
                this.detectSignAction(header);
                return header;
            }

            public void updateSignAction() {
                this.syncAndGetHeader(true);
            }

            public boolean hasSignAction() {
                return this.hasSignAction;
            }

            public boolean hasLoadedChangeHandler() {
                return this.hasLoadedChangeHandler;
            }

            public void detectSignAction() {
                this.detectSignAction(this.cachedHeader);
            }

            private void detectSignAction(SignActionHeader header) {
                Optional<SignActionLookupMap.Entry> actionEntry = SignAction.getLookup().lookup(this.createSignActionEvent(header, RailPiece.NONE));
                this.hasSignAction = actionEntry.isPresent();
                this.hasLoadedChangeHandler = actionEntry.map(SignActionLookupMap.Entry::hasLoadedChangedHandler).orElse(false);
            }

            public void setInitialPower(boolean powered) {
                this.powered = powered;
            }

            public void deactivate() {
                if (this.activated) {
                    this.activated = false;
                    this.handleLoadChange(false);
                }
            }

            public void handleLoadChange(boolean loaded) {
                if (this.hasLoadedChangeHandler) {
                    SignAction.handleLoadChange(Entry.this.sign.getSign(), this.front, loaded);
                }
            }

            public void setRedstonePower(SignActionHeader header, boolean newPowerState) {
                SignActionEvent info = this.createSignActionEvent(header, null);
                SignActionType type = info.getHeader().getRedstoneAction(newPowerState);
                if (this.powered != newPowerState) {
                    this.powered = newPowerState;
                    if (type != SignActionType.NONE) {
                        SignAction.executeAll(info, type);
                    }
                }
                SignAction.executeAll(info, SignActionType.REDSTONE_CHANGE);
            }

            public void setRedstonePowerChanged(SignActionHeader header) {
                SignActionEvent info = this.createSignActionEvent(header, null);
                SignAction.executeAll(info, SignActionType.REDSTONE_CHANGE);
            }

            public RailLookup.TrackedSign createTrackedSign(RailPiece rail) {
                return this.createTrackedSign(this.getHeader(), rail);
            }

            private RailLookup.TrackedSign createTrackedSign(SignActionHeader header, RailPiece rail) {
                RailLookup.TrackedSign trackedSign = RailLookup.TrackedSign.forRealSign(Entry.this.sign, this.front, rail);
                trackedSign.setCachedHeader(header);
                return trackedSign;
            }

            private SignActionEvent createSignActionEvent(SignActionHeader header, RailPiece rail) {
                return new SignActionEvent(this.createTrackedSign(header, rail));
            }
        }

        @FunctionalInterface
        public static interface GetLineFunction {
            public String getLine(SignChangeTracker var1, int var2);
        }
    }

    private class RedstoneUpdateTask
    extends Task {
        public RedstoneUpdateTask(JavaPlugin plugin) {
            super(plugin);
        }

        public void run() {
            SignController.this.pendingRedstoneUpdatesThisTick = 0;
            SignController signController = SignController.this;
            SignController.this.pendingRedstoneUpdates.forEachAndClear(x$0 -> signController.updateRedstoneNow(x$0));
            SignController.this.ignoreRedstoneUpdates.clear();
            SignController.this.cleanupUnloaded();
        }
    }

    public static final class EntryList {
        public static final EntryList NONE = new EntryList(new Entry[0], true);
        private final Entry[] values;
        private boolean sorted;

        private EntryList(Entry[] values, boolean sorted) {
            this.values = values;
            this.sorted = sorted;
        }

        public int count() {
            return this.values.length;
        }

        public Entry[] unsortedValues() {
            return this.values;
        }

        public Entry[] values() {
            if (!this.sorted) {
                this.sorted = true;
                Arrays.sort(this.values, Comparator.comparingLong(e -> e.blockKey));
            }
            return this.values;
        }

        public EntryList add(Entry entry) {
            Entry[] values = this.values;
            int len = values.length;
            if (len == 0) {
                return entry.singletonList;
            }
            Entry[] tmp = Arrays.copyOf(values, len + 1);
            tmp[len] = entry;
            return new EntryList(tmp, false);
        }

        public boolean contains(Entry entry) {
            for (Entry value : this.values) {
                if (value != entry) continue;
                return true;
            }
            return false;
        }

        public EntryList filter(Predicate<Entry> filter) {
            Entry[] values = this.values;
            int len = values.length;
            int numPassingFilter = 0;
            for (int i = 0; i < len; ++i) {
                if (!filter.test(values[i])) continue;
                ++numPassingFilter;
            }
            if (numPassingFilter == len) {
                return this;
            }
            if (numPassingFilter == 0) {
                return NONE;
            }
            Entry[] filteredValues = new Entry[numPassingFilter];
            int currentIndex = 0;
            for (int i = 0; i < len; ++i) {
                Entry e = values[i];
                if (!filter.test(e)) continue;
                filteredValues[currentIndex++] = e;
            }
            return new EntryList(filteredValues, this.sorted);
        }

        public static EntryList of(List<Entry> entries) {
            int count = entries.size();
            if (count == 0) {
                return NONE;
            }
            if (count == 1) {
                return entries.get((int)0).singletonList;
            }
            return new EntryList(entries.toArray(new Entry[0]), false);
        }

        public static EntryList createSingleton(Entry entry) {
            return new EntryList(new Entry[]{entry}, true);
        }
    }
}

