/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.logic.RegionHandlerVanilla;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.storage.RegionFileHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Chunk;
import org.bukkit.World;

class RegionHandler_Vanilla_1_8
extends RegionHandlerVanilla {
    private final Class<?> regionFileCacheType = CommonUtil.getClass("net.minecraft.world.level.chunk.storage.RegionFileStorage");
    private final FastMethod<Boolean> chunkExists = new FastMethod();
    private final FastField<Map<File, Object>> cacheField = new FastField();

    public RegionHandler_Vanilla_1_8() {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClassName("net.minecraft.server.level.ServerChunkCache");
        resolver.addImport("net.minecraft.world.level.chunk.storage.ChunkStorage");
        resolver.addImport("net.minecraft.world.level.chunk.storage.ChunkSerializer");
        resolver.setAllVariables(Common.TEMPLATE_RESOLVER);
        String source = SourceDeclaration.preprocess("public static boolean chunkExists(ServerChunkCache cps, int cx, int cz) {\n    #require net.minecraft.server.level.ServerChunkCache private final ChunkStorage chunkLoader;\n    ChunkStorage loader = cps#chunkLoader;\n    if (loader instanceof ChunkSerializer) {\n        ChunkSerializer crl = (ChunkSerializer) loader;\n#if version >= 1.12\n        return crl.chunkExists(cx, cz);\n#elseif version >= 1.11.2\n        return crl.a(cx, cz);\n#else\n        return crl.chunkExists(cps.world, cx, cz);\n#endif\n    } else {\n        return false;\n    }\n}", resolver);
        MethodDeclaration chunkExistsMethod = new MethodDeclaration(resolver, source);
        this.chunkExists.init(chunkExistsMethod);
        try {
            Field field;
            try {
                field = Resolver.resolveAndGetDeclaredField(this.regionFileCacheType, "cache");
            }
            catch (Throwable t) {
                field = Resolver.resolveAndGetDeclaredField(this.regionFileCacheType, "a");
            }
            this.cacheField.init(field);
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }

    private Map<File, Object> getCache() {
        Map<File, Object> cache = this.cacheField.get(null);
        if (cache == null) {
            throw new IllegalStateException("Failed to find RegionFileStorage cache field");
        }
        return cache;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void closeStreams(World world) {
        Class<?> clazz = this.regionFileCacheType;
        synchronized (clazz) {
            try {
                Map<File, Object> cache = this.getCache();
                String worldPart = "." + File.separator + world.getName();
                Iterator<Map.Entry<File, Object>> iter = cache.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<File, Object> entry = iter.next();
                    if (!entry.getKey().toString().startsWith(worldPart) || entry.getValue() == null) continue;
                    try {
                        RegionFileHandle.createHandle(entry.getValue()).closeStream();
                        iter.remove();
                    }
                    catch (Exception ex) {
                        Logging.LOGGER.log(Level.WARNING, "Failed to close region file handle stream", ex);
                    }
                }
            }
            catch (Exception ex) {
                Logging.LOGGER.log(Level.WARNING, "Exception while closing streams for '" + world.getName() + "'!", ex);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<IntVector3> getRegions3(World world) {
        Object regionFileNames;
        HashSet<File> regionFiles = new HashSet<File>();
        File regionFolder = Common.SERVER.getWorldRegionFolder(world.getName());
        if (regionFolder != null) {
            regionFileNames = regionFolder.list();
            for (String regionFileName : regionFileNames) {
                File file = new File(regionFolder, regionFileName);
                if (!file.isFile() || !file.exists() || file.length() < 4096L) continue;
                regionFiles.add(file);
            }
        }
        regionFileNames = this.regionFileCacheType;
        synchronized (regionFileNames) {
            for (File regionFile : this.getCache().keySet()) {
                if (regionFile == null || !regionFile.getParentFile().equals(regionFolder)) continue;
                regionFiles.add(regionFile);
            }
        }
        HashSet<IntVector3> regionIndices = new HashSet<IntVector3>();
        for (File file : regionFiles) {
            IntVector3 coords;
            IntVector2 regionFileCoordinates = this.getRegionFileCoordinates(file);
            if (regionFileCoordinates == null || (coords = regionFileCoordinates.toIntVector3(0)) == null) continue;
            regionIndices.add(coords);
        }
        for (Chunk chunk : world.getLoadedChunks()) {
            IntVector3 coords = new IntVector3(chunk.getX() >> 5, 0, chunk.getZ() >> 5);
            regionIndices.add(coords);
        }
        return regionIndices;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BitSet getRegionChunks3(World world, int rx, int ry, int rz) {
        BitSet chunks = new BitSet(1024);
        File regionFile = this.getRegionFile(world, rx, rz);
        Class<?> clazz = this.regionFileCacheType;
        synchronized (clazz) {
            Map<File, Object> cache = this.getCache();
            RegionFileHandle regionFileHandle = RegionFileHandle.createHandle(cache.get(regionFile));
            if (regionFileHandle == null) {
                try (DataInputStream stream2 = new DataInputStream(new FileInputStream(regionFile));){
                    for (int coordIndex = 0; coordIndex < 1024; ++coordIndex) {
                        if (stream2.readInt() <= 0) continue;
                        chunks.set(coordIndex);
                    }
                }
                catch (IOException stream2) {}
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
        }
        return chunks;
    }

    @Override
    public boolean isChunkSaved(World world, int cx, int cz) {
        Object cps = ((Template.Method)ServerLevelHandle.T.getChunkProviderServer.raw).invoke(HandleConversion.toWorldHandle(world));
        return this.chunkExists.invoke(null, cps, cx, cz);
    }

    @Override
    public void forceInitialization() {
        this.chunkExists.forceInitialization();
        this.cacheField.forceInitialization();
    }
}

