/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.events.PacketReceiveEvent
 *  com.bergerkiller.bukkit.common.events.PacketSendEvent
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.protocol.PacketListener
 *  com.bergerkiller.bukkit.common.protocol.PacketType
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.wrappers.RelativeFlags
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerPositionPacketHandle
 *  com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle
 *  com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.player.pmc;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.RelativeFlags;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.controller.player.pmc.PlayerMovementControllerPredictedLegacy;
import com.bergerkiller.bukkit.tc.controller.player.pmc.PlayerMovementControllerPredictedModern;
import com.bergerkiller.bukkit.tc.controller.player.pmc.SweptAABB;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerPositionPacketHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public abstract class PlayerMovementController
implements AttachmentViewer.MovementController,
PacketListener {
    protected static final boolean DEBUG_MODE = true;
    private final ControllerType type;
    protected final AttachmentViewer viewer;
    protected final Player player;
    private boolean syncAsArmorstand = true;
    private Vector prevPos = null;
    private Vector lastSyncPos = null;
    protected volatile boolean translateVehicleSteer = false;
    protected boolean isFlightForced = false;
    private Boolean wasFlyingBeforeControl = null;
    private boolean stopped = false;

    protected PlayerMovementController(ControllerType type, AttachmentViewer viewer) {
        this.type = type;
        this.viewer = viewer;
        this.player = viewer.getPlayer();
    }

    public final ControllerType getType() {
        return this.type;
    }

    public void setOptions(AttachmentViewer.MovementController.Options options) {
        this.translateVehicleSteer = options.isPreserveInput();
        this.syncAsArmorstand = options.isSyncAsArmorStand();
        if (!this.syncAsArmorstand) {
            this.lastSyncPos = null;
        }
    }

    @Override
    public final void stop() {
        this.stopped = true;
        this.type.remove(this);
        this.setFlightForced(false);
        if (this.wasFlyingBeforeControl != null) {
            this.player.setFlying(this.player.getAllowFlight() && this.wasFlyingBeforeControl != false);
            this.wasFlyingBeforeControl = null;
        }
    }

    @Override
    public final boolean hasStopped() {
        return this.stopped;
    }

    @Override
    public final boolean update(Vector position, Quaternion orientation, boolean stopOnBlockCollision) {
        if (this.stopped) {
            return false;
        }
        if (this.wasFlyingBeforeControl == null) {
            this.wasFlyingBeforeControl = this.player.isFlying();
        }
        this.setFlightForced(true);
        if (this.syncAsArmorstand) {
            if (this.lastSyncPos == null) {
                this.lastSyncPos = position.clone();
            } else {
                this.lastSyncPos.add(position.clone().subtract(this.lastSyncPos).multiply(0.3333333333333333));
            }
            position = this.lastSyncPos;
        }
        if (this.prevPos == null) {
            this.prevPos = this.player.getLocation().toVector();
        }
        if (stopOnBlockCollision && this.stopOnBlockCollisions(this.prevPos, position)) {
            this.prevPos = null;
            return false;
        }
        MathUtil.setVector((Vector)this.prevPos, (Vector)position);
        this.syncPosition(position, orientation);
        return true;
    }

    protected abstract void syncPosition(Vector var1, Quaternion var2);

    protected void setFlightForced(boolean forced) {
        if (forced) {
            if (!this.player.isFlying()) {
                if (!this.player.getAllowFlight()) {
                    this.isFlightForced = true;
                    this.player.setAllowFlight(true);
                }
                this.player.setFlying(true);
            }
        } else if (this.isFlightForced) {
            this.isFlightForced = false;
            this.player.setAllowFlight(false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean stopOnBlockCollisions(Vector from, Vector to) {
        AABBHandle toBbox;
        if (from.equals((Object)to)) {
            return false;
        }
        AABBHandle playerBBOX = ServerPlayerHandle.fromBukkit((Player)this.player).getBoundingBox();
        AABBHandle fromBbox = (playerBBOX = AABBHandle.createNew((double)(-0.5 * (playerBBOX.getMaxX() - playerBBOX.getMinX())), (double)0.0, (double)(-0.5 * (playerBBOX.getMaxZ() - playerBBOX.getMinZ())), (double)(0.5 * (playerBBOX.getMaxX() - playerBBOX.getMinX())), (double)(playerBBOX.getMaxY() - playerBBOX.getMinY()), (double)(0.5 * (playerBBOX.getMaxZ() - playerBBOX.getMinZ())))).translate(from.getX(), from.getY(), from.getZ());
        SweptAABB.CollisionResult result = SweptAABB.findFirstBlockCollision(fromBbox, toBbox = playerBBOX.translate(to.getX(), to.getY(), to.getZ()), (x, y, z) -> {
            AABBHandle bbox = BlockUtil.getBoundingBox((Block)this.player.getWorld().getBlockAt(x, y, z));
            if (bbox != null) {
                return bbox.translate((double)x, (double)y, (double)z);
            }
            return null;
        }, this.viewer);
        if (result == null) {
            return false;
        }
        Vector delta = to.clone().subtract(from);
        Vector pos = from.add(delta.clone().multiply(result.theta));
        Vector velocity = delta.clone().multiply(1.0 - result.theta);
        ClientboundPlayerPositionPacketHandle packet = ClientboundPlayerPositionPacketHandle.createNew((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (float)0.0f, (float)0.0f, (double)velocity.getX(), (double)velocity.getY(), (double)velocity.getZ(), (RelativeFlags)RelativeFlags.ABSOLUTE_POSITION.withAbsoluteDelta().withRelativeRotation());
        PlayerMovementController playerMovementController = this;
        synchronized (playerMovementController) {
            this.stop();
            this.viewer.send((PacketHandle)packet);
        }
        this.player.sendMessage("Collided with block");
        return true;
    }

    public static final class ControllerType
    implements PacketListener {
        private static final boolean HAS_INPUT_PACKET = Common.evaluateMCVersion((String)">=", (String)"1.21.2");
        private final BiFunction<ControllerType, AttachmentViewer, ? extends PlayerMovementController> factory;
        private final PacketType[] listenedPacketTypes;
        private final List<PlayerMovementController> activeControllers = new ArrayList<PlayerMovementController>();
        private List<PlayerMovementController> activeControllersView = Collections.emptyList();
        public static final ControllerType LEGACY = new ControllerType(PlayerMovementControllerPredictedLegacy::new, new PacketType[]{PacketType.IN_POSITION, PacketType.IN_POSITION_LOOK, PacketType.IN_ABILITIES});
        public static final ControllerType MODERN = new ControllerType(PlayerMovementControllerPredictedModern::new, new PacketType[]{PacketType.IN_CLIENT_TICK_END, PacketType.IN_POSITION, PacketType.IN_POSITION_LOOK, PacketType.IN_STEER_VEHICLE, PacketType.IN_ABILITIES, PacketType.IN_PLAYER_COMMAND});

        public static ControllerType forViewer(AttachmentViewer viewer) {
            if (HAS_INPUT_PACKET && viewer.evaluateGameVersion(">=", "1.21.2")) {
                return MODERN;
            }
            return LEGACY;
        }

        public ControllerType(BiFunction<ControllerType, AttachmentViewer, ? extends PlayerMovementController> factory, PacketType ... listenedPacketTypes) {
            this.factory = factory;
            this.listenedPacketTypes = listenedPacketTypes;
        }

        public synchronized PlayerMovementController create(AttachmentViewer viewer) {
            PlayerMovementController controller = this.factory.apply(this, viewer);
            this.activeControllers.add(controller);
            this.activeControllersView = Collections.unmodifiableList(new ArrayList<PlayerMovementController>(this.activeControllers));
            if (this.activeControllers.size() == 1 && this.listenedPacketTypes.length > 0) {
                viewer.getTrainCarts().register(this, this.listenedPacketTypes);
            }
            return controller;
        }

        public synchronized void remove(PlayerMovementController controller) {
            if (this.activeControllers.remove(controller)) {
                this.activeControllersView = Collections.unmodifiableList(new ArrayList<PlayerMovementController>(this.activeControllers));
                if (this.activeControllers.isEmpty() && this.listenedPacketTypes.length > 0) {
                    controller.viewer.getTrainCarts().unregister(this);
                }
            }
        }

        public void onPacketReceive(PacketReceiveEvent event) {
            Player player = event.getPlayer();
            for (PlayerMovementController controller : this.activeControllers) {
                if (controller.player != player) continue;
                controller.onPacketReceive(event);
            }
        }

        public void onPacketSend(PacketSendEvent event) {
            Player player = event.getPlayer();
            for (PlayerMovementController controller : this.activeControllers) {
                if (controller.player != player) continue;
                controller.onPacketSend(event);
            }
        }
    }

    public static enum HorizontalPlayerInput {
        NONE(0.0, 0.0),
        FORWARDS(0.0, 1.0),
        BACKWARDS(0.0, -1.0),
        LEFT(1.0, 0.0),
        RIGHT(-1.0, 0.0),
        FORWARDS_LEFT(1.0, 1.0),
        FORWARDS_RIGHT(-1.0, 1.0),
        BACKWARDS_LEFT(1.0, -1.0),
        BACKWARDS_RIGHT(-1.0, -1.0);

        private final double left;
        private final double forward;
        private final boolean is_diagonal;
        private HorizontalPlayerInput[] next;

        private HorizontalPlayerInput(double left, double forward) {
            this.left = left;
            this.forward = forward;
            this.is_diagonal = left != 0.0 && forward != 0.0;
        }

        public double forwardSteerInput() {
            return this.forward;
        }

        public double leftSteerInput() {
            return this.left;
        }

        public boolean forwards() {
            return this.forward > 0.0;
        }

        public boolean backwards() {
            return this.forward < 0.0;
        }

        public boolean left() {
            return this.left > 0.0;
        }

        public boolean right() {
            return this.left < 0.0;
        }

        public boolean diagonal() {
            return this.is_diagonal;
        }

        private void setNext(HorizontalPlayerInput ... next) {
            this.next = next;
        }

        public HorizontalPlayerInput[] getNextLikelyInputs() {
            return this.next;
        }

        public static HorizontalPlayerInput fromSteer(boolean left, boolean right, boolean forwards, boolean backwards) {
            if (left && right) {
                right = false;
                left = false;
            }
            if (forwards && backwards) {
                backwards = false;
                forwards = false;
            }
            if (forwards) {
                if (left) {
                    return FORWARDS_LEFT;
                }
                if (right) {
                    return FORWARDS_RIGHT;
                }
                return FORWARDS;
            }
            if (backwards) {
                if (left) {
                    return BACKWARDS_LEFT;
                }
                if (right) {
                    return BACKWARDS_RIGHT;
                }
                return BACKWARDS;
            }
            if (left) {
                return LEFT;
            }
            if (right) {
                return RIGHT;
            }
            return NONE;
        }

        static {
            NONE.setNext(NONE, FORWARDS, LEFT, RIGHT, BACKWARDS, FORWARDS_LEFT, FORWARDS_RIGHT, BACKWARDS_LEFT, BACKWARDS_RIGHT);
            FORWARDS.setNext(FORWARDS, NONE, FORWARDS_LEFT, FORWARDS_RIGHT, BACKWARDS, LEFT, RIGHT, BACKWARDS_LEFT, BACKWARDS_RIGHT);
            BACKWARDS.setNext(BACKWARDS, NONE, BACKWARDS_LEFT, BACKWARDS_RIGHT, FORWARDS, LEFT, RIGHT, FORWARDS_LEFT, FORWARDS_RIGHT);
            LEFT.setNext(LEFT, NONE, FORWARDS_LEFT, BACKWARDS_LEFT, RIGHT, FORWARDS, BACKWARDS, FORWARDS_RIGHT, BACKWARDS_RIGHT);
            RIGHT.setNext(RIGHT, NONE, FORWARDS_RIGHT, BACKWARDS_RIGHT, LEFT, FORWARDS, BACKWARDS, FORWARDS_LEFT, BACKWARDS_LEFT);
            FORWARDS_LEFT.setNext(FORWARDS_LEFT, FORWARDS, LEFT, NONE, FORWARDS_RIGHT, RIGHT, BACKWARDS, BACKWARDS_LEFT, BACKWARDS_RIGHT);
            FORWARDS_RIGHT.setNext(FORWARDS_RIGHT, FORWARDS, RIGHT, NONE, FORWARDS_LEFT, LEFT, BACKWARDS, BACKWARDS_RIGHT, BACKWARDS_LEFT);
            BACKWARDS_LEFT.setNext(BACKWARDS_LEFT, BACKWARDS, LEFT, NONE, BACKWARDS_RIGHT, FORWARDS, RIGHT, FORWARDS_LEFT, FORWARDS_RIGHT);
            BACKWARDS_RIGHT.setNext(BACKWARDS_RIGHT, BACKWARDS, RIGHT, NONE, BACKWARDS_LEFT, FORWARDS, LEFT, FORWARDS_RIGHT, FORWARDS_LEFT);
        }
    }

    public static enum VerticalPlayerInput {
        NONE(0.0f),
        SNEAK(-3.0f),
        JUMP(3.0f);

        private final float yya;

        private VerticalPlayerInput(float yya) {
            this.yya = yya;
        }

        public double getMotion(float speed) {
            return speed * this.yya;
        }

        public static VerticalPlayerInput fromSteer(boolean jump, boolean sneak) {
            if (jump == sneak) {
                return NONE;
            }
            return jump ? JUMP : SNEAK;
        }
    }

    public static final class ComposedInput {
        public static final ComposedInput NONE = new ComposedInput(AttachmentViewer.Input.NONE);
        public final HorizontalPlayerInput horizontal;
        public final VerticalPlayerInput vertical;
        public final boolean sprinting;
        public final AttachmentViewer.Input input;

        public ComposedInput(AttachmentViewer.Input input) {
            this.horizontal = HorizontalPlayerInput.fromSteer(input.left(), input.right(), input.forwards(), input.backwards());
            this.vertical = VerticalPlayerInput.fromSteer(input.jumping(), input.sneaking());
            this.sprinting = input.sprinting();
            this.input = input;
        }

        public ComposedInput(HorizontalPlayerInput horizontal, VerticalPlayerInput vertical, boolean sprinting) {
            this.horizontal = horizontal;
            this.vertical = vertical;
            this.sprinting = sprinting;
            this.input = AttachmentViewer.Input.of(horizontal.left(), horizontal.right(), horizontal.forwards(), horizontal.backwards(), vertical == VerticalPlayerInput.JUMP, vertical == VerticalPlayerInput.SNEAK, sprinting);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof ComposedInput) {
                ComposedInput other = (ComposedInput)o;
                return this.input.equals(other.input);
            }
            return false;
        }
    }

    public static final class ForwardMotion {
        private static final float[] SIN_TABLE;
        public static final float DEG_TO_RAD = (float)Math.PI / 180;
        private static final ForwardMotion[] BY_YAW;
        public final double dx;
        public final double dz;

        public ForwardMotion(float dx, float dz) {
            this.dx = dx;
            this.dz = dz;
        }

        public static ForwardMotion get(float yaw) {
            float yaw_idx = yaw * ((float)Math.PI / 180) * 10430.378f;
            int idx_sin = (int)yaw_idx & 0xFFFF;
            int idx_cos = (int)(yaw_idx + 16384.0f) & 0xFFFF;
            if ((idx_sin + 16384 & 0xFFFF) == idx_cos) {
                return BY_YAW[idx_sin];
            }
            return new ForwardMotion(-SIN_TABLE[idx_sin], SIN_TABLE[idx_cos]);
        }

        public String toString() {
            return "{dx=" + this.dx + ", dz=" + this.dz + "}";
        }

        static {
            int i;
            SIN_TABLE = new float[65536];
            for (i = 0; i < SIN_TABLE.length; ++i) {
                ForwardMotion.SIN_TABLE[i] = (float)Math.sin((double)i * Math.PI * 2.0 / 65536.0);
            }
            BY_YAW = new ForwardMotion[SIN_TABLE.length];
            for (i = 0; i < BY_YAW.length; ++i) {
                ForwardMotion.BY_YAW[i] = new ForwardMotion(-SIN_TABLE[i], SIN_TABLE[i + 16384 & 0xFFFF]);
            }
        }
    }
}

