/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity.vehicle.boat;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.BoatWoodType;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.entity.vehicle.boat.Boat")
public abstract class BoatHandle
extends EntityHandle {
    public static final BoatClass T = Template.Class.create(BoatClass.class, Common.TEMPLATE_RESOLVER);
    public static final DataWatcher.Key<BoatWoodType> DATA_WOOD_TYPE = DataWatcher.Key.Type.BOAT_WOOD_TYPE.createKey(BoatHandle.T.DATA_WOOD_TYPE, -1);

    public static BoatHandle createHandle(Object handleInstance) {
        return (BoatHandle)T.createHandle(handleInstance);
    }

    public static final class BoatClass
    extends Template.Class<BoatHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Integer>> DATA_WOOD_TYPE = new Template.StaticField.Converted();
    }
}

