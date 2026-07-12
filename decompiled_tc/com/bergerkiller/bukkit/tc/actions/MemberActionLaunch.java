/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.actions;

import com.bergerkiller.bukkit.tc.actions.MemberAction;
import com.bergerkiller.bukkit.tc.actions.MovementAction;
import com.bergerkiller.bukkit.tc.actions.registry.ActionRegistry;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.ActionTracker;
import com.bergerkiller.bukkit.tc.controller.status.TrainStatus;
import com.bergerkiller.bukkit.tc.offline.train.format.OfflineDataBlock;
import com.bergerkiller.bukkit.tc.utils.LaunchFunction;
import com.bergerkiller.bukkit.tc.utils.LauncherConfig;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class MemberActionLaunch
extends MemberAction
implements MovementAction {
    private static final double minVelocity = 0.001;
    private static final double minLaunchVelocity = 0.05;
    private double distanceoffset;
    private int timeoffset;
    private double targetvelocity;
    private double targetspeedlimit;
    private double distance;
    private double lastVelocity;
    private double lastspeedlimit;
    private LauncherConfig config;
    private LaunchFunction function;

    public MemberActionLaunch() {
        this.init(LauncherConfig.createDefault(), 0.0);
    }

    public void init(LauncherConfig config, double targetvelocity) {
        this.init(config, targetvelocity, Double.NaN);
    }

    public void init(LauncherConfig config, double targetvelocity, double targetspeedlimit) {
        this.config = config;
        this.targetvelocity = targetvelocity;
        this.targetspeedlimit = targetspeedlimit;
        this.timeoffset = 0;
        this.distanceoffset = 0.0;
        this.initFunction();
        if (this.config.hasDistance() && this.config.getDistance() < 0.001) {
            this.config.setDuration(0);
        }
        this.distance = 0.0;
        this.lastVelocity = 0.0;
    }

    private void initFunction() {
        try {
            this.function = this.config.getFunction().newInstance();
        }
        catch (Throwable t) {
            this.getTrainCarts().getLogger().log(Level.SEVERE, "Unhandled error initializing launch function", t);
            this.function = new LaunchFunction.Linear();
        }
    }

    @Deprecated
    public MemberActionLaunch(double targetdistance, double targetvelocity) {
        this();
        this.initDistance(targetdistance, targetvelocity);
    }

    public void initTime(int timeTicks, double targetvelocity) {
        LauncherConfig newConfig = new LauncherConfig();
        newConfig.setFunction(this.function.getClass());
        newConfig.setDuration(timeTicks);
        this.init(newConfig, targetvelocity);
    }

    public void initDistance(double targetdistance, double targetvelocity) {
        LauncherConfig newConfig = new LauncherConfig();
        newConfig.setFunction(this.function.getClass());
        newConfig.setDistance(targetdistance);
        this.init(newConfig, targetvelocity);
    }

    public void setFunction(Class<? extends LaunchFunction> function) {
        LauncherConfig newConfig = this.config.clone();
        newConfig.setFunction(function);
        this.init(newConfig, this.targetvelocity);
    }

    @Override
    public List<TrainStatus> getStatusInfo() {
        return Collections.singletonList(new TrainStatus.Launching(this.targetvelocity, this.targetspeedlimit, this.config));
    }

    @Override
    public void start() {
        this.lastVelocity = this.getMember().getRealSpeedLimited();
        this.lastspeedlimit = this.getGroup().getProperties().getSpeedLimit();
        if (!Double.isNaN(this.targetspeedlimit) && this.targetspeedlimit > this.lastspeedlimit) {
            this.getGroup().getProperties().setSpeedLimit(this.targetspeedlimit);
            this.lastspeedlimit = this.targetspeedlimit;
        }
        this.function.setMinimumVelocity(0.001);
        this.function.setMaximumVelocity(Double.isNaN(this.targetspeedlimit) ? this.lastspeedlimit : Math.max(this.targetspeedlimit, this.lastspeedlimit));
        this.function.setVelocityRange(this.lastVelocity, Double.isNaN(this.targetspeedlimit) ? this.targetvelocity : Math.min(this.targetspeedlimit, this.targetvelocity));
        if (this.function.getStartVelocity() < 0.05 && this.function.getEndVelocity() < 0.05) {
            this.function.setStartVelocity(0.05);
        }
        this.function.configure(this.config);
    }

    @Override
    public boolean isMovementSuppressed() {
        return true;
    }

    public double getTargetVelocity() {
        return this.targetvelocity;
    }

    public double getTargetDistance() {
        return this.config.getDistance();
    }

    protected void setTargetDistance(double distance) {
        this.config.setDistance(distance);
    }

    public double getDistance() {
        return this.distance;
    }

    @Override
    public boolean update() {
        int time;
        if (this.getMember().isDerailed() && !this.getMember().isMovingVerticalOnly()) {
            this.onLaunchingDone(false);
            return true;
        }
        if (this.function.isInstantaneous()) {
            this.onLaunchingDone(true);
            return true;
        }
        if (this.lastspeedlimit != this.getGroup().getProperties().getSpeedLimit()) {
            this.lastspeedlimit = this.getGroup().getProperties().getSpeedLimit();
            this.targetspeedlimit = Double.NaN;
            this.function.setMaximumVelocity(this.lastspeedlimit);
            this.function.setVelocityRange(this.lastVelocity, this.targetvelocity);
            this.timeoffset = this.elapsedTicks();
            this.distanceoffset = this.distance;
            if (this.config.hasDuration()) {
                this.config.setDuration(this.config.getDuration() - this.timeoffset);
            } else if (this.config.hasDistance()) {
                this.config.setDistance(this.config.getDistance() - this.distanceoffset);
            }
            this.function.configure(this.config);
        }
        if (this.distance != 0.0) {
            for (MinecartMember<?> mm : this.getGroup()) {
                if (!(mm.getRealSpeed() < 0.001) || !(this.lastVelocity > 0.01)) continue;
                this.onLaunchingDone(false);
                return true;
            }
        }
        if ((time = this.elapsedTicks() - this.timeoffset) > this.function.getTotalTime()) {
            this.onLaunchingDone(true);
            return true;
        }
        this.lastVelocity = this.function.getDistance(time) - this.distance + this.distanceoffset;
        this.getGroup().setForwardForce(this.lastVelocity / (double)this.getGroup().getUpdateStepCount());
        if (this.isFullTick()) {
            this.distance += this.lastVelocity;
        }
        return false;
    }

    private void onLaunchingDone(boolean successful) {
        if (!Double.isNaN(this.targetspeedlimit) && this.targetspeedlimit < this.lastspeedlimit) {
            this.getGroup().getProperties().setSpeedLimit(this.targetspeedlimit);
        }
        if (successful) {
            this.getGroup().setForwardForce(this.targetvelocity / (double)this.getGroup().getUpdateStepCount());
        }
    }

    protected static void saveStateTo(DataOutputStream stream, MemberActionLaunch action) throws IOException {
        stream.writeDouble(action.distanceoffset);
        stream.writeInt(action.timeoffset);
        stream.writeDouble(action.targetvelocity);
        stream.writeDouble(action.targetspeedlimit);
        stream.writeDouble(action.distance);
        stream.writeDouble(action.lastVelocity);
        stream.writeDouble(action.lastspeedlimit);
        action.config.writeTo(stream);
        stream.writeDouble(action.function.getMinimumVelocity());
        stream.writeDouble(action.function.getMaximumVelocity());
        stream.writeDouble(action.function.getStartVelocity());
        stream.writeDouble(action.function.getEndVelocity());
    }

    protected static void loadStateFrom(DataInputStream stream, MemberActionLaunch action) throws IOException {
        action.distanceoffset = stream.readDouble();
        action.timeoffset = stream.readInt();
        action.targetvelocity = stream.readDouble();
        action.targetspeedlimit = stream.readDouble();
        action.distance = stream.readDouble();
        action.lastVelocity = stream.readDouble();
        action.lastspeedlimit = stream.readDouble();
        action.config = LauncherConfig.readFrom(stream);
        action.initFunction();
        action.function.setMinimumVelocity(stream.readDouble());
        action.function.setMaximumVelocity(stream.readDouble());
        action.function.setStartVelocity(stream.readDouble());
        action.function.setEndVelocity(stream.readDouble());
        action.function.configure(action.config);
    }

    public static abstract class BaseSerializer<T extends MemberActionLaunch>
    implements ActionRegistry.Serializer<T> {
        @Override
        public boolean save(T action, OfflineDataBlock data, ActionTracker tracker) throws IOException {
            data.addChild("launch-state", stream -> MemberActionLaunch.saveStateTo(stream, action));
            return true;
        }

        public abstract T create(OfflineDataBlock var1) throws IOException;

        @Override
        public T load(OfflineDataBlock data, ActionTracker tracker) throws IOException {
            T action = this.create(data);
            try (DataInputStream stream = data.findChildOrThrow("launch-state").readData();){
                MemberActionLaunch.loadStateFrom(stream, action);
            }
            return action;
        }
    }

    public static class Serializer
    extends BaseSerializer<MemberActionLaunch> {
        @Override
        public MemberActionLaunch create(OfflineDataBlock data) throws IOException {
            return new MemberActionLaunch();
        }
    }
}

