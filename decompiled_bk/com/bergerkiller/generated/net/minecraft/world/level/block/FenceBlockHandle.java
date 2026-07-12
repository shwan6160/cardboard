/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level.block;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.level.block.FenceBlock")
public abstract class FenceBlockHandle
extends Template.Handle {
    public static final FenceBlockClass T = Template.Class.create(FenceBlockClass.class, Common.TEMPLATE_RESOLVER);

    public static FenceBlockHandle createHandle(Object handleInstance) {
        return (FenceBlockHandle)T.createHandle(handleInstance);
    }

    public static final class FenceBlockClass
    extends Template.Class<FenceBlockHandle> {
    }
}

