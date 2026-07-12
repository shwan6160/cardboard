/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.phys;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.phys.AABB")
public abstract class AABBHandle
extends Template.Handle {
    public static final AABBClass T = Template.Class.create(AABBClass.class, Common.TEMPLATE_RESOLVER);

    public static AABBHandle createHandle(Object handleInstance) {
        return (AABBHandle)T.createHandle(handleInstance);
    }

    public static final AABBHandle createNew(double x1, double y1, double z1, double x2, double y2, double z2) {
        return AABBHandle.T.constr_x1_y1_z1_x2_y2_z2.newInstanceVA(x1, y1, z1, x2, y2, z2);
    }

    public abstract AABBHandle grow(double var1, double var3, double var5);

    public abstract AABBHandle transformB(double var1, double var3, double var5);

    public abstract AABBHandle translate(double var1, double var3, double var5);

    public abstract boolean bbTransformA(AABBHandle var1);

    public abstract double calcSomeX(AABBHandle var1, double var2);

    public abstract double calcSomeY(AABBHandle var1, double var2);

    public abstract double calcSomeZ(AABBHandle var1, double var2);

    public AABBHandle growUniform(double size) {
        return this.grow(size, size, size);
    }

    public AABBHandle shrinkUniform(double size) {
        return this.growUniform(-size);
    }

    public abstract double getMinX();

    public abstract void setMinX(double var1);

    public abstract double getMinY();

    public abstract void setMinY(double var1);

    public abstract double getMinZ();

    public abstract void setMinZ(double var1);

    public abstract double getMaxX();

    public abstract void setMaxX(double var1);

    public abstract double getMaxY();

    public abstract void setMaxY(double var1);

    public abstract double getMaxZ();

    public abstract void setMaxZ(double var1);

    public static final class AABBClass
    extends Template.Class<AABBHandle> {
        public final Template.Constructor.Converted<AABBHandle> constr_x1_y1_z1_x2_y2_z2 = new Template.Constructor.Converted();
        public final Template.Field.Double minX = new Template.Field.Double();
        public final Template.Field.Double minY = new Template.Field.Double();
        public final Template.Field.Double minZ = new Template.Field.Double();
        public final Template.Field.Double maxX = new Template.Field.Double();
        public final Template.Field.Double maxY = new Template.Field.Double();
        public final Template.Field.Double maxZ = new Template.Field.Double();
        public final Template.Method.Converted<AABBHandle> grow = new Template.Method.Converted();
        public final Template.Method.Converted<AABBHandle> transformB = new Template.Method.Converted();
        public final Template.Method.Converted<AABBHandle> translate = new Template.Method.Converted();
        public final Template.Method.Converted<Boolean> bbTransformA = new Template.Method.Converted();
        public final Template.Method.Converted<Double> calcSomeX = new Template.Method.Converted();
        public final Template.Method.Converted<Double> calcSomeY = new Template.Method.Converted();
        public final Template.Method.Converted<Double> calcSomeZ = new Template.Method.Converted();
    }
}

