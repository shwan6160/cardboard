/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.pathfinding;

import com.bergerkiller.bukkit.tc.pathfinding.PathConnection;
import com.bergerkiller.bukkit.tc.pathfinding.PathNode;

final class PathSearchResult {
    public static final PathSearchResult DUMMY_NOT_FOUND = new PathSearchResult(null, null, null, null, Double.MAX_VALUE, false);
    public final PathNode node;
    public final PathNode destination;
    public final PathConnection connection;
    public final PathSearchResult next;
    public final double distance;
    public final boolean found;
    private boolean needsToBeCached;

    public static PathSearchResult self(PathNode node) {
        PathSearchResult r = new PathSearchResult(node, node, null, null, 0.0, true);
        r.needsToBeCached = false;
        return r;
    }

    public static PathSearchResult missing(PathNode node, PathNode destination) {
        return new PathSearchResult(node, destination, null, null, Double.MAX_VALUE, false);
    }

    public static PathSearchResult chain(PathNode node, PathNode destination, PathConnection connection, PathSearchResult next) {
        return new PathSearchResult(node, destination, connection, next, connection.distance + next.distance, true);
    }

    private PathSearchResult(PathNode node, PathNode destination, PathConnection connection, PathSearchResult next, double distance, boolean found) {
        this.node = node;
        this.destination = destination;
        this.connection = connection;
        this.next = next;
        this.distance = distance;
        this.found = found;
        this.needsToBeCached = true;
    }

    public void cache() {
        PathSearchResult r = this;
        while (r != null && r.needsToBeCached) {
            r.needsToBeCached = false;
            r.node.getWorld().cacheSearchResult(r);
            r = r.next;
        }
    }

    static {
        PathSearchResult.DUMMY_NOT_FOUND.needsToBeCached = false;
    }
}

