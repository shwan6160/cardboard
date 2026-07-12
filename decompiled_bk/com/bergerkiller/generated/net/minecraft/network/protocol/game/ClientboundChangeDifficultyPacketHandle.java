/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Difficulty
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Difficulty;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket")
public abstract class ClientboundChangeDifficultyPacketHandle
extends PacketHandle {
    public static final ClientboundChangeDifficultyPacketClass T = Template.Class.create(ClientboundChangeDifficultyPacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundChangeDifficultyPacketHandle createHandle(Object handleInstance) {
        return (ClientboundChangeDifficultyPacketHandle)T.createHandle(handleInstance);
    }

    public abstract Difficulty getDifficulty();

    public abstract void setDifficulty(Difficulty var1);

    public abstract boolean isHardcore();

    public abstract void setHardcore(boolean var1);

    public static final class ClientboundChangeDifficultyPacketClass
    extends Template.Class<ClientboundChangeDifficultyPacketHandle> {
        public final Template.Field.Converted<Difficulty> difficulty = new Template.Field.Converted();
        public final Template.Field.Boolean hardcore = new Template.Field.Boolean();
    }
}

