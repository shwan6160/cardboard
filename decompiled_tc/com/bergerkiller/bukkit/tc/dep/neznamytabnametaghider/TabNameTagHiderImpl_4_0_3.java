/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.neznamy.tab.api.TabAPI
 *  me.neznamy.tab.api.TabPlayer
 *  me.neznamy.tab.api.nametag.NameTagManager
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.dep.neznamytabnametaghider;

import com.bergerkiller.bukkit.tc.dep.neznamytabnametaghider.TabNameTagHider;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.nametag.NameTagManager;
import org.bukkit.entity.Player;

public class TabNameTagHiderImpl_4_0_3
implements TabNameTagHider.TabPlayerNameTagHider {
    private final TabPlayer player;
    private final NameTagManager nametagManager;
    private boolean needToRestoreNametag = false;

    private TabNameTagHiderImpl_4_0_3(TabAPI tab, Player player) {
        this.player = tab.getPlayer(player.getUniqueId());
        this.nametagManager = tab.getNameTagManager();
    }

    @Override
    public void hide() {
        if (!this.nametagManager.hasHiddenNameTag(this.player)) {
            this.nametagManager.hideNameTag(this.player);
            this.needToRestoreNametag = true;
        }
    }

    @Override
    public void show() {
        if (this.needToRestoreNametag) {
            this.needToRestoreNametag = false;
            this.nametagManager.showNameTag(this.player);
        }
    }

    public static TabNameTagHider create() {
        TabAPI tab = TabAPI.getInstance();
        tab.getNameTagManager();
        return player -> new TabNameTagHiderImpl_4_0_3(tab, player);
    }
}

