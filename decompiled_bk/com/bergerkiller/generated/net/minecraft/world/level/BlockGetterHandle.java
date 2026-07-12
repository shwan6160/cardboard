/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.level.BlockGetter")
public abstract class BlockGetterHandle
extends Template.Handle {
    public static final BlockGetterClass T = Template.Class.create(BlockGetterClass.class, Common.TEMPLATE_RESOLVER);

    public static BlockGetterHandle createHandle(Object handleInstance) {
        return (BlockGetterHandle)T.createHandle(handleInstance);
    }

    public static final class BlockGetterClass
    extends Template.Class<BlockGetterHandle> {
    }
}

