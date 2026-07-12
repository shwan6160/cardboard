/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundSetPassengersPacket")
public abstract class ClientboundSetPassengersPacketHandle
extends PacketHandle {
    public static final ClientboundSetPassengersPacketClass T = Template.Class.create(ClientboundSetPassengersPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundSetPassengersPacketHandle createHandle(Object handleInstance) {
        return (ClientboundSetPassengersPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundSetPassengersPacketHandle createNew() {
        return ClientboundSetPassengersPacketHandle.T.createNew.invoke();
    }

    public void addMountedEntityId(int entityId) {
        int[] oldIds = this.getMountedEntityIds();
        if (oldIds == null || oldIds.length == 0) {
            this.setMountedEntityIds(new int[]{entityId});
        } else {
            int[] newIds = new int[oldIds.length + 1];
            for (int i = 0; i < oldIds.length; ++i) {
                newIds[i] = oldIds[i];
            }
            newIds[newIds.length - 1] = entityId;
            this.setMountedEntityIds(newIds);
        }
    }

    public static ClientboundSetPassengersPacketHandle createNew(int entityId, int[] mountedEntityIds) {
        ClientboundSetPassengersPacketHandle handle = ClientboundSetPassengersPacketHandle.createNew();
        handle.setEntityId(entityId);
        handle.setMountedEntityIds(mountedEntityIds);
        return handle;
    }

    public abstract int getEntityId();

    public abstract void setEntityId(int var1);

    public abstract int[] getMountedEntityIds();

    public abstract void setMountedEntityIds(int[] var1);

    public static final class ClientboundSetPassengersPacketClass
    extends Template.Class<ClientboundSetPassengersPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field<int[]> mountedEntityIds = new Template.Field();
        public final Template.StaticMethod.Converted<ClientboundSetPassengersPacketHandle> createNew = new Template.StaticMethod.Converted();
    }
}

