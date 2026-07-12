/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.lighting.LightingHandler;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.concurrent.CompletableFuture;
import org.bukkit.World;

class LightingHandler_CubicChunks_1_12_2
implements LightingHandler {
    private final LightingLogicHandle handle = Template.Class.create(LightingLogicHandle.class, Common.TEMPLATE_RESOLVER);

    LightingHandler_CubicChunks_1_12_2() {
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
        return this.handle.isSupported(HandleConversion.toWorldHandle(world));
    }

    @Override
    public byte[] getSectionBlockLight(World world, int cx, int cy, int cz) {
        Object nms_chunk = HandleConversion.toChunkHandle(world.getChunkAt(cx, cz));
        return this.handle.getSectionBlockLight(nms_chunk, cy);
    }

    @Override
    public byte[] getSectionSkyLight(World world, int cx, int cy, int cz) {
        Object nms_chunk = HandleConversion.toChunkHandle(world.getChunkAt(cx, cz));
        return this.handle.getSectionSkyLight(nms_chunk, cy);
    }

    @Override
    public CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        return CommonUtil.runAsyncMainThread(() -> {
            Object nms_chunk = HandleConversion.toChunkHandle(world.getChunkAt(cx, cz));
            this.handle.setSectionBlockLight(nms_chunk, cy, data);
        });
    }

    @Override
    public CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        return CommonUtil.runAsyncMainThread(() -> {
            Object nms_chunk = HandleConversion.toChunkHandle(world.getChunkAt(cx, cz));
            this.handle.setSectionSkyLight(nms_chunk, cy, data);
        });
    }

    @Template.Optional
    @Template.ImportList(value={@Template.Import(value="io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld"), @Template.Import(value="io.github.opencubicchunks.cubicchunks.api.world.ICube"), @Template.Import(value="io.github.opencubicchunks.cubicchunks.api.world.IColumn"), @Template.Import(value="io.github.opencubicchunks.cubicchunks.core.world.cube.Cube"), @Template.Import(value="net.minecraft.world.level.chunk.DataLayer"), @Template.Import(value="net.minecraft.world.level.chunk.LevelChunkSection")})
    @Template.InstanceType(value="net.minecraft.world.level.chunk.LevelChunk")
    public static abstract class LightingLogicHandle
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static boolean isSupported(net.minecraft.server.level.ServerLevel world) {\n    return world instanceof ICubicWorld && ((ICubicWorld) world).isCubicWorld();\n}")
        public abstract boolean isSupported(Object var1);

        @Template.Generated(value="public static byte[] getSectionBlockLight(IColumn chunk, int cy) {\n    ICube cube = chunk.getCube(cy);\n    LevelChunkSection section = cube.getStorage();\n    if (section != null) {\n        DataLayer array = section.getEmittedLightArray();\n        if (array != null) {\n#if version >= 1.9\n            return (byte[]) array.asBytes().clone();\n#else\n                       return (byte[]) array.a().clone();\n#endif\n                   }\n    }\n               return null;\n}")
        public abstract byte[] getSectionBlockLight(Object var1, int var2);

        @Template.Generated(value="public static byte[] getSectionSkyLight(IColumn chunk, int cy) {\n    ICube cube = chunk.getCube(cy);\n    LevelChunkSection section = cube.getStorage();\n    if (section != null) {\n        DataLayer array = section.getSkyLightArray();\n        if (array != null) {\n#if version >= 1.9\n            return (byte[]) array.asBytes().clone();\n#else\n                       return (byte[]) array.a().clone();\n#endif\n                   }\n    }\n               return null;\n}")
        public abstract byte[] getSectionSkyLight(Object var1, int var2);

        @Template.Generated(value="public static void setSectionBlockLight(IColumn chunk, int cy, byte[] data) {\n    ICube cube = chunk.getCube(cy);\n    LevelChunkSection section = cube.getStorage();\n    if (section != null) {\n        section.a(new DataLayer(data));\n        ((Cube) cube).markDirty();\n    }\n           }")
        public abstract void setSectionBlockLight(Object var1, int var2, byte[] var3);

        @Template.Generated(value="public static void setSectionSkyLight(IColumn chunk, int cy, byte[] data) {\n    ICube cube = chunk.getCube(cy);\n    LevelChunkSection section = cube.getStorage();\n    if (section != null) {\n        section.b(new DataLayer(data));\n        ((Cube) cube).markDirty();\n    }\n           }")
        public abstract void setSectionSkyLight(Object var1, int var2, byte[] var3);
    }
}

