/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.Sign
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.events;

import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.PowerState;
import com.bergerkiller.bukkit.tc.SignActionHeader;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.controller.components.RailJunction;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.controller.components.RailTracker;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.rails.direction.RailEnterDirection;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.rails.type.RailTypeRegular;
import com.bergerkiller.bukkit.tc.signactions.SignActionMode;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

public class SignActionEvent
extends Event
implements Cancellable,
TrainCarts.Provider {
    private static final HandlerList handlers = new HandlerList();
    private final RailLookup.TrackedSign sign;
    private final String lowerSecondCleanedLine;
    private RailEnterDirection[] enterDirections;
    private SignActionType actionType;
    private BlockFace raildirection = null;
    private MinecartMember<?> member = null;
    private MinecartGroup group = null;
    private RailState overrideMemberEnterState = null;
    private boolean memberchecked = false;
    private boolean cancelled = false;

    @Deprecated
    public SignActionEvent(Block signblock, MinecartMember<?> member) {
        this(signblock);
        this.member = member;
        this.memberchecked = true;
    }

    @Deprecated
    public SignActionEvent(Block signblock, RailPiece rail, MinecartMember<?> member) {
        this(signblock, rail);
        this.member = member;
        this.memberchecked = true;
    }

    @Deprecated
    public SignActionEvent(Block signblock, MinecartGroup group) {
        this(signblock);
        this.group = group;
        this.memberchecked = true;
    }

    @Deprecated
    public SignActionEvent(Block signblock, RailPiece rail, MinecartGroup group) {
        this(signblock, rail);
        this.group = group;
        this.memberchecked = true;
    }

    @Deprecated
    public SignActionEvent(Block signBlock) {
        this(RailLookup.TrackedSign.forRealSign(signBlock, null));
    }

    @Deprecated
    public SignActionEvent(Block signblock, RailPiece rail) {
        this(RailLookup.TrackedSign.forRealSign(signblock, rail));
    }

    @Deprecated
    public SignActionEvent(Block signblock, Sign sign, RailPiece rail) {
        this(RailLookup.TrackedSign.forRealSign(sign, signblock, rail));
    }

    public SignActionEvent(RailLookup.TrackedSign trackedSign, MinecartMember<?> member) {
        this(trackedSign);
        this.member = member;
        this.memberchecked = true;
    }

    public SignActionEvent(RailLookup.TrackedSign trackedSign, MinecartGroup group) {
        this(trackedSign);
        this.group = group;
        this.memberchecked = true;
    }

    public SignActionEvent(RailLookup.TrackedSign sign) {
        if (sign == null) {
            throw new IllegalArgumentException("Tracked sign is null");
        }
        this.sign = sign;
        this.actionType = SignActionType.NONE;
        this.lowerSecondCleanedLine = Util.cleanSignLine(sign.getLine(1)).toLowerCase(Locale.ENGLISH);
        if (this.sign.getHeader().isLegacyConverted() && this.sign.getHeader().isValid()) {
            this.setLine(0, this.sign.getHeader().toString());
        }
        this.enterDirections = null;
    }

    @Override
    public TrainCarts getTrainCarts() {
        return TrainCarts.plugin;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void setLevers(boolean down) {
        this.getTrackedSign().setOutput(down);
    }

    public boolean isRailsVertical() {
        if (!this.hasRails()) {
            return false;
        }
        BlockFace signDirection = this.getFacing().getOppositeFace();
        RailState state = new RailState();
        state.setRailPiece(this.getRailPiece());
        state.position().setLocation(state.railType().getSpawnLocation(state.railBlock(), signDirection));
        state.position().setMotion(signDirection);
        state.initEnterDirection();
        state.loadRailLogic().getPath().snap(state.position(), state.railBlock());
        return FaceUtil.isVertical((BlockFace)Util.vecToFace(state.position().getMotion(), false));
    }

    public void overrideCartEnterState(RailState enterState) {
        this.overrideMemberEnterState = enterState;
    }

    public Vector getCartEnterDirection() {
        RailState state = this.getCartEnterState();
        if (state != null) {
            return state.enterDirection();
        }
        BlockFace signDirection = this.getWatchedDirections().length > 0 ? this.getWatchedDirections()[0] : this.getFacing().getOppositeFace();
        if (this.hasRails()) {
            RailState state2 = new RailState();
            state2.setRailPiece(this.getRailPiece());
            state2.position().setLocation(state2.railType().getSpawnLocation(state2.railBlock(), signDirection));
            state2.position().setMotion(signDirection);
            state2.initEnterDirection();
            state2.loadRailLogic().getPath().snap(state2.position(), state2.railBlock());
            return state2.position().getMotion();
        }
        return FaceUtil.faceToVector((BlockFace)signDirection);
    }

    public BlockFace getCartEnterFace() {
        RailState state = this.getCartEnterState();
        if (state != null) {
            return state.enterFace();
        }
        BlockFace signDirection = this.getWatchedDirections().length > 0 ? this.getWatchedDirections()[0] : this.getFacing().getOppositeFace();
        if (this.hasRails()) {
            RailState state2 = new RailState();
            state2.setRailPiece(this.getRailPiece());
            state2.position().setLocation(state2.railType().getSpawnLocation(state2.railBlock(), signDirection));
            state2.position().setMotion(signDirection);
            state2.initEnterDirection();
            state2.loadRailLogic().getPath().snap(state2.position(), state2.railBlock());
            state2.initEnterDirection();
            return state2.enterFace();
        }
        return signDirection;
    }

    public RailState getCartEnterState() {
        RailState state = this.overrideMemberEnterState;
        if (state != null) {
            return state;
        }
        if (this.hasRails() && this.hasMember()) {
            RailPiece railPiece = this.getRailPiece();
            for (RailTracker.TrackedRail rail : this.member.getGroup().getRailTracker().getRailInformation()) {
                if (rail.member != this.member || !rail.state.railPiece().equals(railPiece)) continue;
                return rail.state;
            }
        }
        return null;
    }

    @Deprecated
    public BlockFace getCartDirection() {
        return this.getCartEnterFace();
    }

    @Deprecated
    public void setRailsFromTo(BlockFace from, BlockFace to) {
        this.setRailsFromTo(this.findJunction(from), this.findJunction(to));
    }

    @Deprecated
    public void setRailsTo(BlockFace to) {
        this.setRailsTo(this.findJunction(to));
    }

    @Deprecated
    public void setRailsTo(Direction direction) {
        this.setRailsTo(this.findJunction(direction));
    }

    public List<RailJunction> getJunctions() {
        RailPiece piece = this.getRailPiece();
        if (piece.isNone()) {
            return Collections.emptyList();
        }
        return piece.type().getJunctions(piece.block());
    }

    public RailJunction findJunction(String junctionName) {
        for (RailJunction junc : this.getJunctions()) {
            if (!junc.name().equals(junctionName)) continue;
            return junc;
        }
        String dirText = junctionName.toLowerCase(Locale.ENGLISH);
        if (LogicUtil.contains((Object)dirText, (Object[])new String[]{"c", "continue"})) {
            return this.findJunction(Direction.fromFace(this.getCartEnterFace()));
        }
        if (LogicUtil.contains((Object)dirText, (Object[])new String[]{"i", "rev", "reverse", "inverse"})) {
            return this.findJunction(Direction.fromFace(this.getCartEnterFace().getOppositeFace()));
        }
        return this.findJunction(Direction.parse(dirText));
    }

    public RailJunction findJunction(BlockFace face) {
        return RailJunction.findBest(this.getJunctions(), FaceUtil.faceToVector((BlockFace)face)).orElse(null);
    }

    public RailJunction findJunction(Direction direction) {
        if (direction == Direction.NONE || direction == null) {
            return null;
        }
        BlockFace to = direction.getDirection(this.getFacing());
        if ((direction == Direction.IMPLICIT_LEFT || direction == Direction.IMPLICIT_RIGHT) && this.getRailType() instanceof RailTypeRegular && !this.isConnectedRails(to)) {
            to = Direction.FORWARD.getDirection(this.getFacing());
        }
        return this.findJunction(to);
    }

    public RailJunction getEnterJunction() {
        if (this.hasMember()) {
            RailTracker.TrackedRail memberRail = null;
            if (this.hasRails()) {
                Block rails = this.getRails();
                for (RailTracker.TrackedRail rail : this.member.getGroup().getRailTracker().getRailInformation()) {
                    if (rail.member != this.member || !rail.state.railBlock().equals((Object)rails)) continue;
                    memberRail = rail;
                    break;
                }
            }
            if (memberRail == null) {
                memberRail = this.member.getRailTracker().getRail();
            }
            RailState tmp = memberRail.state.cloneAndInvertMotion();
            memberRail.getPath().move(tmp, Double.MAX_VALUE);
            RailPath.Position pos = tmp.position();
            double min_dist = Double.MAX_VALUE;
            RailJunction best_junc = null;
            for (RailJunction junc : memberRail.state.railType().getJunctions(memberRail.state.railBlock())) {
                double dist_sq;
                if (junc.position().relative) {
                    pos.makeRelative(memberRail.state.railBlock());
                } else {
                    pos.makeAbsolute(memberRail.state.railBlock());
                }
                if (!((dist_sq = junc.position().distanceSquared(pos)) < min_dist)) continue;
                min_dist = dist_sq;
                best_junc = junc;
            }
            return best_junc;
        }
        return null;
    }

    public void setRailsTo(String toJunctionName) {
        this.setRailsFromTo(this.getEnterJunction(), this.findJunction(toJunctionName));
    }

    public void setRailsTo(RailJunction toJunction) {
        this.setRailsFromTo(this.getEnterJunction(), toJunction);
    }

    public void setRailsFromTo(String fromJunctionName, String toJunctionName) {
        this.setRailsFromTo(this.findJunction(fromJunctionName), this.findJunction(toJunctionName));
    }

    public void setRailsFromTo(RailJunction fromJunction, String toJunctionName) {
        this.setRailsFromTo(fromJunction, this.findJunction(toJunctionName));
    }

    public void setRailsFromTo(RailJunction fromJunction, RailJunction toJunction) {
        if (!this.hasRails() || fromJunction == null || toJunction == null) {
            return;
        }
        RailPiece rail = this.sign.getRail();
        Predicate<MinecartMember<?>> membersToTeleport = this.isAction(SignActionType.GROUP_ENTER) ? m -> m.getGroup() == this.getGroup() : (this.isAction(SignActionType.MEMBER_ENTER) ? m -> m == this.getMember() : LogicUtil.alwaysTruePredicate());
        if (fromJunction.name().equals(toJunction.name())) {
            RailState state = RailState.getSpawnState(rail);
            RailPath path = state.loadRailLogic().getPath();
            if (path.isEmpty()) {
                for (RailJunction junc : rail.getJunctions()) {
                    if (junc.name().equals(fromJunction.name())) continue;
                    fromJunction = junc;
                    break;
                }
            } else {
                RailPath.Position p0 = path.getStartPosition();
                RailPath.Position p1 = path.getEndPosition();
                double min_dist = Double.MAX_VALUE;
                for (RailJunction junc : rail.getJunctions()) {
                    double dist_sq;
                    if (junc.name().equals(fromJunction.name())) continue;
                    if (junc.position().relative) {
                        p0.makeRelative(rail.block());
                        p1.makeRelative(rail.block());
                    } else {
                        p0.makeAbsolute(rail.block());
                        p1.makeAbsolute(rail.block());
                    }
                    if (!((dist_sq = Math.min(p0.distanceSquared(junc.position()), p1.distanceSquared(junc.position()))) < min_dist)) continue;
                    min_dist = dist_sq;
                    fromJunction = junc;
                }
            }
            rail.switchJunction(fromJunction, toJunction, membersToTeleport);
            if (this.hasMember()) {
                MinecartGroup group = this.member.getGroup();
                if (group != null) {
                    group.getActions().clear();
                    group.split(this.member.getIndex());
                }
                if ((group = this.member.getGroup()) != null) {
                    group.reverse();
                }
            }
            return;
        }
        rail.switchJunction(fromJunction, toJunction, membersToTeleport);
    }

    public SignActionType getAction() {
        return this.actionType;
    }

    public SignActionEvent setAction(SignActionType type) {
        this.actionType = type;
        return this;
    }

    public boolean isAction(SignActionType ... types) {
        return LogicUtil.contains((Object)((Object)this.actionType), (Object[])types);
    }

    public boolean hasRailedMember() {
        return this.hasRails() && this.hasMember();
    }

    public SignActionHeader getHeader() {
        return this.sign.getHeader();
    }

    @Deprecated
    public boolean isPowerInverted() {
        return this.getHeader().isInverted();
    }

    @Deprecated
    public boolean isPowerAlwaysOn() {
        return this.getHeader().isAlwaysOn();
    }

    public PowerState getPower(BlockFace from) {
        return this.sign.getPower(from);
    }

    public boolean isPowered(BlockFace from) {
        if (this.sign.getHeader().isAlwaysOff()) {
            return false;
        }
        return this.sign.getHeader().isAlwaysOn() || this.sign.getHeader().isInverted() != this.getPower(from).hasPower();
    }

    public boolean isPowered() {
        SignActionHeader header = this.sign.getHeader();
        if (header.isAlwaysOff()) {
            return false;
        }
        if (this.actionType == SignActionType.REDSTONE_ON) {
            return true;
        }
        if (header.onPowerRising() || header.onPowerFalling()) {
            return false;
        }
        if (this.actionType == SignActionType.REDSTONE_OFF) {
            return false;
        }
        return header.isAlwaysOn() || this.isPoweredRaw(header.isInverted());
    }

    public boolean isPoweredRaw(boolean invert) {
        if (invert) {
            boolean result = true;
            for (BlockFace face : FaceUtil.BLOCK_SIDES) {
                result &= this.sign.getPower(face) != PowerState.ON;
            }
            return result;
        }
        boolean result = false;
        for (BlockFace face : FaceUtil.BLOCK_SIDES) {
            result |= this.sign.getPower(face).hasPower();
        }
        return result;
    }

    public boolean isPoweredFacing() {
        return this.actionType == SignActionType.REDSTONE_ON || this.isFacing() && this.isPowered();
    }

    public RailLookup.TrackedSign getTrackedSign() {
        return this.sign;
    }

    public Block getBlock() {
        return this.sign.signBlock;
    }

    public Block getAttachedBlock() {
        return this.sign.getAttachedBlock();
    }

    public RailPiece getRailPiece() {
        return this.sign.getRail();
    }

    public RailType getRailType() {
        return this.getRailPiece().type();
    }

    public Block getRails() {
        return this.getRailPiece().block();
    }

    public World getWorld() {
        return this.sign.signBlock.getWorld();
    }

    public boolean hasRails() {
        return !this.getRailPiece().isNone();
    }

    @Deprecated
    public BlockFace getRailDirection() {
        RailPiece rail = this.getRailPiece();
        if (rail.isNone()) {
            return null;
        }
        if (this.raildirection == null) {
            this.raildirection = rail.type().getDirection(rail.block());
        }
        return this.raildirection;
    }

    public Location getCenterLocation() {
        RailPiece railPiece = this.getRailPiece();
        if (railPiece.isNone()) {
            return null;
        }
        return railPiece.type().getSpawnLocation(railPiece.block(), this.getFacing());
    }

    public Location getRailLocation() {
        RailPiece rail = this.sign.getRail();
        if (rail.isNone()) {
            return null;
        }
        return rail.block().getLocation().add(0.5, 0.0, 0.5);
    }

    public Location getLocation() {
        return this.sign.signBlock.getLocation();
    }

    public BlockFace getFacing() {
        return this.sign.getFacing();
    }

    public boolean isFacing() {
        MinecartMember<?> member = this.getMember();
        if (member == null) {
            return false;
        }
        if (!member.isMoving()) {
            return true;
        }
        return this.isEnterActivated();
    }

    public Sign getSign() {
        return this.sign.sign;
    }

    public String[] getExtraLinesBelow() {
        return this.sign.getExtraLines();
    }

    public boolean isConnectedRails(BlockFace direction) {
        return Util.isConnectedRails(this.getRailPiece(), direction);
    }

    public Collection<MinecartGroup> getRCTrainGroups() {
        return MinecartGroup.matchAll(this.getRCName());
    }

    public Collection<TrainProperties> getRCTrainProperties() {
        return TrainProperties.matchAll(this.getRCName());
    }

    public String getRCName() {
        if (this.isRCSign()) {
            return this.sign.getHeader().getRemoteName();
        }
        return null;
    }

    public MinecartMember<?> getMember() {
        if (this.member == null) {
            if (!this.memberchecked) {
                this.member = this.hasRails() ? MinecartMemberStore.getAt(this.getRailPiece().block()) : null;
                this.memberchecked = true;
            }
            if (this.member == null && this.group != null && !this.group.isEmpty()) {
                if (this.actionType == SignActionType.GROUP_LEAVE) {
                    this.member = this.group.tail();
                } else {
                    for (MinecartMember<?> member : this.group) {
                        if (!member.getSignTracker().containsSign(this.sign)) continue;
                        this.member = member;
                        break;
                    }
                    if (this.member == null) {
                        this.member = this.group.head();
                    }
                }
            }
        }
        if (this.member == null || !this.member.isInteractable()) {
            return null;
        }
        return this.member;
    }

    public void setMember(MinecartMember<?> member) {
        this.member = member;
        this.memberchecked = true;
        this.group = member.getGroup();
    }

    public void setGroup(MinecartGroup group) {
        this.member = null;
        this.memberchecked = true;
        this.group = group;
    }

    public boolean hasMember() {
        return this.getMember() != null;
    }

    public boolean isWatchedDirectionsDefined() {
        return this.getHeader().hasEnterDirections();
    }

    public BlockFace[] getWatchedDirections() {
        return RailEnterDirection.toFacesOnly(this.getEnterDirections());
    }

    public RailEnterDirection[] getEnterDirections() {
        if (this.enterDirections == null) {
            if (this.sign.getHeader().hasEnterDirections()) {
                this.enterDirections = this.sign.getHeader().getEnterDirections(this.getRailPiece(), this.getFacing().getOppositeFace());
            } else if (TCConfig.trainsCheckSignFacing) {
                BlockFace[] faces = this.getRailPiece().type().getSignTriggerDirections(this.getRailPiece().block(), this.getBlock(), this.getFacing());
                this.enterDirections = new RailEnterDirection[faces.length];
                for (int i = 0; i < faces.length; ++i) {
                    this.enterDirections[i] = RailEnterDirection.toFace(faces[i]);
                }
            } else {
                this.enterDirections = RailEnterDirection.ALL;
            }
        }
        return this.enterDirections;
    }

    public boolean isEnterActivated(RailState state) {
        for (RailEnterDirection dir : this.getEnterDirections()) {
            if (!dir.match(state)) continue;
            return true;
        }
        return false;
    }

    public boolean isEnterActivated() {
        RailState state = this.getCartEnterState();
        return state != null && this.isEnterActivated(state);
    }

    public BlockFace[] getSpawnDirections() {
        BlockFace[] watched = this.getWatchedDirections();
        BlockFace[] spawndirs = new BlockFace[watched.length];
        for (int i = 0; i < spawndirs.length; ++i) {
            spawndirs[i] = watched[i].getOppositeFace();
        }
        return spawndirs;
    }

    public boolean isWatchedDirection(BlockFace direction) {
        return LogicUtil.contains((Object)RailEnterDirection.toFace(direction), (Object[])this.getEnterDirections());
    }

    public boolean isWatchedDirection(Vector direction) {
        for (RailEnterDirection dir : this.getEnterDirections()) {
            if (!(dir.motionDot(direction) > 0.0)) continue;
            return true;
        }
        return false;
    }

    public MinecartGroup getGroup() {
        if (this.group != null) {
            return this.group;
        }
        MinecartMember<?> mm = this.getMember();
        return mm == null ? null : mm.getGroup();
    }

    public boolean hasGroup() {
        return this.getGroup() != null;
    }

    public Collection<MinecartMember<?>> getMembers() {
        if (this.isTrainSign()) {
            return this.hasGroup() ? this.getGroup() : Collections.EMPTY_LIST;
        }
        if (this.isCartSign()) {
            return this.hasMember() ? Collections.singletonList(this.getMember()) : Collections.EMPTY_LIST;
        }
        if (this.isRCSign()) {
            ArrayList members = new ArrayList();
            for (MinecartGroup group : this.getRCTrainGroups()) {
                members.addAll(group);
            }
            return members;
        }
        return Collections.EMPTY_LIST;
    }

    public String getLine(int index) {
        return Util.cleanSignLine(this.sign.getLine(index));
    }

    public String[] getLines() {
        String[] lines = new String[4];
        for (int i = 0; i < 4; ++i) {
            lines[i] = Util.cleanSignLine(this.sign.getLine(i));
        }
        return lines;
    }

    public void setLine(int index, String line) {
        this.sign.setLine(index, line);
    }

    public SignActionMode getMode() {
        return this.getHeader().getMode();
    }

    public boolean isCartSign() {
        return this.getHeader().isCart();
    }

    public boolean isTrainSign() {
        return this.getHeader().isTrain();
    }

    public boolean isRCSign() {
        return this.getHeader().isRC();
    }

    public boolean isLine(int line, String ... texttypes) {
        String linetext = this.getLine(line).toLowerCase(Locale.ENGLISH);
        for (String type : texttypes) {
            if (!linetext.startsWith(type)) continue;
            return true;
        }
        return false;
    }

    public boolean isType(String ... signtypes) {
        if (this.getHeader().isValid()) {
            String s = this.lowerSecondCleanedLine;
            for (String type : signtypes) {
                if (!s.startsWith(type)) continue;
                return true;
            }
        }
        return false;
    }

    public String getLowerCaseSecondCleanedLine() {
        return this.lowerSecondCleanedLine;
    }

    public String toString() {
        Block signBlock = this.sign.signBlock;
        String text = "{ block=[" + signBlock.getX() + "," + signBlock.getY() + "," + signBlock.getZ() + "]";
        text = text + ", action=" + (Object)((Object)this.actionType);
        text = text + ", watched=[";
        for (int i = 0; i < this.getWatchedDirections().length; ++i) {
            if (i > 0) {
                text = text + ",";
            }
            text = text + this.getWatchedDirections()[i].name();
        }
        text = text + "]";
        if (this.sign == null) {
            text = text + " }";
        } else {
            text = text + ", lines=";
            String[] lines = this.getLines();
            for (int i = 0; i < lines.length; ++i) {
                if (i > 0 && lines[i].length() > 0) {
                    text = text + " ";
                }
                text = text + lines[i];
            }
            text = text + " }";
        }
        return text;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}

