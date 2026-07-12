/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level.block.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.level.block.entity.BlockEntityType")
public abstract class BlockEntityTypeHandle
extends Template.Handle {
    public static final BlockEntityTypeClass T = Template.Class.create(BlockEntityTypeClass.class, Common.TEMPLATE_RESOLVER);

    public static BlockEntityTypeHandle createHandle(Object handleInstance) {
        return (BlockEntityTypeHandle)T.createHandle(handleInstance);
    }

    public static Object getRawByKey(IdentifierHandle key) {
        return BlockEntityTypeHandle.T.getRawByKey.invoke(key);
    }

    public static Object getRawById(int id) {
        return BlockEntityTypeHandle.T.getRawById.invoker.invoke(null, id);
    }

    public abstract IdentifierHandle getKey();

    public abstract int getId();

    public static final class BlockEntityTypeClass
    extends Template.Class<BlockEntityTypeHandle> {
        public final Template.StaticMethod.Converted<Object> getRawByKey = new Template.StaticMethod.Converted();
        public final Template.StaticMethod<Object> getRawById = new Template.StaticMethod();
        public final Template.Method.Converted<IdentifierHandle> getKey = new Template.Method.Converted();
        public final Template.Method<Integer> getId = new Template.Method();
    }
}

