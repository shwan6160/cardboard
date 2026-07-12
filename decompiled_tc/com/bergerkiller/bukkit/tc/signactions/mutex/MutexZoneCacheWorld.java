/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.offline.OfflineWorld
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.wrappers.LongHashMap
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.tc.signactions.mutex;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.TrainPropertiesStore;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZone;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZonePath;
import com.bergerkiller.bukkit.tc.utils.TrackWalkingPoint;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import org.bukkit.World;

public class MutexZoneCacheWorld {
    private static final MutexZone[] NO_ZONES = new MutexZone[0];
    private final OfflineWorld world;
    protected final Map<SignSidePositionKey, MutexZone> bySignPosition = new HashMap<SignSidePositionKey, MutexZone>();
    protected final Map<PathingSignKey, MutexZonePath> byPathingKey = new HashMap<PathingSignKey, MutexZonePath>();
    private final LongHashMap<MutexZone[]> byChunk = new LongHashMap();
    private final Set<MutexZone> newZonesLive = new HashSet<MutexZone>();
    private List<MutexZone> newZones = Collections.emptyList();

    public MutexZoneCacheWorld(OfflineWorld world) {
        this.world = world;
    }

    public World getWorld() {
        return this.world.getLoadedWorld();
    }

    public OfflineWorld getOfflineWorld() {
        return this.world;
    }

    public MovingPoint track(IntVector3 blockPosition) {
        return new MovingPoint((arg_0, arg_1) -> this.byChunk.get(arg_0, arg_1), blockPosition.getChunkX(), blockPosition.getChunkZ());
    }

    public MutexZone find(IntVector3 position) {
        MutexZone[] inChunk = (MutexZone[])this.byChunk.get(position.getChunkX(), position.getChunkZ());
        if (inChunk != null) {
            for (MutexZone zone : inChunk) {
                if (!zone.containsBlock(position)) continue;
                return zone;
            }
        }
        return null;
    }

    public MutexZone findBySign(IntVector3 signPosition, boolean signFront) {
        return this.bySignPosition.get(new SignSidePositionKey(signPosition, signFront));
    }

    public List<MutexZone> getNewZones() {
        return this.newZones;
    }

    public boolean isMutexZoneNearby(IntVector3 block, int radius) {
        int chunkMinX = MathUtil.toChunk((int)(block.x - radius));
        int chunkMaxX = MathUtil.toChunk((int)(block.x + radius));
        int chunkMinZ = MathUtil.toChunk((int)(block.z - radius));
        int chunkMaxZ = MathUtil.toChunk((int)(block.z + radius));
        for (int cz = chunkMinZ; cz <= chunkMaxZ; ++cz) {
            for (int cx = chunkMinX; cx <= chunkMaxX; ++cx) {
                MutexZone[] zonesAtChunk = (MutexZone[])this.byChunk.get(cx, cz);
                if (zonesAtChunk == null) continue;
                for (MutexZone zone : zonesAtChunk) {
                    if (!zone.isNearby(block, radius)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    public List<MutexZone> findNearbyZones(IntVector3 block, int radius) {
        List<MutexZone> result = Collections.emptyList();
        int chunkMinX = block.x - radius >> 4;
        int chunkMaxX = block.x + radius >> 4;
        int chunkMinZ = block.z - radius >> 4;
        int chunkMaxZ = block.z + radius >> 4;
        for (int cz = chunkMinZ; cz <= chunkMaxZ; ++cz) {
            for (int cx = chunkMinX; cx <= chunkMaxX; ++cx) {
                MutexZone[] zonesAtChunk = (MutexZone[])this.byChunk.get(cx, cz);
                if (zonesAtChunk == null) continue;
                for (MutexZone zone : zonesAtChunk) {
                    if (!zone.isNearby(block, radius)) continue;
                    if (result.isEmpty()) {
                        result = new ArrayList<MutexZone>();
                    }
                    result.add(zone);
                }
            }
        }
        return result;
    }

    public void add(MutexZone zone) {
        zone.addToWorld(this);
        this.newZonesLive.add(zone);
        this.mapToChunks(zone, false);
    }

    protected void remove(MutexZone zone) {
        this.newZonesLive.remove(zone);
        int newZoneIdx = this.newZones.indexOf(zone);
        if (newZoneIdx != -1) {
            ArrayList<MutexZone> copy = new ArrayList<MutexZone>(this.newZones);
            copy.remove(newZoneIdx);
            this.newZones = copy;
        }
        this.unmapFromChunks(zone);
    }

    protected void addNewChunks(MutexZone zone) {
        this.mapToChunks(zone, true);
    }

    private void mapToChunks(MutexZone zone, boolean checkDuplicates) {
        MutexZone[] singleZone = new MutexZone[]{zone};
        zone.forAllContainedChunks((cx, cz) -> {
            long key = MathUtil.longHashToLong((int)cx, (int)cz);
            MutexZone[] atChunk = (MutexZone[])this.byChunk.get(key);
            if (atChunk == null) {
                this.byChunk.put(key, (Object)singleZone);
            } else if (!checkDuplicates || !this.isChunkInArray(atChunk, zone)) {
                int len = atChunk.length;
                atChunk = Arrays.copyOf(atChunk, len + 1);
                atChunk[len] = zone;
                this.byChunk.put(key, (Object)atChunk);
            }
        });
    }

    private void unmapFromChunks(MutexZone zone) {
        zone.forAllContainedChunks((cx, cz) -> {
            long key = MathUtil.longHashToLong((int)cx, (int)cz);
            Object[] atChunk = (MutexZone[])this.byChunk.remove(key);
            if (atChunk != null && (atChunk.length > 1 || atChunk[0] != zone)) {
                for (int i = atChunk.length - 1; i >= 0; --i) {
                    if (atChunk[i] != zone) continue;
                    atChunk = (MutexZone[])LogicUtil.removeArrayElement((Object[])atChunk, (int)i);
                }
                this.byChunk.put(key, (Object)atChunk);
            }
        });
    }

    private boolean isChunkInArray(MutexZone[] zones, MutexZone zone) {
        for (MutexZone zoneInZones : zones) {
            if (zoneInZones != zone) continue;
            return true;
        }
        return false;
    }

    public MutexZone removeAtSign(IntVector3 signPosition, boolean front) {
        MutexZone zone = this.bySignPosition.remove(new SignSidePositionKey(signPosition, front));
        if (zone != null) {
            this.remove(zone);
        }
        return zone;
    }

    public MutexZonePath getOrCreatePathingMutex(RailLookup.TrackedSign sign, MinecartGroup group, IntVector3 initialBlock, UnaryOperator<MutexZonePath.OptionsBuilder> optionsBuilder) {
        TrainProperties trainProperties = group.getProperties();
        MutexZonePath path = this.byPathingKey.get(PathingSignKey.of(sign.getUniqueKey(), trainProperties));
        if (path != null) {
            return path;
        }
        path = new MutexZonePath(group.getTrainCarts(), sign, trainProperties, (MutexZonePath.OptionsBuilder)optionsBuilder.apply(MutexZonePath.createOptions()));
        path.addBlock(initialBlock);
        this.add(path);
        return path;
    }

    public void clear() {
        this.bySignPosition.clear();
        this.byPathingKey.clear();
        this.byChunk.clear();
    }

    public void onTick() {
        this.updatePathingMutexes();
        if (this.newZonesLive.isEmpty()) {
            this.newZones = Collections.emptyList();
        } else {
            this.newZones = new ArrayList<MutexZone>(this.newZonesLive);
            this.newZonesLive.clear();
        }
    }

    private void updatePathingMutexes() {
        if (this.byPathingKey.isEmpty()) {
            return;
        }
        int expireTick = CommonUtil.getServerTicks() - 2;
        Iterator<MutexZonePath> iter = this.byPathingKey.values().iterator();
        while (iter.hasNext()) {
            MutexZonePath zonePath = iter.next();
            if (!zonePath.isExpired(expireTick)) continue;
            iter.remove();
            this.remove(zonePath);
        }
    }

    public static final class MovingPoint {
        private final MutexZoneByChunkGetter byChunkGetter;
        private int chunkX;
        private int chunkZ;
        private MutexZone[] chunkZones;

        public MovingPoint(MutexZoneByChunkGetter byChunkGetter, int chunkX, int chunkZ) {
            this.byChunkGetter = byChunkGetter;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            MutexZone[] zones = byChunkGetter.getAt(chunkX, chunkZ);
            this.chunkZones = zones == null ? NO_ZONES : zones;
        }

        public MutexZoneResult get(TrackWalkingPoint walker) {
            RailPath.Position p1 = walker.state.position();
            RailPath.Position p2 = walker.currentRailPath.getEndOfPath(walker.state.railBlock(), p1);
            return this.get(p1, p2);
        }

        /*
         * WARNING - void declaration
         */
        public MutexZoneResult get(RailPath.Position p1, RailPath.Position p2) {
            void var18_25;
            List<Object> zones;
            p1.assertAbsolute();
            p2.assertAbsolute();
            int cx1 = MathUtil.toChunk((double)p1.posX);
            int cz1 = MathUtil.toChunk((double)p1.posZ);
            int cx2 = MathUtil.toChunk((double)p2.posX);
            int cz2 = MathUtil.toChunk((double)p2.posZ);
            if (cx1 == cx2 && cz1 == cz2) {
                zones = Arrays.asList(this.findZonesInChunk(cx1, cz1));
            } else {
                zones = Collections.emptyList();
                int cx_step = cx1 > cx2 ? -1 : 1;
                int cz_step = cz1 > cz2 ? -1 : 1;
                int cz = cz1;
                while (true) {
                    int cx = cx1;
                    while (true) {
                        for (MutexZone zone : this.findZonesInChunk(cx, cz)) {
                            if (zones.isEmpty()) {
                                zones = new ArrayList(4);
                                zones.add(zone);
                                continue;
                            }
                            if (zones.contains(zone)) continue;
                            zones.add(zone);
                        }
                        if (cx == cx2) break;
                        cx += cx_step;
                    }
                    if (cz == cz2) break;
                    cz += cz_step;
                }
            }
            if (zones.isEmpty()) {
                return null;
            }
            double motX = p2.posX - p1.posX;
            double motY = p2.posY - p1.posY;
            double motZ = p2.posZ - p1.posZ;
            double distance = p2.distance(p1);
            if (distance <= 1.0E-10) {
                IntVector3 blockPos = new IntVector3(p1.posX, p1.posY, p1.posZ);
                for (MutexZone mutexZone : zones) {
                    if (!mutexZone.containsBlock(blockPos)) continue;
                    return new MutexZoneResult(mutexZone, 0.0);
                }
                return null;
            }
            double f = 1.0 / distance;
            motX *= f;
            motY *= f;
            motZ *= f;
            MutexZoneResult mutexZoneResult = new MutexZoneResult(null, distance);
            for (MutexZone mutexZone : zones) {
                double dist = mutexZone.hitTest(p1.posX, p1.posY, p1.posZ, motX, motY, motZ);
                if (!(dist < var18_25.distance)) continue;
                MutexZoneResult mutexZoneResult2 = new MutexZoneResult(mutexZone, dist);
            }
            return var18_25.zone == null ? null : var18_25;
        }

        private MutexZone[] findZonesInChunk(int cx, int cz) {
            if (cx != this.chunkX || cz != this.chunkZ) {
                this.chunkX = cx;
                this.chunkZ = cz;
                MutexZone[] zones = this.byChunkGetter.getAt(cx, cz);
                if (zones == null) {
                    zones = NO_ZONES;
                }
                this.chunkZones = zones;
                return zones;
            }
            return this.chunkZones;
        }

        public boolean isNear() {
            if (this.chunkZones != NO_ZONES) {
                return true;
            }
            for (int cz = -1; cz <= 1; ++cz) {
                for (int cx = -1; cx <= 1; ++cx) {
                    if (cx == 0 && cz == 0 || this.byChunkGetter.getAt(this.chunkX + cx, this.chunkZ + cz) == null) continue;
                    return true;
                }
            }
            return false;
        }
    }

    @FunctionalInterface
    public static interface MutexZoneByChunkGetter {
        public MutexZone[] getAt(int var1, int var2);
    }

    protected static class SignSidePositionKey {
        public final IntVector3 position;
        public final boolean front;

        public static SignSidePositionKey ofZone(MutexZone zone) {
            return new SignSidePositionKey(zone.signBlock.getPosition(), zone.signFront);
        }

        public SignSidePositionKey(IntVector3 position, boolean front) {
            this.position = position;
            this.front = front;
        }

        public int hashCode() {
            return this.position.hashCode();
        }

        public boolean equals(Object o) {
            SignSidePositionKey other = (SignSidePositionKey)o;
            return this.position.equals((Object)other.position) && this.front == other.front;
        }
    }

    protected static final class PathingSignKey {
        public final Object uniqueKey;
        public final TrainProperties trainProperties;

        private PathingSignKey(Object signUniqueKey, TrainProperties trainProperties) {
            this.uniqueKey = signUniqueKey;
            this.trainProperties = trainProperties;
        }

        public static PathingSignKey of(Object signUniqueKey, TrainProperties trainProperties) {
            return new PathingSignKey(signUniqueKey, trainProperties);
        }

        public static Optional<PathingSignKey> readFrom(TrainCarts plugin, DataInputStream stream) throws IOException {
            Object signUniqueKey = plugin.getTrackedSignLookup().deserializeUniqueKey(Util.readByteArray(stream));
            TrainProperties trainProperties = TrainPropertiesStore.get(stream.readUTF());
            if (signUniqueKey == null || trainProperties == null) {
                return Optional.empty();
            }
            return Optional.of(PathingSignKey.of(signUniqueKey, trainProperties));
        }

        public boolean writeTo(TrainCarts plugin, DataOutputStream stream) throws IOException {
            if (this.trainProperties.isRemoved()) {
                return false;
            }
            byte[] data = plugin.getTrackedSignLookup().serializeUniqueKey(this.uniqueKey);
            if (data == null) {
                return false;
            }
            Util.writeByteArray(stream, data);
            stream.writeUTF(this.trainProperties.getTrainName());
            return true;
        }

        public int hashCode() {
            return this.uniqueKey.hashCode();
        }

        public boolean equals(Object o) {
            PathingSignKey other = (PathingSignKey)o;
            return this.uniqueKey.equals(other.uniqueKey) && this.trainProperties == other.trainProperties;
        }
    }

    public static class MutexZoneResult {
        public final MutexZone zone;
        public final double distance;

        public MutexZoneResult(MutexZone zone, double distance) {
            this.zone = zone;
            this.distance = distance;
        }
    }
}

