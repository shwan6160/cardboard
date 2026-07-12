/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.potion.PotionEffectType
 */
package com.bergerkiller.generated.net.minecraft.world.effect;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.potion.PotionEffectType;

@Template.InstanceType(value="net.minecraft.world.effect.MobEffect")
public abstract class MobEffectHandle
extends Template.Handle {
    public static final MobEffectClass T = Template.Class.create(MobEffectClass.class, Common.TEMPLATE_RESOLVER);

    public static MobEffectHandle createHandle(Object handleInstance) {
        return (MobEffectHandle)T.createHandle(handleInstance);
    }

    public static MobEffectHandle fromBukkit(PotionEffectType effectType) {
        return MobEffectHandle.T.fromBukkit.invoke(effectType);
    }

    public static Holder<MobEffectHandle> holderFromBukkit(PotionEffectType effectType) {
        return MobEffectHandle.T.holderFromBukkit.invoke(effectType);
    }

    public static PotionEffectType holderToBukkit(Holder<MobEffectHandle> mobEffectList) {
        return MobEffectHandle.T.holderToBukkit.invoke(mobEffectList);
    }

    public static int getId(MobEffectHandle mobeffectlist) {
        return MobEffectHandle.T.getId.invoke(mobeffectlist);
    }

    public static MobEffectHandle fromId(int id) {
        return MobEffectHandle.T.fromId.invoke(id);
    }

    public abstract PotionEffectType toBukkit();

    public static final class MobEffectClass
    extends Template.Class<MobEffectHandle> {
        public final Template.StaticMethod.Converted<MobEffectHandle> fromBukkit = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<Holder<MobEffectHandle>> holderFromBukkit = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<PotionEffectType> holderToBukkit = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<Integer> getId = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<MobEffectHandle> fromId = new Template.StaticMethod.Converted();
        public final Template.Method<PotionEffectType> toBukkit = new Template.Method();
    }
}

