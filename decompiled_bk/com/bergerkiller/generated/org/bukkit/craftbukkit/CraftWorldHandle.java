/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.org.bukkit.WorldHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="org.bukkit.craftbukkit.CraftWorld")
public abstract class CraftWorldHandle
extends WorldHandle {
    public static final CraftWorldClass T = Template.Class.create(CraftWorldClass.class, Common.TEMPLATE_RESOLVER);

    public static CraftWorldHandle createHandle(Object handleInstance) {
        return (CraftWorldHandle)T.createHandle(handleInstance);
    }

    public abstract Object getHandle();

    public static final class CraftWorldClass
    extends Template.Class<CraftWorldHandle> {
        public final Template.Method<Object> getHandle = new Template.Method();
    }
}

