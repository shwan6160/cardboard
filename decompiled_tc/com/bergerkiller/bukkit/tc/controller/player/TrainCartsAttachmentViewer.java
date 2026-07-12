/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.controller.VehicleMountController
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.protocol.CommonPacket
 *  com.bergerkiller.bukkit.common.protocol.PlayerGameInfo
 *  com.bergerkiller.bukkit.common.utils.PlayerUtil
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.player;

import com.bergerkiller.bukkit.common.controller.VehicleMountController;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PlayerGameInfo;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.surface.CollisionSurface;
import com.bergerkiller.bukkit.tc.attachments.surface.CollisionSurfaceTracker;
import com.bergerkiller.bukkit.tc.attachments.surface.StationaryCollisionElement;
import com.bergerkiller.bukkit.tc.controller.player.network.PacketQueue;
import com.bergerkiller.bukkit.tc.controller.player.network.PlayerClientSynchronizer;
import com.bergerkiller.bukkit.tc.controller.player.pmc.PlayerMovementController;
import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.NetworkInterface;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class TrainCartsAttachmentViewer
implements AttachmentViewer {
    private final TrainCarts plugin;
    private final Player player;
    private final VehicleMountController vmc;
    private final PlayerGameInfo playerGameInfo;
    private final double armorStandButtOffset;
    private final boolean supportsDisplayEntityLocationInterpolation;
    private final boolean supportsDisplayEntities;
    private final boolean supportRelativeRotationUpdate;
    private final PacketQueue packetQueue;
    private final PlayerClientSynchronizer playerClientSynchronizer;
    private final Object activeMovementControllerLock;
    private volatile MovementControllerTicket activeMovementController;
    CollisionSurfaceTracker collisionSurfaceTracker;

    TrainCartsAttachmentViewer(TrainCarts plugin, Player player, PlayerGameInfo playerGameInfo, PacketQueue packetQueue) {
        this.plugin = plugin;
        this.player = player;
        this.vmc = PlayerUtil.getVehicleMountController((Player)player);
        this.playerGameInfo = playerGameInfo;
        this.armorStandButtOffset = AttachmentViewer.super.getArmorStandButtOffset();
        this.supportsDisplayEntityLocationInterpolation = AttachmentViewer.super.supportsDisplayEntityLocationInterpolation();
        this.supportsDisplayEntities = AttachmentViewer.super.supportsDisplayEntities();
        this.supportRelativeRotationUpdate = AttachmentViewer.super.supportRelativeRotationUpdate();
        this.packetQueue = packetQueue;
        this.playerClientSynchronizer = plugin.getPlayerClientSynchronizerProvider().forViewer(this);
        this.activeMovementControllerLock = new MovementControllerTicket();
        this.activeMovementController = new MovementControllerTicket();
        this.collisionSurfaceTracker = null;
    }

    PacketQueue getPacketQueue() {
        return this.packetQueue;
    }

    @Override
    public PlayerClientSynchronizer getClientSynchronizer() {
        return this.playerClientSynchronizer;
    }

    @Override
    public TrainCarts getTrainCarts() {
        return this.plugin;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public boolean evaluateGameVersion(String operand, String rightSide) {
        return this.playerGameInfo.evaluateVersion(operand, rightSide);
    }

    @Override
    public boolean supportsDisplayEntityLocationInterpolation() {
        return this.supportsDisplayEntityLocationInterpolation;
    }

    @Override
    public boolean supportsDisplayEntities() {
        return this.supportsDisplayEntities;
    }

    @Override
    public boolean supportRelativeRotationUpdate() {
        return this.supportRelativeRotationUpdate;
    }

    @Override
    public double getArmorStandButtOffset() {
        return this.armorStandButtOffset;
    }

    @Override
    public VehicleMountController getVehicleMountController() {
        return this.vmc;
    }

    @Override
    public NetworkInterface getSmoothCoastersNetwork() {
        return this.packetQueue;
    }

    @Override
    public void send(PacketHandle packet) {
        this.getPacketQueue().send(packet);
    }

    @Override
    public void send(CommonPacket packet) {
        this.getPacketQueue().send(packet);
    }

    @Override
    public void sendSilent(CommonPacket packet) {
        this.getPacketQueue().sendSilent(packet);
    }

    @Override
    public void sendSilent(PacketHandle packet) {
        this.getPacketQueue().sendSilent(packet);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AttachmentViewer.MovementController controlMovement(AttachmentViewer.MovementController.Options options) {
        Object object = this.activeMovementControllerLock;
        synchronized (object) {
            PlayerMovementController controller;
            AttachmentViewer.MovementController prev = this.activeMovementController.controller.getAndSet(AttachmentViewer.MovementController.DISABLED);
            MovementControllerTicket ticket = new MovementControllerTicket();
            if (prev instanceof PlayerMovementController && !prev.hasStopped()) {
                controller = (PlayerMovementController)prev;
            } else if (this.isConnected()) {
                controller = PlayerMovementController.ControllerType.forViewer(this).create(this);
            } else {
                return AttachmentViewer.MovementController.DISABLED;
            }
            controller.setOptions(options);
            ticket.controller.set(controller);
            this.activeMovementController = ticket;
            return ticket;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void stopControllingMovement() {
        AttachmentViewer.MovementController prev;
        Object object = this.activeMovementControllerLock;
        synchronized (object) {
            prev = this.activeMovementController.controller.getAndSet(AttachmentViewer.MovementController.DISABLED);
        }
        prev.stop();
    }

    @Override
    public CollisionSurface createCollisionSurface(int viewRange) {
        CollisionSurfaceTracker collisionSurfaceTracker = this.collisionSurfaceTracker;
        if (collisionSurfaceTracker == null) {
            if (!this.isConnected()) {
                return CollisionSurface.DISABLED;
            }
            this.collisionSurfaceTracker = collisionSurfaceTracker = new CollisionSurfaceTracker(this, viewRange);
        }
        return collisionSurfaceTracker.createSurface();
    }

    @Override
    public void forAllStationaryCollisionElements(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Consumer<StationaryCollisionElement> action) {
        CollisionSurfaceTracker collisionSurfaceTracker = this.collisionSurfaceTracker;
        if (collisionSurfaceTracker != null) {
            collisionSurfaceTracker.forAllStationaryElements(minX, minY, minZ, maxX, maxY, maxZ, action);
        }
    }

    public int hashCode() {
        return this.player.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof AttachmentViewer) {
            return this.player == ((AttachmentViewer)o).getPlayer();
        }
        return false;
    }

    public String toString() {
        return "TCAttachmentViewer{player=" + this.player.getName() + "}";
    }

    private static final class MovementControllerTicket
    implements AttachmentViewer.MovementController {
        private final AtomicReference<AttachmentViewer.MovementController> controller = new AtomicReference<AttachmentViewer.MovementController>(AttachmentViewer.MovementController.DISABLED);

        private MovementControllerTicket() {
        }

        @Override
        public void stop() {
            AttachmentViewer.MovementController oldController = this.controller.getAndSet(AttachmentViewer.MovementController.DISABLED);
            oldController.stop();
        }

        @Override
        public boolean hasStopped() {
            return this.controller.get().hasStopped();
        }

        @Override
        public AttachmentViewer.Input getInput() {
            return this.controller.get().getInput();
        }

        @Override
        public boolean update(Vector position, Quaternion orientation, boolean stopOnCollision) {
            return this.controller.get().update(position, orientation, stopOnCollision);
        }
    }
}

