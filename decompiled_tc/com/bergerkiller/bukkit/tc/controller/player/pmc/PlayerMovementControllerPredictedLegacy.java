/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.PacketReceiveEvent
 *  com.bergerkiller.bukkit.common.events.PacketSendEvent
 *  com.bergerkiller.bukkit.common.protocol.PacketType
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.utils.PacketUtil
 *  com.bergerkiller.bukkit.common.wrappers.PlayerAbilities
 *  com.bergerkiller.bukkit.common.wrappers.RelativeFlags
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerPositionPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundMovePlayerPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacketHandle
 *  com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.player.pmc;

import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.protocol.PacketType;
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
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundMovePlayerPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacketHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

class PlayerMovementControllerPredictedLegacy
extends PlayerMovementControllerPredicted {
    private boolean lastPositionWasLook = false;

    protected PlayerMovementControllerPredictedLegacy(PlayerMovementController.ControllerType type, AttachmentViewer viewer) {
        super(type, viewer);
    }

    protected synchronized void sendPosition(Vector position) {
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
            ClientboundSetEntityMotionPacketHandle p = ClientboundSetEntityMotionPacketHandle.createNew((int)this.player.getEntityId(), (double)diff.getX(), (double)diff.getY(), (double)diff.getZ());
            PacketUtil.sendPacket((Player)this.player, (PacketHandle)p);
            this.sentPositions.add(new PlayerMovementControllerPredicted.SentMotionUpdate(new Vector(p.getMotX(), p.getMotY(), p.getMotZ())));
        } else {
            ClientboundSetEntityMotionPacketHandle p2 = ClientboundSetEntityMotionPacketHandle.createNew((int)this.player.getEntityId(), (double)0.0, (double)0.0, (double)0.0);
            PacketUtil.sendPacket((Player)this.player, (PacketHandle)p2);
            PacketUtil.sendPacket((Player)this.player, (PacketHandle)ClientboundPlayerPositionPacketHandle.createNew((double)position.getX(), (double)position.getY(), (double)position.getZ(), (float)0.0f, (float)0.0f, (RelativeFlags)RelativeFlags.ABSOLUTE_POSITION.withRelativeRotation()));
            this.sentPositions.add(new PlayerMovementControllerPredicted.SentAbsoluteUpdate(position.clone()));
        }
    }

    private synchronized void receiveInput(PlayerMovementControllerPredicted.PlayerPositionInput input) {
        boolean sprinting = false;
        PlayerMovementController.HorizontalPlayerInput[] horizontalPlayerInputArray = input.lastInput.horizontal.getNextLikelyInputs();
        int n = horizontalPlayerInputArray.length;
        for (int i = 0; i < n; ++i) {
            PlayerMovementControllerPredicted.PlayerClientState state = new PlayerMovementControllerPredicted.PlayerClientState(input.lastMotion);
            PlayerMovementController.HorizontalPlayerInput hor = horizontalPlayerInputArray[i];
            PlayerMovementControllerPredicted.ConsumeResult result = this.sentPositions.tryConsumeHorizontalInput(input, state, hor, false);
            if (result == PlayerMovementControllerPredicted.ConsumeResult.FAILED) continue;
            input.currInput = state.input;
            this.isSynchronized = result.isSynchronized();
            return;
        }
        PlayerMovementControllerPredictedLegacy.log("[FORWARD] " + input.currForward);
        PlayerMovementControllerPredictedLegacy.log("[PREVIOUS] " + PlayerMovementControllerPredictedLegacy.strVec(input.lastPosition));
        PlayerMovementControllerPredictedLegacy.log("[BORKED] " + PlayerMovementControllerPredictedLegacy.strVec(input.currPosition));
        PlayerMovementControllerPredictedLegacy.log("[MOTION] " + PlayerMovementControllerPredictedLegacy.strVec(input.lastMotion));
        StringBuilder str = new StringBuilder();
        str.append("Updates in flight predictions:");
        this.sentPositions.appendDebugNextPredictions(str, input, input.lastInput);
        PlayerMovementControllerPredictedLegacy.log(str.toString());
        input.setLastMotionUsingPositionChanges();
        if (this.isSynchronized) {
            this.sentPositions.clear();
        }
        this.isSynchronized = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onPacketReceive(PacketReceiveEvent event) {
        ServerboundPlayerAbilitiesPacketHandle p;
        if (event.getType() == PacketType.IN_POSITION || event.getType() == PacketType.IN_POSITION_LOOK) {
            ServerboundMovePlayerPacketHandle p2 = ServerboundMovePlayerPacketHandle.createHandle((Object)event.getPacket().getHandle());
            PlayerMovementControllerPredictedLegacy playerMovementControllerPredictedLegacy = this;
            synchronized (playerMovementControllerPredictedLegacy) {
                if (this.hasStopped()) {
                    return;
                }
                PlayerMovementControllerPredicted.PlayerPositionInput input = this.input;
                MathUtil.setVector((Vector)input.currPosition, (double)p2.getX(), (double)p2.getY(), (double)p2.getZ());
                if (event.getType() == PacketType.IN_POSITION_LOOK) {
                    input.updateYaw(p2.getYaw());
                    this.lastPositionWasLook = true;
                } else if (this.lastPositionWasLook) {
                    this.lastPositionWasLook = false;
                    if (PlayerMovementControllerPredictedLegacy.isVectorExactlyEqual(input.lastPosition, input.currPosition)) {
                        return;
                    }
                }
                this.receiveInput(input);
                input.updateLast();
                if (this.translateVehicleSteer) {
                    PacketUtil.receivePacket((Player)this.player, (PacketHandle)input.lastInput.input.createSteerPacket());
                }
            }
        } else if (event.getType() == PacketType.IN_ABILITIES && !(p = ServerboundPlayerAbilitiesPacketHandle.createHandle((Object)event.getPacket().getHandle())).isFlying()) {
            event.setCancelled(true);
            PlayerAbilities pa = ServerPlayerHandle.fromBukkit((Player)event.getPlayer()).getAbilities();
            ClientboundPlayerAbilitiesPacketHandle pp = ClientboundPlayerAbilitiesPacketHandle.createNew((PlayerAbilities)pa);
            PacketUtil.queuePacket((Player)event.getPlayer(), (PacketHandle)pp);
        }
    }

    public void onPacketSend(PacketSendEvent event) {
    }
}

