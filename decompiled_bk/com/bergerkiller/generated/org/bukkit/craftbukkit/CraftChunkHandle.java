/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="org.bukkit.craftbukkit.CraftChunk")
public abstract class CraftChunkHandle
extends Template.Handle {
    public static final CraftChunkClass T = Template.Class.create(CraftChunkClass.class, Common.TEMPLATE_RESOLVER);

    public static CraftChunkHandle createHandle(Object handleInstance) {
        return (CraftChunkHandle)T.createHandle(handleInstance);
    }

    public abstract Object getHandle();

    public static final class CraftChunkClass
    extends Template.Class<CraftChunkHandle> {
        public final Template.Method<Object> getHandle = new Template.Method();
    }
}

