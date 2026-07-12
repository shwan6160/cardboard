/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 */
package com.bergerkiller.generated.net.minecraft.network;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import io.netty.channel.Channel;

@Template.InstanceType(value="net.minecraft.network.Connection")
public abstract class ConnectionHandle
extends Template.Handle {
    public static final ConnectionClass T = Template.Class.create(ConnectionClass.class, Common.TEMPLATE_RESOLVER);

    public static ConnectionHandle createHandle(Object handleInstance) {
        return (ConnectionHandle)T.createHandle(handleInstance);
    }

    public static boolean queuePacketUnsafe(Object networkManager, Object packet) {
        return ConnectionHandle.T.queuePacketUnsafe.invoke(networkManager, packet);
    }

    public abstract boolean isConnected();

    public abstract void queue_sendPacketImpl(Object var1, Object var2, boolean var3);

    public abstract Channel getChannel();

    public abstract void setChannel(Channel var1);

    public static final class ConnectionClass
    extends Template.Class<ConnectionHandle> {
        public final Template.Field<Channel> channel = new Template.Field();
        public final Template.StaticMethod.Converted<Boolean> queuePacketUnsafe = new Template.StaticMethod.Converted();
        public final Template.Method<Boolean> isConnected = new Template.Method();
        public final Template.Method.Converted<Void> queue_sendPacketImpl = new Template.Method.Converted();
    }
}

