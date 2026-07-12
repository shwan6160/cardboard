/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket")
public abstract class ClientboundUpdateMobEffectPacketHandle
extends PacketHandle {
    public static final ClientboundUpdateMobEffectPacketClass T = Template.Class.create(ClientboundUpdateMobEffectPacketClass.class, Common.TEMPLATE_RESOLVER);
    public static final int FLAG_AMBIENT = 1;
    public static final int FLAG_VISIBLE = 2;
    public static final int FLAG_SHOW_ICON = 4;

    public static ClientboundUpdateMobEffectPacketHandle createHandle(Object handleInstance) {
        return (ClientboundUpdateMobEffectPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundUpdateMobEffectPacketHandle createNew(int entityId, PotionEffect effect, boolean blend) {
        return ClientboundUpdateMobEffectPacketHandle.T.createNew.invoke(entityId, effect, blend);
    }

    public abstract int getEffectAmplifier();

    public abstract void setEffectAmplifier(int var1);

    public abstract Holder<MobEffectHandle> getEffect();

    public abstract void setEffect(Holder<MobEffectHandle> var1);

    public PotionEffectType getPotionEffectType() {
        return MobEffectHandle.holderToBukkit(this.getEffect());
    }

    public void setPotionEffectType(PotionEffectType effectType) {
        this.setEffect(MobEffectHandle.holderFromBukkit(effectType));
    }

    public static ClientboundUpdateMobEffectPacketHandle createNew(int entityId, PotionEffect effect) {
        return ClientboundUpdateMobEffectPacketHandle.createNew(entityId, effect, false);
    }

    public abstract int getEntityId();

    public abstract void setEntityId(int var1);

    public abstract int getEffectDurationTicks();

    public abstract void setEffectDurationTicks(int var1);

    public abstract byte getFlags();

    public abstract void setFlags(byte var1);

    public static final class ClientboundUpdateMobEffectPacketClass
    extends Template.Class<ClientboundUpdateMobEffectPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Integer effectDurationTicks = new Template.Field.Integer();
        public final Template.Field.Byte flags = new Template.Field.Byte();
        public final Template.StaticMethod.Converted<ClientboundUpdateMobEffectPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<Integer> getEffectAmplifier = new Template.Method();
        public final Template.Method<Void> setEffectAmplifier = new Template.Method();
        public final Template.Method.Converted<Holder<MobEffectHandle>> getEffect = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setEffect = new Template.Method.Converted();
    }
}

