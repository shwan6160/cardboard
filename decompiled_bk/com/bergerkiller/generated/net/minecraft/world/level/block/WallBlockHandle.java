/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level.block;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.level.block.WallBlock")
public abstract class WallBlockHandle
extends Template.Handle {
    public static final WallBlockClass T = Template.Class.create(WallBlockClass.class, Common.TEMPLATE_RESOLVER);

    public static WallBlockHandle createHandle(Object handleInstance) {
        return (WallBlockHandle)T.createHandle(handleInstance);
    }

    public static final class WallBlockClass
    extends Template.Class<WallBlockHandle> {
    }
}

