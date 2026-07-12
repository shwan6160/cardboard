/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.entity.Entity;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket")
public abstract class ClientboundSetEntityLinkPacketHandle
extends PacketHandle {
    public static final ClientboundSetEntityLinkPacketClass T = Template.Class.create(ClientboundSetEntityLinkPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundSetEntityLinkPacketHandle createHandle(Object handleInstance) {
        return (ClientboundSetEntityLinkPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundSetEntityLinkPacketHandle createNew() {
        return ClientboundSetEntityLinkPacketHandle.T.createNew.invoke();
    }

    public static ClientboundSetEntityLinkPacketHandle createNewMount(Entity passengerEntity, Entity vehicleEntity) {
        return ClientboundSetEntityLinkPacketHandle.T.createNewMount.invoke(passengerEntity, vehicleEntity);
    }

    public static ClientboundSetEntityLinkPacketHandle createNewLeash(Entity leashedEntity, Entity holderEntity) {
        return ClientboundSetEntityLinkPacketHandle.T.createNewLeash.invoke(leashedEntity, holderEntity);
    }

    public abstract boolean isLeash();

    public abstract void setIsLeash(boolean var1);

    public static ClientboundSetEntityLinkPacketHandle createNewLeash(int leashedEntityId, int holderEntityId) {
        ClientboundSetEntityLinkPacketHandle packet = ClientboundSetEntityLinkPacketHandle.createNew();
        packet.setVehicleId(holderEntityId);
        packet.setPassengerId(leashedEntityId);
        packet.setIsLeash(true);
        return packet;
    }

    public static ClientboundSetEntityLinkPacketHandle createNewMount(int passengerEntityId, int vehicleEntityId) {
        if (!ClientboundSetEntityLinkPacketHandle.T.leashId.isAvailable()) {
            throw new UnsupportedOperationException("Not supported >= MC 1.9, use Mount packet instead");
        }
        ClientboundSetEntityLinkPacketHandle packet = ClientboundSetEntityLinkPacketHandle.createNew();
        packet.setVehicleId(vehicleEntityId);
        packet.setPassengerId(passengerEntityId);
        packet.setIsLeash(false);
        return packet;
    }

    public abstract int getPassengerId();

    public abstract void setPassengerId(int var1);

    public abstract int getVehicleId();

    public abstract void setVehicleId(int var1);

    public static final class ClientboundSetEntityLinkPacketClass
    extends Template.Class<ClientboundSetEntityLinkPacketHandle> {
        @Template.Optional
        public final Template.Field.Integer leashId = new Template.Field.Integer();
        public final Template.Field.Integer passengerId = new Template.Field.Integer();
        public final Template.Field.Integer vehicleId = new Template.Field.Integer();
        public final Template.StaticMethod.Converted<ClientboundSetEntityLinkPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<ClientboundSetEntityLinkPacketHandle> createNewMount = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<ClientboundSetEntityLinkPacketHandle> createNewLeash = new Template.StaticMethod.Converted();
        public final Template.Method<Boolean> isLeash = new Template.Method();
        public final Template.Method<Void> setIsLeash = new Template.Method();
    }
}

