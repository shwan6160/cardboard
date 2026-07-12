/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.internal.hooks;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.controller.EntityPositionApplier;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.DamageSource;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.InteractionResult;
import com.bergerkiller.bukkit.common.wrappers.MoveType;
import com.bergerkiller.generated.net.minecraft.locale.LanguageHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityTypeHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.item.ItemEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.PlayerHandle;
import com.bergerkiller.generated.net.minecraft.world.level.storage.ValueOutputHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.Vec3Handle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@ClassHook.HookPackage(value="net.minecraft.server")
@ClassHook.HookImportList(value={@ClassHook.HookImport(value="net.minecraft.world.phys.Vec3"), @ClassHook.HookImport(value="net.minecraft.world.entity.Entity"), @ClassHook.HookImport(value="net.minecraft.world.entity.Entity.MoveFunction"), @ClassHook.HookImport(value="net.minecraft.world.InteractionHand"), @ClassHook.HookImport(value="net.minecraft.world.InteractionResult"), @ClassHook.HookImport(value="net.minecraft.world.item.ItemStack"), @ClassHook.HookImport(value="net.minecraft.nbt.CompoundTag")})
@ClassHook.HookLoadVariables(value="com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER")
public class EntityHook
extends ClassHook<EntityHook> {
    private EntityController<?> controller = null;
    private boolean controllerPositionsPassengers = false;
    private Throwable stack = null;
    private boolean isHandlingBaseDie = false;
    private static final FastMethod<Void> moveFunctionMethod = new FastMethod();
    private static final boolean DAMAGE_ENTITY_HAS_WORLD_ARG;
    private static boolean HAS_ONPUSH_WITH_ENTITY;
    private Object lastPushedEntity = null;
    private static final boolean POSITIONRIDER_1_20;
    private static final boolean POSITIONRIDER_1_9_TO_1_20;
    private static final boolean POSITIONRIDER_1_8;
    private static final Object ENTITY_REMOVE_REASON_KILLED;
    private static final Object ENTITY_REMOVE_REASON_DISCARDED;
    private static BaseEntityRemoveHandler BASE_ENTITY_REMOVED;

    public void setStack(Throwable t) {
        this.stack = t;
    }

    private Throwable getStack() {
        if (this.stack == null) {
            this.stack = new Throwable();
        }
        return this.stack;
    }

    public boolean hasController() {
        return this.controller != null;
    }

    public EntityController<?> getController() {
        if (this.controller == null) {
            throw new RuntimeException("Controller is never allowed to be null!");
        }
        return this.controller;
    }

    public void setController(EntityController<?> controller) {
        this.controller = controller;
        this.controllerPositionsPassengers = controller != null && CommonUtil.isMethodOverrided(EntityController.class, controller, "onPositionPassenger", Entity.class, EntityPositionApplier.class);
    }

    public InteractionResult base_onInteractBy(HumanEntity humanEntity, HumanHand humanHand, Vector location) {
        Object entityHumanHandle = HandleConversion.toEntityHandle((Entity)humanEntity);
        Object interactionHandHandle = humanHand.toNMSInteractionHand(humanEntity);
        if (EntityHandle.T.onInteractBy_26_1.isAvailable()) {
            Object locationVec = Vec3Handle.fromBukkitRaw(location);
            return InteractionResult.fromHandle(((EntityHook)this.base).onInteractBy_26_1(entityHumanHandle, interactionHandHandle, locationVec));
        }
        if (EntityHandle.T.onInteractBy_1_16.isAvailable()) {
            return InteractionResult.fromHandle(((EntityHook)this.base).onInteractBy_1_16(entityHumanHandle, interactionHandHandle));
        }
        if (EntityHandle.T.onInteractBy_1_11_2.isAvailable()) {
            return InteractionResult.fromTruthy(((EntityHook)this.base).onInteractBy_1_11_2(entityHumanHandle, interactionHandHandle));
        }
        if (EntityHandle.T.onInteractBy_1_9.isAvailable()) {
            Object item = HandleConversion.toItemStackHandle(HumanHand.getHeldItem(humanEntity, humanHand));
            return InteractionResult.fromTruthy(((EntityHook)this.base).onInteractBy_1_10_2(entityHumanHandle, item, interactionHandHandle));
        }
        if (EntityHandle.T.onInteractBy_1_8_8.isAvailable()) {
            return InteractionResult.fromTruthy(((EntityHook)this.base).onInteractBy_1_8_8(entityHumanHandle));
        }
        throw new UnsupportedOperationException("Don't know what interact method is used!");
    }

    @Deprecated
    @ClassHook.HookMethodCondition(value="version < 1.9")
    @ClassHook.HookMethod(value="public boolean onInteractBy_1_8_8:???(net.minecraft.world.entity.player.Player entityhuman)")
    public boolean onInteractBy_1_8_8(Object entityHuman) {
        return this.onInteractBy((HumanEntity)WrapperConversion.toEntity(entityHuman), HumanHand.RIGHT, null).isTruthy();
    }

    @Deprecated
    @ClassHook.HookMethodCondition(value="version >= 1.9 && version <= 1.11.1")
    @ClassHook.HookMethod(value="public boolean onInteractBy_1_9:???(net.minecraft.world.entity.player.Player entityhuman, ItemStack itemstack, InteractionHand enumhand)")
    public boolean onInteractBy_1_10_2(Object entityHuman, Object itemstack, Object enumHand) {
        return this.onInteractBy_1_11_2(entityHuman, enumHand);
    }

    @Deprecated
    @ClassHook.HookMethodCondition(value="version >= 1.11.2 && version <= 1.15.2")
    @ClassHook.HookMethod(value="public boolean onInteractBy_1_11_2:???(net.minecraft.world.entity.player.Player entityhuman, InteractionHand enumhand)")
    public boolean onInteractBy_1_11_2(Object entityHuman, Object enumHand) {
        HumanEntity humanEntity = (HumanEntity)WrapperConversion.toEntity(entityHuman);
        return this.onInteractBy(humanEntity, HumanHand.fromNMSInteractionHand(humanEntity, enumHand), null).isTruthy();
    }

    @Deprecated
    @ClassHook.HookMethodCondition(value="version >= 1.16 && version < 26.1")
    @ClassHook.HookMethod(value="public InteractionResult onInteractBy_1_16:???(net.minecraft.world.entity.player.Player entityhuman, InteractionHand enumhand)")
    public Object onInteractBy_1_16(Object entityHuman, Object enumHand) {
        HumanEntity humanEntity = (HumanEntity)WrapperConversion.toEntity(entityHuman);
        return this.onInteractBy(humanEntity, HumanHand.fromNMSInteractionHand(humanEntity, enumHand), null).getRawHandle();
    }

    @Deprecated
    @ClassHook.HookMethodCondition(value="version >= 26.1")
    @ClassHook.HookMethod(value="public InteractionResult onInteractBy_26_1:???(net.minecraft.world.entity.player.Player entityhuman, InteractionHand enumhand, Vec3 location)")
    public Object onInteractBy_26_1(Object entityHuman, Object enumHand, Object location) {
        HumanEntity humanEntity = (HumanEntity)WrapperConversion.toEntity(entityHuman);
        Vector bukkitLocation = Vec3Handle.T.toBukkit.invoke(location);
        return this.onInteractBy(humanEntity, HumanHand.fromNMSInteractionHand(humanEntity, enumHand), bukkitLocation).getRawHandle();
    }

    @ClassHook.HookMethodCondition(value="version >= 1.17")
    @ClassHook.HookMethod(value="public boolean isAlwaysTicked:???();")
    public boolean isAlwaysTicked() {
        return true;
    }

    public InteractionResult onInteractBy(HumanEntity humanEntity, HumanHand humanHand, Vector location) {
        try {
            if (this.checkController()) {
                return this.controller.onInteractBy(humanEntity, humanHand, location);
            }
            return this.base_onInteractBy(humanEntity, humanHand, location);
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity onInteractBy callback", t);
            return InteractionResult.FAIL;
        }
    }

    @ClassHook.HookMethodCondition(value="version < 1.21.2")
    @ClassHook.HookMethod(value="public boolean damageEntity:???(net.minecraft.world.damagesource.DamageSource damagesource, float f)")
    public boolean onDamageEntityLegacy(Object damageSource, float damage) {
        try {
            if (this.checkController()) {
                return this.controller.onDamage(DamageSource.getForHandle(damageSource), damage);
            }
            return ((EntityHook)this.base).onDamageEntityLegacy(damageSource, damage);
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity damageEntity callback", t);
            return false;
        }
    }

    @ClassHook.HookMethodCondition(value="version >= 1.21.2")
    @ClassHook.HookMethod(value="public boolean damageEntityWithWorld:hurtServer(net.minecraft.server.level.ServerLevel world, net.minecraft.world.damagesource.DamageSource damagesource, float f)")
    public boolean onDamageEntity(Object world, Object damageSource, float damage) {
        try {
            if (this.checkController()) {
                return this.controller.onDamage(DamageSource.getForHandle(damageSource), damage);
            }
            return ((EntityHook)this.base).onDamageEntity(world, damageSource, damage);
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity damageEntity callback", t);
            return false;
        }
    }

    public boolean baseDamageEntity(Object damageSource, float damage) {
        if (DAMAGE_ENTITY_HAS_WORLD_ARG) {
            Object worldServer = ((Template.Method)EntityHandle.T.getWorld.raw).invoker.invoke(this.instance());
            return ((EntityHook)this.base).onDamageEntity(worldServer, Float.valueOf(damage), damage);
        }
        return ((EntityHook)this.base).onDamageEntityLegacy(damageSource, damage);
    }

    @ClassHook.HookMethod(value="public void onTick:???()")
    public void onTick() {
        try {
            if (this.checkController()) {
                this.controller.onTick();
                if (EntityHandle.T.opt_tick_pushToHopper.isAvailable()) {
                    EntityHandle.T.opt_tick_pushToHopper.invoke(this.instance());
                }
            } else {
                ((EntityHook)this.base).onTick();
            }
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity tick callback", t);
        }
    }

    public void baseOnPush(double dx, double dy, double dz) {
        if (HAS_ONPUSH_WITH_ENTITY) {
            ((EntityHook)this.base).onPushWithEntity(dx, dy, dz, this.lastPushedEntity);
        } else {
            ((EntityHook)this.base).onPush(dx, dy, dz);
        }
    }

    @ClassHook.HookMethodCondition(value="!exists net.minecraft.world.entity.Entity public void push(double x, double y, double z, net.minecraft.world.entity.Entity pushingEntity)")
    @ClassHook.HookMethod(value="public void onPush:???(double d0, double d1, double d2)")
    public void onPush(double dx, double dy, double dz) {
        try {
            if (this.checkController()) {
                this.controller.onPush(dx, dy, dz);
            } else {
                ((EntityHook)this.base).onPush(dx, dy, dz);
            }
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity push callback", t);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @ClassHook.HookMethodCondition(value="exists net.minecraft.world.entity.Entity public void push(double x, double y, double z, net.minecraft.world.entity.Entity pushingEntity)")
    @ClassHook.HookMethod(value="public void push(double d0, double d1, double d2, net.minecraft.world.entity.Entity pushingEntity)")
    public void onPushWithEntity(double dx, double dy, double dz, Object pushingEntity) {
        block6: {
            try {
                if (this.checkController()) {
                    Object before = this.lastPushedEntity;
                    try {
                        this.lastPushedEntity = pushingEntity;
                        this.controller.onPush(dx, dy, dz);
                        break block6;
                    }
                    finally {
                        this.lastPushedEntity = before;
                    }
                }
                ((EntityHook)this.base).onPushWithEntity(dx, dy, dz, pushingEntity);
            }
            catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity push callback", t);
            }
        }
    }

    public void basePositionRider(Entity passenger, EntityPositionApplier applier) {
        if (POSITIONRIDER_1_20) {
            if (!(applier instanceof EntityPositionApplierWithNMSMoveFunction)) {
                throw new IllegalArgumentException("Position applier is of an invalid type");
            }
            Object nmsMoveFunction = ((EntityPositionApplierWithNMSMoveFunction)applier).nmsMoveFunction;
            ((EntityHook)this.base).onPositionRider_1_20(HandleConversion.toEntityHandle(passenger), nmsMoveFunction);
        } else if (POSITIONRIDER_1_9_TO_1_20) {
            ((EntityHook)this.base).onPositionRider_1_9_to_1_20(HandleConversion.toEntityHandle(passenger));
        } else if (POSITIONRIDER_1_8) {
            ((EntityHook)this.base).onPositionRider_1_8();
        }
    }

    @ClassHook.HookMethodCondition(value="version >= 1.20")
    @ClassHook.HookMethod(value="protected void positionRider(Entity passenger, Entity.MoveFunction moveFunction)")
    public void onPositionRider_1_20(Object nmsPassenger, Object nmsMoveFunction) {
        if (!this.controllerPositionsPassengers) {
            ((EntityHook)this.base).onPositionRider_1_20(nmsPassenger, nmsMoveFunction);
            return;
        }
        Entity bPassenger = WrapperConversion.toEntity(nmsPassenger);
        if (bPassenger == null || !((ExtendedEntity)this.controller.getEntity()).isPassenger(bPassenger)) {
            ((EntityHook)this.base).onPositionRider_1_20(nmsPassenger, nmsMoveFunction);
            return;
        }
        try {
            EntityPositionApplierWithNMSMoveFunction applier = new EntityPositionApplierWithNMSMoveFunction(nmsPassenger, nmsMoveFunction);
            this.controller.onPositionPassenger(bPassenger, applier);
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity position rider callback", t);
        }
    }

    @ClassHook.HookMethodCondition(value="version >= 1.9 && version < 1.20")
    @ClassHook.HookMethod(value="public void positionRider:???(Entity passenger)")
    public void onPositionRider_1_9_to_1_20(Object nmsPassenger) {
        if (!this.controllerPositionsPassengers) {
            ((EntityHook)this.base).onPositionRider_1_9_to_1_20(nmsPassenger);
            return;
        }
        Entity bPassenger = WrapperConversion.toEntity(nmsPassenger);
        if (bPassenger == null || !((ExtendedEntity)this.controller.getEntity()).isPassenger(bPassenger)) {
            ((EntityHook)this.base).onPositionRider_1_9_to_1_20(nmsPassenger);
            return;
        }
        try {
            this.controller.onPositionPassenger(bPassenger, new EntityPositionApplierBaseImpl(nmsPassenger));
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity position rider callback", t);
        }
    }

    @ClassHook.HookMethodCondition(value="version < 1.9")
    @ClassHook.HookMethod(value="public void positionRider:al()")
    public void onPositionRider_1_8() {
        if (!this.controllerPositionsPassengers) {
            ((EntityHook)this.base).onPositionRider_1_8();
            return;
        }
        try {
            List<Entity> passengers = ((ExtendedEntity)this.controller.getEntity()).getPassengers();
            for (Entity passenger : passengers) {
                this.controller.onPositionPassenger(passenger, new EntityPositionApplierBaseImpl(HandleConversion.toEntityHandle(passenger)));
            }
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity position rider callback", t);
        }
    }

    @ClassHook.HookMethodCondition(value="version >= 1.14")
    @ClassHook.HookMethod(value="public void move(net.minecraft.world.entity.MoverType enummovetype, net.minecraft.world.phys.Vec3 vec3d)")
    public void onMove_v3(Object enumMoveType, Object vec3d) {
        double dx = Vec3Handle.T.x.getDouble(vec3d);
        double dy = Vec3Handle.T.y.getDouble(vec3d);
        double dz = Vec3Handle.T.z.getDouble(vec3d);
        this.onMove_v2(enumMoveType, dx, dy, dz);
    }

    @ClassHook.HookMethodCondition(value="version >= 1.11 && version <= 1.13.2")
    @ClassHook.HookMethod(value="public void move(net.minecraft.world.entity.MoverType enummovetype, double d0, double d1, double d2)")
    public void onMove_v2(Object enumMoveType, double dx, double dy, double dz) {
        try {
            if (this.checkController()) {
                if (CommonCapabilities.ENTITY_MOVE_VER2) {
                    this.controller.onMove(MoveType.getFromHandle(enumMoveType), dx, dy, dz);
                } else {
                    this.controller.onMove(MoveType.SELF, dx, dy, dz);
                }
            } else if (CommonCapabilities.ENTITY_MOVE_VER3) {
                ((EntityHook)this.base).onMove_v3(enumMoveType, ((Template.Constructor)Vec3Handle.T.constr_x_y_z.raw).newInstance(dx, dy, dz));
            } else if (CommonCapabilities.ENTITY_MOVE_VER2) {
                ((EntityHook)this.base).onMove_v2(enumMoveType, dx, dy, dz);
            } else {
                ((EntityHook)this.base).onMove_v1(dx, dy, dz);
            }
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity move callback", t);
        }
    }

    @ClassHook.HookMethodCondition(value="version <= 1.10.2")
    @ClassHook.HookMethod(value="public void move(double d0, double d1, double d2)")
    public void onMove_v1(double dx, double dy, double dz) {
        this.onMove_v2(MoveType.SELF.getHandle(), dx, dy, dz);
    }

    public void onBaseDeath(boolean killed) {
        try {
            this.isHandlingBaseDie = true;
            if (CommonCapabilities.ENTITY_REMOVE_WITH_REASON) {
                BASE_ENTITY_REMOVED.remove((EntityHook)this.base, killed ? ENTITY_REMOVE_REASON_KILLED : ENTITY_REMOVE_REASON_DISCARDED, null);
            } else {
                ((EntityHook)this.base).die();
            }
        }
        finally {
            this.isHandlingBaseDie = false;
        }
    }

    @ClassHook.HookMethodCondition(value="version <= 1.16.5")
    @ClassHook.HookMethod(value="public void die()")
    public void die() {
        try {
            if (!this.isHandlingBaseDie && this.checkController()) {
                this.controller.onDie(true);
            } else {
                ((EntityHook)this.base).die();
            }
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity remove callback", t);
        }
    }

    @ClassHook.HookMethodCondition(value="version >= 1.17")
    @ClassHook.HookMethod(value="public void remove:???(net.minecraft.world.entity.Entity.RemovalReason removalReason)")
    public void onEntityRemoved(Object removalReason) {
        try {
            if (this.isHandlingBaseDie) {
                ((EntityHook)this.base).onEntityRemoved(removalReason);
            } else if (this.checkController()) {
                if (removalReason == ENTITY_REMOVE_REASON_KILLED) {
                    this.controller.onDie(true);
                } else if (removalReason == ENTITY_REMOVE_REASON_DISCARDED) {
                    this.controller.onDie(false);
                } else {
                    BASE_ENTITY_REMOVED.remove((EntityHook)this.base, removalReason, null);
                }
            } else {
                BASE_ENTITY_REMOVED.remove((EntityHook)this.base, removalReason, null);
            }
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity remove callback", t);
        }
    }

    @ClassHook.HookMethodCondition(value="exists net.minecraft.world.entity.Entity public void remove(    net.minecraft.world.entity.Entity.RemovalReason removalReason,    org.bukkit.event.entity.EntityRemoveEvent.Cause cause)")
    @ClassHook.HookMethod(value="public void remove(net.minecraft.world.entity.Entity.RemovalReason removalReason, org.bukkit.event.entity.EntityRemoveEvent.Cause cause);")
    public void onEntityRemovedWithCause(Object removalReason, Object removeEventCause) {
        try {
            if (this.isHandlingBaseDie) {
                ((EntityHook)this.base).onEntityRemovedWithCause(removalReason, removeEventCause);
            } else if (this.checkController()) {
                if (removalReason == ENTITY_REMOVE_REASON_KILLED) {
                    this.controller.onDie(true);
                } else if (removalReason == ENTITY_REMOVE_REASON_DISCARDED) {
                    this.controller.onDie(false);
                } else {
                    BASE_ENTITY_REMOVED.remove((EntityHook)this.base, removalReason, removeEventCause);
                }
            } else {
                BASE_ENTITY_REMOVED.remove((EntityHook)this.base, removalReason, removeEventCause);
            }
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity remove callback", t);
        }
    }

    @ClassHook.HookMethodCondition(value="version >= 1.13")
    @ClassHook.HookMethod(value="public boolean hasExactlyOnePlayerPassenger()")
    public boolean hasExactlyOnePlayerPassenger() {
        if (this.controller != null && !this.controller.isPlayerTakeable() && ((ExtendedEntity)this.controller.getEntity()).getPassengers().size() >= 2) {
            return true;
        }
        return ((EntityHook)this.base).hasExactlyOnePlayerPassenger();
    }

    @ClassHook.HookMethod(value="public String getStringUUID:???()")
    public String getStringUUID() {
        try {
            if (this.checkController()) {
                return this.controller.getLocalizedName();
            }
            return this.getStringUUID_base(this.instance());
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred serializing saved entity identifier", t);
            return "ERROR";
        }
    }

    public String getStringUUID_base(Object instance) {
        if (Common.evaluateMCVersion(">=", "1.13")) {
            return ((EntityHook)this.base).getStringUUID();
        }
        if (EntityTypeHandle.T.opt_typeNameMap_1_10_2.isAvailable()) {
            return ((EntityHook)this.base).getStringUUID();
        }
        if (PlayerHandle.T.isAssignableFrom(instance) || ItemEntityHandle.T.isAssignableFrom(instance)) {
            return ((EntityHook)this.base).getStringUUID();
        }
        if (EntityHandle.T.hasCustomName.invoke(instance).booleanValue()) {
            return EntityHandle.T.getCustomName.invoke(instance).getMessage();
        }
        int typeId = EntityTypeHandle.getEntityTypeId(EntityHook.findInstanceBaseType(instance));
        String name = EntityTypeHandle.T.opt_typeIdToName_1_11.get().get(typeId);
        if (name == null) {
            name = "generic";
        }
        return LanguageHandle.INSTANCE().get("entity." + name + ".name");
    }

    private boolean handleSaveAsPassenger(Object valueoutput, boolean includeAll, boolean includeNonSaveable, boolean forceSerialization) {
        Object handle = this.instance();
        if (!EntityHandle.T.isSavingAllowed.invoke(handle).booleanValue()) {
            return false;
        }
        ValueOutputHandle.T.putString.invoke(valueoutput, "id", EntityHook.getSavedName(this.instance()));
        ((Template.Method)EntityHandle.T.saveWithoutId.raw).invoke(handle, valueoutput, includeAll, includeNonSaveable, forceSerialization);
        return true;
    }

    @ClassHook.HookMethod(value="public boolean saveAsPassenger(net.minecraft.world.level.storage.ValueOutput valueOutput)")
    public boolean onSaveAsPassenger(Object valueoutput) {
        return this.handleSaveAsPassenger(valueoutput, true, false, false);
    }

    @ClassHook.HookMethodCondition(value="paper && version >= 1.21.4")
    @ClassHook.HookMethod(value="public boolean saveAsPassenger(net.minecraft.world.level.storage.ValueOutput valueOutput, boolean includeAll, boolean includeNonSaveable, boolean forceSerialization)")
    public boolean onSaveAsPassengerPaperMultiFlags(Object valueoutput, boolean includeAll, boolean includeNonSaveable, boolean forceSerialization) {
        return this.handleSaveAsPassenger(valueoutput, includeAll, includeNonSaveable, forceSerialization);
    }

    @ClassHook.HookMethodCondition(value="paper && version >= 1.20.3 && version < 1.21.4")
    @ClassHook.HookMethod(value="public boolean saveAsPassenger(net.minecraft.world.level.storage.ValueOutput valueOutput, boolean includeAll)")
    public boolean onSaveAsPassengerPaperIncludeAll(Object valueoutput, boolean includeAll) {
        return this.handleSaveAsPassenger(valueoutput, includeAll, false, false);
    }

    @ClassHook.HookMethodCondition(value="!paper && version >= 1.20.3")
    @ClassHook.HookMethod(value="public boolean saveAsPassenger(net.minecraft.world.level.storage.ValueOutput valueOutput, boolean includeAll)")
    public boolean onSaveAsPassengerSpigotIncludeAll(Object valueoutput, boolean includeAll) {
        return this.handleSaveAsPassenger(valueoutput, includeAll, false, false);
    }

    @ClassHook.HookMethodCondition(value="version <= 1.12.2")
    @ClassHook.HookMethod(value="public boolean saveEntityIfNotPassenger:d(CompoundTag nbttagcompound)")
    public boolean saveEntity(Object tag) {
        try {
            Object handle = this.instance();
            if (EntityHandle.T.isPassenger.invoke(handle).booleanValue()) {
                return false;
            }
            return this.onSaveAsPassenger(tag);
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the save callback", t);
            return false;
        }
    }

    private static final String getSavedName(Object instance) {
        return EntityTypeHandle.getEntityInternalName(EntityHook.findInstanceBaseType(instance));
    }

    @ClassHook.HookMethod(value="public void collide:???(net.minecraft.world.entity.Entity entity)")
    public void collide(Object entity) {
        try {
            if (this.checkController()) {
                Entity bukkitEntity = WrapperConversion.toEntity(entity);
                if (bukkitEntity.isInsideVehicle()) {
                    return;
                }
                if (this.isPassengerOfRecursive((Entity)((ExtendedEntity)this.controller.getEntity()).getEntity(), bukkitEntity)) {
                    return;
                }
                if (this.controller.onEntityCollision(bukkitEntity)) {
                    this.controller.onEntityBump(bukkitEntity);
                }
            } else {
                ((EntityHook)this.base).collide(entity);
            }
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the collide callback", t);
        }
    }

    private boolean isPassengerOfRecursive(Entity vehicle, Entity passenger) {
        Iterator iterator;
        List vehiclePassengers = (List)com.bergerkiller.generated.org.bukkit.entity.EntityHandle.T.getPassengers.invoker.invoke(vehicle);
        if (!vehiclePassengers.isEmpty() && (iterator = vehiclePassengers.iterator()).hasNext()) {
            Entity vehiclePassenger = (Entity)iterator.next();
            if (vehiclePassenger == passenger) {
                return true;
            }
            return this.isPassengerOfRecursive(vehiclePassenger, passenger);
        }
        return false;
    }

    @ClassHook.HookMethod(value="public void setItem(int i, ItemStack itemstack)", optional=true)
    public void setInventoryItem(int i, Object itemstack) {
        try {
            if (this.checkController()) {
                this.controller.onItemSet(i, (ItemStack)Conversion.toItemStack.convert(itemstack));
            } else {
                ((EntityHook)this.base).setInventoryItem(i, itemstack);
            }
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the setItem callback", t);
        }
    }

    private boolean checkController() {
        if (this.controller == null) {
            Logging.LOGGER.once(Level.SEVERE, "Incorrect state: no controller assigned! Creator:", this.getStack());
            return false;
        }
        if (this.controller.getEntity() == null) {
            Logging.LOGGER.once(Level.SEVERE, "Incorrect state: controller " + this.controller.getClass().getName() + " has no entity bound to it! Creator:", this.getStack());
            return false;
        }
        return true;
    }

    static {
        Class<?> moveFunctionType = CommonUtil.getClass("net.minecraft.world.entity.Entity$MoveFunction");
        if (moveFunctionType != null) {
            try {
                moveFunctionMethod.init(Resolver.resolveAndGetDeclaredMethod(moveFunctionType, "accept", EntityHandle.T.getType(), Double.TYPE, Double.TYPE, Double.TYPE));
                moveFunctionMethod.forceInitialization();
            }
            catch (Throwable t) {
                Logging.LOGGER.log(Level.WARNING, "Failed to find the Entity MoveFunction accept method", t);
            }
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.20")) {
            Logging.LOGGER.warning("Failed to find the Entity MoveFunction class");
        }
        DAMAGE_ENTITY_HAS_WORLD_ARG = CommonBootstrap.evaluateMCVersion(">=", "1.21.2");
        HAS_ONPUSH_WITH_ENTITY = LogicUtil.tryCreate(() -> {
            Resolver.resolveAndGetDeclaredMethod(EntityHandle.T.getType(), "push", Double.TYPE, Double.TYPE, Double.TYPE, EntityHandle.T.getType());
            return true;
        }, err -> false);
        POSITIONRIDER_1_20 = CommonBootstrap.evaluateMCVersion(">=", "1.20");
        POSITIONRIDER_1_9_TO_1_20 = CommonBootstrap.evaluateMCVersion(">=", "1.9") && CommonBootstrap.evaluateMCVersion("<", "1.20");
        POSITIONRIDER_1_8 = CommonBootstrap.evaluateMCVersion("<", "1.9");
        Object killed = null;
        Object discarded = null;
        BaseEntityRemoveHandler baseEntityRemoved = (hook, removalReason, cause) -> {};
        if (CommonCapabilities.ENTITY_REMOVE_WITH_REASON) {
            Class<?> reasonClass = CommonUtil.getClass("net.minecraft.world.entity.Entity.RemovalReason");
            if (reasonClass == null) {
                Logging.LOGGER_REFLECTION.severe("Failed to find Entity.RemovalReason class");
            } else {
                Enum[] values = (Enum[])reasonClass.getEnumConstants();
                killed = Stream.of(values).filter(n -> n.name().equals("KILLED")).findFirst().orElse(null);
                discarded = Stream.of(values).filter(n -> n.name().equals("DISCARDED")).findFirst().orElse(null);
            }
            baseEntityRemoved = (base, removalReason, cause) -> base.onEntityRemoved(removalReason);
            Class<?> bukkitCauseClass = CommonUtil.getClass("org.bukkit.event.entity.EntityRemoveEvent.Cause");
            if (bukkitCauseClass != null) {
                try {
                    Resolver.resolveAndGetDeclaredMethod(EntityHandle.T.getType(), "remove", reasonClass, bukkitCauseClass);
                    baseEntityRemoved = EntityHook::onEntityRemovedWithCause;
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
        }
        ENTITY_REMOVE_REASON_KILLED = killed;
        ENTITY_REMOVE_REASON_DISCARDED = discarded;
        BASE_ENTITY_REMOVED = baseEntityRemoved;
    }

    private static class EntityPositionApplierWithNMSMoveFunction
    extends EntityPositionApplierBaseImpl {
        protected final Object nmsMoveFunction;

        public EntityPositionApplierWithNMSMoveFunction(Object nmsEntity, Object nmsMoveFunction) {
            super(nmsEntity);
            this.nmsMoveFunction = nmsMoveFunction;
        }

        @Override
        public void setPosition(double x, double y, double z) {
            moveFunctionMethod.invoke(this.nmsMoveFunction, this.entityHandle.getRaw(), x, y, z);
        }
    }

    private static class EntityPositionApplierBaseImpl
    implements EntityPositionApplier {
        protected final EntityHandle entityHandle;

        public EntityPositionApplierBaseImpl(Object nmsEntity) {
            this.entityHandle = EntityHandle.createHandle(nmsEntity);
        }

        @Override
        public void setPosition(double x, double y, double z) {
            this.entityHandle.setPosition(x, y, z);
        }

        @Override
        public Vector getPosition() {
            return this.entityHandle.getLoc();
        }

        @Override
        public void setBodyYaw(float yaw) {
            this.entityHandle.setYaw(yaw);
        }

        @Override
        public void setHeadYaw(float yaw) {
            this.entityHandle.setHeadRotation(yaw);
        }

        @Override
        public void setHeadPitch(float pitch) {
            this.entityHandle.setPitch(pitch);
        }

        @Override
        public float getBodyYaw() {
            return this.entityHandle.getYaw();
        }

        @Override
        public float getHeadYaw() {
            return this.entityHandle.getHeadRotation();
        }

        @Override
        public float getHeadPitch() {
            return this.entityHandle.getPitch();
        }
    }

    @FunctionalInterface
    private static interface BaseEntityRemoveHandler {
        public void remove(EntityHook var1, Object var2, Object var3);
    }
}

