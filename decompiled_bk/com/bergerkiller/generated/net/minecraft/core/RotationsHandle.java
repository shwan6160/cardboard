/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.util.Vector;

@Template.InstanceType(value="net.minecraft.core.Rotations")
public abstract class RotationsHandle
extends Template.Handle {
    public static final RotationsClass T = Template.Class.create(RotationsClass.class, Common.TEMPLATE_RESOLVER);

    public static RotationsHandle createHandle(Object handleInstance) {
        return (RotationsHandle)T.createHandle(handleInstance);
    }

    public static final RotationsHandle createNew(float x, float y, float z) {
        return RotationsHandle.T.constr_x_y_z.newInstance(Float.valueOf(x), Float.valueOf(y), Float.valueOf(z));
    }

    public static Object fromBukkitRaw(Vector vector) {
        return RotationsHandle.T.fromBukkitRaw.invoker.invoke(null, vector);
    }

    public abstract float getX();

    public abstract float getY();

    public abstract float getZ();

    public abstract Vector toBukkit();

    public static RotationsHandle fromBukkit(Vector vector) {
        return RotationsHandle.createHandle(RotationsHandle.fromBukkitRaw(vector));
    }

    public static final class RotationsClass
    extends Template.Class<RotationsHandle> {
        public final Template.Constructor.Converted<RotationsHandle> constr_x_y_z = new Template.Constructor.Converted();
        public final Template.StaticMethod<Object> fromBukkitRaw = new Template.StaticMethod();
        public final Template.Method<Float> getX = new Template.Method();
        public final Template.Method<Float> getY = new Template.Method();
        public final Template.Method<Float> getZ = new Template.Method();
        public final Template.Method<Vector> toBukkit = new Template.Method();
    }
}

