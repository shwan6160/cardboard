/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.core.Registry")
public abstract class RegistryHandle
extends Template.Handle {
    public static final RegistryClass T = Template.Class.create(RegistryClass.class, Common.TEMPLATE_RESOLVER);

    public static RegistryHandle createHandle(Object handleInstance) {
        return (RegistryHandle)T.createHandle(handleInstance);
    }

    public static Object getWindowTypeByName(String name) {
        return RegistryHandle.T.getWindowTypeByName.invoker.invoke(null, name);
    }

    public static int getWindowTypeId(Object windowTypeRaw) {
        return (Integer)RegistryHandle.T.getWindowTypeId.invoker.invoke(null, windowTypeRaw);
    }

    public static final class RegistryClass
    extends Template.Class<RegistryHandle> {
        public final Template.StaticMethod<Object> getWindowTypeByName = new Template.StaticMethod();
        public final Template.StaticMethod<Integer> getWindowTypeId = new Template.StaticMethod();
    }
}

