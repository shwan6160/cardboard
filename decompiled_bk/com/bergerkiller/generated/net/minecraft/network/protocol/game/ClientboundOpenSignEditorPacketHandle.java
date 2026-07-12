/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket")
public abstract class ClientboundOpenSignEditorPacketHandle
extends PacketHandle {
    public static final ClientboundOpenSignEditorPacketClass T = Template.Class.create(ClientboundOpenSignEditorPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundOpenSignEditorPacketHandle createHandle(Object handleInstance) {
        return (ClientboundOpenSignEditorPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundOpenSignEditorPacketHandle createNew(IntVector3 signPosition, boolean isFrontText) {
        return ClientboundOpenSignEditorPacketHandle.T.createNew.invoke(signPosition, isFrontText);
    }

    public abstract boolean isFrontText();

    public abstract void setFrontText(boolean var1);

    public static ClientboundOpenSignEditorPacketHandle createNew(IntVector3 signPosition) {
        return ClientboundOpenSignEditorPacketHandle.createNew(signPosition, true);
    }

    public abstract IntVector3 getSignPosition();

    public abstract void setSignPosition(IntVector3 var1);

    public static final class ClientboundOpenSignEditorPacketClass
    extends Template.Class<ClientboundOpenSignEditorPacketHandle> {
        public final Template.Field.Converted<IntVector3> signPosition = new Template.Field.Converted();
        public final Template.StaticMethod.Converted<ClientboundOpenSignEditorPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<Boolean> isFrontText = new Template.Method();
        public final Template.Method<Void> setFrontText = new Template.Method();
    }
}

