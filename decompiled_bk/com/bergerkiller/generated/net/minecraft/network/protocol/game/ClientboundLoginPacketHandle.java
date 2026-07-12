/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Difficulty
 *  org.bukkit.GameMode
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundLoginPacket")
public abstract class ClientboundLoginPacketHandle
extends PacketHandle {
    public static final ClientboundLoginPacketClass T = Template.Class.create(ClientboundLoginPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundLoginPacketHandle createHandle(Object handleInstance) {
        return (ClientboundLoginPacketHandle)T.createHandle(handleInstance);
    }

    public abstract int getPlayerId();

    public abstract boolean isHardcore();

    public abstract int getMaxPlayers();

    public abstract int getViewDistance();

    public abstract boolean isReducedDebugInfo();

    public abstract GameMode getGameMode();

    public abstract DimensionType getDimensionType();

    public abstract GameMode getPreviousGameMode();

    public static final class ClientboundLoginPacketClass
    extends Template.Class<ClientboundLoginPacketHandle> {
        @Template.Optional
        public final Template.Field.Converted<Difficulty> difficulty = new Template.Field.Converted();
        public final Template.Method<Integer> getPlayerId = new Template.Method();
        public final Template.Method<Boolean> isHardcore = new Template.Method();
        public final Template.Method<Integer> getMaxPlayers = new Template.Method();
        public final Template.Method<Integer> getViewDistance = new Template.Method();
        public final Template.Method<Boolean> isReducedDebugInfo = new Template.Method();
        public final Template.Method.Converted<GameMode> getGameMode = new Template.Method.Converted();
        public final Template.Method.Converted<DimensionType> getDimensionType = new Template.Method.Converted();
        public final Template.Method.Converted<GameMode> getPreviousGameMode = new Template.Method.Converted();
    }
}

