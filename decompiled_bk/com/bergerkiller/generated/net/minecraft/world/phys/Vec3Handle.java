/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.generated.net.minecraft.world.phys;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.util.Vector;

@Template.InstanceType(value="net.minecraft.world.phys.Vec3")
public abstract class Vec3Handle
extends Template.Handle {
    public static final Vec3Class T = Template.Class.create(Vec3Class.class, Common.TEMPLATE_RESOLVER);

    public static Vec3Handle createHandle(Object handleInstance) {
        return (Vec3Handle)T.createHandle(handleInstance);
    }

    public static final Vec3Handle createNew(double x, double y, double z) {
        return Vec3Handle.T.constr_x_y_z.newInstance(x, y, z);
    }

    public static Object fromBukkitRaw(Vector vector) {
        return Vec3Handle.T.fromBukkitRaw.invoker.invoke(null, vector);
    }

    public abstract Vector toBukkit();

    public static Vec3Handle fromBukkit(Vector vector) {
        return Vec3Handle.createHandle(Vec3Handle.fromBukkitRaw(vector));
    }

    public abstract double getX();

    public abstract void setX(double var1);

    public abstract double getY();

    public abstract void setY(double var1);

    public abstract double getZ();

    public abstract void setZ(double var1);

    public static final class Vec3Class
    extends Template.Class<Vec3Handle> {
        public final Template.Constructor.Converted<Vec3Handle> constr_x_y_z = new Template.Constructor.Converted();
        public final Template.Field.Double x = new Template.Field.Double();
        public final Template.Field.Double y = new Template.Field.Double();
        public final Template.Field.Double z = new Template.Field.Double();
        public final Template.StaticMethod<Object> fromBukkitRaw = new Template.StaticMethod();
        public final Template.Method<Vector> toBukkit = new Template.Method();
    }
}

