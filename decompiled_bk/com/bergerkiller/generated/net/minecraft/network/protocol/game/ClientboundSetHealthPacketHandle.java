/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundSetHealthPacket")
public abstract class ClientboundSetHealthPacketHandle
extends PacketHandle {
    public static final ClientboundSetHealthPacketClass T = Template.Class.create(ClientboundSetHealthPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundSetHealthPacketHandle createHandle(Object handleInstance) {
        return (ClientboundSetHealthPacketHandle)T.createHandle(handleInstance);
    }

    public abstract float getHealth();

    public abstract void setHealth(float var1);

    public abstract int getFood();

    public abstract void setFood(int var1);

    public abstract float getFoodSaturation();

    public abstract void setFoodSaturation(float var1);

    public static final class ClientboundSetHealthPacketClass
    extends Template.Class<ClientboundSetHealthPacketHandle> {
        public final Template.Field.Float health = new Template.Field.Float();
        public final Template.Field.Integer food = new Template.Field.Integer();
        public final Template.Field.Float foodSaturation = new Template.Field.Float();
    }
}

