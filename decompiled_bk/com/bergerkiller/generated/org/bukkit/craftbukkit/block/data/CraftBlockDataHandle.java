/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit.block.data;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="org.bukkit.craftbukkit.block.data.CraftBlockData")
public abstract class CraftBlockDataHandle
extends Template.Handle {
    public static final CraftBlockDataClass T = Template.Class.create(CraftBlockDataClass.class, Common.TEMPLATE_RESOLVER);

    public static CraftBlockDataHandle createHandle(Object handleInstance) {
        return (CraftBlockDataHandle)T.createHandle(handleInstance);
    }

    public static Object fromData(BlockData data) {
        return CraftBlockDataHandle.T.fromData.invoke(data);
    }

    public abstract BlockData getState();

    public static final class CraftBlockDataClass
    extends Template.Class<CraftBlockDataHandle> {
        public final Template.StaticMethod.Converted<Object> fromData = new Template.StaticMethod.Converted();
        public final Template.Method.Converted<BlockData> getState = new Template.Method.Converted();
    }
}

