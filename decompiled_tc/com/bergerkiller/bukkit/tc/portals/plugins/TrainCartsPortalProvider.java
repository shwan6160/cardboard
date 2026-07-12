/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.tc.portals.plugins;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.portals.PortalDestination;
import com.bergerkiller.bukkit.tc.portals.PortalProvider;
import org.bukkit.World;

public class TrainCartsPortalProvider
implements PortalProvider {
    @Override
    public PortalDestination getPortalDestination(World world, String portalName, MinecartGroup group) {
        return null;
    }

    @Override
    public String getPreferredDestination(SignActionEvent event) {
        if (event.getHeader().isValid() && event.isType("teleport")) {
            return event.getLine(2);
        }
        return null;
    }
}

