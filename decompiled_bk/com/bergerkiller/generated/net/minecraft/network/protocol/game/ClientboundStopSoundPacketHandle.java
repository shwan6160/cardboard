/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundStopSoundPacket")
public abstract class ClientboundStopSoundPacketHandle
extends PacketHandle {
    public static final ClientboundStopSoundPacketClass T = Template.Class.create(ClientboundStopSoundPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundStopSoundPacketHandle createHandle(Object handleInstance) {
        return (ClientboundStopSoundPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundStopSoundPacketHandle createNew(ResourceKey<SoundEffect> soundEffect, String category) {
        return ClientboundStopSoundPacketHandle.T.createNew.invoke(soundEffect, category);
    }

    public static final class ClientboundStopSoundPacketClass
    extends Template.Class<ClientboundStopSoundPacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundStopSoundPacketHandle> createNew = new Template.StaticMethod.Converted();
    }
}

