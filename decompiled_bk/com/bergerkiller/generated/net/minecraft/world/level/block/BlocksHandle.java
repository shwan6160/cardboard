/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level.block;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.level.block.Blocks")
public abstract class BlocksHandle
extends Template.Handle {
    public static final BlocksClass T = Template.Class.create(BlocksClass.class, Common.TEMPLATE_RESOLVER);
    public static final Object AIR = BlocksHandle.T.AIR.getSafe();
    public static final Object LADDER = BlocksHandle.T.LADDER.getSafe();

    public static BlocksHandle createHandle(Object handleInstance) {
        return (BlocksHandle)T.createHandle(handleInstance);
    }

    public static final class BlocksClass
    extends Template.Class<BlocksHandle> {
        public final Template.StaticField.Converted<Object> AIR = new Template.StaticField.Converted();
        public final Template.StaticField.Converted<Object> LADDER = new Template.StaticField.Converted();
    }
}

