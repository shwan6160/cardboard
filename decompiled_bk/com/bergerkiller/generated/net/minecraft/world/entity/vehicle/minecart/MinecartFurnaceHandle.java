/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.AbstractMinecartHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.util.Vector;

@Template.InstanceType(value="net.minecraft.world.entity.vehicle.minecart.MinecartFurnace")
public abstract class MinecartFurnaceHandle
extends AbstractMinecartHandle {
    public static final MinecartFurnaceClass T = Template.Class.create(MinecartFurnaceClass.class, Common.TEMPLATE_RESOLVER);
    public static final DataWatcher.Key<Boolean> DATA_SMOKING = DataWatcher.Key.Type.BOOLEAN.createKey(MinecartFurnaceHandle.T.DATA_SMOKING, 16);

    public static MinecartFurnaceHandle createHandle(Object handleInstance) {
        return (MinecartFurnaceHandle)T.createHandle(handleInstance);
    }

    public abstract Vector getPushForce();

    public abstract void setPushForce(double var1, double var3, double var5);

    public void setPushForce(Vector force) {
        this.setPushForce(force.getX(), force.getY(), force.getZ());
    }

    @Deprecated
    public double getPushForceX() {
        return this.getPushForce().getX();
    }

    @Deprecated
    public double getPushForceZ() {
        return this.getPushForce().getZ();
    }

    @Deprecated
    public void setPushForceX(double x) {
        Vector v = this.getPushForce();
        this.setPushForce(x, v.getY(), v.getZ());
    }

    @Deprecated
    public void setPushForceZ(double z) {
        Vector v = this.getPushForce();
        this.setPushForce(v.getX(), v.getY(), z);
    }

    public abstract int getFuel();

    public abstract void setFuel(int var1);

    public static final class MinecartFurnaceClass
    extends Template.Class<MinecartFurnaceHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Boolean>> DATA_SMOKING = new Template.StaticField.Converted();
        public final Template.Field.Integer fuel = new Template.Field.Integer();
        public final Template.Method.Converted<Vector> getPushForce = new Template.Method.Converted();
        public final Template.Method<Void> setPushForce = new Template.Method();
    }
}

