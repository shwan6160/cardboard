/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.logic.RegionHandlerVanilla;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.storage.RegionFileHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Chunk;
import org.bukkit.World;

class RegionHandler_Vanilla_1_14
extends RegionHandlerVanilla {
    private final RegionHandlerImpl handler = Template.Class.create(RegionHandlerImpl.class, Common.TEMPLATE_RESOLVER);

    private Object findRegionFileCache(World world) {
        Object pcm = ServerLevelHandle.fromBukkit(world).getPlayerChunkMap().getRaw();
        Object rfc = this.handler.findRegionFileCache(pcm);
        return this.handler.findRegionFileCacheStorage(rfc);
    }

    private Object findPOIFileCache(World world) {
        Object pcm = ServerLevelHandle.fromBukkit(world).getChunkProviderServer().getRaw();
        Object rfc = this.handler.findPOIFileCache(pcm);
        return this.handler.findRegionFileCacheStorage(rfc);
    }

    @Override
    public void closeStreams(World world) {
        Object regionFileCache = this.findRegionFileCache(world);
        for (Object regionFile : this.handler.findCacheRegionFileInstances(regionFileCache)) {
            RegionFileHandle.createHandle(regionFile).closeStream();
        }
        regionFileCache = this.findPOIFileCache(world);
        for (Object regionFile : this.handler.findCacheRegionFileInstances(regionFileCache)) {
            RegionFileHandle.createHandle(regionFile).closeStream();
        }
    }

    @Override
    public Set<IntVector3> getRegions3(World world) {
        HashSet<IntVector3> regionIndices = new HashSet<IntVector3>();
        Object regionFileCache = this.findRegionFileCache(world);
        regionIndices.addAll(this.handler.findCacheRegionFileCoordinates(regionFileCache));
        LevelHandle worldHandle = LevelHandle.fromBukkit(world);
        int minRegionY = worldHandle.getMinBuildHeight() >> 9;
        int maxRegionY = worldHandle.getMaxBuildHeight() - 1 >> 9;
        File regionFolder = Common.SERVER.getWorldRegionFolder(world.getName());
        if (regionFolder != null) {
            String[] regionFileNames;
            String[] stringArray = regionFileNames = regionFolder.list();
            int n = stringArray.length;
            for (int i = 0; i < n; ++i) {
                IntVector2 coords;
                String regionFileName = stringArray[i];
                File file = new File(regionFolder, regionFileName);
                if (!file.isFile() || !file.exists() || file.length() < 4096L || (coords = this.getRegionFileCoordinates(file)) == null) continue;
                for (int ry = minRegionY; ry <= maxRegionY; ++ry) {
                    regionIndices.add(coords.toIntVector3(ry));
                }
            }
        }
        for (Chunk chunk : world.getLoadedChunks()) {
            IntVector2 coords = new IntVector2(chunk.getX() >> 5, chunk.getZ() >> 5);
            for (int ry = minRegionY; ry <= maxRegionY; ++ry) {
                regionIndices.add(coords.toIntVector3(ry));
            }
        }
        return regionIndices;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BitSet getRegionChunks3(World world, int rx, int ry, int rz) {
        BitSet chunks = new BitSet(1024);
        Object regionFileCache = this.findRegionFileCache(world);
        RegionFileHandle regionFileHandle = RegionFileHandle.createHandle(this.handler.findRegionFileAt(regionFileCache, rx, rz));
        if (regionFileHandle == null) {
            File regionFile = this.getRegionFile(world, rx, rz);
            if (regionFile.exists()) {
                try (DataInputStream stream2 = new DataInputStream(new FileInputStream(regionFile));){
                    for (int coordIndex = 0; coordIndex < 1024; ++coordIndex) {
                        if (stream2.readInt() <= 0) continue;
                        chunks.set(coordIndex);
                    }
                }
                catch (IOException stream2) {}
            }
        } else {
            int coordIndex = 0;
            for (int cz = 0; cz < 32; ++cz) {
                for (int cx = 0; cx < 32; ++cx) {
                    if (regionFileHandle.chunkExists(cx, cz)) {
                        chunks.set(coordIndex);
                    }
                    ++coordIndex;
                }
            }
        }
        return chunks;
    }

    @Override
    public boolean isChunkSaved(World world, int cx, int cz) {
        int rx = cx >> 5;
        int rz = cz >> 5;
        cx &= 0x1F;
        cz &= 0x1F;
        Object regionFileCache = this.findRegionFileCache(world);
        RegionFileHandle regionFileHandle = RegionFileHandle.createHandle(this.handler.findRegionFileAt(regionFileCache, rx, rz));
        if (regionFileHandle != null) {
            return regionFileHandle.chunkExists(cx, cz);
        }
        return this.getRegionChunks3(world, rx, 0, rz).get(cz << 5 | cx);
    }

    @Override
    public void forceInitialization() {
        this.handler.forceInitialization();
    }

    @Template.Optional
    @Template.ImportList(value={@Template.Import(value="net.minecraft.world.level.ChunkPos"), @Template.Import(value="net.minecraft.server.level.ChunkMap"), @Template.Import(value="net.minecraft.server.level.ServerChunkCache"), @Template.Import(value="net.minecraft.world.level.chunk.storage.ChunkStorage"), @Template.Import(value="net.minecraft.world.level.chunk.storage.RegionFile"), @Template.Import(value="net.minecraft.world.level.chunk.storage.SectionStorage"), @Template.Import(value="net.minecraft.world.level.chunk.storage.SimpleRegionStorage"), @Template.Import(value="net.minecraft.world.entity.ai.village.poi.PoiManager"), @Template.Import(value="it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap"), @Template.Import(value="it.unimi.dsi.fastutil.longs.Long2ObjectMap"), @Template.Import(value="com.bergerkiller.bukkit.common.bases.IntVector3"), @Template.Import(value="it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap")})
    @Template.InstanceType(value="net.minecraft.world.level.chunk.storage.RegionFileStorage")
    public static abstract class RegionHandlerImpl
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static RegionFileStorage findRegionFileCache(ChunkMap pcm) {\n#if assignable RegionFileStorage ChunkMap\n    // On 1.14 and PaperMC this can more trivially be accessed\n    return (RegionFileStorage) pcm;\n#elseif version >= 1.21.11\n    SimpleRegionStorage srs = (SimpleRegionStorage) pcm;\n  #if exists net.minecraft.world.level.chunk.storage.SimpleRegionStorage private final RegionFileStorage storage;\n    #require net.minecraft.world.level.chunk.storage.SimpleRegionStorage private final RegionFileStorage storage;\n    return srs#storage;\n  #else\n               #require net.minecraft.world.level.chunk.storage.SimpleRegionStorage private final net.minecraft.world.level.chunk.storage.IOWorker worker;\n    net.minecraft.world.level.chunk.storage.IOWorker ioworker = srs#worker;\n\n    #require net.minecraft.world.level.chunk.storage.IOWorker private final net.minecraft.world.level.chunk.storage.RegionFileStorage storage;\n    return ioworker#storage;\n  #endif\n           #else\n  #if exists net.minecraft.world.level.chunk.storage.ChunkStorage private final RegionFileStorage storage;\n    // Paper 1.21 moved it back to a field\n    #require net.minecraft.world.level.chunk.storage.ChunkStorage private final RegionFileStorage storage;\n    ChunkStorage icl = (ChunkStorage) pcm;\n    return icl#storage;\n  #elseif exists net.minecraft.world.level.chunk.storage.ChunkStorage protected final RegionFileStorage regionFileCache;\n    // Paperspigot compatible code\n    #require net.minecraft.world.level.chunk.storage.ChunkStorage protected final RegionFileStorage regionFileCache;\n    ChunkStorage icl = (ChunkStorage) pcm;\n    return icl#regionFileCache;\n  #else\n               // Access RegionFileStorage inside IOWorker\n    ChunkStorage icl = (ChunkStorage) pcm;\n    #if version >= 1.17\n      #require net.minecraft.world.level.chunk.storage.ChunkStorage private final net.minecraft.world.level.chunk.storage.IOWorker ioworker:worker;\n    #else\n                 #require net.minecraft.world.level.chunk.storage.ChunkStorage private final net.minecraft.world.level.chunk.storage.IOWorker ioworker:a;\n    #endif\n               IOWorker ioworker = icl#ioworker;\n\n    #if version >= 1.17\n    #require net.minecraft.world.level.chunk.storage.IOWorker private final RegionFileStorage cache:storage;\n    #elseif version >= 1.16\n    #require net.minecraft.world.level.chunk.storage.IOWorker private final RegionFileStorage cache:d;\n    #else\n               #require net.minecraft.world.level.chunk.storage.IOWorker private final RegionFileStorage cache:e;\n    #endif\n               return ioworker#cache;\n  #endif\n           #endif\n}")
        public abstract Object findRegionFileCache(Object var1);

        @Template.Generated(value="public static RegionFileStorage findPOIFileCache(ServerChunkCache cps) {\n#if version >= 1.18\n    PoiManager poi = cps.getPoiManager();\n#elseif version >= 1.14.4\n    PoiManager poi = cps.j();\n#else\n               PoiManager poi = cps.i();\n#endif\n               SectionStorage rfs = (SectionStorage) poi;\n\n#if exists net.minecraft.world.level.chunk.storage.SectionStorage public net.minecraft.world.level.chunk.storage.RegionFileStorage moonrise$getRegionStorage();\n    // New paper chunk system mixin getter method\n    return rfs.moonrise$getRegionStorage();\n#elseif assignable RegionFileStorage SectionStorage\n    // On 1.14 and PaperMC this can more trivially be accessed\n    return (RegionFileStorage) rfs;\n#else\n             #if version >= 1.20.5\n    #require SectionStorage private final SimpleRegionStorage simpleRegionStorage;\n    SimpleRegionStorage srs = rfs#simpleRegionStorage;\n    #require SimpleRegionStorage private final net.minecraft.world.level.chunk.storage.IOWorker ioworker:worker;\n    IOWorker ioworker = srs#ioworker;\n  #else\n               // Access RegionFileStorage inside IOWorker\n    #if version >= 1.17\n        #require SectionStorage private final net.minecraft.world.level.chunk.storage.IOWorker ioworker:worker;\n    #else\n                   #require SectionStorage private final net.minecraft.world.level.chunk.storage.IOWorker ioworker:b;\n    #endif\n               IOWorker ioworker = rfs#ioworker;\n  #endif\n\n             #if version >= 1.17\n    #require net.minecraft.world.level.chunk.storage.IOWorker private final RegionFileStorage cache:storage;\n  #elseif version >= 1.16\n    #require net.minecraft.world.level.chunk.storage.IOWorker private final RegionFileStorage cache:d;\n  #else\n               #require net.minecraft.world.level.chunk.storage.IOWorker private final RegionFileStorage cache:e;\n  #endif\n               return ioworker#cache;\n#endif\n           }")
        public abstract Object findPOIFileCache(Object var1);

        @Template.Generated(value="public static Long2ObjectLinkedOpenHashMap findRegionFileCache(RegionFileStorage rfc) {\n#if exists net.minecraft.world.level.chunk.storage.RegionFileStorage public final Long2ObjectLinkedOpenHashMap<abomination.IRegionFile> regionCache;\n    // Used on Leaf server 1.21.11\n    #require net.minecraft.world.level.chunk.storage.RegionFileStorage public final Long2ObjectLinkedOpenHashMap<abomination.IRegionFile> regionCache;\n#elseif exists net.minecraft.world.level.chunk.storage.RegionFileStorage public final Long2ObjectLinkedOpenHashMap<org.purpurmc.purpur.region.AbstractRegionFile> regionCache;\n    // Used on Leaf server (Purpur) (1.20)\n    #require net.minecraft.world.level.chunk.storage.RegionFileStorage public final Long2ObjectLinkedOpenHashMap<org.purpurmc.purpur.region.AbstractRegionFile> regionCache;\n#elseif exists net.minecraft.world.level.chunk.storage.RegionFileStorage public final Long2ObjectLinkedOpenHashMap<org.stupidcraft.linearpaper.region.IRegionFile> regionCache;\n    // Used on Leaf server (1.21)\n    #require net.minecraft.world.level.chunk.storage.RegionFileStorage public final Long2ObjectLinkedOpenHashMap<org.stupidcraft.linearpaper.region.IRegionFile> regionCache;\n#elseif version >= 1.17\n    #require net.minecraft.world.level.chunk.storage.RegionFileStorage private Long2ObjectLinkedOpenHashMap<RegionFile> regionCache;\n#else\n               #require net.minecraft.world.level.chunk.storage.RegionFileStorage private Long2ObjectLinkedOpenHashMap<RegionFile> regionCache:cache;\n#endif\n               return rfc#regionCache;\n}")
        public abstract Object findRegionFileCacheStorage(Object var1);

        @Template.Generated(value="public static Collection<RegionFile> findWorldRegionFileInstances(Long2ObjectLinkedOpenHashMap cache) {\n    return cache.values();\n}")
        public abstract Collection<Object> findCacheRegionFileInstances(Object var1);

        @Template.Generated(value="public static Collection<IntVector3> findCacheRegionFileCoordinates(Long2ObjectLinkedOpenHashMap cache) {\n    it.unimi.dsi.fastutil.longs.LongSet coordSet;\n    it.unimi.dsi.fastutil.longs.LongIterator iter;\n\n    coordSet = cache.keySet();\n    java.util.ArrayList result = new java.util.ArrayList(coordSet.size());\n    iter = coordSet.iterator();\n    while (iter.hasNext()) {\n        long coord = iter.nextLong();\n        int coord_x = ChunkPos.getX(coord);\n        int coord_z = ChunkPos.getZ(coord);\n        result.add(new com.bergerkiller.bukkit.common.bases.IntVector3(coord_x, 0, coord_z));\n    }\n               return result;\n}")
        public abstract Collection<IntVector3> findCacheRegionFileCoordinates(Object var1);

        @Template.Generated(value="public static RegionFile findRegionFileAt(Long2ObjectLinkedOpenHashMap cache, int rx, int rz) {\n#if version >= 26.1\n    long coord = ChunkPos.pack(rx, rz);\n#elseif version >= 1.18\n    long coord = ChunkPos.asLong(rx, rz);\n#else\n               long coord = ChunkPos.pair(rx, rz);\n#endif\n               return (RegionFile) cache.get(coord);\n}")
        public abstract Object findRegionFileAt(Object var1, int var2, int var3);
    }
}

