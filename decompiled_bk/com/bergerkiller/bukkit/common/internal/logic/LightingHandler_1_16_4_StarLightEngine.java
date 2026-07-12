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
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.lighting.LightingHandler;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.server.level.ThreadedLevelLightEngineHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import org.bukkit.Chunk;
import org.bukkit.World;

class LightingHandler_1_16_4_StarLightEngine
implements LightingHandler {
    private StarLightEngineHandle handle = Template.Class.create(StarLightEngineHandle.class, Common.TEMPLATE_RESOLVER);
    private final Map<World, List<Runnable>> lightUpdateQueue = new IdentityHashMap<World, List<Runnable>>();

    LightingHandler_1_16_4_StarLightEngine() {
    }

    @Override
    public void enable() {
        this.handle.forceInitialization();
    }

    @Override
    public void disable() {
    }

    @Override
    public boolean isSupported(World world) {
        ThreadedLevelLightEngineHandle engine = ThreadedLevelLightEngineHandle.forWorld(world);
        return this.handle.isSupported(engine.getRaw());
    }

    @Override
    public byte[] getSectionBlockLight(World world, int cx, int cy, int cz) {
        Chunk chunk = WorldUtil.getChunk(world, cx, cz);
        if (chunk == null) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to read sky light of [" + cx + "/" + cy + "/" + cz + "]: Chunk not loaded");
            return null;
        }
        try {
            return this.handle.getBlockLightData(HandleConversion.toChunkHandle(chunk), cy);
        }
        catch (Throwable ex) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to read sky light of [" + cx + "/" + cy + "/" + cz + "]", ex);
            return null;
        }
    }

    @Override
    public byte[] getSectionSkyLight(World world, int cx, int cy, int cz) {
        Chunk chunk = WorldUtil.getChunk(world, cx, cz);
        if (chunk == null) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to read sky light of [" + cx + "/" + cy + "/" + cz + "]: Chunk not loaded");
            return null;
        }
        try {
            return this.handle.getSkyLightData(HandleConversion.toChunkHandle(chunk), cy);
        }
        catch (Throwable ex) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to read sky light of [" + cx + "/" + cy + "/" + cz + "]", ex);
            return null;
        }
    }

    @Override
    public CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        CompletableFuture<Void> future = new CompletableFuture<Void>();
        Chunk chunk = WorldUtil.getChunk(world, cx, cz);
        this.scheduleUpdate(world, () -> {
            try {
                this.handle.setSkyLightData(HandleConversion.toChunkHandle(chunk), cx, cy, cz, data);
                future.complete(null);
            }
            catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        CompletableFuture<Void> future = new CompletableFuture<Void>();
        Chunk chunk = WorldUtil.getChunk(world, cx, cz);
        this.scheduleUpdate(world, () -> {
            try {
                this.handle.setBlockLightData(HandleConversion.toChunkHandle(chunk), cx, cy, cz, data);
                future.complete(null);
            }
            catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void scheduleUpdate(World world, Runnable runnable) {
        Map<World, List<Runnable>> map = this.lightUpdateQueue;
        synchronized (map) {
            List<Runnable> queue = this.lightUpdateQueue.get(world);
            if (queue != null) {
                queue.add(runnable);
                return;
            }
            queue = new ArrayList<Runnable>();
            queue.add(runnable);
            this.lightUpdateQueue.put(world, queue);
            ThreadedLevelLightEngineHandle.forWorld(world).schedule(() -> {
                List<Runnable> queue;
                Map<World, List<Runnable>> map = this.lightUpdateQueue;
                synchronized (map) {
                    queue = this.lightUpdateQueue.remove(world);
                }
                if (queue != null) {
                    for (Runnable queuedTask : queue) {
                        queuedTask.run();
                    }
                }
            });
        }
    }

    @Template.Optional
    @Template.ImportList(value={@Template.Import(value="net.minecraft.server.level.ThreadedLevelLightEngine"), @Template.Import(value="net.minecraft.core.SectionPos"), @Template.Import(value="net.minecraft.world.level.chunk.LevelChunk"), @Template.Import(value="net.minecraft.world.level.chunk.DataLayer"), @Template.Import(value="net.minecraft.world.level.LightLayer"), @Template.Import(value="net.minecraft.world.level.chunk.LightChunkGetter")})
    @Template.InstanceType(value="ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray")
    @Template.Require(declaring="net.minecraft.world.level.chunk.LevelChunk", value="public static int getHeightOffset(LevelChunk chunk) {\n    // Note: StarLight uses offset for below-bedrock light buffers, hence + 1\n#if exists ca.spottedleaf.moonrise.common.util.WorldUtil public static int getMinLightSection(net.minecraft.world.level.LevelHeightAccessor world);\n    return -ca.spottedleaf.moonrise.common.util.WorldUtil.getMinLightSection(chunk.getLevel());\n#elseif version >= 1.17\n    return 1 - chunk.getMinSection();\n#else\n           return 1;\n#endif\n       }")
    public static abstract class StarLightEngineHandle
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static boolean isSupported(net.minecraft.server.level.ThreadedLevelLightEngine lightEngineThreaded) {\n#if version >= 1.21\n    return true;\n#else\n             #if exists net.minecraft.server.level.ThreadedLevelLightEngine protected final ca.spottedleaf.moonrise.patches.starlight.light.StarLightInterface theLightEngine;\n    #require net.minecraft.server.level.ThreadedLevelLightEngine protected final ca.spottedleaf.moonrise.patches.starlight.light.StarLightInterface theLightEngine;\n  #else\n               #require net.minecraft.server.level.ThreadedLevelLightEngine protected final ca.spottedleaf.moonrise.patches.starlight.light.ThreadedStarLightEngine theLightEngine;\n  #endif\n               return lightEngineThreaded#theLightEngine != null;\n#endif\n           }")
        public abstract boolean isSupported(Object var1);

        @Template.Generated(value="public static byte[] getSkyLightData(LevelChunk chunk, int cy) {\n    cy += #getHeightOffset(chunk);\n\n#if exists net.minecraft.world.level.chunk.LevelChunk public ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray[] starlight$getSkyNibbles();\n    SWMRNibbleArray[] nibbles = chunk.starlight$getSkyNibbles();\n#else\n               SWMRNibbleArray[] nibbles = chunk.getSkyNibbles();\n#endif\n               SWMRNibbleArray swmr_nibble;\n    if (cy < 0 || cy >= nibbles.length || (swmr_nibble = nibbles[cy]) == null) {\n        return null;\n    }\n\n           #if exists ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray public net.minecraft.world.level.chunk.DataLayer toVanillaNibble();\n    DataLayer nibble = swmr_nibble.toVanillaNibble();\n    if (nibble == null) {\n        return null;\n    } else {\n  #if version >= 1.18\n        return nibble.getData();\n  #else\n                   return nibble.asBytes();\n  #endif\n               }\n#else\n               byte[] newData = new byte[2048];\n    swmr_nibble.copyInto(newData, 0);\n    return newData;\n#endif\n           }")
        public abstract byte[] getSkyLightData(Object var1, int var2);

        @Template.Generated(value="public static byte[] getBlockLightData(LevelChunk chunk, int cy) {\n    cy += #getHeightOffset(chunk);\n\n#if exists net.minecraft.world.level.chunk.LevelChunk public ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray[] starlight$getBlockNibbles();\n    SWMRNibbleArray[] nibbles = chunk.starlight$getBlockNibbles();\n#else\n               SWMRNibbleArray[] nibbles = chunk.getBlockNibbles();\n#endif\n               SWMRNibbleArray swmr_nibble;\n    if (cy < 0 || cy >= nibbles.length || (swmr_nibble = nibbles[cy]) == null) {\n        return null;\n    }\n\n           #if exists ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray public net.minecraft.world.level.chunk.DataLayer toVanillaNibble();\n    DataLayer nibble = swmr_nibble.toVanillaNibble();\n    if (nibble == null) {\n        return null;\n    } else {\n  #if version >= 1.18\n        return nibble.getData();\n  #else\n                   return nibble.asBytes();\n  #endif\n               }\n#else\n               byte[] newData = new byte[2048];\n    swmr_nibble.copyInto(newData, 0);\n    return newData;\n#endif\n           }")
        public abstract byte[] getBlockLightData(Object var1, int var2);

        @Template.Generated(value="public static void setSkyLightData(LevelChunk chunk, int cx, int cy, int cz, byte[] data) {\n    cy += #getHeightOffset(chunk);\n\n#if exists net.minecraft.world.level.chunk.LevelChunk public ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray[] starlight$getSkyNibbles();\n    SWMRNibbleArray[] nibbles = chunk.starlight$getSkyNibbles();\n#else\n               SWMRNibbleArray[] nibbles = chunk.getSkyNibbles();\n#endif\n               if (cy < 0 || cy >= nibbles.length) {\n        return null;\n    }\n               SWMRNibbleArray nibble = nibbles[cy];\n    if (nibble == null) {\n        nibble = new SWMRNibbleArray();\n        nibbles[cy] = nibble;\n    }\n\n           #if exists ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray public void copyFrom(final byte[] src, final int off);\n    nibble.copyFrom(data, 0);\n#else\n               nibble.set(0, 0);\n    #require ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray protected byte[] storageUpdating;\n    byte[] updating = nibble#storageUpdating;\n    System.arraycopy(data, 0, updating, 0, 2048);\n#endif\n\n               if (nibble.updateVisible()) {\n#if version >= 1.18\n        LightChunkGetter lightAccess = chunk.getLevel().getChunkSource();\n        SectionPos position = SectionPos.of(cx, cy-1, cz);\n        lightAccess.onLightUpdate(LightLayer.SKY, position);\n#else\n                   LightChunkGetter lightAccess = chunk.getWorld().getChunkProvider();\n        SectionPos position = SectionPos.a(cx, cy-1, cz);\n  #if exists net.minecraft.world.level.chunk.LightChunkGetter public abstract void markLightSectionDirty(net.minecraft.world.level.LightLayer block, net.minecraft.core.SectionPos pos);\n        lightAccess.markLightSectionDirty(LightLayer.SKY, position);\n  #else\n                   lightAccess.a(LightLayer.SKY, position);\n  #endif\n           #endif\n    }\n           }")
        public abstract void setSkyLightData(Object var1, int var2, int var3, int var4, byte[] var5);

        @Template.Generated(value="public static void setSkyLightData(LevelChunk chunk, int cx, int cy, int cz, byte[] data) {\n    cy += #getHeightOffset(chunk);\n\n#if exists net.minecraft.world.level.chunk.LevelChunk public ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray[] starlight$getBlockNibbles();\n    SWMRNibbleArray[] nibbles = chunk.starlight$getBlockNibbles();\n#else\n               SWMRNibbleArray[] nibbles = chunk.getBlockNibbles();\n#endif\n               if (cy < 0 || cy >= nibbles.length) {\n        return null;\n    }\n               SWMRNibbleArray nibble = nibbles[cy];\n    if (nibble == null) {\n        nibble = new SWMRNibbleArray();\n        nibbles[cy] = nibble;\n    }\n\n           #if exists ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray public void copyFrom(final byte[] src, final int off);\n    nibble.copyFrom(data, 0);\n#else\n               nibble.set(0, 0);\n    #require ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray protected byte[] storageUpdating;\n    byte[] updating = nibble#storageUpdating;\n    System.arraycopy(data, 0, updating, 0, 2048);\n#endif\n\n               if (nibble.updateVisible()) {\n#if version >= 1.18\n        LightChunkGetter lightAccess = chunk.getLevel().getChunkSource();\n        SectionPos position = SectionPos.of(cx, cy-1, cz);\n        lightAccess.onLightUpdate(LightLayer.BLOCK, position);\n#else\n                   LightChunkGetter lightAccess = chunk.getWorld().getChunkProvider();\n        SectionPos position = SectionPos.a(cx, cy-1, cz);\n  #if exists net.minecraft.world.level.chunk.LightChunkGetter public abstract void markLightSectionDirty(net.minecraft.world.level.LightLayer block, net.minecraft.core.SectionPos pos);\n        lightAccess.markLightSectionDirty(LightLayer.BLOCK, position);\n  #else\n                   lightAccess.a(LightLayer.BLOCK, position);\n  #endif\n           #endif\n    }\n           }")
        public abstract void setBlockLightData(Object var1, int var2, int var3, int var4, byte[] var5);
    }
}

