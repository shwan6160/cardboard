/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.EntityEffect
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Vehicle
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.player.PlayerTeleportEvent$TeleportCause
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.bases.mutable.LocationAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.VectorAbstract;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.resources.ResourceCategory;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.generated.net.minecraft.util.RandomSourceHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.MobHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftSoundHandle;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Chunk;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class ExtendedEntity<T extends Entity> {
    public final LocationAbstract loc = new LocationAbstract(){

        @Override
        public World getWorld() {
            return ExtendedEntity.this.getWorld();
        }

        @Override
        public LocationAbstract setWorld(World world) {
            if (this.getWorld() != world) {
                ExtendedEntity.this.setWorld(world);
            }
            return this;
        }

        @Override
        public double getX() {
            return ExtendedEntity.this.handle.getLocX();
        }

        @Override
        public double getY() {
            return ExtendedEntity.this.handle.getLocY();
        }

        @Override
        public double getZ() {
            return ExtendedEntity.this.handle.getLocZ();
        }

        @Override
        public Vector vector() {
            return ExtendedEntity.this.handle.getLoc();
        }

        @Override
        public LocationAbstract setX(double x) {
            ExtendedEntity.this.handle.setLocX(x);
            return this;
        }

        @Override
        public LocationAbstract setY(double y) {
            ExtendedEntity.this.handle.setLocY(y);
            return this;
        }

        @Override
        public LocationAbstract setZ(double z) {
            ExtendedEntity.this.handle.setLocZ(z);
            return this;
        }

        @Override
        public LocationAbstract set(double x, double y, double z) {
            ExtendedEntity.this.handle.setLoc(x, y, z);
            return this;
        }

        @Override
        public float getYaw() {
            return ExtendedEntity.this.handle.getYaw();
        }

        @Override
        public float getPitch() {
            return ExtendedEntity.this.handle.getPitch();
        }

        @Override
        public LocationAbstract setYaw(float yaw) {
            ExtendedEntity.this.handle.setYaw(yaw);
            return this;
        }

        @Override
        public LocationAbstract setPitch(float pitch) {
            ExtendedEntity.this.handle.setPitch(pitch);
            return this;
        }
    };
    public final LocationAbstract last = new LocationAbstract(){

        @Override
        public World getWorld() {
            return ExtendedEntity.this.getWorld();
        }

        @Override
        public LocationAbstract setWorld(World world) {
            return this;
        }

        @Override
        public double getX() {
            return ExtendedEntity.this.handle.getLastX();
        }

        @Override
        public double getY() {
            return ExtendedEntity.this.handle.getLastY();
        }

        @Override
        public double getZ() {
            return ExtendedEntity.this.handle.getLastZ();
        }

        @Override
        public LocationAbstract setX(double x) {
            ExtendedEntity.this.handle.setLastX(x);
            return this;
        }

        @Override
        public LocationAbstract setY(double y) {
            ExtendedEntity.this.handle.setLastY(y);
            return this;
        }

        @Override
        public LocationAbstract setZ(double z) {
            ExtendedEntity.this.handle.setLastZ(z);
            return this;
        }

        @Override
        public float getYaw() {
            return ExtendedEntity.this.handle.getLastYaw();
        }

        @Override
        public float getPitch() {
            return ExtendedEntity.this.handle.getLastPitch();
        }

        @Override
        public LocationAbstract setYaw(float yaw) {
            ExtendedEntity.this.handle.setLastYaw(yaw);
            return this;
        }

        @Override
        public LocationAbstract setPitch(float pitch) {
            ExtendedEntity.this.handle.setLastPitch(pitch);
            return this;
        }
    };
    public final VectorAbstract vel = new VectorAbstract(){

        @Override
        public double getX() {
            return ExtendedEntity.this.handle.getMotX();
        }

        @Override
        public double getY() {
            return ExtendedEntity.this.handle.getMotY();
        }

        @Override
        public double getZ() {
            return ExtendedEntity.this.handle.getMotZ();
        }

        @Override
        public VectorAbstract fixNaN() {
            ExtendedEntity.this.handle.fixMotNaN();
            return this;
        }

        @Override
        public VectorAbstract setX(double x) {
            ExtendedEntity.this.handle.setMotX(x);
            return this;
        }

        @Override
        public VectorAbstract setY(double y) {
            ExtendedEntity.this.handle.setMotY(y);
            return this;
        }

        @Override
        public VectorAbstract setZ(double z) {
            ExtendedEntity.this.handle.setMotZ(z);
            return this;
        }

        @Override
        public VectorAbstract set(double x, double y, double z) {
            ExtendedEntity.this.handle.setMot(x, y, z);
            return this;
        }

        @Override
        public VectorAbstract set(Vector value) {
            ExtendedEntity.this.handle.setMotVector(value);
            return this;
        }

        @Override
        public Vector vector() {
            return ExtendedEntity.this.handle.getMot();
        }
    };
    public static final double MIN_MOVE_SPEED = 0.001;
    protected T entity;
    protected EntityHandle handle = (EntityHandle)EntityHandle.T.createHandle(null, true);

    public ExtendedEntity(T entity) {
        this.setEntity(entity);
    }

    protected void setEntity(T entity) {
        this.entity = entity;
        this.handle = EntityHandle.fromBukkit(entity);
    }

    public T getEntity() {
        return this.entity;
    }

    public Object getHandle() {
        return this.handle.getRaw();
    }

    public <H> H getHandle(Class<H> type) {
        return LogicUtil.tryCast(this.handle.getRaw(), type);
    }

    public EntityHandle getWrappedHandle() {
        return this.handle;
    }

    public Chunk getChunk() {
        return this.handle.getCurrentChunk();
    }

    public int getChunkX() {
        return this.handle.getChunkX();
    }

    public int getChunkY() {
        return this.handle.getChunkY();
    }

    public int getChunkZ() {
        return this.handle.getChunkZ();
    }

    public float getHeadRotation() {
        return this.handle.getHeadRotation();
    }

    public double getMovedX() {
        return this.handle.getLocX() - this.handle.getLastX();
    }

    public double getMovedY() {
        return this.handle.getLocY() - this.handle.getLastY();
    }

    public double getMovedZ() {
        return this.handle.getLocZ() - this.handle.getLastZ();
    }

    public boolean hasMovedHorizontally() {
        return Math.abs(this.getMovedX()) > 0.001 || Math.abs(this.getMovedZ()) > 0.001;
    }

    public boolean hasMovedVertically() {
        return Math.abs(this.getMovedY()) > 0.001;
    }

    public boolean hasMoved() {
        return this.hasMovedHorizontally() || this.hasMovedVertically();
    }

    public double getMovedXZDistance() {
        return MathUtil.length(this.getMovedX(), this.getMovedZ());
    }

    public double getMovedXZDistanceSquared() {
        return MathUtil.lengthSquared(this.getMovedX(), this.getMovedZ());
    }

    public double getMovedDistance() {
        return MathUtil.length(this.getMovedX(), this.getMovedY(), this.getMovedZ());
    }

    public double getMovedDistanceSquared() {
        return MathUtil.lengthSquared(this.getMovedX(), this.getMovedY(), this.getMovedZ());
    }

    public boolean isMovingHorizontally() {
        return this.vel.x.abs() > 0.001 || this.vel.z.abs() > 0.001;
    }

    public boolean isMovingVertically() {
        return this.vel.y.abs() > 0.001;
    }

    public boolean isMoving() {
        return this.isMovingHorizontally() || this.isMovingVertically();
    }

    public void setWorld(World world) {
        if (world == null) {
            throw new IllegalArgumentException("Can not set a null World for Entity");
        }
        if (world == this.getWorld()) {
            return;
        }
        this.handle.setWorld(LevelHandle.fromBukkit(world));
    }

    public void setDead(boolean dead) {
        this.handle.setDestroyed(dead);
    }

    public void setPreventBlockPlace(boolean prevent) {
        this.handle.setPreventBlockPlace(prevent);
    }

    public boolean isPreventBlockPlace() {
        return this.handle.isPreventBlockPlace();
    }

    @Deprecated
    public float getLength() {
        return this.handle.getHeight();
    }

    public float getHeight() {
        return this.handle.getHeight();
    }

    public float getWidth() {
        return this.handle.getWidth();
    }

    public boolean isOnGround() {
        return this.handle.isOnGround();
    }

    public void setOnGround(boolean onGround) {
        this.handle.setOnGround(onGround);
    }

    public boolean isHorizontalMovementBlocked() {
        return this.handle.isHorizontalMovementBlocked();
    }

    public boolean isVerticalMovementBlocked() {
        return this.handle.isVerticalMovementBlocked();
    }

    public boolean isMovementBlocked() {
        return this.handle.isHorizontalMovementBlocked() || this.handle.isVerticalMovementBlocked();
    }

    public boolean isPositionChanged() {
        return this.handle.isPositionChanged();
    }

    public void setPositionChanged(boolean changed) {
        this.handle.setPositionChanged(changed);
    }

    public boolean isVelocityChanged() {
        return this.handle.isVelocityChanged();
    }

    public void setVelocityChanged(boolean changed) {
        this.handle.setVelocityChanged(changed);
    }

    public boolean isInLoadedChunk() {
        return this.handle.isLoadedInWorld();
    }

    public RandomSourceHandle getRandom() {
        return this.handle.getRandom();
    }

    public void makeRandomSound(Sound sound, float volume, float pitch) {
        this.makeRandomSound(CraftSoundHandle.getSoundEffect(sound), volume, pitch);
    }

    public void makeRandomSound(String soundName, float volume, float pitch) {
        this.makeRandomSound(ResourceCategory.sound_effect.createKey(soundName), volume, pitch);
    }

    public void makeRandomSound(ResourceKey<SoundEffect> sound, float volume, float pitch) {
        RandomSourceHandle rand = this.getRandom();
        this.makeSound(sound, volume, MathUtil.clamp(pitch + 0.4f * (rand.nextFloat() - rand.nextFloat()), 0.0f, 1.0f));
    }

    public void makeSound(Sound sound, float volume, float pitch) {
        this.makeSound(CraftSoundHandle.getSoundEffect(sound), volume, pitch);
    }

    public void makeSound(String soundName, float volume, float pitch) {
        this.makeSound(ResourceCategory.sound_effect.createKey(soundName), volume, pitch);
    }

    public void makeSound(ResourceKey<SoundEffect> sound, float volume, float pitch) {
        if (sound != null) {
            this.handle.makeSound(sound, volume, pitch);
        }
    }

    public void makeStepSound(Block block) {
        this.makeStepSound(block.getX(), block.getY(), block.getZ(), block.getType());
    }

    public void makeStepSound(int blockX, int blockY, int blockZ, Material type) {
        this.handle.playStepSound(new IntVector3(blockX, blockY, blockZ), BlockData.fromMaterial(type));
    }

    public List<MetadataValue> getMetadata(String arg0) {
        return this.entity.getMetadata(arg0);
    }

    public boolean hasMetadata(String arg0) {
        return this.entity.hasMetadata(arg0);
    }

    public void removeMetadata(String arg0, Plugin arg1) {
        this.entity.removeMetadata(arg0, arg1);
    }

    public void setMetadata(String arg0, MetadataValue arg1) {
        this.entity.setMetadata(arg0, arg1);
    }

    public boolean eject() {
        List<Entity> passengers = this.getPassengers();
        if (passengers.isEmpty()) {
            return false;
        }
        for (Entity passenger : passengers) {
            this.removePassenger(passenger);
        }
        return true;
    }

    public int getEntityId() {
        return this.entity.getEntityId();
    }

    public float getFallDistance() {
        return this.entity.getFallDistance();
    }

    public int getFireTicks() {
        return this.entity.getFireTicks();
    }

    public EntityDamageEvent getLastDamageCause() {
        return this.entity.getLastDamageCause();
    }

    public Location getLocation() {
        return this.entity.getLocation();
    }

    public Location getLocation(Location arg0) {
        return this.entity.getLocation(arg0);
    }

    public Location getLastLocation() {
        EntityHandle h = this.handle;
        return new Location(this.getWorld(), h.getLastX(), h.getLastY(), h.getLastZ(), h.getLastYaw(), h.getLastPitch());
    }

    public Location getLastLocation(Location loc) {
        if (loc != null) {
            EntityHandle h = this.handle;
            loc.setWorld(this.getWorld());
            loc.setX(h.getLastX());
            loc.setY(h.getLastY());
            loc.setZ(h.getLastZ());
            loc.setYaw(h.getLastYaw());
            loc.setPitch(h.getLastPitch());
        }
        return loc;
    }

    public int getMaxFireTicks() {
        return this.entity.getMaxFireTicks();
    }

    public void setFootLocation(double x, double y, double z, float yaw, float pitch) {
        this.handle.setPositionRotation(x, y, z, yaw, pitch);
    }

    public void setLocation(double x, double y, double z, float yaw, float pitch) {
        this.handle.setLocation(x, y, z, yaw, pitch);
    }

    public void setRotation(float yaw, float pitch) {
        this.handle.setRotation(yaw, pitch);
    }

    public void setPosition(double x, double y, double z) {
        this.handle.setPosition(x, y, z);
    }

    public void setSize(float width, float length) {
        this.handle.setSize(width, length);
    }

    public List<Entity> getNearbyEntities(double radius) {
        return this.getNearbyEntities(radius, radius, radius);
    }

    public List<Entity> getNearbyEntities(double radX, double radY, double radZ) {
        return WorldUtil.getNearbyEntities(this.getEntity(), radX, radY, radZ);
    }

    @Deprecated
    public Entity getPassenger() {
        return this.entity.getPassenger();
    }

    @Deprecated
    public Player getPlayerPassenger() {
        return LogicUtil.tryCast(this.getPassenger(), Player.class);
    }

    public List<Entity> getPassengers() {
        return com.bergerkiller.generated.org.bukkit.entity.EntityHandle.T.getPassengers.invoke(this.entity);
    }

    public List<Player> getPlayerPassengers() {
        List<Entity> passengers = this.getPassengers();
        ArrayList<Player> playerPassengers = new ArrayList<Player>(passengers.size());
        for (Entity entity : passengers) {
            if (!(entity instanceof Player)) continue;
            playerPassengers.add((Player)entity);
        }
        return playerPassengers;
    }

    public boolean hasPassenger() {
        return this.handle.hasPassengers();
    }

    public boolean hasPlayerPassenger() {
        for (EntityHandle handle : this.handle.getPassengers()) {
            if (!ServerPlayerHandle.T.isAssignableFrom(handle.getRaw())) continue;
            return true;
        }
        return false;
    }

    public boolean isVehicle() {
        return this.entity instanceof Vehicle;
    }

    public Server getServer() {
        return this.entity.getServer();
    }

    public int getTicksLived() {
        return this.entity.getTicksLived();
    }

    public EntityType getType() {
        return this.entity.getType();
    }

    public UUID getUniqueId() {
        return this.entity.getUniqueId();
    }

    public Entity getVehicle() {
        return this.entity.getVehicle();
    }

    public Vector getVelocity() {
        return this.entity.getVelocity();
    }

    public boolean isSpawned() {
        LevelHandle world;
        if (this.handle != null && (world = this.handle.getWorld()) != null) {
            return world.getEntityById(this.handle.getIdField()) == this.getEntity();
        }
        return false;
    }

    public World getWorld() {
        try {
            return this.entity.getWorld();
        }
        catch (Throwable t) {
            LevelHandle wHandle = this.handle.getWorld();
            return wHandle != null ? wHandle.getWorld() : null;
        }
    }

    @Deprecated
    public boolean isDead() {
        return this.entity.isDead();
    }

    public boolean isRemoved() {
        return this.entity.isDead();
    }

    public boolean isDestroyed() {
        return this.handle.isDestroyed();
    }

    public boolean isEmpty() {
        return this.entity.isEmpty();
    }

    public boolean isInsideVehicle() {
        return this.entity.isInsideVehicle();
    }

    public Entity getLeashHolder() {
        MobHandle insHandle;
        EntityHandle holder;
        if (this.handle.isInstanceOf(MobHandle.T) && (holder = (insHandle = MobHandle.createHandle(this.handle.getRaw())).getLeashHolder()) != null) {
            return holder.getBukkitEntity();
        }
        return null;
    }

    public boolean isValid() {
        return this.entity.isValid();
    }

    public boolean isInWater() {
        return this.isInWater(false);
    }

    public boolean isInWater(boolean update) {
        if (update) {
            return this.handle.isInWaterUpdate();
        }
        return this.handle.isInWater();
    }

    public void suppressPortalTeleportationThisTick() {
        this.handle.suppressPortalThisTick();
    }

    public boolean isInsidePortalThisTick() {
        return this.handle.isInsidePortalThisTick();
    }

    @Deprecated
    public void setAllowTeleportation(boolean allow) {
        if (!allow) {
            this.suppressPortalTeleportationThisTick();
        }
    }

    @Deprecated
    public boolean getAllowTeleportation() {
        return this.isInsidePortalThisTick();
    }

    public int getPortalCooldown() {
        return EntityUtil.getPortalCooldown(this.entity);
    }

    public void setPortalCooldown(int cooldownTicks) {
        EntityUtil.setPortalCooldown(this.entity, cooldownTicks);
    }

    public int getPortalCooldownMaximum() {
        return EntityUtil.getPortalCooldownMaximum(this.entity);
    }

    public void setPortalTime(int timeTicks) {
        this.handle.setPortalTime(timeTicks);
    }

    public int getPortalTime() {
        return this.handle.getPortalTime();
    }

    public int getPortalWaitTime() {
        return this.handle.getPortalWaitTime();
    }

    public boolean leaveVehicle() {
        Entity vehicle = this.getVehicle();
        return vehicle != null && com.bergerkiller.generated.org.bukkit.entity.EntityHandle.T.removePassenger.invoke(vehicle, this.entity) != false;
    }

    public void playEffect(EntityEffect arg0) {
        this.entity.playEffect(arg0);
    }

    public void remove() {
        this.entity.remove();
    }

    public void setFallDistance(float arg0) {
        this.entity.setFallDistance(arg0);
    }

    public void setFireTicks(int arg0) {
        this.entity.setFireTicks(arg0);
    }

    public boolean setPassenger(Entity passenger) {
        boolean alreadyAdded = false;
        for (Entity entity : this.getPassengers()) {
            if (entity == passenger) {
                alreadyAdded = true;
                continue;
            }
            this.removePassenger(entity);
        }
        return !alreadyAdded && this.addPassenger(passenger);
    }

    public boolean isPassenger(Entity passenger) {
        if (passenger != null) {
            for (Entity currPassenger : this.getPassengers()) {
                if (!currPassenger.getUniqueId().equals(passenger.getUniqueId())) continue;
                return true;
            }
        }
        return false;
    }

    public boolean addPassenger(Entity passenger) {
        return com.bergerkiller.generated.org.bukkit.entity.EntityHandle.T.addPassenger.invoke(this.entity, passenger);
    }

    public boolean removePassenger(Entity passenger) {
        return com.bergerkiller.generated.org.bukkit.entity.EntityHandle.T.removePassenger.invoke(this.entity, passenger);
    }

    public void setPassengersSilent(List<Entity> newPassengers) {
        EntityTrackerEntryHandle entry;
        EntityHandle handle = this.handle;
        ArrayList<EntityHandle> newPassengerHandles = new ArrayList<EntityHandle>(this.handle.getPassengers());
        ArrayList<EntityHandle> removedPassengers = new ArrayList<EntityHandle>(newPassengerHandles.size());
        ArrayList<Entity> keptPassengers = new ArrayList<Entity>(newPassengers.size());
        for (EntityHandle oldPassenger : newPassengerHandles) {
            boolean found = false;
            for (Entity p : newPassengers) {
                if (oldPassenger.getRaw() != HandleConversion.toEntityHandle(p)) continue;
                found = true;
                keptPassengers.add(p);
                break;
            }
            if (found) continue;
            removedPassengers.add(oldPassenger);
        }
        for (EntityHandle passenger : removedPassengers) {
            passenger.setVehicle(null);
            newPassengerHandles.remove(passenger);
        }
        for (Entity p : newPassengers) {
            EntityHandle passengerHandle = EntityHandle.fromBukkit(p);
            if (newPassengerHandles.contains(passengerHandle)) continue;
            newPassengerHandles.add(passengerHandle);
            passengerHandle.setVehicle(handle);
        }
        this.handle.setPassengers(newPassengerHandles);
        if (EntityTrackerEntryStateHandle.T.opt_passengers.isAvailable()) {
            CommonPacket packet = PacketType.OUT_MOUNT.newInstanceHandles((Entity)this.entity, (List<EntityHandle>)newPassengerHandles);
            PacketUtil.broadcastEntityPacket(this.entity, packet);
            entry = WorldUtil.getTracker(this.entity.getWorld()).getEntry((Entity)this.entity);
            if (entry != null) {
                EntityTrackerEntryStateHandle.T.opt_passengers.set(entry.getState().getRaw(), newPassengers);
            }
        } else if (EntityTrackerEntryStateHandle.T.opt_vehicle.isAvailable()) {
            for (EntityHandle passengerHandle : removedPassengers) {
                Entity passenger = passengerHandle.getBukkitEntity();
                PacketUtil.broadcastEntityPacket(passenger, PacketType.OUT_ENTITY_ATTACH.newInstanceMount(passenger, null));
                EntityTrackerEntryHandle entry2 = WorldUtil.getTracker(passenger.getWorld()).getEntry(passenger);
                if (entry2 == null) continue;
                EntityTrackerEntryStateHandle.T.opt_vehicle.set(entry2.getState().getRaw(), null);
            }
            if (!newPassengers.isEmpty()) {
                Entity passenger = newPassengers.get(0);
                PacketUtil.broadcastEntityPacket(passenger, PacketType.OUT_ENTITY_ATTACH.newInstanceMount(passenger, (Entity)this.entity));
                entry = WorldUtil.getTracker(passenger.getWorld()).getEntry(passenger);
                if (entry != null) {
                    EntityTrackerEntryStateHandle.T.opt_vehicle.set(entry.getState().getRaw(), (Entity)this.entity);
                }
            }
        }
    }

    public void sendPacketNearby(CommonPacket packet) {
        WorldUtil.getTracker(this.getWorld()).sendPacket((Entity)this.entity, packet);
    }

    public void setTicksLived(int arg0) {
        this.entity.setTicksLived(arg0);
    }

    public void setVelocity(Vector velocity) {
        if (!(Math.abs(velocity.getX()) <= Double.MAX_VALUE && Math.abs(velocity.getY()) <= Double.MAX_VALUE && Math.abs(velocity.getZ()) <= Double.MAX_VALUE)) {
            throw new IllegalArgumentException("Velocity is not finite: " + velocity);
        }
        this.handle.setMot(velocity.getX(), velocity.getY(), velocity.getZ());
    }

    public void setVelocity(double motX, double motY, double motZ) {
        this.handle.setMot(motX, motY, motZ);
    }

    public boolean teleport(Location location) {
        return this.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        return this.entity.teleport(location, cause);
    }

    public boolean teleport(Entity destination) {
        return this.teleport(destination.getLocation());
    }

    public boolean teleport(Entity destination, PlayerTeleportEvent.TeleportCause cause) {
        return this.teleport(destination.getLocation(), cause);
    }

    public Item spawnItemDrop(Material material, int amount, float force) {
        return this.handle.dropItem(material, amount, force);
    }

    public Item spawnItemDrop(ItemStack item, float force) {
        return this.handle.dropItemStack(item, force);
    }

    public DataWatcher getMetaData() {
        return this.handle.getDataWatcher();
    }

    public DataWatcher getDataWatcher() {
        return this.handle.getDataWatcher();
    }

    public <V> DataWatcher.EntityItem<V> getDataItem(DataWatcher.Key<V> key) {
        return new DataWatcher.EntityItem<V>(this, key);
    }

    public <V> V read(FieldAccessor<V> field) {
        return field.get(this.getHandle());
    }

    public <V> void write(FieldAccessor<V> field, V value) {
        field.set(this.getHandle(), value);
    }

    public int hashCode() {
        return this.entity == null ? super.hashCode() : this.entity.hashCode();
    }

    public String toString() {
        return this.entity == null ? "null" : this.entity.toString();
    }

    public boolean equals(Object object) {
        if (object instanceof ExtendedEntity) {
            return ((ExtendedEntity)object).entity == this.entity;
        }
        return false;
    }
}

