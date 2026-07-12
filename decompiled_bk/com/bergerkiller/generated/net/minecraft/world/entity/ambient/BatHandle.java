/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity.ambient;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.MobHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.entity.ambient.Bat")
public abstract class BatHandle
extends MobHandle {
    public static final BatClass T = Template.Class.create(BatClass.class, Common.TEMPLATE_RESOLVER);
    public static final DataWatcher.Key<Byte> DATA_BAT_FLAGS = DataWatcher.Key.Type.BYTE.createKey(BatHandle.T.DATA_BAT_FLAGS, 16);
    public static final int DATA_BAT_FLAG_HANGING = 1;

    public static BatHandle createHandle(Object handleInstance) {
        return (BatHandle)T.createHandle(handleInstance);
    }

    public static final class BatClass
    extends Template.Class<BatHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Byte>> DATA_BAT_FLAGS = new Template.StaticField.Converted();
    }
}

