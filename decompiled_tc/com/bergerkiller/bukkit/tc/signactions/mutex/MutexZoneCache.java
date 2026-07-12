/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  com.bergerkiller.bukkit.common.offline.OfflineWorld
 *  com.bergerkiller.bukkit.common.offline.OfflineWorldMap
 *  com.bergerkiller.bukkit.common.utils.StreamUtil
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.tc.signactions.mutex;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.offline.OfflineWorldMap;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSign;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSignMetadataHandler;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSignStore;
import com.bergerkiller.bukkit.tc.offline.train.format.OfflineDataBlock;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexSignMetadata;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZone;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneCacheWorld;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZonePath;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneSlot;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneSlotType;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import org.bukkit.World;

public class MutexZoneCache {
    private static final OfflineWorldMap<MutexZoneCacheWorld> cachesByWorld = new OfflineWorldMap();
    private static final Map<String, MutexZoneSlot> slotsByName = new HashMap<String, MutexZoneSlot>();
    private static final List<MutexZoneSlot> slotsList = new ArrayList<MutexZoneSlot>();

    public static void init(TrainCarts plugin) {
        plugin.getOfflineSigns().registerHandler(MutexSignMetadata.class, new OfflineSignMetadataHandler<MutexSignMetadata>(){

            @Override
            public void onAdded(OfflineSignStore store, OfflineSign sign, MutexSignMetadata metadata) {
                MutexZoneCache.addMutexSign(sign.getWorld(), sign.getPosition(), sign.isFrontText(), metadata);
            }

            @Override
            public void onRemoved(OfflineSignStore store, OfflineSign sign, MutexSignMetadata metadata) {
                MutexZoneCache.removeMutexSign(sign.getWorld(), sign.getPosition(), sign.isFrontText());
            }

            @Override
            public void onUpdated(OfflineSignStore store, OfflineSign sign, MutexSignMetadata oldValue, MutexSignMetadata newValue) {
                this.onRemoved(store, sign, oldValue);
                this.onAdded(store, sign, newValue);
            }

            @Override
            public void onEncode(DataOutputStream stream, OfflineSign sign, MutexSignMetadata value) throws IOException {
                stream.writeUTF(value.name);
                value.start.write(stream);
                value.end.write(stream);
                stream.writeUTF(value.statement);
            }

            @Override
            public MutexSignMetadata onDecode(DataInputStream stream, OfflineSign sign) throws IOException {
                String name = stream.readUTF();
                IntVector3 start = IntVector3.read((DataInputStream)stream);
                IntVector3 end = IntVector3.read((DataInputStream)stream);
                String statement = stream.readUTF();
                String typeName = sign.getLine(1).toLowerCase(Locale.ENGLISH);
                MutexZoneSlotType type = typeName.startsWith("smartmutex") || typeName.startsWith("smutex") ? MutexZoneSlotType.SMART : MutexZoneSlotType.NORMAL;
                return new MutexSignMetadata(type, name, start, end, statement);
            }

            @Override
            public boolean isUnloadedWorldsIgnored() {
                return false;
            }
        });
    }

    public static void saveState(TrainCarts plugin, OfflineDataBlock root) {
        OfflineDataBlock stateData = root.addChild("mutex-zones-state");
        for (MutexZoneCacheWorld world : cachesByWorld.values()) {
            world.byPathingKey.values().forEach(p -> p.writeTo(stateData));
        }
        for (MutexZoneSlot slot : slotsList) {
            OfflineDataBlock slotData;
            block6: {
                if (slot.getEnteredGroups().isEmpty() || slot.isAnonymous() && !slot.hasZones()) continue;
                try {
                    slotData = stateData.addChildOrAbort("mutex-zone-slot", stream -> {
                        if (!slot.isAnonymous()) {
                            Util.writeVariableLengthInt(stream, 0);
                            stream.writeUTF(slot.getName());
                        } else {
                            MutexZone zone = slot.getZones().get(0);
                            if (zone instanceof MutexZonePath) {
                                Util.writeVariableLengthInt(stream, 2);
                                MutexZonePath pathMutex = (MutexZonePath)zone;
                                StreamUtil.writeUUID((DataOutputStream)stream, (UUID)pathMutex.signBlock.getWorldUUID());
                                if (!pathMutex.key.writeTo(plugin, stream)) {
                                    throw new OfflineDataBlock.AbortChildException();
                                }
                            } else {
                                Util.writeVariableLengthInt(stream, 1);
                                OfflineBlock.writeTo((DataOutputStream)stream, (OfflineBlock)zone.signBlock);
                                stream.writeBoolean(zone.signFront);
                            }
                        }
                    });
                    if (slotData == null) {
                    }
                    break block6;
                }
                catch (Throwable t) {
                    plugin.getLogger().log(Level.SEVERE, "Failed to save mutex zone slot '" + slot.getName() + "'", t);
                }
                continue;
            }
            for (MutexZoneSlot.EnteredGroup group : slot.getEnteredGroups()) {
                group.unload().save(plugin, slotData);
            }
        }
    }

    public static void loadState(TrainCarts plugin, OfflineDataBlock root) {
        root.findChild("mutex-zones-state").ifPresent(stateData -> {
            for (MutexZonePath pathMutex : MutexZonePath.readAll(plugin, stateData)) {
                MutexZoneCache.forWorld(pathMutex.signBlock.getWorld()).add(pathMutex);
            }
            for (OfflineDataBlock slotData : stateData.findChildren("mutex-zone-slot")) {
                MutexZoneSlot slot;
                block19: {
                    try {
                        DataInputStream stream = slotData.readData();
                        try {
                            int mode = Util.readVariableLengthInt(stream);
                            if (mode == 0) {
                                slot = slotsByName.get(stream.readUTF());
                                if (slot == null) {
                                    continue;
                                }
                            } else if (mode == 1) {
                                MutexZone zone;
                                OfflineBlock mutexSignBlock = OfflineBlock.readFrom((DataInputStream)stream);
                                boolean mutexSignFront = stream.readBoolean();
                                MutexZoneCacheWorld cacheWorld = (MutexZoneCacheWorld)cachesByWorld.get(mutexSignBlock.getWorld());
                                if (cacheWorld == null || (zone = cacheWorld.findBySign(mutexSignBlock.getPosition(), mutexSignFront)) == null) continue;
                                slot = zone.slot;
                            } else {
                                MutexZonePath pathMutex;
                                if (mode != 2) continue;
                                OfflineWorld world = OfflineWorld.of((UUID)StreamUtil.readUUID((DataInputStream)stream));
                                Optional<MutexZoneCacheWorld.PathingSignKey> key = MutexZoneCacheWorld.PathingSignKey.readFrom(plugin, stream);
                                if (!key.isPresent() || (pathMutex = MutexZoneCache.forWorld((OfflineWorld)world).byPathingKey.get(key.get())) == null) continue;
                                slot = pathMutex.slot;
                            }
                            break block19;
                        }
                        finally {
                            if (stream == null) continue;
                            stream.close();
                        }
                    }
                    catch (Throwable t) {
                        plugin.getLogger().log(Level.SEVERE, "Failed to read data of mutex zone slot", t);
                    }
                    continue;
                }
                slot.getEnteredGroups().clear();
                slot.getEnteredGroups().addAll(MutexZoneSlot.UnloadedEnteredGroup.loadAll(plugin, slotData));
            }
        });
    }

    public static MutexZoneCacheWorld forWorld(OfflineWorld world) {
        return (MutexZoneCacheWorld)cachesByWorld.computeIfAbsent(world, MutexZoneCacheWorld::new);
    }

    public static void deinit(TrainCarts plugin) {
        plugin.getOfflineSigns().unregisterHandler(MutexSignMetadata.class);
    }

    public static MutexZonePath getOrCreatePathingMutex(RailLookup.TrackedSign sign, MinecartGroup group, IntVector3 initialBlock, UnaryOperator<MutexZonePath.OptionsBuilder> optionsBuilder) {
        return MutexZoneCache.forWorld(OfflineWorld.of((World)sign.sign.getWorld())).getOrCreatePathingMutex(sign, group, initialBlock, optionsBuilder);
    }

    private static void addMutexSign(OfflineWorld world, IntVector3 signPosition, boolean isFrontText, MutexSignMetadata metadata) {
        MutexZoneCache.forWorld(world).add(MutexZone.createCuboid(world, signPosition, isFrontText, metadata));
    }

    private static void removeMutexSign(OfflineWorld world, IntVector3 signPosition, boolean frontText) {
        MutexZone zone = MutexZoneCache.forWorld(world).removeAtSign(signPosition, frontText);
        if (zone != null) {
            MutexZoneCache.removeMutexZone(zone);
        }
    }

    public static MutexZone find(OfflineBlock block) {
        return MutexZoneCache.forWorld(block.getWorld()).find(block.getPosition());
    }

    public static MutexZone find(OfflineWorld world, IntVector3 block) {
        return MutexZoneCache.forWorld(world).find(block);
    }

    public static boolean isMutexZoneNearby(OfflineWorld world, IntVector3 block, int radius) {
        return MutexZoneCache.forWorld(world).isMutexZoneNearby(block, radius);
    }

    public static List<MutexZone> findNearbyZones(OfflineWorld world, IntVector3 block, int radius) {
        return MutexZoneCache.forWorld(world).findNearbyZones(block, radius);
    }

    public static synchronized MutexZoneSlot findSlot(String name, MutexZone zone) {
        MutexZoneSlot slot;
        if (name == null) {
            throw new IllegalArgumentException("Name is null");
        }
        if (name.isEmpty()) {
            slot = new MutexZoneSlot("");
            slotsList.add(slot);
        } else {
            slot = slotsByName.computeIfAbsent(name, n -> {
                MutexZoneSlot newSlot = new MutexZoneSlot((String)n);
                slotsList.add(newSlot);
                return newSlot;
            });
        }
        return slot.addZone(zone);
    }

    private static synchronized void removeMutexZone(MutexZone zone) {
        zone.slot.removeZone(zone);
        if (!zone.slot.hasZones()) {
            if (!zone.slot.isAnonymous()) {
                slotsByName.remove(zone.slot.getName());
            }
            slotsList.remove(zone.slot);
        }
    }

    public static synchronized void refreshAll() {
        if (!slotsList.isEmpty()) {
            for (int i = 0; i < slotsList.size(); ++i) {
                slotsList.get(i).onTick();
            }
        }
        cachesByWorld.values().forEach(MutexZoneCacheWorld::onTick);
    }

    public static synchronized void unloadGroupInSlots(MinecartGroup group) {
        slotsList.forEach(s -> s.unload(group));
    }
}

