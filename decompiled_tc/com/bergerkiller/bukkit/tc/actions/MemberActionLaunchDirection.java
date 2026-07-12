/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.actions;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.actions.MemberActionLaunch;
import com.bergerkiller.bukkit.tc.actions.MovementAction;
import com.bergerkiller.bukkit.tc.controller.components.ActionTracker;
import com.bergerkiller.bukkit.tc.offline.train.format.OfflineDataBlock;
import com.bergerkiller.bukkit.tc.utils.LauncherConfig;
import java.io.DataInputStream;
import java.io.IOException;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class MemberActionLaunchDirection
extends MemberActionLaunch
implements MovementAction {
    private BlockFace direction;
    private Vector directionVector;
    private boolean isFaceDirection;
    private boolean directionWasCorrected;

    public MemberActionLaunchDirection() {
        this.direction = BlockFace.SELF;
        this.directionVector = new Vector();
        this.isFaceDirection = true;
        this.directionWasCorrected = false;
    }

    @Deprecated
    public MemberActionLaunchDirection(double targetdistance, double targetvelocity, BlockFace direction) {
        this.setDirection(direction);
        this.initDistance(targetdistance, targetvelocity, direction);
    }

    public void init(LauncherConfig config, double targetvelocity, double targetspeedlimit, BlockFace direction) {
        this.setDirection(direction);
        this.init(config, targetvelocity, targetspeedlimit);
    }

    public void init(LauncherConfig config, double targetvelocity, double targetspeedlimit, Vector direction) {
        this.setDirection(direction);
        this.init(config, targetvelocity, targetspeedlimit);
    }

    public void init(LauncherConfig config, double targetvelocity, BlockFace direction) {
        this.setDirection(direction);
        this.init(config, targetvelocity);
    }

    public void init(LauncherConfig config, double targetvelocity, Vector direction) {
        this.setDirection(direction);
        this.init(config, targetvelocity);
    }

    public void initTime(int timeTicks, double targetvelocity, BlockFace direction) {
        this.setDirection(direction);
        this.initTime(timeTicks, targetvelocity);
    }

    public void initTime(int timeTicks, double targetvelocity, Vector direction) {
        this.setDirection(direction);
        this.initTime(timeTicks, targetvelocity);
    }

    public void initDistance(double targetdistance, double targetvelocity, BlockFace direction) {
        this.setDirection(direction);
        this.initDistance(targetdistance, targetvelocity);
    }

    public void initDistance(double targetdistance, double targetvelocity, Vector direction) {
        this.setDirection(direction);
        this.initDistance(targetdistance, targetvelocity);
    }

    public void setDirection(BlockFace direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Direction is null");
        }
        this.direction = direction;
        this.directionVector = direction == BlockFace.SELF ? new Vector() : FaceUtil.faceToVector((BlockFace)direction).normalize();
        this.isFaceDirection = true;
    }

    public void setDirection(Vector direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Direction is null");
        }
        this.direction = FaceUtil.vectorToBlockFace((Vector)direction, (boolean)true);
        this.directionVector = direction;
        this.isFaceDirection = false;
    }

    public BlockFace getDirection() {
        return this.direction;
    }

    public Vector getDirectionVector() {
        return this.directionVector;
    }

    public boolean isFaceDirection() {
        return this.isFaceDirection;
    }

    public void setDirectionCorrected(boolean corrected) {
        this.directionWasCorrected = corrected;
    }

    public boolean isDirectionCorrected() {
        return this.directionWasCorrected;
    }

    @Override
    public boolean update() {
        Vector vel;
        boolean success = super.update();
        if (!this.directionWasCorrected && (vel = ((CommonMinecart)this.getMember().getEntity()).getVelocity()).lengthSquared() > 1.0E-20) {
            this.directionWasCorrected = true;
            if (vel.dot(this.directionVector) < 0.0) {
                this.getGroup().reverse();
            }
        }
        return success;
    }

    public static abstract class BaseSerializer<T extends MemberActionLaunchDirection>
    extends MemberActionLaunch.BaseSerializer<T> {
        @Override
        public boolean save(T action, OfflineDataBlock data, ActionTracker tracker) throws IOException {
            super.save(action, data, tracker);
            data.addChild("launch-direction", stream -> {
                Util.writeVariableLengthInt(stream, action.getDirection().ordinal());
                stream.writeBoolean(action.isDirectionCorrected());
            });
            if (!((MemberActionLaunchDirection)action).isFaceDirection()) {
                data.addChild("launch-direction-vector", stream -> {
                    Vector v = action.getDirectionVector();
                    stream.writeDouble(v.getX());
                    stream.writeDouble(v.getY());
                    stream.writeDouble(v.getZ());
                });
            }
            return true;
        }

        @Override
        public T load(OfflineDataBlock data, ActionTracker tracker) throws IOException {
            MemberActionLaunchDirection action = (MemberActionLaunchDirection)super.load(data, tracker);
            try (DataInputStream stream2 = data.findChildOrThrow("launch-direction").readData();){
                int blockFaceOrd = Util.readVariableLengthInt(stream2);
                BlockFace[] faces = BlockFace.values();
                action.setDirection(blockFaceOrd >= 0 && blockFaceOrd < faces.length ? faces[blockFaceOrd] : BlockFace.NORTH);
                action.setDirectionCorrected(stream2.readBoolean());
            }
            data.tryReadChild("launch-direction-vector", stream -> {
                Vector v = new Vector(stream.readDouble(), stream.readDouble(), stream.readDouble());
                action.setDirection(v);
            });
            return (T)action;
        }
    }

    public static class Serializer
    extends BaseSerializer<MemberActionLaunchDirection> {
        @Override
        public MemberActionLaunchDirection create(OfflineDataBlock data) throws IOException {
            return new MemberActionLaunchDirection();
        }
    }
}

