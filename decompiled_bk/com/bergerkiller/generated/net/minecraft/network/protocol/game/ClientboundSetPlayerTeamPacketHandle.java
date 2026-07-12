/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;
import org.bukkit.ChatColor;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket")
public abstract class ClientboundSetPlayerTeamPacketHandle
extends PacketHandle {
    public static final ClientboundSetPlayerTeamPacketClass T = Template.Class.create(ClientboundSetPlayerTeamPacketClass.class, Common.TEMPLATE_RESOLVER);
    public static final int METHOD_ADD = 0;
    public static final int METHOD_REMOVE = 1;
    public static final int METHOD_CHANGE = 2;
    public static final int METHOD_JOIN = 3;
    public static final int METHOD_LEAVE = 4;

    public static ClientboundSetPlayerTeamPacketHandle createHandle(Object handleInstance) {
        return (ClientboundSetPlayerTeamPacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundSetPlayerTeamPacketHandle createNew() {
        return ClientboundSetPlayerTeamPacketHandle.T.createNew.invoke();
    }

    public abstract ChatText getDisplayName();

    public abstract void setDisplayName(ChatText var1);

    public abstract ChatText getPrefix();

    public abstract void setPrefix(ChatText var1);

    public abstract ChatText getSuffix();

    public abstract void setSuffix(ChatText var1);

    public abstract String getVisibility();

    public abstract void setVisibility(String var1);

    public abstract String getCollisionRule();

    public abstract void setCollisionRule(String var1);

    public abstract ChatColor getColor();

    public abstract void setColor(ChatColor var1);

    public abstract int getTeamOptionFlags();

    public abstract void setTeamOptionFlags(int var1);

    public abstract int getMethod();

    public abstract void setMethod(int var1);

    public abstract String getName();

    public abstract void setName(String var1);

    public abstract Collection<String> getPlayers();

    public abstract void setPlayers(Collection<String> var1);

    public static final class ClientboundSetPlayerTeamPacketClass
    extends Template.Class<ClientboundSetPlayerTeamPacketHandle> {
        public final Template.Field.Integer method = new Template.Field.Integer();
        public final Template.Field<String> name = new Template.Field();
        public final Template.Field<Collection<String>> players = new Template.Field();
        public final Template.StaticMethod.Converted<ClientboundSetPlayerTeamPacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method.Converted<ChatText> getDisplayName = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setDisplayName = new Template.Method.Converted();
        public final Template.Method.Converted<ChatText> getPrefix = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setPrefix = new Template.Method.Converted();
        public final Template.Method.Converted<ChatText> getSuffix = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setSuffix = new Template.Method.Converted();
        public final Template.Method.Converted<String> getVisibility = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setVisibility = new Template.Method.Converted();
        public final Template.Method.Converted<String> getCollisionRule = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setCollisionRule = new Template.Method.Converted();
        public final Template.Method.Converted<ChatColor> getColor = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setColor = new Template.Method.Converted();
        public final Template.Method<Integer> getTeamOptionFlags = new Template.Method();
        public final Template.Method<Void> setTeamOptionFlags = new Template.Method();
    }
}

