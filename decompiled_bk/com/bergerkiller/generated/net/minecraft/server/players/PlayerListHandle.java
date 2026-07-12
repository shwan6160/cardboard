/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.generated.net.minecraft.server.players;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import org.bukkit.World;
import org.bukkit.entity.Player;

@Template.InstanceType(value="net.minecraft.server.players.PlayerList")
public abstract class PlayerListHandle
extends Template.Handle {
    public static final PlayerListClass T = Template.Class.create(PlayerListClass.class, Common.TEMPLATE_RESOLVER);

    public static PlayerListHandle createHandle(Object handleInstance) {
        return (PlayerListHandle)T.createHandle(handleInstance);
    }

    public abstract int getMaxPlayers();

    public abstract void setMaxPlayers(int var1);

    public abstract CommonTagCompound migratePlayerData(CommonTagCompound var1);

    public abstract void savePlayers();

    public abstract void savePlayerFile(Player var1);

    public abstract void sendRawPacketNearby(World var1, double var2, double var4, double var6, double var8, Object var10);

    public abstract List<Player> getPlayers();

    public abstract void setPlayers(List<Player> var1);

    public static final class PlayerListClass
    extends Template.Class<PlayerListHandle> {
        public final Template.Field.Converted<List<Player>> players = new Template.Field.Converted();
        public final Template.Method<Integer> getMaxPlayers = new Template.Method();
        public final Template.Method<Void> setMaxPlayers = new Template.Method();
        public final Template.Method.Converted<CommonTagCompound> migratePlayerData = new Template.Method.Converted();
        public final Template.Method<Void> savePlayers = new Template.Method();
        public final Template.Method.Converted<Void> savePlayerFile = new Template.Method.Converted();
        public final Template.Method.Converted<Void> sendRawPacketNearby = new Template.Method.Converted();
    }
}

