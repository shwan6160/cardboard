/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.common;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.UUID;

@Template.InstanceType(value="net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket")
public abstract class ClientboundResourcePackPushPacketHandle
extends PacketHandle {
    public static final ClientboundResourcePackPushPacketClass T = Template.Class.create(ClientboundResourcePackPushPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundResourcePackPushPacketHandle createHandle(Object handleInstance) {
        return (ClientboundResourcePackPushPacketHandle)T.createHandle(handleInstance);
    }

    public abstract void setRequired(boolean var1);

    public abstract boolean isRequired();

    public abstract void setPrompt(ChatText var1);

    public abstract ChatText getPrompt();

    public abstract UUID getId();

    public abstract String getUrl();

    public abstract void setUrl(String var1);

    public abstract String getHash();

    public abstract void setHash(String var1);

    public static final class ClientboundResourcePackPushPacketClass
    extends Template.Class<ClientboundResourcePackPushPacketHandle> {
        public final Template.Field<String> url = new Template.Field();
        public final Template.Field<String> hash = new Template.Field();
        public final Template.Method<Void> setRequired = new Template.Method();
        public final Template.Method<Boolean> isRequired = new Template.Method();
        public final Template.Method.Converted<Void> setPrompt = new Template.Method.Converted();
        public final Template.Method.Converted<ChatText> getPrompt = new Template.Method.Converted();
        public final Template.Method<UUID> getId = new Template.Method();
    }
}

