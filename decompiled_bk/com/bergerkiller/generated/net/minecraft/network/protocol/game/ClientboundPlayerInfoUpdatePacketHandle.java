/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 */
package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import java.util.Set;
import org.bukkit.GameMode;

@Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket")
public abstract class ClientboundPlayerInfoUpdatePacketHandle
extends PacketHandle {
    public static final ClientboundPlayerInfoUpdatePacketClass T = Template.Class.create(ClientboundPlayerInfoUpdatePacketClass.class, Common.TEMPLATE_RESOLVER);

    public static ClientboundPlayerInfoUpdatePacketHandle createHandle(Object handleInstance) {
        return (ClientboundPlayerInfoUpdatePacketHandle)T.createHandle(handleInstance);
    }

    public static ClientboundPlayerInfoUpdatePacketHandle createNew() {
        return ClientboundPlayerInfoUpdatePacketHandle.T.createNew.invoke();
    }

    public static boolean isPlayerInfoRemovePacket(Object nmsPacketHandle) {
        return (Boolean)ClientboundPlayerInfoUpdatePacketHandle.T.isPlayerInfoRemovePacket.invoker.invoke(null, nmsPacketHandle);
    }

    public abstract Set<ActionHandle> getActions();

    public abstract void setAction(ActionHandle var1);

    public abstract void setActions(Set<ActionHandle> var1);

    public abstract List<EntryHandle> getPlayers();

    public abstract void setPlayers(List<EntryHandle> var1);

    public static final class ClientboundPlayerInfoUpdatePacketClass
    extends Template.Class<ClientboundPlayerInfoUpdatePacketHandle> {
        public final Template.Field.Converted<List<EntryHandle>> players = new Template.Field.Converted();
        public final Template.StaticMethod.Converted<ClientboundPlayerInfoUpdatePacketHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.StaticMethod<Boolean> isPlayerInfoRemovePacket = new Template.StaticMethod();
        public final Template.Method.Converted<Set<ActionHandle>> getActions = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setAction = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setActions = new Template.Method.Converted();
    }

    @Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action")
    public static abstract class ActionHandle
    extends Template.Handle {
        public static final ActionClass T = Template.Class.create(ActionClass.class, Common.TEMPLATE_RESOLVER);
        public static final ActionHandle ADD_PLAYER = ActionHandle.T.ADD_PLAYER.getSafe();
        public static final ActionHandle UPDATE_GAME_MODE = ActionHandle.T.UPDATE_GAME_MODE.getSafe();
        public static final ActionHandle UPDATE_LATENCY = ActionHandle.T.UPDATE_LATENCY.getSafe();
        public static final ActionHandle UPDATE_DISPLAY_NAME = ActionHandle.T.UPDATE_DISPLAY_NAME.getSafe();

        public static ActionHandle createHandle(Object handleInstance) {
            return (ActionHandle)T.createHandle(handleInstance);
        }

        public static final class ActionClass
        extends Template.Class<ActionHandle> {
            public final Template.EnumConstant.Converted<ActionHandle> ADD_PLAYER = new Template.EnumConstant.Converted();
            public final Template.EnumConstant.Converted<ActionHandle> UPDATE_GAME_MODE = new Template.EnumConstant.Converted();
            public final Template.EnumConstant.Converted<ActionHandle> UPDATE_LATENCY = new Template.EnumConstant.Converted();
            public final Template.EnumConstant.Converted<ActionHandle> UPDATE_DISPLAY_NAME = new Template.EnumConstant.Converted();
        }
    }

    @Template.InstanceType(value="net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry")
    public static abstract class EntryHandle
    extends Template.Handle {
        public static final EntryClass T = Template.Class.create(EntryClass.class, Common.TEMPLATE_RESOLVER);

        public static EntryHandle createHandle(Object handleInstance) {
            return (EntryHandle)T.createHandle(handleInstance);
        }

        public static EntryHandle createNew(ClientboundPlayerInfoUpdatePacketHandle packet, GameProfileHandle profile, int ping, GameMode gameMode, ChatText listName, boolean listed) {
            return EntryHandle.T.createNew.invokeVA(packet, profile, ping, gameMode, listName, listed);
        }

        public abstract GameProfileHandle getProfile();

        public abstract int getPing();

        public abstract GameMode getGameMode();

        public abstract ChatText getListName();

        public static EntryHandle createNew(ClientboundPlayerInfoUpdatePacketHandle packet, GameProfileHandle profile, int ping, GameMode gameMode, ChatText listName) {
            return EntryHandle.createNew(packet, profile, ping, gameMode, listName, true);
        }

        public static final class EntryClass
        extends Template.Class<EntryHandle> {
            public final Template.StaticMethod.Converted<EntryHandle> createNew = new Template.StaticMethod.Converted();
            public final Template.Method.Converted<GameProfileHandle> getProfile = new Template.Method.Converted();
            public final Template.Method<Integer> getPing = new Template.Method();
            public final Template.Method.Converted<GameMode> getGameMode = new Template.Method.Converted();
            public final Template.Method.Converted<ChatText> getListName = new Template.Method.Converted();
        }
    }
}

