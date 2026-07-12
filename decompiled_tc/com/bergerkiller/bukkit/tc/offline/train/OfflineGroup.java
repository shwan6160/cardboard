/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.chunk.ForcedChunk
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.offline.OfflineWorld
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.LongHashSet
 *  com.bergerkiller.bukkit.common.wrappers.LongHashSet$LongIterator
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.tc.offline.train;

import com.bergerkiller.bukkit.common.chunk.ForcedChunk;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroupWorldLive;
import com.bergerkiller.bukkit.tc.offline.train.OfflineMember;
import com.bergerkiller.bukkit.tc.offline.train.format.OfflineDataBlock;
import com.bergerkiller.bukkit.tc.properties.TrainPropertiesStore;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.LongConsumer;
import java.util.logging.Level;
import org.bukkit.World;

public final class OfflineGroup {
    public final String name;
    public final OfflineWorld world;
    public final List<OfflineDataBlock> actions;
    public final List<OfflineDataBlock> skippedSigns;
    public final OfflineMember[] members;
    private LongHashSet chunks = null;
    private LongHashSet loadedChunks = null;
    private boolean loaded;
    private boolean isBeingRemoved = false;

    public static OfflineGroup save(MinecartGroup group) {
        try {
            return new OfflineGroup(group);
        }
        catch (IOException ex) {
            throw new RuntimeException("Unexpected IO Exception", ex);
        }
    }

    private OfflineGroup(MinecartGroup group) throws IOException {
        this(group.getProperties().getTrainName(), OfflineWorld.of((World)group.getWorld()), group.getTrainCarts().getActionRegistry().saveTracker(group.getActions()), group.getTrainCarts().getTrackedSignLookup().serializeUniqueKeys(group.getSignTracker().getSignSkipTracker().getSkippedSigns(), "skipped-sign", RailLookup.TrackedSign::getUniqueKey), group, OfflineMember::new);
    }

    <T> OfflineGroup(String name, OfflineWorld world, List<OfflineDataBlock> actions, List<OfflineDataBlock> skippedSigns, Collection<T> memberData, MemberFactory<T> memberFactory) throws IOException {
        this.name = name;
        this.world = world;
        this.actions = actions;
        this.skippedSigns = skippedSigns;
        this.members = memberFactory.createMany(this, memberData);
        this.loaded = false;
    }

    private OfflineGroup(OfflineGroup original, String newName) {
        this.name = newName;
        this.world = original.world;
        this.members = original.members;
        this.chunks = original.chunks;
        this.loadedChunks = original.loadedChunks;
        this.loaded = original.loaded;
        this.isBeingRemoved = original.isBeingRemoved;
        this.actions = original.actions;
        this.skippedSigns = original.skippedSigns;
    }

    public OfflineGroup withName(String newName) {
        return new OfflineGroup(this, newName);
    }

    public OfflineGroup withMembers(List<OfflineMember> newMembers) {
        try {
            return new OfflineGroup(this.name, this.world, this.actions, this.skippedSigns, newMembers, (cgroup, cmember) -> cmember);
        }
        catch (IOException ex) {
            throw new RuntimeException("Unexpected io exception", ex);
        }
    }

    public boolean isLoadedAsGroup() {
        return this.loaded;
    }

    public LongHashSet getChunks() {
        LongHashSet chunks = this.chunks;
        if (chunks == null) {
            int chunkCount = 25 + (int)(0.0 * (double)this.members.length);
            chunks = new LongHashSet(chunkCount);
            for (OfflineMember wm : this.members) {
                for (int x = wm.cx - 2; x <= wm.cx + 2; ++x) {
                    for (int z = wm.cz - 2; z <= wm.cz + 2; ++z) {
                        chunks.add(MathUtil.longHashToLong((int)x, (int)z));
                    }
                }
            }
            this.chunks = chunks;
        }
        return chunks;
    }

    public LongHashSet getLoadedChunks() {
        LongHashSet loadedChunks = this.loadedChunks;
        if (loadedChunks == null) {
            this.loadedChunks = loadedChunks = new LongHashSet(this.getChunks().size());
        }
        return loadedChunks;
    }

    public void forAllChunks(ChunkCoordConsumer action) {
        LongHashSet.LongIterator iter = this.getChunks().longIterator();
        while (iter.hasNext()) {
            long chunk = iter.next();
            action.accept(MathUtil.longHashMsw((long)chunk), MathUtil.longHashLsw((long)chunk));
        }
    }

    public void forAllChunks(LongConsumer action) {
        LongHashSet.LongIterator iter = this.getChunks().longIterator();
        while (iter.hasNext()) {
            action.accept(iter.next());
        }
    }

    public boolean isMoving() {
        for (OfflineMember member : this.members) {
            if (!member.isMoving()) continue;
            return true;
        }
        return false;
    }

    void setBeingRemoved() {
        this.isBeingRemoved = true;
    }

    public boolean testFullyLoaded() {
        if (this.isBeingRemoved) {
            return false;
        }
        return this.getLoadedChunks().size() == this.getChunks().size();
    }

    protected boolean updateLoadedChunks(OfflineGroupWorldLive offlineMap) {
        LongHashSet loadedChunks = this.getLoadedChunks();
        loadedChunks.clear();
        World world = this.world.getLoadedWorld();
        if (world != null && offlineMap.canRestoreGroups()) {
            this.forAllChunks((long chunk) -> {
                if (WorldUtil.isChunkEntitiesLoaded((World)world, (int)MathUtil.longHashMsw((long)chunk), (int)MathUtil.longHashLsw((long)chunk))) {
                    loadedChunks.add(chunk);
                }
            });
            if (offlineMap.getManager().lastUnloadChunk != null) {
                loadedChunks.remove(offlineMap.getManager().lastUnloadChunk.longValue());
            }
            return this.testFullyLoaded();
        }
        return false;
    }

    public List<ForcedChunk> forceLoadChunks(World world) {
        ArrayList<ForcedChunk> chunks = new ArrayList<ForcedChunk>();
        this.forAllChunks((int cx, int cz) -> chunks.add(WorldUtil.forceChunkLoaded((World)world, (int)cx, (int)cz)));
        return chunks;
    }

    public MinecartGroup create(TrainCarts traincarts) {
        ArrayList groupMembers = new ArrayList(this.members.length);
        int missingNo = 0;
        int cx = 0;
        int cz = 0;
        World world = this.world.getLoadedWorld();
        for (OfflineMember offlineMember : this.members) {
            MinecartMember<?> mm = offlineMember.create(traincarts, world);
            if (mm != null) {
                groupMembers.add(mm);
                continue;
            }
            ++missingNo;
            cx = offlineMember.cx;
            cz = offlineMember.cz;
        }
        if (missingNo > 0) {
            traincarts.log(Level.WARNING, missingNo + " carts of group '" + this.name + "' are missing near chunk [" + cx + ", " + cz + "]! (externally edited?)");
        }
        this.loaded = true;
        if (groupMembers.isEmpty()) {
            TrainPropertiesStore.remove(this.name);
            return null;
        }
        MinecartGroup group = MinecartGroup.create(this.name, groupMembers.toArray(new MinecartMember[0]));
        this.load(group);
        for (int i = 0; i < this.members.length; ++i) {
            MinecartMember<?> member;
            OfflineMember offlineMember = this.members[i];
            if (i < group.size() && offlineMember.entityUID.equals(((CommonMinecart)((MinecartMember)group.get(i)).getEntity()).getUniqueId())) {
                member = (MinecartMember<?>)group.get(i);
            } else {
                member = null;
                for (MinecartMember<?> groupMember : group) {
                    if (!offlineMember.entityUID.equals(((CommonMinecart)groupMember.getEntity()).getUniqueId())) continue;
                    member = groupMember;
                    break;
                }
                if (member == null) continue;
            }
            offlineMember.load(member);
        }
        return group;
    }

    void load(MinecartGroup group) {
        group.getTrainCarts().getActionRegistry().loadTracker(group.getActions(), this.actions);
        for (Object signKey : group.getTrainCarts().getTrackedSignLookup().deserializeUniqueKeys(this.skippedSigns)) {
            group.getSignTracker().addOfflineSkippedSignKey(signKey);
        }
        group.getSignTracker().clearUpdates();
    }

    @FunctionalInterface
    public static interface MemberFactory<T> {
        public OfflineMember create(OfflineGroup var1, T var2) throws IOException;

        default public OfflineMember[] createMany(OfflineGroup group, Collection<T> data) throws IOException {
            int index = 0;
            OfflineMember[] members = new OfflineMember[data.size()];
            for (T member : data) {
                members[index++] = this.create(group, member);
            }
            return members;
        }
    }

    @FunctionalInterface
    public static interface ChunkCoordConsumer {
        public void accept(int var1, int var2);
    }
}

