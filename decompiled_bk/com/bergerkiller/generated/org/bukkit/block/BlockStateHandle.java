/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.org.bukkit.block;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="org.bukkit.block.BlockState")
public abstract class BlockStateHandle
extends Template.Handle {
    public static final BlockStateClass T = Template.Class.create(BlockStateClass.class, Common.TEMPLATE_RESOLVER);

    public static BlockStateHandle createHandle(Object handleInstance) {
        return (BlockStateHandle)T.createHandle(handleInstance);
    }

    public abstract BlockData getBlockData();

    public static final class BlockStateClass
    extends Template.Class<BlockStateHandle> {
        @Template.Optional
        public final Template.Method<Boolean> isPlaced = new Template.Method();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted();
    }
}

