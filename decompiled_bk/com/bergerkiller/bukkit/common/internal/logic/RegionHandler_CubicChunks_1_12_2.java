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
import com.bergerkiller.bukkit.common.internal.logic.RegionHandler;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Chunk;
import org.bukkit.World;

class RegionHandler_CubicChunks_1_12_2
extends RegionHandler {
    private final CubicChunksHandle handle = Template.Class.create(CubicChunksHandle.class, Common.TEMPLATE_RESOLVER);
    private int forRegion_base_cx;
    private int forRegion_base_cy;
    private int forRegion_base_cz;
    private final Object forRegionCallbackListChunks;

    public RegionHandler_CubicChunks_1_12_2() throws Throwable {
        ClassInterceptor interceptor = new ClassInterceptor(){

            @Override
            protected Invoker<?> getCallback(Method method) {
                if (method.getName().equals("apply")) {
                    return new Invoker<Object>(){

                        @Override
                        public Object invokeVA(Object instance, Object ... args) {
                            return RegionHandler_CubicChunks_1_12_2.this.handle.listRegionChunkXZ(args[0], RegionHandler_CubicChunks_1_12_2.this.forRegion_base_cx, RegionHandler_CubicChunks_1_12_2.this.forRegion_base_cy, RegionHandler_CubicChunks_1_12_2.this.forRegion_base_cz);
                        }
                    };
                }
                return null;
            }
        };
        this.forRegionCallbackListChunks = interceptor.createInstance(Class.forName("cubicchunks.regionlib.util.CheckedFunction"));
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public boolean isSupported(World world) {
        return this.handle.isSupported(HandleConversion.toWorldHandle(world));
    }

    @Override
    public void forceInitialization() {
        this.handle.forceInitialization();
    }

    @Override
    public void closeStreams(World world) {
    }

    @Override
    public Set<IntVector3> getRegions3ForXZ(World world, Set<IntVector2> regionXZCoordinates) {
        HashSet<IntVector3> regionIndices;
        Object worldHandle = HandleConversion.toWorldHandle(world);
        Object chunkProviderServer = ((Template.Method)ServerLevelHandle.T.getChunkProviderServer.raw).invoke(worldHandle);
        if (this.handle.forEachCube(chunkProviderServer, this.wrap(arg_0 -> RegionHandler_CubicChunks_1_12_2.lambda$getRegions3ForXZ$0(regionXZCoordinates, regionIndices = new HashSet<IntVector3>(), arg_0)))) {
            return regionIndices;
        }
        HashSet<IntVector3> regionIndices2 = this.getWorldRegionFileCoordinates(world, c -> regionXZCoordinates.contains(c.toIntVector2()));
        for (Chunk chunk : world.getLoadedChunks()) {
            IntVector2 region = new IntVector2(chunk.getX() >> 5, chunk.getZ() >> 5);
            if (!regionXZCoordinates.contains(region)) continue;
            List<Integer> cubes_y = this.handle.getLoadedCubesY(HandleConversion.toChunkHandle(chunk));
            for (Integer y : cubes_y) {
                regionIndices2.add(region.toIntVector3(y >> 5));
            }
        }
        return regionIndices2;
    }

    @Override
    public Set<IntVector3> getRegions3(World world) {
        HashSet<IntVector3> regionIndices;
        Object worldHandle = HandleConversion.toWorldHandle(world);
        Object chunkProviderServer = ((Template.Method)ServerLevelHandle.T.getChunkProviderServer.raw).invoke(worldHandle);
        if (this.handle.forEachCube(chunkProviderServer, this.wrap(arg_0 -> RegionHandler_CubicChunks_1_12_2.lambda$getRegions3$0(regionIndices = new HashSet<IntVector3>(), arg_0)))) {
            return regionIndices;
        }
        HashSet<IntVector3> regionIndices2 = this.getWorldRegionFileCoordinates(world, c -> true);
        for (Chunk chunk : world.getLoadedChunks()) {
            IntVector2 region = new IntVector2(chunk.getX() >> 5, chunk.getZ() >> 5);
            List<Integer> cubes_y = this.handle.getLoadedCubesY(HandleConversion.toChunkHandle(chunk));
            for (Integer y : cubes_y) {
                regionIndices2.add(region.toIntVector3(y >> 5));
            }
        }
        return regionIndices2;
    }

    @Override
    public BitSet getRegionChunks3(World world, int rx, int ry, int rz) {
        int base_cx = rx << 5;
        int base_cy = ry << 5;
        int base_cz = rz << 5;
        BitSet chunks = new BitSet();
        Object worldHandle = HandleConversion.toWorldHandle(world);
        Object chunkProviderServer = ((Template.Method)ServerLevelHandle.T.getChunkProviderServer.raw).invoke(worldHandle);
        List<Object> regionProviderList = this.handle.getRegionProviders(chunkProviderServer);
        if (regionProviderList != null) {
            for (Object regionProvider : regionProviderList) {
                this.applyChunksToBitset(chunks, regionProvider, base_cx, base_cy, base_cz);
                this.applyChunksToBitset(chunks, regionProvider, base_cx + 16, base_cy, base_cz);
                this.applyChunksToBitset(chunks, regionProvider, base_cx, base_cy, base_cz + 16);
                this.applyChunksToBitset(chunks, regionProvider, base_cx + 16, base_cy, base_cz + 16);
                this.applyChunksToBitset(chunks, regionProvider, base_cx, base_cy + 16, base_cz);
                this.applyChunksToBitset(chunks, regionProvider, base_cx + 16, base_cy + 16, base_cz);
                this.applyChunksToBitset(chunks, regionProvider, base_cx, base_cy + 16, base_cz + 16);
                this.applyChunksToBitset(chunks, regionProvider, base_cx + 16, base_cy + 16, base_cz + 16);
            }
        } else {
            for (int rel_cz = 0; rel_cz < 32; ++rel_cz) {
                for (int rel_cx = 0; rel_cx < 32; ++rel_cx) {
                    int cx = base_cx + rel_cx;
                    int cz = base_cz + rel_cz;
                    boolean regionHasCubes = false;
                    for (int rel_cy = 0; rel_cy < 32; ++rel_cy) {
                        if (!this.handle.cubeExists(chunkProviderServer, cx, base_cy + rel_cy, cz)) continue;
                        regionHasCubes = true;
                        break;
                    }
                    if (!regionHasCubes) continue;
                    int index = rel_cz << 5 | rel_cx;
                    chunks.set(index);
                }
            }
        }
        return chunks;
    }

    private void applyChunksToBitset(BitSet chunks, Object regionProvider, int base_cx, int base_cy, int base_cz) {
        Optional region_chunks_opt = (Optional)this.forRegion(regionProvider, base_cx, base_cy, base_cz, this.forRegionCallbackListChunks);
        if (!region_chunks_opt.isPresent()) {
            return;
        }
        base_cx &= 0x10;
        base_cz &= 0x10;
        BitSet region_chunks = (BitSet)region_chunks_opt.get();
        int region_data_index = 0;
        for (int cz = 0; cz < 16; ++cz) {
            for (int cx = 0; cx < 16; ++cx) {
                if (!region_chunks.get(region_data_index++)) continue;
                int index = cz + base_cz << 5 | cx + base_cx;
                chunks.set(index);
            }
        }
    }

    private <T> T forRegion(Object regionProvider, int base_cx, int base_cy, int base_cz, Object callback) {
        this.forRegion_base_cx = base_cx;
        this.forRegion_base_cy = base_cy;
        this.forRegion_base_cz = base_cz;
        return (T)this.handle.fromExistingRegion(regionProvider, base_cx, base_cy, base_cz, callback);
    }

    @Override
    public boolean isChunkSaved(World world, int cx, int cz) {
        Object worldHandle = HandleConversion.toWorldHandle(world);
        Object chunkProviderServer = ((Template.Method)ServerLevelHandle.T.getChunkProviderServer.raw).invoke(worldHandle);
        return this.handle.columnExists(chunkProviderServer, cx, cz);
    }

    @Override
    public int getMinHeight(World world) {
        return Integer.MIN_VALUE;
    }

    @Override
    public int getMaxHeight(World world) {
        return Integer.MAX_VALUE;
    }

    protected HashSet<IntVector3> getWorldRegionFileCoordinates(World world, Predicate<IntVector3> filter) {
        HashSet hashSet;
        block8: {
            Path regionPath = new File(Common.SERVER.getWorldFolder(world.getName()), "region3d").toPath();
            Stream stream = (Stream)((Stream)Files.list(regionPath).parallel()).map(path -> this.getRegionFileCoordinates(path.getFileName().toString())).filter(coord -> coord != null).filter(filter).sequential();
            try {
                hashSet = stream.collect(Collectors.toCollection(HashSet::new));
                if (stream == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    Logging.LOGGER.log(Level.SEVERE, "Failed to list region files of world " + world.getName(), e);
                    return new HashSet<IntVector3>();
                }
            }
            stream.close();
        }
        return hashSet;
    }

    protected IntVector3 getRegionFileCoordinates(String regionFileName) {
        if (!regionFileName.endsWith(".3dr")) {
            return null;
        }
        String[] parts = regionFileName.substring(0, regionFileName.length() - 4).split("\\.");
        if (parts.length != 3) {
            return null;
        }
        try {
            int rx = Integer.parseInt(parts[0]);
            int ry = Integer.parseInt(parts[1]);
            int rz = Integer.parseInt(parts[2]);
            return new IntVector3(rx >> 1, ry >> 1, rz >> 1);
        }
        catch (Exception exception) {
            return null;
        }
    }

    private CubePosConsumerAdapter wrap(Consumer<IntVector3> consumer) {
        return new CubePosConsumerAdapter(consumer);
    }

    private static /* synthetic */ void lambda$getRegions3$0(Set regionIndices, IntVector3 cubeCoordinate) {
        regionIndices.add(new IntVector3(cubeCoordinate.x >> 5, cubeCoordinate.y >> 5, cubeCoordinate.z >> 5));
    }

    private static /* synthetic */ void lambda$getRegions3ForXZ$0(Set regionXZCoordinates, Set regionIndices, IntVector3 cubeCoordinate) {
        IntVector2 regionXZ = new IntVector2(cubeCoordinate.x >> 5, cubeCoordinate.z >> 5);
        if (regionXZCoordinates.contains(regionXZ)) {
            regionIndices.add(regionXZ.toIntVector3(cubeCoordinate.y >> 5));
        }
    }

    @Template.Optional
    @Template.ImportList(value={@Template.Import(value="io.github.opencubicchunks.cubicchunks.api.util.CubePos"), @Template.Import(value="io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld"), @Template.Import(value="io.github.opencubicchunks.cubicchunks.api.world.IColumn"), @Template.Import(value="io.github.opencubicchunks.cubicchunks.api.world.ICube"), @Template.Import(value="io.github.opencubicchunks.cubicchunks.api.world.storage.ICubicStorage"), @Template.Import(value="io.github.opencubicchunks.cubicchunks.core.server.chunkio.ICubeIO"), @Template.Import(value="io.github.opencubicchunks.cubicchunks.core.world.ICubeProviderInternal"), @Template.Import(value="io.github.opencubicchunks.cubicchunks.core.server.chunkio.AsyncBatchingCubeIO"), @Template.Import(value="io.github.opencubicchunks.cubicchunks.core.server.chunkio.RegionCubeIO"), @Template.Import(value="io.github.opencubicchunks.cubicchunks.core.server.chunkio.RegionCubeStorage"), @Template.Import(value="cubicchunks.regionlib.api.region.IRegionProvider"), @Template.Import(value="cubicchunks.regionlib.impl.save.SaveSection3D"), @Template.Import(value="cubicchunks.regionlib.impl.SaveCubeColumns"), @Template.Import(value="cubicchunks.regionlib.impl.EntryLocation3D"), @Template.Import(value="com.bergerkiller.bukkit.common.bases.IntVector3")})
    @Template.InstanceType(value="net.minecraft.world.level.Level")
    public static abstract class CubicChunksHandle
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static boolean isSupported(net.minecraft.server.level.ServerLevel world) {\n    return world instanceof ICubicWorld && ((ICubicWorld) world).isCubicWorld();\n}")
        public abstract boolean isSupported(Object var1);

        @Template.Generated(value="public static java.util.List<Integer> getLoadedCubesY(IColumn chunk) {\n    java.util.List result = new java.util.ArrayList();\n    java.util.Iterator iter = chunk.getLoadedCubes().iterator();\n    while (iter.hasNext()) {\n        ICube cube = (ICube) iter.next();\n        result.add(Integer.valueOf(cube.getY()));\n    }\n               return result;\n}")
        public abstract List<Integer> getLoadedCubesY(Object var1);

        @Template.Generated(value="public static Object listRegionChunkXZ(cubicchunks.regionlib.api.region.IRegion region, int base_cx, int base_cy, int base_cz) {\n    java.util.BitSet chunks_xz = new java.util.BitSet(256);\n    int dataIndex = 0;\n    for (int cz = 0; cz < 16; cz++) {\n        for (int cx = 0; cx < 16; cx++) {\n            for (int cy = 0; cy < 16; cy++) {\n                EntryLocation3D key = new EntryLocation3D(base_cx+cx, base_cy+cy, base_cz+cz);\n                if (region.hasValue(key)) {\n                    chunks_xz.set(dataIndex);\n                    break;\n                }\n            }\n            dataIndex++;\n        }\n               }\n    return chunks_xz;\n}")
        public abstract Object listRegionChunkXZ(Object var1, int var2, int var3, int var4);

        @Template.Generated(value="public static java.util.Optional<Object> fromExistingRegion(cubicchunks.regionlib.api.region.IRegionProvider regionProvider, int cx, int cy, int cz, cubicchunks.regionlib.util.CheckedFunction checkedFunction) {\n    EntryLocation3D key = new EntryLocation3D(cx, cy, cz);\n    return regionProvider.fromExistingRegion(key, checkedFunction);\n}")
        public abstract Optional fromExistingRegion(Object var1, int var2, int var3, int var4, Object var5);

        @Template.Generated(value="public static List<Object> forRegion(io.github.opencubicchunks.cubicchunks.core.world.ICubeProviderInternal.Server provider) {\n    ICubeIO cubeIO = provider.getCubeIO();\n\n#if exists io.github.opencubicchunks.cubicchunks.api.world.storage.ICubicStorage\n    // ICubicStorage API\n    if (!(cubeIO instanceof AsyncBatchingCubeIO)) {\n        return null; // signal not supported\n    }\n               ICubicStorage storage = ((AsyncBatchingCubeIO) cubeIO).getStorage();\n    if (!(storage instanceof RegionCubeStorage)) {\n        return null; // signal not supported\n    }\n               RegionCubeStorage regionCubeStorage = (RegionCubeStorage) storage;\n    #require io.github.opencubicchunks.cubicchunks.core.server.chunkio.RegionCubeStorage private cubicchunks.regionlib.impl.SaveCubeColumns save;\n    SaveCubeColumns columns = regionCubeStorage#save;\n#else\n               // Legacy\n    RegionCubeIO regionCubeIO = (RegionCubeIO) cubeIO;\n    #require RegionCubeIO private SaveCubeColumns getSave();\n    SaveCubeColumns columns = regionCubeIO#getSave();\n#endif\n\n               SaveSection3D saveSection = columns.getSaveSection3D();\n    #require cubicchunks.regionlib.api.storage.SaveSection private final java.util.List<IRegionProvider> regionProviders;\n    return saveSection#regionProviders;\n}")
        public abstract List<Object> getRegionProviders(Object var1);

        @Template.Generated(value="public static IntVector3 cubePosToIntVector3(CubePos pos) {\n    return new IntVector3(pos.getX(), pos.getY(), pos.getZ());\n}")
        public abstract IntVector3 cubePosToIntVector3(Object var1);

        @Template.Generated(value="public static boolean forEachCube(io.github.opencubicchunks.cubicchunks.core.world.ICubeProviderInternal.Server provider, java.util.function.Consumer callback) {\n#if !exists io.github.opencubicchunks.cubicchunks.api.world.storage.ICubicStorage\n    return false;\n#elseif !exists io.github.opencubicchunks.cubicchunks.core.server.chunkio.AsyncBatchingCubeIO\n    return false;\n#else\n               ICubeIO cubeIO = provider.getCubeIO();\n    if (!(cubeIO instanceof AsyncBatchingCubeIO)) {\n        return false;\n    }\n\n               ICubicStorage storage = ((AsyncBatchingCubeIO) cubeIO).getStorage();\n    try {\n                   storage.forEachCube(callback);\n        return true;\n    } catch (java.io.IOException ex) {\n        com.bergerkiller.bukkit.common.Logging.LOGGER.log(java.util.logging.Level.WARNING, \"IO Exception while iterating cubes\", ex);\n        return false;\n    }\n           #endif\n}")
        public abstract boolean forEachCube(Object var1, CubePosConsumerAdapter var2);

        @Template.Generated(value="public static boolean columnExists(io.github.opencubicchunks.cubicchunks.core.world.ICubeProviderInternal.Server provider, int cx, int cz) {\n#if !exists io.github.opencubicchunks.cubicchunks.api.world.storage.ICubicStorage\n    return false;\n#elseif !exists io.github.opencubicchunks.cubicchunks.core.server.chunkio.AsyncBatchingCubeIO\n    return false;\n#else\n               ICubeIO cubeIO = provider.getCubeIO();\n    if (!(cubeIO instanceof AsyncBatchingCubeIO)) {\n        return false;\n    }\n\n               ICubicStorage storage = ((AsyncBatchingCubeIO) cubeIO).getStorage();\n    return storage.columnExists(new net.minecraft.world.level.ChunkPos(cx, cz));\n#endif\n           }")
        public abstract boolean columnExists(Object var1, int var2, int var3);

        @Template.Generated(value="public static boolean columnExists(io.github.opencubicchunks.cubicchunks.core.world.ICubeProviderInternal.Server provider, int cx, int cy, int cz) {\n#if !exists io.github.opencubicchunks.cubicchunks.api.world.storage.ICubicStorage\n    return false;\n#elseif !exists io.github.opencubicchunks.cubicchunks.core.server.chunkio.AsyncBatchingCubeIO\n    return false;\n#else\n               ICubeIO cubeIO = provider.getCubeIO();\n    if (!(cubeIO instanceof AsyncBatchingCubeIO)) {\n        return false;\n    }\n\n               ICubicStorage storage = ((AsyncBatchingCubeIO) cubeIO).getStorage();\n    return storage.cubeExists(new CubePos(cx, cy, cz));\n#endif\n           }")
        public abstract boolean cubeExists(Object var1, int var2, int var3, int var4);
    }

    public final class CubePosConsumerAdapter
    implements Consumer<Object> {
        private final Consumer<IntVector3> consumer;

        private CubePosConsumerAdapter(Consumer<IntVector3> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(Object t) {
            this.consumer.accept(RegionHandler_CubicChunks_1_12_2.this.handle.cubePosToIntVector3(t));
        }
    }
}

