/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.controller;

import com.bergerkiller.bukkit.common.internal.logic.PlayerFileDataHandler;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class PlayerDataController {
    private PlayerFileDataHandler.Hook hook = null;

    public CommonTagCompound onLoad(Player player) {
        return this.hook.base_load((HumanEntity)player);
    }

    public CommonTagCompound onLoadOffline(String playerName, String playerUUID) {
        return this.hook.base_load_offline(playerName, playerUUID);
    }

    public void onSave(Player player) {
        this.hook.base_save((HumanEntity)player);
    }

    public void assign() {
        this.hook = PlayerFileDataHandler.INSTANCE.hook(this);
    }

    public void detach() {
        if (this.hook != null) {
            PlayerFileDataHandler.INSTANCE.unhook(this.hook, this);
            this.hook = null;
        }
    }

    public static PlayerDataController get() {
        PlayerDataController controller = PlayerFileDataHandler.INSTANCE.get();
        if (controller == null) {
            controller = new PlayerDataController();
            controller.hook = PlayerFileDataHandler.INSTANCE.mock(controller);
        }
        return controller;
    }
}

