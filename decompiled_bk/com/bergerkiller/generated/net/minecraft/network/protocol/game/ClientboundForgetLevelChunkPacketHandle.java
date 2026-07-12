/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket")
public abstract class ClientboundForgetLevelChunkPacketHandle
extends PacketHandle {
    public static final ClientboundForgetLevelChunkPacketClass T = Template.Class.create(ClientboundForgetLevelChunkPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundForgetLevelChunkPacketHandle createHandle(Object handleInstance) {
        return (ClientboundForgetLevelChunkPacketHandle)T.createHandle(handleInstance);
    }

    public abstract int getCx();

    public abstract int getCz();

    public abstract void setChunk(int var1, int var2);

    public void setCx(int cx) {
        this.setChunk(cx, this.getCz());
    }

    public void setCz(int cz) {
        this.setChunk(this.getCx(), cz);
    }

    public static final class ClientboundForgetLevelChunkPacketClass
    extends Template.Class<ClientboundForgetLevelChunkPacketHandle> {
        public final Template.Method<Integer> getCx = new Template.Method();
        public final Template.Method<Integer> getCz = new Template.Method();
        public final Template.Method<Void> setChunk = new Template.Method();
    }
}

