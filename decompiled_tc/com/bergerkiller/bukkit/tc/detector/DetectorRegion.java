/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.BlockLocation
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.collections.BlockMap
 *  com.bergerkiller.bukkit.common.collections.ImplicitlySharedList
 *  com.bergerkiller.bukkit.common.config.DataReader
 *  com.bergerkiller.bukkit.common.config.DataWriter
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.StreamUtil
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.detector;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.BlockMap;
import com.bergerkiller.bukkit.common.collections.ImplicitlySharedList;
import com.bergerkiller.bukkit.common.config.DataReader;
import com.bergerkiller.bukkit.common.config.DataWriter;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.detector.DetectorListener;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.rails.WorldRailLookup;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

public final class DetectorRegion {
    private static boolean hasChanges = false;
    private static HashMap<UUID, DetectorRegion> regionsById = new HashMap();
    private static BlockMap<DetectorRegion[]> regions = new BlockMap();
    private final UUID id;
    private final String world;
    private final Set<IntVector3> coordinates;
    private final Set<MinecartMember<?>> members = new HashSet();
    private final ImplicitlySharedList<DetectorListener> listeners = new ImplicitlySharedList();

    private DetectorRegion(UUID uniqueId, String world, Set<IntVector3> coordinates) {
        this.world = world;
        this.id = uniqueId;
        this.coordinates = coordinates;
        regionsById.put(this.id, this);
        hasChanges = true;
        WorldRailLookup lookup = RailLookup.forWorldIfInitialized(Bukkit.getWorld((String)world));
        DetectorRegion[] singleRegion = new DetectorRegion[]{this};
        for (IntVector3 coord : this.coordinates) {
            BlockLocation block_coord = new BlockLocation(world, coord);
            DetectorRegion[] regionsAtBlock = (DetectorRegion[])regions.compute((Object)block_coord, (key, array) -> {
                if (array == null) {
                    return singleRegion;
                }
                int len = ((DetectorRegion[])array).length;
                array = Arrays.copyOf(array, len + 1);
                array[len] = this;
                return array;
            });
            if (!lookup.isValid()) continue;
            lookup.storeDetectorRegions(coord, regionsAtBlock);
        }
    }

    public void detectMinecarts() {
        World w = Bukkit.getServer().getWorld(this.world);
        if (w != null) {
            WorldRailLookup railLookup = RailLookup.forWorld(w);
            for (IntVector3 coord : this.coordinates) {
                List<MinecartMember<?>> members = railLookup.findMembersOnRail(coord);
                if (members.isEmpty()) continue;
                for (MinecartMember<?> mm : new ArrayList(members)) {
                    mm.getSignTracker().addToDetectorRegion(this);
                }
            }
        }
    }

    public static void fillRailLookup(WorldRailLookup railLookup) {
        String worldName = railLookup.getWorld().getName();
        for (Map.Entry entry : regions.entrySet()) {
            BlockLocation block = (BlockLocation)entry.getKey();
            if (!worldName.equals(block.world)) continue;
            railLookup.storeDetectorRegions(block.getCoordinates(), (DetectorRegion[])entry.getValue());
        }
    }

    public static List<DetectorRegion> getRegions(Block at) {
        DetectorRegion[] regionsAtBlock = (DetectorRegion[])regions.get(at);
        return regionsAtBlock == null ? Collections.emptyList() : Arrays.asList(regionsAtBlock);
    }

    public static void detectAllMinecarts() {
        for (DetectorRegion region : regionsById.values()) {
            region.detectMinecarts();
        }
    }

    public static DetectorRegion create(Collection<Block> blocks) {
        if (blocks.isEmpty()) {
            return null;
        }
        World world = null;
        HashSet<IntVector3> coords = new HashSet<IntVector3>(blocks.size());
        for (Block b : blocks) {
            if (world == null) {
                world = b.getWorld();
            } else if (world != b.getWorld()) continue;
            coords.add(new IntVector3(b));
        }
        return DetectorRegion.create(world, coords);
    }

    public static DetectorRegion create(World world, Set<IntVector3> coordinates) {
        return DetectorRegion.create(world.getName(), coordinates);
    }

    public static DetectorRegion create(String world, Set<IntVector3> coordinates) {
        IntVector3 coord;
        DetectorRegion[] list;
        Iterator<IntVector3> iterator = coordinates.iterator();
        if (iterator.hasNext() && (list = (DetectorRegion[])regions.get(world, coord = iterator.next())) != null) {
            for (DetectorRegion region : list) {
                if (!region.coordinates.containsAll(coordinates) || !coordinates.containsAll(region.coordinates)) continue;
                return region;
            }
        }
        return new DetectorRegion(UUID.randomUUID(), world, coordinates);
    }

    public static DetectorRegion getRegion(UUID uniqueId) {
        return regionsById.get(uniqueId);
    }

    public static void init(final TrainCarts plugin) {
        regionsById.clear();
        regions.clear();
        new DataReader((Plugin)plugin, "detectorregions.dat"){

            public void read(DataInputStream stream) throws IOException {
                for (int count = stream.readInt(); count > 0; --count) {
                    int coordcount;
                    UUID id = StreamUtil.readUUID((DataInputStream)stream);
                    String world = stream.readUTF();
                    HashSet<IntVector3> coords = new HashSet<IntVector3>(coordcount);
                    for (coordcount = stream.readInt(); coordcount > 0; --coordcount) {
                        coords.add(IntVector3.read((DataInputStream)stream));
                    }
                    new DetectorRegion(id, world, coords);
                }
                if (regionsById.size() == 1) {
                    plugin.log(Level.INFO, regionsById.size() + " detector rail region loaded covering " + regions.size() + " blocks");
                } else {
                    plugin.log(Level.INFO, regionsById.size() + " detector rail regions loaded covering " + regions.size() + " blocks");
                }
            }
        }.read();
        hasChanges = false;
    }

    public static void save(TrainCarts plugin, boolean autosave) {
        if (autosave && !hasChanges) {
            return;
        }
        new DataWriter((Plugin)plugin, "detectorregions.dat"){

            public void write(DataOutputStream stream) throws IOException {
                stream.writeInt(regionsById.size());
                for (DetectorRegion region : regionsById.values()) {
                    StreamUtil.writeUUID((DataOutputStream)stream, (UUID)region.id);
                    stream.writeUTF(region.world);
                    stream.writeInt(region.coordinates.size());
                    for (IntVector3 coord : region.coordinates) {
                        coord.write(stream);
                    }
                }
            }
        }.write();
        hasChanges = false;
    }

    public String getWorldName() {
        return this.world;
    }

    public Set<IntVector3> getCoordinates() {
        return this.coordinates;
    }

    private void cleanUnloadedMembers() {
        Iterator<MinecartMember<?>> iter = this.members.iterator();
        while (iter.hasNext()) {
            MinecartMember<?> mm = iter.next();
            if (!mm.isUnloaded()) continue;
            iter.remove();
            Block pos = null;
            if (mm.getEntity() != null) {
                pos = ((CommonMinecart)mm.getEntity()).getLocation().getBlock();
            } else {
                pos = mm.getRailTracker().getBlock();
                if (pos == null) {
                    pos = mm.getRailTracker().getLastBlock();
                }
            }
            String posStr = "Unknown";
            if (pos != null) {
                posStr = "[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]";
            }
            mm.getTrainCarts().getLogger().warning("[Detector] Purged unloaded Minecart at " + posStr);
        }
    }

    public Set<MinecartMember<?>> getMembers() {
        return this.members;
    }

    public boolean hasMembers() {
        return !this.members.isEmpty();
    }

    public Set<MinecartGroup> getGroups() {
        HashSet<MinecartGroup> rval = new HashSet<MinecartGroup>();
        this.cleanUnloadedMembers();
        for (MinecartMember<?> mm : this.members) {
            if (mm.getGroup() == null) continue;
            rval.add(mm.getGroup());
        }
        return rval;
    }

    public boolean hasGroups() {
        return !this.members.isEmpty();
    }

    public UUID getUniqueId() {
        return this.id;
    }

    public void register(DetectorListener listener) {
        this.listeners.add((Object)listener);
        listener.onRegister(this);
        for (MinecartMember<?> mm : this.members) {
            listener.onEnter(mm);
        }
        for (MinecartGroup group : this.getGroups()) {
            listener.onEnter(group);
        }
    }

    public void unregister(DetectorListener listener) {
        this.listeners.remove((Object)listener);
        for (MinecartMember<?> mm : this.members) {
            listener.onLeave(mm);
        }
        for (MinecartGroup group : this.getGroups()) {
            listener.onLeave(group);
        }
        listener.onUnregister(this);
    }

    public boolean isRegistered() {
        return !this.listeners.isEmpty();
    }

    private void onLeave(MinecartMember<?> mm) {
        for (DetectorListener listener : this.listeners.cloneAsIterable()) {
            listener.onLeave(mm);
        }
        if (mm.isUnloaded()) {
            return;
        }
        this.cleanUnloadedMembers();
        MinecartGroup group = mm.getGroup();
        for (MinecartMember<?> ex : this.members) {
            if (ex == mm || ex.getGroup() != group) continue;
            return;
        }
        for (DetectorListener listener : this.listeners.cloneAsIterable()) {
            listener.onLeave(group);
        }
    }

    private void onEnter(MinecartMember<?> mm) {
        for (DetectorListener listener : this.listeners.cloneAsIterable()) {
            listener.onEnter(mm);
        }
        if (mm.isUnloaded()) {
            return;
        }
        this.cleanUnloadedMembers();
        MinecartGroup group = mm.getGroup();
        for (MinecartMember<?> ex : this.members) {
            if (ex == mm || ex.getGroup() != group) continue;
            return;
        }
        for (DetectorListener listener : this.listeners.cloneAsIterable()) {
            listener.onEnter(group);
        }
    }

    public void unload(MinecartGroup group) {
        if (this.members.removeAll(group)) {
            for (DetectorListener listener : this.listeners.cloneAsIterable()) {
                listener.onUnload(group);
            }
        }
    }

    public void remove(MinecartMember<?> mm) {
        if (this.members.remove(mm)) {
            this.onLeave(mm);
        }
    }

    public boolean add(MinecartMember<?> mm) {
        if (this.members.add(mm)) {
            this.onEnter(mm);
            return true;
        }
        return false;
    }

    public void update(MinecartMember<?> member) {
        for (DetectorListener list : this.listeners.cloneAsIterable()) {
            list.onUpdate(member);
        }
    }

    public void update(MinecartGroup group) {
        for (DetectorListener list : this.listeners.cloneAsIterable()) {
            list.onUpdate(group);
        }
    }

    public void remove() {
        this.cleanUnloadedMembers();
        Iterator<MinecartMember<?>> iter = this.members.iterator();
        while (iter.hasNext()) {
            this.onLeave(iter.next());
            iter.remove();
        }
        regionsById.remove(this.id);
        hasChanges = true;
        WorldRailLookup lookup = RailLookup.forWorldIfInitialized(Bukkit.getWorld((String)this.world));
        for (IntVector3 coord : this.coordinates) {
            BlockLocation block_coord = new BlockLocation(this.world, coord);
            DetectorRegion[] regionsAtBlock = (DetectorRegion[])regions.computeIfPresent((Object)block_coord, (key, list) -> {
                if (((DetectorRegion[])list).length == 1 && list[0] == this) {
                    return null;
                }
                return (DetectorRegion[])LogicUtil.removeArrayElement((Object[])list, (Object)this);
            });
            if (!lookup.isValid()) continue;
            lookup.storeDetectorRegions(coord, regionsAtBlock);
        }
    }
}

