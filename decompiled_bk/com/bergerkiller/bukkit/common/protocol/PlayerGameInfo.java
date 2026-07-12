/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.protocol;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.PlayerGameInfoCache;
import com.bergerkiller.mountiplex.logic.TextValueSequence;
import org.bukkit.entity.Player;

public interface PlayerGameInfo {
    public static final PlayerGameInfo SERVER = new PlayerGameInfo(){
        private final TextValueSequence version = TextValueSequence.parse(CommonBootstrap.initCommonServer().getMinecraftVersion());

        @Override
        public String version() {
            return this.version.toString();
        }

        @Override
        public boolean evaluateVersion(String operand, TextValueSequence rightSide) {
            return TextValueSequence.evaluate(this.version, operand, rightSide);
        }
    };

    public String version();

    default public boolean evaluateVersion(String operand, String rightSide) {
        return this.evaluateVersion(operand, PlayerGameInfoCache.parseVersion(rightSide));
    }

    public boolean evaluateVersion(String var1, TextValueSequence var2);

    public static PlayerGameInfo of(Player player) {
        if (CommonPlugin.hasInstance()) {
            return CommonPlugin.getInstance().getGameInfo(player);
        }
        return SERVER;
    }
}

