/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.MobHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.entity.AgeableMob")
public abstract class AgeableMobHandle
extends MobHandle {
    public static final AgeableMobClass T = Template.Class.create(AgeableMobClass.class, Common.TEMPLATE_RESOLVER);
    public static final DataWatcher.Key<Boolean> DATA_IS_BABY = DataWatcher.Key.Type.BOOLEAN.createKey(AgeableMobHandle.T.DATA_IS_BABY, 12);

    public static AgeableMobHandle createHandle(Object handleInstance) {
        return (AgeableMobHandle)T.createHandle(handleInstance);
    }

    public static final class AgeableMobClass
    extends Template.Class<AgeableMobHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Boolean>> DATA_IS_BABY = new Template.StaticField.Converted();
    }
}

