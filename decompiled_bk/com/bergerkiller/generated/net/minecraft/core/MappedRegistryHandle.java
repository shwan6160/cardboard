/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Map;

@Template.InstanceType(value="net.minecraft.core.MappedRegistry")
public abstract class MappedRegistryHandle
extends Template.Handle {
    public static final MappedRegistryClass T = Template.Class.create(MappedRegistryClass.class, Common.TEMPLATE_RESOLVER);

    public static MappedRegistryHandle createHandle(Object handleInstance) {
        return (MappedRegistryHandle)T.createHandle(handleInstance);
    }

    public abstract Object get(Object var1);

    public abstract Object getKey(Object var1);

    public static final class MappedRegistryClass
    extends Template.Class<MappedRegistryHandle> {
        @Template.Optional
        public final Template.Field<Map<Object, Object>> opt_inverseLookupField = new Template.Field();
        public final Template.Method.Converted<Object> get = new Template.Method.Converted();
        public final Template.Method<Object> getKey = new Template.Method();
    }
}

