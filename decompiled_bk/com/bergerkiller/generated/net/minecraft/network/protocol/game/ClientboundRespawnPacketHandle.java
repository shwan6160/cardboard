/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Difficulty
 *  org.bukkit.GameMode
 *  org.bukkit.World
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundRespawnPacket")
public abstract class ClientboundRespawnPacketHandle
extends PacketHandle {
    public static final ClientboundRespawnPacketClass T = Template.Class.create(ClientboundRespawnPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundRespawnPacketHandle createHandle(Object handleInstance) {
        return (ClientboundRespawnPacketHandle)T.createHandle(handleInstance);
    }

    public abstract DimensionType getDimensionType();

    public abstract GameMode getGamemode();

    public abstract ResourceKey<World> getWorldName();

    public abstract GameMode getPreviousGameMode();

    public static final class ClientboundRespawnPacketClass
    extends Template.Class<ClientboundRespawnPacketHandle> {
        @Template.Optional
        public final Template.Field.Converted<Difficulty> difficulty = new Template.Field.Converted();
        public final Template.Method.Converted<DimensionType> getDimensionType = new Template.Method.Converted();
        public final Template.Method.Converted<GameMode> getGamemode = new Template.Method.Converted();
        public final Template.Method.Converted<ResourceKey<World>> getWorldName = new Template.Method.Converted();
        public final Template.Method.Converted<GameMode> getPreviousGameMode = new Template.Method.Converted();
    }
}

