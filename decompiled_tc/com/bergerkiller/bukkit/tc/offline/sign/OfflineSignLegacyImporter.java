/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.config.DataReader
 *  com.bergerkiller.bukkit.common.config.DataWriter
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  com.bergerkiller.bukkit.common.offline.OfflineWorld
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.utils.StreamUtil
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.Sign
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.world.WorldLoadEvent
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.offline.sign;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.config.DataReader;
import com.bergerkiller.bukkit.common.config.DataWriter;
import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.detector.DetectorRegion;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSignStore;
import com.bergerkiller.bukkit.tc.signactions.detector.DetectorSign;
import com.bergerkiller.bukkit.tc.signactions.spawner.SpawnSignManager;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.Plugin;

class OfflineSignLegacyImporter {
    private final OfflineSignStore store;
    private final TrainCarts plugin;
    private final Map<String, WorldLegacyData> byWorldName = new HashMap<String, WorldLegacyData>();
    private final File spawnSignsFile;
    private final File detectorSignsFile;

    public OfflineSignLegacyImporter(OfflineSignStore store, TrainCarts plugin) {
        this.store = store;
        this.plugin = plugin;
        this.spawnSignsFile = plugin.getDataFile(new String[]{"spawnsigns.dat"});
        this.detectorSignsFile = plugin.getDataFile(new String[]{"detectorsigns.dat"});
    }

    public void enable() {
        this.load();
        boolean imported = false;
        for (World world : Bukkit.getWorlds()) {
            WorldLegacyData legacyData = this.byWorldName.remove(world.getName());
            if (legacyData == null) continue;
            if (!imported) {
                imported = true;
                this.plugin.getLogger().log(Level.WARNING, "Importing legacy sign metadata...");
            }
            legacyData.importData(this.store, world);
        }
        if (imported) {
            this.plugin.getLogger().log(Level.WARNING, "Legacy sign metadata imported!");
            this.save();
        }
        if (!this.byWorldName.isEmpty()) {
            Bukkit.getPluginManager().registerEvents(new Listener(){

                @EventHandler
                public void onWorldLoad(WorldLoadEvent event) {
                    WorldLegacyData legacyData = (WorldLegacyData)OfflineSignLegacyImporter.this.byWorldName.remove(event.getWorld().getName());
                    if (legacyData != null) {
                        OfflineSignLegacyImporter.this.plugin.getLogger().log(Level.WARNING, "Importing legacy sign metadata for world " + event.getWorld().getName() + "...");
                        legacyData.importData(OfflineSignLegacyImporter.this.store, event.getWorld());
                        OfflineSignLegacyImporter.this.save();
                        OfflineSignLegacyImporter.this.plugin.getLogger().log(Level.WARNING, "Legacy sign metadata for world " + event.getWorld().getName() + " imported!");
                    }
                }
            }, (Plugin)this.plugin);
        }
    }

    private void load() {
        if (this.spawnSignsFile.exists()) {
            new DataReader(this.spawnSignsFile){

                public void read(DataInputStream stream) throws IOException {
                    for (int count = stream.readInt(); count > 0; --count) {
                        LegacySpawnSignData spawnSign = LegacySpawnSignData.read(stream);
                        ((OfflineSignLegacyImporter)OfflineSignLegacyImporter.this).dataOnWorld((String)spawnSign.signWorldName).spawnSigns.add(spawnSign);
                    }
                }
            }.read();
        }
        if (this.detectorSignsFile.exists()) {
            new DataReader(this.detectorSignsFile){

                public void read(DataInputStream stream) throws IOException {
                    for (int count = stream.readInt(); count > 0; --count) {
                        LegacyDetectorSignPairData pair = LegacyDetectorSignPairData.read(stream);
                        DetectorRegion region = DetectorRegion.getRegion(pair.detectorRegionUUID);
                        if (region == null) continue;
                        ((OfflineSignLegacyImporter)OfflineSignLegacyImporter.this).dataOnWorld((String)region.getWorldName()).detectorSignPairs.add(pair);
                    }
                }
            }.read();
        }
    }

    private void save() {
        if (this.byWorldName.isEmpty()) {
            this.spawnSignsFile.delete();
            this.detectorSignsFile.delete();
        } else {
            new DataWriter(this.spawnSignsFile){

                public void write(DataOutputStream stream) throws IOException {
                    List spawnSigns = OfflineSignLegacyImporter.this.byWorldName.values().stream().flatMap(data -> data.spawnSigns.stream()).collect(Collectors.toList());
                    stream.writeInt(spawnSigns.size());
                    for (LegacySpawnSignData spawnSign : spawnSigns) {
                        spawnSign.write(stream);
                    }
                }
            }.write();
            new DataWriter(this.detectorSignsFile){

                public void write(DataOutputStream stream) throws IOException {
                    List detectorSignPairs = OfflineSignLegacyImporter.this.byWorldName.values().stream().flatMap(data -> data.detectorSignPairs.stream()).collect(Collectors.toList());
                    stream.writeInt(detectorSignPairs.size());
                    for (LegacyDetectorSignPairData detectorSignPair : detectorSignPairs) {
                        detectorSignPair.write(stream);
                    }
                }
            }.write();
        }
    }

    private WorldLegacyData dataOnWorld(String worldName) {
        return this.byWorldName.computeIfAbsent(worldName, name -> new WorldLegacyData());
    }

    private static class WorldLegacyData {
        public final List<LegacySpawnSignData> spawnSigns = new ArrayList<LegacySpawnSignData>();
        public final List<LegacyDetectorSignPairData> detectorSignPairs = new ArrayList<LegacyDetectorSignPairData>();

        private WorldLegacyData() {
        }

        public void importData(OfflineSignStore store, World world) {
            for (LegacySpawnSignData spawnSign : this.spawnSigns) {
                Sign sign = this.findSign(world, spawnSign.signLocation, "spawn");
                if (sign == null) continue;
                SpawnSignManager.SpawnSignMetadata metadata = new SpawnSignManager.SpawnSignMetadata(spawnSign.interval, System.currentTimeMillis() + spawnSign.remaining - spawnSign.interval, spawnSign.active);
                store.put(sign, true, metadata);
            }
            for (LegacyDetectorSignPairData detectorSignPair : this.detectorSignPairs) {
                DetectorRegion region;
                Sign sign1 = this.findSign(world, detectorSignPair.sign1Location, "detector");
                Sign sign2 = this.findSign(world, detectorSignPair.sign2Location, "detector");
                if (sign1 == null || sign2 == null) {
                    region = DetectorRegion.getRegion(detectorSignPair.detectorRegionUUID);
                    if (region == null || region.isRegistered()) continue;
                    region.remove();
                    continue;
                }
                region = DetectorRegion.getRegion(detectorSignPair.detectorRegionUUID);
                if (region == null) continue;
                OfflineBlock sign1Block = OfflineWorld.of((World)world).getBlockAt(detectorSignPair.sign1Location);
                OfflineBlock sign2Block = OfflineWorld.of((World)world).getBlockAt(detectorSignPair.sign2Location);
                store.put(sign1, true, new DetectorSign.Metadata(sign2Block, true, region, detectorSignPair.sign1LeverDown));
                store.put(sign2, true, new DetectorSign.Metadata(sign1Block, true, region, detectorSignPair.sign2LeverDown));
            }
        }

        private Sign findSign(World world, IntVector3 signLocation, String type) {
            Block signBlock = signLocation.toBlock(world);
            world.getChunkAt(MathUtil.toChunk((int)signBlock.getX()), MathUtil.toChunk((int)signBlock.getZ()));
            Sign sign = BlockUtil.getSign((Block)signBlock);
            if (sign != null && sign.getLine(1).toLowerCase(Locale.ENGLISH).trim().startsWith(type)) {
                return sign;
            }
            return null;
        }
    }

    private static class LegacyDetectorSignPairData {
        public final IntVector3 sign1Location;
        public final IntVector3 sign2Location;
        public final boolean sign1LeverDown;
        public final boolean sign2LeverDown;
        public final UUID detectorRegionUUID;

        public static LegacyDetectorSignPairData read(DataInputStream stream) throws IOException {
            return new LegacyDetectorSignPairData(stream);
        }

        private LegacyDetectorSignPairData(DataInputStream stream) throws IOException {
            this.detectorRegionUUID = StreamUtil.readUUID((DataInputStream)stream);
            this.sign1Location = IntVector3.read((DataInputStream)stream);
            this.sign2Location = IntVector3.read((DataInputStream)stream);
            this.sign1LeverDown = stream.readBoolean();
            this.sign2LeverDown = stream.readBoolean();
        }

        public void write(DataOutputStream stream) throws IOException {
            StreamUtil.writeUUID((DataOutputStream)stream, (UUID)this.detectorRegionUUID);
            this.sign1Location.write(stream);
            this.sign2Location.write(stream);
            stream.writeBoolean(this.sign1LeverDown);
            stream.writeBoolean(this.sign2LeverDown);
        }
    }

    private static class LegacySpawnSignData {
        public final IntVector3 signLocation;
        public final String signWorldName;
        public final long interval;
        public final long remaining;
        public final boolean active;

        public static LegacySpawnSignData read(DataInputStream stream) throws IOException {
            return new LegacySpawnSignData(stream);
        }

        private LegacySpawnSignData(DataInputStream stream) throws IOException {
            this.signLocation = IntVector3.read((DataInputStream)stream);
            this.signWorldName = stream.readUTF();
            this.interval = stream.readLong();
            long remainingVal = stream.readLong();
            if (remainingVal == Long.MAX_VALUE) {
                this.remaining = 0L;
                this.active = false;
            } else {
                this.remaining = remainingVal;
                this.active = true;
            }
        }

        public void write(DataOutputStream stream) throws IOException {
            this.signLocation.write(stream);
            stream.writeUTF(this.signWorldName);
            stream.writeLong(this.interval);
            if (this.active) {
                stream.writeLong(this.remaining);
            } else {
                stream.writeLong(Long.MAX_VALUE);
            }
        }
    }
}

