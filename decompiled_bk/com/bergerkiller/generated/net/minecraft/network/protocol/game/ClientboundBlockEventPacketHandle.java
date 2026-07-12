/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Material;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundBlockEventPacket")
public abstract class ClientboundBlockEventPacketHandle
extends PacketHandle {
    public static final ClientboundBlockEventPacketClass T = Template.Class.create(ClientboundBlockEventPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundBlockEventPacketHandle createHandle(Object handleInstance) {
        return (ClientboundBlockEventPacketHandle)T.createHandle(handleInstance);
    }

    public abstract IntVector3 getPosition();

    public abstract void setPosition(IntVector3 var1);

    public abstract int getB0();

    public abstract void setB0(int var1);

    public abstract int getB1();

    public abstract void setB1(int var1);

    public abstract Material getBlock();

    public abstract void setBlock(Material var1);

    public static final class ClientboundBlockEventPacketClass
    extends Template.Class<ClientboundBlockEventPacketHandle> {
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted();
        public final Template.Field.Integer b0 = new Template.Field.Integer();
        public final Template.Field.Integer b1 = new Template.Field.Integer();
        public final Template.Field.Converted<Material> block = new Template.Field.Converted();
    }
}

