/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.neznamy.tab.api.TabAPI
 *  me.neznamy.tab.api.TabPlayer
 *  me.neznamy.tab.api.team.TeamManager
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.dep.neznamytabnametaghider;

import com.bergerkiller.bukkit.tc.dep.neznamytabnametaghider.TabNameTagHider;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.team.TeamManager;
import org.bukkit.entity.Player;

public class TabNameTagHiderImpl_3_1_4
implements TabNameTagHider.TabPlayerNameTagHider {
    private final TabPlayer player;
    private final TeamManager teamManager;
    private boolean needToRestoreNametag = false;

    private TabNameTagHiderImpl_3_1_4(TabAPI tab, Player player) {
        this.player = tab.getPlayer(player.getUniqueId());
        this.teamManager = tab.getTeamManager();
    }

    @Override
    public void hide() {
        if (!this.teamManager.hasHiddenNametag(this.player)) {
            this.teamManager.hideNametag(this.player);
            this.needToRestoreNametag = true;
        }
    }

    @Override
    public void show() {
        if (this.needToRestoreNametag) {
            this.needToRestoreNametag = false;
            this.teamManager.showNametag(this.player);
        }
    }

    public static TabNameTagHider create() {
        TabAPI tab = TabAPI.getInstance();
        tab.getTeamManager();
        return player -> new TabNameTagHiderImpl_3_1_4(tab, player);
    }
}

