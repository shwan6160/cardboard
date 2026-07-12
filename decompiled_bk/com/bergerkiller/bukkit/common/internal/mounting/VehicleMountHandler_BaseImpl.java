/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.internal.mounting;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.VehicleMountController;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddPlayerPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundMoveEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetCameraPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetPassengersPacketHandle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public abstract class VehicleMountHandler_BaseImpl
implements VehicleMountController {
    public static boolean SUPPORTS_MULTIPLE_PASSENGERS = ClientboundSetPassengersPacketHandle.T.isAvailable();
    private final Player _player;
    protected final CommonPlugin _plugin;
    protected final SpawnedEntity _playerSpawnedEntity;
    private DimensionType _playerDimension;
    protected IntHashMap<SpawnedEntity> _spawnedEntities;
    private final Queue<PacketHandle> _queuedPackets;
    protected int _currentTick = 0;
    private int _vanillaSpectatedEntity;
    private int[] _spectatorStack;

    public VehicleMountHandler_BaseImpl(CommonPlugin plugin, Player player) {
        DimensionType playerDimension = PlayerUtil.getPlayerDimension(player);
        this._plugin = plugin;
        this._player = player;
        this._playerDimension = playerDimension;
        this._playerSpawnedEntity = new SpawnedEntity(player.getEntityId(), CommonEntityType.PLAYER);
        this._playerSpawnedEntity.state = SpawnedEntity.State.SPAWNED;
        this._spawnedEntities = new IntHashMap();
        this._spawnedEntities.put(this._playerSpawnedEntity.id, this._playerSpawnedEntity);
        this._queuedPackets = new ConcurrentLinkedQueue<PacketHandle>();
        this._vanillaSpectatedEntity = player.getEntityId();
        this._spectatorStack = new int[0];
    }

    @Override
    public final Player getPlayer() {
        return this._player;
    }

    @Override
    public boolean mount(int vehicleEntityId, int passengerEntityId) {
        return this.synchronizeAndQueuePackets(() -> {
            Mount mount;
            SpawnedEntity vehicle = this.getSpawnedEntity(vehicleEntityId, true);
            SpawnedEntity passenger = this.getSpawnedEntity(passengerEntityId, true);
            if (vehicle == null || passenger == null) {
                return false;
            }
            Mount prevMount = passenger.vehicleMount;
            if (prevMount != null) {
                if (prevMount.vehicle == vehicle) {
                    return true;
                }
                if (prevMount.sent) {
                    prevMount.sent = false;
                    this.onUnmountVehicle(vehicle, Collections.singletonList(prevMount));
                }
                prevMount.remove();
            }
            if (vehicle.passengerMounts.isEmpty()) {
                passenger.vehicleMount = mount = new Mount(vehicle, passenger);
                vehicle.passengerMounts = Collections.singletonList(mount);
            } else if (SUPPORTS_MULTIPLE_PASSENGERS) {
                passenger.vehicleMount = mount = new Mount(vehicle, passenger);
                if (vehicle.passengerMounts.size() == 1) {
                    vehicle.passengerMounts = new ArrayList<Mount>(vehicle.passengerMounts);
                }
                vehicle.passengerMounts.add(mount);
            } else {
                return false;
            }
            if (vehicle.state.isSpawned() && passenger.state.isSpawned()) {
                mount.sent = true;
                this.onMountReady(mount);
            }
            return true;
        });
    }

    @Override
    public void unmount(int vehicleEntityId, int passengerEntityId) {
        this.synchronizeAndQueuePackets(() -> {
            SpawnedEntity vehicle = this.getSpawnedEntity(vehicleEntityId, false);
            SpawnedEntity passenger = this.getSpawnedEntity(passengerEntityId, false);
            if (vehicle == null || passenger == null) {
                return;
            }
            Mount mount = passenger.vehicleMount;
            if (mount == null || mount.vehicle != vehicle) {
                return;
            }
            mount.remove();
            if (mount.sent) {
                mount.sent = false;
                this.onUnmountVehicle(vehicle, Collections.singletonList(mount));
            }
        });
    }

    @Override
    public void remove(int entityId) {
        this.synchronizeAndQueuePackets(() -> {
            SpawnedEntity entity = this.getSpawnedEntity(entityId, false);
            if (entity != null) {
                this.clear(entity, false);
            }
        });
    }

    @Override
    public void clear(int entityId) {
        this.synchronizeAndQueuePackets(() -> {
            SpawnedEntity entity = this.getSpawnedEntity(entityId, false);
            if (entity != null) {
                this.clear(entity, true);
            }
        });
    }

    private void clear(SpawnedEntity entity, boolean handleUnmount) {
        List<Mount> passengerMounts;
        Mount vehicleMount = entity.vehicleMount;
        if (vehicleMount != null) {
            entity.vehicleMount.remove();
            if (vehicleMount.sent) {
                vehicleMount.sent = false;
                if (handleUnmount) {
                    this.onUnmountVehicle(vehicleMount.vehicle, Collections.singletonList(vehicleMount));
                }
            }
            this.tryRemoveFromTracking(vehicleMount.vehicle);
        }
        if (!(passengerMounts = entity.passengerMounts).isEmpty()) {
            entity.passengerMounts = Collections.emptyList();
            for (Mount m : passengerMounts) {
                m.sent = false;
                m.passenger.vehicleMount = null;
                this.tryRemoveFromTracking(m.passenger);
            }
            if (handleUnmount) {
                this.onUnmountVehicle(entity, passengerMounts);
            }
        }
        this.tryRemoveFromTracking(entity);
    }

    @Override
    public synchronized int getVehicle(int passengerEntityId) {
        SpawnedEntity spawnedEntity = this.getSpawnedEntity(passengerEntityId, false);
        return spawnedEntity == null || spawnedEntity.vehicleMount == null || !spawnedEntity.vehicleMount.sent ? -1 : spawnedEntity.vehicleMount.vehicle.id;
    }

    @Override
    public synchronized int[] getPassengers(int vehicleEntityId) {
        SpawnedEntity spawnedEntity = this.getSpawnedEntity(vehicleEntityId, false);
        return spawnedEntity == null ? new int[]{} : spawnedEntity.collectSentPassengerIds();
    }

    @Override
    public void despawn(int entityId) {
        this.synchronizeAndQueuePackets(() -> {
            SpawnedEntity entity = this.getSpawnedEntity(entityId, true);
            if (entity.state == SpawnedEntity.State.SPAWNED) {
                entity.state = SpawnedEntity.State.SUPPRESSED_INFLIGHT_BLOCKED;
                this.queuePacket(ClientboundRemoveEntitiesPacketHandle.createNewSingle(entityId));
            } else if (entity.state == SpawnedEntity.State.DESPAWNED) {
                entity.state = SpawnedEntity.State.DESPAWNED_BLOCKED;
            }
        });
    }

    @Override
    public void respawn(int entityId, VehicleMountController.RespawnFunctionWithEntityId respawnFunction) {
        this.respawn(entityId, () -> respawnFunction.respawn(this._player, entityId));
    }

    @Override
    public <T extends Entity> void respawn(T entity, VehicleMountController.RespawnFunctionWithEntity<T> respawnFunction) {
        this.respawn(entity.getEntityId(), () -> respawnFunction.respawn(this._player, entity));
    }

    @Override
    public void respawn(int entityId, Runnable respawnAction) {
        this.synchronizeAndQueuePackets(() -> {
            SpawnedEntity entity = this.getSpawnedEntity(entityId, true);
            switch (entity.state.ordinal()) {
                case 2: {
                    entity.state = SpawnedEntity.State.DESPAWNED;
                    this.tryRemoveFromTracking(entity);
                    break;
                }
                case 3: {
                    entity.state = SpawnedEntity.State.SPAWNED;
                    respawnAction.run();
                    break;
                }
                case 4: {
                    entity.state = SpawnedEntity.State.DESPAWNED;
                    respawnAction.run();
                    break;
                }
            }
        });
    }

    @Override
    public synchronized boolean isSpectating(int entityId) {
        int len = this._spectatorStack.length;
        while (--len >= 0) {
            if (this._spectatorStack[len] != entityId) continue;
            return true;
        }
        return false;
    }

    @Override
    public synchronized void startSpectating(int entityId) {
        int len = this._spectatorStack.length;
        if (len == 0) {
            this._spectatorStack = new int[]{entityId};
        } else {
            if (this._spectatorStack[len - 1] == entityId) {
                return;
            }
            this.removeFromSpectatorStack(entityId);
            len = this._spectatorStack.length;
            int[] newArr = Arrays.copyOf(this._spectatorStack, len + 1);
            newArr[len] = entityId;
            this._spectatorStack = newArr;
        }
        PacketUtil.sendPacket(this._player, ClientboundSetCameraPacketHandle.createNew(entityId), false);
    }

    @Override
    public synchronized void stopSpectating(int entityId) {
        int previousSpectatedEntityId = this.handleStopSpectating(entityId);
        if (previousSpectatedEntityId != -1) {
            PacketUtil.sendPacket(this._player, ClientboundSetCameraPacketHandle.createNew(previousSpectatedEntityId), false);
        }
    }

    @Override
    public synchronized void swapSpectating(int oldEntityId, int newEntityId) {
        int idx = this._spectatorStack.length - 1;
        if (idx >= 0) {
            if (this._spectatorStack[idx] == oldEntityId) {
                if (oldEntityId == newEntityId) {
                    return;
                }
                this._spectatorStack[idx] = newEntityId;
            } else {
                while (--idx >= 0) {
                    if (this._spectatorStack[idx] != oldEntityId) continue;
                    this._spectatorStack[idx] = newEntityId;
                    return;
                }
                int len = this._spectatorStack.length;
                this._spectatorStack = Arrays.copyOf(this._spectatorStack, len + 1);
                this._spectatorStack[len] = newEntityId;
            }
        }
        PacketUtil.sendPacket(this._player, ClientboundSetCameraPacketHandle.createNew(newEntityId), false);
    }

    private int handleStopSpectating(int entityId) {
        int len = this._spectatorStack.length;
        if (len == 0) {
            return -1;
        }
        if (len == 1) {
            if (this._spectatorStack[0] != entityId) {
                return -1;
            }
            this._spectatorStack = new int[0];
            return this._vanillaSpectatedEntity;
        }
        if (this._spectatorStack[len - 1] == entityId) {
            this._spectatorStack = Arrays.copyOf(this._spectatorStack, len - 1);
            return this._spectatorStack[len - 2];
        }
        this.removeFromSpectatorStack(entityId);
        return -1;
    }

    private void removeFromSpectatorStack(int entityId) {
        int len = this._spectatorStack.length;
        for (int n = len - 1; n >= 0; --n) {
            if (this._spectatorStack[n] != entityId) continue;
            int[] newArr = new int[len - 1];
            System.arraycopy(this._spectatorStack, 0, newArr, 0, n);
            System.arraycopy(this._spectatorStack, n + 1, newArr, n, len - n - 1);
            this._spectatorStack = newArr;
            return;
        }
    }

    public void update() {
        ++this._currentTick;
    }

    public final void handlePacketSend(CommonPacket packet) {
        this.synchronizeAndQueuePackets(() -> {
            if (this._playerDimension == null) {
                this._playerDimension = PlayerUtil.getPlayerDimension(this._player);
            }
            this.onPacketSend(packet);
            PacketType type = packet.getType();
            if (type == PacketType.OUT_ENTITY_DESTROY) {
                ClientboundRemoveEntitiesPacketHandle dp = ClientboundRemoveEntitiesPacketHandle.createHandle(packet.getHandle());
                if (dp.hasMultipleEntityIds()) {
                    for (int entityId : dp.getEntityIds()) {
                        this.handleDespawn(entityId);
                    }
                } else {
                    this.handleDespawn(dp.getSingleEntityId());
                }
            } else if (type == PacketType.OUT_RESPAWN) {
                DimensionType dimension;
                try {
                    dimension = packet.read(PacketType.OUT_RESPAWN.dimensionType);
                }
                catch (IllegalArgumentException ex) {
                    dimension = null;
                }
                if (dimension != null && !dimension.equals(this._playerDimension)) {
                    this._playerDimension = dimension;
                    this.handleReset();
                }
            } else if (type == PacketType.OUT_CAMERA) {
                this._vanillaSpectatedEntity = packet.read(PacketType.OUT_CAMERA.entityId);
                int len = this._spectatorStack.length;
                if (len > 0) {
                    packet.write(PacketType.OUT_CAMERA.entityId, this._spectatorStack[len - 1]);
                }
            } else if (this.isPositionTracked()) {
                if (type == PacketType.OUT_ENTITY_SPAWN) {
                    ClientboundAddEntityPacketHandle handle = ClientboundAddEntityPacketHandle.createHandle(packet.getHandle());
                    this.handleSpawn(handle.getEntityId(), handle.getCommonEntityType(), new Vector(handle.getPosX(), handle.getPosY(), handle.getPosZ()));
                } else if (type == PacketType.OUT_ENTITY_SPAWN_LIVING) {
                    ClientboundAddMobPacketHandle handle = ClientboundAddMobPacketHandle.createHandle(packet.getHandle());
                    this.handleSpawn(handle.getEntityId(), handle.getCommonEntityType(), new Vector(handle.getPosX(), handle.getPosY(), handle.getPosZ()));
                } else if (type == PacketType.OUT_ENTITY_SPAWN_NAMED) {
                    ClientboundAddPlayerPacketHandle handle = ClientboundAddPlayerPacketHandle.createHandle(packet.getHandle());
                    this.handleSpawn(handle.getEntityId(), CommonEntityType.PLAYER, new Vector(handle.getPosX(), handle.getPosY(), handle.getPosZ()));
                } else if (type == PacketType.OUT_ENTITY_TELEPORT) {
                    ClientboundEntityPositionSyncPacketHandle handle = ClientboundEntityPositionSyncPacketHandle.createHandle(packet.getHandle());
                    this.handleMove(handle.getEntityId(), position -> {
                        position.setX(handle.getPosX());
                        position.setY(handle.getPosY());
                        position.setZ(handle.getPosZ());
                    });
                } else if (type == PacketType.OUT_ENTITY_MOVE || type == PacketType.OUT_ENTITY_MOVE_LOOK) {
                    ClientboundMoveEntityPacketHandle handle = ClientboundMoveEntityPacketHandle.createHandle(packet.getHandle());
                    this.handleMove(handle.getEntityId(), position -> {
                        position.setX(position.getX() + handle.getDeltaX());
                        position.setY(position.getY() + handle.getDeltaY());
                        position.setZ(position.getZ() + handle.getDeltaZ());
                    });
                }
            } else if (type == PacketType.OUT_ENTITY_SPAWN) {
                ClientboundAddEntityPacketHandle handle = ClientboundAddEntityPacketHandle.createHandle(packet.getHandle());
                this.handleSpawn(handle.getEntityId(), handle.getCommonEntityType(), null);
            } else if (type == PacketType.OUT_ENTITY_SPAWN_LIVING) {
                ClientboundAddMobPacketHandle handle = ClientboundAddMobPacketHandle.createHandle(packet.getHandle());
                this.handleSpawn(handle.getEntityId(), handle.getCommonEntityType(), null);
            } else if (type == PacketType.OUT_ENTITY_SPAWN_NAMED) {
                ClientboundAddPlayerPacketHandle handle = ClientboundAddPlayerPacketHandle.createHandle(packet.getHandle());
                this.handleSpawn(handle.getEntityId(), CommonEntityType.PLAYER, null);
            }
        });
    }

    public final void handlePacketReceive(CommonPacket packet) {
        this.synchronizeAndQueuePackets(() -> this.onPacketReceive(packet));
    }

    public final void handleRemoved() {
        this.synchronizeAndQueuePackets(() -> this.onRemoved());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void synchronizeAndQueuePackets(Runnable r) {
        PacketHandle p;
        try {
            VehicleMountHandler_BaseImpl vehicleMountHandler_BaseImpl = this;
            synchronized (vehicleMountHandler_BaseImpl) {
                r.run();
            }
        }
        catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Error handling vehicle mount packets for player " + this._player.getName(), t);
        }
        while ((p = this._queuedPackets.poll()) != null) {
            PacketUtil.queuePacket(this._player, p);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final <T> T synchronizeAndQueuePackets(Supplier<T> s) {
        PacketHandle p;
        T result;
        VehicleMountHandler_BaseImpl vehicleMountHandler_BaseImpl = this;
        synchronized (vehicleMountHandler_BaseImpl) {
            result = s.get();
        }
        while ((p = this._queuedPackets.poll()) != null) {
            PacketUtil.queuePacket(this._player, p);
        }
        return result;
    }

    private final void handleSpawn(int entityId, CommonEntityType type, Vector position) {
        SpawnedEntity entity = this.getSpawnedEntity(entityId, true);
        if (entity == null || entity == this._playerSpawnedEntity) {
            return;
        }
        if (entity.state == SpawnedEntity.State.SPAWNED) {
            entity.state = SpawnedEntity.State.DESPAWNED;
            this.onDespawned(entity);
        }
        if (entity.state.isBlocked()) {
            entity.state = SpawnedEntity.State.SUPPRESSED_INFLIGHT_BLOCKED;
            this.queuePacket(ClientboundRemoveEntitiesPacketHandle.createNewSingle(entityId));
        } else {
            entity.state = SpawnedEntity.State.SPAWNED;
            entity.type = type;
            entity.position = position;
            entity.position_sync = this._currentTick;
            this.onSpawned(entity);
        }
    }

    private final void handleDespawn(int entityId) {
        SpawnedEntity entity = this.getSpawnedEntity(entityId, false);
        if (entity == null || entity == this._playerSpawnedEntity) {
            return;
        }
        switch (entity.state.ordinal()) {
            case 1: {
                entity.state = SpawnedEntity.State.DESPAWNED;
                this.onDespawned(entity);
                this.tryRemoveFromTracking(entity);
                break;
            }
            case 3: {
                entity.state = SpawnedEntity.State.SUPPRESSED_BLOCKED;
                break;
            }
            case 4: {
                entity.state = SpawnedEntity.State.DESPAWNED_BLOCKED;
                break;
            }
        }
    }

    private final void handleMove(int entityId, Consumer<Vector> modify) {
        SpawnedEntity entity = this.getSpawnedEntity(entityId, false);
        if (!(entity == null || entity.vehicleMount != null && entity.vehicleMount.sent)) {
            if (entity.position == null) {
                entity.position = new Vector();
            }
            modify.accept(entity.position);
            entity.position_sync = this._currentTick;
            entity.propagatePosition();
        }
    }

    private final synchronized void handleReset() {
        for (SpawnedEntity entity : this._spawnedEntities.values()) {
            if (entity.vehicleMount != null) {
                entity.vehicleMount.sent = false;
            }
            if (entity == this._playerSpawnedEntity) continue;
            entity.state = entity.state.isBlocked() ? SpawnedEntity.State.DESPAWNED_BLOCKED : SpawnedEntity.State.DESPAWNED;
            this.tryRemoveFromTracking(entity);
        }
    }

    private void onDespawned(SpawnedEntity entity) {
        if (entity.vehicleMount != null) {
            entity.vehicleMount.sent = false;
        }
        for (Mount mount : entity.passengerMounts) {
            mount.sent = false;
        }
        if (this._vanillaSpectatedEntity == entity.id) {
            this._vanillaSpectatedEntity = this._player.getEntityId();
        }
    }

    private final synchronized void tryRemoveFromTracking(SpawnedEntity entity) {
        if (entity.state == SpawnedEntity.State.DESPAWNED && entity.vehicleMount == null && entity.passengerMounts.isEmpty()) {
            this._spawnedEntities.remove(entity.id);
        }
    }

    protected final synchronized SpawnedEntity getSpawnedEntity(int entityId, boolean create) {
        SpawnedEntity spawnedEntity = this._spawnedEntities.get(entityId);
        if (spawnedEntity == null && create && entityId >= 0) {
            spawnedEntity = new SpawnedEntity(entityId);
            this._spawnedEntities.put(entityId, spawnedEntity);
        }
        return spawnedEntity;
    }

    protected final synchronized boolean isSpawned(int entityId) {
        SpawnedEntity spawnedEntity = this._spawnedEntities.get(entityId);
        return spawnedEntity != null && spawnedEntity.state.isSpawned();
    }

    protected final void queuePacket(PacketHandle packet) {
        this._queuedPackets.add(packet);
    }

    protected void onRemoved() {
    }

    protected abstract void onUnmountVehicle(SpawnedEntity var1, List<Mount> var2);

    protected abstract void onMountReady(Mount var1);

    protected abstract void onSpawned(SpawnedEntity var1);

    protected abstract void onPacketSend(CommonPacket var1);

    protected abstract void onPacketReceive(CommonPacket var1);

    protected boolean isPositionTracked() {
        return false;
    }

    protected static final class SpawnedEntity {
        public final int id;
        public CommonEntityType type;
        public State state;
        public List<Mount> passengerMounts;
        public Mount vehicleMount;
        public Vector position;
        public int position_sync;

        public SpawnedEntity(int entityId) {
            this(entityId, CommonEntityType.UNKNOWN);
        }

        public SpawnedEntity(int entityId, CommonEntityType type) {
            this.id = entityId;
            this.state = State.DESPAWNED;
            this.passengerMounts = Collections.emptyList();
            this.vehicleMount = null;
            this.position = null;
            this.position_sync = -1;
            this.type = type;
        }

        public void propagatePosition() {
            for (Mount mount : this.passengerMounts) {
                if (!mount.sent) continue;
                if (mount.passenger.position == null) {
                    mount.passenger.position = this.position.clone();
                } else {
                    MathUtil.setVector(mount.passenger.position, this.position);
                }
                mount.passenger.position_sync = this.position_sync;
                mount.passenger.propagatePosition();
            }
        }

        public int[] collectSentPassengerIds() {
            int size = this.passengerMounts.size();
            if (size == 0) {
                return new int[0];
            }
            if (size == 1) {
                int[] nArray;
                Mount m = this.passengerMounts.get(0);
                if (m.sent) {
                    int[] nArray2 = new int[1];
                    nArray = nArray2;
                    nArray2[0] = m.passenger.id;
                } else {
                    nArray = new int[]{};
                }
                return nArray;
            }
            ArrayList<SpawnedEntity> sentMounts = new ArrayList<SpawnedEntity>(this.passengerMounts.size());
            for (Mount m : this.passengerMounts) {
                if (!m.sent) continue;
                sentMounts.add(m.passenger);
            }
            int[] result = new int[sentMounts.size()];
            for (int i = 0; i < result.length; ++i) {
                result[i] = ((SpawnedEntity)sentMounts.get((int)i)).id;
            }
            return result;
        }

        public String toString() {
            return "{id: " + this.id + "}";
        }

        public static enum State {
            DESPAWNED(false, false),
            SPAWNED(true, false),
            DESPAWNED_BLOCKED(false, true),
            SUPPRESSED_INFLIGHT_BLOCKED(false, true),
            SUPPRESSED_BLOCKED(false, true);

            private final boolean spawned;
            private final boolean blocked;

            private State(boolean spawned, boolean blocked) {
                this.spawned = spawned;
                this.blocked = blocked;
            }

            public boolean isSpawned() {
                return this.spawned;
            }

            public boolean isBlocked() {
                return this.blocked;
            }
        }
    }

    protected static final class Mount {
        public final SpawnedEntity vehicle;
        public final SpawnedEntity passenger;
        public boolean sent;

        public Mount(SpawnedEntity vehicle, SpawnedEntity passenger) {
            this.vehicle = vehicle;
            this.passenger = passenger;
            this.sent = false;
        }

        public void remove() {
            this.vehicle.passengerMounts = Mount.removeFromImmutableList(this.vehicle.passengerMounts, this);
            if (this.passenger.vehicleMount == this) {
                this.passenger.vehicleMount = null;
            }
        }

        private static <T> List<T> removeFromImmutableList(List<T> list, T value) {
            int size = list.size();
            if (size == 2) {
                if (list.get(0) == value) {
                    return Collections.singletonList(list.get(1));
                }
                if (list.get(1) == value) {
                    return Collections.singletonList(list.get(0));
                }
                return list;
            }
            if (size > 2) {
                list.remove(value);
                return list;
            }
            if (size == 1 && list.get(0) == value) {
                return Collections.emptyList();
            }
            return list;
        }
    }
}

