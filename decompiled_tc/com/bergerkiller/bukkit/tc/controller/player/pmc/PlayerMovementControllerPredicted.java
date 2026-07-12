/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.DebugUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.player.pmc;

import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.DebugUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.controller.player.pmc.PlayerMovementController;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

abstract class PlayerMovementControllerPredicted
extends PlayerMovementController {
    protected static final double MIN_MOTION = 0.003;
    protected static final MovementFrictionUpdate FRICTION_UPDATE = new MovementFrictionUpdate();
    protected final SentPositionChain sentPositions = new SentPositionChain();
    protected boolean isSynchronized = false;
    protected final AtomicReference<RequestedPosition> lastRequestedPosition = new AtomicReference<Object>(null);
    protected final PlayerPositionInput input;

    protected PlayerMovementControllerPredicted(PlayerMovementController.ControllerType type, AttachmentViewer viewer) {
        super(type, viewer);
        this.input = new PlayerPositionInput(viewer);
        DebugUtil.getBooleanValue((String)"testcase", (boolean)false);
    }

    @Override
    public AttachmentViewer.Input getInput() {
        return this.input.lastInput.input;
    }

    private RequestedPosition computeNextRequestedPosition(Vector position, Quaternion orientation) {
        RequestedPosition lastRequestedPositionSync = this.lastRequestedPosition.get();
        int serverTicks = CommonUtil.getServerTicks();
        if (lastRequestedPositionSync != null && serverTicks - lastRequestedPositionSync.serverTicks < 20) {
            Vector motion;
            if (lastRequestedPositionSync.serverTicks == serverTicks) {
                motion = lastRequestedPositionSync.motion;
            } else {
                motion = position.clone().subtract(lastRequestedPositionSync.position);
                double fact = 1.0 / (double)(serverTicks - lastRequestedPositionSync.serverTicks);
                motion.multiply(fact);
            }
            if (motion.lengthSquared() < 256.0) {
                if (orientation == null) {
                    orientation = lastRequestedPositionSync.orientation;
                }
                return new RequestedPosition(position, motion, orientation, serverTicks);
            }
        }
        return new RequestedPosition(position, new Vector(), orientation, serverTicks);
    }

    @Override
    protected final void syncPosition(Vector position, Quaternion orientation) {
        this.lastRequestedPosition.set(this.computeNextRequestedPosition(position, orientation));
        if (!this.isSynchronized) {
            this.player.sendMessage("DESYNC DETECTED!");
        }
    }

    protected static void log(String msg) {
        TrainCarts.plugin.getLogger().log(Level.INFO, msg);
    }

    protected static boolean isVectorExactlyEqual(Vector v0, Vector v1) {
        return v0.getX() == v1.getX() && v0.getY() == v1.getY() && v0.getZ() == v1.getZ();
    }

    protected static String strVec(Vector v) {
        return v.getX() + " // " + v.getY() + " // " + v.getZ();
    }

    protected static final class SentPositionChain
    extends SentPositionChainLink {
        private final PlayerClientState lastClientState = new PlayerClientState(new Vector());
        private SentPositionChainLink last = this;
        private int count = 0;

        protected SentPositionChain() {
        }

        public void calcLastClientState(PlayerClientState startState) {
            this.lastClientState.setTo(startState);
            SentPositionUpdate u = this.next;
            while (u != null) {
                MathUtil.setVector((Vector)this.lastClientState.position, (Vector)this.lastClientState.positionAfterMotion);
                u.applyFull(this.lastClientState);
                u = u.next;
            }
        }

        public Vector getCurrentPosition() {
            return this.lastClientState.positionAfterMotion;
        }

        public int size() {
            return this.count;
        }

        public void appendDebugNextPredictions(StringBuilder str, PlayerPositionInput input, PlayerMovementController.ComposedInput controlInput) {
            PlayerClientState state = new PlayerClientState(input.lastMotion);
            state.input = controlInput;
            MathUtil.setVector((Vector)state.position, (Vector)input.lastPosition);
            MathUtil.setVector((Vector)state.inputMotion, (Vector)input.getInputMotion(controlInput.input));
            SentPositionUpdate u = this.next;
            while (u != null) {
                u.applyFull(state);
                SentPositionChain.appendDebugStr(str, u, state, input.currPosition);
                u = u.next;
            }
            FRICTION_UPDATE.applyFull(state);
            SentPositionChain.appendDebugStr(str, FRICTION_UPDATE, state, input.currPosition);
        }

        private static void appendDebugStr(StringBuilder str, SentPositionUpdate update, PlayerClientState state, Vector currPosition) {
            str.append("\n    ").append(update.getClass().getSimpleName()).append(":\n");
            str.append("            Actual ").append(PlayerMovementControllerPredicted.strVec(currPosition)).append("\n");
            str.append("         Predicted ").append(PlayerMovementControllerPredicted.strVec(state.positionAfterMotion));
        }

        public ConsumeResult tryConsumeExactInput(PlayerPositionInput input, PlayerMovementController.ComposedInput controlInput) {
            SentPositionUpdate foundUpdate;
            PlayerClientState state = new PlayerClientState(input.lastMotion);
            SentPositionUpdate curr = this.next;
            if (curr != null && (foundUpdate = curr.findInput(input, controlInput, state)) != null) {
                this.setStart(foundUpdate.next);
                this.calcLastClientState(state);
                return ConsumeResult.OK;
            }
            if (FRICTION_UPDATE.findInput(input, controlInput, state) != null) {
                this.calcLastClientState(state);
                return ConsumeResult.OK;
            }
            return ConsumeResult.FAILED;
        }

        public ConsumeResult tryConsumeHorizontalInput(PlayerPositionInput input, PlayerClientState state, PlayerMovementController.HorizontalPlayerInput horizontalInput, boolean sprinting) {
            SentPositionUpdate foundUpdate;
            SentPositionUpdate curr = this.next;
            if (curr != null && (foundUpdate = curr.findHorizontalInput(input, horizontalInput, sprinting, state)) != null) {
                this.setStart(foundUpdate.next);
                this.calcLastClientState(state);
                return ConsumeResult.OK;
            }
            if (FRICTION_UPDATE.findHorizontalInput(input, horizontalInput, sprinting, state) != null) {
                this.calcLastClientState(state);
                return ConsumeResult.OK;
            }
            return ConsumeResult.FAILED;
        }

        public void setStart(SentPositionUpdate update) {
            this.next = update;
            if (update == null) {
                this.last = this;
                this.count = 0;
            } else {
                int new_count = 1;
                SentPositionUpdate new_last = update;
                while (new_last.next != null) {
                    new_last = new_last.next;
                    ++new_count;
                }
                this.last = new_last;
                this.count = new_count;
            }
        }

        public void clear() {
            this.next = null;
            this.last = this;
            this.count = 0;
        }

        public void add(SentPositionUpdate update) {
            this.last.next = update;
            this.last = update;
            ++this.count;
            update.applyFull(this.lastClientState);
        }
    }

    protected static final class PlayerPositionInput {
        public final Player player;
        public final Vector lastPosition;
        public final Vector lastMotion;
        public final Vector currPosition;
        public float currYaw;
        public PlayerMovementController.ForwardMotion currForward;
        public float currSpeed;
        public PlayerMovementController.ComposedInput lastInput;
        public PlayerMovementController.ComposedInput currInput;
        public final double diagonalSpeedFactor;
        public final float verticalSpeedFactor;

        public PlayerPositionInput(AttachmentViewer viewer) {
            this.player = viewer.getPlayer();
            this.lastPosition = this.player.getLocation().toVector();
            this.lastMotion = this.player.getVelocity();
            this.currPosition = this.player.getLocation().toVector();
            this.currYaw = this.player.getEyeLocation().getYaw();
            this.currForward = PlayerMovementController.ForwardMotion.get(this.currYaw);
            this.currSpeed = 0.5f * this.player.getFlySpeed();
            this.lastInput = PlayerMovementController.ComposedInput.NONE;
            this.currInput = PlayerMovementController.ComposedInput.NONE;
            this.diagonalSpeedFactor = viewer.evaluateGameVersion(">=", "1.21.8") ? 0.7071067094802855 : 0.7071067811865476;
            this.verticalSpeedFactor = 3.0f;
        }

        public void setLastMotionUsingPositionChanges() {
            this.lastMotion.setX(this.currPosition.getX() - this.lastPosition.getX());
            this.lastMotion.setY(this.currPosition.getY() - this.lastPosition.getY());
            this.lastMotion.setZ(this.currPosition.getZ() - this.lastPosition.getZ());
        }

        public void updateYaw(float yaw) {
            this.currYaw = yaw;
            this.currForward = PlayerMovementController.ForwardMotion.get(yaw);
        }

        public void updateLast() {
            MathUtil.setVector((Vector)this.lastPosition, (Vector)this.currPosition);
            this.lastInput = this.currInput;
        }

        public Vector getInputMotion(AttachmentViewer.Input input) {
            double horSpeedDbl = this.currSpeed;
            double verSpeedDbl = this.currSpeed * this.verticalSpeedFactor;
            horSpeedDbl = input.hasDiagonalWalkInput() ? (horSpeedDbl *= this.diagonalSpeedFactor) : (horSpeedDbl *= (double)0.98f);
            if (input.sprinting() && input.forwards() && !input.backwards()) {
                horSpeedDbl *= 2.0;
            }
            double left = input.sidewaysSigNum() * horSpeedDbl;
            double forward = input.forwardsSigNum() * horSpeedDbl;
            double vertical = input.verticalSigNum() * verSpeedDbl;
            return new Vector(forward * this.currForward.dx + left * this.currForward.dz, vertical, forward * this.currForward.dz - left * this.currForward.dx);
        }
    }

    protected static final class RequestedPosition {
        public final Vector position;
        public final Vector motion;
        public final Quaternion orientation;
        public final int serverTicks;
        private final AtomicBoolean consumed = new AtomicBoolean(false);

        public RequestedPosition(Vector position, Vector motion, Quaternion orientation, int serverTicks) {
            this.position = position;
            this.motion = motion;
            this.orientation = orientation;
            this.serverTicks = serverTicks;
        }

        public boolean tryConsume() {
            return this.consumed.compareAndSet(false, true);
        }
    }

    protected static final class MovementFrictionUpdate
    extends SentPositionUpdate {
        protected MovementFrictionUpdate() {
        }

        @Override
        protected void apply(PlayerClientState state) {
            Vector outMotion = state.motion;
            Vector lastMotion = state.lastMotion;
            outMotion.setX(lastMotion.getX() * (double)0.91f);
            outMotion.setY(lastMotion.getY() * 0.6);
            outMotion.setZ(lastMotion.getZ() * (double)0.91f);
            if (Math.abs(outMotion.getX()) < 0.003) {
                outMotion.setX(0.0);
            }
            if (Math.abs(outMotion.getZ()) < 0.003) {
                outMotion.setZ(0.0);
            }
            outMotion.add(state.inputMotion);
            if (Math.abs(outMotion.getY()) < 0.003) {
                outMotion.setY(0.0);
            }
        }
    }

    protected static enum ConsumeResult {
        FAILED(false),
        OK(true),
        LARGE_PACKET_DROP(false);

        private final boolean isSynchronized;

        private ConsumeResult(boolean isSynchronized) {
            this.isSynchronized = isSynchronized;
        }

        public boolean isSynchronized() {
            return this.isSynchronized;
        }
    }

    protected static final class SentAbsoluteUpdate
    extends SentPositionUpdate {
        private final Vector position;

        public SentAbsoluteUpdate(Vector position) {
            this.position = position;
        }

        @Override
        protected void apply(PlayerClientState state) {
            MathUtil.setVector((Vector)state.motion, (Vector)state.inputMotion);
            MathUtil.setVector((Vector)state.position, (Vector)this.position);
        }
    }

    protected static final class SentMotionUpdate
    extends SentPositionUpdate {
        private final Vector motion;

        public SentMotionUpdate(Vector motion) {
            this.motion = motion;
        }

        @Override
        protected void apply(PlayerClientState state) {
            MathUtil.setVector((Vector)state.motion, (Vector)this.motion);
            state.motion.add(state.inputMotion);
            if (Math.abs(state.motion.getY()) < 0.003) {
                state.motion.setY(0.0);
            }
        }
    }

    protected static abstract class SentPositionUpdate
    extends SentPositionChainLink {
        protected SentPositionUpdate() {
        }

        protected abstract void apply(PlayerClientState var1);

        public final void applyFull(PlayerClientState state) {
            this.apply(state);
            MathUtil.setVector((Vector)state.positionAfterMotion, (Vector)state.position);
            state.positionAfterMotion.add(state.motion);
        }

        public SentPositionUpdate findHorizontalInput(PlayerPositionInput input, PlayerMovementController.HorizontalPlayerInput horizontalInput, boolean sprinting, PlayerClientState state) {
            for (PlayerMovementController.VerticalPlayerInput verticalInput : new PlayerMovementController.VerticalPlayerInput[]{PlayerMovementController.VerticalPlayerInput.NONE, PlayerMovementController.VerticalPlayerInput.JUMP, PlayerMovementController.VerticalPlayerInput.SNEAK}) {
                PlayerMovementController.ComposedInput controlInput = new PlayerMovementController.ComposedInput(horizontalInput, verticalInput, sprinting);
                SentPositionUpdate u = this.findInput(input, controlInput, state);
                if (u == null) continue;
                return u;
            }
            return null;
        }

        public SentPositionUpdate findInput(PlayerPositionInput input, PlayerMovementController.ComposedInput controlInput, PlayerClientState state) {
            state.input = controlInput;
            MathUtil.setVector((Vector)state.position, (Vector)input.lastPosition);
            MathUtil.setVector((Vector)state.inputMotion, (Vector)input.getInputMotion(controlInput.input));
            SentPositionUpdate u = this;
            while (u != null) {
                u.applyFull(state);
                if (state.isCorrect(input.currPosition)) {
                    MathUtil.setVector((Vector)input.lastMotion, (Vector)state.motion);
                    return u;
                }
                u = u.next;
            }
            return null;
        }
    }

    protected static class SentPositionChainLink {
        protected SentPositionUpdate next = null;

        protected SentPositionChainLink() {
        }
    }

    protected static final class PlayerClientState {
        public PlayerMovementController.ComposedInput input = PlayerMovementController.ComposedInput.NONE;
        public final Vector inputMotion = new Vector();
        public final Vector lastMotion;
        public final Vector motion = new Vector();
        public final Vector position = new Vector();
        public final Vector positionAfterMotion = new Vector();

        public PlayerClientState(Vector lastMotion) {
            this.lastMotion = lastMotion;
        }

        public void setTo(PlayerClientState state) {
            this.input = state.input;
            MathUtil.setVector((Vector)this.inputMotion, (Vector)state.inputMotion);
            MathUtil.setVector((Vector)this.lastMotion, (Vector)state.lastMotion);
            MathUtil.setVector((Vector)this.motion, (Vector)state.motion);
            MathUtil.setVector((Vector)this.position, (Vector)state.position);
            MathUtil.setVector((Vector)this.positionAfterMotion, (Vector)state.positionAfterMotion);
        }

        public boolean isCorrect(Vector currentPosition) {
            return PlayerMovementControllerPredicted.isVectorExactlyEqual(this.positionAfterMotion, currentPosition);
        }
    }
}

