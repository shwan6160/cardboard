/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.OrientedBoundingBox
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.wrappers.RelativeFlags
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerPositionPacketHandle
 *  com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.surface;

import com.bergerkiller.bukkit.common.math.OrientedBoundingBox;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.RelativeFlags;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.surface.CollisionFloorTileGrid;
import com.bergerkiller.bukkit.tc.attachments.surface.CollisionFloorTileShape;
import com.bergerkiller.bukkit.tc.attachments.surface.CollisionSurface;
import com.bergerkiller.bukkit.tc.attachments.surface.CollisionWallTileGrid;
import com.bergerkiller.bukkit.tc.attachments.surface.OBBSurfaceContext;
import com.bergerkiller.bukkit.tc.attachments.surface.PlayerPusher;
import com.bergerkiller.bukkit.tc.attachments.surface.ShulkerTracker;
import com.bergerkiller.bukkit.tc.attachments.surface.StationaryCollisionElement;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerPositionPacketHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CollisionSurfaceTracker {
    private static final int TICKS_UNTIL_NOT_MOVING = 2;
    private final AttachmentViewer viewer;
    private final int shulkerViewDistance;
    private final PlayerPusher playerPusher;
    private final ShulkerTracker shulkerCache;
    private final CollisionFloorTileGrid floorTiles;
    private final EnumMap<BlockFace, CollisionWallTileGrid> wallTiles;
    private final List<CollisionSurfaceImpl> surfaces = new ArrayList<CollisionSurfaceImpl>();
    private ActiveSurface activeSurface = null;
    private AttachmentViewer.MovementController pmc = null;
    private boolean initialUpdate = true;

    public CollisionSurfaceTracker(AttachmentViewer viewer, int shulkerViewDistance) {
        this.viewer = viewer;
        this.shulkerViewDistance = shulkerViewDistance;
        this.playerPusher = new PlayerPusher(viewer);
        this.shulkerCache = new ShulkerTracker();
        this.floorTiles = new CollisionFloorTileGrid(this.shulkerCache);
        this.wallTiles = new EnumMap(BlockFace.class);
    }

    public void update() {
        Vector newPlayerPosition;
        for (CollisionSurfaceImpl collisionSurfaceImpl : this.surfaces) {
            collisionSurfaceImpl.nextUpdate();
            if (collisionSurfaceImpl.isMoving()) continue;
            collisionSurfaceImpl.spawnShulkers();
        }
        this.floorTiles.update();
        this.wallTiles.values().removeIf(wallTiles -> {
            wallTiles.update();
            return wallTiles.isEmpty();
        });
        this.shulkerCache.update(this.viewer, this.playerPusher);
        if (this.activeSurface != null) {
            if (this.activeSurface.surface.shape == null) {
                this.leaveSurface(true);
            } else {
                this.activeSurface.lastKnownShape = this.activeSurface.surface.shape;
            }
        }
        if (this.initialUpdate && this.activeSurface == null) {
            Vector currPos = this.viewer.getPlayer().getLocation().toVector();
            for (CollisionSurfaceImpl surface : this.surfaces) {
                Vector relativePos;
                if (!surface.isMoving() || !(Math.abs((relativePos = surface.computeRelativePosition(currPos)).getY()) < 0.01)) continue;
                Vector halfSize = surface.shape.getSize().clone().multiply(0.5);
                if (!(Math.abs(relativePos.getX()) <= halfSize.getX()) || !(Math.abs(relativePos.getZ()) <= halfSize.getZ())) continue;
                relativePos.setY(0.0);
                this.setOnSurface(surface, relativePos);
                break;
            }
        }
        if (this.activeSurface != null) {
            if (this.pmc == null) {
                this.pmc = this.viewer.controlMovement();
            }
            this.activeSurface.updatePhysics(this.viewer, this.pmc);
            newPlayerPosition = this.activeSurface.getAbsolutePosition();
            if (!this.activeSurface.surface.isMoving() && !this.activeSurface.airborne) {
                this.leaveSurface(true);
            } else if (!this.pmc.update(newPlayerPosition, this.activeSurface.airborne)) {
                this.leaveSurface(false);
            }
        } else {
            newPlayerPosition = this.viewer.getPlayer().getLocation().toVector();
        }
        List<PositionOnSurface> list = this.findWalkedOntoSurfaces(newPlayerPosition);
        if (!list.isEmpty()) {
            list.sort(Comparator.comparingDouble(s -> -s.motionOnSurface.getY()));
            this.setOnSurface(list.get((int)0).surface, list.get((int)0).posOnSurface);
        }
    }

    private List<PositionOnSurface> findWalkedOntoSurfaces(Vector newPlayerPosition) {
        ArrayList<PositionOnSurface> result = new ArrayList<PositionOnSurface>(2);
        for (CollisionSurfaceImpl s : this.surfaces) {
            Vector intersectPosition;
            if (!s.isMoving()) continue;
            Vector oldRelativePosition = s.lastRelativePosition;
            Vector newRelativePosition = s.computeRelativePosition(newPlayerPosition);
            s.lastRelativePosition = newRelativePosition;
            if (this.activeSurface != null && !this.activeSurface.airborne && this.activeSurface.surface == s) {
                intersectPosition = newRelativePosition.clone().setY(0.0);
                Vector mot = oldRelativePosition != null ? s.computeDirectionOnSurface(newRelativePosition.clone().subtract(oldRelativePosition)) : s.computeDirectionOnSurface(this.activeSurface.relativeVelocity);
                result.add(new PositionOnSurface(s, intersectPosition, mot));
                continue;
            }
            if (oldRelativePosition == null || oldRelativePosition.getY() >= 0.0 == newRelativePosition.getY() >= 0.0) continue;
            intersectPosition = newRelativePosition.clone().setY(0.0);
            Vector halfSize = s.shape.getSize().clone().multiply(0.5);
            if (Math.abs(intersectPosition.getX()) > halfSize.getX() || Math.abs(intersectPosition.getZ()) > halfSize.getZ()) continue;
            Vector mot = s.computeDirectionOnSurface(newRelativePosition.clone().subtract(oldRelativePosition));
            result.add(new PositionOnSurface(s, intersectPosition, mot));
        }
        return result;
    }

    private void leaveSurface(boolean applyPosition) {
        if (this.pmc != null) {
            this.pmc.stop();
            this.pmc = null;
        }
        if (this.activeSurface != null) {
            Vector pos = applyPosition ? this.activeSurface.getAbsolutePosition() : new Vector();
            Vector vel = this.activeSurface.getAbsoluteVelocity();
            this.activeSurface = null;
            ServerPlayerHandle epHandle = ServerPlayerHandle.fromBukkit((Player)this.viewer.getPlayer());
            if (applyPosition) {
                epHandle.setPosition(pos.getX(), pos.getY(), pos.getZ());
            }
            epHandle.setMotVector(vel);
            RelativeFlags flags = RelativeFlags.ABSOLUTE_POSITION.withRelativeRotation().withAbsoluteDelta();
            if (!applyPosition) {
                flags = flags.withRelativePosition();
            }
            ClientboundPlayerPositionPacketHandle packet = ClientboundPlayerPositionPacketHandle.createNew((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (float)0.0f, (float)0.0f, (double)vel.getX(), (double)vel.getY(), (double)vel.getZ(), (RelativeFlags)flags);
            this.viewer.send((PacketHandle)packet);
        }
    }

    private void setOnSurface(CollisionSurfaceImpl surface, Vector relativePosition) {
        if (this.activeSurface == null) {
            CollisionSurfaceTracker.checkSurfaceValid(surface);
            Vector vel = this.viewer.getPlayer().getVelocity();
            CollisionSurfaceTracker.velocityRotation(surface.shape).invTransformPoint(vel);
            this.activeSurface = new ActiveSurface(surface, relativePosition, vel);
        } else if (this.activeSurface.surface != surface) {
            Vector vel = this.activeSurface.getAbsoluteVelocity();
            CollisionSurfaceTracker.velocityRotation(surface.shape).invTransformPoint(vel);
            this.activeSurface = new ActiveSurface(surface, relativePosition, vel);
        } else {
            this.activeSurface.airborne = false;
            MathUtil.setVector((Vector)this.activeSurface.relativePosition, (Vector)relativePosition);
        }
    }

    public CollisionSurface createSurface() {
        return new CollisionSurfaceImpl();
    }

    public void forAllStationaryElements(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Consumer<StationaryCollisionElement> action) {
        this.floorTiles.forAllShulkers(minX, minY, minZ, maxX, maxY, maxZ, action);
        for (CollisionWallTileGrid wallTileGrid : this.wallTiles.values()) {
            wallTileGrid.forAllShulkers(minX, minY, minZ, maxX, maxY, maxZ, action);
        }
    }

    private CollisionWallTileGrid getWallTiles(BlockFace face) {
        return this.wallTiles.computeIfAbsent(face, f -> new CollisionWallTileGrid(this.shulkerCache, (BlockFace)f));
    }

    private ActiveSurface getAbsoluteSurface(CollisionSurfaceImpl surface, Vector playerLocation, Vector playerVelocity) {
        CollisionSurfaceTracker.checkSurfaceValid(surface);
        OrientedBoundingBox shape = surface.shape;
        Vector relativePosition = playerLocation.clone();
        relativePosition.subtract(shape.getPosition());
        shape.getOrientation().invTransformPoint(relativePosition);
        Vector relativeVelocity = playerVelocity.clone();
        CollisionSurfaceTracker.velocityRotation(shape).invTransformPoint(relativeVelocity);
        return new ActiveSurface(surface, relativePosition, relativeVelocity);
    }

    private static void checkSurfaceValid(CollisionSurfaceImpl surface) {
        if (surface == null) {
            throw new IllegalArgumentException("Surface is null");
        }
        if (surface.shape == null) {
            throw new IllegalStateException("Surface was removed (shape is null)");
        }
    }

    private static Quaternion velocityRotation(OrientedBoundingBox shape) {
        return Quaternion.fromLookDirection((Vector)shape.getOrientation().forwardVector().setY(0.0));
    }

    private class CollisionSurfaceImpl
    implements CollisionSurface {
        private int updateCounter = 0;
        private OrientedBoundingBox shape;
        private int moveDetectedAtCount = 0;
        private boolean isMoving = false;
        private Vector lastRelativePosition = null;

        private CollisionSurfaceImpl() {
        }

        public boolean isMoving() {
            return this.isMoving;
        }

        public void nextUpdate() {
            if (this.isMoving && this.updateCounter - this.moveDetectedAtCount >= 2) {
                this.isMoving = false;
            }
            ++this.updateCounter;
        }

        public Vector computeRelativePosition(Vector absolutePosition) {
            Vector rel = absolutePosition.clone();
            rel.subtract(this.shape.getPosition());
            this.shape.getOrientation().invTransformPoint(rel);
            return rel;
        }

        public Vector computeDirectionOnSurface(Vector relativeDirection) {
            Vector abs = relativeDirection.clone();
            abs.setY(0.0);
            this.shape.getOrientation().transformPoint(abs);
            abs.normalize();
            return abs;
        }

        @Override
        public int getUpdateCounter() {
            return this.updateCounter;
        }

        @Override
        public void remove() {
            this.setShape(null);
        }

        @Override
        public void setShape(OrientedBoundingBox shape) {
            if (shape == null) {
                if (this.shape != null) {
                    CollisionSurfaceTracker.this.surfaces.remove(this);
                    this.shape = null;
                    ++this.updateCounter;
                }
                return;
            }
            if (this.shape == null) {
                this.shape = new OrientedBoundingBox(shape.getPosition(), shape.getSize(), shape.getOrientation());
                CollisionSurfaceTracker.this.surfaces.add(this);
                this.isMoving = false;
                return;
            }
            if (this.shape.getOrientation().equals((Object)shape.getOrientation()) && this.shape.getSize().equals((Object)shape.getSize())) {
                Vector oldPos = this.shape.getPosition();
                Vector newPos = shape.getPosition();
                if (newPos.getX() == oldPos.getX() && newPos.getZ() == oldPos.getZ()) {
                    if (newPos.getY() == oldPos.getY()) {
                        return;
                    }
                    if (newPos.getY() < oldPos.getY()) {
                        this.shape.setPosition(newPos);
                        return;
                    }
                }
            }
            this.moveDetectedAtCount = this.updateCounter;
            this.isMoving = true;
            this.shape.setPosition(shape.getPosition());
            this.shape.setOrientation(shape.getOrientation());
            this.shape.setSize(shape.getSize());
        }

        public void spawnShulkers() {
            Vector playerPos = CollisionSurfaceTracker.this.viewer.getPlayer().getLocation().toVector();
            OBBSurfaceContext context = new OBBSurfaceContext(this.shape, playerPos, CollisionSurfaceTracker.this.shulkerViewDistance);
            if (context.isFullyClipped) {
                return;
            }
            if (context.isWall) {
                BlockFace face = Math.abs(context.normal.getX()) > Math.abs(context.normal.getZ()) ? (context.normal.getX() > 0.0 != context.isBackSide ? BlockFace.WEST : BlockFace.EAST) : (context.normal.getZ() > 0.0 != context.isBackSide ? BlockFace.NORTH : BlockFace.SOUTH);
                this.applyWallSurface(context, face);
            } else if (context.normal.getY() < 0.0 != context.isBackSide) {
                this.applyWallSurface(context, BlockFace.UP);
            } else if (context.normal.getY() > 0.95) {
                this.applyLevelSurface(context);
            } else if (Math.abs(context.normal.getX()) < 0.2 || Math.abs(context.normal.getZ()) < 0.2) {
                this.applyAlignedSlopedSurface(context);
            } else {
                this.applyDiagonalSlopedSurface(context);
            }
        }

        public void addFloorTile(int x, int z, CollisionFloorTileShape shape) {
            CollisionSurfaceTracker.this.floorTiles.addFloorTile(this, x, z, shape);
        }

        public void removeFloorTile(int x, int z) {
            CollisionSurfaceTracker.this.floorTiles.removeFloorTile(this, x, z);
        }

        public void addWallTile(BlockFace face, int x, int y, double value) {
            CollisionSurfaceTracker.this.getWallTiles(face).addWallTile(this, x, y, value);
        }

        public void removeWallTile(BlockFace face, int x, int y) {
            CollisionSurfaceTracker.this.getWallTiles(face).removeWallTile(this, x, y);
        }

        public void applyWallSurface(OBBSurfaceContext context, BlockFace face) {
            context.initProjector(face);
            CollisionWallTileGrid wallGrid = CollisionSurfaceTracker.this.getWallTiles(face);
            if (FaceUtil.isAlongY((BlockFace)face)) {
                for (int x = context.cuboid.min.x; x < context.cuboid.max.x; ++x) {
                    for (int z = context.cuboid.min.z; z < context.cuboid.max.z; ++z) {
                        if (!context.project((double)x + 0.5, face == BlockFace.DOWN ? (double)context.cuboid.max.y : (double)context.cuboid.min.y, (double)z + 0.5)) continue;
                        wallGrid.addWallTile(this, x, z, context.projectedPos.getY() + 0.5 * (double)face.getModY());
                    }
                }
            } else if (FaceUtil.isAlongX((BlockFace)face)) {
                for (int z = context.cuboid.min.z; z < context.cuboid.max.z; ++z) {
                    for (int y = context.cuboid.min.y; y < context.cuboid.max.y; ++y) {
                        if (!context.project(face == BlockFace.EAST ? (double)context.cuboid.max.x : (double)context.cuboid.min.x, (double)y + 0.5, (double)z + 0.5)) continue;
                        wallGrid.addWallTile(this, z, y, context.projectedPos.getX() + 0.5 * (double)face.getModX());
                    }
                }
            } else {
                for (int x = context.cuboid.min.x; x < context.cuboid.max.x; ++x) {
                    for (int y = context.cuboid.min.y; y < context.cuboid.max.y; ++y) {
                        if (!context.project((double)x + 0.5, (double)y + 0.5, face == BlockFace.SOUTH ? (double)context.cuboid.max.z : (double)context.cuboid.min.z)) continue;
                        wallGrid.addWallTile(this, x, y, context.projectedPos.getZ() + 0.5 * (double)face.getModZ());
                    }
                }
            }
        }

        public void applyAlignedSlopedSurface(OBBSurfaceContext context) {
            CollisionFloorTileShape.AlignedAxis axis = Math.abs(context.normal.getX()) > Math.abs(context.normal.getZ()) ? CollisionFloorTileShape.AlignedAxis.X : CollisionFloorTileShape.AlignedAxis.Z;
            context.initProjector(BlockFace.DOWN);
            double maxY = context.planeMax.getY() - 0.5;
            for (int x = context.cuboid.min.x; x < context.cuboid.max.x; ++x) {
                for (int z = context.cuboid.min.z; z < context.cuboid.max.z; ++z) {
                    if (!context.project((double)x + 0.5 + axis.getDx(), context.cuboid.max.y, (double)z + 0.5 + axis.getDz())) continue;
                    double yp = context.projectedPos.getY() - 0.5;
                    if (!context.project((double)x + 0.5 - axis.getDx(), context.cuboid.max.y, (double)z + 0.5 - axis.getDz())) continue;
                    double yn = context.projectedPos.getY() - 0.5;
                    if (yp > maxY && yn > maxY) continue;
                    yn = Math.min(maxY, yn);
                    yp = Math.min(maxY, yp);
                    this.addFloorTile(x, z, new CollisionFloorTileShape.AlignedSlope(axis, yp, yn));
                }
            }
        }

        public void applyDiagonalSlopedSurface(OBBSurfaceContext context) {
            context.initProjector(BlockFace.DOWN);
            double maxY = context.planeMax.getY() - 0.5;
            for (int x = context.cuboid.min.x; x < context.cuboid.max.x; ++x) {
                for (int z = context.cuboid.min.z; z < context.cuboid.max.z; ++z) {
                    if (!context.project((double)x + 0.25, context.cuboid.max.y, (double)z + 0.25)) continue;
                    double y00 = context.projectedPos.getY() - 0.5;
                    if (!context.project((double)x + 0.25, context.cuboid.max.y, (double)z + 0.75)) continue;
                    double y01 = context.projectedPos.getY() - 0.5;
                    if (!context.project((double)x + 0.75, context.cuboid.max.y, (double)z + 0.25)) continue;
                    double y10 = context.projectedPos.getY() - 0.5;
                    if (!context.project((double)x + 0.75, context.cuboid.max.y, (double)z + 0.75)) continue;
                    double y11 = context.projectedPos.getY() - 0.5;
                    if (y00 > maxY && y01 > maxY && y10 > maxY && y11 > maxY) continue;
                    y00 = Math.min(maxY, y00);
                    y01 = Math.min(maxY, y01);
                    y10 = Math.min(maxY, y10);
                    y11 = Math.min(maxY, y11);
                    this.addFloorTile(x, z, new CollisionFloorTileShape.ComplexTile(y00, y01, y10, y11));
                }
            }
        }

        public void applyLevelSurface(OBBSurfaceContext context) {
            context.initProjector(BlockFace.DOWN);
            double maxY = context.planeMax.getY() - 0.5;
            for (int x = context.cuboid.min.x; x < context.cuboid.max.x; ++x) {
                for (int z = context.cuboid.min.z; z < context.cuboid.max.z; ++z) {
                    double y;
                    if (!context.project((double)x + 0.5, context.cuboid.max.y, (double)z + 0.5) || (y = context.projectedPos.getY() - 0.5) > maxY) continue;
                    this.addFloorTile(x, z, new CollisionFloorTileShape.Level(y));
                }
            }
        }
    }

    private static class ActiveSurface {
        public final CollisionSurfaceImpl surface;
        public OrientedBoundingBox lastKnownShape;
        public final Vector relativePosition;
        public final Vector relativeVelocity;
        public boolean airborne;

        public ActiveSurface(CollisionSurfaceImpl surface, Vector relativePosition, Vector relativeVelocity) {
            CollisionSurfaceTracker.checkSurfaceValid(surface);
            this.surface = surface;
            this.lastKnownShape = surface.shape;
            this.relativePosition = relativePosition;
            this.relativeVelocity = relativeVelocity;
            this.airborne = false;
        }

        public void updatePhysics(AttachmentViewer viewer, AttachmentViewer.MovementController pmc) {
            AttachmentViewer.Input input = pmc.getInput();
            if (input.hasWalkInput()) {
                Vector vel = new Vector(0.1 * input.sidewaysSigNum(), 0.0, 0.1 * input.forwardsSigNum());
                Quaternion.fromLookDirection((Vector)viewer.getPlayer().getEyeLocation().getDirection().setY(0.0), (Vector)new Vector(0, 1, 0)).transformPoint(vel);
                CollisionSurfaceTracker.velocityRotation(this.lastKnownShape).invTransformPoint(vel);
                this.relativeVelocity.add(vel);
            }
            this.relativePosition.add(this.relativeVelocity);
            this.relativeVelocity.multiply(0.6);
            Vector halfSize = this.lastKnownShape.getSize().clone().multiply(0.5);
            if (this.airborne) {
                this.relativeVelocity.setY(this.relativeVelocity.getY() - 0.15);
            } else if (input.jumping() && !input.sneaking()) {
                this.relativeVelocity.setY(this.relativeVelocity.getY() + 1.8);
                this.airborne = true;
            } else if (Math.abs(this.relativePosition.getX()) > halfSize.getX() || Math.abs(this.relativePosition.getZ()) > halfSize.getZ()) {
                this.airborne = true;
            } else {
                this.relativeVelocity.setY(0.0);
                this.relativePosition.setY(0.0);
            }
        }

        public Vector getAbsolutePosition() {
            Vector pos = this.relativePosition.clone();
            this.lastKnownShape.getOrientation().transformPoint(pos);
            pos.add(this.lastKnownShape.getPosition());
            return pos;
        }

        public Vector getAbsoluteVelocity() {
            Vector rot = this.relativeVelocity.clone();
            CollisionSurfaceTracker.velocityRotation(this.lastKnownShape).transformPoint(rot);
            return rot;
        }
    }

    private static class PositionOnSurface {
        public final CollisionSurfaceImpl surface;
        public final Vector posOnSurface;
        public final Vector motionOnSurface;

        public PositionOnSurface(CollisionSurfaceImpl surface, Vector posOnSurface, Vector motionOnSurface) {
            this.surface = surface;
            this.posOnSurface = posOnSurface;
            this.motionOnSurface = motionOnSurface;
        }
    }
}

