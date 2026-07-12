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

class LightingHandler_1_8_to_1_13_2
implements LightingHandler {
    private final LightingLogicHandle handle = Template.Class.create(LightingLogicHandle.class, Common.TEMPLATE_RESOLVER);

    LightingHandler_1_8_to_1_13_2() {
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
        return true;
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
    @Template.ImportList(value={@Template.Import(value="net.minecraft.world.level.chunk.LevelChunkSection"), @Template.Import(value="net.minecraft.world.level.chunk.DataLayer")})
    @Template.InstanceType(value="net.minecraft.world.level.chunk.LevelChunk")
    @Template.Require(declaring="net.minecraft.world.level.chunk.LevelChunk", value="public LevelChunkSection prepareChunkSection(int sectionIndex) {\n    LevelChunkSection[] sections = instance.getSections();\n    if (sectionIndex < 0 || sectionIndex >= sections.length) {\n        return null;\n    }\n           LevelChunkSection section = sections[sectionIndex];\n    if (section == null) {\n#select version >=\n#case 1.13:   boolean hasSkyLight = instance.world.worldProvider.g();\n#case 1.11:   boolean hasSkyLight = instance.world.worldProvider.m();\n#case 1.9:    boolean hasSkyLight = !instance.world.worldProvider.m();\n#case else:   boolean hasSkyLight = !instance.world.worldProvider.o();\n#endselect\n        sections[sectionIndex] = section = new LevelChunkSection(sectionIndex << 4, hasSkyLight);\n    }\n           return section;\n}")
    public static abstract class LightingLogicHandle
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static byte[] getSectionBlockLight(LevelChunk chunk, int sectionIndex) {\n    LevelChunkSection[] sections = chunk.getSections();\n    if (sectionIndex >= 0 && sectionIndex < sections.length) {\n        LevelChunkSection section = sections[sectionIndex];\n        if (section != null) {\n            DataLayer array = section.getEmittedLightArray();\n            if (array != null) {\n#if version >= 1.9\n                return (byte[]) array.asBytes().clone();\n#else\n                           return (byte[]) array.a().clone();\n#endif\n                       }\n        }\n               }\n    return null;\n}")
        public abstract byte[] getSectionBlockLight(Object var1, int var2);

        @Template.Generated(value="public static byte[] getSectionSkyLight(LevelChunk chunk, int sectionIndex) {\n    LevelChunkSection[] sections = chunk.getSections();\n    if (sectionIndex >= 0 && sectionIndex < sections.length) {\n        LevelChunkSection section = sections[sectionIndex];\n        if (section != null) {\n            DataLayer array = section.getSkyLightArray();\n            if (array != null) {\n#if version >= 1.9\n                return (byte[]) array.asBytes().clone();\n#else\n                           return (byte[]) array.a().clone();\n#endif\n                       }\n        }\n               }\n    return null;\n}")
        public abstract byte[] getSectionSkyLight(Object var1, int var2);

        @Template.Generated(value="public static void setSectionBlockLight(LevelChunk chunk, int sectionIndex, byte[] data) {\n    LevelChunkSection section = chunk#prepareChunkSection(sectionIndex);\n    if (section != null) {\n        section.a(new DataLayer(data));\n    }\n           }")
        public abstract void setSectionBlockLight(Object var1, int var2, byte[] var3);

        @Template.Generated(value="public static void setSectionSkyLight(LevelChunk chunk, int sectionIndex, byte[] data) {\n    LevelChunkSection section = chunk#prepareChunkSection(sectionIndex);\n    if (section != null) {\n        section.b(new DataLayer(data));\n    }\n           }")
        public abstract void setSectionSkyLight(Object var1, int var2, byte[] var3);
    }
}

