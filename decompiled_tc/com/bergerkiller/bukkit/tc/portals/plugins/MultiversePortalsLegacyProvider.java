/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.conversion.type.WrapperConversion
 *  com.onarandombox.MultiversePortals.MVPortal
 *  com.onarandombox.MultiversePortals.MultiversePortals
 *  com.onarandombox.MultiversePortals.PortalLocation
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.portals.plugins;

import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.portals.PortalDestination;
import com.bergerkiller.bukkit.tc.portals.PortalProvider;
import com.onarandombox.MultiversePortals.MVPortal;
import com.onarandombox.MultiversePortals.MultiversePortals;
import com.onarandombox.MultiversePortals.PortalLocation;
import java.util.logging.Level;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class MultiversePortalsLegacyProvider
implements PortalProvider {
    private final MultiversePortals plugin;

    public MultiversePortalsLegacyProvider(TrainCarts traincarts, Plugin plugin) {
        this.plugin = (MultiversePortals)plugin;
        traincarts.log(Level.INFO, "Multiverse Portals detected, trains can be teleported to MV Portals");
    }

    @Override
    public PortalDestination getPortalDestination(World world, String portalName, MinecartGroup group) {
        PortalLocation portalPos;
        World portalWorld;
        MVPortal portal = this.plugin.getPortalManager().getPortal(portalName);
        Direction direction = Direction.NONE;
        if (portal == null) {
            int dirIdx = portalName.lastIndexOf(58);
            if (dirIdx == -1) {
                return null;
            }
            direction = Direction.parse(portalName.substring(dirIdx + 1));
            portalName = portalName.substring(0, dirIdx);
            portal = this.plugin.getPortalManager().getPortal(portalName);
            if (portal == null) {
                return null;
            }
        }
        if ((portalWorld = (portalPos = portal.getLocation()).getMVWorld().getCBWorld()) == null) {
            return null;
        }
        Block minBlock = WrapperConversion.toIntVector3FromVector((Vector)portalPos.getMinimum()).toBlock(portalWorld);
        Block maxBlock = WrapperConversion.toIntVector3FromVector((Vector)portalPos.getMaximum()).toBlock(portalWorld);
        double requiredTrainDistance = group == null ? 0.0 : group.getProperties().getIdealTotalTrainLength();
        return PortalDestination.findDestination(minBlock, maxBlock, direction, requiredTrainDistance);
    }
}

