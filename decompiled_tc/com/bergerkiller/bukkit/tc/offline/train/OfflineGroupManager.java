/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.chunk.ForcedChunk
 *  com.bergerkiller.bukkit.common.offline.OfflineWorld
 *  com.bergerkiller.bukkit.common.offline.OfflineWorldMap
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Minecart
 */
package com.bergerkiller.bukkit.tc.offline.train;

import com.bergerkiller.bukkit.common.chunk.ForcedChunk;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.offline.OfflineWorldMap;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroup;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroupFileHandler;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroupWorld;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroupWorldLive;
import com.bergerkiller.bukkit.tc.offline.train.OfflineMember;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.TrainPropertiesStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;

public class OfflineGroupManager
implements TrainCarts.Provider {
    private final TrainCarts plugin;
    private final OfflineGroupFileHandler fileHandler;
    Long lastUnloadChunk = null;
    private boolean chunkLoadReq = false;
    private boolean isRefreshingGroups = false;
    private Map<String, OfflineGroup> containedTrains = new HashMap<String, OfflineGroup>();
    private HashSet<UUID> containedMinecarts = new HashSet();
    private final OfflineWorldMap<OfflineGroupWorldLiveImpl> worlds = new OfflineWorldMap();

    public OfflineGroupManager(TrainCarts plugin) {
        this.plugin = plugin;
        this.fileHandler = new OfflineGroupFileHandler(this);
    }

    @Override
    public TrainCarts getTrainCarts() {
        return this.plugin;
    }

    private OfflineGroupWorldLiveImpl get(OfflineWorld world) {
        OfflineGroupWorldLiveImpl map = (OfflineGroupWorldLiveImpl)this.worlds.get(world);
        if (map == null) {
            map = new OfflineGroupWorldLiveImpl(this, world);
            this.worlds.put(world, (Object)map);
        }
        return map;
    }

    private OfflineGroupWorldLiveImpl get(World world) {
        OfflineGroupWorldLiveImpl map = (OfflineGroupWorldLiveImpl)this.worlds.get(world);
        if (map == null) {
            map = new OfflineGroupWorldLiveImpl(this, OfflineWorld.of((World)world));
            this.worlds.put(world, (Object)map);
        }
        return map;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void unloadWorld(World world) {
        ArrayList<MinecartGroup> groupsOnWorld = new ArrayList<MinecartGroup>();
        for (MinecartGroup group2 : MinecartGroup.getGroups().cloneAsIterable()) {
            if (group2.getWorld() != world) continue;
            groupsOnWorld.add(group2);
        }
        OfflineGroupManager offlineGroupManager = this;
        synchronized (offlineGroupManager) {
            OfflineGroupWorldLiveImpl map = this.get(world);
            map.setIsDuringWorldUnloadEvent(true);
            try {
                groupsOnWorld.forEach(MinecartGroup::unload);
                map.getGroups().forEach(group -> group.updateLoadedChunks(map));
            }
            finally {
                map.setIsDuringWorldUnloadEvent(false);
            }
        }
    }

    public synchronized void loadChunk(Chunk chunk) {
        this.chunkLoadReq = true;
        if (this.isRefreshingGroups) {
            return;
        }
        OfflineGroupWorldLiveImpl map = (OfflineGroupWorldLiveImpl)this.worlds.get(chunk.getWorld());
        if (map != null && map.canRestoreGroups()) {
            if (map.isEmpty()) {
                this.worlds.remove(chunk.getWorld());
            } else {
                Set<OfflineGroup> groups = map.removeFromChunk(chunk);
                if (groups != null) {
                    for (OfflineGroup group : groups) {
                        if (!group.testFullyLoaded()) continue;
                        if (group.updateLoadedChunks(map)) {
                            map.restoreGroup(group);
                            continue;
                        }
                        map.add(group);
                    }
                }
            }
        }
    }

    public synchronized void unloadChunk(Chunk chunk) {
        long chunkCoordLong = MathUtil.longHashToLong((int)chunk.getX(), (int)chunk.getZ());
        this.lastUnloadChunk = chunkCoordLong;
        World chunkWorld = chunk.getWorld();
        for (MinecartGroup group : MinecartGroup.getGroups().cloneAsIterable()) {
            if (!group.isInChunk(chunkWorld, chunkCoordLong)) continue;
            OfflineGroupManager.unloadChunkForGroup(group, chunk);
        }
        for (Entity entity : WorldUtil.getEntities((Chunk)chunk)) {
            MinecartMember<?> member;
            if (!(entity instanceof Minecart) || (member = MinecartMemberStore.getFromEntity(entity)) == null || !member.isInteractable()) continue;
            OfflineGroupManager.unloadChunkForGroup(member.getGroup(), chunk);
        }
        OfflineGroupWorldLive map = (OfflineGroupWorldLive)this.worlds.get(chunk.getWorld());
        if (map != null) {
            if (map.isEmpty()) {
                this.worlds.remove(chunk.getWorld());
            } else {
                Set<OfflineGroup> groupset = map.getFromChunk(chunk);
                if (groupset != null) {
                    for (OfflineGroup group : groupset) {
                        group.getLoadedChunks().remove(MathUtil.longHashToLong((int)chunk.getX(), (int)chunk.getZ()));
                    }
                }
            }
        }
        this.lastUnloadChunk = null;
    }

    private static void unloadChunkForGroup(MinecartGroup group, Chunk chunk) {
        if (group.canUnload()) {
            group.unload();
        } else if (group.getChunkArea().containsChunk(chunk.getX(), chunk.getZ())) {
            group.getTrainCarts().log(Level.SEVERE, "Chunk " + chunk.getX() + "/" + chunk.getZ() + " of group " + group.getProperties().getTrainName() + " unloaded unexpectedly!");
        } else {
            group.getTrainCarts().log(Level.SEVERE, "Chunk " + chunk.getX() + "/" + chunk.getZ() + " of group " + group.getProperties().getTrainName() + " unloaded because chunk area wasn't up to date!");
        }
    }

    public synchronized void refresh() {
        for (World world : WorldUtil.getWorlds()) {
            this.refresh(world);
        }
    }

    public synchronized void refresh(World world) {
        OfflineGroupWorldLiveImpl map = (OfflineGroupWorldLiveImpl)this.worlds.get(world);
        if (map != null) {
            if (map.isEmpty()) {
                this.worlds.remove(world);
            } else if (map.canRestoreGroups()) {
                map.refreshGroups();
            }
        }
    }

    public synchronized List<OfflineGroupWorld> createSnapshot() {
        ArrayList<OfflineGroupWorld> worldSnapshots = new ArrayList<OfflineGroupWorld>(this.worlds.size());
        Iterator iter = this.worlds.values().iterator();
        while (iter.hasNext()) {
            OfflineGroupWorldLive world = (OfflineGroupWorldLive)iter.next();
            if (world.isEmpty()) {
                iter.remove();
                continue;
            }
            worldSnapshots.add(world.createSnapshot());
        }
        return Collections.unmodifiableList(worldSnapshots);
    }

    synchronized void load(List<OfflineGroupWorld> worlds) {
        int totalgroups = 0;
        int totalmembers = 0;
        int worldcount = worlds.size();
        for (OfflineGroupWorld world : worlds) {
            OfflineGroupWorldLiveImpl liveWorld = this.get(world.getWorld());
            for (OfflineGroup group : world.getGroups()) {
                ((OfflineGroupWorldLive)liveWorld).add(group);
                totalmembers += group.members.length;
                ++totalgroups;
            }
        }
        String msg = totalgroups + " Train";
        msg = totalgroups == 1 ? msg + " has" : msg + "s have";
        msg = msg + " been loaded in " + worldcount + " world";
        if (worldcount != 1) {
            msg = msg + "s";
        }
        msg = msg + ". (" + totalmembers + " Minecart";
        if (totalmembers != 1) {
            msg = msg + "s";
        }
        msg = msg + ")";
        this.plugin.log(Level.INFO, msg);
    }

    public synchronized Map<OfflineGroup, List<ForcedChunk>> getForceLoadedChunks() {
        HashMap<OfflineGroup, List<ForcedChunk>> chunks = new HashMap<OfflineGroup, List<ForcedChunk>>();
        for (World world : WorldUtil.getWorlds()) {
            chunks.putAll(this.getForceLoadedChunks(world));
        }
        return chunks;
    }

    public synchronized Map<OfflineGroup, List<ForcedChunk>> getForceLoadedChunks(World world) {
        HashMap<OfflineGroup, List<ForcedChunk>> chunks = new HashMap<OfflineGroup, List<ForcedChunk>>();
        OfflineGroupWorldLive map = (OfflineGroupWorldLive)this.worlds.get(world);
        if (map != null && !map.isEmpty() && map.canRestoreGroups()) {
            for (OfflineGroup group : map.getGroups()) {
                TrainProperties prop = TrainProperties.get(group.name);
                if (prop == null || !prop.isKeepingChunksLoaded() || TCConfig.keepChunksLoadedOnlyWhenMoving && !group.isMoving()) continue;
                chunks.put(group, group.forceLoadChunks(world));
            }
        }
        return chunks;
    }

    public boolean isDestroyingGroupOf(Minecart minecart) {
        return this.get(minecart.getWorld()).isDestroyingMinecart(minecart.getUniqueId());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CompletableFuture<Boolean> destroyGroupAsync(String groupName) {
        OfflineGroupWorldLive map;
        OfflineGroup group = this.containedTrains.get(groupName);
        if (group == null) {
            return CompletableFuture.completedFuture(Boolean.FALSE);
        }
        World world = group.world.getLoadedWorld();
        if (world == null) {
            this.removeGroup(groupName);
            TrainPropertiesStore.remove(groupName);
            return CompletableFuture.completedFuture(Boolean.TRUE);
        }
        OfflineGroupManager offlineGroupManager = this;
        synchronized (offlineGroupManager) {
            map = (OfflineGroupWorldLive)this.worlds.get(group.world);
            if (map == null) {
                return CompletableFuture.completedFuture(Boolean.FALSE);
            }
        }
        return map.destroyAsync(world, group);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CompletableFuture<Integer> destroyAllAsync(World world, boolean includingVanilla) {
        List<Object> offlineGroups;
        OfflineGroupWorldLive map;
        TrainCarts.Provider g2;
        if (TrainCarts.isWorldDisabled(world)) {
            return CompletableFuture.completedFuture(0);
        }
        int count = 0;
        for (TrainCarts.Provider g2 : MinecartGroup.getGroups().cloneAsIterable()) {
            if (((MinecartGroup)g2).getWorld() != world) continue;
            if (!((ArrayList)((Object)g2)).isEmpty()) {
                ++count;
            }
            ((MinecartGroup)g2).destroy();
        }
        if (includingVanilla) {
            count += OfflineGroupManager.destroyMinecartsInLoadedChunks(world);
        }
        int removedLoadedGroupCount = count;
        OfflineGroupManager.removeBuggedMinecarts(world);
        g2 = this;
        synchronized (g2) {
            map = (OfflineGroupWorldLive)this.worlds.get(world);
            offlineGroups = map == null ? Collections.emptyList() : new ArrayList<OfflineGroup>(map.getGroups());
        }
        if (offlineGroups.isEmpty()) {
            return CompletableFuture.completedFuture(removedLoadedGroupCount);
        }
        CompletableFuture[] destroyFutures = (CompletableFuture[])offlineGroups.stream().map(group -> map.destroyAsync(world, (OfflineGroup)group)).toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(destroyFutures).thenApply(unused -> {
            int count = removedLoadedGroupCount;
            for (CompletableFuture future : destroyFutures) {
                try {
                    if (!((Boolean)future.get()).booleanValue()) continue;
                    ++count;
                }
                catch (InterruptedException | ExecutionException e) {
                    this.plugin.getLogger().log(Level.SEVERE, "Unhandled error destroying carts", e);
                }
            }
            return count;
        });
    }

    public CompletableFuture<Integer> destroyAllAsync(boolean includingVanilla) {
        CompletableFuture[] futures = (CompletableFuture[])Bukkit.getWorlds().stream().map(world -> this.destroyAllAsync((World)world, includingVanilla)).toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(futures).thenApply(unused -> {
            int total = 0;
            for (CompletableFuture future : futures) {
                try {
                    total += ((Integer)future.get()).intValue();
                }
                catch (InterruptedException | ExecutionException e) {
                    this.plugin.getLogger().log(Level.SEVERE, "Unhandled error destroying carts", e);
                }
            }
            TrainProperties.clearAll();
            OfflineGroupManager offlineGroupManager = this;
            synchronized (offlineGroupManager) {
                this.worlds.clear();
            }
            return total;
        });
    }

    private static int destroyMinecartsInLoadedChunks(World world) {
        int count = 0;
        for (Chunk chunk : WorldUtil.getChunks((World)world)) {
            for (Entity e : chunk.getEntities()) {
                if (!(e instanceof Minecart) || e.isDead()) continue;
                e.remove();
                Util.markChunkDirty(chunk);
                if (MinecartMemberStore.getFromEntity(e) != null) continue;
                ++count;
            }
        }
        for (Entity e : world.getEntities()) {
            if (!(e instanceof Minecart) || e.isDead()) continue;
            e.remove();
            Chunk chunk = WorldUtil.getChunk((World)world, (int)EntityUtil.getChunkX((Entity)e), (int)EntityUtil.getChunkZ((Entity)e));
            if (chunk == null) continue;
            Util.markChunkDirty(chunk);
        }
        return count;
    }

    public static void removeBuggedMinecarts(World world) {
        HashSet<Entity> toRemove = new HashSet<Entity>();
        HashSet<Entity> worldentities = new HashSet<Entity>();
        for (Entity entity : WorldUtil.getEntities((World)world)) {
            worldentities.add(entity);
        }
        for (Chunk chunk : WorldUtil.getChunks((World)world)) {
            Iterator iter = WorldUtil.getEntities((Chunk)chunk).iterator();
            while (iter.hasNext()) {
                Entity e = (Entity)iter.next();
                if (worldentities.contains(e)) continue;
                iter.remove();
                toRemove.add(e);
            }
            for (Entity e : toRemove) {
                WorldUtil.removeEntity((Entity)e);
            }
            toRemove.clear();
        }
    }

    public void load() {
        this.fileHandler.load();
    }

    public void save(TrainCarts.SaveMode saveMode) {
        this.fileHandler.save(saveMode);
    }

    public synchronized void deinit() {
        this.worlds.clear();
        this.containedMinecarts.clear();
        this.containedTrains.clear();
    }

    public static OfflineGroup saveGroup(MinecartGroup group) {
        if (group == null || !group.isValid()) {
            return null;
        }
        World world = group.getWorld();
        if (world == null) {
            return null;
        }
        return OfflineGroup.save(group);
    }

    public static List<OfflineGroupWorld> saveAllGroups() {
        IdentityHashMap<OfflineWorld, List<OfflineGroup>> worlds = new IdentityHashMap<OfflineWorld, List<OfflineGroup>>();
        for (MinecartGroup group : MinecartGroupStore.getGroups().cloneAsIterable()) {
            OfflineGroup offlineGroup = OfflineGroupManager.saveGroup(group);
            if (offlineGroup == null) continue;
            worlds.computeIfAbsent(offlineGroup.world, w -> new ArrayList()).add(offlineGroup);
        }
        return OfflineGroupWorld.snapshot(worlds);
    }

    public synchronized void storeGroup(OfflineGroup group) {
        OfflineGroupWorldLiveImpl map = this.get(group.world);
        group.updateLoadedChunks(map);
        map.add(group);
    }

    public synchronized boolean containsMinecart(UUID uniqueId) {
        return this.containedMinecarts.contains(uniqueId);
    }

    public synchronized int getStoredMemberCount(World world) {
        OfflineGroupWorldLiveImpl map = (OfflineGroupWorldLiveImpl)this.worlds.get(world);
        return map == null ? 0 : map.totalMemberCount();
    }

    public synchronized int getStoredCount() {
        return this.containedTrains.size();
    }

    public synchronized int getStoredCountInLoadedWorlds() {
        int count = 0;
        for (OfflineGroupWorldLiveImpl map : this.worlds.values()) {
            if (!map.canRestoreGroups()) continue;
            count += map.totalGroupCount();
        }
        return count;
    }

    public synchronized boolean contains(String trainname) {
        return this.containedTrains.containsKey(trainname);
    }

    public synchronized boolean containsInLoadedWorld(String trainname) {
        OfflineGroup offlineGroup = this.containedTrains.get(trainname);
        return offlineGroup != null && offlineGroup.world.isLoaded();
    }

    public synchronized void rename(String oldtrainname, String newtrainname) {
        for (OfflineGroupWorldLive map : this.worlds.values()) {
            for (OfflineGroup group : map) {
                if (!group.name.equals(oldtrainname)) continue;
                map.remove(group);
                map.add(group.withName(newtrainname));
                return;
            }
        }
    }

    public synchronized void removeMember(UUID memberUUID) {
        block1: {
            OfflineGroupWorldLive map;
            if (!this.containedMinecarts.remove(memberUUID)) break block1;
            Iterator iterator = this.worlds.values().iterator();
            while (iterator.hasNext() && !(map = (OfflineGroupWorldLive)iterator.next()).removeCart(memberUUID)) {
            }
        }
    }

    public synchronized void removeGroup(String groupName) {
        OfflineGroupWorldLive map;
        OfflineGroup group;
        Iterator iterator = this.worlds.values().iterator();
        while (iterator.hasNext() && (group = (map = (OfflineGroupWorldLive)iterator.next()).remove(groupName)) == null) {
        }
    }

    public synchronized OfflineGroup findGroup(String groupName) {
        for (OfflineGroupWorldLive map : this.worlds.values()) {
            for (OfflineGroup group : map.getGroups()) {
                if (!group.name.equals(groupName)) continue;
                return group;
            }
        }
        return null;
    }

    public OfflineMember findMember(String groupName, UUID uuid) {
        OfflineGroup group = this.findGroup(groupName);
        if (group != null) {
            for (OfflineMember member : group.members) {
                if (!member.entityUID.equals(uuid)) continue;
                return member;
            }
        }
        return null;
    }

    private static final class OfflineGroupWorldLiveImpl
    extends OfflineGroupWorldLive {
        public OfflineGroupWorldLiveImpl(OfflineGroupManager manager, OfflineWorld world) {
            super(manager, world);
        }

        public void restoreGroup(OfflineGroup group) {
            this.remove(group);
            group.create(this.manager.plugin);
        }

        public void refreshGroups() {
            this.manager.isRefreshingGroups = true;
            ArrayList<OfflineGroup> groupsBuffer = new ArrayList<OfflineGroup>(this.totalGroupCount());
            try {
                do {
                    this.manager.chunkLoadReq = false;
                    groupsBuffer.clear();
                    groupsBuffer.addAll(this.getGroups());
                    for (OfflineGroup group : groupsBuffer) {
                        if (!group.updateLoadedChunks(this)) continue;
                        this.restoreGroup(group);
                    }
                } while (this.manager.chunkLoadReq);
            }
            catch (Throwable t) {
                this.manager.plugin.getLogger().log(Level.SEVERE, "Unhandled error handling train restoring", t);
            }
            this.manager.isRefreshingGroups = false;
        }

        @Override
        public void add(OfflineGroup group) {
            super.add(group);
            this.manager.containedTrains.put(group.name, group);
            for (OfflineMember member : group.members) {
                this.manager.containedMinecarts.add(member.entityUID);
            }
        }

        @Override
        public void remove(OfflineGroup group) {
            super.remove(group);
            this.manager.containedTrains.remove(group.name);
            for (OfflineMember member : group.members) {
                this.manager.containedMinecarts.remove(member.entityUID);
            }
        }
    }
}

