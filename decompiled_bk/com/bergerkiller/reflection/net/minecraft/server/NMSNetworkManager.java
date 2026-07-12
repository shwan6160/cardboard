/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 */
package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.network.ConnectionHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import io.netty.channel.Channel;

@Deprecated
public class NMSNetworkManager {
    public static final ClassTemplate<?> T = ClassTemplate.create(ConnectionHandle.T.getType());
    public static final FieldAccessor<Channel> channel = ConnectionHandle.T.channel.toFieldAccessor();
    public static final MethodAccessor<Boolean> getIsOpen = ConnectionHandle.T.isConnected.toMethodAccessor();
}

