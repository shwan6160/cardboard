/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.player.PlayerVelocityEvent
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.controller;

import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.bases.mutable.FloatAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.IntegerAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.LocationAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.VectorAbstract;
import com.bergerkiller.bukkit.common.controller.DefaultEntityNetworkController;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.CommonEntityController;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook_1_8_to_1_13_2;
import com.bergerkiller.bukkit.common.internal.logic.EntityTypingHandler;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectInstanceHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributeInstanceHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public abstract class EntityNetworkController<T extends CommonEntity<?>>
extends CommonEntityController<T> {
    public static final double MAX_RELATIVE_DISTANCE = 8.0;
    public static final double MIN_RELATIVE_POS_CHANGE = 0.03125;
    public static final float MIN_RELATIVE_ROT_CHANGE = 0.011111111f;
    public static final double MIN_RELATIVE_VELOCITY = 0.02;
    public static final int ABSOLUTE_UPDATE_INTERVAL = 400;
    private EntityTrackerEntryHandle entry;
    private EntityTrackerEntryStateHandle state;
    private Entity last_passenger_1_8_8 = null;
    public VectorAbstract velSynched = new VectorAbstract(){

        @Override
        public Vector vector() {
            return EntityNetworkController.this.state.getVelocity();
        }

        @Override
        public double getX() {
            return EntityNetworkController.this.state.getXVel();
        }

        @Override
        public double getY() {
            return EntityNetworkController.this.state.getYVel();
        }

        @Override
        public double getZ() {
            return EntityNetworkController.this.state.getZVel();
        }

        @Override
        public VectorAbstract set(double x, double y, double z) {
            EntityNetworkController.this.state.setVelocity(x, y, z);
            return this;
        }

        @Override
        public VectorAbstract setX(double x) {
            EntityNetworkController.this.state.setXVel(x);
            return this;
        }

        @Override
        public VectorAbstract setY(double y) {
            EntityNetworkController.this.state.setYVel(y);
            return this;
        }

        @Override
        public VectorAbstract setZ(double z) {
            EntityNetworkController.this.state.setZVel(z);
            return this;
        }
    };
    public VectorAbstract velLive = new VectorAbstract(){

        @Override
        public double getX() {
            return ((EntityNetworkController)EntityNetworkController.this).entity.vel.getX();
        }

        @Override
        public double getY() {
            return ((EntityNetworkController)EntityNetworkController.this).entity.vel.getY();
        }

        @Override
        public double getZ() {
            return ((EntityNetworkController)EntityNetworkController.this).entity.vel.getZ();
        }

        @Override
        public VectorAbstract setX(double x) {
            ((EntityNetworkController)EntityNetworkController.this).entity.vel.setX(x);
            return this;
        }

        @Override
        public VectorAbstract setY(double y) {
            ((EntityNetworkController)EntityNetworkController.this).entity.vel.setY(y);
            return this;
        }

        @Override
        public VectorAbstract setZ(double z) {
            ((EntityNetworkController)EntityNetworkController.this).entity.vel.setZ(z);
            return this;
        }
    };
    public LocationAbstract locSynched = new LocationAbstract(){

        @Override
        public World getWorld() {
            return EntityNetworkController.this.entity.getWorld();
        }

        @Override
        public LocationAbstract setWorld(World world) {
            EntityNetworkController.this.entity.setWorld(world);
            return this;
        }

        @Override
        public Vector vector() {
            return EntityNetworkController.this.state.getLoc();
        }

        @Override
        public double getX() {
            return EntityNetworkController.this.state.getLocX();
        }

        @Override
        public double getY() {
            return EntityNetworkController.this.state.getLocY();
        }

        @Override
        public double getZ() {
            return EntityNetworkController.this.state.getLocZ();
        }

        @Override
        public LocationAbstract set(double x, double y, double z) {
            EntityNetworkController.this.state.setLoc(x, y, z);
            return this;
        }

        @Override
        public LocationAbstract setX(double x) {
            EntityNetworkController.this.state.setLocX(x);
            return this;
        }

        @Override
        public LocationAbstract setY(double y) {
            EntityNetworkController.this.state.setLocY(y);
            return this;
        }

        @Override
        public LocationAbstract setZ(double z) {
            EntityNetworkController.this.state.setLocZ(z);
            return this;
        }

        @Override
        public float getYaw() {
            return EntityNetworkController.this.state.getYaw();
        }

        @Override
        public float getPitch() {
            return EntityNetworkController.this.state.getPitch();
        }

        @Override
        public LocationAbstract setYaw(float yaw) {
            EntityNetworkController.this.state.setYaw(yaw);
            return this;
        }

        @Override
        public LocationAbstract setPitch(float pitch) {
            EntityNetworkController.this.state.setPitch(pitch);
            return this;
        }
    };
    public LocationAbstract locLive = new LocationAbstract(){

        @Override
        public World getWorld() {
            return EntityNetworkController.this.entity.getWorld();
        }

        @Override
        public LocationAbstract setWorld(World world) {
            EntityNetworkController.this.entity.setWorld(world);
            return this;
        }

        @Override
        public double getX() {
            return ((EntityNetworkController)EntityNetworkController.this).entity.loc.getX();
        }

        @Override
        public double getY() {
            return ((EntityNetworkController)EntityNetworkController.this).entity.loc.getY();
        }

        @Override
        public double getZ() {
            return ((EntityNetworkController)EntityNetworkController.this).entity.loc.getZ();
        }

        @Override
        public LocationAbstract setX(double x) {
            ((EntityNetworkController)EntityNetworkController.this).entity.loc.setX(x);
            return this;
        }

        @Override
        public LocationAbstract setY(double y) {
            ((EntityNetworkController)EntityNetworkController.this).entity.loc.setY(y);
            return this;
        }

        @Override
        public LocationAbstract setZ(double z) {
            ((EntityNetworkController)EntityNetworkController.this).entity.loc.setZ(z);
            return this;
        }

        @Override
        public float getYaw() {
            return ((EntityNetworkController)EntityNetworkController.this).entity.loc.getYaw();
        }

        @Override
        public float getPitch() {
            return ((EntityNetworkController)EntityNetworkController.this).entity.loc.getPitch();
        }

        @Override
        public LocationAbstract setYaw(float yaw) {
            ((EntityNetworkController)EntityNetworkController.this).entity.loc.setYaw(yaw);
            return this;
        }

        @Override
        public LocationAbstract setPitch(float pitch) {
            ((EntityNetworkController)EntityNetworkController.this).entity.loc.setPitch(pitch);
            return this;
        }
    };
    public FloatAbstract headRotSynched = new FloatAbstract(){

        @Override
        public float get() {
            return EntityNetworkController.this.state.getHeadYaw();
        }

        @Override
        public FloatAbstract set(float value) {
            EntityNetworkController.this.state.setHeadYaw(value);
            return this;
        }
    };
    public FloatAbstract headRotLive = new FloatAbstract(){

        @Override
        public float get() {
            return EntityNetworkController.this.entity.getHeadRotation();
        }

        @Override
        public FloatAbstract set(float value) {
            throw new UnsupportedOperationException();
        }
    };
    public IntegerAbstract ticks = new IntegerAbstract(){

        @Override
        public int get() {
            return EntityNetworkController.this.state.getTickCounter();
        }

        @Override
        public IntegerAbstract set(int value) {
            EntityNetworkController.this.state.setTickCounter(value);
            return this;
        }
    };

    public int getViewDistance() {
        return this.entry.getTrackingDistance();
    }

    public void setViewDistance(int blockDistance) {
        this.entry.setTrackingDistance(blockDistance);
    }

    public int getUpdateInterval() {
        return this.state.getUpdateInterval();
    }

    public void setUpdateInterval(int tickInterval) {
        this.state.setUpdateInterval(tickInterval);
    }

    public boolean isMobile() {
        return this.state.isMobile();
    }

    public void setMobile(boolean mobile) {
        this.state.setIsMobile(mobile);
    }

    public int getTicksSinceLocationSync() {
        return this.state.getTimeSinceLocationSync();
    }

    public boolean isUpdateTick() {
        return this.ticks.isMod(this.getUpdateInterval());
    }

    public final void bind(T entity, Object entityTrackerEntry) {
        EntityTrackerEntryHook hook;
        if (this.entity != null) {
            EntityTrackerEntryHook oldHook;
            this.onDetached();
            if (this.entry != null && (oldHook = EntityTypingHandler.INSTANCE.getEntityTrackerEntryHook(this.entry.getRaw())) != null && oldHook.getController() == this) {
                EntityNetworkController defaultController = (EntityNetworkController)LogicUtil.unsafeCast(new DefaultEntityNetworkController());
                defaultController.bind(this.entity, this.entry.getRaw());
            }
            this.entity = null;
            this.entry = null;
            this.state = null;
        }
        if (entity == null) {
            return;
        }
        this.entity = entity;
        this.entry = EntityTrackerEntryHandle.createHandle(entityTrackerEntry);
        this.state = this.entry.getState();
        if (!CommonCapabilities.MULTIPLE_PASSENGERS) {
            this.last_passenger_1_8_8 = ((ExtendedEntity)entity).getPassenger();
        }
        if ((hook = EntityTypingHandler.INSTANCE.getEntityTrackerEntryHook(this.entry.getRaw())) != null) {
            hook.setController(this);
        }
        if (this.entity.isSpawned()) {
            this.onAttached();
        }
    }

    public Object getHandle() {
        return this.entry.getRaw();
    }

    public EntityTrackerEntryStateHandle getStateHandle() {
        return this.state;
    }

    public final Collection<Player> getViewers() {
        return Collections.unmodifiableCollection(this.entry.getViewers());
    }

    public boolean addViewer(Player viewer) {
        if (!this.entry.addViewerToSet(viewer)) {
            return false;
        }
        this.makeVisible(viewer);
        return true;
    }

    public boolean removeViewer(Player viewer) {
        if (!this.entry.removeViewerFromSet(viewer)) {
            return false;
        }
        this.makeHidden(viewer);
        return true;
    }

    @Deprecated
    public final boolean isViewable(Player viewer) {
        return new EntityTrackerEntryHook_1_8_to_1_13_2.ViewableLogic(this).isViewable(viewer);
    }

    public void setRemoveNextTick(Player player, boolean remove) {
        LogicUtil.addOrRemove(PlayerUtil.getEntityRemoveQueue(player), this.entity.getEntityId(), remove);
    }

    public void makeHidden(Player viewer, boolean instant) {
        this.setRemoveNextTick(viewer, !instant);
        if (instant) {
            PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_DESTROY.newInstanceSingle(this.entity.getEntityId()));
        }
    }

    public void makeHidden(Player viewer) {
        this.makeHidden(viewer, false);
    }

    public void makeHiddenForAll(boolean instant) {
        for (Player viewer : this.getViewers()) {
            this.makeHidden(viewer, instant);
        }
    }

    public void makeHiddenForAll() {
        for (Player viewer : this.getViewers()) {
            this.makeHidden(viewer);
        }
    }

    protected void onPassengersChanged(List<Entity> oldPassengers, List<Entity> newPassengers) {
        for (Player viewer : this.getViewers()) {
            this.onSyncPassengers(viewer, oldPassengers, newPassengers);
        }
    }

    protected void onSyncPassengers(Player viewer, List<Entity> oldPassengers, List<Entity> newPassengers) {
        if (PacketType.OUT_MOUNT.isValid()) {
            PacketUtil.sendPacket(viewer, PacketType.OUT_MOUNT.newInstance((Entity)this.entity.getEntity(), newPassengers));
        }
    }

    public void makeVisible(Player viewer) {
        float headRot;
        Entity leashHolder;
        Entity vehicle;
        List<Entity> passengers;
        this.setRemoveNextTick(viewer, false);
        PacketUtil.sendPacket(viewer, this.getSpawnPacket());
        this.initMetaData(viewer);
        if (this.isMobile()) {
            PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_VELOCITY.newInstance(this.entity.getEntityId(), this.velSynched.vector()));
        }
        if (!(passengers = this.getSynchedPassengers()).isEmpty()) {
            this.onSyncPassengers(viewer, new ArrayList<Entity>(0), passengers);
        }
        if (EntityTrackerEntryStateHandle.T.opt_vehicle.isAvailable() && (vehicle = EntityTrackerEntryStateHandle.T.opt_vehicle.get(this.state.getRaw())) != null) {
            PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_ATTACH.newInstanceMount((Entity)this.entity.getEntity(), vehicle));
        }
        if ((leashHolder = this.entity.getLeashHolder()) != null) {
            PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_ATTACH.newInstanceLeash(leashHolder, (Entity)this.entity.getEntity()));
        }
        if ((headRot = this.headRotLive.get()) != 0.0f) {
            PacketUtil.sendPacket(viewer, this.getHeadRotationPacket(headRot));
        }
    }

    public void initMetaData(Player viewer) {
        Object entityHandle;
        DataWatcher metaData = this.entity.getMetaData();
        if (!metaData.isEmpty()) {
            PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_METADATA.newInstance(this.entity.getEntityId(), metaData, true));
        }
        if (LivingEntityHandle.T.isAssignableFrom(entityHandle = this.entity.getHandle())) {
            LivingEntityHandle living = LivingEntityHandle.createHandle(entityHandle);
            Collection<AttributeInstanceHandle> attributes = living.getAttributeMap().getSynchronizedAttributes();
            if (!attributes.isEmpty()) {
                PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_UPDATE_ATTRIBUTES.newInstance(this.entity.getEntityId(), attributes));
            }
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack itemstack;
                if (!EntityUtil.isEquipmentSupported(this.entity.getEntity(), slot) || (itemstack = living.getEquipment(slot)) == null) continue;
                PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_EQUIPMENT.newInstance(this.entity.getEntityId(), slot, itemstack));
            }
            for (MobEffectInstanceHandle effect : living.getEffects()) {
                PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_EFFECT_ADD.newInstance(this.entity.getEntityId(), effect));
            }
        }
    }

    public void syncRespawn() {
        for (Player viewer : this.getViewers()) {
            this.makeHidden(viewer, true);
        }
        this.velSynched.set(this.velLive);
        this.locSynched.set(this.locLive);
        this.headRotSynched.set(this.headRotLive.get());
        for (Player viewer : this.getViewers()) {
            this.makeVisible(viewer);
        }
    }

    @Override
    public void onTick() {
        if (this.entity.isDead()) {
            return;
        }
        this.syncPassengers();
        if (this.isUpdateTick() || this.entity.isPositionChanged() || this.entity.getDataWatcher().isChanged()) {
            this.entity.setPositionChanged(false);
            if (this.getTicksSinceLocationSync() > 400) {
                this.syncLocationAbsolute();
            } else {
                this.syncLocation();
            }
            this.syncVelocity();
        }
        if (this.entity.isVelocityChanged()) {
            this.entity.setVelocityChanged(false);
            Vector velocity = this.velLive.vector();
            boolean cancelled = false;
            if (this.entity.getEntity() instanceof Player) {
                PlayerVelocityEvent event = new PlayerVelocityEvent((Player)this.entity.getEntity(), velocity);
                if (CommonUtil.callEvent(event).isCancelled()) {
                    cancelled = true;
                } else if (!velocity.equals((Object)event.getVelocity())) {
                    velocity = event.getVelocity();
                    this.velLive.set(velocity);
                }
            }
            if (!cancelled) {
                this.broadcast(this.getVelocityPacket(velocity.getX(), velocity.getY(), velocity.getZ()));
            }
        }
        this.syncMetaData();
        this.syncHeadRotation();
    }

    public List<Entity> getSynchedPassengers() {
        if (!EntityTrackerEntryStateHandle.T.opt_passengers.isAvailable()) {
            if (this.last_passenger_1_8_8 == null) {
                return Collections.emptyList();
            }
            return Collections.singletonList(this.last_passenger_1_8_8);
        }
        return Collections.unmodifiableList(EntityTrackerEntryStateHandle.T.opt_passengers.get(this.state.getRaw()));
    }

    public boolean isPassengersChanged() {
        Entity new_vehicle;
        Entity old_vehicle;
        if (EntityTrackerEntryStateHandle.T.opt_passengers.isAvailable()) {
            List<Entity> old_passengers = this.getSynchedPassengers();
            List<Entity> new_passengers = this.entity.getPassengers();
            if (old_passengers.size() != new_passengers.size()) {
                return true;
            }
            for (int i = 0; i < old_passengers.size(); ++i) {
                if (old_passengers.get(i).getEntityId() == new_passengers.get(i).getEntityId()) continue;
                return true;
            }
        } else if (EntityTrackerEntryStateHandle.T.opt_vehicle.isAvailable() && (old_vehicle = EntityTrackerEntryStateHandle.T.opt_vehicle.get(this.state.getRaw())) != (new_vehicle = this.entity.getVehicle())) {
            return true;
        }
        return false;
    }

    public boolean isPositionChanged(double minChange) {
        return Math.abs(this.locLive.getX() - this.locSynched.getX()) >= minChange || Math.abs(this.locLive.getY() - this.locSynched.getY()) >= minChange || Math.abs(this.locLive.getZ() - this.locSynched.getZ()) >= minChange;
    }

    public boolean isRotationChanged(float minChange) {
        return MathUtil.getAngleDifference(this.locLive.getYaw(), this.locSynched.getYaw()) >= minChange || MathUtil.getAngleDifference(this.locLive.getPitch(), this.locSynched.getPitch()) >= minChange;
    }

    public boolean isVelocityChanged(double minChange) {
        return this.velLive.distanceSquared(this.velSynched) > minChange * minChange;
    }

    public boolean isHeadRotationChanged(float minChange) {
        return Math.abs(this.headRotLive.get() - this.headRotSynched.get()) >= minChange;
    }

    public void syncMetaData() {
        Object entityHandle;
        DataWatcher meta = this.entity.getMetaData();
        if (meta.isChanged()) {
            this.broadcast(PacketType.OUT_ENTITY_METADATA.newInstance(this.entity.getEntityId(), meta, false), true);
        }
        if (LivingEntityHandle.T.isAssignableFrom(entityHandle = this.entity.getHandle())) {
            LivingEntityHandle living = LivingEntityHandle.createHandle(entityHandle);
            Collection<AttributeInstanceHandle> attributes = living.getAttributeMap().getSynchronizedAttributes();
            if (!attributes.isEmpty()) {
                this.broadcast(PacketType.OUT_ENTITY_UPDATE_ATTRIBUTES.newInstance(this.entity.getEntityId(), attributes), true);
            }
            attributes.clear();
        }
    }

    public void syncPassengers() {
        if (EntityTrackerEntryStateHandle.T.opt_passengers.isAvailable()) {
            boolean passengersDifferent;
            List<Entity> old_passengers = this.getSynchedPassengers();
            List<Entity> new_passengers = this.entity.getPassengers();
            boolean bl = passengersDifferent = old_passengers.size() != new_passengers.size();
            if (!passengersDifferent) {
                for (int i = 0; i < old_passengers.size(); ++i) {
                    if (old_passengers.get(i).getEntityId() == new_passengers.get(i).getEntityId()) continue;
                    passengersDifferent = true;
                    break;
                }
            }
            if (passengersDifferent) {
                ArrayList<Entity> old_passengers_bu = new ArrayList<Entity>(old_passengers);
                ArrayList<Object> newList = (ArrayList<Object>)LogicUtil.unsafeCast(((Template.Field)EntityTrackerEntryStateHandle.T.opt_passengers.raw).get(this.state.getRaw()));
                if (newList instanceof ArrayList) {
                    newList.clear();
                } else {
                    newList = new ArrayList<Object>(new_passengers.size());
                    ((Template.Field)EntityTrackerEntryStateHandle.T.opt_passengers.raw).set(this.state.getRaw(), newList);
                }
                for (Entity e : new_passengers) {
                    newList.add(HandleConversion.toEntityHandle(e));
                }
                this.onPassengersChanged(old_passengers_bu, new_passengers);
            }
        } else if (EntityTrackerEntryStateHandle.T.opt_vehicle.isAvailable()) {
            Entity new_entity;
            Entity new_vehicle;
            Entity old_vehicle = EntityTrackerEntryStateHandle.T.opt_vehicle.get(this.state.getRaw());
            if (old_vehicle != (new_vehicle = this.entity.getVehicle())) {
                EntityTrackerEntryStateHandle.T.opt_vehicle.set(this.state.getRaw(), new_vehicle);
                this.broadcast(PacketType.OUT_ENTITY_ATTACH.newInstanceMount((Entity)this.entity.getEntity(), new_vehicle));
            }
            if ((new_entity = this.entity.getPassenger()) != this.last_passenger_1_8_8) {
                ArrayList<Entity> old_list = new ArrayList<Entity>(1);
                ArrayList<Entity> new_list = new ArrayList<Entity>(1);
                if (this.last_passenger_1_8_8 != null) {
                    old_list.add(this.last_passenger_1_8_8);
                }
                if (new_entity != null) {
                    new_list.add(new_entity);
                }
                this.last_passenger_1_8_8 = new_entity;
                this.onPassengersChanged(old_list, new_list);
            }
        }
    }

    public void syncHeadRotation() {
        if (this.isHeadRotationChanged(0.011111111f)) {
            this.syncHeadRotation(this.headRotLive.get());
        }
    }

    public void syncHeadRotation(float headRotation) {
        this.headRotSynched.set(headRotation);
        this.broadcast(this.getHeadRotationPacket(headRotation));
    }

    public void syncVelocity() {
        if (!this.isMobile()) {
            return;
        }
        if (this.velLive.lengthSquared() == 0.0 && this.velSynched.lengthSquared() > 0.0 || this.isVelocityChanged(0.02)) {
            this.syncVelocity(this.velLive.getX(), this.velLive.getY(), this.velLive.getZ());
        }
    }

    public void syncVelocity(Vector velocity) {
        this.syncVelocity(velocity.getX(), velocity.getY(), velocity.getZ());
    }

    public void syncVelocity(double velX, double velY, double velZ) {
        this.velSynched.set(velX, velY, velZ);
        if (this.entity.isInsideVehicle()) {
            return;
        }
        this.broadcast(this.getVelocityPacket(velX, velY, velZ));
    }

    public void syncLocation() {
        this.syncLocation(this.isPositionChanged(0.03125), this.isRotationChanged(0.011111111f));
    }

    public void syncLocationAbsolute() {
        this.syncLocationAbsolute(this.locLive.getX(), this.locLive.getY(), this.locLive.getZ(), this.locLive.getYaw(), this.locLive.getPitch());
    }

    public void syncLocationAbsolute(double posX, double posY, double posZ, float yaw, float pitch) {
        this.locSynched.set(posX, posY, posZ, yaw, pitch);
        this.markLocationSyncedAbsolute();
        this.broadcast(this.getLocationPacket(posX, posY, posZ, yaw, pitch));
    }

    public void markLocationSyncedAbsolute() {
        this.state.setTimeSinceLocationSync(0);
    }

    public void syncLocation(boolean position, boolean rotation) {
        if (!position && !rotation) {
            return;
        }
        this.syncLocation(position, rotation, this.locLive.getX(), this.locLive.getY(), this.locLive.getZ(), this.locLive.getYaw(), this.locLive.getPitch());
    }

    public void syncLocation(boolean position, boolean rotation, double posX, double posY, double posZ, float yaw, float pitch) {
        if (position && !this.entity.isInsideVehicle()) {
            double deltaX = posX - this.locSynched.getX();
            double deltaY = posY - this.locSynched.getY();
            double deltaZ = posZ - this.locSynched.getZ();
            if (deltaX == 0.0 && deltaY == 0.0 && deltaZ == 0.0) {
                return;
            }
            if (Math.abs(deltaX) >= 8.0 || Math.abs(deltaY) >= 8.0 || Math.abs(deltaZ) >= 8.0) {
                if (!rotation) {
                    yaw = this.locSynched.getYaw();
                    pitch = this.locSynched.getPitch();
                }
                this.syncLocationAbsolute(posX, posY, posZ, yaw, pitch);
            } else if (rotation) {
                this.locSynched.set(posX, posY, posZ, yaw, pitch);
                this.broadcast(PacketType.OUT_ENTITY_MOVE_LOOK.newInstance(this.entity.getEntityId(), deltaX, deltaY, deltaZ, yaw, pitch, this.entity.isOnGround()));
            } else {
                this.locSynched.set(posX, posY, posZ);
                this.broadcast(PacketType.OUT_ENTITY_MOVE.newInstance(this.entity.getEntityId(), deltaX, deltaY, deltaZ, this.entity.isOnGround()));
            }
        } else if (rotation) {
            this.locSynched.setRotation(yaw, pitch);
            this.broadcast(PacketType.OUT_ENTITY_LOOK.newInstance(this.entity.getEntityId(), yaw, pitch, this.entity.isOnGround()));
        }
    }

    public void broadcast(CommonPacket packet) {
        this.broadcast(packet, false);
    }

    public void broadcast(CommonPacket packet, boolean self) {
        if (self && this.entity.getEntity() instanceof Player) {
            PacketUtil.sendPacket((Player)this.entity.getEntity(), packet);
        }
        for (Player viewer : this.getViewers()) {
            if (viewer == this.entity.getEntity()) continue;
            PacketUtil.sendPacket(viewer, packet);
        }
    }

    public CommonPacket getLocationPacket(double posX, double posY, double posZ, float yaw, float pitch) {
        return PacketType.OUT_ENTITY_TELEPORT.newInstance(this.entity.getEntityId(), posX, posY, posZ, yaw, pitch, true);
    }

    public CommonPacket getVelocityPacket(double velX, double velY, double velZ) {
        return PacketType.OUT_ENTITY_VELOCITY.newInstance(this.entity.getEntityId(), velX, velY, velZ);
    }

    public CommonPacket getSpawnPacket() {
        CommonPacket packet = this.state.getSpawnPacket();
        if (packet != null && packet.getType() == PacketType.OUT_ENTITY_SPAWN) {
            ClientboundAddEntityPacketHandle handle = ClientboundAddEntityPacketHandle.createHandle(packet.getHandle());
            handle.setMotX(this.velSynched.getX());
            handle.setMotY(this.velSynched.getY());
            handle.setMotZ(this.velSynched.getZ());
            handle.setPosX(this.locSynched.getX());
            handle.setPosY(this.locSynched.getY());
            handle.setPosZ(this.locSynched.getZ());
            handle.setYaw(this.locSynched.getYaw());
            handle.setPitch(this.locSynched.getPitch());
        }
        return packet;
    }

    public CommonPacket getHeadRotationPacket(float headRotation) {
        int prot = EntityTrackerEntryStateHandle.getProtocolRotation(headRotation);
        return PacketType.OUT_ENTITY_HEAD_ROTATION.newInstance((Entity)this.entity.getEntity(), (byte)prot);
    }
}

