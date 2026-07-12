/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.block.SignChangeTracker
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 *  org.bukkit.ChatColor
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.Sign
 */
package com.bergerkiller.bukkit.tc.rails;

import com.bergerkiller.bukkit.common.block.SignChangeTracker;
import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.tc.PowerState;
import com.bergerkiller.bukkit.tc.SignActionHeader;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.controller.global.SignController;
import com.bergerkiller.bukkit.tc.controller.global.SignControllerWorld;
import com.bergerkiller.bukkit.tc.detector.DetectorRegion;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.rails.TrackedSignLookup;
import com.bergerkiller.bukkit.tc.rails.WorldRailLookup;
import com.bergerkiller.bukkit.tc.rails.WorldRailLookupImpl;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.FakeSign;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

public final class RailLookup {
    static final int LIFE_TIMER_DELETED = 0;
    static final int LIFE_TIMER_START = 1;
    static int lifeTimer = 1;
    static int lifeTimerAtPosition = 1;
    static int verifyTimer = 1;
    static final DetectorRegion[] NO_DETECTOR_REGIONS = new DetectorRegion[0];
    static final TrackedSign[] NO_SIGNS = new TrackedSign[0];
    static final TrackedSign[] MISSING_RAILS_NO_SIGNS = new TrackedSign[0];
    static final List<MinecartMember<?>> DEFAULT_MEMBER_LIST = Collections.emptyList();
    private static final IdentityHashMap<World, WorldRailLookupImpl> byWorld = new IdentityHashMap();

    public static WorldRailLookup forWorld(World world) {
        WorldRailLookupImpl lookup = byWorld.get(world);
        if (lookup == null) {
            if (world == null) {
                return WorldRailLookup.NONE;
            }
            lookup = new WorldRailLookupImpl(TrainCarts.plugin, world);
            byWorld.put(world, lookup);
            lookup.initialize();
        }
        return lookup;
    }

    public static WorldRailLookup forWorldIfInitialized(World world) {
        IdentityHashMap byWorldCast = (IdentityHashMap)CommonUtil.unsafeCast(byWorld);
        return byWorldCast.getOrDefault(world, WorldRailLookup.NONE);
    }

    public static RailPiece[] findAtStatePosition(RailState state) {
        return state.railLookup().findAtStatePosition(state);
    }

    public static RailPiece[] findAtBlockPosition(OfflineBlock positionBlock) {
        return RailLookup.forWorld(positionBlock.getLoadedWorld()).findAtBlockPosition(positionBlock);
    }

    public static List<MinecartMember<?>> findMembersOnRail(OfflineBlock railOfflineBlock) {
        return RailLookup.forWorldIfInitialized(railOfflineBlock.getLoadedWorld()).findMembersOnRail(railOfflineBlock);
    }

    public static CachedRailPiece lookupCachedRailPieceIfCached(OfflineBlock railOfflineBlock, RailType railType) {
        return RailLookup.forWorldIfInitialized(railOfflineBlock.getLoadedWorld()).lookupCachedRailPieceIfCached(railOfflineBlock, railType);
    }

    public static void clear() {
        byWorld.values().forEach(WorldRailLookupImpl::close);
        byWorld.clear();
    }

    public static void redetectSignActions() {
        for (WorldRailLookup worldRailLookup : byWorld.values()) {
            worldRailLookup.redetectSignActions();
        }
    }

    public static void forceUnloadRail(RailType type) {
        RailLookup.forceRecalculation();
        byWorld.values().forEach(lookup -> lookup.unloadRailType(type));
    }

    public static void forceRecalculation() {
        byWorld.values().forEach(WorldRailLookupImpl::refreshAllBuckets);
        lifeTimer = 1 + TCConfig.cacheExpireTicks + TCConfig.cacheVerificationTicks;
        lifeTimerAtPosition = 1;
        verifyTimer = ++lifeTimer + TCConfig.cacheVerificationTicks;
    }

    public static void removeMemberFromAll(MinecartMember<?> member) {
        for (WorldRailLookup worldRailLookup : byWorld.values()) {
            worldRailLookup.removeMemberFromAll(member);
        }
    }

    public static void update() {
        int deadTimeout = lifeTimer - TCConfig.cacheExpireTicks - TCConfig.cacheVerificationTicks;
        Iterator<WorldRailLookupImpl> iter = byWorld.values().iterator();
        while (iter.hasNext()) {
            WorldRailLookupImpl lookup = iter.next();
            if (lookup.checkCanBeRemoved()) {
                lookup.close();
                iter.remove();
                continue;
            }
            lookup.update(deadTimeout);
        }
        ++lifeTimerAtPosition;
        verifyTimer = ++lifeTimer + TCConfig.cacheVerificationTicks;
    }

    public static RailPiece discoverRailPieceFromSign(Block signblock) {
        return RailLookup.forWorld(signblock.getWorld()).discoverRailPieceFromSign(signblock);
    }

    public static TrackedSign[] discoverSignsAtRailPiece(RailPiece rail) {
        return rail.railLookup().discoverSignsAtRailPiece(rail);
    }

    public static abstract class CachedRailPiece
    extends RailPiece {
        protected List<MinecartMember<?>> members;
        protected TrackedSign[] signs;
        protected DetectorRegion[] detectorRegions;
        public static final CachedRailPiece NONE = new CachedRailPiece(){

            @Override
            public boolean verify() {
                return false;
            }

            @Override
            public boolean verifyExists() {
                return false;
            }

            @Override
            public void forceCacheVerification() {
            }
        };

        private CachedRailPiece() {
            this.members = Collections.unmodifiableList(Collections.emptyList());
            this.signs = NO_SIGNS;
            this.detectorRegions = NO_DETECTOR_REGIONS;
        }

        protected CachedRailPiece(WorldRailLookup railLookup, OfflineBlock offlineBlock, Block block, RailType type) {
            super(railLookup, offlineBlock, block, type);
            this.cached = this;
            this.members = DEFAULT_MEMBER_LIST;
            this.signs = NO_SIGNS;
            this.detectorRegions = NO_DETECTOR_REGIONS;
        }

        public abstract boolean verify();

        public abstract boolean verifyExists();

        @Override
        public abstract void forceCacheVerification();

        public final List<MinecartMember<?>> cachedMembers() {
            return this.members;
        }

        public final List<MinecartMember<?>> cachedMutableMembers() {
            List<MinecartMember<?>> result = this.members;
            if (result == DEFAULT_MEMBER_LIST) {
                this.members = result = new ArrayList(2);
            }
            return result;
        }

        public final TrackedSign[] cachedSigns() {
            return this.signs;
        }

        @Override
        public final void redetectSignActions() {
            for (TrackedSign sign : this.signs) {
                sign.redetectSignAction();
            }
            this.forceCacheVerification();
        }

        public final DetectorRegion[] cachedDetectorRegions() {
            return this.detectorRegions;
        }
    }

    public static abstract class TrackedSign {
        public final Sign sign;
        public final Block signBlock;
        @Deprecated
        public RailPiece rail;
        @Deprecated
        public RailType railType;
        @Deprecated
        public Block railBlock;
        private final int signBlockHashCode;
        private SignActionHeader cachedHeader = null;
        private boolean cachedActionSet = false;
        private SignAction cachedAction = null;

        TrackedSign(Sign sign, Block signBlock, RailPiece rail) {
            if (sign == null) {
                throw new IllegalArgumentException("There is no sign at " + signBlock);
            }
            this.sign = sign;
            this.signBlock = signBlock;
            this.signBlockHashCode = signBlock.hashCode();
            this.rail = rail;
            this.railType = rail.type();
            this.railBlock = rail.block();
        }

        private TrackedSign() {
            this.sign = null;
            this.signBlock = null;
            this.signBlockHashCode = 0;
            this.rail = null;
            this.railType = RailType.NONE;
            this.railBlock = null;
        }

        public abstract boolean verify();

        public abstract boolean isRemoved();

        public abstract BlockFace getFacing();

        public abstract Block getAttachedBlock();

        public void setOutput(boolean output) {
            Block attachedBlock = this.getAttachedBlock();
            if (attachedBlock != null) {
                BlockUtil.setLeversAroundBlock((Block)attachedBlock, (boolean)output);
            }
        }

        public abstract String[] getExtraLines();

        public abstract PowerState getPower(BlockFace var1);

        public abstract boolean isRealSign();

        public abstract String getLine(int var1) throws IndexOutOfBoundsException;

        public abstract void setLine(int var1, String var2) throws IndexOutOfBoundsException;

        public Runnable showDebugHighlight(AttachmentViewer viewer, DebugDisplayOptions options) {
            return () -> {};
        }

        public SignActionHeader getHeader() {
            SignActionHeader header = this.cachedHeader;
            if (header == null) {
                this.cachedHeader = header = SignActionHeader.parse(Util.cleanSignLine(this.getLine(0)));
            }
            return header;
        }

        public void setCachedHeader(SignActionHeader header) {
            this.cachedHeader = header;
        }

        public SignAction getAction() {
            if (this.cachedActionSet) {
                return this.cachedAction;
            }
            this.cachedActionSet = true;
            this.cachedAction = SignAction.getSignAction(this.createEvent(SignActionType.NONE));
            return this.cachedAction;
        }

        public void redetectSignAction() {
            this.cachedActionSet = false;
            this.cachedAction = null;
        }

        public RailPiece getRail() {
            RailPiece rail = this.rail;
            if (rail == null) {
                this.rail = rail = RailLookup.discoverRailPieceFromSign(this.signBlock);
                this.railBlock = rail.block();
                this.railType = rail.type();
            }
            return rail;
        }

        public final SignActionEvent createEvent(SignActionType action) {
            return new SignActionEvent(this).setAction(action);
        }

        private final boolean canFireEvents() {
            return !this.isRemoved() && (this.rail == null || this.rail.type().isRegistered());
        }

        public void executeEventForMember(SignActionType action, MinecartMember<?> member) {
            this.executeEventForMember(action, member, null);
        }

        public void executeEventForMember(SignActionType action, MinecartMember<?> member, RailState enterState) {
            if (this.canFireEvents() && member.isInteractable()) {
                SignActionEvent event = this.createEvent(action);
                event.setMember(member);
                event.overrideCartEnterState(enterState);
                SignAction.executeOne(this.getAction(), event);
            }
        }

        public void executeEventForGroup(SignActionType action, MinecartGroup group) {
            this.executeEventForGroup(action, group, null);
        }

        public void executeEventForGroup(SignActionType action, MinecartGroup group, RailState enterState) {
            if (this.canFireEvents()) {
                SignActionEvent event = this.createEvent(action);
                event.setGroup(group);
                event.overrideCartEnterState(enterState);
                SignAction.executeOne(this.getAction(), event);
            }
        }

        public boolean hasIdenticalText(TrackedSign other) {
            for (int i = 0; i < 4; ++i) {
                if (this.getLine(i).equals(other.getLine(i))) continue;
                return false;
            }
            return true;
        }

        public int hashCode() {
            return this.signBlockHashCode;
        }

        public abstract Object getUniqueKey();

        public boolean equals(Object o) {
            return this == o;
        }

        @Deprecated
        public static TrackedSign forRealSign(SignChangeTracker signTracker, RailPiece rail) {
            return TrackedSign.forRealSign(signTracker, true, rail);
        }

        @Deprecated
        public static TrackedSign forRealSign(Block signBlock, RailPiece rail) {
            return TrackedSign.forRealSign(signBlock, true, rail);
        }

        @Deprecated
        public static TrackedSign forRealSign(Sign sign, RailPiece rail) {
            return TrackedSign.forRealSign(sign, true, rail);
        }

        @Deprecated
        public static TrackedSign forRealSign(Sign sign, Block signBlock, RailPiece rail) {
            return TrackedSign.forRealSign(sign, signBlock, true, rail);
        }

        public static TrackedSign forRealSign(SignChangeTracker signTracker, boolean frontText, RailPiece rail) {
            if (signTracker.isRemoved()) {
                throw new IllegalArgumentException("Sign does not exist at sign block " + signTracker.getBlock());
            }
            if (rail == null) {
                rail = RailLookup.discoverRailPieceFromSign(signTracker.getBlock());
            }
            if (frontText) {
                return new TrackedRealSignFront(TrainCarts.plugin, signTracker, rail);
            }
            return new TrackedRealSignBack(TrainCarts.plugin, signTracker, rail);
        }

        public static TrackedSign forRealSign(Block signBlock, boolean frontText, RailPiece rail) {
            if (signBlock == null) {
                throw new IllegalArgumentException("Sign block is null");
            }
            return TrackedSign.forRealSign(SignChangeTracker.track((Block)signBlock), frontText, rail);
        }

        public static TrackedSign forRealSign(Sign sign, boolean frontText, RailPiece rail) {
            if (sign == null) {
                throw new IllegalArgumentException("Sign is null");
            }
            return TrackedSign.forRealSign(SignChangeTracker.track((Sign)sign), frontText, rail);
        }

        public static TrackedSign forRealSign(Sign sign, Block signBlock, boolean frontText, RailPiece rail) {
            if (sign != null) {
                return TrackedSign.forRealSign(SignChangeTracker.track((Sign)sign), frontText, rail);
            }
            if (signBlock != null) {
                return TrackedSign.forRealSign(SignChangeTracker.track((Block)signBlock), frontText, rail);
            }
            throw new IllegalArgumentException("No sign or sign block specified (null)");
        }

        public static interface DebugDisplayOptions
        extends TrainCarts.Provider {
            public ChatColor getTeamColor();
        }
    }

    public static final class UnitTestTrackedSign
    extends TrackedSign {
        private final String[] lines;

        public static UnitTestTrackedSign of(String ... lines) {
            return new UnitTestTrackedSign(lines);
        }

        private UnitTestTrackedSign(String[] lines) {
            this.lines = lines;
        }

        @Override
        public boolean verify() {
            return true;
        }

        @Override
        public boolean isRemoved() {
            return false;
        }

        @Override
        public BlockFace getFacing() {
            return BlockFace.NORTH;
        }

        @Override
        public Block getAttachedBlock() {
            return null;
        }

        @Override
        public String[] getExtraLines() {
            return new String[0];
        }

        @Override
        public PowerState getPower(BlockFace from) {
            return PowerState.NONE;
        }

        @Override
        public boolean isRealSign() {
            return false;
        }

        @Override
        public String getLine(int index) throws IndexOutOfBoundsException {
            return this.lines[index];
        }

        @Override
        public void setLine(int index, String line) throws IndexOutOfBoundsException {
            throw new UnsupportedOperationException("Not supported for unit test tracked signs");
        }

        @Override
        public Object getUniqueKey() {
            return this;
        }
    }

    private static class TrackedRealSignBack
    extends TrackedRealSignBase {
        private TrackedRealSignBack(TrainCarts plugin, SignChangeTracker tracker, RailPiece rail) {
            super(plugin, tracker, rail, false);
        }

        @Override
        public BlockFace getFacing() {
            return this.facing.getOppositeFace();
        }

        @Override
        public String getLine(int index) throws IndexOutOfBoundsException {
            return this.tracker.getBackLine(index);
        }

        @Override
        public void setLine(int index, String line) throws IndexOutOfBoundsException {
            this.tracker.setBackLine(index, line);
        }
    }

    private static class TrackedRealSignFront
    extends TrackedRealSignBase {
        private TrackedRealSignFront(TrainCarts plugin, SignChangeTracker tracker, RailPiece rail) {
            super(plugin, tracker, rail, true);
        }

        @Override
        public BlockFace getFacing() {
            return this.facing;
        }

        @Override
        public String getLine(int index) throws IndexOutOfBoundsException {
            return this.tracker.getFrontLine(index);
        }

        @Override
        public void setLine(int index, String line) throws IndexOutOfBoundsException {
            this.tracker.setFrontLine(index, line);
        }
    }

    private static abstract class TrackedRealSignBase
    extends TrackedRealSign {
        protected final TrainCarts plugin;
        protected final SignChangeTracker tracker;
        protected final BlockFace facing;
        private final TrackedSignLookup.RealSignKey key;

        private TrackedRealSignBase(TrainCarts plugin, SignChangeTracker tracker, RailPiece rail, boolean front) {
            super(tracker.getSign(), tracker.getBlock(), rail);
            this.plugin = plugin;
            this.facing = tracker.getFacing();
            this.tracker = tracker;
            this.key = new TrackedSignLookup.RealSignKey(OfflineBlock.of((Block)this.signBlock), front);
        }

        @Override
        public Object getUniqueKey() {
            return this.key;
        }

        @Override
        public boolean verify() {
            if (this.tracker.update()) {
                this.plugin.getSignController().notifySignChanged(this.tracker);
            }
            return !this.tracker.isRemoved() && this.tracker.getFacing() == this.facing && this.tracker.getSign() == this.sign;
        }

        @Override
        public boolean isRemoved() {
            return this.tracker.isRemoved();
        }

        @Override
        public boolean isFrontText() {
            return this.key.front;
        }

        @Override
        public String[] getExtraLines() {
            SignController.Entry entry;
            RailPiece rail = this.getRail();
            if (rail.isNone()) {
                return StringUtil.EMPTY_ARRAY;
            }
            ArrayList<String> lines = new ArrayList<String>();
            Block signBlock = this.signBlock.getRelative(BlockFace.DOWN);
            SignControllerWorld signController = this.plugin.getSignController().forWorld(rail.world());
            while ((entry = signController.findForSign(signBlock, false)) != null && entry.sign.getFacing() == this.facing) {
                TrackedSign sign;
                TrackedSign trackedSign = sign = this.isFrontText() ? entry.createFrontTrackedSign(rail) : entry.createBackTrackedSign(rail);
                if (sign.getAction() != null) break;
                for (int i = 0; i < 4; ++i) {
                    lines.add(sign.getLine(i));
                }
                signBlock = signBlock.getRelative(BlockFace.DOWN);
            }
            return lines.toArray(new String[0]);
        }

        @Override
        public Runnable showDebugHighlight(AttachmentViewer viewer, TrackedSign.DebugDisplayOptions options) {
            return SignController.spawnDebugHighlight(viewer, this.tracker, options);
        }

        @Override
        public Block getAttachedBlock() {
            return this.signBlock.getRelative(this.tracker.getAttachedFace());
        }

        @Override
        public PowerState getPower(BlockFace from) {
            return PowerState.get(this.signBlock, from, this.getAction() != null ? PowerState.Options.SIGN_CONNECT_WIRE : PowerState.Options.SIGN);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof TrackedRealSignBase) {
                return ((TrackedRealSignBase)o).key.equals(this.key);
            }
            return false;
        }
    }

    public static abstract class TrackedRealSign
    extends TrackedSign {
        protected TrackedRealSign(Sign sign, Block signBlock, RailPiece rail) {
            super(sign, signBlock, rail);
        }

        @Override
        public final boolean isRealSign() {
            return true;
        }

        public abstract boolean isFrontText();

        public String toString() {
            StringBuilder str = new StringBuilder();
            str.append(this.getClass().getSimpleName()).append('{');
            str.append("world=").append(this.signBlock.getWorld().getName());
            str.append(", x=").append(this.signBlock.getX());
            str.append(", y=").append(this.signBlock.getY());
            str.append(", z=").append(this.signBlock.getZ());
            str.append(", side=").append(this.isFrontText() ? "front" : "back");
            str.append(", lines=[");
            for (int i = 0; i < 4; ++i) {
                if (i > 0) {
                    str.append(" | ");
                }
                str.append(this.getLine(i));
            }
            str.append("]}");
            return str.toString();
        }
    }

    public static abstract class TrackedFakeSign
    extends TrackedSign {
        public TrackedFakeSign(RailPiece rail) {
            this(rail.block(), rail);
        }

        public TrackedFakeSign(Block signBlock, RailPiece rail) {
            super(FakeSign.create(signBlock), signBlock, rail);
            ((FakeSign)this.sign).setHandler(new FakeSign.Handler(){

                @Override
                public String getFrontLine(int index) {
                    return this.getLine(index);
                }

                @Override
                public void setFrontLine(int index, String text) {
                    this.setLine(index, text);
                }

                @Override
                public String getBackLine(int index) {
                    return "";
                }

                @Override
                public void setBackLine(int index, String text) {
                }
            });
        }

        @Override
        public abstract String getLine(int var1) throws IndexOutOfBoundsException;

        @Override
        public abstract void setLine(int var1, String var2) throws IndexOutOfBoundsException;

        @Override
        public boolean isRealSign() {
            return false;
        }

        @Override
        public Object getUniqueKey() {
            return this;
        }
    }

    public static final class RailTypeNotRegisteredException
    extends IllegalArgumentException {
        private static final long serialVersionUID = -3651967639525705930L;

        public RailTypeNotRegisteredException(RailType type) {
            super("Rail type " + type + " is not registered");
        }
    }
}

