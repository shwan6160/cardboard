/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.potion.PotionEffect
 */
package com.bergerkiller.generated.net.minecraft.world.effect;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.potion.PotionEffect;

@Template.InstanceType(value="net.minecraft.world.effect.MobEffectInstance")
public abstract class MobEffectInstanceHandle
extends Template.Handle {
    public static final MobEffectInstanceClass T = Template.Class.create(MobEffectInstanceClass.class, Common.TEMPLATE_RESOLVER);

    public static MobEffectInstanceHandle createHandle(Object handleInstance) {
        return (MobEffectInstanceHandle)T.createHandle(handleInstance);
    }

    public static MobEffectInstanceHandle fromNBT(CommonTagCompound compound) {
        return MobEffectInstanceHandle.T.fromNBT.invoke(compound);
    }

    public static MobEffectInstanceHandle fromBukkit(PotionEffect effect) {
        return MobEffectInstanceHandle.T.fromBukkit.invoke(effect);
    }

    public abstract PotionEffect toBukkit();

    public abstract Holder<MobEffectHandle> getEffectList();

    public abstract void setEffectList(Holder<MobEffectHandle> var1);

    public abstract int getDuration();

    public abstract void setDuration(int var1);

    public abstract int getAmplifier();

    public abstract void setAmplifier(int var1);

    public abstract boolean isAmbient();

    public abstract void setAmbient(boolean var1);

    public abstract boolean isParticles();

    public abstract void setParticles(boolean var1);

    public static final class MobEffectInstanceClass
    extends Template.Class<MobEffectInstanceHandle> {
        public final Template.Field.Converted<Holder<MobEffectHandle>> effectList = new Template.Field.Converted();
        public final Template.Field.Integer duration = new Template.Field.Integer();
        public final Template.Field.Integer amplifier = new Template.Field.Integer();
        public final Template.Field.Boolean ambient = new Template.Field.Boolean();
        public final Template.Field.Boolean particles = new Template.Field.Boolean();
        public final Template.StaticMethod.Converted<MobEffectInstanceHandle> fromNBT = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<MobEffectInstanceHandle> fromBukkit = new Template.StaticMethod.Converted();
        public final Template.Method<PotionEffect> toBukkit = new Template.Method();
    }
}

