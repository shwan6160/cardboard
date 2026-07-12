/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.scoreboard.DisplaySlot
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.scoreboard.DisplaySlot;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket")
public abstract class ClientboundSetDisplayObjectivePacketHandle
extends PacketHandle {
    public static final ClientboundSetDisplayObjectivePacketClass T = Template.Class.create(ClientboundSetDisplayObjectivePacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundSetDisplayObjectivePacketHandle createHandle(Object handleInstance) {
        return (ClientboundSetDisplayObjectivePacketHandle)T.createHandle(handleInstance);
    }

    public abstract DisplaySlot getDisplay();

    public abstract void setDisplay(DisplaySlot var1);

    public abstract String getName();

    public abstract void setName(String var1);

    public static final class ClientboundSetDisplayObjectivePacketClass
    extends Template.Class<ClientboundSetDisplayObjectivePacketHandle> {
        public final Template.Field.Converted<DisplaySlot> display = new Template.Field.Converted();
        public final Template.Field<String> name = new Template.Field();
    }
}

