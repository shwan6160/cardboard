/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity.vehicle;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.world.entity.vehicle.VehicleEntity")
public abstract class VehicleEntityHandle
extends EntityHandle {
    public static final VehicleEntityClass T = Template.Class.create(VehicleEntityClass.class, Common.TEMPLATE_RESOLVER);

    public static VehicleEntityHandle createHandle(Object handleInstance) {
        return (VehicleEntityHandle)T.createHandle(handleInstance);
    }

    public static final class VehicleEntityClass
    extends Template.Class<VehicleEntityHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Integer>> DATA_SHAKING_FACTOR = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Integer>> DATA_SHAKING_DIRECTION = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Float>> DATA_SHAKING_DAMAGE = new Template.StaticField.Converted();
    }
}

