/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.world.level.ForcedChunksSavedData")
public abstract class ForcedChunksSavedDataHandle
extends Template.Handle {
    public static final ForcedChunksSavedDataClass T = Template.Class.create(ForcedChunksSavedDataClass.class, Common.TEMPLATE_RESOLVER);

    public static ForcedChunksSavedDataHandle createHandle(Object handleInstance) {
        return (ForcedChunksSavedDataHandle)T.createHandle(handleInstance);
    }

    public static final class ForcedChunksSavedDataClass
    extends Template.Class<ForcedChunksSavedDataHandle> {
    }
}

