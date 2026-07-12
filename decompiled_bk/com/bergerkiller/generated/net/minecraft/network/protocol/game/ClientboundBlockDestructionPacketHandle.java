/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket")
public abstract class ClientboundBlockDestructionPacketHandle
extends PacketHandle {
    public static final ClientboundBlockDestructionPacketClass T = Template.Class.create(ClientboundBlockDestructionPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundBlockDestructionPacketHandle createHandle(Object handleInstance) {
        return (ClientboundBlockDestructionPacketHandle)T.createHandle(handleInstance);
    }

    public abstract int getId();

    public abstract void setId(int var1);

    public abstract IntVector3 getPosition();

    public abstract void setPosition(IntVector3 var1);

    public abstract int getProgress();

    public abstract void setProgress(int var1);

    public static final class ClientboundBlockDestructionPacketClass
    extends Template.Class<ClientboundBlockDestructionPacketHandle> {
        public final Template.Field.Integer id = new Template.Field.Integer();
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted();
        public final Template.Field.Integer progress = new Template.Field.Integer();
    }
}

