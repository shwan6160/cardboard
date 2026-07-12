/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.BlockLocation
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.pathfinding;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.pathfinding.PathConnection;
import com.bergerkiller.bukkit.tc.pathfinding.PathNodeSnapshot;
import com.bergerkiller.bukkit.tc.pathfinding.PathProvider;
import com.bergerkiller.bukkit.tc.pathfinding.PathSearchResult;
import com.bergerkiller.bukkit.tc.pathfinding.PathWorld;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.block.Block;

public class PathNode {
    private final PathWorld world;
    public final BlockLocation location;
    private final Set<String> names = new HashSet<String>();
    private final List<PathConnection> neighbors = new ArrayList<PathConnection>(3);
    public int index;
    private boolean isRailSwitchable;
    private PathSearchOperation lastSearch = null;
    private PathSearchResult lastSearchResult = null;
    private double lastSearchStartDistance = Double.MAX_VALUE;

    protected PathNode(PathWorld world, BlockLocation location) {
        this.world = world;
        this.location = location;
        this.isRailSwitchable = false;
    }

    public static void clearAll() {
        for (PathWorld world : TrainCarts.plugin.getPathProvider().getWorlds()) {
            world.clearAll();
        }
    }

    public static void reroute() {
        TrainCarts.plugin.getPathProvider().reroute();
    }

    public static PathNode get(BlockLocation railLocation) {
        return TrainCarts.plugin.getPathProvider().getWorld(railLocation.world).getNodeAtRail(railLocation);
    }

    public static PathNode get(Block block) {
        if (block == null) {
            return null;
        }
        return TrainCarts.plugin.getPathProvider().getWorld(block.getWorld()).getNodeAtRail(block);
    }

    public static PathNode remove(Block railsblock) {
        if (railsblock == null) {
            return null;
        }
        return TrainCarts.plugin.getPathProvider().getWorld(railsblock.getWorld()).removeAtRail(railsblock);
    }

    public static PathNode getOrCreate(SignActionEvent event) {
        if (!event.hasRails()) {
            throw new IllegalArgumentException("Sign has no rails - check hasRails()");
        }
        if (event.isType("destination")) {
            PathNode node = PathNode.getOrCreate(event.getRails());
            node.addName(event.getLine(2));
            return node;
        }
        if (event.isCartSign() ? !event.hasMember() || !event.getMember().getProperties().hasDestination() : event.isTrainSign() && (!event.hasGroup() || !event.getGroup().getProperties().hasDestination())) {
            return null;
        }
        PathNode node = PathNode.getOrCreate(event.getRails());
        node.addSwitcher();
        return node;
    }

    public static PathNode getOrCreate(Block location) {
        return PathNode.getOrCreate(new BlockLocation(location));
    }

    public static PathNode getOrCreate(BlockLocation location) {
        return TrainCarts.plugin.getPathProvider().getWorld(location.world).getOrCreateAtRail(location);
    }

    public PathWorld getWorld() {
        return this.world;
    }

    public PathConnection findConnection(String destination) {
        PathNode node = this.world.getNodeByName(destination);
        return node == null ? null : this.findConnection(node);
    }

    public PathConnection[] findRoute(PathNode destination) {
        PathSearchResult result = this.findBestPath(destination);
        if (result.found) {
            ArrayList<PathConnection> route = new ArrayList<PathConnection>();
            while (result.connection != null) {
                route.add(result.connection);
                result = result.next;
            }
            return route.toArray(new PathConnection[0]);
        }
        return new PathConnection[0];
    }

    public PathConnection findConnection(PathNode destination) {
        PathSearchResult result = this.findBestPath(destination);
        if (result.found && result.connection != null) {
            return new PathConnection(destination, result.distance, result.connection.junctionName);
        }
        return null;
    }

    private PathSearchResult findBestPath(PathNode destination) {
        PathSearchResult result = this.findBestPath(new PathSearchOperation(destination), 0.0);
        if (result == PathSearchResult.DUMMY_NOT_FOUND) {
            result = PathSearchResult.missing(this, destination);
        }
        result.cache();
        return result;
    }

    private PathSearchResult findBestPath(PathSearchOperation search, double startDistance) {
        if (startDistance > search.maxTotalDistance) {
            return PathSearchResult.DUMMY_NOT_FOUND;
        }
        if (this.lastSearch == search && startDistance > this.lastSearchStartDistance) {
            return PathSearchResult.DUMMY_NOT_FOUND;
        }
        this.lastSearch = search;
        this.lastSearchStartDistance = startDistance;
        if (this == search.destination) {
            this.lastSearchResult = PathSearchResult.self(this);
            return search.acceptResult(startDistance, this.lastSearchResult);
        }
        this.lastSearchResult = this.world.findCachedSearchResult(this, search.destination);
        PathSearchResult result = this.lastSearchResult;
        if (result != PathSearchResult.DUMMY_NOT_FOUND) {
            return search.acceptResult(startDistance, result);
        }
        for (PathConnection neighbour : this.neighbors) {
            PathSearchResult neigh_result = neighbour.destination.findBestPath(search, startDistance + neighbour.distance);
            if (!neigh_result.found) continue;
            this.lastSearchResult = PathSearchResult.chain(this, search.destination, neighbour, neigh_result);
        }
        return this.lastSearchResult;
    }

    public PathConnection addNeighbour(PathNode to, double distance, String junctionName) {
        PathConnection conn;
        Iterator<PathConnection> iter = this.neighbors.iterator();
        while (iter.hasNext()) {
            conn = iter.next();
            if (conn.destination != to) continue;
            if (conn.distance <= distance) {
                return conn;
            }
            iter.remove();
            break;
        }
        conn = new PathConnection(to, distance, junctionName);
        this.addNeighbourFast(conn);
        this.world.getProvider().scheduleNodeIfNotRecentlyRouted(to);
        this.world.markChanged();
        return conn;
    }

    protected void addNeighbourFast(PathConnection connection) {
        this.neighbors.add(connection);
    }

    public void clear() {
        this.neighbors.clear();
        for (PathNode node : this.world.getNodes()) {
            Iterator<PathConnection> iter = node.neighbors.iterator();
            while (iter.hasNext()) {
                if (iter.next().destination != this) continue;
                iter.remove();
            }
        }
        this.world.markChanged();
    }

    public void removeName(String name) {
        if (!this.names.remove(name)) {
            return;
        }
        this.world.removeNodeName(this, name);
        if (PathProvider.DEBUG_MODE) {
            String dbg = "NODE " + this.location + " NO LONGER HAS NAME " + name;
            if (this.names.isEmpty()) {
                dbg = dbg + " AND IS NOW BEING REMOVED (NO NAMES)";
            }
            this.world.getTrainCarts().log(Level.INFO, dbg);
        }
        if (this.names.isEmpty() && !this.containsSwitcher()) {
            this.remove();
        }
    }

    public void remove() {
        this.clear();
        this.world.removeFromMapping(this);
    }

    public boolean containsName(String name) {
        return this.names.contains(name);
    }

    public boolean containsOnlySwitcher() {
        return this.names.isEmpty() && this.containsSwitcher();
    }

    public Collection<String> getNames() {
        return this.names;
    }

    public BlockLocation getRailLocation() {
        return this.location;
    }

    public Collection<PathConnection> getNeighbours() {
        return this.neighbors;
    }

    public Map<PathConnection, List<PathConnection>> getDeepNeighbours() {
        HashMap<PathNode, PathConnection> connections = new HashMap<PathNode, PathConnection>();
        for (PathConnection neighbour : this.neighbors) {
            connections.put(neighbour.destination, neighbour);
        }
        for (PathConnection neighbour : this.neighbors) {
            neighbour.destination.fillDeepNeighbours(connections, neighbour.junctionName, neighbour.distance);
        }
        connections.remove(this);
        HashMap<PathConnection, List<PathConnection>> result = new HashMap<PathConnection, List<PathConnection>>();
        block2: for (PathConnection connection : connections.values()) {
            boolean found = false;
            for (Map.Entry entry : result.entrySet()) {
                if (!((PathConnection)entry.getKey()).junctionName.equals(connection.junctionName)) continue;
                found = true;
                ((List)entry.getValue()).add(connection);
                break;
            }
            if (found) continue;
            for (PathConnection neighbour : this.neighbors) {
                if (!neighbour.junctionName.equals(connection.junctionName)) continue;
                ArrayList<PathConnection> list = new ArrayList<PathConnection>();
                list.add(connection);
                result.put(neighbour, list);
                continue block2;
            }
        }
        for (List collection : result.values()) {
            Collections.sort(collection, (c1, c2) -> Double.compare(c1.distance, c2.distance));
        }
        return result;
    }

    private void fillDeepNeighbours(Map<PathNode, PathConnection> connections, String junctionName, double startDistance) {
        for (PathConnection neighbour : this.neighbors) {
            double distance = startDistance + neighbour.distance;
            PathConnection previous = connections.get(neighbour.destination);
            if (previous != null && !(previous.distance > distance)) continue;
            connections.put(neighbour.destination, new PathConnection(neighbour.destination, distance, junctionName));
            neighbour.destination.fillDeepNeighbours(connections, junctionName, distance);
        }
    }

    public void rerouteConnectedDeepRecursive() {
        boolean changed;
        HashSet<PathNode> reachable = new HashSet<PathNode>();
        this.addReachable(reachable);
        do {
            changed = false;
            for (PathNode node : this.world.getNodes()) {
                if (reachable.contains(node)) continue;
                for (PathConnection neighbour : node.neighbors) {
                    if (!reachable.contains(neighbour.destination)) continue;
                    changed = true;
                    node.addReachable(reachable);
                }
            }
        } while (changed);
        for (PathNode node : reachable) {
            node.neighbors.clear();
            this.world.removeFromMapping(node);
            this.world.getProvider().discoverFromRail(node.location);
        }
    }

    public void rerouteConnected() {
        this.clear();
        this.world.removeFromMapping(this);
        this.world.getProvider().discoverFromRail(this.location);
    }

    private void addReachable(Set<PathNode> reachable) {
        if (reachable.add(this)) {
            for (PathConnection neighbour : this.neighbors) {
                neighbour.destination.addReachable(reachable);
            }
        }
    }

    public boolean containsSwitcher() {
        return this.isRailSwitchable;
    }

    public void addSwitcher() {
        if (PathProvider.DEBUG_MODE && !this.isRailSwitchable) {
            this.world.getTrainCarts().log(Level.INFO, "NODE AT " + this.location.toString() + " ADDED SWITCHER");
        }
        this.isRailSwitchable = true;
    }

    public String getName() {
        if (!this.names.isEmpty()) {
            return this.names.iterator().next();
        }
        if (this.containsSwitcher()) {
            return this.location.toString();
        }
        return null;
    }

    public String getDisplayName() {
        return PathNode.formatDisplayName(this.location, this.names);
    }

    public String toString() {
        return this.getDisplayName();
    }

    public void addName(String name) {
        if (this.names.add(name)) {
            if (PathProvider.DEBUG_MODE) {
                this.world.getTrainCarts().log(Level.INFO, "NODE AT " + this.location.toString() + " ADDED DESTINATION " + name);
            }
            this.world.addNodeName(this, name);
        }
    }

    public PathNodeSnapshot getSnapshot() {
        return new PathNodeSnapshot(new HashSet<String>(this.names), this.location, this.isRailSwitchable);
    }

    protected static String formatDisplayName(BlockLocation location, Set<String> names) {
        if (names.isEmpty()) {
            return "[" + location.x + "/" + location.y + "/" + location.z + "]";
        }
        if (names.size() == 1) {
            return names.iterator().next();
        }
        StringBuilder builder = new StringBuilder(names.size() * 15);
        builder.append('{');
        for (String name : names) {
            if (builder.length() > 1) {
                builder.append("/");
            }
            builder.append(name);
        }
        builder.append('}');
        return builder.toString();
    }

    private static class PathSearchOperation {
        public final PathNode destination;
        public double maxTotalDistance = Double.MAX_VALUE;

        public PathSearchOperation(PathNode destination) {
            this.destination = destination;
        }

        public PathSearchResult acceptResult(double startDistance, PathSearchResult result) {
            if (!result.found) {
                return result;
            }
            double total = startDistance + result.distance;
            if (total < this.maxTotalDistance) {
                this.maxTotalDistance = total;
                return result;
            }
            return PathSearchResult.DUMMY_NOT_FOUND;
        }
    }
}

