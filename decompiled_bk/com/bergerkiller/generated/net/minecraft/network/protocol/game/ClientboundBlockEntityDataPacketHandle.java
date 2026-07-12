/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.BlockStateType;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket")
public abstract class ClientboundBlockEntityDataPacketHandle
extends PacketHandle {
    public static final ClientboundBlockEntityDataPacketClass T = Template.Class.create(ClientboundBlockEntityDataPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundBlockEntityDataPacketHandle createHandle(Object handleInstance) {
        return (ClientboundBlockEntityDataPacketHandle)T.createHandle(handleInstance);
    }

    public static final ClientboundBlockEntityDataPacketHandle createNew(IntVector3 blockPosition, BlockStateType type, CommonTagCompound data) {
        return ClientboundBlockEntityDataPacketHandle.T.constr_blockPosition_type_data.newInstance(blockPosition, type, data);
    }

    public abstract IntVector3 getPosition();

    public abstract void setPosition(IntVector3 var1);

    public abstract BlockStateType getType();

    public abstract void setType(BlockStateType var1);

    public abstract CommonTagCompound getData();

    public abstract void setData(CommonTagCompound var1);

    public static final class ClientboundBlockEntityDataPacketClass
    extends Template.Class<ClientboundBlockEntityDataPacketHandle> {
        public final Template.Constructor.Converted<ClientboundBlockEntityDataPacketHandle> constr_blockPosition_type_data = new Template.Constructor.Converted();
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted();
        public final Template.Field.Converted<BlockStateType> type = new Template.Field.Converted();
        public final Template.Field.Converted<CommonTagCompound> data = new Template.Field.Converted();
    }
}

