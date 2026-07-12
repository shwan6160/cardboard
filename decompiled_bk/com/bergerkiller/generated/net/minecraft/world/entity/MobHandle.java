/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.entity.Mob")
public abstract class MobHandle
extends LivingEntityHandle {
    public static final MobClass T = Template.Class.create(MobClass.class, Common.TEMPLATE_RESOLVER);
    public static final DataWatcher.Key<Byte> DATA_INSENTIENT_FLAGS = DataWatcher.Key.Type.BYTE.createKey(MobHandle.T.DATA_INSENTIENT_FLAGS, 11);
    public static final int DATA_INSENTIENT_FLAG_NOAI = 1;
    public static final int DATA_INSENTIENT_FLAG_LEFT_HANDED = 2;

    public static MobHandle createHandle(Object handleInstance) {
        return (MobHandle)T.createHandle(handleInstance);
    }

    public abstract EntityHandle getLeashHolder();

    public abstract Object getNavigation();

    public abstract boolean isSleeping();

    public static final class MobClass
    extends Template.Class<MobHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Byte>> DATA_INSENTIENT_FLAGS = new Template.StaticField.Converted();
        public final Template.Method.Converted<EntityHandle> getLeashHolder = new Template.Method.Converted();
        public final Template.Method<Object> getNavigation = new Template.Method();
        public final Template.Method<Boolean> isSleeping = new Template.Method();
    }
}

