/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.core.IdMapper")
public abstract class IdMapperHandle
extends Template.Handle {
    public static final IdMapperClass T = Template.Class.create(IdMapperClass.class, Common.TEMPLATE_RESOLVER);

    public static IdMapperHandle createHandle(Object handleInstance) {
        return (IdMapperHandle)T.createHandle(handleInstance);
    }

    public abstract int getId(Object var1);

    public static final class IdMapperClass
    extends Template.Class<IdMapperHandle> {
        public final Template.Method<Integer> getId = new Template.Method();
    }
}

