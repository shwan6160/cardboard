/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket")
public abstract class ClientboundRemoveEntitiesPacketHandle
extends PacketHandle {
    public static final ClientboundRemoveEntitiesPacketClass T = Template.Class.create(ClientboundRemoveEntitiesPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundRemoveEntitiesPacketHandle createHandle(Object handleInstance) {
        return (ClientboundRemoveEntitiesPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundRemoveEntitiesPacketHandle createNewSingle(int entityId) {
        return ClientboundRemoveEntitiesPacketHandle.T.createNewSingle.invoke(entityId);
    }

    public static ClientboundRemoveEntitiesPacketHandle createNewMultiple(int[] multipleEntityIds) {
        return ClientboundRemoveEntitiesPacketHandle.T.createNewMultiple.invoke(multipleEntityIds);
    }

    public abstract boolean canSupportMultipleEntityIds();

    public abstract boolean hasMultipleEntityIds();

    public abstract int getSingleEntityId();

    public abstract int[] getEntityIds();

    public abstract void setSingleEntityId(int var1);

    public abstract void setMultipleEntityIds(int[] var1);

    public static boolean canDestroyMultiple() {
        return CommonCapabilities.PACKET_DESTROY_MULTIPLE;
    }

    public static final class ClientboundRemoveEntitiesPacketClass
    extends Template.Class<ClientboundRemoveEntitiesPacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundRemoveEntitiesPacketHandle> createNewSingle = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<ClientboundRemoveEntitiesPacketHandle> createNewMultiple = new Template.StaticMethod.Converted();
        public final Template.Method<Boolean> canSupportMultipleEntityIds = new Template.Method();
        public final Template.Method<Boolean> hasMultipleEntityIds = new Template.Method();
        public final Template.Method<Integer> getSingleEntityId = new Template.Method();
        public final Template.Method<int[]> getEntityIds = new Template.Method();
        public final Template.Method<Void> setSingleEntityId = new Template.Method();
        public final Template.Method<Void> setMultipleEntityIds = new Template.Method();
    }
}

