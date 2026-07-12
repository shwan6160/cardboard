/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectInstanceHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributeHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributeInstanceHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributeMapHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;
import java.util.Map;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@Template.InstanceType(value="net.minecraft.world.entity.LivingEntity")
public abstract class LivingEntityHandle
extends EntityHandle {
    public static final LivingEntityClass T = Template.Class.create(LivingEntityClass.class, Common.TEMPLATE_RESOLVER);
    public static final DataWatcher.Key<Byte> DATA_LIVING_FLAGS = DataWatcher.Key.Type.BYTE.createKey(LivingEntityHandle.T.DATA_LIVING_FLAGS, -1);
    public static final DataWatcher.Key<Float> DATA_HEALTH = DataWatcher.Key.Type.FLOAT.createKey(LivingEntityHandle.T.DATA_HEALTH, 6);
    public static final DataWatcher.Key<Boolean> DATA_PARTICLES_HIDDEN = DataWatcher.Key.Type.BOOLEAN.createKey(LivingEntityHandle.T.DATA_PARTICLES_HIDDEN, 8);
    public static final DataWatcher.Key<Integer> DATA_UNKNOWN1 = DataWatcher.Key.Type.INTEGER.createKey(LivingEntityHandle.T.DATA_ARROWCOUNT, 9);
    public static final DataWatcher.Key<IntVector3> DATA_BEDPOSITION = DataWatcher.Key.Type.BLOCK_POSITION.createKey(LivingEntityHandle.T.DATA_BEDPOSITION, -1);

    public static LivingEntityHandle createHandle(Object handleInstance) {
        return (LivingEntityHandle)T.createHandle(handleInstance);
    }

    public abstract void resetAttributes();

    public abstract void detectEquipmentChanges();

    public abstract void loadEquipment(CommonTagCompound var1);

    public abstract CommonTagCompound saveEquipment();

    public abstract AttributeMapHandle getAttributeMap();

    public abstract AttributeInstanceHandle getAttribute(Holder<AttributeHandle> var1);

    public abstract Vector getMoveIntent();

    public abstract Collection<MobEffectInstanceHandle> getEffects();

    public abstract ItemStack getEquipment(EquipmentSlot var1);

    public abstract float getHealth();

    public abstract float getMaxHealth();

    public abstract float getAbsorptionAmount();

    public abstract void setAbsorptionAmount(float var1);

    public float getSideMovement() {
        return (float)this.getMoveIntent().getX();
    }

    public float getForwardMovement() {
        return (float)this.getMoveIntent().getZ();
    }

    public static LivingEntityHandle fromBukkit(LivingEntity livingEntity) {
        return LivingEntityHandle.createHandle(HandleConversion.toEntityHandle((Entity)livingEntity));
    }

    public abstract Map<Holder<MobEffectHandle>, MobEffectInstanceHandle> getMobEffects();

    public abstract void setMobEffects(Map<Holder<MobEffectHandle>, MobEffectInstanceHandle> var1);

    public abstract float getLastDamage();

    public abstract void setLastDamage(float var1);

    public abstract boolean isUpdateEffects();

    public abstract void setUpdateEffects(boolean var1);

    public static final class LivingEntityClass
    extends Template.Class<LivingEntityHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Byte>> DATA_LIVING_FLAGS = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Float>> DATA_HEALTH = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Boolean>> DATA_PARTICLES_HIDDEN = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Integer>> DATA_ARROWCOUNT = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<IntVector3>> DATA_BEDPOSITION = new Template.StaticField.Converted();
        public final Template.Field.Converted<Map<Holder<MobEffectHandle>, MobEffectInstanceHandle>> mobEffects = new Template.Field.Converted();
        public final Template.Field.Float lastDamage = new Template.Field.Float();
        public final Template.Field.Boolean updateEffects = new Template.Field.Boolean();
        public final Template.Method<Void> resetAttributes = new Template.Method();
        public final Template.Method<Void> detectEquipmentChanges = new Template.Method();
        public final Template.Method.Converted<Void> loadEquipment = new Template.Method.Converted();
        public final Template.Method.Converted<CommonTagCompound> saveEquipment = new Template.Method.Converted();
        public final Template.Method.Converted<AttributeMapHandle> getAttributeMap = new Template.Method.Converted();
        public final Template.Method.Converted<AttributeInstanceHandle> getAttribute = new Template.Method.Converted();
        public final Template.Method.Converted<Vector> getMoveIntent = new Template.Method.Converted();
        public final Template.Method.Converted<Collection<MobEffectInstanceHandle>> getEffects = new Template.Method.Converted();
        public final Template.Method.Converted<ItemStack> getEquipment = new Template.Method.Converted();
        public final Template.Method<Float> getHealth = new Template.Method();
        public final Template.Method<Float> getMaxHealth = new Template.Method();
        public final Template.Method<Float> getAbsorptionAmount = new Template.Method();
        public final Template.Method<Void> setAbsorptionAmount = new Template.Method();
    }
}

