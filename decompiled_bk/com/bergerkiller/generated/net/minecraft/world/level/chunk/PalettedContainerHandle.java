/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level.chunk;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.level.chunk.PalettedContainer")
public abstract class PalettedContainerHandle
extends Template.Handle {
    public static final PalettedContainerClass T = Template.Class.create(PalettedContainerClass.class, Common.TEMPLATE_RESOLVER);

    public static PalettedContainerHandle createHandle(Object handleInstance) {
        return (PalettedContainerHandle)T.createHandle(handleInstance);
    }

    public abstract BlockData getBlockData(int var1, int var2, int var3);

    public abstract void setBlockData(int var1, int var2, int var3, BlockData var4);

    public static final class PalettedContainerClass
    extends Template.Class<PalettedContainerHandle> {
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setBlockData = new Template.Method.Converted();
    }
}

