/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.dep.neznamytabnametaghider;

import org.bukkit.entity.Player;

public interface TabNameTagHider {
    public static final TabNameTagHider NONE = player -> TabPlayerNameTagHider.NONE;

    public TabPlayerNameTagHider get(Player var1);

    public static interface TabPlayerNameTagHider {
        public static final TabPlayerNameTagHider NONE = new TabPlayerNameTagHider(){

            @Override
            public void hide() {
            }

            @Override
            public void show() {
            }
        };

        public void hide();

        public void show();
    }
}

