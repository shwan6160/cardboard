/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.mutable.LocationAbstract
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.common.bases.mutable.LocationAbstract;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailTracker;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import java.util.Collections;
import java.util.List;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class WheelTrackerMember {
    public static final double MIN_WHEEL_DISTANCE = 1.0E-5;
    private final MinecartMember<?> _owner;
    private final Wheel _front;
    private final Wheel _back;
    private Quaternion _orientation_last = null;
    private Vector _asyncPosition = null;
    private Vector _position = null;
    private double _centripetalForce = 0.0;
    private double _bankingRoll = 0.0;

    public WheelTrackerMember(MinecartMember<?> owner) {
        this._owner = owner;
        this._front = new Wheel(owner, true);
        this._back = new Wheel(owner, false);
    }

    public MinecartMember<?> getOwner() {
        return this._owner;
    }

    public Wheel front() {
        return this._front;
    }

    public Wheel back() {
        return this._back;
    }

    public Wheel other(Wheel wheel) {
        return this._front == wheel ? this._back : this._front;
    }

    public Wheel movingForwards() {
        return this._owner.isOrientationInverted() ? this._back : this._front;
    }

    public Wheel movingBackwards() {
        return this._owner.isOrientationInverted() ? this._front : this._back;
    }

    public boolean hasWheelDistance() {
        return this._front.getDistance() > 1.0E-5 || this._back.getDistance() > 1.0E-5;
    }

    public Quaternion getLastOrientation() {
        if (this._orientation_last == null) {
            this._orientation_last = this._owner.getOrientation();
        }
        return this._orientation_last;
    }

    public Vector getPosition() {
        if (this._position == null) {
            if (!CommonUtil.isMainThread()) {
                Vector p = this._asyncPosition;
                if (p == null) {
                    p = ((CommonMinecart)this._owner.getEntity()).loc.vector();
                }
                return p;
            }
            double diff = this.back().getDistance() - this.front().getDistance();
            this._position = new Vector();
            this._position.add(this.front().getPosition());
            this._position.add(this.back().getPosition());
            if (diff != 0.0) {
                Vector dir = this.front().getPosition().clone().subtract(this.back().getPosition());
                double n = MathUtil.getNormalizationFactor((Vector)dir);
                if (n < 1.0E10) {
                    dir.multiply(n * diff);
                } else {
                    dir = this.getOwner().getOrientationForward().multiply(diff);
                }
                this._position.add(dir);
            }
            this._position.multiply(0.5);
            this._position.add(((CommonMinecart)this._owner.getEntity()).loc.vector());
            this._asyncPosition = this._position;
        }
        return this._position;
    }

    public double getBankingRoll() {
        return this._bankingRoll;
    }

    public void startTeleport() {
        this._front._invalid = true;
        this._back._invalid = true;
        this._position = null;
    }

    public void update() {
        TrainProperties props;
        Vector up;
        this._orientation_last = this._owner.getOrientation();
        this._position = null;
        this._front.update();
        this._back.update();
        Vector dir = this.front().getPosition().clone().subtract(this.back().getPosition());
        if (dir.lengthSquared() < 1.0E-4) {
            Vector a;
            Vector fwd_b;
            Vector fwd_a = this.front().getForward();
            dir = fwd_a.dot(fwd_b = this.back().getForward()) > 0.0 ? fwd_a.clone().add(fwd_b) : ((a = this._owner.getOrientationForward()).dot(FaceUtil.faceToVector((BlockFace)this._owner.getDirection())) >= 0.0 ? fwd_a : fwd_b);
        }
        if ((up = this.front().getUp().clone().add(this.back().getUp())).lengthSquared() < 1.0E-4) {
            up = this._owner.getOrientation().upVector();
        }
        Quaternion new_orientation = Quaternion.fromLookDirection((Vector)dir, (Vector)up);
        TrainProperties trainProperties = props = this._owner.isUnloaded() ? null : this._owner.getGroup().getProperties();
        if (props != null && props.getBankingStrength() != 0.0) {
            Quaternion q = Quaternion.divide((Quaternion)new_orientation, (Quaternion)this.getLastOrientation());
            double centripetalForceStep = q.forwardVector().getX();
            if (MathUtil.isHeadingTo((BlockFace)this._owner.getDirection(), (Vector)new_orientation.forwardVector())) {
                centripetalForceStep = -centripetalForceStep;
            }
            centripetalForceStep *= this._owner.getRealSpeedLimited();
            if (props.getBankingSmoothness() == 0.0) {
                this._centripetalForce = centripetalForceStep;
            } else {
                this._centripetalForce += centripetalForceStep;
                this._centripetalForce *= 1.0 - 1.0 / props.getBankingSmoothness();
            }
            double angle = MathUtil.atan2((double)this._centripetalForce, (double)(1.0 / props.getBankingStrength()));
            if (props.getBankingSmoothness() == 0.0) {
                this._bankingRoll = angle;
            } else {
                this._bankingRoll += 1.0 / props.getBankingSmoothness() * (angle - this._bankingRoll);
                if (Math.abs(this._bankingRoll) < 0.01) {
                    this._bankingRoll = 0.0;
                }
            }
        } else {
            this._bankingRoll = 0.0;
        }
        if (this._owner.isUnloaded()) {
            this._owner.setOrientation(new_orientation);
        } else {
            this._owner.getRailLogic().onUpdateOrientation(this._owner, new_orientation);
        }
    }

    public static class Wheel {
        private final MinecartMember<?> member;
        private final boolean _front;
        private double _distance = 0.0;
        private final Vector _displayPosition = new Vector();
        private final Vector _position = new Vector();
        private final Vector _forward = new Vector();
        private final Vector _up = new Vector();
        private boolean _invalid = true;
        private boolean _displayInvalid = true;
        private boolean _oriented;
        private final RailPath.Position _railPosition = new RailPath.Position();

        public Wheel(MinecartMember<?> member, boolean front) {
            this.member = member;
            this._front = front;
        }

        public void setDistance(double distance) {
            if (this._distance != distance) {
                this._distance = distance;
                this._invalid = true;
            }
        }

        public double getDistance() {
            return this._distance;
        }

        public double getEdgeDistance() {
            double edgeDistance = 0.5 * (double)((CommonMinecart)this.member.getEntity()).getWidth();
            return edgeDistance -= this._distance;
        }

        public Vector getPosition() {
            if (this._invalid) {
                this.update();
            }
            return this._position;
        }

        public Vector getAbsolutePosition() {
            return this.getPosition().clone().add(((CommonMinecart)this.member.getEntity()).loc.vector());
        }

        public Vector getDisplayPosition() {
            if (this._displayInvalid) {
                if (this._invalid) {
                    this.update();
                }
                LocationAbstract loc = ((CommonMinecart)this.member.getEntity()).loc;
                this._displayPosition.setX(loc.getX() + this._position.getX());
                this._displayPosition.setY(loc.getY() + this._position.getY());
                this._displayPosition.setZ(loc.getZ() + this._position.getZ());
                this._displayInvalid = false;
            }
            return this._displayPosition;
        }

        public Matrix4x4 getAbsoluteTransform() {
            Matrix4x4 result = new Matrix4x4();
            this.getAbsoluteTransform(result);
            return result;
        }

        public void getAbsoluteTransform(Matrix4x4 target) {
            target.setIdentity();
            target.translate(this.getDisplayPosition());
            target.rotate(Quaternion.fromLookDirection((Vector)this._forward.clone(), (Vector)this._up.clone()));
        }

        public Vector getUp() {
            if (this._invalid) {
                this.update();
            }
            return this._up;
        }

        public Vector getForward() {
            if (this._invalid) {
                this.update();
            }
            return this._forward;
        }

        public void update() {
            boolean oriented;
            RailTracker.TrackedRail rail;
            this._invalid = false;
            this._displayInvalid = true;
            List<Object> rails = this.member.isUnloaded() ? Collections.emptyList() : this.member.getGroup().getRailTracker().getRailInformation();
            int railIndex = -1;
            if (!this.member.isDerailed()) {
                for (int i = 0; i < rails.size(); ++i) {
                    rail = (RailTracker.TrackedRail)rails.get(i);
                    if (rail != this.member.getRailTracker().getRail()) continue;
                    railIndex = i;
                    break;
                }
            }
            if (railIndex == -1) {
                Quaternion orientation = this.member.getOrientation();
                Util.setVector(this._up, orientation.upVector());
                Util.setVector(this._forward, orientation.forwardVector());
                Util.setVector(this._position, this._forward);
                this._position.multiply(this._distance);
                if (!this._front) {
                    this._position.multiply(-1.0);
                }
                return;
            }
            RailPath.Position position = this._railPosition;
            position.setLocation(((CommonMinecart)this.member.getEntity()).loc);
            position.setMotion(this.member.getRailTracker().getMotionVector());
            rail = (RailTracker.TrackedRail)rails.get(railIndex);
            rail.getPath().move(position, rail.state.railBlock(), 0.0);
            double initial_position_error = position.distanceSquared(((CommonMinecart)this.member.getEntity()).loc);
            if (initial_position_error > 1.0E-5) {
                int original_rail_index = railIndex;
                if (original_rail_index > 0) {
                    RailPath.Position prev_position = new RailPath.Position();
                    prev_position.setLocation(((CommonMinecart)this.member.getEntity()).loc);
                    prev_position.setMotion(this.member.getRailTracker().getMotionVector());
                    RailTracker.TrackedRail prev_rail = (RailTracker.TrackedRail)rails.get(original_rail_index - 1);
                    prev_rail.getPath().move(prev_position, prev_rail.state.railBlock(), 0.0);
                    double prev_initial_error = prev_position.distanceSquared(((CommonMinecart)this.member.getEntity()).loc);
                    if (prev_initial_error < initial_position_error) {
                        prev_position.copyTo(position);
                        initial_position_error = prev_initial_error;
                        railIndex = original_rail_index - 1;
                    }
                }
                if (original_rail_index < rails.size() - 1) {
                    RailPath.Position next_position = new RailPath.Position();
                    next_position.setLocation(((CommonMinecart)this.member.getEntity()).loc);
                    next_position.setMotion(this.member.getRailTracker().getMotionVector());
                    RailTracker.TrackedRail next_rail = (RailTracker.TrackedRail)rails.get(original_rail_index + 1);
                    next_rail.getPath().move(next_position, next_rail.state.railBlock(), 0.0);
                    double next_initial_error = next_position.distanceSquared(((CommonMinecart)this.member.getEntity()).loc);
                    if (next_initial_error < initial_position_error) {
                        next_position.copyTo(position);
                        initial_position_error = next_initial_error;
                        railIndex = original_rail_index + 1;
                    }
                }
            }
            int order = -1;
            double dot = position.motDot(this.member.getOrientationForward());
            boolean bl = oriented = dot > 0.0;
            if (dot >= -1.0E-4 && dot <= 1.0E-4) {
                oriented = this._oriented;
            }
            this._oriented = oriented;
            if (oriented ^ this._front) {
                position.motX = -position.motX;
                position.motY = -position.motY;
                position.motZ = -position.motZ;
                position.reverse = true;
                order = 1;
            }
            if (this._distance > 1.0E-5) {
                RailTracker.TrackedRail rail2;
                RailPath path;
                double remainingDistance = this._distance;
                for (int index = railIndex; index >= 0 && index < rails.size() && remainingDistance >= 1.0E-4; remainingDistance -= path.move(position, rail2.state.railBlock(), remainingDistance), index += order) {
                    rail2 = (RailTracker.TrackedRail)rails.get(index);
                    path = rail2.getPath();
                }
                position.posX += position.motX * remainingDistance;
                position.posY += position.motY * remainingDistance;
                position.posZ += position.motZ * remainingDistance;
            }
            this._position.setX(position.posX - ((CommonMinecart)this.member.getEntity()).loc.getX());
            this._position.setY(position.posY - ((CommonMinecart)this.member.getEntity()).loc.getY());
            this._position.setZ(position.posZ - ((CommonMinecart)this.member.getEntity()).loc.getZ());
            Quaternion orientation = position.getWheelOrientation();
            Util.setVector(this._up, orientation.upVector());
            Util.setVector(this._forward, orientation.forwardVector());
            if (position.motDot(this._forward) < 0.0) {
                this._forward.multiply(-1.0);
            }
            if (!this._front) {
                this._forward.multiply(-1.0);
            }
            if (TCConfig.wheelTrackerDebugEnabled) {
                Util.spawnBubble(position.toLocation(((CommonMinecart)this.member.getEntity()).getWorld()));
            }
        }
    }
}

