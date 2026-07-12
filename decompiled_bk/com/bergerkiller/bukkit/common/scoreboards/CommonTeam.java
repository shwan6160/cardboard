/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.scoreboards;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacketHandle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CommonTeam
implements Serializable {
    private static final long serialVersionUID = -2928363541719230386L;
    private String name;
    private String displayName;
    private String prefix;
    private String suffix;
    private FriendlyFireType friendlyFire;
    private List<String> players;
    private boolean sendToAll;

    protected CommonTeam(String name) {
        this.name = name;
        this.displayName = name;
        this.prefix = "";
        this.suffix = "";
        this.friendlyFire = FriendlyFireType.ON;
        this.players = new ArrayList<String>();
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public FriendlyFireType getFriendlyFireType() {
        return this.friendlyFire;
    }

    public List<String> getPlayers() {
        return this.players;
    }

    public boolean shouldSendToAll() {
        return this.sendToAll;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.update();
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        this.update();
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
        this.update();
    }

    public void setFriendlyFire(FriendlyFireType friendlyFire) {
        this.friendlyFire = friendlyFire;
        this.update();
    }

    public void setSendToAll(boolean sendToAll) {
        this.sendToAll = sendToAll;
    }

    public void addPlayer(OfflinePlayer player) {
        this.players.add(player.getName());
        if (this.sendToAll) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                PacketUtil.sendPacket(p, this.getPacket(3));
            }
        } else {
            if (player instanceof Player && ((Player)player).isValid()) {
                PacketUtil.sendPacket((Player)player, this.getPacket(0));
            }
            for (String user : this.players) {
                Player p = Bukkit.getPlayer((String)user);
                if (p == null || !p.isValid()) continue;
                PacketUtil.sendPacket(p, this.getPacket(3));
            }
        }
    }

    public void removePlayer(OfflinePlayer player) {
        this.players.remove(player.getName());
        if (this.sendToAll) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                PacketUtil.sendPacket(p, this.getPacket(4));
            }
        } else {
            if (player instanceof Player && ((Player)player).isValid()) {
                PacketUtil.sendPacket((Player)player, this.getPacket(1));
            }
            for (String user : this.players) {
                Player p = Bukkit.getPlayer((String)user);
                if (p == null || !p.isValid()) continue;
                PacketUtil.sendPacket(p, this.getPacket(4));
            }
        }
    }

    public void show() {
        if (this.sendToAll) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                PacketUtil.sendPacket(p, this.getPacket(0));
            }
        } else {
            for (String user : this.players) {
                Player p = Bukkit.getPlayer((String)user);
                if (p == null || !p.isValid()) continue;
                PacketUtil.sendPacket(p, this.getPacket(0));
            }
        }
    }

    public void hide() {
        if (this.sendToAll) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                PacketUtil.sendPacket(p, this.getPacket(1));
            }
        } else {
            for (String user : this.players) {
                Player p = Bukkit.getPlayer((String)user);
                if (p == null || !p.isValid()) continue;
                PacketUtil.sendPacket(p, this.getPacket(1));
            }
        }
    }

    private void update() {
        if (this.sendToAll) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                PacketUtil.sendPacket(p, this.getPacket(2));
            }
        } else {
            for (String user : this.players) {
                Player p = Bukkit.getPlayer((String)user);
                if (p == null || !p.isValid()) continue;
                PacketUtil.sendPacket(p, this.getPacket(2));
            }
        }
    }

    public void send(Player player) {
        PacketUtil.sendPacket(player, this.getPacket(0));
    }

    private CommonPacket getPacket(int method) {
        ClientboundSetPlayerTeamPacketHandle packet = ClientboundSetPlayerTeamPacketHandle.createNew();
        packet.setMethod(method);
        packet.setName(this.name);
        packet.setPlayers(this.players);
        if (method == 0 || method == 2) {
            packet.setDisplayName(ChatText.fromMessage(this.displayName));
            packet.setPrefix(ChatText.fromMessage(this.prefix));
            packet.setSuffix(ChatText.fromMessage(this.suffix));
            packet.setTeamOptionFlags(this.friendlyFire.getRawInt());
        }
        return packet.toCommonPacket();
    }

    public static enum FriendlyFireType {
        OFF,
        ON,
        INVICIBLE;


        public int getRawInt() {
            return this.ordinal();
        }
    }
}

