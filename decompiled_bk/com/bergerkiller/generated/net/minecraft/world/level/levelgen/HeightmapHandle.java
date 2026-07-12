/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level.levelgen;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.LevelChunkHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.level.levelgen.Heightmap")
public abstract class HeightmapHandle
extends Template.Handle {
    public static final HeightmapClass T = Template.Class.create(HeightmapClass.class, Common.TEMPLATE_RESOLVER);

    public static HeightmapHandle createHandle(Object handleInstance) {
        return (HeightmapHandle)T.createHandle(handleInstance);
    }

    public abstract LevelChunkHandle getChunk();

    public abstract int getHeight(int var1, int var2);

    public abstract void setHeight(int var1, int var2, int var3);

    public abstract void initialize();

    public static final class HeightmapClass
    extends Template.Class<HeightmapHandle> {
        public final Template.Method.Converted<LevelChunkHandle> getChunk = new Template.Method.Converted();
        public final Template.Method<Integer> getHeight = new Template.Method();
        public final Template.Method<Void> setHeight = new Template.Method();
        public final Template.Method<Void> initialize = new Template.Method();
    }
}

