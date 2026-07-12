/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.Optional
@Template.InstanceType(value="net.minecraft.world.entity.MoverType")
public abstract class MoverTypeHandle
extends Template.Handle {
    public static final MoverTypeClass T = Template.Class.create(MoverTypeClass.class, Common.TEMPLATE_RESOLVER);
    public static final MoverTypeHandle SELF = MoverTypeHandle.T.SELF.getSafe();
    public static final MoverTypeHandle PLAYER = MoverTypeHandle.T.PLAYER.getSafe();
    public static final MoverTypeHandle PISTON = MoverTypeHandle.T.PISTON.getSafe();
    public static final MoverTypeHandle SHULKER_BOX = MoverTypeHandle.T.SHULKER_BOX.getSafe();
    public static final MoverTypeHandle SHULKER = MoverTypeHandle.T.SHULKER.getSafe();

    public static MoverTypeHandle createHandle(Object handleInstance) {
        return (MoverTypeHandle)T.createHandle(handleInstance);
    }

    public static final class MoverTypeClass
    extends Template.Class<MoverTypeHandle> {
        public final Template.EnumConstant.Converted<MoverTypeHandle> SELF = new Template.EnumConstant.Converted();
        public final Template.EnumConstant.Converted<MoverTypeHandle> PLAYER = new Template.EnumConstant.Converted();
        public final Template.EnumConstant.Converted<MoverTypeHandle> PISTON = new Template.EnumConstant.Converted();
        public final Template.EnumConstant.Converted<MoverTypeHandle> SHULKER_BOX = new Template.EnumConstant.Converted();
        public final Template.EnumConstant.Converted<MoverTypeHandle> SHULKER = new Template.EnumConstant.Converted();
    }
}

