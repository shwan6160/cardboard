/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket")
public abstract class ClientboundSetDefaultSpawnPositionPacketHandle
extends PacketHandle {
    public static final ClientboundSetDefaultSpawnPositionPacketClass T = Template.Class.create(ClientboundSetDefaultSpawnPositionPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundSetDefaultSpawnPositionPacketHandle createHandle(Object handleInstance) {
        return (ClientboundSetDefaultSpawnPositionPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundSetDefaultSpawnPositionPacketHandle createNew(ServerPlayerHandle.RespawnConfigHandle spawnConfig) {
        return ClientboundSetDefaultSpawnPositionPacketHandle.T.createNew.invoke(spawnConfig);
    }

    public abstract ServerPlayerHandle.RespawnConfigHandle getSpawn();

    public static final class ClientboundSetDefaultSpawnPositionPacketClass
    extends Template.Class<ClientboundSetDefaultSpawnPositionPacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundSetDefaultSpawnPositionPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method.Converted<ServerPlayerHandle.RespawnConfigHandle> getSpawn = new Template.Method.Converted();
    }
}

