/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket")
public abstract class ClientboundBlockUpdatePacketHandle
extends PacketHandle {
    public static final ClientboundBlockUpdatePacketClass T = Template.Class.create(ClientboundBlockUpdatePacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundBlockUpdatePacketHandle createHandle(Object handleInstance) {
        return (ClientboundBlockUpdatePacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundBlockUpdatePacketHandle createNewNull() {
        return ClientboundBlockUpdatePacketHandle.T.createNewNull.invoke();
    }

    public static ClientboundBlockUpdatePacketHandle createNew(IntVector3 position, BlockData blockData) {
        return ClientboundBlockUpdatePacketHandle.T.createNew.invoke(position, blockData);
    }

    public abstract IntVector3 getPosition();

    public abstract void setPosition(IntVector3 var1);

    public abstract BlockData getBlockData();

    public abstract void setBlockData(BlockData var1);

    public static final class ClientboundBlockUpdatePacketClass
    extends Template.Class<ClientboundBlockUpdatePacketHandle> {
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted();
        public final Template.Field.Converted<BlockData> blockData = new Template.Field.Converted();
        public final Template.StaticMethod.Converted<ClientboundBlockUpdatePacketHandle> createNewNull = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<ClientboundBlockUpdatePacketHandle> createNew = new Template.StaticMethod.Converted();
    }
}

