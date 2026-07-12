/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.entity;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import java.util.Collection;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public enum PlayerInstancePhase {
    OFFLINE(false, false, false),
    JOINING(true, false, false),
    ALIVE(true, true, true),
    RESPAWNING(true, true, false);

    private final boolean isConnected;
    private final boolean hasJoined;
    private final boolean isAliveOnWorld;

    private PlayerInstancePhase(boolean isConnected, boolean hasJoined, boolean isAliveOnWorld) {
        this.isConnected = isConnected;
        this.hasJoined = hasJoined;
        this.isAliveOnWorld = isAliveOnWorld;
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    public boolean hasJoined() {
        return this.hasJoined;
    }

    public boolean isAliveOnWorld() {
        return this.isAliveOnWorld;
    }

    public static PlayerInstancePhase of(Player player) {
        if (player.isValid()) {
            return ALIVE;
        }
        if (Bukkit.getPlayer((UUID)player.getUniqueId()) != player) {
            if (ServerPlayerHandle.fromBukkit(player).hasDisconnected()) {
                return OFFLINE;
            }
            return JOINING;
        }
        if (player.isDead()) {
            return RESPAWNING;
        }
        return JOINING;
    }

    public static Collection<? extends Player> getJoinedPlayers() {
        return CommonUtil.getOnlinePlayers(p -> PlayerInstancePhase.of(p).hasJoined());
    }

    public static Collection<? extends Player> getAlivePlayers() {
        return CommonUtil.getOnlinePlayers(p -> PlayerInstancePhase.of(p).isAliveOnWorld());
    }
}

