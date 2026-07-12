/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.BlockLocation
 *  com.bergerkiller.bukkit.common.MessageBuilder
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.nbt.CommonTagCompound
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.ChatColor
 *  org.bukkit.Color
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.debug.types;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.components.RailJunction;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.debug.DebugToolUtil;
import com.bergerkiller.bukkit.tc.debug.types.DebugToolTrackWalkerType;
import com.bergerkiller.bukkit.tc.pathfinding.PathConnection;
import com.bergerkiller.bukkit.tc.pathfinding.PathNode;
import com.bergerkiller.bukkit.tc.pathfinding.PathProvider;
import com.bergerkiller.bukkit.tc.pathfinding.PathRailInfo;
import com.bergerkiller.bukkit.tc.pathfinding.PathRoutingHandler;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.utils.TrackWalkingPoint;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class DebugToolTypeListDestinations
extends DebugToolTrackWalkerType {
    private final String destination;
    private int maxDestinations = 5;

    public DebugToolTypeListDestinations() {
        this.destination = null;
    }

    public DebugToolTypeListDestinations(String destination) {
        this.destination = destination;
    }

    public DebugToolTypeListDestinations setMaxDestinations(int limit) {
        this.maxDestinations = limit;
        return this;
    }

    @Override
    public void loadMetadata(CommonTagCompound metadata) {
        if (metadata.containsKey((Object)"maxDestinations")) {
            this.maxDestinations = (Integer)metadata.getValue("maxDestinations", (Object)5);
        }
    }

    @Override
    public void saveMetadata(CommonTagCompound metadata) {
        metadata.putValue("maxDestinations", (Object)this.maxDestinations);
    }

    @Override
    public String getIdentifier() {
        if (this.destination != null) {
            return "Destination " + this.destination;
        }
        return "Destinations";
    }

    @Override
    public String getTitle() {
        if (this.destination != null) {
            return "Pathfinding destination searcher (routes to " + this.destination + ")";
        }
        return "Pathfinding destination searcher";
    }

    @Override
    public String getDescription() {
        if (this.destination != null) {
            return "Identifies the route to reach destination '" + this.destination + "'";
        }
        return "Identifies all the destination routes reachable from the rails clicked";
    }

    @Override
    public String getInstructions() {
        if (this.destination != null) {
            return "Right-click rails to see whether and how a train would travel to " + this.destination + ".";
        }
        return "Right-click rails to see what destinations can be reached from there.";
    }

    @Override
    public void onBlockInteract(final TrainCarts trainCarts, final Player player, final TrackWalkingPoint walker, CommonItemStack item, boolean isRightClick) {
        final PathProvider provider = trainCarts.getPathProvider();
        walker.setNavigator(new TrackWalkingPoint.Navigator<PathRoutingHandler.PathRouteEvent>(){
            final /* synthetic */ DebugToolTypeListDestinations this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void navigate(PathRoutingHandler.PathRouteEvent routeEvent) {
                routeEvent.provider().handleRouting(routeEvent);
                PathRailInfo info = routeEvent.getRailInfo();
                if (info == PathRailInfo.BLOCKED) {
                    if (this.this$0.destination != null) {
                        player.sendMessage(ChatColor.RED + "Destination " + this.this$0.destination + " can not be reached!");
                    }
                    player.sendMessage(ChatColor.RED + "A blocker sign at " + ChatColor.YELLOW + DebugToolUtil.coordinates(walker.state.position()) + ChatColor.RED + " is blocking trains!");
                    routeEvent.abortNavigation();
                } else if (info == PathRailInfo.NODE) {
                    this.this$0.debugListRoutesFrom(trainCarts, player, walker.state, this.this$0.destination, player.isSneaking(), walker.movedTotal);
                    routeEvent.abortNavigation();
                }
            }

            @Override
            public PathRoutingHandler.PathRouteEvent createNewEvent() {
                return new PathRoutingHandler.PathRouteEvent(provider, walker.state.railWorld());
            }
        });
        Block old_railBlock = null;
        double stopDistance = walker.movedTotal + 2000.0;
        int lim = 10000;
        while (true) {
            if (--lim == 0 || walker.movedTotal >= stopDistance) {
                CommonUtil.getPluginExecutor((Plugin)trainCarts).execute(() -> this.onBlockInteract(trainCarts, player, walker, item, isRightClick));
                break;
            }
            if (this.destination != null) {
                if (!walker.move(0.3)) {
                    if (walker.failReason == TrackWalkingPoint.FailReason.NAVIGATION_ABORTED) break;
                    DebugToolUtil.showEndOfTheRail(player, walker, 0.0);
                    break;
                }
                Util.spawnDustParticle(walker.state.positionLocation(), Color.RED);
            } else {
                if (!walker.moveFull()) {
                    if (walker.failReason == TrackWalkingPoint.FailReason.NAVIGATION_ABORTED) break;
                    DebugToolUtil.showEndOfTheRail(player, walker, 0.0);
                    break;
                }
                Util.spawnDustParticle(walker.state.positionLocation(), Color.GRAY);
            }
            if (BlockUtil.equals((Block)walker.state.railBlock(), old_railBlock)) continue;
            old_railBlock = walker.state.railBlock();
        }
    }

    private void debugListRoutesFrom(final TrainCarts trainCarts, final Player player, final RailState state, final String destinationName, boolean reroute, final double initialDistance) {
        final PathProvider provider = trainCarts.getPathProvider();
        if (!state.railLookup().isValid()) {
            player.sendMessage(ChatColor.RED + "Failed to list destinations - World is no longer loaded!");
            return;
        }
        PathNode node = provider.getWorld(state.railWorld()).getNodeAtRail(state.railBlock());
        if (node == null) {
            provider.discoverFromRail(new BlockLocation(state.railBlock()));
            player.sendMessage(ChatColor.YELLOW + "Discovering paths from " + DebugToolUtil.coordinates(state.position()));
        } else if (reroute) {
            reroute = false;
            player.sendMessage(ChatColor.YELLOW + "Rerouting the node network from " + node.getDisplayName());
            node.rerouteConnected();
        }
        if (provider.isProcessing()) {
            final boolean f_reroute = reroute;
            Localization.PATHING_BUSY.message((CommandSender)player, new String[0]);
            new Task(this, (JavaPlugin)trainCarts){
                final /* synthetic */ DebugToolTypeListDestinations this$0;
                {
                    this.this$0 = this$0;
                    super(arg0);
                }

                public void run() {
                    if (!provider.isProcessing()) {
                        this.stop();
                        this.this$0.debugListRoutesFrom(trainCarts, player, state, destinationName, f_reroute, initialDistance);
                    }
                }
            }.start(1L, 1L);
            return;
        }
        if (node == null) {
            player.sendMessage(ChatColor.RED + "[Error] Path finding node is missing at " + DebugToolUtil.coordinates(state.position()) + " after " + (int)initialDistance + " blocks");
            return;
        }
        if (destinationName != null) {
            PathNode destination = node.getWorld().getNodeByName(destinationName);
            if (destination == null) {
                player.sendMessage(ChatColor.RED + "Destination " + destinationName + " does not exist. Try rerouting (sneak-click)");
                return;
            }
            DebugToolTypeListDestinations.debugShowRouteFromTo(player, node, state.railBlock(), destination, initialDistance);
        } else {
            this.debugListAllRoutes(player, node, state.railBlock(), initialDistance);
        }
    }

    private static void debugShowRouteFromTo(Player player, PathNode node, Block railBlock, PathNode destination, double initialDistance) {
        if (node == destination) {
            player.sendMessage(ChatColor.GREEN + "Route to " + ChatColor.YELLOW + destination.getDisplayName() + ChatColor.GREEN + " was found with a distance of " + ChatColor.YELLOW + MathUtil.round((double)initialDistance, (int)1) + ChatColor.GREEN + " blocks");
            return;
        }
        PathConnection[] route = node.findRoute(destination);
        if (route.length == 0) {
            player.sendMessage(ChatColor.RED + "Destination '" + destination.getDisplayName() + "' could not be reached from " + DebugToolUtil.coordinates(railBlock.getX(), railBlock.getY(), railBlock.getZ()));
            return;
        }
        double totalDistance = initialDistance;
        for (PathConnection connection : route) {
            totalDistance += connection.distance;
        }
        double maxDistance = 1600.0;
        Color[] colors = new Color[]{Color.BLUE, Color.GREEN, Color.RED};
        int color_idx = 0;
        int lim = 10000;
        for (PathConnection connection : route) {
            TrackWalkingPoint walker = DebugToolTypeListDestinations.takeJunction(railBlock, connection);
            if (walker == null) {
                player.sendMessage(ChatColor.RED + "Path broke at rail " + DebugToolUtil.coordinates(railBlock.getX(), railBlock.getY(), railBlock.getZ()));
                return;
            }
            railBlock = connection.destination.location.getBlock();
            Color color = colors[color_idx++ % colors.length];
            while (--lim > 0 && !(walker.movedTotal > maxDistance)) {
                if (!walker.move(0.3)) {
                    DebugToolUtil.showEndOfTheRail(player, walker, initialDistance);
                    return;
                }
                Util.spawnDustParticle(walker.state.positionLocation(), color);
                if (!BlockUtil.equals((Block)railBlock, (Block)walker.state.railBlock())) continue;
            }
            if (lim <= 0 || (maxDistance -= walker.movedTotal) <= 0.0) break;
        }
        player.sendMessage(ChatColor.GREEN + "Route to " + ChatColor.YELLOW + destination.getDisplayName() + ChatColor.GREEN + " was found with a distance of " + ChatColor.YELLOW + MathUtil.round((double)totalDistance, (int)1) + ChatColor.GREEN + " blocks");
    }

    private void debugListAllRoutes(Player player, PathNode node, Block railBlock, double initialDistance) {
        MessageBuilder message = new MessageBuilder();
        message.gray(new Object[]{"Node "}).white(new Object[]{DebugToolUtil.coordinates(node.location.x, node.location.y, node.location.z)});
        message.gray(new Object[]{" reached after "}).white(new Object[]{MathUtil.round((double)initialDistance, (int)1)}).gray(new Object[]{" blocks"}).newLine();
        message.gray(new Object[]{"Destinations from "}).white(new Object[]{node.getDisplayName()}).gray(new Object[]{":"}).newLine();
        int color_idx = 0;
        for (Map.Entry<PathConnection, List<PathConnection>> entry : node.getDeepNeighbours().entrySet()) {
            PathConnection connection = entry.getKey();
            Collection destinations = entry.getValue();
            Iterator iter = destinations.iterator();
            while (iter.hasNext()) {
                if (!((PathConnection)iter.next()).destination.containsOnlySwitcher()) continue;
                iter.remove();
            }
            if (destinations.isEmpty()) continue;
            ChatColor chatcolor = DebugToolUtil.getWheelChatColor(color_idx);
            Color color = DebugToolUtil.getWheelColor(color_idx);
            ++color_idx;
            TrackWalkingPoint walker = DebugToolTypeListDestinations.takeJunction(railBlock, connection);
            if (walker != null) {
                int lim = 100;
                while (walker.move(0.3) && --lim > 0 && walker.movedTotal < 3.0) {
                    Util.spawnDustParticle(walker.state.positionLocation(), color);
                }
            }
            message.append(chatcolor, new String[]{"- "});
            message.setIndent(2);
            message.setSeparator(ChatColor.GRAY, " / ");
            int limit = this.maxDestinations;
            for (PathConnection destination : destinations) {
                if (limit > 0) {
                    message.append(chatcolor, new Object[]{"[", MathUtil.round((double)destination.distance, (int)1), "] ", destination.destination.getDisplayName()});
                }
                if (--limit >= 0) continue;
                message.append(chatcolor, new String[]{"..."});
                break;
            }
            message.clearSeparator();
            message.setIndent(0);
            message.newLine();
        }
        message.send((CommandSender)player);
    }

    private static TrackWalkingPoint takeJunction(Block railBlock, PathConnection connection) {
        RailState state = null;
        for (RailType type : RailType.values()) {
            if (!type.isRail(railBlock)) continue;
            List<RailJunction> junctions = type.getJunctions(railBlock);
            RailJunction picked = null;
            for (RailJunction junction : junctions) {
                if (!connection.junctionName.equals(junction.name())) continue;
                picked = junction;
                break;
            }
            if (picked == null) break;
            state = type.takeJunction(railBlock, picked);
            break;
        }
        if (state == null) {
            return null;
        }
        final PathProvider provider = connection.destination.getWorld().getProvider();
        final TrackWalkingPoint walker = new TrackWalkingPoint(state);
        walker.setLoopFilter(true);
        walker.setNavigator(new TrackWalkingPoint.Navigator<PathRoutingHandler.PathRouteEvent>(){

            @Override
            public void navigate(PathRoutingHandler.PathRouteEvent routeEvent) {
                routeEvent.provider().handleRouting(routeEvent);
            }

            @Override
            public PathRoutingHandler.PathRouteEvent createNewEvent() {
                return new PathRoutingHandler.PathRouteEvent(provider, walker.state.railWorld());
            }
        });
        return walker;
    }
}

