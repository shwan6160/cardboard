/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.BlockLocation
 *  com.bergerkiller.bukkit.common.collections.BlockMap
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.pathfinding;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.collections.BlockMap;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.pathfinding.PathNode;
import com.bergerkiller.bukkit.tc.pathfinding.PathNodeSnapshot;
import com.bergerkiller.bukkit.tc.pathfinding.PathProvider;
import com.bergerkiller.bukkit.tc.pathfinding.PathSearchResult;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.block.Block;

public class PathWorld
implements TrainCarts.Provider {
    private final PathProvider _provider;
    private final String _name;
    private final BlockMap<PathNode> _blockNodes;
    private final Map<String, PathNode> _nodes;
    private final Map<PathFromToKey, PathSearchResult> _cachedSearchResults;

    public PathWorld(PathProvider provider, String worldName) {
        this._provider = provider;
        this._name = worldName;
        this._blockNodes = new BlockMap();
        this._nodes = new HashMap<String, PathNode>();
        this._cachedSearchResults = new HashMap<PathFromToKey, PathSearchResult>();
    }

    protected void markChanged() {
        this._cachedSearchResults.clear();
        this._provider.markChanged();
    }

    @Override
    public TrainCarts getTrainCarts() {
        return this._provider.getTrainCarts();
    }

    public PathProvider getProvider() {
        return this._provider;
    }

    public String getName() {
        return this._name;
    }

    public PathNode getNodeAtRail(BlockLocation railLocation) {
        return (PathNode)this._blockNodes.get((Object)railLocation);
    }

    public PathNode getNodeAtRail(Block railBlock) {
        return (PathNode)this._blockNodes.get(railBlock);
    }

    public PathNode getNodeByName(String name) {
        return this._nodes.get(name);
    }

    public PathNode tryFindNodeAgain(PathNodeSnapshot snapshot) {
        PathNode atRail = this.getNodeAtRail(snapshot.getRailLocation());
        if (atRail != null) {
            return atRail;
        }
        for (String name : snapshot.getNames()) {
            PathNode foundNode = this.getNodeByName(name);
            if (foundNode == null) continue;
            return foundNode;
        }
        return null;
    }

    public Set<BlockLocation> getRailBlocks() {
        return this._blockNodes.keySet();
    }

    public Collection<PathNode> getNodes() {
        return this._blockNodes.values();
    }

    public PathNode removeAtRail(Block railBlock) {
        PathNode node = (PathNode)this._blockNodes.remove(railBlock);
        if (node != null) {
            node.remove();
        }
        return node;
    }

    public PathNode getOrCreateAtRail(BlockLocation location) {
        if (location == null) {
            return null;
        }
        PathNode node = this.getNodeAtRail(location);
        return node != null ? node : this.addNode(location);
    }

    public PathNode addNode(BlockLocation location) {
        PathNode node = new PathNode(this, location);
        this.addToMapping(node);
        this._provider.scheduleNode(node);
        this.markChanged();
        return node;
    }

    public void rerouteAll() {
        for (BlockLocation location : this.getRailBlocks()) {
            this._provider.discoverFromRail(location);
        }
        this.clearAll();
        this.markChanged();
    }

    public void rerouteFrom(List<String> destinationNames) {
        for (String name : destinationNames) {
            PathNode node = this.getNodeByName(name);
            if (node == null) continue;
            this._provider.discoverFromNode(node);
            this.removeFromMapping(node);
        }
    }

    public void clearAll() {
        this._nodes.clear();
        this._blockNodes.clear();
        this.markChanged();
    }

    protected void addNodeName(PathNode node, String name) {
        this._nodes.put(name, node);
        this.markChanged();
    }

    protected void removeNodeName(PathNode node, String name) {
        PathNode removed = this._nodes.remove(name);
        if (removed == node) {
            this.markChanged();
        } else if (removed != null) {
            this._nodes.put(name, removed);
        }
    }

    protected void addToMapping(PathNode node) {
        for (String name : node.getNames()) {
            this.addNodeName(node, name);
        }
        this._blockNodes.put((Object)node.location, (Object)node);
        this._nodes.put(node.location.toString(), node);
        this.markChanged();
    }

    protected void removeFromMapping(PathNode node) {
        for (String name : node.getNames()) {
            PathNode removed = this._nodes.remove(name);
            if (removed == null || removed == node) continue;
            this._nodes.put(name, removed);
        }
        PathNode removed = (PathNode)this._blockNodes.remove((Object)node.location);
        if (removed != null && removed != node) {
            this._blockNodes.put((Object)node.location, (Object)removed);
        } else if (removed != null) {
            this._nodes.remove(node.location.toString());
        }
        this.markChanged();
    }

    protected PathSearchResult findCachedSearchResult(PathNode node, PathNode destination) {
        return this._cachedSearchResults.getOrDefault(new PathFromToKey(node, destination), PathSearchResult.DUMMY_NOT_FOUND);
    }

    protected void cacheSearchResult(PathSearchResult result) {
        this._cachedSearchResults.put(new PathFromToKey(result.node, result.destination), result);
    }

    private static final class PathFromToKey {
        private final PathNode node;
        private final PathNode destination;

        public PathFromToKey(PathNode node, PathNode destination) {
            this.node = node;
            this.destination = destination;
        }

        public int hashCode() {
            return this.node.hashCode() + 31 * this.destination.hashCode();
        }

        public boolean equals(Object o) {
            PathFromToKey other = (PathFromToKey)o;
            return this.node == other.node && this.destination == other.destination;
        }
    }
}

