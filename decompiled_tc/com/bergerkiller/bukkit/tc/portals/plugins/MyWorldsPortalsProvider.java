/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.mw.Portal
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.portals.plugins;

import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.mw.Portal;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.portals.PortalDestination;
import com.bergerkiller.bukkit.tc.portals.PortalProvider;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

public class MyWorldsPortalsProvider
implements PortalProvider {
    public MyWorldsPortalsProvider(TrainCarts traincarts, Plugin plugin) {
        traincarts.log(Level.INFO, "MyWorlds detected, support for portal sign train teleportation added!");
    }

    @Override
    public PortalDestination getPortalDestination(World world, String portalName, MinecartGroup group) {
        Location destLoc = Portal.getPortalLocation((String)portalName, (String)world.getName());
        if (destLoc == null) {
            return null;
        }
        Block sign = destLoc.getBlock();
        sign.getChunk();
        if (!((Boolean)MaterialUtil.ISSIGN.get(sign)).booleanValue()) {
            return null;
        }
        SignActionEvent dest_info = new SignActionEvent(RailLookup.TrackedSign.forRealSign(sign, true, null));
        if (!dest_info.hasRails()) {
            return null;
        }
        return new PortalDestination(dest_info.getRails(), dest_info.getSpawnDirections());
    }

    public static String getPortalDestination(Location portalLocation) {
        Portal portal = Portal.get((Location)portalLocation);
        if (portal == null) {
            return null;
        }
        return portal.getDestinationName();
    }

    @Override
    public String getPreferredDestination(SignActionEvent event) {
        if (event.getHeader().getModeText().equals("portal")) {
            return MyWorldsPortalsProvider.getPortalDestination(event.getLocation());
        }
        return null;
    }
}

