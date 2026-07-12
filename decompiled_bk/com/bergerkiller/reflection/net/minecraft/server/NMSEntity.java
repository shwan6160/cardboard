/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.core.BlockPosHandle;
import com.bergerkiller.generated.net.minecraft.util.RandomSourceHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.SafeDirectField;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.World;
import org.bukkit.entity.Entity;

@Deprecated
public class NMSEntity {
    public static final ClassTemplate<?> T = ClassTemplate.create(EntityHandle.T.getType()).addImport("org.bukkit.craftbukkit.entity.CraftEntity");
    public static final FieldAccessor<Entity> bukkitEntity = EntityHandle.T.bukkitEntityField.toFieldAccessor();
    public static final TranslatorFieldAccessor<Entity> vehicleField = ((Template.Field)EntityHandle.T.vehicle.raw).toFieldAccessor().translate(DuplexConversion.entity);
    public static final TranslatorFieldAccessor<World> world = new SafeDirectField<Object>(){

        @Override
        public Object get(Object instance) {
            return ((Template.Method)EntityHandle.T.getWorld.raw).invoke(instance);
        }

        @Override
        public boolean set(Object instance, Object value) {
            ((Template.Method)EntityHandle.T.setWorld.raw).invoke(instance, value);
            return true;
        }
    }.translate(DuplexConversion.world);
    public static final FieldAccessor<Double> lastX = EntityHandle.T.lastX.toFieldAccessor();
    public static final FieldAccessor<Double> lastY = EntityHandle.T.lastY.toFieldAccessor();
    public static final FieldAccessor<Double> lastZ = EntityHandle.T.lastZ.toFieldAccessor();
    public static final FieldAccessor<Float> yaw = EntityHandle.T.yaw.toFieldAccessor();
    public static final FieldAccessor<Float> pitch = EntityHandle.T.pitch.toFieldAccessor();
    public static final FieldAccessor<Float> lastYaw = EntityHandle.T.lastYaw.toFieldAccessor();
    public static final FieldAccessor<Float> lastPitch = EntityHandle.T.lastPitch.toFieldAccessor();
    public static final FieldAccessor<Object> boundingBox = ((Template.Field)EntityHandle.T.boundingBoxField.raw).toFieldAccessor();
    public static final FieldAccessor<Boolean> onGround = EntityHandle.T.onGround.toFieldAccessor();
    public static final FieldAccessor<Boolean> velocityChanged = EntityHandle.T.velocityChanged.toFieldAccessor();
    public static final FieldAccessor<Float> fallDistance = EntityHandle.T.fallDistance.toFieldAccessor();
    @Deprecated
    public static final FieldAccessor<Float> stepCounter = new SafeDirectField<Float>(){

        @Override
        public Float get(Object instance) {
            return Float.valueOf(EntityHandle.createHandle(instance).getStepCounter());
        }

        @Override
        public boolean set(Object instance, Float value) {
            EntityHandle.createHandle(instance).setStepCounter(value.floatValue());
            return true;
        }
    };
    public static final FieldAccessor<Boolean> noclip = EntityHandle.T.noclip.toFieldAccessor();
    public static final FieldAccessor<RandomSourceHandle> random = EntityHandle.T.random.toFieldAccessor();
    public static final TranslatorFieldAccessor<DataWatcher> datawatcher = EntityHandle.T.datawatcherField.toFieldAccessor();
    public static final DataWatcher.Key<Byte> DATA_FLAGS = EntityHandle.DATA_FLAGS;
    public static final DataWatcher.Key<Integer> DATA_AIR_TICKS = EntityHandle.DATA_AIR_TICKS;
    public static final DataWatcher.Key<ChatText> DATA_CUSTOM_NAME = EntityHandle.DATA_CUSTOM_NAME;
    public static final DataWatcher.Key<Boolean> DATA_CUSTOM_NAME_VISIBLE = EntityHandle.DATA_CUSTOM_NAME_VISIBLE;
    public static final DataWatcher.Key<Boolean> DATA_SILENT = EntityHandle.DATA_SILENT;
    public static final DataWatcher.Key<Boolean> DATA_NO_GRAVITY = EntityHandle.DATA_NO_GRAVITY;
    public static final int DATA_FLAG_ON_FIRE = 1;
    public static final int DATA_FLAG_SNEAKING = 2;
    public static final int DATA_FLAG_UNKNOWN1 = 4;
    public static final int DATA_FLAG_SPRINTING = 8;
    public static final int DATA_FLAG_UNKNOWN2 = 16;
    public static final int DATA_FLAG_INVISIBLE = 32;
    public static final int DATA_FLAG_GLOWING = 64;
    public static final int DATA_FLAG_FLYING = 128;
    public static final FieldAccessor<Boolean> positionChanged = EntityHandle.T.positionChanged.toFieldAccessor();
    public static final FieldAccessor<Integer> portalCooldown = EntityHandle.T.portalCooldown.toFieldAccessor();
    public static final FieldAccessor<double[]> move_SomeArray = EntityHandle.T.move_SomeArray.toFieldAccessor();
    public static final FieldAccessor<Long> move_SomeState = EntityHandle.T.move_SomeState.toFieldAccessor();
    private static final MethodAccessor<Void> playStepSound = ((Template.Method)EntityHandle.T.playStepSound.raw).toMethodAccessor();
    private static final MethodAccessor<Void> setRotation = EntityHandle.T.setRotation.toMethodAccessor();
    public static final MethodAccessor<Void> burn = EntityHandle.T.burn.toMethodAccessor();
    public static final MethodAccessor<Object> getSwimSound = ((Template.Method)EntityHandle.T.getSwimSound.raw).toMethodAccessor();
    public static final MethodAccessor<Void> makeSound = ((Template.Method)EntityHandle.T.makeSound.raw).toMethodAccessor();
    private static final MethodAccessor<Boolean> isInWaterUpdate = EntityHandle.T.isInWaterUpdate.toMethodAccessor();
    private static final MethodAccessor<Boolean> isInWaterNoUpdate = EntityHandle.T.isInWater.toMethodAccessor();
    private static final MethodAccessor<Boolean> hasMovementSound = EntityHandle.T.hasMovementSound.toMethodAccessor();
    public static final MethodAccessor<Void> doFallUpdate = ((Template.Method)EntityHandle.T.updateFalling.raw).toMethodAccessor();
    @Deprecated
    public static final MethodAccessor<Double> calculateDistance = EntityHandle.T.calculateDistanceSquared.toMethodAccessor();
    public static final MethodAccessor<Object> getBoundingBox = ((Template.Method)EntityHandle.T.getBoundingBox.raw).toMethodAccessor();

    public static boolean isInWater(Object entityHandle, boolean update) {
        return update ? isInWaterUpdate.invoke(entityHandle, new Object[0]) : isInWaterNoUpdate.invoke(entityHandle, new Object[0]);
    }

    public static void playStepSound(Object entityHandle, int x, int y, int z, Object blockStepped) {
        if (blockStepped != null) {
            playStepSound.invoke(entityHandle, BlockPosHandle.createNew(x, y, z).getRaw(), blockStepped);
        }
    }

    public static boolean hasMovementSound(Object entityHandle) {
        return hasMovementSound.invoke(entityHandle, new Object[0]);
    }

    public static void setRotation(Object entityHandle, float yaw, float pitch) {
        setRotation.invoke(entityHandle, Float.valueOf(yaw), Float.valueOf(pitch));
    }
}

