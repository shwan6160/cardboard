/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;

@Template.Optional
@Template.InstanceType(value="net.minecraft.core.NonNullList")
public abstract class NonNullListHandle
extends Template.Handle {
    public static final NonNullListClass T = Template.Class.create(NonNullListClass.class, Common.TEMPLATE_RESOLVER);

    public static NonNullListHandle createHandle(Object handleInstance) {
        return (NonNullListHandle)T.createHandle(handleInstance);
    }

    public static List<?> create() {
        return NonNullListHandle.T.create.invoke();
    }

    public static final class NonNullListClass
    extends Template.Class<NonNullListHandle> {
        public final Template.StaticMethod.Converted<List<?>> create = new Template.StaticMethod.Converted();
    }
}

