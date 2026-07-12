/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.BlockLocation
 *  com.bergerkiller.bukkit.common.MessageBuilder
 *  org.bukkit.ChatColor
 */
package com.bergerkiller.bukkit.tc.pathfinding;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.tc.pathfinding.PathNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;

public class PathNodeSnapshot
implements Comparable<PathNodeSnapshot> {
    private final Set<String> names;
    private final BlockLocation location;
    private final boolean isRailSwitchable;

    public PathNodeSnapshot(Set<String> names, BlockLocation location, boolean isRailSwitchable) {
        this.names = names;
        this.location = location;
        this.isRailSwitchable = isRailSwitchable;
    }

    public String getDisplayName() {
        return PathNode.formatDisplayName(this.location, this.names);
    }

    public Set<String> getNames() {
        return this.names;
    }

    public BlockLocation getRailLocation() {
        return this.location;
    }

    public String getWorldName() {
        return this.location.world;
    }

    public boolean containsSwitcher() {
        return this.isRailSwitchable;
    }

    public MessageBuilder getUpdateMessage(PathNode node) {
        MessageBuilder message;
        if (!this.isRailSwitchable && this.names.isEmpty()) {
            return null;
        }
        if (node == null) {
            MessageBuilder message2 = new MessageBuilder();
            this.appendInfo(message2, ChatColor.RED);
            message2.red(new Object[]{" was removed"});
            return message2;
        }
        List<String> removedNames = Collections.emptyList();
        List<String> addedNames = Collections.emptyList();
        for (String oldName : this.names) {
            if (node.containsName(oldName)) continue;
            if (removedNames.isEmpty()) {
                removedNames = new ArrayList();
            }
            removedNames.add(oldName);
        }
        for (String newName : node.getNames()) {
            if (this.names.contains(newName)) continue;
            if (addedNames.isEmpty()) {
                addedNames = new ArrayList();
            }
            addedNames.add(newName);
        }
        if (node.getRailLocation().equals((Object)this.getRailLocation())) {
            if (removedNames.isEmpty() && addedNames.isEmpty()) {
                return null;
            }
            message = new MessageBuilder();
            this.appendInfo(message, ChatColor.YELLOW);
            message.yellow(new Object[]{" was changed:"});
            if (!removedNames.isEmpty()) {
                message.newLine().yellow(new Object[]{" - "}).red(new Object[]{"Destinations removed: "});
                PathNodeSnapshot.appendNames(message, ChatColor.RED, removedNames);
            }
            if (!addedNames.isEmpty()) {
                message.newLine().yellow(new Object[]{" - "}).green(new Object[]{"Destinations added: "});
                PathNodeSnapshot.appendNames(message, ChatColor.GREEN, addedNames);
            }
            return message;
        }
        if (!this.names.isEmpty()) {
            message = new MessageBuilder();
            this.appendInfo(message, ChatColor.YELLOW);
            message.yellow(new Object[]{" was moved:"});
            message.newLine().yellow(new Object[]{" - Now at: "});
            PathNodeSnapshot.appendLocation(message, node.getRailLocation());
            if (!removedNames.isEmpty()) {
                message.newLine().yellow(new Object[]{" - "}).red(new Object[]{"Destinations removed: "});
                PathNodeSnapshot.appendNames(message, ChatColor.RED, removedNames);
            }
            if (!addedNames.isEmpty()) {
                message.newLine().yellow(new Object[]{" - "}).green(new Object[]{"Destinations added: "});
                PathNodeSnapshot.appendNames(message, ChatColor.GREEN, addedNames);
            }
            return message;
        }
        return null;
    }

    @Override
    public int compareTo(PathNodeSnapshot pathNodeSnapshot) {
        int comp = Integer.compare(this.names.size(), pathNodeSnapshot.names.size());
        if (comp != 0) {
            return comp;
        }
        comp = Boolean.compare(this.isRailSwitchable, pathNodeSnapshot.isRailSwitchable);
        if (comp != 0) {
            return comp;
        }
        if (this.names.size() == 1) {
            return this.names.iterator().next().compareTo(pathNodeSnapshot.names.iterator().next());
        }
        return 0;
    }

    public int hashCode() {
        return this.location.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof PathNodeSnapshot) {
            PathNodeSnapshot other = (PathNodeSnapshot)o;
            return this.names.equals(other.names) && this.location.equals((Object)other.location) && this.isRailSwitchable == other.isRailSwitchable;
        }
        return false;
    }

    public String toString() {
        return "PathNodeSnapshot{rail=" + this.location + ", names=" + this.names + "}";
    }

    private void appendInfo(MessageBuilder message, ChatColor color) {
        if (this.isRailSwitchable) {
            message.append(color, new String[]{"Switched "});
        }
        if (this.names.isEmpty()) {
            message.append(color, new String[]{"Node"});
        } else if (this.names.size() == 1) {
            message.append(color, new String[]{"Destination "});
            PathNodeSnapshot.appendNames(message, color, this.names);
        } else {
            message.append(color, new String[]{"Destinations ["});
            PathNodeSnapshot.appendNames(message, color, this.names);
            message.append(color, new String[]{"]"});
        }
        message.append(color, new String[]{" at "});
        PathNodeSnapshot.appendLocation(message, this.location);
    }

    private static void appendLocation(MessageBuilder message, BlockLocation location) {
        message.white(new Object[]{"[", location.x, "/", location.y, "/", location.z, "]"});
    }

    private static void appendNames(MessageBuilder message, ChatColor color, Collection<String> names) {
        boolean first = true;
        for (String name : names) {
            if (first) {
                first = false;
            } else {
                message.append(color, new String[]{", "});
            }
            message.white(new Object[]{name});
        }
    }
}

