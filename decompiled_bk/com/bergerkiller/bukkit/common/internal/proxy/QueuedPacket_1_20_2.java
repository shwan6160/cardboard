/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.generated.net.minecraft.network.ConnectionHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.function.Consumer;

public class QueuedPacket_1_20_2
implements Consumer<Object> {
    private final Object packet;
    private final Object packetsendlistener;
    private final boolean flush;

    public QueuedPacket_1_20_2(Object packet, Object packetsendlistener, boolean flush) {
        this.packet = packet;
        this.packetsendlistener = packetsendlistener;
        this.flush = flush;
    }

    @Override
    public void accept(Object networkmanager) {
        ((Template.Method)ConnectionHandle.T.queue_sendPacketImpl.raw).invoke(networkmanager, this.packet, this.packetsendlistener, this.flush);
    }
}

