/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.PacketReceiveEvent
 *  com.bergerkiller.bukkit.common.events.PacketSendEvent
 *  com.bergerkiller.bukkit.common.protocol.PacketType
 *  com.bergerkiller.bukkit.common.utils.DebugUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.utils.PacketUtil
 *  com.bergerkiller.bukkit.common.wrappers.PlayerAbilities
 *  com.bergerkiller.bukkit.common.wrappers.RelativeFlags
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerPositionPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundMovePlayerPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPlayerCommandPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPlayerInputPacketHandle
 *  com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.player.pmc;

import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.DebugUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.bukkit.common.wrappers.RelativeFlags;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.controller.player.pmc.PlayerMovementController;
import com.bergerkiller.bukkit.tc.controller.player.pmc.PlayerMovementControllerPredicted;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerPositionPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundMovePlayerPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPlayerCommandPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPlayerInputPacketHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

class PlayerMovementControllerPredictedModern
extends PlayerMovementControllerPredicted {
    private boolean hasSprintButtonAction = false;
    private boolean hasSprintDoubleTapAction = false;
    private final AtomicInteger skippedPositions = new AtomicInteger(0);
    private static final RelativeFlags FLAGS_ONLY_MOVE = RelativeFlags.RELATIVE_POSITION_ROTATION.withAbsoluteDeltaX().withAbsoluteDeltaY().withAbsoluteDeltaZ().withRelativeDeltaRotation();
    private static final RelativeFlags FLAGS_RESET = RelativeFlags.ABSOLUTE_POSITION.withAbsoluteDeltaX().withAbsoluteDeltaY().withAbsoluteDeltaZ().withRelativeDeltaRotation().withRelativeRotation();

    protected PlayerMovementControllerPredictedModern(PlayerMovementController.ControllerType type, AttachmentViewer viewer) {
        super(type, viewer);
    }

    protected synchronized void schedulePosition(Vector position) {
        if (this.isSynchronized) {
            Vector diff = position.clone().subtract(this.sentPositions.getCurrentPosition());
            if (Math.abs(diff.getX()) < 0.003) {
                diff.setX(0.0);
            }
            if (Math.abs(diff.getY()) < 0.003) {
                diff.setY(0.0);
            }
            if (Math.abs(diff.getZ()) < 0.003) {
                diff.setZ(0.0);
            }
            PacketUtil.sendPacket((Player)this.player, (PacketHandle)ClientboundPlayerPositionPacketHandle.createNew((double)0.0, (double)0.0, (double)0.0, (float)0.0f, (float)0.0f, (double)diff.getX(), (double)diff.getY(), (double)diff.getZ(), (RelativeFlags)FLAGS_ONLY_MOVE));
            this.sentPositions.add(new PlayerMovementControllerPredicted.SentMotionUpdate(diff));
        } else {
            PacketUtil.sendPacket((Player)this.player, (PacketHandle)ClientboundPlayerPositionPacketHandle.createNew((double)position.getX(), (double)position.getY(), (double)position.getZ(), (float)0.0f, (float)0.0f, (RelativeFlags)FLAGS_RESET));
            this.sentPositions.add(new PlayerMovementControllerPredicted.SentAbsoluteUpdate(position.clone()));
        }
    }

    private synchronized void receiveInput(PlayerMovementControllerPredicted.PlayerPositionInput input) {
        if (DebugUtil.getBooleanValue((String)"testcase", (boolean)false)) {
            PlayerMovementControllerPredicted.ConsumeResult result = this.sentPositions.tryConsumeExactInput(input, PlayerMovementController.ComposedInput.NONE);
            if (result != PlayerMovementControllerPredicted.ConsumeResult.FAILED) {
                this.isSynchronized = result.isSynchronized();
                return;
            }
            if (input.lastInput.horizontal == PlayerMovementController.HorizontalPlayerInput.NONE) {
                PlayerMovementControllerPredictedModern.log("\n                new TestCase(\n                        \"NEW TEST CASE FOR " + (Object)((Object)input.currInput.horizontal) + " + " + (Object)((Object)input.currInput.vertical) + "\",\n                        PlayerMovementController.HorizontalPlayerInput." + (Object)((Object)input.currInput.horizontal) + ",\n                        PlayerMovementController.VerticalPlayerInput." + (Object)((Object)input.currInput.vertical) + ",\n                        " + PlayerMovementControllerPredictedModern.bukkitVec(input.lastPosition) + ",\n                        " + PlayerMovementControllerPredictedModern.bukkitVec(input.currPosition) + ",\n                        " + PlayerMovementControllerPredictedModern.bukkitVec(input.lastMotion) + ",\n                        " + input.currYaw + "f\n                ),");
            }
        } else {
            PlayerMovementControllerPredicted.ConsumeResult result = this.sentPositions.tryConsumeExactInput(input, input.lastInput);
            if (result != PlayerMovementControllerPredicted.ConsumeResult.FAILED) {
                this.isSynchronized = result.isSynchronized();
                return;
            }
            result = this.sentPositions.tryConsumeExactInput(input, PlayerMovementController.ComposedInput.NONE);
            if (result != PlayerMovementControllerPredicted.ConsumeResult.FAILED) {
                this.isSynchronized = result.isSynchronized();
                return;
            }
            if (!input.lastInput.equals(input.currInput) && (result = this.sentPositions.tryConsumeExactInput(input, input.currInput)) != PlayerMovementControllerPredicted.ConsumeResult.FAILED) {
                this.isSynchronized = result.isSynchronized();
                return;
            }
            PlayerMovementControllerPredictedModern.log("[INPUTS] " + input.currInput.input);
            PlayerMovementControllerPredictedModern.log("[FORWARD] yaw=" + input.currYaw + " " + input.currForward);
            PlayerMovementControllerPredictedModern.log("[PREVIOUS] " + PlayerMovementControllerPredictedModern.strVec(input.lastPosition));
            PlayerMovementControllerPredictedModern.log(" [CURRENT] " + PlayerMovementControllerPredictedModern.strVec(input.currPosition));
            PlayerMovementControllerPredictedModern.log("  [MOTION] " + PlayerMovementControllerPredictedModern.strVec(input.lastMotion));
            PlayerMovementController.ComposedInput controlInput = input.currInput;
            Vector additionalMotion = input.getInputMotion(controlInput.input);
            if (input.currInput.horizontal != PlayerMovementController.HorizontalPlayerInput.NONE) {
                PlayerMovementControllerPredictedModern.log("[CURR PLAYER SPEED] " + input.currSpeed);
                PlayerMovementControllerPredictedModern.log("[MOVEMENT] " + PlayerMovementControllerPredictedModern.strVec(additionalMotion));
            }
            StringBuilder str = new StringBuilder();
            str.append("Updates in flight predictions:");
            this.sentPositions.appendDebugNextPredictions(str, input, controlInput);
            PlayerMovementControllerPredictedModern.log(str.toString());
        }
        input.setLastMotionUsingPositionChanges();
        if (this.isSynchronized) {
            this.sentPositions.clear();
        }
        this.isSynchronized = false;
    }

    private static String bukkitVec(Vector v) {
        return "new Vector(" + v.getX() + ", " + v.getY() + ", " + v.getZ() + ")";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getType() == PacketType.IN_POSITION || event.getType() == PacketType.IN_POSITION_LOOK) {
            ServerboundMovePlayerPacketHandle p = ServerboundMovePlayerPacketHandle.createHandle((Object)event.getPacket().getHandle());
            PlayerMovementControllerPredictedModern playerMovementControllerPredictedModern = this;
            synchronized (playerMovementControllerPredictedModern) {
                PlayerMovementControllerPredicted.PlayerPositionInput input = this.input;
                MathUtil.setVector((Vector)input.currPosition, (double)p.getX(), (double)p.getY(), (double)p.getZ());
                if (event.getType() == PacketType.IN_POSITION_LOOK) {
                    input.updateYaw(p.getYaw());
                }
            }
        }
        if (event.getType() == PacketType.IN_STEER_VEHICLE) {
            ServerboundPlayerInputPacketHandle packet = ServerboundPlayerInputPacketHandle.createHandle((Object)event.getPacket().getHandle());
            PlayerMovementController.ComposedInput currInput = new PlayerMovementController.ComposedInput(AttachmentViewer.Input.fromVehicleSteer(packet));
            PlayerMovementControllerPredictedModern input = this;
            synchronized (input) {
                this.hasSprintButtonAction = packet.isSprint();
                if (this.hasSprintDoubleTapAction) {
                    currInput = new PlayerMovementController.ComposedInput(currInput.input.withSprinting(true));
                }
                this.input.currInput = currInput;
            }
        }
        if (event.getType() == PacketType.IN_ABILITIES) {
            ServerboundPlayerAbilitiesPacketHandle p = ServerboundPlayerAbilitiesPacketHandle.createHandle((Object)event.getPacket().getHandle());
            if (!p.isFlying()) {
                event.setCancelled(true);
                PlayerAbilities pa = ServerPlayerHandle.fromBukkit((Player)event.getPlayer()).getAbilities();
                ClientboundPlayerAbilitiesPacketHandle pp = ClientboundPlayerAbilitiesPacketHandle.createNew((PlayerAbilities)pa);
                PacketUtil.queuePacket((Player)event.getPlayer(), (PacketHandle)pp);
            }
        } else if (event.getType() == PacketType.IN_PLAYER_COMMAND) {
            ServerboundPlayerCommandPacketHandle packet = ServerboundPlayerCommandPacketHandle.createHandle((Object)event.getPacket().getHandle());
            Object action = packet.getAction();
            if (action != null) {
                String actionName;
                String string = actionName = action instanceof Enum ? ((Enum)action).name() : action.toString();
                if ("START_SPRINTING".equals(actionName)) {
                    PlayerMovementControllerPredictedModern playerMovementControllerPredictedModern = this;
                    synchronized (playerMovementControllerPredictedModern) {
                        this.hasSprintDoubleTapAction = true;
                        this.input.currInput = new PlayerMovementController.ComposedInput(this.input.currInput.input.withSprinting(true));
                    }
                } else if ("STOP_SPRINTING".equals(actionName)) {
                    PlayerMovementControllerPredictedModern playerMovementControllerPredictedModern = this;
                    synchronized (playerMovementControllerPredictedModern) {
                        this.hasSprintDoubleTapAction = false;
                        this.input.currInput = new PlayerMovementController.ComposedInput(this.input.currInput.input.withSprinting(this.hasSprintButtonAction));
                    }
                }
            }
        } else if (event.getType() == PacketType.IN_CLIENT_TICK_END) {
            PlayerMovementControllerPredictedModern playerMovementControllerPredictedModern = this;
            synchronized (playerMovementControllerPredictedModern) {
                if (this.hasStopped()) {
                    return;
                }
                if (!this.input.lastInput.equals(this.input.currInput)) {
                    this.player.sendMessage("Input Changed: " + this.input.currInput.input);
                }
                this.receiveInput(this.input);
                this.input.updateLast();
                PlayerMovementControllerPredicted.RequestedPosition requested = (PlayerMovementControllerPredicted.RequestedPosition)this.lastRequestedPosition.get();
                if (requested != null) {
                    if (requested.tryConsume()) {
                        this.schedulePosition(requested.position);
                        this.skippedPositions.set(0);
                    } else {
                        int count = this.skippedPositions.incrementAndGet();
                        if (count < 10) {
                            // empty if block
                        }
                    }
                }
                if (this.sentPositions.size() > 40) {
                    this.sentPositions.clear();
                    this.isSynchronized = false;
                }
            }
        }
    }

    public void onPacketSend(PacketSendEvent event) {
    }
}

