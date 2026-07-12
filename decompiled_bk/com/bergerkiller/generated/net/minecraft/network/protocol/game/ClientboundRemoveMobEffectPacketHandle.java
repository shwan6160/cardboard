/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.potion.PotionEffectType
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.potion.PotionEffectType;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket")
public abstract class ClientboundRemoveMobEffectPacketHandle
extends PacketHandle {
    public static final ClientboundRemoveMobEffectPacketClass T = Template.Class.create(ClientboundRemoveMobEffectPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundRemoveMobEffectPacketHandle createHandle(Object handleInstance) {
        return (ClientboundRemoveMobEffectPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundRemoveMobEffectPacketHandle createNew(int entityId, Holder<MobEffectHandle> effect) {
        return ClientboundRemoveMobEffectPacketHandle.T.createNew.invoke(entityId, effect);
    }

    public abstract int getEntityId();

    public abstract Holder<MobEffectHandle> getEffect();

    public PotionEffectType getPotionEffectType() {
        return MobEffectHandle.holderToBukkit(this.getEffect());
    }

    public static ClientboundRemoveMobEffectPacketHandle createNew(int entityId, PotionEffectType effectType) {
        return ClientboundRemoveMobEffectPacketHandle.createNew(entityId, MobEffectHandle.holderFromBukkit(effectType));
    }

    public static final class ClientboundRemoveMobEffectPacketClass
    extends Template.Class<ClientboundRemoveMobEffectPacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundRemoveMobEffectPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<Integer> getEntityId = new Template.Method();
        public final Template.Method.Converted<Holder<MobEffectHandle>> getEffect = new Template.Method.Converted();
    }
}

