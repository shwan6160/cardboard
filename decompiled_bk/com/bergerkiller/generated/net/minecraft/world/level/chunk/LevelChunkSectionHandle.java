/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 */
package com.bergerkiller.generated.net.minecraft.world.level.chunk;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.PalettedContainerHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.block.Block;

@Template.InstanceType(value="net.minecraft.world.level.chunk.LevelChunkSection")
public abstract class LevelChunkSectionHandle
extends Template.Handle {
    public static final LevelChunkSectionClass T = Template.Class.create(LevelChunkSectionClass.class, Common.TEMPLATE_RESOLVER);

    public static LevelChunkSectionHandle createHandle(Object handleInstance) {
        return (LevelChunkSectionHandle)T.createHandle(handleInstance);
    }

    public abstract boolean isEmpty();

    public abstract PalettedContainerHandle getBlockPalette();

    public abstract BlockData getBlockData(int var1, int var2, int var3);

    public abstract void setBlockData(int var1, int var2, int var3, BlockData var4);

    public abstract void setBlockDataAtBlock(Block var1, BlockData var2);

    public static final class LevelChunkSectionClass
    extends Template.Class<LevelChunkSectionHandle> {
        public final Template.Method<Boolean> isEmpty = new Template.Method();
        public final Template.Method.Converted<PalettedContainerHandle> getBlockPalette = new Template.Method.Converted();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setBlockData = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setBlockDataAtBlock = new Template.Method.Converted();
    }
}

