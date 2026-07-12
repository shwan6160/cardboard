/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.wrappers.BlockStateChange;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket")
public abstract class ClientboundLevelChunkWithLightPacketHandle
extends PacketHandle {
    public static final ClientboundLevelChunkWithLightPacketClass T = Template.Class.create(ClientboundLevelChunkWithLightPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundLevelChunkWithLightPacketHandle createHandle(Object handleInstance) {
        return (ClientboundLevelChunkWithLightPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundLevelChunkWithLightPacketHandle createNew() {
        return ClientboundLevelChunkWithLightPacketHandle.T.createNew.invoke();
    }

    public abstract CommonTagCompound getHeightmaps();

    public abstract void setHeightmaps(CommonTagCompound var1);

    public abstract byte[] getBuffer();

    public abstract void setBuffer(byte[] var1);

    public abstract List<BlockStateChange> getBlockStates();

    public void setBlockStates(List<BlockStateChange> states) {
        int i;
        List<BlockStateChange> baseStates = this.getBlockStates();
        int count = states.size();
        int limit = Math.min(count, baseStates.size());
        for (i = 0; i < limit; ++i) {
            BlockStateChange change = states.get(i);
            if (baseStates.get(i) == change) continue;
            baseStates.set(i, change);
        }
        for (i = limit; i < count; ++i) {
            baseStates.add(states.get(i));
        }
        while (baseStates.size() > count) {
            baseStates.remove(baseStates.size() - 1);
        }
    }

    public abstract int getX();

    public abstract void setX(int var1);

    public abstract int getZ();

    public abstract void setZ(int var1);

    public static final class ClientboundLevelChunkWithLightPacketClass
    extends Template.Class<ClientboundLevelChunkWithLightPacketHandle> {
        public final Template.Field.Integer x = new Template.Field.Integer();
        public final Template.Field.Integer z = new Template.Field.Integer();
        public final Template.StaticMethod.Converted<ClientboundLevelChunkWithLightPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method.Converted<CommonTagCompound> getHeightmaps = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setHeightmaps = new Template.Method.Converted();
        public final Template.Method<byte[]> getBuffer = new Template.Method();
        public final Template.Method<Void> setBuffer = new Template.Method();
        public final Template.Method<List<BlockStateChange>> getBlockStates = new Template.Method();
    }
}

