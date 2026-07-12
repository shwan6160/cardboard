/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.core.Vec3i")
public abstract class Vec3iHandle
extends Template.Handle {
    public static final Vec3iClass T = Template.Class.create(Vec3iClass.class, Common.TEMPLATE_RESOLVER);

    public static Vec3iHandle createHandle(Object handleInstance) {
        return (Vec3iHandle)T.createHandle(handleInstance);
    }

    public abstract int getX();

    public abstract int getY();

    public abstract int getZ();

    public abstract boolean isPositionInBox(int var1, int var2, int var3, int var4, int var5, int var6);

    public abstract IntVector3 toIntVector3();

    public static final class Vec3iClass
    extends Template.Class<Vec3iHandle> {
        public final Template.Method<Integer> getX = new Template.Method();
        public final Template.Method<Integer> getY = new Template.Method();
        public final Template.Method<Integer> getZ = new Template.Method();
        public final Template.Method<Boolean> isPositionInBox = new Template.Method();
        public final Template.Method<IntVector3> toIntVector3 = new Template.Method();
    }
}

