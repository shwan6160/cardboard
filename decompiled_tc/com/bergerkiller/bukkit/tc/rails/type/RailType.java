/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.internal.CommonCapabilities
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.event.block.BlockPhysicsEvent
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.rails.type;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailAABB;
import com.bergerkiller.bukkit.tc.controller.components.RailJunction;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.controller.global.SignControllerWorld;
import com.bergerkiller.bukkit.tc.editor.RailsTexture;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogic;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicAir;
import com.bergerkiller.bukkit.tc.rails.type.RailTypeActivator;
import com.bergerkiller.bukkit.tc.rails.type.RailTypeCrossing;
import com.bergerkiller.bukkit.tc.rails.type.RailTypeDetector;
import com.bergerkiller.bukkit.tc.rails.type.RailTypeNone;
import com.bergerkiller.bukkit.tc.rails.type.RailTypePowered;
import com.bergerkiller.bukkit.tc.rails.type.RailTypeRegular;
import com.bergerkiller.bukkit.tc.rails.type.RailTypeVertical;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.plugin.Plugin;

public abstract class RailType {
    public static final RailTypeVertical VERTICAL = new RailTypeVertical();
    public static final RailTypeActivator ACTIVATOR_ON = new RailTypeActivator(true);
    public static final RailTypeActivator ACTIVATOR_OFF = new RailTypeActivator(false);
    public static final RailTypeCrossing CROSSING = new RailTypeCrossing();
    public static final RailTypeRegular REGULAR = new RailTypeRegular();
    public static final RailTypeDetector DETECTOR = new RailTypeDetector();
    public static final RailTypePowered BRAKE = new RailTypePowered(false);
    public static final RailTypePowered BOOST = new RailTypePowered(true);
    public static final RailTypeNone NONE = new RailTypeNone();
    private static List<RailType> values = new ArrayList<RailType>();
    private final boolean _isComplexRailBlock = CommonUtil.isMethodOverrided(RailType.class, this.getClass(), (String)"isRail", (Class[])new Class[]{World.class, Integer.TYPE, Integer.TYPE, Integer.TYPE});
    private final boolean _isHandlingPhysics = CommonUtil.isMethodOverrided(RailType.class, this.getClass(), (String)"onBlockPhysics", (Class[])new Class[]{BlockPhysicsEvent.class}) || CommonUtil.isMethodOverrided(RailType.class, this.getClass(), (String)"isRailsSupported", (Class[])new Class[]{Block.class});
    private boolean _registered = false;

    public static void handleCriticalError(RailType railType, Throwable reason) {
        if (!values.contains(railType)) {
            return;
        }
        TrainCarts traincarts = TrainCarts.plugin;
        Plugin plugin = CommonUtil.getPluginByClass(railType.getClass());
        Logger logger = traincarts.getLogger();
        if (plugin == traincarts) {
            logger.log(Level.SEVERE, "An error occurred in RailType '" + railType.getClass().getSimpleName() + "'", reason);
        } else if (plugin != null) {
            logger.log(Level.SEVERE, "An error occurred in RailType '" + railType.getClass().getSimpleName() + "' from plugin " + plugin.getName() + ". The rail type has been disabled.", reason);
            RailType.unregister(railType);
        } else {
            logger.log(Level.SEVERE, "An error occurred in RailType '" + railType.getClass().getSimpleName() + "' from an unknown plugin. The rail type has been disabled.", reason);
            RailType.unregister(railType);
        }
    }

    public static void unregister(RailType type) {
        ArrayList<RailType> newValues = new ArrayList<RailType>(values);
        if (newValues.remove(type)) {
            values = newValues;
            type._registered = false;
            RailLookup.forceUnloadRail(type);
        }
    }

    public static void register(RailType type, boolean withPriority) {
        ArrayList<RailType> newValues = new ArrayList<RailType>(values);
        if (withPriority) {
            newValues.add(0, type);
        } else {
            newValues.add(type);
        }
        values = newValues;
        type._registered = true;
        RailLookup.forceRecalculation();
    }

    public static Collection<RailType> values() {
        return values;
    }

    public static RailType getType(Block railsBlock) {
        if (railsBlock != null) {
            return RailType.getType(railsBlock, WorldUtil.getBlockData((Block)railsBlock));
        }
        return NONE;
    }

    public static RailType getType(Block railsBlock, BlockData railsBlockData) {
        for (RailType type : RailType.values()) {
            if (!RailType.checkRailTypeIsAt(type, railsBlock, railsBlockData)) continue;
            return type;
        }
        return NONE;
    }

    public static boolean checkRailTypeIsAt(RailType type, Block railsBlock, BlockData railsBlockData) {
        try {
            return type.isComplexRailBlock() ? type.isRail(railsBlock) : type.isRail(railsBlockData);
        }
        catch (Throwable t) {
            RailType.handleCriticalError(type, t);
            return false;
        }
    }

    public static boolean loadRailInformation(RailState state) {
        state.initEnterDirection();
        state.position().assertAbsolute();
        RailPiece[] cachedPieces = state.railLookup().findAtStatePosition(state);
        if (cachedPieces.length == 0) {
            state.setRailPiece(RailPiece.create(NONE, state.positionBlock(), state.railLookup()));
            return false;
        }
        RailPiece resultPiece = cachedPieces[0];
        if (cachedPieces.length >= 2) {
            RailPath.ProximityInfo nearest = null;
            for (RailPiece piece : cachedPieces) {
                state.setRailPiece(piece);
                RailLogic logic = state.loadRailLogic();
                RailPath path = logic.getPath();
                RailPath.ProximityInfo near = path.getProximityInfo(state.railPosition(), state.motionVector());
                if (nearest != null && near.compareTo(nearest) >= 0) continue;
                nearest = near;
                resultPiece = piece;
            }
        }
        state.setRailPiece(resultPiece);
        return true;
    }

    @Deprecated
    public static RailPiece findRailPiece(Block blockPosition) {
        RailState state = new RailState();
        state.position().setLocationMidOf(blockPosition);
        state.setRailPiece(RailPiece.createWorldPlaceholder(blockPosition.getWorld()));
        if (RailType.loadRailInformation(state)) {
            return state.railPiece();
        }
        return null;
    }

    public static RailPiece findRailPiece(Location position) {
        RailState state = new RailState();
        state.position().setLocation(position);
        state.setRailPiece(RailPiece.createWorldPlaceholder(position.getWorld()));
        if (RailType.loadRailInformation(state)) {
            return state.railPiece();
        }
        return null;
    }

    public abstract boolean isRail(BlockData var1);

    public boolean isRail(World world, int x, int y, int z) {
        return this.isRail(WorldUtil.getBlockData((World)world, (int)x, (int)y, (int)z));
    }

    public final boolean isRail(Block block, BlockFace offset) {
        return this.isRail(block.getWorld(), block.getX() + offset.getModX(), block.getY() + offset.getModY(), block.getZ() + offset.getModZ());
    }

    public final boolean isRail(Block block) {
        return this.isRail(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    public final boolean isRegistered() {
        return this._registered;
    }

    public RailAABB getBoundingBox(RailState state) {
        return RailAABB.BLOCK;
    }

    public final boolean isComplexRailBlock() {
        return this._isComplexRailBlock;
    }

    public final boolean isHandlingPhysics() {
        return this._isHandlingPhysics;
    }

    public boolean isUpsideDown(Block railsBlock) {
        return false;
    }

    @Deprecated
    public Block findRail(Block pos) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public List<Block> findRails(Block positionBlock) {
        Block rail = this.findRail(positionBlock);
        return rail == null ? Collections.emptyList() : Collections.singletonList(rail);
    }

    @Deprecated
    public Block findMinecartPos(Block trackBlock) {
        return trackBlock;
    }

    @Deprecated
    public abstract BlockFace[] getPossibleDirections(Block var1);

    public List<RailJunction> getJunctions(Block railBlock) {
        RailState state = new RailState();
        state.setRailPiece(RailPiece.create(this, railBlock));
        state.position().setLocation(this.getSpawnLocation(railBlock, BlockFace.DOWN));
        state.position().setMotion(BlockFace.DOWN);
        state.initEnterDirection();
        RailPath path = this.getLogic(state).getPath();
        if (path.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(new RailJunction("1", path.getStartPosition()), new RailJunction("2", path.getEndPosition()));
    }

    public RailState takeJunction(Block railBlock, RailJunction junction) {
        RailState state = new RailState();
        state.setRailPiece(RailPiece.create(this, railBlock));
        junction.position().copyTo(state.position());
        state.position().makeAbsolute(railBlock);
        state.position().smallAdvance();
        if (!RailType.loadRailInformation(state)) {
            return null;
        }
        if (state.railType() == this && state.railBlock().equals((Object)railBlock)) {
            return null;
        }
        return state;
    }

    public void switchJunction(Block railBlock, RailJunction from, RailJunction to) {
    }

    @Deprecated
    public BlockFace getDirection(Block railsBlock) {
        RailState state = new RailState();
        state.setRailPiece(RailPiece.create(this, railsBlock));
        state.setPosition(RailPath.Position.fromLocation(this.getSpawnLocation(railsBlock, BlockFace.SELF)));
        state.initEnterDirection();
        return state.enterFace();
    }

    public abstract BlockFace getSignColumnDirection(Block var1);

    public BlockFace[] getSignTriggerDirections(Block railBlock, Block signBlock, BlockFace signFacing) {
        return FaceUtil.BLOCK_SIDES;
    }

    public Block getSignColumnStart(Block railsBlock) {
        return railsBlock;
    }

    public void discoverSigns(RailPiece railPiece, SignControllerWorld signController, List<RailLookup.TrackedSign> result) {
        Block columnStart = this.getSignColumnStart(railPiece.block());
        if (columnStart == null) {
            return;
        }
        BlockFace direction = this.getSignColumnDirection(railPiece.block());
        if (direction == null || direction == BlockFace.SELF) {
            return;
        }
        signController.forEachSignInColumn(columnStart, direction, true, tracker -> {
            result.add(RailLookup.TrackedSign.forRealSign(tracker, true, railPiece));
            if (CommonCapabilities.HAS_SIGN_BACK_TEXT) {
                result.add(RailLookup.TrackedSign.forRealSign(tracker, false, railPiece));
            }
        });
    }

    @Deprecated
    public RailLogic getLogic(MinecartMember<?> member, Block railsBlock, BlockFace direction) {
        return RailLogicAir.INSTANCE;
    }

    public RailLogic getLogic(RailState state) {
        return this.getLogic(state.member(), state.railBlock(), state.enterFace());
    }

    public void onBlockPlaced(Block railsBlock) {
    }

    public void onBlockPhysics(BlockPhysicsEvent event) {
    }

    public boolean isRailsSupported(Block railsBlock) {
        return true;
    }

    public void onPreMove(MinecartMember<?> member) {
    }

    public void onPostMove(MinecartMember<?> member) {
    }

    public boolean onCollide(MinecartMember<?> with, Block block, BlockFace hitFace) {
        return true;
    }

    public boolean hasBlockActivation(Block railBlock) {
        return false;
    }

    public boolean onBlockCollision(MinecartMember<?> member, Block railsBlock, Block hitBlock, BlockFace hitFace) {
        return true;
    }

    public boolean isHeadOnCollision(MinecartMember<?> member, Block railsBlock, Block hitBlock) {
        return false;
    }

    public abstract Location getSpawnLocation(Block var1, BlockFace var2);

    public RailsTexture getRailsTexture(Block railsBlock) {
        return new RailsTexture();
    }

    static {
        for (RailType type : (RailType[])CommonUtil.getClassConstants(RailType.class)) {
            type._registered = true;
            if (type == NONE) continue;
            values.add(type);
        }
    }
}

