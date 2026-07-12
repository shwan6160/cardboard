/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.common;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.common.ServerboundClientInformationPacket")
public abstract class ServerboundClientInformationPacketHandle
extends PacketHandle {
    public static final ServerboundClientInformationPacketClass T = Template.Class.create(ServerboundClientInformationPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ServerboundClientInformationPacketHandle createHandle(Object handleInstance) {
        return (ServerboundClientInformationPacketHandle)T.createHandle(handleInstance);
    }

    public abstract String getLocale();

    public abstract int getView();

    public abstract Object getChatVisibility();

    public abstract boolean getEnableColors();

    public abstract int getModelPartFlags();

    public abstract HumanHand getMainHand();

    public static final class ServerboundClientInformationPacketClass
    extends Template.Class<ServerboundClientInformationPacketHandle> {
        public final Template.Method<String> getLocale = new Template.Method();
        public final Template.Method<Integer> getView = new Template.Method();
        public final Template.Method<Object> getChatVisibility = new Template.Method();
        public final Template.Method<Boolean> getEnableColors = new Template.Method();
        public final Template.Method<Integer> getModelPartFlags = new Template.Method();
        public final Template.Method.Converted<HumanHand> getMainHand = new Template.Method.Converted();
    }
}

