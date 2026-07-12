/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.WindowType;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundOpenScreenPacket")
public abstract class ClientboundOpenScreenPacketHandle
extends PacketHandle {
    public static final ClientboundOpenScreenPacketClass T = Template.Class.create(ClientboundOpenScreenPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundOpenScreenPacketHandle createHandle(Object handleInstance) {
        return (ClientboundOpenScreenPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundOpenScreenPacketHandle createNew() {
        return ClientboundOpenScreenPacketHandle.T.createNew.invoke();
    }

    public abstract WindowType getWindowType();

    public abstract void setWindowType(WindowType var1);

    public abstract int getWindowId();

    public abstract void setWindowId(int var1);

    public abstract ChatText getWindowTitle();

    public abstract void setWindowTitle(ChatText var1);

    public static final class ClientboundOpenScreenPacketClass
    extends Template.Class<ClientboundOpenScreenPacketHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
        public final Template.Field.Converted<ChatText> windowTitle = new Template.Field.Converted();
        public final Template.StaticMethod.Converted<ClientboundOpenScreenPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<WindowType> getWindowType = new Template.Method();
        public final Template.Method<Void> setWindowType = new Template.Method();
    }
}

