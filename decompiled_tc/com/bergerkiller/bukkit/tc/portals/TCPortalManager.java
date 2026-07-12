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
import com.bergerkiller.bukkit.tc.portals.PortalProvider;
import com.bergerkiller.bukkit.tc.portals.plugins.TrainCartsPortalProvider;
import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.World;

public class TCPortalManager {
    private static final HashMap<String, PortalProvider> portalProviders = new HashMap();

    public static void addPortalSupport(String pluginName, PortalProvider provider) {
        portalProviders.put(pluginName, provider);
    }

    public static void removePortalSupport(String pluginName) {
        portalProviders.remove(pluginName);
    }

    public static boolean isAvailable(String pluginName) {
        return portalProviders.containsKey(pluginName);
    }

    @Deprecated
    public static PortalDestination getPortalDestination(World world, String portalName) {
        return TCPortalManager.getPortalDestination(world, portalName, null);
    }

    public static PortalDestination getPortalDestination(World world, String portalName, MinecartGroup group) {
        PortalProvider provider;
        PortalDestination dest = null;
        Iterator<PortalProvider> iterator = portalProviders.values().iterator();
        while (iterator.hasNext() && (dest = (provider = iterator.next()).getPortalDestination(world, portalName, group)) == null) {
        }
        return dest;
    }

    public static String getPreferredDestination(SignActionEvent event) {
        for (PortalProvider provider : portalProviders.values()) {
            String pref = provider.getPreferredDestination(event);
            if (pref == null) continue;
            return pref;
        }
        return null;
    }

    static {
        TCPortalManager.addPortalSupport("TrainCarts", new TrainCartsPortalProvider());
    }
}

