/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.util.Vector;

@Template.Optional
@Template.InstanceType(value="net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior")
public abstract class NewMinecartBehaviorHandle
extends Template.Handle {
    public static final NewMinecartBehaviorClass T = Template.Class.create(NewMinecartBehaviorClass.class, Common.TEMPLATE_RESOLVER);

    public static NewMinecartBehaviorHandle createHandle(Object handleInstance) {
        return (NewMinecartBehaviorHandle)T.createHandle(handleInstance);
    }

    public static final class NewMinecartBehaviorClass
    extends Template.Class<NewMinecartBehaviorHandle> {
    }

    @Template.Optional
    @Template.InstanceType(value="net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior.MinecartStep")
    public static abstract class MinecartStepHandle
    extends Template.Handle {
        public static final MinecartStepClass T = Template.Class.create(MinecartStepClass.class, Common.TEMPLATE_RESOLVER);

        public static MinecartStepHandle createHandle(Object handleInstance) {
            return (MinecartStepHandle)T.createHandle(handleInstance);
        }

        public static MinecartStepHandle createNew(Vector position, Vector movement, float yaw, float pitch, float weight) {
            return MinecartStepHandle.T.createNew.invoke(position, movement, Float.valueOf(yaw), Float.valueOf(pitch), Float.valueOf(weight));
        }

        public abstract Vector getPosition();

        public abstract Vector getMovement();

        public abstract float getYaw();

        public abstract float getPitch();

        public abstract float getWeight();

        public static final class MinecartStepClass
        extends Template.Class<MinecartStepHandle> {
            public final Template.StaticMethod.Converted<MinecartStepHandle> createNew = new Template.StaticMethod.Converted();
            public final Template.Method.Converted<Vector> getPosition = new Template.Method.Converted();
            public final Template.Method.Converted<Vector> getMovement = new Template.Method.Converted();
            public final Template.Method<Float> getYaw = new Template.Method();
            public final Template.Method<Float> getPitch = new Template.Method();
            public final Template.Method<Float> getWeight = new Template.Method();
        }
    }
}

