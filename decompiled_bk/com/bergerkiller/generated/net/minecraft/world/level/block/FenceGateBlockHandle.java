/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level.block;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.level.block.FenceGateBlock")
public abstract class FenceGateBlockHandle
extends Template.Handle {
    public static final FenceGateBlockClass T = Template.Class.create(FenceGateBlockClass.class, Common.TEMPLATE_RESOLVER);

    public static FenceGateBlockHandle createHandle(Object handleInstance) {
        return (FenceGateBlockHandle)T.createHandle(handleInstance);
    }

    public static final class FenceGateBlockClass
    extends Template.Class<FenceGateBlockHandle> {
    }
}

