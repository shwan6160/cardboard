/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Item
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.EntityPose;
import com.bergerkiller.bukkit.common.wrappers.InteractionResult;
import com.bergerkiller.generated.net.minecraft.CrashReportCategoryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.util.RandomSourceHandle;
import com.bergerkiller.generated.net.minecraft.world.damagesource.DamageSourceHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.storage.ValueOutputHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import java.util.UUID;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@Template.InstanceType(value="net.minecraft.world.entity.Entity")
public abstract class EntityHandle
extends Template.Handle {
    public static final EntityClass T = Template.Class.create(EntityClass.class, Common.TEMPLATE_RESOLVER);
    public static final DataWatcher.Key<Byte> DATA_FLAGS = DataWatcher.Key.Type.BYTE.createKey(EntityHandle.T.DATA_FLAGS, 0);
    public static final DataWatcher.Key<Integer> DATA_AIR_TICKS = DataWatcher.Key.Type.INTEGER.createKey(EntityHandle.T.DATA_AIR_TICKS, 1);
    public static final DataWatcher.Key<ChatText> DATA_CUSTOM_NAME = Common.evaluateMCVersion(">=", "1.13") ? DataWatcher.Key.Type.CHAT_TEXT.createKey(EntityHandle.T.DATA_CUSTOM_NAME, 2) : DataWatcher.Key.Type.STRING.translate(ChatText.class).createKey(EntityHandle.T.DATA_CUSTOM_NAME, 2);
    public static final DataWatcher.Key<Boolean> DATA_CUSTOM_NAME_VISIBLE = DataWatcher.Key.Type.BOOLEAN.createKey(EntityHandle.T.DATA_CUSTOM_NAME_VISIBLE, 3);
    public static final DataWatcher.Key<Boolean> DATA_SILENT = DataWatcher.Key.Type.BOOLEAN.createKey(EntityHandle.T.DATA_SILENT, 4);
    public static final DataWatcher.Key<Boolean> DATA_NO_GRAVITY = DataWatcher.Key.Type.BOOLEAN.createKey(EntityHandle.T.DATA_NO_GRAVITY, -1);
    public static final DataWatcher.Key<EntityPose> DATA_POSE = DataWatcher.Key.Type.ENTITY_POSE.createKey(EntityHandle.T.DATA_POSE, -1);
    public static final int DATA_FLAG_ON_FIRE = 1;
    public static final int DATA_FLAG_SNEAKING = 2;
    public static final int DATA_FLAG_UNKNOWN1 = 4;
    public static final int DATA_FLAG_SPRINTING = 8;
    public static final int DATA_FLAG_UNKNOWN2 = 16;
    public static final int DATA_FLAG_INVISIBLE = 32;
    public static final int DATA_FLAG_GLOWING = 64;
    public static final int DATA_FLAG_FLYING = 128;

    public static EntityHandle createHandle(Object handleInstance) {
        return (EntityHandle)T.createHandle(handleInstance);
    }

    public static int incrementEntityCounter() {
        return (Integer)EntityHandle.T.incrementEntityCounter.invoker.invoke(null);
    }

    public abstract void setTrackerEntry(EntityTrackerEntryHandle var1);

    public abstract List<EntityHandle> getPassengers();

    public abstract boolean isVehicle();

    public abstract boolean isPassenger();

    public abstract boolean hasPassengers();

    public abstract void setPassengers(List<EntityHandle> var1);

    public abstract boolean isInSameVehicle(EntityHandle var1);

    public abstract boolean isIgnoreChunkCheck();

    public abstract void setIgnoreChunkCheck(boolean var1);

    public abstract double getLocX();

    public abstract double getLocY();

    public abstract double getLocZ();

    public abstract void setLoc(double var1, double var3, double var5);

    public abstract void setLocX(double var1);

    public abstract void setLocY(double var1);

    public abstract void setLocZ(double var1);

    public abstract Vector getLoc();

    public abstract void assignEntityReference();

    public abstract boolean isLastAndCurrentPositionDifferent();

    public abstract Vector getMot();

    public abstract void setMotVector(Vector var1);

    public abstract void setMot(double var1, double var3, double var5);

    public abstract void fixMotNaN();

    public abstract void setMotX(double var1);

    public abstract void setMotY(double var1);

    public abstract void setMotZ(double var1);

    public abstract double getMotX();

    public abstract double getMotY();

    public abstract double getMotZ();

    public abstract void setHorizontalMovementBlocked(boolean var1);

    public abstract void setVerticalMovementBlocked(boolean var1);

    public abstract boolean isCollidingWithBlock();

    public abstract Vector getBlockCollisionMultiplier();

    public abstract void setNotCollidingWithBlock();

    public abstract void setRemovedPassive();

    public abstract void setDestroyed(boolean var1);

    public abstract boolean isDestroyed();

    public abstract boolean isSavingAllowed();

    public abstract boolean isLoadedInWorld();

    public abstract int getChunkX();

    public abstract int getChunkY();

    public abstract int getChunkZ();

    public abstract Chunk getCurrentChunk();

    public abstract float getWidth();

    public abstract float getHeight();

    public abstract void setStepCounter(float var1);

    public abstract float getStepCounter();

    public abstract float getHeightOffset();

    public abstract void playStepSound(IntVector3 var1, BlockData var2);

    public abstract void setRotation(float var1, float var2);

    public abstract void burn(float var1);

    public abstract Item dropItem(Material var1, int var2, float var3);

    public abstract Item dropItemStack(ItemStack var1, float var2);

    public abstract ResourceKey<SoundEffect> getSwimSound();

    public abstract void handleMovementEmissionAndPlaySound(Vector var1, IntVector3 var2, BlockData var3);

    public abstract void makeSound(ResourceKey<SoundEffect> var1, float var2, float var3);

    public abstract boolean isWet();

    public abstract boolean isInWaterUpdate();

    public abstract boolean isInWater();

    public abstract boolean hasMovementSound();

    public abstract void updateFalling(double var1, boolean var3, BlockData var4, IntVector3 var5);

    public abstract boolean isOutsideWorldBorder();

    public abstract void setOutsideWorldBorder(boolean var1);

    public abstract void restituteMovementAfterCollisions(BlockData var1, boolean var2, boolean var3, Vector var4);

    public abstract void applyEffectsFromBlocks();

    public abstract double calculateDistanceSquared(double var1, double var3, double var5);

    public abstract boolean damageEntity(DamageSourceHandle var1, float var2);

    public abstract String getStringUUID();

    public abstract void setPosition(double var1, double var3, double var5);

    public abstract void setSize(float var1, float var2);

    public abstract AABBHandle getBoundingBox();

    public abstract void setPositionRotation(double var1, double var3, double var5, float var7, float var8);

    public abstract void setLocation(double var1, double var3, double var5, float var7, float var8);

    public abstract void setBoundingBox(AABBHandle var1);

    public abstract float getHeadRotation();

    public abstract void setHeadRotation(float var1);

    public abstract boolean canCollideWith(EntityHandle var1);

    public abstract AABBHandle getEntityBoundingBox(EntityHandle var1);

    public abstract void setPositionFromBoundingBox();

    public abstract void handleFireBlockTick();

    public abstract boolean isBurning();

    public abstract void setOnFire(float var1);

    public abstract EntityHandle getDriverEntity();

    public abstract void onTick();

    public abstract void loadFromNBT(CommonTagCompound var1);

    public abstract void saveWithoutId(ValueOutputHandle var1, boolean var2, boolean var3, boolean var4);

    public abstract void saveToNBT(CommonTagCompound var1);

    public abstract int getId();

    public abstract UUID getUniqueID();

    public abstract DataWatcher getDataWatcher();

    public abstract boolean isSneaking();

    public abstract void appendEntityCrashDetails(CrashReportCategoryHandle var1);

    public abstract void onPush(double var1, double var3, double var5);

    public abstract void positionRider(Entity var1);

    public abstract int getPortalCooldownMaximum();

    public abstract boolean isInsidePortalThisTick();

    public abstract void suppressPortalThisTick();

    public abstract int getPortalTime();

    public abstract boolean setPortalTime(int var1);

    public abstract int getPortalWaitTime();

    public abstract boolean isAlwaysTicked();

    public abstract boolean hasCustomName();

    public abstract ChatText getCustomName();

    public abstract void collide(EntityHandle var1);

    public abstract World getBukkitWorld();

    public abstract LevelHandle getWorld();

    public abstract void setWorld(LevelHandle var1);

    public abstract Entity getBukkitEntity();

    public ServerLevelHandle getWorldServer() {
        return ServerLevelHandle.createHandle(((Template.Method)EntityHandle.T.getWorld.raw).invoke(this.getRaw()));
    }

    @Deprecated
    public boolean isDead() {
        return this.isDestroyed();
    }

    public int getMaxFireTicks() {
        if (EntityHandle.T.prop_getMaxFireTicks.isAvailable()) {
            return EntityHandle.T.prop_getMaxFireTicks.invoke(this.getRaw());
        }
        if (EntityHandle.T.field_maxFireTicks.isAvailable()) {
            return EntityHandle.T.field_maxFireTicks.getInteger(this.getRaw());
        }
        throw new UnsupportedOperationException("Max Fire Ticks can not be read");
    }

    public static EntityHandle fromBukkit(Entity entity) {
        return EntityHandle.createHandle(HandleConversion.toEntityHandle(entity));
    }

    public abstract Entity getBukkitEntityField();

    public abstract void setBukkitEntityField(Entity var1);

    public abstract int getIdField();

    public abstract void setIdField(int var1);

    public abstract boolean isPreventBlockPlace();

    public abstract void setPreventBlockPlace(boolean var1);

    public abstract EntityHandle getVehicle();

    public abstract void setVehicle(EntityHandle var1);

    public abstract double getLastX();

    public abstract void setLastX(double var1);

    public abstract double getLastY();

    public abstract void setLastY(double var1);

    public abstract double getLastZ();

    public abstract void setLastZ(double var1);

    public abstract float getYaw();

    public abstract void setYaw(float var1);

    public abstract float getPitch();

    public abstract void setPitch(float var1);

    public abstract float getLastYaw();

    public abstract void setLastYaw(float var1);

    public abstract float getLastPitch();

    public abstract void setLastPitch(float var1);

    public abstract AABBHandle getBoundingBoxField();

    public abstract void setBoundingBoxField(AABBHandle var1);

    public abstract boolean isOnGround();

    public abstract void setOnGround(boolean var1);

    @Template.Readonly
    public abstract boolean isHorizontalMovementBlocked();

    @Template.Readonly
    public abstract boolean isVerticalMovementBlocked();

    public abstract boolean isVelocityChanged();

    public abstract void setVelocityChanged(boolean var1);

    public abstract float getFallDistance();

    public abstract void setFallDistance(float var1);

    public abstract boolean isNoclip();

    public abstract void setNoclip(boolean var1);

    public abstract RandomSourceHandle getRandom();

    public abstract void setRandom(RandomSourceHandle var1);

    public abstract int getFireTicks();

    public abstract void setFireTicks(int var1);

    public abstract int getTicksLived();

    public abstract void setTicksLived(int var1);

    public abstract DataWatcher getDatawatcherField();

    public abstract void setDatawatcherField(DataWatcher var1);

    public abstract boolean isPositionChanged();

    public abstract void setPositionChanged(boolean var1);

    public abstract int getPortalCooldown();

    public abstract void setPortalCooldown(int var1);

    public abstract boolean isValid();

    public abstract void setValid(boolean var1);

    public static final class EntityClass
    extends Template.Class<EntityHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Byte>> DATA_FLAGS = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Integer>> DATA_AIR_TICKS = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<?>> DATA_CUSTOM_NAME = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Boolean>> DATA_CUSTOM_NAME_VISIBLE = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Boolean>> DATA_SILENT = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Boolean>> DATA_NO_GRAVITY = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<?>> DATA_POSE = new Template.StaticField.Converted();
        public final Template.Field.Converted<Entity> bukkitEntityField = new Template.Field.Converted();
        public final Template.Field.Integer idField = new Template.Field.Integer();
        public final Template.Field.Boolean preventBlockPlace = new Template.Field.Boolean();
        public final Template.Field.Converted<EntityHandle> vehicle = new Template.Field.Converted();
        public final Template.Field.Double lastX = new Template.Field.Double();
        public final Template.Field.Double lastY = new Template.Field.Double();
        public final Template.Field.Double lastZ = new Template.Field.Double();
        public final Template.Field.Float yaw = new Template.Field.Float();
        public final Template.Field.Float pitch = new Template.Field.Float();
        public final Template.Field.Float lastYaw = new Template.Field.Float();
        public final Template.Field.Float lastPitch = new Template.Field.Float();
        public final Template.Field.Converted<AABBHandle> boundingBoxField = new Template.Field.Converted();
        public final Template.Field.Boolean onGround = new Template.Field.Boolean();
        @Template.Readonly
        public final Template.Field.Boolean horizontalMovementBlocked = new Template.Field.Boolean();
        @Template.Readonly
        public final Template.Field.Boolean verticalMovementBlocked = new Template.Field.Boolean();
        public final Template.Field.Boolean velocityChanged = new Template.Field.Boolean();
        public final Template.Field.Float fallDistance = new Template.Field.Float();
        public final Template.Field.Boolean noclip = new Template.Field.Boolean();
        public final Template.Field.Converted<RandomSourceHandle> random = new Template.Field.Converted();
        public final Template.Field.Integer fireTicks = new Template.Field.Integer();
        public final Template.Field.Integer ticksLived = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer field_maxFireTicks = new Template.Field.Integer();
        public final Template.Field.Converted<DataWatcher> datawatcherField = new Template.Field.Converted();
        public final Template.Field.Boolean positionChanged = new Template.Field.Boolean();
        public final Template.Field.Integer portalCooldown = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field<double[]> move_SomeArray = new Template.Field();
        @Template.Optional
        public final Template.Field.Long move_SomeState = new Template.Field.Long();
        public final Template.Field.Boolean valid = new Template.Field.Boolean();
        public final Template.StaticMethod<Integer> incrementEntityCounter = new Template.StaticMethod();
        public final Template.Method.Converted<Void> setTrackerEntry = new Template.Method.Converted();
        public final Template.Method.Converted<List<EntityHandle>> getPassengers = new Template.Method.Converted();
        public final Template.Method<Boolean> isVehicle = new Template.Method();
        public final Template.Method<Boolean> isPassenger = new Template.Method();
        public final Template.Method<Boolean> hasPassengers = new Template.Method();
        public final Template.Method.Converted<Void> setPassengers = new Template.Method.Converted();
        public final Template.Method.Converted<Boolean> isInSameVehicle = new Template.Method.Converted();
        public final Template.Method<Boolean> isIgnoreChunkCheck = new Template.Method();
        public final Template.Method<Void> setIgnoreChunkCheck = new Template.Method();
        public final Template.Method<Double> getLocX = new Template.Method();
        public final Template.Method<Double> getLocY = new Template.Method();
        public final Template.Method<Double> getLocZ = new Template.Method();
        public final Template.Method<Void> setLoc = new Template.Method();
        public final Template.Method<Void> setLocX = new Template.Method();
        public final Template.Method<Void> setLocY = new Template.Method();
        public final Template.Method<Void> setLocZ = new Template.Method();
        public final Template.Method<Vector> getLoc = new Template.Method();
        public final Template.Method<Void> assignEntityReference = new Template.Method();
        public final Template.Method<Boolean> isLastAndCurrentPositionDifferent = new Template.Method();
        public final Template.Method<Vector> getMot = new Template.Method();
        public final Template.Method<Void> setMotVector = new Template.Method();
        public final Template.Method<Void> setMot = new Template.Method();
        public final Template.Method<Void> fixMotNaN = new Template.Method();
        public final Template.Method<Void> setMotX = new Template.Method();
        public final Template.Method<Void> setMotY = new Template.Method();
        public final Template.Method<Void> setMotZ = new Template.Method();
        public final Template.Method<Double> getMotX = new Template.Method();
        public final Template.Method<Double> getMotY = new Template.Method();
        public final Template.Method<Double> getMotZ = new Template.Method();
        public final Template.Method<Void> setHorizontalMovementBlocked = new Template.Method();
        public final Template.Method<Void> setVerticalMovementBlocked = new Template.Method();
        public final Template.Method<Boolean> isCollidingWithBlock = new Template.Method();
        public final Template.Method<Vector> getBlockCollisionMultiplier = new Template.Method();
        public final Template.Method<Void> setNotCollidingWithBlock = new Template.Method();
        public final Template.Method<Void> setRemovedPassive = new Template.Method();
        public final Template.Method<Void> setDestroyed = new Template.Method();
        public final Template.Method<Boolean> isDestroyed = new Template.Method();
        public final Template.Method<Boolean> isSavingAllowed = new Template.Method();
        public final Template.Method<Boolean> isLoadedInWorld = new Template.Method();
        public final Template.Method<Integer> getChunkX = new Template.Method();
        public final Template.Method<Integer> getChunkY = new Template.Method();
        public final Template.Method<Integer> getChunkZ = new Template.Method();
        @Template.Optional
        public final Template.Method<Void> setLoadedInWorld_pre_1_17 = new Template.Method();
        public final Template.Method.Converted<Chunk> getCurrentChunk = new Template.Method.Converted();
        public final Template.Method<Float> getWidth = new Template.Method();
        public final Template.Method<Float> getHeight = new Template.Method();
        public final Template.Method<Void> setStepCounter = new Template.Method();
        public final Template.Method<Float> getStepCounter = new Template.Method();
        public final Template.Method<Float> getHeightOffset = new Template.Method();
        public final Template.Method.Converted<Void> playStepSound = new Template.Method.Converted();
        public final Template.Method<Void> setRotation = new Template.Method();
        public final Template.Method<Void> burn = new Template.Method();
        public final Template.Method.Converted<Item> dropItem = new Template.Method.Converted();
        public final Template.Method.Converted<Item> dropItemStack = new Template.Method.Converted();
        public final Template.Method.Converted<ResourceKey<SoundEffect>> getSwimSound = new Template.Method.Converted();
        public final Template.Method.Converted<Void> handleMovementEmissionAndPlaySound = new Template.Method.Converted();
        public final Template.Method.Converted<Void> makeSound = new Template.Method.Converted();
        public final Template.Method<Boolean> isWet = new Template.Method();
        public final Template.Method<Boolean> isInWaterUpdate = new Template.Method();
        public final Template.Method<Boolean> isInWater = new Template.Method();
        public final Template.Method<Boolean> hasMovementSound = new Template.Method();
        public final Template.Method.Converted<Void> updateFalling = new Template.Method.Converted();
        @Template.Optional
        public final Template.Method<Void> setLegacyTrackingEntity = new Template.Method();
        public final Template.Method<Boolean> isOutsideWorldBorder = new Template.Method();
        public final Template.Method<Void> setOutsideWorldBorder = new Template.Method();
        public final Template.Method.Converted<Void> restituteMovementAfterCollisions = new Template.Method.Converted();
        public final Template.Method<Void> applyEffectsFromBlocks = new Template.Method();
        public final Template.Method<Double> calculateDistanceSquared = new Template.Method();
        public final Template.Method.Converted<Boolean> damageEntity = new Template.Method.Converted();
        public final Template.Method<String> getStringUUID = new Template.Method();
        public final Template.Method<Void> setPosition = new Template.Method();
        public final Template.Method<Void> setSize = new Template.Method();
        public final Template.Method.Converted<AABBHandle> getBoundingBox = new Template.Method.Converted();
        public final Template.Method<Void> setPositionRotation = new Template.Method();
        public final Template.Method<Void> setLocation = new Template.Method();
        public final Template.Method.Converted<Void> setBoundingBox = new Template.Method.Converted();
        public final Template.Method<Float> getHeadRotation = new Template.Method();
        public final Template.Method<Void> setHeadRotation = new Template.Method();
        public final Template.Method.Converted<Boolean> canCollideWith = new Template.Method.Converted();
        public final Template.Method.Converted<AABBHandle> getEntityBoundingBox = new Template.Method.Converted();
        public final Template.Method<Void> setPositionFromBoundingBox = new Template.Method();
        public final Template.Method<Void> handleFireBlockTick = new Template.Method();
        public final Template.Method<Boolean> isBurning = new Template.Method();
        @Template.Optional
        public final Template.Method<Integer> prop_getMaxFireTicks = new Template.Method();
        public final Template.Method.Converted<Void> setOnFire = new Template.Method.Converted();
        public final Template.Method.Converted<EntityHandle> getDriverEntity = new Template.Method.Converted();
        public final Template.Method<Void> onTick = new Template.Method();
        public final Template.Method.Converted<Void> loadFromNBT = new Template.Method.Converted();
        public final Template.Method.Converted<Void> saveWithoutId = new Template.Method.Converted();
        public final Template.Method.Converted<Void> saveToNBT = new Template.Method.Converted();
        public final Template.Method<Integer> getId = new Template.Method();
        public final Template.Method<UUID> getUniqueID = new Template.Method();
        public final Template.Method.Converted<DataWatcher> getDataWatcher = new Template.Method.Converted();
        public final Template.Method<Boolean> isSneaking = new Template.Method();
        public final Template.Method.Converted<Void> appendEntityCrashDetails = new Template.Method.Converted();
        public final Template.Method<Void> onPush = new Template.Method();
        public final Template.Method.Converted<Void> positionRider = new Template.Method.Converted();
        public final Template.Method<Integer> getPortalCooldownMaximum = new Template.Method();
        public final Template.Method<Boolean> isInsidePortalThisTick = new Template.Method();
        public final Template.Method<Void> suppressPortalThisTick = new Template.Method();
        public final Template.Method<Integer> getPortalTime = new Template.Method();
        public final Template.Method<Boolean> setPortalTime = new Template.Method();
        public final Template.Method<Integer> getPortalWaitTime = new Template.Method();
        @Template.Optional
        public final Template.Method<Void> opt_tick_pushToHopper = new Template.Method();
        @Template.Optional
        public final Template.Method<Void> remove = new Template.Method();
        @Template.Optional
        public final Template.Method.Converted<InteractionResult> onInteractBy_26_1 = new Template.Method.Converted();
        @Template.Optional
        public final Template.Method.Converted<InteractionResult> onInteractBy_1_16 = new Template.Method.Converted();
        @Template.Optional
        public final Template.Method.Converted<Boolean> onInteractBy_1_11_2 = new Template.Method.Converted();
        @Template.Optional
        public final Template.Method.Converted<Boolean> onInteractBy_1_9 = new Template.Method.Converted();
        @Template.Optional
        public final Template.Method.Converted<Boolean> onInteractBy_1_8_8 = new Template.Method.Converted();
        public final Template.Method<Boolean> isAlwaysTicked = new Template.Method();
        public final Template.Method<Boolean> hasCustomName = new Template.Method();
        public final Template.Method.Converted<ChatText> getCustomName = new Template.Method.Converted();
        public final Template.Method.Converted<Void> collide = new Template.Method.Converted();
        public final Template.Method<World> getBukkitWorld = new Template.Method();
        public final Template.Method.Converted<LevelHandle> getWorld = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setWorld = new Template.Method.Converted();
        public final Template.Method<Entity> getBukkitEntity = new Template.Method();
    }
}

