/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.BlockStateType;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData")
public abstract class ClientboundLevelChunkPacketDataHandle
extends Template.Handle {
    public static final ClientboundLevelChunkPacketDataClass T = Template.Class.create(ClientboundLevelChunkPacketDataClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundLevelChunkPacketDataHandle createHandle(Object handleInstance) {
        return (ClientboundLevelChunkPacketDataHandle)T.createHandle(handleInstance);
    }

    public static final class ClientboundLevelChunkPacketDataClass
    extends Template.Class<ClientboundLevelChunkPacketDataHandle> {
    }

    @Template.Optional
    @Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData.BlockEntityInfo")
    public static abstract class BlockEntityInfoHandle
    extends Template.Handle {
        public static final BlockEntityInfoClass T = Template.Class.create(BlockEntityInfoClass.class, Common.TEMPLATE_RESOLVER);

        public static BlockEntityInfoHandle createHandle(Object handleInstance) {
            return (BlockEntityInfoHandle)T.createHandle(handleInstance);
        }

        public static Object encodeRaw(IntVector3 position, BlockStateType type, CommonTagCompound tag) {
            return BlockEntityInfoHandle.T.encodeRaw.invoke(position, type, tag);
        }

        public abstract IntVector3 getPosition(int var1, int var2);

        public abstract BlockStateType getType();

        public abstract void setType(BlockStateType var1);

        public abstract CommonTagCompound getTag();

        public abstract void setTag(CommonTagCompound var1);

        public static final class BlockEntityInfoClass
        extends Template.Class<BlockEntityInfoHandle> {
            public final Template.Field.Converted<BlockStateType> type = new Template.Field.Converted();
            public final Template.Field.Converted<CommonTagCompound> tag = new Template.Field.Converted();
            public final Template.StaticMethod.Converted<Object> encodeRaw = new Template.StaticMethod.Converted();
            public final Template.Method<IntVector3> getPosition = new Template.Method();
        }
    }
}

