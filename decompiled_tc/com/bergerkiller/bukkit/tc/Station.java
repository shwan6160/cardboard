/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.mutable.VectorAbstract
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.material.Sign
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc;

import com.bergerkiller.bukkit.common.bases.mutable.VectorAbstract;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.actions.MemberActionLaunchDirection;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.ActionTrackerGroup;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.utils.LauncherConfig;
import com.bergerkiller.bukkit.tc.utils.TrackWalkingPoint;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Sign;
import org.bukkit.util.Vector;

public class Station {
    private final SignActionEvent info;
    private final LauncherConfig launchConfig;
    private final double launchForce;
    private final long delay;
    private final BlockFace instruction;
    private final Direction nextDirection;
    private final double centerOffset;
    private boolean wasCentered = false;
    private boolean autoRoute = false;

    public Station(SignActionEvent info) {
        this(info, StationConfig.fromSign(info));
    }

    public Station(SignActionEvent info, StationConfig config) {
        this.info = info;
        this.delay = config.getDelay();
        this.instruction = config.getInstruction();
        this.launchForce = config.getLaunchSpeed();
        this.launchConfig = config.getLaunchConfig();
        this.centerOffset = config.getOffsetFromCenter();
        this.nextDirection = config.getNextDirection();
        this.autoRoute = config.isAutoRouting();
    }

    public SignActionEvent getSignInfo() {
        return this.info;
    }

    public String getTag() {
        return StringUtil.blockToString((Block)this.info.getBlock());
    }

    public boolean hasDelay() {
        return this.delay > 0L;
    }

    public long getDelay() {
        return this.delay;
    }

    public LauncherConfig getLaunchConfig() {
        return this.launchConfig;
    }

    public BlockFace getInstruction() {
        return this.instruction;
    }

    public BlockFace getNextDirectionFace() {
        return this.getNextDirection().getDirectionLegacy(this.info.getFacing(), this.info.getMember().getDirection());
    }

    public Direction getNextDirection() {
        return this.nextDirection;
    }

    public boolean isAutoRouting() {
        return this.autoRoute;
    }

    public MinecartGroup getGroup() {
        return this.info.getGroup();
    }

    @Deprecated
    public MinecartMember<?> getCenterCart(int offset) {
        double d2;
        MinecartGroup group = this.getGroup();
        int size = group.size();
        if (this.info.isCartSign()) {
            return this.info.getMember();
        }
        if ((size & 1) == 1) {
            int index = (int)Math.floor((double)size / 2.0);
            if (offset != 0 && size >= 3) {
                double d22;
                Location s = this.info.getCenterLocation();
                double d1 = ((CommonMinecart)((MinecartMember)group.get((int)(index - 1))).getEntity()).loc.distance(s);
                index = d1 < (d22 = ((CommonMinecart)((MinecartMember)group.get((int)(index + 1))).getEntity()).loc.distance(s)) ? (index += offset) : (index -= offset);
            }
            return (MinecartMember)group.get(index);
        }
        int mIdx1 = (int)Math.ceil((double)size / 2.0) - 1;
        int mIdx2 = mIdx1 + 1;
        Location s = this.info.getCenterLocation();
        double d1 = ((CommonMinecart)((MinecartMember)group.get((int)mIdx1)).getEntity()).loc.distance(s);
        if (d1 > (d2 = ((CommonMinecart)((MinecartMember)group.get((int)mIdx2)).getEntity()).loc.distance(s))) {
            return (MinecartMember)group.get(mIdx1 + offset);
        }
        return (MinecartMember)group.get(mIdx2 - offset);
    }

    @Deprecated
    public MinecartMember<?> getCenterCart() {
        return this.getCenterCart(0);
    }

    public MinecartMember<?> getCenterPositionCart() {
        MinecartGroup group = this.getGroup();
        if (group.size() == 1) {
            return (MinecartMember)group.get(0);
        }
        if (this.info.isCartSign()) {
            return this.info.getMember();
        }
        double total_size = 0.5 * (double)((CommonMinecart)group.head().getEntity()).getWidth();
        for (int i = 1; i < group.size(); ++i) {
            total_size += ((CommonMinecart)((MinecartMember)group.get((int)i)).getEntity()).loc.distance((VectorAbstract)((CommonMinecart)((MinecartMember)group.get((int)(i - 1))).getEntity()).loc);
        }
        double half_size = (total_size += 0.5 * (double)((CommonMinecart)group.tail().getEntity()).getWidth()) * 0.5;
        double accum_size = 0.5 * (double)((CommonMinecart)group.head().getEntity()).getWidth();
        if (accum_size > half_size) {
            return group.head();
        }
        for (int i = 1; i < group.size(); ++i) {
            double new_accum_size = accum_size;
            if ((new_accum_size += ((CommonMinecart)((MinecartMember)group.get((int)i)).getEntity()).loc.distance((VectorAbstract)((CommonMinecart)((MinecartMember)group.get((int)(i - 1))).getEntity()).loc)) > half_size) {
                double d_prev = half_size - accum_size;
                double d_curr = new_accum_size - half_size;
                if (d_prev < d_curr) {
                    return (MinecartMember)group.get(i - 1);
                }
                return (MinecartMember)group.get(i);
            }
            accum_size = new_accum_size;
        }
        return group.tail();
    }

    public void waitTrain(long delay) {
        this.waitTrainKeepLeversDown(delay);
        if (delay > 0L) {
            this.setLevers(false);
        }
    }

    public void waitTrainKeepLeversDown(long delay) {
        ActionTrackerGroup actions = this.info.getGroup().getActions();
        if (TCConfig.playHissWhenStopAtStation) {
            actions.addActionSizzle().addTag(this.getTag());
        }
        if (TCConfig.refillAtStations) {
            actions.addActionRefill().addTag(this.getTag());
        }
        this.setLevers(true);
        if (delay == Long.MAX_VALUE) {
            actions.addActionWaitForever().addTag(this.getTag());
        } else if (delay > 0L) {
            actions.addActionWait(delay).addTag(this.getTag());
        }
    }

    public void setLevers(boolean down) {
        this.info.getGroup().getActions().addActionSetSignOutput(this.info.getTrackedSign(), down).addTag(this.getTag());
    }

    public void centerTrain() {
        CartToStationInfo stationInfo = this.getCartToStationInfo();
        if (!this.info.getGroup().getActions().hasAction() && stationInfo.distance <= 0.01) {
            this.info.getGroup().stop();
        } else if (stationInfo.cartDir != null) {
            stationInfo.cart.getActions().addActionLaunch(stationInfo.cartDir, stationInfo.distance, 0.0).addTag(this.getTag());
        } else {
            stationInfo.cart.getActions().addActionLaunch(stationInfo.centerLocation, 0.0).addTag(this.getTag());
        }
        this.wasCentered = true;
    }

    public void launchTo(BlockFace direction) {
        if (!this.wasCentered) {
            CartToStationInfo stationInfo = this.getCartToStationInfo();
            if (stationInfo.cartDir == direction && this.launchConfig.hasDistance()) {
                this.launchConfig.setDistance(this.launchConfig.getDistance() + stationInfo.distance);
            }
        }
        this.setLevers(false);
        MemberActionLaunchDirection action = this.getCenterPositionCart().getActions().addActionLaunch(direction, this.launchConfig, this.launchForce);
        action.addTag(this.getTag());
        this.wasCentered = false;
    }

    private CartToStationInfo getCartToStationInfo() {
        MinecartGroup group;
        CartToStationInfo info = new CartToStationInfo();
        info.cart = this.getCenterPositionCart();
        info.centerLocation = this.info.getCenterLocation();
        RailState centercart_state = info.cart.getRailTracker().getState();
        RailState centercart_state_inv = centercart_state.clone();
        centercart_state_inv.position().invertMotion();
        centercart_state_inv.initEnterDirection();
        info.distance = centercart_state.position().distance(info.centerLocation);
        info.cartDir = Util.vecToFace(info.cart.getRailTracker().getMotionVector(), false);
        info.centerMoveDir = info.cart.getRailTracker().getMotionVector();
        double maxDistance = 2.0 * info.distance;
        TrackWalkingPoint p = new TrackWalkingPoint(centercart_state);
        TrackWalkingPoint p_inv = new TrackWalkingPoint(centercart_state_inv);
        if (p.moveFindRail(this.info.getRails(), maxDistance)) {
            maxDistance = p.movedTotal;
            info.distance = p.movedTotal;
            info.centerMoveDir = p.state.motionVector();
        }
        if (p_inv.moveFindRail(this.info.getRails(), maxDistance)) {
            p = p_inv;
            maxDistance = p.movedTotal;
            info.distance = p.movedTotal;
            info.centerMoveDir = p.state.motionVector();
            info.cartDir = info.cartDir.getOppositeFace();
        }
        if ((group = this.getGroup()).size() > 1 && !this.info.isCartSign()) {
            double center_size;
            double total_size = center_size = 0.5 * (double)((CommonMinecart)((MinecartMember)group.get(0)).getEntity()).getWidth();
            for (int i = 1; i < group.size(); ++i) {
                MinecartMember m = (MinecartMember)group.get(i);
                total_size += 0.5 * (double)((CommonMinecart)m.getEntity()).getWidth();
                total_size += 0.5 * (double)((CommonMinecart)((MinecartMember)group.get(i - 1)).getEntity()).getWidth();
                total_size += m.getCartCouplerLength() + ((MinecartMember)group.get(i - 1)).getCartCouplerLength();
                if (m != info.cart) continue;
                center_size = total_size;
            }
            info.distance += 0.5 * (total_size += 0.5 * (double)((CommonMinecart)group.tail().getEntity()).getWidth()) - center_size;
        }
        if (this.centerOffset != 0.0) {
            Vector stationMoveDir = info.centerMoveDir.clone();
            if (stationMoveDir.getX() + stationMoveDir.getY() + stationMoveDir.getZ() < 0.0) {
                stationMoveDir.multiply(-1.0);
            }
            Vector facingVec = FaceUtil.faceToVector((BlockFace)this.info.getFacing());
            if (stationMoveDir.dot(facingVec = new Vector(facingVec.getZ(), facingVec.getY(), facingVec.getX())) < 0.0) {
                stationMoveDir.multiply(-1.0);
            }
            info.distance = stationMoveDir.dot(info.centerMoveDir) < 0.0 ? (info.distance += this.centerOffset) : (info.distance -= this.centerOffset);
        }
        return info;
    }

    public static class StationConfig {
        private double _offsetFromCenter = 0.0;
        private Direction _nextDirection = Direction.NONE;
        private double _launchSpeed = TCConfig.launchForce;
        private LauncherConfig _launchConfig = LauncherConfig.createDefault();
        private BlockFace _instruction = null;
        private long _delay = 0L;
        private boolean _autoRoute = false;
        private static final Pattern STATION_OFFSET_PATTERN = Pattern.compile("(?:^|\\s|[a-zA-Z])((?:\\-)?[\\d.,]+)m(?:$|\\s|[0-9\\-])");

        public double getOffsetFromCenter() {
            return this._offsetFromCenter;
        }

        public void setOffsetFromCenter(double offset) {
            this._offsetFromCenter = offset;
        }

        public Direction getNextDirection() {
            return this._nextDirection;
        }

        public void setNextDirection(Direction nextDirection) {
            this._nextDirection = nextDirection;
        }

        public double getLaunchSpeed() {
            return this._launchSpeed;
        }

        public void setLaunchSpeed(double speed) {
            this._launchSpeed = speed;
        }

        public LauncherConfig getLaunchConfig() {
            return this._launchConfig;
        }

        public void setLaunchConfig(LauncherConfig config) {
            this._launchConfig = config;
        }

        public long getDelay() {
            return this._delay;
        }

        public void setDelay(long delay) {
            this._delay = delay;
        }

        public boolean isAutoRouting() {
            return this._autoRoute;
        }

        public void setAutoRouting(boolean autoRoute) {
            this._autoRoute = autoRoute;
        }

        public BlockFace getInstruction() {
            return this._instruction;
        }

        public void setInstruction(BlockFace instruction) {
            this._instruction = instruction;
        }

        public void setAutoModeUsingSign(SignActionEvent info) {
            for (String part : info.getLine(3).split(" ")) {
                if (part.equalsIgnoreCase("route")) {
                    this.setAutoRouting(true);
                    continue;
                }
                Direction direction = Direction.parse(part);
                if (direction != Direction.NONE) {
                    this.setNextDirection(direction);
                    continue;
                }
                this.setLaunchSpeed(StationConfig.parseLaunchForce(part, info));
            }
        }

        public void setInstructionUsingSign(SignActionEvent info) {
            if (info.isRailsVertical()) {
                boolean up = info.isPowered(BlockFace.UP);
                boolean down = info.isPowered(BlockFace.DOWN);
                if (up && !down) {
                    this.setInstruction(BlockFace.UP);
                } else if (!up && down) {
                    this.setInstruction(BlockFace.DOWN);
                } else if (info.isPowered()) {
                    this.setInstruction(BlockFace.SELF);
                } else {
                    this.setInstruction(null);
                }
            } else {
                Vector railDirection = info.getCartEnterDirection();
                if (Util.isDiagonal(railDirection)) {
                    Sign sign_material = (Sign)BlockUtil.getData((Block)info.getBlock(), Sign.class);
                    if (!info.getTrackedSign().isRealSign() || sign_material == null || sign_material.isWallSign()) {
                        BlockFace facing = info.getFacing();
                        if (FaceUtil.isAlongX((BlockFace)facing)) {
                            boolean north = info.isPowered(BlockFace.NORTH);
                            boolean south = info.isPowered(BlockFace.SOUTH);
                            if (north && !south) {
                                this.setInstruction(BlockFace.NORTH);
                            } else if (south && !north) {
                                this.setInstruction(BlockFace.SOUTH);
                            } else if (info.isPowered()) {
                                this.setInstruction(BlockFace.SELF);
                            } else {
                                this.setInstruction(null);
                            }
                        } else {
                            boolean west = info.isPowered(BlockFace.WEST);
                            boolean east = info.isPowered(BlockFace.EAST);
                            if (west && !east) {
                                this.setInstruction(BlockFace.WEST);
                            } else if (east && !west) {
                                this.setInstruction(BlockFace.EAST);
                            } else if (info.isPowered()) {
                                this.setInstruction(BlockFace.SELF);
                            } else {
                                this.setInstruction(null);
                            }
                        }
                    } else {
                        boolean pow2;
                        BlockFace face_x = railDirection.getX() > 0.0 ? BlockFace.EAST : BlockFace.WEST;
                        BlockFace face_z = railDirection.getZ() > 0.0 ? BlockFace.SOUTH : BlockFace.NORTH;
                        boolean pow1 = info.isPowered(face_x) || info.isPowered(face_z);
                        boolean bl = pow2 = info.isPowered(face_x.getOppositeFace()) || info.isPowered(face_z.getOppositeFace());
                        if (pow1 && !pow2) {
                            this.setInstruction(FaceUtil.combine((BlockFace)face_x, (BlockFace)face_z));
                        } else if (!pow1 && pow2) {
                            this.setInstruction(FaceUtil.combine((BlockFace)face_x.getOppositeFace(), (BlockFace)face_z.getOppositeFace()));
                        } else if (info.isPowered()) {
                            this.setInstruction(BlockFace.SELF);
                        } else {
                            this.setInstruction(null);
                        }
                    }
                } else if (Math.abs(railDirection.getX()) > Math.abs(railDirection.getZ())) {
                    boolean west = info.isPowered(BlockFace.WEST);
                    boolean east = info.isPowered(BlockFace.EAST);
                    if (west && !east) {
                        this.setInstruction(BlockFace.WEST);
                    } else if (east && !west) {
                        this.setInstruction(BlockFace.EAST);
                    } else if (info.isPowered()) {
                        this.setInstruction(BlockFace.SELF);
                    } else {
                        this.setInstruction(null);
                    }
                } else {
                    boolean north = info.isPowered(BlockFace.NORTH);
                    boolean south = info.isPowered(BlockFace.SOUTH);
                    if (north && !south) {
                        this.setInstruction(BlockFace.NORTH);
                    } else if (south && !north) {
                        this.setInstruction(BlockFace.SOUTH);
                    } else if (info.isPowered()) {
                        this.setInstruction(BlockFace.SELF);
                    } else {
                        this.setInstruction(null);
                    }
                }
            }
        }

        public static StationConfig fromSign(SignActionEvent info) {
            StationConfig config = new StationConfig();
            config.setDelay(ParseUtil.parseTime((String)info.getLine(2)));
            config.setAutoModeUsingSign(info);
            config.setInstructionUsingSign(info);
            String launchConfigStr = info.getLine(1).trim();
            int i = 0;
            while (true) {
                if (i == launchConfigStr.length()) {
                    launchConfigStr = "";
                    break;
                }
                char c = launchConfigStr.charAt(i);
                if (!Character.isLetter(c) && c != ' ') {
                    launchConfigStr = launchConfigStr.substring(i);
                    break;
                }
                ++i;
            }
            Matcher matcher = STATION_OFFSET_PATTERN.matcher(launchConfigStr);
            if (matcher.find()) {
                config.setOffsetFromCenter(ParseUtil.parseDouble((String)matcher.group(1), (double)0.0));
                launchConfigStr = launchConfigStr.substring(0, matcher.start(1)) + " " + launchConfigStr.substring(matcher.end(1) + 1);
                while (launchConfigStr.startsWith(" ")) {
                    launchConfigStr = launchConfigStr.substring(1);
                }
            }
            config.setLaunchConfig(LauncherConfig.parse(launchConfigStr));
            if (!(config.getLaunchConfig().hasDuration() || config.getLaunchConfig().hasDistance() || config.getLaunchConfig().hasAcceleration() || config.getInstruction() == null)) {
                double length;
                BlockFace launchDir = config.getInstruction();
                if (launchDir == BlockFace.SELF) {
                    launchDir = config.getNextDirection().getDirectionLegacy(info.getFacing(), info.getMember().getDirection());
                }
                if ((length = Util.calculateStraightLength(info.getRails(), launchDir)) == 0.0) {
                    length += 1.0;
                }
                config.getLaunchConfig().setDistance(length);
            }
            return config;
        }

        private static double parseLaunchForce(String text, SignActionEvent info) {
            if (text.equalsIgnoreCase("max") && info.hasGroup()) {
                return info.getGroup().getProperties().getSpeedLimit();
            }
            return Util.parseVelocity(text, TCConfig.launchForce);
        }
    }

    private static class CartToStationInfo {
        public MinecartMember<?> cart;
        public BlockFace cartDir;
        public Vector centerMoveDir;
        public double distance;
        public Location centerLocation;

        private CartToStationInfo() {
        }
    }
}

