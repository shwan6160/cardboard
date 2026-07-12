/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.util;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.util.RandomSource")
public abstract class RandomSourceHandle
extends Template.Handle {
    public static final RandomSourceClass T = Template.Class.create(RandomSourceClass.class, Common.TEMPLATE_RESOLVER);

    public static RandomSourceHandle createHandle(Object handleInstance) {
        return (RandomSourceHandle)T.createHandle(handleInstance);
    }

    public abstract int nextIntUnbounded();

    public abstract int nextInt(int var1);

    public abstract long nextLong();

    public abstract boolean nextBoolean();

    public abstract float nextFloat();

    public abstract double nextDouble();

    public static final class RandomSourceClass
    extends Template.Class<RandomSourceHandle> {
        public final Template.Method<Integer> nextIntUnbounded = new Template.Method();
        public final Template.Method<Integer> nextInt = new Template.Method();
        public final Template.Method<Long> nextLong = new Template.Method();
        public final Template.Method<Boolean> nextBoolean = new Template.Method();
        public final Template.Method<Float> nextFloat = new Template.Method();
        public final Template.Method<Double> nextDouble = new Template.Method();
    }
}

