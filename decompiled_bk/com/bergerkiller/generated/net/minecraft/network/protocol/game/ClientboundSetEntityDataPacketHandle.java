/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket")
public abstract class ClientboundSetEntityDataPacketHandle
extends PacketHandle {
    public static final ClientboundSetEntityDataPacketClass T = Template.Class.create(ClientboundSetEntityDataPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundSetEntityDataPacketHandle createHandle(Object handleInstance) {
        return (ClientboundSetEntityDataPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundSetEntityDataPacketHandle createForSpawn(int entityId, DataWatcher datawatcher) {
        return ClientboundSetEntityDataPacketHandle.T.createForSpawn.invoke(entityId, datawatcher);
    }

    public static ClientboundSetEntityDataPacketHandle createForChanges(int entityId, DataWatcher datawatcher) {
        return ClientboundSetEntityDataPacketHandle.T.createForChanges.invoke(entityId, datawatcher);
    }

    public static ClientboundSetEntityDataPacketHandle createNew(int entityId, DataWatcher datawatcher, boolean includeUnchangedData) {
        if (includeUnchangedData) {
            return ClientboundSetEntityDataPacketHandle.createForSpawn(entityId, datawatcher);
        }
        return ClientboundSetEntityDataPacketHandle.createForChanges(entityId, datawatcher);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.OUT_ENTITY_METADATA;
    }

    public abstract int getEntityId();

    public abstract void setEntityId(int var1);

    public abstract List<DataWatcher.PackedItem<Object>> getMetadataItems();

    public abstract void setMetadataItems(List<DataWatcher.PackedItem<Object>> var1);

    public static final class ClientboundSetEntityDataPacketClass
    extends Template.Class<ClientboundSetEntityDataPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Converted<List<DataWatcher.PackedItem<Object>>> metadataItems = new Template.Field.Converted();
        public final Template.StaticMethod.Converted<ClientboundSetEntityDataPacketHandle> createForSpawn = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<ClientboundSetEntityDataPacketHandle> createForChanges = new Template.StaticMethod.Converted();
    }
}

