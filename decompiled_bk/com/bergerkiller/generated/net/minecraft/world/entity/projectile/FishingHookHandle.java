/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity.projectile;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.OptionalInt;

@Template.InstanceType(value="net.minecraft.world.entity.projectile.FishingHook")
public abstract class FishingHookHandle
extends EntityHandle {
    public static final FishingHookClass T = Template.Class.create(FishingHookClass.class, Common.TEMPLATE_RESOLVER);
    public static final DataWatcher.Key<OptionalInt> DATA_HOOKED_ENTITY_ID = DataWatcher.Key.Type.ENTITY_ID.createKey(FishingHookHandle.T.DATA_HOOKED_ENTITY_ID, -1);

    public static FishingHookHandle createHandle(Object handleInstance) {
        return (FishingHookHandle)T.createHandle(handleInstance);
    }

    public static final class FishingHookClass
    extends Template.Class<FishingHookHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Integer>> DATA_HOOKED_ENTITY_ID = new Template.StaticField.Converted();
    }
}

