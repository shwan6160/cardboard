/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.conversion.type.WrapperConversion
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.util.Vector
 *  org.mvplugins.multiverse.core.world.LoadedMultiverseWorld
 *  org.mvplugins.multiverse.portals.MVPortal
 *  org.mvplugins.multiverse.portals.MultiversePortalsApi
 *  org.mvplugins.multiverse.portals.PortalLocation
 */
package com.bergerkiller.bukkit.tc.portals.plugins;

import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.portals.PortalDestination;
import com.bergerkiller.bukkit.tc.portals.PortalProvider;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.portals.MVPortal;
import org.mvplugins.multiverse.portals.MultiversePortalsApi;
import org.mvplugins.multiverse.portals.PortalLocation;

public class MultiversePortalsProvider
implements PortalProvider {
    private final MultiversePortalsApi api = (MultiversePortalsApi)Bukkit.getServicesManager().load(MultiversePortalsApi.class);

    public MultiversePortalsProvider(TrainCarts traincarts, Plugin plugin) {
        traincarts.log(Level.INFO, "Multiverse Portals detected, trains can be teleported to MV Portals");
    }

    @Override
    public PortalDestination getPortalDestination(World world, String portalName, MinecartGroup group) {
        PortalLocation portalPos;
        LoadedMultiverseWorld loadedMultiverseWorld;
        MVPortal portal = this.api.getPortalManager().getPortal(portalName);
        Direction direction = Direction.NONE;
        if (portal == null) {
            int dirIdx = portalName.lastIndexOf(58);
            if (dirIdx == -1) {
                return null;
            }
            direction = Direction.parse(portalName.substring(dirIdx + 1));
            portalName = portalName.substring(0, dirIdx);
            portal = this.api.getPortalManager().getPortal(portalName);
            if (portal == null) {
                return null;
            }
        }
        if ((loadedMultiverseWorld = (portalPos = portal.getPortalLocation()).getMVWorld()) == null) {
            return null;
        }
        World portalWorld = (World)loadedMultiverseWorld.getBukkitWorld().getOrElse((Object)null);
        if (portalWorld == null) {
            return null;
        }
        Block minBlock = WrapperConversion.toIntVector3FromVector((Vector)portalPos.getMinimum()).toBlock(portalWorld);
        Block maxBlock = WrapperConversion.toIntVector3FromVector((Vector)portalPos.getMaximum()).toBlock(portalWorld);
        double requiredTrainDistance = group == null ? 0.0 : group.getProperties().getIdealTotalTrainLength();
        return PortalDestination.findDestination(minBlock, maxBlock, direction, requiredTrainDistance);
    }
}

