/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.chunk.ChunkFutureProvider
 *  com.bergerkiller.bukkit.common.chunk.ForcedChunk
 *  com.bergerkiller.bukkit.common.offline.OfflineWorld
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.LongHashMap
 *  org.bukkit.Chunk
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.offline.train;

import com.bergerkiller.bukkit.common.chunk.ChunkFutureProvider;
import com.bergerkiller.bukkit.common.chunk.ForcedChunk;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroup;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroupManager;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroupWorld;
import com.bergerkiller.bukkit.tc.offline.train.OfflineMember;
import com.bergerkiller.bukkit.tc.properties.TrainPropertiesStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class OfflineGroupWorldLive
extends OfflineGroupWorld {
    protected final OfflineGroupManager manager;
    private Set<OfflineGroup> groups = new HashSet<OfflineGroup>();
    private LongHashMap<HashSet<OfflineGroup>> groupmap = new LongHashMap();
    private Set<UUID> minecartEntityUUIDsBeingDestroyed = new HashSet<UUID>();
    private boolean isDuringWorldUnloadEvent = false;

    public OfflineGroupWorldLive(OfflineGroupManager manager, OfflineWorld world) {
        super(world);
        this.manager = manager;
    }

    public OfflineGroupManager getManager() {
        return this.manager;
    }

    @Override
    public Collection<OfflineGroup> getGroups() {
        return this.groups;
    }

    public OfflineGroupWorld createSnapshot() {
        return OfflineGroupWorldLive.snapshot(this.world, this.groups);
    }

    public void add(OfflineGroup group) {
        this.groups.add(group);
        group.forAllChunks(chunk -> {
            if (!group.getLoadedChunks().contains(chunk)) {
                this.getOrCreateChunk(chunk).add(group);
            }
        });
    }

    public void setIsDuringWorldUnloadEvent(boolean isDuringWorldUnloadEvent) {
        this.isDuringWorldUnloadEvent = isDuringWorldUnloadEvent;
    }

    public boolean canRestoreGroups() {
        return !this.isDuringWorldUnloadEvent && this.world.isLoaded();
    }

    public CompletableFuture<Boolean> destroyAsync(World world, OfflineGroup group) {
        ChunkFutureProvider futureProvider = ChunkFutureProvider.of((Plugin)TrainCarts.plugin);
        group.setBeingRemoved();
        List minecartEntityUUIDs = Stream.of(group.members).map(m -> m.entityUID).collect(Collectors.toList());
        HashSet minecartEntityUUIDsRemaining = new HashSet(minecartEntityUUIDs);
        this.minecartEntityUUIDsBeingDestroyed.addAll(minecartEntityUUIDs);
        CompletableFuture result = new CompletableFuture();
        List<ForcedChunk> chunks = group.forceLoadChunks(world);
        CompletableFuture[] chunkLoadEntitiesFuture = (CompletableFuture[])chunks.stream().map(forcedChunk -> futureProvider.whenEntitiesLoaded(world, forcedChunk.getX(), forcedChunk.getZ()).thenAccept(chunk -> {
            block4: {
                try {
                    if (minecartEntityUUIDsRemaining.isEmpty()) break block4;
                    for (Entity e : new ArrayList(WorldUtil.getEntities((Chunk)chunk))) {
                        if (!minecartEntityUUIDsRemaining.remove(e.getUniqueId())) continue;
                        e.remove();
                        if (!minecartEntityUUIDsRemaining.isEmpty()) continue;
                        result.complete(Boolean.TRUE);
                        break;
                    }
                }
                finally {
                    forcedChunk.close();
                }
            }
        })).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(chunkLoadEntitiesFuture).thenAccept(u -> result.complete(Boolean.FALSE));
        return result.thenApply(found -> {
            this.remove(group);
            TrainPropertiesStore.remove(group.name);
            this.minecartEntityUUIDsBeingDestroyed.removeAll(minecartEntityUUIDs);
            return found;
        });
    }

    public boolean isDestroyingMinecart(UUID minecartUUID) {
        return this.minecartEntityUUIDsBeingDestroyed.contains(minecartUUID);
    }

    public void remove(OfflineGroup group) {
        this.groups.remove(group);
        group.forAllChunks(chunk -> {
            Set<OfflineGroup> groups = this.getOrCreateChunk(chunk);
            if (groups != null) {
                groups.remove(group);
                if (groups.isEmpty()) {
                    this.groupmap.remove(chunk);
                }
            }
        });
    }

    public boolean removeCart(UUID memberUUID) {
        for (OfflineGroup group : this.groups) {
            for (OfflineMember member : group.members) {
                if (!member.entityUID.equals(memberUUID)) continue;
                ArrayList<OfflineMember> newMembers = new ArrayList<OfflineMember>();
                for (OfflineMember m : group.members) {
                    if (m.entityUID.equals(memberUUID)) continue;
                    newMembers.add(m);
                }
                this.remove(group);
                if (!newMembers.isEmpty()) {
                    this.add(group.withMembers(newMembers));
                }
                return true;
            }
        }
        return false;
    }

    public final OfflineGroup remove(String groupName) {
        for (OfflineGroup group : this.groups) {
            if (!group.name.equals(groupName)) continue;
            this.remove(group);
            return group;
        }
        return null;
    }

    public Set<OfflineGroup> removeFromChunk(Chunk chunk) {
        return this.removeFromChunk(chunk.getX(), chunk.getZ());
    }

    public Set<OfflineGroup> removeFromChunk(int x, int z) {
        return this.removeFromChunk(MathUtil.longHashToLong((int)x, (int)z));
    }

    public Set<OfflineGroup> removeFromChunk(long chunk) {
        Set rval = (Set)this.groupmap.remove(chunk);
        if (rval != null) {
            for (OfflineGroup group : rval) {
                group.getLoadedChunks().add(chunk);
            }
        }
        return rval;
    }

    public Set<OfflineGroup> getFromChunk(Chunk chunk) {
        return this.getFromChunk(chunk.getX(), chunk.getZ());
    }

    public Set<OfflineGroup> getFromChunk(int x, int z) {
        return this.getFromChunk(MathUtil.longHashToLong((int)x, (int)z));
    }

    public Set<OfflineGroup> getFromChunk(long chunk) {
        return (Set)this.groupmap.get(chunk);
    }

    public Set<OfflineGroup> getOrCreateChunk(long chunk) {
        HashSet rval = (HashSet)this.groupmap.get(chunk);
        if (rval == null) {
            rval = new HashSet(1);
            this.groupmap.put(chunk, rval);
        }
        return rval;
    }
}

