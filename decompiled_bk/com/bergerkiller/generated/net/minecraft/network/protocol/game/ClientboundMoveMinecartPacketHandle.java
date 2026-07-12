/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.NewMinecartBehaviorHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;

@Template.Optional
@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundMoveMinecartPacket")
public abstract class ClientboundMoveMinecartPacketHandle
extends PacketHandle {
    public static final ClientboundMoveMinecartPacketClass T = Template.Class.create(ClientboundMoveMinecartPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundMoveMinecartPacketHandle createHandle(Object handleInstance) {
        return (ClientboundMoveMinecartPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundMoveMinecartPacketHandle createNew(int entityId, List<NewMinecartBehaviorHandle.MinecartStepHandle> lerpSteps) {
        return ClientboundMoveMinecartPacketHandle.T.createNew.invoke(entityId, lerpSteps);
    }

    public abstract int getEntityId();

    public abstract List<NewMinecartBehaviorHandle.MinecartStepHandle> getLerpSteps();

    public static final class ClientboundMoveMinecartPacketClass
    extends Template.Class<ClientboundMoveMinecartPacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundMoveMinecartPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<Integer> getEntityId = new Template.Method();
        public final Template.Method.Converted<List<NewMinecartBehaviorHandle.MinecartStepHandle>> getLerpSteps = new Template.Method.Converted();
    }
}

