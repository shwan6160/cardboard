/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.dep.neznamytabnametaghider;

import com.bergerkiller.bukkit.tc.dep.neznamytabnametaghider.TabNameTagHider;
import com.bergerkiller.bukkit.tc.dep.neznamytabnametaghider.TabNameTagHiderImpl_3_1_4;
import com.bergerkiller.bukkit.tc.dep.neznamytabnametaghider.TabNameTagHiderImpl_4_0_3;
import com.bergerkiller.bukkit.tc.dep.softdependency.SoftDependency;
import org.bukkit.plugin.Plugin;

public class TabNameTagHiderDependency
extends SoftDependency<TabNameTagHider> {
    public TabNameTagHiderDependency(Plugin owningPlugin) {
        super(owningPlugin, "TAB", TabNameTagHider.NONE);
    }

    @Override
    protected TabNameTagHider initialize(Plugin plugin) throws Error, Exception {
        ClassLoader loader = plugin.getClass().getClassLoader();
        Class.forName("me.neznamy.tab.api.TabAPI", false, loader);
        boolean hasNameTagManager = false;
        try {
            Class.forName("me.neznamy.tab.api.nametag.NameTagManager", false, loader);
            hasNameTagManager = true;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (!hasNameTagManager) {
            return TabNameTagHiderDependency.create_3_1_4();
        }
        return TabNameTagHiderDependency.create_4_0_3();
    }

    private static TabNameTagHider create_4_0_3() {
        return TabNameTagHiderImpl_4_0_3.create();
    }

    private static TabNameTagHider create_3_1_4() {
        return TabNameTagHiderImpl_3_1_4.create();
    }
}

