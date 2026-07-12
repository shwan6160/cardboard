/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.core.particles;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;

@Template.InstanceType(value="net.minecraft.core.particles.ParticleType")
public abstract class ParticleTypeHandle
extends Template.Handle {
    public static final ParticleTypeClass T = Template.Class.create(ParticleTypeClass.class, Common.TEMPLATE_RESOLVER);

    public static ParticleTypeHandle createHandle(Object handleInstance) {
        return (ParticleTypeHandle)T.createHandle(handleInstance);
    }

    public static Object byName(String name) {
        return ParticleTypeHandle.T.byName.invoker.invoke(null, name);
    }

    public static List<?> values() {
        return (List)ParticleTypeHandle.T.values.invoker.invoke(null);
    }

    public abstract boolean hasOptions();

    public abstract String getName();

    public static final class ParticleTypeClass
    extends Template.Class<ParticleTypeHandle> {
        public final Template.StaticMethod<Object> byName = new Template.StaticMethod();
        public final Template.StaticMethod<List<?>> values = new Template.StaticMethod();
        public final Template.Method<Boolean> hasOptions = new Template.Method();
        public final Template.Method<String> getName = new Template.Method();
    }
}

