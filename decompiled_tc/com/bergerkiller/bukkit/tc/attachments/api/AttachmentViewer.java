/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.controller.VehicleMountController
 *  com.bergerkiller.bukkit.common.internal.CommonCapabilities
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.protocol.CommonPacket
 *  com.bergerkiller.bukkit.common.protocol.PacketListener
 *  com.bergerkiller.bukkit.common.protocol.PacketType
 *  com.bergerkiller.bukkit.common.utils.PacketUtil
 *  com.bergerkiller.bukkit.common.utils.PlayerUtil
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddPlayerPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPlayerInputPacketHandle
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.api;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.controller.VehicleMountController;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.surface.CollisionSurface;
import com.bergerkiller.bukkit.tc.attachments.surface.StationaryCollisionElement;
import com.bergerkiller.bukkit.tc.controller.player.network.PlayerClientSynchronizer;
import com.bergerkiller.bukkit.tc.controller.player.network.PlayerPacketListener;
import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.NetworkInterface;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddPlayerPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundPlayerInputPacketHandle;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public interface AttachmentViewer
extends TrainCarts.Provider {
    public Player getPlayer();

    default public String getName() {
        return this.getPlayer().getName();
    }

    @Override
    public TrainCarts getTrainCarts();

    public VehicleMountController getVehicleMountController();

    public NetworkInterface getSmoothCoastersNetwork();

    public void send(CommonPacket var1);

    public void send(PacketHandle var1);

    public void sendSilent(CommonPacket var1);

    public void sendSilent(PacketHandle var1);

    default public void sendEntityLivingSpawnPacket(ClientboundAddMobPacketHandle packet, DataWatcher metadata) {
        if (packet.hasDataWatcherSupport()) {
            packet.setDataWatcher(metadata);
            this.send((PacketHandle)packet);
        } else {
            this.send((PacketHandle)packet);
            this.send((PacketHandle)ClientboundSetEntityDataPacketHandle.createNew((int)packet.getEntityId(), (DataWatcher)metadata, (boolean)true));
        }
    }

    default public void sendNamedEntitySpawnPacket(ClientboundAddPlayerPacketHandle packet, DataWatcher metadata) {
        if (packet.hasDataWatcherSupport()) {
            packet.setDataWatcher(metadata);
            this.send((PacketHandle)packet);
        } else {
            this.send((PacketHandle)packet);
            this.send((PacketHandle)ClientboundSetEntityDataPacketHandle.createNew((int)packet.getEntityId(), (DataWatcher)metadata, (boolean)true));
        }
    }

    default public void sendDisableCollision(UUID entityUUID) {
        this.getTrainCarts().getTeamProvider().noCollisionTeam().join(this, entityUUID);
    }

    default public void sendDisableCollision(Iterable<UUID> entityUUIDs) {
        this.getTrainCarts().getTeamProvider().noCollisionTeam().join(this, entityUUIDs);
    }

    default public boolean isValid() {
        return this.getPlayer().isValid();
    }

    default public boolean isConnected() {
        Player player = this.getPlayer();
        return player.isValid() || Bukkit.getPlayer((UUID)player.getUniqueId()) == player;
    }

    default public PlayerClientSynchronizer getClientSynchronizer() {
        return this.getTrainCarts().getPlayerClientSynchronizerProvider().forPlayer(this.getPlayer());
    }

    default public <L extends PacketListener> PlayerPacketListener<L> createPacketListener(L packetListener, PacketType ... packetTypes) {
        TrainCarts trainCarts = this.getTrainCarts();
        return trainCarts.getPlayerPacketListenerProvider().create(this.getPlayer(), packetListener, packetTypes);
    }

    default public int getEntityId() {
        return this.getPlayer().getEntityId();
    }

    default public boolean evaluateGameVersion(String operand, String rightSide) {
        return PlayerUtil.evaluateGameVersion((Player)this.getPlayer(), (String)operand, (String)rightSide);
    }

    default public boolean supportsDisplayEntities() {
        return CommonCapabilities.HAS_DISPLAY_ENTITY && this.evaluateGameVersion(">=", "1.19.4");
    }

    default public boolean supportsDisplayEntityLocationInterpolation() {
        return CommonCapabilities.HAS_DISPLAY_ENTITY_LOCATION_INTERPOLATION && this.evaluateGameVersion(">=", "1.20.2");
    }

    default public boolean supportRelativeRotationUpdate() {
        return !(!Common.evaluateMCVersion((String)"<", (String)"1.21.2") && !Common.evaluateMCVersion((String)">=", (String)"1.21.9") || !this.evaluateGameVersion("<", "1.21.2") && !this.evaluateGameVersion(">=", "1.21.9"));
    }

    default public double getArmorStandButtOffset() {
        return this.evaluateGameVersion(">=", "1.20.2") ? 0.0 : 0.27;
    }

    default public void resetGlowColor(UUID entityUUID) {
        this.getTrainCarts().getGlowColorTeamProvider().reset(this, entityUUID);
    }

    default public void updateGlowColor(UUID entityUUID, ChatColor color) {
        this.getTrainCarts().getGlowColorTeamProvider().update(this, entityUUID, color);
    }

    default public void updateGlowColor(Iterable<UUID> entityUUIDs, ChatColor color) {
        this.getTrainCarts().getGlowColorTeamProvider().update(this, entityUUIDs, color);
    }

    public static AttachmentViewer forPlayer(Player player) {
        TrainCarts trainCarts = TrainCarts.plugin;
        if (trainCarts != null && trainCarts.isEnabled()) {
            return trainCarts.getAttachmentViewer(player);
        }
        return AttachmentViewer.fallback(player);
    }

    default public MovementController controlMovement() {
        return this.controlMovement(MovementController.Options.create());
    }

    default public MovementController controlMovement(MovementController.Options options) {
        TrainCarts plugin = this.getTrainCarts();
        if (plugin.isEnabled() && this.isConnected()) {
            return plugin.getAttachmentViewer(this.getPlayer()).controlMovement(options);
        }
        return MovementController.DISABLED;
    }

    default public void stopControllingMovement() {
        TrainCarts plugin = this.getTrainCarts();
        if (plugin.isEnabled()) {
            plugin.getAttachmentViewer(this.getPlayer()).stopControllingMovement();
        }
    }

    default public CollisionSurface createCollisionSurface() {
        return this.createCollisionSurface(8);
    }

    default public CollisionSurface createCollisionSurface(int shulkerViewDistance) {
        TrainCarts plugin = this.getTrainCarts();
        if (plugin.isEnabled() && this.isConnected()) {
            return plugin.getAttachmentViewer(this.getPlayer()).createCollisionSurface(shulkerViewDistance);
        }
        return CollisionSurface.DISABLED;
    }

    default public void forAllStationaryCollisionElements(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Consumer<StationaryCollisionElement> action) {
        TrainCarts plugin = this.getTrainCarts();
        if (plugin.isEnabled() && this.isConnected()) {
            plugin.getAttachmentViewer(this.getPlayer()).forAllStationaryCollisionElements(minX, minY, minZ, maxX, maxY, maxZ, action);
        }
    }

    public static AttachmentViewer fallback(final Player player) {
        return new AttachmentViewer(){

            @Override
            public TrainCarts getTrainCarts() {
                return TrainCarts.plugin;
            }

            @Override
            public Player getPlayer() {
                return player;
            }

            @Override
            public VehicleMountController getVehicleMountController() {
                return PlayerUtil.getVehicleMountController((Player)player);
            }

            @Override
            public NetworkInterface getSmoothCoastersNetwork() {
                return null;
            }

            @Override
            public void send(CommonPacket packet) {
                PacketUtil.sendPacket((Player)player, (CommonPacket)packet);
            }

            @Override
            public void send(PacketHandle packet) {
                PacketUtil.sendPacket((Player)player, (PacketHandle)packet);
            }

            @Override
            public void sendSilent(CommonPacket packet) {
                PacketUtil.sendPacket((Player)player, (CommonPacket)packet, (boolean)false);
            }

            @Override
            public void sendSilent(PacketHandle packet) {
                PacketUtil.sendPacket((Player)player, (PacketHandle)packet, (boolean)false);
            }

            public int hashCode() {
                return player.hashCode();
            }

            public boolean equals(Object o) {
                if (o == this) {
                    return true;
                }
                if (o instanceof AttachmentViewer) {
                    return ((AttachmentViewer)o).getPlayer() == player;
                }
                return false;
            }
        };
    }

    public static Iterable<AttachmentViewer> fallbackIterable(final Iterable<Player> players) {
        return () -> new Iterator<AttachmentViewer>(){
            private final Iterator<Player> baseIter;
            {
                this.baseIter = players.iterator();
            }

            @Override
            public boolean hasNext() {
                return this.baseIter.hasNext();
            }

            @Override
            public AttachmentViewer next() {
                return AttachmentViewer.fallback(this.baseIter.next());
            }

            @Override
            public void remove() {
                this.baseIter.remove();
            }

            @Override
            public void forEachRemaining(Consumer<? super AttachmentViewer> action) {
                this.baseIter.forEachRemaining((? super E p) -> action.accept(AttachmentViewer.fallback(p)));
            }
        };
    }

    public static interface MovementController {
        public static final MovementController DISABLED = new MovementController(){

            @Override
            public void stop() {
            }

            @Override
            public boolean hasStopped() {
                return true;
            }

            @Override
            public Input getInput() {
                return Input.NONE;
            }

            @Override
            public boolean update(Vector position, Quaternion orientation, boolean stopOnCollision) {
                return false;
            }
        };

        public void stop();

        public boolean hasStopped();

        public Input getInput();

        public boolean update(Vector var1, Quaternion var2, boolean var3);

        default public boolean update(Vector position, boolean stopOnBlockCollision) {
            return this.update(position, null, stopOnBlockCollision);
        }

        default public boolean update(Vector position, Quaternion orientation) {
            return this.update(position, orientation, false);
        }

        default public boolean update(Vector position) {
            return this.update(position, false);
        }

        public static final class Options {
            private boolean preserveInput = false;
            private boolean syncAsArmorStand = true;

            public static Options create() {
                return new Options();
            }

            private Options() {
            }

            public Options preserveInput(boolean preserve) {
                this.preserveInput = preserve;
                return this;
            }

            public Options syncAsArmorstand(boolean sync) {
                this.syncAsArmorStand = sync;
                return this;
            }

            public boolean isPreserveInput() {
                return this.preserveInput;
            }

            public boolean isSyncAsArmorStand() {
                return this.syncAsArmorStand;
            }
        }
    }

    public static final class Input {
        public static final Input NONE = Input.of(false, false, false, false, false, false, false);
        private final boolean left;
        private final boolean right;
        private final boolean forwards;
        private final boolean backwards;
        private final boolean jumping;
        private final boolean sneaking;
        private final boolean sprinting;

        public static Input of(boolean left, boolean right, boolean forwards, boolean backwards, boolean jumping, boolean sneaking, boolean sprinting) {
            return new Input(left, right, forwards, backwards, jumping, sneaking, sprinting);
        }

        public static Input fromVehicleSteer(ServerboundPlayerInputPacketHandle packet) {
            return Input.of(packet.isLeft(), packet.isRight(), packet.isForward(), packet.isBackward(), packet.isJump(), packet.isUnmount(), packet.isSprint());
        }

        private Input(boolean left, boolean right, boolean forwards, boolean backwards, boolean jumping, boolean sneaking, boolean sprinting) {
            this.left = left;
            this.right = right;
            this.forwards = forwards;
            this.backwards = backwards;
            this.jumping = jumping;
            this.sneaking = sneaking;
            this.sprinting = sprinting;
        }

        public boolean left() {
            return this.left;
        }

        public boolean right() {
            return this.right;
        }

        public boolean forwards() {
            return this.forwards;
        }

        public boolean backwards() {
            return this.backwards;
        }

        public boolean jumping() {
            return this.jumping;
        }

        public boolean sneaking() {
            return this.sneaking;
        }

        public boolean sprinting() {
            return this.sprinting;
        }

        public boolean hasWalkInput() {
            return this.left || this.right || this.forwards || this.backwards;
        }

        public boolean hasDiagonalWalkInput() {
            return this.left != this.right && this.forwards != this.backwards;
        }

        public double sidewaysSigNum() {
            return this.left == this.right ? 0.0 : (this.left ? 1.0 : -1.0);
        }

        public double forwardsSigNum() {
            return this.forwards == this.backwards ? 0.0 : (this.forwards ? 1.0 : -1.0);
        }

        public double verticalSigNum() {
            return this.jumping == this.sneaking ? 0.0 : (this.jumping ? 1.0 : -1.0);
        }

        public Input withSprinting(boolean newSprinting) {
            return Input.of(this.left, this.right, this.forwards, this.backwards, this.jumping, this.sneaking, newSprinting);
        }

        public ServerboundPlayerInputPacketHandle createSteerPacket() {
            return ServerboundPlayerInputPacketHandle.createNew((boolean)this.left, (boolean)this.right, (boolean)this.forwards, (boolean)this.backwards, (boolean)this.jumping, (boolean)this.sneaking, (boolean)this.sprinting);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Input) {
                Input other = (Input)o;
                return this.left == other.left && this.right == other.right && this.forwards == other.forwards && this.backwards == other.backwards && this.jumping == other.jumping && this.sneaking == other.sneaking && this.sprinting == other.sprinting;
            }
            return false;
        }

        public String toString() {
            StringBuilder str = new StringBuilder();
            str.append("Input{");
            Input.appendIf(str, this.left, "left ");
            Input.appendIf(str, this.right, "right ");
            Input.appendIf(str, this.forwards, "forwards ");
            Input.appendIf(str, this.backwards, "backwards ");
            Input.appendIf(str, this.jumping, "jumping ");
            Input.appendIf(str, this.sneaking, "sneaking ");
            Input.appendIf(str, this.sprinting, "sprinting ");
            if (str.charAt(str.length() - 1) == ' ') {
                str.replace(str.length() - 1, str.length(), "");
            }
            str.append("}");
            return str.toString();
        }

        private static void appendIf(StringBuilder str, boolean condition, String text) {
            if (condition) {
                str.append(text);
            }
        }
    }
}

