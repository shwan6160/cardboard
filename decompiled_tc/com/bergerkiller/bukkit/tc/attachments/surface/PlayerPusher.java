/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.wrappers.RelativeFlags
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerPositionPacketHandle
 *  com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle
 *  com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.attachments.surface;

import com.bergerkiller.bukkit.common.wrappers.RelativeFlags;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.surface.Shulker;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerPositionPacketHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import org.bukkit.Location;
import org.bukkit.entity.Player;

final class PlayerPusher {
    private static final double PUSH_EXTRA = 1.0E-4;
    private static final double PUSH_UP_VELOCITY = 0.04;
    private final AttachmentViewer viewer;
    private final ServerPlayerHandle handle;
    private final Location viewerLocation;
    private AABBHandle bbox;
    private double pushX = 0.0;
    private double pushY = 0.0;
    private double pushZ = 0.0;

    public PlayerPusher(AttachmentViewer viewer) {
        this.viewer = viewer;
        this.handle = ServerPlayerHandle.fromBukkit((Player)viewer.getPlayer());
        this.viewerLocation = viewer.getPlayer().getLocation();
        this.reset();
    }

    public void reset() {
        this.bbox = this.handle.getBoundingBox();
        this.viewer.getPlayer().getLocation(this.viewerLocation);
        this.pushX = 0.0;
        this.pushY = 0.0;
        this.pushZ = 0.0;
    }

    public void sendPush() {
        if (this.pushX != 0.0 || this.pushY != 0.0 || this.pushZ != 0.0) {
            RelativeFlags flags = RelativeFlags.RELATIVE_POSITION_ROTATION;
            flags = flags.withRelativeDelta();
            double x = 0.0;
            double y = 0.0;
            double z = 0.0;
            double deltaX = 0.0;
            double deltaY = 0.0;
            double deltaZ = 0.0;
            if (this.pushX != 0.0) {
                flags = flags.withAbsoluteDeltaX().withAbsoluteX();
                x = this.viewerLocation.getX() + this.pushX;
            }
            if (this.pushZ != 0.0) {
                flags = flags.withAbsoluteDeltaZ().withAbsoluteZ();
                z = this.viewerLocation.getZ() + this.pushZ;
            }
            if (this.pushY != 0.0) {
                flags = flags.withAbsoluteDeltaY().withAbsoluteY();
                y = this.viewerLocation.getY() + this.pushY;
                if (this.pushY > 0.0) {
                    deltaY = 0.04;
                }
            }
            ClientboundPlayerPositionPacketHandle packet = ClientboundPlayerPositionPacketHandle.createNew((double)x, (double)y, (double)z, (float)0.0f, (float)0.0f, (double)deltaX, (double)deltaY, (double)deltaZ, (RelativeFlags)flags);
            this.viewer.send((PacketHandle)packet);
        }
    }

    public boolean shulkerSpawned(Shulker shulker) {
        if (!this.intersects(shulker.x, shulker.y, shulker.z)) {
            return false;
        }
        switch (shulker.pushDirection) {
            case NORTH: {
                double newPushZ = shulker.z - 0.5 - 0.5 * (this.bbox.getMaxZ() - this.bbox.getMinZ()) - this.viewerLocation.getZ() + 1.0E-4;
                if (!(newPushZ < this.pushZ)) break;
                this.pushZ = newPushZ;
                return true;
            }
            case SOUTH: {
                double newPushZ = shulker.z + 0.5 + 0.5 * (this.bbox.getMaxZ() - this.bbox.getMinZ()) - this.viewerLocation.getZ() - 1.0E-4;
                if (!(newPushZ > this.pushZ)) break;
                this.pushZ = newPushZ;
                return true;
            }
            case WEST: {
                double newPushX = shulker.x - 0.5 - 0.5 * (this.bbox.getMaxX() - this.bbox.getMinX()) - this.viewerLocation.getX() - 1.0E-4;
                if (!(newPushX < this.pushX)) break;
                this.pushX = newPushX;
                return true;
            }
            case EAST: {
                double newPushX = shulker.x + 0.5 + 0.5 * (this.bbox.getMaxX() - this.bbox.getMinX()) - this.viewerLocation.getX() + 1.0E-4;
                if (!(newPushX > this.pushX)) break;
                this.pushX = newPushX;
                return true;
            }
            case DOWN: {
                double newPushY = shulker.y - 0.5 - (this.bbox.getMaxY() - this.bbox.getMinY()) - this.viewerLocation.getY() - 1.0E-4;
                if (!(newPushY < this.pushY)) break;
                this.pushY = newPushY;
                return true;
            }
            case UP: {
                double newPushY = shulker.y + 0.5 - this.viewerLocation.getY() + 1.0E-4;
                if (!(newPushY > this.pushY)) break;
                this.pushY = newPushY;
                return true;
            }
        }
        return false;
    }

    private boolean intersects(double x, double y, double z) {
        AABBHandle bbox = this.bbox;
        double minX = (x -= this.pushX) - 0.5;
        double maxX = x + 0.5;
        double minY = (y -= this.pushY) - 0.5;
        double maxY = y + 0.5;
        double minZ = (z -= this.pushZ) - 0.5;
        double maxZ = z + 0.5;
        boolean overlapX = bbox.getMaxX() >= minX && bbox.getMinX() <= maxX;
        boolean overlapY = bbox.getMaxY() >= minY && bbox.getMinY() <= maxY;
        boolean overlapZ = bbox.getMaxZ() >= minZ && bbox.getMinZ() <= maxZ;
        return overlapX && overlapY && overlapZ;
    }
}

