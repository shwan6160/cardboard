/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Art
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.UUID;
import org.bukkit.Art;
import org.bukkit.block.BlockFace;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundAddPaintingPacket")
public abstract class ClientboundAddPaintingPacketHandle
extends PacketHandle {
    public static final ClientboundAddPaintingPacketClass T = Template.Class.create(ClientboundAddPaintingPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundAddPaintingPacketHandle createHandle(Object handleInstance) {
        return (ClientboundAddPaintingPacketHandle)T.createHandle(handleInstance);
    }

    public static boolean hasArtField() {
        return (Boolean)ClientboundAddPaintingPacketHandle.T.hasArtField.invoker.invoke(null);
    }

    public abstract Art getArt();

    public abstract void setArt(Art var1);

    public abstract IntVector3 getPosition();

    public abstract void setPosition(IntVector3 var1);

    public abstract BlockFace getFacing();

    public abstract void setFacing(BlockFace var1);

    public abstract void setEntityUUID(UUID var1);

    public abstract int getEntityId();

    public abstract void setEntityId(int var1);

    public static final class ClientboundAddPaintingPacketClass
    extends Template.Class<ClientboundAddPaintingPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.StaticMethod<Boolean> hasArtField = new Template.StaticMethod();
        public final Template.Method.Converted<Art> getArt = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setArt = new Template.Method.Converted();
        public final Template.Method.Converted<IntVector3> getPosition = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setPosition = new Template.Method.Converted();
        public final Template.Method.Converted<BlockFace> getFacing = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setFacing = new Template.Method.Converted();
        public final Template.Method<Void> setEntityUUID = new Template.Method();
    }
}

