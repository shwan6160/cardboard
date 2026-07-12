/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.tc.portals;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.portals.PortalDestination;
import org.bukkit.World;

public interface PortalProvider {
    public PortalDestination getPortalDestination(World var1, String var2, MinecartGroup var3);

    default public String getPreferredDestination(SignActionEvent event) {
        return null;
    }
}

