/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import org.bukkit.ChatColor;

public class TeamColorConversion {
    private static final Class<?> teamColorType;
    private static final Enum<?>[] teamColorValues;
    private static final ChatColor[] bukkitChatColors;

    public static boolean isInitialized() {
        return teamColorValues != null;
    }

    @ConverterMethod(output="net.minecraft.world.scores.TeamColor")
    public static Object fromBukkit(ChatColor color) {
        int ord = color.ordinal();
        if (ord < teamColorValues.length) {
            return teamColorValues[ord];
        }
        return null;
    }

    @ConverterMethod(input="net.minecraft.world.scores.TeamColor")
    public static ChatColor toBukkit(Object teamColor) {
        if (teamColor == null) {
            return null;
        }
        if (!teamColorType.isInstance(teamColor)) {
            throw new IllegalArgumentException("Not a TeamColor: " + teamColor.getClass().getName());
        }
        int ord = ((Enum)teamColor).ordinal();
        return bukkitChatColors[ord];
    }

    static {
        CommonBootstrap.initCommonServer();
        teamColorType = CommonUtil.getClass("net.minecraft.world.scores.TeamColor");
        teamColorValues = teamColorType == null ? null : (Enum[])teamColorType.getEnumConstants();
        bukkitChatColors = ChatColor.values();
    }
}

