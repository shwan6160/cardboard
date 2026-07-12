/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.offline.OfflineWorld
 */
package com.bergerkiller.bukkit.tc.offline.train;

import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class OfflineGroupWorld
implements Iterable<OfflineGroup> {
    protected final OfflineWorld world;

    public OfflineGroupWorld(OfflineWorld world) {
        this.world = world;
    }

    public abstract Collection<OfflineGroup> getGroups();

    public OfflineWorld getWorld() {
        return this.world;
    }

    @Override
    public Iterator<OfflineGroup> iterator() {
        return this.getGroups().iterator();
    }

    public boolean isEmpty() {
        return this.getGroups().isEmpty();
    }

    public int totalGroupCount() {
        return this.getGroups().size();
    }

    public int totalMemberCount() {
        int count = 0;
        for (OfflineGroup group : this.getGroups()) {
            count += group.members.length;
        }
        return count;
    }

    public static OfflineGroupWorld snapshot(OfflineWorld world, Collection<OfflineGroup> groups) {
        final List<OfflineGroup> snapshotGroups = Collections.unmodifiableList(new ArrayList<OfflineGroup>(groups));
        return new OfflineGroupWorld(world){

            @Override
            public Collection<OfflineGroup> getGroups() {
                return snapshotGroups;
            }
        };
    }

    public static List<OfflineGroupWorld> snapshot(Map<OfflineWorld, List<OfflineGroup>> groupsByWorld) {
        ArrayList<OfflineGroupWorld> worldsList = new ArrayList<OfflineGroupWorld>(groupsByWorld.size());
        for (Map.Entry<OfflineWorld, List<OfflineGroup>> entry : groupsByWorld.entrySet()) {
            worldsList.add(OfflineGroupWorld.snapshot(entry.getKey(), (Collection<OfflineGroup>)entry.getValue()));
        }
        return Collections.unmodifiableList(worldsList);
    }

    public static List<OfflineGroupWorld> mergeSnapshots(List<OfflineGroupWorld> first, List<OfflineGroupWorld> second) {
        if (first.isEmpty()) {
            return second;
        }
        if (second.isEmpty()) {
            return first;
        }
        IdentityHashMap<OfflineWorld, List<OfflineGroup>> merged = new IdentityHashMap<OfflineWorld, List<OfflineGroup>>(Math.max(first.size(), second.size()));
        for (OfflineGroupWorld world : first) {
            merged.computeIfAbsent(world.getWorld(), w -> new ArrayList(world.getGroups().size())).addAll(world.getGroups());
        }
        for (OfflineGroupWorld world : second) {
            merged.computeIfAbsent(world.getWorld(), w -> new ArrayList(world.getGroups().size())).addAll(world.getGroups());
        }
        return OfflineGroupWorld.snapshot(merged);
    }
}

